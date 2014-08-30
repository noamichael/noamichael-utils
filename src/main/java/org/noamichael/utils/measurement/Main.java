package org.noamichael.utils.measurement;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {
        Dimension dimensions = new Dimension();
        dimensions.addValue(Dimension.Imperial.FEET, 5);
        dimensions.addValue(Dimension.Imperial.YARDS, 3);
        dimensions.addValue(Dimension.Imperial.INCHES, 100);
        Dimension dimension2 = new Dimension();
        dimension2.addValue(Dimension.Imperial.MILES, 3);
    }
}
