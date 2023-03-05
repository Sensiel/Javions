package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftDescription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftDescriptionTest {
    @Test
    void AircraftDescriptionThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("l1p")); // caractères invalides
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L9P"));
    }
    @Test
    void AircraftDescriptionWorksOnTrivialString() {
        AircraftDescription b = new AircraftDescription("L1P");
        assertEquals("L1P", b.string());
    }
    @Test
    void AircraftDescriptionWorksOnEmptyString(){
        AircraftDescription b = new AircraftDescription("");
        assertEquals("",b.string());
    }
}
