package org.noamichael.utils.measurement;

/**
 *
 * @author michael
 */
public class UnitValue {

    private double value;
    private Unit<?> unit;

    public UnitValue(double value, Unit<?> unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the unit
     */
    public Unit<?> getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(Unit<?> unit) {
        this.unit = unit;
    }

}
