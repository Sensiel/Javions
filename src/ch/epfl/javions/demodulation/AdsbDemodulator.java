package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private PowerWindow demodulateur;
    private static double lastSommeP = 0;
    private static double nextSommeP = 0;
    private static final long DURATION_2_SAMPLES = 4; //jsp c quoi la valeur le prof veut pas me la passer :/

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        demodulateur = new PowerWindow(samplesStream, 1200);
    }


    public RawMessage nextMessage() throws IOException {
        int result = 0;
        byte[] bytes = new byte[14];
        while (demodulateur.isFull()) {
            double sommeP = demodulateur.get(0) + demodulateur.get(10) + demodulateur.get(35) + demodulateur.get(45);
            double sommeV = demodulateur.get(5) + demodulateur.get(15) + demodulateur.get(20) + demodulateur.get(25)
                    + demodulateur.get(30) + demodulateur.get(40);
            lastSommeP = sommeP; // pour 1er faire somme = 0 #329
            nextSommeP = demodulateur.get(1) + demodulateur.get(11) + demodulateur.get(36) + demodulateur.get(46);
            boolean conditionSatisfied = sommeP > lastSommeP && sommeP > nextSommeP && sommeP >= 2 * sommeV;
            if (conditionSatisfied) {
                for (int i = 0; i < 112; i++) {
                    if (demodulateur.get(80 + 10 * i) < demodulateur.get(85 + 10 * i)) {
                        result = (byte) (result << 1);
                    } else {
                        result = (byte) ((result << 1) | 1);
                    }
                    if(i % 7 == 0 && i > 0){
                        bytes[i/7 - 1]= (byte)result;
                        result = 0;
                        if ((RawMessage.size(bytes[0])) != RawMessage.LENGTH){
                            demodulateur.advance();}
                    }
                }
                demodulateur.advanceBy(1200); // passage au prochain message ?
                return RawMessage.of(demodulateur.position()*DURATION_2_SAMPLES,bytes);}
            else { demodulateur.advance(); }
            // horodotage #345
        }
        return null;
    }
}