package GUI.ATDMHelper.IO;

import GUI.ATDMHelper.TableModels.ATDMModel;
import GUI.RLHelper.summary.ScenarioSummaryTableModel;
import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author Lake Trask
 */
public class ScenarioFilterDialog extends javax.swing.JDialog {

    private boolean filterOptionsSet;

    /**
     * Creates new form ScenarioFilterDialog
     *
     * @param atdmModel
     * @param parent
     * @param modal
     */
    public ScenarioFilterDialog(ATDMModel atdmModel, java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initializeDialog();

        DecimalFormat formatter = new DecimalFormat("###,###.##");

        demandMultRangeLabel.setText("(" + formatter.format(atdmModel.getMinDemandMult()) + " - " + formatter.format(atdmModel.getMaxDemandMult()) + ")");
        numWZRangeLabel.setText("(" + formatter.format(atdmModel.getMinNumWZ()) + " - " + formatter.format(atdmModel.getMaxNumWZ()) + ")");
        numIncidentsRangeLabel.setText("(" + formatter.format(atdmModel.getMinNumIncident()) + " - " + formatter.format(atdmModel.getMaxNumIncident()) + ")");
        numWeatherRangeLabel.setText("(" + formatter.format(atdmModel.getMinNumWeather()) + " - " + formatter.format(atdmModel.getMaxNumWeather()) + ")");
        maxTTIRangeLabel.setText("(" + formatter.format(atdmModel.getMinTTI()) + " - " + formatter.format(atdmModel.getMaxTTI()) + ")");
        maxDelayRangeLabel.setText("(" + formatter.format(atdmModel.getMinDelay()) + " - " + formatter.format(atdmModel.getMaxDelay()) + ")");

        filterOptionsSet = false;
    }

