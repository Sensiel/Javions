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
        samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        batch = new short[batchSize];
        this.batchSize = batchSize;
    }

    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nSampleRead = samplesDecoder.readBatch(this.batch);
        currSample = new short[8];
        for(int iPowerSample = 0; iPowerSample < batchSize; iPowerSample++){
            int iSample = iPowerSample*2;
            currSample[iSample%8] = this.batch[iSample];
            currSample[(iSample + 1)%8] = this.batch[iSample + 1];
             // The following formula is P(n) but we added 8 to each index used in the formula ( because (iSample - 6)%8 == (iSample + 2)%8 and it's easier to calculate)
                batch[iPowerSample] = (int)Math.pow(currSample[(iSample + 2) % 8] - currSample[(iSample + 4) % 8] + currSample[(iSample + 6) % 8] - currSample[iSample % 8], 2)
                +  (int)Math.pow(currSample[(iSample + 1) % 8] - currSample[(iSample + 3) % 8] + currSample[(iSample + 5) % 8] - currSample[(iSample + 7) % 8], 2);
        }

        return nSampleRead;
    }
}
