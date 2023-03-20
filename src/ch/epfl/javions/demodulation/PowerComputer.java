package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    private short[] sampleBatch;
    private SamplesDecoder samplesDecoder;
    private int batchSize;

    private short[] currSample;

    /**
     * Public Constructor
     * @param stream : the input stream needed to create an instance of SamplesDecoder
     * @param batchSize : the size of the batch
     * @throws IllegalArgumentException if the given batchSize is not a multiple of 8 or if it is negative
     */
    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % 8 == 0 && batchSize > 0);
        samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        sampleBatch = new short[batchSize * 2];
        this.batchSize = batchSize;
        currSample = new short[8];
    }

    /**
     * Calculate the power samples and place them in the given array
     * @param batch : the array that will be filled with the power samples
     * @return the number of power samples placed in the given array
     * @throws IOException if there's an input/output error
     * @throws IllegalArgumentException if the size of the given array is not equal to the batchSize given in the constructor
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nSampleRead = samplesDecoder.readBatch(this.sampleBatch);
        int nPowerSampleRead = (int)Math.floor(nSampleRead/2f);
        for(int iPowerSample = 0; iPowerSample < nPowerSampleRead; iPowerSample++){
            int iSample = iPowerSample * 2;
            currSample[iSample % 8] = this.sampleBatch[iSample];
            currSample[(iSample + 1) % 8] = this.sampleBatch[iSample + 1];
             // The following formula is P(n) but we added 8 to each index used in the formula ( because (iSample - 6)%8 == (iSample + 2)%8 and it's easier to calculate)
                batch[iPowerSample] = (int)Math.pow(currSample[(iSample + 2) % 8] - currSample[(iSample + 4) % 8] + currSample[(iSample + 6) % 8] - currSample[iSample % 8], 2)
                +  (int)Math.pow(currSample[(iSample + 3) % 8] - currSample[(iSample + 5) % 8] + currSample[(iSample + 7) % 8] - currSample[(iSample + 1) % 8], 2);
        }
        return nPowerSampleRead;
    }
}
