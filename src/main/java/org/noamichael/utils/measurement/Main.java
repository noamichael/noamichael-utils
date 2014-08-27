package org.noamichael.utils.measurement;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {
        Dimension demensions = new Dimension();
        demensions.addValue(Dimension.Imperial.FEET, 5);
        demensions.addValue(Dimension.Imperial.YARDS, 3);
        demensions.addValue(Dimension.Imperial.INCHES, 100);
        System.out.println(demensions.getDoubleValue(Dimension.Imperial.MILES));
            }
        }
