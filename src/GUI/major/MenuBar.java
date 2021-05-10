package GUI.major;

import GUI.seedEditAndIOHelper.TruckPCEGuidanceDialog;
import com.sun.glass.events.KeyEvent;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import main.FREEVAL_HCM;

/**
 * This class is the menu bar in main window. Most of the methods provide a link
 * to call methods in mainWindow instead of containing actual codes.
 *
 * @author Shu Liu
 */
public class MenuBar extends JMenuBar {

    private MainWindow mainWindow;

    /**
     * Constructor
     */
    public MenuBar() {
        super();

        // <editor-fold defaultstate="collapsed" desc="create File menu">
        fileMenu = new JMenu("Project");
        add(fileMenu);

        newMenuItem = new JMenuItem("New Project");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.newSeed();
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem = new JMenuItem("Open Project");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.openSeed();
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem = new JMenuItem("Save Project");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.saveSeed();
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem = new JMenuItem("Save Project As...");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.saveAsSeed();
            }
        });
        fileMenu.add(saveAsMenuItem);

        closeMenuItem = new JMenuItem("Close Project");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.closeActiveProject(true);
            }
        });
        fileMenu.add(closeMenuItem);

        fileMenu.add(new javax.swing.JSeparator());

        importASCIIMenuItem = new JMenuItem("Import Seed from ASCII File");
        importASCIIMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.importASCII();
            }
        });
        fileMenu.add(importASCIIMenuItem);

        exportASCIIMenuItem = new JMenuItem("Export Seed to ASCII File");
        exportASCIIMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.exportASCII();
            }
        });
        fileMenu.add(exportASCIIMenuItem);
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                configureFileMenu();
            }
        });
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="create Analyze menu">
        analyzeMenu = new JMenu("Analyze");
        add(analyzeMenu);

        toggleMLMenuItem = new JMenuItem("Turn On Managed Lanes");
        toggleMLMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.toggleManagedLane();
            }
        });
        analyzeMenu.add(toggleMLMenuItem);

        analyzeMenu.add(new javax.swing.JSeparator());

        geneScenMenuItem = new JMenuItem("Generate Reliability Scenarios");
        geneScenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.generateRL();
            }
        });
        analyzeMenu.add(geneScenMenuItem);

        deleteScenMenuItem = new JMenuItem("Delete All Reliability Scenarios");
        deleteScenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.deleteAllScen();
            }
        });
        analyzeMenu.add(deleteScenMenuItem);

        showRLSummaryMenuItem = new JMenuItem("Show Reliability Summary");
        showRLSummaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showRLSummary();
            }
        });
        analyzeMenu.add(showRLSummaryMenuItem);

        analyzeMenu.add(new javax.swing.JSeparator());

        assignATDMItem = new JMenuItem("Assign ATDM Plans");
        assignATDMItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.generateATDM();
            }
        });
        analyzeMenu.add(assignATDMItem);

        deleteATDMItem = new JMenuItem("Delete ATDM Sets");
        deleteATDMItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.deleteAllATDM();
            }
        });
        analyzeMenu.add(deleteATDMItem);

        showATDMSummaryItem = new JMenuItem("Show ATDM Summary");
        showATDMSummaryItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showATDMSummary();
            }
        });
        analyzeMenu.add(showATDMSummaryItem);

        printInterValues = new JMenuItem("Print Intermediate Values");
        printInterValues.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                JFileChooser fc = new JFileChooser();
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getAbsolutePath().endsWith(".csv");
                    }

                    @Override
                    public String getDescription() {
                        return "Comma Separated Value files (*.csv)";
                    }
                });
                int success = fc.showSaveDialog(mainWindow);
                if (success == JFileChooser.APPROVE_OPTION) {
                    mainWindow.getActiveSeed().printDiss2 = true;
                    mainWindow.getActiveSeed().printDissOutputFileName2 = fc.getSelectedFile().getAbsolutePath();
                    if (mainWindow.getActiveSeed().printDissOutputFileName2.endsWith(".csv") == false) {
                        mainWindow.getActiveSeed().printDissOutputFileName2 = mainWindow.getActiveSeed().printDissOutputFileName2 + ".csv";
                    }
                    mainWindow.getActiveSeed().enableForceOversat(true);
                    mainWindow.getActiveSeed().singleRun(0, -1);
                    JOptionPane.showMessageDialog(mainWindow, "Finished");
                    mainWindow.getActiveSeed().enableForceOversat(false);
                    mainWindow.getActiveSeed().printDiss2 = false;
                }
            }
        });
        if (false) {
            analyzeMenu.addSeparator();
            analyzeMenu.add(printInterValues);
        }

        analyzeMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                switch (mainWindow.getCurrentToolboxId()) {
                    case MainWindow.TOOLBOX_RL_ATDM:
                        toggleMLMenuItem.setEnabled(true);
                        geneScenMenuItem.setEnabled(true);
                        deleteScenMenuItem.setEnabled(true);
                        showRLSummaryMenuItem.setEnabled(true);
                        assignATDMItem.setEnabled(true);
                        deleteATDMItem.setEnabled(true);
                        showATDMSummaryItem.setEnabled(true);
                        break;
                    case MainWindow.TOOLBOX_WZ:
                        toggleMLMenuItem.setEnabled(false);
                        geneScenMenuItem.setEnabled(false);
                        deleteScenMenuItem.setEnabled(false);
                        showRLSummaryMenuItem.setEnabled(false);
                        assignATDMItem.setEnabled(false);
                        deleteATDMItem.setEnabled(false);
                        showATDMSummaryItem.setEnabled(false);
                        break;
                    case MainWindow.TOOLBOX_DSS:
                        geneScenMenuItem.setEnabled(false);
                        deleteScenMenuItem.setEnabled(false);
                        showRLSummaryMenuItem.setEnabled(false);
                        assignATDMItem.setEnabled(false);
                        deleteATDMItem.setEnabled(false);
                        showATDMSummaryItem.setEnabled(false);
                        break;
                }
            }
        });
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="create Edit menu">
        editMenu = new JMenu("Edit");
        add(editMenu);

        globalInputMenuItem = new JMenuItem("Global Input");
        globalInputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.globalInput();
            }
        });
        editMenu.add(globalInputMenuItem);

        adaptiveRampMeteringMenuItem = new JMenuItem("Adaptive Ramp Metering");
        adaptiveRampMeteringMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.editAdaptiveRampMetering();
            }
        });
        if (FREEVAL_HCM.getAdaptiveRampMeteringAvailable()) {
            editMenu.add(adaptiveRampMeteringMenuItem);
        }

        fillDataMenuItem = new JMenuItem("Fill Data");
        fillDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.getCurrentToolbox().fillData();
            }
        });
        editMenu.add(fillDataMenuItem);

        editMenu.add(new javax.swing.JSeparator());

        insertSegMenuItem = new JMenuItem("Add Segment");
        insertSegMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.addSegment();
            }
        });
        editMenu.add(insertSegMenuItem);

        deleteSegMenuItem = new JMenuItem("Delete Segment");
        deleteSegMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.delSegment();
            }
        });
        editMenu.add(deleteSegMenuItem);

        editMenu.add(new javax.swing.JSeparator());

        insertPeriodMenuItem = new JMenuItem("Add Analysis Period");
        insertPeriodMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.addPeriod();
            }
        });
        editMenu.add(insertPeriodMenuItem);

        deletePeriodMenuItem = new JMenuItem("Delete Analysis Period");
        deletePeriodMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.delPeriod();
            }
        });
        editMenu.add(deletePeriodMenuItem);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="create View menu">
        viewMenu = new JMenu("View");
        add(viewMenu);

        singleScenIOMenu = new JMenu("Single Seed/Scenario Input/Output");
        viewMenu.add(singleScenIOMenu);

        showInputMenuItem = new JMenuItem("Show Input");
        showInputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showInput();
            }
        });
        singleScenIOMenu.add(showInputMenuItem);

        showOutputMenuItem = new JMenuItem("Show Output");
        showOutputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showOutput();
            }
        });
        singleScenIOMenu.add(showOutputMenuItem);

        showInputAndOutputMenuItem = new JMenuItem("Show input & Output");
        showInputAndOutputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showInputAndOutput();
            }
        });
        singleScenIOMenu.add(showInputAndOutputMenuItem);

        singleScenIOMenu.add(new javax.swing.JSeparator());

        showGPOnlyMenuItem = new JMenuItem("Show GP in Table Only");
        showGPOnlyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showGPOnly();
            }
        });
        singleScenIOMenu.add(showGPOnlyMenuItem);

        showMLOnlyMenuItem = new JMenuItem("Show ML in Table Only");
        showMLOnlyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showMLOnly();
            }
        });
        singleScenIOMenu.add(showMLOnlyMenuItem);

        showGPMLMenuItem = new JMenuItem("Show GP & ML in Table");
        showGPMLMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showGPML();
            }
        });
        singleScenIOMenu.add(showGPMLMenuItem);

        showComputedDownstreamValuesMenuItem = new JCheckBoxMenuItem("Show Computed Downstream Values");
        showComputedDownstreamValuesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.toggleShowComputedDownstreamValues(showComputedDownstreamValuesMenuItem.isSelected());
            }
        });
        singleScenIOMenu.add(new javax.swing.JSeparator());
        singleScenIOMenu.add(showComputedDownstreamValuesMenuItem);

        viewMenu.add(new javax.swing.JSeparator());

        firstAPMenuItem = new JMenuItem("Show First Analysis Period");
        firstAPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showFirstPeriod();
            }
        });
        viewMenu.add(firstAPMenuItem);

        previousAPMenuItem = new JMenuItem("Show Previous Analysis Period");
        previousAPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showPrevPeriod();
            }
        });
        viewMenu.add(previousAPMenuItem);

        nextAPMenuItem = new JMenuItem("Show Next Analysis Period");
        nextAPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showNextPeriod();
            }
        });
        viewMenu.add(nextAPMenuItem);

        lastAPMenuItem = new JMenuItem("Show Last Analysis Period");
        lastAPMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showLastPeriod();
            }
        });
        viewMenu.add(lastAPMenuItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="create Settings menu">
        settingMenu = new JMenu("Settings");
        add(settingMenu);

        tableSettingMenuItem = new JMenuItem("Table Display Settings");
        tableSettingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showTableSettings();
            }
        });
        settingMenu.add(tableSettingMenuItem);

        graphicSettingMenuItem = new JMenuItem("Graphic Display Settings");
        graphicSettingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showGraphicSettings();
            }
        });
        settingMenu.add(graphicSettingMenuItem);

        settingMenu.add(new javax.swing.JSeparator());

        createFloatingWindowItem = new JMenuItem("Create Floating Window");
        createFloatingWindowItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.createFloatingWindow();
            }
        });
        settingMenu.add(createFloatingWindowItem);
        settingMenu.addSeparator();
        showDebugOutputMenuItem = new JCheckBoxMenuItem("Print Density Debug Output in Log");
        showDebugOutputMenuItem.setSelected(true);
        showDebugOutputMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.setShowDebugOutput(showDebugOutputMenuItem.isSelected());
            }
        });
        settingMenu.add(showDebugOutputMenuItem);
        // </editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Toolbox Menu">
        toolboxMenu = new JMenu("Toolbox");
        add(toolboxMenu);

        coreToolboxMenuItem = new JCheckBoxMenuItem("Core RL/ATDM Toolbox");
        coreToolboxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                if (coreToolboxMenuItem.isSelected()) {
                    if (!MainWindow.TOOLBOX_RL_ATDM.equalsIgnoreCase(mainWindow.getCurrentToolboxId())) {
                        selectToolbox(MainWindow.TOOLBOX_RL_ATDM);
                        //Toolbox toolbox = new ToolboxFREEVAL();
                        //toolbox.setMainWindow(mainWindow);
                        //mainWindow.toggleToolbox(MainWindow.TOOLBOX_RL_ATDM, toolbox);
                        //updateAllMenus();
                    }
                }
            }
        });
        coreToolboxMenuItem.setSelected(true);
        toolboxMenu.add(coreToolboxMenuItem);

        toolBoxButtonGroup = new ButtonGroup();
        toolBoxButtonGroup.add(coreToolboxMenuItem);
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="create Help menu">
        helpMenu = new JMenu("Help");
        add(helpMenu);

        hcmTableMenuItem = new JMenuItem("HCM Truck-PC Equvalent Tables");
        hcmTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                TruckPCEGuidanceDialog truckPCEGuidanceDialog = new TruckPCEGuidanceDialog(null, true);
                truckPCEGuidanceDialog.setLocationRelativeTo(mainWindow);
                truckPCEGuidanceDialog.setVisible(true);
            }
        });
        helpMenu.add(hcmTableMenuItem);
        helpMenu.add(new javax.swing.JSeparator());

        helpDocumentItem = new JMenuItem("Source Code Documentation");
        helpDocumentItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showJavaDoc();
            }
        });
        helpMenu.add(helpDocumentItem);

        helpMenu.add(new javax.swing.JSeparator());
        aboutMenuItem = new JMenuItem("About & Contact");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                mainWindow.showAbout();
            }
        });
        helpMenu.add(aboutMenuItem);

        fuzzyRMDebugMenuItem = new JMenuItem("Print Fuzzy RM Debug Output");
        fuzzyRMDebugMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public final void actionPerformed(java.awt.event.ActionEvent evt) {
                if (mainWindow.getActiveSeed() != null) {
                    JFileChooser fileChooser = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
                    int option = fileChooser.showSaveDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        mainWindow.getActiveSeed().fuzzyRMDebugOutputFileName = fileChooser.getSelectedFile().getAbsolutePath();
                        try {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(mainWindow.getActiveSeed().fuzzyRMDebugOutputFileName));
                            bw.write("scen,seg,period,step,DStream_SPd,UStream_SPD,DStream_density,UStream_Density,Dstream_Occupancy,Ustream_Occupancy,ONR_Queue_Length,Output_area_0,Output_area_1,Output_area_2,Output_area_3,Output_area_4,Output_centroid_0,Output_centroid_1,Output_centroid_2,Output_centroid_3,Output_centroid_4,Ustream_OCC_membership_0,Ustream_OCC_membership_1,Ustream_OCC_membership_2,Ustream_OCC_membership_3,Ustream_OCC_membership_4,Dstream_OCC_membership,Ustream_SPD_membership_0,Ustream_SPD_membership_1,Ustream_SPD_membership_2,Ustream_SPD_membership_3,Ustream_SPD_membership_4,Dstream_SPD_membership,Degree_of_activation_0,Degree_of_activation_1,Degree_of_activation_2,Degree_of_activation_3,Degree_of_activation_4,Degree_of_activation_5,Degree_of_activation_6,Degree_of_activation_7,Degree_of_activation_8,Degree_of_activation_9,Degree_of_activation_10,numerator_1,numerator_2,numerator_3,denominator_1,denominator_2,denominator_3,ramp_metering\n");
                            bw.close();
                            bw = new BufferedWriter(new FileWriter(mainWindow.getActiveSeed().fuzzyRMDebugOutputFileName.replace(".csv", "_cand.csv")));
                            bw.write("scen,seg,period,step,cand_station, cand_station_density, critical_density\n");
                            bw.close();
                        } catch (IOException e) {

                        }
                        mainWindow.getActiveSeed().printRM = true;
                        mainWindow.getActiveSeed().enableForceOversat(true);
                        mainWindow.getActiveSeed().singleRun(mainWindow.getActiveScen(), -1);
                        mainWindow.getActiveSeed().printRM = false;
                        mainWindow.getActiveSeed().enableForceOversat(false);
                        JOptionPane.showMessageDialog(null, "Files Written", "Task Completed", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No Seed Selected.", "Error: No Active Seed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        helpMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                // Do nothing
            }

            @Override
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                configureHelpMenu();
            }
        });
        // </editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Adding Keyboard Shortcuts (Ignored for Beta/HCM)">
        // File Menu Items
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        importASCIIMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        exportASCIIMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));

        // Analyze Menu Items
        geneScenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        assignATDMItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        // Edit Menu Items
        fillDataMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        // View Menu Items
        showInputMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
        showOutputMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        showInputAndOutputMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
        showComputedDownstreamValuesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        nextAPMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
        previousAPMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        // Toolbox Menu Items
        // Help Menu Items
