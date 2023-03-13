package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private PowerWindow demodulateur;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        demodulateur = new PowerWindow(samplesStream, 1200);
    }
    public RawMessage nextMessage() throws IOException {
        //une boucle while et s'arrête jusqu'a ce qu'on ait trouve un message valide
        return null;
    }
}
