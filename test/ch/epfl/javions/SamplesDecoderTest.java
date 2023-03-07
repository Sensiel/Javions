package ch.epfl.javions;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
class SamplesDecoderTest {
    @Test
    void TrivialDecoderTest() throws IOException {
        InputStream input = new ByteArrayInputStream(new byte[]{(byte) 0xFD, (byte)0x07});
        SamplesDecoder decoder = new SamplesDecoder(input, 1);
        short[] batchRead = new short[1];
        assertEquals(1, decoder.readBatch(batchRead));
        assertEquals(-3, batchRead[0]);
    }

    @Test
    void NullArgDecoderTest() {
        InputStream input = new ByteArrayInputStream(new byte[]{(byte) 0xFD, (byte)0x07});
        SamplesDecoder decoder = new SamplesDecoder(input, 1);
        short[] batchRead = null;
        assertThrows(NullPointerException.class, () -> decoder.readBatch(batchRead));
    }

    @Test
    void IllegalArgDecoderTest() {
        InputStream input = new ByteArrayInputStream(new byte[]{(byte) 0xFD, (byte)0x07});
        SamplesDecoder decoder = new SamplesDecoder(input, 1);
        short[] batchRead = new short[0];
        assertThrows(IllegalArgumentException.class, () -> decoder.readBatch(batchRead));
    }
}
