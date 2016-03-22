package GUI.ATDMHelper;

import GUI.ATDMHelper.TableModels.ATDMModel;
import GUI.RLHelper.summary.ScenarioSummaryTableModel;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lake Trask
 */
public class ScenarioSummaryRenderer extends DefaultTableCellRenderer {

    //private int firstColumnAlignment = JTextField.LEFT;
    //private boolean firstColumnBold = false;
    private boolean useTTIcontour = true;

    private float minVal = 0.0f;

    private float midVal = 0.0f;

    private float maxVal = 0.0f;
    //private final Color minColor = Color.green;

    private final Color minColor = new Color(102, 255, 102);
    //private final Color midColor = Color.yellow;

    private final Color midColor = new Color(255, 255, 102);
    //private final Color maxColor = Color.red;

    private final Color maxColor = new Color(255, 102, 102);

    private final float rMin = minColor.getRed() / 255.0f;

    private final float rMid = midColor.getRed() / 255.0f;

    private final float rMax = maxColor.getRed() / 255.0f;

    private final float gMin = minColor.getGreen() / 255.0f;

    private final float gMid = midColor.getGreen() / 255.0f;

    private final float gMax = maxColor.getGreen() / 255.0f;

    private final float bMin = minColor.getBlue() / 255.0f;

    private final float bMid = midColor.getBlue() / 255.0f;

    private final float bMax = maxColor.getBlue() / 255.0f;

    private final int tableType;

    private final int ttiColumn;

    private final int vhdColumn;

    public static final int TYPE_RL_SUMMARY = 0;
    public static final int TYPE_ATDM = 1;

    public ScenarioSummaryRenderer(int tableType) {
        if (tableType == TYPE_RL_SUMMARY || tableType == TYPE_ATDM) {
            this.tableType = tableType;
        } else {
            throw new RuntimeException("Invalid Table Type Specification");
        }

        switch (tableType) {
            default:
            case TYPE_RL_SUMMARY:
                ttiColumn = ScenarioSummaryTableModel.COL_MAX_TTI;
                vhdColumn = ScenarioSummaryTableModel.COL_MAX_VHD;
                break;
            case TYPE_ATDM:
                ttiColumn = ATDMModel.COL_MAX_TTI;
                vhdColumn = ATDMModel.COL_MAX_VHD;
                break;
        }

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setForeground(null);
        setBackground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == ttiColumn && useTTIcontour) {
//            try {
            float cellValue = Float.parseFloat(value.toString());
            Color color = getColor(cellValue);
            if (color != null) {
                setBackground(color);
            }
//            } catch (NumberFormatException e) {
//                //setBackground(Color.white);
//                //super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                setBackground(null);
//            }
            if (hasFocus) {
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            } else {
                setBorder(null);
            }
        }
//        } else {
//            setBackground(null);
//            //setBackground(super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, 1).getBackground());
//            //super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        }

        if (value != null) {
            if (column == vhdColumn || column == ttiColumn) {
                setText(tryFloat_2f(Float.parseFloat(value.toString())));
            } else {
                setText(value.toString());
            }
        }

        return this;
    }

    private Color getColor(float cellValue) {

        if (minVal == maxVal || cellValue == 0) {
            return null;
        } else {

            if (cellValue < midVal) {
                float p = (cellValue - minVal) / (midVal - minVal);
                return new Color(rMin * (1.0f - p) + rMid * p,
                        gMin * (1.0f - p) + gMid * p,
                        bMin * (1.0f - p) + bMid * p,
                        0.7f);
            } else if (cellValue < maxVal) {
                float p = (cellValue - midVal) / (maxVal - midVal);
                return new Color(rMid * (1.0f - p) + rMax * p,
                        gMid * (1.0f - p) + gMax * p,
                        bMid * (1.0f - p) + bMax * p,
                        0.7f);
            } else {
                return maxColor;
            }
        }
    }

    /**
     *
     * @param newMin
     * @param newMax
     */
    public void setColorRange(float newMin, float newMax) {
        if (newMin <= newMax) {
            minVal = newMin;
            midVal = (newMin + newMax) / 2.0f;
            maxVal = newMax;
        }
    }

    /**
     *
     * @param useContour
     */
    public void setTTIcontour(boolean useContour) {
        useTTIcontour = useContour;
    }

    private String tryFloat_2f(float value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(value);
    }
}
