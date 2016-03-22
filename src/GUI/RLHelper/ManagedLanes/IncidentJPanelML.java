package GUI.RLHelper.ManagedLanes;

import GUI.RLHelper.IncidentProbabilityWizard;
import GUI.RLHelper.Renderer.AltColorRendererWithBlackout;
import GUI.RLHelper.Renderer.AltColorRendererWithRedOut;
import GUI.RLHelper.Renderer.AlternatingColorsRenderer;
import GUI.RLHelper.TableModels.IncidentDataModel;
import GUI.RLHelper.TableModels.IncidentDistDataModel;
import GUI.RLHelper.TableSelectionCellEditor;
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
public class IncidentJPanelML extends javax.swing.JPanel {

    private IncidentData incidentData;

    private IncidentDataModel dataModel;

    private IncidentDistDataModel dataDistModel;

    private MLIncidentAdjFactorModel incSAFModel;

    private MLIncidentAdjFactorModel incCAFModel;

    private MLIncidentAdjFactorModel incDAFModel;

    private MLIncidentAdjFactorModel incLAFModel;

    private AlternatingColorsRenderer alternatingColorsRenderer;

    private AltColorRendererWithRedOut altColorsRendererWithWarning;

    private AltColorRendererWithBlackout blackoutRenderer;

    private DemandData demandData; //  Necessary for incident probability wizard

    /**
     * Creates new form IncidentJPanel
     */
    public IncidentJPanelML() {
        initComponents();
    }

    /**
     * General activator method for the incident panel. If no incidentData
     * object exists, a new empty one is created.
     */
    public void activateIncidentJPanel() {

        // Set up the data and model
        if (incidentData == null) {
            incidentData = new IncidentData(IncidentData.TYPE_ML);
        }
        dataModel = new IncidentDataModel(incidentData, demandData);
        dataDistModel = new IncidentDistDataModel(incidentData);
        incSAFModel = new MLIncidentAdjFactorModel(incidentData, MLIncidentAdjFactorModel.ID_TABLE_TYPE_SAF);
        incCAFModel = new MLIncidentAdjFactorModel(incidentData, MLIncidentAdjFactorModel.ID_TABLE_TYPE_CAF);
        incDAFModel = new MLIncidentAdjFactorModel(incidentData, MLIncidentAdjFactorModel.ID_TABLE_TYPE_DAF);
        incLAFModel = new MLIncidentAdjFactorModel(incidentData, MLIncidentAdjFactorModel.ID_TABLE_TYPE_LAF);

        // Renderer for frequencies
        altColorsRendererWithWarning = new AltColorRendererWithRedOut(true);
        altColorsRendererWithWarning.setRowHeaderAlignment(JTextField.CENTER);

        // Renderer for distribution
        alternatingColorsRenderer = new AlternatingColorsRenderer(true, "%.1f");
        alternatingColorsRenderer.setRowHeaderAlignment(JTextField.CENTER);

        // Renderer for Adjustment factors
        blackoutRenderer = new AltColorRendererWithBlackout(true);
        blackoutRenderer.setRowHeaderAlignment(JTextField.CENTER);
        //blackoutRenderer.setHorizontalAlignment(JTextField.CENTER);

        incidentTable.setModel(dataModel);
        incidentTable.setDefaultRenderer(Object.class, altColorsRendererWithWarning);
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
        incSAFs.setModel(incSAFModel);
        incSAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incSAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        incSAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incSAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incSAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident CAF table
        incCAFs.setModel(incCAFModel);
        incCAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incCAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        incCAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incCAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incCAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident DAF table
        incDAFs.setModel(incDAFModel);
        incDAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incDAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        incDAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incDAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incDAFs.getTableHeader().setFont(newHeaderFont);

        // Setting up incident LAF table
        incLAFs.setModel(incLAFModel);
        incLAFs.setDefaultRenderer(Object.class, blackoutRenderer);
        incLAFs.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
        incLAFs.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Make the table look a little nicer
        tableFont = incLAFs.getTableHeader().getFont();
        newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
        incLAFs.getTableHeader().setFont(newHeaderFont);

        warningLabel.setText("<HTML><CENTER>A red background indicates<br>that the"
                + " frequency values<br>have not be set or<br>are very small "
                + "(&#60;0.01)");

    }

