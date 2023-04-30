package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Offer getters of two message's attributes
 * @author Zablocki Victor (361602)
 */
public interface Message {
     /**
      * @return the time stamp of the message in nanoseconds
      */
     long timeStampNs();

     /**
      * @return the ICAO address associated to the message
      */
     IcaoAddress icaoAddress();
}
