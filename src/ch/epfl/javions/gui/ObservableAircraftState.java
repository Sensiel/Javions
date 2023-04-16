package ch.epfl.javions.gui;

import java.util.List;
import java.util.Objects;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

public final class ObservableAircraftState implements AircraftStateSetter {
    public record AirbornePos(GeoPos pos, double altitude) {
        public AirbornePos{

        }
    }

    //private final IcaoAddress icaoAddress;
    private LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private IntegerProperty category = new SimpleIntegerProperty();
    private ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private ObservableList<AirbornePos> trajectory = observableArrayList();
    private ObservableList<AirbornePos> readOnlyTrajectory = unmodifiableObservableList(trajectory);
    private DoubleProperty altitude = new SimpleDoubleProperty();
    private DoubleProperty velocity = new SimpleDoubleProperty();
    private DoubleProperty trackOrHeading = new SimpleDoubleProperty();

    private long lastTimeStampNsChangingTraj = 0L;

    public ObservableAircraftState(String icaoAddress, AircraftData data) {
        Objects.requireNonNull(icaoAddress);
    }
    //--------------------------------------------------------------//
    ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }
    int getCategory(){
        return category.get();
    }
    @Override
    public void setCategory(int category){
        this.category.set(category);
    }

    //--------------------------------------------------------------//

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs){
        this.lastMessageTimeStampNs.set(timeStampNs);
    }

    ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }
    long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setCallSign(CallSign callSign){
        this.callSign.set(callSign);
    }

    ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }
    CallSign getCallSign(){
        return callSign.get();
    }

    //--------------------------------------------------------------//
    @Override
    public void setPosition(GeoPos position){
        if(trajectory.isEmpty() || position != getPosition()){
            trajectory.add(new AirbornePos(position, getAltitude()));
            lastTimeStampNsChangingTraj = getLastMessageTimeStampNs();
        }
        else if(lastTimeStampNsChangingTraj == getLastMessageTimeStampNs())
            trajectory.set(trajectory.size() - 1, new AirbornePos(position, getAltitude()));

        this.position.set(position);
    }

    ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }
    GeoPos getPosition(){
        return position.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setAltitude(double altitude){
        if(lastTimeStampNsChangingTraj == getLastMessageTimeStampNs())
            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), altitude));

        this.altitude.set(altitude);
    }

    ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }
    double getAltitude(){
        return altitude.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setVelocity(double velocity){
        this.velocity.set(velocity);
    }

    ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }
    double getVelocity(){
        return velocity.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setTrackOrHeading(double trackOrHeading){
        this.trackOrHeading.set(trackOrHeading);
    }

    ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeading;
    }
    double getTrackOrHeading(){
        return trackOrHeading.get();
    }

    //--------------------------------------------------------------//

    ObservableList<AirbornePos> trajectoryProperty(){
        return readOnlyTrajectory;
    }
    //TODO je sais pas trop
    ObservableList<AirbornePos> getTrajectory(){
        return trajectory;
    }

    //--------------------------------------------------------------//

}