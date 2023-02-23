package ch.epfl.javions;

import java.util.Objects;

public class Bits {
    private Bits(){

    }
    public static int extractUInt(long value, int start, int size){
        if(size <= 0 || size >= Integer.SIZE){
            throw new IllegalArgumentException();
        }
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        return (int) ((value >>> start) % (1L << size) );
    }

    public static boolean testBit(long value, int index){
        Objects.checkIndex(index, Long.SIZE);
        return ((value & (1L << index)) >>> index) == 1L;
    }
}
