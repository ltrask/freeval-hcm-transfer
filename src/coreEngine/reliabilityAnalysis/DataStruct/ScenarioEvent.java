/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.io.Serializable;

/**
 *
 * @author jltrask
 */
public abstract class ScenarioEvent implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 135156431265L;

    /**
     * Seed instance associated with the Reliability analysis containing the
     * scenario event.
     */
    public final Seed seed;
    /**
     * Index of the scenario the event is associated with. Indexing starts at 0.
     */
    public final ScenarioInfo scenarioInfo;
    /**
     * Period in which the event starts (First period is considered to be period
     * 0).
     */
    public int startPeriod;
    /**
     * Duration (in periods) of the event.
     */
    public int duration;
    /**
     * Severity of the event.
     */
    public int severity;

    /**
     * Constructor of Scenario Event
     *
     * @param seed Seed instance associated with the reliability analysis
     * containing the event.
     * @param scenarioInfo Scenario the event is associated with
     * @param severity Severity of the event
     * @param startPeriod Period in which the event starts.
     * @param duration Duration (in periods) of the event.
     */
    public ScenarioEvent(Seed seed, ScenarioInfo scenarioInfo, int severity, int startPeriod, int duration) {
        this.seed = seed;
        this.scenarioInfo = scenarioInfo;
        this.severity = severity;
        this.startPeriod = startPeriod;
        this.duration = duration;
    }

    /**
     * Returns the end period of the ScenarioEvent. Scenario Events can "wrap"
     * around the Analysis Period. Also note that the end period is "inclusive"
     * in that it is the last period in which the event is active. If an event
     * starts in period 4 and has a duration of 1, then the end period is also
     * 4.
     *
     * @return Last period in which the event is active.
     */
    public int getEndPeriod() {
        if (startPeriod + duration - 1 >= this.seed.getValueInt(CEConst.IDS_NUM_PERIOD)) {
            return (startPeriod + duration - 1) % this.seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        } else {
            return startPeriod + duration - 1;
        }
    }

    /**
     * Method to check if the scenario event startTime + duration exceeds causes
     * its endPeriod to fall beyond the final analysis period. If so, the event
     * is consider to have "periodWrapping".
     *
     * @return True if the event wraps past the number of periods, false
     * otherwise.
     */
    public boolean hasPeriodWrapping() {
        return startPeriod > getEndPeriod();
    }

    /**
     * Checks to see if the event is active in the input period.
     *
     * @param period Input period
     * @return True if the event is active, false otherwise.
     */
    public boolean checkActiveInPeriod(int period) {
        if (!hasPeriodWrapping()) {
            return (period >= startPeriod && period <= getEndPeriod());
        } else {
            if (period < startPeriod) {
                return (period <= getEndPeriod());
            } else {
                return true;
            }
        }
    }

    /**
     * Checks to see if the event overlaps with an event of the same type
     * (weather, incident, or work zone).
     *
     * @param event Event with which to check overlap.
     * @return True if the events overlap, false otherwise.
     */
    public abstract boolean hasOverlap(ScenarioEvent event);

    /**
     * Getter for the event Capacity Adjustment factor in a given period and
     * segment.
     *
     * @param period
     * @param segment
     * @return Event Adjustment Factor
     */
    public abstract float getEventCAF(int period, int segment);

    /**
     * Getter for the event Origin Demand Adjustment factor in a given period
     * and segment.
     *
     * @param period
     * @param segment
     * @return Event Adjustment Factor
     */
    public abstract float getEventOAF(int period, int segment);

    /**
     * Getter for the event Destination Demand Adjustment factor in a given
     * period and segment.
     *
     * @param period
     * @param segment
     * @return Event Adjustment Factor
     */
    public abstract float getEventDAF(int period, int segment);

    /**
     * Getter for the event Free Flow Speed Adjustment factor in a given period
     * and segment.
     *
     * @param period
     * @param segment
     * @return Event Adjustment Factor
     */
    public abstract float getEventSAF(int period, int segment);

    /**
     * Getter for the event Lane Adjustment factor in a given period and
     * segment.
     *
     * @param period
     * @param segment
     * @return Event Adjustment Factor
     */
    public abstract int getEventLAF(int period, int segment);

}
