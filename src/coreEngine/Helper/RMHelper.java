package coreEngine.Helper;

import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Helper.CompressArray.CA2DString;
import coreEngine.Seed;
import java.io.Serializable;

/**
 * This class is used to store ramp metering data.
 *
 * @author Lake Trask
 */
public class RMHelper implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 92352654736L;

    /**
     * Ramp metering type
     */
    private final CA2DInt RMType;

    /**
     * Fixed ramp metering rate
     */
    private final CA2DInt RMRate;

    /**
     * ALINEA ramp metering scheme key
     */
    private final CA2DString RM_ALINEA_Key;

    /**
     * Fuzzy logic ramp metering scheme key
     */
    private final CA2DString RM_Fuzzy_Key;

    /**
     * Constructor
     *
     * @param seed seed instance
     */
    public RMHelper(Seed seed) {
        this(seed.getValueInt(CEConst.IDS_NUM_SEGMENT), seed.getValueInt(CEConst.IDS_NUM_PERIOD));

    }

    /**
     * Constructor
     *
     * @param numSegments number of segments
     * @param numPeriods number of periods
     */
    public RMHelper(int numSegments, int numPeriods) {
        RMType = new CA2DInt(numSegments, numPeriods, CEConst.IDS_RAMP_METERING_TYPE_NONE);
        RMRate = new CA2DInt(numSegments, numPeriods, 2100);
        RM_ALINEA_Key = new CA2DString(numSegments, numPeriods, "Default");
        RM_Fuzzy_Key = new CA2DString(numSegments, numPeriods, "Default");
    }

    /**
     * Get ramp metering type
     *
     * @return ramp metering type compressed array
     */
    public CA2DInt getRampMeteringType() {
        return RMType;
    }

    /**
     * Get fixed ramp metering rate
     *
     * @return fixed ramp metering rate compressed array
     */
    public CA2DInt getRampMeteringFixRate() {
        return RMRate;
    }

    /**
     * Get ramp metering ALINEA scheme key
     *
     * @return ramp metering ALINEA scheme key compressed array
     */
    public CA2DString getRampMeteringALINEAKey() {
        return RM_ALINEA_Key;
    }

    /**
     * Get ramp metering fuzzy logic scheme key
     *
     * @return ramp metering fuzzy logic scheme key compressed array
     */
    public CA2DString getRampMeteringFuzzyKey() {
        return RM_Fuzzy_Key;
    }

    /**
     * Set global ramp metering type
     *
     * @param rampMeteringType new ramp metering type
     */
    public void setGlobalRMType(int rampMeteringType) {
        RMType.set(rampMeteringType, 0, 0, RMType.getSizeX() - 1, RMType.getSizeY() - 1);
    }

    /**
     * Remove some periods
     *
     * @param startPeriod starting period
     * @param numPeriodToBeDeleted number of periods
     */
    public void removePeriod(int startPeriod, int numPeriodToBeDeleted) {
        getRampMeteringFixRate().removeColumn(startPeriod, startPeriod + numPeriodToBeDeleted - 1);
        getRampMeteringType().removeColumn(startPeriod, startPeriod + numPeriodToBeDeleted - 1);
        getRampMeteringALINEAKey().removeColumn(startPeriod, startPeriod + numPeriodToBeDeleted - 1);
        getRampMeteringFuzzyKey().removeColumn(startPeriod, startPeriod + numPeriodToBeDeleted - 1);
    }

    /**
     * Remove some segments
     *
     * @param startSegment starting segment
     * @param numSegmentToBeDeleted number of segments
     */
    public void removeSegment(int startSegment, int numSegmentToBeDeleted) {
        getRampMeteringFixRate().removeRow(startSegment, startSegment + numSegmentToBeDeleted - 1);
        getRampMeteringType().removeRow(startSegment, startSegment + numSegmentToBeDeleted - 1);
        getRampMeteringALINEAKey().removeRow(startSegment, startSegment + numSegmentToBeDeleted - 1);
        getRampMeteringFuzzyKey().removeRow(startSegment, startSegment + numSegmentToBeDeleted - 1);
    }

    /**
     * Add some periods
     *
     * @param startPeriod starting period
     * @param numPeriodToBeAdded number of periods
     */
    public void addPeriod(int startPeriod, int numPeriodToBeAdded) {
        getRampMeteringFixRate().addColumn(startPeriod, numPeriodToBeAdded, 2100);
        getRampMeteringType().addColumn(startPeriod, numPeriodToBeAdded, 0);
        getRampMeteringALINEAKey().addColumn(startPeriod, numPeriodToBeAdded, "Default");
        getRampMeteringFuzzyKey().addColumn(startPeriod, numPeriodToBeAdded, "Default");
    }

    /**
     * Add some segments
     *
     * @param startSegment starting segment
     * @param numSegmentToBeAdded number of segments
     */
    public void addSegment(int startSegment, int numSegmentToBeAdded) {
        getRampMeteringFixRate().addRow(startSegment, numSegmentToBeAdded, 2100);
        getRampMeteringType().addRow(startSegment, numSegmentToBeAdded, 0);
        getRampMeteringALINEAKey().addRow(startSegment, numSegmentToBeAdded, "Default");
        getRampMeteringFuzzyKey().addRow(startSegment, numSegmentToBeAdded, "Default");
    }
}
