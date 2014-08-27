package org.noamichael.utils.measurement;

import java.io.Serializable;

/**
 *
 * @author michael
 */
public class Dimension implements Serializable {

    private double meterValue;

    @FunctionalInterface
    public interface ConversionOperation {

        /**
         * Converts the value to the unit indicated by the Java variable name of
         * the operation.
         *
         * @param value The value to convert.
         * @return The converted value.
         */
        double convertValue(double value);
    }

    /**
     * Inches
     */
    public static final ConversionOperation FEET_TO_INCHES = (value) -> value * 12;
    public static final ConversionOperation YARDS_TO_INCHES = (value) -> value * 36;
    public static final ConversionOperation METER_TO_INCHES = (value) -> value * 39.3701;
    /**
     * Feet
     */
    public static final ConversionOperation INCHES_TO_FEET = (value) -> value / 12;
    public static final ConversionOperation YARDS_TO_FEET = (value) -> value * 3;
    public static final ConversionOperation METERS_TO_FEET = (value) -> value * 3.28084;
    /**
     * Yards
     */
    public static final ConversionOperation INCHES_TO_YARDS = (value) -> value / 36;
    public static final ConversionOperation FEET_TO_YARDS = (value) -> value / 3;
    public static final ConversionOperation METERS_TO_YARDS = (value) -> value * 1.09361;
    /**
     * Miles
     */
    public static final ConversionOperation METERS_TO_MILES = (value) -> value / 1609.34;
    /**
     * Meters
     */
    public static final ConversionOperation INCHES_TO_METERS = (value) -> value * 0.0254;
    public static final ConversionOperation FEET_TO_METERS = (value) -> value * 0.3048;
    public static final ConversionOperation YARDS_TO_METERS = (value) -> value * 0.9144;
    public static final ConversionOperation MILES_TO_METERS = (value) -> value / 1609.34;
    public static final ConversionOperation KILOMETERS_TO_METERS = (value) -> value * 1000;

    /**
     * Kilometers
     */
    public static final ConversionOperation METERS_TO_KILOMETERS = (value) -> value / 1000;

    public Dimension() {
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Dimension(UnitValue... args) {
        for (UnitValue unitValue : args) {
            this.addValue(unitValue.getUnit(), unitValue.getValue());
        }
    }

        public enum Imperial implements Unit<Imperial> {

            INCHES("Inches"),
            FEET("Feet"),
            YARDS("Yards"),
            MILES("MILES");
            private final String toString;

            private Imperial(String toString) {
                this.toString = toString;
            }

            @Override
            public String toString() {
                return toString;
            }

            @Override
            public Imperial getValue() {
                return this;
            }

        }

        public enum Metric implements Unit<Metric> {

            METER("Meter"),
            KILOMETER("Kilometer");
            private final String toString;

            private Metric(String toString) {
                this.toString = toString;
            }

            @Override
            public String toString() {
                return toString;
            }

            @Override
            public Metric getValue() {
                return this;
            }
        }

    public void addValue(Unit<?> unit, double value) {
        if (unit == null) {
            throw new DimensionException("Null is an unknown unit.");
        }
        if (unit.getValue() instanceof Imperial) {
            addValue((Imperial) unit.getValue(), value);
        } else if (unit.getValue() instanceof Metric) {
            addValue((Metric) unit.getValue(), value);
        }
    }

    public double getDoubleValue(Unit<?> unit) {
        if (unit == null) {
            throw new DimensionException("Null is an unknown unit.");
        }
        if (unit.getValue() instanceof Imperial) {
            return getDoubleValue((Imperial) unit.getValue());
        } else if (unit.getValue() instanceof Metric) {
            return getDoubleValue((Metric) unit.getValue());
        }
        throw new DimensionException("Unknown unit");
    }

    private void addValue(Imperial unit, double value) {
        if (unit == null) {
            throw new DimensionException("Null is an unknown unit.");
        }
        switch (unit) {
            case INCHES: {
                this.meterValue += INCHES_TO_METERS.convertValue(value);
                break;
            }
            case FEET: {
                this.meterValue += FEET_TO_METERS.convertValue(value);
                break;
            }
            case YARDS: {
                this.meterValue += YARDS_TO_METERS.convertValue(value);
                break;
            }
            case MILES: {
                this.meterValue += MILES_TO_METERS.convertValue(value);
            }
            default: {
                throw new DimensionException(String.format("[%s] is an unknown imperial unit.", unit.getClass().getName()));

            }
        }
    }

    public void clear() {
        this.meterValue = 0.0d;
    }

    private void addValue(Metric unit, double value) {
        if (unit == null) {
            throw new DimensionException("Null is an unknown unit.");
        }
        switch (unit) {
            case METER: {
                this.meterValue += value;
                break;
            }
            case KILOMETER: {
                this.meterValue += KILOMETERS_TO_METERS.convertValue(value);
                break;
            }
            default: {
                throw new DimensionException(String.format("[%s] is an unknown metric unit.", unit.getClass().getName()));

            }
        }
    }

    private double getDoubleValue(Imperial unit) {
        switch (unit) {
            case INCHES: {
                return METER_TO_INCHES.convertValue(meterValue);
            }
            case FEET: {
                return METERS_TO_FEET.convertValue(meterValue);
            }
            case YARDS: {;
                return METERS_TO_YARDS.convertValue(meterValue);
            }
            case MILES: {
                return METERS_TO_MILES.convertValue(meterValue);
            }
            default: {
                throw new DimensionException(String.format("[%s] is an unknown imperial unit.", unit.getClass().getName()));
            }
        }
    }

    private double getDoubleValue(Metric unit) {
        switch (unit) {
            case METER: {
                return meterValue;
            }
            case KILOMETER: {
                return METERS_TO_KILOMETERS.convertValue(meterValue);
            }
            default: {
                throw new DimensionException(String.format("[%s] is an unknown metric unit.", unit.getClass().getName()));
            }
        }
    }
}
