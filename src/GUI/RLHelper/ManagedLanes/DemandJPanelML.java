package GUI.RLHelper.ManagedLanes;

import GUI.RLHelper.Renderer.TableColorRenderer;
import GUI.RLHelper.TableModels.DemandDataModel;
import GUI.RLHelper.TableSelectionCellEditor;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author lake and Tristan
 */
public class DemandJPanelML extends javax.swing.JPanel {

    private DemandData demandData;

    private DemandDataModel dataModel;

    private TableColorRenderer tableRenderer;

    /**
     * String containing names of all months.
     */
    public final static String[] monthString = new String[]{
        "January", "February", "March", "April", "May", "June", "July", "August", "September",
        "October", "November", "December"
    };

    /**
     * Creates new form DemandJPanel.
     */
    public DemandJPanelML() {
        initComponents();
    }

    /**
     * Activator method that creates a new DemandData object with the given RRP
     * dates.
     *
     * @param year
     * @param startMonth Note, 1 will be added to value so indexing will start
     * at 1
     * @param startDay Note, 1 will be added to value so indexing will start at
     * 1
     * @param endMonth Note, 1 will be added to value so indexing will start at
     * 1
     * @param endDay Note, 1 will be added to value so indexing will start at 1
     */
    public void activateDemandJPanel(int year, int startMonth, int startDay, int endMonth, int endDay) {

        // Create new DemandData
        demandData = new DemandData(year, startMonth + 1, startDay + 1, endMonth + 1, endDay + 1, DemandData.TYPE_ML); //Adding one to fix indexing
        activateDemandJPanel();

        // Setting defaults for demand values
        facilitySpecificButton.doClick();

    }

    /**
     * Activator method that creates a new DemandData object from the seed file.
     *
     * @param seed
     */
    public void activateDemandJPanel(Seed seed) {

        // Set up DemandData from seed
        demandData = new DemandData(seed, DemandData.TYPE_ML);

        activateDemandJPanel();
    }

    /**
     * General activator method that should be called once demandData has been
     * set. Sets up the rest of the panel using values from demandData.
     */
    public void activateDemandJPanel() {
        if (demandData != null) {

            dataModel = new DemandDataModel(demandData);
            tableRenderer = new TableColorRenderer();

            demandTable.setModel(dataModel);
            demandTable.setDefaultRenderer(Object.class, tableRenderer);
            demandTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));

