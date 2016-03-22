/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Toolbox.GUI.major.IOHelper;

import GUI.major.MainWindow;
import static GUI.major.MainWindow.printLog;
import coreEngine.Helper.ASCIIAdapter.ASCIISeedFileAdapter_GPMLFormat;
import coreEngine.Helper.ASCIIAdapter.ASCIISeedFileAdapter_RLFormat;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import main.FREEVAL_HCM;

/**
 *
 * @author jltrask
 */
public class NewToolboxProjectDialog extends javax.swing.JDialog {

    private Seed seed = null;

    private int returnStatus = -1;

    public static final int RETURN_STATUS_CANCEL = -1;
    public static final int RETURN_STATUS_NEW_SEED = 0;
    public static final int RETURN_STATUS_FROM_EXISTING_SEED = 1;

    private JButton seedFileChooserOkButton;

    private final String TYPE;
    private String projFileExtension;

    private boolean useFacilityGeometryOnly = true;

    private JDialog launchDlg;

    /**
     * Creates new form NewToolboxProjectDialog
     *
     * @param parent
     * @param modal
     * @param toolboxID
     */
    public NewToolboxProjectDialog(java.awt.Frame parent, boolean modal, String toolboxID) {
        super(parent, modal);
        launchDlg = new JDialog(parent, "Please Wait", false);
        launchDlg.setLayout(new GridLayout(2, 1));
        launchDlg.setSize(250, 100);
        JLabel launchLabel = new JLabel("Loading Components and Defaults....");
        //launchLabel.setHorizontalAlignment(JLabel.CENTER);
        launchDlg.add(launchLabel);
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        launchDlg.add(pb);
        Task initializeDialog = new Task(parent);
        initializeDialog.execute();
        initComponents();
        this.TYPE = toolboxID;
        initDialog();
        launchDlg.setVisible(false);
        launchDlg.dispose();
    }

