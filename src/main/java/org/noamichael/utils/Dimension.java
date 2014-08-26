package org.noamichael.utils;

import java.io.Serializable;

/**
 *
 * @author michael
 */
public class Dimension implements Serializable {

    private double footValue;

    private double meterValue;

    @FunctionalInterface
    public interface ConversionOperation {

        double convertValue(double value);
    }

    public static final ConversionOperation FEET_TO_INCHES = (value) -> value * 12;
    public static final ConversionOperation YARDS_TO_INCHES = (value) -> value * 36;
    public static final ConversionOperation METER_TO_INCHES = (value) -> value * 39.3701;

    public static final ConversionOperation INCHES_TO_FEET = (value) -> value / 12;
    public static final ConversionOperation YARDS_TO_FEET = (value) -> value * 3;
    public static final ConversionOperation METERS_TO_FEET = (value) -> value * 3.28084;

    public static final ConversionOperation INCHES_TO_YARDS = (value) -> value / 36;
    public static final ConversionOperation FEET_TO_YARDS = (value) -> value / 3;
    public static final ConversionOperation METERS_TO_YARDS = (value) -> value * 1.09361;

    public static final ConversionOperation INCHES_TO_METERS = (value) -> value * 0.0254;
    public static final ConversionOperation FEET_TO_METERS = (value) -> value / 12;
    public static final ConversionOperation YARDS_TO_METERS = (value) -> value * 0.9144;

    public static class Units {

        public enum Imperial {

            INCHES("Inches"),
            FEET("Feet"),
            YARDS("Yards");
            private final String toString;

            private Imperial(String toString) {
                this.toString = toString;
            }

            @Override
            public String toString() {
                return toString;
            }

        }

        public enum Metric {

            METER("Meter");
            private final String toString;

            private Metric(String toString) {
                this.toString = toString;
            }

            @Override
            public String toString() {
                return toString;
            }
        }
    }

    public void addValue(Units.Imperial unit, double value) {
        if (unit == null) {
            throw new DimensionException("Null is an unknown unit.");
        }
        switch (unit) {
            case INCHES: {
                this.footValue += INCHES_TO_FEET.convertValue(value);
                break;
            }
            case FEET: {
                this.footValue += value;
                break;
            }
            case YARDS: {
                this.footValue += YARDS_TO_FEET.convertValue(value);
                break;
            }
            default: {
                throw new DimensionException(String.format("Null is an unknown unit.", unit.getClass().getName()));

            }
        }
    }

    public double getDoubleValue(Units.Imperial unit) {
        double runningValue = 0;
        switch (unit) {
            case INCHES: {
                runningValue += FEET_TO_INCHES.convertValue(footValue);
                runningValue += METER_TO_INCHES.convertValue(meterValue);
                return runningValue;
            }
            case FEET: {
                runningValue += footValue;
                runningValue += METERS_TO_FEET.convertValue(meterValue);
                return runningValue;
            }
            case YARDS: {
                runningValue += FEET_TO_YARDS.convertValue(footValue);
                runningValue += METERS_TO_YARDS.convertValue(meterValue);
                return runningValue;
            }
            default: {
                throw new DimensionException(String.format("Null is an unknown unit.", unit.getClass().getName()));
            }
        }
    }
}
