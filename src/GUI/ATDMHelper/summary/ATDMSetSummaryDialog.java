package GUI.ATDMHelper.summary;

import GUI.major.MainWindow;
import GUI.seedEditAndIOHelper.CSVFileChooser;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.CSVWriter;
import coreEngine.Seed;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
 * This dialog shows the reliability summary for one seed
 *
 * @author Shu Liu
 */
public class ATDMSetSummaryDialog extends javax.swing.JDialog {

    private class Result implements Comparable {

        float TTI;

        float prob;

        /**
         *
         * @param prob
         * @param TTI
         */
        public Result(float prob, float TTI) {
            this.prob = prob;
            this.TTI = TTI;
        }

        @Override
        public int compareTo(Object o) {
            return Float.compare(TTI, ((Result) o).TTI);
        }
    }

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form ATDMSummaryDialog
     *
     * @param seed the seed to be shown
     * @param atdmIndex
     * @param inSetOnly
     * @param mainWindow
     */
    public ATDMSetSummaryDialog(Seed seed, int atdmIndex, boolean inSetOnly, MainWindow mainWindow) {
        super(mainWindow, true);
        initComponents();

        this.setTitle("ATDM Set #" + (atdmIndex + 1) + " Summary  "
                + (inSetOnly ? "(In ATDM Set Only)" : "(Projected All Scenarios)"));

        this.seed = seed;
        this.atdmIndex = atdmIndex;
        this.inSetOnly = inSetOnly;
        this.mainWindow = mainWindow;

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
    }

