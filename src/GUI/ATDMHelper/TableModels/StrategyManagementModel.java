package GUI.ATDMHelper.TableModels;

import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMStrategy;
import coreEngine.atdm.DataStruct.ATDMStrategyMat;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake
 */
public class StrategyManagementModel extends AbstractTableModel {

    private final String[] columnNames;

    //private final ATDMDatabase atdmDatabase;
    private final Seed seed;

    private final ArrayList<ATDMStrategy> atdmStrat;

    private final String modelType;

    private final JTable parentTable;

    /**
     *
     * @param modelType
     * @param seed
     * @param parentTable
     */
    public StrategyManagementModel(String modelType, Seed seed, JTable parentTable) {
        this.modelType = modelType;
        this.seed = seed;
        this.atdmStrat = seed.getATDMDatabase().getStrategy(modelType);

        switch (modelType) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
            default:
                //columnNames=new String[] {"ID", "Description", "Category", "Dem Adj"};
                columnNames = new String[]{"<HTML><CENTER>&#35<br>&nbsp",
                    "<HTML><CENTER>Description",
                    "<HTML><CENTER>Category",
                    "<HTML><CENTER>Demand<br>Adjustment"};
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                //columnNames=new String[] {"ID", "Description", "Category", "Dem Adj", "FFS Adj", "Cap Adj"};
                columnNames = new String[]{"<HTML><CENTER>&#35<br>&nbsp",
                    "<HTML><CENTER>Description",
                    "<HTML><CENTER>Category",
                    "<HTML><CENTER>Demand<br>Adjustment",
                    "<HTML><CENTER>FFS<br>Adjustment",
                    "<HTML><CENTER>Capacity<br>Adjustment"};
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                //columnNames=new String[] {"ID", "Description", "Category", "Dem Adj", "FFS Adj", "Cap Adj", "Dur Red"};
                columnNames = new String[]{"<HTML><CENTER>&#35<br>&nbsp",
                    "<HTML><CENTER>Description",
                    "<HTML><CENTER>Category",
                    "<HTML><CENTER>Demand<br>Adjustment",
                    "<HTML><CENTER>FFS<br>Adjustment",
                    "<HTML><CENTER>Capacity<br>Adjustment",
                    "<HTML><CENTER>Duration<br>Reduction"};
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                columnNames = new String[]{"<HTML><CENTER>&#35",
                    "<HTML><CENTER>Description"};
                break;
        }

        this.parentTable = parentTable;
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int getRowCount() {
        return atdmStrat.size();
    }

    @Override
    public int getColumnCount() {
        switch (modelType) {
            default:
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                return columnNames.length - 1;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                return columnNames.length;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return atdmStrat.get(rowIndex).getId();
            case 1:
                return atdmStrat.get(rowIndex).getDescription();
//            case 2:
//                return atdmStrat.get(rowIndex).getCategory();
            case 2:
            case 3:
            case 4:
                return atdmStrat.get(rowIndex).getAdjFactor(columnIndex - 2);
            case 5:
                return String.valueOf(atdmStrat.get(rowIndex).getIncidentDurationReduction()) + " min";
            default:
                return 1.0f;
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col < 2) {
            return columnNames[col];
        } else {
            return columnNames[col + 1];
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            if (col == 0) {
                atdmStrat.get(row).setId((int) value);
            } else if (col == 1) {
                atdmStrat.get(row).setDescription((String) value);
//            } else if (col == 2) {
//                atdmStrat.get(row).setCategory((String) value);
            } else if (col == 5) {
                atdmStrat.get(row).setIncidentDurationReduction(Integer.parseInt(((String) value).split(" ")[0]));
            } else {
                float val = Float.parseFloat((String) value);
                if (val >= 0.0f) {
                    atdmStrat.get(row).setAdjFactor(val, col - 2);
                }
            }
        } catch (NumberFormatException e) {
            // Do nothing, dont change values
        }
    }

    @Override
    public void fireTableStructureChanged() {
        super.fireTableStructureChanged();
        if (parentTable != null) {
            parentTable.getColumnModel().getColumn(0).setMinWidth(25);
            parentTable.getColumnModel().getColumn(0).setMaxWidth(25);
            //parentTable.getColumnModel().getColumn(2).setMinWidth(135);
            //parentTable.getColumnModel().getColumn(2).setMaxWidth(200);
            for (int colIdx = 2; colIdx < parentTable.getColumnModel().getColumnCount(); colIdx++) {
                parentTable.getColumnModel().getColumn(colIdx).setMinWidth(75);
                parentTable.getColumnModel().getColumn(colIdx).setMaxWidth(75);
            }
        }
    }

    // </editor-fold>
    /**
     *
     */
    public void addStrategy() {
        switch (modelType) {
            default:
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                seed.getATDMDatabase().addStrategy(modelType, new ATDMStrategy(atdmStrat.size() + 1, "Please add description here"));
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                seed.getATDMDatabase().addStrategy(modelType, new ATDMStrategyMat(atdmStrat.size() + 1,
                        "Please add description here",
                        seed.getValueInt(CEConst.IDS_NUM_SEGMENT),
                        seed.getValueInt(CEConst.IDS_NUM_PERIOD),
                        modelType));
                break;
        }
        fireTableDataChanged();
    }

    /**
     *
     * @param description
     * @param categoryIdx
     */
    public void addStrategy(String description, int categoryIdx) {
        switch (modelType) {
            default:
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                seed.getATDMDatabase().addStrategy(modelType, new ATDMStrategy(atdmStrat.size() + 1, description, categoryIdx));
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                seed.getATDMDatabase().addStrategy(modelType, new ATDMStrategyMat(atdmStrat.size() + 1,
                        description,
                        seed.getValueInt(CEConst.IDS_NUM_SEGMENT),
                        seed.getValueInt(CEConst.IDS_NUM_PERIOD),
                        modelType));
                break;
        }
        fireTableDataChanged();
    }

    /**
     *
     * @param strategyIdx
     */
    public void deleteStrategy(int strategyIdx) {
        seed.getATDMDatabase().removeStrategy(modelType, strategyIdx);
        fireTableDataChanged();
    }

    /**
     *
     * @param stratIdx
     * @return
     */
    public ATDMStrategy getStrategy(int stratIdx) {
        return seed.getATDMDatabase().getStrategy(modelType, stratIdx);
    }

    /**
     *
     * @return
     */
    public String getModelType() {
        return modelType;
    }
}
