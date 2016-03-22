package GUI.ATDMHelper.TableModels;

import GUI.ATDMHelper.ATDMScenarioSelectorDialog;
import GUI.ATDMHelper.ScenarioSummaryRenderer;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMPlan;
import coreEngine.atdm.DataStruct.ATDMScenario;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake Trask
 */
public class ATDMModel extends AbstractTableModel {

    //ATDMModel variables
    private final String[] columnNames;

    private final ArrayList<ScenarioInfo> scenarioInfos;

    private final Boolean[] scenarioUsed;

    private final boolean[] scenarioVisible;

    private final ArrayList<Boolean[]> filters;

    private float[] demandMultRange;

    private float[] maxTTIRange;

    private float[] maxDelayRange;

    private final JTable parentTable;

    private final Seed seed;

    private final float[] maxTTI;

    private float maxMaxTTI;

    private float minMaxTTI;

    private final float[] maxDelay;

    private float maxMaxDelay;

    private float minMaxDelay;

    private float pti;

    private final int[] numBadAPs;

    private float congestedAPCutOffTTI;

    private final ATDMPlan[] atdmPlans;

    private final boolean[] isCustom;

    private final ATDMScenarioSelectorDialog atdmDialog;

    public static final int COL_CHECK_BOX = 0;
    public static final int COL_SCEN_IDX = 1;
    public static final int COL_DEMAND_PATTERN = 2;
    public static final int COL_DEMAND_MULTIPLIER = 3;
    public static final int COL_NUM_WORK_ZONES = 4;
    public static final int COL_NUM_WEATHER_EVENTS = 5;
    public static final int COL_NUM_INCIDENTS = 6;
    public static final int COL_MAX_TTI = 7;
    public static final int COL_NUM_BAD_APS = 8;
    public static final int COL_MAX_VHD = 9;
    public static final int COL_ATDM_PLAN = 10;
    public static final int COL_ATDM_PLAN_DESCRIPTION = 11;

