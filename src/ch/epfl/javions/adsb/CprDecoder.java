package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private static final int ZLAT0 = 60;
    private static final int ZLAT1 = 59;
    private static final int LARGEUR_0 = 1 / ZLAT0;
    private static final int LARGEUR_1 = 1 / ZLAT1;

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
        double resultLat;
        double latPair, latImp;
        y0 = Math.scalb(y0,-17);
        y1 = Math.scalb(y1,-17);
        double zoneLat = Math.rint((y0 * ZLAT1) - (y1 * ZLAT0));
        if (zoneLat < 0) {
                latPair = LARGEUR_0 * ((zoneLat + ZLAT0) + y0);
                latImp = LARGEUR_1 * ((zoneLat + ZLAT1) + y1);
        } else {
                latPair = LARGEUR_0 * (zoneLat + y0);
                latImp = LARGEUR_1 * (zoneLat + y1);
        }

        if(mostRecent == 0){
            resultLat = latPair;
        } else {
            resultLat = latImp;
        }

        //creer methode prv pour check validité de lat
        double resultLatDegree = Units.convert(resultLat, Units.Angle.TURN, Units.Angle.DEGREE);

        if (!(resultLatDegree >= -90 && resultLatDegree <= 90)) // jsp si 90 et -90 inclus ?
            return null;

        // fin latitude

        // debut longitude

        // verification de l'égalité des zones
        double aImp = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(latImp), 2)));
        double aPair = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(latPair), 2)));
        if(aPair != aImp){ return null; }

        double resultLong;
        x0 = Math.scalb(x0,-17);
        x1 = Math.scalb(x1,-17);
        double A = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * LARGEUR_0)) / Math.pow(Math.cos(resultLat), 2)));
        if (Double.isNaN(A)) {// normalement je dois utiliser isNaN()
            if (mostRecent == 0) {
                resultLong = x0;
            } else {
                resultLong = x1;
            }
        } else {
            double zLong0 = Math.floor(2 * Math.PI / A);
            double zLong1 = zLong0 - 1;
            double zoneLong = Math.rint((x0 * zLong1) - (x1 * zLong0));

            if (zoneLong < 0) {
                if (mostRecent == 0) {
                    resultLong = (1 / zLong0) * ((zoneLong + zLong0) + x0);
                } else {
                    resultLong = (1 / zLong1) * ((zoneLong + zLong1) + x1);
                }
            } else {
                if (mostRecent == 0) {
                    resultLong = (1 / zLong0) * (zoneLong + x0);
                } else {
                    resultLong = (1 / zLong1) * (zoneLong + x1);
                }
            }
        }
        // creer methode prv pour recentrer
        if (resultLat >= 1 / 2 * Units.Angle.TURN) {
            resultLat -= Units.Angle.TURN;
        }
        if (resultLong >= 1 / 2 * Units.Angle.TURN) {
            resultLong -= Units.Angle.TURN;
        }

        int latT32 = (int) Math.rint(Units.convert(resultLat, Units.Angle.TURN, Units.Angle.T32));
        int longT32 = (int) Math.rint(Units.convert(resultLong, Units.Angle.TURN, Units.Angle.T32));
        // verifier que les nv x1/x0/y1/y0 sont compris entre 0 et 1 ? #661 ?
        return new GeoPos(longT32, latT32);
    }
}




