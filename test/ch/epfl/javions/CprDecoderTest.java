package ch.epfl.javions;

import ch.epfl.javions.adsb.CprDecoder;
import org.junit.jupiter.api.Test;

import static java.lang.Math.scalb;
import static java.lang.Math.toDegrees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CprDecoderTest {
    private static double cpr(double cpr) {
        return scalb(cpr, -17);
    }
    @Test
    void checkDecodePosition(int cprX0,
                             int cprY0,
                             int cprX1,
                             int cprY1,
                             int mostRecent,
                             double expectedLonDeg,
                             double expectedLatDeg,
                             double delta) {
        var x0 = cpr(cprX0);
        var x1 = cpr(cprX1);
        var y0 = cpr(cprY0);
        var y1 = cpr(cprY1);
        var p = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(p);
        assertEquals(expectedLonDeg, toDegrees(p.longitude()), delta);
        assertEquals(expectedLatDeg, toDegrees(p.latitude()), delta);
    }
    @Test
    void cprDecoderDecodePositionWorksWithOnlyOneLatitudeBand() {
        checkDecodePosition(2458, 92843, 2458, 60712, 0, 6.75, 88.25, 1e-2);
        checkDecodePosition(2458, 92843, 2458, 60712, 1, 6.75, 88.25, 1e-2);
    }
}
