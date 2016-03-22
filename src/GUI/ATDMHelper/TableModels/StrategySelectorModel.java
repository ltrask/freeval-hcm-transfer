package GUI.ATDMHelper.TableModels;

import coreEngine.Helper.CEConst;
import coreEngine.atdm.DataStruct.ATDMPlan;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake Trask
 */
public class StrategySelectorModel extends AbstractTableModel {

    private final StrategyManagementModel strategies;

    private ATDMPlan atdmPlan;

    private final Boolean[] selected;

    /**
     *
     * @param strategies
     */
    public StrategySelectorModel(StrategyManagementModel strategies) {
        this.strategies = strategies;

        this.selected = new Boolean[strategies.getRowCount()];
        initSelectedArray();
    }

    /**
     *
     * @param atdmPlan
     */
    public void setPlan(ATDMPlan atdmPlan) {
        this.atdmPlan = atdmPlan;
        setSelectedStrategies();
    }

    private void setSelectedStrategies() {
        for (int stratIdx = 0; stratIdx < strategies.getRowCount(); stratIdx++) {
            this.selected[stratIdx] = atdmPlan.hasStrategy(strategies.getStrategy(stratIdx));
        }

        fireTableDataChanged();

    }

    private void initSelectedArray() {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = Boolean.FALSE;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int getRowCount() {
        return strategies.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return strategies.getColumnCount() + 1;
    }

    @Override
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (columnIndex == 0) {
            return selected[rowIndex];
        } else {
            return strategies.getValueAt(rowIndex, columnIndex - 1);
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "<HTML><CENTER>\u2713<br>&nbsp";
        } else {
            return strategies.getColumnName(col - 1);
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        switch (strategies.getModelType()) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                if (col == 0) {
                    if (atdmPlan != null) {
                        selected[row] = (boolean) value;
                        if ((boolean) value) {
                            atdmPlan.addStrategy(strategies.getModelType(), strategies.getStrategy(row));
                        } else {
                            atdmPlan.removeStrategy(strategies.getStrategy(row));
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a plan from the"
                                + " menu on the left before assigning any strategies",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                if (col == 0) {
                    if (atdmPlan != null) {
                        for (int rowIdx = 0; rowIdx < getRowCount(); rowIdx++) {
                            if (rowIdx == row) {
                                selected[rowIdx] = (boolean) value;
                                atdmPlan.useShoulderOpening((boolean) value);
                                if ((boolean) value) {
                                    atdmPlan.addStrategy(strategies.getModelType(), strategies.getStrategy(row));
                                } else {
                                    atdmPlan.removeStrategy(strategies.getStrategy(row));
                                }
                            } else if (selected[rowIdx]) {
                                selected[rowIdx] = false;
                                atdmPlan.removeStrategy(strategies.getStrategy(rowIdx));
                                this.fireTableCellUpdated(rowIdx, 0);
                            }
                        }
                    }
                    break;
                }
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                if (col == 0) {
                    if (atdmPlan != null) {
                        for (int rowIdx = 0; rowIdx < getRowCount(); rowIdx++) {
                            if (rowIdx == row) {
                                selected[rowIdx] = (boolean) value;
                                atdmPlan.useRampMetering((boolean) value);
                                if ((boolean) value) {
                                    atdmPlan.addStrategy(strategies.getModelType(), strategies.getStrategy(row));
                                } else {
                                    atdmPlan.removeStrategy(strategies.getStrategy(row));
                                }
                            } else if (selected[rowIdx]) {
                                selected[rowIdx] = false;
                                atdmPlan.removeStrategy(strategies.getStrategy(rowIdx));
                                this.fireTableCellUpdated(rowIdx, 0);
                            }
                        }
                    }
                    break;
                }
        }
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Exceptions">
    class IncompatableSizeException extends RuntimeException {

        /**
         *
         * @param desc
         */
        public IncompatableSizeException(String desc) {
            super(desc);
        }
    }
    // </editor-fold>
}
