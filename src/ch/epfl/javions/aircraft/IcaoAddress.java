package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    private static final Pattern IcaoRegex = Pattern.compile("[0-9A-F]{6}");
    public IcaoAddress{
        if(!isValidIcaoAddress(string)) throw new IllegalArgumentException();
    }

    private static boolean isValidIcaoAddress(String string){
        return IcaoRegex.matcher(string).matches() && !string.isEmpty();
    }
}
