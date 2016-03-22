package GUI.RLHelper;

import GUI.RLHelper.TableModels.IncidentDistDataModel;
import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CEDate;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Tristan and Lake
 */
public class IncidentProbabilityWizard extends javax.swing.JDialog {

    /**
     * Creates new form IncidentProbabilityWizard
     */
    // Private incidentData data
    private DemandData demandData;

    private IncidentData incidentData;

    private DefaultTableCellRenderer centerRenderer;

    //private TableSelectionCellEditor defaultEditor;
    /**
     *
     * @param parent
     * @param modal
     */
    public IncidentProbabilityWizard(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //distributionTableIncident.setModel(new IncidentDistDataModel(incidentData));
        initializeTables();
    }

    /**
     *
     * @param parent
     * @param modal
     * @param incidentData
     * @param demandData
     */
    public IncidentProbabilityWizard(java.awt.Frame parent, boolean modal, IncidentData incidentData, DemandData demandData) {
        super(parent, modal);
        initComponents();

        IncidentDistDataModel incidentDistDataModel = new IncidentDistDataModel(incidentData);

        //distributionTableIncident.setModel(incidentDistDataModel);
        //distributionTableCrash.setModel(incidentDistDataModel);
        //distributionTableHERS.setModel(incidentDistDataModel);
        this.demandData = demandData;
        this.incidentData = incidentData;

        crashRateRatio.setText(String.valueOf(incidentData.getCrashRateRatio()));

        initializeTables();

    }

