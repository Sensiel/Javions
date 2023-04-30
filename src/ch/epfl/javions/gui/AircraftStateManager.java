package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

import static javafx.collections.FXCollections.observableSet;

public final class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> states = observableSet();
    private long lastTimeStampNs = 0L ;
    private final AircraftDatabase dataBase;
    private final static double MINUTE_IN_NS = 60E10;
    public AircraftStateManager(AircraftDatabase data){
        dataBase = data;
    }

    public ObservableSet<ObservableAircraftState> states(){
        Set<ObservableAircraftState> resultSet = new HashSet<>();
        for(AircraftStateAccumulator<ObservableAircraftState> asa : table.values()){
            if(asa.stateSetter().getPosition() != null){
                resultSet.add(asa.stateSetter());
            }
        }
        return observableSet(resultSet);
    }

    public void updateWithMessage(Message message){
        //TODO peut être enlever ce test
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
        lastTimeStampNs = message.timeStampNs();
    }

    public void purge(){
        for(ObservableAircraftState aircraftState : states()){
            if(lastTimeStampNs - aircraftState.getLastMessageTimeStampNs() >= MINUTE_IN_NS){
                states().remove(aircraftState);
            }
        }
    }

}
