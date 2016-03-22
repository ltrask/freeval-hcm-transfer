package GUI.major;

import GUI.ATDMHelper.ATDMBatchRunDialog;
import GUI.ATDMHelper.ATDMDeleteSelectionDialog;
import GUI.ATDMHelper.ATDMScenarioSelectorDialog;
import GUI.ATDMHelper.summary.ATDMSetSummaryDialog;
import GUI.ATDMHelper.summary.SummaryTypeSelectionDialog;
import GUI.RLHelper.ScenarioGeneratorDialog;
import GUI.RLHelper.summary.RLBatchRunDialog;
import GUI.RLHelper.summary.RLSummaryDialog;
import GUI.adaptiveRampMetering.AdaptiveRampMeteringDialog;
import GUI.major.floatingWindowHelper.CreateFloatingWindowDialog;
import GUI.major.floatingWindowHelper.FloatingWindow;
import GUI.major.graphicHelper.PeriodBinding;
import GUI.major.menuHelper.AboutDialog;
import GUI.seedEditAndIOHelper.ConfigIO;
import GUI.seedEditAndIOHelper.ExcelAdapter;
import GUI.seedEditAndIOHelper.SeedFillDataDialog;
import GUI.seedEditAndIOHelper.SeedGlobalDialog;
import GUI.seedEditAndIOHelper.SeedIOHelper;
import GUI.settingHelper.GraphicSettingDialog;
import GUI.settingHelper.TableSettingDialog;
import coreEngine.Helper.ASCIIAdapter.ASCIISeedFileAdapter_GPMLFormat;
import coreEngine.Helper.ASCIIAdapter.ASCIISeedFileAdapter_RLFormat;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.Scenario;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import main.FREEVAL_HCM;

/**
 * This class is the main window of FREEVAL. All seeds are contained in this
 * class. All central control functions are in this class.
 *
 * @author Shu Liu
 */
public class MainWindow extends javax.swing.JFrame {

    private static int MAX_NUM_PERIODS;

    private static int MAX_NUM_SEGMENTS;

    private static MainWindow mainWindow;

    private static String log = "";

    private ArrayList<Seed> seedList = new ArrayList<>();

    private HashMap<Seed, FREEVALProject> toolboxAuxHash = new HashMap<>();

    private Seed activeSeed;

    private int activeScen = 0;

    private int activePeriod = 0;

    private int activeATDM = -1;

    private boolean isShowingInput = true;

    private boolean isShowingOutput = false;

    private boolean numPeriodChanged = false;

    private boolean isOutputEnabled = true;

    private boolean showDebugOutput = true;

    /**
     * Default table font
     */
    public static final Font DEFAULT_TABLE_FONT = new Font("Arial", Font.PLAIN, 17);

    private static Font tableFont = DEFAULT_TABLE_FONT;

    /**
     * Default log font
     */
    public static final Font DEFAULT_LOG_FONT = new Font("Arial", Font.PLAIN, 13);

    private static Font logFont = DEFAULT_LOG_FONT;

    // Active Period Bindings
    private final ArrayList<PeriodBinding> activePeriodBindings = new ArrayList();

    //<editor-fold defaultstate="collapsed" desc="Toolbox Fields">
    public static final String TOOLBOX_RL_ATDM = "TOOLBOX_RL_ATDM";

    public static final String TOOLBOX_DSS = "TOOLBOX_DSS";
    //private MainWindowDSS mainWindowDSS = null;

    public static final String TOOLBOX_WZ = "TOOLBOX_WZ";
    //private MainWindowWZ mainWindowWZ = null;

    private String currToolbox = TOOLBOX_RL_ATDM;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CONSTRUCTOR">
    /**
     * Constructor. Creates new form mainWindow
     */
    public MainWindow() {

        mainWindow = this;

        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }

        //ArrayList<ImageIcon> icons = new ArrayList<>();
        List<Image> icons = new ArrayList<>();
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo.png"))).getImage());
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo16.png"))).getImage());
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo32.png"))).getImage());
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo64.png"))).getImage());
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo128.png"))).getImage());
        icons.add((new ImageIcon(getClass().getResource("/GUI/iconHelper/freeval-logo256.png"))).getImage());
        this.setIconImages(icons);

        initComponents();

        toolbox = new ToolboxFREEVAL();
        jScrollPane2.setViewportView(toolbox);

        setLocationRelativeTo(this.getRootPane()); //center starting position

        connect();

        update();

        setVisible(true);
        toFront();

        tableDisplay.setCellSettings(ConfigIO.loadTableConfig(this));
        graphicDisplay.setScaleColors(ConfigIO.loadGraphicConfig(this));

        configVersion();