//</editor-fold>

        turnOffML();
        disableATDM();
    }

    /**
     * Enable show output option
     */
    public final void enableOutput() {
        showOutputMenuItem.setEnabled(true);
    }

    /**
     * Disable show output option
     */
    public final void disableOutput() {
        showOutputMenuItem.setEnabled(false);
    }

    /**
     * Enable reliability analysis options
     */
    public final void enableRL() {
        geneScenMenuItem.setEnabled(true);
        deleteScenMenuItem.setEnabled(true);
        showRLSummaryMenuItem.setEnabled(true);
    }

    /**
     * Disable reliability analysis options
     */
    public final void disableRL() {
        geneScenMenuItem.setEnabled(false);
        deleteScenMenuItem.setEnabled(false);
        showRLSummaryMenuItem.setEnabled(false);
    }

    /**
     * Enable ATDM analysis options
     */
    public final void enableATDM() {
        assignATDMItem.setEnabled(true);
        showATDMSummaryItem.setEnabled(true);
        deleteATDMItem.setEnabled(true);
    }

    /**
     * Disable ATDM analysis options
     */
    public final void disableATDM() {
        assignATDMItem.setEnabled(false);
        showATDMSummaryItem.setEnabled(false);
        deleteATDMItem.setEnabled(false);
    }

    /**
     * Turn on managed lanes options
     */
    public final void turnOnML() {
        showMLOnlyMenuItem.setEnabled(true);
        showGPMLMenuItem.setEnabled(true);
        toggleMLMenuItem.setText("Turn Off Managed Lane");
    }

    /**
     * Turn off managed lanes options
     */
    public final void turnOffML() {
        showMLOnlyMenuItem.setEnabled(false);
        showGPMLMenuItem.setEnabled(false);
        toggleMLMenuItem.setText("Turn On Managed Lane");
    }

    /**
     * Enable managed lanes options
     */
    public void enableML() {
        toggleMLMenuItem.setEnabled(true);
    }

    /**
     * Disable managed lanes options
     */
    public void disableML() {
        toggleMLMenuItem.setEnabled(false);
    }

    /**
     * Configure display when the seed is null
     */
    public final void setNullSeed() {
        analyzeMenu.setEnabled(false);
        editMenu.setEnabled(false);
        viewMenu.setEnabled(false);
    }

    /**
     * Configure display when the seed is not null
     */
    public final void setNonNullSeed() {
        analyzeMenu.setEnabled(true);
        editMenu.setEnabled(true);
        viewMenu.setEnabled(true);
    }

    /**
     * Configure the options in the File menu based on the Current toolbox
     *
     */
    private void configureFileMenu() {
        switch (mainWindow.getCurrentToolboxId()) {
            default:
            case MainWindow.TOOLBOX_RL_ATDM:
                newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
                openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
                saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
                saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
                closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
                newMenuItem.setEnabled(true);
                openMenuItem.setEnabled(true);
                saveAsMenuItem.setEnabled(true);
                saveMenuItem.setEnabled(true);
                closeMenuItem.setEnabled(true);
                importASCIIMenuItem.setEnabled(true);
                exportASCIIMenuItem.setEnabled(true);
                fileMenu.removeAll();
                fileMenu.add(newMenuItem);
                fileMenu.add(openMenuItem);
                fileMenu.add(saveMenuItem);
                fileMenu.add(saveAsMenuItem);
                fileMenu.add(closeMenuItem);
                if (mainWindow.getActiveSeed() == null) {
                    saveAsMenuItem.setEnabled(false);
                    saveMenuItem.setEnabled(false);
                    closeMenuItem.setEnabled(false);
                }
                fileMenu.add(new javax.swing.JSeparator());
                fileMenu.add(importASCIIMenuItem);
                fileMenu.add(exportASCIIMenuItem);
                break;
        }
        this.revalidate();
    }

    private void configureHelpMenu() {

    }

    public void removeToolboxMenu(String toolboxName) {
        switch (toolboxName) {
            case MainWindow.TOOLBOX_RL_ATDM:
                break;
            default:
                //Do Nothing
                break;
        }
        this.revalidate();
    }

    public void returnToCoreToolbox() {
        coreToolboxMenuItem.setSelected(true);
        coreToolboxMenuItem.getActionListeners()[0].actionPerformed(null);
    }

    private void updateAllMenus() {
        configureFileMenu();
        configureHelpMenu();
        switch (mainWindow.getCurrentToolboxId()) {
            case MainWindow.TOOLBOX_RL_ATDM:
                toggleMLMenuItem.setEnabled(true);
                geneScenMenuItem.setEnabled(true);
                deleteScenMenuItem.setEnabled(true);
                showRLSummaryMenuItem.setEnabled(true);
                assignATDMItem.setEnabled(true);
                deleteATDMItem.setEnabled(true);
                showATDMSummaryItem.setEnabled(true);
                break;
            case MainWindow.TOOLBOX_WZ:
                toggleMLMenuItem.setEnabled(false);
                geneScenMenuItem.setEnabled(false);
                deleteScenMenuItem.setEnabled(false);
                showRLSummaryMenuItem.setEnabled(false);
                assignATDMItem.setEnabled(false);
                deleteATDMItem.setEnabled(false);
                showATDMSummaryItem.setEnabled(false);
                break;
            case MainWindow.TOOLBOX_DSS:
                geneScenMenuItem.setEnabled(false);
                deleteScenMenuItem.setEnabled(false);
                showRLSummaryMenuItem.setEnabled(false);
                assignATDMItem.setEnabled(false);
                deleteATDMItem.setEnabled(false);
                showATDMSummaryItem.setEnabled(false);
                break;
        }
    }

    public void selectToolbox(String toolboxID) {
        switch (toolboxID) {
            default:
            case MainWindow.TOOLBOX_RL_ATDM:
                Toolbox toolbox = new ToolboxFREEVAL();
                toolbox.setMainWindow(mainWindow);
                mainWindow.toggleToolbox(MainWindow.TOOLBOX_RL_ATDM, toolbox);
                updateAllMenus();
                coreToolboxMenuItem.setSelected(true);
                List<Image> icons = new ArrayList<>();
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo.png"))).getImage());
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo16.png"))).getImage());
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo32.png"))).getImage());
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo64.png"))).getImage());
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo128.png"))).getImage());
                icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo256.png"))).getImage());
                mainWindow.setIconImages(icons);
                break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="setter and getters">
    /**
     * Setter for mainWindow connection
     *
     * @param mainWindow main window instance
     */
    public final void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
    // </editor-fold>

    private final JMenu fileMenu,
            analyzeMenu,
            editMenu,
            viewMenu,
            settingMenu,
            helpMenu,
            singleScenIOMenu;

    private final JMenuItem newMenuItem,
            openMenuItem,
            saveMenuItem,
            saveAsMenuItem,
            closeMenuItem,
            importASCIIMenuItem,
            exportASCIIMenuItem;

    private final JMenuItem toggleMLMenuItem,
            geneScenMenuItem,
            deleteScenMenuItem, showRLSummaryMenuItem;
    private final JMenuItem assignATDMItem,
            deleteATDMItem,
            showATDMSummaryItem;

    private final JMenuItem printInterValues;

    private final JMenuItem globalInputMenuItem,
            adaptiveRampMeteringMenuItem,
            fillDataMenuItem;

    private final JMenuItem insertSegMenuItem,
            deleteSegMenuItem,
            insertPeriodMenuItem,
            deletePeriodMenuItem;

    private final JMenuItem showInputMenuItem,
            showOutputMenuItem,
            showInputAndOutputMenuItem,
            showGPOnlyMenuItem,
            showMLOnlyMenuItem,
            showGPMLMenuItem;
    private final JCheckBoxMenuItem showComputedDownstreamValuesMenuItem;

    private final JMenuItem firstAPMenuItem,
            previousAPMenuItem,
            nextAPMenuItem,
            lastAPMenuItem;
    private final JCheckBoxMenuItem showDebugOutputMenuItem;

    private final JMenuItem tableSettingMenuItem,
            graphicSettingMenuItem,
            createFloatingWindowItem;

    private final JMenuItem hcmTableMenuItem, helpDocumentItem,
            aboutMenuItem, fuzzyRMDebugMenuItem;

    private final ButtonGroup toolBoxButtonGroup;

    private final JMenu toolboxMenu;
    private final JCheckBoxMenuItem coreToolboxMenuItem;
}
