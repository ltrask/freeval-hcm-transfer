package GUI.ATDMHelper.Tutorial;

/**
 *
 * @author Lake Trask
 */
public class ATDMTutorial extends javax.swing.JDialog {

    private int currIndex = 0;

    private final String[] imageList;

    /**
     * Creates new form ATDMTutorial
     *
     * @param parent
     * @param modal
     */
    public ATDMTutorial(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //String prefixPath = "C:\\Users\\jlake_000\\";
        imageList = new String[]{"/GUI/ATDMHelper/Tutorial/Imgs/tut1.png",
            "/GUI/ATDMHelper/Tutorial/Imgs/tut2.png",
            "/GUI/ATDMHelper/Tutorial/Imgs/tut3.png",
            "/GUI/ATDMHelper/Tutorial/Imgs/tut4.png",
            "/GUI/ATDMHelper/Tutorial/Imgs/tut5.png",
            "/GUI/ATDMHelper/Tutorial/Imgs/tut6.png"
        };

        tutorialPanel.nextImage(imageList[currIndex]);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tutorialPanel = new GUI.ATDMHelper.Tutorial.ImagePanel();
        nextButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tutorialPanel.setPreferredSize(new java.awt.Dimension(800, 480));

        javax.swing.GroupLayout tutorialPanelLayout = new javax.swing.GroupLayout(tutorialPanel);
        tutorialPanel.setLayout(tutorialPanelLayout);
        tutorialPanelLayout.setHorizontalGroup(
                tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 813, Short.MAX_VALUE)
        );
        tutorialPanelLayout.setVerticalGroup(
                tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 493, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(tutorialPanel);

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(backButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nextButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(closeButton)))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nextButton)
                                .addComponent(closeButton)
                                .addComponent(backButton))
                        .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (currIndex == imageList.length - 1) {
            closeButtonActionPerformed(evt);
        } else {
            tutorialPanel.nextImage(imageList[++currIndex]);
        }
        if (currIndex == imageList.length - 1) {
            nextButton.setText("Finish");
        }
        if (currIndex == 1) {
            backButton.setEnabled(true);
        }

    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (currIndex == imageList.length - 1) {
            nextButton.setText("Next");
        }

        if (currIndex > 0) {
            tutorialPanel.nextImage(imageList[currIndex - 1]);
            currIndex--;
            if (currIndex == 0) {
                backButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_backButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ATDMTutorial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ATDMTutorial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ATDMTutorial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ATDMTutorial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ATDMTutorial dialog = new ATDMTutorial(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;

    private javax.swing.JButton closeButton;

    private javax.swing.JScrollPane jScrollPane1;

    private javax.swing.JButton nextButton;

    private GUI.ATDMHelper.Tutorial.ImagePanel tutorialPanel;
    // End of variables declaration//GEN-END:variables
}
