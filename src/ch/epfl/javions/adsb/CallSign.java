package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record CallSign(String string) {
    private static final Pattern CallSignRegex = Pattern.compile("[A-Z0-9 ]{0,8}");
    /**
     * Compact Constructor
     * @param string : the string associated to the call sign
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public CallSign{
        Preconditions.checkArgument(isValidCallSign(string));
    }

    /**
     * @param string : the given CallSign
     * @return isValid : a boolean indicating if the given CallSign is valid
     */
    private static boolean isValidCallSign(String string){
        return CallSignRegex.matcher(string).matches() || string.isEmpty();
    }
}