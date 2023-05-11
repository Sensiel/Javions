package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {

    Queue<RawMessage> messages;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(8, 33530, 23070);

        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        ObjectProperty<ObservableAircraftState> selAircraft = new SimpleObjectProperty<>();

        AircraftStateManager asm = new AircraftStateManager(db);
        BaseMapController bmc = new BaseMapController(tm, mp);
        StatusLineController slc = new StatusLineController();
        AircraftTableController atc= new AircraftTableController(asm.states(), selAircraft);
        AircraftController ac = new AircraftController(mp, asm.states(), selAircraft);

        slc.getAircraftCountProperty().bind(Bindings.size(asm.states()));

        StackPane sp = new StackPane(bmc.pane(), ac.pane());
        BorderPane bp =  new BorderPane();
        bp.setCenter(atc.pane());
        bp.setTop(slc.pane());

        var root = new SplitPane(sp,bp);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        messages = new ConcurrentLinkedQueue<>();

        //TODO lire les messages et les mettre dans la file

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while(!messages.isEmpty()) {
                    Message m = MessageParser.parse(messages.poll());
                    if (m != null) asm.updateWithMessage(m);
                }
                asm.purge();
            }
        }.start();
    }

    public static void main(String[] args) { launch(args); }
}
