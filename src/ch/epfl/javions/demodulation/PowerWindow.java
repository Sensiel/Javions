package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    private final static int BATCH_SIZE = 1 << 16;
    private int[] batchpos1;
    private int[] batchpos2;
    private int windowSize;
    private PowerComputer powerComputer;
    private static long WindowPos = 0;
    private static int nSampleRead;

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
        nSampleRead = powerComputer.readBatch(batchpos1);
        batchpos2 = new int[BATCH_SIZE];

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
        return ( nSampleRead - WindowPos >= size());
    }

    /**
     * @param i : the wanted index
     * @return the power sample at the given index of the window
     */
    public int get(int i){
            if( i < 0 || i >= windowSize) throw new IndexOutOfBoundsException();
            int i1 = (int) position() % BATCH_SIZE + i ; // pour meilleure encapsulation , oui ou pas la peine ?
            if(position() % BATCH_SIZE + i >= BATCH_SIZE) {
                return batchpos2[i1 % BATCH_SIZE];
            }
            else {
                return batchpos1[i1]; }

    }

    /**
     * Advances the Window's position and manages the content of the two arrays
     * @throws IOException if there's an input/output error since we're using readBatch(short[]) from PowerComputer
     */
    public void advance() throws IOException{
        WindowPos +=1 ;
        if(WindowPos % BATCH_SIZE + size() >= BATCH_SIZE ){ //The window overlaps the second batch
            int n = powerComputer.readBatch(batchpos2);}
        if(WindowPos % BATCH_SIZE == 0 && WindowPos >= BATCH_SIZE){ //The window is totally contained in the second batch
                batchpos1 = batchpos2 ;
            }
        }

    /**
     * Advance the Window's position by the given number of samples
     * @param offset : the number of steps we're going to advance by
     * @throws IOException if there's an input/output error since we're using advance()
     */
    public void advanceBy(int offset) throws IOException{
            Preconditions.checkArgument(offset > 0 );
            for(int i =1; i<= offset ; i++){
            advance();
            }
    }
}
