package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the facility segment summary table
 *
 * @author Shu Liu
 */
public class FacilitySegmentJTableRest extends JTable {

    private final FacilitySegmentNumAndStringRenderer renderer;

    private final FacilitySegmentTableModel facilitySegmentTableModel;

    private Seed seed = null;

    private int scen = 0;

    private int atdm = -1;

    /**
     * Model constant identifier
     */
    public static final int MODEL_GP = 1;

    /**
     * Model constant identifier
     */
    public static final int MODEL_ML = 2;

    /**
     * Model constant identifier
     */
    public static final int MODEL_CB = 3;

    private int currentModel = MODEL_GP;

    /**
     * Constructor
     */
    public FacilitySegmentJTableRest() {
        super();
        this.renderer = new FacilitySegmentNumAndStringRenderer();
        this.facilitySegmentTableModel = new FacilitySegmentTableModel();
        this.setModel(facilitySegmentTableModel);
        this.resetLayout();
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     * @param scen index of scenario to be displayed
     * @param atdm ATDM set index
     */
    public void selectSeedScenATDM(Seed seed, int scen, int atdm) {
        this.seed = seed;
        this.scen = scen;
        this.atdm = atdm;
        facilitySegmentTableModel.fireTableStructureChanged();
    }

    /**
     * Update
     */
    public void update() {
        facilitySegmentTableModel.fireTableStructureChanged();
    }

    private void resetLayout() {
        this.setAutoResizeMode(AUTO_RESIZE_OFF);
        this.setVisible(true);
        this.setRowHeight(19);
        this.setFont(MainWindow.getTableFont());
        this.getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Change the model
     *
     * @param model new model identifier
     */
    public void configModel(int model) {
        currentModel = model;
        facilitySegmentTableModel.fireTableStructureChanged();
    }

    private class FacilitySegmentTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : 12;
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_SEGMENT) + 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            try {
                switch (currentModel) {
                    case MODEL_GP:
                        if (column == seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_TOTAL_LENGTH_MI);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_SP_ACTUAL_TIME, 0, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_SP_VMTD, 0, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_SP_VMTV, 0, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_SP_PMTD, 0, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_SP_PMTV, 0, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_SP_VHT, 0, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_SP_VHD, 0, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_SP_SPACE_MEAN_SPEED, 0, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_SP_REPORT_DENSITY_PC, 0, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_SP_MAX_DC, 0, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_SP_MAX_VC, 0, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        } else {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_SEGMENT_LENGTH_MI, column);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_S_ACTUAL_TIME, column, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_S_VMTD, column, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_S_VMTV, column, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_S_PMTD, column, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_S_PMTV, column, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_S_VHT, column, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_S_VHD, column, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_S_SPACE_MEAN_SPEED, column, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_S_REPORT_DENSITY_PC, column, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_S_MAX_DC, column, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_S_MAX_VC, column, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        }
                    case MODEL_ML:
                        if (column == seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_TOTAL_LENGTH_MI);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_ML_SP_ACTUAL_TIME, 0, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_ML_SP_VMTD, 0, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_ML_SP_VMTV, 0, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_ML_SP_PMTD, 0, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_ML_SP_PMTV, 0, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_ML_SP_VHT, 0, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_ML_SP_VHD, 0, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_ML_SP_SPACE_MEAN_SPEED, 0, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_ML_SP_REPORT_DENSITY_PC, 0, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_ML_SP_MAX_DC, 0, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_ML_SP_MAX_VC, 0, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        } else {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_ML_SEGMENT_LENGTH_MI, column);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_ML_S_ACTUAL_TIME, column, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_ML_S_VMTD, column, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_ML_S_VMTV, column, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_ML_S_PMTD, column, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_ML_S_PMTV, column, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_ML_S_VHT, column, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_ML_S_VHD, column, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_ML_S_SPACE_MEAN_SPEED, column, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_ML_S_REPORT_DENSITY_PC, column, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_ML_S_MAX_DC, column, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_ML_S_MAX_VC, column, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        }
                    case MODEL_CB:
                        if (column == seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_TOTAL_LENGTH_MI);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_CB_SP_ACTUAL_TIME, 0, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_CB_SP_VMTD, 0, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_CB_SP_VMTV, 0, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_CB_SP_PMTD, 0, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_CB_SP_PMTV, 0, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_CB_SP_VHT, 0, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_CB_SP_VHD, 0, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_CB_SP_SPACE_MEAN_SPEED, 0, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_CB_SP_REPORT_DENSITY_PC, 0, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_CB_SP_MAX_DC, 0, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_CB_SP_MAX_VC, 0, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        } else {
                            switch (row) {
                                case 0:
                                    return seed.getValueString(CEConst.IDS_TOTAL_LENGTH_MI, column);
                                case 1:
                                    return seed.getValueString(CEConst.IDS_CB_S_ACTUAL_TIME, column, 0, scen, atdm);
                                case 2:
                                    return seed.getValueString(CEConst.IDS_CB_S_VMTD, column, 0, scen, atdm);
                                case 3:
                                    return seed.getValueString(CEConst.IDS_CB_S_VMTV, column, 0, scen, atdm);
                                case 4:
                                    return seed.getValueString(CEConst.IDS_CB_S_PMTD, column, 0, scen, atdm);
                                case 5:
                                    return seed.getValueString(CEConst.IDS_CB_S_PMTV, column, 0, scen, atdm);
                                case 6:
                                    return seed.getValueString(CEConst.IDS_CB_S_VHT, column, 0, scen, atdm);
                                case 7:
                                    return seed.getValueString(CEConst.IDS_CB_S_VHD, column, 0, scen, atdm);
                                case 8:
                                    return seed.getValueString(CEConst.IDS_CB_S_SPACE_MEAN_SPEED, column, 0, scen, atdm);
                                case 9:
                                    return seed.getValueString(CEConst.IDS_CB_S_REPORT_DENSITY_PC, column, 0, scen, atdm);
                                case 10:
                                    return seed.getValueString(CEConst.IDS_CB_S_MAX_DC, column, 0, scen, atdm);
                                case 11:
                                    return seed.getValueString(CEConst.IDS_CB_S_MAX_VC, column, 0, scen, atdm);
                                default:
                                    return "Error";
                            }
                        }
                    default:
                        return "Error";
                }
            } catch (Exception e) {
                return "Error";
            }
        }

        @Override
        public String getColumnName(int column) {
            if (column == seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
                return "Facility Total";
            } else {
                return "Seg. " + (column + 1);
            }
        }
    }

    private class FacilitySegmentNumAndStringRenderer extends DefaultTableCellRenderer {

        private FacilitySegmentNumAndStringRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            this.setForeground(null);
            this.setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            if (column == seed.getValueInt(CEConst.IDS_NUM_SEGMENT)) {
                this.setFont(MainWindow.getTableFont().deriveFont(Font.BOLD));
            }
            try {
                switch (row) {
                    case 0:
                        tryFloat_2f(value.toString());
                        break;
                    case 1:
                        tryFloat_2f(value.toString());
                        break;
                    case 2:
                        tryFloat_0f(value.toString());
                        break;
                    case 3:
                        tryFloat_0f(value.toString());
                        break;
                    case 4:
                        tryFloat_0f(value.toString());
                        break;
                    case 5:
                        tryFloat_0f(value.toString());
                        break;
                    case 6:
                        tryFloat_0f(value.toString());
                        break;
                    case 7:
                        tryFloat_0f(value.toString());
                        break;
                    case 8:
                        tryFloat_1f(value.toString());
                        break;
                    case 9:
                        tryFloat_1f(value.toString());
                        break;
                    case 10:
                        tryFloat_2f(value.toString());
                        break;
                    case 11:
                        tryFloat_2f(value.toString());
                        break;
                    default:
                        tryFloat_1f(value.toString());
                }
            } catch (IllegalArgumentException e2) {
                setText(value.toString());
            }
            return this;
        }

        private void tryInt(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Integer.parseInt(value)));
        }

        private void tryFloat_2f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_1f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.0");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_0f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Float.parseFloat(value)));
        }
    }
}
