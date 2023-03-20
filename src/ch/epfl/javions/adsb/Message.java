package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message {
     /**
      * @return the time stamp of the message in nanoseconds
      */
     long timeStampNs();

     /**
      * @return the ICAO address of the sender of the message
      */
     IcaoAddress icaoAddress();
}
