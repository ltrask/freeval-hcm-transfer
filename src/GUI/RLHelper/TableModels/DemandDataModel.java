package GUI.RLHelper.TableModels;

import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author lake and tristan
 */
public class DemandDataModel extends AbstractTableModel {

    private final String[] weekdayString;

    private final boolean[] weekdayVisible;

    private final String[] monthString;

    private final boolean[] monthVisible;

    private final DemandData data;

    /**
     *
     * @param data
     */
    public DemandDataModel(DemandData data) {
        this.data = data;
        weekdayString = new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        };
        weekdayVisible = new boolean[7];

        for (int i = 0; i < data.getActiveDays().length; i++) {
            weekdayVisible[i] = data.getActiveDays()[i];
        }

        monthString = new String[]{
            "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"
        };
        monthVisible = new boolean[12];

        //showAllWeekdays();
        initializeVisibleMonths();
    }

    @Override
    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < 12; ++i) {
            if (monthVisible[i]) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        int count = 1;
        for (int i = 0; i < 7; ++i) {
            if (weekdayVisible[i]) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (columnIndex != 0) {
            return data.getValue(convertRowToMonth(rowIndex), convertColumnToDay(columnIndex - 1));
        } else {
            return monthString[convertRowToMonth(rowIndex)];
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col != 0) {
            return weekdayString[convertColumnToDay(col - 1)];
        } else {
            return "";
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
                data.setValue(convertRowToMonth(row), convertColumnToDay(col - 1), val);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
            fireTableCellUpdated(row, col);
        }
    }

    /**
     *
     */
    public final void showAllDays() {
        data.useAllDays();
        for (int i = 0; i < 7; ++i) {
            weekdayVisible[i] = data.getDayActive(i);
        }
        fireTableStructureChanged();
    }

    /**
     *
     */
    public final void showAllWeekdays() {
        data.useWeekDays();
        for (int i = 0; i < 7; ++i) {
            weekdayVisible[i] = data.getDayActive(i);
        }
        fireTableStructureChanged();
    }

    /**
     *
     */
    public final void showAllMonths() {
        data.useAllMonths();
        for (int i = 0; i < 12; ++i) {
            monthVisible[i] = data.getMonthActive(i);
        }
        fireTableStructureChanged();
    }

    /**
     *
     * @param day
     * @param visible
     */
    public final void setWeekdayVisible(int day, boolean visible) {
        if (day >= 0 && day < 7) {
            data.setDayActive(day, visible);
            weekdayVisible[day] = data.getDayActive(day);
            fireTableStructureChanged();
        }
    }

    /**
     *
     * @param month
     * @param visible
     */
    public final void setMonthVisible(int month, boolean visible) {
        if (month >= 0 && month < 12) {
            data.setMonthActive(month, visible);
            monthVisible[month] = data.getMonthActive(month);
            fireTableStructureChanged();
        }
    }

    private void initializeVisibleMonths() {
        for (int i = 0; i < 12; ++i) {
            monthVisible[i] = data.getMonthActive(i);
        }
    }

    private int convertColumnToDay(int col) {
        int count = 0;
        for (int i = 0; i < 7; ++i) {
            if (weekdayVisible[i]) {
                ++count;
                if (count - 1 == col) {
                    return i;
                }
            }
        }
        return 0;
    }

    private int convertRowToMonth(int row) {
        int count = 0;
        for (int i = 0; i < 12; ++i) {
            if (monthVisible[i]) {
                ++count;
                if (count - 1 == row) {
                    return i;
                }
            }
        }
        return 0;
    }

}
