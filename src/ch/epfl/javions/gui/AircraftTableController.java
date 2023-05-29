package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

/**
 * Manage the aircraft table
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class AircraftTableController {
    private final ObservableSet<ObservableAircraftState> states;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final TableView<ObservableAircraftState> table = new TableView<>();;
    private final NumberFormat numberFormat4 = NumberFormat.getInstance();
    private final NumberFormat numberFormat0 = NumberFormat.getInstance();
    private Consumer<ObservableAircraftState> stateConsumer;
    private final int OACI_WIDTH = 60;
    private final int CALLSIGN_DESCRIPTION_WIDTH = 70;
    private final int MODEL_WIDTH = 230;
    private final int REGISTRATION_WIDTH = 90;
    private final int TYPE_DESIGNATOR_WIDTH = 50;
    private final int NUMERIC_COLUMN_WIDTH = 85;
    private final int SIZE_AFTER_DECMAL_PTS4 = 4;
    private final int SIZE_AFTER_DECMAL_PTS0 = 0;

    /**
     * Public Constructor
     * @param states : the observable set of aircraft states that must appear on the view (table)
     * @param selectedAircraft : the state of the selected aircraft
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft){
        this.states = states;
        this.selectedAircraft = selectedAircraft;
        createListeners();

        table.getStylesheets().add("table.css");
        table.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        table.setTableMenuButtonVisible(true);
        table.setOnMouseClicked(e-> {
            if(e.getClickCount() == 2 && e.getButton().compareTo(MouseButton.PRIMARY) == 0 && stateConsumer != null){
                stateConsumer.accept(selectedAircraft.get());
            }
        });

        numberFormat4.setMinimumFractionDigits(SIZE_AFTER_DECMAL_PTS4);
        numberFormat4.setMaximumFractionDigits(SIZE_AFTER_DECMAL_PTS4);

        numberFormat0.setMinimumFractionDigits(SIZE_AFTER_DECMAL_PTS0);
        numberFormat0.setMaximumFractionDigits(SIZE_AFTER_DECMAL_PTS0);
        createTableLine();
    }

    /**
     * Getter for the JavaFX pane
     * @return the TableView associated to the set of aircraft states
     */
    public TableView<ObservableAircraftState> pane(){ return table; }

    /**
     * Call consumer's accept method when there's a double click
     * @param consumer : a lambda defining the accept method
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        stateConsumer = consumer;
    }
    private void createListeners(){
        states.addListener(this::handleStatesChanges);
        selectedAircraft.addListener(this::handleSelectedAircraftChanges);
        table.getSelectionModel().selectedItemProperty().addListener(this::handleSelectedItemProperty);
    }

    private void handleStatesChanges(SetChangeListener.Change<? extends ObservableAircraftState> change) {
        if(change.wasAdded()){
            ObservableAircraftState state = change.getElementAdded();
            table.getItems().add(state);

            table.sort();
        }
        else if(change.wasRemoved()){
            ObservableAircraftState state = change.getElementRemoved();
            table.getItems().remove(state);
        }
    }

    private void handleSelectedAircraftChanges(Observable observable){
        if(!selectedAircraft.get().equals(table.getSelectionModel().getSelectedItem()))
            table.scrollTo(selectedAircraft.get());
        table.getSelectionModel().select(selectedAircraft.get());
    }

    private void handleSelectedItemProperty(Observable observable){
        selectedAircraft.set(table.getSelectionModel().getSelectedItem());
    }

    private TableColumn<ObservableAircraftState, String> numericColumn(String title,
                                                                       double width,
                                                                       NumberFormat nf,
                                                                       Callback<TableColumn.CellDataFeatures
                                                                           <ObservableAircraftState, String>,
                                                                           ObservableValue<String>> var){
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.getStyleClass().add("numeric");
        column.setPrefWidth(width);
        column.setCellValueFactory(var);
        column.setComparator((s1, s2) -> {
            try {
                return (s1.isEmpty() || s2.isEmpty()) ? s1.compareTo(s2) :
                    Double.compare(nf.parse(s1).doubleValue(), nf.parse(s2).doubleValue());
            }
            catch (ParseException e) {throw new Error(e);}
        });
        return column;
    }

    private TableColumn<ObservableAircraftState, String> textColumn(String title,
                                                                       double width,
                                                                       Callback<TableColumn.CellDataFeatures
                                                                               <ObservableAircraftState, String>,
                                                                               ObservableValue<String>> var){
        TableColumn<ObservableAircraftState,String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(var);
        return column;
    }
    private void createTableLine(){
        var addressIcao = textColumn("OACI",OACI_WIDTH,
            scdf -> new ReadOnlyObjectWrapper<>
                (scdf.getValue().address().string()));

        var callSign =  textColumn("CallSign",CALLSIGN_DESCRIPTION_WIDTH,
            scdf ->
                scdf.getValue().callSignProperty().map(CallSign::string));

        var model = textColumn("Model",MODEL_WIDTH,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(AircraftData::model);});


        var registration = textColumn("Registration",REGISTRATION_WIDTH,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.registration().string());}
        );

        var description = textColumn("Description",CALLSIGN_DESCRIPTION_WIDTH,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.description().string());});

        var typeDesignator = textColumn("Type Designator",TYPE_DESIGNATOR_WIDTH,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.typeDesignator().string());});

        var longitude = numericColumn("Longitude (°)", NUMERIC_COLUMN_WIDTH,
            numberFormat4, scdf -> scdf.getValue().positionProperty().map(pos ->
                        numberFormat4.format(Units.convertTo(pos.longitude(),Units.Angle.DEGREE))));

        var latitude = numericColumn("Latitude (°)",NUMERIC_COLUMN_WIDTH,
            numberFormat4, scdf -> scdf.getValue().positionProperty().map(pos ->
                        numberFormat4.format(Units.convertTo(pos.latitude(),Units.Angle.DEGREE))));

        var altitude = numericColumn("Altitude (m)",NUMERIC_COLUMN_WIDTH,
            numberFormat0, scdf -> scdf.getValue().altitudeProperty().map
                        (alt -> numberFormat0.format(alt.doubleValue())));


        var velocity = numericColumn("Velocity (km/h)",NUMERIC_COLUMN_WIDTH,
            numberFormat0, scdf -> scdf.getValue().velocityProperty().map(speed ->
                        numberFormat0.format(Units.convertTo(speed.doubleValue(),Units.Speed.KILOMETER_PER_HOUR))));


        table.getColumns().setAll(List.of(addressIcao, callSign, registration, model, typeDesignator,
            description, longitude, latitude, altitude, velocity));
    }
}
