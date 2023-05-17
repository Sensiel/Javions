package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

/**
 * Represent a gradient of colors
 * @author Zablocki Victor (361602)
 */
public final class ColorRamp {
    private final Color[] colors;

    /**
     * Public Constructor
     * @param colors : a JavaFX color sequence
     * @throws IllegalArgumentException if the given colors are less than 2
     */
    public ColorRamp(Color... colors){
        Preconditions.checkArgument(colors.length >= 2);
        this.colors = colors.clone();
    }

    /**
     * Get the color associated to the given index
     * @param index : index of the color
     * @return the color associated to the given index
     */
    public Color at(double index){
        if(index <= 0d)
            return colors[0];
        if(index >= 1d)
            return colors[colors.length - 1];
        int iColorBefore = (int)Math.floor(index * (colors.length - 1));
        int iColorAfter = iColorBefore + 1;

        double indexBefore = (double)iColorBefore/(colors.length - 1);
        double indexAfter = (double)iColorAfter/(colors.length - 1);

        Color c1 = colors[iColorBefore], c2 = colors[iColorAfter];

        return c1.interpolate(c2, (index - indexBefore)/(indexAfter-indexBefore));
    }

    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));// The Plasma gradient of colors
}
