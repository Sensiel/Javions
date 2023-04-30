package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Decode the bytes from the AirSpy into signed 12-bit samples
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class SamplesDecoder {
    private final static int DIFFERENCE = 2048;
    private final byte[] batch;
    private final InputStream stream;
    private final int batchSize;

    /**
     * Public Constructor
     * @param stream : the input stream given to get the bytes from the AirSpy radio
     * @param batchSize : the size of the batch
     * @throws NullPointerException if the given batch size is not strictly positive
     * @throws IllegalArgumentException if the given stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize > 0);
        batch = new byte[batchSize * 2];
        this.batchSize = batchSize;
        this.stream = stream;
    }

    /**
     * Convert each 2 bytes into a signed sample
     * @param batch : the array that will be filled with the signed samples
     * @return the number of converted samples
     * @throws IOException if there's an input/output error
     * @throws IllegalArgumentException if the size of the given array is not equal to the batchSize given in the constructor
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nByteRead = stream.readNBytes(this.batch, 0, batchSize*Short.BYTES);
        int nSampleRead = nByteRead/Short.BYTES;
        for(int iByte = 0; iByte < nSampleRead; iByte++) {
            batch[iByte] = (short) ((((Byte.toUnsignedInt(this.batch[iByte*2 + 1])) << 8) |
                    (Byte.toUnsignedInt(this.batch[iByte*2]))) - DIFFERENCE);
        }
        return nSampleRead;
    }
}
