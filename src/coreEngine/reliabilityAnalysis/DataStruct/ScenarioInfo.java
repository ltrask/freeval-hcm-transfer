package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMDatabase;
import coreEngine.atdm.DataStruct.ATDMPlan;
import coreEngine.atdm.DataStruct.ATDMScenario;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains information for each Reliability Analysis scenario.
 * Should be 1-D array in Seed class only.
 *
 * @author Shu Liu
 * @author Lake Trask
 */
public class ScenarioInfo implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="Class and Instance Variables">
    private static final long serialVersionUID = 5839875102332L;

    /**
     * Seed instance from which the scenarios have been created.
     */
    private Seed seed;

    /**
     * Probability of a scenario
     */
    public float prob;

    /**
     * Group number of a scenario. Also the scenario index
     */
    public int group;

    /**
     * Name of the scenario.
     */
    public String name;

    /**
     * Detail containing event summary of a scenario.
     */
    private String detail;

    /**
     * Month of the scenario. (1 - Jan, 2 - Feb, etc.)
     */
    public int month;

    /**
     * Day of week represented by the scenario. (0 - Mon, 1 - Tue, 2 - Wed, 3 -
     * Thu, 4 - Fri, 5 - Sat, 6 - Sun)
     */
    public int day;

    /**
     * Input/Output status of a scenario.
     */
    public int statusRL = CEConst.SCENARIO_INPUT_ONLY;

    /**
     * Name of demand pattern. (i.e. Jan - Mon)
     */
    private String demandPatternName;

    /**
     * Demand Multiplier corresponding to the Scenario demand pattern.
     */
    private float demandMultiplier;

    /**
     * List of weather events
     */
    private final ArrayList<WeatherEvent> weatherEvents;

    /**
     * List of GP incident events
     */
    private final ArrayList<IncidentEvent> incidentEvents_GP;

    /**
     * List of GP incident events
     */
    private final ArrayList<IncidentEvent> incidentEvents_ML;

    /**
     * List of work zones.
     */
    private final ArrayList<WorkZone> workZones;

    // </editor-fold>
    /**
     * Constructor of ScenarioInfo class.
     */
    public ScenarioInfo() {
        this(0, 0, "");
    }

    /**
     * Constructor of ScenarioInfo class.
     *
     * @param prob Probability of a scenario
     */
    public ScenarioInfo(float prob) {
        this(prob, 0, "");
    }

    /**
     * Constructor of ScenarioInfo class.
     *
     * @param prob Probability of a scenario
     * @param group Group number of a scenario
     */
    public ScenarioInfo(float prob, int group) {
        this(prob, group, "");
    }

    /**
     * Constructor of ScenarioInfo class.
     *
     * @param prob Probability of a scenario
     * @param group Group number of a scenario
     * @param name Name of a scenario
     */
    public ScenarioInfo(float prob, int group, String name) {
        this.prob = prob;
        this.group = group;
        this.demandPatternName = name;

        weatherEvents = new ArrayList<>();
        incidentEvents_GP = new ArrayList<>();
        incidentEvents_ML = new ArrayList<>();
        workZones = new ArrayList<>();

        updateName();
    }

    //<editor-fold defaultstate="collapsed" desc="Generate Detail String">
    /**
     * Getter for the detail string representing the information about the
     * scenario.
     *
     * @return String Scenario Detail.
     */
    public String getDetail() {
        // Generating detail from all scenario events
        detail = " Seed Demand Multiplier: " + this.demandMultiplier + " \n";
        detail = detail + " Scenario Probability: " + String.format("%.2f", this.prob * 100.0f) + "% ";
        if (workZones.size() > 0) {
            detail = detail + "\n\n Work Zones:  ";
            for (WorkZone wz : workZones) {
                detail = detail + "\n " + wz.getSeverityString() + " at segment(s) "
                        + (wz.getStartSegment() + 1) + " - " + (wz.getEndSegment() + 1)
                        + " daily for periods " + (wz.getStartPeriod() + 1)
                        + " - " + (wz.getEndPeriod() + 1) + " ";
            }
        }

        if (weatherEvents.size() > 0) {
            detail = detail + "\n\n Weather Events:  ";
            for (WeatherEvent wEvent : weatherEvents) {
                if (wEvent.getEventOccurs()) {
                    if (wEvent.duration > 1) {
                        detail = detail + "\n " + WeatherData.getWeatherTypeFull(wEvent.severity) + ": Starting in period " + (wEvent.startPeriod + 1) + " for " + Math.min(wEvent.duration, seed.getValueInt(CEConst.IDS_NUM_PERIOD)) + " periods. ";
                    } else {
                        detail = detail + "\n " + WeatherData.getWeatherTypeFull(wEvent.severity) + ": Starting in period " + (wEvent.startPeriod + 1) + " for " + wEvent.duration + " period. ";
                    }
                }
            }
        }

        if (incidentEvents_GP.size() > 0) {
            detail = detail + "\n\n Incidents (GP):  ";
            for (IncidentEvent incEvent : incidentEvents_GP) {
                if (incEvent.duration > 1) {
                    detail = detail + "\n " + IncidentData.getIncidentTypeFull(incEvent.severity) + ": At segment " + (incEvent.getSegment() + 1) + " starting in period " + (incEvent.startPeriod + 1) + " for " + Math.min(incEvent.duration, seed.getValueInt(CEConst.IDS_NUM_PERIOD)) + " periods. ";
                } else {
                    detail = detail + "\n " + IncidentData.getIncidentTypeFull(incEvent.severity) + ": At segment " + (incEvent.getSegment() + 1) + " starting in period " + (incEvent.startPeriod + 1) + " for " + incEvent.duration + " period. ";
                }
            }
        }
        if (incidentEvents_ML.size() > 0) {
            detail = detail + "\n\n Incidents (ML):  ";
            for (IncidentEvent incEvent : incidentEvents_ML) {
                if (incEvent.duration > 1) {
                    detail = detail + "\n " + IncidentData.getIncidentTypeFull(incEvent.severity) + ": At segment " + (incEvent.getSegment() + 1) + " starting in period " + (incEvent.startPeriod + 1) + " for " + Math.min(incEvent.duration, seed.getValueInt(CEConst.IDS_NUM_PERIOD)) + " periods. ";
                } else {
                    detail = detail + "\n " + IncidentData.getIncidentTypeFull(incEvent.severity) + ": At segment " + (incEvent.getSegment() + 1) + " starting in period " + (incEvent.startPeriod + 1) + " for " + incEvent.duration + " period. ";
                }
            }
        }
        return detail;
    }
