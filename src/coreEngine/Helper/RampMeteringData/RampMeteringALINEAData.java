package coreEngine.Helper.RampMeteringData;

import java.io.Serializable;

/**
 * Adaptive Ramp Metering Data for ALINEA
 *
 * @author Shu Liu
 */
public class RampMeteringALINEAData implements RampMeteringData, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 27895675959029L;

    /**
     * Regulator Parameters (mi/step)
     */
    public double K_R = 40.0;
    /**
     * Target Density (pc/mi/ln)
     */
    public double targetDensity = 42.0;
    /**
     * Minimum Ramp Metering Rate (veh/h)
     */
    public double min = 240.0;
    /**
     * Maximum Ramp Metering Rate (veh/h)
     */
    public double max = 2100.0;

    /**
     * List of item names
     */
    private static final String[] ITEM_NAMES = {"Regulator Parameters (vph/step)", "Target Density (pc/mi/ln)",
        "Minimum Ramp Metering Rate (vph)", "Maximum Ramp Metering Rate (vph)"};

    /**
     * Get number of items
     *
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return ITEM_NAMES.length;
    }

    /**
     * Get item name at a given index
     *
     * @param index index of item
     * @return item name at a given index
     */
    @Override
    public String getItemName(int index) {
        return ITEM_NAMES[index];
    }

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @return item value at a given index
     */
    @Override
    public double getItemValue(int index) {
        switch (index) {
            case 0:
                return K_R;
            case 1:
                return targetDensity;
            case 2:
                return min;
            case 3:
                return max;
        }
        return -1;
    }

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @param value new item value
     */
    @Override
    public void setItemValue(int index, double value) {
        switch (index) {
            case 0:
                K_R = value;
                break;
            case 1:
                targetDensity = value;
                break;
            case 2:
                min = value;
                break;
            case 3:
                max = value;
                break;
        }
    }
}
