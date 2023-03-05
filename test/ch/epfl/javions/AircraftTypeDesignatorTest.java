package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftTypeDesignatorTest {
    @Test
    void AircraftTypeDesignatorThrowsOnInvalidString(){
        assertThrows(IllegalArgumentException.class,() -> new AircraftTypeDesignator("A20NG")); // length superior to 4
        assertThrows(IllegalArgumentException.class,() -> new AircraftTypeDesignator("lnce")); // caractères invalides
    }
    @Test
    void AircraftTypeDesignatorWorksOnTrivialString(){
        AircraftTypeDesignator b = new AircraftTypeDesignator("A20N");
        assertEquals("A20N",b.string());
    }
    @Test
    void AircraftTypeDesignatorWorksOnEmptyString(){
        AircraftTypeDesignator b = new AircraftTypeDesignator("");
        assertEquals("",b.string());
    }


}
