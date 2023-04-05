package ch.epfl.javions;

public class WebMercator {
    /**
     * Project the longitude
     * @param zoomLevel : the level of the zoom
     * @param longitude : the given longitude
     * @return the coordinate x corresponding to the longitude and zoomLevel given
     */
    public static double x(int zoomLevel, double longitude){
        return Math.scalb(Units.convertTo(longitude, Units.Angle.TURN) + 0.5f, 8 + zoomLevel);
    }

    /**
     * Project the latitude
     * @param zoomLevel : the level of the zoom
     * @param latitude : the given latitude
     * @return the coordinate y corresponding to the longitude and zoomLevel given
     */
    public static double y(int zoomLevel, double latitude){
        return Math.scalb(-Units.convertTo( Math2.asinh( Math.tan(latitude) ), Units.Angle.TURN) + 0.5f, 8 + zoomLevel);
    }

}
