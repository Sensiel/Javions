package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private final PowerWindow window;
    private static final long DURATION_2_SAMPLES = 100;

    /**
     * Public Constructor
     * @param samplesStream : the input stream needed to create an instance of PowerWindow
     * @throws IOException if an input/output error occurs during the creation of the instance of PowerWindow
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, 1200);
    }

    /**
     * Evaluate the next ADS-B message of the given sample stream in the constructor
     * @return a RawMessage associated to the ADS-B message if the end of the sample stream has not been reached
     * @throws IOException if an input/output error
     */
    public RawMessage nextMessage() throws IOException {
        if(!window.isFull())
            return null;
        double lastSommeP = 0;
        double nextSommeP = window.get(0) + window.get(10) + window.get(35) + window.get(45);

        while (window.isFull()) {
            double sommeP = nextSommeP;
            double sommeV = window.get(5) + window.get(15) + window.get(20) + window.get(25)
                    + window.get(30) + window.get(40);

            nextSommeP = window.get(1) + window.get(11) + window.get(36) + window.get(46);

            boolean conditionSatisfied = sommeP > lastSommeP && sommeP > nextSommeP && sommeP >= 2 * sommeV;
            lastSommeP = sommeP;
            boolean isWindowValid = true;
            if (conditionSatisfied) {
                byte result = 0;
                byte[] bytes = new byte[14];
                for (int i = 0; i < 112; i++) {
                    byte currBit = (byte) ((window.get(80 + 10 * i) < window.get(85 + 10 * i)) ? 0 : 1);
                    result = (byte)((result << 1) | currBit);

                    if(i % 8 == 7){
                        bytes[(i-7)/8] = result;

                        if (i == 7 && (RawMessage.size(result)) != RawMessage.LENGTH){
                            window.advance();
                            isWindowValid = false;
                            break;
                        }
                        result = 0;
                    }
                }
                if(isWindowValid){
                    long timeStamp = window.position() * DURATION_2_SAMPLES;
                    RawMessage potentialResult = RawMessage.of(timeStamp,bytes);
                    if(potentialResult == null)
                        window.advance();
                    else {
                        window.advanceBy(1200);
                        return potentialResult;
                    }
                }
            }
            else
                window.advance();
        }
        return null;
    }
}