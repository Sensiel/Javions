package ch.epfl.javions;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IcaoAddressTest {
    @Test
    void IcaoAddressThrowsOnInvalidString(){
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress("4B18145")); // plus que 6 caractères
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress("A2G790")); // caractère non valide (G)
    }
    @Test
    void IcaoAddressWorksOnTrivialString(){
       IcaoAddress b = new IcaoAddress("4B1814");
       assertEquals("4B1814",b.string());

    }
}
