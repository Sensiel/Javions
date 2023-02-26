package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {
    private static final Pattern DescriptionRegex = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription{
        if(!isValidDescription(string)) throw new IllegalArgumentException();
    }

    private static boolean isValidDescription(String string){
        return DescriptionRegex.matcher(string).matches() || string.isEmpty();
    }
}