package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private final PowerWindow demodulator;
    private static final long DURATION_2_SAMPLES = 100;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        demodulator = new PowerWindow(samplesStream, 1200);
    }


    public RawMessage nextMessage() throws IOException {
        double lastSommeP = 0;
        double nextSommeP = 0;

        parcour:
        while (demodulator.isFull()) {

            byte[] bytes = new byte[14];
            byte result = 0;
            double sommeP = demodulator.get(0) + demodulator.get(10) + demodulator.get(35) + demodulator.get(45);
            double sommeV = demodulator.get(5) + demodulator.get(15) + demodulator.get(20) + demodulator.get(25)
                    + demodulator.get(30) + demodulator.get(40);


            nextSommeP = demodulator.get(1) + demodulator.get(11) + demodulator.get(36) + demodulator.get(46);

            boolean conditionSatisfied = sommeP > lastSommeP && sommeP > nextSommeP && sommeP >= 2 * sommeV;
            lastSommeP = sommeP; // pour 1er faire somme = 0 #329

            if (conditionSatisfied) {
                for (int i = 0; i < 112; i++) {

                    byte currBit = (byte) ((demodulator.get(80 + 10 * i) < demodulator.get(85 + 10 * i)) ? 0 : 1);
                    result = (byte)(result << 1 | currBit);

                    if(i % 8 == 7){
                        bytes[(i-7)/8]= result;
                        result = 0;
                        if (i == 7 && (RawMessage.size(result)) != RawMessage.LENGTH){
                            demodulator.advance();
                            continue parcour;
                        }
                    }
                }
                long timeStamp = demodulator.position() * DURATION_2_SAMPLES;
                demodulator.advanceBy(1200);
                return RawMessage.of(timeStamp,bytes);
            }
            else demodulator.advance();
        }
        return null;
    }
}