            // This will update the cell background color when the user changes a value
            // in the table.
            dataModel.addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {
                    float minVal = demandData.getMinValue();
                    float maxVal = demandData.getMaxValue();
                    tableRenderer.setColorRange(minVal, maxVal);
                    demandTable.repaint();
                }
            });

            // Make the table look a little nicer
            Font tableFont = demandTable.getTableHeader().getFont();
            Font newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
            demandTable.getTableHeader().setFont(newHeaderFont);
            dataModel.fireTableDataChanged();

            demandTable.getColumnModel().getColumn(0).setMinWidth(125);
            demandTable.getColumnModel().getColumn(0).setMaxWidth(125);

        } else {
            System.err.println("DemandData not set, panel not activated.");
        }
    }

    /**
     * Updates the panel to reflect changes in RRP dates. Maintains demand data
     * current displayed in panel.
     *
     * @param year
     * @param startMonth 1 will be added to fix indexing
     * @param startDay 1 will be added to fix indexing
     * @param endMonth 1 will be added to fix indexing
     * @param endDay 1 will be added to fix indexing
     */
    public void updateDemandJPanel(int year, int startMonth, int startDay, int endMonth, int endDay) {

        // Set up data and model
        //demandData = new DemandData(year, startMonth+1, startDay+1, endMonth+1, endDay+1); //Adding one to fix indexing
        demandData.setStartMonth(startMonth + 1);
        demandData.setStartDay(startDay + 1);
        demandData.setEndMonth(endMonth + 1);
        demandData.setEndDay(endDay + 1);
        demandData.setYear(year);

        // Updating list of active months
        for (int i = 0; i < 12; ++i) {
            if (i < startMonth || i > endMonth) {
                demandData.setMonthActive(i, false);
            } else {
                demandData.setMonthActive(i, true);
            }
        }

        activateDemandJPanel();

    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get method for panel DemandData object.
     *
     * @return demandData of the panel
     */
    public DemandData getDemandData() {
        return demandData;
    }

    /**
     *
     * @return
     */
    public boolean useGPValues() {
        return useGPValuesCB.isSelected();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        demandTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        urbanDefaultsButton = new javax.swing.JButton();
        ruralDefaultsButton = new javax.swing.JButton();
        seedValuesButton = new javax.swing.JButton();
        facilitySpecificButton = new javax.swing.JButton();
        useGPValuesCB = new javax.swing.JCheckBox();

        setAutoscrolls(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Daily Demand Multipliers"));

        demandTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        demandTable.setFillsViewportHeight(true);
        demandTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(demandTable);

        jPanel4.setLayout(new java.awt.GridLayout(1, 5));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GP Defaults:");
        jPanel4.add(jLabel1);

        urbanDefaultsButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        urbanDefaultsButton.setText("Urban Default Values");
        urbanDefaultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                urbanDefaultsButtonActionPerformed(evt);
            }
        });
        jPanel4.add(urbanDefaultsButton);

        ruralDefaultsButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ruralDefaultsButton.setText("Rural Default Values");
        ruralDefaultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruralDefaultsButtonActionPerformed(evt);
            }
        });
        jPanel4.add(ruralDefaultsButton);

        seedValuesButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        seedValuesButton.setText("Saved Facility Specific");
        seedValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seedValuesButtonActionPerformed(evt);
            }
        });
        jPanel4.add(seedValuesButton);

        facilitySpecificButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        facilitySpecificButton.setText("User Input Values");
        facilitySpecificButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facilitySpecificButtonActionPerformed(evt);
            }
        });
        jPanel4.add(facilitySpecificButton);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        useGPValuesCB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        useGPValuesCB.setText("Use Values Specified in GP - Demand Tab");
        useGPValuesCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useGPValuesCBItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(useGPValuesCB)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(useGPValuesCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
	// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Actions">
    private void urbanDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urbanDefaultsButtonActionPerformed
        demandData.useUrbanDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_urbanDefaultsButtonActionPerformed

    private void ruralDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruralDefaultsButtonActionPerformed
        demandData.useRuralDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_ruralDefaultsButtonActionPerformed

    private void facilitySpecificButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facilitySpecificButtonActionPerformed
        demandData.useFacilitySpecificDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_facilitySpecificButtonActionPerformed

    private void seedValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seedValuesButtonActionPerformed
        demandData.useSeedValues();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_seedValuesButtonActionPerformed

    private void useGPValuesCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useGPValuesCBItemStateChanged

        boolean val = !useGPValuesCB.isSelected();

        demandTable.setEnabled(val);
        urbanDefaultsButton.setEnabled(val);
        ruralDefaultsButton.setEnabled(val);
        seedValuesButton.setEnabled(val);
        facilitySpecificButton.setEnabled(val);
        tableRenderer.greyOut(!val);
        if (val) {
            jLabel1.setForeground(Color.BLACK);
        } else {
            jLabel1.setForeground(Color.GRAY);
        }

    }//GEN-LAST:event_useGPValuesCBItemStateChanged
    // </editor-fold>

    /**
     * Setter for table font. Does not override renderer font, but sets table
     * row heights correctly.
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        demandTable.setFont(newTableFont);
        demandTable.setRowHeight(newTableFont.getSize() + 2);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable demandTable;
    private javax.swing.JButton facilitySpecificButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton ruralDefaultsButton;
    private javax.swing.JButton seedValuesButton;
    private javax.swing.JButton urbanDefaultsButton;
    private javax.swing.JCheckBox useGPValuesCB;
    // End of variables declaration//GEN-END:variables
}
