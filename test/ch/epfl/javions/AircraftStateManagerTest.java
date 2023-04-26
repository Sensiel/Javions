package ch.epfl.javions;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Comparator;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

public class AircraftStateManagerTest {
    @Test
    void trivialTest() {
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (IOException e) { /* nothing to do */ }
    }

}
