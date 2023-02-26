package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

public record CallSign(String string) {
    private static final Pattern CallSignRegex = Pattern.compile("[A-Z0-9 ]{0,8}");
    public CallSign{
        if(!isValidCallSign(string)) throw new IllegalArgumentException();
    }

    private static boolean isValidCallSign(String string){
        return CallSignRegex.matcher(string).matches() || string.isEmpty();
    }
}