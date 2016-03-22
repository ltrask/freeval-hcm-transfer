package coreEngine.atdm.DataStruct;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Lake Trask
 */
public class ATDMStrategy implements Serializable, Cloneable {

    /**
     * Description of the ATDM Strategy
     */
    private String description;

    /**
     * ATDM Strategy ID Number
     */
    private int id;

    /**
     * ATDM Strategy Type/Category. Indicates Demand, Weather, Incident, Work
     * Zone.
     */
    private String category;

    /**
     * 1D float array of adjustment factors associated with the strategy.
     * Ordered as Demand Adjustment Factor (OAF and DAF), Free Flow Speed
     * Adjustment Factor (SAF), and Capacity Adjustment Factor (CAF).
     */
    private float[] adjFactors;

    /**
     * Incident duration adjustment associated with the strategy (Incident
     * Category Only).
     */
    private int incidentDurationReduction;

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 24865713845L;

    /**
     * List of Strategy Subtypes.
     */
    private static final String[] categoriesString = {"Control Strategy", "Advisory Strategy", "Treatment Strategy",
        "Site Management & Traffic Control", "Detection & Verification",
        "Quick Clearance & Recovery"};

    /**
     * Constructor to create a basic ATDM Strategy Object. This object
     * represents a category (or type) that can be Demand, Weather, Incident, or
     * Work Zone. For Ramp Metering or Hard Shoulder Running, an ATDMStrategyMat
     * object must be used.
     *
     * @param id Integer index of the strategy.
     * @param description String description of the strategy.
     */
    public ATDMStrategy(int id, String description) {

        this.id = id;
        this.description = description;
        this.category = categoriesString[0];

        this.adjFactors = new float[3];
        Arrays.fill(adjFactors, 1.0f);
        incidentDurationReduction = 0;

    }

    /**
     * Constructor to create a basic ATDM Strategy Object. This object
     * represents a category (or type) that can be Demand, Weather, Incident, or
     * Work Zone. For Ramp Metering, an ATDMStrategyMat object must be used.
     *
     * @param id Integer index of the strategy.
     * @param description String description of the strategy.
     * @param categoryType Category of the Object (use CEConst identifier).
     */
    public ATDMStrategy(int id, String description, int categoryType) {

        this.id = id;
        this.description = description;
        this.category = categoriesString[categoryType];

        this.adjFactors = new float[4];
        Arrays.fill(adjFactors, 1.0f);

    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Getter for the ATDM Strategy Description
     *
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the ATDM Strategy ID
     *
     * @return int ID
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the ATDM Strategy Category
     *
     * @return String ID (from CEConst)
     */
    public String getCategory() {
        return category;
    }

    /**
     * Getter for the ATDM Strategy adjustment factor array.
     *
     * @return 1D array of float adjustment factors (DAF/OAF, SAF, CAF)
     */
    public float[] getAdjFactor() {
        return adjFactors;
    }

    /**
     * Getter for the ATDM Strategy adjustment factor by index.
     *
     * @param afIdx 0 - DAF/OAF, 1 - SAF, 2 - CAF
     * @return Float adjustment factor
     */
    public float getAdjFactor(int afIdx) {
        return adjFactors[afIdx];
    }

    /**
     * Getter for the ATDM Strategy Duration Reduction (Incident Category Only).
     *
     * @return Integer number of minutes an incident is reduced by the strategy.
     */
    public int getIncidentDurationReduction() {
        return incidentDurationReduction;
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Setter for the ATDM Strategy Description.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the ATDM Strategy ID.
     *
     * @param id new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter for the ATDM Strategy Category
     *
     * @param category new Category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Setter for the ATDM Strategy Subtype
     *
     * @param categoryIdx new Category Index
     */
    public void setCategory(int categoryIdx) {
        this.category = categoriesString[categoryIdx];
    }

    /**
     * Setter for the ATDM Strategy Adjustment Factor Array
     *
     * @param adjFactors new Adjustment Factor Array.
     */
    public void setAdjFactors(float[] adjFactors) {
        this.adjFactors = adjFactors;
    }

    /**
     * Setter for the ATDM Strategy adjustment factors by index
     *
     * @param newVal New adjustment factor value
     * @param afIdx 0 - DAF/OAF, 1 - SAF, 2 - CAF
     */
    public void setAdjFactor(float newVal, int afIdx) {
        this.adjFactors[afIdx] = newVal;
    }

    /**
     * Setter for the ATDM Strategy Incident Duration Reduction (in minutes).
     *
     * @param incidentDurationReduction new Duration reduction (in minutes).
     */
    public void setIncidentDurationReduction(int incidentDurationReduction) {
        this.incidentDurationReduction = incidentDurationReduction;
    }

//</editor-fold>
    @Override
    public ATDMStrategy clone() throws CloneNotSupportedException {
        return (ATDMStrategy) super.clone();
    }

}
