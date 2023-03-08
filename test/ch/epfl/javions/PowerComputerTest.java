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
        int[] batchRead = new int[160];
        PowerComputer decoder = new PowerComputer(stream, 160);
        assertEquals(160 , decoder.readBatch(batchRead));
        assertEquals(73, batchRead[0]);
        int[] expectedValue = {73,292,65,745,98,4226,12244,25722,36818,23825};
        for(int iValue = 0; iValue < 10; iValue++){
            assertEquals(expectedValue[iValue], batchRead[iValue]);
        }
    }
}
