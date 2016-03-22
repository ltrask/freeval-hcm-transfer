/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major.tableHelper;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.io.Serializable;

/**
 *
 * @author jtrask
 */
public class SensorSet implements Serializable {

    private static final long serialVersionUID = 87151684651L;

    /**
     * Seed instance for the sensor locations
     */
    private Seed seed;

    /**
     * Number of sensors in the facility
     */
    //public int numSensors;
    /**
     * Array of Sensor Names
     */
    public String[] sensorNames;

    /**
     * Array of Sensor Locations
     */
    public boolean[][] sensorLocations;

    public SensorSet(Seed seed) {
        this.seed = seed;
        this.sensorNames = new String[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];
        this.sensorLocations = new boolean[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)][2];
        // Filling default values
        for (int i = 0; i < sensorNames.length; i++) {
            sensorNames[i] = "Sensor " + (i + 1);
            sensorLocations[i][0] = false;
            if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, i) == CEConst.SEG_TYPE_ONR
                    || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, i) == CEConst.SEG_TYPE_W) {
                sensorLocations[i][1] = true;
            }
        }
    }

    public SensorSet(Seed seed, String[] sensorNames, boolean[][] sensorLocations) {
        this.seed = seed;
        //this.numSensors = numSensors;
        this.sensorNames = sensorNames;
        this.sensorLocations = sensorLocations;
    }

    public void seedUpdated() {
        String[] oldNames = sensorNames.clone();
        boolean[][] oldLocations = sensorLocations.clone();
        this.sensorNames = new String[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];
        this.sensorLocations = new boolean[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)][2];
        // Filling default values
        for (int i = 0; i < sensorNames.length; i++) {
            if (i < oldNames.length) {
                sensorNames[i] = oldNames[i];
                sensorLocations[i][0] = oldLocations[i][0];
                sensorLocations[i][1] = oldLocations[i][1];
            } else {
                sensorNames[i] = "Sensor " + (i + 1);
                sensorLocations[i][0] = false;
                if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, i) == CEConst.SEG_TYPE_ONR
                        || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, i) == CEConst.SEG_TYPE_W) {
                    sensorLocations[i][1] = true;
                }
            }
        }
    }

    public String[] getSensorNameBySegmentArray() {
//        String[] tempSensorNameArr = new String[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];  // Need number of segments
//        Arrays.fill(tempSensorNameArr, "");
//        for (int i = 0; i < numSensors; i++) {
//            if (sensorLocations[i] >= 0) {
//                tempSensorNameArr[sensorLocations[i] - 1] = sensorNames[i];
//            }
//        }
//        return tempSensorNameArr;
        return sensorNames;
    }

    /**
     * Returns the number of sensors located at segments (does not include ONR
     * sensors).
     *
     * @return number of sensors
     */
    public int getNumSensors() {
        int numSensors = 0;
        for (int seg = 0; seg < sensorLocations.length; seg++) {
            if (sensorLocations[seg][0]) {
                numSensors++;
            }
        }
        return numSensors;
    }

    public boolean[][] getSensorAtSegmentArray() {
//        boolean[] tempArr = new boolean[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];
//        Arrays.fill(tempArr, false);
//        for (int i = 0; i < numSensors; i++) {
//            if (sensorLocations[i] >= 0) {
//                tempArr[sensorLocations[i] - 1] = true;
//            }
//        }
//        return tempArr;
        return sensorLocations;
    }

    public int convertSensorIdxToSegment(int sensorIdx) {
        int sensorCount = -1;
        for (int seg = 0; seg <= sensorLocations.length; seg++) {
            if (sensorLocations[seg][0]) {
                sensorCount++;
            }
            if (sensorCount == sensorIdx) {
                return seg;
            }
        }
        return 0;
    }
}