    private void initDialog() {
        //this.TYPE = toolboxID;
        seedFileChooserOkButton = seedFileChooser.getUI().getDefaultButton(seedFileChooser);
        seedFileChooser.setFileFilter(new MultiSeedFileFilter());
        seedFileChooser.addChoosableFileFilter(new SeedFileFilter());
        seedFileChooser.addChoosableFileFilter(new AsciiFileFilter());
        seedFileChooser.setCurrentDirectory(FREEVAL_HCM.getInitialDirectory());

        seedFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    openFromExistingSeed();
                } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                    cancelButtonPressed();
                }
            }
        });

        configureForToolboxID();
        startFromNewRadioButtonActionPerformed(null);
    }

    private void configureForToolboxID() {
        switch (TYPE) {
            case MainWindow.TOOLBOX_DSS:
                this.setTitle("Create new FREEVAL-DSS Project");
                hideUseGeometryOnlyPanel();
                projFileExtension = ".dss";
                break;
            case MainWindow.TOOLBOX_WZ:
                this.setTitle("Create new FREEVAL-WZ Project");
                projFileExtension = ".wz";
                break;
            default:
                this.setTitle("Create new Toolbox Project");
                hideUseGeometryOnlyPanel();
                projFileExtension = ".seed";
                break;
        }
    }

    private void hideUseGeometryOnlyPanel() {
        existingFacilityPanel.remove(useGeometryPanel);
    }

    private class SeedFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".seed");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "FREEVAL-2015e Project files (*.seed)";
        }
    }

    private class AsciiFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "FREEVAL ASCII files (*.txt)";
        }
    }

    private class MultiSeedFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt") || file.getAbsolutePath().endsWith(".seed");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "FREEVAL Seed files (*.seed, *.txt)";
        }
    }

    private void doClose() {
        this.setVisible(false);
    }

    public Seed getSeed() {
        return seed;
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public String getNewProjectName() {
        return newProjectNameTextField.getText();
    }

    private void openFromExistingSeed() {
        returnStatus = RETURN_STATUS_FROM_EXISTING_SEED;
        String openFileName = seedFileChooser.getSelectedFile().getAbsolutePath();
        if (openFileName.endsWith(".seed")) {
            try {
                //open seed from file
                FileInputStream fis = new FileInputStream(openFileName);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                this.seed = (Seed) ois.readObject();
                this.seed.resetSeedToInputOnly();
                ois.close();
                this.seed.setValue(CEConst.IDS_SEED_FILE_NAME, openFileName.replace(".seed", projFileExtension));
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to open file");
            }
            doClose();
        } else if (openFileName.endsWith(".txt")) {
            try {
                Scanner input = new Scanner(new File(openFileName));
                String firstLine = input.nextLine();
                input.close();
                //choose correct ASCII adapter based on ASCII input file format
                if (firstLine.startsWith("<")) {
                    ASCIISeedFileAdapter_GPMLFormat textSeed = new ASCIISeedFileAdapter_GPMLFormat();
                    this.seed = textSeed.importFromASCII(openFileName);
                    if (this.seed != null) {
                        printLog("Seed added from ASCII file : " + this.seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                        this.seed.setValue(CEConst.IDS_SEED_FILE_NAME, null);
                    } else {
                        printLog("Fail to import ASCII file");
                    }
                } else {
                    ASCIISeedFileAdapter_RLFormat textSeed = new ASCIISeedFileAdapter_RLFormat();
                    this.seed = textSeed.importFromFile(openFileName);
                    if (this.seed != null) {
                        printLog("Seed added from ASCII file : " + this.seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                        this.seed.setValue(CEConst.IDS_SEED_FILE_NAME, null);
                    } else {
                        printLog("Fail to import ASCII file");
                    }
                }
            } catch (IOException e) {

            }
            doClose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please choose a valid input file.",
                    "Error: Invalid File Type",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void cancelButtonPressed() {
        this.returnStatus = RETURN_STATUS_CANCEL;
        doClose();
    }

    public boolean isUseFacilityGeometryOnly() {
        return useFacilityGeometryOnly;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        facilityGeometryButtonGroup = new javax.swing.ButtonGroup();
        newOrExistingButtonGroup = new javax.swing.ButtonGroup();
        newFacilityPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        newProjectNameTextField = new javax.swing.JTextField();
        createNewButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        existingFacilityPanel = new javax.swing.JPanel();
        seedFileChooser = new javax.swing.JFileChooser();
        useGeometryPanel = new javax.swing.JPanel();
        useFacilityGeometryCheckBox = new javax.swing.JCheckBox();
        useFacilityDemandsCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        startFromNewRadioButton = new javax.swing.JRadioButton();
        startFromExistingRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        newFacilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Start from New Facility", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 13))); // NOI18N

        jLabel1.setText("Project Name: ");

        newProjectNameTextField.setText("New Project");
        newProjectNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                newProjectNameTextFieldFocusGained(evt);
            }
        });

        createNewButton.setText("Create");
        createNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newFacilityPanelLayout = new javax.swing.GroupLayout(newFacilityPanel);
        newFacilityPanel.setLayout(newFacilityPanelLayout);
        newFacilityPanelLayout.setHorizontalGroup(
            newFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newFacilityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newProjectNameTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createNewButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        newFacilityPanelLayout.setVerticalGroup(
            newFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(newProjectNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addComponent(createNewButton)
                .addComponent(cancelButton))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        existingFacilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Import From Existing Facility", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N

        seedFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                seedFileChooserPropertyChange(evt);
            }
        });

        useGeometryPanel.setLayout(new java.awt.GridLayout(1, 2));

        facilityGeometryButtonGroup.add(useFacilityGeometryCheckBox);
        useFacilityGeometryCheckBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        useFacilityGeometryCheckBox.setSelected(true);
        useFacilityGeometryCheckBox.setText("Use Facility Geometry Only");
        useFacilityGeometryCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useFacilityGeometryCheckBoxActionPerformed(evt);
            }
        });
        useGeometryPanel.add(useFacilityGeometryCheckBox);

        facilityGeometryButtonGroup.add(useFacilityDemandsCheckBox);
        useFacilityDemandsCheckBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        useFacilityDemandsCheckBox.setText("Use Included Facility Demands");
        useFacilityDemandsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useFacilityDemandsCheckBoxActionPerformed(evt);
            }
        });
        useGeometryPanel.add(useFacilityDemandsCheckBox);

        javax.swing.GroupLayout existingFacilityPanelLayout = new javax.swing.GroupLayout(existingFacilityPanel);
        existingFacilityPanel.setLayout(existingFacilityPanelLayout);
        existingFacilityPanelLayout.setHorizontalGroup(
            existingFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(existingFacilityPanelLayout.createSequentialGroup()
                .addGroup(existingFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(existingFacilityPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(useGeometryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE))
                    .addComponent(seedFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        existingFacilityPanelLayout.setVerticalGroup(
            existingFacilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, existingFacilityPanelLayout.createSequentialGroup()
                .addComponent(useGeometryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seedFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        newOrExistingButtonGroup.add(startFromNewRadioButton);
        startFromNewRadioButton.setSelected(true);
        startFromNewRadioButton.setText("Start From New Facility");
        startFromNewRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startFromNewRadioButtonActionPerformed(evt);
            }
        });
        jPanel2.add(startFromNewRadioButton);

        newOrExistingButtonGroup.add(startFromExistingRadioButton);
        startFromExistingRadioButton.setText("Import From Existing Facility");
        startFromExistingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startFromExistingRadioButtonActionPerformed(evt);
            }
        });
        jPanel2.add(startFromExistingRadioButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(existingFacilityPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newFacilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newFacilityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(existingFacilityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewButtonActionPerformed
        this.returnStatus = RETURN_STATUS_NEW_SEED;
        doClose();
    }//GEN-LAST:event_createNewButtonActionPerformed

    private void newProjectNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_newProjectNameTextFieldFocusGained
        seedFileChooser.setSelectedFile(null);
        this.getRootPane().setDefaultButton(createNewButton);
    }//GEN-LAST:event_newProjectNameTextFieldFocusGained

    private void seedFileChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_seedFileChooserPropertyChange
        this.getRootPane().setDefaultButton(seedFileChooserOkButton);
    }//GEN-LAST:event_seedFileChooserPropertyChange

    private void useFacilityGeometryCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useFacilityGeometryCheckBoxActionPerformed
        useFacilityGeometryOnly = true;
    }//GEN-LAST:event_useFacilityGeometryCheckBoxActionPerformed

    private void useFacilityDemandsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useFacilityDemandsCheckBoxActionPerformed
        useFacilityGeometryOnly = false;
    }//GEN-LAST:event_useFacilityDemandsCheckBoxActionPerformed

    private void startFromNewRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startFromNewRadioButtonActionPerformed
        // Disable Existing Facility Options
        existingFacilityPanel.setEnabled(false);
        useGeometryPanel.setEnabled(false);
        useFacilityGeometryCheckBox.setEnabled(false);
        useFacilityDemandsCheckBox.setEnabled(false);
        seedFileChooser.setEnabled(false);
        seedFileChooser.setVisible(false);
        seedFileChooserOkButton.setEnabled(false);
        switch (TYPE) {
            case MainWindow.TOOLBOX_DSS:
                this.setSize(653, 175);
                break;
            case MainWindow.TOOLBOX_WZ:
                this.setSize(653, 200);
        }
        // Enable New Facility Options
        newFacilityPanel.setEnabled(true);
        jLabel1.setEnabled(true);
        newProjectNameTextField.setEnabled(true);
        createNewButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }//GEN-LAST:event_startFromNewRadioButtonActionPerformed

    private void startFromExistingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startFromExistingRadioButtonActionPerformed
        // Enable Existing Facility Options
        existingFacilityPanel.setEnabled(true);
        useGeometryPanel.setEnabled(true);
        useFacilityGeometryCheckBox.setEnabled(true);
        useFacilityDemandsCheckBox.setEnabled(true);
        switch (TYPE) {
            case MainWindow.TOOLBOX_DSS:
                this.setSize(653, 531);
                break;
            case MainWindow.TOOLBOX_WZ:
                this.setSize(653, 565);
        }
        seedFileChooser.setEnabled(true);
        seedFileChooser.setVisible(true);
        seedFileChooserOkButton.setEnabled(true);

        // Disable New Facility Options
        newFacilityPanel.setEnabled(false);
        jLabel1.setEnabled(false);
        newProjectNameTextField.setEnabled(false);
        createNewButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }//GEN-LAST:event_startFromExistingRadioButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.returnStatus = RETURN_STATUS_CANCEL;
        doClose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton createNewButton;
    private javax.swing.JPanel existingFacilityPanel;
    private javax.swing.ButtonGroup facilityGeometryButtonGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel newFacilityPanel;
    private javax.swing.ButtonGroup newOrExistingButtonGroup;
    private javax.swing.JTextField newProjectNameTextField;
    private javax.swing.JFileChooser seedFileChooser;
    private javax.swing.JRadioButton startFromExistingRadioButton;
    private javax.swing.JRadioButton startFromNewRadioButton;
    private javax.swing.JCheckBox useFacilityDemandsCheckBox;
    private javax.swing.JCheckBox useFacilityGeometryCheckBox;
    private javax.swing.JPanel useGeometryPanel;
    // End of variables declaration//GEN-END:variables

    private class Task extends SwingWorker<Void, Void> {
        /*
         * Main intialization, performed in background
         */

        private final java.awt.Frame parent;

        public Task(java.awt.Frame parent) {
            this.parent = parent;
        }

        @Override
        public Void doInBackground() {
            //launchDlg = new JDialog(parent, "Please Wait", false);
            //launchDlg.setLayout(new GridLayout(2, 1));
            //launchDlg.setSize(250, 100);
            //JLabel launchLabel = new JLabel("Loading Components and Defaults....");
            //launchDlg.add(launchLabel);
            //JProgressBar pb = new JProgressBar();
            //pb.setIndeterminate(true);
            //launchDlg.add(pb);
            launchDlg.setLocationRelativeTo(null);
            launchDlg.setVisible(true);
            return null;
        }

        @Override
        public void done() {
            //launchDlg.setVisible(false);
            //launchDlg.dispose();
        }
    }

}
