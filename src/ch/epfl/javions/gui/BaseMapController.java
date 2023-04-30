package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public final class BaseMapController {
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private boolean needRedraw;
    private ObjectProperty<Point2D> lastPosMouse = new SimpleObjectProperty<>();

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        canvas = new Canvas();
        pane = new Pane(canvas);

        createListeners();
    }
    public Pane pane(){
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

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        int minTileX = (int)Math.floor(mapParameters.getMinX()/256d);
        double maxX = mapParameters.getMinX() + canvas.getWidth();
        int maxTileX = (int)Math.floor(maxX/256d);

        int minTileY = (int)Math.floor(mapParameters.getMinY()/256d);
        double maxY = mapParameters.getMinY() + canvas.getHeight();
        int maxTileY = (int)Math.floor(maxY/256d);

        for(int y = minTileY; y <= maxTileY; y++){
            for(int x = minTileX; x <= maxTileX; x++){

                if(!TileManager.TileId.isValid(mapParameters.getZoom(), x, y))
                    continue;
                TileManager.TileId tileId = new TileManager.TileId(mapParameters.getZoom(), x, y);
                try{
                    graphicsContext.drawImage(tileManager.imageForTileAt(tileId),
                            (x * 256) - mapParameters.getMinX(),
                            (y * 256) - mapParameters.getMinY());

                }
                catch(IOException ignored){}

            }
        }
    }
    private void redrawOnNextPulse() {
        needRedraw = true;
        Platform.requestNextPulse();
    }

    private void createListeners(){
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 50); // TODO mettre à 200
            mapParameters.scroll(e.getX(), e.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-e.getX(), -e.getY());
        });

        DoubleProperty oldMouseX = new SimpleDoubleProperty();
        DoubleProperty oldMouseY = new SimpleDoubleProperty();

        pane.setOnMousePressed(e -> {
            oldMouseX.set(e.getX());
            oldMouseY.set(e.getY());
        });

        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(oldMouseX.get() - e.getX(), oldMouseY.get() - e.getY());
            oldMouseX.set(e.getX());
            oldMouseY.set(e.getY());
        });


        pane.widthProperty().addListener((obs, oldVal, newVal) -> redrawOnNextPulse());
        pane.heightProperty().addListener((obs, oldVal, newVal) -> redrawOnNextPulse());
        mapParameters.minXProperty().addListener((obs, oldVal, newVal) -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener((obs, oldVal, newVal) -> redrawOnNextPulse());
        mapParameters.zoomProperty().addListener((obs, oldVal, newVal) -> redrawOnNextPulse());
    }
}
