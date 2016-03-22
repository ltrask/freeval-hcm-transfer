package coreEngine.atdm.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Lake Trask
 */
public class ATDMDatabase implements Serializable {

    /**
     * List of Demand ATDM Strategies
     */
    private final ArrayList<ATDMStrategy> demandStrat;

    /**
     * List of Weather ATDM Strategies
     */
    private final ArrayList<ATDMStrategy> weatherStrat;

    /**
     * List of Incident ATDM Strategies
     */
    private final ArrayList<ATDMStrategy> incidentStrat;

    /**
     * List of Work Zone ATMD Strategies
     */
    private final ArrayList<ATDMStrategy> workZoneStrat;

    /**
     * List of Ramp Metering ATMD Strategies
     */
    private final ArrayList<ATDMStrategy> rmStrat;

    /**
     * List of Hard Shoulder Running ATMD Strategies
     */
    private final ArrayList<ATDMStrategy> hsrStrat;

    /**
     * List of ATDM Plans
     */
    private final ArrayList<ATDMPlan> atdmPlans;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 51348672125L;

    /**
     * Constructor to create an ATDM Database Object
     */
    public ATDMDatabase() {

        demandStrat = new ArrayList<>();
        weatherStrat = new ArrayList<>();
        incidentStrat = new ArrayList<>();
        workZoneStrat = new ArrayList<>();
        rmStrat = new ArrayList<>();
        hsrStrat = new ArrayList<>();

        atdmPlans = new ArrayList<>();

    }

    //<editor-fold defaultstate="collapsed" desc="Initialize Defaults">
    /**
     * Initializes the default set of databases based on the HCM 6th Edition.
     */
    public void initDefaultDatabase() {
        initDemandDefaultStrategies();
        initWeatherDefaultStrategies();
        initIncidentDefaultStrategies();
        initWorkZoneDefaultStrategies();

        initATDMPlans();
    }

    /**
     * Creates the set of default demand ATDM strategies.
     */
    private void initDemandDefaultStrategies() {
        int startId = demandStrat.size() + 1;

        demandStrat.add(new ATDMStrategy(startId++, "HOT Lane - Static Toll", 0));
        demandStrat.add(new ATDMStrategy(startId++, "HOT Lane - Congestion Pricing", 0));
        demandStrat.add(new ATDMStrategy(startId++, "Full Facility Static Toll", 0));
        demandStrat.add(new ATDMStrategy(startId++, "Full Facility Dynamic Congestion Pricing", 0));
        demandStrat.add(new ATDMStrategy(startId++, "Pre-Trip Traveler Info", 1));
        demandStrat.add(new ATDMStrategy(startId++, "Variable Message Signs", 1));
        demandStrat.add(new ATDMStrategy(startId, "Employer TDM", 1));
    }

    /**
     * Creates the set of weather demand ATDM strategies.
     */
    private void initWeatherDefaultStrategies() {
        int startId = weatherStrat.size() + 1;

        weatherStrat.add(new ATDMStrategy(startId++, "Vehicle Restrictions (chain controls)", 0));
        weatherStrat.add(new ATDMStrategy(startId++, "Pre-trip Travel Info", 1));
        weatherStrat.add(new ATDMStrategy(startId++, "Variable Message Signs", 1));
        weatherStrat.add(new ATDMStrategy(startId++, "Employer TDM", 1));
        weatherStrat.add(new ATDMStrategy(startId++, "Snow Removal", 2));
        weatherStrat.add(new ATDMStrategy(startId++, "Anti-Icing", 2));
        weatherStrat.add(new ATDMStrategy(startId++, "Fog Dispersion", 2));
    }

    /**
     * Creates the set of incident demand ATDM strategies.
     */
    private void initIncidentDefaultStrategies() {
        int startId = incidentStrat.size() + 1;

        incidentStrat.add(new ATDMStrategy(startId++, "Incident Command System", 3));
        incidentStrat.add(new ATDMStrategy(startId++, "Traffic Control With On-Site Traffic Management Teams", 3));
        incidentStrat.add(new ATDMStrategy(startId++, "End-of-Queue Advance Warning Systems", 3));
        incidentStrat.add(new ATDMStrategy(startId++, "Pre-trip Travel Info", 1));
        incidentStrat.add(new ATDMStrategy(startId++, "Variable Message Signs", 1));
        incidentStrat.add(new ATDMStrategy(startId++, "Portable Message Signs", 1));
        incidentStrat.add(new ATDMStrategy(startId++, "Employer TDM", 1));
        incidentStrat.add(new ATDMStrategy(startId++, "Field Verification by On-Site Responders", 4));
        incidentStrat.add(new ATDMStrategy(startId++, "Closed-Circuit Television Cameras", 4));
        incidentStrat.add(new ATDMStrategy(startId++, "Frequent/Enhanced Roadway Reference Markers", 4));
        incidentStrat.add(new ATDMStrategy(startId++, "Enhanced 911/Automated Positioning Systems", 4));
        incidentStrat.add(new ATDMStrategy(startId++, "Motorist Aid Call Boxes", 4));
        incidentStrat.add(new ATDMStrategy(startId++, "Automated Collision Notification Systems", 4));
    }

