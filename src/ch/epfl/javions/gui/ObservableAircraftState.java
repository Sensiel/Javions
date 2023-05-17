package ch.epfl.javions.gui;

import java.util.Objects;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * Represent the state of an aircraft
 * @author Zablocki Victor (361602)
 */
public final class ObservableAircraftState implements AircraftStateSetter {
    /**
     * Represent the positions of the aircraft
     * @param pos : the position of the aircraft
     * @param altitude : the altitude of the aircraft
     */
    public record AirbornePos(GeoPos pos, double altitude) {
        /**
         * Compact constructor
         * @param pos : the position of the aircraft
         * @param altitude : the altitude of the aircraft
         */
        public AirbornePos{}
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

    /**
     * Public Constructor
     * @param icaoAddress : the ICAO address of the aircraft
     * @param data : the fied data of the aircraft
     * @throws NullPointerException if icaoAddress is null
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        Objects.requireNonNull(icaoAddress);
        this.icaoAddress= icaoAddress;
        this.data = data;
    }

    /**
     * Getter for the icaoAddress
     * @return the icaoAddress of the aircraft
     */
    public IcaoAddress address() {
        return icaoAddress;
    }
    /**
     * Getter for the data
     * @return the data of the aircraft
     */
    public AircraftData getData() {
        return data;
    }

    //--------------------------------------------------------------//
    /**
     * Getter for the categoryProperty
     * @return a read-only property of the category of the aircraft
     */
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }

    /**
     * Getter for the value of the category
     * @return the category of the aircraft
     */
    public int getCategory(){
        return category.get();
    }

    /**
     * Setter for the category of the aircraft
     * @param category : the given  category
     */
    @Override
    public void setCategory(int category){
        this.category.set(category);
    }

    //--------------------------------------------------------------//
    /**
     * Setter for the timeStamp of the aircraft
     * @param timeStampNs : the given timeStampNs
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs){
        this.lastMessageTimeStampNs.set(timeStampNs);
    }
    /**
     * Getter for the last timeStampProperty
     * @return a read-only property of the timeStamp of the last aircraft
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }
    /**
     * Getter for the value of the timeStamp
     * @return the category of the timeStamp of the last aircraft
     */
    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }

    //--------------------------------------------------------------//
    /**
     * Setter for the Callsign of the aircraft
     * @param callSign : the given callsign
     */
    @Override
    public void setCallSign(CallSign callSign){
        this.callSign.set(callSign);
    }
    /**
     * Getter for the Callsign
     * @return a read-only property of the callsign of the aircraft
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }
    /**
     * Getter for the value of the callSign
     * @return the callSign of the aircraft
     */
    public CallSign getCallSign(){
        return callSign.get();
    }

    //--------------------------------------------------------------//

    /**
     * Setter for the Position of the aircraft
     * @param position : the given position
     */
    @Override
    public void setPosition(GeoPos position){
        if(getAltitude() != 0){
            trajectory.add(new AirbornePos(position, getAltitude()));
            lastTimeStampNsChangingTraj = getLastMessageTimeStampNs();
        }

        this.position.set(position);
    }

    /**
     * Getter for the Position
     * @return a read-only property of the position of the aircraft
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    /**
     * Getter for the value of the position
     * @return the position of the aircraft
     */
    public GeoPos getPosition(){
        return position.get();
    }

    //--------------------------------------------------------------//

    /**
     * Setter for the altitude of the aircraft
     * @param altitude : the given altitude
     */
    @Override
    public void setAltitude(double altitude){
        if(trajectory.isEmpty() && getPosition() != null) {
            trajectory.add(new AirbornePos(getPosition(), altitude));
            lastTimeStampNsChangingTraj = getLastMessageTimeStampNs();
        }
        if(lastTimeStampNsChangingTraj == getLastMessageTimeStampNs())
            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), altitude));

        this.altitude.set(altitude);
    }

    /**
     * Getter for the altitude
     * @return a read-only property of the altitude of the aircraft
     */
    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }

    /**
     * Getter for the value of the altitude
     * @return the altitude of the aircraft
     */
    public double getAltitude(){
        return altitude.get();
    }

    //--------------------------------------------------------------//

    /**
     * Setter for the Velocity of the aircraft
     * @param velocity : the given velocity
     */
    @Override
    public void setVelocity(double velocity){
        this.velocity.set(velocity);
    }

    /**
     * Getter for the velocity
     * @return a read-only property of the velocity of the aircraft
     */
    public ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }

    /**
     * Getter for the value of the velocity
     * @return the velocity of the aircraft
     */
    public double getVelocity(){
        return velocity.get();
    }

    //--------------------------------------------------------------//

    /**
     * Setter for the direction of the aircraft
     * @param trackOrHeading : the given direction
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading){
        this.trackOrHeading.set(trackOrHeading);
    }

    /**
     * Getter for the direction
     * @return a read-only property of the direction of the aircraft
     */
    ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeading;
    }

    /**
     * Getter for the value of the direction
     * @return the direction of the aircraft
     */
    double getTrackOrHeading(){
        return trackOrHeading.get();
    }

    //--------------------------------------------------------------//

    /**
     * Getter for the trajectory
     * @return a readable-only List of the trajectory of the aircraft
     */
    ObservableList<AirbornePos> trajectoryProperty(){
        return readOnlyTrajectory;
    }

    //--------------------------------------------------------------//

}