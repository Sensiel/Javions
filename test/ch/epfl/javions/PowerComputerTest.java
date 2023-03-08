package ch.epfl.javions;


import ch.epfl.javions.demodulation.PowerComputer;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTest {
    @Test
    void TrivialDecoderTest() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("resources/samples.bin"))));
        int[] batchRead = new int[1201];
        PowerComputer decoder = new PowerComputer(stream, 1201);
        assertEquals(1201 , decoder.readBatch(batchRead));
        assertEquals(73, batchRead[0]);

    }
}
