package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Seed;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the facility segment summary table
 *
 * @author Shu Liu
 */
public class FacilitySegmentJTableFirst extends JTable {

    private final FacilitySegmentTableModel facilitySegmentTableModel;

    private Seed seed = null;

    /**
     * Constructor
     */
    public FacilitySegmentJTableFirst() {
        super();
        this.facilitySegmentTableModel = new FacilitySegmentTableModel();
        this.setModel(facilitySegmentTableModel);
        this.resetLayout();
    }

    /**
     * update
     */
    public void update() {
        facilitySegmentTableModel.fireTableStructureChanged();
    }

    private void resetLayout() {
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
     */
    public void selectSeed(Seed seed) {
        this.seed = seed;
        facilitySegmentTableModel.fireTableStructureChanged();
    }

    private class FacilitySegmentTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : 12;
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            try {
                switch (row) {
                    case 0:
                        return "Length (mi)";
                    case 1:
                        return "Average Travel Time (min)";
                    case 2:
                        return "VMTD (veh-miles / interval)";
                    case 3:
                        return "VMTV (veh-miles / interval)";
                    case 4:
                        return "PMTD (p-miles / interval)";
                    case 5:
                        return "PMTV (p-miles / interval)";
                    case 6:
                        return "VHT (travel / interval (hrs))";
                    case 7:
                        return "VHD (delay / interval (hrs))";
                    case 8:
                        return "Space Mean Speed (mph)";
                    case 9:
                        return "Reported Density (pc/mi/ln)";
                    case 10:
                        return "Max D/C";
                    case 11:
                        return "Max V/C";
                    default:
                        return "Error";
                }
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
