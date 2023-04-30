package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represent the description of the aircraft
 * @author Zablocki Victor (361602)
 * @param string : the string associated to the description of the aircraft
 */
public record AircraftDescription(String string) {
    private static final Pattern DESCRIPTION_REGEX = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Compact Constructor
     * @param string : the string associated to the description of the aircraft
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public AircraftDescription{
        Preconditions.checkArgument(isValidDescription(string));
    }

    private static boolean isValidDescription(String string){
        return DESCRIPTION_REGEX.matcher(string).matches() || string.isEmpty();
    }
}