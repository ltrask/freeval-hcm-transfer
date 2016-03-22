package GUI.ATDMHelper.TableModels;

import java.util.ArrayList;

/**
 *
 * @author Lake Trask
 */
public class ATDMPlanData {

    private final ArrayList<String> planNames;

    private final ArrayList<Boolean[]> planInfo;

    private final javax.swing.JComboBox planCB;

    /**
     *
     * @param planCB
     */
    public ATDMPlanData(javax.swing.JComboBox planCB) {
        planNames = new ArrayList();
        planInfo = new ArrayList();
        this.planCB = planCB;
    }

    /**
     * Adds a blank plan to the data structure
     */
    public void addPlan() {
        addPlan("Plan " + (planNames.size() + 1), new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
        planCB.addItem(planNames.get(planNames.size() - 1));
    }

    /**
     *
     * @param name
     * @param strategySet
     */
    public void addPlan(String name, Boolean[] strategySet) {
        planNames.add(name);
        planInfo.add(strategySet);
    }

    /**
     *
     * @return
     */
    public int getNumberOfPlans() {
        return planNames.size();
    }

    /**
     *
     * @param planNum
     * @return
     */
    public String getPlanName(int planNum) {
        return planNames.get(planNum);
    }

    /**
     *
     * @param planNum
     * @return
     */
    public Boolean[] getPlanInfo(int planNum) {
        return planInfo.get(planNum);
    }

    /**
     *
     * @param planName
     * @return
     */
    public Boolean[] getPlanInfo(String planName) {
        return getPlanInfo(planNames.indexOf(planName));
    }

    /**
     *
     * @param planNum
     * @param newName
     */
    public void setPlanName(int planNum, String newName) {
        planNames.set(planNum, newName);
        //planCB.setModel(new DefaultComboBoxModel(planNames.toArray()));
        planCB.insertItemAt(newName, planNum + 1);
        planCB.removeItemAt(planNum + 2);
    }

    /**
     *
     * @param planNum
     * @param strategyNum
     * @param newValue
     */
    public void setPlanInfo(int planNum, int strategyNum, Boolean newValue) {
        planInfo.get(planNum)[strategyNum] = newValue;
    }
}
