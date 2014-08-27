package org.noamichael.utils;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {
        Dimension demensions = new Dimension();
        demensions.addValue(Dimension.Units.Imperial.FEET, 5);
        demensions.addValue(Dimension.Units.Imperial.YARDS, 3);
        demensions.addValue(Dimension.Units.Imperial.INCHES, 100);
        System.out.println(demensions.getDoubleValue(Dimension.Units.Metric.KILOMETER));
            }
        }
