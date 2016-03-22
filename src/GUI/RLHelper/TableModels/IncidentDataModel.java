package GUI.RLHelper.TableModels;

import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import javax.swing.table.AbstractTableModel;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;

/**
 *
 * @author Lake and Tristan
 */
public class IncidentDataModel extends AbstractTableModel {

    //private final int[] months;
    private final String[] months;
    //private final String[] incidentTypes;

    private final boolean[] monthVisible;

    private final IncidentData data;

    /**
     *
     * @param data
     * @param dmdData
     */
    public IncidentDataModel(IncidentData data, DemandData dmdData) {

        this.data = data;

        //months = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        //incidentTypes = new String[] {"No Incident", "Shoulder Closure","One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"}
        this.monthVisible = dmdData.getActiveMonths();
    }

    @Override
    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < months.length; ++i) {
            if (monthVisible[i]) {
                ++count;
            }
        }
        return count;
    }

    /**
     *
     * @return
     */
    public int getTotalRowCount() {
        return months.length;
    }

    private int convertRowToMonth(int row) {
        int count = 0;
        for (int i = 0; i < months.length; ++i) {
            if (monthVisible[i]) {
                ++count;
                if (count - 1 == row) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        //return incidentTypes.length + 1;
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //String formatPercent = "%.2f%%";
        String formatFreq = "%.2f";
        if (columnIndex == 0) {
            return months[convertRowToMonth(rowIndex)];
        } else {
            return String.format(formatFreq, data.getIncidentFrequencyMonth(convertRowToMonth(rowIndex)));
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col != 0) {
            return "Frequency";
        } else {
            return "Month";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col != 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col != 0) {
            String input = value.toString();
            try {
                float val = Float.parseFloat(input);
                if (val >= 0.0f) {
                    data.setIncidentFrequencyMonth(convertRowToMonth(row), val);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid Input");
            }
            fireTableCellUpdated(row, col);
        }
    }

    /**
     *
     */
    public void update() {
        this.fireTableChanged(null);
    }
}