        //load last opened files
        try {
            //remove .jar file at the tail for some platform
            String classFileName = FREEVAL_HCM.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
            URI settingFileName = new URI(classFileName.substring(0, classFileName.lastIndexOf("/") + 1) + "cfg/seedlist.cfg");

            File seedListFile = new File(settingFileName);
            if (seedListFile.exists()) {
                Scanner fileScanner = new Scanner(seedListFile);
                if (fileScanner.hasNextLine()) {
                    int result = JOptionPane.showConfirmDialog(this, "Do you want to load last opened files?",
                            "Load Last Opened Files", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        ConfigIO.loadSeedListFromConfig(this);
                    }
                }
                fileScanner.close();
            }
        } catch (Exception e) {
            printLog("Error when load last opened seed " + e.toString());
        }
    }

    private void configVersion() {
        MAX_NUM_PERIODS = 96;
        MAX_NUM_SEGMENTS = 500;
    }

    /**
     * Connect major components for central control
     */
    private void connect() {
        menuBar.setMainWindow(this);
        toolbox.setMainWindow(this);
        apControlPanel.setMainWindow(this);
        graphicDisplay.setMainWindow(this);
        navigator.setMainWindow(this);
        tableDisplay.setMainWindow(this);
        comparePanel.setMainWindow(this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FLOATING WINDOW">
    private static final String TOOLBOX = "Toolbox";

    private static final String CONTOUR = "Result Contours";

    private static final String COMPARE = "Compare";

    private static final String SINGLE = "Single Seed/Scenario I/O";

    private static final String PERIOD = "Analysis Period Summary";

    private static final String SEGMENT = "Segment & Facility Summary";

    private static final String LOG = "Log";

    private static final String NAVIGATOR = "Navigator";

    private boolean isToolboxInFloat = false;

    private boolean isContourInFloat = false;

    private boolean isCompareInFloat = false;

    private boolean isSingleInFloat = false;

    private boolean isLogInFloat = false;

    private boolean isNavigatorInFloat = false;

    private boolean isPeriodInFloat = false;

    private boolean isSegmentInFloat = false;

    private boolean isPeriodInFloat_ML = false;

    private boolean isSegmentInFloat_ML = false;

    FloatingWindow floatingWindowToolbox;

    FloatingWindow floatingWindowContour;

    FloatingWindow floatingWindowCompare;

    FloatingWindow floatingWindowSingle;

    FloatingWindow floatingWindowLog;

    FloatingWindow floatingWindowNavigator;

    FloatingWindow floatingWindowPeriod;

    FloatingWindow floatingWindowSegment;

    FloatingWindow floatingWindowPeriod_ML;

    FloatingWindow floatingWindowSegment_ML;

    /**
     * Create floating window
     */
    public void createFloatingWindow() {
        CreateFloatingWindowDialog dlg = new CreateFloatingWindowDialog(this);
        dlg.setLocationRelativeTo(this.getRootPane());
        dlg.setVisible(true);

        if (dlg.getReturnStatus() == CreateFloatingWindowDialog.RET_OK) {
            if (dlg.isToolboxSelected() && !isToolboxInFloat) {
                isToolboxInFloat = true;
                floatingWindowToolbox = new FloatingWindow(this, toolbox);
                floatingWindowToolbox.setTitle(TOOLBOX);
                floatingWindowToolbox.setLocationRelativeTo(this.getRootPane());
                toolboxSplitPanel.setDividerLocation(0);
                floatingWindowToolbox.setVisible(true);
            }

            if (!dlg.isToolboxSelected() && isToolboxInFloat) {
                floatingWindowToolbox.dispose();
            }

            if (dlg.isSingleSelected() && !isSingleInFloat) {
                isSingleInFloat = true;
                floatingWindowSingle = new FloatingWindow(this, singleScenSplitPanel);
                floatingWindowSingle.setTitle(SINGLE);
                floatingWindowSingle.setLocationRelativeTo(this.getRootPane());
                tabPanel.remove(singleScenSplitPanel);
                floatingWindowSingle.setVisible(true);
            }

            if (!dlg.isSingleSelected() && isSingleInFloat) {
                floatingWindowSingle.dispose();
            }

            if (dlg.isCompareSelected() && !isCompareInFloat) {
                isCompareInFloat = true;
                floatingWindowCompare = new FloatingWindow(this, comparePanel);
                floatingWindowCompare.setTitle(COMPARE);
                floatingWindowCompare.setLocationRelativeTo(this.getRootPane());
                tabPanel.remove(comparePanel);
                floatingWindowCompare.setVisible(true);
            }

            if (!dlg.isCompareSelected() && isCompareInFloat) {
                floatingWindowCompare.dispose();
            }

            if (dlg.isContourSelected() && !isContourInFloat) {
                isContourInFloat = true;
                floatingWindowContour = new FloatingWindow(this, contourPanel);
                floatingWindowContour.setTitle(CONTOUR);
                floatingWindowContour.setLocationRelativeTo(this.getRootPane());
                tabPanel.remove(contourPanel);
                floatingWindowContour.setVisible(true);
            }

            if (!dlg.isContourSelected() && isContourInFloat) {
                floatingWindowContour.dispose();
            }

            if (dlg.isPeriodSelected() && !isPeriodInFloat) {
                isPeriodInFloat = true;
                floatingWindowPeriod = new FloatingWindow(this, periodSummaryPanel);
                floatingWindowPeriod.setTitle(PERIOD);
                floatingWindowPeriod.setLocationRelativeTo(this.getRootPane());
                tabPanel.remove(periodSummaryPanel);
                floatingWindowPeriod.setVisible(true);
            }

            if (!dlg.isPeriodSelected() && isPeriodInFloat) {
                floatingWindowPeriod.dispose();
            }

            if (dlg.isSegmentSelected() && !isSegmentInFloat) {
                isSegmentInFloat = true;
                floatingWindowSegment = new FloatingWindow(this, segmentSummaryPanel);
                floatingWindowSegment.setTitle(SEGMENT);
                floatingWindowSegment.setLocationRelativeTo(this.getRootPane());
                tabPanel.remove(segmentSummaryPanel);
                floatingWindowSegment.setVisible(true);
            }

            if (!dlg.isSegmentSelected() && isSegmentInFloat) {
                floatingWindowSegment.dispose();
            }

            if (dlg.isNavigatorSelected() && !isNavigatorInFloat) {
                isNavigatorInFloat = true;
                floatingWindowNavigator = new FloatingWindow(this, navigator);
                floatingWindowNavigator.setTitle(NAVIGATOR);
                floatingWindowNavigator.setLocationRelativeTo(this.getRootPane());
                logSplitPanel.setDividerLocation(0);
                //navigatorSplitPanel.setDividerLocation(0);
                floatingWindowNavigator.setVisible(true);
            }

            if (!dlg.isNavigatorSelected() && isNavigatorInFloat) {
                floatingWindowNavigator.dispose();
            }

            if (dlg.isLogSelected() && !isLogInFloat) {
                isLogInFloat = true;
                floatingWindowLog = new FloatingWindow(this, logScrollPanel);
                floatingWindowLog.setTitle(LOG);
                floatingWindowLog.setLocationRelativeTo(this.getRootPane());
                logSplitPanel.setDividerLocation(logSplitPanel.getMaximumDividerLocation());
                floatingWindowLog.setVisible(true);
            }

            if (!dlg.isLogSelected() && isLogInFloat) {
                floatingWindowLog.dispose();
            }
        }
    }

    /**
     * Put component back after floating window closed
     *
     * @param title title of the closed floating window (as identifier)
     */
    public void floatingWindowClosed(String title) {
        switch (title) {
            case TOOLBOX:
                isToolboxInFloat = false;
                toolboxSplitPanel.setLeftComponent(toolbox);
                toolboxSplitPanel.setDividerLocation(toolbox.getPreferredSize().height);
                break;
            case SINGLE:
                isSingleInFloat = false;
                //tabPanel.addTab("Single Scenario", singleScenSplitPanel);
                tabPanel.insertTab(SINGLE, null, singleScenSplitPanel, null, 0);
                tabPanel.setSelectedComponent(singleScenSplitPanel);
                break;
            case COMPARE:
                isCompareInFloat = false;
                tabPanel.addTab(COMPARE, comparePanel);
                tabPanel.setSelectedComponent(comparePanel);
                break;
            case CONTOUR:
                isContourInFloat = false;
                if (isOutputEnabled) {
                    tabPanel.addTab(CONTOUR, contourPanel);
                    tabPanel.setSelectedComponent(contourPanel);
                }
                break;
            case PERIOD:
                isPeriodInFloat = false;
                if (isOutputEnabled) {
                    tabPanel.addTab(PERIOD, periodSummaryPanel);
                    tabPanel.setSelectedComponent(periodSummaryPanel);
                }
                break;
            case SEGMENT:
                isSegmentInFloat = false;
                if (isOutputEnabled) {
                    tabPanel.addTab(SEGMENT, segmentSummaryPanel);
                    tabPanel.setSelectedComponent(segmentSummaryPanel);
                }
                break;
            case NAVIGATOR:
                isNavigatorInFloat = false;
                logSplitPanel.setLeftComponent(navigator);
                logSplitPanel.setDividerLocation(isLogInFloat ? logSplitPanel.getMaximumDividerLocation() : logSplitPanel.getMaximumDividerLocation() - 80);
                //navigatorSplitPanel.setDividerLocation(navigator.getPreferredSize().width);
                break;
            case LOG:
                isLogInFloat = false;
                logSplitPanel.setRightComponent(logScrollPanel);
                logSplitPanel.setDividerLocation(isNavigatorInFloat ? 0 : logSplitPanel.getMaximumDividerLocation() - 80);
                break;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CENTRAL CONTROL FUNCTIONS">
    // <editor-fold defaultstate="collapsed" desc="SEED IO FUNCTIONS">
    /**
     * Create a new seed
     */
    public void newSeed() {
        SeedGlobalDialog seedCreaterDialog = new SeedGlobalDialog(null, this);
        seedCreaterDialog.setVisible(true);
        Seed seed = seedCreaterDialog.getSeed();
        if (seed != null) {
            addSeed(seed);
            printLog("New seed created");
        }
    }

    /**
     * Opens the seed creator dialog with the specified project name.
     *
     * @param initialProjectName
     */
    public void newSeed(String initialProjectName) {
        SeedGlobalDialog seedCreaterDialog = new SeedGlobalDialog(null, this);
        seedCreaterDialog.setIncomingProjectName(initialProjectName);
        seedCreaterDialog.setVisible(true);
        Seed seed = seedCreaterDialog.getSeed();
        if (seed != null) {
            addSeed(seed);
            printLog("New Project created");
        }
    }

    /**
     * Open a .seed file
     */
    public void openSeed() {
        if (FREEVAL_HCM.LICENSE_VERSION.equalsIgnoreCase("DEVELOP")) {
            Seed[] seeds = SeedIOHelper.openMultiSeed(this);
            if (seeds != null) {
                for (Seed seed : seeds) {
                    if (seed != null) {
                        if (openedSeed(seed)) {
                            JOptionPane.showMessageDialog(this, "This seed file is already opened", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            addSeed(seed);
                        }
                    } else {
                        printLog("Fail to open seed");
                    }
                }
            }
        } else {
            Seed seed = SeedIOHelper.openSeed(this);
            if (seed != null) {
                if (openedSeed(seed)) {
                    JOptionPane.showMessageDialog(this, "This seed file is already opened", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    addSeed(seed);
                }
            } else {
                //printLog("Fail to open seed");
            }
        }
    }

    /**
     * Check whether a seed file is ready opened
     *
     * @param seed seed to be checked
     * @return whether a seed file is ready opened
     */
    public boolean openedSeed(Seed seed) {
        for (Seed openedSeed : seedList) {
            try {
                if (openedSeed.getValueString(CEConst.IDS_SEED_FILE_NAME).equals(seed.getValueString(CEConst.IDS_SEED_FILE_NAME))
                        && mainWindow.getToolboxAuxHash().get(seed).PROJECT_TYPE.equalsIgnoreCase(MainWindow.TOOLBOX_RL_ATDM)) {
                    return true;
                }
            } catch (Exception e) {
                //skip this seed
            }
        }
        return false;
    }

    /**
     * Save active seed to file
     */
    public void saveSeed() {
        saveSeed(activeSeed);
    }

    /**
     * Save a seed to file
     *
     * @param seed seed to be saved
     */
    public void saveSeed(Seed seed) {
        printLog(SeedIOHelper.saveSeed(this, seed));
        update();
    }

    /**
     * Save active seed to another seed file
     */
    public void saveAsSeed() {
        printLog(SeedIOHelper.saveAsSeed(this, activeSeed));
        update();
    }

    /**
     * Close active seed (and corresponding project)
     *
     * @param askForSave True if the user should be prompted if they want to
     * save the project
     * @return true if seed closed successfully, false if canceled by user or
     * failed to close
     */
    public boolean closeActiveProject(boolean askForSave) {
        String outputString = navigator.closeActiveProject(askForSave);
        printLog(outputString);
        return outputString.contains("Seed closed");
    }

    /**
     * Closes the specified seed and corresponding project.
     *
     * @param seed Seed to close
     * @param askForSave Boolean true if the user should be prompted to save
     * before closing
     * @return
     */
    public boolean closeSeed(Seed seed, boolean askForSave) {
        navigator.closeProjectBySeed(seed, askForSave);
        return true;
    }

    /**
     * Import a seed from ASCII file
     */
    public void importASCII() {
        try {
            final JFileChooser fc = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
            fc.setDialogTitle("Open - Import from ASCII FIle");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                Scanner input = new Scanner(new File(fc.getSelectedFile().getAbsolutePath()));
                String firstLine = input.nextLine();
                input.close();
                //choose correct ASCII adapter based on ASCII input file format
                if (firstLine.startsWith("<")) {
                    ASCIISeedFileAdapter_GPMLFormat textSeed = new ASCIISeedFileAdapter_GPMLFormat();
                    Seed _seed = textSeed.importFromASCII(fc.getSelectedFile().getAbsolutePath());
                    if (_seed != null) {
                        printLog("Seed added from ASCII file : " + _seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                        _seed.setValue(CEConst.IDS_SEED_FILE_NAME, null);
                        addSeed(_seed);
                    } else {
                        printLog("Fail to import ASCII file");
                    }
                } else {
                    ASCIISeedFileAdapter_RLFormat textSeed = new ASCIISeedFileAdapter_RLFormat();
                    Seed _seed = textSeed.importFromFile(fc.getSelectedFile().getAbsolutePath());
                    if (_seed != null) {
                        printLog("Seed added from ASCII file : " + _seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                        _seed.setValue(CEConst.IDS_SEED_FILE_NAME, null);
                        addSeed(_seed);
                    } else {
                        printLog("Fail to import ASCII file");
                    }
                }

            }
        } catch (Exception ex) {
            printLog(ex.toString());
            return;
        }
    }

    /**
     * Export active seed to ASCII file
     */
    public void exportASCII() {
        if (activeSeed != null) {
            ASCIISeedFileAdapter_GPMLFormat exporter = new ASCIISeedFileAdapter_GPMLFormat();
            String fileName = exporter.exportToASCII(activeSeed, this);
            //ASCIISeedFileAdapter exporter = new ASCIISeedFileAdapter();
            //String fileName = exporter.exportToFile(activeSeed);
            if (fileName != null) {
                printLog("Exported seed to ASCII file : " + fileName);
            } else {
                printLog("Fail to exported seed to ASCII file");
            }
        }
    }

    /**
     * Add a seed to seed list
     *
     * @param seed seed to be added
     */
    public void addSeed(Seed seed) {
        if (seed.getValueInt(CEConst.IDS_NUM_SEGMENT) > MAX_NUM_SEGMENTS || seed.getValueInt(CEConst.IDS_NUM_PERIOD) > MAX_NUM_PERIODS) {
            JOptionPane.showMessageDialog(this, "This FREEVAL is limited to upto " + MAX_NUM_SEGMENTS
                    + " segments and " + MAX_NUM_PERIODS + " analysis periods.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            seedList.add(seed);
            showInput();
            toolboxAuxHash.put(seed, new RLATDMProject(seed));
            navigator.projectAdded(seed);
        }
    }

    /**
     * Add a seed to seed list
     *
     * @param seed seed to be added
     * @param toolboxAuxObject Auxiliary toolbox object for the seed
     */
    public void addToolboxProject(Seed seed, FREEVALProject toolboxAuxObject) {
        if (seed.getValueInt(CEConst.IDS_NUM_SEGMENT) > MAX_NUM_SEGMENTS || seed.getValueInt(CEConst.IDS_NUM_PERIOD) > MAX_NUM_PERIODS) {
            JOptionPane.showMessageDialog(this, "This FREEVAL is limited to upto " + MAX_NUM_SEGMENTS
                    + " segments and " + MAX_NUM_PERIODS + " analysis periods.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            seedList.add(seed);
            toolboxAuxHash.put(seed, toolboxAuxObject);
            navigator.projectAdded(seed, currToolbox);
        }
    }

    /**
     * Set the auxiliary toolbox object for the specified seed
     *
     * @param seed
     * @param toolboxAuxObject
     */
    public void setToolboxAuxObjectForSeed(Seed seed, FREEVALProject toolboxAuxObject) {
        toolboxAuxHash.put(seed, toolboxAuxObject);
    }

    /**
     * Edit Adaptive Ramp Metering
     */
    public void editAdaptiveRampMetering() {
        if (activeSeed != null) {
            AdaptiveRampMeteringDialog adaptiveRampMeteringDialog = new AdaptiveRampMeteringDialog(activeSeed, this);
            adaptiveRampMeteringDialog.setVisible(true);

            updateSeed();
            update();
        }
    }

    /**
     * Show global input for active seed
     */
    public void globalInput() {
        if (activeSeed != null) {
            SeedGlobalDialog seedCreaterDialog = new SeedGlobalDialog(activeSeed, this);
            seedCreaterDialog.setVisible(true);

            updateSeed();
            update();
        }
    }

    /**
     * Show fill data dialog
     */
    public void fillData() {
        if (activeSeed != null) {
            SeedFillDataDialog fillDataDialog = new SeedFillDataDialog(activeSeed, this);
            fillDataDialog.setVisible(true);

            updateSeed();
            update();
        }
    }

    /**
     * Copy table in table display to clipboard
     *
     * @param firstColumnTable first column of the split table
     * @param restColumnTable rest of the columns of the split table
     */
    public void copyTable(JTable firstColumnTable, JTable restColumnTable) {
        printLog(ExcelAdapter.copySplitTable(firstColumnTable, restColumnTable));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DISPLAY CONTROL FUNCTIONS">
    /**
     * Update display when seed and scenario selected
     *
     * @param seed seed selected
     * @param scen index of the selected RL scenario
     */
    public void selectSeedScen(Seed seed, int scen) {
        selectSeedScen(seed, scen, -1);
    }

    /**
     * Update display when seed and scenario selected
     *
     * @param seed seed selected
     * @param scen index of the selected RL scenario
     * @param atdm index of the selected ATDM scenario
     */
    public void selectSeedScen(Seed seed, int scen, int atdm) {
        if (activeScen == scen && activeATDM == atdm
                && activeSeed != null && seed != null && activeSeed.equals(seed)) {
            return;
        }

        try {
            if (seed == null || scen < 0 || scen > seed.getValueInt(CEConst.IDS_NUM_SCEN)) {
                activeScen = 0;
            } else {
                activeScen = scen;
            }

            activeATDM = atdm;

            if (activeSeed == null || !activeSeed.equals(seed)) {
                activeSeed = seed;
                numPeriodChanged = true;
                selectPeriod(0);
                updateTitle();
            }

            if (activeSeed != null) {
                printLog(seed.toString() + " Scen#" + activeScen
                        + (activeATDM >= 0 ? " ATDM#" + (activeATDM + 1) : "") + " selected");

                tableDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);
                graphicDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);
                contourPanel.selectSeedScenATDM(activeSeed, activeScen, activeATDM);
                periodSummaryPanel.selectSeedScenATDM(activeSeed, activeScen, activeATDM);
                segmentSummaryPanel.selectSeedScenATDM(activeSeed, activeScen, activeATDM);
                //toolbox.selectSeedScen(activeSeed, activeScen, activeATDM, activePeriod);
            } else {
                tableDisplay.selectSeedScenATDMPeriod(null, 0, - 1, 0);
                graphicDisplay.selectSeedScenATDMPeriod(null, 0, -1, 0);
                contourPanel.selectSeedScenATDM(null, 0, -1);
                periodSummaryPanel.selectSeedScenATDM(null, 0, -1);
                segmentSummaryPanel.selectSeedScenATDM(null, 0, -1);
            }
            update();
        } catch (Exception e) {
            e.printStackTrace();
            printLog("Error in selectSeedScen : " + e.toString());
        }
    }

    private void setNonNullSeed() {
        apControlPanel.setNonNullSeed();
    }

    private void setNullSeed() {
        apControlPanel.setNullSeed();
    }

    private boolean hasOutput() {
        return activeSeed.hasValidOutput(activeScen, activeATDM);
    }

    /**
     * Configure show input in table display
     */
    public void showInput() {
        isShowingInput = true;
        isShowingOutput = false;
        tableDisplay.showInput();
        apControlPanel.setIsShowingInput();
    }

    /**
     * Configure show output in table display
     */
    public void showOutput() {
        isShowingInput = false;
        isShowingOutput = true;
        tableDisplay.showOutput();
        apControlPanel.setIsShowingOutput();
    }

    /**
     * Configure show input and output in table display
     */
    public void showInputAndOutput() {
        isShowingInput = true;
        isShowingOutput = true;
        tableDisplay.showInputAndOutput();
        apControlPanel.setIsShowingInputAndOutput();

    }

    /**
     * Configure whether or not computed downstream values are shown in the
     * single seed/scenario IO table.
     *
     * @param toggle True if shown, false if hidden
     */
    public void toggleShowComputedDownstreamValues(boolean toggle) {
        tableDisplay.toggleShowComputedDownstreamValues(toggle);
    }

    public void enableOutput() {
        isOutputEnabled = true;
        menuBar.enableOutput();

        apControlPanel.enableOutput();

        if (isContourInFloat) {
            floatingWindowContour.setVisible(true);
        } else {
            if (tabPanel.indexOfComponent(contourPanel) < 0) {
                tabPanel.addTab(CONTOUR, contourPanel);
            }
        }

        if (isPeriodInFloat) {
            floatingWindowPeriod.setVisible(true);
        } else {
            if (tabPanel.indexOfComponent(periodSummaryPanel) < 0) {
                tabPanel.addTab(PERIOD, periodSummaryPanel);
            }
        }

        if (isSegmentInFloat) {
            floatingWindowSegment.setVisible(true);
        } else {
            if (tabPanel.indexOfComponent(segmentSummaryPanel) < 0) {
                tabPanel.addTab(SEGMENT, segmentSummaryPanel);
            }
        }
    }

    public void disableOutput() {
        isOutputEnabled = false;
        showInput();
        menuBar.disableOutput();

        apControlPanel.disableOutput();
        if (isContourInFloat) {
            floatingWindowContour.setVisible(false);
        } else {
            if (tabPanel.indexOfComponent(contourPanel) >= 0) {
                if (tabPanel.getSelectedComponent() == contourPanel) {
                    tabPanel.setSelectedComponent(singleScenSplitPanel);
                }
                tabPanel.remove(contourPanel);
            }
        }

        if (isPeriodInFloat) {
            floatingWindowPeriod.setVisible(false);
        } else {
            if (tabPanel.indexOfComponent(periodSummaryPanel) >= 0) {
                if (tabPanel.getSelectedComponent() == periodSummaryPanel) {
                    tabPanel.setSelectedComponent(singleScenSplitPanel);
                }
                tabPanel.remove(periodSummaryPanel);
            }
        }

        if (isSegmentInFloat) {
            floatingWindowSegment.setVisible(false);
        } else {
            if (tabPanel.indexOfComponent(segmentSummaryPanel) >= 0) {
                if (tabPanel.getSelectedComponent() == segmentSummaryPanel) {
                    tabPanel.setSelectedComponent(singleScenSplitPanel);
                }
                tabPanel.remove(segmentSummaryPanel);
            }
        }
    }

    private void enableManagedLane() {
        apControlPanel.enableManagedLane();
        toolbox.enableML();
        menuBar.enableML();
        configGPMLDisplay();
    }

    private void disableManagedLane() {
        apControlPanel.disableManagedLane();
        toolbox.disableML();
        menuBar.disableML();
        configGPMLDisplay();
    }

    private void enableRL() {
        toolbox.enableRL();
        menuBar.enableRL();
    }

    private void disableRL() {
        toolbox.disableRL();
        menuBar.disableRL();
    }

    private void enableATDM() {
        toolbox.enableATDM();
        menuBar.enableATDM();
    }

    private void disableATDM() {
        toolbox.disableATDM();
        menuBar.disableATDM();
    }

    /**
     * Show table settings window
     */
    public static void showTableSettings() {
        TableSettingDialog settingDialog = new TableSettingDialog(mainWindow, mainWindow.tableDisplay.getSegIOTable());
        settingDialog.setVisible(true);

        mainWindow.update();
    }

    /**
     * Show graphic settings window
     */
    public static void showGraphicSettings() {
        GraphicSettingDialog settingDialog = new GraphicSettingDialog(mainWindow, mainWindow.graphicDisplay);
        settingDialog.setVisible(true);

        mainWindow.update();
    }

    /**
     * Show reliability analysis summary
     */
    public void showRLSummary() {
        if (checkRLHasFullResult()) {
            RLSummaryDialog RLSummaryDialog = new RLSummaryDialog(activeSeed, this);
            RLSummaryDialog.setVisible(true);
        }
    }

    /**
     * Show reliability analysis summary
     */
    public void showATDMSummary() {
        if (checkATDMHasFullResult()) {
            SummaryTypeSelectionDialog summaryTypeSelectionDialog = new SummaryTypeSelectionDialog(this, activeSeed);
            summaryTypeSelectionDialog.setVisible(true);

            if (summaryTypeSelectionDialog.getReturnStatus() == SummaryTypeSelectionDialog.RET_OK) {
                int atdmIndex = summaryTypeSelectionDialog.getAtdmSetSelected();
                ATDMSetSummaryDialog atdmSetSummaryDialog;
                switch (summaryTypeSelectionDialog.getAtdmSummaryType()) {
                    case SummaryTypeSelectionDialog.ATDM_SINGLE_SET_SET_ONLY:
                        atdmSetSummaryDialog = new ATDMSetSummaryDialog(activeSeed, atdmIndex, true, this);
                        atdmSetSummaryDialog.setVisible(true);
                        break;
                    case SummaryTypeSelectionDialog.ATDM_SINGLE_SET_ALL:
                        runBatchRL();
                        atdmSetSummaryDialog = new ATDMSetSummaryDialog(activeSeed, atdmIndex, false, this);
                        atdmSetSummaryDialog.setVisible(true);
                        break;
                }
            }
        }
    }

    private boolean checkRLHasFullResult() {
        if (activeSeed != null) {
            if (activeSeed.getValueInt(CEConst.IDS_NUM_SCEN) >= 1) {
                for (int scen = 1; scen <= activeSeed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
                    if (!activeSeed.hasValidOutput(scen, -1)) {
                        //not enough result
                        runBatchRL();
                        return true;
                    }
                }
                return true;
            } else {
                //no scenarios
                JOptionPane.showMessageDialog(this, "No scenario in selected seed. Please generate scenarios first.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            //null seed
            JOptionPane.showMessageDialog(this, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean checkATDMHasFullResult() {
        if (activeSeed == null) {
            //null seed
            JOptionPane.showMessageDialog(this, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (activeSeed.getValueInt(CEConst.IDS_NUM_SCEN) <= 0) {
            //no scenarios
            JOptionPane.showMessageDialog(this, "No scenario in selected seed. Please generate scenario first.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (activeSeed.getValueInt(CEConst.IDS_ATDM_SET_NUM) <= 0) {
            //no ATDM
            JOptionPane.showMessageDialog(this, "No ATDM in selected seed. Please assign ATDM first.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        for (int atdm = 0; atdm < activeSeed.getValueInt(CEConst.IDS_ATDM_SET_NUM); atdm++) {
            for (int scen : activeSeed.getATDMSets().get(atdm).keySet()) {
                if (!activeSeed.hasValidOutput(scen, atdm)) {
                    runBatchATDM();
                    return true;
                }
            }
        }

        return true;
    }

    /**
     * Print log message in log display area in main window
     *
     * @param msg message to be print
     */
    public static void printLog(String msg) {
        if (msg.toLowerCase().contains("error") || msg.contains("Exception") || msg.toLowerCase().contains("fail") || msg.toLowerCase().contains("warning")) {
            msg = "<font color=\"red\">" + msg + "</font>";
        } else if (msg.toLowerCase().contains("finish")) {
            msg = "<font color=\"blue\">" + msg + "</font>";
        } else if (msg.toLowerCase().contains("selected")) {
            msg = "<font color=\"green\">" + msg + "</font>";
        }

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(now);
        if (log.equals("")) {
            log = "<b>" + time + "</b> " + msg;
        } else {
            log += "<br><b>" + time + "</b> " + msg;
        }
        mainWindow.logText.setFont(logFont);
        mainWindow.logText.setText(log);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Point point = mainWindow.logScrollPanel.getViewport().getViewPosition();
                point.x = 0;
                mainWindow.logScrollPanel.getViewport().setViewPosition(point);
            }
        });
    }

    /**
     * Update date main window title
     */
    public void updateTitle() {
        String toolboxTitle = "FREEVAL-2015e";
        switch (currToolbox) {
            default:
            case TOOLBOX_RL_ATDM:
                toolboxTitle = "FREEVAL-2015e";
                break;
            case TOOLBOX_DSS:
                toolboxTitle = "FREEVAL-DSS (ADMIN)";
                break;
            case TOOLBOX_WZ:
                toolboxTitle = "FREEVAL-WZ";
                break;

        }
        if (activeSeed == null) {
            //this.setTitle("FREEVAL-2015e");
            this.setTitle(toolboxTitle);
        } else {
            if (activeSeed.getValueString(CEConst.IDS_SEED_FILE_NAME) == null) {
                this.setTitle(toolboxTitle + " - Not Saved New File");
            } else {
                this.setTitle(toolboxTitle + " - " + activeSeed.getValueString(CEConst.IDS_SEED_FILE_NAME));
            }
        }
    }

    /**
     * Show first analysis period data
     */
    public void showFirstPeriod() {
        selectPeriod(0);
    }

    /**
     * Show last analysis period data
     */
    public void showLastPeriod() {
        if (activeSeed != null) {
            selectPeriod(activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD) - 1);
        }
    }

    /**
     * Show previous analysis period data
     */
    public void showPrevPeriod() {
        selectPeriod(activePeriod - 1);
    }

    /**
     * Show next analysis period data
     */
    public void showNextPeriod() {
        selectPeriod(activePeriod + 1);
    }

    /**
     * Show a particular analysis period data
     *
     * @param period index of period selected
     */
    public void selectPeriod(int period) {
        if (activeSeed == null) {
            period = -1;
        } else {
            if (period >= activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD)) {
                period = 0;
            } else {
                if (period < 0) {
                    period = activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD) - 1;
                }
            }
        }

        if (activePeriod != period || numPeriodChanged) {
            activePeriod = period;
            numPeriodChanged = false;
            tableDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);
            graphicDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);

            apControlPanel.selectPeriod(period);

            if (activePeriod >= 0) {
                printLog("Analysis period " + (period + 1) + " selected");
            }
            updateActivePeriodBindings();
        }
    }

    /**
     * Show a particular analysis period data allowing the update to be forced.
     *
     * @param period index of period selected
     */
    public void selectPeriod(int period, boolean forcePeriodUpdate) {
        if (activeSeed == null) {
            period = -1;
        } else {
            if (period >= activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD)) {
                period = 0;
            } else {
                if (period < 0) {
                    period = activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD) - 1;
                }
            }
        }

        if (activePeriod != period || numPeriodChanged || forcePeriodUpdate) {
            activePeriod = period;
            numPeriodChanged = false;
            tableDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);
            graphicDisplay.selectSeedScenATDMPeriod(activeSeed, activeScen, activeATDM, activePeriod);

            apControlPanel.selectPeriod(period);

            if (activePeriod >= 0) {
                printLog("Analysis period " + (period + 1) + " selected");
            }
            updateActivePeriodBindings();
        }
    }

    private void updateActivePeriodBindings() {
        for (PeriodBinding binding : activePeriodBindings) {
            binding.selectPeriod(activePeriod);
        }
    }

    /**
     * Update display when a particular segment is selected by graphic display
     *
     * @param seg segment index (start with 0)
     */
    public void segmentSelectedByGraph(int seg) {
        tableDisplay.setHighlight(seg);
    }

    /**
     * Update display when a particular segment is selected by table display
     *
     * @param seg segment index (start with 0)
     */
    public void segmentSelectedByTable(int seg) {
        graphicDisplay.setHighlight(seg);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ANALYSIS CONTROL FUNCTIONS">
    /**
     * Run single scenario
     */
    public void runSingle() {
        printLog(runSeedSingle());
        update();
    }

    private String runSeedSingle() {
        if (activeSeed == null) {
            JOptionPane.showMessageDialog(this, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return "Fail to run seed (no seed is selected)";
        }

        //timing starts
        long timingStart = new Date().getTime();

        activeSeed.singleRun(activeScen, activeATDM);

        //timing ends
        long timingEnd = new Date().getTime();

        contourPanel.selectSeedScenATDM(activeSeed, activeScen, activeATDM);

        return ("Run seed finished ("
                + ((timingEnd - timingStart) > 1000
                        ? (timingEnd - timingStart) / 1000 + " s)"
                        : (timingEnd - timingStart) + " ms)"));
    }

    /**
     * Generate scenario
     */
    public void generateRL() {
        if (activeSeed != null) {
            activeSeed.singleRun(0, -1);
            if (ScenarioGeneratorDialog.validateSeedDataForReliabiityAnalysis(activeSeed)) {
                final ScenarioGeneratorDialog dlg = new ScenarioGeneratorDialog(activeSeed, this, true);
                dlg.setLocationRelativeTo(this.getRootPane());
                dlg.setVisible(true);
                if (dlg.scenariosGenerated()) {
                    Scenario scenariosGP = dlg.getScenariosGP();
                    Scenario scenariosML = dlg.getScenariosML(); //To Lake, change this line
                    printLog(scenariosGP.size() + " scenarios fully generated");
                    printLog(activeSeed.setRLScenarios(scenariosGP, scenariosML, dlg.getScenarioInfoList()));

                    if (dlg.toggleRun) {
                        runBatchRL();
                    }

                    updateSeed();
                    update();
                } else {
                    printLog("Scenarios not generated");
                }
                dlg.dispose();
            } else {
                int[] formatErrorLocation = ScenarioGeneratorDialog.getSeedDataFormatErrorLocation(activeSeed);
                JOptionPane.showMessageDialog(null, "<HTML><Center> The seed data "
                        + "contains format errors and thus cannot be used for "
                        + "reliability analysis.<br>This error indicates that for "
                        + "at least one segment, the number of lanes in the segment"
                        + " at a later period <br>"
                        + " differs from the number of lanes in the segment in the"
                        + " initial analysis period."
                        + "<br>&nbsp<br>"
                        + "Error found at Segment " + (formatErrorLocation[0] + 1)
                        + " and Analysis Period " + (formatErrorLocation[1] + 1) + ".",
                        "Seed Data Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Delete all scenarios (including RL and ATDM)
     */
    public void deleteAllScen() {
        if (activeSeed != null && activeSeed.getValueInt(CEConst.IDS_NUM_SCEN) > 0) {
            int n = JOptionPane.showConfirmDialog(this,
                    "Warning: Delete scenarios cannot be undone",
                    "Warning",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (n == JOptionPane.OK_OPTION) {
                activeSeed.cleanScenarios();
                updateSeed();
                update();
                printLog("All Scenarios Deleted");
            }
        }
    }

    /**
     * Run multiple RL scenarios
     */
    private void runBatchRL() {
        RLBatchRunDialog batchRunDialog = new RLBatchRunDialog(activeSeed, this);
        batchRunDialog.setVisible(true);
        if (hasOutput()) {
            enableOutput();
            showOutput();
        }
    }

    /**
     * Run multiple ATDM scenarios
     */
    private void runBatchATDM() {
        //TODO need to fix
        ATDMBatchRunDialog batchRunDialog = new ATDMBatchRunDialog(activeSeed, this);
        batchRunDialog.setVisible(true);
        if (hasOutput()) {
            enableOutput();
            showOutput();
        }
    }

    /**
     * Generate ATDM scenarios for active seed
     */
    public void generateATDM() {
        if (checkRLHasFullResult()) {
            ATDMScenarioSelectorDialog atdmScenarioSelectorDialog = new ATDMScenarioSelectorDialog(activeSeed, this, true);

            atdmScenarioSelectorDialog.setLocationRelativeTo(this.getRootPane());
            atdmScenarioSelectorDialog.setVisible(true);

            if (atdmScenarioSelectorDialog.atdmCompleted()) {
                updateSeed();
                update();
            }
            atdmScenarioSelectorDialog.dispose();
        }
    }

    /**
     * Delete all ATDM scenarios in active seed
     */
    public void deleteAllATDM() {
        ATDMDeleteSelectionDialog atdmDeleteSelectionDialog = new ATDMDeleteSelectionDialog(this, activeSeed);
        atdmDeleteSelectionDialog.setVisible(true);

        if (atdmDeleteSelectionDialog.getReturnStatus() == SummaryTypeSelectionDialog.RET_OK) {
            if (activeSeed != null && activeSeed.getValueInt(CEConst.IDS_NUM_SCEN) > 0) {
                int n = JOptionPane.showConfirmDialog(this,
                        "Warning: Delete ATDM cannot be undone",
                        "Warning",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (n == JOptionPane.OK_OPTION) {
                    int atdmIndex = atdmDeleteSelectionDialog.getAtdmSetSelected();

                    if (atdmIndex < 0) {
                        activeSeed.deleteAllATDM();
                    } else {
                        activeSeed.deleteATDMSet(atdmIndex);
                    }

                    updateSeed();
                    update();
                    printLog("All ATDM Deleted");
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SEED MODIFICATION FUNCTIONS">
    /**
     * Add one or multiple segments
     */
    public void addSegment() {
        if (activeSeed != null) {
            JTextField indexText = new JTextField(3);
            JTextField numText = new JTextField(3);

            JPanel myPanel = new JPanel();
            Box vBox1 = Box.createVerticalBox();
            JLabel warningLabel = new JLabel(toolbox.getAddSegmentWarningString());
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            //warningLabel.setHorizontalAlignment(JLabel.LEFT);
            vBox1.add(warningLabel);
            vBox1.add(Box.createVerticalStrut(5));
            Box hBox1 = Box.createHorizontalBox();
            hBox1.add(new JLabel("Insert Before Segment: "));
            hBox1.add(indexText);
            hBox1.add(Box.createHorizontalStrut(15)); // a spacer
            hBox1.add(new JLabel("Number of Segments: "));
            hBox1.add(numText);
            vBox1.add(hBox1);
            myPanel.add(vBox1);

            int result = JOptionPane.showConfirmDialog(this, myPanel,
                    "Add Segment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    if (activeSeed.getValueInt(CEConst.IDS_NUM_SEGMENT) + Integer.parseInt(numText.getText()) > MAX_NUM_SEGMENTS || activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD) > MAX_NUM_PERIODS) {
                        JOptionPane.showMessageDialog(this, "This FREEVAL is limited to upto " + MAX_NUM_SEGMENTS + " segments and " + MAX_NUM_PERIODS + " analysis periods.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        printLog(activeSeed.addSegment(Integer.parseInt(indexText.getText()) - 1, Integer.parseInt(numText.getText())));
                        seedDataChanged(Toolbox.SEED_CHANGE_NUM_SEGMENTS, Integer.parseInt(indexText.getText()) - 1, Integer.parseInt(indexText.getText()) - 1 + Integer.parseInt(numText.getText()));
                        update();
                    }
                } catch (Exception e) {
                    printLog("Fail to add segment.");
                }
            }
        }
    }

    /**
     * Delete one or multiple segments
     */
    public void delSegment() {
        if (activeSeed != null) {
            JTextField firstIndexText = new JTextField(3);
            JTextField lastIndexTest = new JTextField(3);

            JPanel myPanel = new JPanel();
            Box vBox1 = Box.createVerticalBox();
            JLabel warningLabel = new JLabel(toolbox.getDelSegmentWarningString());
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            //warningLabel.setHorizontalAlignment(JLabel.LEFT);
            vBox1.add(warningLabel);
            vBox1.add(Box.createVerticalStrut(5));
            Box hBox1 = Box.createHorizontalBox();
            hBox1.add(new JLabel("Delete from segment: "));
            hBox1.add(firstIndexText);
            hBox1.add(Box.createHorizontalStrut(15)); // a spacer
            hBox1.add(new JLabel("to segment: "));
            hBox1.add(lastIndexTest);
            vBox1.add(hBox1);
            myPanel.add(vBox1);

            int result = JOptionPane.showConfirmDialog(this, myPanel,
                    "Delete Segment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int n = JOptionPane.showConfirmDialog(this,
                        "Warning: Delete segment cannot be undone",
                        "Warning",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (n == JOptionPane.OK_OPTION) {
                    printLog(activeSeed.delSegment(Integer.parseInt(firstIndexText.getText()) - 1, Integer.parseInt(lastIndexTest.getText()) - 1));
                    seedDataChanged(Toolbox.SEED_CHANGE_NUM_SEGMENTS, Integer.parseInt(firstIndexText.getText()) - 1, Integer.parseInt(lastIndexTest.getText()));
                    update();
                }
            }
        }
    }

    /**
     * Add one or multiple periods
     */
    public void addPeriod() {
        if (activeSeed != null) {
            JTextField numText = new JTextField(3);
            JComboBox positionCombo = new JComboBox();
            positionCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"beginning", "end"}));

            JPanel myPanel = new JPanel();
            Box vBox1 = Box.createVerticalBox();
            JLabel warningLabel = new JLabel(toolbox.getAddPeriodWarningString());
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            //warningLabel.setHorizontalAlignment(JLabel.LEFT);
            vBox1.add(warningLabel);
            vBox1.add(Box.createVerticalStrut(5));
            Box hBox1 = Box.createHorizontalBox();
            hBox1.add(new JLabel("Add: "));
            hBox1.add(numText);
            hBox1.add(Box.createHorizontalStrut(15)); // a spacer
            hBox1.add(new JLabel("analysis periods at the: "));
            hBox1.add(positionCombo);
            vBox1.add(hBox1);
            myPanel.add(vBox1);

            int result = JOptionPane.showConfirmDialog(this, myPanel,
                    "Add Period", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    if (activeSeed.getValueInt(CEConst.IDS_NUM_SEGMENT) > MAX_NUM_SEGMENTS || activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD) + Integer.parseInt(numText.getText()) > MAX_NUM_PERIODS) {
                        JOptionPane.showMessageDialog(this, "This FREEVAL is limited to upto " + MAX_NUM_SEGMENTS + " segments and " + MAX_NUM_PERIODS + " analysis periods.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        printLog(activeSeed.addPeriod(Integer.parseInt(numText.getText()), positionCombo.getSelectedIndex() == 0));
                        numPeriodChanged = true;
                        seedDataChanged(Toolbox.SEED_CHANGE_NUM_PERIODS);
                        update();
                    }
                } catch (Exception e) {
                    printLog("Fail to add period.");
                }
            }
        }
    }

    public void addPeriod(int numPeriods, boolean isBeforeStart) {
        try {
            printLog(activeSeed.addPeriod(numPeriods, isBeforeStart));
            numPeriodChanged = true;
            seedDataChanged(Toolbox.SEED_CHANGE_NUM_PERIODS);
            update();
        } catch (Exception e) {
            printLog("Fail to add period.");
        }
    }

    /**
     * Delete one or multiple periods
     */
    public void delPeriod() {
        if (activeSeed != null) {
            JTextField numText = new JTextField(3);
            JComboBox positionCombo = new JComboBox();
            positionCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"beginning", "end"}));

            JPanel myPanel = new JPanel();
            Box vBox1 = Box.createVerticalBox();
            JLabel warningLabel = new JLabel(toolbox.getDelPeriodWarningString());
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            //warningLabel.setHorizontalAlignment(JLabel.LEFT);
            vBox1.add(warningLabel);
            vBox1.add(Box.createVerticalStrut(5));
            Box hBox1 = Box.createHorizontalBox();
            hBox1.add(new JLabel("Delete: "));
            hBox1.add(numText);
            hBox1.add(Box.createHorizontalStrut(15)); // a spacer
            hBox1.add(new JLabel("analysis periods at the: "));
            hBox1.add(positionCombo);
            vBox1.add(hBox1);
            myPanel.add(vBox1);

            int result = JOptionPane.showConfirmDialog(this, myPanel,
                    "Delete Period", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int n = JOptionPane.showConfirmDialog(this,
                            "Warning: Delete period cannot be undone",
                            "Warning",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (n == JOptionPane.OK_OPTION) {
                        printLog(activeSeed.delPeriod(Integer.parseInt(numText.getText()), positionCombo.getSelectedIndex() == 0));
                        numPeriodChanged = true;
                        seedDataChanged(Toolbox.SEED_CHANGE_NUM_PERIODS);
                        update();
                    }
                } catch (Exception e) {
                    printLog("Fail to Delete period.");
                }
            }
        }
    }

    public void delPeriod(int numPeriods, boolean isAtBeginning) {
        printLog(activeSeed.delPeriod(numPeriods, isAtBeginning));
        numPeriodChanged = true;
        seedDataChanged(Toolbox.SEED_CHANGE_NUM_PERIODS);
        update();
    }

    /**
     * Update all related display when seed data is changed
     */
    public void seedDataChanged() {
        toolbox.seedDataChanged(Toolbox.SEED_CHANGE_INPUT_FIELD);
        navigator.updateSeed(activeSeed);
        update();
    }

    public void seedDataChanged(int changeType) {
        toolbox.seedDataChanged(changeType);
        navigator.updateSeed(activeSeed);
        update();
    }

    public void seedDataChanged(int changeType, int segChangeStart, int segChangeEnd) {
        toolbox.seedDataChanged(changeType, segChangeStart, segChangeEnd);
        navigator.updateSeed(activeSeed);
        update();
    }

    /**
     * Update display, data and/or setting
     */
    private void update() {
        if (activeSeed != null) {
            setNonNullSeed();
            toolbox.setNonNullSeed();
            menuBar.setNonNullSeed();

            if (!hasOutput()) {
                runSeedSingle();
                if (showDebugOutput) {
                    debugActiveSeedOutput();
                }
            }
            if (!currToolbox.equalsIgnoreCase(TOOLBOX_WZ)) {
                enableOutput();
            }

            if (isShowingInput && isShowingOutput) {
                showInputAndOutput();
            } else if (isShowingInput) {
                showInput();
            } else {
                showOutput();
            }

            if (activeSeed.isManagedLaneUsed()) {
                enableManagedLane();
                //disableATDM();
            } else {
                disableManagedLane();
            }

            if (activeSeed.getValueInt(CEConst.IDS_NUM_SCEN) > 0) {
                enableATDM();
            } else {
                disableATDM();
                mainWindow.selectSeedScen(activeSeed, 0);

            }

            if (activeSeed.getValueInt(CEConst.IDS_ATDM_SET_NUM) > 0) {
                toolbox.turnOffML();
                toolbox.disableML();
                menuBar.turnOffML();
                menuBar.disableML();
            } else {
                toolbox.enableML();
                menuBar.enableML();
                if (activeSeed.isManagedLaneUsed()) {
                    toolbox.turnOnML();
                    menuBar.turnOnML();
                } else {
                    toolbox.turnOffML();
                    menuBar.turnOffML();
                }
            }
        } else {
            disableOutput();
            disableManagedLane();
            showInput();
            setNullSeed();
            toolbox.setNullSeed();
            menuBar.setNullSeed();
        }

        selectPeriod(activePeriod);
        tableDisplay.update();
        graphicDisplay.update();
        updateTitle();

        periodSummaryPanel.update();
        segmentSummaryPanel.update();

    }

    private void debugActiveSeedOutput() {
        for (int seg = 0; seg < activeSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            for (int period = 0; period < activeSeed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                //if (segment.scenSpeed[period] <= CEConst.ZERO) {
                //    System.out.println("Speed Error: value" + segment.scenSpeed[period] + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
                //}
                if (activeSeed.getValueFloat(CEConst.IDS_TOTAL_DENSITY_PC, seg, period, 0, -1) <= CEConst.ZERO) {
                    MainWindow.printLog("Density Error: value" + activeSeed.getValueFloat(CEConst.IDS_TOTAL_DENSITY_PC, seg, period, 0, -1) + ", scen: " + ", period: " + (period + 1) + ", seg: " + (seg + 1));
                }
                //if (segment.getScenTTI(period) < 1) {
                //    System.out.println("TTI Error: value" + segment.getScenTTI(period) + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
                //}
            }
        }
    }

    /**
     * Update seed display
     */
    private void updateSeed() {
        navigator.updateSeed(activeSeed);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OTHER MENU ITEM FUNCTIONS">
    /**
     * Show local JavaDoc files in system default browser
     */
    public void showJavaDoc() {
        try {
            Desktop.getDesktop().browse(new File("javadoc/index.html").toURI());
        } catch (Exception e) {
            printLog("Cannot open browser " + e.toString());
        }
    }

    /**
     * Show FREEVAL version and contact information
     */
    public void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

    /**
     * Add a scenario to compare
     *
     * @param seed seed
     * @param scen scenario index
     * @param atdm atdm index
     * @param name scenario name
     */
    public void addScenarioToCompare(Seed seed, int scen, int atdm, String name) {
        if (!seed.hasValidOutput(scen, atdm)) {
            seed.singleRun(scen, atdm);
        }
        comparePanel.addScenarioToCompare(seed, scen, atdm, name);
    }
    // </editor-fold>
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Toolbox Toggle Functions">
    public String getCurrentToolboxId() {
        return currToolbox;
    }

    public void toggleToolbox(String toolboxID, Toolbox toolbox) {
        if (!toolboxID.equalsIgnoreCase(currToolbox)) {
            this.toolbox.onLeavingToolbox();
            this.toolbox = toolbox;
            currToolbox = toolboxID;
            navigator.configureForToolbox();
            this.toolbox.onEnteringToolbox();
            jScrollPane2.setViewportView(toolbox);
            update();
            //menuBar.configureToolboxMenus();
            updateTitle();
        }
    }

    public void toggleEnabledNavigator(boolean toggle) {
        //navigator.setEnabledActions(toggle);
        if (toggle && activeSeed != null) {
            navigator.updateSeed(activeSeed);
        }
    }

    public void returnToCoreToolbox() {
        menuBar.returnToCoreToolbox();
        tabPanel.setSelectedComponent(singleScenSplitPanel);
        if (activeSeed != null) {
            showInput();
        }
    }

    public Toolbox getCurrentToolbox() {
        return toolbox;
    }

    public void addTab(String tabName, Component newTab) {
        tabPanel.add(tabName, newTab);
    }

    public void insertTab(String tabName, Component newTab, String tip, int tabIndex) {
        tabPanel.insertTab(tabName, null, newTab, tip, tabIndex);
    }

    public void insertTab(String tabName, Component newTab, String tip, int tabIndex, boolean enabled) {
        tabPanel.insertTab(tabName, null, newTab, tip, tabIndex);
        tabPanel.setEnabledAt(tabIndex, enabled);
    }

    public void removeTab(Component tab) {
        tabPanel.remove(tab);
    }

    public void removeTabAt(int tabIndex) {
        tabPanel.remove(tabIndex);
    }

    public void selectTab(int tabIndex) {
        tabPanel.setSelectedIndex(tabIndex);
    }

    public void selectTab(Component tab) {
        tabPanel.setSelectedComponent(tab);
    }

    public Component getComponentAt(int tabIndex) {
        return tabPanel.getComponentAt(tabIndex);
    }

    public void setTabEnabledAt(int tabIndex, boolean enabled, String toolTipText) {
        if (toolTipText == null || toolTipText.equalsIgnoreCase("")) {
            tabPanel.setEnabledAt(tabIndex, enabled);
        } else {
            tabPanel.setEnabledAt(tabIndex, enabled);
            tabPanel.setToolTipTextAt(tabIndex, toolTipText);
        }
    }

    public void setTabEnabledAt(Component tabComp, boolean enabled, String toolTipText) {
        // Find component index
        int idx = -1;
        for (int tabIdx = 0; tabIdx < tabPanel.getTabCount(); tabIdx++) {
            if (tabPanel.getComponentAt(tabIdx) == tabComp) {
                idx = tabIdx;
                break;
            }
        }
        if (idx > 0) { // Indicates tab was found, do nothing if tabComp not in tabPanel
            tabPanel.setEnabledAt(idx, enabled);
            if (toolTipText != null && !toolTipText.equalsIgnoreCase("")) {
                tabPanel.setToolTipTextAt(idx, toolTipText);
            }
        }
    }

    public void enableSingleSeedIOAndComparePanels() {
        tabPanel.setEnabledAt(0, true);
        this.setTabEnabledAt(comparePanel, true, null);
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

        jScrollPane4 = new javax.swing.JScrollPane();
        APPanel1 = new javax.swing.JPanel();
        periodLabel1 = new javax.swing.JLabel();
        timeLabel1 = new javax.swing.JLabel();
        firstButton1 = new javax.swing.JButton();
        previousButton1 = new javax.swing.JButton();
        nextButton1 = new javax.swing.JButton();
        lastButton1 = new javax.swing.JButton();
        jumpToButton1 = new javax.swing.JButton();
        jumpText1 = new javax.swing.JTextField();
        toolboxSplitPanel = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        navigatorSplitPanel = new javax.swing.JSplitPane();
        logSplitPanel = new javax.swing.JSplitPane();
        navigator = new GUI.major.Navigator();
        logScrollPanel = new javax.swing.JScrollPane();
        logText = new javax.swing.JEditorPane("text/html", "");
        tabPanel = new javax.swing.JTabbedPane();
        singleScenSplitPanel = new javax.swing.JSplitPane();
        tableDisplay = new GUI.major.TableDisplay();
        jPanel1 = new javax.swing.JPanel();
        apControlPanel = new GUI.major.AnalysisPeriodControlPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        graphicDisplay = new GUI.major.GraphicDisplay();
        comparePanel = new GUI.major.outputPanel.ComparePanel();
        contourPanel = new GUI.major.outputPanel.ContourPanel();
        periodSummaryPanel = new GUI.major.outputPanel.FacilityPeriodSummaryPanel();
        segmentSummaryPanel = new GUI.major.outputPanel.FacilitySegmentSummaryPanel();
        menuBar = new GUI.major.MenuBar();

        jScrollPane4.setBorder(null);

        APPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Analysis Period (A.P.) Control"));
        APPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        APPanel1.setPreferredSize(new java.awt.Dimension(596, 23));
        APPanel1.setLayout(new java.awt.GridLayout(1, 7));

        periodLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        periodLabel1.setText("A.P.");
        APPanel1.add(periodLabel1);

        timeLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel1.setText("-");
        timeLabel1.setToolTipText("Analysis Period Time (HH:MM)");
        APPanel1.add(timeLabel1);

        firstButton1.setText("First");
        firstButton1.setToolTipText("Go to the first analysis period");
        APPanel1.add(firstButton1);

        previousButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/iconHelper/Back24.gif"))); // NOI18N
        previousButton1.setToolTipText("Go to previous analysis period");
        APPanel1.add(previousButton1);

        nextButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/iconHelper/Forward24.gif"))); // NOI18N
        nextButton1.setToolTipText("Go to next analysis period");
        APPanel1.add(nextButton1);

        lastButton1.setText("Last");
        lastButton1.setToolTipText("GO to the last analysis period");
        APPanel1.add(lastButton1);

        jumpToButton1.setText("Jump To");
        jumpToButton1.setToolTipText("Jump to a specified analysis period");
        APPanel1.add(jumpToButton1);

        jumpText1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jumpText1.setToolTipText("Enter the specified analysis period index");
        APPanel1.add(jumpText1);

        jScrollPane4.setViewportView(APPanel1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new java.awt.Dimension(600, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        toolboxSplitPanel.setBorder(null);
        toolboxSplitPanel.setDividerLocation(48);
        toolboxSplitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPane2.setBorder(null);
        toolboxSplitPanel.setLeftComponent(jScrollPane2);

        navigatorSplitPanel.setBorder(null);
        navigatorSplitPanel.setDividerLocation(280);
        navigatorSplitPanel.setResizeWeight(0.15);
        navigatorSplitPanel.setToolTipText("");

        logSplitPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        logSplitPanel.setDividerLocation(600);
        logSplitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        logSplitPanel.setResizeWeight(0.85);
        logSplitPanel.setLeftComponent(navigator);

        logText.setEditable(false);
        logText.setToolTipText("Use mouse wheel to adjust log font size");
        logText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        logText.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                logTextMouseWheelMoved(evt);
            }
        });
        logScrollPanel.setViewportView(logText);

        logSplitPanel.setBottomComponent(logScrollPanel);

        navigatorSplitPanel.setLeftComponent(logSplitPanel);

        tabPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        tabPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPanelStateChanged(evt);
            }
        });

        singleScenSplitPanel.setBorder(null);
        singleScenSplitPanel.setDividerLocation(200);
        singleScenSplitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        singleScenSplitPanel.setResizeWeight(0.35);
        singleScenSplitPanel.setToolTipText("Input and output tables for a seed, a reliability analysis scenario, or an ATDM scenario");
        singleScenSplitPanel.setRightComponent(tableDisplay);

        graphicDisplay.setToolTipText("Use mouse wheel to zoom in/out");

        javax.swing.GroupLayout graphicDisplayLayout = new javax.swing.GroupLayout(graphicDisplay);
        graphicDisplay.setLayout(graphicDisplayLayout);
        graphicDisplayLayout.setHorizontalGroup(
            graphicDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphicDisplayLayout.setVerticalGroup(
            graphicDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(graphicDisplay);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 940, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(apControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 939, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(apControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        singleScenSplitPanel.setLeftComponent(jPanel1);

        tabPanel.addTab("Single Seed/Scenario I/O", singleScenSplitPanel);

        comparePanel.setToolTipText("Compare results from multiple seeds, reliability analysis scenarios, or ATDM scenarios");
        tabPanel.addTab("Compare", comparePanel);

        contourPanel.setToolTipText("Result contours for a seed, a reliability analysis scenario, or an ATDM scenario");
        tabPanel.addTab("Result Contours", contourPanel);

        periodSummaryPanel.setToolTipText("Analysis period summary for a seed, a reliability analysis scenario, or an ATDM scenario");
        tabPanel.addTab("Analysis Period Summary", periodSummaryPanel);

        segmentSummaryPanel.setToolTipText("Segment and facility summary for a seed, a reliability analysis scenario, or an ATDM scenario");
        tabPanel.addTab("Segment & Facility Summary", segmentSummaryPanel);

        navigatorSplitPanel.setRightComponent(tabPanel);

        toolboxSplitPanel.setRightComponent(navigatorSplitPanel);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1250, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(toolboxSplitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1250, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 852, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(toolboxSplitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int result;
        for (Seed seed : seedList) {
            switch (toolboxAuxHash.get(seed).PROJECT_TYPE) {
                default:
                case MainWindow.TOOLBOX_RL_ATDM:
                    result = JOptionPane.showConfirmDialog(this, "Do you want to save any changes you made to seed \"" + seed.getValueString(CEConst.IDS_PROJECT_NAME) + "\"?",
                            "Save Files", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        saveSeed(seed);
                    }
                    break;
            }
        }
        ConfigIO.saveSeedListToConfig(seedList, toolboxAuxHash);
    }//GEN-LAST:event_formWindowClosing

    private void logTextMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_logTextMouseWheelMoved
        logFont = logFont.deriveFont((float) Math.min(Math.max(logFont.getSize() + evt.getWheelRotation(), 12), 20));
        logText.setFont(logFont);
    }//GEN-LAST:event_logTextMouseWheelMoved

    private void tabPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPanelStateChanged

    }//GEN-LAST:event_tabPanelStateChanged

    private void configGPMLDisplay() {
        switch (apControlPanel.getGPMLCBSelectedIndex()) {
            case 0:
                showGPOnly();
                break;
            case 1:
                showMLOnly();
                break;
            case 2:
                showGPML();
                break;
        }
    }

    /**
     * Configure display to show general purpose segments only
     */
    public void showGPOnly() {
        tableDisplay.showGPOnly();
    }

    /**
     * Configure display to show managed lanes segments only
     */
    public void showMLOnly() {
        tableDisplay.showMLOnly();
    }

    /**
     * Configure display to show both general purpose and managed lanes segments
     */
    public void showGPML() {
        tableDisplay.showGPML();
    }

    /**
     * Toggle whether managed lanes are used in active seed and change button
     * display accordingly
     */
    public void toggleManagedLane() {
        if (activeSeed != null) {
            if (activeSeed.isManagedLaneUsed()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Warning: Disable managed lanes will delete all existing managed lanes and reliability data. This cannot be undone.",
                        "Warning",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    activeSeed.setManagedLaneUsed(false);

                    updateSeed();
                    update();
                }
            } else {
                int result = JOptionPane.OK_OPTION;
                if (activeSeed.getRLScenarioGP() != null) {
                    result = JOptionPane.showConfirmDialog(this,
                            "Warning: Enabling managed lanes will delete all existing reliability analysis data for this seed. This cannot be undone.",
                            "Warning",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }
                if (result == JOptionPane.OK_OPTION) {
                    activeSeed.setManagedLaneUsed(true);

                    updateSeed();
                    update();
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="setter and getters">
    /**
     * Getter for active seed
     *
     * @return active seed instance
     */
    public Seed getActiveSeed() {
        return activeSeed;
    }

    /**
     * Getter for active scenario index
     *
     * @return active scenario index
     */
    public int getActiveScen() {
        return activeScen;
    }

    /**
     * Getter for active period index
     *
     * @return active period index (start with 0)
     */
    public int getActivePeriod() {
        return activePeriod;
    }

    /**
     * Getter for opened seed list
     *
     * @return opened seed list
     */
    public ArrayList<Seed> getSeedList() {
        return seedList;
    }

    /**
     * Getter for Toolbox Auxiliary Datastructure HashMap
     *
     * @return Toolbox Auxiliary Datastructure HashMap
     */
    public HashMap<Seed, FREEVALProject> getToolboxAuxHash() {
        return toolboxAuxHash;
    }

    public FREEVALProject getActiveSeedToolboxAuxObject() {
        return toolboxAuxHash.getOrDefault(activeSeed, null);
    }

    /**
     * Getter for table font
     *
     * @return table font
     */
    public static Font getTableFont() {
        return tableFont;
    }

    /**
     * Setter for table font
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        tableFont = newTableFont;
        tableDisplay.setTableFont(newTableFont);
        comparePanel.setTableFont(newTableFont);
        contourPanel.setTableFont(newTableFont);
        periodSummaryPanel.setTableFont(newTableFont);
        segmentSummaryPanel.setTableFont(newTableFont);
    }

    /**
     * Check whether output is enabled
     *
     * @return whether output is enabled
     */
    public boolean isOutputEnabled() {
        return isOutputEnabled;
    }

    /**
     * Check whether toolbox is in floating window mode
     *
     * @return whether toolbox is in floating window mode
     */
    public boolean isToolboxInFloat() {
        return isToolboxInFloat;
    }

    /**
     * Check whether contour is in floating window mode
     *
     * @return whether contour is in floating window mode
     */
    public boolean isContourInFloat() {
        return isContourInFloat;
    }

    /**
     * Check whether compare is in floating window mode
     *
     * @return whether compare is in floating window mode
     */
    public boolean isCompareInFloat() {
        return isCompareInFloat;
    }

    /**
     * Check whether single I/O is in floating window mode
     *
     * @return whether single I/O is in floating window mode
     */
    public boolean isSingleInFloat() {
        return isSingleInFloat;
    }

    /**
     * Check whether log is in floating window mode
     *
     * @return whether log is in floating window mode
     */
    public boolean isLogInFloat() {
        return isLogInFloat;
    }

    /**
     * Check whether navigator is in floating window mode
     *
     * @return whether navigator is in floating window mode
     */
    public boolean isNavigatorInFloat() {
        return isNavigatorInFloat;
    }

    /**
     * Check whether GP period summary is in floating window mode
     *
     * @return whether GP period summary is in floating window mode
     */
    public boolean isPeriodInFloat() {
        return isPeriodInFloat;
    }

    /**
     * Check whether GP segment and facility summary is in floating window mode
     *
     * @return whether GP segment and facility summary is in floating window
     * mode
     */
    public boolean isSegmentInFloat() {
        return isSegmentInFloat;
    }

    /**
     * Check whether ML period summary is in floating window mode
     *
     * @return whether ML period summary is in floating window mode
     */
    public boolean isPeriodMLInFloat() {
        return isPeriodInFloat_ML;
    }

    /**
     * Check whether ML segment and facility summary is in floating window mode
     *
     * @return whether ML segment and facility summary is in floating window
     * mode
     */
    public boolean isSegmentMLInFloat() {
        return isSegmentInFloat_ML;
    }
    // </editor-fold>

    public boolean isShowDebugOutput() {
        return showDebugOutput;
    }

    public void setShowDebugOutput(boolean showDebugOutput) {
        this.showDebugOutput = showDebugOutput;
    }

    public void addActivePeriodBinding(PeriodBinding newBinding) {
        this.activePeriodBindings.add(newBinding);
    }

    public void removeActivePeriodBinding(PeriodBinding perBinding) {
        this.activePeriodBindings.remove(perBinding);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel APPanel1;
    private GUI.major.AnalysisPeriodControlPanel apControlPanel;
    private GUI.major.outputPanel.ComparePanel comparePanel;
    private GUI.major.outputPanel.ContourPanel contourPanel;
    private javax.swing.JButton firstButton1;
    private GUI.major.GraphicDisplay graphicDisplay;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jumpText1;
    private javax.swing.JButton jumpToButton1;
    private javax.swing.JButton lastButton1;
    private javax.swing.JScrollPane logScrollPanel;
    private javax.swing.JSplitPane logSplitPanel;
    private javax.swing.JEditorPane logText;
    private GUI.major.MenuBar menuBar;
    private GUI.major.Navigator navigator;
    private javax.swing.JSplitPane navigatorSplitPanel;
    private javax.swing.JButton nextButton1;
    private javax.swing.JLabel periodLabel1;
    private GUI.major.outputPanel.FacilityPeriodSummaryPanel periodSummaryPanel;
    private javax.swing.JButton previousButton1;
    private GUI.major.outputPanel.FacilitySegmentSummaryPanel segmentSummaryPanel;
    private javax.swing.JSplitPane singleScenSplitPanel;
    private javax.swing.JTabbedPane tabPanel;
    private GUI.major.TableDisplay tableDisplay;
    private javax.swing.JLabel timeLabel1;
    private javax.swing.JSplitPane toolboxSplitPanel;
    // End of variables declaration//GEN-END:variables
    private GUI.major.Toolbox toolbox;
}
