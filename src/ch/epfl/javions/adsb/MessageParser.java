package ch.epfl.javions.adsb;

public class MessageParser {
    private MessageParser(){}
    Message parse(RawMessage rawMessage){
        if((rawMessage.typeCode() >= 9 && rawMessage.typeCode() <= 18) ||(rawMessage.typeCode() >= 20 && rawMessage.typeCode() <= 22)){
            return AirbornePositionMessage.of(rawMessage); //jsp laquelle est meilleure entre les deux
        }
        switch (rawMessage.typeCode()){
            case 19 -> {return AirborneVelocityMessage.of(rawMessage);}
            case 1,2,3,4 -> { return AircraftIdentificationMessage.of(rawMessage);}
            case 9,10,11,12,13,14,15,16,17,18,20,21,22 -> {return AirbornePositionMessage.of(rawMessage);}
            default -> {return null;}
        }
    }
}