//</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Adders">
    /**
     * Adds a work zone to the scenario.
     *
     * @param workZone Work Zone Event.
     */
    public void addWorkZone(WorkZone workZone) {
        workZones.add(workZone);

        updateName();
    }

    /**
     * Add a work zone event to a scenario at a specific event index (DSS ONLY).
     *
     * @param workZone ScenarioEvent instance of the weather event
     * @param eventIndex Index at which the event is added to the event list
     */
    public void addWorkZone(WorkZone workZone, int eventIndex) {
        workZones.add(eventIndex, workZone);

        updateName();
    }

    /**
     * Add a weather event to a scenario.
     *
     * @param wEvent ScenarioEvent instance of the weather event
     */
    public void addWeatherEvent(WeatherEvent wEvent) {

        // Updating weather event info
        weatherEvents.add(wEvent);

        updateName();
    }

    /**
     * Add a weather event to a scenario at a specific event index (DSS ONLY).
     *
     * @param wEvent ScenarioEvent instance of the weather event
     * @param eventIndex Index at which the event is added to the event list
     */
    public void addWeatherEvent(WeatherEvent wEvent, int eventIndex) {

        // Updating weather event info
        weatherEvents.add(eventIndex, wEvent);

        updateName();
    }

    /**
     * Add an incident event to a scenario.
     *
     * @param incEvent
     */
    public void addIncidentEventGP(IncidentEvent incEvent) {

        // Updating incident event info
        incidentEvents_GP.add(incEvent);

        updateName();
    }

    /**
     * Add an incident event to a scenario at a specific event index (DSS ONLY).
     *
     * @param incEvent ScenarioEvent instance of the weather event
     * @param eventIndex Index at which the event is added to the event list
     */
    public void addIncidentEventGP(IncidentEvent incEvent, int eventIndex) {

        // Updating incident event info
        incidentEvents_GP.add(eventIndex, incEvent);

        updateName();
    }

    public void addIncidentEventML(IncidentEvent incEvent) {

        // Updating incident event info
        incidentEvents_ML.add(incEvent);

        updateName();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Has event functions">
    /**
     * Whether this scenario has weather event
     *
     * @return Whether this scenario has weather event
     */
    public boolean hasWeatherEvent() {
        return (weatherEvents.size() > 0);
    }

    /**
     * Whether this scenario has any GP incident events
     *
     * @return Whether this scenario has incident event
     */
    public boolean hasIncidentGP() {
        return (incidentEvents_GP.size() > 0);
    }

    /**
     * Whether this scenario has any ML incident events
     *
     * @return Whether this scenario has incident event
     */
    public boolean hasIncidentML() {
        return (incidentEvents_ML.size() > 0);
    }

    /**
     * Checks to see if the scenario has a work zone event.
     *
     * @return
     */
    public boolean hasWorkZone() {
        return (workZones.size() > 0);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overlap Checks">
    /**
     * Check whether weather event overlap previously assigned weather event
     *
     * @param weatherEvent WeatherEvent instance
     * @return boolean overlap: false if event does not overlap any previously
     * assigned event, true otherwise.
     */
    public boolean checkWeatherOverlap(WeatherEvent weatherEvent) {
        return weatherEvents.stream().anyMatch((wEvent) -> (wEvent.hasOverlap(weatherEvent)));
    }

    /**
     * Check whether a GP incident event overlaps any previously assigned
     * incident event
     *
     * @param incidentEvent IncidentEvent instance
     * @return boolean overlap: false if event does not overlap any previously
     * assigned event, true otherwise.
     */
    public boolean checkGPIncidentOverlap(IncidentEvent incidentEvent) {
        if (incidentEvent.getSegmentType() == CEConst.SEG_TYPE_GP) {
            return incidentEvents_GP.stream().anyMatch((incEvent) -> (incEvent.hasOverlap(incidentEvent)));
        } else {
            return false;
        }
    }

    /**
     * Checks to see if there are any overlaps in existing GP incidents assigned
     * to the scenario.
     *
     * @return True if there is overlap, false otherwise.
     */
    public boolean checkGPIncidentOverlap() {
        if (incidentEvents_GP.size() > 1) {
            for (int incIdx = 0; incIdx < incidentEvents_GP.size() - 1; incIdx++) {
                for (int incIdx2 = incIdx + 1; incIdx2 < incidentEvents_GP.size(); incIdx2++) {
                    if (incidentEvents_GP.get(incIdx).hasOverlap(incidentEvents_GP.get(incIdx2))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Check whether an ML incident event overlaps any previously assigned
     * incident event
     *
     * @param incidentEvent IncidentEvent instance
     * @return boolean overlap: false if event does not overlap any previously
     * assigned event, true otherwise.
     */
    public boolean checkMLIncidentOverlap(IncidentEvent incidentEvent) {
        if (incidentEvent.getSegmentType() == CEConst.SEG_TYPE_ML) {
            return incidentEvents_ML.stream().anyMatch((incEvent) -> (incEvent.hasOverlap(incidentEvent)));
        } else {
            return false;
        }
    }

    /**
     * Checks to see if there are any overlaps in existing ML incidents assigned
     * to the scenario.
     *
     * @return True if there is overlap, false otherwise.
     */
    public boolean checkMLIncidentOverlap() {
        if (incidentEvents_ML.size() > 1) {
            for (int incIdx = 0; incIdx < incidentEvents_ML.size() - 1; incIdx++) {
                for (int incIdx2 = incIdx + 1; incIdx2 < incidentEvents_ML.size(); incIdx2++) {
                    if (incidentEvents_ML.get(incIdx).hasOverlap(incidentEvents_ML.get(incIdx2))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Check whether a work zone event overlaps any previously assigned work
     * zone event.
     *
     * @param workZone WorkZoneData instance
     * @return
     */
    public boolean checkWorkZoneOverlap(WorkZone workZone) {
        boolean overlap = false;
        for (WorkZone wz : workZones) {
            if (wz.hasOverlap(workZone)) {
                overlap = true;
                break;
            }
        }

        return overlap;
    }
    // </editor-fold>

    /**
     * Update scenario name.
     */
    public void updateName() {
        name = demandPatternName;

        if (demandMultiplier != 0.0f) {
            name = name + " (" + String.format("%.3f", demandMultiplier) + ")";
        }

        if (workZones.size() > 0) {
            name = name + "  " + workZones.size() + "WZ";
        }

        if (weatherEvents.size() > 0) {
            name = name + "   " + weatherEvents.size() + "W";
        }
        if (incidentEvents_GP.size() > 0) {
            name = name + "   " + incidentEvents_GP.size() + "IGP";
        }
        if (incidentEvents_ML.size() > 0) {
            name = name + "   " + incidentEvents_ML.size() + "IML";
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Incident Event Functions">
    /**
     * Checks if the incident is valid for the scenario. An incident is valid
     * only if its severity (lane closure) is less than the number of open lanes
     * in the segment for each period in the duration of the incident.
     *
     * @deprecated Replaced by methods with IncidentEvent
     *
     * @param newIncident
     * @return True if the incident is valid, False otherwise
     */
    public boolean checkIncidentHasAllowableLaneClosure(IncidentEvent newIncident) {
        // Shoulder closures are always allowable
        if (newIncident.severity == 0) {
            return true;
        }

        // Creating array of available lanes for the incident
        float[] numAvailableLanes = new float[newIncident.duration];
        for (int period = 0; period < newIncident.duration; period++) {
            int mappedPeriod = (newIncident.startPeriod + period) % seed.getValueInt(CEConst.IDS_NUM_PERIOD);
            if (newIncident.segmentType == CEConst.SEG_TYPE_GP) {
                numAvailableLanes[period] = seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, newIncident.getSegment(), mappedPeriod);
            } else {
                numAvailableLanes[period] = seed.getValueInt(CEConst.IDS_ML_NUM_LANES, newIncident.getSegment(), mappedPeriod);
            }
        }

        // Subtracting any work zone lane closures
        if (newIncident.segmentType == CEConst.SEG_TYPE_GP) {
            for (WorkZone wz : workZones) {
                for (int period = 0; period <= newIncident.duration; period++) {
                    int mappedPeriod = (newIncident.startPeriod + period) % seed.getValueInt(CEConst.IDS_NUM_PERIOD);
                    if (wz.isActiveIn(newIncident.getSegment(), mappedPeriod)) {
                        numAvailableLanes[period] += wz.getEventLAF(newIncident.getSegment());
                    }
                }
            }
        }

        for (int idx = 0; idx < numAvailableLanes.length; idx++) {
            if (numAvailableLanes[idx] <= newIncident.severity) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an array of the number of lanes available in the input GP segment
     * for the specified periods.
     *
     * @param segment Segment number in which to count lane availability.
     * @param startPeriod First period in which to count lane availability.
     * @param duration Duration for which to count lane availability.
     * @return Array containing the number of lanes available in segment for the
     * time period.
     */
    public int[] getAvailableGPLanes(int segment, int startPeriod, int duration) {
        int[] numAvailableLanes = new int[duration];
        for (int period = 0; period < duration; period++) {
            int mappedPeriod = (startPeriod + period) % seed.getValueInt(CEConst.IDS_NUM_PERIOD);
            numAvailableLanes[period] = seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, segment, mappedPeriod);
        }

        // Subtracting any work zone lane closures
        for (WorkZone wz : workZones) {
            for (int period = 0; period < duration; period++) {
                int mappedPeriod = (startPeriod + period) % seed.getValueInt(CEConst.IDS_NUM_PERIOD);
                if (wz.isActiveIn(segment, mappedPeriod)) {
                    numAvailableLanes[period] += wz.getEventLAF(segment);
                }
            }
        }
        return numAvailableLanes;
    }

    /**
     * Returns an array of the number of lanes available in the input ML segment
     * for the specified periods.
     *
     * @param segment Segment number in which to count lane availability.
     * @param startPeriod First period in which to count lane availability.
     * @param duration Duration for which to count lane availability.
     * @return Array containing the number of lanes available in segment for the
     * time period.
     */
    public int[] getAvailableMLLanes(int segment, int startPeriod, int duration) {
        int[] numAvailableLanes = new int[duration];
        for (int period = 0; period < duration; period++) {
            int mappedPeriod = (startPeriod + period) % seed.getValueInt(CEConst.IDS_NUM_PERIOD);
            numAvailableLanes[period] = seed.getValueInt(CEConst.IDS_ML_NUM_LANES, segment, mappedPeriod);
        }
        return numAvailableLanes;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Setter for seed.
     *
     * @param seed
     */
    public void setSeed(Seed seed) {
        this.seed = seed;
    }

    /**
     * Setter for demand pattern name.
     *
     * @param demandPatternName name of the demand pattern
     */
    public void setDemandPatternName(String demandPatternName) {
        this.demandPatternName = demandPatternName;
        updateName();
    }

    /**
     * SEtter for the demand multiplier for the scenario.
     *
     * @param demandMultiplier new demand multiplier.
     */
    public void setDemandMultiplier(float demandMultiplier) {
        this.demandMultiplier = demandMultiplier;
        updateName();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Getter for number of weather events
     *
     * @return number of weather events
     */
    public int getNumberOfWeatherEvents() {
        return weatherEvents.size();
    }

    /**
     * Getter for number of GP incident events
     *
     * @return number of incident events
     */
    public int getNumberOfGPIncidentEvents() {
        return incidentEvents_GP.size();
    }

    /**
     * Getter for number of ML incident events
     *
     * @return number of incident events
     */
    public int getNumberOfMLIncidentEvents() {
        return incidentEvents_ML.size();
    }

    /**
     * Getter for number of work zones
     *
     * @return number of incident events
     */
    public int getNumberOfWorkZones() {
        return workZones.size();
    }

    /**
     * Getter for the list of weather events for the scenario.
     *
     * @return List of WeatherEvent instances.
     */
    public ArrayList<WeatherEvent> getWeatherEventList() {
        return weatherEvents;
    }

    /**
     * Getter for the list of work zone events for the scenario.
     *
     * @return
     */
    public ArrayList<WorkZone> getWorkZoneEventList() {
        return workZones;
    }

    /**
     * Getter for the list of incident events for the scenario.
     *
     * @return List of IncidentEvent instances.
     */
    public ArrayList<IncidentEvent> getGPIncidentEventList() {
        return incidentEvents_GP;
    }

    /**
     * Returns the month of the demand combination of the scenario. Indexing
     * starts at 0;
     *
     * @return
     */
    public int getMonth() {
        return month;
    }

    /**
     * Returns the day type (day of week) of the demand combination of the
     * scenario. 0 - Monday, 1 - Tuesday, 2 - Wednesday, 3 - Thursday, 4 -
     * Friday, 5 - Saturday, 6 - Sunday.
     *
     * @return
     */
    public int getDayType() {
        return day;
    }

    /**
     *
     * @return
     */
    public float getDemandMultiplier() {
        return this.demandMultiplier;
    }

    /**
     * Getter for the work zone detail string. String lists the details for each
     * work zone assigned to the scenario.
     *
     * @return String listing the details of all work zones events.
     */
    public String getWorkZoneDetail() {
        if (workZones.size() > 0) {
            return this.getDetail().split("\n\n")[1];
        } else {
            return "";
        }
    }

    /**
     * Getter for the weather events detail string. String lists the details for
     * each weather event assigned to the scenario.
     *
     * @return String listing the details of all scenario weather events.
     */
    public String getWeatherDetail() {
        if (weatherEvents.size() > 0) {
            if (workZones.size() > 0) {
                return this.getDetail().split("\n\n")[2];
            } else {
                return this.getDetail().split("\n\n")[1];
            }
        } else {
            return "";
        }
    }

    /**
     * Getter for the general purpose incident events detail string. String
     * lists the details for each general purpose incident event assigned to the
     * scenario.
     *
     * @return String listing the details of all scenario general purpose
     * events.
     */
    public String getGPIncidentDetail() {
        if (incidentEvents_GP.size() > 0) {
            if (workZones.size() > 0) {
                if (weatherEvents.size() > 0) {
                    return this.getDetail().split("\n\n")[3];
                } else {
                    return this.getDetail().split("\n\n")[2];
                }
            } else {
                if (weatherEvents.size() > 0) {
                    return this.getDetail().split("\n\n")[2];
                } else {
                    return this.getDetail().split("\n\n")[1];
                }
            }
        } else {
            return "";
        }
    }

    //<editor-fold defaultstate="collapsed" desc="ATDM with diversion for incidents">
    //<editor-fold defaultstate="collapsed" desc="ATDMScenario Generation for GP">
    /**
     * Returns an ATDMScenario object that reflects the application of the
     * strategies in the specified ATDM plan for GP segments.
     *
     * @param atdmPlan ATDM plan to be applied to the reliability scenario.
     * @return ATDMScenario instance with the ATDM plan strategies applied.
     */
    public ATDMScenario generateATDMScenario(ATDMPlan atdmPlan) {
        int numAnalysisPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        int numSegments = seed.getValueInt(CEConst.IDS_NUM_SEGMENT);

        ATDMScenario tempATDMScenario = new ATDMScenario(numSegments, numAnalysisPeriods);
        tempATDMScenario.setName(atdmPlan.getName());
        tempATDMScenario.setDiscription(atdmPlan.getInfo());

        float[][] atdmAdjFactors = atdmPlan.getATDMadjFactors();

        // Demand management strategies (applied for all segments, all periods)
        tempATDMScenario.DAF().set(atdmAdjFactors[0][0],
                0, 0,
                numSegments - 1, numAnalysisPeriods - 1);
        tempATDMScenario.OAF().set(atdmAdjFactors[0][0],
                0, 0,
                numSegments - 1, numAnalysisPeriods - 1);

        // Work Zone ATDM applied only if scenario has a work zone
        // Applied as a diversion strategy.  Upstream mainline demand reduced as
        // well as fo any on ramp segments
        // Work Zone periods will never wrap
        if (workZones.size() > 0) {
            for (int wzIdx = 0; wzIdx < workZones.size(); wzIdx++) {
                // Applying demand to upstream mainline demand
                tempATDMScenario.OAF().multiply(atdmAdjFactors[3][0],
                        0,
                        workZones.get(wzIdx).getStartPeriod(),
                        0,
                        workZones.get(wzIdx).getEndPeriod());
                tempATDMScenario.DAF().multiply(atdmAdjFactors[3][0],
                        0,
                        workZones.get(wzIdx).getStartPeriod(),
                        0,
                        workZones.get(wzIdx).getEndPeriod());
                // Applying to all upstream segments
                for (int seg = 1; seg < workZones.get(wzIdx).getStartSegment(); seg++) {
                    if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_ONR || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_W) {
                        tempATDMScenario.OAF().multiply(atdmAdjFactors[3][0],
                                seg,
                                workZones.get(wzIdx).getStartPeriod(),
                                seg,
                                workZones.get(wzIdx).getEndPeriod());
                    }
                }

                // Applying non-diversion ATDM AFs to work zone segments
                tempATDMScenario.SAF().multiply(atdmAdjFactors[3][1],
                        workZones.get(wzIdx).getStartSegment(),
                        workZones.get(wzIdx).getStartPeriod(),
                        workZones.get(wzIdx).getEndSegment() - 1,
                        workZones.get(wzIdx).getEndPeriod());
                tempATDMScenario.CAF().multiply(atdmAdjFactors[3][2],
                        workZones.get(wzIdx).getStartSegment(),
                        workZones.get(wzIdx).getStartPeriod(),
                        workZones.get(wzIdx).getEndSegment(),
                        workZones.get(wzIdx).getEndPeriod());
            }

        }

        // Weather applied only if weather event in period
        // (Does wrap time periods)
        if (weatherEvents.size() > 0) {
            for (WeatherEvent wEvent : weatherEvents) {
                if (!wEvent.hasPeriodWrapping()) {
                    // Event does not wrap
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                } else {
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());

                }
            }
        }

        if (incidentEvents_GP.size() > 0) {
            int seg;
            int durReduction = atdmPlan.getIncidentDurationReduction();
            for (IncidentEvent incEvent : incidentEvents_GP) {
                seg = incEvent.getSegment();
                // Get list of upstream mainline and onramp segments
                ArrayList<Integer> divSegments = new ArrayList<>();
                divSegments.add(0);
                for (int segIdx = 1; segIdx < incEvent.getSegment(); segIdx++) {
                    if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, segIdx) == CEConst.SEG_TYPE_ONR || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, segIdx) == CEConst.SEG_TYPE_W) {
                        divSegments.add(segIdx);
                    }
                }

                if (incEvent.duration <= durReduction) {                                        // Case 1: Duration is shorter than duration reduction
                    // Reverse adjustmentFactors applied to scenario
                    if (!incEvent.hasPeriodWrapping()) {                                        // Case 1a: incident does not wrap
                        for (int per = incEvent.startPeriod; per <= incEvent.getEndPeriod(); per++) {
                            //adjNumLanes = Math.min(Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, per), 6) - 2, 0);
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod());

                    } else {                                                    // Case 1b: Incident wraps
                        //endTime = endTime % numAnalysisPeriods;
                        for (int per = incEvent.startPeriod; per <= numAnalysisPeriods - 1; per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }

                        for (int per = 0; per <= incEvent.getEndPeriod(); per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }

                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.startPeriod,
                                seg,
                                numAnalysisPeriods - 1);
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                0,
                                seg,
                                incEvent.getEndPeriod());
                    }
                } else {                                                        // Case 2: Duration is greater than duration reduction
                    if (!incEvent.hasPeriodWrapping()) {                        // Case 2a: incident does not wrap
                        // Reversal of AFs for incident duration reduction
                        for (int per = incEvent.getEndPeriod() - (durReduction - 1); per <= incEvent.getEndPeriod(); per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.getEndPeriod() - (durReduction - 1),
                                seg,
                                incEvent.getEndPeriod());

                        // Application of non-diversion AFs to incident segments
                        tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);
                        tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);
                        tempATDMScenario.LAF().add(0,
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);

                        // Application of AFs for incident diversion
                        for (int divSeg : divSegments) {
                            tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                    divSeg,
                                    incEvent.startPeriod,
                                    divSeg,
                                    incEvent.getEndPeriod() - durReduction);
                        }
                    } else {                                                    // Case 2b: incident wraps
                        if ((incEvent.startPeriod + incEvent.duration - durReduction) < numAnalysisPeriods) {     // Case 2b1: wrap occurs during duration reduction
                            //int wrappedEndTime = endTime % numAnalysisPeriods;
                            // Reversal of AFs for incident duration reduction
                            for (int per = incEvent.startPeriod + incEvent.duration - durReduction; per <= numAnalysisPeriods - 1; per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction,
                                    seg,
                                    numAnalysisPeriods - 1);
                            for (int per = 0; per <= incEvent.getEndPeriod(); per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod());

                            // Applying ATDM AFs for incident segments
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);

                            // Applying Incident Diversion
                            for (int divSeg : divSegments) {
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        incEvent.startPeriod,
                                        divSeg,
                                        incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            }

                        } else {                                                // Case 2b2: wrap occurs before duration reduction
                            // Reversal of AFs for incident duration reduction
                            for (int per = incEvent.getEndPeriod() - (durReduction - 1); per <= incEvent.getEndPeriod(); per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    incEvent.getEndPeriod() - (durReduction - 1),
                                    seg,
                                    incEvent.getEndPeriod());

                            //Application of non-diversion AFs
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);

                            // Application of incident diversion AFs
                            for (int divSeg : divSegments) {
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        incEvent.startPeriod,
                                        divSeg,
                                        numAnalysisPeriods - 1);
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        0,
                                        divSeg,
                                        incEvent.getEndPeriod() - durReduction);
                            }

                        }
                    }
                }
            }
        }

        if (atdmPlan.hasShoulderOpening()) {
            CA2DInt hsrMat = atdmPlan.getHSRMatrix();
            int numLanesSegment;
            for (int segment = 0; segment < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); segment++) {
                for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    if (hsrMat.get(segment, period) == 1) {
                        numLanesSegment = seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, segment, period);
                        tempATDMScenario.LAF().add(1, segment, period); // Adding lane

                        // Calculating new segment CAF using shoulder CAF
                        float rlCAF = seed.getRLCAF(group + 1, segment, period, CEConst.SEG_TYPE_GP); //To Lake: Maybe need to switch between GP and ML
                        float seedCAF = seed.getValueFloat(CEConst.IDS_GP_USER_CAF, segment, period);
                        float newCAF = ((numLanesSegment * seedCAF * rlCAF * tempATDMScenario.CAF().get(segment, period)) + atdmPlan.getHSRCAF(numLanesSegment)) / (numLanesSegment + 1);
                        //System.out.println(newCAF);
                        tempATDMScenario.CAF().set((newCAF / rlCAF), segment, period);
                    }
                }
            }
        }

        if (atdmPlan.hasRampMetering()) {
            tempATDMScenario.RM().setGlobalRMType(CEConst.IDS_RAMP_METERING_TYPE_FIX);
            tempATDMScenario.RM().getRampMeteringFixRate().deepCopyFrom(atdmPlan.getRMRate());
            for (int segment = 0; segment < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); segment++) {
                for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    if (atdmPlan.getRMRate().get(segment, period) < 1800) {
                        tempATDMScenario.CAF().multiply(1 + atdmPlan.getCapacityIncreaseDueToRM(), segment, period);
                    }
                }
            }
        }
        return tempATDMScenario;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ATDMScenario Generation for ML">
    /**
     * Returns an ATDMScenario object that reflects the application of the
     * strategies in the specified ATDM plan for ML segments.
     *
     * @param atdmPlan ATDM plan to be applied to the reliability scenario.
     * @return ATDMScenario instance with the ATDM plan strategies applied.
     */
    public ATDMScenario generateATDMScenarioML(ATDMPlan atdmPlan) {
        if (!seed.isManagedLaneUsed()) {
            return null;
        }
        int numAnalysisPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        int numSegments = seed.getValueInt(CEConst.IDS_NUM_SEGMENT);

        ATDMScenario tempATDMScenario = new ATDMScenario(numSegments, numAnalysisPeriods);
        tempATDMScenario.setName(atdmPlan.getName());
        tempATDMScenario.setDiscription(atdmPlan.getInfo());

        float[][] atdmAdjFactors = atdmPlan.getATDMadjFactors();

        // Demand management strategies (applied for all segments, all periods)
        tempATDMScenario.DAF().set(atdmAdjFactors[0][0],
                0, 0,
                numSegments - 1, numAnalysisPeriods - 1);
        tempATDMScenario.OAF().set(atdmAdjFactors[0][0],
                0, 0,
                numSegments - 1, numAnalysisPeriods - 1);

        // Weather applied only if weather event in period
        // (Does wrap time periods)
        if (weatherEvents.size() > 0) {
            for (WeatherEvent wEvent : weatherEvents) {
                if (!wEvent.hasPeriodWrapping()) {
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                } else {
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.OAF().multiply(atdmAdjFactors[1][0],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.DAF().multiply(atdmAdjFactors[1][0],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.SAF().multiply(atdmAdjFactors[1][1],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            wEvent.startPeriod,
                            numSegments - 1,
                            numAnalysisPeriods - 1);
                    tempATDMScenario.CAF().multiply(atdmAdjFactors[1][2],
                            0,
                            0,
                            numSegments - 1,
                            wEvent.getEndPeriod());

                }
            }
        }

        if (incidentEvents_ML.size() > 0) {
            int seg;
            int durReduction = atdmPlan.getIncidentDurationReduction();
            for (IncidentEvent incEvent : incidentEvents_ML) {
                seg = incEvent.getSegment();
                // Get list of upstream mainline and onramp segments
                ArrayList<Integer> divSegments = new ArrayList<>();
                divSegments.add(0);
                for (int segIdx = 1; segIdx < incEvent.getSegment(); segIdx++) {
                    if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, segIdx) == CEConst.SEG_TYPE_ONR || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, segIdx) == CEConst.SEG_TYPE_W) {
                        divSegments.add(segIdx);
                    }
                }

                if (incEvent.duration <= durReduction) {                                        // Case 1: Duration is shorter than duration reduction
                    // Reverse adjustmentFactors applied to scenario
                    if (!incEvent.hasPeriodWrapping()) {                                        // Case 1a: incident does not wrap
                        for (int per = incEvent.startPeriod; per <= incEvent.getEndPeriod(); per++) {
                            //adjNumLanes = Math.min(Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, per), 6) - 2, 0);
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod());

                    } else {                                                    // Case 1b: Incident wraps
                        //endTime = endTime % numAnalysisPeriods;
                        for (int per = incEvent.startPeriod; per <= numAnalysisPeriods - 1; per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }

                        for (int per = 0; per <= incEvent.getEndPeriod(); per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }

                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.startPeriod,
                                seg,
                                numAnalysisPeriods - 1);
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                0,
                                seg,
                                incEvent.getEndPeriod());
                    }
                } else {                                                        // Case 2: Duration is greater than duration reduction
                    if (!incEvent.hasPeriodWrapping()) {                        // Case 2a: incident does not wrap
                        // Reversal of AFs for incident duration reduction
                        for (int per = incEvent.getEndPeriod() - (durReduction - 1); per <= incEvent.getEndPeriod(); per++) {
                            tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                            tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                    seg,
                                    per,
                                    seg,
                                    per);
                        }
                        tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                seg,
                                incEvent.getEndPeriod() - (durReduction - 1),
                                seg,
                                incEvent.getEndPeriod());

                        // Application of non-diversion AFs to incident segments
                        tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);
                        tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);
                        tempATDMScenario.LAF().add(0,
                                seg,
                                incEvent.startPeriod,
                                seg,
                                incEvent.getEndPeriod() - durReduction);

                        // Application of AFs for incident diversion
                        for (int divSeg : divSegments) {
                            tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                    divSeg,
                                    incEvent.startPeriod,
                                    divSeg,
                                    incEvent.getEndPeriod() - durReduction);
                        }
                    } else {                                                    // Case 2b: incident wraps
                        if ((incEvent.startPeriod + incEvent.duration - durReduction) < numAnalysisPeriods) {     // Case 2b1: wrap occurs during duration reduction
                            //int wrappedEndTime = endTime % numAnalysisPeriods;
                            // Reversal of AFs for incident duration reduction
                            for (int per = incEvent.startPeriod + incEvent.duration - durReduction; per <= numAnalysisPeriods - 1; per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction,
                                    seg,
                                    numAnalysisPeriods - 1);
                            for (int per = 0; per <= incEvent.getEndPeriod(); per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod());

                            // Applying ATDM AFs for incident segments
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    incEvent.startPeriod + incEvent.duration - durReduction - 1);

                            // Applying Incident Diversion
                            for (int divSeg : divSegments) {
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        incEvent.startPeriod,
                                        divSeg,
                                        incEvent.startPeriod + incEvent.duration - durReduction - 1);
                            }

                        } else {                                                // Case 2b2: wrap occurs before duration reduction
                            // Reversal of AFs for incident duration reduction
                            for (int per = incEvent.getEndPeriod() - (durReduction - 1); per <= incEvent.getEndPeriod(); per++) {
                                tempATDMScenario.OAF().multiply((1.0f / incEvent.getEventOAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.DAF().multiply((1.0f / incEvent.getEventDAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.SAF().multiply((1.0f / incEvent.getEventSAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                                tempATDMScenario.CAF().multiply((1.0f / incEvent.getEventCAF(per, seg)),
                                        seg,
                                        per,
                                        seg,
                                        per);
                            }
                            tempATDMScenario.LAF().add(incEvent.getEventLAF(incEvent.startPeriod, seg),
                                    seg,
                                    incEvent.getEndPeriod() - (durReduction - 1),
                                    seg,
                                    incEvent.getEndPeriod());

                            //Application of non-diversion AFs
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.SAF().multiply(atdmAdjFactors[2][1],
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.CAF().multiply(atdmAdjFactors[2][2],
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    incEvent.startPeriod,
                                    seg,
                                    numAnalysisPeriods - 1);
                            tempATDMScenario.LAF().add(0,
                                    seg,
                                    0,
                                    seg,
                                    incEvent.getEndPeriod() - durReduction);

                            // Application of incident diversion AFs
                            for (int divSeg : divSegments) {
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        incEvent.startPeriod,
                                        divSeg,
                                        numAnalysisPeriods - 1);
                                tempATDMScenario.OAF().multiply(atdmAdjFactors[2][0],
                                        divSeg,
                                        0,
                                        divSeg,
                                        incEvent.getEndPeriod() - durReduction);
                            }

                        }
                    }
                }
            }
        }

        // No hard shoulder running for Managed Lanes.
        // No ramp metering for Managed Lanes
        tempATDMScenario.RM().setGlobalRMType(CEConst.IDS_RAMP_METERING_TYPE_NONE);

        //if (atdmPlan.hasRampMetering()) {
        //    tempATDMScenario.setRampMetering(true);
        //    tempATDMScenario.RM().deepCopyFrom(atdmPlan.getRMRate());
        //}
        //System.out.println("Sucessfully Created ML ATDM");
        return tempATDMScenario;
    }
    //</editor-fold>
    //</editor-fold>

    /**
     * Method that applies the specified plan of the ATDMDatabase to the
     * scenario. The method also returns an ATDMScenario object for use in ATDM
     * analysis.
     *
     * @param atdmDatabase ATDMDatabase for the ATDM analysis.
     * @param planIdx Index of the desired ATDM plan to apply.
     * @return ATDMScenario with plan applied to the reliability scenario.
     */
    public ATDMScenario[] applyAndGetATDMScenarios(ATDMDatabase atdmDatabase, int planIdx) {
        return new ATDMScenario[]{generateATDMScenario(atdmDatabase.getPlan(planIdx)), generateATDMScenarioML(atdmDatabase.getPlan(planIdx))};

    }
    // </editor-fold>
}
