package GUI.ATDMHelper.hardShoulderRunning;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Lake Trask
 */
public class HSRCapacityDialog extends javax.swing.JDialog {

    private int laneMaxCapacity;

    private boolean returnStatus = false;

    /**
     * Creates new form ATMParameterDialog
     *
     * @param parent
     * @param modal
     */
    public HSRCapacityDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        pctMainlineLaneRadioButtonActionPerformed(null);

        //<editor-fold defaultstate="collapsed" desc="Spinner Listeners">
        hsr1LPct.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr1LPctStateChanged(evt);
            }
        });

        hsr1LVPH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr1LVPHStateChanged(evt);
            }
        });
        hsr2LPct.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr2LPctStateChanged(evt);
            }
        });

        hsr2LVPH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr2LVPHStateChanged(evt);
            }
        });
        hsr3LPct.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr3LPctStateChanged(evt);
            }
        });

        hsr3LVPH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr3LVPHStateChanged(evt);
            }
        });
        hsr4LPct.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr4LPctStateChanged(evt);
            }
        });

        hsr4LVPH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr4LVPHStateChanged(evt);
            }
        });
        hsr5LPct.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr5LPctStateChanged(evt);
            }
        });

        hsr5LVPH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hsr5LVPHStateChanged(evt);
            }
        });
