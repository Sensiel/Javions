package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Arrays;
import java.util.Objects;

/**
 * Accumulate ADS-B messages from an aircraft to determine its state over time
 * @author Zablocki Victor (361602)
 * @param <T> : the state of the aircraft
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter>{
    private final T state;
    private final static double NEEDED_TIME_NS = 1e10;
    private AirbornePositionMessage[] messages = new AirbornePositionMessage[2];

    /**
     * Public Construtor
     * @param stateSetter : the state of the aircraft
     * @throws NullPointerException if stateSetter is null
     */
    public AircraftStateAccumulator(T stateSetter){
        Objects.requireNonNull(stateSetter);
        this.state = stateSetter;
    }

    /**
     * @return the state of the aircraft
     */
    public T stateSetter(){
        return state;
    }

    /**
     * Update the state of the aircraft according to the given message
     * @param message : the given message
     */
    public void update(Message message){
        state.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {state.setCategory(aim.category()); state.setCallSign(aim.callSign());}
            case AirborneVelocityMessage avm -> {state.setVelocity(avm.speed()); state.setTrackOrHeading(avm.trackOrHeading());}
            case AirbornePositionMessage apm -> {
                messages[apm.parity()] = apm;
                state.setAltitude(apm.altitude());
                if(messages[(apm.parity() + 1) % 2] != null && apm.timeStampNs() - messages[(apm.parity() + 1) % 2].timeStampNs() <= NEEDED_TIME_NS){
                    GeoPos pos = CprDecoder.decodePosition(messages[0].x(), messages[0].y(),messages[1].x(), messages[1].y(), apm.parity());
                    if(pos != null)
                        state.setPosition(pos);
                }
            }
            default -> throw new Error();
        }
    }
}
