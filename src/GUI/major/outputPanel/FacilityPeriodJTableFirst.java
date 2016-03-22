package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Seed;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * This is the facility period summary table
 *
 * @author Shu Liu
 */
public class FacilityPeriodJTableFirst extends JTable {

    private final FacilityPeriodTableModel facilityTableModel;

    private static final String[] header = {"Actual Travel Time (min)",
        "FFS Travel Time (min)",
        "Mainline Delay (min)",
        "Deny Entry Queue (veh)",
        "VMTD (veh-miles / interval)",
        "VMTV (veh-miles / interval)",
        "PMTD (p-miles / interval)",
        "PMTV (p-miles / interval)",
        "VHT (travel / interval (hrs))",
        "VHD (delay / interval (hrs))",
        "Space Mean Speed (mph)",
        "Total Density (veh/mi/ln)",
        "Total Density (pc/mi/ln)",
        "Report LOS",
        "Travel Time Index",
        "Max D/C",
        "Max V/C"};

    private Seed seed = null;

    /**
     * Constructor
     */
    public FacilityPeriodJTableFirst() {
        super();
        facilityTableModel = new FacilityPeriodTableModel();
        setModel(facilityTableModel);
        resetLayout();
    }

    /**
     * Update
     */
    public void update() {
        facilityTableModel.fireTableStructureChanged();
    }

    private void resetLayout() {
        setVisible(true);
        setRowHeight(19);
        this.setFont(MainWindow.getTableFont());
        getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     */
    public void selectSeedScenPeriod(Seed seed) {
        this.seed = seed;
        facilityTableModel.fireTableStructureChanged();
    }

    private class FacilityPeriodTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : header.length;
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            try {
                return header[row];
            } catch (Exception e) {
                return "Error";
            }
        }

        @Override
        public String getColumnName(int column) {
            return " ";
        }

    }
}
