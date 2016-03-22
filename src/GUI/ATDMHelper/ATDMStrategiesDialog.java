package GUI.ATDMHelper;

import GUI.ATDMHelper.TableModels.StrategyManagementModel;
import GUI.ATDMHelper.hardShoulderRunning.ATDMHSRDialog;
import GUI.ATDMHelper.rampMetering.ATDMRMDialog;
import GUI.RLHelper.TableSelectionCellEditor;
import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMStrategyMat;
import java.util.Arrays;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lake
 */
public class ATDMStrategiesDialog extends javax.swing.JDialog {

    private final Seed seed;

    private final boolean strategiesSet = true;

    private final StrategyManagementModel demandManagementModel;

    private final StrategyManagementModel weatherManagementModel;

    private final StrategyManagementModel incidentManagementModel;

    private final StrategyManagementModel workZoneManagementModel;

    private final StrategyManagementModel rmManagementModel;

    private final StrategyManagementModel hsrManagementModel;

    private JComboBox[] categoryCBs;

    private JComboBox durReductionCB;

    /**
     * Creates new form ATDMStrategiesDialog
     *
     * @param startPane
     * @param seed
     * @param parent
     * @param modal
     */
    public ATDMStrategiesDialog(int startPane, Seed seed, java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.seed = seed;

        infoLabelDemand.setText("Demand strategy adjustment factors are applied across the entire facility (all segments) for all time periods.");
        infoLabelDemand.setFont(MainWindow.getTableFont());
        infoLabelWeather.setText("For all weather events (if any) of the scenario, Weather strategy adjustment factors are applied "
                + "across the entire facility (all segments) for only the time periods in which a weather event occurs.");
        infoLabelWeather.setFont(MainWindow.getTableFont());
        infoLabelIncident.setText("For all incident events (if any) of the scenario, incident strategy SAFs and CAFs are only applied to "
                + "the segments and time periods in which the incidents occur. DAFs are treated as a diversion strategy, "
                + "applied only to the upstream mainline segment and all on-ramp segments upstream of the incident. "
                + "Incident duration reduction reduces the length of each incident of the scenario by reversing "
                + "any incident adjustment factors for the specified number of periods. Duration reduction can  "
                + "effectively \"delete\" incidents if the reduction is longer than the original incident duration.");
        infoLabelIncident.setFont(MainWindow.getTableFont());
        infoLabelWZ.setText("For all work zones (if any) of the scenario, work zone strategy SAFS and CAFs are applied only to the"
                + "segments and time periods in which the work zones occur. DAFs are treated as a diversion strategy, "
                + "applied only to the upstream mainline segment and all on-ramp segments upstream of the work zone.");
        infoLabelWZ.setFont(MainWindow.getTableFont());

        //ATDMDatabase atdmDB = seed.getATDMDatabase();
        demandManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, seed, demandManagementTable);
        weatherManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, seed, weatherManagementTable);
        incidentManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, seed, incidentManagementTable);
        workZoneManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, seed, workZoneManagementTable);
        rmManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING, seed, rmManagementTable);
        hsrManagementModel = new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING, seed, hsrManagementTable);

        demandManagementTable.setModel(demandManagementModel);
        weatherManagementTable.setModel(weatherManagementModel);
        incidentManagementTable.setModel(incidentManagementModel);
        workZoneManagementTable.setModel(workZoneManagementModel);
        rmManagementTable.setModel(rmManagementModel);
        hsrManagementTable.setModel(hsrManagementModel);

        demandManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        weatherManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        incidentManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        workZoneManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        rmManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        hsrManagementTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        atdmStrategyTabbedPane.setSelectedIndex(startPane);

        // Centering Tables
        centerTables();
        setupTableFonts();
        fixColumnWidths(demandManagementTable);
        fixColumnWidths(weatherManagementTable);
        fixColumnWidths(incidentManagementTable);
        fixColumnWidths(workZoneManagementTable);
        fixColumnWidths(rmManagementTable);
        fixColumnWidths(hsrManagementTable);

        // Adding default strategies
        initCBs();

    }

    /**
     *
     * @return
     */
    public boolean stratOptionsSet() {
        return strategiesSet;
    }

    private void centerTables() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        demandManagementTable.setDefaultRenderer(Object.class, centerRenderer);
        demandManagementTable.setDefaultRenderer(Integer.class, centerRenderer);
        demandManagementTable.setDefaultRenderer(Float.class, centerRenderer);
        weatherManagementTable.setDefaultRenderer(Object.class, centerRenderer);
        weatherManagementTable.setDefaultRenderer(Integer.class, centerRenderer);
        weatherManagementTable.setDefaultRenderer(Float.class, centerRenderer);
        incidentManagementTable.setDefaultRenderer(Object.class, centerRenderer);
        incidentManagementTable.setDefaultRenderer(Integer.class, centerRenderer);
        incidentManagementTable.setDefaultRenderer(Float.class, centerRenderer);
        workZoneManagementTable.setDefaultRenderer(Object.class, centerRenderer);
        workZoneManagementTable.setDefaultRenderer(Integer.class, centerRenderer);
        workZoneManagementTable.setDefaultRenderer(Float.class, centerRenderer);

        rmManagementTable.setDefaultRenderer(Object.class, centerRenderer);
        hsrManagementTable.setDefaultRenderer(Object.class, centerRenderer);
    }

    private void setupTableFonts() {
        // Setting table fonts and row heights
        demandManagementTable.setFont(MainWindow.getTableFont());
        weatherManagementTable.setFont(MainWindow.getTableFont());
        incidentManagementTable.setFont(MainWindow.getTableFont());
        workZoneManagementTable.setFont(MainWindow.getTableFont());
        demandManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        weatherManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        incidentManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        workZoneManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        rmManagementTable.setFont(MainWindow.getTableFont());
        hsrManagementTable.setFont(MainWindow.getTableFont());
        rmManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        hsrManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        //JTextField textFieldForCellEditor = new JTextField();
        //DefaultCellEditor defaultCellEditor = new DefaultCellEditor(textFieldForCellEditor);
        //textFieldForCellEditor.setHorizontalAlignment(JTextField.LEFT);
        //textFieldForCellEditor.setBorder(null);
        //textFieldForCellEditor.setFont(MainWindow.getTableFont());
        TableSelectionCellEditor defaultCellEditor = new TableSelectionCellEditor(true);
        demandManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        weatherManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        incidentManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        workZoneManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        demandManagementTable.setDefaultEditor(Float.class, defaultCellEditor);
        weatherManagementTable.setDefaultEditor(Float.class, defaultCellEditor);
        incidentManagementTable.setDefaultEditor(Float.class, defaultCellEditor);
        workZoneManagementTable.setDefaultEditor(Float.class, defaultCellEditor);

        rmManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        hsrManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
    }

    private void fixColumnWidths(JTable table) {
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setMinWidth(25);
        table.getColumnModel().getColumn(0).setMaxWidth(25);
        //table.getColumnModel().getColumn(2).setMinWidth(130);
        //table.getColumnModel().getColumn(2).setMaxWidth(200);
        for (int colIdx = 2; colIdx < table.getColumnModel().getColumnCount(); colIdx++) {
            table.getColumnModel().getColumn(colIdx).setMinWidth(75);
            table.getColumnModel().getColumn(colIdx).setMaxWidth(75);
        }
    }

    private void initCBs() {
        categoryCBs = new JComboBox[4];
        // Demand Strategy CB
        categoryCBs[0] = new JComboBox();
        categoryCBs[0].addItem("Control Strategy");
        categoryCBs[0].addItem("Advisory Strategy");

        // Demand Strategy CB
        categoryCBs[1] = new JComboBox();
        categoryCBs[1].addItem("Control Strategy");
        categoryCBs[1].addItem("Advisory Strategy");
        categoryCBs[1].addItem("Treatment Strategy");

        // Demand Strategy CB
        categoryCBs[2] = new JComboBox();
        categoryCBs[2].addItem("Site Management & Traffic Control");
        categoryCBs[2].addItem("Advisory Strategy");
        categoryCBs[2].addItem("Detection & Verification");
        categoryCBs[2].addItem("Quick Clearance & Recovery");

        // Demand Strategy CB
        categoryCBs[3] = new JComboBox();
        categoryCBs[3].addItem("Site Management & Signal Control");
        categoryCBs[3].addItem("Advisory Strategy");

        // Duration Reduction CB
        durReductionCB = new JComboBox();
        durReductionCB.addItem("0 min");
        durReductionCB.addItem("15 min");
        durReductionCB.addItem("30 min");
        durReductionCB.addItem("45 min");
        durReductionCB.addItem("60 min");
        durReductionCB.addItem("75 min");
        durReductionCB.addItem("90 min");

        setupComboBoxes();
    }

    private void setupComboBoxes() {
        //demandManagementTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(categoryCBs[0]));
        //weatherManagementTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(categoryCBs[1]));
        //incidentManagementTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(categoryCBs[2]));
        incidentManagementTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(durReductionCB));
        //workZoneManagementTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(categoryCBs[3]));
    }

    private void createNewStrategy() {
        switch (atdmStrategyTabbedPane.getSelectedIndex()) {
            case 0:
                demandManagementModel.addStrategy();
                break;
            case 1:
                weatherManagementModel.addStrategy();
                break;
            case 2:
                incidentManagementModel.addStrategy();
                break;
            case 3:
                workZoneManagementModel.addStrategy();
                break;
            default:
                break;

        }

        setupComboBoxes();
    }

    /**
     *
     * @param strategyType
     */
    public void createNewMatStrategy(String strategyType) {
        switch (strategyType) {
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                rmManagementModel.addStrategy();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                hsrManagementModel.addStrategy();
                break;
        }

    }

    private void removeStrategy() {
        StrategyManagementModel currModel;
        int[] selectedRows;
        // Getting model type of table and selected rows for deletion
        switch (atdmStrategyTabbedPane.getSelectedIndex()) {
            case 0:
                currModel = demandManagementModel;
                selectedRows = demandManagementTable.getSelectedRows();
                break;
            case 1:
                currModel = weatherManagementModel;
                selectedRows = weatherManagementTable.getSelectedRows();
                break;
            case 2:
                currModel = incidentManagementModel;
                selectedRows = incidentManagementTable.getSelectedRows();
                break;
            case 3:
                currModel = workZoneManagementModel;
                selectedRows = workZoneManagementTable.getSelectedRows();
                break;
            default: // never used
                currModel = demandManagementModel;
                selectedRows = new int[0];
        }

        if (selectedRows.length > 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Warning: All deleted stratgies will be removed"
                    + " from any ATDM plans containing them.",
                    "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                Arrays.sort(selectedRows);
                // Iterating backwards through sorted array so as to delete strategies correctly.
                for (int stratIdx = selectedRows.length - 1; stratIdx >= 0; stratIdx--) {
                    currModel.deleteStrategy(selectedRows[stratIdx]);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No stategies selected", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
     * @param stratType
     */
    public void removeStrategy(String stratType) {
        StrategyManagementModel currModel;
        int[] selectedRows;
        // Getting model type of table and selected rows for deletion
        switch (stratType) {
            case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                currModel = demandManagementModel;
                selectedRows = demandManagementTable.getSelectedRows();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                currModel = weatherManagementModel;
                selectedRows = weatherManagementTable.getSelectedRows();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                currModel = incidentManagementModel;
                selectedRows = incidentManagementTable.getSelectedRows();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                currModel = workZoneManagementModel;
                selectedRows = workZoneManagementTable.getSelectedRows();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                currModel = rmManagementModel;
                selectedRows = rmManagementTable.getSelectedRows();
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                currModel = hsrManagementModel;
                selectedRows = hsrManagementTable.getSelectedRows();
                break;
            default: // never used
                currModel = demandManagementModel;
                selectedRows = new int[0];
        }

        if (selectedRows.length > 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Warning: All deleted stratgies will be removed"
                    + " from any ATDM plans containing them.",
                    "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                Arrays.sort(selectedRows);
                // Iterating backwards through sorted array so as to delete strategies correctly.
                for (int stratIdx = selectedRows.length - 1; stratIdx >= 0; stratIdx--) {
                    currModel.deleteStrategy(selectedRows[stratIdx]);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No stategies selected", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Matrix Strategy Editing">
    private void editRM(CA2DInt oldRampMeteringRate, int stratIdx) {
        ATDMRMDialog atdmRMDialog = new ATDMRMDialog(this, true, seed, oldRampMeteringRate);
        atdmRMDialog.setCapacityIncreaseDueToRM(((ATDMStrategyMat) seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING, stratIdx)).getCapacityIncreaseDueToRM());
        atdmRMDialog.setLocationRelativeTo(this.getRootPane());
        atdmRMDialog.setVisible(true);

        if (atdmRMDialog.getReturnStatus() == ATDMRMDialog.RET_OK) {
            CA2DInt rampMeteringRate = atdmRMDialog.getRampMeteringRate();
            ((ATDMStrategyMat) seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING, stratIdx)).setStrategyMatrix(rampMeteringRate);
            ((ATDMStrategyMat) seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING, stratIdx)).setCapacityIncreaseDueToRM(atdmRMDialog.getCapacityIncreaseDueToRM());
        }
        atdmRMDialog.dispose();
    }

    private void editHSR(CA2DInt oldHSRMatrix, int stratIdx) {
        ATDMHSRDialog atdmHSRDialog = new ATDMHSRDialog(this, true, seed, oldHSRMatrix);
        ATDMStrategyMat currStrat = (ATDMStrategyMat) seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING, stratIdx);
        atdmHSRDialog.setHSRCAF(
                currStrat.getShoulderCapacity()
        );

        atdmHSRDialog.setLocationRelativeTo(this.getRootPane());
        atdmHSRDialog.setVisible(true);

        if (atdmHSRDialog.getReturnStatus() == ATDMHSRDialog.RET_OK) {
            CA2DInt hsrMatrix = atdmHSRDialog.getHSRMatrix();
            currStrat.setStrategyMatrix(hsrMatrix);
            currStrat.setShoulderCapacity(atdmHSRDialog.getHSRCAF());
        }
    }

    /**
     *
     * @param stratType
     */
    public void editMatrixStrategy(String stratType) {
        JTable currTable;
        switch (stratType) {
            default:
            case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                currTable = rmManagementTable;
                break;
            case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                currTable = hsrManagementTable;
                break;
        }
        int currSelectedRow = currTable.getSelectedRow();
        if (currSelectedRow >= 0) {
            CA2DInt currRateMatrix = ((ATDMStrategyMat) seed.getATDMDatabase().getStrategy(stratType,
                    currSelectedRow)).getStrategyMatrix();
            CA2DInt tempRateMatrix = new CA2DInt(currRateMatrix.getSizeX(), currRateMatrix.getSizeY(), 0);
            tempRateMatrix.deepCopyFrom(currRateMatrix);
            switch (stratType) {
                case CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING:
                    editRM(tempRateMatrix, currSelectedRow);
                    break;
                case CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING:
                    editHSR(tempRateMatrix, currSelectedRow);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(null, "No Strategy Selected", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
//</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        atdmStrategyTabbedPane = new javax.swing.JTabbedPane();
        demandManagementPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        demandManagementTable = new javax.swing.JTable();
        removeButton = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        infoLabelDemand = new javax.swing.JTextArea();
        weatherManagementPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        weatherManagementTable = new javax.swing.JTable();
        addButton1 = new javax.swing.JButton();
        removeButton1 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        infoLabelWeather = new javax.swing.JTextArea();
        incidentManagementPanel = new javax.swing.JPanel();
        incidentManagementScroll = new javax.swing.JScrollPane();
        incidentManagementTable = new javax.swing.JTable();
        removeButton2 = new javax.swing.JButton();
        addButton2 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        infoLabelIncident = new javax.swing.JTextArea();
        workZonePanel = new javax.swing.JPanel();
        workZoneManagementScroll = new javax.swing.JScrollPane();
        workZoneManagementTable = new javax.swing.JTable();
        addButton3 = new javax.swing.JButton();
        removeButton3 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        infoLabelWZ = new javax.swing.JTextArea();
        otherStratPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        rmManagementTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        addRMStratButton = new javax.swing.JButton();
        editRMStratButton = new javax.swing.JButton();
        removeRMStratButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        hsrManagementTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        addHSRStratButton = new javax.swing.JButton();
        editHSRStratButton = new javax.swing.JButton();
        removeHSRStratButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATDM Strategy Configuration");
        setMinimumSize(new java.awt.Dimension(950, 613));
        setPreferredSize(new java.awt.Dimension(950, 613));
        setResizable(false);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        addButton.setText("Create New Strategy");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(900, 300));

        demandManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Description", "Category", "Demand Adj."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        demandManagementTable.setPreferredSize(new java.awt.Dimension(900, 300));
        demandManagementTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(demandManagementTable);

        removeButton.setText("Remove Strategy");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jScrollPane8.setBorder(null);
        jScrollPane8.setFocusable(false);

        infoLabelDemand.setEditable(false);
        infoLabelDemand.setBackground(new java.awt.Color(240, 240, 240));
        infoLabelDemand.setColumns(20);
        infoLabelDemand.setLineWrap(true);
        infoLabelDemand.setRows(5);
        infoLabelDemand.setWrapStyleWord(true);
        infoLabelDemand.setBorder(null);
        jScrollPane8.setViewportView(infoLabelDemand);

        javax.swing.GroupLayout demandManagementPanelLayout = new javax.swing.GroupLayout(demandManagementPanel);
        demandManagementPanel.setLayout(demandManagementPanelLayout);
        demandManagementPanelLayout.setHorizontalGroup(
            demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(demandManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, demandManagementPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addContainerGap())
        );

        demandManagementPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, removeButton});

        demandManagementPanelLayout.setVerticalGroup(
            demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(demandManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(removeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        demandManagementPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton, removeButton});

        atdmStrategyTabbedPane.addTab("Demand Management", demandManagementPanel);

        weatherManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        weatherManagementTable.setPreferredSize(new java.awt.Dimension(900, 300));
        weatherManagementTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(weatherManagementTable);

        addButton1.setText("Create New Strategy");
        addButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton1ActionPerformed(evt);
            }
        });

        removeButton1.setText("Remove Strategy");
        removeButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButton1ActionPerformed(evt);
            }
        });

        jScrollPane7.setBorder(null);
        jScrollPane7.setFocusable(false);

        infoLabelWeather.setEditable(false);
        infoLabelWeather.setBackground(new java.awt.Color(240, 240, 240));
        infoLabelWeather.setColumns(20);
        infoLabelWeather.setLineWrap(true);
        infoLabelWeather.setRows(5);
        infoLabelWeather.setWrapStyleWord(true);
        infoLabelWeather.setBorder(null);
        jScrollPane7.setViewportView(infoLabelWeather);

        javax.swing.GroupLayout weatherManagementPanelLayout = new javax.swing.GroupLayout(weatherManagementPanel);
        weatherManagementPanel.setLayout(weatherManagementPanelLayout);
        weatherManagementPanelLayout.setHorizontalGroup(
            weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, weatherManagementPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton1)))
                .addContainerGap())
        );

        weatherManagementPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton1, removeButton1});

        weatherManagementPanelLayout.setVerticalGroup(
            weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton1)
                    .addComponent(removeButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        weatherManagementPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton1, removeButton1});

        atdmStrategyTabbedPane.addTab("Weather Traffic Management", weatherManagementPanel);

        incidentManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        incidentManagementTable.getTableHeader().setReorderingAllowed(false);
        incidentManagementScroll.setViewportView(incidentManagementTable);

        removeButton2.setText("Remove Strategy");
        removeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButton2ActionPerformed(evt);
            }
        });

        addButton2.setText("Create New Strategy");
        addButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton2ActionPerformed(evt);
            }
        });

        jScrollPane6.setBorder(null);
        jScrollPane6.setFocusable(false);

        infoLabelIncident.setEditable(false);
        infoLabelIncident.setBackground(new java.awt.Color(240, 240, 240));
        infoLabelIncident.setColumns(20);
        infoLabelIncident.setLineWrap(true);
        infoLabelIncident.setRows(5);
        infoLabelIncident.setWrapStyleWord(true);
        infoLabelIncident.setBorder(null);
        jScrollPane6.setViewportView(infoLabelIncident);

        javax.swing.GroupLayout incidentManagementPanelLayout = new javax.swing.GroupLayout(incidentManagementPanel);
        incidentManagementPanel.setLayout(incidentManagementPanelLayout);
        incidentManagementPanelLayout.setHorizontalGroup(
            incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incidentManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(incidentManagementPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton2))
                    .addComponent(incidentManagementScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE))
                .addContainerGap())
        );

        incidentManagementPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton2, removeButton2});

        incidentManagementPanelLayout.setVerticalGroup(
            incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incidentManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(incidentManagementScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeButton2)
                    .addComponent(addButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        incidentManagementPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton2, removeButton2});

        atdmStrategyTabbedPane.addTab("Traffic Incident Management", incidentManagementPanel);

        workZoneManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        workZoneManagementTable.getTableHeader().setReorderingAllowed(false);
        workZoneManagementScroll.setViewportView(workZoneManagementTable);

        addButton3.setText("Create New Strategy");
        addButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton3ActionPerformed(evt);
            }
        });

        removeButton3.setText("Remove Strategy");
        removeButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButton3ActionPerformed(evt);
            }
        });

        jScrollPane5.setBorder(null);
        jScrollPane5.setFocusable(false);

        infoLabelWZ.setEditable(false);
        infoLabelWZ.setBackground(new java.awt.Color(240, 240, 240));
        infoLabelWZ.setColumns(20);
        infoLabelWZ.setLineWrap(true);
        infoLabelWZ.setRows(5);
        infoLabelWZ.setWrapStyleWord(true);
        infoLabelWZ.setBorder(null);
        jScrollPane5.setViewportView(infoLabelWZ);

        javax.swing.GroupLayout workZonePanelLayout = new javax.swing.GroupLayout(workZonePanel);
        workZonePanel.setLayout(workZonePanelLayout);
        workZonePanelLayout.setHorizontalGroup(
            workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workZonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(workZoneManagementScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workZonePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton3)))
                .addContainerGap())
        );

        workZonePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton3, removeButton3});

        workZonePanelLayout.setVerticalGroup(
            workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workZonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workZoneManagementScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton3)
                    .addComponent(removeButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        workZonePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton3, removeButton3});

        atdmStrategyTabbedPane.addTab("Work Zone Traffic Maintenance", workZonePanel);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ramp Metering"));

        rmManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        rmManagementTable.getTableHeader().setReorderingAllowed(false);
        rmManagementTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rmManagementTableFocusGained(evt);
            }
        });
        jScrollPane3.setViewportView(rmManagementTable);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        addRMStratButton.setText("Create New Ramp Metering Strategy");
        addRMStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRMStratButtonActionPerformed(evt);
            }
        });
        jPanel4.add(addRMStratButton);

        editRMStratButton.setText("View/Edit Selected Ramp Metering Strategy");
        editRMStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRMStratButtonActionPerformed(evt);
            }
        });
        jPanel4.add(editRMStratButton);

        removeRMStratButton.setText("Remove Selected Ramp Metering Strategy");
        removeRMStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRMStratButtonActionPerformed(evt);
            }
        });
        jPanel4.add(removeRMStratButton);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Hard Shoulder Running"));

        hsrManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        hsrManagementTable.getTableHeader().setReorderingAllowed(false);
        hsrManagementTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hsrManagementTableFocusGained(evt);
            }
        });
        jScrollPane4.setViewportView(hsrManagementTable);

        jPanel5.setLayout(new java.awt.GridLayout(1, 0));

        addHSRStratButton.setText("Create New HSR Strategy");
        addHSRStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHSRStratButtonActionPerformed(evt);
            }
        });
        jPanel5.add(addHSRStratButton);

        editHSRStratButton.setText("View/Edit Selected HSR Strategy");
        editHSRStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editHSRStratButtonActionPerformed(evt);
            }
        });
        jPanel5.add(editHSRStratButton);

        removeHSRStratButton.setText("Remove Selected HSR Strategy");
        removeHSRStratButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeHSRStratButtonActionPerformed(evt);
            }
        });
        jPanel5.add(removeHSRStratButton);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout otherStratPanelLayout = new javax.swing.GroupLayout(otherStratPanel);
        otherStratPanel.setLayout(otherStratPanelLayout);
        otherStratPanelLayout.setHorizontalGroup(
            otherStratPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        otherStratPanelLayout.setVerticalGroup(
            otherStratPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherStratPanelLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        atdmStrategyTabbedPane.addTab("Other", otherStratPanel);

        jPanel1.add(atdmStrategyTabbedPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        createNewStrategy();
    }//GEN-LAST:event_addButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        removeStrategy();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton1ActionPerformed
        createNewStrategy();
    }//GEN-LAST:event_addButton1ActionPerformed

    private void removeButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButton1ActionPerformed
        removeStrategy();
    }//GEN-LAST:event_removeButton1ActionPerformed

    private void removeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButton2ActionPerformed
        removeStrategy();
    }//GEN-LAST:event_removeButton2ActionPerformed

    private void addButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton2ActionPerformed
        createNewStrategy();
    }//GEN-LAST:event_addButton2ActionPerformed

    private void addButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton3ActionPerformed
        createNewStrategy();
    }//GEN-LAST:event_addButton3ActionPerformed

    private void removeButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButton3ActionPerformed
        removeStrategy();
    }//GEN-LAST:event_removeButton3ActionPerformed

    private void addRMStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRMStratButtonActionPerformed
        createNewMatStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING);
    }//GEN-LAST:event_addRMStratButtonActionPerformed

    private void removeRMStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRMStratButtonActionPerformed
        removeStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING);
    }//GEN-LAST:event_removeRMStratButtonActionPerformed

    private void addHSRStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHSRStratButtonActionPerformed
        createNewMatStrategy(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING);
    }//GEN-LAST:event_addHSRStratButtonActionPerformed

    private void removeHSRStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeHSRStratButtonActionPerformed
        removeStrategy(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING);
    }//GEN-LAST:event_removeHSRStratButtonActionPerformed

    private void editRMStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRMStratButtonActionPerformed
        editMatrixStrategy(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING);
    }//GEN-LAST:event_editRMStratButtonActionPerformed

    private void editHSRStratButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editHSRStratButtonActionPerformed
        editMatrixStrategy(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING);
    }//GEN-LAST:event_editHSRStratButtonActionPerformed

    private void rmManagementTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rmManagementTableFocusGained
        hsrManagementTable.clearSelection();
    }//GEN-LAST:event_rmManagementTableFocusGained

    private void hsrManagementTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hsrManagementTableFocusGained
        rmManagementTable.clearSelection();
    }//GEN-LAST:event_hsrManagementTableFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addButton1;
    private javax.swing.JButton addButton2;
    private javax.swing.JButton addButton3;
    private javax.swing.JButton addHSRStratButton;
    private javax.swing.JButton addRMStratButton;
    private javax.swing.JTabbedPane atdmStrategyTabbedPane;
    private javax.swing.JPanel demandManagementPanel;
    private javax.swing.JTable demandManagementTable;
    private javax.swing.JButton editHSRStratButton;
    private javax.swing.JButton editRMStratButton;
    private javax.swing.JTable hsrManagementTable;
    private javax.swing.JPanel incidentManagementPanel;
    private javax.swing.JScrollPane incidentManagementScroll;
    private javax.swing.JTable incidentManagementTable;
    private javax.swing.JTextArea infoLabelDemand;
    private javax.swing.JTextArea infoLabelIncident;
    private javax.swing.JTextArea infoLabelWZ;
    private javax.swing.JTextArea infoLabelWeather;
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
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel otherStratPanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeButton1;
    private javax.swing.JButton removeButton2;
    private javax.swing.JButton removeButton3;
    private javax.swing.JButton removeHSRStratButton;
    private javax.swing.JButton removeRMStratButton;
    private javax.swing.JTable rmManagementTable;
    private javax.swing.JPanel weatherManagementPanel;
    private javax.swing.JTable weatherManagementTable;
    private javax.swing.JScrollPane workZoneManagementScroll;
    private javax.swing.JTable workZoneManagementTable;
    private javax.swing.JPanel workZonePanel;
    // End of variables declaration//GEN-END:variables
}
