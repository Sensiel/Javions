package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDataTest {

    @Test
    void aircraftDataThrowsOnNullArgument(){
        assertThrows(NullPointerException.class, () -> new AircraftData(
                new AircraftRegistration("CC-AKI"),
                new AircraftTypeDesignator("A20N"),
                null,
                new AircraftDescription("L1P"),
                WakeTurbulenceCategory.HEAVY));
    }
}
