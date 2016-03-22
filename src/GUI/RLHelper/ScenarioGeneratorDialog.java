package GUI.RLHelper;

import GUI.major.MainWindow;
import coreEngine.Helper.CEDate;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import coreEngine.reliabilityAnalysis.DataStruct.IncidentData;
import coreEngine.reliabilityAnalysis.DataStruct.Scenario;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import coreEngine.reliabilityAnalysis.DataStruct.WeatherData;
import coreEngine.reliabilityAnalysis.DataStruct.WorkZone;
import coreEngine.reliabilityAnalysis.ScenarioGenerator;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author tristan
 */
public class ScenarioGeneratorDialog extends javax.swing.JDialog {

    private ScenarioGenerator scenarioGenerator;

    private final Seed seed;

    private boolean scenariosCreated = false;

    /**
     *
     */
    public boolean toggleRun = false;

    /**
     * Creates new form ScenarioGeneratorDialog
     *
     * @param seed
     * @param modal
     * @param parent
     */
    public ScenarioGeneratorDialog(Seed seed, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initializeDialog();

        getRootPane().setDefaultButton(genAndRunScenariosButton);

        this.seed = seed;
        scenarioGenerator = new ScenarioGenerator(seed);
        //set up major panels
        //property panel
        resetDateComboBoxes();
        if (seed.getRLRNGSeed() == null) {
            prevRNGSeedLabel.setVisible(false);
            seedFileSeedRB.setEnabled(false);
        } else {
            prevRNGSeedLabel.setVisible(true);
            prevRNGSeedLabel.setText(seed.getRLRNGSeed().toString());
            seedFileSeedRB.setEnabled(true);
        }
        newSeedRBActionPerformed(null);

        //demand panel
        demandPanel.activateDemandJPanel(seed);
        demandPanel.setTableFont(MainWindow.getTableFont());

        //weather panel
        weatherPanel.activateWeatherJPanel(seed);
        weatherPanel.setTableFont(MainWindow.getTableFont());

        //incident panel
        incidentPanel.setDemandData(demandPanel.getDemandData());
        incidentPanel.activateIncidentJPanel(seed);
        incidentPanel.setTableFont(MainWindow.getTableFont());

        //workzone panel
        workZonePanel.setDemandData(demandPanel.getDemandData());
        workZonePanel.activateWorkZonePanel(seed);
        workZonePanel.setTableFont(MainWindow.getTableFont());

        //Managed Lanes
        if (seed.isManagedLaneUsed()) {
            //Managed Lanes Demand Panel
            demandJPanelML.activateDemandJPanel(seed);
            demandJPanelML.setTableFont(MainWindow.getTableFont());

            //Managed Lanes Incident Panel
            incidentMLCheck.setEnabled(true);
            incidentMLCheck.setSelected(true);
            incidentJPanelML.setDemandData(demandJPanelML.getDemandData());
            incidentJPanelML.activateIncidentJPanel(seed);
            incidentJPanelML.setTableFont(MainWindow.getTableFont());
        } else {
            incidentMLCheck.setEnabled(false);
            tabbedPane.remove(demandJPanelML);
            tabbedPane.remove(incidentJPanelML);
        }

    }

    private void resetDateComboBoxes() {
        analysisYear.setSelectedIndex(2020 - seed.getSeedFileDate().year);
        seedMonth.setSelectedIndex(seed.getSeedFileDate().month - 1);
        seedDay.setModel(modelCreator(seedMonth.getSelectedIndex() + 1, seed.getSeedFileDate().year));
        seedDay.setSelectedIndex(seed.getSeedFileDate().day - 1);
        startMonth.setSelectedIndex(seed.getRRPStartDate().month - 1);
        startDay.setModel(modelCreator(startMonth.getSelectedIndex() + 1, seed.getSeedFileDate().year));
        startDay.setSelectedIndex(seed.getRRPStartDate().day - 1);
        endMonth.setSelectedIndex(seed.getRRPEndDate().month - 1);
        endDay.setModel(modelCreator(endMonth.getSelectedIndex() + 1, seed.getSeedFileDate().year));
        endDay.setSelectedIndex(seed.getRRPEndDate().day - 1);
        changeNotAppliedLabel.setVisible(false);
    }

    /**
     *
     * @return
     */
    public boolean scenariosGenerated() {
        return scenariosCreated;
    }

    /**
     * Getter for GP (general purpose) Scenario
     *
     * @return
     */
    public Scenario getScenariosGP() {
        return scenarioGenerator.getGPScenario();
    }

