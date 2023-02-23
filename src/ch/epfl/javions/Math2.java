package ch.epfl.javions;

public final class Math2 {
    private Math2(){

    }
    public static int clamp(int min, int v, int max){
        return Math.min( Math.max(min,v) , max);
    }
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }
}
