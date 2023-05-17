package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Contain the main program which represent a JavaFX application
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class Main extends Application {
    private Queue<RawMessage> messages;
    private long bootTime ;
    private long bootTimeAT;

    /**
     *
     * @param primaryStage : the main window of the application
     * @throws Exception if there's un URL exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        bootTime = System.nanoTime();
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
        atc.setOnDoubleClick(oas -> bmc.centerOn(oas.getPosition()));
        // TODO La sélection d'un aéronef dans la table provoque sa sélection dans la vue des aéronefs, et inversement

        StackPane sp = new StackPane(bmc.pane(), ac.pane());
        BorderPane bp =  new BorderPane();
        bp.setCenter(atc.pane());
        bp.setTop(slc.pane());

        var root = new SplitPane(sp,bp);
        root.setOrientation(Orientation.VERTICAL);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        messages = new ConcurrentLinkedQueue<>();

        var t = new Thread(() -> {
            try {
                collectMessages();
            } catch (IOException e) {
                throw new Error(e);
            }
        });
        t.setDaemon(true);
        t.start();

        bootTimeAT = System.nanoTime();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while(!messages.isEmpty()) {
                    Message m = MessageParser.parse(messages.poll());
                    if (m != null) {
                        asm.updateWithMessage(m);
                        slc.getMessageCountProperty().set(slc.getMessageCountProperty().get() + 1);
                    }
                }
                if(bootTimeAT - now >= 10E9) {
                    asm.purge();
                }
            }
        }.start();
    }
    private void collectMessages() throws IOException {
        List<String> args = this.getParameters().getRaw();
        while (true) {
        if (args.isEmpty()) {
            AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
            messages.add(demodulator.nextMessage());
        } else {
                try (DataInputStream s = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(args.get(0))))) {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    while (true) {
                        long timeStampNs = s.readLong();
                        int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                        assert bytesRead == RawMessage.LENGTH;
                        ByteString message = new ByteString(bytes);
                        long elapsedTime = System.nanoTime() - bootTime;
                        bootTime = System.nanoTime();
                        if (timeStampNs == elapsedTime) {
                            messages.add(new RawMessage(timeStampNs, message));
                        } else {
                            if (timeStampNs >= elapsedTime) {
                                Thread.sleep((long) ((timeStampNs - elapsedTime) * 10E-6));
                                messages.add(new RawMessage(timeStampNs, message));
                            }
                        }
                    }
                } catch (EOFException e) { /* nothing to do */ }
                catch (InterruptedException e) { throw new Error();}
            }
        }
    }

    public static void main(String[] args) { launch(args);}
}
