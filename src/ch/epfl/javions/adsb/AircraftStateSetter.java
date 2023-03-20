package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {
    /**
     * Change the timestamp, of the last message received from the aircraft, to the given value
     * @param timeStampNs : the given timeStampNs
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Change the category of the aircraft to the given value
     * @param category : the given value of category
     */
    void setCategory(int category);

    /**
     * Change the callsign of the aircraft to the given value
     * @param callSign : the given value
     */
    void setCallSign(CallSign callSign);

    /**
     * Change the position of the aircraft to the given value
     * @param position : the given position
     */
    void setPosition(GeoPos position);

    /**
     * Change the altitude of the aircraft to the given value
     * @param altitude : the given altitude
     */
    void setAltitude(double altitude);

    /**
     * Change the speed of the aircraft to the given value
     * @param velocity : the given velocity
     */
    void setVelocity(double velocity);

    /**
     * Change the direction of the aircraft to the given value
     * @param trackOrHeading : the given direction
     */
    void setTrackOrHeading(double trackOrHeading);
}