    private void initializeTables() {
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Setting Editors and Renderers
        //distributionTableIncident.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        //distributionTableIncident.setDefaultRenderer(Object.class, centerRenderer);
        //distributionTableIncident.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        //distributionTableCrash.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        //distributionTableHERS.setDefaultRenderer(Object.class, centerRenderer);
        //distributionTableCrash.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        //distributionTableHERS.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        //distributionTableCrash.setDefaultRenderer(Object.class, centerRenderer);
        //distributionTableHERS.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        incidentRatesTable.setModel(new RatesTableModel(1526.0f));
        incidentRatesTable.setDefaultEditor(Float.class, new TableSelectionCellEditor(true));
        incidentRatesTable.setDefaultRenderer(Object.class, centerRenderer);
        incidentRatesTable.setDefaultRenderer(Float.class, centerRenderer);
        incidentRatesTable.setDefaultRenderer(Integer.class, centerRenderer);
        incidentRatesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        crashRatesTable.setModel(new RatesTableModel(165.4f));
        crashRatesTable.setDefaultEditor(Float.class, new TableSelectionCellEditor(true));
        crashRatesTable.setDefaultRenderer(Object.class, centerRenderer);
        crashRatesTable.setDefaultRenderer(Float.class, centerRenderer);
        crashRatesTable.setDefaultRenderer(Integer.class, centerRenderer);
        crashRatesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        crashRatesHERSTable.setModel(new RatesTableModel(152.6f));
        crashRatesHERSTable.setDefaultEditor(Float.class, new TableSelectionCellEditor(true));
        crashRatesHERSTable.setDefaultRenderer(Object.class, centerRenderer);
        crashRatesHERSTable.setDefaultRenderer(Float.class, centerRenderer);
        crashRatesHERSTable.setDefaultRenderer(Integer.class, centerRenderer);
        crashRatesHERSTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Setting Distribution Column sizes
        //distributionTableIncident.getColumnModel().getColumn(0).setMinWidth(150);
        //distributionTableCrash.getColumnModel().getColumn(0).setMinWidth(150);
        //distributionTableHERS.getColumnModel().getColumn(0).setMinWidth(150);
        // Setting up fonts
        //distributionTableIncident.setFont(MainWindow.DEFAULT_TABLE_FONT);
        //distributionTableIncident.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        //distributionTableCrash.setFont(MainWindow.DEFAULT_TABLE_FONT);
        //distributionTableCrash.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        //distributionTableHERS.setFont(MainWindow.DEFAULT_TABLE_FONT);
        //distributionTableHERS.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        crashRatesTable.setFont(MainWindow.DEFAULT_TABLE_FONT);
        crashRatesTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        incidentRatesTable.setFont(MainWindow.DEFAULT_TABLE_FONT);
        incidentRatesTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        crashRatesHERSTable.setFont(MainWindow.DEFAULT_TABLE_FONT);
        crashRatesHERSTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        incidentRatesTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        crashRatesTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        crashRateRatio = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        crashRatesHERSTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        pctAADTTextField = new javax.swing.JTextField();
        calculateHERSButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        calculateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Incident Frequency Calculator");

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Incident Rates"));
        jPanel4.setMaximumSize(new java.awt.Dimension(250, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(250, 100));

        incidentRatesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"January",  new Float(1526.0)},
                {"February",  new Float(1526.0)},
                {"March",  new Float(1526.0)},
                {"April",  new Float(1526.0)},
                {"May",  new Float(1526.0)},
                {"June",  new Float(1526.0)},
                {"July",  new Float(1526.0)},
                {"August",  new Float(1526.0)},
                {"September",  new Float(1526.0)},
                {"October",  new Float(1526.0)},
                {"November",  new Float(1526.0)},
                {"December",  new Float(1526.0)}
            },
            new String [] {
                "Month", "Incident Rate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        incidentRatesTable.setFillsViewportHeight(true);
        incidentRatesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(incidentRatesTable);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("(Per 100 million VMT)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap(103, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Incident Rates", jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Crash Rates"));
        jPanel6.setMaximumSize(new java.awt.Dimension(250, 32767));
        jPanel6.setMinimumSize(new java.awt.Dimension(250, 0));
        jPanel6.setPreferredSize(new java.awt.Dimension(270, 430));

        crashRatesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"January",  new Float(165.4)},
                {"February",  new Float(165.4)},
                {"March",  new Float(165.4)},
                {"April",  new Float(165.4)},
                {"May",  new Float(165.4)},
                {"June",  new Float(165.4)},
                {"July",  new Float(165.4)},
                {"August",  new Float(165.4)},
                {"September",  new Float(165.4)},
                {"October",  new Float(165.4)},
                {"November",  new Float(165.4)},
                {"December",  new Float(165.4)}
            },
            new String [] {
                "Month", "Crash Rate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        crashRatesTable.setFillsViewportHeight(true);
        crashRatesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(crashRatesTable);

        jLabel2.setText("Incident/Crash Ratio");

        crashRateRatio.setText("4.9");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("National Average is ~4.9");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("(Per 100 million VMT)");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(crashRateRatio, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(crashRateRatio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Crash Rates", jPanel2);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Crash Rates"));
        jPanel8.setMaximumSize(new java.awt.Dimension(250, 32767));
        jPanel8.setMinimumSize(new java.awt.Dimension(250, 0));

        crashRatesHERSTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"January",  new Float(152.6)},
                {"February",  new Float(152.6)},
                {"March",  new Float(152.6)},
                {"April",  new Float(152.6)},
                {"May",  new Float(152.6)},
                {"June",  new Float(152.6)},
                {"July",  new Float(152.6)},
                {"August",  new Float(152.6)},
                {"September",  new Float(152.6)},
                {"October",  new Float(152.6)},
                {"November",  new Float(152.6)},
                {"December",  new Float(152.6)}
            },
            new String [] {
                "Month", "Crash Rate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        crashRatesHERSTable.setFillsViewportHeight(true);
        crashRatesHERSTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(crashRatesHERSTable);

        jLabel3.setText("Crash/Incident Rate Ratio:");

        jTextField2.setText("4.9");

        jLabel4.setText("Percent of AADT (0-100%):");

        pctAADTTextField.setText("25.00");

        calculateHERSButton.setText("Calculate Crash Rates Using HERS");
        calculateHERSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateHERSButtonActionPerformed(evt);
            }
        });

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("(Per 100 million VMT)");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(calculateHERSButton, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2)
                            .addComponent(pctAADTTextField))))
                .addContainerGap())
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pctAADTTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calculateHERSButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("HERS Model", jPanel3);

        tabbedPane.setSelectedIndex(1);

        calculateButton.setText("Calculate Frequencies Using Crash Rates");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calculateButton))
                    .addComponent(tabbedPane))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(calculateButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged

        int selectedTab = tabbedPane.getSelectedIndex();
        if (selectedTab == 0) {
            calculateButton.setText("Calculate Frequencies Using Incident Rates");
        } else if (selectedTab == 1) {
            calculateButton.setText("Calculate Frequencies Using Crash Rates");
        } else if (selectedTab == 2) {
            calculateButton.setText("Calculate Frequencies Using the HERS Model");
        }

        }//GEN-LAST:event_tabbedPaneStateChanged

        private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed

        MainWindow.printLog("Calculating Incident Probabilities...");

        float[] crashRate = new float[12];

        int selectedTab = tabbedPane.getSelectedIndex(); // 0 - Incident rates, 1 - crash rates, 2 - HERS model

        if (selectedTab == 0) {
            // Calculating probabilites from incident rates
            for (int i = 0; i < 12; i++) {
                crashRate[i] = (float) incidentRatesTable.getValueAt(i, 1);
            }

        } else if (selectedTab == 1) {
            // Calculating probablities from crash rates
            for (int i = 0; i < 12; i++) {
                crashRate[i] = ((float) crashRatesTable.getValueAt(i, 1)) * Float.parseFloat(crashRateRatio.getText());
                incidentData.setCrashRateRatio(Float.parseFloat(crashRateRatio.getText()));
            }

        } else if (selectedTab == 2) {
            // Calculating probabilites from HERS model
            for (int i = 0; i < 12; i++) {
                crashRate[i] = ((float) crashRatesHERSTable.getValueAt(i, 1)) * Float.parseFloat(crashRateRatio.getText());
                incidentData.setCrashRateRatio(Float.parseFloat(crashRateRatio.getText()));
                incidentData.setPercentOfAADT(Float.parseFloat(pctAADTTextField.getText()));
            }
        }

        // Creating weighted average demand multipliers for all days in month j relative to seed demand multiplier
        float[] wAvgDemandMult = createWAvgDemandMultipliers(demandData);

        float nj = 0.0f;
        for (int month = 0; month < 12; month++) {
            nj = crashRate[month] * 1e-8f * wAvgDemandMult[month] * demandData.getSeedTotalVMT();
            //System.out.println(nj);
            //System.out.println(crashRate[month]+" "+wAvgDemandMult[month]+" "+demandData.getSeedTotalVMT());
            //System.out.println(nj);
            incidentData.setIncidentFrequencyMonth(month, nj);
        }

        this.setVisible(false);

        }//GEN-LAST:event_calculateButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void calculateHERSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateHERSButtonActionPerformed
        // Checking valid AADT %
        float aadtpct = -1.0f;
        boolean validAADTPct = false;
        try {
            aadtpct = Float.valueOf(pctAADTTextField.getText());
            validAADTPct = true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, "Error: Please specify a valid number for % of AADT.", "Invalid Value Error", JOptionPane.ERROR_MESSAGE);
        }
        if (validAADTPct && aadtpct <= 0.0f) {
            JOptionPane.showMessageDialog(rootPane, "Error: Percent of AADT must have a value greater than 0.", "Invalid Value Error", JOptionPane.ERROR_MESSAGE);
        } else if (validAADTPct) {
            Seed activeSeed = demandData.getSeed();

            // Calculating AADT
            // Calculating average traffic demand and average number of lanes
            float totalWeightedDemand = 0;
            float totalFacilityLengthMiles = 0;
            float segLengthMi;
            float totalWeightedNumLanes = 0;
            float averagePeriodDemand;
            for (int seg = 0; seg < activeSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                segLengthMi = activeSeed.getValueFloat(CEConst.IDS_SEGMENT_LENGTH_MI, seg);
                totalFacilityLengthMiles += segLengthMi;
                // Getting demand for all periods
                averagePeriodDemand = 0.0f;
                for (int period = 0; period < activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    averagePeriodDemand += segLengthMi * activeSeed.getValueInt(CEConst.IDS_ADJUSTED_MAIN_DEMAND, seg, period);
                }
                totalWeightedDemand += segLengthMi * (averagePeriodDemand / activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                totalWeightedNumLanes += segLengthMi * activeSeed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg);
            }

            float averageTrafficDemand = totalWeightedDemand / totalFacilityLengthMiles;
            float averageNumLanes = totalWeightedNumLanes / totalFacilityLengthMiles;
            float directionalAADT = 0;

            try {
                directionalAADT = averageTrafficDemand / (Float.valueOf(pctAADTTextField.getText()) / 100.0f);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(rootPane, "Error: Please specify a valid number for % of AADT.", "Invalid Value Error", JOptionPane.ERROR_MESSAGE);
            }

            float acr = directionalAADT / (averageNumLanes * 2200);  // 2200 is estimated capacity

            // Retrieveing lane width(ft) from seed
            float laneWidth = 0.0f;
            if (Boolean.valueOf(activeSeed.getValueString(CEConst.IDS_FFS_KNOWN))) {
                // If free flow speed is known, use default lane width of 12 feet
                laneWidth = 12.0f;
            } else {
                // If free flow speed is NOT known, calculate average lane width
                float totalWeightedLaneWidth = 0.0f;
                for (int seg = 0; seg < activeSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    totalWeightedLaneWidth += activeSeed.getValueFloat(CEConst.IDS_SEGMENT_LENGTH_MI, seg) * activeSeed.getValueFloat(CEConst.IDS_LANE_WIDTH, seg);
                }
                laneWidth = totalWeightedLaneWidth / totalFacilityLengthMiles;
            }

            float crashRate = (154.0f - (1.203f * acr) + (0.258f * acr * acr) - (0.00000524f * acr * acr * acr * acr * acr) * ((float) Math.exp(0.082 * (12.0f - laneWidth))));
            for (int row = 0; row < crashRatesHERSTable.getModel().getRowCount(); row++) {
                crashRatesHERSTable.setValueAt(String.valueOf(crashRate), row, 1);
            }
        }
    }//GEN-LAST:event_calculateHERSButtonActionPerformed

    // <editor-fold defaultstate="collapsed" desc="utility functions">
    /**
     * Method to create the weighted average demand multipliers used in
     * calculating the expected frequency of all incidents per study period in
     * month j.
     *
     * @param demandData
     * @return float[12] containing weighted average demand multipliers of each
     * month.
     */
    private float[] createWAvgDemandMultipliers(DemandData demandData) {

        // Initializing variables
        int sumActiveDays;
        float wSumActiveDays;
        float[] wAvgDemandMult = new float[12];

        // Getting array consiting of number of active days per month in analysis period
        int[] activeDaysPerMonthInAp = CEDate.numDayOfWeekInMonthAP(demandData);  // int[84] (12x7, i.e. first 7 are january)
        for (int month = 0; month < 12; month++) {
            sumActiveDays = 0;
            wSumActiveDays = 0.0f;
            for (int day = 0; day < 7; day++) {
                if (demandData.getDayActive(day)) {
                    sumActiveDays += activeDaysPerMonthInAp[month * 7 + day];
                    wSumActiveDays += activeDaysPerMonthInAp[month * 7 + day] * demandData.getValue(month, day);
                }
            }
            //System.out.println(wSumActiveDays);
            //System.out.println(sumActiveDays);
            if (wSumActiveDays == 0.0f || sumActiveDays == 0) {
                wAvgDemandMult[month] = 0.0f;
            } else {
                wAvgDemandMult[month] = wSumActiveDays / sumActiveDays;
            }
            //System.out.println(wAvgDemandMult[month]);
        }

        return wAvgDemandMult;
    }

    // </editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateButton;
    private javax.swing.JButton calculateHERSButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField crashRateRatio;
    private javax.swing.JTable crashRatesHERSTable;
    private javax.swing.JTable crashRatesTable;
    private javax.swing.JTable incidentRatesTable;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField pctAADTTextField;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    private class RatesTableModel extends AbstractTableModel {

        private final float[] rateArray;

        public RatesTableModel(float defaultValue) {
            rateArray = new float[12];
            Arrays.fill(rateArray, defaultValue);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int col) {
            return col == 0 ? "Month" : "Rate";
        }

        @Override
        public int getRowCount() {
            return 12;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return (col == 0) ? CEDate.getMonthString(row + 1) : rateArray[row];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return (col == 0) ? String.class : Float.class;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            try {
                rateArray[row] = Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                // Do nothing, don't accept input
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col != 0;
        }
    }

}