    /**
     * Activates the incident panel and creates incident data from the seed.
     *
     * @param seed
     */
    public void activateIncidentJPanel(Seed seed) {

        // Set up the data and model
        incidentData = new IncidentData(seed, IncidentData.TYPE_ML);
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

    /**
     * Get method to return the incidentData object of the panel.
     *
     * @return
     */
    public IncidentData getIncidentData() {
        return incidentData;
    }

    /**
     *
     * @return
     */
    public boolean useGPValues() {
        return useGPValuesCB.isSelected();
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
     *
     * @param value
     */
    public void setAllComponentsEnabled(boolean value) {
        durationPanel.setEnabled(value);
        distributionTable.setEnabled(value);
        useNationDefaultData.setEnabled(value);
        useSeedFileValues.setEnabled(value);
        useStandardDistribution.setEnabled(value);
        useDefaultDurationsButton.setEnabled(value);
        calculateProbabilitiesButton.setEnabled(value);
        frequencyPanel.setEnabled(value);
        incidentTable.setEnabled(value);
        afScrollPanel.setEnabled(value);
        incCAFs.setEnabled(value);
        incDAFs.setEnabled(value);
        incSAFs.setEnabled(value);
        incLAFs.setEnabled(value);

        if (value) {
            alternatingColorsRenderer.setEnabled();
            altColorsRendererWithWarning.setEnabled();
            blackoutRenderer.setEnabled();
        } else {
            alternatingColorsRenderer.setDisabled();
            altColorsRendererWithWarning.setDisabled();
            blackoutRenderer.setDisabled();
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

        frequencyPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        incidentTable = new javax.swing.JTable();
        calculateProbabilitiesButton = new javax.swing.JButton();
        useSeedFileValues = new javax.swing.JButton();
        warningLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        afScrollPanel = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        incCAFs = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        incSAFs = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        incDAFs = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        incLAFs = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        durationPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        distributionTable = new javax.swing.JTable();
        useNationDefaultData = new javax.swing.JButton();
        useStandardDistribution = new javax.swing.JButton();
        useDefaultDurationsButton = new javax.swing.JButton();
        useGPValuesCB = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(992, 608));

        frequencyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Incident Frequencies"));
        frequencyPanel.setPreferredSize(new java.awt.Dimension(421, 200));

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

        javax.swing.GroupLayout frequencyPanelLayout = new javax.swing.GroupLayout(frequencyPanel);
        frequencyPanel.setLayout(frequencyPanelLayout);
        frequencyPanelLayout.setHorizontalGroup(
            frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, frequencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(frequencyPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(frequencyPanelLayout.createSequentialGroup()
                        .addGroup(frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(useSeedFileValues, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(calculateProbabilitiesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        frequencyPanelLayout.setVerticalGroup(
            frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frequencyPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(calculateProbabilitiesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useSeedFileValues)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Adjustment Factors"));

        jPanel4.setLayout(new java.awt.GridLayout(1, 4, 5, 0));

        jScrollPane8.setPreferredSize(new java.awt.Dimension(136, 170));

        incCAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        incCAFs.getTableHeader().setResizingAllowed(false);
        incCAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(incCAFs);

        jPanel4.add(jScrollPane8);

        jScrollPane9.setPreferredSize(new java.awt.Dimension(136, 170));

        incSAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        incSAFs.getTableHeader().setResizingAllowed(false);
        incSAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(incSAFs);

        jPanel4.add(jScrollPane9);

        jScrollPane10.setPreferredSize(new java.awt.Dimension(136, 170));

        incDAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        incDAFs.getTableHeader().setResizingAllowed(false);
        incDAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(incDAFs);

        jPanel4.add(jScrollPane10);

        jScrollPane11.setPreferredSize(new java.awt.Dimension(136, 170));

        incLAFs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        incLAFs.getTableHeader().setResizingAllowed(false);
        incLAFs.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(incLAFs);

        jPanel4.add(jScrollPane11);

        afScrollPanel.setViewportView(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(1, 4, 5, 0));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Capacity Adjustment Factors (CAFs)");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("FFS Adjustment Factors (SAFs)");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Demand Adjustment Factors (DAFs)");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(jLabel3);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Lane Adjustment Factors");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(jLabel4);

        jScrollPane12.setViewportView(jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
            .addComponent(afScrollPanel, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(afScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        durationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Incident Durations"));

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

        useStandardDistribution.setText("Use Standard Distribution");
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

        javax.swing.GroupLayout durationPanelLayout = new javax.swing.GroupLayout(durationPanel);
        durationPanel.setLayout(durationPanelLayout);
        durationPanelLayout.setHorizontalGroup(
            durationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(durationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(durationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addGroup(durationPanelLayout.createSequentialGroup()
                        .addGroup(durationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(useNationDefaultData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .addComponent(useStandardDistribution, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(useDefaultDurationsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        durationPanelLayout.setVerticalGroup(
            durationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(durationPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(durationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useStandardDistribution)
                    .addComponent(useDefaultDurationsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useNationDefaultData))
        );

        useGPValuesCB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        useGPValuesCB.setText("Use Values Specified in GP - Incidents Tab");
        useGPValuesCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useGPValuesCBItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(useGPValuesCB)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(frequencyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(durationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useGPValuesCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frequencyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(durationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {durationPanel, frequencyPanel});

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
        incidentData.useMLDefaultDistribution();

        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_useStandardDistributionActionPerformed

    private void useSeedFileValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSeedFileValuesActionPerformed
        incidentData.useFrequenciesFromSeed();

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_useSeedFileValuesActionPerformed

    private void useGPValuesCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useGPValuesCBItemStateChanged
        if (useGPValuesCB.isSelected()) {
            setAllComponentsEnabled(false);
        } else {
            setAllComponentsEnabled(true);
        }
    }//GEN-LAST:event_useGPValuesCBItemStateChanged

    private void useDefaultDurationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useDefaultDurationsButtonActionPerformed
        incidentData.useDefaultDuration();

        dataDistModel.fireTableDataChanged();
    }//GEN-LAST:event_useDefaultDurationsButtonActionPerformed

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
    private javax.swing.JScrollPane afScrollPanel;
    private javax.swing.JButton calculateProbabilitiesButton;
    private javax.swing.JTable distributionTable;
    private javax.swing.JPanel durationPanel;
    private javax.swing.JPanel frequencyPanel;
    private javax.swing.JTable incCAFs;
    private javax.swing.JTable incDAFs;
    private javax.swing.JTable incLAFs;
    private javax.swing.JTable incSAFs;
    private javax.swing.JTable incidentTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton useDefaultDurationsButton;
    private javax.swing.JCheckBox useGPValuesCB;
    private javax.swing.JButton useNationDefaultData;
    private javax.swing.JButton useSeedFileValues;
    private javax.swing.JButton useStandardDistribution;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
}
