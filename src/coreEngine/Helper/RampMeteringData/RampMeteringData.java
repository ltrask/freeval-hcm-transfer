package coreEngine.Helper.RampMeteringData;

/**
 * Interface for Adaptive Ramp Metering Data
 *
 * @author Shu Liu
 */
public interface RampMeteringData {

    /**
     * Get number of items
     *
     * @return number of items
     */
    public int getItemCount();

    /**
     * Get item name at a given index
     *
     * @param index index of item
     * @return item name at a given index
     */
    public String getItemName(int index);

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @return item value at a given index
     */
    public double getItemValue(int index);

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @param value new item value
     */
    public void setItemValue(int index, double value);
}
