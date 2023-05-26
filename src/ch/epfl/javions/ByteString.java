package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Represent an immutable sequence of bytes interpreted in an unsigned way
 * @author Zablocki Victor (361602)
 */
public final class ByteString {
    private final byte[] bytes;
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

    /**
     * Public Constructor
     * @param bytes : Array of bytes
     */
    public ByteString(byte[] bytes){
        this.bytes = bytes.clone();
    }

    /**
     * Convert a Hexadecimal String into an array of bytes then a byteString
     * @param hexString : String in Hexadecimal representation
     * @throws IllegalArgumentException if hexString is not of even length or if it contains a character that is not a hexadecimal digit
     * @return the byteString associated to hexString
     */
    public static ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(hexString.length()%2 == 0);
        return new ByteString(HEX_FORMAT.parseHex(hexString));
    }

    /**
     * @return size of the array associated to the ByteString
     */
    public int size(){
        return bytes.length;
    }

    /**
     * Extract a byte at the given index
     * @param index : the chosen index
     * @throws IndexOutOfBoundsException if index isn't valid
     * @return the integer representing the byte at the given index
     */
    public int byteAt(int index){
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Extract the bytes between the two indexes given
     * @param fromIndex : the index from where we'll start extracting
     * @param toIndex : the index where we'll end extracting
     * @return a long corresponding to the bytes between the indexes fromIndex and toIndex
     * @throws IllegalArgumentException if the range given by the indexes is upper than 8
     * @throws IndexOutOfBoundsException if if the sub-range is out of bounds
     */
    public long bytesInRange(int fromIndex, int toIndex){
        long result = 0;
        Preconditions.checkArgument(toIndex - fromIndex < Long.BYTES);
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        for(int index = fromIndex; index < toIndex; index++){
            result |= byteAt(index);
            result <<= Byte.SIZE;
        }
        result >>>= Byte.SIZE;
        return result;
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof ByteString that) {
            return Arrays.equals(bytes, that.bytes);
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString(){
        return HEX_FORMAT.formatHex(bytes);
    }
}
