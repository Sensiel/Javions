package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {

    private byte[] batch;

    private InputStream stream;

    private int batchSize;

    public SamplesDecoder(InputStream stream, int batchSize){
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize > 0);
        batch = new byte[batchSize * 2];
        this.batchSize = batchSize;
        this.stream = stream;
    }

    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nByteRead = stream.readNBytes(this.batch, 0, batchSize*2);
        int nSampleRead = (int) Math.floor(nByteRead/2f);
        for(int iByte = 0; iByte < nSampleRead; iByte++){
            batch[iByte] = (short) ((((this.batch[iByte*2 + 1] & 0xFF) << 8) | (this.batch[iByte*2]) & 0xFF) - 2048);
        }

        return nSampleRead;
    }
}
