package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    private short[] batch;
    private SamplesDecoder samplesDecoder;
    private int batchSize;

    private short[] currSample;

    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % 8 == 0 && batchSize >= 0);
        samplesDecoder = new SamplesDecoder(stream, batchSize);
        batch = new short[batchSize];
        this.batchSize = batchSize;
    }

    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nSampleRead = samplesDecoder.readBatch(this.batch);
        currSample = new short[8];
        for(int iSample = 0; iSample < batchSize/2; iSample++){
            currSample[(iSample*2)%8] = this.batch[iSample*2];
            currSample[(iSample*2 + 1)%8] = this.batch[iSample*2 + 1];
        }

        return nSampleRead;
    }
}
