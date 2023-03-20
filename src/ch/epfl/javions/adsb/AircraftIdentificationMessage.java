package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AircraftIdentificationMessage (long timeStampNs,
                                             IcaoAddress icaoAddress,
                                             int category,
                                             CallSign callSign) implements Message{
    public AircraftIdentificationMessage{
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        String string = "";
        for(int i = 7; i >= 0 ; i--){
            int c = Bits.extractUInt(rawMessage.payload(),i*7,6) & 0xFF;
            if(c == 0 || (c > 26 && c < 32 ) || (c > 32 && c < 48)) // methode prv ?
                return null ;
            if(c <= 26){
                c += 64;
            }
            string += (char) c;
        }
        CallSign callSign = new CallSign(string);
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = (14 - rawMessage.typeCode()) << 3 | Bits.extractUInt(rawMessage.payload(),48,3) ;
        long timeStampsNs = rawMessage.timeStampNs();

        return new AircraftIdentificationMessage(timeStampsNs, icaoAddress, category, callSign);
    }




}

