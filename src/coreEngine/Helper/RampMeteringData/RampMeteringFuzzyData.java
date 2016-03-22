package coreEngine.Helper.RampMeteringData;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Adaptive Ramp Metering Data for Fuzzy Logic
 *
 * @author Shu Liu
 */
public class RampMeteringFuzzyData implements RampMeteringData, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 457567834634349L;

    /**
     * Data map
     */
    public final TreeMap<String, Double> data = new TreeMap();

    /**
     * Constructor, initialize default values
     */
    public RampMeteringFuzzyData() {
        data.put("Downstream Sensor Location Minimum Offset", 1.0);
        data.put("Downstream Sensor Location Maximum Offset", 1.0);
        data.put("L_v", 20.0);
        data.put("L_d", 6.0);
        data.put("Output_VS_center_threshold", 12.125);
        data.put("Output_VS_upper_threshold", 14.75);
        data.put("Output_S_lower_threshold", 13.438);
        data.put("Output_S_upper_threshold", 16.063);
        data.put("Output_M_lower_threshold", 15.625);
        data.put("Output_M_upper_threshold", 17.375);
        data.put("Output_B_lower_threshold", 16.938);
        data.put("Output_B_upper_threshold", 19.563);
        data.put("Output_VB_lower_threshold", 18.25);
        data.put("Output_VB_center_threshold", 20.875);
        data.put("Up_OCC_VS_center_threshold", 8.0);
        data.put("Up_OCC_VS_upper_threshold", 11.0);
        data.put("Up_OCC_S_lower_threshold", 9.5);
        data.put("Up_OCC_S_upper_threshold", 12.5);
        data.put("Up_OCC_M_lower_threshold", 12.0);
        data.put("Up_OCC_M_upper_threshold", 14.0);
        data.put("Up_OCC_B_lower_threshold", 13.5);
        data.put("Up_OCC_B_upper_threshold", 16.5);
        data.put("Up_OCC_VB_lower_threshold", 15.0);
        data.put("Up_OCC_VB_center_threshold", 18.0);
        data.put("Down_OCC_VB_lower_threshold", 8.0);
        data.put("Down_OCC_VB_center_threshold", 18.0);
        data.put("Up_SPD_VS_center_threshold", 30.0);
        data.put("Up_SPD_VS_upper_threshold", 39.0);
        data.put("Up_SPD_S_lower_threshold", 34.5);
        data.put("Up_SPD_S_upper_threshold", 43.5);
        data.put("Up_SPD_M_lower_threshold", 42.0);
        data.put("Up_SPD_M_upper_threshold", 48.0);
        data.put("Up_SPD_B_lower_threshold", 46.5);
        data.put("Up_SPD_B_upper_threshold", 55.5);
        data.put("Up_SPD_VB_lower_threshold", 51.0);
        data.put("Up_SPD_VB_center_threshold", 60.0);
        data.put("Down_SPD_VS_center_threshold", 30.0);
        data.put("Down_SPD_VS_upper_threshold", 60.0);
        data.put("ONR_min_queue_length", 0.0);
        data.put("ONR_max_queue_length", 150.0);
        data.put("RW_ramp_queue", 2.0);
    }

    /**
     * Get number of items
     *
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Get item name at a given index
     *
     * @param index index of item
     * @return item name at a given index
     */
    @Override
    public String getItemName(int index) {
        return data.navigableKeySet().toArray()[index].toString();
    }

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @return item value at a given index
     */
    @Override
    public double getItemValue(int index) {
        return data.get(data.navigableKeySet().toArray()[index].toString());
    }

    /**
     * Get item value at a given index
     *
     * @param index index of item
     * @param value new item value
     */
    @Override
    public void setItemValue(int index, double value) {
        data.put(data.navigableKeySet().toArray()[index].toString(), value);
    }
}
