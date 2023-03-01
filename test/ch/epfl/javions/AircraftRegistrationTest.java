package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {
    @Test
    void AircraftRegistrationThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("n9686m")); // caractères invalides

    }

    @Test
    void AircraftRegistrationWorksOnTrivialString() {
        AircraftRegistration b = new AircraftRegistration("CC-AKI");
        assertEquals("CC-AKI", b.string());
    }
}
