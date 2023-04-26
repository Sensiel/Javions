package ch.epfl.javions;

import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;
import javafx.collections.ObservableSet;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static java.util.Collections.sort;

public class TextUI {
    public static void main(String[] args) {
        AircraftStateManager aircraftStateManager = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            long startTime = System.nanoTime();
            while (true) {
                long timeStampNs = s.readLong();
                if( nanoTime() - startTime < timeStampNs)
                    sleep((long) ((timeStampNs + startTime - nanoTime())/1e6));
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                aircraftStateManager.updateWithMessage(MessageParser.parse(RawMessage.of(timeStampNs,bytes)));

                updateBoard(aircraftStateManager);
                sleep(100);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) { /* nothing to do */ }
    }

    private static void updateBoard(AircraftStateManager aircraftStateManager) {
        String CSI = "\u001B";
        System.out.print(CSI + "[2J" + CSI + "[1;1f");
        System.out.println("OACI    Indicatif Immat.  Modèle             Longitude   Latitude   Alt.  Vit.");
        System.out.println("------------------------------------------------------------------------------");

        ObservableSet<ObservableAircraftState> states = aircraftStateManager.states();
        List<ObservableAircraftState> sortedStates = new ArrayList<>(states);
        AddressComparator addrComp = new AddressComparator();
        sortedStates.sort(addrComp);
        for(ObservableAircraftState state : sortedStates){
            System.out.print(state.address().string() + "  ");
            System.out.printf("%-10s", (state.getCallSign() != null )? state.getCallSign().string() : "");
            System.out.printf("%-8s", (state.getData() != null && state.getData().registration() != null)? state.getData().registration().string() : "");
            System.out.printf("%-16s",(state.getData() != null && state.getData().model() != null)? state.getData().model().substring(0,Math.min(16, state.getData().model().length())) : "");
            if(state.getData() != null && state.getData().model() != null && state.getData().model().length() > 16)
                System.out.print("…");
            else
                System.out.print(" ");
            System.out.print("   ");
            System.out.printf("% 3.5f", (state.getPosition() != null) ? Units.convertTo(state.getPosition().longitude(), Units.Angle.DEGREE): Double.NaN);
            System.out.print("   ");
            System.out.printf("% 3.5f", (state.getPosition() != null) ? Units.convertTo(state.getPosition().latitude(), Units.Angle.DEGREE): Double.NaN);
            System.out.print("  ");
            System.out.printf("%5d", (int)state.getAltitude());
            System.out.print("  ");
            System.out.printf("%4d", (int)state.getVelocity());
            System.out.println();

        }
    }

    private static class AddressComparator
            implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.address().string();
            String s2 = o2.address().string();
            return s1.compareTo(s2);
        }
    }
}
