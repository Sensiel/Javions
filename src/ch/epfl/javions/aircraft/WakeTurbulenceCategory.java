package ch.epfl.javions.aircraft;

/**
 * Represent the wake turbulence category of the aircraft
 * @author Zablocki Victor (361602)
 */
public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * Convert the textual values of the database into elements of the enumerated type
     * @param s : the abbreviation of the WTC
     * @return the wake turbulence category corresponding to the given string
     */
    public static WakeTurbulenceCategory of(String s){
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}
