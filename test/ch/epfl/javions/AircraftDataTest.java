package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AircraftDataTest {
    @Test
    void CorrectAircraftDataBaseGet() throws IOException {
        assertEquals(new AircraftDatabase("resources/aircraft.zip").get(new IcaoAddress("381C3C")).registration().string(), "F-JTNC");
    }

    @Test
    void InvalidAircraftDataBaseGet() throws IOException {
        assertNull(new AircraftDatabase("resources/aircraft.zip").get(new IcaoAddress("381E3C")));
    }
}
