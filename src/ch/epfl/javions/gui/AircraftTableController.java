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

public final class AircraftTableController {
    private final ObservableSet<ObservableAircraftState> states;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final TableView<ObservableAircraftState> table = new TableView<>();;
    private final NumberFormat numberFormat4 = NumberFormat.getInstance();
    private final NumberFormat numberFormat0 = NumberFormat.getInstance();
    private Consumer<ObservableAircraftState> stateConsumer;

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

        numberFormat4.setMinimumFractionDigits(4);
        numberFormat4.setMaximumFractionDigits(4);

        numberFormat0.setMinimumFractionDigits(0);
        numberFormat0.setMaximumFractionDigits(0);
        createTableLine();
    }
    public TableView<ObservableAircraftState> pane(){ return table; }
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
        table.getSelectionModel().select(selectedAircraft.get());
        if(!selectedAircraft.get().equals(table.getSelectionModel().getSelectedItem()))
            table.scrollTo(selectedAircraft.get());
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
                return (s1 == "" || s2 == "") ? s1.compareTo(s2) :
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
        var addressIcao = textColumn("OACI",60,
            scdf -> new ReadOnlyObjectWrapper<>
                (scdf.getValue().address().string()));

        var callSign =  textColumn("CallSign",70,
            scdf ->
                scdf.getValue().callSignProperty().map(CallSign::string));

        var model = textColumn("Model",230,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(AircraftData::model);});


        var registration = textColumn("Registration",90,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.registration().string());}
        );

        var description = textColumn("Description",70,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.description().string());});

        var typeDesignator = textColumn("Type Designator",50,
            scdf -> { AircraftData ad = scdf.getValue().getData();
                return new ReadOnlyObjectWrapper<>(ad).map(d -> d.typeDesignator().string());});

        var longitude = numericColumn("Longitude (°)", 85,
            numberFormat4, scdf -> scdf.getValue().positionProperty().map(pos ->
                        numberFormat4.format(Units.convertTo(pos.longitude(),Units.Angle.DEGREE))));

        var latitude = numericColumn("Latitude (°)",85,
            numberFormat4, scdf -> scdf.getValue().positionProperty().map(pos ->
                        numberFormat4.format(Units.convertTo(pos.latitude(),Units.Angle.DEGREE))));

        var altitude = numericColumn("Altitude (m)",85,
            numberFormat0, scdf -> scdf.getValue().altitudeProperty().map
                        (alt -> numberFormat0.format(alt.doubleValue())));


        var velocity = numericColumn("Velocity (km/h)",85,
            numberFormat0, scdf -> scdf.getValue().velocityProperty().map(speed ->
                        numberFormat0.format(Units.convertTo(speed.doubleValue(),Units.Speed.KILOMETER_PER_HOUR))));


        table.getColumns().setAll(List.of(addressIcao, callSign, registration, model, typeDesignator,
            description, longitude, latitude, altitude, velocity));
    }
}
