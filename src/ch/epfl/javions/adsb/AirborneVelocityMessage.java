package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message {
    /**
     * Compact Constructor
     * @param timeStampNs : the time stamp of the message in nanoseconds
     * @param icaoAddress : the ICAO address of the sender of the message
     * @param speed : the speed of the aircraft in m/s
     * @param trackOrHeading : the direction of the aircraft in radians
     * @throws IllegalArgumentException if timeStampNs, speed or trackOrHeading are strictly negative
     * @throws NullPointerException if icaoAddress is null
     */
    public AirborneVelocityMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(speed >= 0);
        Preconditions.checkArgument(trackOrHeading >= 0);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * Evaluate all the needed attributes starting from the given raw message
     * @param rawMessage : the given message
     * @return the AirborneVelocityMessage corresponding to the given raw message
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage){
        if(rawMessage.typeCode() != 19) return null;
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        long ME = rawMessage.payload();
        int ST = Bits.extractUInt(ME,48,3) & 0xff;
        long neededBits = Bits.extractUInt(ME,21,22);

        if(ST == 1 || ST == 2){

            int vew = Bits.extractUInt(neededBits,11,10);
            int vns = Bits.extractUInt(neededBits,0,10);
            if(vns == 0 || vew == 0) return null;

            double speed = Units.convertFrom(Math.hypot(vns - 1,vew - 1) * Math.pow(ST, 2), Units.Speed.KNOT);
            double angle;
            //
            if(Bits.testBit(neededBits,10))
                 angle = Bits.testBit(neededBits,21) ? Math.atan2(1 - vew, 1 - vns) : Math.atan2(vew - 1, 1 - vns);
            else
                 angle = Bits.testBit(neededBits,21) ? Math.atan2(1 - vew, vns - 1) : Math.atan2(vew - 1, vns - 1);

            if(angle < 0)
                angle += 2d * Math.PI;

            return new AirborneVelocityMessage(timeStampNs, icaoAddress, speed, angle);

        }
        else if ((ST == 3 || ST == 4) && Bits.testBit(neededBits,21)) {
            int HDG = Bits.extractUInt(neededBits, 11, 10);
            double cap = Units.convertFrom(Math.scalb(HDG, -10), Units.Angle.TURN);
            double ASKnots = ((Bits.extractUInt(neededBits, 0, 10)));

            if(ASKnots == 0)
                return null;
            ASKnots--;
            if(ST == 4) ASKnots *= 4;

            double AS = Units.convertFrom(ASKnots, Units.Speed.KNOT);
            return new AirborneVelocityMessage(timeStampNs, icaoAddress, AS, cap);
        }
        else
            return null;
    }
}
