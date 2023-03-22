package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AircraftIdentificationMessage (long timeStampNs,
                                             IcaoAddress icaoAddress,
                                             int category,
                                             CallSign callSign) implements Message{
    /**
     * Compact Constructor
     * @param timeStampNs : the time stamp of the message, expressed in nanoseconds from a given origin
     * @param icaoAddress : the ICAO address of the sender of the message
     * @param category : the aircraft category
     * @param callSign : the callSign
     */
    public AircraftIdentificationMessage{
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * @param rawMessage : a raw ADS-B message
     * @return the identification message associated to the given raw message
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        String string = "";
        if(rawMessage.typeCode() < 1 || rawMessage.typeCode() > 4) return null;

        for(int i = 7; i >= 0 ; i--){
            int c = Bits.extractUInt(rawMessage.payload(),i*6,6);
            if((c >= '0' && c <= '9') || c == ' ')
                string += (char) c;
            else if(c >= 1 && c <= 26)
                string += (char) (c + 'A' - 1);
            else return null;
        }
        CallSign callSign = new CallSign(string.strip());
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = ((14 - rawMessage.typeCode()) << 4) | Bits.extractUInt(rawMessage.payload(),48,3) ; // Potentiellement une erreur ici
        long timeStampsNs = rawMessage.timeStampNs();

        return new AircraftIdentificationMessage(timeStampsNs, icaoAddress, category, callSign);
    }
}

