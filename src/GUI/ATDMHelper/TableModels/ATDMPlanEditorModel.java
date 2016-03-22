package GUI.ATDMHelper.TableModels;

import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMPlan;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake Trask
 */
public class ATDMPlanEditorModel extends AbstractTableModel {

    private final String[] columnNames;

    private final Seed seed;
    //private final ATDMDatabase atdmDatabase;

    private final ATDMModel atdmModel;

    private final JTable parentTable;

    private final ArrayList<Boolean> selected;

    private final int modelType;

    /**
     *
     * @param modelType
     * @param seed
     * @param atdmModel
     * @param parentTable
     */
    public ATDMPlanEditorModel(int modelType, Seed seed, ATDMModel atdmModel, JTable parentTable) {
        this.modelType = modelType;
        if (this.modelType == 0) {
            //columnNames = new String[] {"<HTML><CENTER>\u2713<br>&nbsp","<HTML><CENTER>ID", "<HTML>Plan<br>Name","<HTML><Center>Description"};
            columnNames = new String[]{"<HTML><CENTER>\u2713<br>&nbsp", "<HTML><CENTER>ID", "<HTML>Plan<br>Name"};
        } else {
            columnNames = new String[]{"<HTML><CENTER>ID<br>&nbsp", "<HTML>Plan<br>Name"};
        }
        this.seed = seed;
        //this.atdmDatabase = seed.getATDMDatabase();
        this.atdmModel = atdmModel;
        this.parentTable = parentTable;

        // Creating ArrayList holding which plan is selected
        selected = new ArrayList<>();
        update(-1);

    }

    /**
     *
     */
    public void addPlan() {
        seed.getATDMDatabase().addPlan(new ATDMPlan(seed.getATDMDatabase().getNumberOfATDMPlans() + 1, "Plan " + (seed.getATDMDatabase().getNumberOfATDMPlans() + 1)));

    }

    /**
     *
     * @param planIdx
     * @return
     */
    public ATDMPlan getPlan(int planIdx) {
        return seed.getATDMDatabase().getPlan(planIdx);
    }

    /**
     *
     * @param selectedRow
     * @return
     */
    public int update(int selectedRow) {
        if (modelType == 0) {
            while (selected.size() < seed.getATDMDatabase().getNumberOfATDMPlans() + 1) {       // Plus one for no plan option
                selected.add(Boolean.FALSE);
            }

            while (selected.size() > seed.getATDMDatabase().getNumberOfATDMPlans() + 1) {       // Plus one for no plan option
                selected.remove(selected.size() - 1);
            }

            if (selectedRow >= 0 && Integer.parseInt(atdmModel.getValueAt(selectedRow, ATDMModel.COL_SCEN_IDX).toString()) != 0) {
                String selectedRowPlanName = (String) atdmModel.getValueAt(selectedRow, ATDMModel.COL_ATDM_PLAN);
                selected.set(0, selectedRowPlanName.equalsIgnoreCase("No Plan"));
                for (int planIdx = 0; planIdx < seed.getATDMDatabase().getNumberOfATDMPlans(); planIdx++) {
                    selected.set(planIdx + 1, selectedRowPlanName.equalsIgnoreCase(seed.getATDMDatabase().getPlan(planIdx).getName()));
                }
            } else {
                for (int planIdx = 0; planIdx < selected.size(); planIdx++) {
                    selected.set(planIdx, Boolean.FALSE);
                }
            }

            fireTableDataChanged();
        }
        return selected.indexOf(Boolean.TRUE);
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int getRowCount() {
        if (modelType == 0) {
            return seed.getATDMDatabase().getNumberOfATDMPlans() + 1;
        } else {
            return seed.getATDMDatabase().getNumberOfATDMPlans();
        }
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
        // Incrementing column index if selection column is hidden
        if (modelType == 0) {
            if (columnIndex == 0) {
                return selected.get(rowIndex);
            } else {
                if (rowIndex == 0) {
                    if (columnIndex == 1) {
                        return "-";
                    } else if (columnIndex == 2) {
                        return "No Plan";
                    } else {
                        return "";
                    }
                } else {
                    rowIndex--;
                    if (columnIndex == 1) {
                        return seed.getATDMDatabase().getPlan(rowIndex).getID();
                    } else if (columnIndex == 2) {
                        return seed.getATDMDatabase().getPlan(rowIndex).getName();
                    } else {
                        return seed.getATDMDatabase().getPlan(rowIndex).getDescription();
                    }
                }
            }
        } else {
            if (columnIndex == 0) {
                return seed.getATDMDatabase().getPlan(rowIndex).getID();
            } else {
                return seed.getATDMDatabase().getPlan(rowIndex).getName();
            }
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (modelType == 1) {
            return true;
        } else {
            return col == 0;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // Incrementing column index if selection column is hidden
        if (modelType == 0) {
            if (columnIndex == 0) {
                if (rowIndex == 0) {
                    atdmModel.setPlanForSelectedRows(null);
                } else {
                    rowIndex--;
                    atdmModel.setPlanForSelectedRows(seed.getATDMDatabase().getPlan(rowIndex));
                }
                int selectedRow = atdmModel.getSelectedRow();
                update(selectedRow);
                atdmModel.fireTableDataChanged();
                atdmModel.setSelectedRow(selectedRow);
            }
        } else {
            if (columnIndex == 0) {
                seed.getATDMDatabase().getPlan(rowIndex).setID((int) value);
            } else {
                seed.getATDMDatabase().getPlan(rowIndex).setName((String) value);
            }
        }
    }

    @Override
    public void fireTableStructureChanged() {
        super.fireTableStructureChanged();
        if (modelType == 0) {
            parentTable.getColumnModel().getColumn(0).setMaxWidth(50);
            parentTable.getColumnModel().getColumn(1).setMaxWidth(25);
            // parentTable.getColumnModel().getColumn(2).setMaxWidth(65);
        } else {
            parentTable.getColumnModel().getColumn(0).setMaxWidth(25);
        }
    }

//</editor-fold>
}
