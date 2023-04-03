package ch.epfl.javions.adsb;

import java.util.Objects;

public class AircraftStateAccumulator<T extends AircraftStateSetter>{
    private final T state;
    private AirbornePositionMessage[] messages = new AirbornePositionMessage[2];
    public AircraftStateAccumulator(T stateSetter){
        Objects.requireNonNull(stateSetter);
        this.state = stateSetter;
    }

    public T stateSetter(){
        return state;
    }

    public void update(Message message){
        state.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {state.setCategory(aim.category()); state.setCallSign(aim.callSign());}
            case AirborneVelocityMessage avm -> {state.setVelocity(avm.speed()); state.setTrackOrHeading(avm.trackOrHeading());}
            case AirbornePositionMessage apm -> {
                messages[apm.parity()] = apm;
                if(messages[(apm.parity() + 1) % 2] != null && apm.timeStampNs() - messages[(apm.parity() + 1) % 2].timeStampNs() <= 1e10){
                    state.setPosition(CprDecoder.decodePosition(messages[0].x(), messages[0].y(),messages[1].x(), messages[1].y(), apm.parity()));
                }
                state.setAltitude(apm.altitude());
            }
            default -> throw new Error();
        }
    }

}
