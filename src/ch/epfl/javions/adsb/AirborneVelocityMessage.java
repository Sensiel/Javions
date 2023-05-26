package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represent a speed message in flight
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 * @param timeStampNs : the time stamp of the message in nanoseconds
 * @param icaoAddress : the ICAO address of the sender of the message
 * @param speed : the speed of the aircraft in m/s
 * @param trackOrHeading : the direction of the aircraft in radians
 */
public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message {
    private static final int ST_SIZE = 3;
    private static final int ST_START_INDEX = 48;
    private static final int HDG_VEW_VNS_SIZE = 10;
    private static final int DNS_INDEX = 10;
    private static final int DEW_SH_INDEX = 21;
    private static final int VEW_HDG_START_INDEX = 11;
    private static final int AS_VNS_START_INDEX = 0;
    private static final int AS_SIZE = 10;

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
        long me = rawMessage.payload();
        int st = Bits.extractUInt(me,ST_START_INDEX,ST_SIZE) & 0xff;
        long neededBits = Bits.extractUInt(me,21,22);

        if(st == 1 || st == 2){

            int vew = Bits.extractUInt(neededBits,VEW_HDG_START_INDEX, HDG_VEW_VNS_SIZE);
            int vns = Bits.extractUInt(neededBits,AS_VNS_START_INDEX, HDG_VEW_VNS_SIZE);
            if(vns == 0 || vew == 0) return null;

            double speed = Units.convertFrom(Math.hypot(vns - 1,vew - 1) * Math.pow(st, 2), Units.Speed.KNOT);
            double angle;

            if(Bits.testBit(neededBits,DNS_INDEX))
                 angle = Bits.testBit(neededBits, DEW_SH_INDEX) ? Math.atan2(1 - vew, 1 - vns) : Math.atan2(vew - 1, 1 - vns);
            else
                 angle = Bits.testBit(neededBits, DEW_SH_INDEX) ? Math.atan2(1 - vew, vns - 1) : Math.atan2(vew - 1, vns - 1);

            if(angle < 0)
                angle += Units.Angle.TURN;

            return new AirborneVelocityMessage(timeStampNs, icaoAddress, speed, angle);

        }
        else if ((st == 3 || st == 4) && Bits.testBit(neededBits, DEW_SH_INDEX)) {
            int hdg = Bits.extractUInt(neededBits, VEW_HDG_START_INDEX, HDG_VEW_VNS_SIZE);
            double cap = Units.convertFrom(Math.scalb(hdg, -10), Units.Angle.TURN);
            double asKnots = ((Bits.extractUInt(neededBits, AS_VNS_START_INDEX, AS_SIZE)));

            if(asKnots == 0)
                return null;
            asKnots--;
            if(st == 4) asKnots *= 4;

            double as = Units.convertFrom(asKnots, Units.Speed.KNOT);
            return new AirborneVelocityMessage(timeStampNs, icaoAddress, as, cap);
        }
        else
            return null;
    }
}
