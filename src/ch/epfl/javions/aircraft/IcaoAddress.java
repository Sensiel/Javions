package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    private static final Pattern IcaoRegex = Pattern.compile("[0-9A-F]{6}");

    /**
     * Compact Constructor
     * @param string
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public IcaoAddress{
        Preconditions.checkArgument(isValidIcaoAddress(string));
    }

    private static boolean isValidIcaoAddress(String string){
        return IcaoRegex.matcher(string).matches() && !string.isEmpty();
    }
}
