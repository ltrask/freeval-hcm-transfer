package GUI.RLHelper.TableModels;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake Trask
 */
public class AdjFactorModel extends AbstractTableModel {

    private final int tableType;

    private final String[] closureTypes;

    private final int[] lanes;
    //private final IncidentData data;
    //private final WorkZoneData data;

    /**
     *
     * @param tableType
     */
    public AdjFactorModel(int tableType) {

        this.tableType = tableType;
        //incidentTypes = new String[] {"Shoulder Closure","One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"};
        closureTypes = new String[]{"<HTML><CENTER>Shoulder<br>Closure", "<HTML><CENTER>1 Lane<br>Closure", "<HTML><CENTER>2 Lane<br>Closure", "<HTML><CENTER>3 Lane<br>Closure", "<HTML><CENTER>4 Lane<br>Closure"};
        lanes = new int[]{2, 3, 4, 5, 6, 7, 8};

    }

    @Override
    public int getRowCount() {
        return lanes.length;
    }

    @Override
    public int getColumnCount() {
        //return incidentTypes.length + 1;
        return closureTypes.length + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        if (columnIndex == 0) {
//            return lanes[rowIndex];
//        } else {
//            if (tableType == 3) {
//                return (int) WorkZoneEvent.getAdjFactor(tableType, rowIndex, columnIndex - 1);
//            } else {
//                return WorkZoneEvent.getAdjFactor(tableType, rowIndex, columnIndex - 1);
//            }
//        }
        return 1.0f;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "<HTML><CENTER>Segment<br>Lanes";
        } else {
            return closureTypes[col - 1];
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (tableType == 3) {
            return false;
        } else {
            return col != 0;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
//        if (col > 0) {
//            if (tableType == 0) {          // FFSAF table
//                String input = value.toString();
//                try {
//                    float val = Float.parseFloat(input);
//                    WorkZoneEvent.setFFSAF(col - 1, row, val);
//                } catch (NumberFormatException e) {
//                    System.err.println("Invalid Input");
//                }
//            } else if (tableType == 1) {   // CAF table
//                String input = value.toString();
//                try {
//                    float val = Float.parseFloat(input);
//                    WorkZoneEvent.setCAF(col - 1, row, val);
//                } catch (NumberFormatException e) {
//                    System.err.println("Invalid Input");
//                }
//            } else if (tableType == 2) {   // DAF table
//                String input = value.toString();
//                try {
//                    float val = Float.parseFloat(input);
//                    WorkZoneEvent.setDAF(col - 1, row, val);
//                } catch (NumberFormatException e) {
//                    System.err.println("Invalid Input");
//                }
//            } else {                       // LAF table
//                String input = value.toString();
//                try {
//                    int val = Integer.parseInt(input);
//                    WorkZoneEvent.setLAF(col - 1, row, val);
//                } catch (NumberFormatException e) {
//                    System.err.println("Invalid Input");
//                }
//            }
//
//            fireTableCellUpdated(row, col);
//    }
    }
}
