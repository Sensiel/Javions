package ch.epfl.javions;

/**
 * Represent a 24-bit CRC calculator
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class Crc24 {
    public final static int GENERATOR = 0xFFF409; // the generator allowing us to check the validity of Crc24
    private final static int TABLE_LENGTH = 256;
    private final int[] table;
    private static final int GENERATOR_LENGTH = 24;

    /**
     * Public Constructor that builds the array associated to the generator
     * @param generator : the given generator
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
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(aByte)) ^
                    table[Bits.extractUInt(crc, GENERATOR_LENGTH - Byte.SIZE, Byte.SIZE)];
        }
        for(int i = 0; i < GENERATOR_LENGTH/Byte.SIZE ; i++){
            crc = ((crc << Byte.SIZE) ^
                    table[Bits.extractUInt(crc, GENERATOR_LENGTH - Byte.SIZE, Byte.SIZE)]);
        }
        crc = Bits.extractUInt(crc,0, GENERATOR_LENGTH);
        return crc;
    }

    /**
     * Calculate the CRC24 bit by bit
     * @param generator : the given generator
     * @param bytes : the message
     * @return the CRC24 associated to the generator and message given
     */
    private static int crc_bitwise(int generator, byte[] bytes){
        int crc = 0;
        int[] table = new int[]{0,generator};
        for (byte b : bytes) {
            for (int j = Byte.SIZE - 1; j >= 0; j--) {
                // Byte.toUnsignedLong() is necessary, if you don't believe it just delete it
                int currMessageBit = Bits.extractUInt(Byte.toUnsignedLong(b), j, 1);
                int currCRCBit = Bits.extractUInt(crc, GENERATOR_LENGTH - 1, 1);
                crc = ((crc << 1 | currMessageBit) ^ table[currCRCBit]);
            }
        }
        for(int i = 0; i < GENERATOR_LENGTH; i++) {
            int currCRCBit = Bits.extractUInt(crc, GENERATOR_LENGTH - 1, 1);
            crc = ((crc << 1) ^ table[currCRCBit]);
        }
        crc = Bits.extractUInt(crc,0, GENERATOR_LENGTH);
        return crc;
    }

    /**
     * Build the table of 256 entries corresponding to the new generator
     * @param generator : the given generator
     * @return the array associated to the given generator
     */
    static int[] buildTable(int generator){
        int[] table = new int[TABLE_LENGTH];
        for(int i = 0; i < TABLE_LENGTH ; ++i ){
            table[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }
        return table;
    }


}
