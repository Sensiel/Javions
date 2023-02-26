package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    private static final Pattern RegistrationRegex = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration{
        if(!isValidRegistration(string)) throw new IllegalArgumentException();
    }

    private static boolean isValidRegistration(String string){
        return RegistrationRegex.matcher(string).matches() && !string.isEmpty();
    }
}
