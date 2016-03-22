package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the contour table
 *
 * @author Shu Liu
 */
public class ContourJTableFirst extends JTable {

    private final ContourTableModel summaryTableModel;

    private Seed seed = null;

    /**
     * Constructor
     *
     */
    public ContourJTableFirst() {
        super();
        this.summaryTableModel = new ContourTableModel();
        this.setModel(summaryTableModel);
        this.resetLayout();
    }

    private void resetLayout() {
        this.setVisible(true);
        this.setRowHeight(19);
        this.setFont(MainWindow.getTableFont());
        this.getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            //increase column width for the first column
            this.getColumnModel().getColumn(0).setPreferredWidth(120);
        } catch (Exception e) {
            //TODO empty
        }
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     */
    public void selectSeedScenPeriod(Seed seed) {
        this.seed = seed;
        summaryTableModel.fireTableStructureChanged();
    }

    private class ContourTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : 1;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return seed.getValueString(CEConst.IDS_ANALYSIS_PERIOD_HEADING, 0, row);
        }

        @Override
        public String getColumnName(int column) {
            return "Analysis Period";
        }
    }
}
