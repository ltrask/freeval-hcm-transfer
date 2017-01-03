package GUI.RLHelper;

import GUI.RLHelper.Renderer.AltColorRendererWithBlackout;
import GUI.RLHelper.Renderer.AlternatingColorsRenderer;
import GUI.RLHelper.TableModels.AdjFactorModel;
import GUI.major.MainWindow;
import static coreEngine.Helper.CEConst.IDS_NUM_PERIOD;
import static coreEngine.Helper.CEConst.IDS_NUM_SEGMENT;
import coreEngine.Helper.CEDate;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import coreEngine.reliabilityAnalysis.DataStruct.WorkZone;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Lake Trask
 */
public class WorkZonePanel extends javax.swing.JPanel {

    private DemandData demandData;
    //private IncidentData incidentData;

    private ArrayList<WorkZone> wzData;

    private Seed seed;

    private AdjFactorModel wzFFSAFModel;

    private AdjFactorModel wzCAFModel;

    private AdjFactorModel wzDAFModel;

    private AdjFactorModel wzLAFModel;

    private WorkZoneAdjustmentFactorTableModel adjFactorModel;
    //private AlternatingColorsRenderer alternatingColorsRenderer;

    private AltColorRendererWithBlackout blackoutRenderer;

    /**
     * Creates new form WorkZonePanel
     */
    public WorkZonePanel() {
        initComponents();

        workZoneAFInfoLabel.setText("<HTML><CENTER> Select a work zone to use "
                + "the above table to view and edit adjustment factors for the "
                + "work zone.  The default factors are those computed through "
                + "the methodology of Ch. 10 (3-107).");
    }

    // <editor-fold defaultstate="collapsed" desc="Activators">
    /**
     *
     * @param demandData
     */
    public void activateWorkZonePanel(DemandData demandData) {
        this.demandData = demandData;

        activateWorkZonePanel();
    }

    /**
     *
     * @param seed
     */
    public void activateWorkZonePanel(Seed seed) {

        this.seed = seed;
        initializeWZPickerComboBoxes(this.seed);
        activateWorkZonePanel();
        wzData = seed.getRLWorkZones();
        if (wzData == null) {
            wzData = new ArrayList<>();
        }
        // Adding existing work zones to display model
        DefaultListModel model = (DefaultListModel) workZonesList.getModel();
        for (WorkZone wz : wzData) {
            model.addElement(wz.toString());
        }

    }

    public void activateWorkZonePanel() {

        wzData = new ArrayList<>();

        workZonesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //wzAdjFactorTable = new WorkZoneAdjustmentFactorTable();
        adjFactorModel = new WorkZoneAdjustmentFactorTableModel(null, wzAdjFactorTable);

        wzAdjFactorTable.setModel(adjFactorModel);
        wzAdjFactorTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        wzAdjFactorTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        wzAdjFactorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        wzAdjFactorTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        adjFactorModel.fireTableStructureChanged();
    }

    /**
     *
     */
    public void activateWorkZonePanelOld() {

        wzData = new ArrayList<>();
        wzFFSAFModel = new AdjFactorModel(0);
        wzCAFModel = new AdjFactorModel(1);
        wzDAFModel = new AdjFactorModel(2);
        wzLAFModel = new AdjFactorModel(3);

        // Renderer for adjustment factors
        //alternatingColorsRenderer = new AlternatingColorsRenderer(true);
        //alternatingColorsRenderer.setRowHeaderAlignment(JTextField.CENTER);
        blackoutRenderer = new AltColorRendererWithBlackout(true);
        blackoutRenderer.setRowHeaderAlignment(JTextField.CENTER);

        // Setting up incident FFSAF table
        wzSAFs.setModel(wzFFSAFModel);
        wzSAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        wzSAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        wzSAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        Font tableFont = wzSAFs.getTableHeader().getFont();
        Font newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        wzSAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident DAF table
        wzDAFs.setModel(wzDAFModel);
        wzDAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        wzDAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        wzDAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = wzDAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        wzDAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident CAF table
        wzCAFs.setModel(wzCAFModel);
        wzCAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        wzCAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        wzCAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = wzCAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        wzCAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident LAF table
        wzLAFs.setModel(wzLAFModel);
        wzLAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        wzLAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        wzLAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = wzLAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        wzLAFs.getTableHeader().setFont(newHeaderFont);

    }

