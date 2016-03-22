package GUI.major.outputPanel;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the contour table
 *
 * @author Shu Liu
 */
public class ContourJTableRest extends JTable {

    private final ContourNumAndStringRenderer renderer;

    private final ContourTableModel summaryTableModel;
    /**
     * Speed mph Model
     */
    public final static int MODEL_SPEED = 0;
    /**
     * Total Density in veh/mi/ln Model
     */
    public final static int MODEL_TOTAL_DENSITY_VEH = 1;
    /**
     * Total Density in pc/mi/ln Model
     */
    public final static int MODEL_TOTAL_DENSITY_PC = 2;
    /**
     * Influenced Area Density in pc/mi/ln Model
     */
    public final static int MODEL_IA_DENSITY = 3;
    /**
     * Density Based Level of Service Model
     */
    public final static int MODEL_DENSITY_LOS = 4;
    /**
     * Demand Based Level of Service Model
     */
    public final static int MODEL_DEMAND_LOS = 5;
    /**
     * Demand over Capacity Ratio Model
     */
    public final static int MODEL_DC = 6;
    /**
     * Volume over Capacity Ratio Model
     */
    public final static int MODEL_VC = 7;
    /**
     * Queue Percentage Model
     */
    public final static int MODEL_QUEUE_PERCENTAGE = 8;
    /**
     * Speed mph Model
     */
    public final static int MODEL_SPEED_ML = 9;
    /**
     * Total Density in veh/mi/ln Model
     */
    public final static int MODEL_TOTAL_DENSITY_VEH_ML = 10;
    /**
     * Total Density in pc/mi/ln Model
     */
    public final static int MODEL_TOTAL_DENSITY_PC_ML = 11;
    /**
     * Influenced Area Density in pc/mi/ln Model
     */
    public final static int MODEL_IA_DENSITY_ML = 12;
    /**
     * Density Based Level of Service Model
     */
    public final static int MODEL_DENSITY_LOS_ML = 13;
    /**
     * Demand Based Level of Service Model
     */
    public final static int MODEL_DEMAND_LOS_ML = 14;
    /**
     * Demand over Capacity Ratio Model
     */
    public final static int MODEL_DC_ML = 15;
    /**
     * Volume over Capacity Ratio Model
     */
    public final static int MODEL_VC_ML = 16;
    /**
     * Queue Percentage Model
     */
    public final static int MODEL_QUEUE_PERCENTAGE_ML = 17;

    private Seed seed = null;

    private int scen = 0;

    private int atdm = -1;

    private int zoomColWidth = 75;

    private int zoomRowHeight = 19;

