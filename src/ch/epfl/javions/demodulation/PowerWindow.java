package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Represent a window of fixed size over a sequence of power samples produced by a power computer
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class PowerWindow {
    private final static int BATCH_SIZE = 1 << 16;
    private int[] batch1;
    private int[] batch2;
    private final int windowSize;
    private final PowerComputer powerComputer;
    private long WindowPos = 0;
    private int nSampleRead;

    /**
     * Public Constructor
     * @param stream : the input stream needed to create an instance of PowerComputer
     * @param windowSize : the size of the Window
     * @throws IOException if there's an input/output error since we're using readBatch(short[]) from PowerComputer
     * @throws IllegalArgumentException if the given window size is not in the right interval
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.windowSize = windowSize;
        batch1 = new int[BATCH_SIZE];
        nSampleRead = powerComputer.readBatch(batch1);
        batch2 = new int[BATCH_SIZE];
    }

    /**
     * @return the window's size
     */
    public int size(){
        return windowSize;
    }

    /**
     * @return the current position of the window, relative to the beginning of the power value stream
     */
    public long position(){
        return WindowPos;
    }

    /**
     * @return true if the window is full
     */
    public boolean isFull(){
        return ((position() + size()) % BATCH_SIZE <= nSampleRead);
    }

    /**
     * @param i : the wanted index
     * @return the power sample at the given index of the window
     */
    public int get(int i){
        Objects.checkIndex(i, size());
        int newIndex = (int) position() % BATCH_SIZE + i;
        if(newIndex >= BATCH_SIZE)
            return batch2[newIndex % BATCH_SIZE];
        else
            return batch1[newIndex];
    }

    /**
     * Advances the Window's position and manages the content of the two arrays
     * @throws IOException if there's an input/output error since we're using readBatch(short[]) from PowerComputer
     */
    public void advance() throws IOException{
        WindowPos += 1;
        if((position() % BATCH_SIZE) + size() == BATCH_SIZE ) //The window overlaps the second batch
            nSampleRead = powerComputer.readBatch(batch2);

        if(position() % BATCH_SIZE == 0 && position()  >= BATCH_SIZE) //The window is totally contained in the second batch
            batch1 = batch2.clone();
    }

    /**
     * Advance the Window's position by the given number of samples
     * @param offset : the number of steps we're going to advance by
     * @throws IOException if there's an input/output error since we're using advance()
     */
    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset > 0);
        for(int i = 1; i <= offset; i++){
            advance();
        }
    }
}
