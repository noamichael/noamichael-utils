package org.noamichael.utils.se;

/**
 *
 * @author Michael Kucinski
 */
public class IntegerUtil {

    public static boolean matches(int x, String regex) {
        return String.valueOf(x).matches(regex);
    }

    public static boolean size(int valueToCheck, int numberOfDigits) {
        return String.valueOf(valueToCheck).length() == numberOfDigits;
    }
    
    public static boolean checkScale(double valueToCheck, long scale){
        String stringRepresentation = String.valueOf(valueToCheck);
        String[] splitString = stringRepresentation.split(".");
        if(splitString.length < 0){
            return scale == 0;
        }
        else{
            String rightOfDecimal = splitString[1];
            return rightOfDecimal.length() == scale;
        }
    }

}