    //</editor-fold>
    /**
     *
     * @param demandData
     */
    public void setDemandData(DemandData demandData) {
        this.demandData = demandData;
    }

    /**
     *
     * @return
     */
    public ArrayList<WorkZone> getWorkZoneData() {
        return this.wzData;
    }

    /**
     * Method to check whether or not a candidate work zone overlaps with any of
     * the existing work zones.
     *
     * @param wz
     * @return Boolean True if there is overlap, Boolean False otherwise.
     */
    public boolean checkOverlap(WorkZone wz) {
        for (WorkZone workZone : wzData) {
            if (workZone.hasOverlap(wz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the work zone panel if the year is changed for the RRP Dates
     *
     * @param year
     */
    public void updatePanel(int year) {
        updateWZPickerComboBoxes(year);
        removeAllWorkZones();
    }

    /**
     * Method to read and set list of excluded days from the seed.
     *
     * @param seed
     */
    private void initializeWZPickerComboBoxes(Seed seed) {

        // Setting up model for combox boxes.
        updateWZPickerComboBoxes(seed.getSeedFileDate().year);
        startSegmentCB.setModel(segmentCBModelCreator());
        endSegmentCB.setModel(segmentCBModelCreator());
        startPeriodCB.setModel(periodCBModelCreator(0));
        endPeriodCB.setModel(periodCBModelCreator(1));
    }

    /**
     * Updates the model of for the dayPicker combo box to show correct days.
     * Called when the RRP date changes.
     *
     * @param year RRP year
     */
    private void updateWZPickerComboBoxes(int year) {
        dayPickerBegin.setModel(ScenarioGeneratorDialog.modelCreator(monthPickerBegin.getSelectedIndex() + 1, year));
        dayPickerEnd.setModel(ScenarioGeneratorDialog.modelCreator(monthPickerEnd.getSelectedIndex() + 1, year));
    }

    private DefaultComboBoxModel segmentCBModelCreator() {
        String[] tempArr = new String[seed.getValueInt(IDS_NUM_SEGMENT) + 1];
        //System.out.println(tempArr.length);
        tempArr[0] = "Select";
        for (int segIdx = 1; segIdx <= tempArr.length - 1; segIdx++) {
            tempArr[segIdx] = String.valueOf(segIdx);
        }

        return new DefaultComboBoxModel(tempArr);
    }

    /**
     * Creates the model for the period selection combo box. The type designates
     * whether it is for start period (0) selection or end period selection (1).
     * End period selection indicates that the periods go through the end
     * period, and the clock time displayed is the time at the end of the period
     * (as opposed to clock time at the beginning of the period for start
     * times).
     *
     * @param type
     * @return
     */
    private DefaultComboBoxModel periodCBModelCreator(int type) {
        String[] tempArr = new String[seed.getValueInt(IDS_NUM_PERIOD) + 1];
        tempArr[0] = "Select";
        int currHour = seed.getStartTime().hour;
        int currMin = seed.getStartTime().minute;
        if (type == 1) {
            currMin += 15;
        }
        for (int perIdx = 1; perIdx <= tempArr.length - 1; perIdx++) {
            if (currMin == 60) {
                currMin = 0;
                currHour++;
            }
            if (currMin == 0) {
                tempArr[perIdx] = String.valueOf(perIdx) + "  (" + currHour + ":00)";
            } else {
                tempArr[perIdx] = String.valueOf(perIdx) + "  (" + currHour + ":" + currMin + ")";
            }
            currMin += 15;
        }

        return new DefaultComboBoxModel(tempArr);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        adjFactorPanelDeprecated = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        wzCAFs = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        wzSAFs = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        wzDAFs = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        wzLAFs = new javax.swing.JTable();
        allSegmentsCheckBox = new javax.swing.JCheckBox();
        workZoneSelectorPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        workZonesList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        monthPickerBegin = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        dayPickerBegin = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        monthPickerEnd = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        dayPickerEnd = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        startSegmentCB = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        endSegmentCB = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        wzTypeCB = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        areaTypeCB = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        barrierTypeCB = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        wzSpeedLimitTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        latDistanceSpinner = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        alphaWZSpinner = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        startPeriodCB = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        endPeriodCB = new javax.swing.JComboBox();
        allPeriodsCheckBox = new javax.swing.JCheckBox();
        wzAdjFactorPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        wzAdjFactorTable = new javax.swing.JTable() {
            @Override
            public TableCellRenderer getCellRenderer(int row, int col) {
                return ((WorkZoneAdjustmentFactorTableModel) this.getModel()).getRenderer(row, col);
            }
        };
        workZoneAFInfoLabel = new javax.swing.JLabel();

        adjFactorPanelDeprecated.setBorder(javax.swing.BorderFactory.createTitledBorder("Work Zone Adjustment Factors"));
        java.awt.GridBagLayout adjFactorPanelLayout = new java.awt.GridBagLayout();
        adjFactorPanelLayout.columnWidths = new int[] {480, 480};
        adjFactorPanelLayout.rowHeights = new int[] {16, 134, 16, 134};
        adjFactorPanelDeprecated.setLayout(adjFactorPanelLayout);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Capacity Adjustment Factors (CAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        adjFactorPanelDeprecated.add(jLabel2, gridBagConstraints);

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("FFS Adjustment Factors (SAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        adjFactorPanelDeprecated.add(jLabel1, gridBagConstraints);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(452, 100));

        wzCAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"3",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"4",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"5",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"6",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"7",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"8",  new Float(1.0),  new Float(1.0), null}
            },
            new String [] {
                "Number of Lanes (1 Direction)", "No Incident", "Shoulder Closure", "One Lane Closure"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        wzCAFs.setFillsViewportHeight(true);
        wzCAFs.setPreferredSize(new java.awt.Dimension(450, 135));
        wzCAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(wzCAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        adjFactorPanelDeprecated.add(jScrollPane2, gridBagConstraints);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(452, 100));

        wzSAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"3",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"4",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"5",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"6",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"7",  new Float(1.0),  new Float(1.0),  new Float(1.0)},
                {"8",  new Float(1.0),  new Float(1.0),  new Float(1.0)}
            },
            new String [] {
                "Number of Lanes (1 Direction)", "No Incident", "Shoulder Closure", "One Lane Closure"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        wzSAFs.setFillsViewportHeight(true);
        wzSAFs.setPreferredSize(new java.awt.Dimension(450, 135));
        wzSAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(wzSAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        adjFactorPanelDeprecated.add(jScrollPane3, gridBagConstraints);

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Demand Adjustment Factors (DAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        adjFactorPanelDeprecated.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Lane Adjustment Factors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        adjFactorPanelDeprecated.add(jLabel4, gridBagConstraints);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(452, 100));

        wzDAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"3",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"4",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"5",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"6",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"7",  new Float(1.0),  new Float(0.81),  new Float(0.7)},
                {"8",  new Float(1.0),  new Float(0.81),  new Float(0.7)}
            },
            new String [] {
                "Number of Lanes (1 Direction)", "No Incident", "Shoulder Closure", "One Lane Closure"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        wzDAFs.setFillsViewportHeight(true);
        wzDAFs.setPreferredSize(new java.awt.Dimension(450, 135));
        wzDAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(wzDAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        adjFactorPanelDeprecated.add(jScrollPane4, gridBagConstraints);

        jScrollPane6.setPreferredSize(new java.awt.Dimension(452, 100));

        wzLAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2", null, null, null},
                {"3", null, null, null},
                {"4", null, null, null},
                {"5", null, null, null},
                {"6", null, null, null},
                {"7", null, null, null},
                {"8", null, null, null}
            },
            new String [] {
                "Number of Lanes", "No Incident", "Shoulder Closure", "One Lane Closure"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        wzLAFs.setFillsViewportHeight(true);
        wzLAFs.setPreferredSize(new java.awt.Dimension(450, 135));
        wzLAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(wzLAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        adjFactorPanelDeprecated.add(jScrollPane6, gridBagConstraints);

        allSegmentsCheckBox.setText("All");
        allSegmentsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        allSegmentsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                allSegmentsCheckBoxItemStateChanged(evt);
            }
        });

        workZoneSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Work Zone Time and Location"));

        workZonesList.setModel(new DefaultListModel());
        workZonesList.setFixedCellWidth(100);
        workZonesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                workZonesListValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(workZonesList);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Dates"));
        jPanel1.setLayout(new java.awt.GridLayout(4, 2));

        jLabel6.setText(" Start:");
        jPanel1.add(jLabel6);

        monthPickerBegin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        monthPickerBegin.setMaximumSize(new java.awt.Dimension(122, 27));
        monthPickerBegin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                monthPickerBeginItemStateChanged(evt);
            }
        });
        jPanel1.add(monthPickerBegin);
        jPanel1.add(jLabel7);

        dayPickerBegin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        dayPickerBegin.setMaximumSize(new java.awt.Dimension(122, 27));
        dayPickerBegin.setMinimumSize(new java.awt.Dimension(122, 27));
        dayPickerBegin.setPreferredSize(new java.awt.Dimension(122, 27));
        jPanel1.add(dayPickerBegin);

        jLabel11.setText(" End:");
        jPanel1.add(jLabel11);

        monthPickerEnd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", " " }));
        monthPickerEnd.setMaximumSize(new java.awt.Dimension(122, 27));
        monthPickerEnd.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                monthPickerEndItemStateChanged(evt);
            }
        });
        jPanel1.add(monthPickerEnd);
        jPanel1.add(jLabel10);

        dayPickerEnd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        dayPickerEnd.setMaximumSize(new java.awt.Dimension(122, 27));
        dayPickerEnd.setMinimumSize(new java.awt.Dimension(122, 27));
        dayPickerEnd.setPreferredSize(new java.awt.Dimension(122, 27));
        jPanel1.add(dayPickerEnd);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Segments"));
        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        jLabel17.setText(" Start:");
        jPanel2.add(jLabel17);

        jPanel2.add(startSegmentCB);

        jLabel16.setText(" End:");
        jPanel2.add(jLabel16);

        jPanel2.add(endSegmentCB);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Work Zone Parameters"));
        jPanel3.setLayout(new java.awt.GridLayout(6, 2));

        jLabel9.setText(" Severity");
        jPanel3.add(jLabel9);

        wzTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Choose Type", "Shoulder Closure", "1 Lane Closure", "2 Lane Closure", "3+ Lane Closure" }));
        jPanel3.add(wzTypeCB);

        jLabel15.setText(" Area Type");
        jPanel3.add(jLabel15);

        areaTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Choose Area Type", "Urban", "Rural" }));
        jPanel3.add(areaTypeCB);

        jLabel12.setText(" Barrier Type");
        jPanel3.add(jLabel12);

        barrierTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Choose Barrier Type", "Concrete", "Type II", "Plastic Drum" }));
        jPanel3.add(barrierTypeCB);

        jLabel13.setText(" Work Zone Speed Limit");
        jPanel3.add(jLabel13);

        wzSpeedLimitTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        wzSpeedLimitTextField.setText("55.0");
        jPanel3.add(wzSpeedLimitTextField);

        jLabel14.setText(" Lateral Distance (ft.)");
        jPanel3.add(jLabel14);

        latDistanceSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(12.0f), Float.valueOf(0.01f)));
        jPanel3.add(latDistanceSpinner);

        jLabel5.setText("WZ Capacity Drop (%)");
        jLabel5.setToolTipText("The percentage drop in pre-breakdown capacity at the work zone due to queuing conditions (%).");
        jPanel3.add(jLabel5);

        alphaWZSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(13.4f), Float.valueOf(0.0f), Float.valueOf(30.0f), Float.valueOf(0.1f)));
        alphaWZSpinner.setToolTipText("The percentage drop in pre-breakdown capacity at the work zone due to queuing conditions (%).");
        jPanel3.add(alphaWZSpinner);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jPanel4.add(addButton);

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jPanel4.add(removeButton);

        removeAllButton.setText("Remove All");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });
        jPanel4.add(removeAllButton);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Analysis Periods"));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("to");
        jLabel8.setPreferredSize(new java.awt.Dimension(18, 16));

        allPeriodsCheckBox.setText("All");
        allPeriodsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        allPeriodsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                allPeriodsCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startPeriodCB, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endPeriodCB, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allPeriodsCheckBox)
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endPeriodCB, startPeriodCB});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(startPeriodCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(endPeriodCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(allPeriodsCheckBox))))
                .addGap(0, 0, 0))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {endPeriodCB, startPeriodCB});

        javax.swing.GroupLayout workZoneSelectorPanelLayout = new javax.swing.GroupLayout(workZoneSelectorPanel);
        workZoneSelectorPanel.setLayout(workZoneSelectorPanelLayout);
        workZoneSelectorPanelLayout.setHorizontalGroup(
            workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workZoneSelectorPanelLayout.createSequentialGroup()
                .addGroup(workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(workZoneSelectorPanelLayout.createSequentialGroup()
                        .addGroup(workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)))
        );
        workZoneSelectorPanelLayout.setVerticalGroup(
            workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workZoneSelectorPanelLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5))
            .addGroup(workZoneSelectorPanelLayout.createSequentialGroup()
                .addGroup(workZoneSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(workZoneSelectorPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wzAdjFactorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Work Zone Adjustment Factors\n"));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        wzAdjFactorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        wzAdjFactorTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(wzAdjFactorTable);

        workZoneAFInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        workZoneAFInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        workZoneAFInfoLabel.setText("Label");

        javax.swing.GroupLayout wzAdjFactorPanelLayout = new javax.swing.GroupLayout(wzAdjFactorPanel);
        wzAdjFactorPanel.setLayout(wzAdjFactorPanelLayout);
        wzAdjFactorPanelLayout.setHorizontalGroup(
            wzAdjFactorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(workZoneAFInfoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        wzAdjFactorPanelLayout.setVerticalGroup(
            wzAdjFactorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wzAdjFactorPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workZoneAFInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(workZoneSelectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(wzAdjFactorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(workZoneSelectorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wzAdjFactorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void monthPickerEndItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_monthPickerEndItemStateChanged
        int year = demandData.getYear();
        int currIdx = dayPickerEnd.getSelectedIndex();
        dayPickerEnd.setModel(ScenarioGeneratorDialog.modelCreator(monthPickerEnd.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= dayPickerEnd.getModel().getSize()) {
            currIdx = dayPickerEnd.getModel().getSize() - 1;
        }
        dayPickerEnd.setSelectedIndex(currIdx);
    }//GEN-LAST:event_monthPickerEndItemStateChanged

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        boolean datesOutsideOfRRP = false;
        boolean wzDatesInvalid = false;
        boolean segmentsInvalid = false;
        boolean periodsInvalid = false;
        boolean wzSpeedLimitInvalid = false;

        int startSegment;
        int endSegment;
        if (allSegmentsCheckBox.isSelected() == false) {
            startSegment = startSegmentCB.getSelectedIndex();
            endSegment = endSegmentCB.getSelectedIndex();
        } else {
            startSegment = 1;
            endSegment = endSegmentCB.getItemCount() - 1;
        }

        if (endSegment < startSegment) {
            segmentsInvalid = true;
        }

        int startPeriod;
        int endPeriod;
        if (allPeriodsCheckBox.isSelected() == false) {
            startPeriod = startPeriodCB.getSelectedIndex();
            endPeriod = endPeriodCB.getSelectedIndex();
        } else {
            startPeriod = 1;
            endPeriod = endPeriodCB.getItemCount() - 1;
        }

        if (endPeriod < startPeriod) {
            periodsInvalid = true;
        }

        if (monthPickerEnd.getSelectedIndex() < monthPickerBegin.getSelectedIndex()) {
            wzDatesInvalid = true;
        } else if (monthPickerEnd.getSelectedIndex() == monthPickerBegin.getSelectedIndex()) {
            if (dayPickerEnd.getSelectedIndex() < dayPickerBegin.getSelectedIndex()) {
                wzDatesInvalid = true;
            }
        }
        try {
            float tempVal = Float.valueOf(wzSpeedLimitTextField.getText());
            if (tempVal <= 0.0f) {
                wzSpeedLimitInvalid = true;
            }
        } catch (NumberFormatException e) {
            wzSpeedLimitInvalid = true;
        }

        if ((startSegment == 0 && startSegmentCB.isEditable()) || (endSegment == 0 && endSegmentCB.isEnabled()) || segmentsInvalid) {
            JOptionPane.showMessageDialog(this, "Please Specifiy Valid Work Zone Segments");
        } else if ((startPeriod == 0 && startPeriodCB.isEnabled()) || (endPeriodCB.isEnabled() && endPeriod == 0) || periodsInvalid) {
            JOptionPane.showMessageDialog(this, "Please Specifiy Valid Work Zone Periods", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else if (wzTypeCB.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please Specifiy Valid Work Zone Type", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else if (wzDatesInvalid) {
            JOptionPane.showMessageDialog(this, "Please specifiy Valid Work Zone Dates", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else if (barrierTypeCB.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please specifiy Valid Work Zone Barrier Type", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else if (areaTypeCB.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please specifiy Valid Work Zone Area Type", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else if (wzSpeedLimitInvalid) {
            JOptionPane.showMessageDialog(this, "Please specifiy A Valid Work Zone Speed Limit", "Work Zone: Data Entry Error", JOptionPane.ERROR_MESSAGE);
        } else {

            CEDate startDate = new CEDate(demandData.getYear(), monthPickerBegin.getSelectedIndex() + 1, dayPickerBegin.getSelectedIndex() + 1);
            CEDate endDate = new CEDate(demandData.getYear(), monthPickerEnd.getSelectedIndex() + 1, dayPickerEnd.getSelectedIndex() + 1);

            if (startDate.month < seed.getRRPStartDate().month || (startDate.month == seed.getRRPStartDate().month && startDate.day < seed.getRRPStartDate().day)) {
                datesOutsideOfRRP = true;
            } else if (endDate.month > seed.getRRPEndDate().month || (endDate.month == seed.getRRPEndDate().month && endDate.day > seed.getRRPEndDate().day)) {
                datesOutsideOfRRP = true;
            }
            if (datesOutsideOfRRP) {
                String errorString = "<HTML><CENTER>Error: Work zone falls outside of specified RRP Dates:<br><br>"
                        + seed.getRRPStartDate().toWorkZoneString() + " - " + seed.getRRPEndDate().toWorkZoneString();
                JOptionPane.showMessageDialog(this, errorString, "Work Zone Dates Error", JOptionPane.ERROR_MESSAGE);
            } else {
                DefaultListModel model = (DefaultListModel) workZonesList.getModel();
                String text = (String) wzTypeCB.getSelectedItem() + ": "
                        + monthPickerBegin.getSelectedItem().toString()
                        + " "
                        + dayPickerBegin.getSelectedItem().toString()
                        + " - " + monthPickerEnd.getSelectedItem().toString()
                        + " "
                        + dayPickerEnd.getSelectedItem().toString()
                        + "  (Seg. " + startSegment + " - " + endSegment + ")"
                        + "  (Per. " + startPeriod + " - " + endPeriod + ")";

                //WorkZoneEvent candidateWorkZone = new WorkZoneEvent(startDate, endDate, startSegment, endSegment, startPeriod, endPeriod, wzTypeCB.getSelectedIndex() - 1);
                WorkZone candidateWorkZone = new WorkZone(seed, // seed instance
                        wzTypeCB.getSelectedIndex() - 1, // Severity
                        startPeriod - 1, // Start period
                        endPeriod - startPeriod + 1, // Duration
                        startDate, // Start date
                        endDate, // End date
                        startSegment - 1, // Start segment
                        endSegment - 1 // End segment
                );

                // Setting work zone parameters
                if (barrierTypeCB.getSelectedIndex() == 1) {
                    candidateWorkZone.setBarrierType(WorkZone.BARRIER_TYPE_CONCRETE);
                } else {
                    candidateWorkZone.setBarrierType(WorkZone.BARRIER_TYPE_OTHER);
                }
                if (areaTypeCB.getSelectedIndex() == 1) {
                    candidateWorkZone.setAreaType(WorkZone.AREA_TYPE_URBAN);
                } else {
                    candidateWorkZone.setAreaType(WorkZone.AREA_TYPE_RURAL);
                }

                candidateWorkZone.setLateralDistance((float) latDistanceSpinner.getValue());
                candidateWorkZone.setWorkZoneSpeedLimit(Float.valueOf(wzSpeedLimitTextField.getText()));
                candidateWorkZone.alphaWZ = (float) alphaWZSpinner.getValue();

                if (candidateWorkZone.checkFeasibleLaneClosure()) {
                    if (!checkOverlap(candidateWorkZone)) {
                        model.addElement(text);

                        wzData.add(candidateWorkZone);
                    } else {
                        JOptionPane.showMessageDialog(this, "<HTML><CENTER>Error:"
                                + "  The work zone you are trying to add overlaps"
                                + " with an<br>existing work zone and cannot be "
                                + "added.", "Overlapping Work Zones Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "<HTML><CENTER> ERROR: "
                            + "The work zone is infeasible because the severity"
                            + " exceeds the allowable lane closure in one or "
                            + "more of the specified segments.<br>At least one "
                            + "lane must remain open in every segment for the "
                            + "work zone to be feasible.", "Infeasible Work Zone Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }//GEN-LAST:event_addButtonActionPerformed

    private void monthPickerBeginItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_monthPickerBeginItemStateChanged
        int year = demandData.getYear();
        int currIdx = dayPickerBegin.getSelectedIndex();
        dayPickerBegin.setModel(ScenarioGeneratorDialog.modelCreator(monthPickerBegin.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= dayPickerBegin.getModel().getSize()) {
            currIdx = dayPickerBegin.getModel().getSize() - 1;
        }
        dayPickerBegin.setSelectedIndex(currIdx);
    }//GEN-LAST:event_monthPickerBeginItemStateChanged

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed

        removeAllWorkZones();
    }//GEN-LAST:event_removeAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

        DefaultListModel model = (DefaultListModel) workZonesList.getModel();
        List<String> selectedStrings = workZonesList.getSelectedValuesList();
        int[] mapper = workZonesList.getSelectedIndices();
        for (int i = selectedStrings.size() - 1; i >= 0; i--) {                   //Backwards loop to ensure correct removal
            model.removeElement(selectedStrings.get(i));
            wzData.remove(mapper[i]);
        }

    }//GEN-LAST:event_removeButtonActionPerformed

    private void allPeriodsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_allPeriodsCheckBoxItemStateChanged
        if (allPeriodsCheckBox.isSelected()) {
            startPeriodCB.setEnabled(false);
            endPeriodCB.setEnabled(false);
        } else {
            startPeriodCB.setEnabled(true);
            endPeriodCB.setEnabled(true);
        }
    }//GEN-LAST:event_allPeriodsCheckBoxItemStateChanged

    private void allSegmentsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_allSegmentsCheckBoxItemStateChanged
        if (allSegmentsCheckBox.isSelected()) {
            startSegmentCB.setEnabled(false);
            endSegmentCB.setEnabled(false);
        } else {
            startSegmentCB.setEnabled(true);
            endSegmentCB.setEnabled(true);
        }
    }//GEN-LAST:event_allSegmentsCheckBoxItemStateChanged

    private void workZonesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_workZonesListValueChanged
        if (workZonesList.getSelectedIndex() == -1) {
            adjFactorModel.setWorkZone(null);
        } else {
            adjFactorModel.setWorkZone(wzData.get(workZonesList.getSelectedIndex()));
        }
    }//GEN-LAST:event_workZonesListValueChanged

    /**
     * Setter for table font. Does not override renderer font, but sets table
     * row heights correctly.
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        wzCAFs.setFont(newTableFont);
        wzCAFs.setRowHeight(newTableFont.getSize() + 2);
        wzDAFs.setFont(newTableFont);
        wzDAFs.setRowHeight(newTableFont.getSize() + 2);
        wzLAFs.setFont(newTableFont);
        wzLAFs.setRowHeight(newTableFont.getSize() + 2);
        wzSAFs.setFont(newTableFont);
        wzSAFs.setRowHeight(newTableFont.getSize() + 2);
    }

    /**
     * Method to clear all existing work zones.
     */
    public void removeAllWorkZones() {
        DefaultListModel model = (DefaultListModel) workZonesList.getModel();
        model.clear();

        wzData.clear();
    }

    private class WorkZoneAdjustmentFactorTableModel extends AbstractTableModel {

        private static final int ROW_CAF = 0;
        private static final int ROW_DAF = 2;
        private static final int ROW_SAF = 1;

        private final String[] rowNames = {"CAF", "SAF", "DAF"};

        private WorkZone workZone;

        private final JTable parentTable;

        private final AlternatingColorsRenderer altColorRenderer;

        public WorkZoneAdjustmentFactorTableModel(WorkZone workZone, JTable parentTable) {

            this.workZone = workZone;

            this.parentTable = parentTable;

            // Renderers
            this.altColorRenderer = new AlternatingColorsRenderer(true, "%.2f");

        }

        public void setWorkZone(WorkZone workZone) {
            this.workZone = workZone;
            this.fireTableStructureChanged();
        }

        @Override
        public int getColumnCount() {
            if (workZone == null) {
                return 1;
            } else {
                return workZone.getNumberOfSegments() + 1;
            }
        }

        @Override
        public String getColumnName(int col) {
            //int col_to_seg = (col - 1) + workZone.getStartSegment();
            return (col == 0) ? "*" : "Seg " + (col + workZone.getStartSegment());  // Adding one so indexing starts at 1
        }

        @Override
        public int getRowCount() {
            return rowNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return rowNames[row];
            } else {
                int col_to_seg = (col - 1) + workZone.getStartSegment();
                switch (row) {
                    case ROW_CAF:
                        return workZone.getEventCAF(col_to_seg);
                    case ROW_SAF:
                        return workZone.getEventSAF(col_to_seg);
                    case ROW_DAF:
                        return workZone.getEventDAF(col_to_seg);
                    default:
                        throw new RuntimeException("Invalid Row");
                }
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            try {
                float fVal = Float.valueOf((String) value);
                int col_to_seg = (col - 1) + workZone.getStartSegment();
                switch (row) {
                    case ROW_CAF:
                        workZone.setEventCAF(fVal, col_to_seg);
                        break;
                    case ROW_SAF:
                        workZone.setEventSAF(fVal, col_to_seg);
                        break;
                    case ROW_DAF:
                        workZone.setEventDAF(fVal, col_to_seg);
                        break;
                    default:
                        throw new RuntimeException("Invalid Row");
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void fireTableStructureChanged() {
            super.fireTableStructureChanged();
            // Fix Table column sizes
            parentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            parentTable.getColumnModel().getColumn(0).setMinWidth(100);
            for (int col = 1; col < this.getColumnCount(); col++) {
                parentTable.getColumnModel().getColumn(col).setMinWidth(100);
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return (col != 0 && workZone != null);
        }

        public TableCellRenderer getRenderer(int row, int col) {
            return altColorRenderer;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel adjFactorPanelDeprecated;
    private javax.swing.JCheckBox allPeriodsCheckBox;
    private javax.swing.JCheckBox allSegmentsCheckBox;
    private javax.swing.JSpinner alphaWZSpinner;
    private javax.swing.JComboBox areaTypeCB;
    private javax.swing.JComboBox barrierTypeCB;
    private javax.swing.JComboBox dayPickerBegin;
    private javax.swing.JComboBox dayPickerEnd;
    private javax.swing.JComboBox endPeriodCB;
    private javax.swing.JComboBox endSegmentCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSpinner latDistanceSpinner;
    private javax.swing.JComboBox monthPickerBegin;
    private javax.swing.JComboBox monthPickerEnd;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox startPeriodCB;
    private javax.swing.JComboBox startSegmentCB;
    private javax.swing.JLabel workZoneAFInfoLabel;
    private javax.swing.JPanel workZoneSelectorPanel;
    private javax.swing.JList workZonesList;
    private javax.swing.JPanel wzAdjFactorPanel;
    private javax.swing.JTable wzAdjFactorTable;
    private javax.swing.JTable wzCAFs;
    private javax.swing.JTable wzDAFs;
    private javax.swing.JTable wzLAFs;
    private javax.swing.JTable wzSAFs;
    private javax.swing.JTextField wzSpeedLimitTextField;
    private javax.swing.JComboBox wzTypeCB;
    // End of variables declaration//GEN-END:variables
}
