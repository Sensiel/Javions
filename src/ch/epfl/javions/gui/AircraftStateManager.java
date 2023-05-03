package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

import static javafx.collections.FXCollections.observableSet;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

public final class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> states = observableSet();
    private final ObservableSet<ObservableAircraftState> unmodifiableStates;
    private long lastTimeStampNs = 0L ;
    private final AircraftDatabase dataBase;
    private final static double MINUTE_IN_NS = 6E10;
    public AircraftStateManager(AircraftDatabase data){
        dataBase = data;
        unmodifiableStates = unmodifiableObservableSet(states);
    }

    public ObservableSet<ObservableAircraftState> states(){
        return unmodifiableStates;
    }

    public void updateWithMessage(Message message){
        if(message == null)
            return;

        if(!table.containsKey(message.icaoAddress())){
            try {
                table.put(message.icaoAddress(),
                        new AircraftStateAccumulator<>(new ObservableAircraftState(message.icaoAddress(),
                                dataBase.get(message.icaoAddress()))));
            } catch (IOException ignored) {System.out.println("Problem " + message.icaoAddress());}
        }
        AircraftStateAccumulator<ObservableAircraftState> asa = table.get(message.icaoAddress());
        asa.update(message);
        if(!states.contains(asa.stateSetter()) && asa.stateSetter().getPosition() != null){
            states.add(asa.stateSetter());
        }

        lastTimeStampNs = message.timeStampNs();
    }

    public void purge(){
        states.removeIf(aircraftState -> lastTimeStampNs - aircraftState.getLastMessageTimeStampNs() >= MINUTE_IN_NS);
    }

}
