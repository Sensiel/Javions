package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;
import java.util.Objects;

/**
 * Represents a raw ADS-B message (i.e. its ME attribute has not yet been analyzed)
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 * @param timeStampNs : the time stamp of the message, expressed in nanoseconds from a given origin
 * @param bytes : the bytes of the message
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final int DF_VALUE = 17;

    /**
     * Compact Constructor
     * @param timeStampNs : the time stamp of the message, expressed in nanoseconds from a given origin
     * @param bytes : the bytes of the message
     * @throws NullPointerException if the given array of bytes is null
     * @throws IllegalArgumentException if the time stamp is negative or if the array does not contain 14 bytes
     */
    public RawMessage{
        Objects.requireNonNull(bytes);
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH );
    }

    /**
     * Check if the Crc24 associated to the array of bytes is equal to zero
     * @param timeStampsNs : the time stamp of the message, expressed in nanoseconds from a given origin
     * @param bytes : the bytes of the message
     * @return a RawMessage associated to the given timestamp and bytes
     */
    public static RawMessage of(long timeStampsNs, byte[] bytes) {
        int crc24 = new Crc24(Crc24.GENERATOR).crc(bytes);
        if (crc24 == 0)
            return new RawMessage(timeStampsNs, new ByteString (bytes));
        else
            return null;
    }

    /**
     * Check the validity of the message
     * @param byte0 : the first byte of a message
     * @return the size of a message whose first byte is the given one
     */
    public static int size(byte byte0){
        int DF = Bits.extractUInt(Byte.toUnsignedLong(byte0),3,5);
        if(DF == DF_VALUE)
            return LENGTH;
        return 0;
    }

    /**
     * @param payload : the message's payload
     * @return the type code of the ME attribute passed as argument
     */
    public static int typeCode(long payload){
        return Bits.extractUInt(payload,51,5);
    }

    /**
     * @return the format of the message
     */
    public int downLinkFormat(){
        return Bits.extractUInt(Integer.toUnsignedLong(bytes.byteAt(0)),3,5); // Potentiellement des bug là
    }

    /**
     * @return the ICAO address of the sender of the message
     */
    public IcaoAddress icaoAddress(){
        long icaoAddress = bytes.bytesInRange(1,4);
        String hexString = HexFormat.of().withUpperCase().toHexDigits(icaoAddress).substring(10);
        return new IcaoAddress(hexString);
    }

    /**
     * @return the payload of the message (56 bits)
     */
    public long payload(){
        return bytes().bytesInRange(4,11);
    }

    /**
     * @return the type code of the message, i.e. the five most significant bits of its payload
     */
    public int typeCode(){
        return typeCode(payload());
    }
}
