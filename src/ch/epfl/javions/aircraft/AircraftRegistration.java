package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represent the registration of the Aircraft
 * @author Imane Raihane (362230)
 * @param string : the string  associated to the registration of the aircraft
 */
public record AircraftRegistration(String string) {
    private static final Pattern REGISTRATION_REGEX = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Compact Constructor
     * @param string : the string  associated to the registration of the aircraft
     * @throws IllegalArgumentException if the given string isn't valid
     */
    public AircraftRegistration{
        Preconditions.checkArgument(isValidRegistration(string));
    }

    private static boolean isValidRegistration(String string){
        return REGISTRATION_REGEX.matcher(string).matches() && !string.isEmpty();
    }
}
