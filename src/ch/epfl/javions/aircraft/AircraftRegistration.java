package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    private static final Pattern RegistrationRegex = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Compact Constructor
     * @param string
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public AircraftRegistration{
        Preconditions.checkArgument(isValidRegistration(string));
    }

    private static boolean isValidRegistration(String string){
        return RegistrationRegex.matcher(string).matches() && !string.isEmpty();
    }
}
