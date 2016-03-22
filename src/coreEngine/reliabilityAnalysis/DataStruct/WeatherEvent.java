/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.util.Comparator;

/**
 *
 * @author jltrask
 */
public class WeatherEvent extends ScenarioEvent implements Comparable<WeatherEvent> {

    private static final long serialVersionUID = 156355516425L;

    /**
     * Constructor to create a weather event for use in FREEVAL's reliability
     * analysis.
     *
     * @param seed Seed on which the analysis is being conducted.
     * @param scenarioInfo ScenarioInfo object representing the scenario to
     * which the event is going to be applied.
     * @param severity Severity type of the weather event.
     * @param startPeriod Analysis period in which the weather event starts.
     * @param duration Duration of the weather event.
     */
    public WeatherEvent(Seed seed, ScenarioInfo scenarioInfo, int severity, int startPeriod, int duration) {
        super(seed, scenarioInfo, severity, startPeriod, duration);
    }

    /**
     * Checks to see if the event overlaps with the specified weather event. The
     * input event must be a weather event or else the method will automatically
     * return false.
     *
     * @param event Event with which to check overlap.
     * @return True if the events overlap, false otherwise.
     */
    @Override
    public boolean hasOverlap(ScenarioEvent event) {
        try {
            WeatherEvent wEvent = (WeatherEvent) event;
            if (this.startPeriod <= wEvent.startPeriod && this.startPeriod >= wEvent.duration) {
                return true;
            } else if (this.startPeriod >= wEvent.startPeriod && this.startPeriod <= wEvent.startPeriod + wEvent.duration) {
                return true;
            }
        } catch (ClassCastException e) {
            System.err.println("Comparing events of different types.");
            return false;
        }
        return false;
    }

    /**
     * Checks to see if the event overlaps with the specified weather event.
     *
     * @param wEvent Event with which to check overlap.
     * @return True if the events overlap, false otherwise.
     */
    public boolean hasOverlap(WeatherEvent wEvent) {
        if (this.startPeriod <= wEvent.startPeriod && this.startPeriod >= wEvent.duration) {
            return true;
        } else if (this.startPeriod >= wEvent.startPeriod && this.startPeriod <= wEvent.startPeriod + wEvent.duration) {
            return true;
        }
        return false;
    }

    /**
     * Getter for the string representation of the weather event severity type.
     * For printing and display purposes.
     *
     * @return String of the severity type.
     */
    public String getSeverityString() {
        String desc = "";
        switch (severity) {
            default:
            case 0:
                desc += "Medium Rain";
                break;
            case 1:
                desc += "Heavy Rain";
                break;
            case 2:
                desc += "Light Snow";
                break;
            case 3:
                desc += "Light Medium Snow";
                break;
            case 4:
                desc += "Medium Heavy Snow";
                break;
            case 5:
                desc += "Heavy Snow";
                break;
            case 6:
                desc += "Severe Cold";
                break;
            case 7:
                desc += "Low Visibility";
                break;
            case 8:
                desc += "Very Low Visibility";
                break;
            case 9:
                desc += "Minimum Visibility";
                break;
        }
        return desc;
    }

    @Override
    public String toString() {
        return getSeverityString();
    }

    @Override
    public float getEventCAF(int period, int segment) {
        return this.seed.getWeatherCAF()[severity];
    }

    /**
     * Getter for the weather event capacity adjustment factor.
     *
     * @return Weather event CAF
     */
    public float getEventCAF() {
        return this.getEventCAF(0, 0);
    }

    @Override
    public float getEventOAF(int period, int segment) {
        return this.seed.getWeatherDAF()[severity];
    }

    /**
     * Getter for the weather event origin demand adjustment factor.
     *
     * @return Weather event OAF
     */
    public float getEventOAF() {
        return this.getEventOAF(0, 0);
    }

    @Override
    public float getEventDAF(int period, int segment) {
        return this.seed.getWeatherDAF()[severity];
    }

    /**
     * Getter for the weather event destination demand adjustment factor.
     *
     * @return Weather event DAF
     */
    public float getEventDAF() {
        return this.getEventDAF(0, 0);
    }

    @Override
    public float getEventSAF(int period, int segment) {
        return this.seed.getWeatherSAF()[severity];
    }

    /**
     * Getter for the weather event free flow speed adjustment factor.
     *
     * @return Weather event SAF
     */
    public float getEventSAF() {
        return this.getEventSAF(0, 0);
    }

    @Override
    public int getEventLAF(int period, int segment) {
        return 0;
    }

    /**
     * Getter for the weather event lane adjustment factor.
     *
     * @return Weather event LAF
     */
    public float getEventLAF() {
        return this.getEventLAF(0, 0);
    }

    @Override
    public int compareTo(WeatherEvent o) {
        return WeatherEvent.Comparators.STARTPERIOD.compare(this, o);
    }

    /**
     * Always returns true.
     *
     * @deprecated
     * @return
     */
    public boolean getEventOccurs() {
        return true;
    }

    /**
     * Checks to see if the event is valid for the seed/scenario
     *
     * @return true if the event is valid, false if not valid
     */
    public boolean isValid() {
        return this.startPeriod < seed.getValueInt(CEConst.IDS_NUM_PERIOD);
    }

    /**
     * Helper comparator class.
     */
    public static class Comparators {

        public static Comparator<WeatherEvent> STARTPERIOD = (WeatherEvent o1, WeatherEvent o2) -> o1.startPeriod - o2.startPeriod;
    }

}
