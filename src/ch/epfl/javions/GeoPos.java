package ch.epfl.javions;

/**
 * Represent geographic coordinates
 * @author Zablocki Victor (361602)
 * @param longitudeT32 : longitude in the T32 unit
 * @param latitudeT32 : latitude in the T32 unit
 */
public record GeoPos(int longitudeT32, int latitudeT32){
    private final static int MIN_LAT32 = -(1 << 30);
    private final static int MAX_LAT32 = 1 << 30;
    /**
     * Compact Constructor
     * @param longitudeT32 : longitude in the T32 unit
     * @param latitudeT32 : latitude in the T32 unit
     * @throws IllegalArgumentException if the given latitude is invalid
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * Check if latitude32 is in the right interval
     * @param latitudeT32 : latitude in the T32 unit
     * @return true if the given latitude is valid
     */

    public static boolean isValidLatitudeT32(int latitudeT32){
        return latitudeT32 >= MIN_LAT32 && latitudeT32 <= MAX_LAT32;
    }

    /**
     * Convert longitude from T32 to the basic unit
     * @return longitudeT32 in the basic unit
     */

    public double longitude(){
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }
    /**
     * Convert latitude from T32 to the basic unit
     * @return latitudeT32 in the basic unit
     */

    public double latitude(){
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    @Override
    public String toString(){
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, " + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }
}