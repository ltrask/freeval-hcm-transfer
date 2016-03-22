package GUI.RLHelper.ManagedLanes;

import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jlaketrask
 */
public class MLIncidentAdjFactorModel extends AbstractTableModel {

    private final int tableType;

    private final String[] incidentTypes;

    private final int[] lanes;

    private final IncidentData data;

    /**
     *
     */
    public static final int ID_TABLE_TYPE_SAF = 0;

    /**
     *
     */
    public static final int ID_TABLE_TYPE_CAF = 1;

    /**
     *
     */
    public static final int ID_TABLE_TYPE_DAF = 2;

    /**
     *
     */
    public static final int ID_TABLE_TYPE_LAF = 3;

    /**
     *
     * @param data
     * @param tableType
     */
    public MLIncidentAdjFactorModel(IncidentData data, int tableType) {

        this.data = data;
        this.tableType = tableType;
        //incidentTypes = new String[] {"Shoulder Closure","One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"};
        //incidentTypes = new String[] {"Shoulder","One Lane", "Two Lane", "Three Lane", "Four Lane"};
        incidentTypes = new String[]{"<HTML><CENTER>Shoulder<br>Closure", "<HTML><CENTER>1 Lane<br>Closure"};
        //lanes = new int[]{2, 3, 4, 5, 6, 7, 8};
        lanes = new int[]{1, 2};
    }

    @Override
    public int getRowCount() {
        return lanes.length;
    }

    @Override
    public int getColumnCount() {
        //return incidentTypes.length + 1;
        return incidentTypes.length + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //String formatPercent = "%.2f%%";
        //String formatFloat = "%.2f";
        if (columnIndex == 0) {
            return lanes[rowIndex];
        } else {
            if (tableType == ID_TABLE_TYPE_SAF) {          // FFSAF table
                return data.getIncidentFFSAF(columnIndex - 1, rowIndex);
            } else if (tableType == ID_TABLE_TYPE_CAF) {   // CAF table
                return data.getIncidentCAF(columnIndex - 1, rowIndex);
            } else if (tableType == ID_TABLE_TYPE_DAF) {   // DAF table
                return data.getIncidentDAF(columnIndex - 1, rowIndex);
            } else {                       // LAF table
                return data.getIncidentLAF(columnIndex - 1, rowIndex);
            }
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "<HTML><CENTER>Segment<br>Lanes";
        } else {
            return incidentTypes[col - 1];
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (tableType != 3) {
            return col != 0;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col > 0) {
            if (tableType == ID_TABLE_TYPE_SAF) {          // FFSAF table
                String input = value.toString();
                try {
                    float val = Float.parseFloat(input);
                    data.setIncidentFFSAF(col - 1, row, val);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Input");
                }
            } else if (tableType == ID_TABLE_TYPE_CAF) {   // CAF table
                String input = value.toString();
                try {
                    float val = Float.parseFloat(input);
                    data.setIncidentCAF(col - 1, row, val);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Input");
                }
            } else if (tableType == ID_TABLE_TYPE_DAF) {   // DAF table
                String input = value.toString();
                try {
                    float val = Float.parseFloat(input);
                    data.setIncidentDAF(col - 1, row, val);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Input");
                }
            } else {                       // LAF table
                String input = value.toString();
                try {
                    int val = Integer.parseInt(input);
                    data.setIncidentLAF(col - 1, row, val);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Input");
                }
            }

            fireTableCellUpdated(row, col);
        }
    }
}
