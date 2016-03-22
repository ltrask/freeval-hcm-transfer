package GUI.RLHelper.summary;

import GUI.ATDMHelper.IO.ScenarioFilterDialog;
import GUI.ATDMHelper.ScenarioSummaryRenderer;
import GUI.ATDMHelper.StayOpenOnActionPopupMenu;
import GUI.major.MainWindow;
import GUI.seedEditAndIOHelper.CSVFileChooser;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CSVWriter;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.IncidentEvent;
import coreEngine.reliabilityAnalysis.DataStruct.WeatherEvent;
import coreEngine.reliabilityAnalysis.DataStruct.WorkZone;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import main.FREEVAL_HCM;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This dialog shows the reliability summary for one seed.
 *
 * @author Shu Liu
 */
public class RLSummaryDialog extends javax.swing.JDialog {

    private class RLResult implements Comparable {

        float TTI;

        float prob;

        /**
         * Constructor
         *
         * @param prob probability of this result
         * @param TTI travel time index of this result
         */
        public RLResult(float prob, float TTI) {
            this.prob = prob;
            this.TTI = TTI;
        }

        @Override
        public int compareTo(Object o) {
            return Float.compare(TTI, ((RLResult) o).TTI);
        }
    }

    /**
     * A return status code - returned if Cancel button has been pressed.
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed.
     */
    public static final int RET_OK = 1;

    /**
     * Table model for the scenario summaries table.
     */
    private ScenarioSummaryTableModel scenSummariesModel;

    private JPopupMenu dmColPopupMenu;

    private JPopupMenu numWorkZonesPopupMenu;

    private JPopupMenu numWeatherEventsPopupMenu;

    private JPopupMenu numIncidentEventsPopupMenu;

    private JPopupMenu maxTTIColPopupMenu;

    private JPopupMenu maxVHDColPopupMenu;

    private JPopupMenu badAPsPopupMenu;

    private JPopupMenu ttiBySegmentPopup;

    private JCheckBoxMenuItem[] segCBMenuItem;

    private JCheckBoxMenuItem chartsUpdateMenuItem;

    private final boolean[] includeSegmentForTTI;

    private final JPopupMenu tooltip = new JPopupMenu();

    /**
     * Creates new form RLSummaryDialog
     *
     * @param seed the seed to be shown
     * @param mainWindow MainWindow instance
     */
    public RLSummaryDialog(Seed seed, MainWindow mainWindow) {
        super(mainWindow, true);
        this.seed = seed;
        this.mainWindow = mainWindow;

        includeSegmentForTTI = new boolean[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];
        Arrays.fill(includeSegmentForTTI, true);

        initComponents();

        //set starting position
        this.setLocationRelativeTo(this.getRootPane());

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

        addGeneralInfo();
        addResultAndChart();
        setupScenarioSummariesTable();
        setupFilterPopupMenus();
        setupTTIBySegmentPopupMenu();

        exportToDSSButton.setEnabled(false);
        exportToDSSButton.setVisible(false);

        rlSegmentButton.setVisible(false);
        rlSegmentButton.setEnabled(false);
    }

