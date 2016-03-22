package GUI.major.outputPanel;

import GUI.major.MainWindow;
import GUI.major.tableHelper.SplitTableJPanel;
import GUI.seedEditAndIOHelper.ExcelAdapter;
import coreEngine.Seed;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * This is the contour table for single seed/scenario output.
 *
 * @author Shu Liu
 */
public class ContourPanel extends javax.swing.JPanel {

    private final ContourJTableFirst contourJTableFirst;

    private final ContourJTableRest contourJTableRest;

    private MainWindow mainWindow;

    /**
     * Creates new form ContourPanel
     */
    public ContourPanel() {
        initComponents();

        contourJTableFirst = new ContourJTableFirst();
        contourJTableRest = new ContourJTableRest();
        SplitTableJPanel contourSplitPanel = new SplitTableJPanel(contourJTableFirst, contourJTableRest);
        contourSplitPanel.setDividerLocation(160);
        jPanel2.add(contourSplitPanel);
        contourJTableRest.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {
                if (evt.isControlDown()) {
                    boolean zoomRow = evt.getWheelRotation() > 0
                            || contourSplitPanel.getHeight() < contourJTableRest.getRowCount() * contourJTableRest.getRowHeight();
                    boolean zoomColumn = true; // Todo limit to size of component
                    contourJTableRest.updateZoom(evt.getWheelRotation(), zoomRow, zoomColumn);
                    contourJTableFirst.setRowHeight(contourJTableRest.getRowHeight());
                }
            }
        });
        contourJTableRest.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.isShiftDown()
                        && (e.getKeyChar() == '+' || e.getKeyChar() == '_')) {
                    if (e.getKeyChar() == '+') {
                        boolean zoomRow = true || contourSplitPanel.getHeight() < contourJTableRest.getRowCount() * contourJTableRest.getRowHeight();
                        boolean zoomColumn = true; // Todo limit to size of component
                        contourJTableRest.updateZoom(1, zoomRow, zoomColumn);
                        contourJTableFirst.setRowHeight(contourJTableRest.getRowHeight());
                    } else {
                        boolean zoomRow = false || contourSplitPanel.getHeight() < contourJTableRest.getRowCount() * contourJTableRest.getRowHeight();
                        boolean zoomColumn = true; // Todo limit to size of component
                        contourJTableRest.updateZoom(-1, zoomRow, zoomColumn);
                        contourJTableFirst.setRowHeight(contourJTableRest.getRowHeight());
                    }
                }
            }
        });
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     * @param scen index of scenario to be displayed
     * @param atdm atdm index
     */
    public void selectSeedScenATDM(Seed seed, int scen, int atdm) {
        if (seed != null && seed.isManagedLaneUsed()) {
            mlButton.setEnabled(true);
        } else {
            mlButton.setEnabled(false);
            gpButton.setSelected(true);
        }
        contourJTableFirst.selectSeedScenPeriod(seed);
        contourJTableRest.selectSeedScenATDM(seed, scen, atdm);
    }

    /**
     * Setter for mainWindow connection
     *
     * @param mainWindow main window instance
     */
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Setter for table font
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        contourJTableFirst.setFont(newTableFont);
        contourJTableFirst.setRowHeight(newTableFont.getSize() + 2);
        contourJTableRest.setFont(newTableFont);
        contourJTableRest.setRowHeight(newTableFont.getSize() + 2);
    }

    /**
     * Copy table to clipboard in excel format
     *
     * @return whether copy is successful
     */
    public String copyTable() {
        try {
            return (ExcelAdapter.copySplitTable(contourJTableFirst, contourJTableRest));
        } catch (Exception e) {
            return "Error when copy contour table " + e.toString();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        showSpeedButton = new javax.swing.JRadioButton();
        totalDensityVEHButton = new javax.swing.JRadioButton();
        totalDensityPCButton = new javax.swing.JRadioButton();
        IADensityButton = new javax.swing.JRadioButton();
        densityLOSButton = new javax.swing.JRadioButton();
        demandLOSButton = new javax.swing.JRadioButton();
        dcButton = new javax.swing.JRadioButton();
        vcButton = new javax.swing.JRadioButton();
        queuePercentButton = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        gpButton = new javax.swing.JRadioButton();
        mlButton = new javax.swing.JRadioButton();

        jPanel2.setPreferredSize(new java.awt.Dimension(1020, 460));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Item"));
        jPanel1.setLayout(new java.awt.GridLayout(3, 3));

        buttonGroup1.add(showSpeedButton);
        showSpeedButton.setSelected(true);
        showSpeedButton.setText("Speed (mi/h)");
        showSpeedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSpeedButtonActionPerformed(evt);
            }
        });
        jPanel1.add(showSpeedButton);

        buttonGroup1.add(totalDensityVEHButton);
        totalDensityVEHButton.setText("Total Density (veh/mi/ln)");
        totalDensityVEHButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalDensityVEHButtonActionPerformed(evt);
            }
        });
        jPanel1.add(totalDensityVEHButton);

        buttonGroup1.add(totalDensityPCButton);
        totalDensityPCButton.setText("Total Density (pc/mi/ln)");
        totalDensityPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalDensityPCButtonActionPerformed(evt);
            }
        });
        jPanel1.add(totalDensityPCButton);

        buttonGroup1.add(IADensityButton);
        IADensityButton.setText("Influence Area Density (pc/mi/ln)");
        IADensityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IADensityButtonActionPerformed(evt);
            }
        });
        jPanel1.add(IADensityButton);

        buttonGroup1.add(densityLOSButton);
        densityLOSButton.setText("Density Based LOS");
        densityLOSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                densityLOSButtonActionPerformed(evt);
            }
        });
        jPanel1.add(densityLOSButton);

        buttonGroup1.add(demandLOSButton);
        demandLOSButton.setText("Demand Based LOS");
        demandLOSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demandLOSButtonActionPerformed(evt);
            }
        });
        jPanel1.add(demandLOSButton);

        buttonGroup1.add(dcButton);
        dcButton.setText("D/C");
        dcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dcButtonActionPerformed(evt);
            }
        });
        jPanel1.add(dcButton);

        buttonGroup1.add(vcButton);
        vcButton.setText("V/C");
        vcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vcButtonActionPerformed(evt);
            }
        });
        jPanel1.add(vcButton);

        buttonGroup1.add(queuePercentButton);
        queuePercentButton.setText("Queue %");
        queuePercentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queuePercentButtonActionPerformed(evt);
            }
        });
        jPanel1.add(queuePercentButton);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Segment Type"));
        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        buttonGroup2.add(gpButton);
        gpButton.setSelected(true);
        gpButton.setText("General Purpose Segments");
        gpButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gpButtonItemStateChanged(evt);
            }
        });
        gpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpButtonActionPerformed(evt);
            }
        });
        gpButton.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                gpButtonPropertyChange(evt);
            }
        });
        jPanel3.add(gpButton);

        buttonGroup2.add(mlButton);
        mlButton.setText("Managed Lanes Segments");
        mlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mlButtonActionPerformed(evt);
            }
        });
        jPanel3.add(mlButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 898, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showSpeedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSpeedButtonActionPerformed
        configModel();
    }//GEN-LAST:event_showSpeedButtonActionPerformed

    private void totalDensityVEHButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalDensityVEHButtonActionPerformed
        configModel();
    }//GEN-LAST:event_totalDensityVEHButtonActionPerformed

    private void totalDensityPCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalDensityPCButtonActionPerformed
        configModel();
    }//GEN-LAST:event_totalDensityPCButtonActionPerformed

    private void IADensityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IADensityButtonActionPerformed
        configModel();
    }//GEN-LAST:event_IADensityButtonActionPerformed

    private void densityLOSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_densityLOSButtonActionPerformed
        configModel();
    }//GEN-LAST:event_densityLOSButtonActionPerformed

    private void demandLOSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demandLOSButtonActionPerformed
        configModel();
    }//GEN-LAST:event_demandLOSButtonActionPerformed

    private void dcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dcButtonActionPerformed
        configModel();
    }//GEN-LAST:event_dcButtonActionPerformed

    private void vcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vcButtonActionPerformed
        configModel();
    }//GEN-LAST:event_vcButtonActionPerformed

    private void queuePercentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queuePercentButtonActionPerformed
        configModel();
    }//GEN-LAST:event_queuePercentButtonActionPerformed

    private void gpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gpButtonActionPerformed
        configModel();
    }//GEN-LAST:event_gpButtonActionPerformed

    private void mlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mlButtonActionPerformed
        configModel();
    }//GEN-LAST:event_mlButtonActionPerformed

    private void gpButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gpButtonItemStateChanged
        configModel();
    }//GEN-LAST:event_gpButtonItemStateChanged

    private void gpButtonPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_gpButtonPropertyChange
        configModel();
    }//GEN-LAST:event_gpButtonPropertyChange

    private void updateTableZoom(int zoom) {

    }

    private void configModel() {
        if (gpButton.isSelected()) {
            if (showSpeedButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_SPEED);
                return;
            }
            if (totalDensityVEHButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_TOTAL_DENSITY_VEH);
                return;
            }
            if (totalDensityPCButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_TOTAL_DENSITY_PC);
                return;
            }
            if (IADensityButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_IA_DENSITY);
                return;
            }
            if (densityLOSButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DENSITY_LOS);
                return;
            }
            if (demandLOSButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DEMAND_LOS);
                return;
            }
            if (dcButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DC);
                return;
            }
            if (vcButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_VC);
                return;
            }
            if (queuePercentButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_QUEUE_PERCENTAGE);
                return;
            }
        } else {
            if (showSpeedButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_SPEED_ML);
                return;
            }
            if (totalDensityVEHButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_TOTAL_DENSITY_VEH_ML);
                return;
            }
            if (totalDensityPCButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_TOTAL_DENSITY_PC_ML);
                return;
            }
            if (IADensityButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_IA_DENSITY_ML);
                return;
            }
            if (densityLOSButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DENSITY_LOS_ML);
                return;
            }
            if (demandLOSButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DEMAND_LOS_ML);
                return;
            }
            if (dcButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_DC_ML);
                return;
            }
            if (vcButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_VC_ML);
                return;
            }
            if (queuePercentButton.isSelected()) {
                contourJTableRest.setModel(ContourJTableRest.MODEL_QUEUE_PERCENTAGE_ML);
                return;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton IADensityButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JRadioButton dcButton;
    private javax.swing.JRadioButton demandLOSButton;
    private javax.swing.JRadioButton densityLOSButton;
    private javax.swing.JRadioButton gpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton mlButton;
    private javax.swing.JRadioButton queuePercentButton;
    private javax.swing.JRadioButton showSpeedButton;
    private javax.swing.JRadioButton totalDensityPCButton;
    private javax.swing.JRadioButton totalDensityVEHButton;
    private javax.swing.JRadioButton vcButton;
    // End of variables declaration//GEN-END:variables
}
