package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {
    private IntegerProperty zoom = new SimpleIntegerProperty();
    private DoubleProperty minX = new SimpleDoubleProperty();
    private DoubleProperty minY = new SimpleDoubleProperty();

    public MapParameters(int zoom, double minX, double minY){
        Preconditions.checkArgument(zoom >= 6 && zoom <= 19);
        this.zoom.set(zoom);
        this.minX.set(minX);
        this.minY.set(minY);
    }
    public void scroll(double x ,double y){
        minX.set(getMinX()+x);
        minY.set(getMinY()+y);
    }
    public void changeZoomLevel(int difference){
        zoom.set(Math2.clamp(6,getZoom() + difference,19));
    }
    //------------------------------------------------------------
    public  ReadOnlyIntegerProperty zoomProperty(){
        return zoom;
    }
    public int getZoom(){
        return zoom.get();
    }
    //------------------------------------------------------------
    public  ReadOnlyDoubleProperty minXProperty(){
        return minX;

    }
    public double getMinX(){
        return minX.get();
    }
    //------------------------------------------------------------
    public  ReadOnlyDoubleProperty minYProperty(){
        return minY;
    }
    public double getMinY(){
        return minY.get();
    }

}
