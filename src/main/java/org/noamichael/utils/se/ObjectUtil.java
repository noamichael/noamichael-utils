package org.noamichael.utils.se;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author michael
 */
public class ObjectUtil {

    /**
     * Returns a new instance of the object represented by the class by invoking
     * the no-arg constructor (if public). In the event that the constructor is
     * not available, returns null.
     *
     * @param <T> The object type.
     * @param clazz The class representation
     * @return The new object.
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (IllegalAccessException |
                IllegalArgumentException |
                InstantiationException |
                NoSuchMethodException |
                SecurityException |
                InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * A method to find all of the changed fields between two object.
     * @param oldObject The old Object
     * @param newObject The new Object
     * @return A List of {@link ChangedValue}
     * @throws RuntimeException
     * @throws NullPointerException
     */
    public static List<ChangedValue> getChangedValues(Object oldObject, Object newObject) {
        if (!oldObject.getClass().equals(newObject.getClass())) {
            return Collections.emptyList();
        }
        List<ChangedValue> changedValues = new ArrayList();
        Class<?> clazz = oldObject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);
                if (!oldValue.equals(newValue)) {
                    changedValues.add(new ChangedValue(fieldName, oldValue, newValue));
                }
            } catch (IllegalArgumentException |
                    IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return changedValues;
    }

    public static class ChangedValue {

        private final String fieldName;
        private final Object oldValue;
        private final Object newValue;

        public ChangedValue(String fieldName, Object oldValue, Object newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * @return the oldValue
         */
        public Object getOldValue() {
            return oldValue;
        }

        /**
         * @return the newValue
         */
        public Object getNewValue() {
            return newValue;
        }

        /**
         * @return the fieldName
         */
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String toString() {
            return "ChangedValue[" + "fieldName=" + fieldName + ", oldValue=" + oldValue + ", newValue=" + newValue + ']';
        }

    }
}
