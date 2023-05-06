package ch.epfl.javions;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class AircraftTableControllerTest extends Application {
        public static void main(String[] args) { launch(args); }

        static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
            List<RawMessage> result = new ArrayList<>();
            try (DataInputStream s = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream("resources/messages_20230318_0915.bin")))){
                byte[] bytes = new byte[RawMessage.LENGTH];
                while (true) {
                    long timeStampNs = s.readLong();
                    int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                    assert bytesRead == RawMessage.LENGTH;
                    ByteString message = new ByteString(bytes);
                    result.add(new RawMessage(timeStampNs, message));
                }
            } catch (EOFException e) { /* nothing to do */ }
            return result;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Path tileCache = Path.of("tile-cache");
            TileManager tm =
                    new TileManager(tileCache, "tile.openstreetmap.org");
            MapParameters mp =
                    new MapParameters(17, 17_389_327, 11_867_430);
            BaseMapController bmc = new BaseMapController(tm, mp);

            // Création de la base de données
            URL dbUrl = getClass().getResource("/aircraft.zip");
            assert dbUrl != null;
            String f = Path.of(dbUrl.toURI()).toString();
            var db = new AircraftDatabase(f);

            AircraftStateManager asm = new AircraftStateManager(db);
            ObjectProperty<ObservableAircraftState> sap =
                    new SimpleObjectProperty<>();
            AircraftTableController ac= new AircraftTableController(asm.states(),sap);
           // AircraftController ac = new AircraftController(mp, asm.states(), sap);
            var root = new StackPane(bmc.pane(), ac.pane());
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            var mi = readAllMessages("messages_20230318_0915.bin")
                    .iterator();

            // Animation des aéronefs
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) asm.updateWithMessage(m);
                    }
                    asm.purge();
                }
            }.start();
        }
    }

