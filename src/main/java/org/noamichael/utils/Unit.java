package org.noamichael.utils;

/**
 *
 * @author michael
 * @param <T>
 */
public interface Unit<T extends Enum> {

    public String getStringValue();
    
    public T getValue();

}
