package coreEngine.atdm.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DFloat;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Helper.RMHelper;
import java.io.Serializable;

/**
 * This class contains information (adjustment factors) for one scenario.
 *
 * @author Shu Liu
 * @author Lake Trask
 */
public class ATDMScenario implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 15346363453532L;

    /**
     * Matrix to hold the Capacity Adjustment Factors of the ATDM Scenario.
     */
    private final CA2DFloat CAF;

    /**
     * Matrix to hold the Free Flow Speed Adjustment Factors of the ATDM
     * Scenario.
     */
    private final CA2DFloat SAF;

    /**
     * Matrix to hold the Destination Demand Adjustment Factors of the ATDM
     * Scenario.
     */
    private final CA2DFloat DAF;

    /**
     * Matrix to hold the Origin Demand Adjustment Factors of the ATDM Scenario.
     */
    private final CA2DFloat OAF;

    /**
     * Helper object for the Ramp Metering Rate of the ATDM Scenario.
     */
    private final RMHelper RM;

    /**
     * Matrix to hold the Lane Adjustment Factors of the ATDM Scenario.
     */
    private final CA2DInt LAF;

    /**
     * Matrix to hold the Ramp Metering Types of the ATDM Scenario.
     */
    private final CA2DInt atdmRampMeteringType;

    /**
     * ATDM Scenario Name.
     */
    private String name;

    /**
     * ATDM Scenario Description
     */
    private String discription;

    /**
     * ATDM Scenario Status (Input Only or Has Output).
     */
    private int status = CEConst.SCENARIO_INPUT_ONLY;

    /**
     * Constructor to create an ATDMScenario Object. This object holds the
     * adjustment factors and ramp metering information that will be applied to
     * a reliability (RL) scenario to simulate the effects of apply an ATDM
     * plan.
     *
     * @param numSeg Number of Segments of the facility.
     * @param numPeriod Number of periods of the analysis.
     */
    public ATDMScenario(int numSeg, int numPeriod) {
        CAF = new CA2DFloat(numSeg, numPeriod, 1);
        SAF = new CA2DFloat(numSeg, numPeriod, 1);
        DAF = new CA2DFloat(numSeg, numPeriod, 1);
        OAF = new CA2DFloat(numSeg, numPeriod, 1);
        //RM = new CA2DInt(numSeg, numPeriod, 2100);
        RM = new RMHelper(numSeg, numPeriod);
        LAF = new CA2DInt(numSeg, numPeriod, 0);
        atdmRampMeteringType = new CA2DInt(numSeg, numPeriod, CEConst.IDS_RAMP_METERING_TYPE_NONE);
    }

    /**
     * Getter for the matrix of Capacity Adjustment Factors.
     *
     * @return 2D matrix of float values.
     */
    public CA2DFloat CAF() {
        return CAF;
    }

    /**
     * Getter for the matrix of Free Flow Speed Adjustment Factors.
     *
     * @return 2D matrix of float values.
     */
    public CA2DFloat SAF() {
        return SAF;
    }

    /**
     * Getter for the matrix of Destination Demand Adjustment Factors.
     *
     * @return 2D matrix of float values.
     */
    public CA2DFloat DAF() {
        return DAF;
    }

    /**
     * Getter for the matrix of Origin Demand Adjustment Factors.
     *
     * @return 2D matrix of float values.
     */
    public CA2DFloat OAF() {
        return OAF;
    }

    /**
     * Getter for the Ramp Metering Helper Object
     *
     * @return Ramp Metering Helper.
     */
    public RMHelper RM() {
        return RM;
    }

    /**
     * Getter for the matrix of Lane Adjustment Factors.
     *
     * @return 2D matrix of int values.
     */
    public CA2DInt LAF() {
        return LAF;
    }

    /**
     * Getter for the ATDM Scenario Name.
     *
     * @return String name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the ATDM Scenario Name.
     *
     * @param name String new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the ATDM Scenario Description
     *
     * @return String description
     */
    public String getDiscription() {
        return discription;
    }

    /**
     * Setter for the ATDM Scenario Description
     *
     * @param discription String new Description.
     */
    public void setDiscription(String discription) {
        this.discription = discription;
    }

    /**
     * Getter for the ATDM Scenario Status.
     *
     * @return Input Only vs Has Output.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Setter for the ATDM Scenario status (Input Only vs Has Output).
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Getter for the ATDM Scenario's matrix of Ramp Metering types.
     *
     * @return 2D Integer matrix of ramp metering types.
     */
    public CA2DInt getRampMeteringType() {
        return atdmRampMeteringType;
    }
}
