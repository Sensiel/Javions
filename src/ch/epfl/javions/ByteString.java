package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {
    private final byte[] bytes;

    /**
     * Public Constructor
     * @param bytes : Array of bytes
     */
    public ByteString(byte[] bytes){
        this.bytes = bytes.clone();
    }

    /**
     * Convert Hexadecimal into an array of bytes then a byteString
     * @param hexString : String in Hexadecimal representation
     * @return returns the byteString corresponding to the hexadecimal representation of the argument
     */
    public static ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(hexString.length()%2 == 0);
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    /**
     * @return size of the array associated to the ByteString
     */
    public int size(){
        return bytes.length;
    }

    /**
     * Extract a byte at the given index
     * @param index
     * @return the integer representing the byte at the given index
     */
    public int byteAt(int index){
        Objects.checkIndex(index, size());
        return bytes[index] & 0xff;
    }

    /**
     * Extract the bytes between the two indexes given
     * @param fromIndex
     * @param toIndex
     * @return returns the bytes between the indexes fromIndex and toIndex as a long
     */
    public long bytesInRange(int fromIndex, int toIndex){
        long result = 0;
        Preconditions.checkArgument(toIndex - fromIndex <= 8);
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        for(int index = fromIndex; index < toIndex; index++){
            result |= bytes[index] & 0xff;
            result <<= 8;
        }
        result >>>= 8;
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
        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(bytes);
    }
}
