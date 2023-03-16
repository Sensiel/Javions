package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;
import java.util.Objects;

public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final int DF_VALUE = 17;

    public RawMessage{
        Objects.requireNonNull(bytes);
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH );
    }

    public static RawMessage of(long timeStampsNs, byte[] bytes) {
        int crc24 = new Crc24(Crc24.GENERATOR).crc(bytes);
        if (crc24 == 0) {
            return new RawMessage(timeStampsNs, new ByteString (bytes));
        }
        else return null;
    }

    public static int size(byte byte0){
        int DF = Bits.extractUInt(Byte.toUnsignedLong(byte0),3,5);
        if(DF == DF_VALUE) return LENGTH;
        return 0;
    }

    public static int typeCode(long payload){
        return Bits.extractUInt(payload,51,5);
    }

    public int downLinkFormat(){
        return Bits.extractUInt(Integer.toUnsignedLong(bytes.byteAt(0)),3,5); // Potentiellement des bug là
    }

    public IcaoAddress icaoAddress(){
        long icaoAddress = bytes.bytesInRange(1,4);
        String hexString = HexFormat.of().withUpperCase().toHexDigits(icaoAddress).substring(10);
        return new IcaoAddress(hexString);
    }

    public long payload(){
        return bytes().bytesInRange(4,11);
    }
    public int typeCode(){
        return typeCode(payload());
    }
}
