package GUI.RLHelper.TableModels;

import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lake
 */
public class IncidentDistDataModel extends AbstractTableModel {

    private final String[] incidentTypes;

    private final IncidentData data;

    /**
     *
     * @param data
     */
    public IncidentDistDataModel(IncidentData data) {

        this.data = data;
        switch (data.getModelType()) {
            default:
            case IncidentData.TYPE_GP:
                incidentTypes = new String[]{"Shoulder Closure", "One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"};
                break;
            case IncidentData.TYPE_ML:
                //incidentTypes = new String[]{"Shoulder Closure", "One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"};
                incidentTypes = new String[]{"Shoulder Closure", "One Lane Closure"};
                break;
        }

    }

    @Override
    public int getRowCount() {
        return incidentTypes.length;
    }

    @Override
    public int getColumnCount() {
        //return incidentTypes.length + 1;
        return 6;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            default:
            case 0:
                return incidentTypes[row];
            case 1:
                return data.getIncidentDistribution(row);
            case 2:
                return data.getIncidentDuration(row);
            case 3:
                return data.getIncidentDurationStdDev(row);
            case 4:
                return data.getIncidentDurMin(row);
            case 5:
                return data.getIncidentDurMax(row);
        }
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            default:
            case 0:
                return "<HTML><CENTER>Incident<br>Severity";
            case 1:
                return "<HTML><CENTER>Distribution<br>%";
            case 2:
                return "<HTML><CENTER>Mean<br>Duration";
            case 3:
                return "Std. Dev.";
            case 4:
                return "<HTML><CENTER>Minimum<br>Duration";
            case 5:
                return "<HTML><CENTER>Maximum<br>Duration";

        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col != 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            float val = Float.parseFloat(value.toString());
            if (val >= 0.0f) {
                switch (col) {
                    case 1:
                        data.setIncidentDistribution(row, val);
                        break;
                    case 2:
                        data.setIncidentDuration(row, val);
                        break;
                    case 3:
                        data.setIncidentDurationStdDev(row, val);
                        break;
                    case 4:
                        data.setIncidentDurMin(row, val);
                        break;
                    case 5:
                        data.setIncidentDurMax(row, val);
                    default:
                }
            }
        } catch (NumberFormatException e) {

        }
    }

//    @Override
//    public void fireTableDataChanged() {
//        super.fireTableDataChanged();
//
//    }
}
