package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

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
        if(!((rawMessage.typeCode() >= 9 && rawMessage.typeCode() <= 18) ||(rawMessage.typeCode() >= 20 && rawMessage.typeCode() <= 22)))
            return null;
        long ME = rawMessage.payload();
        long ALT = Bits.extractUInt(ME,36,12);

        double altitudeFinale;

        if(Bits.testBit(ALT,4)){
            int part1 = Bits.extractUInt(ALT,5,7);
            int part2 = Bits.extractUInt(ALT,0,4);
            altitudeFinale = (part1 << 4 | part2)*25d - 1000;
        }
        else {
            long altitude = 0L;
            int[] index = {9,3,10,4,11,5,6,0,7,1,8,2};
            for(int iBit = 0; iBit < 12; iBit++){
                long currBit = Bits.testBit(ALT, iBit) ? 1L : 0L;
                altitude = altitude | (currBit << index[iBit]);
            }

            int lowPart = Bits.extractUInt(altitude,0,3);
            int upPart = Bits.extractUInt(altitude,3,9);
            lowPart = (lowPart)^(lowPart >> 1)^(lowPart >> 2);
            int upGray = 0;
            for(int i = 0 ; i < 9; i++)
                upGray ^= upPart >> i;

            if (lowPart == 0 || lowPart == 5 || lowPart == 6) return null;
            if (lowPart == 7) lowPart = 5;
            if(upGray % 2 == 1) lowPart = 6 - lowPart;

            altitudeFinale = -1300 + (lowPart * 100) + (upGray * 500);
        }
        altitudeFinale = Units.convertFrom(altitudeFinale,Units.Length.FOOT);
        double lonLocal = Math.scalb(Bits.extractUInt(ME,0,17),-17);
        double latLocal = Math.scalb(Bits.extractUInt(ME,17,17),-17);
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int parity = Bits.extractUInt(ME,34,1);
        long timeStampsNs = rawMessage.timeStampNs();
        return new AirbornePositionMessage(timeStampsNs,icaoAddress,altitudeFinale,parity,lonLocal,latLocal);
    }


}
