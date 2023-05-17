package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.WebMercator;
import javafx.beans.property.*;

/**
 * Represent the parameters of the portion of the map visible in the graphical interface
 * @author Imane Raihane (362230)
 */
public final class MapParameters {
    private final IntegerProperty zoom = new SimpleIntegerProperty();
    private final DoubleProperty minX = new SimpleDoubleProperty();
    private final DoubleProperty minY = new SimpleDoubleProperty();


    /**
     * Public Constructor
     * @param zoom : the level of zoom of the map
     * @param minX : the x-coordinate of the top-left corner of the visible portion of the map
     * @param minY : the y-coordinate of the top-left corner of the visible portion of the map
     */
    public MapParameters(int zoom, double minX, double minY){
        Preconditions.checkArgument(zoom >= WebMercator.ZOOM_MIN && zoom <= WebMercator.ZOOM_MAX);
        this.zoom.set(zoom);
        this.minX.set(minX);
        this.minY.set(minY);
    }

    /**
     * Translate the top-left corner of the displayed map portion of this vector
     * @param x : the x-coordinate of the vector
     * @param y : the y-coordinate of the vector
     */
    public void scroll(double x ,double y){
        minX.set(getMinX() + x);
        minY.set(getMinY() + y);
    }

    /**
     * Update the displayed map portion with the given difference in zoom
     * @param difference : the difference in zoom level
     */
    public void changeZoomLevel(int difference){
        int realDiff = Math2.clamp(WebMercator.ZOOM_MIN,getZoom() + difference,WebMercator.ZOOM_MAX) - getZoom();
        if(realDiff == 0)
            return;
        zoom.set(getZoom() + realDiff);
        minX.set(Math.scalb(getMinX(), realDiff));
        minY.set(Math.scalb(getMinY(), realDiff));
    }
    //------------------------------------------------------------

    /**
     * Getter for the zoom level
     * @return a read-only property of the zoom level of the map
     */
    public  ReadOnlyIntegerProperty zoomProperty(){
        return zoom;
    }

    /**
     * Getter for the value of the zoom level
     * @return the zoom level of the map
     */
    public int getZoom(){
        return zoom.get();
    }
    //------------------------------------------------------------

    /**
     * Getter for x-coordinate of the top-left corner of the displayed map
     * @return a read-only property x-coordinate of top-left corner of the displayed map
     */
    public  ReadOnlyDoubleProperty minXProperty(){
        return minX;
    }

    /**
     * Getter for the value of x-coordinate of the top-left corner of the displayed map
     * @return x-coordinate of the top-left corner of the displayed map
     */
    public double getMinX(){
        return minX.get();
    }
    //------------------------------------------------------------

    /**
     * Getter for y-coordinate of the top-left corner of the displayed map
     * @return a read-only property y-coordinate of top-left corner of the displayed map
     */
    public  ReadOnlyDoubleProperty minYProperty(){
        return minY;
    }

    /**
     * Getter for the value of y-coordinate of the top-left corner of the displayed map
     * @return y-coordinate of the top-left corner of the displayed map
     */
    public double getMinY(){
        return minY.get();
    }

}
