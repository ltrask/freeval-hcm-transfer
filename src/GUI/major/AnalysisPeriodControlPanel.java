/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major;

import static GUI.major.MainWindow.printLog;
import coreEngine.Helper.CEConst;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author jltrask
 */
public class AnalysisPeriodControlPanel extends JPanel {

    private MainWindow mainWindow;

    private final JPanel tableDisplayOptionPanel, APPanel;

    private final JScrollPane jScrollPane1;

    private final JComboBox inOutCB, GPMLCB;

    private final JLabel periodLabel, timeLabel;

    private final JButton firstButton, previousButton, nextButton, lastButton,
            jumpToButton;

    private final JTextField jumpText;

    private static final DefaultComboBoxModel GPML_MODEL = new DefaultComboBoxModel(new String[]{"GP Only", "ML Only", "GP & ML"});

    private static final DefaultComboBoxModel GP_ONLY_MODEL = new DefaultComboBoxModel(new String[]{"GP Only"});

    private static final DefaultComboBoxModel INPUT_OUTPUT_MODEL = new DefaultComboBoxModel(new String[]{"Input", "Output", "In & Out"});

    private static final DefaultComboBoxModel INPUT_ONLY_MODEL = new DefaultComboBoxModel(new String[]{"Input"});

    public AnalysisPeriodControlPanel() {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Creating components
        tableDisplayOptionPanel = new javax.swing.JPanel();
        inOutCB = new javax.swing.JComboBox();
        GPMLCB = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        APPanel = new javax.swing.JPanel();
        periodLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        firstButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        lastButton = new javax.swing.JButton();
        jumpToButton = new javax.swing.JButton();
        jumpText = new javax.swing.JTextField();

        tableDisplayOptionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Display Options"));
        tableDisplayOptionPanel.setLayout(new java.awt.GridLayout(1, 2));

        inOutCB.setBackground(new java.awt.Color(255, 255, 153));
        inOutCB.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Input"}));
        inOutCB.setToolTipText("Switch between input and output tables");
        inOutCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inOutCBActionPerformed(evt);
            }
        });
        tableDisplayOptionPanel.add(inOutCB);

        GPMLCB.setBackground(new java.awt.Color(255, 255, 153));
        GPMLCB.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"GP Only"}));
        GPMLCB.setToolTipText("Switch between general purpose (GP) and managed lanes (ML) tables");
        GPMLCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GPMLCBActionPerformed(evt);
            }
        });
        tableDisplayOptionPanel.add(GPMLCB);

        jScrollPane1.setBorder(null);

        APPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Analysis Period (A.P.) Control"));
        //APPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        //APPanel.setPreferredSize(new java.awt.Dimension(596, 60));
        APPanel.setLayout(new java.awt.GridLayout(1, 7, 3, 1));

        periodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        periodLabel.setText("A.P.");
        APPanel.add(periodLabel);

        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel.setText("-");
        timeLabel.setToolTipText("Analysis Period Time (HH:MM)");
        APPanel.add(timeLabel);

        firstButton.setText("First");
        firstButton.setToolTipText("Go to the first analysis period");
        firstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstButtonActionPerformed(evt);
            }
        });
        APPanel.add(firstButton);

        previousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/iconHelper/Back24.gif"))); // NOI18N
        previousButton.setToolTipText("Go to previous analysis period");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        APPanel.add(previousButton);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/iconHelper/Forward24.gif"))); // NOI18N
        nextButton.setToolTipText("Go to next analysis period");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        APPanel.add(nextButton);

        lastButton.setText("Last");
        lastButton.setToolTipText("GO to the last analysis period");
        lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastButtonActionPerformed(evt);
            }
        });
        APPanel.add(lastButton);

        jumpToButton.setText("Jump To");
        jumpToButton.setToolTipText("Jump to a specified analysis period");
        jumpToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jumpToButtonActionPerformed(evt);
            }
        });
        APPanel.add(jumpToButton);

        jumpText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jumpText.setToolTipText("Enter the specified analysis period index");
        jumpText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jumpTextKeyPressed(evt);
            }
        });
        APPanel.add(jumpText);

        jScrollPane1.setViewportView(APPanel);
        jScrollPane1.setPreferredSize(new Dimension(955, 60));

        this.add(tableDisplayOptionPanel);
        this.add(APPanel);
        this.setPreferredSize(new Dimension(955, 60));
        //this.add(tableDisplayOptionPanel);
        //this.add(APPanel);

    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void setNonNullSeed() {
        APPanel.setEnabled(true);
        firstButton.setEnabled(true);
        jumpText.setEnabled(true);
        jumpToButton.setEnabled(true);
        lastButton.setEnabled(true);
        nextButton.setEnabled(true);
        periodLabel.setEnabled(true);
        timeLabel.setEnabled(true);
        previousButton.setEnabled(true);
        tableDisplayOptionPanel.setEnabled(true);
        inOutCB.setEnabled(true);
        GPMLCB.setEnabled(true);
    }

    public void setNullSeed() {
        APPanel.setEnabled(false);
        firstButton.setEnabled(false);
        jumpText.setEnabled(false);
        jumpToButton.setEnabled(false);
        lastButton.setEnabled(false);
        nextButton.setEnabled(false);
        periodLabel.setEnabled(false);
        timeLabel.setEnabled(false);
        previousButton.setEnabled(false);
        tableDisplayOptionPanel.setEnabled(false);
        inOutCB.setEnabled(false);
        GPMLCB.setEnabled(false);
    }

    private void inOutCBActionPerformed(java.awt.event.ActionEvent evt) {
        switch (inOutCB.getSelectedIndex()) {
            case 0:
                mainWindow.showInput();
                break;
            case 1:
                mainWindow.showOutput();
                break;
            case 2:
                mainWindow.showInputAndOutput();
                break;
        }
    }

    private void GPMLCBActionPerformed(java.awt.event.ActionEvent evt) {
        switch (GPMLCB.getSelectedIndex()) {
            case 0:
                mainWindow.showGPOnly();
                break;
            case 1:
                mainWindow.showMLOnly();
                break;
            case 2:
                mainWindow.showGPML();
                break;
        }
    }

    private void firstButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainWindow.showFirstPeriod();
    }

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainWindow.showPrevPeriod();
    }

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainWindow.showNextPeriod();
    }

    private void lastButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainWindow.showLastPeriod();
    }

    private void jumpToButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            mainWindow.selectPeriod(Integer.parseInt(jumpText.getText()) - 1);
            jumpText.setText("");
        } catch (Exception e) {
            printLog("Invalid period");
            jumpText.setText("");
        }
    }

    private void jumpTextKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jumpToButtonActionPerformed(null);
        }
    }

    public void setIsShowingInput() {
        inOutCB.setSelectedIndex(0);
    }

    public void setIsShowingOutput() {
        inOutCB.setSelectedIndex(1);
    }

    public void setIsShowingInputAndOutput() {
        inOutCB.setSelectedIndex(2);
    }

    public void enableOutput() {
        if (!inOutCB.getModel().equals(INPUT_OUTPUT_MODEL)) {
            inOutCB.setModel(INPUT_OUTPUT_MODEL);
        }
    }

    public void disableOutput() {
        if (!inOutCB.getModel().equals(INPUT_ONLY_MODEL)) {
            inOutCB.setModel(INPUT_ONLY_MODEL);
        }
    }

    public void enableManagedLane() {
        if (!GPMLCB.getModel().equals(GPML_MODEL)) {
            GPMLCB.setModel(GPML_MODEL);
        }
    }

    public void disableManagedLane() {
        if (!GPMLCB.getModel().equals(GP_ONLY_MODEL)) {
            GPMLCB.setModel(GP_ONLY_MODEL);
        }
    }

    public void selectPeriod(int period) {
        if (period < 0) {
            periodLabel.setText("A.P.");
            timeLabel.setText("-");
        } else {
            periodLabel.setText("A.P. " + (period + 1) + "/" + mainWindow.getActiveSeed().getValueInt(CEConst.IDS_NUM_PERIOD));
            timeLabel.setText(mainWindow.getActiveSeed().getValueString(CEConst.IDS_PERIOD_TIME, 0, period));
        }
    }

    public int getGPMLCBSelectedIndex() {
        return GPMLCB.getSelectedIndex();
    }

}
