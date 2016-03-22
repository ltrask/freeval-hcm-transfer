package GUI.ATDMHelper.hardShoulderRunning;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the contour table
 *
 * @author Shu Liu
 * @author Lake Trask
 */
public class HSRContourJTableRest extends JTable {

    private final ContourBooleanRenderer renderer;

    private final ContourTableModel summaryTableModel;

    private Seed seed = null;

    private CA2DInt hardShoulder;

    private final float minValue = 0;

    private float maxValue = 1;

    /**
     * Constructor
     *
     * @param seed seed instance
     */
    public HSRContourJTableRest(Seed seed) {
        super();

        this.seed = seed;

        hardShoulder = new CA2DInt(seed.getValueInt(CEConst.IDS_NUM_SEGMENT), seed.getValueInt(CEConst.IDS_NUM_PERIOD), 0);

        configTable();

        renderer = new ContourBooleanRenderer();
        //setColorRange(minValue, maxValue);  // Deprecated, hard ranges now set for the color range
        setColorRange(0.2f, 1.0f);

        summaryTableModel = new ContourTableModel();
        setModel(summaryTableModel);

        resetLayout();
    }

    private void configTable() {
        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                maxValue = Math.max(maxValue, seed.getValueFloat(CEConst.IDS_DC, seg, period));
            }
        }
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    private void resetLayout() {
        this.setAutoResizeMode(AUTO_RESIZE_OFF);
        this.setVisible(true);
        this.setRowHeight(19);
        this.setFont(MainWindow.getTableFont());
        this.getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private class ContourBooleanRenderer extends JCheckBox implements TableCellRenderer {

        public ContourBooleanRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
            calcScale();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            this.setSelected((boolean) value);
            this.setBackground(getColor(seed.getValueFloat(CEConst.IDS_DC, column, row)));

            return this;
        }

    }

    private Color getColor(float cellValue) {
        if (minVal == maxVal) {
            return Color.white;
        } else {

            if (cellValue < midVal) {
                float p = (cellValue - minVal) / (midVal - minVal);
                return new Color(rMin * (1.0f - p) + rMid * p,
                        gMin * (1.0f - p) + gMid * p,
                        bMin * (1.0f - p) + bMid * p,
                        0.7f);
            } else {
                float p = (cellValue - midVal) / (maxVal - midVal);
                return new Color(rMid * (1.0f - p) + rMax * p,
                        gMid * (1.0f - p) + gMax * p,
                        bMid * (1.0f - p) + bMax * p,
                        0.7f);
            }
        }
    }

    private void setColorRange(float newMin, float newMax) {
        if (newMin <= newMax) {
            minVal = newMin;
            midVal = (newMin + newMax) / 2.0f;
            maxVal = newMax;

            minColor = MIN_COLOR;
            midColor = MID_COLOR;
            maxColor = MAX_COLOR;

            calcScale();
        } else {
            minVal = newMax;
            midVal = (newMin + newMax) / 2.0f;
            maxVal = newMin;

            minColor = MAX_COLOR;
            midColor = MID_COLOR;
            maxColor = MIN_COLOR;

            calcScale();
        }
    }

    private void calcScale() {
        rMin = minColor.getRed() / 255.0f;
        rMid = midColor.getRed() / 255.0f;
        rMax = maxColor.getRed() / 255.0f;
        gMin = minColor.getGreen() / 255.0f;
        gMid = midColor.getGreen() / 255.0f;
        gMax = maxColor.getGreen() / 255.0f;
        bMin = minColor.getBlue() / 255.0f;
        bMid = midColor.getBlue() / 255.0f;
        bMax = maxColor.getBlue() / 255.0f;
    }

    private float minVal = 0.0f;

    private float midVal = 0.0f;

    private float maxVal = 0.0f;

    private final Color MIN_COLOR = Color.green;

    private final Color MID_COLOR = Color.yellow;

    private final Color MAX_COLOR = Color.red;

    private Color minColor = MIN_COLOR;

    private Color midColor = MID_COLOR;

    private Color maxColor = MAX_COLOR;

    private float rMin;

    private float rMid;

    private float rMax;

    private float gMin;

    private float gMid;

    private float gMax;

    private float bMin;

    private float bMid;

    private float bMax;

    private class ContourTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        }

        @Override
        public int getColumnCount() {
            return seed == null ? 0 : seed.getValueInt(CEConst.IDS_NUM_SEGMENT);
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (hardShoulder.get(column, row) == 1) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                if ((boolean) aValue) {
                    hardShoulder.set(1, columnIndex, rowIndex);
                } else {
                    hardShoulder.set(0, columnIndex, rowIndex);
                }
            } catch (Exception e) {
                System.out.println("Excepted");
            }
        }

        @Override
        public String getColumnName(int column) {
            return "Seg. " + (column + 1);
        }

        @Override
        public Class getColumnClass(int column) {
            return (getValueAt(0, column).getClass());
        }

    }

    /**
     * Getter for hard shoulder running
     *
     * @return hard shoulder running matrix
     */
    public CA2DInt getHardShoulder() {
        return hardShoulder;
    }

    /**
     * Setter for hard shoulder running
     *
     * @param hardShoulder hard shoulder running matrix
     */
    public void setHardShoulder(CA2DInt hardShoulder) {
        this.hardShoulder = hardShoulder;
    }

    /**
     * Fill a part of the table
     *
     * @param value value to be filled
     * @param startPeriod start period index (inclusive)
     * @param endPeriod end period index (inclusive)
     * @param startSegment start segment index (inclusive)
     * @param endSegment end segment index (inclusive)
     */
    public void fill(int value, int startPeriod, int endPeriod, int startSegment, int endSegment) {
        for (int col = startSegment - 1; col <= endSegment - 1; col++) {
            for (int row = startPeriod - 1; row <= endPeriod - 1; row++) {
                summaryTableModel.setValueAt((value == 1), row, col);
            }
        }
        summaryTableModel.fireTableDataChanged();
    }

}
