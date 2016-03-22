package GUI.ATDMHelper.rampMetering;

import GUI.RLHelper.TableSelectionCellEditor;
import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the contour table
 *
 * @author Shu Liu
 */
public class RMContourJTableRest extends JTable {

    private final ContourNumAndStringRenderer renderer;

    private final ContourTableModel summaryTableModel;

    private final JTextField textFieldForCellEditor = new JTextField();

    private final TableSelectionCellEditor defaultCellEditor = new TableSelectionCellEditor(true);

    private Seed seed = null;

    private int numSeg;

    private final HashMap<Integer, Integer> colToSegMap = new HashMap();

    private final HashMap<Integer, Integer> segToColMap = new HashMap();

    private CA2DInt rampMeteringRate;

    private final float minValue = 0;

    private float maxValue = 1;

    /**
     * Constructor
     *
     * @param seed
     */
    public RMContourJTableRest(Seed seed) {
        super();

        this.seed = seed;

        rampMeteringRate = new CA2DInt(seed.getValueInt(CEConst.IDS_NUM_SEGMENT), seed.getValueInt(CEConst.IDS_NUM_PERIOD), 2100);

        configTable();

        renderer = new ContourNumAndStringRenderer();
        //setColorRange(minValue, maxValue); // Deprecated, hard ranges now set for the color range
        setColorRange(0.2f, 1.0f);

        textFieldForCellEditor.setHorizontalAlignment(JTextField.CENTER);
        textFieldForCellEditor.setBorder(null);
        textFieldForCellEditor.setFont(MainWindow.getTableFont());

        summaryTableModel = new ContourTableModel();
        setModel(summaryTableModel);

        resetLayout();
    }

    private void configTable() {
        colToSegMap.clear();
        numSeg = 0;
        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_ONR || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_W) {
                colToSegMap.put(numSeg, seg);
                segToColMap.put(seg, numSeg);
                numSeg++;
            }
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
    public TableCellEditor getCellEditor(int row, int column) {
        //textFieldForCellEditor.setBackground(getColor(seed.getValueFloat(CEConst.IDS_DC, segColMap.get(column), row)));
        return defaultCellEditor;
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

    private class ContourNumAndStringRenderer extends DefaultTableCellRenderer {

        /**
         *
         */
        public ContourNumAndStringRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
            calcScale();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            this.setForeground(null);
            this.setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            setText(value.toString());
            this.setBackground(getColor(seed.getValueFloat(CEConst.IDS_DC, colToSegMap.get(column), row)));

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
            return seed == null ? 0 : numSeg;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return rampMeteringRate.get(colToSegMap.get(column), row);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                int val = Integer.parseInt(aValue.toString());
                if (val >= 0) {
                    rampMeteringRate.set(val, colToSegMap.get(columnIndex), rowIndex);
                }
            } catch (Exception e) {

            }
        }

        @Override
        public String getColumnName(int column) {
            return "Seg. " + (colToSegMap.get(column) + 1);
        }
    }

    /**
     *
     * @return
     */
    public CA2DInt getRampMeteringRate() {
        return rampMeteringRate;
    }

    /**
     *
     * @param rampMeteringRate
     */
    public void setRampMeteringRate(CA2DInt rampMeteringRate) {
        this.rampMeteringRate = rampMeteringRate;
    }

    /**
     *
     * @param value
     * @param startPeriod
     * @param endPeriod
     * @param startSegment
     * @param endSegment
     */
    public void fill(int value, int startPeriod, int endPeriod, int startSegment, int endSegment) {
        for (int col = segToColMap.get(startSegment - 1); col <= segToColMap.get(endSegment - 1); col++) {
            for (int row = startPeriod - 1; row <= endPeriod - 1; row++) {
                summaryTableModel.setValueAt(value, row, col);
            }
        }
        summaryTableModel.fireTableDataChanged();
    }

    /**
     *
     * @param value
     * @param period
     */
    public void fillPeriod(int value, int period) {
        for (int col = 0; col < summaryTableModel.getColumnCount(); col++) {
            summaryTableModel.setValueAt(value, period, col);
        }
        summaryTableModel.fireTableDataChanged();
    }

    /**
     *
     * @param value
     * @param seg
     */
    public void fillSeg(int value, int seg) {
        for (int row = 0; row < summaryTableModel.getRowCount(); row++) {
            summaryTableModel.setValueAt(value, row, segToColMap.get(seg));
        }
        summaryTableModel.fireTableDataChanged();
    }
}
