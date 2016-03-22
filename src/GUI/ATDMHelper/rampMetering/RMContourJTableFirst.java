package GUI.ATDMHelper.rampMetering;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CETime;
import coreEngine.Seed;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This class is the contour table
 *
 * @author Shu Liu
 */
public class RMContourJTableFirst extends JTable {

    private final ContourTableModel summaryTableModel;

    private Seed seed = null;

    /**
     * Constructor
     *
     * @param seed
     */
    public RMContourJTableFirst(Seed seed) {
        super();
        this.seed = seed;
        this.summaryTableModel = new ContourTableModel();
        this.setModel(summaryTableModel);
        this.resetLayout();
    }

    private void resetLayout() {
        this.setVisible(true);
        this.setRowHeight(19);
        this.setFont(MainWindow.getTableFont());
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        this.setDefaultRenderer(String.class, centerRenderer);
        this.setDefaultRenderer(Integer.class, centerRenderer);
        this.setDefaultRenderer(Object.class, centerRenderer);
        this.getTableHeader().setReorderingAllowed(false);
        //this.getTableHeader().setResizingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            //increase column width for the first column
            this.getColumnModel().getColumn(0).setMaxWidth(45);
        } catch (Exception e) {
            //TODO empty
        }
        //this.getColumnModel().
    }

    private class ContourTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                return (row + 1);
            } else {
                CETime stime = seed.getStartTime();
                CETime inc = new CETime(0, 15);
                return CETime.addTime(stime, inc, row).toString() + " - " + CETime.addTime(stime, inc, row + 1).toString();
            }
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return "Period";
            } else {
                return "Time";
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
