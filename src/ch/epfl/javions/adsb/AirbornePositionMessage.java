package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represent an ADS-B flight positioning message
 * @author Imane Raihane (362230)
 * @param timeStampNs : the time stamp of the message, expressed in nanoseconds from a given origin
 * @param icaoAddress : the ICAO address of the sender of the message
 * @param altitude : the altitude of the aircraft at the time the message was sent, in meters
 * @param parity : the parity of the message
 * @param x : the local and normalized longitude
 * @param y : the local and normalized latitude
 */
public record AirbornePositionMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double altitude,
                                      int parity,
                                      double x,
                                      double y) implements Message {
    /**
     * Compact Constructor
     * @param timeStampNs : the time stamp of the message, expressed in nanoseconds from a given origin
     * @param icaoAddress : the ICAO address of the sender of the message
     * @param altitude : the altitude of the aircraft at the time the message was sent, in meters
     * @param parity : the parity of the message
     * @param x : the local and normalized longitude
     * @param y : the local and normalized latitude
     * @throws IllegalArgumentException if parity, x and y aren't valid
     * @throws NullPointerException if icaoAddress is null
     */
    public AirbornePositionMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 1 || parity == 0);
        Preconditions.checkArgument((x >= 0d && x < 1d) && (y >= 0d && y < 1d));
    }

    /**
     * @param rawMessage : a raw ADS-B message
     * @return the flight positioning message corresponding to the given raw message
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long me = rawMessage.payload();
        long alt = Bits.extractUInt(me,36,12);

        double finalAltitude;

        if(Bits.testBit(alt,4)){
            int part1 = Bits.extractUInt(alt,5,7);
            int part2 = Bits.extractUInt(alt,0,4);
            finalAltitude = (part1 << 4 | part2) * 25d - 1000;
        }
        else {
            finalAltitude = gray(alt);
            if(Double.isNaN(finalAltitude)) return null;
        }
        finalAltitude = Units.convertFrom(finalAltitude,Units.Length.FOOT);
        double lonLocal = Math.scalb(Bits.extractUInt(me,0,17),-17);
        double latLocal = Math.scalb(Bits.extractUInt(me,17,17),-17);
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int parity = Bits.extractUInt(me,34,1);
        long timeStampsNs = rawMessage.timeStampNs();

        return new AirbornePositionMessage(timeStampsNs,icaoAddress, finalAltitude,parity,lonLocal,latLocal);
    }

    private static double gray(long alt){
        long altitude = 0L;
        double finalAltitude = 0d;
        //We decided to do the shuffle using an index list because it was the best way to optimize tu readability and the complexity of the code
        final int[] INDEX_SHUFFLE = {9,3,10,4,11,5,6,0,7,1,8,2};
        for(int iBit = 0; iBit < 12; iBit++){
            long currBit = Bits.extractUInt(alt,iBit,1);
            altitude = altitude | (currBit << INDEX_SHUFFLE[iBit]);

            int lowerPart = Bits.extractUInt(altitude,0,3);
            int upperPart = Bits.extractUInt(altitude,3,9);
            lowerPart = (lowerPart)^(lowerPart >> 1)^(lowerPart >> 2);
            int upGray = 0;
            for(int i = 0 ; i < 9; i++)
                upGray ^= upperPart >> i;

            if (lowerPart == 0 || lowerPart == 5 || lowerPart == 6)
                return Double.NaN;
            if (lowerPart == 7) lowerPart = 5;

            if(upGray % 2 == 1) lowerPart = 6 - lowerPart;

            finalAltitude = -1300d + (lowerPart * 100d) + (upGray * 500d);
        }
        return finalAltitude;
    }
}
