package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsbDemodulatorTest {
    @Test
    void WorksOnTrivialString() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            assertEquals(0b10111,
                    RawMessage.typeCode(0b10111011_10010110_10001110_10101010_01010101_10001110_10101010L));
            while ((m = d.nextMessage()) != null){
                System.out.println(m);
                System.out.println(m.downLinkFormat() + " " + m.typeCode() + " " + m.icaoAddress());
            }

        }
    }
}
