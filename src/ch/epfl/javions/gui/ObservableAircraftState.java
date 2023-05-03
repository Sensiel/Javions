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

    private final IcaoAddress icaoAddress;

    private final AircraftData data;

    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectory = observableArrayList();
    private final ObservableList<AirbornePos> readOnlyTrajectory = unmodifiableObservableList(trajectory);
    private final DoubleProperty altitude = new SimpleDoubleProperty();
    private final DoubleProperty velocity = new SimpleDoubleProperty();
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();

    private long lastTimeStampNsChangingTraj = 0L;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        Objects.requireNonNull(icaoAddress);
        this.icaoAddress= icaoAddress;
        this.data = data;
    }

    public IcaoAddress address() {
        return icaoAddress;
    }

    public AircraftData getData() {
        return data;
    }

    //--------------------------------------------------------------//
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }
    public int getCategory(){
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

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }
    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setCallSign(CallSign callSign){
        this.callSign.set(callSign);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }
    public CallSign getCallSign(){
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

    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }
    public GeoPos getPosition(){
        return position.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setAltitude(double altitude){
        if(trajectory.isEmpty()) {
            trajectory.add(new AirbornePos(getPosition(), altitude));
            lastTimeStampNsChangingTraj = getLastMessageTimeStampNs();
        }
        if(lastTimeStampNsChangingTraj == getLastMessageTimeStampNs())
            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), altitude));

        this.altitude.set(altitude);
    }

    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }
    public double getAltitude(){
        return altitude.get();
    }

    //--------------------------------------------------------------//

    @Override
    public void setVelocity(double velocity){
        this.velocity.set(velocity);
    }

    public ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }
    public double getVelocity(){
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

    //--------------------------------------------------------------//

}