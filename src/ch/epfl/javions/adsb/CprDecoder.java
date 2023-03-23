package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private static final int ZLAT0 = 60;
    private static final int ZLAT1 = 59;
    private static final double LARGEUR_0 = 1d / ZLAT0;
    private static final double LARGEUR_1 = 1d / ZLAT1;

    private CprDecoder() {} //pour le rendre non instanciable

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
        //latitude

        y0 = Math.scalb(y0,-17);
        y1 = Math.scalb(y1,-17);

        double zoneLat = Math.rint((y0 * ZLAT1) - (y1 * ZLAT0));
        double latPair = (zoneLat < 0d) ? LARGEUR_0 * ((zoneLat + ZLAT0) + y0) : LARGEUR_0 * (zoneLat + y0);
        double latImp = (zoneLat < 0d) ? LARGEUR_1 * ((zoneLat + ZLAT1) + y1) : LARGEUR_1 * (zoneLat + y1);

        double resultLat;
        resultLat = (mostRecent == 0) ? latPair : latImp;
        // debut longitude

        // verification de l'égalité des zones
        // Faut changer de TURN à Radian je pense
        double aImp = Math.acos(1d - ((1d - Math.cos(2d * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(latImp), 2d)));
        double aPair = Math.acos(1d - ((1d - Math.cos(2d * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(latPair), 2d)));
        if(aPair != aImp) return null;

        x0 = Math.scalb(x0,-17);
        x1 = Math.scalb(x1,-17);

        double A = Math.acos(1d - ((1d - Math.cos(2d * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(resultLat), 2d)));
        double resultLong;
        if (Double.isNaN(A)) {
            resultLong = (mostRecent == 0) ? x0 : x1;
        }
        else {
            double zLong0 = Math.floor(2d * Math.PI / A);
            double zLong1 = zLong0 - 1d;
            double zoneLong = Math.rint((x0 * zLong1) - (x1 * zLong0));

            if (zoneLong < 0)
                resultLong = (mostRecent == 0) ? (1d / zLong0) * (zoneLong + zLong0 + x0) : (1d / zLong1) * (zoneLong + zLong1 + x1);
            else
                resultLong = (mostRecent == 0) ? (1d / zLong0) * (zoneLong + x0) : (1d / zLong1) * (zoneLong + x1);
        }
        // creer methode prv pour recentrer
        if (resultLat >= 1d / 2d * Units.Angle.TURN) {
            resultLat -= Units.Angle.TURN;
        }
        if (resultLong >= 1d / 2d * Units.Angle.TURN) {
            resultLong -= Units.Angle.TURN;
        }

        //creer methode prv pour check validité de lat
        double resultLatDegree = Units.convert(resultLat, Units.Angle.TURN, Units.Angle.DEGREE);
        if (resultLatDegree < -90d || resultLatDegree > 90d) return null;

        int latT32 = (int) Math.rint(Units.convert(resultLat, Units.Angle.TURN, Units.Angle.T32));
        int longT32 = (int) Math.rint(Units.convert(resultLong, Units.Angle.TURN, Units.Angle.T32));
        return new GeoPos(longT32, latT32);
    }
}




