package GUI.RLHelper;

import GUI.RLHelper.Renderer.TableColorRenderer;
import GUI.RLHelper.TableModels.DemandDataModel;
import coreEngine.Helper.CEDate;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.DemandData;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author lake and Tristan
 */
public class DemandJPanel extends javax.swing.JPanel {

    private DemandData demandData;

    private DemandDataModel dataModel;

    private TableColorRenderer tableRenderer;

    /**
     * String containing names of all months.
     */
    public final static String[] monthString = new String[]{
        "January", "February", "March", "April", "May", "June", "July", "August", "September",
        "October", "November", "December"
    };

    /**
     * Creates new form DemandJPanel.
     */
    public DemandJPanel() {
        initComponents();
        disableEverything();
    }

    /**
     * Activator method that creates a new DemandData object with the given RRP
     * dates.
     *
     * @param year
     * @param startMonth Note, 1 will be added to value so indexing will start
     * at 1
     * @param startDay Note, 1 will be added to value so indexing will start at
     * 1
     * @param endMonth Note, 1 will be added to value so indexing will start at
     * 1
     * @param endDay Note, 1 will be added to value so indexing will start at 1
     */
    public void activateDemandJPanel(int year, int startMonth, int startDay, int endMonth, int endDay) {

        enableEverything();

        // Create new DemandData
        demandData = new DemandData(year, startMonth + 1, startDay + 1, endMonth + 1, endDay + 1, DemandData.TYPE_GP); //Adding one to fix indexing
        activateDemandJPanel();

        // Setting defaults for demand values
        facilitySpecificButton.doClick();

    }

    /**
     * Activator method that creates a new DemandData object from the seed file.
     *
     * @param seed
     */
    public void activateDemandJPanel(Seed seed) {

        enableEverything();

        // Set up DemandData from seed
        demandData = new DemandData(seed, DemandData.TYPE_GP);

        // Set up day exclusion from seed
        setupDayExclusion(seed);

        activateDemandJPanel();
    }

    /**
     * General activator method that should be called once demandData has been
     * set. Sets up the rest of the panel using values from demandData.
     */
    public void activateDemandJPanel() {
        if (demandData != null) {
            setupDayUsed();  //Checking the appropriate boxes for days used

            dataModel = new DemandDataModel(demandData);
            tableRenderer = new TableColorRenderer();

            demandTable.setModel(dataModel);
            demandTable.setDefaultRenderer(Object.class, tableRenderer);
            demandTable.setDefaultEditor(Object.class, new TableSelectionCellEditor(true));

            // This will update the cell background color when the user changes a value
            // in the table.
            dataModel.addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {
                    float minVal = demandData.getMinValue();
                    float maxVal = demandData.getMaxValue();
                    tableRenderer.setColorRange(minVal, maxVal);
                    demandTable.repaint();
                }
            });

            // Make the table look a little nicer
            Font tableFont = demandTable.getTableHeader().getFont();
            Font newHeaderFont = new Font(tableFont.getFamily(), Font.BOLD, tableFont.getSize());
            demandTable.getTableHeader().setFont(newHeaderFont);
            dataModel.fireTableDataChanged();

