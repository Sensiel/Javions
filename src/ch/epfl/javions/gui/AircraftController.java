package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import ch.epfl.javions.gui.ObservableAircraftState.AirbornePos;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

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
        Group aircraft = new Group(iconAndLabel(state), trajectoryGroup(state));
        aircraft.setId(state.address().string());
        aircraft.viewOrderProperty().bind(state.altitudeProperty().negate());
        aircraft.setOnMouseClicked(event -> selectedAircraft.set(state));
        return aircraft;
    }

    private Group trajectoryGroup(ObservableAircraftState state){
        Group trajectoryGroup = new Group();
        trajectoryGroup.visibleProperty().bind(selectedAircraft.isEqualTo(state));
        trajectoryGroup.getStyleClass().add("trajectory");
        trajectoryGroup.visibleProperty().addListener(
            (observable, oldValue, newValue) -> updateTrajectory(state, trajectoryGroup));
        state.trajectoryProperty().addListener(
            (ListChangeListener<? super AirbornePos>) c -> updateTrajectory(state, trajectoryGroup));
        mapParameters.zoomProperty().addListener(
            (observable, oldValue, newValue) -> updateTrajectory(state, trajectoryGroup));
        return trajectoryGroup;
    }

    private void updateTrajectory(ObservableAircraftState state, Group trajectoryGroup) {
        trajectoryGroup.getChildren().clear();

        if(trajectoryGroup.isVisible()){
            ObservableList<AirbornePos> trajectory = state.trajectoryProperty();
            for(int iLine = 0; iLine < trajectory.size() - 1; iLine++) {
                AirbornePos aPos1 = trajectory.get(iLine);
                AirbornePos aPos2 = trajectory.get(iLine + 1);
                if (aPos1.pos() == null)
                    continue;

                Point2D wmPos1 = new Point2D(
                        WebMercator.x(mapParameters.getZoom(), aPos1.pos().longitude()),
                        WebMercator.y(mapParameters.getZoom(), aPos1.pos().latitude()));
                Point2D wmPos2 = new Point2D(
                        WebMercator.x(mapParameters.getZoom(), aPos2.pos().longitude()),
                        WebMercator.y(mapParameters.getZoom(), aPos2.pos().latitude()));

                Line currLine = new Line(
                        wmPos1.getX(), wmPos1.getY(),
                        wmPos2.getX(), wmPos2.getY());

                currLine.layoutXProperty().bind(mapParameters.minXProperty().negate());
                currLine.layoutYProperty().bind(mapParameters.minYProperty().negate());

                if(Double.compare(aPos1.altitude(), aPos2.altitude()) == 0)
                    currLine.setStroke(
                        ColorRamp.PLASMA.at(
                            normalizedAltitude(aPos1.altitude())));
                else{
                    Stop s1 = new Stop(0,
                        ColorRamp.PLASMA.at(
                            normalizedAltitude(aPos1.altitude())));
                    Stop s2 = new Stop(1,
                        ColorRamp.PLASMA.at(
                                normalizedAltitude(aPos2.altitude())));
                    currLine.setStroke(
                        new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));
                }
                trajectoryGroup.getChildren().add(currLine);
            }
        }
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

        label.visibleProperty().bind(mapParameters
                .zoomProperty()
                .greaterThanOrEqualTo(11)
                .or(selectedAircraft.isEqualTo(state)));
        return label;
    }

    private Text createLabelText(ObservableAircraftState state) {
        Text txt = new Text();
        txt.textProperty().bind(Bindings.createStringBinding(()-> {
            String callSignOrRegistration;
            /*----------------First Line----------------*/
            if(state.getData() == null || state.getData().registration() == null)
                callSignOrRegistration = (state.getCallSign() == null) ?
                    state.address().string():
                    state.getCallSign().string();
            else
                callSignOrRegistration = state.getData().registration().string();
            /*----------------Second Line----------------*/
            String velocity = state.getVelocity() == 0 ?
                "?" : String.valueOf((int)state.getVelocity());
            String altitude = state.getAltitude() == 0 ?
                "?" : String.valueOf((int)state.getAltitude());
            return String.format("%s\n%s km/h\u2002%s m", callSignOrRegistration, velocity, altitude);
        },state.callSignProperty(), state.altitudeProperty(), state.velocityProperty()));
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
