package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represent an ICAO address
 * @author Imane Raihane (362230)
 * @param string : the string associated to the ICAO address
 */
public record IcaoAddress(String string) {
    private static final Pattern ICAO_REGEX = Pattern.compile("[0-9A-F]{6}");

    /**
     * Compact Constructor
     * @param string : the string associated to the ICAO address
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public IcaoAddress{
        Preconditions.checkArgument(isValidIcaoAddress(string));
    }

    private static boolean isValidIcaoAddress(String string){
        return ICAO_REGEX.matcher(string).matches() && !string.isEmpty();
    }
}
