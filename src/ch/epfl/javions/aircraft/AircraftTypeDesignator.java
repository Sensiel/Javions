package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {
    private static final Pattern DesignatorRegex = Pattern.compile("[A-Z0-9]{2,4}");
    /**
     * Compact Constructor
     * @param string : the string associated to the TypeDesignator of the aircraft
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public AircraftTypeDesignator{
        Preconditions.checkArgument(isValidDesignator(string));
    }

    private static boolean isValidDesignator(String string){
        return DesignatorRegex.matcher(string).matches() || string.isEmpty();
    }
}
