package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {
    public final byte[] bytes; // TODO PEUT ETRE METTRE EN private
    public ByteString(byte[] bytes){
        this.bytes = bytes.clone();
    }
    public static ByteString ofHexadecimalString(String hexString){
        if(hexString.length()%2 == 1) throw new IllegalArgumentException();
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    public int size(){
        return bytes.length;
    }

    public int byteAt(int index){
        Objects.checkIndex(index, size());
        return bytes[index] & 0xff;
    }

    public long bytesInRange(int fromIndex, int toIndex){
        long result = 0;
        if(toIndex - fromIndex > 8) throw new IllegalArgumentException();
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
