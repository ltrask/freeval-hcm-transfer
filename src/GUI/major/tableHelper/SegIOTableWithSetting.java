package GUI.major.tableHelper;

import GUI.major.MainWindow;
import GUI.major.Toolbox;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.RampMeteringData.RampMeteringALINEAData;
import coreEngine.Helper.RampMeteringData.RampMeteringFuzzyData;
import coreEngine.Seed;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class is contains tables for segment input and output
 *
 * @author Shu Liu
 */
public class SegIOTableWithSetting implements FREEVAL_TableWithSetting {

    // <editor-fold defaultstate="collapsed" desc="CORE FUNCTIONS">
    private final JPopupMenu tooltip;

    private SensorSet sensorSet;

    private boolean isSensorView;

    /**
     * Constructor of Input/Output Table. Create 2 table models and 2 table
     */
    public SegIOTableWithSetting() {
        _resetCellSettings();

        firstColumnModel = new SegIOTableModel(true, this);
        firstColumnTable = new FREEVAL_JTable(firstColumnModel);

        restColumnModel = new SegIOTableModel(false, this);
        restColumnTable = new FREEVAL_JTable(restColumnModel);

        restColumnTable.setColumnSelectionAllowed(true);
        restColumnTable.setRowSelectionAllowed(false);

        textFieldForCellEditor.setHorizontalAlignment(JTextField.CENTER);
        textFieldForCellEditor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldForCellEditor.selectAll();
            }
        });
        textFieldForCellEditor.setBorder(null);
        textFieldForCellEditor.setFont(MainWindow.getTableFont());

        restColumnTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tableMouseMoved(evt);
            }
        });

        tooltip = new JPopupMenu();
    }

    private void tableMouseMoved(MouseEvent evt) {
        try {
            //int column = restColumnTable.columnAtPoint(evt.getPoint());
            int row = restColumnTable.rowAtPoint(evt.getPoint());
            tooltip.removeAll();
            //int truckPCErow = restColum.findColumn();
            if (((String) firstColumnModel.getValueAt(row, 0)).equalsIgnoreCase("Truck-PC Equivalent (ET)")) {
                JTextArea tip = new JTextArea(
                        " If the Truck-PC Equivalent needs to be calculated \n"
                        + " or is unknown, please consult the tables found in \n"
                        + " the Help drop down menu. ");
                tip.setEditable(false);
                tooltip.add(tip);
                tooltip.show(restColumnTable, evt.getX() + 15, evt.getY());
            } else {
                tooltip.setVisible(false);
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * Getter for first column table
     *
     * @return first column table
     */
    @Override
    public FREEVAL_JTable getFirstColumnTable() {
        return firstColumnTable;
    }

    /**
     * Getter for rest column table
     *
     * @return rest column table
     */
    @Override
    public FREEVAL_JTable getRestColumnTable() {
        return restColumnTable;
    }

    /**
     * Show input in table
     */
    public void showInput() {
        showInput = true;
        showOutput = false;
        update();
    }

    /**
     * Show output in table
     */
    public void showOutput() {
        showInput = false;
        showOutput = true;
        update();
    }

    /**
     * Show both input and output in table
     */
    public void showInputAndOutput() {
        showInput = true;
        showOutput = true;
        update();
    }

    /**
     * Configure whether or not computed downstream values are shown in the
     * single seed/scenario IO table.
     *
     * @param toggle True if shown, false if hidden
     */
    public void toggleShowComputedDownstreamValues(boolean toggle) {
        this.showComputedDownstreamValues = toggle;
        this.restColumnModel.fireTableDataChanged();
    }

    public void toggleSensorView(boolean toggle, SensorSet sensorSet) {
        this.isSensorView = toggle;
        this.sensorSet = sensorSet;
    }

    /**
     * Getter for cell editor
     *
     * @param isFirstColumn whether it is first column model
     * @param row row index
     * @param col column index
     * @return cell editor
     */
    public TableCellEditor getCellEditor(boolean isFirstColumn, int row, int col) {
        TableCellSetting setting = findCellSetting(row);

        if (isFirstColumn) {
            return defaultCellEditor;
        } else {
            int segTypeGP = seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, col);
            int segTypeML = 0;
            if (seed.isManagedLaneUsed()) {
                segTypeML = seed.getValueInt(CEConst.IDS_ML_SEGMENT_TYPE, col);
            }
            switch (setting.identifier) {
                case CEConst.IDS_SEGMENT_TYPE:
                case CEConst.IDS_ML_SEGMENT_TYPE:
                    if (seed.isManagedLaneUsed()) {
                        tableSegTypeEditor.switchToGPAndML();
                    } else {
                        tableSegTypeEditor.switchToGPOnly();
                    }
                    return tableSegTypeEditor;
                case CEConst.IDS_ML_SEPARATION_TYPE:
                    return tableSeparationTypeEditor;
                case CEConst.IDS_ON_RAMP_SIDE:
                    if (segTypeGP == CEConst.SEG_TYPE_ONR || segTypeGP == CEConst.SEG_TYPE_W) {
                        return tableRampSideEditor;
                    } else {
                        return defaultCellEditor;
                    }
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                    if (segTypeML == CEConst.SEG_TYPE_ONR || segTypeML == CEConst.SEG_TYPE_W) {
                        return tableRampSideEditor;
                    } else {
                        return defaultCellEditor;
                    }
                case CEConst.IDS_OFF_RAMP_SIDE:
                    if (segTypeGP == CEConst.SEG_TYPE_OFR || segTypeGP == CEConst.SEG_TYPE_W) {
                        return tableRampSideEditor;
                    } else {
                        return defaultCellEditor;
                    }
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                    if (segTypeML == CEConst.SEG_TYPE_OFR || segTypeML == CEConst.SEG_TYPE_W) {
                        return tableRampSideEditor;
                    } else {
                        return defaultCellEditor;
                    }
                case CEConst.IDS_TERRAIN:
                    return tableTerrainEditor;
                case CEConst.IDS_RAMP_METERING_TYPE:
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                    return tableRampMeteringTypeEditor;
                case CEConst.IDS_ON_RAMP_METERING_RATE_ALINEA_KEY:
                    return tableRampMeteringALINEAEditor;
                case CEConst.IDS_ON_RAMP_METERING_RATE_FUZZY_KEY:
                    return tableRampMeteringFuzzyEditor;
                case CEConst.IDS_HAS_CROSS_WEAVE:
                    return tableCheckBoxEditor;
                default:
                    return defaultCellEditor;
            }
        }
    }

    /**
     * Getter for cell render
     *
     * @param isFirstColumn whether it is first column model
     * @param row row index
     * @param col column index
     * @return cell render
     */
    public TableCellRenderer getCellRenderer(boolean isFirstColumn, int row, int col) {
        TableCellSetting setting = findCellSetting(row);

        if (isFirstColumn) {
            return tableFirstColumnRenderer;
        } else {
            int segTypeGP = seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, col);
            int segTypeML = 0;
            if (seed.isManagedLaneUsed()) {
                segTypeML = seed.getValueInt(CEConst.IDS_ML_SEGMENT_TYPE, col);
            }
            switch (setting.identifier) {
                case CEConst.IDS_SEGMENT_TYPE:
                case CEConst.IDS_TYPE_USED:
                case CEConst.IDS_ML_SEGMENT_TYPE:
                case CEConst.IDS_ML_TYPE_USED:
                    return tableSegTypeRenderer;
                case CEConst.IDS_ML_SEPARATION_TYPE:
                    return tableSeparationTypeRenderer;
                case CEConst.IDS_ON_RAMP_SIDE:
                    if (segTypeGP == CEConst.SEG_TYPE_ONR || segTypeGP == CEConst.SEG_TYPE_W) {
                        return tableRampSideRenderer;
                    } else {
                        return tableNumAndStringRenderer;
                    }
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                    if (segTypeML == CEConst.SEG_TYPE_ONR || segTypeML == CEConst.SEG_TYPE_W) {
                        return tableRampSideRenderer;
                    } else {
                        return tableNumAndStringRenderer;
                    }
                case CEConst.IDS_OFF_RAMP_SIDE:
                    if (segTypeGP == CEConst.SEG_TYPE_OFR || segTypeGP == CEConst.SEG_TYPE_W) {
                        return tableRampSideRenderer;
                    } else {
                        return tableNumAndStringRenderer;
                    }
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                    if (segTypeML == CEConst.SEG_TYPE_OFR || segTypeML == CEConst.SEG_TYPE_W) {
                        return tableRampSideRenderer;
                    } else {
                        return tableNumAndStringRenderer;
                    }
                case CEConst.IDS_TERRAIN:
                    return tableTerrainRenderer;
                case CEConst.IDS_RAMP_METERING_TYPE:
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                    return tableRampMeteringTypeRenderer;
                case CEConst.IDS_HAS_CROSS_WEAVE:
                    return tableCheckBoxRenderer;
                case CEConst.IDS_OCCUPANCY_PCT:
                    return tableNumAndStringRenderer;
                case CEConst.IDS_SPEED:
                    return tableNumAndStringRenderer;
                default:
                    return tableNumAndStringRenderer;
            }
        }
    }

    /**
     * Getter for row count
     *
     * @return row count
     */
    public int getRowCount() {
        int count = 0;
        for (TableCellSetting setting : settings) {
            if (((setting.showInInput && showInput) || (setting.showInOutput && showOutput))
                    && ((setting.showInGP && showGP) || (setting.showInML && showML))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Getter for column count
     *
     * @param isFirstColumn whether it is first column model
     * @return column count
     */
    public int getColumnCount(boolean isFirstColumn) {
        if (isFirstColumn) {
            return (seed == null ? 0 : 1);
        } else {
            return (seed == null) ? 0 : (isSensorView ? sensorSet.getNumSensors() : seed.getValueInt(CEConst.IDS_NUM_SEGMENT));
        }
    }

    /**
     * Getter for value at a cell
     *
     * @param isFirstColumn whether it is first column model
     * @param row row index
     * @param col column index
     * @return value at a cell
     */
    public Object getValueAt(boolean isFirstColumn, int row, int col) {
        TableCellSetting setting = findCellSetting(row);
        if (isFirstColumn) {
            return setting.header;
        } else if (isSensorView) {
            if (col == sensorSet.getNumSensors()) {
                return 0.0f;
            } else {
                return seed.getValueString(setting.identifier, sensorSet.convertSensorIdxToSegment(col), period, scen, atdm);
            }
        } else {
            return seed.getValueString(setting.identifier, col, period, scen, atdm);
        }
    }

    /**
     * Getter for whether a cell is editable
     *
     * @param isFirstColumn whether it is first column model
     * @param row row index
     * @param col column index
     * @return whether a cell is editable
     */
    public boolean isCellEditable(boolean isFirstColumn, int row, int col) {
        if (getValueAt(isFirstColumn, row, col) == null) {
            return false;
        } else {
            return !isFirstColumn && findCellSetting(row).editable
                    && !getValueAt(isFirstColumn, row, col).equals(CEConst.IDS_NA)
                    && !getValueAt(isFirstColumn, row, col).equals(CEConst.IDS_NA_SPECIAL);
        }
    }

    /**
     * Setter for value at a cell
     *
     * @param isFirstColumn whether it is first column model
     * @param value new value
     * @param row row index
     * @param col column index
     */
    public void setValueAt(boolean isFirstColumn, Object value, int row, int col) {
        TableCellSetting setting = findCellSetting(row);
        if (!isFirstColumn && setting.editable) {
            if (isSensorView) {
                col = sensorSet.convertSensorIdxToSegment(col);
            }
            try {
                if (setting.identifier.equalsIgnoreCase(CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH)) {
                    int cand_demand = Integer.parseInt(value.toString());
                    int max_possible_rr = Math.min(seed.getValueInt(CEConst.IDS_ON_RAMP_DEMAND_VEH, col, period), seed.getValueInt(CEConst.IDS_OFF_RAMP_DEMAND_VEH, col, period));
                    if (cand_demand > max_possible_rr) {
                        JOptionPane.showMessageDialog(null,
                                "Ramp to Ramp Demand cannot exceed the On-ramp or Off-ramp demand of the weaving segment.",
                                "Warnining: Demand Imbalance",
                                JOptionPane.WARNING_MESSAGE);
                        cand_demand = max_possible_rr;
                    }
                    seed.setValue(setting.identifier, cand_demand, col, period, scen, atdm);
                    mainWindow.seedDataChanged(Toolbox.SEED_CHANGE_INPUT_FIELD);
                } else if (setting.identifier.equalsIgnoreCase(CEConst.IDS_GP_RL_LAFI)
                        || setting.identifier.equals(CEConst.IDS_GP_RL_LAFWZ)) {
                    seed.setValue(setting.identifier, value, col, period, scen, atdm);
                    mainWindow.seedDataChanged(Toolbox.SEED_CHANGE_INPUT_FIELD);
                } else if (!setting.identifier.equalsIgnoreCase(CEConst.IDS_GP_RL_LAFI)
                        && !setting.identifier.equalsIgnoreCase(CEConst.IDS_GP_RL_LAFWZ)
                        && Float.valueOf(value.toString()) >= 0.0f) {
                    seed.setValue(setting.identifier, value, col, period, scen, atdm);
                    mainWindow.seedDataChanged(Toolbox.SEED_CHANGE_INPUT_FIELD);
                }
            } catch (NumberFormatException e) {
                seed.setValue(setting.identifier, value, col, period, scen, atdm);
                mainWindow.seedDataChanged(Toolbox.SEED_CHANGE_INPUT_FIELD);
            }

        }
    }

    /**
     * Getter for column header
     *
     * @param isFirstColumn whether it is first column model
     * @param col column index
     * @return column header
     */
    public String getColumnName(boolean isFirstColumn, int col) {
        if (isFirstColumn) {
            return (isSensorView) ? "Sensor" : "Segment";
        } else if (isSensorView) {
            return (col == sensorSet.getNumSensors()) ? "Facility" : sensorSet.sensorNames[sensorSet.convertSensorIdxToSegment(col)];
        } else {
            return "Seg. " + Integer.toString(col + 1);
        }
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     * @param scen index of scenario to be displayed
     * @param atdm index of ATDM set to be displayed
     * @param period index of period to be displayed
     */
    public void selectSeedScenPeriod(Seed seed, int scen, int atdm, int period) {
        this.seed = seed;
        this.scen = scen;
        this.atdm = atdm;
        this.period = period;
        update();
    }

    /**
     * Configure display to show general purpose segments only
     */
    public void showGPOnly() {
        showGP = true;
        showML = false;
        update();
    }

    /**
     * Configure display to show managed lanes segments only
     */
    public void showMLOnly() {
        showGP = false;
        showML = true;
        update();
    }

    /**
     * Configure display to show both general purpose and managed lanes segments
     */
    public void showGPML() {
        showGP = true;
        showML = true;
        update();
    }

    /**
     * Update table content
     */
    public void update() {
        autoConfigRowDisplay();
        firstColumnModel.fireTableStructureChanged();
        restColumnModel.fireTableStructureChanged();
    }

    /**
     * Setter for MainWindow
     *
     * @param mainWindow MainWindow instance
     */
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Getter for cell settings currently in use
     *
     * @return cell settings currently in use
     */
    public ArrayList<TableCellSetting> getCellSettings() {
        return settings;
    }

    /**
     * Setter for table font
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        textFieldForCellEditor.setFont(newTableFont);
    }

    /**
     * Setter for new cell settings to be used
     *
     * @param settings new cell settings to be used
     */
    public void setCellSettings(ArrayList<TableCellSetting> settings) {
        if (settings != null) {
            this.settings = settings;
            update();
        }
    }

    /**
     * Auto configure row display to show or hide some rows
     */
    private void autoConfigRowDisplay() {
        if (seed != null) {
            for (TableCellSetting setting : settings) {
                switch (setting.identifier) {
                    case CEConst.IDS_LANE_WIDTH:
                    case CEConst.IDS_LATERAL_CLEARANCE:
                        //auto hide rows depending on whether free flow speed is known
                        setting.showInInput = !seed.isFreeFlowSpeedKnown();
                        setting.showInOutput = setting.showInOutput && !seed.isFreeFlowSpeedKnown();
                        break;
                    case CEConst.IDS_MAIN_FREE_FLOW_SPEED:
                        //auto hide rows depending on whether free flow speed is known
                        setting.showInInput = seed.isFreeFlowSpeedKnown();
                        setting.showInOutput = setting.showInOutput && seed.isFreeFlowSpeedKnown();
                        break;
                    case CEConst.IDS_GP_RL_CAF:
                    case CEConst.IDS_GP_RL_OAF:
                    case CEConst.IDS_GP_RL_DAF:
                    case CEConst.IDS_GP_RL_SAF:
                    case CEConst.IDS_GP_RL_LAFI:
                    case CEConst.IDS_GP_RL_LAFWZ:
                    case CEConst.IDS_ML_RL_CAF:
                    case CEConst.IDS_ML_RL_OAF:
                    case CEConst.IDS_ML_RL_DAF:
                    case CEConst.IDS_ML_RL_SAF:
                    case CEConst.IDS_ML_RL_LAF:
                        //auto hide rows depending on whether it is default scenario or generated RL or ATDM scenario
                        if (!mainWindow.getCurrentToolboxId().equalsIgnoreCase(MainWindow.TOOLBOX_DSS)) {
                            setting.showInInput = scen != 0;
                            setting.showInOutput = setting.showInOutput && scen != 0;
                        } else {
                            setting.showInInput = false;
                            setting.showInOutput = scen != 0;
                            setting.editable = false;
                        }
                        break;
                    case CEConst.IDS_GP_ATDM_CAF:
                    case CEConst.IDS_GP_ATDM_OAF:
                    case CEConst.IDS_GP_ATDM_DAF:
                    case CEConst.IDS_GP_ATDM_SAF:
                    case CEConst.IDS_GP_ATDM_LAF:
                    case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:
                    case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                    case CEConst.IDS_ML_ATDM_CAF:
                    case CEConst.IDS_ML_ATDM_OAF:
                    case CEConst.IDS_ML_ATDM_DAF:
                    case CEConst.IDS_ML_ATDM_SAF:
                    case CEConst.IDS_ML_ATDM_LAF:
                        //auto hide rows depending on whether it is ATDM or not
                        setting.showInInput = atdm >= 0;
                        setting.showInOutput = setting.showInOutput && atdm >= 0;
                        break;
                    case CEConst.IDS_CROSS_WEAVE_VOLUME:
                    case CEConst.IDS_HAS_CROSS_WEAVE:
                    case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                        //auto hide rows depending on whether managed lanes are used
                        setting.showInInput = seed.isManagedLaneUsed();
                        setting.showInOutput = false;
                        break;
                    case CEConst.IDS_CROSS_WEAVE_CAF:
                        //auto hide rows depending on whether managed lanes are used
                        setting.showInInput = false;
                        setting.showInOutput = seed.isManagedLaneUsed();
                        break;
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CELL SETTINGS">
    /**
     * Reset all cell settings to default
     */
    public void resetCellSettings() {
        _resetCellSettings();
    }

    private void _resetCellSettings() {
        //reset header strings
        // <editor-fold defaultstate="collapsed" desc="default GP header strings">
        // Basic Segment Variable Column Text
        STR_GP_HEADER = "General Purpose Segment Data";
        STR_SEGMENT_NAME = "General Purpose Segment Name";
        STR_SEGMENT_TYPE = "General Purpose Segment Type";
        STR_SEGMENT_LENGTH = "Segment Length (ft)";
        STR_SEGMENT_WIDTH = "Lane Width (ft)";
        STR_LATERAL_CLEARANCE = "Lateral Clearance (ft)";
        STR_TERRAIN = "Terrain";
        STR_TRUCK_CAR_EQ = "Truck-PC Equivalent (ET)";

        STR_NUM_LANES = "# of Lanes: Mainline";
        STR_FREE_FLOW_SPEED = "Free Flow Speed (mph)";
        STR_DEMAND_VEH = "Mainline Dem. (vph)";
        STR_TRUCK_SINGLE_PERCENTAGE_MAINLINE = "Mainline Single Unit Truck and Bus (%)";
        STR_TRUCK_TRAILER_PERCENTAGE_MAINLINE = "Mainline Tractor Trailer (%)";
        STR_U_CAF = "Seed Capacity Adj. Fac.";
        STR_U_OAF = "Seed Entering Dem. Adj. Fac.";
        STR_U_DAF = "Seed Exit Dem. Adj. Fac.";
        STR_U_SAF = "Seed Free Flow Speed Adj. Fac.";
        STR_U_DPCAF = "Seed Driver Pop. Capacity Adj. Fac.";
        STR_U_DPSAF = "Seed Driver Pop. Free Flow Speed Adj. Fac.";
        STR_RL_CAF = "Scen. Capacity Adj. Fac.";
        STR_RL_OAF = "Scen. Entering Dem. Adj. Fac.";
        STR_RL_DAF = "Scen. Exit Dem. Adj. Fac.";
        STR_RL_SAF = "Scen. Free Flow Speed Adj. Fac.";
        STR_RL_LAFI = "Scen. # Lanes Adj. Fac. I.";
        STR_RL_LAFWZ = "Scen. # Lanes Adj. Fac. WZ.";
        STR_ATDM_CAF = "ATDM Capacity Adj. Fac.";
        STR_ATDM_OAF = "ATDM Entering Dem. Adj. Fac.";
        STR_ATDM_DAF = "ATDM Exit Dem. Adj. Fac.";
        STR_ATDM_SAF = "ATDM Free Flow Speed Adj. Fac.";
        STR_ATDM_LAF = "ATDM # Lanes Adj. Fac.";
        STR_ATDM_RAMP_METERING_TYPE = "ATDM Ramp Metering Type";
        STR_ATDM_RM = "ATDM Ramp Metering (vph)";

        STR_ACC_DEC_LANE_LENGTH = "Acc/Dec Lane Length (ft)";

        // ONR Variable Column Text
        STR_ON_RAMP_SIDE = "ONR Side";

        STR_NUM_ON_RAMP_LANES = "# Lanes: ONR";
        STR_ON_RAMP_QUEUE_CAPACITY_VPL = "ONR Queue Capacity (veh/ln)";
        STR_ON_RAMP_DEMAND_VEH = "ONR/Entering Dem. (vph)";
        STR_TRUCK_SINGLE_PERCENTAGE_ONR = "ONR Single Unit Truck and Bus (%)";
        STR_TRUCK_TRAILER_PERCENTAGE_ONR = "ONR Tractor Trailer (%)";
        STR_ON_RAMP_FREE_FLOW_SPEED = "ONR Free Flow Speed (mph)";
        STR_ON_RAMP_METERING_TYPE = "ONR Metering Type";
        STR_ON_RAMP_METERING_RATE_FIX = "ONR Metering Fixed Rate (vph)";
        STR_ON_RAMP_METERING_RATE_ALINEA = "ONR Adaptive Metering ALINEA";
        STR_ON_RAMP_METERING_RATE_FUZZY = "ONR Adaptive Metering Fuzzy";

        // OFR Variable Column Text
        STR_OFF_RAMP_SIDE = "OFR Side";

        STR_NUM_OFF_RAMP_LANES = "# Lanes: OFR";
        STR_OFF_RAMP_DEMAND_VEH = "OFR/Exit Dem. (vph)";
        STR_TRUCK_SINGLE_PERCENTAGE_OFR = "OFR Single Unit Truck and Bus (%)";
        STR_TRUCK_TRAILER_PERCENTAGE_OFR = "OFR Tractor Trailer (%)";
        STR_OFF_RAMP_FREE_FLOW_SPEED = "OFR Free Flow Speed (mph)";

        // Weaving Segment Variable Column Text
        STR_LENGTH_OF_WEAVING = "Weave Segment Ls (ft)";
        STR_MIN_LANE_CHANGE_ONR_TO_FRWY = "Weave Segment LCRF";
        STR_MIN_LANE_CHANGE_FRWY_TO_OFR = "Weave Segment LCFR";
        STR_MIN_LANE_CHANGE_ONR_TO_OFR = "Weave Segment LCRR";
        STR_NUM_LANES_WEAVING = "Weave Segment NW";

        STR_RAMP_TO_RAMP_DEMAND_VEH = "Ramp to Ramp Dem. (vph)";

        // Basic Segment Output Column Text
        STR_TYPE_USED = "Processed Segment Type";
        STR_SPEED = "Speed (mph)";
        STR_TOTAL_DENSITY_VEH = "Total Density (veh/mi/ln)";
        STR_TOTAL_DENSITY_PC = "Total Density (pc/mi/ln)";
        STR_INFLUENCED_DENSITY_PC = "Influence Area Density (pc/mi/ln)";
        STR_CAPACITY = "Adjusted Capacity (vph)";
        STR_ADJUSTED_DEMAND = "Adjusted Mainline Dem. (vph)";
        STR_DC = "D/C";
        STR_VOLUME_SERVED = "Mainline Volume Served (vph)";
        STR_VC = "V/C";
        STR_DENSITY_BASED_LOS = "Density Based LOS";
        STR_DEMAND_BASED_LOS = "Dem. Based LOS";
        STR_QUEUE_LENGTH = "Mainline Queue Length (ft)";
        STR_QUEUE_PERCENTAGE = "Mainline Queue Length (%)";
        STR_ON_QUEUE_VEH = "ONR Queue (veh)";

        STR_ACTUAL_TIME = "Actual Travel Time (min)";
        STR_FFS_TIME = "FFS Travel Time (min)";
        STR_MAINLINE_DELAY = "Mainline Delay (min)";
        STR_VMTD = "VMTD (veh-miles / interval)";
        STR_VMTV = "VMTV (veh-miles / interval)";
        STR_PMTD = "PMTD (p-miles / interval)";
        STR_PMTV = "PMTV (p-miles / interval)";
        STR_VHT = "VHT (travel / interval (hrs))";
        STR_VHD_M = "VHD-M (delay / interval (hrs))";
        STR_VHD_MDE = "VHD-MDE (delay / interval (hrs))";
        STR_VHD_R = "VHD-R (delay / interval (hrs))";
        STR_VHD_ACCESS = "VHD-Access (delay / interval (hrs))";
        STR_VHD = "VHD (delay / interval (hrs))";
        STR_SPACE_MEAN_SPEED = "Space Mean Speed (mph)";
        STR_TRAVEL_TIME_INDEX = "Travel Time Index";

        // Special Output Column Text
        STR_ON_RAMP_CAPACITY = "ONR Capacity (vph)";
        STR_ADJUSTED_ON_RAMP_DEMAND = "Adjusted ONR Dem. (vph)";
        STR_ON_RAMP_VOLUME_SERVED = "ONR Volume Served (vph)";
        STR_ON_RAMP_AVG_RM_RATE = "ONR Avg RM Rate (vph)";
        STR_ON_RAMP_TIME_METERED = "ONR Time RM Active (min)";
        STR_OFF_RAMP_CAPACITY = "OFR Capacity (vph)";
        STR_ADJUSTED_OFF_RAMP_DEMAND = "Adjusted OFR Dem. (vph)";
        STR_OFF_RAMP_VOLUME_SERVED = "OFR Volume Served (vph)";
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="default ML header strings">
        // Basic Segment Variable Column Text
        STR_ML_HEADER = "Managed Lanes Segment Data";
        STR_ML_SEGMENT_TYPE = "ML Segment Type";
        STR_ML_SEPARATION_TYPE = "ML Type of Separation";

        STR_ML_NUM_LANES = "ML # of Lanes: Mainline";
        STR_ML_FREE_FLOW_SPEED = "ML Free Flow Speed (mph)";
        STR_ML_DEMAND_VEH = "ML Mainline Dem. (vph)";
        STR_ML_TRUCK_SINGLE_PERCENTAGE = "ML Single Unit Truck and Bus (%)";
        STR_ML_TRUCK_TRAILER_PERCENTAGE = "ML Tractor Trailer (%)";

        STR_ML_UCAF = "ML Seed Capacity Adj. Fac.";
        STR_ML_UOAF = "ML Seed Entering Dem. Adj. Fac.";
        STR_ML_UDAF = "ML Seed Exit Dem. Adj. Fac.";
        STR_ML_USAF = "ML Seed Free Flow Speed Adj. Fac.";
        STR_ML_SCAF = "ML RL Capacity Adj. Fac.";
        STR_ML_SOAF = "ML RL Entering Dem. Adj. Fac.";
        STR_ML_SDAF = "ML RL Exit Dem. Adj. Fac.";
        STR_ML_SSAF = "ML RL Free Flow Speed Adj. Fac.";
        STR_ML_SLAF = "ML RL # Lanes Adj. Fac. I.";
        STR_ML_ATDM_CAF = "ML ATDM Capacity Adj. Fac.";
        STR_ML_ATDM_OAF = "ML ATDM Entering Dem. Adj. Fac.";
        STR_ML_ATDM_DAF = "ML ATDM Exit Dem. Adj. Fac.";
        STR_ML_ATDM_SAF = "ML ATDM Free Flow Speed Adj. Fac.";
        STR_ML_ATDM_LAF = "ML ATDM # Lanes Adj. Fac.";

        STR_ML_ACC_DEC_LANE_LENGTH = "ML Acc/Dec Lane Length (ft)";

        // ONR Variable Column Text
        STR_ML_ON_RAMP_SIDE = "ML ONR Side";

        STR_ML_NUM_ON_RAMP_LANES = "ML # Lanes: ONR";
        STR_ML_ON_RAMP_DEMAND_VEH = "ML ONR/Entering Dem. (vph)";
        STR_ML_ON_RAMP_FREE_FLOW_SPEED = "ML ONR Free Flow Speed (mph)";

        // OFR Variable Column Text
        STR_ML_OFF_RAMP_SIDE = "ML OFR Side";

        STR_ML_NUM_OFF_RAMP_LANES = "ML # Lanes: OFR";
        STR_ML_OFF_RAMP_DEMAND_VEH = "ML OFR/Exiting Dem. (vph)";
        STR_ML_OFF_RAMP_FREE_FLOW_SPEED = "ML OFR Free Flow Speed (mph)";

        // Weaving Segment Variable Column Text
        STR_ML_LENGTH_SHORT = "ML Length Short (ft)";
        STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY = "ML Weave Segment LCRF";
        STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR = "ML Weave Segment LCFR";
        STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR = "ML Weave Segment LCRR";
        STR_ML_NUM_LANES_WEAVING = "ML Weave Segment NW";
        STR_ML_LC_MIN = "ML Min Lane Change";
        STR_ML_LC_MAX = "ML Max Lane Change";
        STR_ML_RAMP_TO_RAMP_DEMAND_VEH = "ML Ramp to Ramp Dem. (vph)";

        STR_ML_HAS_CROSS_WEAVE = "Analysis of Cross Weave Effect";
        STR_ML_CROSS_WEAVE_LC_MIN = "Cross Weave LC-Min";
        STR_ML_CROSS_WEAVE_VOLUME = "Cross Weave Volume";
        STR_ML_CROSS_WEAVE_CAF = "Cross Weave CAF";

        // Basic Segment Output Column Text
        STR_ML_TYPE_USED = "ML Processed Segment Type";
        STR_ML_SPEED = "ML Speed (mph)";
        STR_ML_TOTAL_DENSITY_VEH = "ML Total Density (veh/mi/ln)";
        STR_ML_TOTAL_DENSITY_PC = "ML Total Density (pc/mi/ln)";
        STR_ML_INFLUENCED_DENSITY_PC = "ML Influence Area Density (pc/mi/ln)";
        STR_ML_CAPACITY = "ML Adjusted Capacity (vph)";
        STR_ML_ADJUSTED_DEMAND = "ML Adjusted Mainline Dem. (vph)";
        STR_ML_DC = "ML D/C";
        STR_ML_VOLUME_SERVED = "ML Mainline Volume Served (vph)";
        STR_ML_VC = "ML V/C";
        STR_ML_DENSITY_BASED_LOS = "ML Density Based LOS";
        STR_ML_DEMAND_BASED_LOS = "ML Dem. Based LOS";
        STR_ML_QUEUE_LENGTH = "ML Mainline Queue Length (ft)";
        STR_ML_QUEUE_PERCENTAGE = "ML Mainline Queue Length (%)";
        STR_ML_ON_QUEUE_VEH = "ML ONR Queue (veh)";

        STR_ML_ACTUAL_TIME = "ML Actual Travel Time (min)";
        STR_ML_FFS_TIME = "ML FFS Travel Time (min)";
        STR_ML_MAINLINE_DELAY = "ML Mainline Delay (min)";
        //STR_ML_SYSTEM_DELAY = "ML System Delay (min)";
        STR_ML_VMTD = "ML VMTD (veh-miles / interval)";
        STR_ML_VMTV = "ML VMTV (veh-miles / interval)";
        STR_ML_PMTD = "ML PMTD (p-miles / interval)";
        STR_ML_PMTV = "ML PMTV (p-miles / interval)";
        STR_ML_VHT = "ML VHT (travel / interval (hrs))";
        STR_ML_VHD_M = "ML VHD-M (delay / interval (hrs))";
        STR_ML_VHD_R = "ML VHD-R (delay / interval (hrs))";
        STR_ML_VHD_ACCESS = "ML VHD-Access (delay / interval (hrs))";
        STR_ML_VHD_MDE = "ML VHD-MDE (delay / interval (hrs))";
        STR_ML_VHD = "ML VHD (delay / interval (hrs))";
        STR_ML_SPACE_MEAN_SPEED = "ML Space Mean Speed (mph)";
        STR_ML_TRAVEL_TIME_INDEX = "ML Travel Time Index";

        // Special Output Column Text
        STR_ML_ON_RAMP_CAPACITY = "ML ONR Capacity (vph)";
        STR_ML_ADJUSTED_ON_RAMP_DEMAND = "ML Adjusted ONR Dem. (vph)";
        STR_ML_ON_RAMP_VOLUME_SERVED = "ML ONR Volume Served (vph)";
        STR_ML_OFF_RAMP_CAPACITY = "ML OFR Capacity (vph)";
        STR_ML_ADJUSTED_OFF_RAMP_DEMAND = "ML Adjusted OFR Dem. (vph)";
        STR_ML_OFF_RAMP_VOLUME_SERVED = "ML OFR Volume Served (vph)";
        // </editor-fold>

        //reset order and visibility
        // <editor-fold defaultstate="collapsed" desc="create default order and visibility">
        settings = new ArrayList<>();
        // <editor-fold defaultstate="collapsed" desc="GP Default">
        //basic fixed input data
        settings.add(new TableCellSetting(STR_GP_HEADER, CEConst.IDS_DASH, true, true, null, true, false, false));
        settings.add(new TableCellSetting(STR_SEGMENT_NAME, CEConst.IDS_SEGMENT_NAME, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_SEGMENT_TYPE, CEConst.IDS_SEGMENT_TYPE, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_SEGMENT_LENGTH, CEConst.IDS_SEGMENT_LENGTH_FT, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_SEGMENT_WIDTH, CEConst.IDS_LANE_WIDTH, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_LATERAL_CLEARANCE, CEConst.IDS_LATERAL_CLEARANCE, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TERRAIN, CEConst.IDS_TERRAIN, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_CAR_EQ, CEConst.IDS_TRUCK_CAR_ET, true, false, COLOR_GP_FIX_INPUT, true, false, true));

        //basic time depended input data
        settings.add(new TableCellSetting(STR_NUM_LANES, CEConst.IDS_MAIN_NUM_LANES_IN, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_FREE_FLOW_SPEED, CEConst.IDS_MAIN_FREE_FLOW_SPEED, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_DEMAND_VEH, CEConst.IDS_MAIN_DEMAND_VEH, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_SINGLE_PERCENTAGE_MAINLINE, CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_TRAILER_PERCENTAGE_MAINLINE, CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_CAF, CEConst.IDS_GP_USER_CAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_OAF, CEConst.IDS_GP_USER_OAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_DAF, CEConst.IDS_GP_USER_DAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_SAF, CEConst.IDS_GP_USER_SAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_DPCAF, CEConst.IDS_GP_USER_DPCAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_U_DPSAF, CEConst.IDS_GP_USER_DPSAF, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_RL_OAF, CEConst.IDS_GP_RL_OAF, true, false, COLOR_GP_SCENARIO_1, true, false, true));
        settings.add(new TableCellSetting(STR_RL_DAF, CEConst.IDS_GP_RL_DAF, true, false, COLOR_GP_SCENARIO_1, true, false, true));
        settings.add(new TableCellSetting(STR_RL_CAF, CEConst.IDS_GP_RL_CAF, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_RL_SAF, CEConst.IDS_GP_RL_SAF, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_RL_LAFI, CEConst.IDS_GP_RL_LAFI, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_RL_LAFWZ, CEConst.IDS_GP_RL_LAFWZ, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_OAF, CEConst.IDS_GP_ATDM_OAF, true, false, COLOR_GP_SCENARIO_1, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_DAF, CEConst.IDS_GP_ATDM_DAF, true, false, COLOR_GP_SCENARIO_1, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_CAF, CEConst.IDS_GP_ATDM_CAF, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_SAF, CEConst.IDS_GP_ATDM_SAF, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_LAF, CEConst.IDS_GP_ATDM_LAF, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_RAMP_METERING_TYPE, CEConst.IDS_ATDM_RAMP_METERING_TYPE, true, false, COLOR_GP_SCENARIO_2, true, false, true));
        settings.add(new TableCellSetting(STR_ATDM_RM, CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX, true, false, COLOR_GP_SCENARIO_2, true, false, true));

        //special input data
        settings.add(new TableCellSetting(STR_ACC_DEC_LANE_LENGTH, CEConst.IDS_ACC_DEC_LANE_LENGTH, true, false, COLOR_GP_FIX_INPUT, true, false, true));

        settings.add(new TableCellSetting(STR_ON_RAMP_SIDE, CEConst.IDS_ON_RAMP_SIDE, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_NUM_ON_RAMP_LANES, CEConst.IDS_NUM_ON_RAMP_LANES, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ON_RAMP_QUEUE_CAPACITY_VPL, CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ON_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ON_RAMP_DEMAND_VEH, CEConst.IDS_ON_RAMP_DEMAND_VEH, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_SINGLE_PERCENTAGE_ONR, CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_TRAILER_PERCENTAGE_ONR, CEConst.IDS_TRUCK_TRAILER_PCT_ONR, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ON_RAMP_METERING_TYPE, CEConst.IDS_RAMP_METERING_TYPE, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ON_RAMP_METERING_RATE_FIX, CEConst.IDS_ON_RAMP_METERING_RATE_FIX, true, false, COLOR_GP_TIME_INPUT, true, false, true));

        //settings.add(new TableCellSetting(STR_ON_RAMP_METERING_RATE_ALINEA, CEConst.IDS_ON_RAMP_METERING_RATE_ALINEA_KEY, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        //settings.add(new TableCellSetting(STR_ON_RAMP_METERING_RATE_FUZZY, CEConst.IDS_ON_RAMP_METERING_RATE_FUZZY_KEY, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_OFF_RAMP_SIDE, CEConst.IDS_OFF_RAMP_SIDE, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_NUM_OFF_RAMP_LANES, CEConst.IDS_NUM_OFF_RAMP_LANES, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_OFF_RAMP_FREE_FLOW_SPEED, CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_OFF_RAMP_DEMAND_VEH, CEConst.IDS_OFF_RAMP_DEMAND_VEH, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_SINGLE_PERCENTAGE_OFR, CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR, true, false, COLOR_GP_TIME_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_TRUCK_TRAILER_PERCENTAGE_OFR, CEConst.IDS_TRUCK_TRAILER_PCT_OFR, true, false, COLOR_GP_TIME_INPUT, true, false, true));

        settings.add(new TableCellSetting(STR_LENGTH_OF_WEAVING, CEConst.IDS_LENGTH_OF_WEAVING, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_MIN_LANE_CHANGE_ONR_TO_FRWY, CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_MIN_LANE_CHANGE_FRWY_TO_OFR, CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_MIN_LANE_CHANGE_ONR_TO_OFR, CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_NUM_LANES_WEAVING, CEConst.IDS_NUM_LANES_WEAVING, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_RAMP_TO_RAMP_DEMAND_VEH, CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH, true, false, COLOR_GP_TIME_INPUT, true, false, true));

        settings.add(new TableCellSetting(STR_ML_HAS_CROSS_WEAVE, CEConst.IDS_HAS_CROSS_WEAVE, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ML_CROSS_WEAVE_LC_MIN, CEConst.IDS_CROSS_WEAVE_LC_MIN, true, false, COLOR_GP_FIX_INPUT, true, false, true));
        settings.add(new TableCellSetting(STR_ML_CROSS_WEAVE_VOLUME, CEConst.IDS_CROSS_WEAVE_VOLUME, true, false, COLOR_GP_TIME_INPUT, true, false, true));

        //basic and special output data
        settings.add(new TableCellSetting(STR_TYPE_USED, CEConst.IDS_TYPE_USED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_SPEED, CEConst.IDS_SPEED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_TOTAL_DENSITY_VEH, CEConst.IDS_TOTAL_DENSITY_VEH, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_TOTAL_DENSITY_PC, CEConst.IDS_TOTAL_DENSITY_PC, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_INFLUENCED_DENSITY_PC, CEConst.IDS_INFLUENCED_DENSITY_PC, false, true, COLOR_GP_OUTPUT, true, false, false));

        settings.add(new TableCellSetting(STR_ML_CROSS_WEAVE_CAF, CEConst.IDS_CROSS_WEAVE_CAF, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_CAPACITY, CEConst.IDS_MAIN_CAPACITY, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ADJUSTED_DEMAND, CEConst.IDS_ADJUSTED_MAIN_DEMAND, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_DC, CEConst.IDS_DC, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VOLUME_SERVED, CEConst.IDS_MAIN_VOLUME_SERVED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VC, CEConst.IDS_VC, false, true, COLOR_GP_OUTPUT, true, false, false));

        settings.add(new TableCellSetting(STR_ADJUSTED_ON_RAMP_DEMAND, CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ON_RAMP_CAPACITY, CEConst.IDS_ON_RAMP_CAPACITY, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ON_RAMP_VOLUME_SERVED, CEConst.IDS_ON_RAMP_VOLUME_SERVED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ON_RAMP_AVG_RM_RATE, CEConst.IDS_ON_RAMP_AVG_METERING_RATE, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ON_RAMP_TIME_METERED, CEConst.IDS_ON_RAMP_TIME_METERED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ADJUSTED_OFF_RAMP_DEMAND, CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_OFF_RAMP_CAPACITY, CEConst.IDS_OFF_RAMP_CAPACITY, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_OFF_RAMP_VOLUME_SERVED, CEConst.IDS_OFF_RAMP_VOLUME_SERVED, false, true, COLOR_GP_OUTPUT, true, false, false));

        settings.add(new TableCellSetting(STR_DENSITY_BASED_LOS, CEConst.IDS_DENSITY_BASED_LOS, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_DEMAND_BASED_LOS, CEConst.IDS_DEMAND_BASED_LOS, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_QUEUE_LENGTH, CEConst.IDS_QUEUE_LENGTH, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_QUEUE_PERCENTAGE, CEConst.IDS_QUEUE_PERCENTAGE, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_ON_QUEUE_VEH, CEConst.IDS_ON_QUEUE_VEH, false, true, COLOR_GP_OUTPUT, true, false, false));

        settings.add(new TableCellSetting(STR_ACTUAL_TIME, CEConst.IDS_ACTUAL_TIME, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_FFS_TIME, CEConst.IDS_FFS_TIME, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_MAINLINE_DELAY, CEConst.IDS_MAINLINE_DELAY, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VMTD, CEConst.IDS_VMTD, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VMTV, CEConst.IDS_VMTV, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_PMTD, CEConst.IDS_PMTD, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_PMTV, CEConst.IDS_PMTV, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHT, CEConst.IDS_VHT, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHD_M, CEConst.IDS_VHD_M, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHD_R, CEConst.IDS_VHD_R, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHD_ACCESS, CEConst.IDS_VHD_ACCESS, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHD_MDE, CEConst.IDS_VHD_MDE, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_VHD, CEConst.IDS_VHD, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_SPACE_MEAN_SPEED, CEConst.IDS_SPACE_MEAN_SPEED, false, true, COLOR_GP_OUTPUT, true, false, false));
        settings.add(new TableCellSetting(STR_TRAVEL_TIME_INDEX, CEConst.IDS_TRAVEL_TIME_INDEX, false, true, COLOR_GP_OUTPUT, true, false, false));
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="ML Default">
        //ML input
        settings.add(new TableCellSetting(STR_ML_HEADER, CEConst.IDS_DASH, true, true, null, false, true, false));
        settings.add(new TableCellSetting(STR_ML_SEGMENT_TYPE, CEConst.IDS_ML_SEGMENT_TYPE, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        //settings.add(new TableCellSetting(STR_ML_METHOD_TYPE, CEConst.IDS_ML_METHOD_TYPE, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SEPARATION_TYPE, CEConst.IDS_ML_SEPARATION_TYPE, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        //settings.add(new CellSetting(STR_ML_SEGMENT_LENGTH, CEConst.IDS_ML_SEGMENT_LENGTH_FT, true, false, COLOR_ML_FIX_INPUT, false, true, true));

        settings.add(new TableCellSetting(STR_ML_NUM_LANES, CEConst.IDS_ML_NUM_LANES, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_FREE_FLOW_SPEED, CEConst.IDS_ML_FREE_FLOW_SPEED, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_DEMAND_VEH, CEConst.IDS_ML_DEMAND_VEH, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_TRUCK_SINGLE_PERCENTAGE, CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_TRUCK_TRAILER_PERCENTAGE, CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_UCAF, CEConst.IDS_ML_USER_CAF, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_UOAF, CEConst.IDS_ML_USER_OAF, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_UDAF, CEConst.IDS_ML_USER_DAF, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_USAF, CEConst.IDS_ML_USER_SAF, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SOAF, CEConst.IDS_ML_RL_OAF, true, false, COLOR_ML_SCENARIO_1, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SDAF, CEConst.IDS_ML_RL_DAF, true, false, COLOR_ML_SCENARIO_1, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SCAF, CEConst.IDS_ML_RL_CAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SSAF, CEConst.IDS_ML_RL_SAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));
        settings.add(new TableCellSetting(STR_ML_SLAF, CEConst.IDS_ML_RL_LAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));

        settings.add(new TableCellSetting(STR_ML_ATDM_OAF, CEConst.IDS_ML_ATDM_OAF, true, false, COLOR_ML_SCENARIO_1, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ATDM_DAF, CEConst.IDS_ML_ATDM_DAF, true, false, COLOR_ML_SCENARIO_1, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ATDM_CAF, CEConst.IDS_ML_ATDM_CAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ATDM_SAF, CEConst.IDS_ML_ATDM_SAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ATDM_LAF, CEConst.IDS_ML_ATDM_LAF, true, false, COLOR_ML_SCENARIO_2, false, true, true));

        settings.add(new TableCellSetting(STR_ML_ACC_DEC_LANE_LENGTH, CEConst.IDS_ML_ACC_DEC_LANE_LENGTH, true, false, COLOR_ML_FIX_INPUT, false, true, true));

        settings.add(new TableCellSetting(STR_ML_ON_RAMP_SIDE, CEConst.IDS_ML_ON_RAMP_SIDE, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_NUM_ON_RAMP_LANES, CEConst.IDS_ML_NUM_ON_RAMP_LANES, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ON_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_ON_RAMP_DEMAND_VEH, CEConst.IDS_ML_ON_RAMP_DEMAND_VEH, true, false, COLOR_ML_TIME_INPUT, false, true, true));

        settings.add(new TableCellSetting(STR_ML_OFF_RAMP_SIDE, CEConst.IDS_ML_OFF_RAMP_SIDE, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_NUM_OFF_RAMP_LANES, CEConst.IDS_ML_NUM_OFF_RAMP_LANES, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_OFF_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED, true, false, COLOR_ML_TIME_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_OFF_RAMP_DEMAND_VEH, CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH, true, false, COLOR_ML_TIME_INPUT, false, true, true));

        settings.add(new TableCellSetting(STR_ML_LENGTH_SHORT, CEConst.IDS_ML_LENGTH_SHORT, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR, CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_NUM_LANES_WEAVING, CEConst.IDS_ML_NUM_LANES_WEAVING, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_RAMP_TO_RAMP_DEMAND_VEH, CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH, true, false, COLOR_ML_TIME_INPUT, false, true, true));

        settings.add(new TableCellSetting(STR_ML_LC_MIN, CEConst.IDS_ML_MIN_LANE_CHANGE_ML, true, false, COLOR_ML_FIX_INPUT, false, true, true));
        settings.add(new TableCellSetting(STR_ML_LC_MAX, CEConst.IDS_ML_MAX_LANE_CHANGE_ML, true, false, COLOR_ML_FIX_INPUT, false, true, true));

        //ML output
        settings.add(new TableCellSetting(STR_ML_TYPE_USED, CEConst.IDS_ML_TYPE_USED, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_SPEED, CEConst.IDS_ML_SPEED, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_TOTAL_DENSITY_VEH, CEConst.IDS_ML_TOTAL_DENSITY_VEH, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_TOTAL_DENSITY_PC, CEConst.IDS_ML_TOTAL_DENSITY_PC, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_INFLUENCED_DENSITY_PC, CEConst.IDS_ML_INFLUENCED_DENSITY_PC, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_CAPACITY, CEConst.IDS_ML_MAIN_CAPACITY, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_ADJUSTED_DEMAND, CEConst.IDS_ML_ADJUSTED_MAIN_DEMAND, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_DC, CEConst.IDS_ML_DC, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VOLUME_SERVED, CEConst.IDS_ML_MAIN_VOLUME_SERVED, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VC, CEConst.IDS_ML_VC, false, true, COLOR_ML_OUTPUT, false, true, false));

        settings.add(new TableCellSetting(STR_ML_ON_RAMP_CAPACITY, CEConst.IDS_ML_ON_RAMP_CAPACITY, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_ADJUSTED_ON_RAMP_DEMAND, CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_ON_RAMP_VOLUME_SERVED, CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_OFF_RAMP_CAPACITY, CEConst.IDS_ML_OFF_RAMP_CAPACITY, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_ADJUSTED_OFF_RAMP_DEMAND, CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_OFF_RAMP_VOLUME_SERVED, CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED, false, true, COLOR_ML_OUTPUT, false, true, false));

        settings.add(new TableCellSetting(STR_ML_DENSITY_BASED_LOS, CEConst.IDS_ML_DENSITY_BASED_LOS, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_DEMAND_BASED_LOS, CEConst.IDS_ML_DEMAND_BASED_LOS, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_QUEUE_LENGTH, CEConst.IDS_ML_QUEUE_LENGTH, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_QUEUE_PERCENTAGE, CEConst.IDS_ML_QUEUE_PERCENTAGE, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_ON_QUEUE_VEH, CEConst.IDS_ML_ON_QUEUE_VEH, false, true, COLOR_ML_OUTPUT, false, true, false));

        settings.add(new TableCellSetting(STR_ML_ACTUAL_TIME, CEConst.IDS_ML_ACTUAL_TIME, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_FFS_TIME, CEConst.IDS_ML_FFS_TIME, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_MAINLINE_DELAY, CEConst.IDS_ML_MAINLINE_DELAY, false, true, COLOR_ML_OUTPUT, false, true, false));

        settings.add(new TableCellSetting(STR_ML_VMTD, CEConst.IDS_ML_VMTD, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VMTV, CEConst.IDS_ML_VMTV, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_PMTD, CEConst.IDS_ML_PMTD, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_PMTV, CEConst.IDS_ML_PMTV, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHT, CEConst.IDS_ML_VHT, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHD_M, CEConst.IDS_ML_VHD_M, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHD_R, CEConst.IDS_ML_VHD_R, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHD_ACCESS, CEConst.IDS_ML_VHD_ACCESS, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHD_MDE, CEConst.IDS_ML_VHD_MDE, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_VHD, CEConst.IDS_ML_VHD, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_SPACE_MEAN_SPEED, CEConst.IDS_ML_SPACE_MEAN_SPEED, false, true, COLOR_ML_OUTPUT, false, true, false));
        settings.add(new TableCellSetting(STR_ML_TRAVEL_TIME_INDEX, CEConst.IDS_ML_TRAVEL_TIME_INDEX, false, true, COLOR_ML_OUTPUT, false, true, false));
        // </editor-fold>
        // </editor-fold>
    }

    // <editor-fold defaultstate="collapsed" desc="GP header strings">
    // Basic Segment Variable Column Text
    transient public String STR_GP_HEADER;

    transient public String STR_SEGMENT_NAME;

    transient public String STR_SEGMENT_TYPE;

    transient public String STR_SEGMENT_LENGTH;

    transient public String STR_SEGMENT_WIDTH;

    transient public String STR_LATERAL_CLEARANCE;

    transient public String STR_TERRAIN;

    transient public String STR_TRUCK_CAR_EQ;

    transient public String STR_NUM_LANES;

    transient public String STR_FREE_FLOW_SPEED;

    transient public String STR_DEMAND_VEH;

    transient public String STR_TRUCK_SINGLE_PERCENTAGE_MAINLINE;

    transient public String STR_TRUCK_TRAILER_PERCENTAGE_MAINLINE;

    transient public String STR_U_CAF;

    transient public String STR_U_OAF;

    transient public String STR_U_DAF;

    transient public String STR_U_SAF;

    transient public String STR_U_DPCAF;

    transient public String STR_U_DPSAF;

    transient public String STR_RL_CAF;

    transient public String STR_RL_OAF;

    transient public String STR_RL_DAF;

    transient public String STR_RL_SAF;

    transient public String STR_RL_LAFI;

    transient public String STR_RL_LAFWZ;

    transient public String STR_ATDM_CAF;

    transient public String STR_ATDM_OAF;

    transient public String STR_ATDM_DAF;

    transient public String STR_ATDM_SAF;

    transient public String STR_ATDM_LAF;

    transient public String STR_ATDM_RAMP_METERING_TYPE;

    transient public String STR_ATDM_RM;

    transient public String STR_ACC_DEC_LANE_LENGTH;

    // ONR Variable Column Text
    transient public String STR_ON_RAMP_SIDE;

    transient public String STR_NUM_ON_RAMP_LANES;

    transient public String STR_ON_RAMP_QUEUE_CAPACITY_VPL;

    transient public String STR_ON_RAMP_DEMAND_VEH;

    transient public String STR_TRUCK_SINGLE_PERCENTAGE_ONR;

    transient public String STR_TRUCK_TRAILER_PERCENTAGE_ONR;

    transient public String STR_ON_RAMP_FREE_FLOW_SPEED;

    transient public String STR_ON_RAMP_METERING_RATE_FIX;

    transient public String STR_ON_RAMP_METERING_RATE_ALINEA;

    transient public String STR_ON_RAMP_METERING_RATE_FUZZY;

    transient public String STR_ON_RAMP_METERING_TYPE;

    // OFR Variable Column Text
    transient public String STR_OFF_RAMP_SIDE;

    transient public String STR_NUM_OFF_RAMP_LANES;

    transient public String STR_OFF_RAMP_DEMAND_VEH;

    transient public String STR_TRUCK_SINGLE_PERCENTAGE_OFR;

    transient public String STR_TRUCK_TRAILER_PERCENTAGE_OFR;

    transient public String STR_OFF_RAMP_FREE_FLOW_SPEED;

    // Weaving Segment Variable Column Text
    transient public String STR_LENGTH_OF_WEAVING;

    transient public String STR_MIN_LANE_CHANGE_ONR_TO_FRWY;

    transient public String STR_MIN_LANE_CHANGE_FRWY_TO_OFR;

    transient public String STR_MIN_LANE_CHANGE_ONR_TO_OFR;

    transient public String STR_NUM_LANES_WEAVING;

    transient public String STR_RAMP_TO_RAMP_DEMAND_VEH;

    // Basic Segment Output Column Text
    transient public String STR_TYPE_USED;

    transient public String STR_SPEED;

    transient public String STR_TOTAL_DENSITY_VEH;

    transient public String STR_TOTAL_DENSITY_PC;

    transient public String STR_INFLUENCED_DENSITY_PC;

    transient public String STR_CAPACITY;

    transient public String STR_ADJUSTED_DEMAND;

    transient public String STR_DC;

    transient public String STR_VOLUME_SERVED;

    transient public String STR_VC;

    transient public String STR_DENSITY_BASED_LOS;

    transient public String STR_DEMAND_BASED_LOS;

    transient public String STR_QUEUE_LENGTH;

    transient public String STR_QUEUE_PERCENTAGE;

    transient public String STR_ON_QUEUE_VEH;

    transient public String STR_ACTUAL_TIME;

    transient public String STR_FFS_TIME;

    transient public String STR_MAINLINE_DELAY;

    transient public String STR_VMTD;

    transient public String STR_VMTV;

    transient public String STR_PMTD;

    transient public String STR_PMTV;

    transient public String STR_VHT;

    transient public String STR_VHD_M;

    transient public String STR_VHD_R;

    transient public String STR_VHD_MDE;

    transient public String STR_VHD;

    transient public String STR_SPACE_MEAN_SPEED;

    transient public String STR_TRAVEL_TIME_INDEX;

    // Special Output Column Text
    transient public String STR_ON_RAMP_CAPACITY;

    transient public String STR_ADJUSTED_ON_RAMP_DEMAND;

    transient public String STR_ON_RAMP_VOLUME_SERVED;

    transient public String STR_ON_RAMP_AVG_RM_RATE;

    transient public String STR_ON_RAMP_TIME_METERED;

    transient public String STR_OFF_RAMP_CAPACITY;

    transient public String STR_ADJUSTED_OFF_RAMP_DEMAND;

    transient public String STR_OFF_RAMP_VOLUME_SERVED;

    transient public String STR_VHD_ACCESS;

    //Used on GP but only when ML is on
    transient public String STR_ML_HAS_CROSS_WEAVE;

    transient public String STR_ML_CROSS_WEAVE_LC_MIN;

    transient public String STR_ML_CROSS_WEAVE_VOLUME;

    transient public String STR_ML_CROSS_WEAVE_CAF;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ML header strings">
    // Basic Segment Variable Column Text
    transient public String STR_ML_HEADER;

    transient public String STR_ML_SEGMENT_TYPE;

    transient public String STR_ML_SEPARATION_TYPE;

    transient public String STR_ML_NUM_LANES;

    transient public String STR_ML_FREE_FLOW_SPEED;

    transient public String STR_ML_DEMAND_VEH;

    transient public String STR_ML_TRUCK_SINGLE_PERCENTAGE;

    transient public String STR_ML_TRUCK_TRAILER_PERCENTAGE;

    transient public String STR_ML_UCAF;

    transient public String STR_ML_UOAF;

    transient public String STR_ML_UDAF;

    transient public String STR_ML_USAF;

    transient public String STR_ML_SCAF;

    transient public String STR_ML_SOAF;

    transient public String STR_ML_SDAF;

    transient public String STR_ML_SSAF;

    transient public String STR_ML_SLAF;

    transient public String STR_ML_ATDM_CAF;
    transient public String STR_ML_ATDM_OAF;
    transient public String STR_ML_ATDM_DAF;
    transient public String STR_ML_ATDM_SAF;
    transient public String STR_ML_ATDM_LAF;

    transient public String STR_ML_ACC_DEC_LANE_LENGTH;

    // ONR Variable Column Text
    transient public String STR_ML_ON_RAMP_SIDE;

    transient public String STR_ML_NUM_ON_RAMP_LANES;

    transient public String STR_ML_ON_RAMP_DEMAND_VEH;

    transient public String STR_ML_ON_RAMP_FREE_FLOW_SPEED;

    // OFR Variable Column Text
    transient public String STR_ML_OFF_RAMP_SIDE;

    transient public String STR_ML_NUM_OFF_RAMP_LANES;

    transient public String STR_ML_OFF_RAMP_DEMAND_VEH;

    transient public String STR_ML_OFF_RAMP_FREE_FLOW_SPEED;

    // Weaving Segment Variable Column Text
    transient public String STR_ML_LENGTH_SHORT;

    transient public String STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY;

    transient public String STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR;

    transient public String STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR;

    transient public String STR_ML_NUM_LANES_WEAVING;

    transient public String STR_ML_LC_MIN;

    transient public String STR_ML_LC_MAX;

    transient public String STR_ML_RAMP_TO_RAMP_DEMAND_VEH;

    // Basic Segment Output Column Text
    transient public String STR_ML_TYPE_USED;

    transient public String STR_ML_SPEED;

    transient public String STR_ML_TOTAL_DENSITY_VEH;

    transient public String STR_ML_TOTAL_DENSITY_PC;

    transient public String STR_ML_INFLUENCED_DENSITY_PC;

    transient public String STR_ML_CAPACITY;

    transient public String STR_ML_ADJUSTED_DEMAND;

    transient public String STR_ML_DC;

    transient public String STR_ML_VOLUME_SERVED;

    transient public String STR_ML_VC;

    transient public String STR_ML_DENSITY_BASED_LOS;

    transient public String STR_ML_DEMAND_BASED_LOS;

    transient public String STR_ML_QUEUE_LENGTH;

    transient public String STR_ML_QUEUE_PERCENTAGE;

    transient public String STR_ML_ON_QUEUE_VEH;

    transient public String STR_ML_ACTUAL_TIME;

    transient public String STR_ML_FFS_TIME;

    transient public String STR_ML_MAINLINE_DELAY;

    transient public String STR_ML_VMTD;

    transient public String STR_ML_VMTV;

    transient public String STR_ML_PMTD;

    transient public String STR_ML_PMTV;

    transient public String STR_ML_VHT;

    transient public String STR_ML_VHD;

    transient public String STR_ML_SPACE_MEAN_SPEED;

    transient public String STR_ML_TRAVEL_TIME_INDEX;

    // Special Output Column Text
    transient public String STR_ML_ON_RAMP_CAPACITY;

    transient public String STR_ML_ADJUSTED_ON_RAMP_DEMAND;

    transient public String STR_ML_ON_RAMP_VOLUME_SERVED;

    transient public String STR_ML_OFF_RAMP_CAPACITY;

    transient public String STR_ML_ADJUSTED_OFF_RAMP_DEMAND;

    transient public String STR_ML_OFF_RAMP_VOLUME_SERVED;

    transient public String STR_ML_VHD_R;

    transient public String STR_ML_VHD_M;

    transient public String STR_ML_VHD_ACCESS;

    transient public String STR_ML_VHD_MDE;
    // </editor-fold>

    private ArrayList<TableCellSetting> settings;

    private static final Color COLOR_GP_FIX_INPUT = new Color(153, 255, 153);

    private static final Color COLOR_GP_TIME_INPUT = new Color(255, 255, 150); //new Color(255, 255, 0)

    private static final Color COLOR_GP_SCENARIO_1 = new Color(255, 200, 200); //Color.pink; //new Color(255, 175, 175)

    private static final Color COLOR_GP_SCENARIO_2 = new Color(255, 230, 230); //Color.orange; //new Color(255, 200, 0)

    private static final Color COLOR_GP_OUTPUT = Color.cyan; //new Color(0, 255, 255)

    private static final Color COLOR_ML_FIX_INPUT = new Color(0, 200, 100);

    private static final Color COLOR_ML_TIME_INPUT = new Color(255, 200, 0);

    private static final Color COLOR_ML_SCENARIO_1 = new Color(255, 120, 120);

    private static final Color COLOR_ML_SCENARIO_2 = new Color(255, 160, 160);

    private static final Color COLOR_ML_OUTPUT = new Color(0, 200, 255);

    /**
     * search for cell setting at a particular row in the table
     *
     * @param row row index
     * @return cell setting at a particular row in the table
     */
    private TableCellSetting findCellSetting(int row) {
        int count = -1;
        for (TableCellSetting setting : settings) {
            if (((setting.showInInput && showInput) || (setting.showInOutput && showOutput))
                    && ((setting.showInGP && showGP) || (setting.showInML && showML))) {
                count++;
                if (count == row) {
                    return setting;
                }
            }
        }
        return null;
    }

    /**
     * Getter for color at a row
     *
     * @param row row index
     * @return color at a row
     */
    public Color getColorAt(int row) {
        return findCellSetting(row).bgColor;
    }

    /**
     * Getter for cell setting at a row
     *
     * @param row row index
     * @return cell setting at a row
     */
    public TableCellSetting getCellSettingAt(int row) {
        return findCellSetting(row);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CELL RENDERER AND EDITOR">
    private final TableFirstColumnRenderer tableFirstColumnRenderer = new TableFirstColumnRenderer(this);

    private final TableNumAndStringRenderer tableNumAndStringRenderer = new TableNumAndStringRenderer(this);

    private final TableSegTypeRenderer tableSegTypeRenderer = new TableSegTypeRenderer();

    private final TableRampSideRenderer tableRampSideRenderer = new TableRampSideRenderer();

    private final TableTerrainRenderer tableTerrainRenderer = new TableTerrainRenderer();

    private final TableRampMeteringTypeRenderer tableRampMeteringTypeRenderer = new TableRampMeteringTypeRenderer();

    private final TableCheckBoxRenderer tableCheckBoxRenderer = new TableCheckBoxRenderer();

    private final TableSeparationTypeRenderer tableSeparationTypeRenderer = new TableSeparationTypeRenderer();

    private final JTextField textFieldForCellEditor = new JTextField();

    private final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(textFieldForCellEditor);

    private final TableSegTypeEditor tableSegTypeEditor = new TableSegTypeEditor();

    private final TableTerrainEditor tableTerrainEditor = new TableTerrainEditor();

    private final TableRampMeteringTypeEditor tableRampMeteringTypeEditor = new TableRampMeteringTypeEditor();

    private final TableRampMeteringALINEAEditor tableRampMeteringALINEAEditor = new TableRampMeteringALINEAEditor();

    private final TableRampMeteringFuzzyEditor tableRampMeteringFuzzyEditor = new TableRampMeteringFuzzyEditor();

    private final TableRampSideEditor tableRampSideEditor = new TableRampSideEditor();

    private final TableCheckBoxEditor tableCheckBoxEditor = new TableCheckBoxEditor();

    private final TableSeparationTypeEditor tableSeparationTypeEditor = new TableSeparationTypeEditor();

    private class TableFirstColumnRenderer extends DefaultTableCellRenderer {

        SegIOTableWithSetting wrapper;

        /**
         * Constructor
         *
         * @param wrapper instance that gives table data and information
         */
        TableFirstColumnRenderer(SegIOTableWithSetting wrapper) {
            super();
            this.wrapper = wrapper;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            this.setForeground(null);
            this.setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            this.setHorizontalAlignment(JLabel.RIGHT);
            this.setBackground(wrapper.getColorAt(row));
            setText(value == null ? "null" : value.toString());

            return this;
        }
    }

    private class TableNumAndStringRenderer extends DefaultTableCellRenderer {

        SegIOTableWithSetting wrapper;

        /**
         * Constructor
         *
         * @param wrapper instance that gives table data and information
         */
        TableNumAndStringRenderer(SegIOTableWithSetting wrapper) {
            super();
            this.wrapper = wrapper;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            setForeground(null);
            setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            setHorizontalAlignment(JLabel.CENTER);
            TableCellSetting setting = wrapper.getCellSettingAt(row);
            if (setting == null) {
                setText(value.toString());
            } else if (value == null) {
                setText(CEConst.IDS_NA);
            } else {
                try {

                    tryInt(value.toString());
                    //only change color if it is an integer
                    switch (setting.identifier) {
                        case CEConst.IDS_SEGMENT_LENGTH_FT:
                        case CEConst.IDS_MAIN_DEMAND_VEH:
                        case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                        case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                        case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                        case CEConst.IDS_LENGTH_OF_WEAVING:
                        case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                        case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                        case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                        case CEConst.IDS_NUM_LANES_WEAVING:

                        case CEConst.IDS_ML_SEGMENT_LENGTH_FT:
                        case CEConst.IDS_ML_DEMAND_VEH:
                        case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                        case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                        case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                        case CEConst.IDS_ML_LENGTH_SHORT:
                        case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                        case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                        case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                        case CEConst.IDS_ML_NUM_LANES_WEAVING:
                        case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                        case CEConst.IDS_CROSS_WEAVE_VOLUME:
                        case CEConst.IDS_ML_MIN_LANE_CHANGE_ML:
                        case CEConst.IDS_ML_MAX_LANE_CHANGE_ML:
                            this.setBackground(setting.bgColor);
                            break;
                    }
                } catch (IllegalArgumentException e1) {
                    try {
                        switch (setting.identifier) {
                            case CEConst.IDS_ADJUSTED_MAIN_DEMAND:
                            case CEConst.IDS_MAIN_CAPACITY:
                            case CEConst.IDS_MAIN_VOLUME_SERVED:
                            case CEConst.IDS_VMTD:
                            case CEConst.IDS_VMTV:
                            case CEConst.IDS_PMTD:
                            case CEConst.IDS_PMTV:
                            case CEConst.IDS_ON_RAMP_CAPACITY:
                            case CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND:
                            case CEConst.IDS_ON_RAMP_VOLUME_SERVED:
                            case CEConst.IDS_OFF_RAMP_CAPACITY:
                            case CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND:
                            case CEConst.IDS_OFF_RAMP_VOLUME_SERVED:
                            case CEConst.IDS_ML_ADJUSTED_MAIN_DEMAND:
                            case CEConst.IDS_ML_MAIN_CAPACITY:
                            case CEConst.IDS_ML_MAIN_VOLUME_SERVED:
                            case CEConst.IDS_ML_VMTD:
                            case CEConst.IDS_ML_VMTV:
                            case CEConst.IDS_ML_PMTD:
                            case CEConst.IDS_ML_PMTV:
                            case CEConst.IDS_ML_ON_RAMP_CAPACITY:
                            case CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND:
                            case CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED:
                            case CEConst.IDS_ML_OFF_RAMP_CAPACITY:
                            case CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND:
                            case CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED:
                                tryFloat_0f(value.toString());
                                break;
                            case CEConst.IDS_SPEED:
                            case CEConst.IDS_TOTAL_DENSITY_VEH:
                            case CEConst.IDS_TOTAL_DENSITY_PC:
                            case CEConst.IDS_INFLUENCED_DENSITY_PC:
                            case CEConst.IDS_SPACE_MEAN_SPEED:
                            case CEConst.IDS_ML_SPEED:
                            case CEConst.IDS_ML_TOTAL_DENSITY_VEH:
                            case CEConst.IDS_ML_TOTAL_DENSITY_PC:
                            case CEConst.IDS_ML_INFLUENCED_DENSITY_PC:
                            case CEConst.IDS_ML_SPACE_MEAN_SPEED:
                                tryFloat_1f(value.toString());
                                break;
                            case CEConst.IDS_QUEUE_LENGTH:
                            case CEConst.IDS_ON_QUEUE_VEH:
                            case CEConst.IDS_ML_QUEUE_LENGTH:
                            case CEConst.IDS_ML_ON_QUEUE_VEH:
                                tryFloat_0f_pos(value.toString());
                                break;
                            case CEConst.IDS_QUEUE_PERCENTAGE:
                            case CEConst.IDS_ML_QUEUE_PERCENTAGE:
                                tryPercentage(value.toString());
                                break;
                            default:
                                tryFloat_2f(value.toString());
                        }
                    } catch (IllegalArgumentException e2) {
                        if (value.toString().equals(CEConst.IDS_NA)) {
                            this.setForeground(Color.darkGray);
                            this.setBackground(Color.darkGray);
                            setText(value.toString());
                        } else if (value.toString().equals(CEConst.IDS_NA_SPECIAL)) {
                            this.setForeground(showComputedDownstreamValues ? setting.bgColor : Color.DARK_GRAY);
                            this.setBackground(Color.DARK_GRAY);
                            switch (setting.identifier) {
                                case CEConst.IDS_MAIN_DEMAND_VEH:
                                case CEConst.IDS_ML_DEMAND_VEH:
                                    setText(String.valueOf(seed.getValueInt(setting.identifier, column, period)));
                                    break;
                                default:
                                    tryFloat_2f(String.valueOf(seed.getValueFloat(setting.identifier, column, period)));
                                    break;
                            }
                        } else {
                            setText(value.toString());
                        }
                    }
                }
            }

            return this;
        }

        private void tryPercentage(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.0");
            if (Float.parseFloat(value) > CEConst.ZERO) {
                setText(formatter.format(Float.parseFloat(value) * 100) + "%");
            } else {
                setText("");
            }
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

        private void tryFloat_0f_pos(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            if (Float.parseFloat(value) > CEConst.ZERO) {
                setText(formatter.format(Float.parseFloat(value)));
            } else {
                setText("");
            }
        }
    }

    private class TableRampSideEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox rampSideCombox
                = new JComboBox(new String[]{CEConst.STR_RAMP_SIDE_LEFT, CEConst.STR_RAMP_SIDE_RIGHT});

        private final TableRampSideEditor editor;

        public TableRampSideEditor() {
            super();
            editor = this;
            rampSideCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            switch (rampSideCombox.getSelectedIndex()) {
                case 1:
                    return CEConst.RAMP_SIDE_RIGHT;
                default:
                    return CEConst.RAMP_SIDE_LEFT;
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            switch (Integer.parseInt(value.toString())) {
                case CEConst.RAMP_SIDE_RIGHT:
                    rampSideCombox.setSelectedIndex(1);
                    break;
                default:
                    rampSideCombox.setSelectedIndex(0);
                    break;
            }

            return rampSideCombox;
        }
    }

    private class TableRampSideRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
         */
        TableRampSideRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public void setValue(Object value) {
            try {
                switch (Integer.parseInt(value.toString())) {
                    case CEConst.RAMP_SIDE_LEFT:
                        setText(CEConst.STR_RAMP_SIDE_LEFT);
                        break;
                    case CEConst.RAMP_SIDE_RIGHT:
                        setText(CEConst.STR_RAMP_SIDE_RIGHT);
                        break;
                    default:
                        setText("RS Error: " + value.toString());
                }
            } catch (NumberFormatException e) {
                setText("RS Error: " + value.toString());
            }
        }
    }

    private class TableSegTypeEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox SEG_TYPE_GP_DEFAULT = new JComboBox(new String[]{CEConst.STR_SEG_TYPE_B, CEConst.STR_SEG_TYPE_ONR,
            CEConst.STR_SEG_TYPE_OFR, CEConst.STR_SEG_TYPE_W, CEConst.STR_SEG_TYPE_R});

        private final JComboBox SEG_TYPE_GP_ML_ACS = new JComboBox(new String[]{CEConst.STR_SEG_TYPE_B, CEConst.STR_SEG_TYPE_ONR,
            CEConst.STR_SEG_TYPE_OFR, CEConst.STR_SEG_TYPE_W, CEConst.STR_SEG_TYPE_R, CEConst.STR_SEG_TYPE_ACS});

        private JComboBox segTypeCombox = SEG_TYPE_GP_DEFAULT;

        private final TableSegTypeEditor editor;

        public TableSegTypeEditor() {
            super();
            editor = this;
            SEG_TYPE_GP_DEFAULT.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
            SEG_TYPE_GP_ML_ACS.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            switch (segTypeCombox.getSelectedIndex()) {
                case 1:
                    return CEConst.SEG_TYPE_ONR;
                case 2:
                    return CEConst.SEG_TYPE_OFR;
                case 3:
                    return CEConst.SEG_TYPE_W;
                case 4:
                    return CEConst.SEG_TYPE_R;
                case 5:
                    return CEConst.SEG_TYPE_ACS;
                default:
                    return CEConst.SEG_TYPE_B;
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            switch (Integer.parseInt(value.toString())) {
                case CEConst.SEG_TYPE_ONR:
                    segTypeCombox.setSelectedIndex(1);
                    break;
                case CEConst.SEG_TYPE_OFR:
                    segTypeCombox.setSelectedIndex(2);
                    break;
                case CEConst.SEG_TYPE_W:
                    segTypeCombox.setSelectedIndex(3);
                    break;
                case CEConst.SEG_TYPE_R:
                    segTypeCombox.setSelectedIndex(4);
                    break;
                case CEConst.SEG_TYPE_ACS:
                    segTypeCombox.setSelectedIndex(5);
                    break;
                default:
                    segTypeCombox.setSelectedIndex(0);
                    break;
            }

            return segTypeCombox;
        }

        /**
         * Change to general purpose segments only (no access segment)
         */
        public void switchToGPOnly() {
            segTypeCombox = SEG_TYPE_GP_DEFAULT;
        }

        /**
         * Change to both general purpose and managed lanes segments (access
         * segment included)
         */
        public void switchToGPAndML() {
            segTypeCombox = SEG_TYPE_GP_ML_ACS;
        }
    }

    private class TableSegTypeRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
         */
        TableSegTypeRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public void setValue(Object value) {
            try {
                switch (Integer.parseInt(value.toString())) {
                    case CEConst.SEG_TYPE_B:
                        setText(CEConst.STR_SEG_TYPE_B);
                        break;
                    case CEConst.SEG_TYPE_ONR:
                        setText(CEConst.STR_SEG_TYPE_ONR);
                        break;
                    case CEConst.SEG_TYPE_ONR_B:
                        setText(CEConst.STR_SEG_TYPE_ONR_B);
                        break;
                    case CEConst.SEG_TYPE_OFR:
                        setText(CEConst.STR_SEG_TYPE_OFR);
                        break;
                    case CEConst.SEG_TYPE_OFR_B:
                        setText(CEConst.STR_SEG_TYPE_OFR_B);
                        break;
                    case CEConst.SEG_TYPE_R:
                        setText(CEConst.STR_SEG_TYPE_R);
                        break;
                    case CEConst.SEG_TYPE_W:
                        setText(CEConst.STR_SEG_TYPE_W);
                        break;
                    case CEConst.SEG_TYPE_W_B:
                        setText(CEConst.STR_SEG_TYPE_W_B);
                        break;
                    case CEConst.SEG_TYPE_ACS:
                        setText(CEConst.STR_SEG_TYPE_ACS);
                        break;
                    default:
                        setText("ST Error: " + value.toString());
                }
            } catch (NumberFormatException e) {
                setText("ST Error: " + value.toString());
            }
        }
    }

    private class TableTerrainEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox terrainCombox = new JComboBox(new String[]{CEConst.STR_TERRAIN_LEVEL,
            CEConst.STR_TERRAIN_ROLLING});
        //CEConst.STR_TERRAIN_VARYING_OR_OTHER});
        private final TableTerrainEditor editor;

        public TableTerrainEditor() {
            super();
            editor = this;
            terrainCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            switch (terrainCombox.getSelectedIndex()) {
                case 1:
                    return CEConst.TERRAIN_ROLLING;
                case 2:
                    return CEConst.TERRAIN_VARYING_OR_OTHER;
                default:
                    return CEConst.TERRAIN_LEVEL;
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            switch (Integer.parseInt(value.toString())) {
                case CEConst.TERRAIN_ROLLING:
                    terrainCombox.setSelectedIndex(1);
                    break;
                case CEConst.TERRAIN_VARYING_OR_OTHER:
                    terrainCombox.setSelectedIndex(2);
                    break;
                default:
                    terrainCombox.setSelectedIndex(0);
                    break;
            }

            return terrainCombox;
        }
    }

    private class TableTerrainRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
         */
        TableTerrainRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public void setValue(Object value) {
            try {
                switch (Integer.parseInt(value.toString())) {
                    case CEConst.TERRAIN_LEVEL:
                        setText(CEConst.STR_TERRAIN_LEVEL);
                        break;
                    case CEConst.TERRAIN_ROLLING:
                        setText(CEConst.STR_TERRAIN_ROLLING);
                        break;
                    case CEConst.TERRAIN_VARYING_OR_OTHER:
                        setText(CEConst.STR_TERRAIN_VARYING_OR_OTHER);
                        break;
                    default:
                        setText("TE Error: " + value.toString());
                }
            } catch (NumberFormatException e) {
                setText("TE Error: " + value.toString());
            }
        }
    }

    private class TableRampMeteringTypeEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox typeCombox = new JComboBox(new String[]{CEConst.STR_RAMP_METERING_TYPE_NONE, CEConst.STR_RAMP_METERING_TYPE_FIX});
        private final TableRampMeteringTypeEditor editor;

        public TableRampMeteringTypeEditor() {
            super();
            editor = this;
            typeCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            switch (typeCombox.getSelectedIndex()) {
                case 1:
                    return CEConst.IDS_RAMP_METERING_TYPE_FIX;
                case 2:
                    return CEConst.IDS_RAMP_METERING_TYPE_ALINEA;
                case 3:
                    return CEConst.IDS_RAMP_METERING_TYPE_FUZZY;
                default:
                    return CEConst.IDS_RAMP_METERING_TYPE_NONE;
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            switch (Integer.parseInt(value.toString())) {
                case CEConst.IDS_RAMP_METERING_TYPE_FIX:
                    typeCombox.setSelectedIndex(1);
                    break;
                case CEConst.IDS_RAMP_METERING_TYPE_ALINEA:
                    typeCombox.setSelectedIndex(2);
                    break;
                case CEConst.IDS_RAMP_METERING_TYPE_FUZZY:
                    typeCombox.setSelectedIndex(3);
                    break;
                default:
                    typeCombox.setSelectedIndex(0);
                    break;
            }

            return typeCombox;
        }
    }

    private class TableRampMeteringALINEAEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox dataKeyCombox;

        private final TableRampMeteringALINEAEditor editor;

        public TableRampMeteringALINEAEditor() {
            super();
            editor = this;
        }

        @Override
        public Object getCellEditorValue() {
            return dataKeyCombox.getSelectedItem().toString();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            String selected = value.toString();

            TreeMap<String, RampMeteringALINEAData> map = seed.getRampMeteringALINEAData();
            String[] keys = new String[map.keySet().size()];
            keys[0] = "Default";
            int count = 1;
            for (String key : map.navigableKeySet()) {
                if (!key.equals("Default")) {
                    keys[count++] = key;
                }
            }
            dataKeyCombox = new JComboBox(keys);
            dataKeyCombox.setSelectedItem(selected);
            dataKeyCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
            return dataKeyCombox;
        }
    }

    private class TableRampMeteringFuzzyEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox dataKeyCombox;

        private final TableRampMeteringFuzzyEditor editor;

        public TableRampMeteringFuzzyEditor() {
            super();
            editor = this;
        }

        @Override
        public Object getCellEditorValue() {
            return dataKeyCombox.getSelectedItem().toString();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            String selected = value.toString();

            TreeMap<String, RampMeteringFuzzyData> map = seed.getRampMeteringFuzzyData();
            String[] keys = new String[map.keySet().size()];
            keys[0] = "Default";
            int count = 1;
            for (String key : map.navigableKeySet()) {
                if (!key.equals("Default")) {
                    keys[count++] = key;
                }
            }
            dataKeyCombox = new JComboBox(keys);
            dataKeyCombox.setSelectedItem(selected);
            dataKeyCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
            return dataKeyCombox;
        }
    }

    private class TableRampMeteringTypeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            setForeground(null);
            setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            setHorizontalAlignment(JLabel.CENTER);

            try {
                switch (Integer.parseInt(value.toString())) {
                    case CEConst.IDS_RAMP_METERING_TYPE_NONE:
                        setText(CEConst.STR_RAMP_METERING_TYPE_NONE);
                        break;
                    case CEConst.IDS_RAMP_METERING_TYPE_FIX:
                        setText(CEConst.STR_RAMP_METERING_TYPE_FIX);
                        break;
                    case CEConst.IDS_RAMP_METERING_TYPE_ALINEA:
                        setText(CEConst.STR_RAMP_METERING_TYPE_ALINEA);
                        break;
                    case CEConst.IDS_RAMP_METERING_TYPE_FUZZY:
                        setText(CEConst.STR_RAMP_METERING_TYPE_FUZZY);
                        break;
                    default:
                        setText("? " + value.toString());
                }
            } catch (NumberFormatException e) {
                setText(value.toString());
                setForeground(Color.darkGray);
                setBackground(Color.darkGray);
            }
            return this;
        }
    }

    private class TableCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

        TableCheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setSelected(value != null && Boolean.parseBoolean(value.toString()));
            return this;
        }
    }

    private class TableCheckBoxEditor extends AbstractCellEditor implements TableCellEditor {

        private final JCheckBox checkBox = new JCheckBox();

        TableCheckBoxEditor() {
            checkBox.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            checkBox.setSelected(value != null && Boolean.parseBoolean(value.toString()));
            return checkBox;
        }
    }

    private class TableSeparationTypeEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox separationTypeCombox = new JComboBox(new String[]{CEConst.STR_ML_SEPARATION_MARKING, CEConst.STR_ML_SEPARATION_BUFFER,
            CEConst.STR_ML_SEPARATION_BARRIER});
        private final TableSeparationTypeEditor editor;

        public TableSeparationTypeEditor() {
            super();
            editor = this;
            separationTypeCombox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            switch (separationTypeCombox.getSelectedIndex()) {
                case 1:
                    return CEConst.ML_SEPARATION_BUFFER;
                case 2:
                    return CEConst.ML_SEPARATION_BARRIER;
                default:
                    return CEConst.ML_SEPARATION_MARKING;
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            switch (Integer.parseInt(value.toString())) {
                case CEConst.ML_SEPARATION_BUFFER:
                    separationTypeCombox.setSelectedIndex(1);
                    break;
                case CEConst.ML_SEPARATION_BARRIER:
                    separationTypeCombox.setSelectedIndex(2);
                    break;
                default:
                    separationTypeCombox.setSelectedIndex(0);
                    break;
            }

            return separationTypeCombox;
        }
    }

    private class TableSeparationTypeRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
         */
        TableSeparationTypeRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public void setValue(Object value) {
            try {
                switch (Integer.parseInt(value.toString())) {
                    case CEConst.ML_SEPARATION_MARKING:
                        setText(CEConst.STR_ML_SEPARATION_MARKING);
                        break;
                    case CEConst.ML_SEPARATION_BUFFER:
                        setText(CEConst.STR_ML_SEPARATION_BUFFER);
                        break;
                    case CEConst.ML_SEPARATION_BARRIER:
                        setText(CEConst.STR_ML_SEPARATION_BARRIER);
                        break;
                    default:
                        setText("ST Error: " + value.toString());
                }
            } catch (NumberFormatException e) {
                setText("ST Error: " + value.toString());
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TABLE AND TABLE MODELS">
    private final FREEVAL_JTable firstColumnTable;

    private final FREEVAL_TableModel firstColumnModel;

    private final FREEVAL_JTable restColumnTable;

    private final FREEVAL_TableModel restColumnModel;

    private class SegIOTableModel extends FREEVAL_TableModel {

        /**
         * Constructor
         *
         * @param isFirstColumn whether it is first column model
         * @param tableWithSetting the SegIOTableWithSetting object that
         * contains this model
         */
        public SegIOTableModel(boolean isFirstColumn, SegIOTableWithSetting tableWithSetting) {
            this.isFirstColumn = isFirstColumn;
            //this.isInput = isInput;
            this.tableWithSetting = tableWithSetting;
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            return tableWithSetting.getCellEditor(isFirstColumn, row, column);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            return tableWithSetting.getCellRenderer(isFirstColumn, row, column);
        }

        @Override
        public int getRowCount() {
            return tableWithSetting.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return tableWithSetting.getColumnCount(isFirstColumn);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return tableWithSetting.getValueAt(isFirstColumn, rowIndex, columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return tableWithSetting.isCellEditable(isFirstColumn, row, column);
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            tableWithSetting.setValueAt(isFirstColumn, value, row, column);
        }

        @Override
        public String getColumnName(int column) {
            return tableWithSetting.getColumnName(isFirstColumn, column);
        }

        private final boolean isFirstColumn;

        private final SegIOTableWithSetting tableWithSetting;
    }
    // </editor-fold>

    private Seed seed;

    private int period = 0;

    private int scen = 0;

    private int atdm = -1;

    private boolean showInput = true;

    private boolean showOutput = false;

    private boolean showComputedDownstreamValues = true;

    private boolean showGP = true;

    private boolean showML = false;

    private MainWindow mainWindow;
}
