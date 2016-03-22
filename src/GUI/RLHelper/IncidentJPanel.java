package GUI.RLHelper;

import GUI.RLHelper.Renderer.AltColorRendererWithBlackout;
import GUI.RLHelper.Renderer.AltColorRendererWithRedOut;
import GUI.RLHelper.Renderer.AlternatingColorsRenderer;
import GUI.RLHelper.TableModels.IncidentAdjFactorModel;
import GUI.RLHelper.TableModels.IncidentDataModel;
import GUI.RLHelper.TableModels.IncidentDistDataModel;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;

/**
 *
 * @author Tristan
 */
public class IncidentJPanel extends javax.swing.JPanel {

    private IncidentData incidentData;

    private IncidentDataModel dataModel;

    private IncidentDistDataModel dataDistModel;

    private IncidentAdjFactorModel incFFSAFModel;

    private IncidentAdjFactorModel incCAFModel;

    private IncidentAdjFactorModel incDAFModel;

    private IncidentAdjFactorModel incLAFModel;

    private AlternatingColorsRenderer alternatingColorsRenderer;

    private AltColorRendererWithRedOut alternatingColorsRendererRed;

    private AltColorRendererWithBlackout blackoutRenderer;

    private DemandData demandData; //  Necessary for incident probability wizard

    /**
     * Creates new form IncidentJPanel
     */
    public IncidentJPanel() {
        initComponents();
        disableEverything();
    }

