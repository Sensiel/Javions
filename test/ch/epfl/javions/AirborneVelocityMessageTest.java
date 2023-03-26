package ch.epfl.javions;

import ch.epfl.javions.adsb.AirborneVelocityMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AirborneVelocityMessageTest {
    @Test
    void returnTrivialValue() throws IOException {
        String f = "resources/samples_20230304_1442 3.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
                System.out.println(AirborneVelocityMessage.of(m));
        }
    }
}
