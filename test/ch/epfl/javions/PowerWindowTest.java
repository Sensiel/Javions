package ch.epfl.javions;


import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.*;

public class PowerWindowTest {
    @Test
    void TrivialPowerWindowTest() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("resources/samples.bin"))));
        PowerWindow pWin = new PowerWindow(stream, 160);
        System.out.println(pWin.get(0));
        pWin.advance();
        System.out.println(pWin.get(0));
        pWin.advanceBy(1040);
        System.out.println(pWin.get(0));
        System.out.println(pWin.position() + " " + pWin.isFull());
    }

}
