/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.RLHelper.summary;

import GUI.ATDMHelper.ScenarioSummaryRenderer;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jltrask
 */
public class ScenarioSummaryTableModel extends AbstractTableModel {

    private final String[] columnNames;

    private final ArrayList<ScenarioInfo> scenarioInfos;

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

    public static final int COL_SCEN_IDX = 0;
    public static final int COL_DEMAND_PATTERN = 1;
    public static final int COL_DEMAND_MULTIPLIER = 2;
    public static final int COL_NUM_WORK_ZONES = 3;
    public static final int COL_NUM_WEATHER_EVENTS = 4;
    public static final int COL_NUM_INCIDENTS = 5;
    public static final int COL_MAX_TTI = 6;
    public static final int COL_NUM_BAD_APS = 7;
    public static final int COL_MAX_VHD = 8;

    public ScenarioSummaryTableModel(Seed seed, JTable parentTable) {

        columnNames = new String[]{"<HTML><CENTER>Scenario<br>#", "<HTML><CENTER>Demand<br>Pattern",
            "<HTML><CENTER>Demand<br>Multiplier", "<HTML><CENTER># Work<br>Zones",
            "<HTML><CENTER># Weather<br>Events", "<HTML><CENTER>#<br>Incidents",
            "<HTML><CENTER>Max TTI<br>(15 min)", "<HTML><CENTER># Congested<br>APs", "<HTML><CENTER>Max VHD<br>(15 min)",};

        this.seed = seed;
        this.scenarioInfos = seed.getRLScenarioInfo();

        this.parentTable = parentTable;

        maxTTI = new float[scenarioInfos.size()];
        maxDelay = new float[scenarioInfos.size()];
        fillMaxArrays();

        calculatePTI();
        congestedAPCutOffTTI = pti;
        numBadAPs = new int[scenarioInfos.size()];
        Arrays.fill(numBadAPs, 0);
        calculateNumBadAPs();

        scenarioVisible = new boolean[scenarioInfos.size()];
        allScenariosVisible();

        filters = new ArrayList<>();
        filters.add(new Boolean[getAllValueOptions(COL_NUM_WORK_ZONES) + 1]);
        filters.add(new Boolean[getAllValueOptions(COL_NUM_WEATHER_EVENTS) + 1]);
        filters.add(new Boolean[getAllValueOptions(COL_NUM_INCIDENTS) + 1]);
        for (int i = 0; i < filters.size(); i++) {
            for (int j = 0; j < filters.get(i).length; j++) {
                filters.get(i)[j] = Boolean.TRUE;
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
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
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
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
        return false;
    }

    @Override
    public void fireTableStructureChanged() {
        ScenarioSummaryRenderer atdmSSRend = (ScenarioSummaryRenderer) parentTable.getColumnModel().getColumn(COL_MAX_TTI).getCellRenderer();
        super.fireTableStructureChanged();
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Hiders and Filters">
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
            if ((demandMultRange != null) && !(scenarioInfos.get(scenIdx).getDemandMultiplier() >= demandMultRange[0] && scenarioInfos.get(scenIdx).getDemandMultiplier() <= demandMultRange[1])) {
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
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
        }
    }

    public float getCongestedAPCutOffTTI() {
        return congestedAPCutOffTTI;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    public void setCongestedAPCutOffTTI(float newVal) {
        congestedAPCutOffTTI = newVal;
    }
    //</editor-fold>

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
        return getAllValueOptions(COL_NUM_WORK_ZONES);
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
        return getAllValueOptions(COL_NUM_WEATHER_EVENTS);
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
        return getAllValueOptions(COL_NUM_INCIDENTS);
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
