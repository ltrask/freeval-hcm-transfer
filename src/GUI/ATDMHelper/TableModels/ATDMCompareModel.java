package GUI.ATDMHelper.TableModels;

import coreEngine.Helper.FacilitySummary;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake Trask
 */
public class ATDMCompareModel extends AbstractTableModel {

    //private final ATDMDatabase atdmDatabase;
    private final ArrayList<ScenarioInfo> scenarioInfos;

    private final JTable parentTable;

    private final Seed seed;

    private FacilitySummary baseScen;

    private final ArrayList<FacilitySummary> atdmProperties;

    private String selectedScenarioName;

    /**
     *
     * @param seed
     * @param parentTable
     */
    public ATDMCompareModel(Seed seed, JTable parentTable) {

        this.atdmProperties = new ArrayList<>();
        this.seed = seed;
        //this.atdmDatabase = seed.getATDMDatabase();
        this.parentTable = parentTable;
        scenarioInfos = this.seed.getRLScenarioInfo();

        baseScen = new FacilitySummary(seed, 0);
        selectedScenarioName = "Seed";

    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            switch (row) {
                case 0:
                    return FacilitySummary.HEADER_actualTravelTime;
                case 1:
                    return FacilitySummary.HEADER_VMTD;
                case 2:
                    return FacilitySummary.HEADER_VMTV;
                case 3:
                    return FacilitySummary.HEADER_PMTD;
                case 4:
                    return FacilitySummary.HEADER_PMTV;
                case 5:
                    return FacilitySummary.HEADER_VHT;
                case 6:
                    return FacilitySummary.HEADER_VHD;
                case 7:
                    return FacilitySummary.HEADER_spaceMeanSpeed;
                case 8:
                    return FacilitySummary.HEADER_reportDensity;
                case 9:
                    return FacilitySummary.HEADER_maxDC;
                case 10:
                    return FacilitySummary.HEADER_maxVC;
                default:
                    return "";
            }
        } else if (col == 1) {
            switch (row) {
                case 0:
                    return tryFloat_2f(baseScen.actualTravelTime);
                case 1:
                    return tryFloat_0f(baseScen.VMTD);
                case 2:
                    return tryFloat_0f(baseScen.VMTV);
                case 3:
                    return tryFloat_0f(baseScen.PMTD);
                case 4:
                    return tryFloat_0f(baseScen.PMTV);
                case 5:
                    return tryFloat_0f(baseScen.VHT);
                case 6:
                    return tryFloat_0f(baseScen.VHD);
                case 7:
                    return tryFloat_1f(baseScen.spaceMeanSpeed);
                case 8:
                    return tryFloat_1f(baseScen.reportDensity);
                case 9:
                    return tryFloat_2f(baseScen.maxDC);
                case 10:
                    return tryFloat_2f(baseScen.maxVC);
                default:
                    return 1.0;
            }
        } else {
            switch (row) {
                case 0:
                    return tryFloat_2f(atdmProperties.get(col - 2).actualTravelTime);
                case 1:
                    return tryFloat_0f(atdmProperties.get(col - 2).VMTD);
                case 2:
                    return tryFloat_0f(atdmProperties.get(col - 2).VMTV);
                case 3:
                    return tryFloat_0f(atdmProperties.get(col - 2).PMTD);
                case 4:
                    return tryFloat_0f(atdmProperties.get(col - 2).PMTV);
                case 5:
                    return tryFloat_0f(atdmProperties.get(col - 2).VHT);
                case 6:
                    return tryFloat_0f(atdmProperties.get(col - 2).VHD);
                case 7:
                    return tryFloat_1f(atdmProperties.get(col - 2).spaceMeanSpeed);
                case 8:
                    return tryFloat_1f(atdmProperties.get(col - 2).reportDensity);
                case 9:
                    return tryFloat_2f(atdmProperties.get(col - 2).maxDC);
                case 10:
                    return tryFloat_2f(atdmProperties.get(col - 2).maxVC);
                default:
                    return 1.0;
            }
        }
    }

    ;

    @Override
    public int getColumnCount() {
        return 2 + atdmProperties.size();
    }

    @Override
    public int getRowCount() {
        return 11;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return selectedScenarioName;
        } else if (col == 1) {
            return "Base Scenario";
        } else {
            return seed.getATDMDatabase().getPlan(col - 2).getName();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void fireTableStructureChanged() {
        super.fireTableStructureChanged();
        parentTable.getColumnModel().getColumn(0).setMinWidth(180);
        parentTable.getColumnModel().getColumn(0).setMaxWidth(180);

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Formatters">
    private String tryFloat_2f(float value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(value);
    }

    private String tryFloat_1f(float value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.0");
        return formatter.format(value);
    }

    private String tryFloat_0f(float value) {
        DecimalFormat formatter = new DecimalFormat("#,##0");
        return formatter.format(value);
    }
    // </editor-fold>

    /**
     *
     * @param scenario
     */
    public void updateData(int scenario) {
        baseScen = new FacilitySummary(seed, scenario);
        atdmProperties.clear();
        if (scenario == 0) {
            selectedScenarioName = "Seed";
        } else {
            selectedScenarioName = scenarioInfos.get(scenario).name;
            if (scenario > 0 && seed.getATDMDatabase().getNumberOfATDMPlans() > 0) {
                for (int planIdx = 0; planIdx < seed.getATDMDatabase().getNumberOfATDMPlans(); planIdx++) {
                    atdmProperties.add(seed.testATDM(scenarioInfos.get(scenario).applyAndGetATDMScenarios(seed.getATDMDatabase(), planIdx), scenario));
                }
            }
        }
        fireTableStructureChanged();
    }
}