    /**
     * General activator method for the incident panel. If no incidentData
     * object exists, a new empty one is created.
     */
    public void activateIncidentJPanel() {

        enableEverything();

        // Set up the data and model
        if (incidentData == null) {
            incidentData = new IncidentData(IncidentData.TYPE_GP);
        }
        dataModel = new IncidentDataModel(incidentData, demandData);
        dataDistModel = new IncidentDistDataModel(incidentData);
        incFFSAFModel = new IncidentAdjFactorModel(incidentData, 0);
        incCAFModel = new IncidentAdjFactorModel(incidentData, 1);
        incDAFModel = new IncidentAdjFactorModel(incidentData, 2);
        incLAFModel = new IncidentAdjFactorModel(incidentData, 3);

        // Renderers for Frequencies
        alternatingColorsRendererRed = new AltColorRendererWithRedOut(true);
        alternatingColorsRendererRed.setRowHeaderAlignment(JTextField.CENTER);

        // Renderers for Distribution
        alternatingColorsRenderer = new AlternatingColorsRenderer(true, "%.1f");
        alternatingColorsRenderer.setRowHeaderAlignment(JTextField.CENTER);

        // Renderer for Adjustment factors
        blackoutRenderer = new AltColorRendererWithBlackout(true);
        blackoutRenderer.setRowHeaderAlignment(JTextField.CENTER);
        //blackoutRenderer.setHorizontalAlignment(JTextField.CENTER);

        incidentTable.setModel(dataModel);
        incidentTable.setDefaultRenderer(Object.class, alternatingColorsRendererRed);
        incidentTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        incidentTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        Font tableFont = incidentTable.getTableHeader().getFont();
        Font newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incidentTable.getTableHeader().setFont(newHeaderFont);

        // Setting up distribution table
        distributionTable.setModel(dataDistModel);
        distributionTable.setDefaultRenderer(Object.class, alternatingColorsRenderer);
        distributionTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        distributionTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        distributionTable.getColumnModel().getColumn(0).setMinWidth(125);
        distributionTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        distributionTable.getColumnModel().getColumn(1).setMinWidth(85);
        distributionTable.getColumnModel().getColumn(1).setPreferredWidth(85);

        // Make the table look a little nicer
        tableFont = distributionTable.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        distributionTable.getTableHeader().setFont(newHeaderFont);

        // Setting up incident FFSAF table
        incSAFs.setModel(incFFSAFModel);
        incSAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incSAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor());
        incSAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incSAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incSAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident CAF table
        incCAFs.setModel(incCAFModel);
        incCAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incCAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor());
        incCAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incCAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incCAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident DAF table
        incDAFs.setModel(incDAFModel);
        incDAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incDAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor());
        incDAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incDAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incDAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident LAF table
        incLAFs.setModel(incLAFModel);
        incLAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incLAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor());
        incLAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incLAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incLAFs.getTableHeader().setFont(newHeaderFont);

        warningLabel.setText("<HTML><CENTER>Frequencies represent the<br>"
                + "number of incidents per study period per month."
                + "<br>&nbsp<br>A red background indicates<br>that the"
                + " frequency values<br>have not be set or<br>are very small "
                + "(&#60;0.01)");

    }

    /**
     * Activates the incident panel and creates incident data from the seed.
     *
     * @param seed
     */
    public void activateIncidentJPanel(Seed seed) {

        enableEverything();

        // Set up the data and model
        incidentData = new IncidentData(seed, IncidentData.TYPE_GP);
        activateIncidentJPanel();
    }

    /**
     * Updates the incident panel to reflect any changes in the RRP dates.
     *
     * @param oldIncidentData
     */
    public void updateIncidentJPanel(IncidentData oldIncidentData) {
        incidentData = oldIncidentData;
        activateIncidentJPanel();
    }

    //<editor-fold defaultstate="collapsed" desc="disableEverything and enableEverything">
    /**
     * Disables all components of the panel.
     */
    private void disableEverything() {
        calculateProbabilitiesButton.setEnabled(false);
        incidentTable.setEnabled(false);
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jPanel1.setEnabled(false);
        jPanel2.setEnabled(false);
        jScrollPane1.setEnabled(false);
        jScrollPane2.setEnabled(false);
        jScrollPane3.setEnabled(false);
        jScrollPane4.setEnabled(false);
        incDAFs.setEnabled(false);
        incSAFs.setEnabled(false);
        incCAFs.setEnabled(false);
    }

    /**
     * Enables all components of the panel.
     */
    private void enableEverything() {
        calculateProbabilitiesButton.setEnabled(true);
        incidentTable.setEnabled(true);
        jLabel1.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jPanel1.setEnabled(true);
        jPanel2.setEnabled(true);
        jScrollPane1.setEnabled(true);
        jScrollPane2.setEnabled(true);
        jScrollPane3.setEnabled(true);
        jScrollPane4.setEnabled(true);
        incDAFs.setEnabled(true);
        incSAFs.setEnabled(true);
        incCAFs.setEnabled(true);
    }

    //</editor-fold>
    /**
     * Get method to return the incidentData object of the panel.
     *
     * @return
     */
    public IncidentData getIncidentData() {
        return incidentData;
    }

    /**
     * Sets the DemandData object for the panel.
     *
     * @param data
     */
    public void setDemandData(DemandData data) {
        demandData = data;
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        incidentTable = new javax.swing.JTable();
        calculateProbabilitiesButton = new javax.swing.JButton();
        useSeedFileValues = new javax.swing.JButton();
        warningLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        incDAFs = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        incSAFs = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        incCAFs = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        incLAFs = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        distributionTable = new javax.swing.JTable();
        useNationDefaultData = new javax.swing.JButton();
        useStandardDistribution = new javax.swing.JButton();
        useDefaultDurationsButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Incident Frequencies"));
        jPanel1.setPreferredSize(new java.awt.Dimension(421, 200));

        incidentTable.setFillsViewportHeight(true);
        incidentTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(incidentTable);

        calculateProbabilitiesButton.setText("Calculate Frequencies...");
        calculateProbabilitiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateProbabilitiesButtonActionPerformed(evt);
            }
        });

        useSeedFileValues.setText("Use Seed File Values");
        useSeedFileValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSeedFileValuesActionPerformed(evt);
            }
        });

        warningLabel.setText("Multi-line label set in code");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(useSeedFileValues, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calculateProbabilitiesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(calculateProbabilitiesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useSeedFileValues)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Adjustment Factors"));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        incDAFs.setModel(new javax.swing.table.DefaultTableModel(
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
        incDAFs.setFillsViewportHeight(true);
        incDAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(incDAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 431;
        gridBagConstraints.ipady = 61;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("FFS Adjustment Factors (SAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 331;
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Capacity Adjustment Factors (CAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 294;
        jPanel2.add(jLabel2, gridBagConstraints);

        incSAFs.setModel(new javax.swing.table.DefaultTableModel(
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
        incSAFs.setFillsViewportHeight(true);
        incSAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(incSAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 431;
        gridBagConstraints.ipady = 61;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Demand Adjustment Factors (DAFs)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 295;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(jLabel3, gridBagConstraints);

        incCAFs.setModel(new javax.swing.table.DefaultTableModel(
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
        incCAFs.setFillsViewportHeight(true);
        incCAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(incCAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 431;
        gridBagConstraints.ipady = 61;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel2.add(jScrollPane4, gridBagConstraints);

        incLAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2", null, null, null, null, null},
                {"3", null, null, null, null, null},
                {"4", null, null, null, null, null},
                {"5", null, null, null, null, null},
                {"6", null, null, null, null, null},
                {"7", null, null, null, null, null},
                {"8", null, null, null, null, null}
            },
            new String [] {
                "Number of Lanes", "Shoulder Closure", "One Lane Closure", "Two Lane Closure", "Three Lane Closure", "Four Lane Closure"
            }
        ));
        incLAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(incLAFs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 431;
        gridBagConstraints.ipady = 61;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel2.add(jScrollPane6, gridBagConstraints);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Lane Adjustment Factors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 291;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(jLabel4, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Incident Durations"));

        distributionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        distributionTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(distributionTable);

        useNationDefaultData.setText("Use National Default Data");
        useNationDefaultData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useNationDefaultDataActionPerformed(evt);
            }
        });

        useStandardDistribution.setText("Use Saved Seed File Distribution");
        useStandardDistribution.setToolTipText("If distribution data has been saved with the current project seed, use this button to return to the saved distribution values.");
        useStandardDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useStandardDistributionActionPerformed(evt);
            }
        });

        useDefaultDurationsButton.setText("Use Default Durations");
        useDefaultDurationsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDefaultDurationsButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Use Saved Seed File Durations");
        jButton1.setToolTipText("If duration data has been saved with the current project seed, use this button to return to the saved duration values.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(useNationDefaultData, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(useDefaultDurationsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(useStandardDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useDefaultDurationsButton)
                    .addComponent(useNationDefaultData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useStandardDistribution)
                    .addComponent(jButton1)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel3});

    }// </editor-fold>//GEN-END:initComponents

        private void calculateProbabilitiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateProbabilitiesButtonActionPerformed

        IncidentProbabilityWizard wizard = new IncidentProbabilityWizard(null, true, incidentData, demandData);

        wizard.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {

                // Get calculated probabilities from the wizard here
                //System.out.println("Done.");
                dataModel.update();

            }
        });

        wizard.setLocationRelativeTo(this.getRootPane());
        wizard.setVisible(true);

        }//GEN-LAST:event_calculateProbabilitiesButtonActionPerformed

    private void useNationDefaultDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useNationDefaultDataActionPerformed
        incidentData.useNationalDefaultDistribution();

        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_useNationDefaultDataActionPerformed

    private void useStandardDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useStandardDistributionActionPerformed
        //incidentData.useDefaultDistribution();
        incidentData.useSeedFileDistribution();
        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_useStandardDistributionActionPerformed

    private void useSeedFileValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSeedFileValuesActionPerformed
        incidentData.useFrequenciesFromSeed();

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_useSeedFileValuesActionPerformed

    private void useDefaultDurationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useDefaultDurationsButtonActionPerformed
        incidentData.useDefaultDuration();

        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_useDefaultDurationsButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        incidentData.useSeedFileDurations();

        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Setter for table font. Does not override renderer font, but sets table
     * row heights correctly.
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        distributionTable.setFont(newTableFont);
        distributionTable.setRowHeight(newTableFont.getSize() + 2);
        incCAFs.setFont(newTableFont);
        incCAFs.setRowHeight(newTableFont.getSize() + 2);
        incDAFs.setFont(newTableFont);
        incDAFs.setRowHeight(newTableFont.getSize() + 2);
        incLAFs.setFont(newTableFont);
        incLAFs.setRowHeight(newTableFont.getSize() + 2);
        incSAFs.setFont(newTableFont);
        incSAFs.setRowHeight(newTableFont.getSize() + 2);
        incidentTable.setFont(newTableFont);
        incidentTable.setRowHeight(newTableFont.getSize() + 2);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateProbabilitiesButton;
    private javax.swing.JTable distributionTable;
    private javax.swing.JTable incCAFs;
    private javax.swing.JTable incDAFs;
    private javax.swing.JTable incLAFs;
    private javax.swing.JTable incSAFs;
    private javax.swing.JTable incidentTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JButton useDefaultDurationsButton;
    private javax.swing.JButton useNationDefaultData;
    private javax.swing.JButton useSeedFileValues;
    private javax.swing.JButton useStandardDistribution;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
}
