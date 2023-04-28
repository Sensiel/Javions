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
    private MapParameters mapParameters;
    private Canvas canvas = new Canvas();
    private Pane pane = new Pane();
    private boolean redrawNeeded; // vrai si un redessin de la carte est nécessaire
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
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        // faut draw toutes les tuiles visibles #1404 (faut det ces tuiles visibles)
        // rjt try with ressources + qd on catch une exception on dessine r
        graphicsContext.drawImage(tileManager.imageForTileAt(
                new TileManager.TileId(mapParameters.getZoom(), mapParameters.getMinX(), mapParameters.getMinY()),
                ));

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        pane = new StackPane(canvas); //pas sure
        return pane;
    }
    public void centerOn(GeoPos pos){ // pas sure non plus
        double x = WebMercator.x(mapParameters.getZoom(),pos.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), pos.latitude());
        mapParameters.scroll(x, y);
    }
// si mapparameters changé ou dimension canvas changé --> redessin carte
// pr faire ca : creer des auditeurs java qui detectent ces changements et appeler redrawOnNextPulse() dès que ya chgmt
    private void redrawIfNeeded() {
        if (redrawNeeded) {
            redrawNeeded = false;
            // dessin de la carte
        }
    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
