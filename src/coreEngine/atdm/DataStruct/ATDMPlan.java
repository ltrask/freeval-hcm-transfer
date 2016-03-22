package coreEngine.atdm.DataStruct;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Lake Trask
 */
public class ATDMPlan implements Serializable, Cloneable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7234909154855844L;

    /**
     * ATDM plan ID
     */
    private int id;

    /**
     * ATDM plan name
     */
    private String name;

    /**
     * ATDM plan description
     */
    private String description;

    /**
     * Strategies contained in this ATDM plan
     */
    private HashMap<ATDMStrategy, String> strategies;

    /**
     * Whether this ATDM plan has ramp metering
     */
    private boolean hasRampMetering = false;

    /**
     * Whether this ATDM plan has shoulder opening
     */
    private boolean hasShoulderOpening = false;

    /**
     * Constructor of an empty ATDM plan
     *
     * @param id ATDM plan ID
     * @param name ATDM plan name
     */
    public ATDMPlan(int id, String name) {

        this.id = id;
        this.name = name;
        this.description = "";

        this.strategies = new HashMap<>();
    }

    /**
     * Creates a new ATDMPlan instance with all the strategies of the plan
     * specified in the constructor.
     *
     * @param id ATDM plan ID
     * @param name ATDM plan name
     * @param basePlan ATDM plan to be copied from
     */
    public ATDMPlan(int id, String name, ATDMPlan basePlan) {
        this.id = id;
        this.name = name;
        this.description = "";

        copyStrategies(basePlan);
    }

    /**
     * Add a strategy to the plan.
     *
     * @param strategyType ATDM Strategy Type
     * (CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, CEConst.IDS_ATDM_STRAT_TYPE_WEATHER,
     * etc.).
     * @param newStrat New ATDM Strategy
     */
    public void addStrategy(String strategyType, ATDMStrategy newStrat) {
        if (strategies.containsKey(newStrat) == false) {
            strategies.put(newStrat, strategyType);
        }
    }

    /**
     * Remove a strategy from the plan.
     *
     * @param strat ATDMStrategy object to be removed.
     */
    public void removeStrategy(ATDMStrategy strat) {
        if (strategies.containsKey(strat)) {
            strategies.remove(strat);
        }
    }

    /**
     * Copy the strategies from an existing separate ATDM Plan object. This sets
     * the strategies of the current ATDMPlan to be clones of those of the plan
     * being passed to the method.
     *
     * @param basePlan
     */
    private void copyStrategies(ATDMPlan basePlan) {
        strategies = (HashMap<ATDMStrategy, String>) basePlan.getAppliedStrategies().clone();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Getter for the ATDM Plan Name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the ATDM Plan Description
     *
     * @return String Description
     */
    public String getDescription() {
        String descD = "";
        String descW = "";
        String descI = "";
        String descWZ = "";
        String descRM = "Ramp Metering: ";
        String descSO = "";
        for (ATDMStrategy strategy : strategies.keySet()) {
            switch (strategies.get(strategy)) {
                case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                    descD = descD + "DM:" + strategy.getDescription() + ",";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                    descW = descD + "WM:" + strategy.getDescription() + ",";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                    descI = descD + "IM:" + strategy.getDescription() + ",";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                    descWZ = descD + "WZM:" + strategy.getDescription() + ",";
                    break;
            }
        }

        if (hasRampMetering) {
            descRM = descRM + "On,";
        } else {
            descRM = descRM + "Off,";
        }

        if (hasShoulderOpening) {
            descSO = "Shoulder Opening,";
        }

        description = descD + descW + descI + descWZ + descRM + descSO;
        return description;
    }

    /**
     * Getter for the ATDM Plan Info
     *
     * @return String info
     */
    public String getInfo() {
        String descD = "";
        String descW = "";
        String descI = "";
        String descWZ = "";
        String descRM = "Ramp Metering: ";
        String descSO = "";
        for (ATDMStrategy strategy : strategies.keySet()) {
            switch (strategies.get(strategy)) {
                case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                    descD = descD + "DM:" + strategy.getDescription() + "\n";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                    descD = descD + "WM:" + strategy.getDescription() + "\n";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                    descD = descD + "IM:" + strategy.getDescription() + "\n";
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                    descD = descD + "WZM:" + strategy.getDescription() + "\n";
                    break;
            }
        }

        if (hasRampMetering) {
            descRM = descRM + "On\n";
        } else {
            descRM = descRM + "Off\n";
        }

        if (hasShoulderOpening) {
            descSO = "Shoulder Opening\n";
        }

        return name + ":\n" + descD + descW + descI + descWZ + descRM + descSO;
    }

    /**
     * Getter for the ATDM Plan ID
     *
     * @return int ID
     */
    public int getID() {
        return this.id;
    }

    /**
     * Getter for the ATDM Plan's strategies
     *
     * @return HashMap of the applied strategies.
     */
    public HashMap<ATDMStrategy, String> getAppliedStrategies() {
        return strategies;
    }

    /**
     * Getter for the ATDM Plan's combined adjustment factors for demand,
     * weather, incident, and work zones strategies. Returns a 4 by 3 array of
     * floats, where the first row consists of the adjustment factors for DAF,
     * SAF, and CAF computed by combining all demand based ATDM strategies. The
     * second, third, and fourth rows correspond to weather, incident, and work
     * zone ATDM strategies, respectively.
     *
     * @return 2D Array of Floats representing the combined ATDM Plan Adjustment
     * Factors.
     */
    public float[][] getATDMadjFactors() {
        float[][] afArray = new float[4][3];
        Arrays.fill(afArray[0], 1.0f);
        Arrays.fill(afArray[1], 1.0f);
        Arrays.fill(afArray[2], 1.0f);
        Arrays.fill(afArray[3], 1.0f);

        for (ATDMStrategy strategy : strategies.keySet()) {
            switch (strategies.get(strategy)) {
                case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                    for (int afIdx = 0; afIdx < afArray[0].length; afIdx++) {
                        afArray[0][afIdx] *= strategy.getAdjFactor(afIdx);
                    }
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                    for (int afIdx = 0; afIdx < afArray[0].length; afIdx++) {
                        afArray[1][afIdx] *= strategy.getAdjFactor(afIdx);
                    }
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                    for (int afIdx = 0; afIdx < afArray[0].length; afIdx++) {
                        afArray[2][afIdx] *= strategy.getAdjFactor(afIdx);
                    }
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                    for (int afIdx = 0; afIdx < afArray[0].length; afIdx++) {
                        afArray[3][afIdx] *= strategy.getAdjFactor(afIdx);
                    }
                    break;
            }
        }
        return afArray;
    }

    /**
     * Returns the incident duration computed from applying all existing
     * Incident ATDM Strategies assigned to the plan.
     *
     * @return Integer number of 15 minute analysis periods an incident is
     * reduced by strategies of the plan.
     */
    public int getIncidentDurationReduction() {
        int durAdj = 0;
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT.equalsIgnoreCase(strategies.get(strategy))) {
                durAdj += (strategy.getIncidentDurationReduction() / 15);
            }
        }
        return durAdj;
    }

    /**
     * Getter for the combined Ramp Metering rate Matrix of the Ramp Metering
     * ATDM strategies assigned to the plan.
     *
     * @return Ramp Metering Rate Matrix
     */
    public CA2DInt getRMRate() {
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING.equalsIgnoreCase(strategies.get(strategy))) {
                return ((ATDMStrategyMat) strategy).getStrategyMatrix();
            }
        }
        return null;
    }

    /**
     * Getter for the combined Hard Shoulder Running Matrix of the Hard Shoulder
     * ATDM strategies assigned to the plan.
     *
     * @return Hard Shoulder Running Matrix
     */
    public CA2DInt getHSRMatrix() {
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING.equalsIgnoreCase(strategies.get(strategy))) {
                return ((ATDMStrategyMat) strategy).getStrategyMatrix();
            }
        }
        return null;
    }

    /**
     * Getter for the combined Hard Shoulder Running CAF of the Hard Shoulder
     * ATDM strategies assigned to the plan.
     *
     * @return Float array hard should capacity adjustment factors.
     */
    public float[] getHSRCAF() {
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING.equalsIgnoreCase(strategies.get(strategy))) {
                return ((ATDMStrategyMat) strategy).getShoulderCapacity();
            }
        }
        throw new RuntimeException("Error: No HSR Strategy assigned to plan");
    }

    /**
     * Getter for the combined Hard Shoulder Running CAF based on the number of
     * lanes of the segment of the Hard Shoulder ATDM strategies assigned to the
     * plan.
     *
     * @param numLanes Number of lanes
     * @return Float array hard should capacity adjustment factors.
     */
    public float getHSRCAF(int numLanes) {
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING.equalsIgnoreCase(strategies.get(strategy))) {
                return ((ATDMStrategyMat) strategy).getShoulderCapacity(numLanes);
            }
        }
        throw new RuntimeException("Error: No HSR Strategy assigned to plan");
    }

    /**
     * Getter for the combined capacity increase due to ramp metering for the
     * ramp metering ATDM strategies assigned to the plan.
     *
     * @return Float Capacity Increase due to Ramp Metering.
     */
    public float getCapacityIncreaseDueToRM() {
        for (ATDMStrategy strategy : strategies.keySet()) {
            if (CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING.equalsIgnoreCase(strategies.get(strategy))) {
                return ((ATDMStrategyMat) strategy).getCapacityIncreaseDueToRM();
            }
        }
        throw new RuntimeException("Error: No Ramp Metering Strategy assigned to plan");
    }

    /**
     * Method to check if a strategy has been assigned to the plan.
     *
     * @param strategy Strategy to search for.
     * @return True if the strategy has been assigned to the plan, false
     * otherwise
     */
    public boolean hasStrategy(ATDMStrategy strategy) {
        for (ATDMStrategy strat : strategies.keySet()) {
            if (strat == strategy) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the ATDM plan has ramp metering.
     *
     * @return True if the plan has ramp metering, false otherwise.
     */
    public boolean hasRampMetering() {
        return hasRampMetering;
    }

    /**
     * Checks if the ATDM plan has hard shoulder opening.
     *
     * @return True if the plan has hard shoulder running, false otherwise.
     */
    public boolean hasShoulderOpening() {
        return hasShoulderOpening;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Setter for the ATDM Plan ID
     *
     * @param newID New Plan ID
     */
    public void setID(int newID) {
        this.id = newID;
    }

    /**
     * Setter for the ATDM Plan Name
     *
     * @param newName New Plan Name
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Setter for the ATDM Plan Description
     *
     * @param newDescription New Plan Description
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Setter for indicating if the plan has Ramp Metering.
     *
     * @param val True if the plan has ramp metering, false otherwise.
     */
    public void useRampMetering(boolean val) {
        hasRampMetering = val;
    }

    /**
     * Setter for indicating if the plan has hard shoulder running.
     *
     * @param val True if the plan has hard shoulder running, false otherwise.
     */
    public void useShoulderOpening(boolean val) {
        hasShoulderOpening = val;
    }
    //</editor-fold>

    /**
     * Returns a deep copy of the ATDMPlan object. Same as clone (overridden and
     * implemented to be a deep copy in the source code).
     *
     * @return
     */
    public ATDMPlan getDeepCopy() {
        try {
            return this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public ATDMPlan clone() throws CloneNotSupportedException {
        ATDMPlan cloned = (ATDMPlan) super.clone();
        HashMap<ATDMStrategy, String> tempHash = (HashMap<ATDMStrategy, String>) cloned.getAppliedStrategies().clone();
        for (ATDMStrategy strat : tempHash.keySet()) {
            tempHash.put(strat.clone(), tempHash.remove(strat));
        }
        return cloned;
    }

}
