package ch.epfl.javions;

public final class Units {
    public final static double CENTI = 0.01d;
    public final static double KILO = 1000d;

    public final class Angle {
        public static final double RADIAN = 1d;
        public static final double TURN = 2d * Math.PI;

        public static final double DEGREE = TURN/360d;
        public static final double T32 = Math.scalb(TURN,-32);

    }
    public final class Length {
        public static final double METER = 1d;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54d * CENTIMETER;
        public static final double FOOT = 12d * INCH;
        public static final double NAUTICAL_MILE = 1852d * METER;
    }

    public final class Time{
        public static final double SECOND = 1d;
        public static final double MINUTE = 60d * SECOND;
        public static final double HOUR = 60d * MINUTE;
    }

    public final class Speed{
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    public static double convert(double value, double fromUnit, double toUnit){
        return value * (fromUnit/toUnit);
    }
    public static double convertFrom(double value, double fromUnit){
        return value * fromUnit;
    }
    public static double convertTo(double value, double toUnit){
        return value / toUnit;
    }
}
