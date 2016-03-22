package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CETime;
import coreEngine.Seed;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * This is the facility period summary table
 *
 * @author Shu Liu
 */
public class FacilityPeriodJTableRest extends JTable {

    private final FacilityPeriodNumAndStringRenderer renderer;

    private final FacilityPeriodTableModel facilityTableModel;

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

    private static final String[] itemGP = {CEConst.IDS_P_ACTUAL_TIME, CEConst.IDS_P_FFS_TIME, CEConst.IDS_P_MAIN_DELAY,
        CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH, CEConst.IDS_P_VMTD, CEConst.IDS_P_VMTV,
        CEConst.IDS_P_PMTD, CEConst.IDS_P_PMTV, CEConst.IDS_P_VHT,
        CEConst.IDS_P_VHD, CEConst.IDS_P_SPACE_MEAN_SPEED, CEConst.IDS_P_TOTAL_DENSITY_VEH,
        CEConst.IDS_P_TOTAL_DENSITY_PC, CEConst.IDS_P_REPORT_LOS, CEConst.IDS_P_TTI,
        CEConst.IDS_P_MAX_DC, CEConst.IDS_P_MAX_VC};
    private static final String[] itemML = {CEConst.IDS_ML_P_ACTUAL_TIME, CEConst.IDS_ML_P_FFS_TIME, CEConst.IDS_ML_P_MAIN_DELAY,
        CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_VEH, CEConst.IDS_ML_P_VMTD, CEConst.IDS_ML_P_VMTV,
        CEConst.IDS_ML_P_PMTD, CEConst.IDS_ML_P_PMTV, CEConst.IDS_ML_P_VHT,
        CEConst.IDS_ML_P_VHD, CEConst.IDS_ML_P_SPACE_MEAN_SPEED, CEConst.IDS_ML_P_TOTAL_DENSITY_VEH,
        CEConst.IDS_ML_P_TOTAL_DENSITY_PC, CEConst.IDS_ML_P_REPORT_LOS, CEConst.IDS_ML_P_TTI,
        CEConst.IDS_ML_P_MAX_DC, CEConst.IDS_ML_P_MAX_VC};
    private static final String[] itemCB = {CEConst.IDS_CB_P_ACTUAL_TIME, CEConst.IDS_CB_P_FFS_TIME, CEConst.IDS_CB_P_MAIN_DELAY,
        CEConst.IDS_CB_P_TOTAL_DENY_QUEUE_VEH, CEConst.IDS_CB_P_VMTD, CEConst.IDS_CB_P_VMTV,
        CEConst.IDS_CB_P_PMTD, CEConst.IDS_CB_P_PMTV, CEConst.IDS_CB_P_VHT,
        CEConst.IDS_CB_P_VHD, CEConst.IDS_CB_P_SPACE_MEAN_SPEED, CEConst.IDS_CB_P_TOTAL_DENSITY_VEH,
        CEConst.IDS_CB_P_TOTAL_DENSITY_PC, CEConst.IDS_CB_P_REPORT_LOS, CEConst.IDS_CB_P_TTI,
        CEConst.IDS_CB_P_MAX_DC, CEConst.IDS_CB_P_MAX_VC};
    private static final String[] formatType = {"2f", "2f", "2f",
        "0f", "0f", "0f",
        "0f", "0f", "0f",
        "0f", "1f", "1f",
        "1f", "S", "2f",
        "2f", "2f"};

    /**
     * Constructor
     */
    public FacilityPeriodJTableRest() {
        super();
        this.renderer = new FacilityPeriodNumAndStringRenderer();
        this.facilityTableModel = new FacilityPeriodTableModel();
        this.setModel(facilityTableModel);
        this.resetLayout();
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    /**
     * Update
     */
    public void update() {
        facilityTableModel.fireTableStructureChanged();
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
        facilityTableModel.fireTableStructureChanged();
    }

    /**
     * Change the model
     *
     * @param model new model identifier
     */
    public void configModel(int model) {
        currentModel = model;
        facilityTableModel.fireTableStructureChanged();
    }

    private class FacilityPeriodNumAndStringRenderer extends DefaultTableCellRenderer {

        private FacilityPeriodNumAndStringRenderer() {
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
            try {
                switch (formatType[row]) {
                    case "2f":
                        tryFloat_2f(value.toString());
                        break;
                    case "0f":
                        tryFloat_0f(value.toString());
                        break;
                    case "1f":
                        tryFloat_1f(value.toString());
                        break;
                    default:
                        if (value.toString().equals(CEConst.IDS_NA)) {
                            this.setForeground(Color.darkGray);
                            this.setBackground(Color.darkGray);
                        }
                        setText(value.toString());
                }
            } catch (IllegalArgumentException e2) {
                setText(value.toString());
            }
            return this;
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

    private class FacilityPeriodTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : itemGP.length;
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        }

        @Override
        public Object getValueAt(int row, int column) {
            try {
                switch (currentModel) {
                    case MODEL_GP:
                        return seed.getValueString(itemGP[row], 0, column, scen, atdm);
                    case MODEL_ML:
                        return seed.getValueString(itemML[row], 0, column, scen, atdm);
                    case MODEL_CB:
                        return seed.getValueString(itemCB[row], 0, column, scen, atdm);
                    default:
                        return "Error";
                }

            } catch (Exception e) {
                return "Error";
            }
        }

        @Override
        public String getColumnName(int column) {
            return (CETime.addTime(seed.getStartTime(), new CETime(0, 15), column)).toString()
                    + " (" + String.valueOf(column + 1) + ")";
        }

    }
}
