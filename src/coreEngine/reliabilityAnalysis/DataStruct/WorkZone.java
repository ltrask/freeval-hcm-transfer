package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CEDate;
import coreEngine.Helper.CEHelper;
import coreEngine.Seed;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Lake Trask
 */
public class WorkZone implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8234793115000L;

    /**
     * Seed instance to which the work zone is associated.
     */
    protected final Seed seed;

    /**
     * Severity of the work zone.
     */
    private final int severity;

    /**
     * Analysis period in which the work zone starts.
     */
    protected final int startPeriod;
    /**
     * Duration (in analysis periods) of the work zone on a daily basis.
     */
    protected final int duration;

    /**
     * Start date of the work zone in the Reliability Reporting Period.
     */
    private final CEDate startDate;

    /**
     * End date of the work zone in the Reliability Reporting Period.
     */
    private final CEDate endDate;

    /**
     * Starting segment of the Work Zone (Segment indexing starts at 0).
     */
    private final int startSegment;

    /**
     * Ending segment of the Work Zone (Segment indexing starts at 0).
     */
    private final int endSegment;

    /**
     * Work zone barrier type
     */
    private int fBr;

    /**
     * Work Zone
     */
    private int fA;

    /**
     * Work zone lateral distance. Distance from the edge of the travel lane
     * adjacent to the work zone to the barrier, barricades, or cones; 0 to 12
     * feet.
     */
    private float fLAT;

    /**
     * Identifier for daylight or night.
     */
    private int fDN = 0;

    /**
     * Work zone speed limit.
     */
    private float fS;

    /**
     * Number of on-ramps and off-ramps within three miles upstream and
     * downstream of the work zone area.
     */
    private float fNr;

    /**
     * The percentage drop in pre-breakdown capacity at the work zone due to
     * queuing conditions (%). The recommended default value for instances when
     * an analyst lacks data is 13.4%.
     */
    public float alphaWZ = 13.4f;

    /**
     * Array to hold the Capacity Adjustment Factors for the work zone.
     */
    private float[] wzCAF;

    /**
     * Array to hold the Demand Adjustment Factors for the work zone.
     */
    private float[] wzDAF;

    /**
     * Array to hold the Speed Adjustment Factors for the work zone.
     */
    private float[] wzSAF;

    /**
     * Identifier for concrete barriers.
     */
    public static final int BARRIER_TYPE_CONCRETE = 0;

    /**
     * Identifier for cone, Type II barricade, or plastic drums barriers
     */
    public static final int BARRIER_TYPE_OTHER = 1;

    /**
     * Identifier for urban area type for the work zone.
     */
    public static final int AREA_TYPE_URBAN = 0;

    /**
     * Identifier for rural area type for the work zone.
     */
    public static final int AREA_TYPE_RURAL = 1;

    /**
     * Constructor to create a work zone event to be assigned to a reliability
     * analysis ScenarioInfo object.
     *
     * @param seed Associated seed file.
     * @param severity Severity of the work zone.
     * @param startPeriod Analysis Period in which the work zone starts.
     * @param duration Duration of the work zone event.
     * @param dates Dates the work zone is active (start and end dates).
     * @param segments Array containing the start segment (at first index) and
     * end segment (second index) of the work zone event.
     */
    public WorkZone(Seed seed, int severity, int startPeriod, int duration, CEDate[] dates, int[] segments) {
        this.seed = seed;
        this.severity = severity;
        this.startPeriod = startPeriod;
        this.duration = duration;
        this.startDate = dates[0];
        this.endDate = dates[1];
        this.startSegment = segments[0];
        this.endSegment = segments[1];

        initializeWorkZone();
    }

    /**
     * Constructor to create a work zone event to be assigned to a reliability
     * analysis ScenarioInfo object.
     *
     * @param seed Associated seed file.
     * @param severity Severity of the work zone.
     * @param startPeriod Analysis Period in which the work zone starts.
     * @param duration Duration of the work zone event.
     * @param dates Dates the work zone is active (start and end dates).
     * @param segments Array containing the start segment (at first index) and
     * end segment (second index) of the work zone event. The end segment can be
     * the same as the start segment if the work zone only spans a single
     * segment.
     */
    public WorkZone(Seed seed, int severity, int startPeriod, int duration, CEDate[] dates, Integer[] segments) {
        this.seed = seed;
        this.severity = severity;
        this.startPeriod = startPeriod;
        this.duration = duration;
        this.startDate = dates[0];
        this.endDate = dates[1];
        this.startSegment = segments[0];
        this.endSegment = segments[1];

        initializeWorkZone();
    }

    /**
     * Constructor to create a work zone event to be assigned to a reliability
     * analysis ScenarioInfo object.
     *
     * @param seed Associated seed file.
     * @param severity Severity of the work zone.
     * @param startPeriod Analysis Period in which the work zone starts.
     * @param duration Duration of the work zone event.
     * @param startDate Start date of the work zone.
     * @param endDate End date of the work zone.
     * @param startSegment Segment in which the work zone starts.
     * @param endSegment Final segment of the work zone (can be the same as the
     * start segment if the work zone only spans a single segment).
     */
    public WorkZone(Seed seed, int severity, int startPeriod, int duration, CEDate startDate, CEDate endDate, int startSegment, int endSegment) {
        this.seed = seed;
        this.severity = severity;
        this.startPeriod = startPeriod;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startSegment = startSegment;
        this.endSegment = endSegment;

        initializeWorkZone();
    }

    /**
     * Initalizes the work zone event.
     */
    private void initializeWorkZone() {

        // Calculating the number of ramps within three miles of the wok zone.
        this.fNr = calcfNr();

        // Creating and filling adjustment factor arrays
        this.wzCAF = new float[endSegment - startSegment + 1];
        Arrays.fill(wzCAF, 1.0f);
        this.wzDAF = new float[endSegment - startSegment + 1];
        Arrays.fill(wzDAF, 1.0f);
        this.wzSAF = new float[endSegment - startSegment + 1];
        Arrays.fill(wzSAF, 1.0f);

        this.fBr = 0;
        this.fA = 0;
        this.fS = 55.0f;
        this.fLAT = 1.0f;

        updateAdjustmentFactors();

    }

    @Override
    public String toString() {
        String severityString = "";
        switch (severity) {
            case 0:
                severityString = "Shoulder Closure";
                break;
            case 1:
                severityString = "1 Lane Closure";
                break;
            case 2:
                severityString = "2 Lane Closure";
                break;
            case 3:
                severityString = "3+ Lane Closure";
                break;
        }
        return severityString + ": " + startDate.toWorkZoneString() + " - " + endDate.toWorkZoneString()
                + "  (Seg. " + (startSegment + 1) + " - " + (endSegment + 1) + ")"
                + "  (Per. " + (startPeriod + 1) + " - " + (getEndPeriod() + 1) + ")";
    }

    /**
     * Gets the string describing the work zone without giving the work zone
     * dates.
     *
     * @return String Work Zone Description
     */
    public String getNoDateString() {
        String severityString = "";
        switch (severity) {
            case 0:
                severityString = "Shoulder Closure";
                break;
            case 1:
                severityString = "1 Lane Closure";
                break;
            case 2:
                severityString = "2 Lane Closure";
                break;
            case 3:
                severityString = "3+ Lane Closure";
                break;
        }
        return severityString + ": "
                + "  (Seg. " + (startSegment + 1) + " - " + (endSegment + 1) + ")"
                + "  (Per. " + (startPeriod + 1) + " - " + (getEndPeriod() + 1) + ")";
    }

    /**
     * Checks if the work zone is active in a specified segment and period. Date
     * of the work zone not considered.
     *
     * @param segment Segment to check if within the work zone.
     * @param period Period to check if during work zone periods.
     * @return True if the work zone is active in the segment and period, false
     * otherwise.
     */
    public boolean isActiveIn(int segment, int period) {
        if (segment >= startSegment && segment <= endSegment) {
            // Note work zones do not wrap
            return (period >= startPeriod && period <= getEndPeriod());
        } else {
            return false;
        }
    }

    /**
     * Checks if the work zone is active on the facility for a specific period.
     * Date of the work zone not considered.
     *
     * @param period Period to check if during work zone periods.
     * @return True if the work zone is active in the segment and period, false
     * otherwise.
     */
    public boolean isActiveIn(int period) {
        // Note work zones do not wrap
        return (period >= startPeriod && period <= getEndPeriod());
    }

    /**
     * Checks if the work zone overlaps another work zone.
     *
     * @param wz WorkZoneEvent object to check overlap with.
     * @return True if the work zones overlap, false otherwise.
     */
    public boolean hasOverlap(WorkZone wz) {
        boolean hasOverlap = false;
        boolean hasDateOverlap = false;
        boolean hasDateAndSegmentOverlap = false;
        if (this.startDate.isBeforeOrSameAs(wz.startDate) && this.endDate.isAfterOrSameAs(wz.startDate)) {
            hasDateOverlap = true;
        } else if (this.startDate.isAfter(wz.startDate) && this.startDate.isBeforeOrSameAs(wz.endDate)) {
            hasDateOverlap = true;
        }

        // If dates overlap, check if segments overlap
        if (hasDateOverlap) {
            if (this.startSegment <= wz.startSegment && this.endSegment >= wz.startSegment) {
                hasDateAndSegmentOverlap = true;
            } else if (this.startSegment > wz.startSegment && this.startSegment <= wz.endSegment) {
                hasDateAndSegmentOverlap = true;
            }
        }

        // If necessary, check period overlap
        if (hasDateAndSegmentOverlap) {
            if (this.startPeriod <= wz.startPeriod && this.getEndPeriod() >= wz.startPeriod) {
                hasOverlap = true;
            } else if (this.startPeriod > wz.startPeriod && this.startPeriod <= wz.getEndPeriod()) {
                hasOverlap = true;
            }
        }

        return hasOverlap;
    }

    /**
     * Checks if the work zone is feasible over the work zone segments. Checks
     * if the lane closures are allowable in all segments.
     *
     * @return True if the work zone is feasible, false otherwise.
     */
    public boolean checkFeasibleLaneClosure() {
        boolean feasible = true;

        for (int seg = startSegment; seg <= endSegment; seg++) {
            if (seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN) <= severity) {
                feasible = false;
                break;
            }
        }

        return feasible;
    }

    /**
     * Checks to see if the event is valid for the seed/scenario in both the
     * spatial and temporal sense.
     *
     * @return true if the event is valid, false if not valid
     */
    public boolean isValid() {
        if (this.startPeriod >= seed.getValueInt(CEConst.IDS_NUM_PERIOD)) {
            return false;
        }
        if (this.startSegment >= seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
            return false;
        }
        return checkFeasibleLaneClosure();
    }

    /**
     * Getter for the work zone event capacity adjustment factor for the
     * specified segment.
     *
     * @param segment Segment index of the facility facility in which the work
     * zone is active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public float getEventCAF(int segment) {
        return wzCAF[segmentMapper(segment)];
    }

    /**
     * Getter for the work zone event origin demand adjustment factor for the
     * specified segment.
     *
     * @param segment Segment index of the facility facility in which the work
     * zone is active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public float getEventOAF(int segment) {
        return wzDAF[segmentMapper(segment)];
    }

    /**
     * Getter for the work zone event destination demand adjustment factor for
     * the specified segment.
     *
     * @param segment Segment index of the facility facility in which the work
     * zone is active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public float getEventDAF(int segment) {
        return wzDAF[segmentMapper(segment)];
    }

    /**
     * Getter for the work zone event free flow speed adjustment factor for the
     * specified segment.
     *
     * @param segment Segment index of the facility in which the work zone is
     * active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public float getEventSAF(int segment) {
        return wzSAF[segmentMapper(segment)];
    }

    /**
     * Returns Lane Adjustment factor (always less than or equal to 0) for the
     * work zone in the specified period and segment.
     *
     * @param segment Segment index of the facility in which the work zone is
     * active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public int getEventLAF(int segment) {
        return -1 * severity;
    }

    /**
     * Getter for the adjustment factor of a specific type for a work zone in a
     * specified segment in which it is active.
     *
     * @param adjFactorType 0 - CAF, 1 - OAF/DAF, 2 - SAF, 3 - LAF.
     * @param segment Segment of the facility in which the work zone is active.
     * @return Adjustment factor of the work zone in the segment.
     */
    public float getAdjFactor(int adjFactorType, int segment) {
        switch (adjFactorType) {
            default:
            case 0:
                return getEventCAF(segmentMapper(segment));
            case 1:
                return getEventDAF(segmentMapper(segment));
            case 2:
                return getEventSAF(segmentMapper(segment));
            case 3:
                return getEventLAF(segmentMapper(segment));
        }
    }

    /**
     * Setter for the capacity adjustment factor of the work zone for a
     * specified segment of the facility.
     *
     * @param val new adjustment factor value.
     * @param segment Segment index of the facility.
     */
    public void setEventCAF(float val, int segment) {
        wzCAF[segmentMapper(segment)] = val;
    }

    /**
     * Setter for the origin and destination demand adjustment factor of the
     * work zone for a specified segment of the facility.
     *
     * @param val new adjustment factor value.
     * @param segment Segment index of the facility.
     */
    public void setEventDAF(float val, int segment) {
        wzDAF[segmentMapper(segment)] = val;
    }

    /**
     * Setter for the free flow speed adjustment factor of the work zone for a
     * specified segment of the facility.
     *
     * @param val new adjustment factor value.
     * @param segment Segment index of the facility.
     */
    public void setEventSAF(float val, int segment) {
        wzSAF[segmentMapper(segment)] = val;
    }

    /**
     * Setter for the work zone barrier type.
     *
     * @param barrierType 0 - Concrete, 1 - Plastic Drum/Type II/Other
     */
    public void setBarrierType(int barrierType) {
        switch (barrierType) {
            case WorkZone.BARRIER_TYPE_CONCRETE:
            case WorkZone.BARRIER_TYPE_OTHER:
                this.fBr = barrierType;
                break;
            default:
                throw new RuntimeException("Invalid Barrier Type Specification");
        }

    }

    /**
     * Setter for the work zone area type.
     *
     * @param areaType 0 - Urban, 1 - Rural.
     */
    public void setAreaType(int areaType) {
        switch (areaType) {
            case WorkZone.AREA_TYPE_RURAL:
            case WorkZone.AREA_TYPE_URBAN:
                this.fA = areaType;
                break;
            default:
                throw new RuntimeException("Invalid Area Type Specification");
        }
    }

    /**
     * Setter for the work zone lateral distance.
     *
     * @param lateralDistance float lateral distance between the driving lane
     * and work zone barrier in feet.
     */
    public void setLateralDistance(float lateralDistance) {
        this.fLAT = lateralDistance;
    }

    /**
     * Setter for the work zone speed limit.
     *
     * @param speed work zone speed limit in mph.
     */
    public void setWorkZoneSpeedLimit(float speed) {
        this.fS = speed;
    }

    /**
     * Method to calculate the lane close severity index (LCSI) as specified by
     * the 3-107 Methodology included in the HCM 6th Edition.
     *
     * @param segment Segment index to check LCSI.
     * @return LCSI at the segment.
     */
    private float calcLCSI(int segment) {
        float numLanes = (float) seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, segment);
        return 1.0f / (((numLanes - severity) / numLanes) * (numLanes - severity));
    }

    /**
     * Method to calculate the queue discharge rate of the work zone at the
     * segment. QDR is calculated according to the 3-107 methodology included in
     * the HCM 6th Edition.
     *
     * @param segment Segment index to check the Work Zone QDR.
     * @return Work Zone QDR at the segment.
     */
    private float calcQDR(int segment) {
        return 2093.0f - (154 * calcLCSI(segment)) - (194 * fBr) - (179 * fA) + (9 * fLAT) - (59 * fDN);
    }

    /**
     * Calculate the number of ramps of the facility.
     *
     * @return Number of ramps.
     */
    private float calcfNr() {
        int numRamp = 0;

        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            switch (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg)) {
                case CEConst.SEG_TYPE_ONR:
                case CEConst.SEG_TYPE_OFR:
                    numRamp += 1;
                    break;
                case CEConst.SEG_TYPE_W:
                    numRamp += 2;
                    break;
            }
        }

        float totalRampDensity = numRamp / seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI);
        return totalRampDensity;
        //float workZoneLength = 0.0f;
        //for (int seg = this.startSegment; seg <= this.endSegment; seg++) {
        //    workZoneLength += seed.getValueFloat(CEConst.IDS_SEGMENT_LENGTH_MI, seg);
        //}
        //return (workZoneLength + 6.0f) * totalRampDensity / workZoneLength;
    }

    /**
     * Calculates the work zone capacity (or pre-breakdown flow rate) in pc/h/l
     *
     * @param segment Segment index of the facility
     * @return Work Zone Capacity at the segment.
     */
    private float calcWZCapacity(int segment) {
        return 100.0f * calcQDR(segment) / (100.0f - alphaWZ);
    }

    /**
     * Method to calculate the speed ratio of the work zone at the specified
     * facility segment during the given analysis period.
     *
     * @param segment Facility segment index.
     * @param period Analysis period index.
     * @return Speed ratio
     */
    public float calcfSr(int segment, int period) {
        return this.fS / seed.getValueFloat(CEConst.IDS_MAIN_FREE_FLOW_SPEED, segment, period);
    }

    /**
     * Method to compute the work zone free flow speed according to the 3-107
     * methodology for the specified facility segment and analysis period. The
     * 3-107 methodology is included in the HCM 6th Edition.
     *
     * @param segment Facility Segment Index.
     * @param period Analysis period index.
     * @return Computed work zone free flow speed.
     */
    private float calcFFSwz(int segment, int period) {
        //System.out.println("seg,per=" + segment + "," + period);
        //System.out.println("fsr=" + calcfSr(segment, period));
        //System.out.println("LCSI=" + calcLCSI(segment));
        //System.out.println("fNr=" + fNr);
        return 9.95f + (33.49f * calcfSr(segment, period)) + (0.53f * fS) - (5.6f * calcLCSI(segment)) - (3.84f * fBr) - (1.71f * fDN) - (8.7f * fNr);
    }

    /**
     * Method to calculate the capacity adjustment factor for the work zone in
     * the specified segment. The capacity is determined based on the 3-107
     * methodology included in the HCM 6th edition.
     *
     * @param segment Facility Segment Index
     * @return Computed Capacity Adjustment Factor.
     */
    private float calculateCAF(int segment) {
        float c = seed.getValueInt(CEConst.IDS_MAIN_CAPACITY, segment, 0) / seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, segment);
        c = c / seed.getValueFloat(CEConst.IDS_GP_USER_CAF, segment, 0);
        float fHV = (float) (1.0
                / (1.0 + (seed.getValueFloat(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, segment, 0) + seed.getValueFloat(CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, segment, 0)) * (seed.getValueFloat(CEConst.IDS_TRUCK_CAR_ET) - 1.0) / 100.0));
        //System.out.println("FHV: " + String.valueOf(fHV));
        c = CEHelper.veh_to_pc(c, fHV);
        //System.out.println("Capacity: " + String.valueOf(c));
        return Math.min(1.0f, calcWZCapacity(segment) / c);
    }

    /**
     * Method to calculate the free flow speed adjustment factor for the work
     * zone in the specified segment. The free flow speed is determined based on
     * the 3-107 methodology included in the HCM 6th edition.
     *
     * @param segment Facility Segment Index
     * @return Computed speed Adjustment Factor.
     */
    private float calculateSAF(int segment) {
        float candSAF = calcFFSwz(segment, 0) / seed.getValueFloat(CEConst.IDS_MAIN_FREE_FLOW_SPEED, segment, 0);
        return Math.min(candSAF, 1.0f);
    }

    /**
     * Update the computed adjustment factors of the work zone.
     */
    public void updateAdjustmentFactors() {
        for (int seg = startSegment; seg <= endSegment; seg++) {
            wzCAF[segmentMapper(seg)] = calculateCAF(seg);
            wzSAF[segmentMapper(seg)] = calculateSAF(seg);
        }
    }

    /**
     * Maps the segment of the facility to the index of the segment in the work
     * zone.
     *
     * @param segment
     */
    private int segmentMapper(int segment) {
        return segment - startSegment;
    }

    // <editor-fold defaultstate="collapsed" desc="Date/Time/Location Getters">
    /**
     * Getter for the work zone start date.
     *
     * @return Work zone start date
     */
    public CEDate getStartDate() {
        return this.startDate;
    }

    /**
     * Getter for the work zone end date.
     *
     * @return Work Zone End Date
     */
    public CEDate getEndDate() {
        return this.endDate;
    }

    /**
     * Getter for the work zone start and end dates.
     *
     * @return CEDate array of work zone start and end dates.
     */
    public CEDate[] getDates() {
        return new CEDate[]{startDate, endDate};
    }

    /**
     * Getter for the segment in which the work zone starts.
     *
     * @return Integer facility segment index.
     */
    public int getStartSegment() {
        return this.startSegment;
    }

    /**
     * Getter for the segment in which the work zone ends.
     *
     * @return Integer facility segment index.
     */
    public int getEndSegment() {
        return this.endSegment;
    }

    /**
     * Getter for the number of segments in which the work zone is active.
     *
     * @return Integer number of segments the work zone spans.
     */
    public int getNumberOfSegments() {
        return endSegment - startSegment + 1;
    }

    /**
     * Getter for the start and end segment array.
     *
     * @return Integer array of start and end segments for the work zone.
     */
    public int[] getSegments() {
        return new int[]{startSegment, endSegment};
    }

    /**
     * Getter for the work zone start period.
     *
     * @return Integer analysis period index in which the work zone starts.
     */
    public int getStartPeriod() {
        return this.startPeriod;
    }

    /**
     * Returns the end period of the WorkZone. Note that the end period is
     * "inclusive" in that it is the last period in which the work zone is
     * active. If a work zone starts in period 4 and has a duration of 1, then
     * the end period is also 4.
     *
     * @return Last period in which the work zone is active.
     */
    public int getEndPeriod() {
        return startPeriod + duration - 1;
    }

    /**
     * Getter for the start and end period array for the work zone.
     *
     * @return Integer array giving the start and end period of the work zone.
     */
    public int[] getPeriods() {
        return new int[]{startPeriod, getEndPeriod()};
    }

    /**
     * Getter for the work zone severity.
     *
     * @return 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, etc.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Getter for the string representing the work zone severity. Mostly used
     * for display purposes.
     *
     * @return String representation of the work zone severity.
     */
    public String getSeverityString() {
        switch (this.severity) {
            case 0:
                return "Shoulder Closure";
            default:
                return this.severity + " Lane Closure";
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Static Getters">
    /**
     * Getter for the full string severity type of the work zone. Mostly for
     * display purposes.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, etc.
     * @return Full String Representation of the work zone severity.
     */
    public static String getTypeFull(int incType) {
        switch (incType) {
            case 0:
                return "Shoulder closure";
            case 1:
                return "One lane closure";
            case 2:
                return "Two lane closure";
            case 3:
                return "Three lane closure";
            default:
                return "Four or more lane closure";
        }
    }

    // </editor-fold>
}
