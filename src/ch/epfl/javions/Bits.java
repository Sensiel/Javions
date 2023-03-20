package ch.epfl.javions;

import java.util.Objects;

public class Bits {
    private Bits(){
    }

    /**
     * Extract from value the size bit range starting at the start index bit
     * @param value : the 64 bits from where we will extract the wanted bits
     * @param start : the index representing the start of the bit
     * @param size : the size of the bits we want to extract
     * @return an unsigned value corresponding to the extracted bits
     */
    public static int extractUInt(long value, int start, int size){
        Preconditions.checkArgument(size > 0 & size < Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        return (int) ((value >>> start) % (1L << size) );
    }

    /**
     * Check if the bit at given index is equal to 1
     * @param value : the 64 bits from where we will extract the bit
     * @param index : the index of the wanted bit
     * @return true if the value of the bit at the given index is equal to 1
     */
    public static boolean testBit(long value, int index){
        Objects.checkIndex(index, Long.SIZE);
        return ((value & (1L << index)) >>> index) == 1L;
    }
}
