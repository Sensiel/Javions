package ch.epfl.javions;

public final class Math2 {
    private Math2(){

    }

    /**
     * @param min : the chosen minimum
     * @param v : the given value
     * @param max : the chosen maximum
     * @return the value v clamped between the minimum min and the maximum max
     */
    public static int clamp(int min, int v, int max){
        return Math.min( Math.max(min,v) , max);
    }

    /**
     * @param x : the given value
     * @return the inverse of the hyperbolic sin applied to x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }
}