    private void addGeneralInfo() {
        projectNameText.setText(seed.getValueString(CEConst.IDS_PROJECT_NAME));
        numSegText.setText(seed.getValueString(CEConst.IDS_NUM_SEGMENT));
        numPeriodText.setText(seed.getValueString(CEConst.IDS_NUM_PERIOD));
        numScenText.setText(seed.getValueString(CEConst.IDS_NUM_SCEN));
        lengthText.setText(formatter1d.format(seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI)));
        seedDateText.setText(seed.getSeedFileDate().toString());
        startDateText.setText(seed.getRRPStartDate().toString());
        endDateText.setText(seed.getRRPEndDate().toString());
    }

    private void addResultAndChart() {
        chartPanel.removeAll();

        try {

            // <editor-fold defaultstate="collapsed" desc="EXTRACT DATA AND CALCULATE %">
            ArrayList<RLResult> RLResults = new ArrayList<>();

            float mean = 0;
            float ratingCount = 0;
            float VMT2Count = 0;
            float semiSTD = 0;

            //extract data from seed, and modify probability to match per period
            for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
                for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD);
                    float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                    //float actualTravelTime = 0.0f;
                    //float ffsTravelTime = 0.0f;
                    //for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    //    if (includeSegmentForTTI[seg]) {
                    //        actualTravelTime += seed.getValueFloat(CEConst.IDS_ACTUAL_TIME, seg, period, scen, -1);
                    //        ffsTravelTime += seed.getValueFloat(CEConst.IDS_FFS_TIME, seg, period, scen, -1);
                    //    }
                    //}
                    //float TTI = actualTravelTime / ffsTravelTime;

                    RLResults.add(new RLResult(prob, TTI));
                    mean += TTI * prob;
                    semiSTD += (TTI - 1) * (TTI - 1) * prob;
                    if (TTI < 1.333333f) {
                        ratingCount += prob;
                    }
                    if (TTI > 2) {
                        VMT2Count += prob;
                    }
                }
            }
            Collections.sort(RLResults);

            //--------------------------------------------------------------------------
            minTTI = RLResults.get(0).TTI;
            maxTTI = RLResults.get(RLResults.size() - 1).TTI;

            meanText.setText(formatter2d.format(mean));

            semiSTD = (float) Math.sqrt(semiSTD);
            semiSTDText.setText(formatter2d.format(semiSTD));

            //
            //p80Text.setText(formatter2d.format(RLResults.get((int) (RLResults.size() * 0.8)).TTI));
            //p95Text.setText(formatter2d.format(RLResults.get((int) (RLResults.size() * 0.95)).TTI));
            float probCount = 0;
            float miseryTotal = 0, miseryWeight = 0;
            for (RLResult RLResult : RLResults) {
                //find 50th %
                if (probCount <= 0.5 && probCount + RLResult.prob >= 0.5) {
                    p50Text.setText(formatter2d.format(RLResult.TTI));
                }
                //find 80th %
                if (probCount <= 0.8 && probCount + RLResult.prob >= 0.8) {
                    p80Text.setText(formatter2d.format(RLResult.TTI));
                }
                //find 95th %
                if (probCount <= 0.95 && probCount + RLResult.prob >= 0.95) {
                    p95Text.setText(formatter2d.format(RLResult.TTI));
                }

                for (int i = 0; i < PERCENT_GROUP.length; i++) {
                    if (probCount < PERCENT_GROUP[i] && probCount + RLResult.prob >= PERCENT_GROUP[i]) {
                        TTI_Group[i] = RLResult.TTI;
                    }
                }

                probCount += RLResult.prob;

                if (probCount >= 0.95) {
                    miseryTotal += RLResult.TTI * RLResult.prob;
                    miseryWeight += RLResult.prob;
                }
            }

            miseryText.setText(formatter2d.format(miseryTotal / miseryWeight));
            ratingText.setText(formatter2dp.format(ratingCount));
            VMT2Text.setText(formatter2dp.format(VMT2Count));
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="ADD TWO CHARTS">
            // Define the data for the both charts
            final XYSeries seriesTTI = new XYSeries("RL TTI");
            final XYSeries fieldSeriesTTI = new XYSeries("Field TTI");
            float[] groupCount = new float[NUM_GROUP];

            float count = 0;
            for (RLResult result : RLResults) {
                count += result.prob;
                seriesTTI.add(result.TTI, count);
                int groupIndex = (int) Math.min(Math.max(result.TTI - LOWER_BOUND, 0) / GROUP_INCREMENT,
                        NUM_GROUP - 1);
                groupCount[groupIndex] += result.prob;
            }

            for (int i = 0; i < seed.getTTI_Value().length; i++) {
                fieldSeriesTTI.add(seed.getTTI_Value()[i], PERCENT_GROUP[i]);
            }

            //create bar chart
            DefaultCategoryDataset barChartDataset = new DefaultCategoryDataset();
            for (int i = 0; i < groupCount.length - 1; i++) {
                barChartDataset.addValue(groupCount[i], "TTI",
                        "[" + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * i) + ","
                        + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (i + 1)) + ")");
            }
            barChartDataset.addValue(groupCount[groupCount.length - 1], "TTI",
                    formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (groupCount.length - 1)) + "+");
            JFreeChart barChartObject = ChartFactory.createBarChart(
                    "Probability  Distribution Function", "TTI", "Percentage",
                    barChartDataset, PlotOrientation.VERTICAL, true, true, false);
            ((NumberAxis) ((CategoryPlot) barChartObject.getPlot()).getRangeAxis()).setNumberFormatOverride(NumberFormat.getPercentInstance());
            ((CategoryPlot) barChartObject.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            chartPanel.add(new ChartPanel(barChartObject));

            // create line chart
            final XYSeriesCollection lineChartDataset = new XYSeriesCollection();
            lineChartDataset.addSeries(seriesTTI);
            lineChartDataset.addSeries(fieldSeriesTTI);
            JFreeChart lineChartObject = ChartFactory.createXYLineChart(
                    "Cumulative  Distribution Function", "TTI", "Percentage",
                    lineChartDataset, PlotOrientation.VERTICAL, true, true, false);
            ((XYPlot) lineChartObject.getPlot()).getDomainAxis().setRange(1, 5);
            ((NumberAxis) ((XYPlot) lineChartObject.getPlot()).getRangeAxis()).setNumberFormatOverride(NumberFormat.getPercentInstance());
            lineChartObject.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(3.0f));
            chartPanel.add(new ChartPanel(lineChartObject));

            chartPanel.validate();
            // </editor-fold>
        } catch (Exception e) {
            mainWindow.printLog("Error when create chart " + e.toString());
        }

    }

    private void setupScenarioSummariesTable() {

        // Setting up the Scenario Summaries Table
        scenSummariesModel = new ScenarioSummaryTableModel(seed, scenSummariesTable);
        scenSummariesTable.setModel(scenSummariesModel);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        scenSummariesTable.setDefaultRenderer(Object.class, centerRenderer);
        scenSummariesTable.setDefaultRenderer(Integer.class, centerRenderer);
        scenSummariesTable.setDefaultRenderer(Float.class, centerRenderer);
        ScenarioSummaryRenderer atdmSSRend = new ScenarioSummaryRenderer(ScenarioSummaryRenderer.TYPE_RL_SUMMARY);
        atdmSSRend.setHorizontalAlignment(JLabel.CENTER);
        atdmSSRend.setColorRange(scenSummariesModel.getMinTTI(), scenSummariesModel.getPTI());
        scenSummariesTable.getColumnModel().getColumn(ScenarioSummaryTableModel.COL_MAX_TTI).setCellRenderer(atdmSSRend);
        scenSummariesTable.getColumnModel().getColumn(ScenarioSummaryTableModel.COL_MAX_VHD).setCellRenderer(atdmSSRend);
        scenSummariesModel.fireTableDataChanged();
        scenSummariesTable.setFont(MainWindow.getTableFont());
        scenSummariesTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        scenSummariesTable.setRowSorter(new TableRowSorter(scenSummariesModel));
        scenSummariesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupFilterPopupMenus() {

        ActionListener menuItemCheckedListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                filterOptionsChanged();
            }
        };

        ActionListener selectAllOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int col = scenSummariesTable.getSelectedColumn();
                JPopupMenu currPopupMenu;
                switch (col) {
                    default:
                    case ScenarioSummaryTableModel.COL_NUM_INCIDENTS:
                        currPopupMenu = numIncidentEventsPopupMenu;
                        break;
                    case ScenarioSummaryTableModel.COL_NUM_WORK_ZONES:
                        currPopupMenu = numWorkZonesPopupMenu;
                        break;
                    case ScenarioSummaryTableModel.COL_NUM_WEATHER_EVENTS:
                        currPopupMenu = numWeatherEventsPopupMenu;
                        break;

                }
                for (int i = 0; i < currPopupMenu.getComponentCount() - 4; i++) {
                    ((JCheckBoxMenuItem) currPopupMenu.getComponent(i + 4)).setSelected(true);
                }
                filterOptionsChanged();
            }
        };

        ActionListener deselectAllOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JPopupMenu currPopupMenu;
                int col = scenSummariesTable.getSelectedColumn();
                switch (col) {
                    default:
                    case ScenarioSummaryTableModel.COL_NUM_INCIDENTS:
                        currPopupMenu = numIncidentEventsPopupMenu;
                        break;
                    case ScenarioSummaryTableModel.COL_NUM_WORK_ZONES:
                        currPopupMenu = numWorkZonesPopupMenu;
                        break;
                    case ScenarioSummaryTableModel.COL_NUM_WEATHER_EVENTS:
                        currPopupMenu = numWeatherEventsPopupMenu;
                        break;

                }
                for (int i = 0; i < currPopupMenu.getComponentCount() - 4; i++) {
                    ((JCheckBoxMenuItem) currPopupMenu.getComponent(i + 4)).setSelected(false);
                }
                filterOptionsChanged();
            }
        };

        ActionListener defineRangeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //int col = scenSelectorTable.getSelectedColumn();
                int col = scenSummariesTable.getSelectedColumn();
                if (col == ScenarioSummaryTableModel.COL_MAX_VHD) {
                    fireFilterDialog(col - 3);
                } else {
                    fireFilterDialog(col - 2);
                }
            }
        };

        ActionListener setCongestedTIIValueListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String newStr = (String) JOptionPane.showInputDialog(null, "Enter new cutoff value for congested AP TTI:\n"
                        + "(PTI is " + scenSummariesModel.getPTI() + ")",
                        "Enter Congested TTI Cutoff Value",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null, scenSummariesModel.getCongestedAPCutOffTTI());
                if (newStr != null && newStr.length() > 0) {
                    scenSummariesModel.setCongestedAPCutOffTTI(Float.valueOf(newStr));
                    scenSummariesModel.updateCongestedTTICutoff();
                }

            }
        };

        numWorkZonesPopupMenu = new StayOpenOnActionPopupMenu();
        JMenuItem defineRangeMenuItem = new JMenuItem("Define Filter Range");
        defineRangeMenuItem.addActionListener(defineRangeListener);
        numWorkZonesPopupMenu.add(defineRangeMenuItem);
        JMenuItem selectAllMenuItem = new JMenuItem("Select All Options");
        selectAllMenuItem.addActionListener(selectAllOptionListener);
        numWorkZonesPopupMenu.add(selectAllMenuItem);  //puchanged
        JMenuItem deselectAllMenuItem = new JMenuItem("Deselect All Options");
        deselectAllMenuItem.addActionListener(deselectAllOptionListener);
        numWorkZonesPopupMenu.add(deselectAllMenuItem);  //puchanged
        numWorkZonesPopupMenu.add(new Separator());  //puchanged
        int numItems = scenSummariesModel.getAllValueOptions(ScenarioSummaryTableModel.COL_NUM_WORK_ZONES);
        for (int j = 0; j <= numItems; j++) {
            JCheckBoxMenuItem currCBMenuItem = new JCheckBoxMenuItem(String.valueOf(j), true);
            currCBMenuItem.addActionListener(menuItemCheckedListener);
            numWorkZonesPopupMenu.add(currCBMenuItem);  //puchanged
        }

        numWeatherEventsPopupMenu = new StayOpenOnActionPopupMenu();
        defineRangeMenuItem = new JMenuItem("Define Filter Range");
        defineRangeMenuItem.addActionListener(defineRangeListener);
        numWeatherEventsPopupMenu.add(defineRangeMenuItem);
        selectAllMenuItem = new JMenuItem("Select All Options");
        selectAllMenuItem.addActionListener(selectAllOptionListener);
        numWeatherEventsPopupMenu.add(selectAllMenuItem);  //puchanged
        deselectAllMenuItem = new JMenuItem("Deselect All Options");
        deselectAllMenuItem.addActionListener(deselectAllOptionListener);
        numWeatherEventsPopupMenu.add(deselectAllMenuItem);  //puchanged
        numWeatherEventsPopupMenu.add(new Separator());  //puchanged
        numItems = scenSummariesModel.getAllValueOptions(ScenarioSummaryTableModel.COL_NUM_WEATHER_EVENTS);
        for (int j = 0; j <= numItems; j++) {
            JCheckBoxMenuItem currCBMenuItem = new JCheckBoxMenuItem(String.valueOf(j), true);
            currCBMenuItem.addActionListener(menuItemCheckedListener);
            numWeatherEventsPopupMenu.add(currCBMenuItem);  //puchanged
        }

        numIncidentEventsPopupMenu = new StayOpenOnActionPopupMenu();
        defineRangeMenuItem = new JMenuItem("Define Filter Range");
        defineRangeMenuItem.addActionListener(defineRangeListener);
        numIncidentEventsPopupMenu.add(defineRangeMenuItem);
        selectAllMenuItem = new JMenuItem("Select All Options");
        selectAllMenuItem.addActionListener(selectAllOptionListener);
        numIncidentEventsPopupMenu.add(selectAllMenuItem);  //puchanged
        deselectAllMenuItem = new JMenuItem("Deselect All Options");
        deselectAllMenuItem.addActionListener(deselectAllOptionListener);
        numIncidentEventsPopupMenu.add(deselectAllMenuItem);  //puchanged
        numIncidentEventsPopupMenu.add(new Separator());  //puchanged
        numItems = scenSummariesModel.getAllValueOptions(ScenarioSummaryTableModel.COL_NUM_INCIDENTS);
        for (int j = 0; j <= numItems; j++) {
            JCheckBoxMenuItem currCBMenuItem = new JCheckBoxMenuItem(String.valueOf(j), true);
            currCBMenuItem.addActionListener(menuItemCheckedListener);
            numIncidentEventsPopupMenu.add(currCBMenuItem);  //puchanged
        }

        // Addding define range for demand multiplier column
        dmColPopupMenu = new JPopupMenu();
        dmColPopupMenu.add(new JMenuItem("Define Demand Multiplier Range"));
        ((JMenuItem) dmColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);

        // Addding define range for demand multiplier column
        maxTTIColPopupMenu = new JPopupMenu();
        maxTTIColPopupMenu.add(new JMenuItem("Define Max TTI Range"));
        ((JMenuItem) maxTTIColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);

        // Addding define range for demand multiplier column
        maxVHDColPopupMenu = new JPopupMenu();
        maxVHDColPopupMenu.add(new JMenuItem("Define Max Delay Range"));
        ((JMenuItem) maxVHDColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);

        badAPsPopupMenu = new JPopupMenu();
        badAPsPopupMenu.add(new JMenuItem("Set Congested TTI Cutoff Value"));
        ((JMenuItem) badAPsPopupMenu.getComponent(0)).addActionListener(setCongestedTIIValueListener);
    }

    private void filterOptionsChanged() {
        int col = scenSummariesTable.getSelectedColumn();

        for (int filterIdx = 0; filterIdx < numWorkZonesPopupMenu.getComponentCount() - 4; filterIdx++) {
            boolean isSelected = ((JCheckBoxMenuItem) numWorkZonesPopupMenu.getComponent(filterIdx + 4)).isSelected();
            scenSummariesModel.updateFilter(4, filterIdx, isSelected);
        }

        for (int filterIdx = 0; filterIdx < numWeatherEventsPopupMenu.getComponentCount() - 4; filterIdx++) {
            boolean isSelected = ((JCheckBoxMenuItem) numWeatherEventsPopupMenu.getComponent(filterIdx + 4)).isSelected();
            scenSummariesModel.updateFilter(5, filterIdx, isSelected);
        }

        for (int filterIdx = 0; filterIdx < numIncidentEventsPopupMenu.getComponentCount() - 4; filterIdx++) {
            boolean isSelected = ((JCheckBoxMenuItem) numIncidentEventsPopupMenu.getComponent(filterIdx + 4)).isSelected();
            scenSummariesModel.updateFilter(6, filterIdx, isSelected);
        }

        scenSummariesModel.applyFilters();
        scenSummariesTable.setColumnSelectionInterval(col, col);

    }

    private void updateFilterPopups() {

        for (int filterIdx = 0; filterIdx < numWorkZonesPopupMenu.getComponentCount() - 4; filterIdx++) {
            ((JCheckBoxMenuItem) numWorkZonesPopupMenu.getComponent(filterIdx + 4)).setSelected(scenSummariesModel.getFilterOption(4, filterIdx));
            ((JCheckBoxMenuItem) numWeatherEventsPopupMenu.getComponent(filterIdx + 4)).setSelected(scenSummariesModel.getFilterOption(5, filterIdx));
            ((JCheckBoxMenuItem) numIncidentEventsPopupMenu.getComponent(filterIdx + 4)).setSelected(scenSummariesModel.getFilterOption(6, filterIdx));
        }
    }

    private void fireFilterDialog(int checkOption) {
        if (scenSummariesModel != null) {
            ScenarioFilterDialog atdmFilterDialog = new ScenarioFilterDialog(scenSummariesModel, this, true);
            if (checkOption > -1) {
                atdmFilterDialog.enableFilterOption(checkOption);
            }

            atdmFilterDialog.setLocationRelativeTo(this.getRootPane());
            atdmFilterDialog.setVisible(true);

            if (atdmFilterDialog.filterOptionsSet()) {
                scenSummariesModel.filterByDemandMultRange(atdmFilterDialog.getDemandFilter());
                scenSummariesModel.filterByNumberIncidentsRange(atdmFilterDialog.getIncidentFilter());
                scenSummariesModel.filterByNumberWeatherEventsRange(atdmFilterDialog.getWeatherFilter());
                scenSummariesModel.filterByMaxTTIRange(atdmFilterDialog.getTTIFilter());
                scenSummariesModel.filterByMaxDelayRange(atdmFilterDialog.getDelayFilter());
                scenSummariesModel.applyFilters();
                scenSummariesModel.fireTableDataChanged();
            }
            atdmFilterDialog.dispose();
            updateFilterPopups();
        }
    }

    private void scenarioSelectorTablePopupTriggered(java.awt.event.MouseEvent evt) {
        JTable source = (JTable) evt.getSource();
        int column = source.columnAtPoint(evt.getPoint());
        int row = source.rowAtPoint(evt.getPoint());
        if (!source.isColumnSelected(column)) {
            source.changeSelection(row, column, false, false);
        }
        switch (column) {
            case ScenarioSummaryTableModel.COL_SCEN_IDX:
            case ScenarioSummaryTableModel.COL_DEMAND_PATTERN:
                break;
            case ScenarioSummaryTableModel.COL_DEMAND_MULTIPLIER:
                dmColPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_NUM_WORK_ZONES:
                numWorkZonesPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_NUM_WEATHER_EVENTS:
                numWeatherEventsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_NUM_INCIDENTS:
                numIncidentEventsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_MAX_TTI:
                maxTTIColPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_MAX_VHD:
                maxVHDColPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case ScenarioSummaryTableModel.COL_NUM_BAD_APS:
                badAPsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
        }
    }

    private void setupTTIBySegmentPopupMenu() {
        segCBMenuItem = new JCheckBoxMenuItem[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];

        ttiBySegmentPopup = new JPopupMenu();
        JMenuItem clearAllMenuItem = new JMenuItem("Clear All");
        clearAllMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int seg = 0; seg < segCBMenuItem.length; seg++) {
                    includeSegmentForTTI[seg] = false;
                    segCBMenuItem[seg].setSelected(false);
                }
                chartsUpdateMenuItem.setSelected(false);
            }
        });
        ttiBySegmentPopup.add(clearAllMenuItem);
        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int seg = 0; seg < segCBMenuItem.length; seg++) {
                    includeSegmentForTTI[seg] = false;
                    segCBMenuItem[seg].setSelected(true);
                }
                chartsUpdateMenuItem.setSelected(false);
            }
        });
        ttiBySegmentPopup.add(selectAllMenuItem);
        chartsUpdateMenuItem = new JCheckBoxMenuItem("Charts Up To Date", true);
        chartsUpdateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addResultAndChart();
            }
        });
        ttiBySegmentPopup.add(chartsUpdateMenuItem);
        ttiBySegmentPopup.addSeparator();
        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            segCBMenuItem[seg] = new JCheckBoxMenuItem("Segment " + String.valueOf(seg + 1), true);
            segCBMenuItem[seg].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem temp = (JCheckBoxMenuItem) e.getSource();
                    int segId = Integer.valueOf(temp.getText().split(" ")[1]);
                    includeSegmentForTTI[segId] = temp.isSelected();
                    chartsUpdateMenuItem.setSelected(false);
                }
            });
            ttiBySegmentPopup.add(segCBMenuItem[seg]);
        }
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

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        facilityPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        meanText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        p50Text = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        p80Text = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        p95Text = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        miseryText = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        semiSTDText = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ratingText = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        VMT2Text = new javax.swing.JTextField();
        exportRawDataButton = new javax.swing.JButton();
        generalInfoPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        projectNameText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        numSegText = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        numPeriodText = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        numScenText = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lengthText = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        seedDateText = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        startDateText = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        endDateText = new javax.swing.JTextField();
        detailTTIButton = new javax.swing.JButton();
        scenarioEventSummaryButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        chartPanel = new javax.swing.JPanel();
        scenarioSummariesTablePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        scenSummariesTable = new javax.swing.JTable();
        showAllButton = new javax.swing.JButton();
        filterScenariosButton = new javax.swing.JButton();
        exportToDSSButton = new javax.swing.JButton();
        rlSegmentButton = new javax.swing.JButton();

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        setTitle("Reliability Analysis Result Summary");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("Close");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        facilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Facility Reliability Performance Measures"));
        facilityPanel.setLayout(new java.awt.GridLayout(2, 8));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(" Mean TTI ");
        facilityPanel.add(jLabel1);

        meanText.setEditable(false);
        meanText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        meanText.setText("1.00");
        facilityPanel.add(meanText);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("     50th % TTI ");
        facilityPanel.add(jLabel4);

        p50Text.setEditable(false);
        p50Text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p50Text.setText("1.00");
        facilityPanel.add(p50Text);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("     80th % TTI ");
        facilityPanel.add(jLabel2);

        p80Text.setEditable(false);
        p80Text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p80Text.setText("1.00");
        facilityPanel.add(p80Text);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("  95th % TTI (PTI) ");
        facilityPanel.add(jLabel3);

        p95Text.setEditable(false);
        p95Text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p95Text.setText("1.00");
        facilityPanel.add(p95Text);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Misery Index ");
        facilityPanel.add(jLabel13);

        miseryText.setEditable(false);
        miseryText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        miseryText.setText("1.00");
        facilityPanel.add(miseryText);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Semi-STD ");
        facilityPanel.add(jLabel14);

        semiSTDText.setEditable(false);
        semiSTDText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        semiSTDText.setText("1.00");
        facilityPanel.add(semiSTDText);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("  Reliability Rating ");
        facilityPanel.add(jLabel15);

        ratingText.setEditable(false);
        ratingText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ratingText.setText("1.00");
        facilityPanel.add(ratingText);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("  VMT % at TTI > 2 ");
        facilityPanel.add(jLabel16);

        VMT2Text.setEditable(false);
        VMT2Text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        VMT2Text.setText("1.00");
        facilityPanel.add(VMT2Text);

        exportRawDataButton.setText("Export Raw Result Data ...");
        exportRawDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportRawDataButtonActionPerformed(evt);
            }
        });

        generalInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General Information"));
        generalInfoPanel.setLayout(new java.awt.GridLayout(2, 6));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Project Name ");
        generalInfoPanel.add(jLabel5);

        projectNameText.setEditable(false);
        projectNameText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        projectNameText.setText("New Project");
        generalInfoPanel.add(projectNameText);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("# of Segments ");
        generalInfoPanel.add(jLabel6);

        numSegText.setEditable(false);
        numSegText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numSegText.setText("34");
        generalInfoPanel.add(numSegText);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("# of A.P. ");
        generalInfoPanel.add(jLabel7);

        numPeriodText.setEditable(false);
        numPeriodText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numPeriodText.setText("20");
        generalInfoPanel.add(numPeriodText);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("# of Scenarios ");
        generalInfoPanel.add(jLabel11);

        numScenText.setEditable(false);
        numScenText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numScenText.setText("240");
        generalInfoPanel.add(numScenText);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Facility Length (mi) ");
        generalInfoPanel.add(jLabel12);

        lengthText.setEditable(false);
        lengthText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lengthText.setText("20");
        generalInfoPanel.add(lengthText);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Seed File Date ");
        generalInfoPanel.add(jLabel8);

        seedDateText.setEditable(false);
        seedDateText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        seedDateText.setText("2014-01-01");
        generalInfoPanel.add(seedDateText);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("RRP Start Date ");
        generalInfoPanel.add(jLabel9);

        startDateText.setEditable(false);
        startDateText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        startDateText.setText("2014-01-01");
        generalInfoPanel.add(startDateText);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("RRP End Date ");
        generalInfoPanel.add(jLabel10);

        endDateText.setEditable(false);
        endDateText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        endDateText.setText("2014-12-31");
        generalInfoPanel.add(endDateText);

        detailTTIButton.setText("Show TTI Percentile Detail");
        detailTTIButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailTTIButtonActionPerformed(evt);
            }
        });

        scenarioEventSummaryButton.setText("Show Scenario Event Summary");
        scenarioEventSummaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenarioEventSummaryButtonActionPerformed(evt);
            }
        });

        chartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        chartPanel.setLayout(new java.awt.GridLayout(1, 2));
        jTabbedPane1.addTab("Reliability Analysis Summary Charts", chartPanel);

        scenSummariesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scenSummariesTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                scenSummariesTableMouseMoved(evt);
            }
        });
        scenSummariesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scenSummariesTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                scenSummariesTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(scenSummariesTable);

        showAllButton.setText("Show All");
        showAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllButtonActionPerformed(evt);
            }
        });

        filterScenariosButton.setText("Filter Scenarios");
        filterScenariosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterScenariosButtonActionPerformed(evt);
            }
        });

        exportToDSSButton.setText("Export to DSS");
        exportToDSSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToDSSButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scenarioSummariesTablePanelLayout = new javax.swing.GroupLayout(scenarioSummariesTablePanel);
        scenarioSummariesTablePanel.setLayout(scenarioSummariesTablePanelLayout);
        scenarioSummariesTablePanelLayout.setHorizontalGroup(
            scenarioSummariesTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scenarioSummariesTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 795, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scenarioSummariesTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterScenariosButton, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(exportToDSSButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        scenarioSummariesTablePanelLayout.setVerticalGroup(
            scenarioSummariesTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scenarioSummariesTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scenarioSummariesTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scenarioSummariesTablePanelLayout.createSequentialGroup()
                        .addComponent(showAllButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterScenariosButton)
                        .addGap(45, 45, 45)
                        .addComponent(exportToDSSButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Scenario Summaries Table", scenarioSummariesTablePanel);

        rlSegmentButton.setText("View RL By Segment(s)");
        rlSegmentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rlSegmentButtonMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportRawDataButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailTTIButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scenarioEventSummaryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rlSegmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap())
            .addComponent(generalInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(facilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(generalInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(facilityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(exportRawDataButton)
                    .addComponent(detailTTIButton)
                    .addComponent(scenarioEventSummaryButton)
                    .addComponent(rlSegmentButton))
                .addContainerGap())
        );

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

    private void exportRawDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportRawDataButtonActionPerformed
        try {
            CSVFileChooser csvFileChooser = new CSVFileChooser(FREEVAL_HCM.getInitialDirectory());
            int option = csvFileChooser.showSaveDialog(mainWindow);
            if (option == JFileChooser.APPROVE_OPTION) {
                String csvFileName = csvFileChooser.getSelectedFile().getAbsolutePath();
                if (!csvFileName.endsWith(".csv")) {
                    csvFileName += ".csv";
                }

                //Extract data from seed
                String[][] resultData = new String[seed.getValueInt(CEConst.IDS_NUM_SCEN) * seed.getValueInt(CEConst.IDS_NUM_PERIOD)][csvHeader.length];
                for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        int row = (scen - 1) * seed.getValueInt(CEConst.IDS_NUM_PERIOD) + period;
                        resultData[row][0] = Integer.toString(scen);
                        resultData[row][1] = Integer.toString(period + 1);
                        resultData[row][2] = Float.toString(seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD));
                        resultData[row][3] = seed.getValueString(CEConst.IDS_P_TTI, 0, period, scen, -1);
                        resultData[row][4] = seed.getValueString(CEConst.IDS_P_MAX_DC, 0, period, scen, -1);
                        resultData[row][5] = seed.getValueString(CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT, 0, period, scen, -1);
                        resultData[row][6] = seed.getValueString(CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH, 0, period, scen, -1);
                        resultData[row][7] = seed.getValueString(CEConst.IDS_P_TOTAL_ON_QUEUE_VEH, 0, period, scen, -1);
                        resultData[row][8] = seed.getValueString(CEConst.IDS_P_MAIN_DELAY, 0, period, scen, -1);
                        resultData[row][9] = seed.getValueString(CEConst.IDS_P_VMTD, 0, period, scen, -1);
                        resultData[row][10] = seed.getValueString(CEConst.IDS_P_VMTV, 0, period, scen, -1);
                        resultData[row][11] = seed.getValueString(CEConst.IDS_P_VHT, 0, period, scen, -1);
                        resultData[row][12] = seed.getValueString(CEConst.IDS_P_VHD, 0, period, scen, -1);
                        resultData[row][13] = String.valueOf(seed.getRLScenarioInfo().get(scen).getNumberOfWeatherEvents());
                        int evtCounter = 0;
                        if (seed.getRLScenarioInfo().get(scen).hasWeatherEvent()) {
                            for (WeatherEvent event : seed.getRLScenarioInfo().get(scen).getWeatherEventList()) {
                                evtCounter += event.checkActiveInPeriod(period) ? 1 : 0;
                            }
                        }
                        resultData[row][14] = String.valueOf(evtCounter);
                        resultData[row][15] = String.valueOf(seed.getRLScenarioInfo().get(scen).getNumberOfGPIncidentEvents());
                        evtCounter = 0;
                        if (seed.getRLScenarioInfo().get(scen).hasIncidentGP()) {
                            for (IncidentEvent event : seed.getRLScenarioInfo().get(scen).getGPIncidentEventList()) {
                                evtCounter += event.checkActiveInPeriod(period) ? 1 : 0;
                            }
                        }
                        resultData[row][16] = String.valueOf(evtCounter);
                        resultData[row][17] = String.valueOf(seed.getRLScenarioInfo().get(scen).getNumberOfWorkZones());
                        evtCounter = 0;
                        if (seed.getRLScenarioInfo().get(scen).hasWorkZone()) {
                            for (WorkZone event : seed.getRLScenarioInfo().get(scen).getWorkZoneEventList()) {
                                evtCounter += event.isActiveIn(period) ? 1 : 0;
                            }
                        }
                        resultData[row][18] = String.valueOf(evtCounter);
                    }
                }

                //Save data to csv file
                CSVWriter writer = new CSVWriter(new FileWriter(csvFileName), ',');
                writer.writeNext(csvHeader);
                for (String[] item : resultData) {
                    writer.writeNext(item);
                }
                writer.close();

                mainWindow.printLog("Saved raw result data in " + csvFileName);
            }
        } catch (Exception e) {
            mainWindow.printLog("Error when export results " + e.toString());
        }
    }//GEN-LAST:event_exportRawDataButtonActionPerformed

    private void detailTTIButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailTTIButtonActionPerformed
        showPercentileDetail();
    }//GEN-LAST:event_detailTTIButtonActionPerformed

    private void scenarioEventSummaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenarioEventSummaryButtonActionPerformed
        RLScenarioGenerationSummaryDialog rlFSGSummaryDlg = new RLScenarioGenerationSummaryDialog(null, true);
        rlFSGSummaryDlg.setSeed(mainWindow.getActiveSeed());
        rlFSGSummaryDlg.setVisible(true);
    }//GEN-LAST:event_scenarioEventSummaryButtonActionPerformed

    private void scenSummariesTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scenSummariesTableMousePressed
        if (evt.isPopupTrigger()) {
            scenarioSelectorTablePopupTriggered(evt);
        }
    }//GEN-LAST:event_scenSummariesTableMousePressed

    private void scenSummariesTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scenSummariesTableMouseReleased
        if (evt.isPopupTrigger()) {
            scenarioSelectorTablePopupTriggered(evt);
        }
    }//GEN-LAST:event_scenSummariesTableMouseReleased

    private void showAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllButtonActionPerformed
        scenSummariesModel.showAllScenarios();
    }//GEN-LAST:event_showAllButtonActionPerformed

    private void filterScenariosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterScenariosButtonActionPerformed
        fireFilterDialog(-1);
    }//GEN-LAST:event_filterScenariosButtonActionPerformed

    private void exportToDSSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToDSSButtonActionPerformed
        int row = scenSummariesTable.getSelectedRow();
        int scenarioIdx = (int) scenSummariesModel.getValueAt(scenSummariesTable.getRowSorter().convertRowIndexToModel(row), 0) - 1;
        //Seed newSeed = new Seed(seed);
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Please select a scenario from the table to before exporting to DSS.", "No Scenario Selected", JOptionPane.ERROR_MESSAGE);
        } else {
            //System.out.println(scenarioIdx);
            //DSSIOHelper.exportScenarioToDSS(seed, scenarioIdx);
        }

    }//GEN-LAST:event_exportToDSSButtonActionPerformed

    private void scenSummariesTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scenSummariesTableMouseMoved
        tooltip.removeAll();
        tooltip.setVisible(false);
        if (evt.isControlDown()) {
            try {
                int column = scenSummariesTable.columnAtPoint(evt.getPoint());
                int row = scenSummariesTable.rowAtPoint(evt.getPoint());

                JTextArea tip = new JTextArea(scenSummariesModel.getToolTip(scenSummariesTable.getRowSorter().convertRowIndexToModel(row), column));
                tip.setEditable(false);
                tooltip.add(tip);
                tooltip.show(scenSummariesTable, evt.getX() + 15, evt.getY());
            } catch (NullPointerException e) {
                // Catches the chance if there is no plan assigned to scenario
            }
        }
    }//GEN-LAST:event_scenSummariesTableMouseMoved

    private void rlSegmentButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rlSegmentButtonMousePressed
        ttiBySegmentPopup.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_rlSegmentButtonMousePressed

    private void showPercentileDetail() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new java.awt.GridLayout(3 + PERCENT_GROUP.length, 3));

        detailPanel.add(new JLabel(" "));
        JLabel titalLebelRL = new JLabel(" RL TTI ");
        titalLebelRL.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(titalLebelRL);
        JLabel titalLebelField = new JLabel(" Field TTI ");
        titalLebelField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(titalLebelField);

        detailPanel.add(new JLabel("  Min TTI "));
        JTextField minText = new JTextField();
        minText.setEditable(false);
        minText.setText(formatter2d.format(minTTI));
        minText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(minText);
        JTextField minTextField = new JTextField();
        minTextField.setEditable(false);
        minTextField.setText(" ");
        minTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(minTextField);

        final JTextField[] percentTextField = new JTextField[PERCENT_GROUP.length];
        for (int i = 0; i < PERCENT_GROUP.length; i++) {
            detailPanel.add(new JLabel("  " + formatter0dp.format(PERCENT_GROUP[i]) + " TTI "));
            JTextField percentText = new JTextField();
            percentText.setEditable(false);
            percentText.setText(formatter2d.format(TTI_Group[i]));
            percentText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            detailPanel.add(percentText);
            percentTextField[i] = new JTextField();
            percentTextField[i].setEditable(true);
            percentTextField[i].setText(formatter2d.format(seed.getTTI_Value()[i]));
            percentTextField[i].setHorizontalAlignment(javax.swing.JTextField.CENTER);
            detailPanel.add(percentTextField[i]);
        }

        detailPanel.add(new JLabel("  Max TTI "));
        JTextField maxText = new JTextField();
        maxText.setEditable(false);
        maxText.setText(formatter2d.format(maxTTI));
        maxText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(maxText);

        //save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                for (int i = 0; i < PERCENT_GROUP.length; i++) {
                    try {
                        seed.getTTI_Value()[i] = Float.parseFloat(percentTextField[i].getText());
                    } catch (Exception e) {
                        //skip
                    }
                    percentTextField[i].setText(formatter2d.format(seed.getTTI_Value()[i]));
                    addResultAndChart();
                }
            }
        });
        detailPanel.add(saveButton);

        JOptionPane.showMessageDialog(this, detailPanel,
                "TTI Percentile Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField VMT2Text;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton detailTTIButton;
    private javax.swing.JTextField endDateText;
    private javax.swing.JButton exportRawDataButton;
    private javax.swing.JButton exportToDSSButton;
    private javax.swing.JPanel facilityPanel;
    private javax.swing.JButton filterScenariosButton;
    private javax.swing.JPanel generalInfoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lengthText;
    private javax.swing.JTextField meanText;
    private javax.swing.JTextField miseryText;
    private javax.swing.JTextField numPeriodText;
    private javax.swing.JTextField numScenText;
    private javax.swing.JTextField numSegText;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField p50Text;
    private javax.swing.JTextField p80Text;
    private javax.swing.JTextField p95Text;
    private javax.swing.JTextField projectNameText;
    private javax.swing.JTextField ratingText;
    private javax.swing.JButton rlSegmentButton;
    private javax.swing.JTable scenSummariesTable;
    private javax.swing.JButton scenarioEventSummaryButton;
    private javax.swing.JPanel scenarioSummariesTablePanel;
    private javax.swing.JTextField seedDateText;
    private javax.swing.JTextField semiSTDText;
    private javax.swing.JButton showAllButton;
    private javax.swing.JTextField startDateText;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;

    private float minTTI, maxTTI;

    private final Seed seed;

    private final MainWindow mainWindow;

    private static final DecimalFormat formatter2d = new DecimalFormat("#.00");

    private static final DecimalFormat formatter1d = new DecimalFormat("#.0");

    private static final DecimalFormat formatter2dp = new DecimalFormat("#.00%");

    private static final DecimalFormat formatter0dp = new DecimalFormat("#%");

    private static final int NUM_GROUP = 21;

    private static final float GROUP_INCREMENT = 0.2f;

    private static final float LOWER_BOUND = 1;

    private static final String[] csvHeader = new String[]{
        "Scenario #", "Analysis Period #", "Probability", "TTI",
        "max d/c ratio", "Total Queue length at end of time interval (ft)",
        "Total Denied Entry Queue Length (ft)", "Total On-Ramp queue (veh)",
        "Freeway mainline delay (min)", //"System delay-- includes on-ramps  (min)",
        "VMTD Veh-miles / interval  (Demand)", "VMTV Veh-miles / interval (Volume served)",
        "VHT travel / interval (hrs)", "VHD  delay /interval  (hrs)",
        "Number of Scenario Weather Events",
        "Number of Weather Events Active in AP",
        "Number of Scenario Incidents (GP)",
        "Number of Incidents Active in AP",
        "Number of Scenario Work Zones",
        "Number of Work Zones Active in AP"}; //19 items for now

    private static final float[] PERCENT_GROUP = new float[]{0.05f, 0.10f, 0.15f, 0.20f,
        0.25f, 0.30f, 0.35f, 0.40f,
        0.45f, 0.50f, 0.55f, 0.60f,
        0.65f, 0.70f, 0.75f, 0.80f,
        0.85f, 0.90f, 0.95f, 0.99f};

    private final float[] TTI_Group = new float[PERCENT_GROUP.length];
}
