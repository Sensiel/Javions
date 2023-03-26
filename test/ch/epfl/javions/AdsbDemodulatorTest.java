package ch.epfl.javions;

import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.CprDecoder;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

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
            /*while ((m = d.nextMessage()) != null){
                if(AircraftIdentificationMessage.of(m) != null)
                    System.out.println(AircraftIdentificationMessage.of(m));
                if(AirbornePositionMessage.of(m) != null)
                    System.out.println(AirbornePositionMessage.of(m));
            }*/

            System.out.println(AirbornePositionMessage.of(RawMessage.of(0, HexFormat.of().parseHex("8D39203559B225F07550ADBE328F"))));
            System.out.println(AirbornePositionMessage.of(RawMessage.of(0, HexFormat.of().parseHex("8DAE02C85864A5F5DD4975A1A3F5"))));
            System.out.println(CprDecoder.decodePosition(0.62,0.42,0.6200000000000000001,0.4200000000000000001,0));
            System.out.println(CprDecoder.decodePosition(Math.scalb(111600,-17), Math.scalb(94445,-17), Math.scalb(108865,-17), Math.scalb(77558,-17), 0));
        }
    }
}