    /**
     * Creates new form ScenarioFilterDialog
     *
     * @param scenSummaryModel
     * @param parent
     * @param modal
     */
    public ScenarioFilterDialog(ScenarioSummaryTableModel scenSummaryModel, java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initializeDialog();

        DecimalFormat formatter = new DecimalFormat("###,###.##");

        demandMultRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinDemandMult()) + " - " + formatter.format(scenSummaryModel.getMaxDemandMult()) + ")");
        numWZRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinNumWZ()) + " - " + formatter.format(scenSummaryModel.getMaxNumWZ()) + ")");
        numIncidentsRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinNumIncident()) + " - " + formatter.format(scenSummaryModel.getMaxNumIncident()) + ")");
        numWeatherRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinNumWeather()) + " - " + formatter.format(scenSummaryModel.getMaxNumWeather()) + ")");
        maxTTIRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinTTI()) + " - " + formatter.format(scenSummaryModel.getMaxTTI()) + ")");
        maxDelayRangeLabel.setText("(" + formatter.format(scenSummaryModel.getMinDelay()) + " - " + formatter.format(scenSummaryModel.getMaxDelay()) + ")");

        filterOptionsSet = false;
    }

    private void initializeDialog() {

        this.setTitle("Filter Scenarios");

        //filterButton.setEnabled(false);
    }

    /**
     *
     * @return
     */
    public boolean filterOptionsSet() {
        return filterOptionsSet;
    }

    /**
     *
     * @param optionIdx
     */
    public void enableFilterOption(int optionIdx) {
        switch (optionIdx) {
            case 0:
                demandMultRangeCheck.setSelected(true);
                break;
            case 1:
                numWZRangeCheck.setSelected(true);
                break;
            case 3:
                numIncidentsRangeCheck.setSelected(true);
                break;
            case 2:
                numWeatherRangeCheck.setSelected(true);
                break;
            case 4:
                maxTTIRangeCheck.setSelected(true);
                break;
            case 5:
                maxDelayRangeCheck.setSelected(true);
                break;
        }
    }

    private boolean verifyFilters() {
        boolean demandFilter = true;
        boolean wzFilter = true;
        boolean incidentFilter = true;
        boolean weatherFilter = true;
        boolean ttiFilter = true;
        boolean delayFilter = true;

        if (demandMultRangeCheck.isSelected()) {
            try {
                if (Float.parseFloat(demandMultMin.getText()) > Float.parseFloat(demandMultMax.getText())) {
                    demandFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                demandFilter = false;
            }
        }
        if (numWZRangeCheck.isSelected()) {
            try {
                if (Integer.parseInt(numWZMin.getText()) > Integer.parseInt(numWZMax.getText())) {
                    wzFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                wzFilter = false;
            }
        }
        if (numIncidentsRangeCheck.isSelected()) {
            try {
                if (Integer.parseInt(numIncidentsMin.getText()) > Integer.parseInt(numIncidentsMax.getText())) {
                    incidentFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                incidentFilter = false;
            }
        }
        if (numWeatherRangeCheck.isSelected()) {
            try {
                if (Float.parseFloat(numWeatherMin.getText()) > Float.parseFloat(numWeatherMax.getText())) {
                    weatherFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                weatherFilter = false;
            }
        }
        if (maxTTIRangeCheck.isSelected()) {
            try {
                if (Float.parseFloat(maxTTIMin.getText()) > Float.parseFloat(maxTTIMax.getText())) {
                    ttiFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                ttiFilter = false;
            }
        }
        if (maxDelayRangeCheck.isSelected()) {
            try {
                if (Float.parseFloat(maxDelayMin.getText()) > Float.parseFloat(maxDelayMax.getText())) {
                    delayFilter = false;
                }
            } catch (NumberFormatException | NullPointerException e) {
                delayFilter = false;
            }
        }

        if (demandFilter && wzFilter && incidentFilter && weatherFilter && ttiFilter && delayFilter) {
            return true;
        } else {
            String warning = "<HTML><CENTER>Invalid Ranges specified for the following filtes:<br>&nbsp";
            if (!demandFilter) {
                warning = warning + "<br>Demand Multiplier";
            }
            if (!wzFilter) {
                warning = warning + "<br>Number of Work Zones";
            }
            if (!incidentFilter) {
                warning = warning + "<br>Number of Incidents";
            }
            if (!weatherFilter) {
                warning = warning + "<br>Number of Weather Events";
            }
            if (!ttiFilter) {
                warning = warning + "<br>Max TTI";
            }
            if (!delayFilter) {
                warning = warning + "<br>Max Delay";
            }
            warning = warning + "<br>&nbsp<br>Please fix range(s) or turn off offending filter(s).";
            JOptionPane.showMessageDialog(this, warning, "Invalid Filter Ranges", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Has Filter Checks">
    /**
     *
     * @return
     */
    public boolean hasDemandFilter() {
        return demandMultRangeCheck.isSelected();
    }

    /**
     *
     * @return
     */
    public boolean hasIncidentFilter() {
        return numIncidentsRangeCheck.isSelected();
    }

    /**
     *
     * @return
     */
    public boolean hasWeatherFilter() {
        return numWeatherRangeCheck.isSelected();
    }

    /**
     *
     * @return
     */
    public boolean hasTTIFilter() {
        return maxTTIRangeCheck.isSelected();
    }

    /**
     *
     * @return
     */
    public boolean hasDelayFilter() {
        return maxDelayRangeCheck.isSelected();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters for filter ranges">
    /**
     *
     * @return
     */
    public float[] getDemandFilter() {
        if (demandMultRangeCheck.isSelected()) {
            return new float[]{Float.parseFloat(demandMultMin.getText()), Float.parseFloat(demandMultMax.getText())};
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public int[] getWZFilter() {
        if (numWZRangeCheck.isSelected()) {
            return new int[]{Integer.parseInt(numWZMin.getText()), Integer.parseInt(numWZMax.getText())};
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public int[] getIncidentFilter() {
        if (numIncidentsRangeCheck.isSelected()) {
            return new int[]{Integer.parseInt(numIncidentsMin.getText()), Integer.parseInt(numIncidentsMax.getText())};
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public int[] getWeatherFilter() {
        if (numWeatherRangeCheck.isSelected()) {
            return new int[]{Integer.parseInt(numWeatherMin.getText()), Integer.parseInt(numWeatherMax.getText())};
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public float[] getTTIFilter() {
        if (maxTTIRangeCheck.isSelected()) {
            return new float[]{Float.parseFloat(maxTTIMin.getText()), Float.parseFloat(maxTTIMax.getText())};
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public float[] getDelayFilter() {
        if (maxDelayRangeCheck.isSelected()) {
            return new float[]{Float.parseFloat(maxDelayMin.getText()), Float.parseFloat(maxDelayMax.getText())};
        } else {
            return null;
        }
    }
    //</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        demandMultLabel = new javax.swing.JLabel();
        numWZLabel = new javax.swing.JLabel();
        numIncidentsLabel = new javax.swing.JLabel();
        numWeatherLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        demandMultRangeCheck = new javax.swing.JCheckBox();
        numWZRangeCheck = new javax.swing.JCheckBox();
        numIncidentsRangeCheck = new javax.swing.JCheckBox();
        numWeatherRangeCheck = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        demandMultMin = new javax.swing.JTextField();
        numWZMin = new javax.swing.JTextField();
        numIncidentsMin = new javax.swing.JTextField();
        numWeatherMin = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        demandMultMax = new javax.swing.JTextField();
        numWZMax = new javax.swing.JTextField();
        numIncidentsMax = new javax.swing.JTextField();
        numWeatherMax = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        demandMultRangeLabel = new javax.swing.JLabel();
        numWZRangeLabel = new javax.swing.JLabel();
        numIncidentsRangeLabel = new javax.swing.JLabel();
        numWeatherRangeLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        maxTTILabel = new javax.swing.JLabel();
        maxDelayLabel = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        maxTTIRangeCheck = new javax.swing.JCheckBox();
        maxDelayRangeCheck = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        maxTTIMin = new javax.swing.JTextField();
        maxDelayMin = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        maxTTIMax = new javax.swing.JTextField();
        maxDelayMax = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        maxTTIRangeLabel = new javax.swing.JLabel();
        maxDelayRangeLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        filterButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter Options"));

        jLabel6.setText("Min");

        jLabel7.setText("Max");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Scenario Properties"));

        jPanel4.setLayout(new java.awt.GridLayout(4, 1));

        demandMultLabel.setForeground(java.awt.Color.gray);
        demandMultLabel.setText("Demand Multiplier Range:");
        jPanel4.add(demandMultLabel);

        numWZLabel.setForeground(java.awt.Color.gray);
        numWZLabel.setText("# of Work Zones Range:");
        jPanel4.add(numWZLabel);

        numIncidentsLabel.setForeground(java.awt.Color.gray);
        numIncidentsLabel.setText("# of Incidents Range:");
        jPanel4.add(numIncidentsLabel);

        numWeatherLabel.setForeground(java.awt.Color.gray);
        numWeatherLabel.setText("# of Weather Events Range:");
        jPanel4.add(numWeatherLabel);

        jPanel5.setLayout(new java.awt.GridLayout(4, 1));

        demandMultRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                demandMultRangeCheckItemStateChanged(evt);
            }
        });
        jPanel5.add(demandMultRangeCheck);

        numWZRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                numWZRangeCheckItemStateChanged(evt);
            }
        });
        jPanel5.add(numWZRangeCheck);

        numIncidentsRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                numIncidentsRangeCheckItemStateChanged(evt);
            }
        });
        jPanel5.add(numIncidentsRangeCheck);

        numWeatherRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                numWeatherRangeCheckItemStateChanged(evt);
            }
        });
        jPanel5.add(numWeatherRangeCheck);

        jPanel6.setLayout(new java.awt.GridLayout(4, 1, 0, 3));

        demandMultMin.setEnabled(false);
        jPanel6.add(demandMultMin);

        numWZMin.setEnabled(false);
        jPanel6.add(numWZMin);

        numIncidentsMin.setEnabled(false);
        jPanel6.add(numIncidentsMin);

        numWeatherMin.setEnabled(false);
        jPanel6.add(numWeatherMin);

        jPanel7.setLayout(new java.awt.GridLayout(4, 1, 0, 3));

        demandMultMax.setEnabled(false);
        jPanel7.add(demandMultMax);

        numWZMax.setEnabled(false);
        jPanel7.add(numWZMax);

        numIncidentsMax.setEnabled(false);
        jPanel7.add(numIncidentsMax);

        numWeatherMax.setEnabled(false);
        jPanel7.add(numWeatherMax);

        jPanel8.setLayout(new java.awt.GridLayout(4, 1));

        demandMultRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        demandMultRangeLabel.setText("Range");
        jPanel8.add(demandMultRangeLabel);

        numWZRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numWZRangeLabel.setText("Range");
        jPanel8.add(numWZRangeLabel);

        numIncidentsRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numIncidentsRangeLabel.setText("Range");
        jPanel8.add(numIncidentsRangeLabel);

        numWeatherRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numWeatherRangeLabel.setText("Range");
        jPanel8.add(numWeatherRangeLabel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("RL Results Values"));

        jPanel9.setLayout(new java.awt.GridLayout(2, 1));

        maxTTILabel.setForeground(java.awt.Color.gray);
        maxTTILabel.setText("Max TTI Value Range:");
        jPanel9.add(maxTTILabel);

        maxDelayLabel.setForeground(java.awt.Color.gray);
        maxDelayLabel.setText("Max Delay (hrs) Range:");
        jPanel9.add(maxDelayLabel);

        jPanel10.setLayout(new java.awt.GridLayout(2, 1));

        maxTTIRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                maxTTIRangeCheckItemStateChanged(evt);
            }
        });
        jPanel10.add(maxTTIRangeCheck);

        maxDelayRangeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                maxDelayRangeCheckItemStateChanged(evt);
            }
        });
        jPanel10.add(maxDelayRangeCheck);

        jPanel11.setLayout(new java.awt.GridLayout(2, 1, 0, 3));

        maxTTIMin.setEnabled(false);
        jPanel11.add(maxTTIMin);

        maxDelayMin.setEnabled(false);
        jPanel11.add(maxDelayMin);

        jPanel12.setLayout(new java.awt.GridLayout(2, 1, 0, 3));

        maxTTIMax.setEnabled(false);
        jPanel12.add(maxTTIMax);

        maxDelayMax.setEnabled(false);
        jPanel12.add(maxDelayMax);

        jPanel13.setLayout(new java.awt.GridLayout(2, 1));

        maxTTIRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maxTTIRangeLabel.setText("Range");
        jPanel13.add(maxTTIRangeLabel);

        maxDelayRangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maxDelayRangeLabel.setText("Range");
        jPanel13.add(maxDelayRangeLabel);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        filterButton.setText("Filter");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Use Filter");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Values Range");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(185, 185, 185)
                .addComponent(jLabel8)
                .addGap(37, 37, 37)
                .addComponent(jLabel6)
                .addGap(43, 43, 43)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addGap(4, 4, 4)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void demandMultRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_demandMultRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (demandMultRangeCheck.isSelected()) {
            enabledBool = true;
            demandMultLabel.setForeground(Color.black);
        } else {
            demandMultLabel.setForeground(Color.gray);
        }
        demandMultMax.setEnabled(enabledBool);
        demandMultMin.setEnabled(enabledBool);
    }//GEN-LAST:event_demandMultRangeCheckItemStateChanged

    private void numIncidentsRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_numIncidentsRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (numIncidentsRangeCheck.isSelected()) {
            enabledBool = true;
            numIncidentsLabel.setForeground(Color.black);
        } else {
            numIncidentsLabel.setForeground(Color.gray);
        }
        numIncidentsMax.setEnabled(enabledBool);
        numIncidentsMin.setEnabled(enabledBool);
    }//GEN-LAST:event_numIncidentsRangeCheckItemStateChanged

    private void numWeatherRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_numWeatherRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (numWeatherRangeCheck.isSelected()) {
            enabledBool = true;
            numWeatherLabel.setForeground(Color.black);
        } else {
            numWeatherLabel.setForeground(Color.gray);
        }
        numWeatherMax.setEnabled(enabledBool);
        numWeatherMin.setEnabled(enabledBool);
    }//GEN-LAST:event_numWeatherRangeCheckItemStateChanged

    private void maxTTIRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_maxTTIRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (maxTTIRangeCheck.isSelected()) {
            enabledBool = true;
            maxTTILabel.setForeground(Color.black);
        } else {
            maxTTILabel.setForeground(Color.gray);
        }
        maxTTIMax.setEnabled(enabledBool);
        maxTTIMin.setEnabled(enabledBool);
    }//GEN-LAST:event_maxTTIRangeCheckItemStateChanged

    private void maxDelayRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_maxDelayRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (maxDelayRangeCheck.isSelected()) {
            enabledBool = true;
            maxDelayLabel.setForeground(Color.black);
        } else {
            maxDelayLabel.setForeground(Color.gray);
        }
        maxDelayMax.setEnabled(enabledBool);
        maxDelayMin.setEnabled(enabledBool);
    }//GEN-LAST:event_maxDelayRangeCheckItemStateChanged

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        if (verifyFilters()) {
            filterOptionsSet = true;
            this.setVisible(false);
        }
    }//GEN-LAST:event_filterButtonActionPerformed

    private void numWZRangeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_numWZRangeCheckItemStateChanged
        boolean enabledBool = false;
        if (numWZRangeCheck.isSelected()) {
            enabledBool = true;
            numWZLabel.setForeground(Color.black);
        } else {
            numWZLabel.setForeground(Color.gray);
        }
        numWZMax.setEnabled(enabledBool);
        numWZMin.setEnabled(enabledBool);
    }//GEN-LAST:event_numWZRangeCheckItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel demandMultLabel;
    private javax.swing.JTextField demandMultMax;
    private javax.swing.JTextField demandMultMin;
    private javax.swing.JCheckBox demandMultRangeCheck;
    private javax.swing.JLabel demandMultRangeLabel;
    private javax.swing.JButton filterButton;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel maxDelayLabel;
    private javax.swing.JTextField maxDelayMax;
    private javax.swing.JTextField maxDelayMin;
    private javax.swing.JCheckBox maxDelayRangeCheck;
    private javax.swing.JLabel maxDelayRangeLabel;
    private javax.swing.JLabel maxTTILabel;
    private javax.swing.JTextField maxTTIMax;
    private javax.swing.JTextField maxTTIMin;
    private javax.swing.JCheckBox maxTTIRangeCheck;
    private javax.swing.JLabel maxTTIRangeLabel;
    private javax.swing.JLabel numIncidentsLabel;
    private javax.swing.JTextField numIncidentsMax;
    private javax.swing.JTextField numIncidentsMin;
    private javax.swing.JCheckBox numIncidentsRangeCheck;
    private javax.swing.JLabel numIncidentsRangeLabel;
    private javax.swing.JLabel numWZLabel;
    private javax.swing.JTextField numWZMax;
    private javax.swing.JTextField numWZMin;
    private javax.swing.JCheckBox numWZRangeCheck;
    private javax.swing.JLabel numWZRangeLabel;
    private javax.swing.JLabel numWeatherLabel;
    private javax.swing.JTextField numWeatherMax;
    private javax.swing.JTextField numWeatherMin;
    private javax.swing.JCheckBox numWeatherRangeCheck;
    private javax.swing.JLabel numWeatherRangeLabel;
    // End of variables declaration//GEN-END:variables
}
