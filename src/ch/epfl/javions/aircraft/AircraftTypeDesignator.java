package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {
    private static final Pattern DesignatorRegex = Pattern.compile("[A-Z0-9]{2,4}");
    public AircraftTypeDesignator{
        if(!isValidDesignator(string)) throw new IllegalArgumentException();
    }

    private static boolean isValidDesignator(String string){
        return DesignatorRegex.matcher(string).matches() || string.isEmpty();
    }
}