            demandTable.getColumnModel().getColumn(0).setMinWidth(125);
            demandTable.getColumnModel().getColumn(0).setMaxWidth(125);

        } else {
            System.err.println("DemandData not set, panel not activated.");
        }
    }

    /**
     * Updates the panel to reflect changes in RRP dates. Maintains demand data
     * current displayed in panel.
     *
     * @param year
     * @param startMonth 1 will be added to fix indexing
     * @param startDay 1 will be added to fix indexing
     * @param endMonth 1 will be added to fix indexing
     * @param endDay 1 will be added to fix indexing
     */
    public void updateDemandJPanel(int year, int startMonth, int startDay, int endMonth, int endDay) {

        enableEverything();

        // Set up data and model
        //demandData = new DemandData(year, startMonth+1, startDay+1, endMonth+1, endDay+1); //Adding one to fix indexing
        demandData.setStartMonth(startMonth + 1);
        demandData.setStartDay(startDay + 1);
        demandData.setEndMonth(endMonth + 1);
        demandData.setEndDay(endDay + 1);
        demandData.setYear(year);
        updateDayExclusionComboBoxes(year);
        clearDayExclusionList();

        // Updating list of active months
        for (int i = 0; i < 12; ++i) {
            if (i < startMonth || i > endMonth) {
                demandData.setMonthActive(i, false);
            } else {
                demandData.setMonthActive(i, true);
            }
        }

        activateDemandJPanel();

    }

    /**
     * Sets check boxes for active days of the demand data.
     */
    private void setupDayUsed() {
        if (demandData != null) {
            mondayCheck.setSelected(demandData.getDayActive(0));
            tuesdayCheck.setSelected(demandData.getDayActive(1));
            wednesdayCheck.setSelected(demandData.getDayActive(2));
            thursdayCheck.setSelected(demandData.getDayActive(3));
            fridayCheck.setSelected(demandData.getDayActive(4));
            saturdayCheck.setSelected(demandData.getDayActive(5));
            sundayCheck.setSelected(demandData.getDayActive(6));
        } else {
            System.err.println("demandData is null, check boxes not set.");
        }
    }

    /**
     * Method to read and set list of excluded days from the seed.
     *
     * @param seed
     */
    private void setupDayExclusion(Seed seed) {

        // Setting up model for combox boxes.
        dayPicker.setModel(ScenarioGeneratorDialog.modelCreator(monthPicker.getSelectedIndex() + 1, seed.getSeedFileDate().year));

        // Reading excluded days from seed file.
        ArrayList<CEDate> seedDayExcluded = seed.getDayExcluded();

        DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();

        CEDate tempCEDate;

        for (int exDay = 0; exDay < seedDayExcluded.size(); exDay++) {

            tempCEDate = seedDayExcluded.get(exDay);
            String text = CEDate.getMonthString(tempCEDate.month) // Month String
                    + " " //
                    + tempCEDate.day;                                           // Date string

            if (!model.contains(text)) {
                model.addElement(text);
            }
        }

    }

    /**
     * Updates the model of for the dayPicker combo box to show correct days.
     * Called when the RRP date changes.
     *
     * @param year RRP year
     */
    private void updateDayExclusionComboBoxes(int year) {
        dayPicker.setModel(ScenarioGeneratorDialog.modelCreator(monthPicker.getSelectedIndex() + 1, year));
    }

    /**
     * Method to clear the existing list of excluded days. To be used when the
     * panel is updated to reflect a change in RRP dates.
     */
    private void clearDayExclusionList() {
        ((DefaultListModel) excludeDaysList.getModel()).clear();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get method for panel DemandData object.
     *
     * @return demandData of the panel
     */
    public DemandData getDemandData() {
        return demandData;
    }

    /**
     * Method to get number of days skipped (excluded) in a specified month.
     *
     * @param month integer index to check the number of days excluded in the
     * month (indexing from 0)
     * @return int[] array of number of each day of the week excluded. (0 -
     * monday, etc.)
     */
    public int[] getDaysSkippedInMonth(int month) {
        int[] count = new int[7];
        if (month >= 0 && month < 12) {
            String monthStr = monthString[month];
            DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();

            for (int i = 0; i < model.getSize(); ++i) {
                //System.out.println(model.get(i).toString()); //String is in form "Month DayNum DayName"
                if (model.get(i).toString().contains(monthStr)) {
                    int excludedDayOfWeek = CEDate.dayOfWeek(Integer.parseInt((model.get(i).toString().split(" "))[1]), month + 1, demandData.getYear());
                    count[excludedDayOfWeek]++;
                }
            }
        }
        return count;
    }

    /**
     * Deprecated.
     *
     * @param month
     * @return
     */
    public int getDaysSkippedInMonthOrig(int month) {
        int count = 0;
        if (month >= 0 && month < 12) {
            String monthStr = monthString[month];
            DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();

            for (int i = 0; i < model.getSize(); ++i) {
                if (model.get(i).toString().contains(monthStr)) {
                    ++count;
                }
            }
        }
        return count;
    }

    /**
     * Method to return an ArrayList of CEDate's for every excluded day. Used
     * when the excluded days are written to the seed.
     *
     * @return ArrayList of CEDate's of excluded days.
     */
    public ArrayList<CEDate> getExcludedDaysArrayList() {
        String[] dateStr;
        ArrayList<CEDate> excludedDayArrList = new ArrayList<>();
        DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();
        for (int i = 0; i < model.getSize(); ++i) {
            dateStr = ((String) model.getElementAt(i)).split(" ");
            excludedDayArrList.add(new CEDate(CEDate.getMonthNumber(dateStr[0]), Integer.parseInt(dateStr[1])));
        }
        return excludedDayArrList;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Panel Disable and Enable">
    private void disableEverything() {
        addButton.setEnabled(false);
        dayPicker.setEnabled(false);
        demandTable.setEnabled(false);
        excludeDaysList.setEnabled(false);
        facilitySpecificButton.setEnabled(false);
        fridayCheck.setEnabled(false);
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jPanel1.setEnabled(false);
        jPanel2.setEnabled(false);
        jScrollPane1.setEnabled(false);
        jScrollPane2.setEnabled(false);
        mondayCheck.setEnabled(false);
        monthPicker.setEnabled(false);
        removeAllButton.setEnabled(false);
        removeButton.setEnabled(false);
        ruralDefaultsButton.setEnabled(false);
        saturdayCheck.setEnabled(false);
        selectAllButton.setEnabled(false);
        selectWeekdays.setEnabled(false);
        selectWeekends.setEnabled(false);
        sundayCheck.setEnabled(false);
        thursdayCheck.setEnabled(false);
        tuesdayCheck.setEnabled(false);
        urbanDefaultsButton.setEnabled(false);
        wednesdayCheck.setEnabled(false);
    }

    private void enableEverything() {
        addButton.setEnabled(true);
        dayPicker.setEnabled(true);
        demandTable.setEnabled(true);
        excludeDaysList.setEnabled(true);
        facilitySpecificButton.setEnabled(true);
        fridayCheck.setEnabled(true);
        jLabel1.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jPanel1.setEnabled(true);
        jPanel2.setEnabled(true);
        jScrollPane1.setEnabled(true);
        jScrollPane2.setEnabled(true);
        mondayCheck.setEnabled(true);
        monthPicker.setEnabled(true);
        removeAllButton.setEnabled(true);
        removeButton.setEnabled(true);
        ruralDefaultsButton.setEnabled(true);
        saturdayCheck.setEnabled(true);
        selectAllButton.setEnabled(true);
        selectWeekdays.setEnabled(true);
        selectWeekends.setEnabled(true);
        sundayCheck.setEnabled(true);
        thursdayCheck.setEnabled(true);
        tuesdayCheck.setEnabled(true);
        urbanDefaultsButton.setEnabled(true);
        wednesdayCheck.setEnabled(true);

        mondayCheck.setSelected(true);
        tuesdayCheck.setSelected(true);
        wednesdayCheck.setSelected(true);
        thursdayCheck.setSelected(true);
        fridayCheck.setSelected(true);
        //saturdayCheck.setSelected(true);
        //sundayCheck.setSelected(true);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        demandTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        urbanDefaultsButton = new javax.swing.JButton();
        ruralDefaultsButton = new javax.swing.JButton();
        seedValuesButton = new javax.swing.JButton();
        facilitySpecificButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        excludeDaysList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();
        monthPicker = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        dayPicker = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        mondayCheck = new javax.swing.JCheckBox();
        tuesdayCheck = new javax.swing.JCheckBox();
        wednesdayCheck = new javax.swing.JCheckBox();
        thursdayCheck = new javax.swing.JCheckBox();
        fridayCheck = new javax.swing.JCheckBox();
        saturdayCheck = new javax.swing.JCheckBox();
        sundayCheck = new javax.swing.JCheckBox();
        selectAllButton = new javax.swing.JButton();
        selectWeekdays = new javax.swing.JButton();
        selectWeekends = new javax.swing.JButton();

        setAutoscrolls(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Daily Demand Multipliers"));

        demandTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        demandTable.setFillsViewportHeight(true);
        demandTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(demandTable);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        urbanDefaultsButton.setText("Urban Default Values");
        urbanDefaultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                urbanDefaultsButtonActionPerformed(evt);
            }
        });
        jPanel4.add(urbanDefaultsButton);

        ruralDefaultsButton.setText("Rural Default Values");
        ruralDefaultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruralDefaultsButtonActionPerformed(evt);
            }
        });
        jPanel4.add(ruralDefaultsButton);

        seedValuesButton.setText("Saved Facility Specific");
        seedValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seedValuesButtonActionPerformed(evt);
            }
        });
        jPanel4.add(seedValuesButton);

        facilitySpecificButton.setText("User Input Values");
        facilitySpecificButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facilitySpecificButtonActionPerformed(evt);
            }
        });
        jPanel4.add(facilitySpecificButton);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Exclude Specific Calendar Dates From RRP"));

        excludeDaysList.setModel(new DefaultListModel());
        excludeDaysList.setFixedCellWidth(150);
        excludeDaysList.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        excludeDaysList.setVisibleRowCount(6);
        jScrollPane2.setViewportView(excludeDaysList);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Dates Excluded From RRP");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        removeAllButton.setText("Remove All");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        monthPicker.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        monthPicker.setMaximumSize(new java.awt.Dimension(122, 27));
        monthPicker.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                monthPickerItemStateChanged(evt);
            }
        });

        jLabel2.setText("Month:");

        dayPicker.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        dayPicker.setMaximumSize(new java.awt.Dimension(122, 27));
        dayPicker.setMinimumSize(new java.awt.Dimension(122, 27));
        dayPicker.setPreferredSize(new java.awt.Dimension(122, 27));

        jLabel3.setText("Day:");

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(monthPicker, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dayPicker, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(removeAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(dayPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeAllButton))
                    .addComponent(jScrollPane2)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Days in RRP"));
        jPanel3.setLayout(new java.awt.GridLayout(10, 1));

        mondayCheck.setSelected(true);
        mondayCheck.setText("Monday");
        mondayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mondayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(mondayCheck);

        tuesdayCheck.setSelected(true);
        tuesdayCheck.setText("Tuesday");
        tuesdayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tuesdayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(tuesdayCheck);

        wednesdayCheck.setSelected(true);
        wednesdayCheck.setText("Wednesday");
        wednesdayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wednesdayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(wednesdayCheck);

        thursdayCheck.setSelected(true);
        thursdayCheck.setText("Thursday");
        thursdayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thursdayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(thursdayCheck);

        fridayCheck.setSelected(true);
        fridayCheck.setText("Friday");
        fridayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fridayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(fridayCheck);

        saturdayCheck.setText("Saturday");
        saturdayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saturdayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(saturdayCheck);

        sundayCheck.setText("Sunday");
        sundayCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sundayCheckActionPerformed(evt);
            }
        });
        jPanel3.add(sundayCheck);

        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        jPanel3.add(selectAllButton);

        selectWeekdays.setText("Select Weekdays");
        selectWeekdays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectWeekdaysActionPerformed(evt);
            }
        });
        jPanel3.add(selectWeekdays);

        selectWeekends.setText("Select Weekends");
        selectWeekends.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectWeekendsActionPerformed(evt);
            }
        });
        jPanel3.add(selectWeekends);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents
	// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Actions">
    private void urbanDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urbanDefaultsButtonActionPerformed
        demandData.useUrbanDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_urbanDefaultsButtonActionPerformed

    private void ruralDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruralDefaultsButtonActionPerformed
        demandData.useRuralDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_ruralDefaultsButtonActionPerformed

    private void facilitySpecificButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facilitySpecificButtonActionPerformed
        demandData.useFacilitySpecificDefaults();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_facilitySpecificButtonActionPerformed

        private void mondayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mondayCheckActionPerformed
        dataModel.setWeekdayVisible(0, mondayCheck.isSelected());
        }//GEN-LAST:event_mondayCheckActionPerformed

        private void tuesdayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tuesdayCheckActionPerformed
        dataModel.setWeekdayVisible(1, tuesdayCheck.isSelected());
        }//GEN-LAST:event_tuesdayCheckActionPerformed

        private void wednesdayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wednesdayCheckActionPerformed
        dataModel.setWeekdayVisible(2, wednesdayCheck.isSelected());
        }//GEN-LAST:event_wednesdayCheckActionPerformed

        private void thursdayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thursdayCheckActionPerformed
        dataModel.setWeekdayVisible(3, thursdayCheck.isSelected());
        }//GEN-LAST:event_thursdayCheckActionPerformed

        private void fridayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fridayCheckActionPerformed
        dataModel.setWeekdayVisible(4, fridayCheck.isSelected());
        }//GEN-LAST:event_fridayCheckActionPerformed

        private void saturdayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saturdayCheckActionPerformed
        dataModel.setWeekdayVisible(5, saturdayCheck.isSelected());
        }//GEN-LAST:event_saturdayCheckActionPerformed

        private void sundayCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sundayCheckActionPerformed
        dataModel.setWeekdayVisible(6, sundayCheck.isSelected());
        }//GEN-LAST:event_sundayCheckActionPerformed

        private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        mondayCheck.setSelected(true);
        tuesdayCheck.setSelected(true);
        wednesdayCheck.setSelected(true);
        thursdayCheck.setSelected(true);
        fridayCheck.setSelected(true);
        saturdayCheck.setSelected(true);
        sundayCheck.setSelected(true);
        dataModel.showAllWeekdays();
        }//GEN-LAST:event_selectAllButtonActionPerformed

        private void selectWeekdaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectWeekdaysActionPerformed
        mondayCheck.setSelected(true);
        tuesdayCheck.setSelected(true);
        wednesdayCheck.setSelected(true);
        thursdayCheck.setSelected(true);
        fridayCheck.setSelected(true);
        dataModel.setWeekdayVisible(0, true);
        dataModel.setWeekdayVisible(1, true);
        dataModel.setWeekdayVisible(2, true);
        dataModel.setWeekdayVisible(3, true);
        dataModel.setWeekdayVisible(4, true);
        }//GEN-LAST:event_selectWeekdaysActionPerformed

        private void selectWeekendsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectWeekendsActionPerformed
        saturdayCheck.setSelected(true);
        sundayCheck.setSelected(true);
        dataModel.setWeekdayVisible(5, true);
        dataModel.setWeekdayVisible(6, true);
        }//GEN-LAST:event_selectWeekendsActionPerformed

        private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Check if excluded date is in the RRP
        CEDate excludedDate = new CEDate(demandData.getYear(), monthPicker.getSelectedIndex() + 1, dayPicker.getSelectedIndex() + 1);
        if (excludedDate.isBefore(demandData.getStartDate()) || excludedDate.isAfter(demandData.getEndDate())) {
            JOptionPane.showMessageDialog(this, "Date falls outside of Reliabiity Reporting Period and cannot be excluded.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();
            String text = monthPicker.getSelectedItem().toString()
                    + " "
                    + dayPicker.getSelectedItem().toString().split(" ")[0]
                    + " (" + dayPicker.getSelectedItem().toString().split(" ")[2] + ")";

            if (!model.contains(text)) {
                model.addElement(text);
            }
        }
        }//GEN-LAST:event_addButtonActionPerformed

        private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

        DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();
        List<String> selectedStrings = excludeDaysList.getSelectedValuesList();
        for (int i = 0; i < selectedStrings.size(); ++i) {
            model.removeElement(selectedStrings.get(i));
        }

        }//GEN-LAST:event_removeButtonActionPerformed

        private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed

        DefaultListModel model = (DefaultListModel) excludeDaysList.getModel();
        model.clear();

        }//GEN-LAST:event_removeAllButtonActionPerformed

    private void seedValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seedValuesButtonActionPerformed
        demandData.useSeedValues();

        float minVal = demandData.getMinValue();
        float maxVal = demandData.getMaxValue();
        tableRenderer.setColorRange(minVal, maxVal);

        dataModel.fireTableDataChanged();
    }//GEN-LAST:event_seedValuesButtonActionPerformed

    private void monthPickerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_monthPickerItemStateChanged
        int year = demandData.getYear();
        int currIdx = dayPicker.getSelectedIndex();
        dayPicker.setModel(ScenarioGeneratorDialog.modelCreator(monthPicker.getSelectedIndex() + 1, year));
        //Setting the date index
        if (currIdx >= dayPicker.getModel().getSize()) {
            currIdx = dayPicker.getModel().getSize() - 1;
        }
        dayPicker.setSelectedIndex(currIdx);
    }//GEN-LAST:event_monthPickerItemStateChanged
    // </editor-fold>

    /**
     * Setter for table font. Does not override renderer font, but sets table
     * row heights correctly.
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        demandTable.setFont(newTableFont);
        demandTable.setRowHeight(newTableFont.getSize() + 2);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox dayPicker;
    private javax.swing.JTable demandTable;
    private javax.swing.JList excludeDaysList;
    private javax.swing.JButton facilitySpecificButton;
    private javax.swing.JCheckBox fridayCheck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox mondayCheck;
    private javax.swing.JComboBox monthPicker;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton ruralDefaultsButton;
    private javax.swing.JCheckBox saturdayCheck;
    private javax.swing.JButton seedValuesButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton selectWeekdays;
    private javax.swing.JButton selectWeekends;
    private javax.swing.JCheckBox sundayCheck;
    private javax.swing.JCheckBox thursdayCheck;
    private javax.swing.JCheckBox tuesdayCheck;
    private javax.swing.JButton urbanDefaultsButton;
    private javax.swing.JCheckBox wednesdayCheck;
    // End of variables declaration//GEN-END:variables
}
