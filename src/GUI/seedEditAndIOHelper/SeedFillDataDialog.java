package GUI.seedEditAndIOHelper;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * This is a dialog that user could use to fill input data in order to model
 * facility faster
 *
 * @author Shu Liu
 */
public class SeedFillDataDialog extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    private final Seed seed;

    private final MainWindow mainWindow;

    private DefaultListModel periodModel_fix;

    private DefaultListModel periodModel_var;

    private DefaultListModel segmentModel_all_GP;

    private DefaultListModel segmentModel_all_ML;

    private DefaultListModel segmentModel_first;

    private DefaultListModel segmentModel_ONR_OFR_GP;

    private DefaultListModel segmentModel_ONR_W_GP;

    private DefaultListModel segmentModel_ONR_W_ACS_GP;

    private DefaultListModel segmentModel_OFR_W_GP;

    private DefaultListModel segmentModel_OFR_W_ACS_GP;

    private DefaultListModel segmentModel_W_GP;

    private DefaultListModel segmentModel_ONR_OFR_ML;

    private DefaultListModel segmentModel_ONR_W_ML;

    private DefaultListModel segmentModel_ONR_W_ACS_ML;

    private DefaultListModel segmentModel_OFR_W_ML;

    private DefaultListModel segmentModel_OFR_W_ACS_ML;

    private DefaultListModel segmentModel_W_ML;

    //GP Segments
    private final static String STR_SEGMENT_LENGTH = "Segment Length (ft)";

    private final static String STR_LANE_WIDTH = "Lane Width (ft)";

    private final static String STR_LATERAL_CLEARANCE = "Lateral Clearance (ft)";

    private final static String STR_TRUCK_CAR_ET = "Truck-PC Equivalent (ET)";

    private final static String STR_NUM_LANES = "# of Lanes: Mainline";

    private final static String STR_FREE_FLOW_SPEED = "Free Flow Speed (mph)";

    private final static String STR_DEMAND_VEH = "Mainline Demand (vph)";

    private final static String STR_TRUCK_SINGLE_PCT_MAINLINE = "Mainline Single Unit Truck and Bus (%)";

    private final static String STR_TRUCK_TRAILER_PCT_MAINLINE = "Mainline Tractor Trailer (%)";

    private final static String STR_U_CAF = "Seed Capacity Adj. Factor";

    private final static String STR_U_OAF = "Seed Entering (Origin) Demand Adj. Factor";

    private final static String STR_U_DAF = "Seed Exiting (Destination) Demand Adj. Factor";

    private final static String STR_U_FFSAF = "Seed Free Flow Speed Adj. Factor";

    private final static String STR_U_DPCAF = "Seed Driver Pop. Capacity Adj. Fac.";

    private final static String STR_U_DPSAF = "Seed Driver Pop. Free Flow Speed Adj. Fac.";

    // On Ramp Variable Column Text
    private final static String STR_ACC_DEC_LANE_LENGTH = "Acc/Dec Lane Length (ft)";

    private final static String STR_NUM_ON_RAMP_LANES = "# Of Lanes: On Ramp";

    private final static String STR_ON_RAMP_DEMAND_VEH = "On Ramp / Entering Demand (vph)";

    private final static String STR_TRUCK_SINGLE_PCT_ONR = "On Ramp Single Unit Truck and Bus (%)";

    private final static String STR_TRUCK_TRAILER_PCT_ONR = "On Ramp Tractor Trailer (%)";

    private final static String STR_ON_RAMP_FREE_FLOW_SPEED = "On Ramp Free Flow Speed (mph)";

    private final static String STR_NUM_ON_RAMP_QUEUE_CAPACITY = "On Ramp Queue Capacity (veh/ln)";

    private final static String STR_ON_RAMP_METERING_TYPE = "On Ramp Metering Type";

    private final static String STR_ON_RAMP_METERING_RATE = "On Ramp Metering Rate (vph)";

    // Off Ramp Variable Column Text
    private final static String STR_NUM_OFF_RAMP_LANES = "# Of Lanes: Off Ramp";

    private final static String STR_OFF_RAMP_DEMAND_VEH = "Off Ramp / Exit Demand (vph)";

    private final static String STR_TRUCK_SINGLE_PCT_OFR = "Off Ramp Single Unit Truck and Bus (%)";

    private final static String STR_TRUCK_TRAILER_PCT_OFR = "Off Ramp Tractor Trailer (%)";

    private final static String STR_OFF_RAMP_FREE_FLOW_SPEED = "Off Ramp Free Flow Speed (mph)";

    // Weaving Segment Variable Column Text
    private final static String STR_LENGTH_OF_WEAVING = "Weave Segment Ls (ft)";

    private final static String STR_MIN_LANE_CHANGE_ONR_TO_FRWY = "Weave Segment LCRF";

    private final static String STR_MIN_LANE_CHANGE_FRWY_TO_OFR = "Weave Segment LCFR";

    private final static String STR_MIN_LANE_CHANGE_ONR_TO_OFR = "Weave Segment LCRR";

    private final static String STR_NUM_LANES_WEAVING = "Weave Segment NW";

    private final static String STR_RAMP_TO_RAMP_DEMAND_VEH = "Ramp to Ramp Demand (vph)";

    //ML Segments
    private final static String STR_ML_SEPARATION = "ML Separation Type";

    private final static String STR_ML_NUM_LANES = "ML # of Lanes: Mainline";

    private final static String STR_ML_FREE_FLOW_SPEED = "ML Free Flow Speed (mph)";

    private final static String STR_ML_DEMAND_VEH = "ML Mainline Demand (vph)";

    private final static String STR_ML_TRUCK_SINGLE_PERCENTAGE = "ML Single Unit Truck and Bus (%)";

    private final static String STR_ML_TRUCK_TRAILER_PERCENTAGE = "ML Tractor Trailer (%)";

    private final static String STR_ML_CAF = "ML Seed Capacity Adj. Factor";

    private final static String STR_ML_OAF = "ML Seed Origin Demand Adj. Factor";

    private final static String STR_ML_DAF = "ML Seed Destination Demand Adj. Factor";

    private final static String STR_ML_FFSAF = "ML Seed Free Flow Speed Adj. Factor";

    // On Ramp Variable Column Text
    private final static String STR_ML_ACC_DEC_LANE_LENGTH = "ML Acc/Dec Lane Length (ft)";

    private final static String STR_ML_NUM_ON_RAMP_LANES = "ML # Of Lanes: On Ramp";

    private final static String STR_ML_ON_RAMP_DEMAND_VEH = "ML On Ramp / Entering Demand (vph)";

    private final static String STR_ML_ON_RAMP_FREE_FLOW_SPEED = "ML On Ramp Free Flow Speed (mph)";

    // Off Ramp Variable Column Text
    private final static String STR_ML_NUM_OFF_RAMP_LANES = "ML # Of Lanes: Off Ramp";

    private final static String STR_ML_OFF_RAMP_DEMAND_VEH = "ML Off Ramp / Exit Demand (vph)";

    private final static String STR_ML_OFF_RAMP_FREE_FLOW_SPEED = "ML Off Ramp Free Flow Speed (mph)";

    // Weaving Segment Variable Column Text
    private final static String STR_ML_LENGTH_OF_WEAVING = "ML Weave Segment Ls (ft)";

    private final static String STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY = "ML Weave Segment LCRF";

    private final static String STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR = "ML Weave Segment LCFR";

    private final static String STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR = "ML Weave Segment LCRR";

    private final static String STR_ML_NUM_LANES_WEAVING = "ML Weave Segment NW";

    private final static String STR_ML_RAMP_TO_RAMP_DEMAND_VEH = "ML Ramp to Ramp Demand (vph)";

    private final static String STR_ML_MIN_LC = "ML Min Lane Change";

    private final static String STR_ML_MAX_LC = "ML Max Lane Change";

    private final static String STR_ML_HAS_CW = "Analysis of Cross Weave Effect";

    private final static String STR_ML_CW_LC_MIN = "Cross Weave LC Min";

    private final static String STR_ML_CW_VOLUME = "Cross Weave Volume";

    private class ItemSetting {

        /**
         * Name of the setting item
         */
        public final String name;

        /**
         * Period list model used for this item
         */
        public final DefaultListModel periodModel;

        /**
         * Segment list model used for this item
         */
        public final DefaultListModel segmentModel;

        /**
         * Constructor
         *
         * @param name Name of the setting item
         * @param periodModel Period list model used for this item
         * @param segmentModel Segment list model used for this item
         */
        public ItemSetting(String name, DefaultListModel periodModel, DefaultListModel segmentModel) {
            this.name = name;
            this.periodModel = periodModel;
            this.segmentModel = segmentModel;
        }
    }

    private ItemSetting[] itemSettingsGP;

    private ItemSetting[] itemSettingsML;

    private DefaultComboBoxModel itemModelGP;

    private DefaultComboBoxModel itemModelML;

    /**
     * Creates new form fillDataDialog
     *
     * @param seed seed to be filled
     * @param mainWindow mainWindow for return message
     */
    public SeedFillDataDialog(Seed seed, MainWindow mainWindow) {
        super(mainWindow, true);
        initComponents();

        getRootPane().setDefaultButton(fillDataButton);

        //set starting position
        this.setLocationRelativeTo(this.getRootPane());

        this.seed = seed;
        this.mainWindow = mainWindow;

        if (!seed.isManagedLaneUsed()) {
            mlButton.setEnabled(false);
        }

        setupComboBox();

        ((JLabel) itemCB.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    private void setupComboBox() {
        //segment models x4---------------------------------------------------------------
        segmentModel_all_GP = new DefaultListModel();
        segmentModel_all_ML = new DefaultListModel();

        segmentModel_first = new DefaultListModel();
        segmentModel_first.addElement(1);

        segmentModel_ONR_OFR_GP = new DefaultListModel();
        segmentModel_ONR_W_GP = new DefaultListModel();
        segmentModel_ONR_W_ACS_GP = new DefaultListModel();
        segmentModel_OFR_W_GP = new DefaultListModel();
        segmentModel_OFR_W_ACS_GP = new DefaultListModel();
        segmentModel_W_GP = new DefaultListModel();

        segmentModel_ONR_OFR_ML = new DefaultListModel();
        segmentModel_ONR_W_ML = new DefaultListModel();
        segmentModel_ONR_W_ACS_ML = new DefaultListModel();
        segmentModel_OFR_W_ML = new DefaultListModel();
        segmentModel_OFR_W_ACS_ML = new DefaultListModel();
        segmentModel_W_ML = new DefaultListModel();

        for (int segIndex = 0; segIndex < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); segIndex++) {
            segmentModel_all_GP.addElement(segIndex + 1);

            switch (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, segIndex)) {
                case CEConst.SEG_TYPE_ONR:
                    segmentModel_ONR_OFR_GP.addElement(segIndex + 1);
                    segmentModel_ONR_W_GP.addElement(segIndex + 1);
                    segmentModel_ONR_W_ACS_GP.addElement(segIndex + 1);
                    break;
                case CEConst.SEG_TYPE_OFR:
                    segmentModel_ONR_OFR_GP.addElement(segIndex + 1);
                    segmentModel_OFR_W_GP.addElement(segIndex + 1);
                    segmentModel_OFR_W_ACS_GP.addElement(segIndex + 1);
                    break;
                case CEConst.SEG_TYPE_W:
                    segmentModel_ONR_W_GP.addElement(segIndex + 1);
                    segmentModel_ONR_W_ACS_GP.addElement(segIndex + 1);
                    segmentModel_OFR_W_GP.addElement(segIndex + 1);
                    segmentModel_OFR_W_ACS_GP.addElement(segIndex + 1);
                    segmentModel_W_GP.addElement(segIndex + 1);
                    break;
                case CEConst.SEG_TYPE_ACS:
                    segmentModel_ONR_W_ACS_GP.addElement(segIndex + 1);
                    segmentModel_OFR_W_ACS_GP.addElement(segIndex + 1);
                    break;
            }
        }

        if (seed.isManagedLaneUsed()) {
            for (int segIndex = 0; segIndex < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); segIndex++) {
                segmentModel_all_ML.addElement(segIndex + 1);

                switch (seed.getValueInt(CEConst.IDS_ML_SEGMENT_TYPE, segIndex)) {
                    case CEConst.SEG_TYPE_ONR:
                        segmentModel_ONR_OFR_ML.addElement(segIndex + 1);
                        segmentModel_ONR_W_ML.addElement(segIndex + 1);
                        segmentModel_ONR_W_ACS_ML.addElement(segIndex + 1);
                        break;
                    case CEConst.SEG_TYPE_OFR:
                        segmentModel_ONR_OFR_ML.addElement(segIndex + 1);
                        segmentModel_OFR_W_ML.addElement(segIndex + 1);
                        segmentModel_OFR_W_ACS_ML.addElement(segIndex + 1);
                        break;
                    case CEConst.SEG_TYPE_W:
                        segmentModel_ONR_W_ML.addElement(segIndex + 1);
                        segmentModel_ONR_W_ACS_ML.addElement(segIndex + 1);
                        segmentModel_OFR_W_ML.addElement(segIndex + 1);
                        segmentModel_OFR_W_ACS_ML.addElement(segIndex + 1);
                        segmentModel_W_ML.addElement(segIndex + 1);
                        break;
                    case CEConst.SEG_TYPE_ACS:
                        segmentModel_ONR_W_ACS_ML.addElement(segIndex + 1);
                        segmentModel_OFR_W_ACS_ML.addElement(segIndex + 1);
                        break;
                }
            }
        }

        segmentList.setModel(segmentModel_all_GP);

        //analysis period models x2-------------------------------------------------------
        periodModel_fix = new DefaultListModel();

        periodModel_var = new DefaultListModel();
        for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
            periodModel_var.addElement(period + 1);
        }

        periodList.setModel(periodModel_fix);

        //item model------------------------------------------------------------------------
        itemSettingsGP = new ItemSetting[]{
            new ItemSetting(STR_SEGMENT_LENGTH, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_LANE_WIDTH, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_LATERAL_CLEARANCE, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_TRUCK_CAR_ET, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_NUM_LANES, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_FREE_FLOW_SPEED, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_DEMAND_VEH, periodModel_var, segmentModel_first),
            new ItemSetting(STR_TRUCK_SINGLE_PCT_MAINLINE, periodModel_var, segmentModel_first),
            new ItemSetting(STR_TRUCK_TRAILER_PCT_MAINLINE, periodModel_var, segmentModel_first),
            new ItemSetting(STR_U_CAF, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_U_OAF, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_U_DAF, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_U_FFSAF, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_U_DPCAF, periodModel_var, segmentModel_all_GP),
            new ItemSetting(STR_U_DPSAF, periodModel_var, segmentModel_all_GP),
            // On Ramp Variable Column Text
            new ItemSetting(STR_ACC_DEC_LANE_LENGTH, periodModel_fix, segmentModel_ONR_OFR_GP),
            new ItemSetting(STR_NUM_ON_RAMP_LANES, periodModel_fix, segmentModel_ONR_W_GP),
            new ItemSetting(STR_NUM_ON_RAMP_QUEUE_CAPACITY, periodModel_fix, segmentModel_ONR_W_GP),
            new ItemSetting(STR_ON_RAMP_FREE_FLOW_SPEED, periodModel_var, segmentModel_ONR_W_GP),
            new ItemSetting(STR_ON_RAMP_DEMAND_VEH, periodModel_var, segmentModel_ONR_W_ACS_GP),
            new ItemSetting(STR_TRUCK_SINGLE_PCT_ONR, periodModel_var, segmentModel_ONR_W_GP),
            new ItemSetting(STR_TRUCK_TRAILER_PCT_ONR, periodModel_var, segmentModel_ONR_W_GP),
            new ItemSetting(STR_ON_RAMP_METERING_TYPE, periodModel_var, segmentModel_ONR_W_GP),
            new ItemSetting(STR_ON_RAMP_METERING_RATE, periodModel_var, segmentModel_ONR_W_GP),
            // Off Ramp Variable Column Text
            new ItemSetting(STR_NUM_OFF_RAMP_LANES, periodModel_fix, segmentModel_OFR_W_GP),
            new ItemSetting(STR_OFF_RAMP_FREE_FLOW_SPEED, periodModel_var, segmentModel_OFR_W_GP),
            new ItemSetting(STR_OFF_RAMP_DEMAND_VEH, periodModel_var, segmentModel_OFR_W_ACS_GP),
            new ItemSetting(STR_TRUCK_SINGLE_PCT_OFR, periodModel_var, segmentModel_OFR_W_GP),
            new ItemSetting(STR_TRUCK_TRAILER_PCT_OFR, periodModel_var, segmentModel_OFR_W_GP),
            // Weaving Segment Variable Column Text
            new ItemSetting(STR_LENGTH_OF_WEAVING, periodModel_fix, segmentModel_W_GP),
            new ItemSetting(STR_MIN_LANE_CHANGE_ONR_TO_FRWY, periodModel_fix, segmentModel_W_GP),
            new ItemSetting(STR_MIN_LANE_CHANGE_FRWY_TO_OFR, periodModel_fix, segmentModel_W_GP),
            new ItemSetting(STR_MIN_LANE_CHANGE_ONR_TO_OFR, periodModel_fix, segmentModel_W_GP),
            new ItemSetting(STR_NUM_LANES_WEAVING, periodModel_fix, segmentModel_W_GP),
            new ItemSetting(STR_RAMP_TO_RAMP_DEMAND_VEH, periodModel_var, segmentModel_W_GP),
            //new ItemSetting(STR_ML_HAS_CW, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_ML_CW_LC_MIN, periodModel_fix, segmentModel_all_GP),
            new ItemSetting(STR_ML_CW_VOLUME, periodModel_var, segmentModel_all_GP)
        };

        itemSettingsML = new ItemSetting[]{
            new ItemSetting(STR_ML_SEPARATION, periodModel_fix, segmentModel_all_ML),
            new ItemSetting(STR_ML_NUM_LANES, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_FREE_FLOW_SPEED, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_DEMAND_VEH, periodModel_var, segmentModel_first),
            new ItemSetting(STR_ML_TRUCK_SINGLE_PERCENTAGE, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_TRUCK_TRAILER_PERCENTAGE, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_CAF, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_OAF, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_DAF, periodModel_var, segmentModel_all_ML),
            new ItemSetting(STR_ML_FFSAF, periodModel_var, segmentModel_all_ML),
            // On Ramp Variable Column Text
            new ItemSetting(STR_ML_ACC_DEC_LANE_LENGTH, periodModel_fix, segmentModel_ONR_OFR_ML),
            new ItemSetting(STR_ML_NUM_ON_RAMP_LANES, periodModel_fix, segmentModel_ONR_W_ML),
            new ItemSetting(STR_ML_ON_RAMP_FREE_FLOW_SPEED, periodModel_var, segmentModel_ONR_W_ML),
            new ItemSetting(STR_ML_ON_RAMP_DEMAND_VEH, periodModel_var, segmentModel_ONR_W_ACS_ML),
            // Off Ramp Variable Column Text
            new ItemSetting(STR_ML_NUM_OFF_RAMP_LANES, periodModel_fix, segmentModel_OFR_W_ML),
            new ItemSetting(STR_ML_OFF_RAMP_FREE_FLOW_SPEED, periodModel_var, segmentModel_OFR_W_ML),
            new ItemSetting(STR_ML_OFF_RAMP_DEMAND_VEH, periodModel_var, segmentModel_OFR_W_ACS_ML),
            // Weaving Segment Variable Column Text
            new ItemSetting(STR_ML_LENGTH_OF_WEAVING, periodModel_fix, segmentModel_W_ML),
            new ItemSetting(STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, periodModel_fix, segmentModel_W_ML),
            new ItemSetting(STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, periodModel_fix, segmentModel_W_ML),
            new ItemSetting(STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR, periodModel_fix, segmentModel_W_ML),
            new ItemSetting(STR_ML_NUM_LANES_WEAVING, periodModel_fix, segmentModel_W_ML),
            new ItemSetting(STR_ML_RAMP_TO_RAMP_DEMAND_VEH, periodModel_var, segmentModel_W_ML),
            //ML extra items
            new ItemSetting(STR_ML_MIN_LC, periodModel_fix, segmentModel_all_ML),
            new ItemSetting(STR_ML_MAX_LC, periodModel_fix, segmentModel_all_ML)
        };

        itemModelGP = new DefaultComboBoxModel();
        for (ItemSetting itemSetting : itemSettingsGP) {
            itemModelGP.addElement(itemSetting.name);
        }

        itemModelML = new DefaultComboBoxModel();
        for (ItemSetting itemSetting : itemSettingsML) {
            itemModelML.addElement(itemSetting.name);
        }

        itemCB.setModel(itemModelGP);
    }

    /**
     * Getter for return status
     *
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        segmentButtonGroup = new javax.swing.ButtonGroup();
        periodButtonGroup = new javax.swing.ButtonGroup();
        GPMLButtonGroup = new javax.swing.ButtonGroup();
        okButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        itemCB = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        valueText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        segmentList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        periodList = new javax.swing.JList();
        allSegmentButton = new javax.swing.JRadioButton();
        selectSegmentButton = new javax.swing.JRadioButton();
        allPeriodButton = new javax.swing.JRadioButton();
        selectPeriodButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        gpButton = new javax.swing.JRadioButton();
        mlButton = new javax.swing.JRadioButton();
        tipLabel = new javax.swing.JLabel();
        fillDataButton = new javax.swing.JButton();

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        setTitle("Fill Data");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("Close");
        okButton.setPreferredSize(new java.awt.Dimension(100, 29));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Please select item to be filled"));

        jLabel1.setText("Item");

        itemCB.setMaximumRowCount(25);
        itemCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        itemCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                itemCBItemStateChanged(evt);
            }
        });

        jLabel3.setText("Segments");

        jLabel4.setText("Analysis Periods");

        jLabel2.setText("Value");

        valueText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        valueText.setText("0");
        valueText.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        segmentList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        segmentList.setEnabled(false);
        jScrollPane1.setViewportView(segmentList);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        periodList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        periodList.setEnabled(false);
        jScrollPane2.setViewportView(periodList);

        segmentButtonGroup.add(allSegmentButton);
        allSegmentButton.setSelected(true);
        allSegmentButton.setText("All Applicable Segments");
        allSegmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allSegmentButtonActionPerformed(evt);
            }
        });

        segmentButtonGroup.add(selectSegmentButton);
        selectSegmentButton.setText("Selected Segments");
        selectSegmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSegmentButtonActionPerformed(evt);
            }
        });

        periodButtonGroup.add(allPeriodButton);
        allPeriodButton.setSelected(true);
        allPeriodButton.setText("All Analysis Periods");
        allPeriodButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allPeriodButtonActionPerformed(evt);
            }
        });

        periodButtonGroup.add(selectPeriodButton);
        selectPeriodButton.setText("Selected Analysis Periods");
        selectPeriodButton.setEnabled(false);
        selectPeriodButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPeriodButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Type");

        GPMLButtonGroup.add(gpButton);
        gpButton.setSelected(true);
        gpButton.setText("General Purpose Segments");
        gpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpButtonActionPerformed(evt);
            }
        });

        GPMLButtonGroup.add(mlButton);
        mlButton.setText("Managed Lanes Segments");
        mlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mlButtonActionPerformed(evt);
            }
        });

        tipLabel.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectSegmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(allSegmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)
                                .addComponent(jLabel4))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectPeriodButton, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(allPeriodButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(gpButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mlButton))
                            .addComponent(itemCB, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(valueText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tipLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mlButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(itemCB, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueText, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tipLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allSegmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(allPeriodButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectPeriodButton)
                    .addComponent(selectSegmentButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        fillDataButton.setText("Fill Data");
        fillDataButton.setPreferredSize(new java.awt.Dimension(100, 29));
        fillDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillDataButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fillDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fillDataButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void itemCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_itemCBItemStateChanged
        checkCBModel();
    }//GEN-LAST:event_itemCBItemStateChanged

    private void fillDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillDataButtonActionPerformed
        fillData();
    }//GEN-LAST:event_fillDataButtonActionPerformed

    private void allSegmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allSegmentButtonActionPerformed
        segmentList.setEnabled(false);
        if (!segmentList.isEnabled()) {
            segmentList.clearSelection();
        }
    }//GEN-LAST:event_allSegmentButtonActionPerformed

    private void selectSegmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectSegmentButtonActionPerformed
        segmentList.setEnabled(true);
    }//GEN-LAST:event_selectSegmentButtonActionPerformed

    private void allPeriodButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allPeriodButtonActionPerformed
        periodList.setEnabled(false);
        if (!periodList.isEnabled()) {
            segmentList.clearSelection();
        }
    }//GEN-LAST:event_allPeriodButtonActionPerformed

    private void selectPeriodButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPeriodButtonActionPerformed
        periodList.setEnabled(true);
    }//GEN-LAST:event_selectPeriodButtonActionPerformed

    private void gpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gpButtonActionPerformed
        configModel();
    }//GEN-LAST:event_gpButtonActionPerformed

    private void mlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mlButtonActionPerformed
        configModel();
    }//GEN-LAST:event_mlButtonActionPerformed

    private void configModel() {
        if (gpButton.isSelected()) {
            itemCB.setModel(itemModelGP);
        } else {
            itemCB.setModel(itemModelML);
        }
        checkCBModel();
    }

    private void checkCBModel() {
        switch (itemCB.getSelectedItem().toString()) {
//            case STR_ML_TYPE:
//                tipLabel.setText(CEConst.STR_ML_METHOD_HOV + ":" + CEConst.ML_METHOD_HOV + " "
//                        + CEConst.STR_ML_METHOD_HOT + ":" + CEConst.ML_METHOD_HOT);
//                break;
            case STR_ML_SEPARATION:
                tipLabel.setText(CEConst.STR_ML_SEPARATION_MARKING + ":" + CEConst.ML_SEPARATION_MARKING + " "
                        + CEConst.STR_ML_SEPARATION_BUFFER + ":" + CEConst.ML_SEPARATION_BUFFER + " "
                        + CEConst.STR_ML_SEPARATION_BARRIER + ":" + CEConst.ML_SEPARATION_BARRIER);
                break;
            case STR_ON_RAMP_METERING_TYPE:
                tipLabel.setText(CEConst.STR_RAMP_METERING_TYPE_NONE + ":" + CEConst.IDS_RAMP_METERING_TYPE_NONE + " "
                        + CEConst.STR_RAMP_METERING_TYPE_FIX + ":" + CEConst.IDS_RAMP_METERING_TYPE_FIX);
                break;
            default:
                tipLabel.setText(" ");
        }

        for (ItemSetting itemSetting : itemSettingsGP) {
            if (itemSetting.name.equals(itemCB.getSelectedItem().toString())) {

                if (!periodList.getModel().equals(itemSetting.periodModel)) {
                    periodList.setModel(itemSetting.periodModel);

                    selectPeriodButton.setEnabled(itemSetting.periodModel.getSize() > 0);
                    if (!selectPeriodButton.isEnabled()) {
                        allPeriodButton.setSelected(true);
                    } else {
                        periodList.setEnabled(selectPeriodButton.isSelected());
                    }
                }

                if (!segmentList.getModel().equals(itemSetting.segmentModel)) {
                    segmentList.setModel(itemSetting.segmentModel);
                }

                break;
            }
        }

        for (ItemSetting itemSetting : itemSettingsML) {
            if (itemSetting.name.equals(itemCB.getSelectedItem().toString())) {

                if (!periodList.getModel().equals(itemSetting.periodModel)) {
                    periodList.setModel(itemSetting.periodModel);

                    selectPeriodButton.setEnabled(itemSetting.periodModel.getSize() > 0);
                    if (!selectPeriodButton.isEnabled()) {
                        allPeriodButton.setSelected(true);
                    } else {
                        periodList.setEnabled(selectPeriodButton.isSelected());
                    }
                }

                if (!segmentList.getModel().equals(itemSetting.segmentModel)) {
                    segmentList.setModel(itemSetting.segmentModel);
                }

                break;
            }
        }
    }

    private void fillData() {
        try {
            if (allSegmentButton.isSelected()) {
                //for (int segIndex = 0; segIndex < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); segIndex++) {
                for (int listIndex = 0; listIndex < segmentList.getModel().getSize(); listIndex++) {
                    int segIndex = (int) segmentList.getModel().getElementAt(listIndex);
                    if (allPeriodButton.isSelected()) {
                        for (int periodIndex = 0;
                                periodIndex < (selectPeriodButton.isEnabled() ? seed.getValueInt(CEConst.IDS_NUM_PERIOD) : 1);
                                periodIndex++) {
                            fillDataHelper(segIndex - 1, periodIndex);
                        }
                    } else {
                        for (int periodIndex : (List<Integer>) periodList.getSelectedValuesList()) {
                            fillDataHelper(segIndex - 1, periodIndex - 1);
                        }
                    }
                }
            } else {
                for (int segIndex : (List<Integer>) segmentList.getSelectedValuesList()) {
                    if (allPeriodButton.isSelected()) {
                        for (int periodIndex = 0;
                                periodIndex < (selectPeriodButton.isEnabled() ? seed.getValueInt(CEConst.IDS_NUM_PERIOD) : 1);
                                periodIndex++) {
                            fillDataHelper(segIndex - 1, periodIndex);
                        }
                    } else {
                        for (int periodIndex : (List<Integer>) periodList.getSelectedValuesList()) {
                            fillDataHelper(segIndex - 1, periodIndex - 1);
                        }
                    }
                }
            }
            mainWindow.printLog("Data Filled");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error when fill data, please check", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillDataHelper(int segIndex, int periodIndex) {
        switch (itemCB.getSelectedItem().toString()) {
            case STR_SEGMENT_LENGTH:
                seed.setValue(CEConst.IDS_SEGMENT_LENGTH_FT, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_LANE_WIDTH:
                seed.setValue(CEConst.IDS_LANE_WIDTH, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_LATERAL_CLEARANCE:
                seed.setValue(CEConst.IDS_LATERAL_CLEARANCE, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_TRUCK_CAR_ET:
                seed.setValue(CEConst.IDS_TRUCK_CAR_ET, Float.parseFloat(valueText.getText()), segIndex);
                break;

            case STR_NUM_LANES:
                seed.setValue(CEConst.IDS_MAIN_NUM_LANES_IN, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_MAIN_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_DEMAND_VEH:
                seed.setValue(CEConst.IDS_MAIN_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_SINGLE_PCT_MAINLINE:
                seed.setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_TRAILER_PCT_MAINLINE:
                seed.setValue(CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_SINGLE_PCT_ONR:
                seed.setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_TRAILER_PCT_ONR:
                seed.setValue(CEConst.IDS_TRUCK_TRAILER_PCT_ONR, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_SINGLE_PCT_OFR:
                seed.setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_TRUCK_TRAILER_PCT_OFR:
                seed.setValue(CEConst.IDS_TRUCK_TRAILER_PCT_OFR, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_CAF:
                seed.setValue(CEConst.IDS_GP_USER_CAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_OAF:
                seed.setValue(CEConst.IDS_GP_USER_OAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_DAF:
                seed.setValue(CEConst.IDS_GP_USER_DAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_FFSAF:
                seed.setValue(CEConst.IDS_GP_USER_SAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_DPCAF:
                seed.setValue(CEConst.IDS_GP_USER_DPCAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_U_DPSAF:
                seed.setValue(CEConst.IDS_GP_USER_DPSAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ACC_DEC_LANE_LENGTH:
                seed.setValue(CEConst.IDS_ACC_DEC_LANE_LENGTH, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_NUM_ON_RAMP_LANES:
                seed.setValue(CEConst.IDS_NUM_ON_RAMP_LANES, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ON_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_ON_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ON_RAMP_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_NUM_ON_RAMP_QUEUE_CAPACITY:
                seed.setValue(CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ON_RAMP_METERING_TYPE:
                if (Integer.parseInt(valueText.getText()) == 0 || Integer.parseInt(valueText.getText()) == 1) {
                    seed.setValue(CEConst.IDS_RAMP_METERING_TYPE, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                }
                break;

            case STR_ON_RAMP_METERING_RATE:
                seed.setValue(CEConst.IDS_ON_RAMP_METERING_RATE_FIX, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_NUM_OFF_RAMP_LANES:
                seed.setValue(CEConst.IDS_NUM_OFF_RAMP_LANES, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_OFF_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_OFF_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_OFF_RAMP_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_LENGTH_OF_WEAVING:
                seed.setValue(CEConst.IDS_LENGTH_OF_WEAVING, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_MIN_LANE_CHANGE_ONR_TO_FRWY:
                seed.setValue(CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_MIN_LANE_CHANGE_FRWY_TO_OFR:
                seed.setValue(CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_MIN_LANE_CHANGE_ONR_TO_OFR:
                seed.setValue(CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_NUM_LANES_WEAVING:
                seed.setValue(CEConst.IDS_NUM_LANES_WEAVING, Integer.parseInt(valueText.getText()), segIndex);
                break;

            case STR_RAMP_TO_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

//            case STR_ML_TYPE:
//                seed.setValue(CEConst.IDS_ML_METHOD_TYPE, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
//                break;
            case STR_ML_SEPARATION:
                seed.setValue(CEConst.IDS_ML_SEPARATION_TYPE, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_NUM_LANES:
                seed.setValue(CEConst.IDS_ML_NUM_LANES, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_ML_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_DEMAND_VEH:
                seed.setValue(CEConst.IDS_ML_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_TRUCK_SINGLE_PERCENTAGE:
                seed.setValue(CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_TRUCK_TRAILER_PERCENTAGE:
                seed.setValue(CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_CAF:
                seed.setValue(CEConst.IDS_ML_USER_CAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_OAF:
                seed.setValue(CEConst.IDS_ML_USER_OAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_DAF:
                seed.setValue(CEConst.IDS_ML_USER_DAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_FFSAF:
                seed.setValue(CEConst.IDS_ML_USER_SAF, Float.parseFloat(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_ACC_DEC_LANE_LENGTH:
                seed.setValue(CEConst.IDS_ML_ACC_DEC_LANE_LENGTH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_NUM_ON_RAMP_LANES:
                seed.setValue(CEConst.IDS_ML_NUM_ON_RAMP_LANES, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_ON_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_ML_ON_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_ON_RAMP_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_NUM_OFF_RAMP_LANES:
                seed.setValue(CEConst.IDS_ML_NUM_OFF_RAMP_LANES, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_OFF_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_OFF_RAMP_FREE_FLOW_SPEED:
                seed.setValue(CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_LENGTH_OF_WEAVING:
                seed.setValue(CEConst.IDS_ML_LENGTH_SHORT, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                seed.setValue(CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                seed.setValue(CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                seed.setValue(CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_NUM_LANES_WEAVING:
                seed.setValue(CEConst.IDS_ML_NUM_LANES_WEAVING, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_RAMP_TO_RAMP_DEMAND_VEH:
                seed.setValue(CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_MIN_LC:
                seed.setValue(CEConst.IDS_ML_MIN_LANE_CHANGE_ML, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_MAX_LC:
                seed.setValue(CEConst.IDS_ML_MAX_LANE_CHANGE_ML, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_HAS_CW:
                seed.setValue(CEConst.IDS_HAS_CROSS_WEAVE, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_CW_LC_MIN:
                seed.setValue(CEConst.IDS_CROSS_WEAVE_LC_MIN, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            case STR_ML_CW_VOLUME:
                seed.setValue(CEConst.IDS_CROSS_WEAVE_VOLUME, Integer.parseInt(valueText.getText()), segIndex, periodIndex);
                break;

            default:
                System.out.println("Fill Data Error: " + itemCB.getSelectedItem().toString());
        }
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup GPMLButtonGroup;
    private javax.swing.JRadioButton allPeriodButton;
    private javax.swing.JRadioButton allSegmentButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton fillDataButton;
    private javax.swing.JRadioButton gpButton;
    private javax.swing.JComboBox itemCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton mlButton;
    private javax.swing.JButton okButton;
    private javax.swing.ButtonGroup periodButtonGroup;
    private javax.swing.JList periodList;
    private javax.swing.ButtonGroup segmentButtonGroup;
    private javax.swing.JList segmentList;
    private javax.swing.JRadioButton selectPeriodButton;
    private javax.swing.JRadioButton selectSegmentButton;
    private javax.swing.JLabel tipLabel;
    private javax.swing.JTextField valueText;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
