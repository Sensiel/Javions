package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public final class BaseMapController {
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Canvas canvas = new Canvas();
    private Pane pane = new Pane();
    private boolean needRedraw;
    private ObjectProperty<Point2D> lastPosMouse = new SimpleObjectProperty<>();

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

    }
    public Pane pane() throws IOException {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        pane = new Pane(canvas);
        return pane;
    }
    public void centerOn(GeoPos pos){
        double x = WebMercator.x(mapParameters.getZoom(), pos.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), pos.latitude());
        mapParameters.scroll(x - mapParameters.getMinX(), y - mapParameters.getMinX()); // pas certain que WebMercator permettent les additions
    }

// si mapparameters changé ou dimension canvas changé --> redessin carte
// pr faire ca : creer des auditeurs java qui detectent ces changements et appeler redrawOnNextPulse() dès que ya chgmt

    private void redrawIfNeeded() {
        if (!needRedraw) return;
        needRedraw = false;
    }
    private void redrawOnNextPulse() {
        needRedraw = true;
        Platform.requestNextPulse();
    }
}
