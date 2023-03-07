package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    private final static int BATCH_SIZE = 1 << 16; //changer le nom ?
    private int[] batchpos1;
    private int[] batchpos2;
    private int windowSize;
    private PowerComputer powerComputer;
    private static long WindowPos = 0; // doit etre un long ?

    PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.windowSize = windowSize;
        int n = powerComputer.readBatch(batchpos1); // doit etre stocké ds attribut privé ?
        batchpos2 = new int[BATCH_SIZE];

    }
    public int size(){
        return windowSize;
    }
    public long position(){
        return WindowPos;
    }
    public boolean isFull(){

        return false;
    }
    public int get(int i){
            if( i < 0 || i >= windowSize) throw new IndexOutOfBoundsException();
        return 0;
        //vous devez calculer si l'index passé appartient au premier ou au deuxième tableau
        //la méthode get doit controler que l'indice soit dans la fenetre
    }
    public void advance() throws IOException{
        WindowPos +=1 ;
        if(WindowPos >= BATCH_SIZE ){ // FAUT RJT MODULO
            int n = powerComputer.readBatch(batchpos2);}
        if(WindowPos % BATCH_SIZE == size() && WindowPos >BATCH_SIZE){
                batchpos1 = batchpos2 ;
            }
            //placer le contenu de ce nouveau lot dans le deuxième tableau
            //determiner si la fenêtre intersecte un nouveau lot, et si oui, appeler readBatch et lire un nouveau lot.
        }

    public void advanceBy(int offset) throws IOException{
            Preconditions.checkArgument(offset > 0 );
            for(int i =1; i<= offset ; i++){
            advance();
            }
    }
}
