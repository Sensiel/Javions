package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> states;
    private ObjectProperty<ObservableAircraftState> selectedAircraft;

    private final static double MAX_ALTITUDE = 12000d;

    private final Pane pane;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> states,
                              ObjectProperty<ObservableAircraftState> selectedAircraft){
        this.mapParameters = mapParameters;
        this.states = states;
        this.selectedAircraft = selectedAircraft;
        pane = new Pane();
        pane.getStylesheets().add("aircraft.css");
        pane.setPickOnBounds(false);
        createListeners();
    }

    public Pane pane(){
        return pane;
    }

    private void createListeners(){
        states.addListener(this::handleStatesChanges);
    }

    private void handleStatesChanges(SetChangeListener.Change<? extends ObservableAircraftState> change) {
        if(change.wasAdded()){
            ObservableAircraftState state = change.getElementAdded();
            Group aircraft = createAircraftGroup(state);
            pane.getChildren().add(aircraft);
        }
        else if(change.wasRemoved()){
            ObservableAircraftState state = change.getElementRemoved();
            pane.getChildren().removeIf(currAircraft -> currAircraft.getId().equals(state.address().string()));
        }
    }
    private Group createAircraftGroup(ObservableAircraftState state){
        Group aircraft = new Group(iconAndLabel(state));
        aircraft.setId(state.address().string());
        aircraft.viewOrderProperty().bind(state.altitudeProperty().negate());

        return aircraft;
    }

    private Group iconAndLabel(ObservableAircraftState state){
        Group iconAndLabel = new Group(createAircraftIcon(state), createLabel(state));

        DoubleBinding webMYBinding = Bindings.createDoubleBinding(() ->
            WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()) - mapParameters.getMinY(),
                mapParameters.zoomProperty(),
                state.positionProperty(),
                mapParameters.minYProperty());

        DoubleBinding webMXBinding = Bindings.createDoubleBinding(() ->
            WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()) - mapParameters.getMinX(),
                mapParameters.zoomProperty(),
                state.positionProperty(),
                mapParameters.minXProperty());


        iconAndLabel.layoutYProperty().bind(webMYBinding);
        iconAndLabel.layoutXProperty().bind(webMXBinding);
        return iconAndLabel;
    }

    private SVGPath createAircraftIcon(ObservableAircraftState state) {
        SVGPath svgIcon = new SVGPath();
        svgIcon.getStyleClass().add("aircraft");
        AircraftData data = state.getData();

        AircraftIcon aircraftIcon;

        //Todo c'est pas très joli
        boolean isDataNull = data != null;

        aircraftIcon = AircraftIcon.iconFor(
            (isDataNull && data.typeDesignator() != null) ?
                data.typeDesignator() :
                new AircraftTypeDesignator(""),
            (isDataNull && data.description() != null) ?
                data.description() :
                new AircraftDescription(""),
            state.getCategory(),
            (isDataNull && data.wakeTurbulenceCategory() != null) ?
                data.wakeTurbulenceCategory() :
                WakeTurbulenceCategory.UNKNOWN);


        svgIcon.setContent(aircraftIcon.svgPath());
        svgIcon.rotateProperty().bind(Bindings.createDoubleBinding(()->
                Units.convertTo(state.getTrackOrHeading(), Units.Angle.DEGREE), state.trackOrHeadingProperty()));

        svgIcon.fillProperty().bind(Bindings.createObjectBinding(() ->
            ColorRamp.PLASMA.at(normalizedAltitude(state.getAltitude())),
                state.altitudeProperty()));

        return svgIcon;
    }

    private Group createLabel(ObservableAircraftState state) {
        Text txt = createLabelText(state);
        Rectangle rect = createLabelRect(state, txt);
        Group label = new Group(rect, txt);
        label.getStyleClass().add("label");

        //Todo ajouter condition visible ( avec bind comme le reste )
        return label;
    }

    private Text createLabelText(ObservableAircraftState state) {
        Text txt = new Text();
        //Todo Ben le faire quoi
        return txt;
    }

    private Rectangle createLabelRect(ObservableAircraftState state, Text txt) {
        Rectangle rect = new Rectangle();
        rect.setFill(new Color(0f,0f,0f,.5f ));
        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        return rect;
    }

    private double normalizedAltitude(double alt){
        return Math.cbrt(alt/MAX_ALTITUDE);
    }
}
