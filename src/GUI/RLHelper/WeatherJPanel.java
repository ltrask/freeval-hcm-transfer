package GUI.RLHelper;

import GUI.RLHelper.Renderer.AlternatingColorsRenderer;
import GUI.RLHelper.TableModels.WeatherDataModel;
import GUI.major.MainWindow;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.WeatherData;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import main.FREEVAL_HCM;

/**
 *
 * @author Tristan and Lake
 */
public class WeatherJPanel extends javax.swing.JPanel {

    private WeatherData weatherData;

    private WeatherDataModel upperDataModel;

    private WeatherDataModel lowerDataModel;

    private AlternatingColorsRenderer alternatingRenderer;

    private AlternatingColorsRenderer alternatingRendererPct;

    private Seed seed;

    /**
     * Creates new form WeatherJPanel
     */
    public WeatherJPanel() {
        initComponents();
        disableEverything();
    }

    /**
     * Activates the weather panel with a new WeatherData with given RRP dates.
     *
     * @param year
     * @param startMonth
     * @param startDay
     * @param endMonth
     * @param endDay
     */
    public void activateWeatherJPanel(int year, int startMonth, int startDay, int endMonth, int endDay) {

        enableEverything();

        // Set up data and model
        weatherData = new WeatherData();

        for (int i = 0; i < 12; ++i) {
            if (i < startMonth || i > endMonth) {
                weatherData.setMonthActive(i, false);
            }
        }

        activateWeatherJPanel();
    }

    /**
     * Activates the weather panel pulling data from the seed.
     *
     * @param seed
     */
    public void activateWeatherJPanel(Seed seed) {
        this.seed = seed;

        enableEverything();

        int year = seed.getSeedFileDate().year;
        int startMonth = seed.getRRPStartDate().month - 1;  // Minus 1 to correct indexing (stored starting at 1, but indexing starts at 0)
        int startDay = seed.getRRPStartDate().day - 1;      // Minus 1 to correct indexing
        int endMonth = seed.getRRPEndDate().month - 1;      // Minus 1 to correct indexing
        int endDay = seed.getRRPEndDate().day - 1;          // Minus 1 to correct indexing

        // Set up data and model
        weatherData = new WeatherData();
        weatherData.initializeBySeed(seed);

        for (int i = 0; i < 12; ++i) {
            if (i < startMonth || i > endMonth) {
                weatherData.setMonthActive(i, false);
            }
        }

        updateMetroArea(weatherData.getNearestMetroArea());
        activateWeatherJPanel();
        //setupTableColumns();
    }

