package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {
    private static final Pattern DescriptionRegex = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Compact Constructor
     * @param string
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public AircraftDescription{
        Preconditions.checkArgument(isValidDescription(string));
    }

    private static boolean isValidDescription(String string){
        return DescriptionRegex.matcher(string).matches() || string.isEmpty();
    }
}