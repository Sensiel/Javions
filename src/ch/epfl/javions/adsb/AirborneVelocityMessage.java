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
    public AirborneVelocityMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(speed >= 0);
        Preconditions.checkArgument(trackOrHeading >= 0);
        Preconditions.checkArgument(timeStampNs >= 0);
    }
    static AirborneVelocityMessage of(RawMessage rawMessage){
        if(rawMessage.typeCode() != 19) return null;
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        long ME = rawMessage.payload();
        int ST = Bits.extractUInt(ME,48,3) & 0xff;
        long neededBits = Bits.extractUInt(ME,21,22); // j'ai le droit de mettre long, sachant que ca retourne int ?

        if(ST == 1 || ST == 2){
            int vew = Bits.extractUInt(neededBits,11,10);
            int vns = Bits.extractUInt(neededBits,0,10);
            if(vns == 0 || vew == 0) return null;
            double speed = Units.convertFrom(Math.hypot(vns - 1,vew - 1) * Math.pow(ST,2),Units.Speed.KNOT); // si ST = 2 : 4noeuds sinon 1 noeud
            // calcul angle selon atan2 quels sont les conditions ?
            return new AirborneVelocityMessage(timeStampNs,icaoAddress,speed,0);

        } else if ((ST == 3 || ST == 4) && Bits.testBit(neededBits,21)) {
            int HDG = Bits.extractUInt(neededBits,11,10) & 0xff;
            double cap = Units.convertFrom(Math.scalb(HDG,-10),Units.Angle.TURN);
            double AS = Units.convertFrom((Bits.extractUInt(neededBits,0,10) & 0xff ) * Math.pow(ST-2,2),Units.Speed.KNOT);
            return new AirborneVelocityMessage(timeStampNs,icaoAddress,AS,cap);

        } else {return null;}
    }
}