    /**
     * Creates the set of work zone demand ATDM strategies.
     */
    private void initWorkZoneDefaultStrategies() {
        int startId = workZoneStrat.size() + 1;

        workZoneStrat.add(new ATDMStrategy(startId++, "End-of-Queue Advance Warning Systems", 3));
        workZoneStrat.add(new ATDMStrategy(startId++, "Speed Feedback Signs", 3));
        workZoneStrat.add(new ATDMStrategy(startId++, "Automated Speed Enforcement", 3));
        workZoneStrat.add(new ATDMStrategy(startId++, "Pre-trip Travel Info", 1));
        workZoneStrat.add(new ATDMStrategy(startId++, "Variable Message Signs", 1));
        workZoneStrat.add(new ATDMStrategy(startId++, "Portable Message Signs", 1));
        workZoneStrat.add(new ATDMStrategy(startId++, "Employer TDM", 1));
        workZoneStrat.add(new ATDMStrategy(startId++, "Detours", 1));
    }

    /**
     * Creates the set of default demand ATDM plans.
     */
    private void initATDMPlans() {
        // If no plans exist, create one empty plan
        if (atdmPlans.isEmpty()) {
            atdmPlans.add(new ATDMPlan(1, "Plan " + 1));
        }
    }
//</editor-fold>

