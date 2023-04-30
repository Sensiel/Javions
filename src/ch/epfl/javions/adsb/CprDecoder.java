package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Represent a CPR position decoder
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public class CprDecoder {
    private static final int ZLAT0 = 60;
    private static final int ZLAT1 = 59;
    private static final double WIDTH_0 = 1d / ZLAT0;
    private static final double WIDTH_1 = 1d / ZLAT1;

    private CprDecoder() {}

    /**
     * Evaluate the geographical positions from the given positions
     * @param x0 : the local longitude of an even message
     * @param y0 : the local latitude of an even message
     * @param x1 : the local longitude of an odd message
     * @param y1 : the local latitude of an odd message
     * @param mostRecent : the parity of the most recent message
     * @return the geographical position corresponding to the given normalized local positions
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        double zoneLat = Math.rint((y0 * ZLAT1) - (y1 * ZLAT0));
        double latEven = (zoneLat < 0d) ? WIDTH_0 * ((zoneLat + ZLAT0) + y0) : WIDTH_0 * (zoneLat + y0);
        double latOdd = (zoneLat < 0d) ? WIDTH_1 * ((zoneLat + ZLAT1) + y1) : WIDTH_1 * (zoneLat + y1);

        double resultLat;
        resultLat = (mostRecent == 0) ? latEven : latOdd;

        double aOdd = Math.acos(1d - ((1d - Math.cos(Units.Angle.TURN * WIDTH_0))
                / Math.pow(Math.cos(Units.convertFrom(latOdd, Units.Angle.TURN)), 2d)));
        double aEven = Math.acos(1d - ((1d - Math.cos(Units.Angle.TURN * WIDTH_0))
                / Math.pow(Math.cos(Units.convertFrom(latEven, Units.Angle.TURN)), 2d)));

        if((int)Math.floor(Units.Angle.TURN / aEven) != (int)Math.floor(Units.Angle.TURN / aOdd))
            return null;

        double A = Math.acos(1d - ((1d - Math.cos(Units.Angle.TURN * WIDTH_0))
                / Math.pow(Math.cos(Units.convertFrom(resultLat, Units.Angle.TURN) ), 2d)));
        double resultLong;

        if (Double.isNaN(A))
            resultLong = (mostRecent == 0) ? x0 : x1;
        else {
            double zLong0 = Math.floor(Units.Angle.TURN / A);
            double zLong1 = zLong0 - 1d;
            double zoneLong = Math.rint((x0 * zLong1) - (x1 * zLong0));

            if (zoneLong < 0)
                resultLong = (mostRecent == 0) ? (1d / zLong0) * (zoneLong + zLong0 + x0) : (1d / zLong1) * (zoneLong + zLong1 + x1);
            else
                resultLong = (mostRecent == 0) ? (1d / zLong0) * (zoneLong + x0) : (1d / zLong1) * (zoneLong + x1);
        }

        if (resultLat >= 0.5d)
            resultLat -= 1;

        if (resultLong >= 0.5d)
            resultLong -= 1;

        int latT32 = (int) Math.rint(Units.convert(resultLat, Units.Angle.TURN, Units.Angle.T32));
        int longT32 = (int) Math.rint(Units.convert(resultLong, Units.Angle.TURN, Units.Angle.T32));

        if(!GeoPos.isValidLatitudeT32(latT32)) return null;

        return new GeoPos(longT32, latT32);
    }
}




