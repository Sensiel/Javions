package ch.epfl.javions;

/**
 * Define SI prefixes as well as different units useful to the project, and offer conversion methods
 * @author Imane Raihane (362230)
 * @author Zablocki Victor (361602)
 */
public final class Units {
    public final static double CENTI = 0.01d; // SI prefix
    public final static double KILO = 1000d; // SI prefix

    /**
     * Defines different angle units useful to the project
     */
    public static final class Angle {
        public static final double RADIAN = 1d;
        public static final double TURN = 2d * Math.PI;

        public static final double DEGREE = TURN/360d;
        public static final double T32 = Math.scalb(TURN,-32);

    }

    /**
     * Defines different length units useful to the project
     */
    public static final class Length {
        public static final double METER = 1d;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54d * CENTIMETER;
        public static final double FOOT = 12d * INCH;
        public static final double NAUTICAL_MILE = 1852d * METER;
    }

    /**
     * Defines different time units useful to the project
     */
    public static final class Time{
        public static final double SECOND = 1d;
        public static final double MINUTE = 60d * SECOND;
        public static final double HOUR = 60d * MINUTE;
    }

    /**
     * Defines different speed units useful to the project
     */
    public static final class Speed{
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    /**
     * Convert units
     * @param value : the value that'll be converted
     * @param fromUnit : the unit of value
     * @param toUnit : the unit we want to convert to
     * @return value converted in toUnit
     */
    public static double convert(double value, double fromUnit, double toUnit){
        return value * (fromUnit/toUnit);
    }

    /**
     * Convert to the basic unit
     * @param value : the value that'll be converted
     * @param fromUnit : the unit of value
     * @return value converted in the basic unit
     */
    public static double convertFrom(double value, double fromUnit){
        return value * fromUnit;
    }

    /**
     * Convert from the basic unit to the given unit
     * @param value : the value that'll be converted
     * @param toUnit : the unit we want to convert to
     * @return value converted in toUnit
     */
    public static double convertTo(double value, double toUnit){
        return value / toUnit;
    }
}
