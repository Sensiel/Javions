package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javafx.collections.FXCollections.observableSet;

public final class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> states = observableSet();
    private long lastTimeStampNs = 0L ;

    private final AircraftDatabase dataBase;
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
        AircraftStateAccumulator<ObservableAircraftState> asa = table.get(message.icaoAddress());
        asa.update(message);
        lastTimeStampNs = message.timeStampNs();

    }

    public void purge(){
        for(ObservableAircraftState aircraftState : states()){
        if(lastTimeStampNs - aircraftState.getLastMessageTimeStampNs() >= 60E10){
            states().remove(aircraftState);
        }
        }
    }

}
