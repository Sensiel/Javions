package ch.epfl.javions;

public final class Math2 {
    private Math2(){

    }
//? pas capté
    /**
     *
     * @param min
     * @param v
     * @param max
     * @return
     */
    public static int clamp(int min, int v, int max){
        return Math.min( Math.max(min,v) , max);
    }

    /**
     * @param x
     * @return the inverse of the hyperbolic sin applied to x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }
}
