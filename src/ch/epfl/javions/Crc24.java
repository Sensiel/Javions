package ch.epfl.javions;

import java.util.HexFormat;

public final class Crc24 {
    public final static int GENERATOR = 0xFFF409;
    public int[] table;
    public static final int GeneratorLength = 24;

    /**
     * Constructor that builds the array associated to the generator
     * @param generator
     */
    public Crc24(int generator){
        table = buildTable(generator);
    }

    /**
     * Calculate in an optimized way the CRC24
     * @param bytes : the given message
     * @return the CRC24 associated to the given array of bytes
     */
    public int crc(byte[] bytes){
        int crc = 0;
        for (byte aByte : bytes) {
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(aByte)) ^ table[Bits.extractUInt(crc, GeneratorLength - Byte.SIZE, Byte.SIZE)];
        }
        for(int i = 0; i < 3 ; i++){ //augmentation message
            crc = ((crc << Byte.SIZE) ^ table[Bits.extractUInt(crc, GeneratorLength - Byte.SIZE, Byte.SIZE)]);
        }
        crc = Bits.extractUInt(crc,0,GeneratorLength);
        return crc;
    }



    /**
     * Calculate the CRC24 bit by bit
     * @param generator
     * @param bytes : the message
     * @return the CRC24 associated to the generator and message given
     */
    private static int crc_bitwise(int generator, byte[] bytes){
        int crc = 0;
        int[] table = new int[]{0,generator};
        for (byte b : bytes) {
            for (int j = 7; j >= 0; j--) {
                int currMessageBit = Bits.extractUInt(Byte.toUnsignedLong(b), j, 1);
                int currCRCBit = Bits.extractUInt(crc, GeneratorLength - 1, 1);
                crc = ((crc << 1 | currMessageBit) ^ table[currCRCBit]);
            }
        }
        for(int i = 0; i < 24; i++) { //augmentation message
            int currCRCBit = Bits.extractUInt(crc, GeneratorLength - 1, 1);
            crc = ((crc << 1) ^ table[currCRCBit]);
        }
        crc = Bits.extractUInt(crc,0,GeneratorLength);
        return crc;
    }

    /**
     * Build the table of 256 entries corresponding to a generator
     * @param generator
     * @return the array associated to the given generator
     */
    static int[] buildTable(int generator){
        int[] table = new int[256];
        for(int i = 0; i < 256 ; ++i ){
            table[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }
        return table;
    }


}
