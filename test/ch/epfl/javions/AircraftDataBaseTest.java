package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.NoSuchFileException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class AircraftDataBaseTest {
    @Test
    void CorrectAircraftDataBaseGet() throws IOException {
        String archiveFile = getClass().getResource("/aircraft.zip").getFile();
        archiveFile = URLDecoder.decode(archiveFile, UTF_8);
        assertEquals(new AircraftDatabase(archiveFile).get(new IcaoAddress("381C3C")).registration().string(), "F-JTNC");
    }

    @Test
    void InvalidAircraftDataBaseGet() throws IOException {
        String archiveFile = getClass().getResource("/aircraft.zip").getFile();
        archiveFile = URLDecoder.decode(archiveFile, UTF_8);
        assertNull(new AircraftDatabase(archiveFile).get(new IcaoAddress("381E3C")));
    }

    @Test
    void InvalidFileName() throws IOException {
        assertThrows(NoSuchFileException.class, () -> new AircraftDatabase("no").get(new IcaoAddress("381E3C")));
    }
}