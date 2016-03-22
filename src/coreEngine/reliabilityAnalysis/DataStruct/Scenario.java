package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CompressArray.CA3DFloat;
import coreEngine.Helper.CompressArray.CA3DInt;
import java.io.Serializable;

/**
 * This class contains information (adjustment factors) for one scenario.
 *
 * @author Shu Liu
 * @author Lake Trask
 */
public class Scenario implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 59009123790834L;

    /**
     * Array of Scenario Capacity Adjustment Factors.
     */
    private CA3DFloat CAF;

    /**
     * Array of Scenario Free Flow Speed Adjustment Factors.
     */
    private CA3DFloat SAF;

    /**
     * Array of Scenario Destination Demand Adjustment Factors.
     */
    private CA3DFloat DAF;

    /**
     * Array of Scenario Origin Demand Adjustment Factors.
     */
    private CA3DFloat OAF;

    /**
     * Array of Scenario Lane Adjustment Factors.
     */
    private CA3DInt LAFI;

    /**
     * Array of Scenario Work Zone Lane Adjustment Factors.
     */
    private CA3DInt LAFWZ;

    /**
     * Number of Scenarios in the RL Analysis.
     */
    private int numScen;

    /**
     * Creates a Scenario object to hold all adjustment factors for all
     * scenarios of a reliability analysis.
     *
     * @param numScen Number of Scenarios.
     * @param numSeg Number of segments of the facility.
     * @param numPeriod Number of periods of the facility analysis.
     */
    public Scenario(int numScen, int numSeg, int numPeriod) {
        this.numScen = numScen;
        CAF = new CA3DFloat(numScen, numSeg, numPeriod, 1);
        SAF = new CA3DFloat(numScen, numSeg, numPeriod, 1);
        DAF = new CA3DFloat(numScen, numSeg, numPeriod, 1);
        OAF = new CA3DFloat(numScen, numSeg, numPeriod, 1);
        LAFI = new CA3DInt(numScen, numSeg, numPeriod, 0);
        LAFWZ = new CA3DInt(numScen, numSeg, numPeriod, 0);
    }

    /**
     * Getter for the set of Capacity Adjustment Factors for all Scenarios of
     * the RL analysis.
     *
     * @return 3D Float Matrix (Scenario by Segment by Period) of Capacity
     * Adjustment Factors.
     */
    public CA3DFloat CAF() {
        return CAF;
    }

    /**
     * Getter for the set of Free Flow Speed Adjustment Factors for all
     * Scenarios of the RL analysis.
     *
     * @return 3D Float Matrix (Scenario by Segment by Period) of Free Flow
     * Speed Adjustment Factors.
     */
    public CA3DFloat SAF() {
        return SAF;
    }

    /**
     * Getter for the set of Destination Demand Adjustment Factors for all
     * Scenarios of the RL analysis.
     *
     * @return 3D Float Matrix (Scenario by Segment by Period) of Destination
     * Demand Adjustment Factors.
     */
    public CA3DFloat DAF() {
        return DAF;
    }

    /**
     * Getter for the set of Origin Demand Adjustment Factors for all Scenarios
     * of the RL analysis.
     *
     * @return 3D Float Matrix (Scenario by Segment by Period) of Origin Demand
     * Adjustment Factors.
     */
    public CA3DFloat OAF() {
        return OAF;
    }

    /**
     * Getter for the set of Lane Adjustment Factors for all Scenarios of the RL
     * analysis.
     *
     * @return 3D Integer Matrix (Scenario by Segment by Period) of Lane
     * Adjustment Factors.
     */
    public CA3DInt LAFI() {
        return LAFI;
    }

    /**
     * Getter for the set of Work Zone Lane Adjustment Factors for all Scenarios
     * of the RL analysis.
     *
     * @return 3D Int Matrix (Scenario by Segment by Period) of Work Zone Lane
     * Adjustment Factors.
     */
    public CA3DInt LAFWZ() {
        return LAFWZ;
    }

    /**
     * Getter for the size of the set of scenarios, i.e. the number of
     * scenarios.
     *
     * @return
     */
    public int size() {
        return numScen;
    }
}