    /**
     *
     * @return
     */
    public Scenario getScenariosML() {
        return scenarioGenerator.getMLScenario();
    }

    /**
     * Getter for ArrayList of ScenarioInfo Objects
     *
     * @return
     */
    public ArrayList<ScenarioInfo> getScenarioInfoList() {
        return scenarioGenerator.getScenarioInfoList();
    }

    private void initializeDialog() {
        this.setTitle("Scenario Generator");
    }

    private void generateScenarios() {
        // Generate scenarios here
        DemandData demand = demandPanel.getDemandData();
        ArrayList<WorkZone> workZone = workZonePanel.getWorkZoneData();
        WeatherData weather = weatherPanel.getWeatherData();
        IncidentData incident = incidentPanel.getIncidentData();

        scenarioGenerator.setDemandDataGP(demand);
        scenarioGenerator.setWorkZoneDataGP(workZone);
        scenarioGenerator.setWeatherData(weather);
        scenarioGenerator.setGPIncidentData(incident);
        scenarioGenerator.includeGPWorkZones(wzCheck.isSelected());
        scenarioGenerator.includeIncidentsGP(incidentCheck.isSelected());
        scenarioGenerator.includeWeather(weatherCheck.isSelected());

        if (seed.isManagedLaneUsed()) {
            scenarioGenerator.setMLUsed(true);
            if (demandJPanelML.useGPValues()) {
                scenarioGenerator.setMLDemandData(demand);
            } else {
                scenarioGenerator.setMLDemandData(demandJPanelML.getDemandData());
            }
            if (incidentJPanelML.useGPValues()) {
                scenarioGenerator.setMLIncidentData(incident);
            } else {
                scenarioGenerator.setMLIncidentData(incidentJPanelML.getIncidentData());
            }
            scenarioGenerator.includeIncidentsML(incidentMLCheck.isSelected());
        }

        for (int i = 0; i < 12; ++i) {
            scenarioGenerator.excludeDaysInMonth(demandPanel.getDaysSkippedInMonth(i), i + 1);
        }

        //scenarios = scenarioGenerator.generateScenarios();
        scenariosCreated = scenarioGenerator.generateScenarios();
    }

    private void closeWindow() {
        scenarioGenerator.setNumberOfReplications(Integer.parseInt(numReplicatesTF.getText()));

        // Setting scenario generator seed
        if (newSeedRB.isSelected()) {
            long newSeed = System.currentTimeMillis() % 1000000;
            scenarioGenerator.setRNGSeed(newSeed);
            seed.setRLRNGSeed(newSeed);
        } else if (userSeedRB.isSelected()) {
            long userSeed = Long.parseLong(valueTextField.getText());
            scenarioGenerator.setRNGSeed(userSeed);
            seed.setRLRNGSeed(userSeed);
        } else if (seedFileSeedRB.isSelected()) {
            //if (seed.getRLRNGSeed() != null) {
            scenarioGenerator.setRNGSeed(seed.getRLRNGSeed());
            //}
        }

        MainWindow.printLog("Generating scenarios using RNG seed: " + scenarioGenerator.getRNGSeed());

        // Assigning demand, weather, and incidents to seed file
        assignGPDemandToSeedFile(seed, demandPanel.getDemandData()); // assigns any changes in demand to the seed.
        assignWeatherToSeedFile(seed, weatherPanel.getWeatherData()); // assigns any changes in weather to the seed.
        assignGPIncidentToSeedFile(seed, incidentPanel.getIncidentData());
        assignWorkZonesToSeedFile(seed, workZonePanel.getWorkZoneData());

        if (seed.isManagedLaneUsed()) {
            assignMLDemandToSeedFile(seed, demandJPanelML.getDemandData());
            assignMLIncidentToSeedFile(seed, incidentJPanelML.getIncidentData());
        }

        generateScenarios();

        //this.setVisible(false);
        dispose();
    }

    public static boolean validateSeedDataForReliabiityAnalysis(Seed seed) {
        return ScenarioGenerator.validateSeedForReliabilityAnalysis(seed);

    }

    public static int[] getSeedDataFormatErrorLocation(Seed seed) {
        return ScenarioGenerator.getSeedDataFormatErrorLocation(seed);
    }