    /**
     * General activator for the weather panel. Can only be called if
     * weatherData has been set.
     */
    public void activateWeatherJPanel() {

        if (weatherData != null) {
            upperDataModel = new WeatherDataModel(weatherData, 1);
            lowerDataModel = new WeatherDataModel(weatherData, 2);
            weatherProbabilityTable.setModel(upperDataModel);
            adjustmentFactorTable.setModel(lowerDataModel);

            alternatingRenderer = new AlternatingColorsRenderer(true, "%.2f");
            alternatingRendererPct = new AlternatingColorsRenderer(true, "%.1f%%");
            weatherProbabilityTable.setDefaultRenderer(Object.class, alternatingRendererPct);
            weatherProbabilityTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));
            adjustmentFactorTable.setDefaultRenderer(Object.class, alternatingRenderer);
            adjustmentFactorTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));

            // Make the tables look a little nicer
            Font tableFont = weatherProbabilityTable.getTableHeader().getFont();
            Font newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
            weatherProbabilityTable.getTableHeader().setFont(newHeaderFont);

            tableFont = adjustmentFactorTable.getTableHeader().getFont();
            newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
            adjustmentFactorTable.getTableHeader().setFont(newHeaderFont);

        } else {
            System.err.println("weatherData is null, panel not activated.");
        }
    }

    /**
     * Updates the panel with data pulled from an existing WeatherData. Allows
     * for the panel to be correctly updated when the RRP dates are changed
     * since the weather data has not yet been saved to the seed.
     *
     * @param oldWeatherData
     * @param year
     * @param startMonth
     * @param startDay
     * @param endMonth
     * @param endDay
     */
    public void updateWeatherJPanel(WeatherData oldWeatherData, int year, int startMonth, int startDay, int endMonth, int endDay) {

        enableEverything();

        // Set up data and model
        //weatherData = new WeatherData();
        weatherData = oldWeatherData;

        for (int i = 0; i < 12; ++i) {
            if (i < startMonth || i > endMonth) {
                weatherData.setMonthActive(i, false);
            } else {
                weatherData.setMonthActive(i, true);
            }
        }

        activateWeatherJPanel();
        //setupTableColumns();
    }

    // <editor-fold defaultstate="collapsed" desc="Utility Functions">
    /**
     * Disables all components of the panel.
     */
    private void disableEverything() {
        adjustmentFactorTable.setEnabled(false);
        extractButton.setEnabled(false);
        jLabel1.setEnabled(false);
        jScrollPane1.setEnabled(false);
        jScrollPane2.setEnabled(false);
        metroAreaComboBox.setEnabled(false);
        weatherProbabilityTable.setEnabled(false);
    }

    /**
     * Enables all components of the panel.
     */
    private void enableEverything() {
        adjustmentFactorTable.setEnabled(true);
        extractButton.setEnabled(true);
        jLabel1.setEnabled(true);
        jScrollPane1.setEnabled(true);
        jScrollPane2.setEnabled(true);
        metroAreaComboBox.setEnabled(true);
        weatherProbabilityTable.setEnabled(true);

    }

    /**
     * Updates the nearest metro area combo box to be the specified location.
     *
     * @param location
     */
    private void updateMetroArea(String location) {
        int metroIndex = 0;
        if (location != null) {
            for (int idx = 0; idx < metroAreaComboBox.getItemCount(); idx++) {
                if (location.equalsIgnoreCase((String) metroAreaComboBox.getItemAt(idx))) {
                    //metroAreaComboBox.setSelectedIndex(idx);
                    metroIndex = idx;
                    break;
                }
            }
        }
        metroAreaComboBox.setSelectedIndex(metroIndex < metroAreaComboBox.getItemCount() ? metroIndex : 0);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * Get method for the WeatherData object of the panel.
     *
     * @return WeatherData object of the panel.
     */
    public WeatherData getWeatherData() {
        return weatherData;
    }
    //</editor-fold>

    private void exportToCSVFile() {
        String facilityName = JOptionPane.showInputDialog("Enter a location (city,state) or name for the custom facility:", metroAreaComboBox.getSelectedItem());
        JFileChooser fc = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
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
        fc.setDialogTitle("Save - Choose Weather Info File");
        int option = fc.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            String saveFileName = fc.getSelectedFile().getAbsolutePath();
            if (!saveFileName.endsWith(".csv")) {
                saveFileName += ".csv";
            }
            // Write to file
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(saveFileName));
                bw.write(facilityName + "\n");
                bw.write("<Begin Weather Probabilities>\n");
                bw.write("Month,Med_Rain,Heavy_Rain,Light_Snow,LM_Snow,MH_Snow,Heavy_Snow,Severe_Cold,Low_Vis,Very_Low_Vis,Min_Vis,Normal_Weather\n");
                for (int month = 1; month <= 12; month++) {
                    bw.write(String.valueOf(month));
                    for (int wType = 0; wType < 11; wType++) {
                        bw.write("," + weatherData.getProbability(month - 1, wType));
                    }
                    bw.write("\n");
                }
                bw.write("<End Weather Probabilities>\n");
                bw.write("<Begin Weather Durations>\n");
                bw.write("duration");
                for (int wType = 0; wType < 11; wType++) {
                    bw.write("," + weatherData.getAverageDurationMinutes(wType));
                }
                bw.write("\n");
                bw.write("<End Weather Durations>\n");
                bw.write("<Begin Weather Adjustment Factors>\n");
                //CAF
                bw.write("CAF");
                for (int wType = 0; wType < 11; wType++) {
                    bw.write("," + weatherData.getWeatherCAF(wType));
                }
                bw.write("\n");
                //SAF
                bw.write("SAF");
                for (int wType = 0; wType < 11; wType++) {
                    bw.write("," + weatherData.getWeatherSAF(wType));
                }
                bw.write("\n");
                //DAF
                bw.write("DAF");
                for (int wType = 0; wType < 11; wType++) {
                    bw.write("," + weatherData.getWeatherDAF(wType));
                }
                bw.write("\n");
                bw.write("<End Weather Adjustment Factors>\n");
                bw.close();
                MainWindow.printLog("Weather Data Exported to: " + saveFileName);
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }

    private void importFromCSVFile() {
        JFileChooser fc = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
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
        fc.setDialogTitle("Import - Choose Weather Info File");
        int option = fc.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            String openFileName = fc.getSelectedFile().getAbsolutePath();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(openFileName));
                weatherData.setNearestMetroArea(br.readLine());
                br.readLine(); // Header line
                br.readLine(); // Header line
                float[][] newProbabilities = new float[12][11];
                for (int month = 1; month <= 12; month++) {
                    String[] tokens = br.readLine().split(",");
                    for (int wType = 0; wType < 10; wType++) {
                        newProbabilities[month - 1][wType + 1] = Float.valueOf(tokens[wType]);
                    }
                }
                weatherData.setProbability(newProbabilities);
                br.readLine(); // Header line
                br.readLine(); // Header line
                String[] tokens = br.readLine().split(",");
                float[] tempDurationsMinutes = new float[10];
                for (int wType = 0; wType < 10; wType++) {
                    tempDurationsMinutes[wType] = Float.valueOf(tokens[wType + 1]);
                }
                weatherData.setAverageDurations(tempDurationsMinutes);
                br.readLine(); // Header line
                br.readLine(); // Header line
                //CAF
                tokens = br.readLine().split(",");
                for (int wType = 0; wType < 10; wType++) {
                    weatherData.setWeatherCAF(wType, Float.valueOf(tokens[wType + 1]));
                }
                //SAF
                tokens = br.readLine().split(",");
                for (int wType = 0; wType < 10; wType++) {
                    weatherData.setWeatherSAF(wType, Float.valueOf(tokens[wType + 1]));
                }
                //DAF
                tokens = br.readLine().split(",");
                for (int wType = 0; wType < 10; wType++) {
                    weatherData.setWeatherDAF(wType, Float.valueOf(tokens[wType + 1]));
                }
                br.close();
                MainWindow.printLog("Weather Data Imported From: " + openFileName);
            } catch (IOException e) {

            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        metroAreaComboBox = new javax.swing.JComboBox();
        extractButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        weatherProbabilityTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        adjustmentFactorTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        useSeedValuesButton = new javax.swing.JButton();
        exportWeatherDataButton = new javax.swing.JButton();
        importFromFileButton = new javax.swing.JButton();

        jLabel1.setText("Nearest Metropolitan Area:");

        metroAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "New Facility Specific", "Akron,OH", "Albany,NY", "Albuquerque,NM", "Allentown,PA", "Atlanta,GA", "Augusta,GA", "Austin,TX", "Bakersfield,CA", "Baltimore,MD", "Baton Rouge,LA", "Birmingham,AL", "Boise City,ID", "Boston,MA", "Buffalo,NY", "Cape Coral,FL", "Charleston,SC", "Charlotte,NC", "Chattanooga,TN", "Chicago,IL", "Cincinnati,OH", "Cleveland,OH", "Colorado Springs,CO", "Columbia,SC", "Columbus,OH", "Dallas,TX", "Dayton,OH", "Denver,CO", "Des Moines,IA", "Detroit,MI", "Durham,NC", "El Paso,TX", "Fresno,CA", "Grand Rapids,MI", "Greensboro,NC", "Greenville,SC", "Harrisburg,PA", "Hartford,CT", "Honolulu,HI", "Houston,TX", "Indianapolis,IN", "Jackson,MS", "Jacksonville,FL", "Kansas City,MO", "Knoxville,TN", "Lakeland,FL", "Lancaster,PA", "Las Vegas,NV", "Little Rock,AR", "Los Angeles,CA", "Louisville,KY", "Madison,WI", "McAllen,TX", "Memphis,TN", "Miami,FL", "Milwaukee,WI", "Minneapolis,MN", "Modesto,CA", "Nashville,TN", "New Orleans,LA", "New York,NY", "North Port,FL", "Ogden,UT", "Oklahoma City,OK", "Omaha,NE", "Orlando,FL", "Oxnard,CA", "Palm Bay,FL", "Philadelphia,PA", "Phoenix,AZ", "Pittsburgh,PA", "Portland,ME", "Portland,OR", "Poughkeepsie,NY", "Providence,RI", "Provo,UT", "Raleigh,NC", "Richmond,VA", "Riverside,CA", "Rochester,NY", "Sacramento,CA", "San Antonio,TX", "San Diego,CA", "San Francisco,CA", "San Jose,CA", "Scranton,PA", "Seattle,WA", "Springfield,MA", "St. Louis,MO", "Stockton,CA", "Syracuse,NY", "Toledo,OH", "Tucson,AZ", "Tulsa,OK", "Virginia Beach,VA", "Washington,DC", "Wichita,KS", "Worcester,MA", "Youngstown,OH" }));

        extractButton.setText("Extract Longterm Regional Weather Data");
        extractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractButtonActionPerformed(evt);
            }
        });

        weatherProbabilityTable.setFillsViewportHeight(true);
        weatherProbabilityTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(weatherProbabilityTable);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        adjustmentFactorTable.setFillsViewportHeight(true);
        adjustmentFactorTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(adjustmentFactorTable);

        jLabel2.setText("Please enter probabilities, durations, and adjustment factors for weather events, or fill by specifying the nearest metropolitan area:");

        useSeedValuesButton.setText("Use Values Stored In Seed");
        useSeedValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSeedValuesButtonActionPerformed(evt);
            }
        });

        exportWeatherDataButton.setText("Export");
        exportWeatherDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportWeatherDataButtonActionPerformed(evt);
            }
        });

        importFromFileButton.setText("Import");
        importFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFromFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(metroAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(extractButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(useSeedValuesButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportWeatherDataButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importFromFileButton))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(metroAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(extractButton)
                    .addComponent(useSeedValuesButton)
                    .addComponent(exportWeatherDataButton)
                    .addComponent(importFromFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void extractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractButtonActionPerformed

        // Load values from database here...
        if (metroAreaComboBox.getSelectedIndex() > 0) {
            weatherData.extractFromWeatherDB((String) metroAreaComboBox.getSelectedItem(), seed.getStartTime(), seed.getEndTime());
        } else {
            weatherData.useFacilitySpecific();
        }
        upperDataModel.fireTableDataChanged();
        lowerDataModel.fireTableDataChanged();

    }//GEN-LAST:event_extractButtonActionPerformed

    private void useSeedValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSeedValuesButtonActionPerformed
        weatherData.useSeedValues(this.seed);
        updateMetroArea(weatherData.getNearestMetroArea());
        upperDataModel.fireTableDataChanged();
        lowerDataModel.fireTableDataChanged();
    }//GEN-LAST:event_useSeedValuesButtonActionPerformed

    private void exportWeatherDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportWeatherDataButtonActionPerformed
        exportToCSVFile();
    }//GEN-LAST:event_exportWeatherDataButtonActionPerformed

    private void importFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFromFileButtonActionPerformed
        importFromCSVFile();
        updateMetroArea(weatherData.getNearestMetroArea());
        weatherData.updateNormalWeather();
        upperDataModel.fireTableDataChanged();
        lowerDataModel.fireTableDataChanged();
    }//GEN-LAST:event_importFromFileButtonActionPerformed

    /**
     * Setter for table font. Does not override renderer font, but sets table
     * row heights correctly.
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        adjustmentFactorTable.setFont(newTableFont);
        adjustmentFactorTable.setRowHeight(newTableFont.getSize() + 4);
        weatherProbabilityTable.setFont(newTableFont);
        weatherProbabilityTable.setRowHeight(newTableFont.getSize() + 4);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable adjustmentFactorTable;
    private javax.swing.JButton exportWeatherDataButton;
    private javax.swing.JButton extractButton;
    private javax.swing.JButton importFromFileButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox metroAreaComboBox;
    private javax.swing.JButton useSeedValuesButton;
    private javax.swing.JTable weatherProbabilityTable;
    // End of variables declaration//GEN-END:variables
}
