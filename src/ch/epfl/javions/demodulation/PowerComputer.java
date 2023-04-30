package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
/**
 * Calculate the signal power samples from the signed samples produced by a sample decoder
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class PowerComputer {
    private final static int FILTER_SIZE = 8;
    private final short[] sampleBatch;
    private final SamplesDecoder samplesDecoder;
    private final int batchSize;
    private final short[] currSample;

    /**
     * Public Constructor
     * @param stream : the input stream needed to create an instance of SamplesDecoder
     * @param batchSize : the size of the batch
     * @throws IllegalArgumentException if the given batchSize is not a multiple of 8 or if it is negative
     */
    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % FILTER_SIZE == 0 && batchSize > 0);
        samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        sampleBatch = new short[batchSize * 2];
        this.batchSize = batchSize;
        currSample = new short[FILTER_SIZE];
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
            setBatch(iPowerSample,batch);
        }
        return nPowerSampleRead;
    }
    private void setBatch(int index, int[] table){
        int iSample = index * 2;
        currSample[iSample % FILTER_SIZE] = this.sampleBatch[iSample];
        currSample[(iSample + 1) % FILTER_SIZE] = this.sampleBatch[iSample + 1];
        // The following formula is P(n) but we added 8 to each index used in the formula ( because (iSample - 6)%8 == (iSample + 2)%8 and it's easier to calculate)
        int evenSamples = currSample[(iSample + 2) % FILTER_SIZE]
                - currSample[(iSample + 4) % FILTER_SIZE]
                + currSample[(iSample + 6) % FILTER_SIZE]
                - currSample[iSample % 8];
        int evenSamplesSquare = evenSamples * evenSamples;
        int oddSamples = currSample[(iSample + 3) % FILTER_SIZE]
                - currSample[(iSample + 5) % FILTER_SIZE]
                + currSample[(iSample + 7) % FILTER_SIZE]
                - currSample[(iSample + 1) % FILTER_SIZE];
        int oddSamplesSquare =oddSamples * oddSamples;
        table[index]= evenSamplesSquare + oddSamplesSquare;
    }
}