    //<editor-fold defaultstate="collapsed" desc="Input Verifiers">
    private boolean verifyProperties() {
        // Verifying valid seed properties
        boolean seedInputVerified = verifySeed();
        seedInputVerified = verifyNumberRealizations() && seedInputVerified;
        seedInputVerified = !changeNotAppliedLabel.isVisible() && seedInputVerified;

        if (!seedInputVerified && tabbedPane.getSelectedIndex() != 0) {
            tabbedPane.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, "Error found in input, please check", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return seedInputVerified;
    }

    private boolean verifyInputs() {

        // Verifying valid seed properties
        boolean seedInputVerified = verifySeed();
        seedInputVerified = verifyNumberRealizations() && seedInputVerified;
        seedInputVerified = !changeNotAppliedLabel.isVisible() && seedInputVerified;

        // Verifying valid demand properties
        boolean demandDataVerified = verifyDemandInput();

        // Verifying valid work zone properites
        boolean wzDataVerified = verifyWorkZoneInput();

        // Verifying valid weather properties
        boolean weatherDataVerified = verifyWeatherInput();

        // Verifying valid incident properties (Incident distribution check)
        boolean incidentDataVerified = verifyIncidentInput();

        if (!seedInputVerified && tabbedPane.getSelectedIndex() != 0) {
            tabbedPane.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, "Error found in input, please check", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!demandDataVerified) {
            tabbedPane.setSelectedComponent(demandPanel);
            JOptionPane.showMessageDialog(this, "Error found in demand input, please check inputs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } else if (!wzDataVerified) {
            tabbedPane.setSelectedComponent(workZonePanel);
            JOptionPane.showMessageDialog(this, "Error found in work zone input, please check inputs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } else if (!weatherDataVerified) {
            tabbedPane.setSelectedComponent(weatherPanel);
            JOptionPane.showMessageDialog(this, "Error found in weather input, please check inputs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } else if (!incidentDataVerified) {
            tabbedPane.setSelectedComponent(incidentPanel);
            JOptionPane.showMessageDialog(this, "Error found in incident input, please make sure the Incident distribution sum is 100%", "Input Error", JOptionPane.ERROR_MESSAGE);
        }

        return (seedInputVerified && demandDataVerified && wzDataVerified && weatherDataVerified && incidentDataVerified);
    }

    private boolean verifySeed() {
        // Setting scenario generator seed
        if (newSeedRB.isSelected()) {
            return true;
        }

        if (userSeedRB.isSelected()) {
            try {
                long userSeed = Long.parseLong(valueTextField.getText());
                valueTextField.setForeground(Color.black);
                return true;
            } catch (NumberFormatException e) {
                valueTextField.setForeground(Color.red);
                return false;
            }
        }

        return true;
    }

    private boolean verifyNumberRealizations() {
        try {
            long num = Long.parseLong(numReplicatesTF.getText());
            if (num > 0) {
                numReplicatesTF.setForeground(Color.black);
                return true;
            } else {
                numReplicatesTF.setForeground(Color.red);
                return false;
            }
        } catch (NumberFormatException e) {
            numReplicatesTF.setForeground(Color.red);
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean verifyDemandInput() {
        boolean verified = true;
        // Checking to see if any days of the week are selected
        if (demandPanel.getDemandData().getActiveDaysCount() == 0) {
            verified = false;
        }
        return verified;
    }

    /**
     *
     * @return
     */
    public boolean verifyWorkZoneInput() {
        return true;
    }

    /**
     *
     * @return
     */
    public boolean verifyWeatherInput() {
        return weatherPanel.getWeatherData().checkNonNegative();
    }

    /**
     *
     * @return
     */
    public boolean verifyIncidentInput() {
        boolean verified = true;
        if (Math.pow(incidentPanel.getIncidentData().getIncidentDistributionSum() - 100.0f, 2) >= 10e-10) {
            verified = false;
        }
        return verified;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utility Functions">
    private void updateAllDayCBs() {

        int year = Integer.parseInt((String) analysisYear.getSelectedItem());
        // Updating seedDay
        int currIdx = seedDay.getSelectedIndex();
        seedDay.setModel(modelCreator(seedMonth.getSelectedIndex() + 1, year));
        if (currIdx >= seedDay.getModel().getSize()) {
            currIdx = seedDay.getModel().getSize() - 1;
        }
        seedDay.setSelectedIndex(currIdx);

        // Updating startDay
        currIdx = startDay.getSelectedIndex();
        startDay.setModel(modelCreator(startMonth.getSelectedIndex() + 1, year));
        if (currIdx >= startDay.getModel().getSize()) {
            currIdx = startDay.getModel().getSize() - 1;
        }
        startDay.setSelectedIndex(currIdx);

        // Updating seedDay
        currIdx = endDay.getSelectedIndex();
        endDay.setModel(modelCreator(endMonth.getSelectedIndex() + 1, year));
        if (currIdx >= endDay.getModel().getSize()) {
            currIdx = endDay.getModel().getSize() - 1;
        }
        endDay.setSelectedIndex(currIdx);

    }

    /**
     *
     * @param seed
     * @param demand
     */
    public void assignGPDemandToSeedFile(Seed seed, DemandData demand) {

        // Assigning the demand data from the panel to the seed.
        seed.setSpecifiedGPDemand(demand.getSpecifiedDemand());
        seed.setWeekdayUsed(demand.getActiveDays());
        seed.setDayExcluded(demandPanel.getExcludedDaysArrayList());
    }

    /**
     *
     * @param seed
     * @param demand
     */
    public void assignMLDemandToSeedFile(Seed seed, DemandData demand) {

        // Assigning the demand data from the panel to the seed.
        seed.setSpecifiedMLDemand(demand.getSpecifiedDemand());
    }

    /**
     *
     * @param seed
     * @param weather
     */
    public void assignWeatherToSeedFile(Seed seed, WeatherData weather) {

        // Assigning the weather data from the panel to the seed.
        seed.setWeatherProbability(weather.getProbability());
        seed.setWeatherAverageDuration(weather.getAverageDurationMinutes()); // Sets the average duration of weather types in minutes
        seed.setWeatherCAF(weather.getWeatherCAFArray());
        seed.setWeatherDAF(weather.getWeatherDAFArray());
        seed.setWeatherSAF(weather.getWeatherSAFArray());
        seed.setWeatherLocation(weather.getNearestMetroArea());
    }

    /**
     *
     * @param seed
     * @param incidents
     */
    public void assignGPIncidentToSeedFile(Seed seed, IncidentData incidents) {

        // Assigning the incident data from the panel to the seed.
        seed.setGPIncidentCrashRatio(incidents.getCrashRateRatio());
        seed.setGPIncidentDistribution(incidents.getIncidentDistribution());
        seed.setGPIncidentDuration(incidents.getIncidentDurationInfo());
        seed.setGPIncidentFrequency(incidents.getIncidentFrequencyArr());
        seed.setGPIncidentCAF(incidents.getIncidentCAF());
        seed.setGPIncidentDAF(incidents.getIncidentDAF());
        seed.setGPIncidentLAF(incidents.getIncidentLAF());
        seed.setGPIncidentSAF(incidents.getIncidentFFSAF());
    }

    /**
     *
     * @param seed
     * @param wzData
     */
    public void assignWorkZonesToSeedFile(Seed seed, ArrayList<WorkZone> wzData) {

        seed.setRLWorkZones(wzData);

    }

    /**
     *
     * @param seed
     * @param incidents
     */
    public void assignMLIncidentToSeedFile(Seed seed, IncidentData incidents) {

        // Assigning the incident data from the panel to the seed.
        seed.setMLIncidentCrashRatio(incidents.getCrashRateRatio());
        seed.setMLIncidentDuration(incidents.getIncidentDurationInfo());
        seed.setMLIncidentDistribution(incidents.getIncidentDistribution());
        seed.setMLIncidentFrequency(incidents.getIncidentFrequencyArr());
        seed.setMLIncidentCAF(incidents.getIncidentCAF());
        seed.setMLIncidentDAF(incidents.getIncidentDAF());
        seed.setMLIncidentLAF(incidents.getIncidentLAF());
        seed.setMLIncidentSAF(incidents.getIncidentFFSAF());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Model Creators">
    /**
     *
     * @param month
     * @param year
     * @return
     */
    public static DefaultComboBoxModel modelCreator(int month, int year) {
        String[] dateArr = new String[CEDate.daysInMonth(month, year)];
        for (int day = 1; day <= dateArr.length; day++) {
            dateArr[day - 1] = day + "  " + CEDate.getDayName(CEDate.dayOfWeek(day, month, year));
        }
        return new DefaultComboBoxModel(dateArr);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        currSeedLabel = new javax.swing.JLabel();
        generateScenariosButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabbedPane = new javax.swing.JTabbedPane();
        propertiesPanel = new javax.swing.JPanel();
        datePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startMonth = new javax.swing.JComboBox();
        startDay = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        endMonth = new javax.swing.JComboBox();
        endDay = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        analysisYear = new javax.swing.JComboBox();
        setPeriodButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        seedMonth = new javax.swing.JComboBox();
        seedDay = new javax.swing.JComboBox();
        changeNotAppliedLabel = new javax.swing.JLabel();
        discardDateButton = new javax.swing.JButton();
        optionPanel = new javax.swing.JPanel();
        wzCheck = new javax.swing.JCheckBox();
        incidentCheck = new javax.swing.JCheckBox();
        weatherCheck = new javax.swing.JCheckBox();
        incidentMLCheck = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        newSeedRB = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        userSeedRB = new javax.swing.JRadioButton();
        valueTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        seedFileSeedRB = new javax.swing.JRadioButton();
        prevRNGSeedLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        numReplicatesTF = new javax.swing.JTextField();
        demandPanel = new GUI.RLHelper.DemandJPanel();
        workZonePanel = new GUI.RLHelper.WorkZonePanel();
        incidentPanel = new GUI.RLHelper.IncidentJPanel();
        weatherPanel = new GUI.RLHelper.WeatherJPanel();
        demandJPanelML = new GUI.RLHelper.ManagedLanes.DemandJPanelML();
        incidentJPanelML = new GUI.RLHelper.ManagedLanes.IncidentJPanelML();
        genAndRunScenariosButton = new javax.swing.JButton();

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText(" (Changes RNG seed each time)");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText(" (Uses same specified value each time)");

        currSeedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        generateScenariosButton.setText("Generate Scenarios Only");
        generateScenariosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateScenariosButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(null);

        tabbedPane.setPreferredSize(new java.awt.Dimension(995, 540));
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reliability Analysis Properties", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        datePanel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Start Date:");

        startMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        startMonth.setPreferredSize(new java.awt.Dimension(90, 27));
        startMonth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                startMonthItemStateChanged(evt);
            }
        });

        startDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        startDay.setPreferredSize(new java.awt.Dimension(90, 27));
        startDay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                startDayItemStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("End Date:");

        endMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        endMonth.setSelectedIndex(11);
        endMonth.setPreferredSize(new java.awt.Dimension(90, 27));
        endMonth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                endMonthItemStateChanged(evt);
            }
        });

        endDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        endDay.setSelectedIndex(30);
        endDay.setPreferredSize(new java.awt.Dimension(90, 27));
        endDay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                endDayItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Year:");

        analysisYear.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002", "2001", "2000" }));
        analysisYear.setSelectedIndex(6);
        analysisYear.setPreferredSize(new java.awt.Dimension(90, 27));
        analysisYear.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                analysisYearItemStateChanged(evt);
            }
        });

        setPeriodButton.setText("Set RRP Period");
        setPeriodButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPeriodButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Seed Date:");

        seedMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        seedMonth.setPreferredSize(new java.awt.Dimension(90, 27));
        seedMonth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                seedMonthItemStateChanged(evt);
            }
        });

        seedDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        seedDay.setPreferredSize(new java.awt.Dimension(90, 27));
        seedDay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                seedDayItemStateChanged(evt);
            }
        });

        changeNotAppliedLabel.setForeground(new java.awt.Color(251, 8, 8));
        changeNotAppliedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        changeNotAppliedLabel.setText("RRP Date Changes Not Applied");

        discardDateButton.setText("Discard Changes");
        discardDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardDateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setHorizontalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(seedDay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(seedMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(startDay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(startMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(endDay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(endMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(changeNotAppliedLabel)
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(analysisYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(setPeriodButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(discardDateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        datePanelLayout.setVerticalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(seedMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(startMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(endMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(analysisYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setPeriodButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seedDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeNotAppliedLabel)
                    .addComponent(discardDateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        optionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Include Event Types", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        optionPanel.setLayout(new java.awt.GridLayout(1, 4));

        wzCheck.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        wzCheck.setSelected(true);
        wzCheck.setText("GP - Work Zones");
        wzCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                wzCheckItemStateChanged(evt);
            }
        });
        optionPanel.add(wzCheck);

        incidentCheck.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        incidentCheck.setSelected(true);
        incidentCheck.setText("GP - Incidents");
        incidentCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                incidentCheckItemStateChanged(evt);
            }
        });
        optionPanel.add(incidentCheck);

        weatherCheck.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        weatherCheck.setSelected(true);
        weatherCheck.setText("Weather");
        weatherCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                weatherCheckItemStateChanged(evt);
            }
        });
        optionPanel.add(weatherCheck);

        incidentMLCheck.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        incidentMLCheck.setText("ML - Incidents");
        incidentMLCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                incidentMLCheckItemStateChanged(evt);
            }
        });
        optionPanel.add(incidentMLCheck);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Random Number Generator (RNG) Seed Options (Any new RNG Seed value will be saved to the seed file)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        buttonGroup1.add(newSeedRB);
        newSeedRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        newSeedRB.setSelected(true);
        newSeedRB.setText("Use new random RNG seed");
        newSeedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSeedRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(userSeedRB);
        userSeedRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        userSeedRB.setText("Use user specified RNG seed");
        userSeedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userSeedRBActionPerformed(evt);
            }
        });

        valueTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        valueTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        valueTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                valueTextFieldFocusGained(evt);
            }
        });

        buttonGroup1.add(seedFileSeedRB);
        seedFileSeedRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        seedFileSeedRB.setText("Use previous used RNG seed");
        seedFileSeedRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seedFileSeedRBActionPerformed(evt);
            }
        });

        prevRNGSeedLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        prevRNGSeedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevRNGSeedLabel.setText("N/A");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(userSeedRB, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                    .addComponent(seedFileSeedRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newSeedRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(prevRNGSeedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newSeedRB, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userSeedRB, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prevRNGSeedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seedFileSeedRB, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Number of Demand Combination Realizations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Number of realizations (default 4): ");

        numReplicatesTF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        numReplicatesTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numReplicatesTF.setText("4");
        numReplicatesTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                numReplicatesTFFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numReplicatesTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(706, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(numReplicatesTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout propertiesPanelLayout = new javax.swing.GroupLayout(propertiesPanel);
        propertiesPanel.setLayout(propertiesPanelLayout);
        propertiesPanelLayout.setHorizontalGroup(
            propertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertiesPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(propertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(datePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        propertiesPanelLayout.setVerticalGroup(
            propertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertiesPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(172, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Properties", propertiesPanel);
        tabbedPane.addTab("GP - Demand", demandPanel);
        tabbedPane.addTab("GP - Work Zones", workZonePanel);
        tabbedPane.addTab("GP - Incidents", incidentPanel);
        tabbedPane.addTab("Weather", weatherPanel);
        tabbedPane.addTab("ML - Demand", demandJPanelML);
        tabbedPane.addTab("ML - Incidents", incidentJPanelML);

        jScrollPane1.setViewportView(tabbedPane);

        genAndRunScenariosButton.setText("Generate and Run Scenarios");
        genAndRunScenariosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genAndRunScenariosButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(generateScenariosButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genAndRunScenariosButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateScenariosButton)
                    .addComponent(cancelButton)
                    .addComponent(genAndRunScenariosButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Button Actions">
        private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
        }//GEN-LAST:event_cancelButtonActionPerformed

        private void generateScenariosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateScenariosButtonActionPerformed
        toggleRun = false;
        if (verifyInputs()) {
            closeWindow();
        }
        }//GEN-LAST:event_generateScenariosButtonActionPerformed

        private void setPeriodButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPeriodButtonActionPerformed

        int sMonth = startMonth.getSelectedIndex(); //Note that indexing begins at 0
        int sDay = startDay.getSelectedIndex();
        int eMonth = endMonth.getSelectedIndex();
        int eDay = endDay.getSelectedIndex();
        int year = Integer.parseInt((String) analysisYear.getSelectedItem());

        if ((sMonth < eMonth) || ((sMonth == eMonth) && (sDay <= eDay))) {
            int okCancelResult = JOptionPane.showConfirmDialog(this, "<HTML><CENTER>Warning: Changing the RRP dates will clear any existing work zones<br>"
                    + "and the list of excluded days.", "Warning: Seed Date Change", JOptionPane.OK_CANCEL_OPTION);
            if (okCancelResult == JOptionPane.OK_OPTION) {
                //Setting seed values
                seed.setSeedFileDate(new CEDate(year, seedMonth.getSelectedIndex() + 1, seedDay.getSelectedIndex() + 1));
                seed.setRRPStartDate(new CEDate(year, sMonth + 1, sDay + 1)); //Adding 1 to fix indexing
                seed.setRRPEndDate(new CEDate(year, eMonth + 1, eDay + 1)); //Adding 1 to fix indexing

                tabbedPane.setEnabled(true);
                generateScenariosButton.setEnabled(true);
                demandPanel.updateDemandJPanel(year, sMonth, sDay, eMonth, eDay);
                workZonePanel.updatePanel(year);
                weatherPanel.updateWeatherJPanel(weatherPanel.getWeatherData(), year, sMonth, sDay, eMonth, eDay);
                //incidentPanel.activateIncidentJPanel();
                incidentPanel.updateIncidentJPanel(incidentPanel.getIncidentData());
                if (seed.isManagedLaneUsed()) {
                    demandJPanelML.updateDemandJPanel(year, sMonth, sDay, eMonth, eDay);
                    incidentJPanelML.updateIncidentJPanel(incidentJPanelML.getIncidentData());
                }

                changeNotAppliedLabel.setVisible(false);
            }
        } else {
            //JFrame frame = new JFrame;
            JOptionPane.showMessageDialog(this, "Invalid RRP start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        }//GEN-LAST:event_setPeriodButtonActionPerformed

    private void seedMonthItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_seedMonthItemStateChanged

        int year = Integer.parseInt((String) analysisYear.getSelectedItem());
        int currIdx = seedDay.getSelectedIndex();
        seedDay.setModel(modelCreator(seedMonth.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= seedDay.getModel().getSize()) {
            currIdx = seedDay.getModel().getSize() - 1;
        }
        seedDay.setSelectedIndex(currIdx);

        if (seedMonth.getSelectedIndex() + 1 == seed.getSeedFileDate().month && seedDay.getSelectedIndex() + 1 == seed.getSeedFileDate().day) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_seedMonthItemStateChanged

    private void endMonthItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_endMonthItemStateChanged

        int year = Integer.parseInt((String) analysisYear.getSelectedItem());
        int currIdx = endDay.getSelectedIndex();
        endDay.setModel(modelCreator(endMonth.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= endDay.getModel().getSize()) {
            currIdx = endDay.getModel().getSize() - 1;
        }
        endDay.setSelectedIndex(currIdx);

        if (endMonth.getSelectedIndex() + 1 == seed.getRRPEndDate().month && endDay.getSelectedIndex() + 1 == seed.getRRPEndDate().day) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_endMonthItemStateChanged

    private void startMonthItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_startMonthItemStateChanged

        int year = Integer.parseInt((String) analysisYear.getSelectedItem());
        int currIdx = startDay.getSelectedIndex();
        startDay.setModel(modelCreator(startMonth.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= startDay.getModel().getSize()) {
            currIdx = startDay.getModel().getSize() - 1;
        }
        startDay.setSelectedIndex(currIdx);

        if (startMonth.getSelectedIndex() + 1 == seed.getRRPStartDate().month && startDay.getSelectedIndex() + 1 == seed.getRRPStartDate().day) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_startMonthItemStateChanged

    private void analysisYearItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_analysisYearItemStateChanged
        // Update day combo boxes to reflect any changes
        updateAllDayCBs();
        if (Integer.parseInt((String) analysisYear.getSelectedItem()) == seed.getSeedFileDate().year) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_analysisYearItemStateChanged

    private void seedDayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_seedDayItemStateChanged
        if (seedDay.getSelectedIndex() + 1 == seed.getSeedFileDate().day && seedMonth.getSelectedIndex() + 1 == seed.getSeedFileDate().month) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_seedDayItemStateChanged

    private void startDayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_startDayItemStateChanged
        if (startDay.getSelectedIndex() + 1 == seed.getRRPStartDate().day && startMonth.getSelectedIndex() + 1 == seed.getRRPStartDate().month) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_startDayItemStateChanged

    private void endDayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_endDayItemStateChanged
        if (endDay.getSelectedIndex() + 1 == seed.getRRPEndDate().day && endMonth.getSelectedIndex() + 1 == seed.getRRPEndDate().month) {
            changeNotAppliedLabel.setVisible(false);
        } else {
            changeNotAppliedLabel.setVisible(true);
        }
    }//GEN-LAST:event_endDayItemStateChanged

    private void weatherCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_weatherCheckItemStateChanged
        if (weatherCheck.isSelected()) {
            int tabIdx = 2;
            if (wzCheck.isSelected()) {
                tabIdx++;
            }
            if (incidentCheck.isSelected()) {
                tabIdx++;
            }
            tabbedPane.insertTab("Weather", null, weatherPanel, null, tabIdx);
        } else {
            tabbedPane.remove(weatherPanel);
        }

    }//GEN-LAST:event_weatherCheckItemStateChanged

    private void incidentCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_incidentCheckItemStateChanged
        if (incidentCheck.isSelected()) {
            if (wzCheck.isSelected()) {
                tabbedPane.insertTab("GP - Incident", null, incidentPanel, null, 3);
            } else {
                tabbedPane.insertTab("GP - Incident", null, incidentPanel, null, 2);
            }
        } else {
            tabbedPane.remove(incidentPanel);
        }

    }//GEN-LAST:event_incidentCheckItemStateChanged

    private void genAndRunScenariosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genAndRunScenariosButtonActionPerformed
        toggleRun = true;
        if (verifyInputs()) {
            closeWindow();
        }
    }//GEN-LAST:event_genAndRunScenariosButtonActionPerformed

    private void wzCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_wzCheckItemStateChanged
        if (wzCheck.isSelected()) {
            tabbedPane.insertTab("GP - Work Zones", null, workZonePanel, null, 2);
        } else {
            tabbedPane.remove(workZonePanel);
        }
    }//GEN-LAST:event_wzCheckItemStateChanged

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        verifyProperties();
//        if (tabbedPane.getSelectedIndex() != 0 && !verifyProperties()) {
//            tabbedPane.setSelectedIndex(0);
//        }
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void seedFileSeedRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seedFileSeedRBActionPerformed
        configRNGOptions();
    }//GEN-LAST:event_seedFileSeedRBActionPerformed

    private void userSeedRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userSeedRBActionPerformed
        configRNGOptions();
    }//GEN-LAST:event_userSeedRBActionPerformed

    private void newSeedRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSeedRBActionPerformed
        configRNGOptions();
    }//GEN-LAST:event_newSeedRBActionPerformed

    private void numReplicatesTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numReplicatesTFFocusGained
        numReplicatesTF.setForeground(Color.black);
    }//GEN-LAST:event_numReplicatesTFFocusGained

    private void discardDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardDateButtonActionPerformed
        resetDateComboBoxes();
    }//GEN-LAST:event_discardDateButtonActionPerformed

    private void valueTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTextFieldFocusGained
        valueTextField.setForeground(Color.black);
    }//GEN-LAST:event_valueTextFieldFocusGained

    private void incidentMLCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_incidentMLCheckItemStateChanged
        if (incidentMLCheck.isSelected()) {
            int tabIdx = 3;
            if (wzCheck.isSelected()) {
                tabIdx++;
            }
            if (incidentCheck.isSelected()) {
                tabIdx++;
            }
            if (weatherCheck.isSelected()) {
                tabIdx++;
            }
            tabbedPane.insertTab("ML - Incidents", null, incidentJPanelML, null, tabIdx);
        } else {
            tabbedPane.remove(incidentJPanelML);
        }
    }//GEN-LAST:event_incidentMLCheckItemStateChanged

    private void configRNGOptions() {
        if (userSeedRB.isSelected()) {
            valueTextField.setVisible(true);
            valueTextField.requestFocusInWindow();
        } else {
            valueTextField.setVisible(false);
        }
        jPanel1.revalidate();
        jPanel1.repaint();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox analysisYear;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel changeNotAppliedLabel;
    private javax.swing.JLabel currSeedLabel;
    private javax.swing.JPanel datePanel;
    private GUI.RLHelper.ManagedLanes.DemandJPanelML demandJPanelML;
    private GUI.RLHelper.DemandJPanel demandPanel;
    private javax.swing.JButton discardDateButton;
    private javax.swing.JComboBox endDay;
    private javax.swing.JComboBox endMonth;
    private javax.swing.JButton genAndRunScenariosButton;
    private javax.swing.JButton generateScenariosButton;
    private javax.swing.JCheckBox incidentCheck;
    private GUI.RLHelper.ManagedLanes.IncidentJPanelML incidentJPanelML;
    private javax.swing.JCheckBox incidentMLCheck;
    private GUI.RLHelper.IncidentJPanel incidentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton newSeedRB;
    private javax.swing.JTextField numReplicatesTF;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JLabel prevRNGSeedLabel;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JComboBox seedDay;
    private javax.swing.JRadioButton seedFileSeedRB;
    private javax.swing.JComboBox seedMonth;
    private javax.swing.JButton setPeriodButton;
    private javax.swing.JComboBox startDay;
    private javax.swing.JComboBox startMonth;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JRadioButton userSeedRB;
    private javax.swing.JTextField valueTextField;
    private javax.swing.JCheckBox weatherCheck;
    private GUI.RLHelper.WeatherJPanel weatherPanel;
    private GUI.RLHelper.WorkZonePanel workZonePanel;
    private javax.swing.JCheckBox wzCheck;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