//</editor-fold>

        laneMaxCapacity = 2000;

        hsr1LVPH.setModel(new SpinnerNumberModel(0, 0, laneMaxCapacity, 1));
        hsr2LVPH.setModel(new SpinnerNumberModel(0, 0, laneMaxCapacity, 1));
        hsr3LVPH.setModel(new SpinnerNumberModel(0, 0, laneMaxCapacity, 1));
        hsr4LVPH.setModel(new SpinnerNumberModel(0, 0, laneMaxCapacity, 1));
        hsr5LVPH.setModel(new SpinnerNumberModel(0, 0, laneMaxCapacity, 1));

        hsr1LPct.setModel(new SpinnerNumberModel(50, 0, 100, 1));
        hsr2LPct.setModel(new SpinnerNumberModel(50, 0, 100, 1));
        hsr3LPct.setModel(new SpinnerNumberModel(50, 0, 100, 1));
        hsr4LPct.setModel(new SpinnerNumberModel(50, 0, 100, 1));
        hsr5LPct.setModel(new SpinnerNumberModel(50, 0, 100, 1));

        updateHSRSpinnersVPH();
    }

    /**
     *
     * @param hsrCAFs
     */
    public void setHardShouldCAFs(float[] hsrCAFs) {
        hsr1LPct.setValue(Math.round(hsrCAFs[0] * 100.0f));
        hsr2LPct.setValue(Math.round(hsrCAFs[1] * 100.0f));
        hsr3LPct.setValue(Math.round(hsrCAFs[2] * 100.0f));
        hsr4LPct.setValue(Math.round(hsrCAFs[3] * 100.0f));
        hsr5LPct.setValue(Math.round(hsrCAFs[4] * 100.0f));
    }

    /**
     *
     * @return
     */
    public float[] getHardShoulderCAFs() {
        float[] hsrCAFs = new float[5];
        hsrCAFs[0] = ((int) hsr1LPct.getValue()) / 100.0f;
        hsrCAFs[1] = ((int) hsr2LPct.getValue()) / 100.0f;
        hsrCAFs[2] = ((int) hsr3LPct.getValue()) / 100.0f;
        hsrCAFs[3] = ((int) hsr4LPct.getValue()) / 100.0f;
        hsrCAFs[4] = ((int) hsr5LPct.getValue()) / 100.0f;

        return hsrCAFs;
    }

    /**
     *
     * @return
     */
    public boolean getReturnStatus() {
        return returnStatus;
    }

    private void doClose() {
        this.setVisible(false);
    }

    private void updateHSRSpinnersVPH() {
        updateHSRSpinnersVPH(1);
        updateHSRSpinnersVPH(2);
        updateHSRSpinnersVPH(3);
        updateHSRSpinnersVPH(4);
        updateHSRSpinnersVPH(5);
    }

    private void updateHSRSpinnersVPH(int numLanes) {
        int val;
        switch (numLanes) {
            case 1:
                val = Math.round(((int) hsr1LPct.getValue()) / 100.0f * laneMaxCapacity);
                hsr1LVPH.setValue(val);
                break;
            case 2:
                val = Math.round(((int) hsr2LPct.getValue()) / 100.0f * laneMaxCapacity);
                hsr2LVPH.setValue(val);
                break;
            case 3:
                val = Math.round(((int) hsr3LPct.getValue()) / 100.0f * laneMaxCapacity);
                hsr3LVPH.setValue(val);
                break;
            case 4:
                val = Math.round(((int) hsr4LPct.getValue()) / 100.0f * laneMaxCapacity);
                hsr4LVPH.setValue(val);
                break;
            case 5:
                val = Math.round(((int) hsr5LPct.getValue()) / 100.0f * laneMaxCapacity);
                hsr5LVPH.setValue(val);
                break;
        }
    }

    private void updateHSRSpinnersPct() {
        updateHSRSpinnersPct(1);
        updateHSRSpinnersPct(2);
        updateHSRSpinnersPct(3);
        updateHSRSpinnersPct(4);
        updateHSRSpinnersPct(5);
    }

    private void updateHSRSpinnersPct(int numLanes) {
        int val;
        switch (numLanes) {
            case 1:
                val = Math.round(100.0f * ((int) hsr1LVPH.getValue()) / laneMaxCapacity);
                hsr1LPct.setValue(val);
                break;
            case 2:
                val = Math.round(100.0f * ((int) hsr2LVPH.getValue()) / laneMaxCapacity);
                hsr2LPct.setValue(val);
                break;
            case 3:
                val = Math.round(100.0f * ((int) hsr3LVPH.getValue()) / laneMaxCapacity);
                hsr3LPct.setValue(val);
                break;
            case 4:
                val = Math.round(100.0f * ((int) hsr4LVPH.getValue()) / laneMaxCapacity);
                hsr4LPct.setValue(val);
                break;
            case 5:
                val = Math.round(100.0f * ((int) hsr5LVPH.getValue()) / laneMaxCapacity);
                hsr5LPct.setValue(val);
                break;
        }
    }

    private void hsr1LPctStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersVPH(1);
    }

    private void hsr1LVPHStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersPct(1);
    }

    private void hsr2LPctStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersVPH(2);
    }

    private void hsr2LVPHStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersPct(2);
    }

    private void hsr3LPctStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersVPH(3);
    }

    private void hsr3LVPHStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersPct(3);
    }

    private void hsr4LPctStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersVPH(4);
    }

    private void hsr4LVPHStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersPct(1);
    }

    private void hsr5LPctStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersVPH(5);
    }

    private void hsr5LVPHStateChanged(javax.swing.event.ChangeEvent evt) {
        updateHSRSpinnersPct(5);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shoulderCapacityDefinitionType = new javax.swing.ButtonGroup();
        hsrPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        hsr1LPct = new javax.swing.JSpinner();
        hsr2LPct = new javax.swing.JSpinner();
        hsr3LPct = new javax.swing.JSpinner();
        hsr4LPct = new javax.swing.JSpinner();
        hsr5LPct = new javax.swing.JSpinner();
        hsr1LVPH = new javax.swing.JSpinner();
        hsr2LVPH = new javax.swing.JSpinner();
        hsr3LVPH = new javax.swing.JSpinner();
        hsr4LVPH = new javax.swing.JSpinner();
        hsr5LVPH = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pctMainlineLaneRadioButton = new javax.swing.JRadioButton();
        vehPerHourRadioButton = new javax.swing.JRadioButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Hard Shoulder Capacities");

        hsrPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setLayout(new java.awt.GridLayout(3, 7, 2, 0));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("One Lane");
        jPanel1.add(jLabel7);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Two Lane");
        jPanel1.add(jLabel8);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("3 Lane");
        jPanel1.add(jLabel9);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("4 Lane");
        jPanel1.add(jLabel10);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("5+ Lane");
        jPanel1.add(jLabel11);
        jPanel1.add(hsr1LPct);
        jPanel1.add(hsr2LPct);
        jPanel1.add(hsr3LPct);
        jPanel1.add(hsr4LPct);
        jPanel1.add(hsr5LPct);
        jPanel1.add(hsr1LVPH);
        jPanel1.add(hsr2LVPH);
        jPanel1.add(hsr3LVPH);
        jPanel1.add(hsr4LVPH);
        jPanel1.add(hsr5LVPH);

        jPanel2.setLayout(new java.awt.GridLayout(3, 1));

        jLabel1.setText("Define Shoulder Capacity:");
        jPanel2.add(jLabel1);

        shoulderCapacityDefinitionType.add(pctMainlineLaneRadioButton);
        pctMainlineLaneRadioButton.setSelected(true);
        pctMainlineLaneRadioButton.setText("% of 1 Mainline Lane:");
        pctMainlineLaneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pctMainlineLaneRadioButtonActionPerformed(evt);
            }
        });
        jPanel2.add(pctMainlineLaneRadioButton);

        shoulderCapacityDefinitionType.add(vehPerHourRadioButton);
        vehPerHourRadioButton.setText("Veh. Per Hour:");
        vehPerHourRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehPerHourRadioButtonActionPerformed(evt);
            }
        });
        jPanel2.add(vehPerHourRadioButton);

        javax.swing.GroupLayout hsrPanelLayout = new javax.swing.GroupLayout(hsrPanel);
        hsrPanel.setLayout(hsrPanelLayout);
        hsrPanelLayout.setHorizontalGroup(
            hsrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hsrPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        hsrPanelLayout.setVerticalGroup(
            hsrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hsrPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hsrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hsrPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hsrPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.returnStatus = true;
        doClose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.returnStatus = false;
        doClose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void pctMainlineLaneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pctMainlineLaneRadioButtonActionPerformed
        hsr1LPct.setEnabled(true);
        hsr2LPct.setEnabled(true);
        hsr3LPct.setEnabled(true);
        hsr4LPct.setEnabled(true);
        hsr5LPct.setEnabled(true);

        hsr1LVPH.setEnabled(false);
        hsr2LVPH.setEnabled(false);
        hsr3LVPH.setEnabled(false);
        hsr4LVPH.setEnabled(false);
        hsr5LVPH.setEnabled(false);
    }//GEN-LAST:event_pctMainlineLaneRadioButtonActionPerformed

    private void vehPerHourRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehPerHourRadioButtonActionPerformed
        hsr1LPct.setEnabled(false);
        hsr2LPct.setEnabled(false);
        hsr3LPct.setEnabled(false);
        hsr4LPct.setEnabled(false);
        hsr5LPct.setEnabled(false);

        hsr1LVPH.setEnabled(true);
        hsr2LVPH.setEnabled(true);
        hsr3LVPH.setEnabled(true);
        hsr4LVPH.setEnabled(true);
        hsr5LVPH.setEnabled(true);
    }//GEN-LAST:event_vehPerHourRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JSpinner hsr1LPct;
    private javax.swing.JSpinner hsr1LVPH;
    private javax.swing.JSpinner hsr2LPct;
    private javax.swing.JSpinner hsr2LVPH;
    private javax.swing.JSpinner hsr3LPct;
    private javax.swing.JSpinner hsr3LVPH;
    private javax.swing.JSpinner hsr4LPct;
    private javax.swing.JSpinner hsr4LVPH;
    private javax.swing.JSpinner hsr5LPct;
    private javax.swing.JSpinner hsr5LVPH;
    private javax.swing.JPanel hsrPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton pctMainlineLaneRadioButton;
    private javax.swing.ButtonGroup shoulderCapacityDefinitionType;
    private javax.swing.JRadioButton vehPerHourRadioButton;
    // End of variables declaration//GEN-END:variables
}
