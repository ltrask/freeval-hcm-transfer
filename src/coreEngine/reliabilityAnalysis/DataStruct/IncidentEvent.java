/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author jltrask
 */
public class IncidentEvent extends ScenarioEvent implements Comparable<IncidentEvent> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 355345366L;
    /**
     * Identifier for General Purpose (GP) segment incidents.
     */
    private static final int TYPE_GP = CEConst.SEG_TYPE_GP;
    /**
     * Identifier for Managed Lane (ML) segment incidents.
     */
    private static final int TYPE_ML = CEConst.SEG_TYPE_ML;
    /**
     * Segment type of the incident (GP or ML).
     */
    public final int segmentType;
    /**
     * Segment in which the incident event occurs.
     */
    private int segment;
    /**
     * Array holding the number of open lanes in the segment in each period for
     * the duration of the incident event. Used to calculate adjustment factors.
     */
    private int[] scenarioLanesAvailable;

    /**
     * Constructor for Incident ScenarioEvents
     *
     * @param seed Seed instance associated with the Reliability analysis
     * containing the scenario event.
     * @param scenarioInfo ScenarioInfo instance to which the event is/will be
     * assigned.
     * @param severity Incident severity (0 - Shoulder closure, 1 - 1 Lane
     * closure, 2 - 2 lane closure, 3 - 3 Lane closure, 4 - 4+ Lane closure).
     * @param startPeriod Period in which the incident event begins.
     * @param duration Duration of the incident event.
     * @param segment Segment in which the incident event occurs.
     * @param segmentType Identifier for a GP or ML incident.
     */
    public IncidentEvent(Seed seed, ScenarioInfo scenarioInfo, int severity, int startPeriod, int duration, int segment, int segmentType) {
        super(seed, scenarioInfo, severity, startPeriod, duration);
        this.segment = segment;
        if (segmentType == TYPE_GP || segmentType == TYPE_ML) {
            this.segmentType = segmentType;
        } else {
            throw new InvalidSegmentTypeException();
        }

        updateNumLanes();
    }

    /**
     * Updates the number of lanes array to reflect any change in segment.
     */
    private void updateNumLanes() {
        switch (segmentType) {
            case TYPE_GP:
                scenarioLanesAvailable = scenarioInfo.getAvailableGPLanes(segment, startPeriod, duration);
                break;
            case TYPE_ML:
                scenarioLanesAvailable = scenarioInfo.getAvailableMLLanes(segment, startPeriod, duration);
                break;
        }
    }

    /**
     * Checks if the incident is valid for the scenario. An incident is valid
     * only if its severity (lane closure) is less than the number of open lanes
     * in the segment for each period in the duration of the incident.
     *
     * @return True if the incident is valid, False otherwise
     */
    public boolean isValidSeverity() {
        if (severity == 0) {
            // Check to see if there is already a work zone shoulder closure
            for (WorkZone wz : scenarioInfo.getWorkZoneEventList()) {
                for (int per = startPeriod; per < startPeriod + duration; per++) {
                    if (wz.getSeverity() == 0 && wz.isActiveIn(segment, per)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            for (int idx = 0; idx < scenarioLanesAvailable.length; idx++) {
                if (scenarioLanesAvailable[idx] <= this.severity) {
                    return false;
                }
            }
            return true;
        }
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
        if (this.segment >= seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
            return false;
        }
        return isValidSeverity();
    }

    /**
     * Checks if swapping the segments of two incidents (GP) is valid.
     *
     * @param scenarioInfos List of ScenarioInfos for the reliability analysis
     * the IncidentEvents are associated with.
     * @param inc1 First IncidentEvent (GP)
     * @param inc2 Second IncidentEvent (GP)
     * @return True if the segments can be swapped, false otherwise
     */
    public static boolean checkSegmentSwapGP(ArrayList<ScenarioInfo> scenarioInfos, IncidentEvent inc1, IncidentEvent inc2) {
        if (inc1.segmentType != TYPE_GP || inc2.segmentType != TYPE_GP) {
            return false;
        }

        IncidentEvent tempInc1 = new IncidentEvent(inc1.seed,
                inc1.scenarioInfo,
                inc1.severity,
                inc1.startPeriod,
                inc1.duration,
                inc2.getSegment(),
                inc1.segmentType);
        if (tempInc1.isValidSeverity() && !inc1.scenarioInfo.checkGPIncidentOverlap(inc1)) {
            // Create the second incident and check if it is valid
            IncidentEvent tempInc2 = new IncidentEvent(inc2.seed,
                    inc2.scenarioInfo,
                    inc2.severity,
                    inc2.startPeriod,
                    inc2.duration,
                    inc1.getSegment(),
                    inc2.segmentType);
            if (tempInc2.isValidSeverity()) {
                // Check if altered incident creates overlap.
                inc2.setSegment(inc1.getSegment());
                if (!inc2.scenarioInfo.checkGPIncidentOverlap()) {
                    // Resetting inc2's segment to its original
                    inc2.setSegment(tempInc1.getSegment());
                    return true;
                } else {
                    // Resetting inc2's segment to its original
                    inc2.setSegment(tempInc1.getSegment());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks if swapping the segments of two incidents (ML) is valid.
     *
     * @param scenarioInfos List of ScenarioInfos for the reliability analysis
     * the IncidentEvents are associated with.
     * @param inc1 First IncidentEvent (ML)
     * @param inc2 Second IncidentEvent (ML)
     * @return True if the segments can be swapped, false otherwise
     */
    public static boolean checkSegmentSwapML(ArrayList<ScenarioInfo> scenarioInfos, IncidentEvent inc1, IncidentEvent inc2) {
        if (inc1.segmentType != TYPE_ML || inc2.segmentType != TYPE_ML) {
            return false;
        }

        IncidentEvent tempInc1 = new IncidentEvent(inc1.seed,
                inc1.scenarioInfo,
                inc1.severity,
                inc1.startPeriod,
                inc1.duration,
                inc2.getSegment(),
                inc1.segmentType);
        if (tempInc1.isValidSeverity() && !inc1.scenarioInfo.checkMLIncidentOverlap(inc1)) {
            // Create the second incident and check if it is valid
            IncidentEvent tempInc2 = new IncidentEvent(inc2.seed,
                    inc2.scenarioInfo,
                    inc2.severity,
                    inc2.startPeriod,
                    inc2.duration,
                    inc1.getSegment(),
                    inc2.segmentType);
            if (tempInc2.isValidSeverity()) {
                // Check if altered incident creates overlap.
                inc2.setSegment(inc1.getSegment());
                if (!inc2.scenarioInfo.checkMLIncidentOverlap()) {
                    // Resetting inc2's segment to its original
                    inc2.setSegment(tempInc1.getSegment());
                    return true;
                } else {
                    // Resetting inc2's segment to its original
                    inc2.setSegment(tempInc1.getSegment());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if the event overlaps with the specified incident event.
     * The input event must be a incident event or else the method will
     * automatically return false.
     *
     * @param event Event with which to check overlap.
     * @return True if the events overlap, false otherwise.
     */
    @Override
    public boolean hasOverlap(ScenarioEvent event) {
        try {
            IncidentEvent incEvent = (IncidentEvent) event;
            return hasOverlap(incEvent);
        } catch (ClassCastException e) {
            System.err.println("Comparing events of different types.");
            return false;
        }
    }

    /**
     * Checks to see if the event overlaps with the specified incident event.
     *
     * @param incEvent Event with which to check overlap.
     * @return True if the events overlap, false otherwise.
     */
    public boolean hasOverlap(IncidentEvent incEvent) {
        if (incEvent.getSegmentType() == this.segmentType) {
            if (this.segment == incEvent.getSegment()) {
                // Check if neither event wraps past end of study period
                if (this.startPeriod <= incEvent.startPeriod && this.startPeriod + this.duration >= incEvent.startPeriod) {
                    return true;
                } else if (this.startPeriod >= incEvent.startPeriod && this.startPeriod <= incEvent.startPeriod + incEvent.duration) {
                    return true;
                } else if (this.startPeriod > this.getEndPeriod() && this.getEndPeriod() >= incEvent.startPeriod) {
                    return true;
                }
                if (incEvent.startPeriod > incEvent.getEndPeriod() && incEvent.getEndPeriod() >= this.startPeriod) {
                    return true;
                }
            } else {
                // The events cannot overlap if they are not in the same segment
                return false;
            }
        }
        return false;
    }

    @Override
    public float getEventCAF(int period, int segment) {
        int adjPeriod = mapPeriod(period);
        int adjLanes;
        switch (segmentType) {
            default:
            case TYPE_GP:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 2, 0);
                return this.seed.getGPIncidentCAF()[severity][adjLanes];
            case TYPE_ML:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 1, 0);
                return this.seed.getMLIncidentCAF()[severity][adjLanes];
        }
    }

    @Override
    public float getEventOAF(int period, int segment) {
        int adjPeriod = mapPeriod(period);
        int adjLanes;
        switch (segmentType) {
            default:
            case TYPE_GP:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 2, 0);
                return this.seed.getGPIncidentDAF()[severity][adjLanes];
            case TYPE_ML:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 1, 0);
                return this.seed.getMLIncidentDAF()[severity][adjLanes];
        }
    }

    @Override
    public float getEventDAF(int period, int segment) {
        int adjPeriod = mapPeriod(period);
        int adjLanes;
        switch (segmentType) {
            default:
            case TYPE_GP:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 2, 0);
                return this.seed.getGPIncidentDAF()[severity][adjLanes];
            case TYPE_ML:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 1, 0);
                return this.seed.getMLIncidentDAF()[severity][adjLanes];
        }
    }

    @Override
    public float getEventSAF(int period, int segment) {
        int adjPeriod = mapPeriod(period);
        int adjLanes;
        switch (segmentType) {
            default:
            case TYPE_GP:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 2, 0);
                return this.seed.getGPIncidentSAF()[severity][adjLanes];
            case TYPE_ML:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 1, 0);
                return this.seed.getMLIncidentSAF()[severity][adjLanes];
        }
    }

    @Override
    public int getEventLAF(int period, int segment) {
        int adjPeriod = mapPeriod(period);
        int adjLanes;
        switch (segmentType) {
            default:
            case TYPE_GP:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 2, 0);
                return this.seed.getGPIncidentLAF()[severity][adjLanes];
            case TYPE_ML:
                adjLanes = Math.max(scenarioLanesAvailable[adjPeriod] - 1, 0);
                return this.seed.getMLIncidentLAF()[severity][adjLanes];
        }
    }

    @Override
    public int compareTo(IncidentEvent o) {
        return Comparators.STARTPERIOD.compare(this, o);
    }

    /**
     * Getter for the segment index in which the incident occurs (starts from
     * 0).
     *
     * @return Segment Index (Starting from 0).
     */
    public int getSegment() {
        return segment;
    }

    /**
     * Setter for the segment index in which the incident occurs (starts from
     * 0).
     *
     * @param newSegment new segment index (starting from 0).
     */
    public void setSegment(int newSegment) {
        this.segment = newSegment;
        updateNumLanes();
    }

    /**
     * Getter for the Segment type of the incident (GP or ML).
     *
     * @return Segment type of the incident (GP or ML).
     */
    public int getSegmentType() {
        return segmentType;
    }

    /**
     * Used to map the analysis period to the number of periods the incident has
     * been occurring.
     *
     * @param period Seed analysis period index
     * @return Incident analysis period index
     */
    private int mapPeriod(int period) {
        if (period >= startPeriod) {
            return period - startPeriod;
        } else {
            return duration - (getEndPeriod() - period) - 1;
        }
    }

    @Override
    public String toString() {
        String desc = "";
        switch (severity) {
            case 0:
                desc += "Shoulder Closure";
                break;
            case 4:
                desc += "4+ Lane Closure";
                break;
            default:
                desc += severity + " Lane Closure";
                break;
        }
        desc += " at segment " + (segment + 1);
        return desc;
    }

    /**
     * Getter for the string representation of the severity of the index. Mostly
     * used for display and printing purposes.
     *
     * @return
     */
    public String getSeverityString() {
        switch (severity) {
            case 0:
                return "Shoulder Closure";
            case 4:
                return "4+ Lane Closure";
            default:
                return severity + " Lane Closure";
        }
    }

    /**
     * Helper class to compare whether or not an incident precedes another.
     */
    public static class Comparators {

        public static Comparator<IncidentEvent> SEGMENT = (IncidentEvent o1, IncidentEvent o2) -> o1.getSegment() - o2.getSegment();

        public static Comparator<IncidentEvent> STARTPERIOD = (IncidentEvent o1, IncidentEvent o2) -> o1.startPeriod - o2.startPeriod;
    }

    /**
     * Helper Exception Class
     */
    private class InvalidSegmentTypeException extends RuntimeException {

        public InvalidSegmentTypeException() {
            super("Please specify valid segment type identifier (TYPE_GP or TYPE_ML)");
        }
    }
}
