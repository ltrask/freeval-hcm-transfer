package GUI.ATDMHelper.rampMetering;

import GUI.major.MainWindow;
import GUI.major.tableHelper.SplitTableJPanel;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CompressArray.CA2DInt;
import coreEngine.Seed;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * This is used for ATDM ramp metering setting.
 *
 * @author Shu Liu
 */
public class ATDMRMDialog extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    private final RMContourJTableFirst contourJTableFirst;

    private final RMContourJTableRest contourJTableRest;

    private final Seed seed;

    /**
     * Creates new form ATDMRMDialog
     *
     * @param parent parent frame
     * @param modal modal
     * @param seed seed instance
     * @param oldRampMeteringRate matrix for original ramp metering rate
     */
    public ATDMRMDialog(java.awt.Dialog parent, boolean modal, Seed seed, CA2DInt oldRampMeteringRate) {
        super(parent, modal);
        initComponents();

        // Disabling Keyboard Edits for the Capacity Increase due to RM spinner
        JTextField tempTF = (JTextField) ((JSpinner.NumberEditor) capacityIncreaseDueToRMSpinner.getEditor()).getTextField();
        tempTF.setEditable(false);
        tempTF.setBackground(java.awt.Color.WHITE);

        this.seed = seed;
        periodLabel.setText("<HTML><CENTER>Fill all<br>periods between:<br>(inclusive)");
        segmentLabel.setText("<HTML><Center>For all ramp<br>segments between:<br>(inclusive)");
        noteLabel.setText("<HTML>Note: Color contour is based on the seed's D/C "
                + "ratio and all quantities are in vehicles per hour.");
        DefaultComboBoxModel periodStartModel = new DefaultComboBoxModel();
        DefaultComboBoxModel periodEndModel = new DefaultComboBoxModel();
        for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
            periodStartModel.addElement("Period " + (period + 1));
            periodEndModel.addElement("Period " + (period + 1));
        }
        periodStartCB.setModel(periodStartModel);
        periodEndCB.setModel(periodEndModel);

        DefaultComboBoxModel segmentStartModel = new DefaultComboBoxModel();
        DefaultComboBoxModel segmentEndModel = new DefaultComboBoxModel();
        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            if (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_ONR || seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg) == CEConst.SEG_TYPE_W) {
                segmentStartModel.addElement("Segment " + (seg + 1));
                segmentEndModel.addElement("Segment " + (seg + 1));
            }
        }

        segmentStartCB.setModel(segmentStartModel);
        segmentEndCB.setModel(segmentEndModel);

        contourJTableFirst = new RMContourJTableFirst(seed);
        contourJTableRest = new RMContourJTableRest(seed);

        if (oldRampMeteringRate != null) {
            contourJTableRest.setRampMeteringRate(oldRampMeteringRate);
        }

        contourJTableFirst.setFont(MainWindow.getTableFont());
        contourJTableFirst.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        contourJTableRest.setFont(MainWindow.getTableFont());
        contourJTableRest.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        SplitTableJPanel contourSplitPanel = new SplitTableJPanel(contourJTableFirst, contourJTableRest);
        contourSplitPanel.setDividerLocation(160);
        jPanel2.add(contourSplitPanel);

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

    public void setCapacityIncreaseDueToRM(float capacityIncreaseDueToRM) {
        capacityIncreaseDueToRMSpinner.setValue(capacityIncreaseDueToRM);
    }

    public float getCapacityIncreaseDueToRM() {
        return (float) capacityIncreaseDueToRMSpinner.getValue();
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

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        periodLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fillValueText = new javax.swing.JTextField();
        fillButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();
        segmentLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        periodStartCB = new javax.swing.JComboBox();
        periodEndCB = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        segmentStartCB = new javax.swing.JComboBox();
        segmentEndCB = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        capacityIncreaseDueToRMSpinner = new javax.swing.JSpinner();

        setTitle("ATDM Ramp Metering");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel2.setToolTipText("test");
        jPanel2.setPreferredSize(new java.awt.Dimension(1020, 460));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        periodLabel.setText("Fill");

        jLabel3.setText("with");

        fillValueText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fillValueText.setText("2100");

        fillButton.setText("Fill");
        fillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillButtonActionPerformed(evt);
            }
        });

        noteLabel.setText("Note: Color contour is based on the seed's D/C ratio and all values are in Vehicles per Hour");

        segmentLabel.setText("jLabel4");

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 0, 3));

        periodStartCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(periodStartCB);

        periodEndCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(periodEndCB);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 0, 3));

        segmentStartCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(segmentStartCB);

        segmentEndCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(segmentEndCB);

        jLabel1.setText("Mainline Capacity Increase Due to Ramp Metering:");

        capacityIncreaseDueToRMSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.03f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.01f)));
        capacityIncreaseDueToRMSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(capacityIncreaseDueToRMSpinner, "#%"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(periodLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(segmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fillButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fillValueText, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(capacityIncreaseDueToRMSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(noteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(segmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(periodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(capacityIncreaseDueToRMSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(29, 29, 29))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(fillValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fillButton)))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel3});

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

    private void fillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillButtonActionPerformed
//        try {
        int value = Integer.parseInt(fillValueText.getText());
        if (value > 0) {
            int startPeriod = Integer.parseInt(periodStartCB.getSelectedItem().toString().split(" ")[1]);
            int endPeriod = Integer.parseInt(periodEndCB.getSelectedItem().toString().split(" ")[1]);
            int startSegment = Integer.parseInt(segmentStartCB.getSelectedItem().toString().split(" ")[1]);
            int endSegment = Integer.parseInt(segmentEndCB.getSelectedItem().toString().split(" ")[1]);
            if (startPeriod > endPeriod && startSegment <= endSegment) {
                // Periods are not valid, segments are valid
                JOptionPane.showMessageDialog(this, "The start period must be before the end period.", "Error: Invalid Periods Specified", JOptionPane.ERROR_MESSAGE);
            } else if (startPeriod <= endPeriod && startSegment > endSegment) {
                // Periods are valid, segments are not valid
                JOptionPane.showMessageDialog(this, "The starting segment must be before the end segment.", "Error: Invalid Segments Specified", JOptionPane.ERROR_MESSAGE);
            } else if (startPeriod > endPeriod && startSegment > endSegment) {
                JOptionPane.showMessageDialog(this, "<HTML><CENTER>The start period must be before the end period.<br>The starting segment must be before the end segment.", "Error: Invalid Input", JOptionPane.ERROR_MESSAGE);
            } else { //input is correct
                contourJTableRest.fill(value, startPeriod, endPeriod, startSegment, endSegment);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ramp Metering Must Be A Positive Integer", "Error", JOptionPane.ERROR_MESSAGE);
            fillValueText.setText("2100");
        }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Ramp Metering Must Be A Positive Integer", "Error", JOptionPane.ERROR_MESSAGE);
//            fillValueText.setText("2100");
//        }
    }//GEN-LAST:event_fillButtonActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * Getter for ramp metering rate matrix
     *
     * @return ramp metering rate matrix
     */
    public CA2DInt getRampMeteringRate() {
        return contourJTableRest.getRampMeteringRate();
    }

    /**
     * Setter for ramp metering rate matrix
     *
     * @param rampMeteringRate ramp metering rate matrix
     */
    public void setRampMeteringRate(CA2DInt rampMeteringRate) {
        contourJTableRest.setRampMeteringRate(rampMeteringRate);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JSpinner capacityIncreaseDueToRMSpinner;
    private javax.swing.JButton fillButton;
    private javax.swing.JTextField fillValueText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox periodEndCB;
    private javax.swing.JLabel periodLabel;
    private javax.swing.JComboBox periodStartCB;
    private javax.swing.JComboBox segmentEndCB;
    private javax.swing.JLabel segmentLabel;
    private javax.swing.JComboBox segmentStartCB;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
