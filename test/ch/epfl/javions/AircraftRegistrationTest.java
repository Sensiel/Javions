package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftRegistrationTest {
    @Test
    void AircraftRegistrationThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("n9686m")); // caractères invalides

    }

    @Test
    void AircraftRegistrationWorksOnTrivialString() {
        AircraftRegistration b = new AircraftRegistration("CC-AKI");
        assertEquals("CC-AKI", b.string());
    }
}