    /**
     *
     * @param seed
     * @param atdmDialog
     */
    public ATDMModel(Seed seed, ATDMScenarioSelectorDialog atdmDialog) {

        columnNames = new String[]{"<HTML><CENTER>\u2713<br>&nbsp", "<HTML><CENTER>Scen.<br>#", "<HTML><CENTER>Demand<br>Pattern",
            "<HTML><CENTER>Demand<br>Multiplier", "<HTML><CENTER># Work<br>Zones",
            "<HTML><CENTER># Weather<br>Events", "<HTML><CENTER>#<br>Incidents",
            "<HTML><CENTER>Max TTI<br>(15 min)", "<HTML><CENTER># Cong.<br>APs", "<HTML><CENTER>Max VHD<br>(15 min)",
            "<HTML><CENTER>ATDM<br>Plan", "<HTML><CENTER>Strategies"};

        this.seed = seed;
        this.scenarioInfos = seed.getRLScenarioInfo();

        this.parentTable = atdmDialog.getATDMTable();

        // Creating and filling arrays to hold maximum TTI and delay values for
        // each scenario
        maxTTI = new float[scenarioInfos.size()];
        maxDelay = new float[scenarioInfos.size()];
        fillMaxArrays();

        calculatePTI();
        congestedAPCutOffTTI = pti;
        numBadAPs = new int[scenarioInfos.size()];
        Arrays.fill(numBadAPs, 0);
        calculateNumBadAPs();

        scenarioUsed = new Boolean[scenarioInfos.size()];
        initScenarioUsed();

        scenarioVisible = new boolean[scenarioInfos.size()];
        allScenariosVisible();

        filters = new ArrayList<>();
        filters.add(new Boolean[getAllValueOptions(4) + 1]);
        filters.add(new Boolean[getAllValueOptions(5) + 1]);
        filters.add(new Boolean[getAllValueOptions(6) + 1]);
        for (int i = 0; i < filters.size(); i++) {
            for (int j = 0; j < filters.get(i).length; j++) {
                filters.get(i)[j] = Boolean.TRUE;
            }
        }

        this.atdmPlans = new ATDMPlan[scenarioInfos.size()];
        this.isCustom = new boolean[scenarioInfos.size()];
        //setupATDMPlanArr();

        this.atdmDialog = atdmDialog;

    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < scenarioInfos.size(); ++i) {
            if (scenarioVisible[i]) {
                ++count;
            }
        }
        return count;
    }

    private int convertRowToScenario(int row) {
        int count = 0;
        for (int i = 0; i < scenarioInfos.size(); ++i) {
            if (scenarioVisible[i]) {
                ++count;
                if (count - 1 == row) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     *
     * @return
     */
    public int getTotalRowCount() {
        return scenarioInfos.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length - 1;
    }

    @Override
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case COL_CHECK_BOX:
                return scenarioUsed[convertRowToScenario(rowIndex)];
            case COL_SCEN_IDX:
                return convertRowToScenario(rowIndex);
            case COL_DEMAND_PATTERN:
                if (convertRowToScenario(rowIndex) == 0) {
                    return "Seed";
                } else {
                    return scenarioInfos.get(convertRowToScenario(rowIndex)).name.split(" ")[0];
                }
            case COL_DEMAND_MULTIPLIER:
                return String.format("%.2f", scenarioInfos.get(convertRowToScenario(rowIndex)).getDemandMultiplier());
            case COL_NUM_WORK_ZONES:
                return scenarioInfos.get(convertRowToScenario(rowIndex)).getNumberOfWorkZones();
            case COL_NUM_WEATHER_EVENTS:
                return scenarioInfos.get(convertRowToScenario(rowIndex)).getNumberOfWeatherEvents();
            case COL_NUM_INCIDENTS:
                return scenarioInfos.get(convertRowToScenario(rowIndex)).getNumberOfGPIncidentEvents();
            case COL_MAX_TTI:
                return maxTTI[convertRowToScenario(rowIndex)];
            case COL_NUM_BAD_APS:
                return numBadAPs[convertRowToScenario(rowIndex)];
            case COL_MAX_VHD:
                return maxDelay[convertRowToScenario(rowIndex)];
            case COL_ATDM_PLAN:
                if (atdmPlans[convertRowToScenario(rowIndex)] != null) {
                    return atdmPlans[convertRowToScenario(rowIndex)].getName();
                } else {
                    return "No Plan";
                }
            case COL_ATDM_PLAN_DESCRIPTION:
                if (atdmPlans[convertRowToScenario(rowIndex)] != null) {
                    return atdmPlans[convertRowToScenario(rowIndex)].getDescription();
                } else {
                    return "";
                }
            default:
                return 1;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == COL_CHECK_BOX);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COL_CHECK_BOX) {
            scenarioUsed[convertRowToScenario(row)] = (boolean) value;
            atdmDialog.updateSelectedProbability();
        } else if (col == COL_ATDM_PLAN) {
            if (((String) value).equalsIgnoreCase("No Plan")) {
                atdmPlans[convertRowToScenario(row)] = null;
            } else if (((String) value).equalsIgnoreCase("Custom")) {
                // Do nothing, custom selection not allowed
            } else {
                atdmPlans[convertRowToScenario(row)] = getPlanByName((String) value);
            }
        }
    }

    @Override
    public void fireTableStructureChanged() {
        ScenarioSummaryRenderer atdmSSRend = (ScenarioSummaryRenderer) parentTable.getColumnModel().getColumn(COL_MAX_TTI).getCellRenderer();
        super.fireTableStructureChanged();
        parentTable.getColumnModel().getColumn(COL_CHECK_BOX).setMinWidth(25);                    //Check
        parentTable.getColumnModel().getColumn(COL_CHECK_BOX).setMaxWidth(25);
        parentTable.getColumnModel().getColumn(COL_SCEN_IDX).setMinWidth(50);                    //Scenario Number
        parentTable.getColumnModel().getColumn(COL_SCEN_IDX).setMaxWidth(50);
        parentTable.getColumnModel().getColumn(COL_DEMAND_PATTERN).setMinWidth(75);                    // Demand Pattern Name
        parentTable.getColumnModel().getColumn(COL_DEMAND_PATTERN).setMaxWidth(75);
        for (int colIdx = 3; colIdx < parentTable.getColumnModel().getColumnCount() - 2; colIdx++) {
            parentTable.getColumnModel().getColumn(colIdx).setMinWidth(65);
            parentTable.getColumnModel().getColumn(colIdx).setMaxWidth(65);
        }
        parentTable.getColumnModel().getColumn(parentTable.getColumnModel().getColumnCount() - 2).setMinWidth(85);
        parentTable.getColumnModel().getColumn(parentTable.getColumnModel().getColumnCount() - 2).setMaxWidth(85);

        atdmSSRend.setColorRange(getMinTTI(), getPTI());
        parentTable.getColumnModel().getColumn(COL_MAX_TTI).setCellRenderer(atdmSSRend);
        parentTable.getColumnModel().getColumn(COL_MAX_VHD).setCellRenderer(atdmSSRend);
    }

    @Override
    public void fireTableDataChanged() {
        ScenarioSummaryRenderer atdmSSRend = (ScenarioSummaryRenderer) parentTable.getColumnModel().getColumn(COL_MAX_TTI).getCellRenderer();
        super.fireTableDataChanged();
        atdmSSRend.setColorRange(getMinTTI(), getPTI());
        parentTable.getColumnModel().getColumn(COL_MAX_TTI).setCellRenderer(atdmSSRend);
        parentTable.getColumnModel().getColumn(COL_MAX_VHD).setCellRenderer(atdmSSRend);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Hiders and Filters">
    /**
     *
     */
    public void hideUnselected() {
        scenarioVisible[0] = false;
        for (int i = 1; i < scenarioInfos.size(); i++) {
            scenarioVisible[i] = scenarioUsed[i];
        }
        fireTableStructureChanged();
    }

    /**
     *
     */
    public void hideSelected() {
        scenarioVisible[0] = false;
        for (int i = 1; i < scenarioInfos.size(); i++) {
            scenarioVisible[i] = !scenarioUsed[i];
        }
        fireTableStructureChanged();
    }

    /**
     *
     */
    public void showAllScenarios() {
        Arrays.fill(scenarioVisible, true);
        scenarioVisible[0] = false;
        fireTableDataChanged();
    }

    /**
     *
     * @param col
     * @param value
     * @param show
     */
    public void updateFilter(int col, int value, boolean show) {
        filters.get(col - 4)[value] = show;
    }

    /**
     *
     * @param col
     * @param value
     * @return
     */
    public boolean getFilterOption(int col, int value) {
        switch (col) {
            case 4:
            case 5:
            case 6:
                return filters.get(col - 4)[value];
            default:
                return true;
        }
    }

    /**
     *
     */
    public void applyFilters() {
        boolean show;
        scenarioVisible[0] = false;
        for (int scenIdx = 1; scenIdx < scenarioInfos.size(); scenIdx++) {
            show = true;

            // Demand multiplier filter check
            if ((demandMultRange != null) && (scenarioInfos.get(scenIdx).getDemandMultiplier() >= demandMultRange[0] && scenarioInfos.get(scenIdx).getDemandMultiplier() <= demandMultRange[1])) {
                show = false;
            }

            // Work zone filter check
            if (!filters.get(0)[scenarioInfos.get(scenIdx).getNumberOfWorkZones()]) {
                show = false;
            }

            // Weather events filter check
            if (!filters.get(1)[scenarioInfos.get(scenIdx).getNumberOfWeatherEvents()]) {
                show = false;
            }

            // Incident events filter check
            if (!filters.get(2)[scenarioInfos.get(scenIdx).getNumberOfGPIncidentEvents()]) {
                show = false;
            }

            // Max TTI filter check
            if ((maxTTIRange != null) && !(maxTTI[scenIdx] >= maxTTIRange[0] && maxTTI[scenIdx] <= maxTTIRange[1])) {
                show = false;
            }

            // Max delay filter check
            if ((maxDelayRange != null) && !(maxDelay[scenIdx] >= maxDelayRange[0] && maxDelay[scenIdx] <= maxDelayRange[1])) {
                show = false;
            }

            scenarioVisible[scenIdx] = show;
        }
        fireTableStructureChanged();
    }

    /**
     *
     * @param range
     */
    public void filterByDemandMultRange(float[] range) {
        demandMultRange = range;
    }

    /**
     *
     * @param range
     */
    public void filterByNumberWZRange(int[] range) {
        // Updating filters
        if (range != null) {
            for (int filterVal = 0; filterVal < filters.get(0).length; filterVal++) {
                filters.get(0)[filterVal] = (filterVal >= range[0] && filterVal <= range[1]);
            }
        } else {
            Arrays.fill(filters.get(0), true);
        }
    }

    /**
     *
     * @param range
     */
    public void filterByNumberWeatherEventsRange(int[] range) {
        // Updating filters
        if (range != null) {
            for (int filterVal = 0; filterVal < filters.get(1).length; filterVal++) {
                filters.get(1)[filterVal] = (filterVal >= range[0] && filterVal <= range[1]);
            }
        } else {
            Arrays.fill(filters.get(1), true);
        }
    }

    /**
     *
     * @param range
     */
    public void filterByNumberIncidentsRange(int[] range) {
        // Updating filters
        if (range != null) {
            for (int filterVal = 0; filterVal < filters.get(2).length; filterVal++) {
                filters.get(2)[filterVal] = (filterVal >= range[0] && filterVal <= range[1]);
            }
        } else {
            Arrays.fill(filters.get(2), true);
        }
    }

    /**
     *
     * @param range
     */
    public void filterByMaxTTIRange(float[] range) {
        maxTTIRange = range;
    }

    /**
     *
     * @param range
     */
    public void filterByMaxDelayRange(float[] range) {
        maxDelayRange = range;
    }

    public void updateCongestedTTICutoff() {
        calculateNumBadAPs();
        this.fireTableDataChanged();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     *
     * @return
     */
    public boolean[] getVisibleScenarios() {
        return scenarioVisible;
    }

    /**
     *
     * @param columnIdx
     * @return
     */
    public int getAllValueOptions(int columnIdx) {
        int max = -1;
        int curr = 0;
        if (columnIdx == COL_NUM_WEATHER_EVENTS) {
            for (int idx = 0; idx < scenarioInfos.size(); idx++) {
                curr = scenarioInfos.get(idx).getNumberOfWeatherEvents();
                if (curr > max) {
                    max = curr;
                }
            }
            //return max;
        } else if (columnIdx == COL_NUM_INCIDENTS) {
            for (int idx = 0; idx < scenarioInfos.size(); idx++) {
                curr = scenarioInfos.get(idx).getNumberOfGPIncidentEvents();
                if (curr > max) {
                    max = curr;
                }
            }
        } else if (columnIdx == COL_NUM_WORK_ZONES) {
            for (int idx = 0; idx < scenarioInfos.size(); idx++) {
                curr = scenarioInfos.get(idx).getNumberOfWorkZones();
                if (curr > max) {
                    max = curr;
                }
            }
        }
        return max;
    }

    /**
     *
     * @return
     */
    public float getProbabilityOfDisplayedScenarios() {
        float prob = 0.0f;
        for (int scenIdx = 0; scenIdx < scenarioInfos.size(); scenIdx++) {
            if (scenarioVisible[scenIdx]) {
                prob += scenarioInfos.get(scenIdx).prob;
            }
        }
        return prob;
    }

    /**
     *
     * @return
     */
    public float getProbabilityOfSelectedScenarios() {
        float prob = 0.0f;
        for (int scenIdx = 0; scenIdx < scenarioInfos.size(); scenIdx++) {
            if (scenarioUsed[scenIdx]) {
                prob += scenarioInfos.get(scenIdx).prob;
            }
        }
        return prob;
    }

    private ATDMPlan getPlanByName(String planName) {
        return seed.getATDMDatabase().getPlan(planName);
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    public String getToolTip(int row, int col) {
        switch (col) {
            default:
                return null;
            case COL_SCEN_IDX:
            case COL_DEMAND_PATTERN:
                return scenarioInfos.get(convertRowToScenario(row)).getDetail();
            case COL_DEMAND_MULTIPLIER: // Demand Multiplier
                return "Scenario Probability: " + String.format("%.2f%%", scenarioInfos.get(convertRowToScenario(row)).prob * 100.0f) + " ";
            case COL_NUM_WORK_ZONES: //Work Zone
                //System.out.println(scenarioInfos.get(convertRowToScenario(row)).getWorkZoneDetail());
                return scenarioInfos.get(convertRowToScenario(row)).getWorkZoneDetail();
            case COL_NUM_WEATHER_EVENTS:  // Weather
                return scenarioInfos.get(convertRowToScenario(row)).getWeatherDetail();
            case COL_NUM_INCIDENTS: // incidents
                return scenarioInfos.get(convertRowToScenario(row)).getGPIncidentDetail();
            case COL_ATDM_PLAN: // Strategy detail
                return atdmPlans[convertRowToScenario(row)].getInfo();
        }
    }

    /**
     *
     * @param scenIdx
     * @return
     */
    public ATDMPlan getPlan(int scenIdx) {
        return atdmPlans[scenIdx];
    }

    /**
     *
     * @param scenIdx
     * @return
     */
    public boolean isCustom(int scenIdx) {
        return isCustom[scenIdx];
    }

    /**
     *
     * @return
     */
    public int getSelectedRow() {
        return atdmDialog.getATDMTable().getSelectedRow();
    }

    public float getCongestedAPCutOffTTI() {
        return congestedAPCutOffTTI;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Sets the boolean for scenario scenIdx that indicates if the scenario has
     * a custom plan.
     *
     * @param scenIdx
     * @param newVal
     */
    public void setCustom(int scenIdx, boolean newVal) {
        isCustom[scenIdx] = newVal;
    }

    /**
     *
     * @param scenIdx
     * @param newPlan
     */
    public void setPlan(int scenIdx, ATDMPlan newPlan) {
        atdmPlans[scenIdx] = newPlan;
    }

    /**
     *
     * @param newPlan
     */
    public void setPlanForSelectedRows(ATDMPlan newPlan) {
        int[] selectedRows = atdmDialog.getATDMTable().getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            atdmPlans[convertRowToScenario(parentTable.getRowSorter().convertRowIndexToModel(selectedRows[i]))] = newPlan;
        }
    }

    public void setCongestedAPCutOffTTI(float newVal) {
        congestedAPCutOffTTI = newVal;
    }

    // </editor-fold>
    private void initScenarioUsed() {
        for (int count = 0; count < scenarioUsed.length; count++) {
            scenarioUsed[count] = Boolean.FALSE;
        }
    }

    private void allScenariosVisible() {
        scenarioVisible[0] = false;
        for (int i = 1; i < scenarioInfos.size(); i++) {
            scenarioVisible[i] = true;
        }
    }

    private void calculatePTI() {
        ArrayList<RLResult> RLResults = new ArrayList<>();

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD);
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                RLResults.add(new RLResult(prob, TTI));
                //mean += TTI * prob;
                //semiSTD += (TTI - 1) * (TTI - 1) * prob;
                //if (TTI < 1.333333f) {
                //    ratingCount += prob;
                //}
                //if (TTI > 2) {
                //    VMT2Count += prob;
                //}
            }
        }
        Collections.sort(RLResults);

        float probCount = 0;
        for (RLResult RLResult : RLResults) {

            //find 95th %
            if (probCount <= 0.95 && probCount + RLResult.prob >= 0.95) {
                pti = RLResult.TTI;
                break;
            }

            probCount += RLResult.prob;

        }
    }

    private void calculateNumBadAPs() {
        for (int scen = 0; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            numBadAPs[scen] = 0;
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                if (TTI >= congestedAPCutOffTTI) {
                    numBadAPs[scen] += 1;
                }
            }
        }
    }

    /**
     *
     * @param b
     */
    public void checkAllVisible(boolean b) {
        for (int scenIdx = 0; scenIdx < scenarioInfos.size(); scenIdx++) {
            if (scenarioVisible[scenIdx]) {
                scenarioUsed[scenIdx] = b;
            }
        }
        fireTableDataChanged();
    }

    /**
     *
     * @param b
     * @param highlighted
     */
    public void toggleHighlightedScenarioSelection(boolean b, int[] highlighted) {
        for (int scenIdx = 0; scenIdx < highlighted.length; scenIdx++) {
            scenarioUsed[convertRowToScenario(highlighted[scenIdx])] = b;
        }
        fireTableDataChanged();
    }

    /**
     *
     * @param planName
     * @param highlighted
     */
    public void setGroupPlan(String planName, int[] highlighted) {
        ATDMPlan plan = getPlanByName(planName);
        for (int scenIdx = 0; scenIdx < highlighted.length; scenIdx++) {
            int convertedIdx = convertRowToScenario(highlighted[scenIdx]);
            atdmPlans[convertedIdx] = plan;
            isCustom[convertedIdx] = false;
        }
        fireTableDataChanged();
    }

    /**
     *
     * @param selectedRow
     */
    public void setSelectedRow(int selectedRow) {
        atdmDialog.getATDMTable().setRowSelectionInterval(selectedRow, selectedRow);
    }

    /**
     *
     * @param oldName
     * @param newName
     */
    public void comboBoxUpdated(String oldName, String newName) {
        fireTableDataChanged();
    }

    /**
     *
     */
    public void applyPlansToScenarioInfos() {

        HashMap<Integer, ATDMScenario[]> atdmSet = new HashMap<>();

        for (int scenIdx = 0; scenIdx < scenarioInfos.size(); scenIdx++) {
            if (scenarioUsed[scenIdx] && atdmPlans[scenIdx] != null) {
                ATDMScenario[] tempATDMArr = new ATDMScenario[2];
                tempATDMArr[0] = scenarioInfos.get(scenIdx).generateATDMScenario(atdmPlans[scenIdx]);
                if (seed.isManagedLaneUsed()) {
                    tempATDMArr[1] = scenarioInfos.get(scenIdx).generateATDMScenarioML(atdmPlans[scenIdx]);
                }
                atdmSet.put(scenIdx, tempATDMArr);
            }
        }

        // Adding atdm set hashmap to seed
        if (atdmSet.size() > 0) {
            seed.addATDMSet(atdmSet);
        }
    }

    private void fillMaxArrays() {
        float candTTI, candDelay;
        maxMaxTTI = 0.0f;
        minMaxTTI = 10e20f;
        maxMaxDelay = 0.0f;
        minMaxDelay = 10e20f;

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                // TTI testing
                candTTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                if (candTTI > maxTTI[scen]) {
                    maxTTI[scen] = candTTI;
                }

                // Delay testing
                candDelay = seed.getValueFloat(CEConst.IDS_P_VHD, 0, period, scen, -1);
                if (candDelay > maxDelay[scen]) {
                    maxDelay[scen] = candDelay;
                }
            }
            if (maxTTI[scen] < minMaxTTI) {
                minMaxTTI = maxTTI[scen];
            } else if (maxTTI[scen] > maxMaxTTI) {
                maxMaxTTI = maxTTI[scen];
            }

            if (maxDelay[scen] < minMaxDelay) {
                minMaxDelay = maxDelay[scen];
            } else if (maxDelay[scen] > maxMaxDelay) {
                maxMaxDelay = maxDelay[scen];
            }
        }
    }

    /**
     *
     * @return
     */
    public float getMaxTTI() {
        return maxMaxTTI;
    }

    /**
     *
     * @return
     */
    public float getPTI() {
        return pti;
    }

    /**
     *
     * @return
     */
    public float getMinTTI() {
        return minMaxTTI;
    }

    /**
     *
     * @return
     */
    public float getMaxDelay() {
        return maxMaxDelay;
    }

    /**
     *
     * @return
     */
    public float getMinDelay() {
        return minMaxDelay;
    }

    /**
     *
     * @return
     */
    public float getMaxDemandMult() {
        float maxDemandMult = -1.0f;
        for (ScenarioInfo scenarioInfo : scenarioInfos) {
            if (scenarioInfo.getDemandMultiplier() > maxDemandMult) {
                maxDemandMult = scenarioInfo.getDemandMultiplier();
            }
        }
        return maxDemandMult;
    }

    /**
     *
     * @return
     */
    public float getMinDemandMult() {
        float minDemandMult = 10e20f;
        for (ScenarioInfo scenarioInfo : scenarioInfos) {
            if (scenarioInfo.getDemandMultiplier() < minDemandMult) {
                minDemandMult = scenarioInfo.getDemandMultiplier();
            }
        }
        return minDemandMult;
    }

    /**
     *
     * @return
     */
    public int getMinNumWZ() {
        return 0;
    }

    /**
     *
     * @return
     */
    public int getMaxNumWZ() {
        return getAllValueOptions(4);
    }

    /**
     *
     * @return
     */
    public int getMinNumWeather() {
        return 0;
    }

    /**
     *
     * @return
     */
    public int getMaxNumWeather() {
        return getAllValueOptions(5);
    }

    /**
     *
     * @return
     */
    public int getMinNumIncident() {
        return 0;
    }

    /**
     *
     * @return
     */
    public int getMaxNumIncident() {
        return getAllValueOptions(6);
    }

    /**
     *
     * @return
     */
    public boolean runCheck() {

        String warning = "<HTML>";
        boolean firstErrorNoScenariosSelected = true;
        boolean firstErrorNullPlan = false;
        boolean firstErrorNotSelected = false;

        for (int scenIdx = 0; scenIdx < scenarioInfos.size(); scenIdx++) {
            firstErrorNoScenariosSelected = (firstErrorNoScenariosSelected && scenarioUsed[scenIdx]
                    ? false : firstErrorNoScenariosSelected);
            if (scenarioUsed[scenIdx] && atdmPlans[scenIdx] == null) {
                if (!firstErrorNullPlan) {
                    firstErrorNullPlan = true;
                }
            } else if ((!scenarioUsed[scenIdx]) && atdmPlans[scenIdx] != null) {
                if (!firstErrorNotSelected) {
                    firstErrorNotSelected = true;
                }
            }

        }

        if (firstErrorNoScenariosSelected) {
            warning = warning + "Warning: No Scenarios Selected for the ATDM Analysis.<br>";
        }
        if (firstErrorNullPlan) {
            warning = warning + "Warning: Some selected scenarios have no plan assigned to them.<br>";
        }
        if (firstErrorNotSelected) {
            warning = warning + "Warning: Some scenarios with a plan assigned to them are not selected for the ATDM set.";
        }

        if (firstErrorNoScenariosSelected || firstErrorNullPlan || firstErrorNotSelected) {
            return (JOptionPane.showConfirmDialog(atdmDialog, warning, "Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION);
        } else {
            return true;
        }
    }

    /**
     *
     * @param atdmPlan
     * @return
     */
    public ArrayList<Integer> checkPlan(ATDMPlan atdmPlan) {
        ArrayList<Integer> conflicts = new ArrayList<>();
        for (int scenIdx = 0; scenIdx < atdmPlans.length; scenIdx++) {
            if (atdmPlans[scenIdx] == atdmPlan) {
                conflicts.add(scenIdx);
            }
        }
        return conflicts;
    }

    /**
     *
     * @param atdmPlan
     */
    public void removePlan(ATDMPlan atdmPlan) {
        for (int scenIdx = 0; scenIdx < atdmPlans.length; scenIdx++) {
            if (atdmPlans[scenIdx] == atdmPlan) {
                atdmPlans[scenIdx] = null;
            }
        }
    }

    /**
     *
     */
    public void removeAllPlans() {
        for (int scenIdx = 0; scenIdx < atdmPlans.length; scenIdx++) {
            atdmPlans[scenIdx] = null;
        }
        fireTableDataChanged();
    }

    private class RLResult implements Comparable {

        float TTI;

        float prob;

        /**
         * Constructor
         *
         * @param prob probability of this result
         * @param TTI travel time index of this result
         */
        public RLResult(float prob, float TTI) {
            this.prob = prob;
            this.TTI = TTI;
        }

        @Override
        public int compareTo(Object o) {
            return Float.compare(TTI, ((RLResult) o).TTI);
        }
    }

}
