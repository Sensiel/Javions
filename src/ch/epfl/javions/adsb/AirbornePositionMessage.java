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
        Preconditions.checkArgument((x >= 0 && x < 1) && (y >= 0 && y < 1));
    }

    /**
     * @param rawMessage : a raw ADS-B message
     * @return the flight positioning message corresponding to the given raw message
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long ME = rawMessage.payload();
        long timeStampsNs = rawMessage.timeStampNs();
        int parity = Bits.extractUInt(ME,34,1);
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        double latLocal = Math.scalb(Bits.extractUInt(ME,17,17),-17);
        double lonLocal = Math.scalb(Bits.extractUInt(ME,0,17),-17);
        long ALT = Bits.extractUInt(ME,36,12);
        // décodage ALT
        double altitudeFinale;
        //degueu
        byte grpD = 0;
        byte grpA = 0;
        byte grpB = 0;
        byte grpC = 0;
        if(Bits.testBit(ME,4)){
            int part1 = Bits.extractUInt(ME,5,7);
            int part2 = Bits.extractUInt(ME,0,4);
            altitudeFinale = part1 << 4 | part2;
        } else {
            // démelage
            for(int i = 0; i <= 2; ++i){
                grpD = (byte)(grpD << 1 | Bits.extractUInt(ALT,4-2*i,1));
                grpA = (byte)(grpA << 1 | Bits.extractUInt(ALT,10-2*i,1));
                grpB = (byte)(grpB << 1 | Bits.extractUInt(ALT,5-2*i,1));
                grpC = (byte)(grpC << 1 | Bits.extractUInt(ALT,11-2*i,1));
            }
            long altitude = (( ((grpD << 3) | grpA) << 3) | grpB) << 3 | grpC ; // c hyper moche
            // conversion en gray
            byte pdsfaible = (byte) Bits.extractUInt(altitude,0,3);
            byte pdsfort = (byte) Bits.extractUInt(altitude,3,9);
            int pdsFaibleGray = (pdsfaible >> 0)^(pdsfaible >> 1)^(pdsfaible >> 2);
            int pdsFortGray = 0;
            for(int i = 0 ; i < 9; i++){ pdsFortGray ^=  pdsfort >> i; }
            if(pdsFaibleGray == 0 || pdsFaibleGray == 5 || pdsFaibleGray == 6){ return null; }
            if(pdsFaibleGray == 7){ pdsFaibleGray = 5; }
            if(pdsFortGray % 2 == 1){ pdsFaibleGray = 6 - pdsFaibleGray; }
            altitudeFinale = Units.convertFrom(-1300 + (pdsFaibleGray * 100) + (pdsFortGray * 500),Units.Length.FOOT);
        }
        return new AirbornePositionMessage(timeStampsNs,icaoAddress,altitudeFinale,parity,lonLocal,latLocal);
    }


}
