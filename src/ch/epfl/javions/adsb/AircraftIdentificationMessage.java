package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represent an ADS-B message of identification and category
 * @author Zablocki Victor (361602)
 * @param timeStampNs : the time stamp of the message, expressed in nanoseconds
 * @param icaoAddress : the ICAO address of the message
 * @param category : the aircraft category of the message
 * @param callSign : the callSign of the message
 */
public record AircraftIdentificationMessage (long timeStampNs,
                                             IcaoAddress icaoAddress,
                                             int category,
                                             CallSign callSign) implements Message{
    private static final int CA_SIZE = 3;
    private static final int CA_START_INDEX = 48;

    private static final int CALLSIGN_CHARACTERS = 8;

    /**
     * Compact Constructor
     * @param timeStampNs : the time stamp of the message, expressed in nanoseconds
     * @param icaoAddress : the ICAO address of the message
     * @param category : the aircraft category of the message
     * @param callSign : the callSign of the message
     * @throws NullPointerException if callSign or icaoAddress are null
     * @throws IllegalArgumentException if timeStampNs is negative
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
        long me = rawMessage.payload();
        StringBuilder stringBuilder = new StringBuilder();
        for(int iChar = CALLSIGN_CHARACTERS - 1 ; iChar >= 0 ; iChar--){
            int c = Bits.extractUInt(me,iChar*6,6);
            if((c >= '0' && c <= '9') || c == ' ')
               stringBuilder.append((char) c);
            else if(c >= 1 && c <= 26)
                stringBuilder.append((char) (c + 'A' - 1));
            else
                return null;
        }
        CallSign callSign = new CallSign(stringBuilder.toString().trim());
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int category = ((14 - rawMessage.typeCode()) << 4) | Bits.extractUInt(me,CA_START_INDEX,CA_SIZE);
        long timeStampsNs = rawMessage.timeStampNs();

        return new AircraftIdentificationMessage(timeStampsNs, icaoAddress, category, callSign);
    }
}

