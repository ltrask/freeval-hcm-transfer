package GUI.ATDMHelper.TableModels;

import java.util.ArrayList;

/**
 *
 * @author Lake Trask
 */
public class ATDMStrategyData {

    private ArrayList<String> description;

    private ArrayList<Integer> id;

    private ArrayList<String> category;

    private final String[] categoriesString = {"Control Strategy", "Advisory Strategy", "Treatment Strategy",
        "Site Management & Traffic Control", "Detection & Verification",
        "Quick Clearance & Recovery"};

    private ArrayList<float[]> adjFactors;

    private int numAFs;

    /**
     *
     * @param strategyType
     */
    public ATDMStrategyData(int strategyType) {
        description = new ArrayList<>();
        id = new ArrayList<>();
        category = new ArrayList<>();
        adjFactors = new ArrayList<>();

        switch (strategyType) {
            case 0:
                numAFs = 1;
                initDemandDefaultStrategies();
                break;
            case 1:
                numAFs = 3;
                initWeatherDefaultStrategies();
                break;
            case 3:
                numAFs = 3;
                initWorkZoneDefaultStrategies();
                break;
            case 2:
                numAFs = 4;
                initIncidentDefaultStrategies();
                break;
            default:
                numAFs = 1;
                initDefaultStrategies();
                break;
        }
    }

    /**
     *
     */
    public void addStrategy() {
        id.add(id.size() + 1);
        description.add("Please add your description");
        category.add("Control Strategy");
        float[] tempArr = new float[numAFs];
        for (int i = 0; i < numAFs; i++) {
            tempArr[i] = 1.0f;
        }
        adjFactors.add(tempArr);
    }

    /**
     *
     * @param description
     * @param categoryIdx
     */
    public void addStrategy(String description, int categoryIdx) {
        id.add(id.size() + 1);
        this.description.add(description);
        this.category.add(categoriesString[categoryIdx]);
        float[] tempArr = new float[numAFs];
        for (int i = 0; i < numAFs; i++) {
            tempArr[i] = 1.0f;
        }
        adjFactors.add(tempArr);
    }

    /**
     *
     * @param stratIdx
     */
    public void removeStrategy(int stratIdx) {
        id.remove(stratIdx);
        description.remove(stratIdx);
        category.remove(stratIdx);
        adjFactors.remove(stratIdx);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     *
     * @return
     */
    public int getNumberOfStrategies() {
        return id.size();
    }

    /**
     *
     * @param stratIdx
     * @return
     */
    public int getId(int stratIdx) {
        return id.get(stratIdx);
    }

    /**
     *
     * @param stratIdx
     * @return
     */
    public String getDescription(int stratIdx) {
        return description.get(stratIdx);
    }

    /**
     *
     * @param stratIdx
     * @return
     */
    public String getCategory(int stratIdx) {
        return category.get(stratIdx);
    }

    /**
     *
     * @param stratIdx
     * @return
     */
    public float[] getAFs(int stratIdx) {
        return adjFactors.get(stratIdx);
    }

    /**
     *
     * @param stratIdx
     * @param afIdx
     * @return
     */
    public float getAFs(int stratIdx, int afIdx) {
        return adjFactors.get(stratIdx)[afIdx];
    }

    /**
     *
     * @return
     */
    public int getNumAFs() {
        return numAFs;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     *
     * @param stratIdx
     * @param newValue
     */
    public void setId(int stratIdx, int newValue) {
        id.set(stratIdx, newValue);
    }

    /**
     *
     * @param stratIdx
     * @param newValue
     */
    public void setDescription(int stratIdx, String newValue) {
        description.set(stratIdx, newValue);
    }

    /**
     *
     * @param stratIdx
     * @param newValue
     */
    public void setCategory(int stratIdx, String newValue) {
        category.set(stratIdx, newValue);
    }

    /**
     *
     * @param stratIdx
     * @param newValues
     */
    public void setAFs(int stratIdx, float[] newValues) {
        adjFactors.set(stratIdx, newValues);
    }

    /**
     *
     * @param stratIdx
     * @param afIdx
     * @param newValue
     */
    public void setAFs(int stratIdx, int afIdx, float newValue) {
        adjFactors.get(stratIdx)[afIdx] = newValue;
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Defaults">
    private void initDemandDefaultStrategies() {
        addStrategy("HOT Lane - Static Toll", 0);
        addStrategy("HOT Lane - Congestion Price", 0);
        addStrategy("Full Facility Static Toll", 0);
        addStrategy("Full Facility Dynamic Congestion Pricing", 0);
        addStrategy("Pre-Trip Traveler Info", 1);
        addStrategy("Variable Message Signs", 1);
        addStrategy("Employer TDM", 1);
    }

    private void initWeatherDefaultStrategies() {
        addStrategy("Vehicle Restrictions (chain controls)", 0);
        addStrategy("Pre-trip Travel Info", 1);
        addStrategy("Variable Message Signs", 1);
        addStrategy("Employer TDM", 1);
        addStrategy("Snow Removal", 2);
        addStrategy("Anit-Icing", 2);
        addStrategy("Fog Dispersion", 2);
    }

    private void initIncidentDefaultStrategies() {
        addStrategy("Incident Command System", 3);
        addStrategy("Traffic Control With On-Site Traffic Management Teams", 3);
        addStrategy("End-of-Queue Advance Warnign Systems", 3);
        addStrategy("Pre-trip Travel Info", 1);
        addStrategy("Variable Message Signs", 1);
        addStrategy("Portable Message Signs", 1);
        addStrategy("Employer TDM", 1);
        addStrategy("Field Verifciation by On-Site Responders", 4);
        addStrategy("Closed-Circuit Television Cameras", 4);
        addStrategy("Frequent/Enhanced Roadway Reference Markers", 4);
        addStrategy("Enhanced 911/Automated Positioning Systems", 4);
        addStrategy("Motorist Aid Call Boxes", 4);
        addStrategy("Automated Collision Notification Systems", 4);
    }

    private void initWorkZoneDefaultStrategies() {
        addStrategy("End-of-Queue Advance Warnign Systems", 3);
        addStrategy("Speed Feedback Signs", 3);
        addStrategy("Automated Speed Enforcement", 3);
        addStrategy("Pre-trip Travel Info", 1);
        addStrategy("Variable Message Signs", 1);
        addStrategy("Portable Message Signs", 1);
        addStrategy("Employer TDM", 1);
        addStrategy("Detours", 1);
    }

    private void initDefaultStrategies() {
        addStrategy();
    }

    // </editor-fold>
}