    /**
     * Constructor
     */
    public ContourJTableRest() {
        super();
        this.renderer = new ContourNumAndStringRenderer();
        this.summaryTableModel = new ContourTableModel();
        this.setModel(summaryTableModel);
        //renderer.setColorRange(getMinValue(), getMaxValue());
        this.resetLayout();

        // This will update the cell background color when the user changes a value
        // in the table.
        summaryTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //renderer.setColorRange(getMinValue(), getMaxValue());
                ContourJTableRest.this.repaint();
            }
        });
    }

    public ContourJTableRest(boolean useSoftCoorPalate, boolean useGreyScale) {
        super();
        this.renderer = new ContourNumAndStringRenderer();
        renderer.useSoftColorPalate(useSoftCoorPalate);
        renderer.useGreyScale(useGreyScale);
        this.summaryTableModel = new ContourTableModel();
        this.setModel(summaryTableModel);
        //renderer.setColorRange(getMinValue(), getMaxValue());
        this.resetLayout();

        // This will update the cell background color when the user changes a value
        // in the table.
        summaryTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //renderer.setColorRange(getMinValue(), getMaxValue());
                ContourJTableRest.this.repaint();
            }
        });
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     * @param scen index of scenario to be displayed
     * @param atdm ATDM set index
     */
    public void selectSeedScenATDM(Seed seed, int scen, int atdm) {
        this.seed = seed;
        this.scen = scen;
        this.atdm = atdm;
        //renderer.setColorRange(getMinValue(), getMaxValue());
        summaryTableModel.fireTableStructureChanged();
        this.resetLayout();
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return renderer;
    }

    /**
     * Setter for model type
     *
     * @param model model type
     */
    public void setModel(int model) {
        summaryTableModel.setModel(model);
        this.resetLayout();
    }

    private void resetLayout() {
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setVisible(true);
        this.setRowHeight(zoomRowHeight);
        for (int col = 0; col < this.getColumnCount(); col++) {
            this.getColumnModel().getColumn(col).setMaxWidth(zoomColWidth);
            this.getColumnModel().getColumn(col).setMinWidth(zoomColWidth);
            this.getColumnModel().getColumn(col).setPreferredWidth(zoomColWidth);
        }
        this.setFont(MainWindow.getTableFont());
        this.getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void updateZoom(int zoom, boolean rowHeightExceedsScrollPane, boolean colWidthExceedsScrollPane) {
        if (rowHeightExceedsScrollPane) {
            zoomRowHeight = Math.min(19, Math.max(this.getRowHeight() + zoom, 2));
            this.setRowHeight(zoomRowHeight);
        }
        if (colWidthExceedsScrollPane) {
            for (int col = 0; col < this.getColumnCount(); col++) {
                zoomColWidth = Math.min(75, Math.max(this.getColumnModel().getColumn(col).getWidth() + zoom, 3));
                this.getColumnModel().getColumn(col).setMaxWidth(zoomColWidth);
                this.getColumnModel().getColumn(col).setMinWidth(zoomColWidth);
                this.getColumnModel().getColumn(col).setPreferredWidth(zoomColWidth);
            }

        }
    }

    /**
     * Getter for current displayed model type
     *
     * @return current displayed model type
     */
    public int getModelType() {
        return summaryTableModel.getModelType();
    }

    public float getMaxValue() {
        if (summaryTableModel.getModelType() == MODEL_SPEED || summaryTableModel.getModelType() == MODEL_SPEED_ML) {
            return Math.min(summaryTableModel.getMinValue(), 35.0f);
        } else {
            return summaryTableModel.getMaxValue();
        }
    }

    public float getMinValue() {
        if (summaryTableModel.getModelType() == MODEL_SPEED || summaryTableModel.getModelType() == MODEL_SPEED_ML) {
            return Math.max(summaryTableModel.getMaxValue(), 65.0f);
        } else {
            return summaryTableModel.getMinValue();
        }
    }

    public void setColorRange(float minValue, float maxValue) {
        renderer.setColorRange(minValue, maxValue);
    }

    private class ContourNumAndStringRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
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
            try {
                if (value.toString().equals(CEConst.IDS_NA)) {
                    this.setForeground(Color.darkGray);
                    this.setBackground(Color.darkGray);
                    setText(value.toString());
                } else {
                    switch (((ContourJTableRest) table).getModelType()) {
                        case MODEL_SPEED:
                        case MODEL_TOTAL_DENSITY_VEH:
                        case MODEL_TOTAL_DENSITY_PC:
                        case MODEL_IA_DENSITY:
                        case MODEL_SPEED_ML:
                        case MODEL_TOTAL_DENSITY_VEH_ML:
                        case MODEL_TOTAL_DENSITY_PC_ML:
                        case MODEL_IA_DENSITY_ML:
                            tryFloat_1f(value.toString());
                            try {
                                this.setBackground(getColor(Float.parseFloat(value.toString())));
                            } catch (Exception e) {
                                System.out.println(value.toString() + " " + e.toString());
                            }
                            break;
                        case MODEL_DENSITY_LOS:
                        case MODEL_DEMAND_LOS:
                        case MODEL_DENSITY_LOS_ML:
                        case MODEL_DEMAND_LOS_ML:
                            this.setBackground(getColor(LOSvalue(value.toString())));
                            setText(value.toString());
                            break;
                        case MODEL_DC:
                        case MODEL_VC:
                        case MODEL_DC_ML:
                        case MODEL_VC_ML:
                            tryFloat_2f(value.toString());
                            try {
                                this.setBackground(getColor(Float.parseFloat(value.toString())));
                            } catch (Exception e) {
                                System.out.println(value.toString());
                            }
                            break;
                        case MODEL_QUEUE_PERCENTAGE:
                        case MODEL_QUEUE_PERCENTAGE_ML:
                            tryPercent_0f(value.toString());
                            try {
                                this.setBackground(getColor(Float.parseFloat(value.toString())));
                            } catch (Exception e) {
                                System.out.println(value.toString());
                            }
                            break;
                        default:
                            setText(value.toString());
                    }
                }
            } catch (IllegalArgumentException e2) {
                setText(value.toString());
            }

            return this;
        }

        private void tryInt(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Integer.parseInt(value)));
        }

        private void tryFloat_2f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_1f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.0");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_0f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryPercent_2f(String value) {
            DecimalFormat formatter = new DecimalFormat("#.00%");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryPercent_0f(String value) {
            DecimalFormat formatter = new DecimalFormat("#%");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private float LOSvalue(String LOS) {
            try {
                switch (LOS.toUpperCase().charAt(0)) {
                    case 'B':
                        return 80;
                    case 'C':
                        return 60;
                    case 'D':
                        return 40;
                    case 'E':
                        return 20;
                    case 'F':
                        return 0;
                    default: //'A'
                        return 100;
                }
            } catch (Exception e) {
                return 100;
            }
        }

        private Color getColor(float cellValue) {
            if ((minVal - maxVal) * (minVal - maxVal) < 10e-4) {
                return Color.white;
            } else {
                if (cellValue < minVal) {
                    return minColor;
                }
                if (cellValue < midVal) {
                    float p = (cellValue - minVal) / (midVal - minVal);
                    return new Color(rMin * (1.0f - p) + rMid * p,
                            gMin * (1.0f - p) + gMid * p,
                            bMin * (1.0f - p) + bMid * p,
                            0.7f);
                } else if (cellValue <= maxVal) {
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
         * Set color range
         *
         * @param newMin minimum value
         * @param newMax maximum value
         */
        public void setColorRange(float newMin, float newMax) {
            if (newMin <= newMax) {
                minVal = newMin;
                midVal = (newMin + newMax) / 2.0f;
                maxVal = newMax;

                if (useSoftColors) {
                    minColor = MIN_COLOR_SOFT;
                    midColor = MID_COLOR_SOFT;
                    maxColor = MAX_COLOR_SOFT;
                } else if (useGreyScale) {
                    minColor = MIN_COLOR_GS;
                    midColor = MID_COLOR_GS;
                    maxColor = MAX_COLOR_GS;
                } else {
                    minColor = MIN_COLOR;
                    midColor = MID_COLOR;
                    maxColor = MAX_COLOR;
                }
                //minColor = useSoftColors ? MIN_COLOR_SOFT : MIN_COLOR;
                //midColor = useSoftColors ? MID_COLOR_SOFT : MID_COLOR;
                //maxColor = useSoftColors ? MAX_COLOR_SOFT : MAX_COLOR;

                calcScale();
            } else {
                minVal = newMax;
                midVal = (newMin + newMax) / 2.0f;
                maxVal = newMin;

                if (useSoftColors) {
                    minColor = MAX_COLOR_SOFT;
                    midColor = MID_COLOR_SOFT;
                    maxColor = MIN_COLOR_SOFT;
                } else if (useGreyScale) {
                    minColor = MAX_COLOR_GS;
                    midColor = MID_COLOR_GS;
                    maxColor = MIN_COLOR_GS;
                } else {
                    minColor = MAX_COLOR;
                    midColor = MID_COLOR;
                    maxColor = MIN_COLOR;
                }

                //minColor = useSoftColors ? MAX_COLOR_SOFT : MAX_COLOR;
                //midColor = useSoftColors ? MID_COLOR_SOFT : MID_COLOR;
                //maxColor = useSoftColors ? MIN_COLOR_SOFT : MIN_COLOR;
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

        public void useSoftColorPalate(boolean val) {
            this.useSoftColors = val;
        }

        public void useGreyScale(boolean val) {
            this.useGreyScale = val;
        }

        private float minVal = 0.0f;

        private float midVal = 0.0f;

        private float maxVal = 0.0f;

        private final Color MIN_COLOR = Color.green;
        private final Color MIN_COLOR_SOFT = new Color(102, 255, 102);
        private final Color MID_COLOR = Color.yellow;
        private final Color MID_COLOR_SOFT = new Color(255, 255, 102);
        private final Color MAX_COLOR = Color.red;
        private final Color MAX_COLOR_SOFT = new Color(255, 102, 102);

        private final Color MIN_COLOR_GS = Color.WHITE;
        private final Color MID_COLOR_GS = Color.lightGray;
        private final Color MAX_COLOR_GS = Color.gray;

        private boolean useSoftColors = false;

        private boolean useGreyScale = false;

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
    }

    private class ContourTableModel extends AbstractTableModel {

        private int modelType = MODEL_SPEED;

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
            if (seed != null && seed.hasValidOutput(scen, atdm)) {
                switch (modelType) {
                    case MODEL_SPEED:
                        return seed.getValueString(CEConst.IDS_SPEED, column, row, scen, atdm);
                    case MODEL_TOTAL_DENSITY_VEH:
                        return seed.getValueString(CEConst.IDS_TOTAL_DENSITY_VEH, column, row, scen, atdm);
                    case MODEL_TOTAL_DENSITY_PC:
                        return seed.getValueString(CEConst.IDS_TOTAL_DENSITY_PC, column, row, scen, atdm);
                    case MODEL_IA_DENSITY:
                        return seed.getValueString(CEConst.IDS_INFLUENCED_DENSITY_PC, column, row, scen, atdm);
                    case MODEL_DENSITY_LOS:
                        return seed.getValueString(CEConst.IDS_DENSITY_BASED_LOS, column, row, scen, atdm);
                    case MODEL_DEMAND_LOS:
                        return seed.getValueString(CEConst.IDS_DEMAND_BASED_LOS, column, row, scen, atdm);
                    case MODEL_DC:
                        return seed.getValueString(CEConst.IDS_DC, column, row, scen, atdm);
                    case MODEL_VC:
                        return seed.getValueString(CEConst.IDS_VC, column, row, scen, atdm);
                    case MODEL_QUEUE_PERCENTAGE:
                        return seed.getValueString(CEConst.IDS_QUEUE_PERCENTAGE, column, row, scen, atdm);
                    case MODEL_SPEED_ML:
                        return seed.getValueString(CEConst.IDS_ML_SPEED, column, row, scen, atdm);
                    case MODEL_TOTAL_DENSITY_VEH_ML:
                        return seed.getValueString(CEConst.IDS_ML_TOTAL_DENSITY_VEH, column, row, scen, atdm);
                    case MODEL_TOTAL_DENSITY_PC_ML:
                        return seed.getValueString(CEConst.IDS_ML_TOTAL_DENSITY_PC, column, row, scen, atdm);
                    case MODEL_IA_DENSITY_ML:
                        return seed.getValueString(CEConst.IDS_ML_INFLUENCED_DENSITY_PC, column, row, scen, atdm);
                    case MODEL_DENSITY_LOS_ML:
                        return seed.getValueString(CEConst.IDS_ML_DENSITY_BASED_LOS, column, row, scen, atdm);
                    case MODEL_DEMAND_LOS_ML:
                        return seed.getValueString(CEConst.IDS_ML_DEMAND_BASED_LOS, column, row, scen, atdm);
                    case MODEL_DC_ML:
                        return seed.getValueString(CEConst.IDS_ML_DC, column, row, scen, atdm);
                    case MODEL_VC_ML:
                        return seed.getValueString(CEConst.IDS_ML_VC, column, row, scen, atdm);
                    case MODEL_QUEUE_PERCENTAGE_ML:
                        return seed.getValueString(CEConst.IDS_ML_QUEUE_PERCENTAGE, column, row, scen, atdm);
                    default:
                        return "Error";
                }
            } else {
                return CEConst.IDS_NA;
            }
        }

        @Override
        public String getColumnName(int column) {
            return "Seg. " + (column + 1);
        }

        public void setModel(int model) {
            this.modelType = model;
            updateColorRange();
            this.fireTableStructureChanged();
        }

        private void updateColorRange() {
            switch (modelType) {
                case ContourJTableRest.MODEL_SPEED:
                case ContourJTableRest.MODEL_SPEED_ML:
                    renderer.setColorRange(70, 20);
                    break;
                case ContourJTableRest.MODEL_DC:
                case ContourJTableRest.MODEL_VC:
                case ContourJTableRest.MODEL_DC_ML:
                case ContourJTableRest.MODEL_VC_ML:
                    renderer.setColorRange(0.2f, 1.0f);
                    break;
                case ContourJTableRest.MODEL_QUEUE_PERCENTAGE:
                case ContourJTableRest.MODEL_QUEUE_PERCENTAGE_ML:
                    renderer.setColorRange(0.0f, 0.2f);
                    break;
                case ContourJTableRest.MODEL_TOTAL_DENSITY_VEH:
                case ContourJTableRest.MODEL_TOTAL_DENSITY_VEH_ML:
                case ContourJTableRest.MODEL_TOTAL_DENSITY_PC:
                case ContourJTableRest.MODEL_TOTAL_DENSITY_PC_ML:
                case ContourJTableRest.MODEL_IA_DENSITY:
                case ContourJTableRest.MODEL_IA_DENSITY_ML:
                    renderer.setColorRange(11.5f, 45.5f);
                    break;
                case ContourJTableRest.MODEL_DEMAND_LOS:
                case ContourJTableRest.MODEL_DENSITY_LOS:
                case ContourJTableRest.MODEL_DEMAND_LOS_ML:
                case ContourJTableRest.MODEL_DENSITY_LOS_ML:
                    renderer.setColorRange(100, 0);
                    break;
            }
        }

        public int getModelType() {
            return modelType;
        }

        public float getMaxValue() {
            float max = 0;
            float value;
            for (int row = 0; row < getRowCount(); row++) {
                for (int col = 0; col < getColumnCount(); col++) {
                    try {
                        value = Float.parseFloat(getValueAt(row, col).toString());
                        if (value > max) {
                            max = value;
                        }
                    } catch (Exception e) {
                        //skip this value
                    }
                }
            }
            return max;
        }

        public float getMinValue() {
            float min = 100;
            float value;
            for (int row = 0; row < getRowCount(); row++) {
                for (int col = 0; col < getColumnCount(); col++) {
                    try {
                        value = Float.parseFloat(getValueAt(row, col).toString());
                        if (value < min) {
                            min = value;
                        }
                    } catch (Exception e) {
                        //skip this value
                    }
                }
            }
            return min;
        }
    }
}