    private void addGeneralInfo() {
        projectNameText.setText(seed.getValueString(CEConst.IDS_PROJECT_NAME));
        numSegText.setText(seed.getValueString(CEConst.IDS_NUM_SEGMENT));
        numPeriodText.setText(seed.getValueString(CEConst.IDS_NUM_PERIOD));
        numScenText.setText(seed.getValueString(CEConst.IDS_ATDM_SCEN_IN_EACH_SET, 0, 0, 0, atdmIndex) + " / " + seed.getValueString(CEConst.IDS_NUM_SCEN));
        lengthText.setText(formatter1d.format(seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI)));
        seedDateText.setText(seed.getSeedFileDate().toString());
        startDateText.setText(seed.getRRPStartDate().toString());
        endDateText.setText(seed.getRRPEndDate().toString());
    }

    private void addResultAndChart() {
        try {

            // <editor-fold defaultstate="collapsed" desc="EXTRACT DATA AND CALCULATE %">
            ArrayList<Result> ATDMResults = new ArrayList<>();
            ArrayList<Result> RLResults = new ArrayList<>();

            float totalProb = 0;
            if (inSetOnly) {
                //count total probability
                for (int scen : seed.getATDMSets().get(atdmIndex).keySet()) {
                    totalProb += seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1);
                }
            } else {
                totalProb = 1;
            }

            // <editor-fold defaultstate="collapsed" desc="EXTRACT BEFORE ATDM (RL) RESULT">
            //calculate RL results
            float meanRL = 0;
            float ratingCountRL = 0;
            float VMT2CountRL = 0;
            float semiSTDRL = 0;

            //extract data from seed, and modify probability to match per period
            if (inSetOnly) {
                for (int scen : seed.getATDMSets().get(atdmIndex).keySet()) {
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD) / totalProb;
                        float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                        RLResults.add(new Result(prob, TTI));
                        meanRL += TTI * prob;
                        semiSTDRL += (TTI - 1) * (TTI - 1) * prob;
                        if (TTI < 1.333333f) {
                            ratingCountRL += prob;
                        }
                        if (TTI > 2) {
                            VMT2CountRL += prob;
                        }
                    }
                }
            } else {
                for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD) / totalProb;
                        float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                        RLResults.add(new Result(prob, TTI));
                        meanRL += TTI * prob;
                        semiSTDRL += (TTI - 1) * (TTI - 1) * prob;
                        if (TTI < 1.333333f) {
                            ratingCountRL += prob;
                        }
                        if (TTI > 2) {
                            VMT2CountRL += prob;
                        }
                    }
                }
            }
            Collections.sort(RLResults);

            //--------------------------------------------------------------------------
            minTTIRL = RLResults.get(0).TTI;
            maxTTIRL = RLResults.get(RLResults.size() - 1).TTI;

            meanTextBefore.setText(formatter2d.format(meanRL));

            semiSTDRL = (float) Math.sqrt(semiSTDRL);
            semiSTDTextBefore.setText(formatter2d.format(semiSTDRL));

            float probCountRL = 0;
            float miseryTotalRL = 0, miseryWeightRL = 0;
            for (Result RLResult : RLResults) {
                //find 50th %
                if (probCountRL <= 0.5 && probCountRL + RLResult.prob >= 0.5) {
                    p50TextBefore.setText(formatter2d.format(RLResult.TTI));
                }
                //find 80th %
                if (probCountRL <= 0.8 && probCountRL + RLResult.prob >= 0.8) {
                    p80TextBefore.setText(formatter2d.format(RLResult.TTI));
                }
                //find 95th %
                if (probCountRL <= 0.95 && probCountRL + RLResult.prob >= 0.95) {
                    p95TextBefore.setText(formatter2d.format(RLResult.TTI));
                }

                for (int i = 0; i < PERCENT_GROUP.length; i++) {
                    if (probCountRL < PERCENT_GROUP[i] && probCountRL + RLResult.prob >= PERCENT_GROUP[i]) {
                        TTI_Group_RL[i] = RLResult.TTI;
                    }
                }

                probCountRL += RLResult.prob;

                if (probCountRL >= 0.95) {
                    miseryTotalRL += RLResult.TTI * RLResult.prob;
                    miseryWeightRL += RLResult.prob;
                }
            }

            miseryTextBefore.setText(formatter2d.format(miseryTotalRL / miseryWeightRL));
            ratingTextBefore.setText(formatter2dp.format(ratingCountRL));
            VMT2TextBefore.setText(formatter2dp.format(VMT2CountRL));
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="EXTRACT AFTER ATDM RESULT">
            //calculate ATDM results
            float meanATDM = 0;
            float ratingCountATDM = 0;
            float VMT2CountATDM = 0;
            float semiSTDATDM = 0;

            //extract data from seed, and modify probability to match per period
            if (inSetOnly) {
                for (int scen : seed.getATDMSets().get(atdmIndex).keySet()) {
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, atdmIndex) / seed.getValueInt(CEConst.IDS_NUM_PERIOD) / totalProb;
                        float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, atdmIndex);
                        ATDMResults.add(new Result(prob, TTI));
                        meanATDM += TTI * prob;
                        semiSTDATDM += (TTI - 1) * (TTI - 1) * prob;
                        if (TTI < 1.333333f) {
                            ratingCountATDM += prob;
                        }
                        if (TTI > 2) {
                            VMT2CountATDM += prob;
                        }
                    }
                }
            } else {
                Integer[] tempSampleInt = seed.getATDMSets().get(atdmIndex).keySet().toArray(new Integer[0]);
                int[] sample = new int[tempSampleInt.length];
                for (int i = 0; i < tempSampleInt.length; i++) {
                    sample[i] = tempSampleInt[i];
                }
                MomentMatcher mm = new MomentMatcher(seed, sample, 2, atdmIndex);
                mm.MMHeuristic();
                float[] pi = mm.getPi();
                float[] atdmTTI = mm.getSampleATDMTTI();

                for (int i = 0; i < pi.length; i++) {
                    ATDMResults.add(new Result(pi[i], atdmTTI[i]));
                    meanATDM += atdmTTI[i] * pi[i];
                    semiSTDATDM += (atdmTTI[i] - 1) * (atdmTTI[i] - 1) * pi[i];
                    if (atdmTTI[i] < 1.333333f) {
                        ratingCountATDM += pi[i];
                    }
                    if (atdmTTI[i] > 2) {
                        VMT2CountATDM += pi[i];
                    }
                }
//                for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
//                    int mapATDMIndex = seed.getATDMSets().get(atdmIndex).containsKey(scen) ? atdmIndex : -1;
//                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
//                        float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, mapATDMIndex) / seed.getValueInt(CEConst.IDS_NUM_PERIOD) / totalProb;
//                        float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, mapATDMIndex);
//                        ATDMResults.add(new Result(prob, TTI));
//                        meanATDM += TTI * prob;
//                        semiSTDATDM += (TTI - 1) * (TTI - 1) * prob;
//                        if (TTI < 1.333333f) {
//                            ratingCountATDM += prob;
//                        }
//                        if (TTI > 2) {
//                            VMT2CountATDM += prob;
//                        }
//                    }
//                }
            }
            Collections.sort(ATDMResults);

            //--------------------------------------------------------------------------
            minTTIATDM = ATDMResults.get(0).TTI;
            maxTTIATDM = ATDMResults.get(ATDMResults.size() - 1).TTI;

            meanTextAfter.setText(formatter2d.format(meanATDM));

            semiSTDATDM = (float) Math.sqrt(semiSTDATDM);
            semiSTDTextAfter.setText(formatter2d.format(semiSTDATDM));

            float probCountATDM = 0;
            float miseryTotalATDM = 0, miseryWeightATDM = 0;
            for (Result ATDMResult : ATDMResults) {
                //find 50th %
                if (probCountATDM <= 0.5 && probCountATDM + ATDMResult.prob >= 0.5) {
                    p50TextAfter.setText(formatter2d.format(ATDMResult.TTI));
                }
                //find 80th %
                if (probCountATDM <= 0.8 && probCountATDM + ATDMResult.prob >= 0.8) {
                    p80TextAfter.setText(formatter2d.format(ATDMResult.TTI));
                }
                //find 95th %
                if (probCountATDM <= 0.95 && probCountATDM + ATDMResult.prob >= 0.95) {
                    p95TextAfter.setText(formatter2d.format(ATDMResult.TTI));
                }

                for (int i = 0; i < PERCENT_GROUP.length; i++) {
                    if (probCountATDM < PERCENT_GROUP[i] && probCountATDM + ATDMResult.prob >= PERCENT_GROUP[i]) {
                        TTI_Group_ATDM[i] = ATDMResult.TTI;
                    }
                }

                probCountATDM += ATDMResult.prob;

                if (probCountATDM >= 0.95) {
                    miseryTotalATDM += ATDMResult.TTI * ATDMResult.prob;
                    miseryWeightATDM += ATDMResult.prob;
                }
            }

            miseryTextAfter.setText(formatter2d.format(miseryTotalATDM / miseryWeightATDM));
            ratingTextAfter.setText(formatter2dp.format(ratingCountATDM));
            VMT2TextAfter.setText(formatter2dp.format(VMT2CountATDM));
            // </editor-fold>
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="ADD TWO CHARTS">
            // Define the data for the both charts
            final XYSeries seriesTTIRL = new XYSeries("TTI - Before");
            float[] groupCountRL = new float[NUM_GROUP];

            float count = 0;
            for (Result result : RLResults) {
                count += result.prob;
                seriesTTIRL.add(result.TTI, count);
                int groupIndex = (int) Math.min(Math.max(result.TTI - LOWER_BOUND, 0) / GROUP_INCREMENT,
                        NUM_GROUP - 1);
                groupCountRL[groupIndex] += result.prob;
            }

            final XYSeries seriesTTIATDM = new XYSeries("TTI - After");
            float[] groupCountATDM = new float[NUM_GROUP];

            count = 0;
            for (Result result : ATDMResults) {
                count += result.prob;
                seriesTTIATDM.add(result.TTI, count);
                int groupIndex = (int) Math.min(Math.max(result.TTI - LOWER_BOUND, 0) / GROUP_INCREMENT,
                        NUM_GROUP - 1);
                groupCountATDM[groupIndex] += result.prob;
            }

            //create bar chart
            DefaultCategoryDataset barChartDataset = new DefaultCategoryDataset();
            for (int i = 0; i < groupCountRL.length - 1; i++) {
                barChartDataset.addValue(groupCountRL[i], "TTI - Before",
                        "[" + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * i) + ","
                        + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (i + 1)) + ")");
                barChartDataset.addValue(groupCountATDM[i], "TTI - After",
                        "[" + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * i) + ","
                        + formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (i + 1)) + ")");
            }

            barChartDataset.addValue(groupCountRL[groupCountRL.length - 1], "TTI - Before",
                    formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (groupCountRL.length - 1)) + "+");
            barChartDataset.addValue(groupCountATDM[groupCountATDM.length - 1], "TTI - After",
                    formatter1d.format(LOWER_BOUND + GROUP_INCREMENT * (groupCountRL.length - 1)) + "+");

            JFreeChart barChartObject = ChartFactory.createBarChart(
                    "Probability  Distribution Function", "TTI", "Percentage",
                    barChartDataset, PlotOrientation.VERTICAL, true, true, false);
            ((NumberAxis) ((CategoryPlot) barChartObject.getPlot()).getRangeAxis()).setNumberFormatOverride(NumberFormat.getPercentInstance());
            ((CategoryPlot) barChartObject.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            chartPanel.add(new ChartPanel(barChartObject));

            // create line chart
            final XYSeriesCollection lineChartDataset = new XYSeriesCollection();
            lineChartDataset.addSeries(seriesTTIRL);
            lineChartDataset.addSeries(seriesTTIATDM);
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
        facilityPanelBefore = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        meanTextBefore = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        p50TextBefore = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        p80TextBefore = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        p95TextBefore = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        miseryTextBefore = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        semiSTDTextBefore = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ratingTextBefore = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        VMT2TextBefore = new javax.swing.JTextField();
        chartPanel = new javax.swing.JPanel();
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
        facilityPanelAfter = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        meanTextAfter = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        p50TextAfter = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        p80TextAfter = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        p95TextAfter = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        miseryTextAfter = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        semiSTDTextAfter = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        ratingTextAfter = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        VMT2TextAfter = new javax.swing.JTextField();

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        setTitle("ATDM Set #1 Summary");
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

        facilityPanelBefore.setBorder(javax.swing.BorderFactory.createTitledBorder("Facility Reliability Performance Measures Before ATDM"));
        facilityPanelBefore.setLayout(new java.awt.GridLayout(2, 8));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(" Mean TTI ");
        facilityPanelBefore.add(jLabel1);

        meanTextBefore.setEditable(false);
        meanTextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        meanTextBefore.setText("1.00");
        facilityPanelBefore.add(meanTextBefore);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("     50th % TTI ");
        facilityPanelBefore.add(jLabel4);

        p50TextBefore.setEditable(false);
        p50TextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p50TextBefore.setText("1.00");
        facilityPanelBefore.add(p50TextBefore);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("     80th % TTI ");
        facilityPanelBefore.add(jLabel2);

        p80TextBefore.setEditable(false);
        p80TextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p80TextBefore.setText("1.00");
        facilityPanelBefore.add(p80TextBefore);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("  95th % TTI ");
        facilityPanelBefore.add(jLabel3);

        p95TextBefore.setEditable(false);
        p95TextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p95TextBefore.setText("1.00");
        facilityPanelBefore.add(p95TextBefore);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Misery Index ");
        facilityPanelBefore.add(jLabel13);

        miseryTextBefore.setEditable(false);
        miseryTextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        miseryTextBefore.setText("1.00");
        facilityPanelBefore.add(miseryTextBefore);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Semi-STD ");
        facilityPanelBefore.add(jLabel14);

        semiSTDTextBefore.setEditable(false);
        semiSTDTextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        semiSTDTextBefore.setText("1.00");
        facilityPanelBefore.add(semiSTDTextBefore);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("  Reliability Rating ");
        facilityPanelBefore.add(jLabel15);

        ratingTextBefore.setEditable(false);
        ratingTextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ratingTextBefore.setText("1.00");
        facilityPanelBefore.add(ratingTextBefore);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("  VMT % at TTI > 2 ");
        facilityPanelBefore.add(jLabel16);

        VMT2TextBefore.setEditable(false);
        VMT2TextBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        VMT2TextBefore.setText("1.00");
        facilityPanelBefore.add(VMT2TextBefore);

        chartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chart"));
        chartPanel.setLayout(new java.awt.GridLayout(1, 2));

        exportRawDataButton.setText("Export Raw Result Data ...");
        exportRawDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportRawDataButtonActionPerformed(evt);
            }
        });

        generalInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("ATDM Set General Information"));
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

        facilityPanelAfter.setBorder(javax.swing.BorderFactory.createTitledBorder("Facility Reliability Performance Measures After ATDM"));
        facilityPanelAfter.setLayout(new java.awt.GridLayout(2, 8));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText(" Mean TTI ");
        facilityPanelAfter.add(jLabel17);

        meanTextAfter.setEditable(false);
        meanTextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        meanTextAfter.setText("1.00");
        facilityPanelAfter.add(meanTextAfter);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("     50th % TTI ");
        facilityPanelAfter.add(jLabel18);

        p50TextAfter.setEditable(false);
        p50TextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p50TextAfter.setText("1.00");
        facilityPanelAfter.add(p50TextAfter);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("     80th % TTI ");
        facilityPanelAfter.add(jLabel19);

        p80TextAfter.setEditable(false);
        p80TextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p80TextAfter.setText("1.00");
        facilityPanelAfter.add(p80TextAfter);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("  95th % TTI ");
        facilityPanelAfter.add(jLabel20);

        p95TextAfter.setEditable(false);
        p95TextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        p95TextAfter.setText("1.00");
        facilityPanelAfter.add(p95TextAfter);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Misery Index ");
        facilityPanelAfter.add(jLabel21);

        miseryTextAfter.setEditable(false);
        miseryTextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        miseryTextAfter.setText("1.00");
        facilityPanelAfter.add(miseryTextAfter);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Semi-STD ");
        facilityPanelAfter.add(jLabel22);

        semiSTDTextAfter.setEditable(false);
        semiSTDTextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        semiSTDTextAfter.setText("1.00");
        facilityPanelAfter.add(semiSTDTextAfter);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("  Reliability Rating ");
        facilityPanelAfter.add(jLabel23);

        ratingTextAfter.setEditable(false);
        ratingTextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ratingTextAfter.setText("1.00");
        facilityPanelAfter.add(ratingTextAfter);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("  VMT % at TTI > 2 ");
        facilityPanelAfter.add(jLabel24);

        VMT2TextAfter.setEditable(false);
        VMT2TextAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        VMT2TextAfter.setText("1.00");
        facilityPanelAfter.add(VMT2TextAfter);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportRawDataButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailTTIButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap())
            .addComponent(generalInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(facilityPanelBefore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(facilityPanelAfter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(generalInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(facilityPanelBefore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(facilityPanelAfter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(exportRawDataButton)
                    .addComponent(detailTTIButton))
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
                String[][] resultData
                        = new String[seed.getValueInt(CEConst.IDS_ATDM_SCEN_IN_EACH_SET, 0, 0, 0, atdmIndex) * 2 * seed.getValueInt(CEConst.IDS_NUM_PERIOD)][csvHeader.length];
                int row = 0;
                for (int scen : seed.getATDMSets().get(atdmIndex).keySet()) {
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        //TODO hard code order for now
                        resultData[row][0] = "Scenario " + Integer.toString(scen) + " Before ATDM";
                        resultData[row][1] = Integer.toString(period + 1);
                        resultData[row][2] = Float.toString(seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD));
                        resultData[row][3] = seed.getValueString(CEConst.IDS_P_TTI, 0, period, scen, -1);
                        resultData[row][4] = seed.getValueString(CEConst.IDS_P_MAX_DC, 0, period, scen, -1);
                        resultData[row][5] = seed.getValueString(CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT, 0, period, scen, -1);
                        resultData[row][6] = seed.getValueString(CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH, 0, period, scen, -1);
                        resultData[row][7] = seed.getValueString(CEConst.IDS_P_TOTAL_ON_QUEUE_VEH, 0, period, scen, -1);
                        resultData[row][8] = seed.getValueString(CEConst.IDS_P_MAIN_DELAY, 0, period, scen, -1);
                        //resultData[row][9] = seed.getValueString(CEConst.IDS_P_SYS_DELAY, 0, period, scen, -1);
                        resultData[row][9] = seed.getValueString(CEConst.IDS_P_VMTD, 0, period, scen, -1);
                        resultData[row][10] = seed.getValueString(CEConst.IDS_P_VMTV, 0, period, scen, -1);
                        resultData[row][11] = seed.getValueString(CEConst.IDS_P_VHT, 0, period, scen, -1);
                        resultData[row][12] = seed.getValueString(CEConst.IDS_P_VHD, 0, period, scen, -1);
                        row++;
                    }
                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                        //TODO hard code order for now
                        resultData[row][0] = "Scenario " + Integer.toString(scen) + " After ATDM";
                        resultData[row][1] = Integer.toString(period + 1);
                        resultData[row][2] = Float.toString(seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, atdmIndex) / seed.getValueInt(CEConst.IDS_NUM_PERIOD));
                        resultData[row][3] = seed.getValueString(CEConst.IDS_P_TTI, 0, period, scen, atdmIndex);
                        resultData[row][4] = seed.getValueString(CEConst.IDS_P_MAX_DC, 0, period, scen, atdmIndex);
                        resultData[row][5] = seed.getValueString(CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT, 0, period, scen, atdmIndex);
                        resultData[row][6] = seed.getValueString(CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH, 0, period, scen, atdmIndex);
                        resultData[row][7] = seed.getValueString(CEConst.IDS_P_TOTAL_ON_QUEUE_VEH, 0, period, scen, atdmIndex);
                        resultData[row][8] = seed.getValueString(CEConst.IDS_P_MAIN_DELAY, 0, period, scen, atdmIndex);
                        //resultData[row][9] = seed.getValueString(CEConst.IDS_P_SYS_DELAY, 0, period, scen, atdmIndex);
                        resultData[row][9] = seed.getValueString(CEConst.IDS_P_VMTD, 0, period, scen, atdmIndex);
                        resultData[row][10] = seed.getValueString(CEConst.IDS_P_VMTV, 0, period, scen, atdmIndex);
                        resultData[row][11] = seed.getValueString(CEConst.IDS_P_VHT, 0, period, scen, atdmIndex);
                        resultData[row][12] = seed.getValueString(CEConst.IDS_P_VHD, 0, period, scen, atdmIndex);
                        row++;
                    }
                }

                //Save data to csv file
                CSVWriter writer = new CSVWriter(new FileWriter(csvFileName), '\t');
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

    private void showPercentileDetail() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new java.awt.GridLayout(3 + PERCENT_GROUP.length, 3));

        //create title
        detailPanel.add(new JLabel(" "));
        JLabel titalLebelBefore = new JLabel(" Before ATDM ");
        titalLebelBefore.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(titalLebelBefore);
        JLabel titalLebelAfter = new JLabel(" After ATDM ");
        titalLebelAfter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(titalLebelAfter);

        detailPanel.add(new JLabel("  Min TTI "));
        JTextField minTextRL = new JTextField();
        minTextRL.setEditable(false);
        minTextRL.setText(formatter2d.format(minTTIRL));
        minTextRL.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(minTextRL);
        JTextField minTextATDM = new JTextField();
        minTextATDM.setEditable(false);
        minTextATDM.setText(formatter2d.format(minTTIATDM));
        minTextATDM.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(minTextATDM);

        for (int i = 0; i < PERCENT_GROUP.length; i++) {
            detailPanel.add(new JLabel("  " + formatter0dp.format(PERCENT_GROUP[i]) + " TTI "));
            JTextField percentTextRL = new JTextField();
            percentTextRL.setEditable(false);
            percentTextRL.setText(formatter2d.format(TTI_Group_RL[i]));
            percentTextRL.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            detailPanel.add(percentTextRL);
            JTextField percentTextATDM = new JTextField();
            percentTextATDM.setEditable(false);
            percentTextATDM.setText(formatter2d.format(TTI_Group_ATDM[i]));
            percentTextATDM.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            detailPanel.add(percentTextATDM);
        }

        detailPanel.add(new JLabel("  Max TTI "));
        JTextField maxTextRL = new JTextField();
        maxTextRL.setEditable(false);
        maxTextRL.setText(formatter2d.format(maxTTIRL));
        maxTextRL.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(maxTextRL);
        JTextField maxTextATDM = new JTextField();
        maxTextATDM.setEditable(false);
        maxTextATDM.setText(formatter2d.format(maxTTIATDM));
        maxTextATDM.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        detailPanel.add(maxTextATDM);

        JOptionPane.showMessageDialog(this, detailPanel,
                "TTI Percentile Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField VMT2TextAfter;
    private javax.swing.JTextField VMT2TextBefore;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton detailTTIButton;
    private javax.swing.JTextField endDateText;
    private javax.swing.JButton exportRawDataButton;
    private javax.swing.JPanel facilityPanelAfter;
    private javax.swing.JPanel facilityPanelBefore;
    private javax.swing.JPanel generalInfoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField lengthText;
    private javax.swing.JTextField meanTextAfter;
    private javax.swing.JTextField meanTextBefore;
    private javax.swing.JTextField miseryTextAfter;
    private javax.swing.JTextField miseryTextBefore;
    private javax.swing.JTextField numPeriodText;
    private javax.swing.JTextField numScenText;
    private javax.swing.JTextField numSegText;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField p50TextAfter;
    private javax.swing.JTextField p50TextBefore;
    private javax.swing.JTextField p80TextAfter;
    private javax.swing.JTextField p80TextBefore;
    private javax.swing.JTextField p95TextAfter;
    private javax.swing.JTextField p95TextBefore;
    private javax.swing.JTextField projectNameText;
    private javax.swing.JTextField ratingTextAfter;
    private javax.swing.JTextField ratingTextBefore;
    private javax.swing.JTextField seedDateText;
    private javax.swing.JTextField semiSTDTextAfter;
    private javax.swing.JTextField semiSTDTextBefore;
    private javax.swing.JTextField startDateText;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;

    private float minTTIRL,
            maxTTIRL,
            minTTIATDM,
            maxTTIATDM;

    private final Seed seed;

    private final int atdmIndex;

    private final boolean inSetOnly;

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
        "VHT travel / interval (hrs)", "VHD  delay /interval  (hrs)"}; //14 items for now

    private static final float[] PERCENT_GROUP = new float[]{0.05f, 0.10f, 0.15f, 0.20f,
        0.25f, 0.30f, 0.35f, 0.40f,
        0.45f, 0.50f, 0.55f, 0.60f,
        0.65f, 0.70f, 0.75f, 0.80f,
        0.85f, 0.90f, 0.95f, 0.99f};

    private final float[] TTI_Group_RL = new float[PERCENT_GROUP.length];

    private final float[] TTI_Group_ATDM = new float[PERCENT_GROUP.length];
}
