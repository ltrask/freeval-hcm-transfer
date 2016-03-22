package GUI.RLHelper.TableModels;

import coreEngine.reliabilityAnalysis.DataStruct.WeatherData;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake and tristan
 */
public class WeatherDataModel extends AbstractTableModel {

    private final int tableType;

    private final int numCols;

    private final String[] monthString;

    private final String[] adjFactorString;

    private final String[] weatherString;

    private final boolean[] monthVisible;

    private final WeatherData data;

    /**
     *
     * @param data
     * @param tableType
     */
    public WeatherDataModel(WeatherData data, int tableType) {

        this.data = data;

        // Type has to be 1 or 2
        if (tableType == 2) {
            this.tableType = 2;
        } else {
            this.tableType = 1;
        }

        numCols = 12;
        monthString = new String[]{
            "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"
        };
        adjFactorString = new String[]{
            "Avg Dur (min)", "CAF", "SAF", "DAF"
        };
//        weatherString = new String[]{
//            "Med Rain", "Heavy Rain", "Light Snow", "LM Snow", "MH Snow", "Heavy Snow",
//            "Severe Cold", "Low Vis", "Very Low Vis", "Min Vis", "<HTML><CENTER>Normal<br>Weather"
//        };

        weatherString = new String[]{
            "Med Rain", "<HTML><CENTER>Heavy<br>Rain", "<HTML><CENTER>Light<br>Snow", "LM Snow", "MH Snow", "<HTML><CENTER>Heavy<br>Snow",
            "<HTML><CENTER>Severe<br>Cold", "Low Vis", "<HTML><CENTER>Very Low<br>Vis", "Min Vis", "<HTML><CENTER>Normal<br>Weather"
        };

        monthVisible = new boolean[12];
        initializeVisibleMonths();

    }

    @Override
    public int getRowCount() {
        if (tableType == 1) {
            int count = 0;
            for (int i = 0; i < 12; ++i) {
                if (monthVisible[i]) {
                    ++count;
                }
            }
            return count;
        } else {
            return 4;
        }
    }

    @Override
    public int getColumnCount() {

        return numCols;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col != 0) {
            if (tableType == 1) {
                return data.getProbability(convertRowToMonth(row), col - 1);
            } else if (row == 0) {
                if (col != 11) {
                    return data.getAverageDurationMinutes(col - 1);
                } else {
                    return "";
                }
            } else {
                switch (row) {
                    case 1:
                        data.getAdjustmentFactor(WeatherData.AF_TYPE_CAF, col - 1);
                        break;
                    case 2:
                        data.getAdjustmentFactor(WeatherData.AF_TYPE_SAF, col - 1);
                        break;
                    case 3:
                        data.getAdjustmentFactor(WeatherData.AF_TYPE_DAF, col - 1);
                        break;
                    default:
                        break;

                }
                return data.getAdjustmentFactor(row - 1, col - 1);
            }
        } else if (tableType == 1) {
            return monthString[convertRowToMonth(row)];
        } else {
            return adjFactorString[row];
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col != 0) {
            return weatherString[col - 1];
        } else {
            return "<HTML>&nbsp<br>&nbsp";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col != 0 && col != 11) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (tableType == 1) { // For updating the upper table
            if (col != 0) {
                String input = value.toString();
                try {
                    float val = Float.parseFloat(input);
                    data.setValue(convertRowToMonth(row), col - 1, val);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input");
                }
                fireTableCellUpdated(row, col);
                fireTableCellUpdated(row, 11);
            }
        } else {  //Updating the lower table
            if (col != 0) {
                String input = value.toString();
                try {
                    float val = Float.parseFloat(input);
                    switch (row) {
                        case 1:
                            data.setAdjustmentFactor(WeatherData.AF_TYPE_CAF, col - 1, val);
                            break;
                        case 2:
                            data.setAdjustmentFactor(WeatherData.AF_TYPE_SAF, col - 1, val);
                            break;
                        case 3:
                            data.setAdjustmentFactor(WeatherData.AF_TYPE_DAF, col - 1, val);
                            break;
                        case 0:
                            if (col != 11) {
                                data.getAverageDurationMinutes()[col - 1] = val;
                            }
                            break;
                        default:
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input");
                }
                fireTableCellUpdated(row, col);
                //fireTableCellUpdated(row,11);
            }

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
