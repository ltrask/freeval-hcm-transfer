package GUI.ATDMHelper;

import GUI.ATDMHelper.TableModels.ATDMModel;
import GUI.ATDMHelper.TableModels.ATDMPlanEditorModel;
import GUI.ATDMHelper.TableModels.StrategyManagementModel;
import GUI.ATDMHelper.TableModels.StrategySelectorModel;
import GUI.ATDMHelper.rampMetering.ATDMRMDialog;
import GUI.RLHelper.TableSelectionCellEditor;
import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMDatabase;
import coreEngine.atdm.DataStruct.ATDMPlan;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lake Trask
 */
public class ATDMPlansDialog extends javax.swing.JDialog {

    private final ATDMPlanEditorModel atdmPlanModel;

    private final ATDMModel atdmModel;

    private final StrategySelectorModel[] strategySelectorModels;

    private final Seed seed;

    private final DecimalFormat cafFormatter = new DecimalFormat("###,###.0##");

    /**
     * Creates new form ATDMPlansDialog
     *
     * @param parent
     * @param modal
     * @param seed
     * @param atdmModel
     */
    public ATDMPlansDialog(java.awt.Dialog parent, boolean modal, Seed seed, ATDMModel atdmModel) {
        super(parent, modal);
        initComponents();

        this.seed = seed;
        this.atdmModel = atdmModel;

        infoLabelDemand.setText("<HTML><b>Demand strategy</b> adjustment factors are applied across the entire facility (all segments) for all RRP time periods.");
        infoLabelDemand.setFont(MainWindow.getTableFont());
        infoLabelWeather.setText("<HTML>For all weather events (if any) of the scenario, <b>Weather strategy</b> adjustment factors "
                + "are applied across the entire facility (all segments)"
                + " for only the RRP time periods in which a weather event occurs.");
        infoLabelWeather.setFont(MainWindow.getTableFont());
        infoLabelIncident.setText("<HTML>For all incident events (if any) of the scenario, <b>incident strategy</b> SAFs and CAFs are only applied to "
                + "the segments and RRP periods in which the incidents occur. DAFs are treated as a diversion strategy, "
                + "applied only to the upstream mainline segment and all on-ramp segments upstream of the incident. "
                + "<b>Incident duration reduction</b> reduces the length of each incident of the scenario by reversing "
                + "any incident adjustment factors for the specified number of periods. Duration reduction can "
                + "effectively \"delete\" incidents if the reduction is longer than the original incident duration.");
        infoLabelIncident.setFont(MainWindow.getTableFont());
        infoLabelWZ.setText("<HTML>For all work zones (if any) of the scenario, <b>work zone strategy</b> SAFS and CAFs are applied only to the "
                + "segments and RRP periods in which the work zones occur.  DAFs are treated as a diversion strategy, "
                + "applied only to the upstream mainline segment and all on-ramp segments upstream of the work zone. ");
        infoLabelWZ.setFont(MainWindow.getTableFont());

        atdmPlanModel = new ATDMPlanEditorModel(1, seed, atdmModel, planTable);
        planTable.setModel(atdmPlanModel);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        planTable.getColumnModel().getColumn(0).setMaxWidth(25);
        planTable.setDefaultRenderer(Integer.class, centerRenderer);
        planTable.setDefaultRenderer(Object.class, centerRenderer);

        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                updateStrategies();
            }
        };
        planTable.getSelectionModel().addListSelectionListener(selectionListener);

        if (atdmPlanModel.getRowCount() > 0) {
            atdmStrategyTabbedPane.setVisible(true);
        }

        //strategySelectorModels = new StrategySelectorModel[4];
        strategySelectorModels = new StrategySelectorModel[6];
        strategySelectorModels[0] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, seed, null));       // Demand strategy management model
        strategySelectorModels[1] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, seed, null));       // Weather strategy management model
        strategySelectorModels[2] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, seed, null));       // Incident strategy management model
        strategySelectorModels[3] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, seed, null));       // WorkZone strategy management model
        strategySelectorModels[4] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_RAMP_METERING, seed, null));       // Ramp Metering management model
        strategySelectorModels[5] = new StrategySelectorModel(new StrategyManagementModel(CEConst.IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING, seed, null));       // HSR management model

        demandManagementTable.setModel(strategySelectorModels[0]);
        weatherManagementTable.setModel(strategySelectorModels[1]);
        incidentManagementTable.setModel(strategySelectorModels[2]);
        workZoneManagementTable.setModel(strategySelectorModels[3]);

        rmManagementTable.setModel(strategySelectorModels[4]);
        hsrManagementTable.setModel(strategySelectorModels[5]);

        centerTables();
        setupTableFonts();
        fixColumnWidths(demandManagementTable);
        fixColumnWidths(weatherManagementTable);
        fixColumnWidths(incidentManagementTable);
        fixColumnWidths(workZoneManagementTable);
        fixColumnWidths(rmManagementTable);
        fixColumnWidths(hsrManagementTable);

        demandManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (demandManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        weatherManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (weatherManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        incidentManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (incidentManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        workZoneManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (workZoneManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        rmManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (rmManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        hsrManagementTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (hsrManagementTable.columnAtPoint(evt.getPoint()) != 0) {
                        JOptionPane.showMessageDialog(evt.getComponent(),
                                "ATDM Strategy values must be edited in the ATDM Strategies dialog",
                                "ATDM", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        if (atdmPlanModel == null || atdmPlanModel.getRowCount() == 0) {
            atdmStrategyTabbedPane.setVisible(false);
        } else {
            planTable.setRowSelectionInterval(0, 0);
        }
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
        planTable.setFont(MainWindow.getTableFont());
        demandManagementTable.setFont(MainWindow.getTableFont());
        weatherManagementTable.setFont(MainWindow.getTableFont());
        incidentManagementTable.setFont(MainWindow.getTableFont());
        workZoneManagementTable.setFont(MainWindow.getTableFont());
        planTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        demandManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        weatherManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        incidentManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        workZoneManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        rmManagementTable.setFont(MainWindow.getTableFont());
        hsrManagementTable.setFont(MainWindow.getTableFont());
        rmManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        hsrManagementTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        TableSelectionCellEditor defaultCellEditor = new TableSelectionCellEditor(true);
        planTable.setDefaultEditor(Object.class, defaultCellEditor);
        demandManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        weatherManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        incidentManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        workZoneManagementTable.setDefaultEditor(Object.class, defaultCellEditor);

        rmManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
        hsrManagementTable.setDefaultEditor(Object.class, defaultCellEditor);
    }

    private void fixColumnWidths(JTable table) {
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setMinWidth(25);
        table.getColumnModel().getColumn(0).setMaxWidth(25);
        table.getColumnModel().getColumn(1).setMinWidth(25);
        table.getColumnModel().getColumn(1).setMaxWidth(25);
        //table.getColumnModel().getColumn(3).setMinWidth(135);
        //table.getColumnModel().getColumn(3).setMaxWidth(200);
        for (int colIdx = 3; colIdx < table.getColumnModel().getColumnCount(); colIdx++) {
            table.getColumnModel().getColumn(colIdx).setMinWidth(75);
            table.getColumnModel().getColumn(colIdx).setMaxWidth(75);
        }
    }

    private void updateStrategies() {
        if (planTable.getSelectedRow() > -1) {
            ATDMPlan currPlan = atdmPlanModel.getPlan(planTable.getSelectedRow());
            strategySelectorModels[0].setPlan(currPlan);
            strategySelectorModels[1].setPlan(currPlan);
            strategySelectorModels[2].setPlan(currPlan);
            strategySelectorModels[3].setPlan(currPlan);
            strategySelectorModels[4].setPlan(currPlan);
            strategySelectorModels[5].setPlan(currPlan);
            useRMCB.setSelected(currPlan.hasRampMetering());
            useHSRCB.setSelected(currPlan.hasShoulderOpening());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editRMButton = new javax.swing.JButton();
        useRMCB = new javax.swing.JCheckBox();
        useHSRCB = new javax.swing.JCheckBox();
        closeButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        planTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        addPlanButton = new javax.swing.JButton();
        removePlanButton = new javax.swing.JButton();
        atdmStrategyTabbedPane = new javax.swing.JTabbedPane();
        demandManagementPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        demandManagementTable = new javax.swing.JTable();
        infoLabelDemand = new javax.swing.JLabel();
        weatherManagementPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        weatherManagementTable = new javax.swing.JTable();
        infoLabelWeather = new javax.swing.JLabel();
        incidentManagementPanel = new javax.swing.JPanel();
        incidentManagementScroll = new javax.swing.JScrollPane();
        incidentManagementTable = new javax.swing.JTable();
        infoLabelIncident = new javax.swing.JLabel();
        workZonePanel = new javax.swing.JPanel();
        workZoneManagementScroll = new javax.swing.JScrollPane();
        workZoneManagementTable = new javax.swing.JTable();
        infoLabelWZ = new javax.swing.JLabel();
        rmPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        rmManagementTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        hsrManagementTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        editRMButton.setText("Edit Ramp Metering Rate");
        editRMButton.setEnabled(false);
        editRMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRMButtonActionPerformed(evt);
            }
        });

        useRMCB.setText("Use Ramp Metering");
        useRMCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useRMCBItemStateChanged(evt);
            }
        });

        useHSRCB.setText("Hard Shoulder Running");
        useHSRCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useHSRCBItemStateChanged(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATDM Plan Designer");

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jSplitPane1.setBorder(null);

        planTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        planTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        planTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(planTable);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        addPlanButton.setText("Add Plan");
        addPlanButton.setPreferredSize(new java.awt.Dimension(93, 29));
        addPlanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPlanButtonActionPerformed(evt);
            }
        });
        jPanel3.add(addPlanButton);

        removePlanButton.setText("Remove Plan");
        removePlanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePlanButtonActionPerformed(evt);
            }
        });
        jPanel3.add(removePlanButton);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

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
        demandManagementTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(demandManagementTable);

        infoLabelDemand.setText("STRATEGY INFO HERE (TEXT REPLACED)");

        javax.swing.GroupLayout demandManagementPanelLayout = new javax.swing.GroupLayout(demandManagementPanel);
        demandManagementPanel.setLayout(demandManagementPanelLayout);
        demandManagementPanelLayout.setHorizontalGroup(
            demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, demandManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLabelDemand, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        demandManagementPanelLayout.setVerticalGroup(
            demandManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(demandManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabelDemand, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );

        atdmStrategyTabbedPane.addTab("Demand Management", demandManagementPanel);

        weatherManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        weatherManagementTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(weatherManagementTable);

        infoLabelWeather.setText("STRATEGY INFO HERE (TEXT REPLACED)");

        javax.swing.GroupLayout weatherManagementPanelLayout = new javax.swing.GroupLayout(weatherManagementPanel);
        weatherManagementPanel.setLayout(weatherManagementPanelLayout);
        weatherManagementPanelLayout.setHorizontalGroup(
            weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, weatherManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabelWeather, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        weatherManagementPanelLayout.setVerticalGroup(
            weatherManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabelWeather, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        infoLabelIncident.setText("STRATEGY INFO HERE (TEXT REPLACED)");

        javax.swing.GroupLayout incidentManagementPanelLayout = new javax.swing.GroupLayout(incidentManagementPanel);
        incidentManagementPanel.setLayout(incidentManagementPanelLayout);
        incidentManagementPanelLayout.setHorizontalGroup(
            incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, incidentManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabelIncident, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(incidentManagementScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        incidentManagementPanelLayout.setVerticalGroup(
            incidentManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incidentManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(incidentManagementScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabelIncident, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        infoLabelWZ.setText("STRATEGY INFO HERE (TEXT REPLACED)");

        javax.swing.GroupLayout workZonePanelLayout = new javax.swing.GroupLayout(workZonePanel);
        workZonePanel.setLayout(workZonePanelLayout);
        workZonePanelLayout.setHorizontalGroup(
            workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workZonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabelWZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(workZoneManagementScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        workZonePanelLayout.setVerticalGroup(
            workZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workZonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workZoneManagementScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabelWZ, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );

        atdmStrategyTabbedPane.addTab("Work Zone Traffic Maintenance", workZonePanel);

        rmManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(rmManagementTable);

        hsrManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(hsrManagementTable);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Ramp Metering");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Hard Shoulder Running");

        javax.swing.GroupLayout rmPanelLayout = new javax.swing.GroupLayout(rmPanel);
        rmPanel.setLayout(rmPanelLayout);
        rmPanelLayout.setHorizontalGroup(
            rmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rmPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        rmPanelLayout.setVerticalGroup(
            rmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rmPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );

        rmPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2});

        atdmStrategyTabbedPane.addTab("Other", rmPanel);

        jSplitPane1.setRightComponent(atdmStrategyTabbedPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSplitPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void addPlanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPlanButtonActionPerformed
        atdmPlanModel.addPlan();
        if (atdmStrategyTabbedPane.isVisible() == false) {
            atdmStrategyTabbedPane.setVisible(true);
        }

        atdmPlanModel.fireTableDataChanged();
        planTable.setRowSelectionInterval(atdmPlanModel.getRowCount() - 1, atdmPlanModel.getRowCount() - 1);
    }//GEN-LAST:event_addPlanButtonActionPerformed

    private void useRMCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useRMCBItemStateChanged
        if (planTable.getSelectedRowCount() > 0) {
            if (useRMCB.isSelected()) {
                //editRMButton.setEnabled(true);
                seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).useRampMetering(true);
            } else {
                //editRMButton.setEnabled(false);
                seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).useRampMetering(false);
                //deleteRM();
            }
        } else {
            // Cannot select if now plan is selected
            useRMCB.setSelected(false);
        }
        strategySelectorModels[4].fireTableDataChanged();
    }//GEN-LAST:event_useRMCBItemStateChanged

    private void editRMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRMButtonActionPerformed
        CA2DInt currRMRate = seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).getRMRate();
        CA2DInt tempRMRate = new CA2DInt(currRMRate.getSizeX(), currRMRate.getSizeY(), 0);
        tempRMRate.deepCopyFrom(currRMRate);
        editRM(tempRMRate);
    }//GEN-LAST:event_editRMButtonActionPerformed

    private void removePlanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePlanButtonActionPerformed
        if (planTable.getSelectedRowCount() >= 1) {
            ATDMDatabase atdmDatabase = seed.getATDMDatabase();
            ArrayList<ArrayList<Integer>> conflicts = new ArrayList<>();
            int[] selectedPlans = planTable.getSelectedRows();
            for (int planIdx = 0; planIdx < selectedPlans.length; planIdx++) {
                conflicts.add(atdmModel.checkPlan(atdmDatabase.getPlan(selectedPlans[planIdx])));
            }
            String dialogString = "<HTML>Warning: If the selected plan(s) is(are) deleted, "
                    + "they will be removed from being assigned to "
                    + "any scenario of the <b>current</b> ATDM Set.<br>&nbsp";

            for (int row = 0; row < selectedPlans.length; row++) {
                String tempString;
                if (conflicts.get(row).isEmpty()) {
                    tempString = "None";
                } else {
                    tempString = "";
                    for (int scenIdx : conflicts.get(row)) {
                        tempString = tempString + scenIdx + ",";
                    }
                    tempString = tempString.substring(0, tempString.length() - 1);
                }
                dialogString = dialogString + "<br>" + atdmDatabase.getPlan(selectedPlans[row]).getName()
                        + " is currently assigned to the following scenarios: " + tempString;
            }

            int option = JOptionPane.showConfirmDialog(this,
                    dialogString,
                    "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                Arrays.sort(selectedPlans);
                for (int i = selectedPlans.length - 1; i >= 0; i--) {
                    atdmModel.removePlan(atdmDatabase.getPlan(selectedPlans[i]));
                    atdmDatabase.removePlan(selectedPlans[i]);
                }
                atdmPlanModel.fireTableDataChanged();
                atdmModel.fireTableDataChanged();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No plans selected for deletion.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_removePlanButtonActionPerformed

    private void useHSRCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useHSRCBItemStateChanged
        if (planTable.getSelectedRowCount() > 0) {
            ATDMPlan currPlan = seed.getATDMDatabase().getPlan(planTable.getSelectedRow());
            if (useHSRCB.isSelected()) {
                currPlan.useShoulderOpening(true);
                //editHSRButton.setEnabled(true);
            } else {
                currPlan.useShoulderOpening(false);
                //editHSRButton.setEnabled(false);
            }
        } else {
            // Cannot select if no plan is selected
            useHSRCB.setSelected(false);
        }
        strategySelectorModels[5].fireTableDataChanged();
    }//GEN-LAST:event_useHSRCBItemStateChanged

    private void editRM(CA2DInt oldRampMeteringRate) {
        ATDMRMDialog atdmRMDialog = new ATDMRMDialog(this, true, seed, oldRampMeteringRate);

        atdmRMDialog.setLocationRelativeTo(this.getRootPane());
        atdmRMDialog.setVisible(true);

        if (atdmRMDialog.getReturnStatus() == ATDMRMDialog.RET_OK) {
            CA2DInt rampMeteringRate = atdmRMDialog.getRampMeteringRate();
            //seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).setRMRate(rampMeteringRate);
        } else if (atdmRMDialog.getReturnStatus() == ATDMRMDialog.RET_CANCEL && seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).getRMRate() == null) {
            // If no plan exists and cancel option is selected, set check box as false
            useRMCB.setSelected(false);
        } else {
            // Leave options as what they were on cancel
        }

        atdmRMDialog.dispose();
    }

//    /**
//     *
//     * @param oldHSRMatrix
//     */
//    private void editHSR(CA2DInt oldHSRMatrix) {
//        ATDMHSRDialog atdmHSRDialog = new ATDMHSRDialog(this, true, seed, oldHSRMatrix);
//        if (oldHSRMatrix != null) {
//            atdmHSRDialog.setHSRCAF(seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).getHSRCAF());
//        }
//
//        atdmHSRDialog.setLocationRelativeTo(this.getRootPane());
//        atdmHSRDialog.setVisible(true);
//
//        if (atdmHSRDialog.getReturnStatus() == ATDMHSRDialog.RET_OK) {
//            CA2DInt hsrMatrix = atdmHSRDialog.getHSRMatrix();
//            //seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).setHSRMatrix(hsrMatrix);
//            //seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).setHSRCAF(atdmHSRDialog.getHSRCAF());
//        } else if (atdmHSRDialog.getReturnStatus() == ATDMHSRDialog.RET_CANCEL && seed.getATDMDatabase().getPlan(planTable.getSelectedRow()).getHSRMatrix() == null) {
//            // If no plan exists and cancel option is selected, set check box as false
//            useHSRCB.setSelected(false);
//        } else {
//            // Leave options as what they were on cancel
//        }
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPlanButton;
    private javax.swing.JTabbedPane atdmStrategyTabbedPane;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel demandManagementPanel;
    private javax.swing.JTable demandManagementTable;
    private javax.swing.JButton editRMButton;
    private javax.swing.JTable hsrManagementTable;
    private javax.swing.JPanel incidentManagementPanel;
    private javax.swing.JScrollPane incidentManagementScroll;
    private javax.swing.JTable incidentManagementTable;
    private javax.swing.JLabel infoLabelDemand;
    private javax.swing.JLabel infoLabelIncident;
    private javax.swing.JLabel infoLabelWZ;
    private javax.swing.JLabel infoLabelWeather;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable planTable;
    private javax.swing.JButton removePlanButton;
    private javax.swing.JTable rmManagementTable;
    private javax.swing.JPanel rmPanel;
    private javax.swing.JCheckBox useHSRCB;
    private javax.swing.JCheckBox useRMCB;
    private javax.swing.JPanel weatherManagementPanel;
    private javax.swing.JTable weatherManagementTable;
    private javax.swing.JScrollPane workZoneManagementScroll;
    private javax.swing.JTable workZoneManagementTable;
    private javax.swing.JPanel workZonePanel;
    // End of variables declaration//GEN-END:variables
}
