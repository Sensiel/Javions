package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record RawMessage(long timeStampsNs, ByteString bytes) {
    public static final int LENGTH = 14;
    public RawMessage{
        Preconditions.checkArgument(timeStampsNs >= 0 && bytes.size() == LENGTH );
    }

    public static RawMessage of(long timeStampsNs, byte[] bytes) {
        int crc24 = new Crc24(Crc24.GENERATOR).crc(bytes);
        if (crc24 == 0) {
            return new RawMessage(timeStampsNs, new ByteString (bytes));
        } else {
            return null;
        }
    }

    public static int size(byte byte0){
        int DF = Bits.extractUInt(Byte.toUnsignedInt(byte0),3,5); // est ce que je dois convertir le byte0 en long ?
        if(DF == 17){
            return LENGTH;
        } else{
            System.out.println("The message is not of a known type"); //jsp si j'ai bien capté l'énoncé
            return 0;
        }
    }

    public static int typeCode(long payload){
        return Bits.extractUInt(payload,51,5);
    }

    public int downLinkFormat(){
        return Bits.extractUInt(bytes.byteAt(0),3,5);
    }

    public IcaoAddress icaoAddress(){
        long icaoAddress = bytes.bytesInRange(1,3);
        String hexString = Long.toHexString(icaoAddress);
        // je sais pas si y'a une meilleure maniere d'implementer
        // cette methode en utilisant les methodes de byteString
        return new IcaoAddress(hexString);
    }

    public long payload(){
        return bytes().bytesInRange(4,10);
    }
    public int typecode(){
        return typeCode(payload());
    }
}
