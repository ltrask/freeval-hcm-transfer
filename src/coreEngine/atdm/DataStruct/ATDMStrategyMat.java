package coreEngine.atdm.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;

/**
 *
 * @author Lake Trask
 */
public class ATDMStrategyMat extends ATDMStrategy {

    /**
     * Matrix to hold the strategy information.
     */
    private CA2DInt strategyMatrix;

    /**
     * Number of segments of the facility.
     */
    private final int numSeg;

    /**
     * Number of the periods of the facility analysis.
     */
    private final int numPeriods;

    /**
     * Strategy Type (Ramp Metering vs Hard Shoulder running. Should use a
     * CEConst ATDM Identifier.
     */
    private final String strategyType;

    /**
     * Array to hold the capacity of the shoulder for hard shoulder running.
     * Based on the number of open lanes 1 - 5plus lanes.
     */
    private float shoulderCapacity[];

    /**
     * Capacity increase due to Ramp Metering
     */
    private float capacityIncreaseDueToRM;

    /**
     * Constructor to create a matrix based ATDM Strategy Object. This object
     * represents a category (or type) that can be Ramp Metering or Hard
     * Shoulder Running. For Demand, Weather, Incident, or Work Zone, an
     * ATDMStrategy object must be used.
     *
     * @param id Integer index of the strategy.
     * @param description String description of the strategy.
     * @param numSegments Number of segments of the facility.
     * @param numPeriods Number of periods of the facility analysis.
     * @param strategyType CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING or
     * CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING
     */
    public ATDMStrategyMat(int id, String description, int numSegments, int numPeriods, String strategyType) {
        super(id, description);
        this.strategyType = strategyType;
        this.numSeg = numSegments;
        this.numPeriods = numPeriods;
        this.shoulderCapacity = new float[5];
        this.capacityIncreaseDueToRM = 0.03f;
        switch (this.strategyType) {
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                this.strategyMatrix = new CA2DInt(numSeg, this.numPeriods, 2100);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                this.strategyMatrix = new CA2DInt(numSeg, this.numPeriods, 0);
                fillShoulderDefaults();
                break;
            default:
                throw new RuntimeException("Invalid Strategy Type");
        }

    }

    /**
     * Getter for the ATDM Strategy parameter matrix. For Ramp Metering
     * strategies this is a matrix (segment by period) of ramp metering rates.
     * For Hard Shoulder running, it indicates if a shouler is open (1) or
     * closed (0) for each segment (col) and time period (row).
     *
     * @return Matrix of strategy parameters.
     */
    public CA2DInt getStrategyMatrix() {
        return strategyMatrix;
    }

    /**
     * Setter for the ATDM Strategy Parameter Matrix. For Ramp Metering
     * strategies this is a matrix (segment by period) of ramp metering rates.
     * For Hard Shoulder running, it indicates if a shouler is open (1) or
     * closed (0) for each segment (col) and time period (row).
     *
     * @param newMatrix New Matrix of strategy parameters.
     */
    public void setStrategyMatrix(CA2DInt newMatrix) {
        this.strategyMatrix = newMatrix;
    }

    /**
     * Getter for the capacity of a shoulder for the strategy based on the
     * number of lanes of the segment.
     *
     * @param numLanes Number of lanes of the segment.
     * @return Float Shoulder Capacity.
     */
    public float getShoulderCapacity(int numLanes) {
        return shoulderCapacity[numLanes];
    }

    /**
     * Getter for the entire array of shoulder capacities for the strategy.
     *
     * @return 1D float array of shoulder capacities.
     */
    public float[] getShoulderCapacity() {
        return shoulderCapacity;
    }

    /**
     * Individual setter for capacities of the open shoulder.
     *
     * @param newValue new shoulder capacity.
     * @param numLanes number of lanes for the segment with the open shoulder.
     */
    public void setShoulderCapacity(float newValue, int numLanes) {
        shoulderCapacity[numLanes] = newValue;
    }

    /**
     * Setter for the array of shoulder capacities based on the number of lanes
     * of the segment for which the shoulder is open. Indexes: 0 - 1 lane, 1 - 2
     * Lane, 2 - 3 Lane, 3 - 4 Lane, 4 - 5+ lanes.
     *
     * @param newValues 1D float array of shoulder capacities.
     */
    public void setShoulderCapacity(float[] newValues) {
        shoulderCapacity = newValues;
    }

    /**
     * Use the default values for shoulder capacities.
     */
    private void fillShoulderDefaults() {
        shoulderCapacity[0] = 0.7f;
        shoulderCapacity[1] = 0.75f;
        shoulderCapacity[2] = 0.8f;
        shoulderCapacity[3] = 0.85f;
        shoulderCapacity[4] = 0.9f;

    }

    /**
     * Getter for the capacity increase due to Ramp Metering parameter.
     *
     * @return float Capacity increase.
     */
    public float getCapacityIncreaseDueToRM() {
        return capacityIncreaseDueToRM;
    }

    /**
     * Setter for the capacity increase due to Ramp Metering parameter.
     *
     * @param capacityIncreaseDueToRM float capacity increase.
     */
    public void setCapacityIncreaseDueToRM(float capacityIncreaseDueToRM) {
        this.capacityIncreaseDueToRM = capacityIncreaseDueToRM;
    }

}