    /**
     * Adds a basic (demand, weather, incident, work zone) strategy to the
     * database.
     *
     * @param categoryID Strategy Type (CEConst.IDS_ATDM_STRAT_TYPE_DEMAND,
     * CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, etc.).
     * @param newStrategy ATDM Strategy to be added.
     */
    public void addStrategy(String categoryID, ATDMStrategy newStrategy) {

        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                demandStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                weatherStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                incidentStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                workZoneStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                rmStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                hsrStrat.add(newStrategy);
                break;
        }
    }

    /**
     * Adds a matrix based (ramp metering, hard shoulder running) ATDM strategy
     * to the database.
     *
     * @param categoryID Type of Strategy
     * (CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING,
     * CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING)
     * @param newStrategy ATDMStrategyMat Object
     */
    public void addStrategy(String categoryID, ATDMStrategyMat newStrategy) {
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                rmStrat.add(newStrategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                hsrStrat.add(newStrategy);
                break;
        }
    }

    /**
     * Adds a new ATDM plan to the database.
     *
     * @param newPlan ATDMPlan object
     */
    public void addPlan(ATDMPlan newPlan) {
        atdmPlans.add(newPlan);
    }

    /**
     * Getter for the list of strategies of a certain type.
     *
     * @param categoryID ATDM Strategy Type (CEConst.IDS_ATDM_STRAT_TYPE_DEMAND,
     * CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, etc.).
     * @return ArrayList of ATDM strategies of the specified type.
     */
    public ArrayList<ATDMStrategy> getStrategy(String categoryID) {
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                return demandStrat;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                return weatherStrat;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                return incidentStrat;
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                return workZoneStrat;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                return rmStrat;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                return hsrStrat;
            default:
                return null;
        }
    }

    /**
     * Getter for an individual strategy of a specified type.
     *
     * @param categoryID ATDM Strategy Type (CEConst.IDS_ATDM_STRAT_TYPE_DEMAND,
     * CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, etc.).
     * @param stratIdx Index of the desired strategy.
     * @return Single ATDM strategy of the specified type.
     */
    public ATDMStrategy getStrategy(String categoryID, int stratIdx) {
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                return demandStrat.get(stratIdx);
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                return weatherStrat.get(stratIdx);
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                return incidentStrat.get(stratIdx);
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                return workZoneStrat.get(stratIdx);
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                return rmStrat.get(stratIdx);
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                return hsrStrat.get(stratIdx);
            default:
                return null;
        }
    }

    /**
     * Index based getter for an ATDM Plan contained in the database.
     *
     * @param planIdx Index of the plan.
     * @return ATDM plan of the current database stored at the specified index.
     */
    public ATDMPlan getPlan(int planIdx) {
        return atdmPlans.get(planIdx);
    }

    /**
     * Name based getter for an ATDM Plan contained in the database.
     *
     * @param planName Name of the plan.
     * @return ATDM plan of the current database stored at the specified name.
     */
    public ATDMPlan getPlan(String planName) {
        for (ATDMPlan atdmPlan : atdmPlans) {
            if (atdmPlan.getName().equalsIgnoreCase(planName)) {
                return atdmPlan;
            }
        }
        return null;
    }

    /**
     * Getter for the number of ATDM plans currently in the database.
     *
     * @return Integer number of plans.
     */
    public int getNumberOfATDMPlans() {
        return atdmPlans.size();
    }

    /**
     * Getter for the number of strategies of a specific type in the database.
     *
     * @param categoryID ATDM Strategy Type (CEConst.IDS_ATDM_STRAT_TYPE_DEMAND,
     * CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, etc.).
     * @return Integer number of strategies of the type in the database.
     */
    public int getNumberOfStrategies(String categoryID) {
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                return demandStrat.size();
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                return weatherStrat.size();
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                return incidentStrat.size();
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                return workZoneStrat.size();
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                return rmStrat.size();
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                return hsrStrat.size();
            default:
                return 0;
        }
    }

    /**
     * Removes the strategy at index strategyIdx from the given category/type
     * (Demand, Weather, Incident, Workzone).
     *
     * @param categoryID
     * @param strategyIdx
     */
    public void removeStrategy(String categoryID, int strategyIdx) {

        ArrayList<ATDMStrategy> currStratDB;
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                currStratDB = demandStrat;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                currStratDB = weatherStrat;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                currStratDB = incidentStrat;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                currStratDB = workZoneStrat;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                currStratDB = rmStrat;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                currStratDB = hsrStrat;
                break;
            default: // never called
                currStratDB = new ArrayList<>();
                break;
        }

        ATDMStrategy removedStrat = currStratDB.remove(strategyIdx);
        // Removing from all plans in the active ATDMDatabase.
        for (ATDMPlan atdmPlan : atdmPlans) {
            atdmPlan.getAppliedStrategies().remove(removedStrat);
        }

    }

    /**
     * Removes a strategy from the specified category/type. Pointers must match.
     *
     * @param categoryID
     * @param strategy
     */
    public void removeStrategy(String categoryID, ATDMStrategy strategy) {
        switch (categoryID) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                demandStrat.remove(strategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                weatherStrat.remove(strategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                incidentStrat.remove(strategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                workZoneStrat.remove(strategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                rmStrat.remove(strategy);
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                hsrStrat.remove(strategy);
                break;
        }

        for (ATDMPlan atdmPlan : atdmPlans) {
            atdmPlan.getAppliedStrategies().remove(strategy);
        }
    }

    /**
     * Removes the plan at index planIdx from the database
     *
     * @param planIdx
     */
    public void removePlan(int planIdx) {
        atdmPlans.remove(planIdx);
    }

    /**
     * Removes the specified ATDMPlan (pointers much match)
     *
     * @param atdmPlan
     */
    public void removePlan(ATDMPlan atdmPlan) {
        atdmPlans.remove(atdmPlan);
    }

    /**
     * Prepares database for export to binary save file. Removes ramp metering
     * and hard shoulder running strategies.
     */
    public void validateFacilitySpecificStrategies(Seed seed) {
        int numSeg = seed.getValueInt(CEConst.IDS_NUM_SEGMENT);
        int numPeriod = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        boolean verified = false;
        boolean validFacilitySize = false;
        if (rmStrat.size() > 0) {
            verified = true;
            ATDMStrategyMat tempStrat = (ATDMStrategyMat) rmStrat.get(0);
            int stratNumSeg = tempStrat.getStrategyMatrix().getSizeX();
            int stratNumPeriod = tempStrat.getStrategyMatrix().getSizeY();
            if (stratNumSeg == numSeg && stratNumPeriod == numPeriod) {
                validFacilitySize = true;
            }
        }

        if (hsrStrat.size() > 0 && !verified) {
            ATDMStrategyMat tempStrat = (ATDMStrategyMat) hsrStrat.get(0);
            int stratNumSeg = tempStrat.getStrategyMatrix().getSizeX();
            int stratNumPeriod = tempStrat.getStrategyMatrix().getSizeY();
            if (stratNumSeg == numSeg && stratNumPeriod == numPeriod) {
                validFacilitySize = true;
            }
        }

        if (!validFacilitySize) {
            rmStrat.clear();
            hsrStrat.clear();
        }
    }
}
