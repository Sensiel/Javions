package ch.epfl.javions.adsb;

public class MessageParser {
    private MessageParser(){}

    /**
     * Evaluate the typecode of the given message in order to identify the corresponding class
     * @param rawMessage : the given message
     * @return the instance of AircraftIdentificationMessage,AirbornePositionMessage or AirborneVelocityMessage corresponding to the given raw message
     */
    public static Message parse(RawMessage rawMessage){
        if((rawMessage.typeCode() >= 9 && rawMessage.typeCode() <= 18) ||(rawMessage.typeCode() >= 20 && rawMessage.typeCode() <= 22)){
            return AirbornePositionMessage.of(rawMessage);
        }
        switch (rawMessage.typeCode()){
            case 19 -> {return AirborneVelocityMessage.of(rawMessage);}
            case 1,2,3,4 -> { return AircraftIdentificationMessage.of(rawMessage);}
            default -> {return null;}
        }
    }
}
