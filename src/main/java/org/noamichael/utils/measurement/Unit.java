package org.noamichael.utils.measurement;

/**
 *
 * @author michael
 * @param <T>
 */
public interface Unit<T extends Enum> {
    
    public T getValue();

}
