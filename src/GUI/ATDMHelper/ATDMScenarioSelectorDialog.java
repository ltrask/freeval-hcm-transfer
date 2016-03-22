package GUI.ATDMHelper;

import GUI.ATDMHelper.IO.ATDMIOHelper;
import GUI.ATDMHelper.IO.ScenarioFilterDialog;
import GUI.ATDMHelper.TableModels.ATDMCompareModel;
import GUI.ATDMHelper.TableModels.ATDMModel;
import GUI.ATDMHelper.TableModels.ATDMPlanEditorModel;
import GUI.ATDMHelper.Tutorial.ATDMTutorial;
import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.atdm.DataStruct.ATDMDatabase;
import coreEngine.atdm.DataStruct.ATDMPlan;
import coreEngine.atdm.DataStruct.ATDMStrategy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author jlaketrask
 */
public class ATDMScenarioSelectorDialog extends javax.swing.JDialog {

    private boolean atdmCompletedBool = false;

    private JPopupMenu[] colFilterMenus;

    private JPopupMenu badAPsPopupMenu;

    private JPopupMenu groupSelectionPopupMenu;

    private JPopupMenu strategyOptionsPopupMenu;

    private final JPopupMenu tooltip = new JPopupMenu();

    private final ATDMModel atdmModel;

    private final ATDMCompareModel atdmCompareModel;

    private final Seed seed;

    private final ATDMPlanEditorModel atdmPlanSelectorModel;

    /**
     * Creates new form ATDMScenarioSelectorDialog
     *
     * @param activeSeed
     * @param parent
     * @param modal
     */
    public ATDMScenarioSelectorDialog(Seed activeSeed, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initializeDialog();

        this.seed = activeSeed;
        if (seed.getATDMDatabase() == null) {
            ATDMDatabase newDB = new ATDMDatabase();
            newDB.initDefaultDatabase();
            seed.setATDMDatabase(newDB);
        }

        //Creating renderer to center columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        atdmModel = new ATDMModel(seed, this);
        atdmTable.setModel(atdmModel);

        // Adding row sorter
        atdmTable.setRowSorter(new TableRowSorter(atdmModel));

        // Making tables look nicer
        atdmTable.setDefaultRenderer(Object.class, centerRenderer);
        atdmTable.setDefaultRenderer(Integer.class, centerRenderer);
        atdmTable.setDefaultRenderer(Float.class, centerRenderer);
        ScenarioSummaryRenderer atdmSSRend = new ScenarioSummaryRenderer(ScenarioSummaryRenderer.TYPE_ATDM);
        atdmSSRend.setHorizontalAlignment(JLabel.CENTER);
        atdmSSRend.setColorRange(atdmModel.getMinTTI(), atdmModel.getPTI());
        atdmTable.getColumnModel().getColumn(ATDMModel.COL_MAX_TTI).setCellRenderer(atdmSSRend);
        atdmTable.getColumnModel().getColumn(ATDMModel.COL_MAX_VHD).setCellRenderer(atdmSSRend);

        // Creating the ATDMPlanEditorModel and assigning it to atdmPlanEditorTable
        this.atdmPlanSelectorModel = new ATDMPlanEditorModel(0, seed, atdmModel, planSelectorTable);
        planSelectorTable.setModel(atdmPlanSelectorModel);

        atdmCompareModel = new ATDMCompareModel(seed, propertiesTable);
        propertiesTable.setModel(atdmCompareModel);
        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (atdmTable.getSelectedRowCount() == 1) {
                    int row = atdmTable.getSelectedRow();
                    int sortedRow = atdmTable.getRowSorter().convertRowIndexToModel(row);
                    int planRow = atdmPlanSelectorModel.update(sortedRow);
                    atdmCompareModel.updateData((int) atdmModel.getValueAt(sortedRow, ATDMModel.COL_SCEN_IDX));
                    atdmTable.setRowSelectionInterval(row, row);
                    if (planRow >= 0) {
                        planSelectorTable.setRowSelectionInterval(planRow, planRow);
                    }
                } else {
                    planSelectorTable.clearSelection();
                    atdmPlanSelectorModel.update(-1);
                }
            }
        };

        atdmTable.getSelectionModel().addListSelectionListener(selectionListener);

        ListSelectionListener planSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                DefaultListModel model = (DefaultListModel) strategiesList.getModel();
                model.removeAllElements();
                if (planSelectorTable.getSelectedRowCount() == 1) {
                    int row = planSelectorTable.getSelectedRow();
                    //DefaultListModel model = (DefaultListModel) strategiesList.getModel();
                    ATDMPlan tempATDMPlan = seed.getATDMDatabase().getPlan((String) planSelectorTable.getValueAt(row, 2));
                    if (tempATDMPlan != null) {
                        for (ATDMStrategy strategy : tempATDMPlan.getAppliedStrategies().keySet()) {
                            switch (tempATDMPlan.getAppliedStrategies().get(strategy)) {
                                case CEConst.IDS_ATDM_STRAT_TYPE_DEMAND:
                                    model.addElement("DM: " + strategy.getDescription());
                                    break;
                                case CEConst.IDS_ATDM_STRAT_TYPE_WEATHER:
                                    model.addElement("WM: " + strategy.getDescription());
                                    break;
                                case CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT:
                                    model.addElement("IM: " + strategy.getDescription());
                                    break;
                                case CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE:
                                    model.addElement("WZM: " + strategy.getDescription());
                                    break;
                            }
                        }
                        if (tempATDMPlan.hasRampMetering()) {
                            model.addElement("User Specified Ramp Metering");
                        }

                        if (tempATDMPlan.hasShoulderOpening()) {
                            model.addElement("Shoulder Opening");
                        }

                    }
                }
            }
        };

        planSelectorTable.getSelectionModel().addListSelectionListener(planSelectionListener);

        setupFilterPopupMenus();
        atdmModel.fireTableStructureChanged();
        setupTableFonts();
        initTableSizing();

        updateSelectedProbability();

    }

    private void initializeDialog() {

        this.setTitle("ATDM Scenario Selection");
    }

    /**
     *
     * @return
     */
    public boolean atdmCompleted() {
        return atdmCompletedBool;
    }

    private void showAllScenarios() {
        atdmModel.showAllScenarios();
    }

    private void hideUnselected() {
        atdmModel.hideUnselected();
    }

    private void hideSelected() {

        if (atdmModel != null) {
            atdmModel.hideSelected();
        }
    }

    private void initATDMStrategyDialog(int startPane) {
        ATDMStrategiesDialog atdmStratDlg = new ATDMStrategiesDialog(startPane, seed, this, true);
        atdmStratDlg.setLocationRelativeTo(this.getRootPane());
        atdmStratDlg.setVisible(true);

        if (atdmStratDlg.stratOptionsSet()) {
            // do something
        } else {
            System.err.println("Strategy updates failed");
        }
        atdmStratDlg.dispose();
        atdmTable.clearSelection();
    }

    private boolean setupStrategyPopupMenu() {
        ATDMPlan currPlan = atdmModel.getPlan((int) atdmModel.getValueAt(atdmTable.getSelectedRow(), ATDMModel.COL_SCEN_IDX));

        if (currPlan != null) {
            ActionListener menuItemCheckedListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {

                    int selectedRow = atdmTable.getSelectedRow();
                    int currScenario = (int) atdmModel.getValueAt(selectedRow, ATDMModel.COL_SCEN_IDX);

                    if (!atdmModel.isCustom(currScenario)) {
                        atdmModel.setPlan(currScenario, new ATDMPlan(seed.getATDMDatabase().getNumberOfATDMPlans() + 1, "Custom", atdmModel.getPlan(currScenario)));
                        atdmModel.setCustom(currScenario, true);
                    }

                    ATDMPlan currPlan = atdmModel.getPlan(currScenario);

                    StayOpenCheckBoxMenuItem item = (StayOpenCheckBoxMenuItem) event.getSource();
                    //System.out.println(event.getSource());
                    JMenu tempMenu = (JMenu) ((JPopupMenu) item.getParent()).getInvoker();
                    String stratType = tempMenu.getText();

                    // moving through items to find parent menu
                    for (int i = 0; i < tempMenu.getItemCount(); i++) {
                        if (item.getText().equalsIgnoreCase(tempMenu.getItem(i).getText())) {
                            if (stratType.equalsIgnoreCase("Demand Management")) {
                                if (item.isSelected()) {
                                    currPlan.addStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND,
                                            seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, i));
                                } else {
                                    currPlan.removeStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, i));
                                }
                            } else if (stratType.equalsIgnoreCase("Weather Management")) {
                                if (item.isSelected()) {
                                    currPlan.addStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER,
                                            seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, i));
                                } else {
                                    currPlan.removeStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, i));
                                }
                            } else if (stratType.equalsIgnoreCase("Incident Management")) {
                                if (item.isSelected()) {
                                    currPlan.addStrategy(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT,
                                            seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, i));
                                } else {
                                    currPlan.removeStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, i));
                                }
                            } else if (stratType.equalsIgnoreCase("Work Zone Management")) {
                                if (item.isSelected()) {
                                    currPlan.addStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE,
                                            seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, i));
                                } else {
                                    currPlan.removeStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, i));
                                }
                            }
                            break;
                        }
                    }
                    atdmModel.fireTableDataChanged();
                    atdmTable.setRowSelectionInterval(selectedRow, selectedRow);
                }
            };

            strategyOptionsPopupMenu = new StayOpenOnActionPopupMenu();
            JMenu[] popupSubMenus = new JMenu[4];
            popupSubMenus[0] = new JMenu("Demand Management");
            for (int i = 0; i < seed.getATDMDatabase().getNumberOfStrategies(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND); i++) {
                JCheckBoxMenuItem currCBMenuItem = new StayOpenCheckBoxMenuItem(
                        seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, i).getDescription(),
                        currPlan.hasStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND, i)));
                currCBMenuItem.addActionListener(menuItemCheckedListener);
                popupSubMenus[0].add(currCBMenuItem);
            }
            popupSubMenus[1] = new JMenu("Weather Management");
            for (int i = 0; i < seed.getATDMDatabase().getNumberOfStrategies(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER); i++) {
                JCheckBoxMenuItem currCBMenuItem = new StayOpenCheckBoxMenuItem(
                        seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, i).getDescription(),
                        currPlan.hasStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WEATHER, i)));
                currCBMenuItem.addActionListener(menuItemCheckedListener);
                popupSubMenus[1].add(currCBMenuItem);
            }
            popupSubMenus[2] = new JMenu("Incident Management");
            for (int i = 0; i < seed.getATDMDatabase().getNumberOfStrategies(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT); i++) {
                JCheckBoxMenuItem currCBMenuItem = new StayOpenCheckBoxMenuItem(
                        seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, i).getDescription(),
                        currPlan.hasStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_INCIDENT, i)));
                currCBMenuItem.addActionListener(menuItemCheckedListener);
                popupSubMenus[2].add(currCBMenuItem);
            }
            popupSubMenus[3] = new JMenu("Work Zone Management");
            for (int i = 0; i < seed.getATDMDatabase().getNumberOfStrategies(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE); i++) {
                JCheckBoxMenuItem currCBMenuItem = new StayOpenCheckBoxMenuItem(
                        seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, i).getDescription(),
                        currPlan.hasStrategy(seed.getATDMDatabase().getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_WORKZONE, i)));
                currCBMenuItem.addActionListener(menuItemCheckedListener);
                popupSubMenus[3].add(currCBMenuItem);
            }

            strategyOptionsPopupMenu.add(popupSubMenus[0]);
            strategyOptionsPopupMenu.add(popupSubMenus[1]);
            strategyOptionsPopupMenu.add(popupSubMenus[2]);
            strategyOptionsPopupMenu.add(popupSubMenus[3]);
            return true;
        } else {
            //System.err.println("Error: Plan customization Popup menu setup failed.");
            return false;
        }
    }

    private void setupGroupSelectionPopupMenu() {

        ActionListener planMenuItemListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selectedRows = atdmTable.getSelectedRows();
                // converting indices from (sorted) view to underlying (unsorted) model
                int[] sortedSelectedRows = new int[selectedRows.length];
                for (int i = 0; i < sortedSelectedRows.length; i++) {
                    sortedSelectedRows[i] = atdmTable.getRowSorter().convertRowIndexToModel(selectedRows[i]);
                }
                //atdmTable.getRowSorter().convertRowIndexToModel(WIDTH);
                JMenuItem item = (JMenuItem) event.getSource();
                atdmModel.setGroupPlan(item.getText(), sortedSelectedRows);

                // Resetting row selection
                ListSelectionModel selector = atdmTable.getSelectionModel();
                for (int idx = 0; idx < selectedRows.length; idx++) {
                    selector.addSelectionInterval(selectedRows[idx], selectedRows[idx]);
                }
            }
        };

        ActionListener checkUncheckHighlightedOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selectedRows = atdmTable.getSelectedRows();
                // converting indices from (sorted) view to underlying (unsorted) model
                int[] sortedSelectedRows = new int[selectedRows.length];
                for (int i = 0; i < sortedSelectedRows.length; i++) {
                    sortedSelectedRows[i] = atdmTable.getRowSorter().convertRowIndexToModel(selectedRows[i]);
                }
                AbstractButton aButton = (AbstractButton) event.getSource();
                if (aButton.getText().equalsIgnoreCase("Check All Highlighted Scenarios")) {
                    atdmModel.toggleHighlightedScenarioSelection(true, sortedSelectedRows);
                    postUpdate();
                } else {
                    atdmModel.toggleHighlightedScenarioSelection(false, sortedSelectedRows);
                    postUpdate();
                }

                // Resetting row selection
                ListSelectionModel selector = atdmTable.getSelectionModel();
                for (int idx = 0; idx < selectedRows.length; idx++) {
                    selector.addSelectionInterval(selectedRows[idx], selectedRows[idx]);
                }

            }
        };

        groupSelectionPopupMenu = new StayOpenOnActionPopupMenu();
        groupSelectionPopupMenu.add(new JMenuItem("Check All Highlighted Scenarios"));
        ((JMenuItem) groupSelectionPopupMenu.getComponent(0)).addActionListener(checkUncheckHighlightedOptionListener);
        groupSelectionPopupMenu.add(new JMenuItem("Uncheck All Highlighted Scenarios"));
        ((JMenuItem) groupSelectionPopupMenu.getComponent(1)).addActionListener(checkUncheckHighlightedOptionListener);

        if (seed.getATDMDatabase().getNumberOfATDMPlans() > 0) {
            JMenu assignGroupPlan = new JMenu("Assign Plan To Group");
            assignGroupPlan.add(new JMenuItem("No Plan"));
            assignGroupPlan.getItem(0).addActionListener(planMenuItemListener);
            for (int planIdx = 1; planIdx <= seed.getATDMDatabase().getNumberOfATDMPlans(); planIdx++) {
                assignGroupPlan.add(new JMenuItem(seed.getATDMDatabase().getPlan(planIdx - 1).getName()));
                ((JMenuItem) assignGroupPlan.getItem(planIdx)).addActionListener(planMenuItemListener);
            }
            groupSelectionPopupMenu.add(assignGroupPlan);
        }
    }

    private void setupFilterPopupMenus() {
        colFilterMenus = new JPopupMenu[7];

        ActionListener menuItemCheckedListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                filterOptionsChanged();
            }
        };

        ActionListener selectAllOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //AbstractButton aButton = (AbstractButton) event.getSource();
                //int col = scenSelectorTable.getSelectedColumn()-2;  //puchanged
                int col = atdmTable.getSelectedColumn() - 2;  //puchanged
                for (int i = 0; i < colFilterMenus[col].getComponentCount() - 4; i++) {
                    ((JCheckBoxMenuItem) colFilterMenus[col].getComponent(i + 4)).setSelected(true);
                }
                filterOptionsChanged();
            }
        };

        ActionListener deselectAllOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //AbstractButton aButton = (AbstractButton) event.getSource();
                //int col = scenSelectorTable.getSelectedColumn()-2;   //puchanged
                int col = atdmTable.getSelectedColumn() - 2;  //puchanged
                for (int i = 0; i < colFilterMenus[col].getComponentCount() - 4; i++) {
                    ((JCheckBoxMenuItem) colFilterMenus[col].getComponent(i + 4)).setSelected(false);
                }
                filterOptionsChanged();
            }
        };

        ActionListener checkUncheckVisibleOptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                if (aButton.getText().equalsIgnoreCase("Check All Visible Scenarios")) {
                    atdmModel.checkAllVisible(true);
                    postUpdate();
                } else {
                    atdmModel.checkAllVisible(false);
                    postUpdate();
                }
            }
        };

        ActionListener defineRangeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //int col = scenSelectorTable.getSelectedColumn();
                int col = atdmTable.getSelectedColumn();
                if (col == ATDMModel.COL_MAX_VHD) {
                    fireFilterDialog(col - 4);
                } else {
                    fireFilterDialog(col - 3);
                }
            }
        };

        ActionListener setCongestedTIIValueListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String newStr = (String) JOptionPane.showInputDialog(
                        null, "Enter new cutoff value for congested AP TTI:\n"
                        + "(PTI is " + atdmModel.getPTI() + ")",
                        "Enter Congested TTI Cutoff Value",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null, atdmModel.getCongestedAPCutOffTTI());
                if (newStr != null && newStr.length() > 0) {
                    atdmModel.setCongestedAPCutOffTTI(Float.valueOf(newStr));
                    atdmModel.updateCongestedTTICutoff();
                }

            }
        };

        for (int i = 4; i < 7; i++) {
            colFilterMenus[i - 2] = new StayOpenOnActionPopupMenu();
            JMenuItem defineRangeMenuItem = new JMenuItem("Define Filter Range");
            defineRangeMenuItem.addActionListener(defineRangeListener);
            colFilterMenus[i - 2].add(defineRangeMenuItem);
            JMenuItem selectAllMenuItem = new JMenuItem("Select All Options");
            selectAllMenuItem.addActionListener(selectAllOptionListener);
            colFilterMenus[i - 2].add(selectAllMenuItem);  //puchanged
            JMenuItem deselectAllMenuItem = new JMenuItem("Deselect All Options");
            deselectAllMenuItem.addActionListener(deselectAllOptionListener);
            colFilterMenus[i - 2].add(deselectAllMenuItem);  //puchanged
            colFilterMenus[i - 2].add(new Separator());  //puchanged
            int numItems = atdmModel.getAllValueOptions(i);
            //System.out.println(numItems);
            for (int j = 0; j <= numItems; j++) {
                JCheckBoxMenuItem currCBMenuItem = new JCheckBoxMenuItem(String.valueOf(j), true);
                currCBMenuItem.addActionListener(menuItemCheckedListener);
                colFilterMenus[i - 2].add(currCBMenuItem);  //puchanged
            }
        }

        //TableColumn tc = atdmTable.getColumnModel().getColumn(4);
        //tc.setHeaderRenderer(new TableHeaderRendererWithPopup(atdmTable.getTableHeader(), colFilterMenus));
        JPopupMenu cbColPopupMenu = new JPopupMenu();
        cbColPopupMenu.add(new JMenuItem("Check All Visible Scenarios"));
        ((JMenuItem) cbColPopupMenu.getComponent(0)).addActionListener(checkUncheckVisibleOptionListener);
        cbColPopupMenu.add(new JMenuItem("Uncheck All Visible Scenarios"));
        ((JMenuItem) cbColPopupMenu.getComponent(1)).addActionListener(checkUncheckVisibleOptionListener);
        colFilterMenus[0] = cbColPopupMenu;

        // Addding define range for demand multiplier column
        JPopupMenu dmColPopupMenu = new JPopupMenu();
        dmColPopupMenu.add(new JMenuItem("Define Demand Multiplier Range"));
        ((JMenuItem) dmColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);
        colFilterMenus[1] = dmColPopupMenu;

        // Addding define range for demand multiplier column
        JPopupMenu maxTTIColPopupMenu = new JPopupMenu();
        maxTTIColPopupMenu.add(new JMenuItem("Define Max TTI Range"));
        ((JMenuItem) maxTTIColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);
        colFilterMenus[5] = maxTTIColPopupMenu;

        // Addding define range for demand multiplier column
        JPopupMenu maxDelayColPopupMenu = new JPopupMenu();
        maxDelayColPopupMenu.add(new JMenuItem("Define Max Delay Range"));
        ((JMenuItem) maxDelayColPopupMenu.getComponent(0)).addActionListener(defineRangeListener);
        colFilterMenus[6] = maxDelayColPopupMenu;

        badAPsPopupMenu = new JPopupMenu();
        badAPsPopupMenu.add(new JMenuItem("Set Congested TTI Cutoff Value"));
        ((JMenuItem) badAPsPopupMenu.getComponent(0)).addActionListener(setCongestedTIIValueListener);
    }

    private void filterOptionsChanged() {
        int col = atdmTable.getSelectedColumn();
        for (int colIdx = 0; colIdx < 3; colIdx++) {      //puchanged (length-4)
            for (int filterIdx = 0; filterIdx < colFilterMenus[colIdx + 2].getComponentCount() - 4; filterIdx++) {
                boolean isSelected = ((JCheckBoxMenuItem) colFilterMenus[colIdx + 2].getComponent(filterIdx + 4)).isSelected();
                atdmModel.updateFilter(colIdx + 4, filterIdx, isSelected);
            }
        }
        atdmModel.applyFilters();
        atdmTable.setColumnSelectionInterval(col, col);
        postUpdate();
    }

    private void updateFilterPopups() {
        for (int colIdx = 0; colIdx < 3; colIdx++) {
            for (int filterIdx = 0; filterIdx < colFilterMenus[colIdx + 2].getComponentCount() - 4; filterIdx++) {
                ((JCheckBoxMenuItem) colFilterMenus[colIdx + 2].getComponent(filterIdx + 4)).setSelected(atdmModel.getFilterOption(colIdx + 4, filterIdx));
            }
        }
    }

    /**
     *
     * @return
     */
    public ATDMModel getATDMModel() {
        return atdmModel;
    }

    /**
     *
     * @return
     */
    public JTable getATDMTable() {
        return atdmTable;
    }

    private void fireFilterDialog(int checkOption) {
        if (atdmModel != null) {
            ScenarioFilterDialog atdmFilterDialog = new ScenarioFilterDialog(atdmModel, this, true);
            if (checkOption > -1) {
                atdmFilterDialog.enableFilterOption(checkOption);
            }

            atdmFilterDialog.setLocationRelativeTo(this.getRootPane());
            atdmFilterDialog.setVisible(true);

            if (atdmFilterDialog.filterOptionsSet()) {
                atdmModel.filterByDemandMultRange(atdmFilterDialog.getDemandFilter());
                atdmModel.filterByNumberIncidentsRange(atdmFilterDialog.getIncidentFilter());
                atdmModel.filterByNumberWeatherEventsRange(atdmFilterDialog.getWeatherFilter());
                atdmModel.filterByMaxTTIRange(atdmFilterDialog.getTTIFilter());
                atdmModel.filterByMaxDelayRange(atdmFilterDialog.getDelayFilter());
                atdmModel.applyFilters();
                atdmModel.fireTableDataChanged();
            }
            atdmFilterDialog.dispose();
            updateFilterPopups();
        }
    }

    /**
     *
     */
    public void updateSelectedProbability() {
        statusBarLabel1.setText("Total probability displayed: "
                + String.format("%.2f%%", atdmModel.getProbabilityOfDisplayedScenarios() * 100)
                + ", selected: "
                + String.format("%.2f%%", atdmModel.getProbabilityOfSelectedScenarios() * 100));
    }

    /**
     *
     */
    public void postUpdate() {
        //setupTableColumns(atdmTable);
        //setupComboBoxColumn(planSelector);
        updateSelectedProbability();
    }

    private void setPlanEditorTableSizing() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        planSelectorTable.getColumnModel().getColumn(0).setMinWidth(25);
        planSelectorTable.getColumnModel().getColumn(0).setMaxWidth(25);
        planSelectorTable.getColumnModel().getColumn(1).setMinWidth(25);
        planSelectorTable.getColumnModel().getColumn(1).setMaxWidth(25);
        //planSelectorTable.getColumnModel().getColumn(2).setMinWidth(100);
        planSelectorTable.setDefaultRenderer(Integer.class, centerRenderer);
        planSelectorTable.setDefaultRenderer(Object.class, centerRenderer);
    }

    private void setPropertiesTableSizing() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        propertiesTable.getColumnModel().getColumn(0).setMinWidth(180);
        propertiesTable.getColumnModel().getColumn(0).setMaxWidth(180);
        propertiesTable.setDefaultRenderer(Integer.class, centerRenderer);
        propertiesTable.setDefaultRenderer(Object.class, centerRenderer);
    }

    private void initTableSizing() {
        setPlanEditorTableSizing();
        setPropertiesTableSizing();
    }

    private void setupTableFonts() {
        // Setting table fonts and row heights
        propertiesTable.setFont(MainWindow.getTableFont());
        planSelectorTable.setFont(MainWindow.getTableFont());
        atdmTable.setFont(MainWindow.getTableFont());
        propertiesTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        planSelectorTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
        atdmTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);
    }

    private void scenarioSelectorTablePopupTriggered(java.awt.event.MouseEvent evt) {
        JTable source = (JTable) evt.getSource();
        if (source.getSelectedRowCount() > 1) {
            setupGroupSelectionPopupMenu();
            groupSelectionPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        } else {
            int column = source.columnAtPoint(evt.getPoint());
            int row = source.rowAtPoint(evt.getPoint());
            if (!source.isColumnSelected(column)) {
                source.changeSelection(row, column, false, false);
            }
            switch (column) {
                case ATDMModel.COL_CHECK_BOX:
                case ATDMModel.COL_SCEN_IDX:
                case ATDMModel.COL_DEMAND_PATTERN:
                    colFilterMenus[column].show(evt.getComponent(), evt.getX(), evt.getY());
                    break;
                case ATDMModel.COL_ATDM_PLAN:
                    if (setupStrategyPopupMenu()) {
                        strategyOptionsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                    break;
                case ATDMModel.COL_DEMAND_MULTIPLIER:
                case ATDMModel.COL_NUM_WORK_ZONES:
                case ATDMModel.COL_NUM_WEATHER_EVENTS:
                case ATDMModel.COL_NUM_INCIDENTS:
                case ATDMModel.COL_MAX_TTI:
                    colFilterMenus[column - 2].show(evt.getComponent(), evt.getX(), evt.getY());
                    break;
                case ATDMModel.COL_MAX_VHD:
                    colFilterMenus[column - 3].show(evt.getComponent(), evt.getX(), evt.getY());
                    break;
                case ATDMModel.COL_NUM_BAD_APS:
                    badAPsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    break;
            }
            //if (column == 0) {
            //    colFilterMenus[column].show(evt.getComponent(), evt.getX(), evt.getY());
            //}
            //if (column > 2 && column < 9) {
            //    colFilterMenus[column - 2].show(evt.getComponent(), evt.getX(), evt.getY());  //puchanged
            //} else if (column == ATDMModel.ROW_ATDM_PLAN) {  // right click on plan name
            //    if (setupStrategyPopupMenu()) {
            //        strategyOptionsPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            //    }
            //}
        }
    }

    private void resetForNewATDMDatabase() {
        atdmPlanSelectorModel.update(-1);
        atdmModel.removeAllPlans();
        //atdmPlanSelectorModel.fireTableDataChanged();
        planSelectorTable.clearSelection();
        atdmTable.clearSelection();
    }

    private void openATDMPlanMenu() {
        if (seed.getATDMDatabase() != null) {
            ATDMPlansDialog atdmPlansDialog = new ATDMPlansDialog(this, true, seed, atdmModel);

            atdmPlansDialog.setLocationRelativeTo(this.getRootPane());
            atdmPlansDialog.setVisible(true);

            atdmPlansDialog.dispose();
        } else {
            System.out.println("Plans are null");
        }
        atdmPlanSelectorModel.update(-1);
        atdmTable.clearSelection();
        atdmPlanSelectorModel.fireTableDataChanged();
        planSelectorTable.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scenarioSelectorPanel = new javax.swing.JPanel();
        atdmScrollPane = new javax.swing.JScrollPane();
        atdmTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        planSelectorTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        strategiesList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        propertiesScrollPane = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        statusBarLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        atdmStrategiesMenu = new javax.swing.JMenu();
        stratDemandMangementMenuItem = new javax.swing.JMenuItem();
        stratWeatherManagementMenuItem = new javax.swing.JMenuItem();
        stratIncidentManagementMenuItem = new javax.swing.JMenuItem();
        stratWorkZoneManagementMenuItem = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        atdmPlanMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        scenariosMenu = new javax.swing.JMenu();
        scenShowAllMenuItem = new javax.swing.JMenuItem();
        scenShowSelectedMenuItem = new javax.swing.JMenuItem();
        scenShowUnselectedMenuItem = new javax.swing.JMenuItem();
        scenShowPerFilterMenuItem = new javax.swing.JMenuItem();
        importExportMenu = new javax.swing.JMenu();
        importDatabaseMenu = new javax.swing.JMenu();
        importBinaryMenuItem = new javax.swing.JMenuItem();
        exportDatabaseMenu = new javax.swing.JMenu();
        exportBinaryMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATDM Scenario Selection");

        scenarioSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Scenarios for ATDM Analysis"));

        atdmTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        atdmTable.getTableHeader().setReorderingAllowed(false);
        atdmTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                atdmTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                atdmTableMouseReleased(evt);
            }
        });
        atdmTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                atdmTableMouseMoved(evt);
            }
        });
        atdmScrollPane.setViewportView(atdmTable);

        planSelectorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "\u2713", "Plan Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Boolean.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        planSelectorTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(planSelectorTable);

        strategiesList.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(strategiesList);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ATDM Plan Previews for Highlighted Scenario", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        propertiesScrollPane.setViewportView(propertiesTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(propertiesScrollPane)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(propertiesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout scenarioSelectorPanelLayout = new javax.swing.GroupLayout(scenarioSelectorPanel);
        scenarioSelectorPanel.setLayout(scenarioSelectorPanelLayout);
        scenarioSelectorPanelLayout.setHorizontalGroup(
            scenarioSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scenarioSelectorPanelLayout.createSequentialGroup()
                .addComponent(atdmScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scenarioSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        scenarioSelectorPanelLayout.setVerticalGroup(
            scenarioSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scenarioSelectorPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(scenarioSelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scenarioSelectorPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addComponent(atdmScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        okButton.setText("Create ATDM Set For Selected Scenarios");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        statusBarLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        statusBarLabel1.setText("Total Probability displayed: ");

        atdmStrategiesMenu.setText("ATDM Strategies");

        stratDemandMangementMenuItem.setText("Demand Management");
        stratDemandMangementMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stratDemandMangementMenuItemActionPerformed(evt);
            }
        });
        atdmStrategiesMenu.add(stratDemandMangementMenuItem);

        stratWeatherManagementMenuItem.setText("Weather Traffic Management");
        stratWeatherManagementMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stratWeatherManagementMenuItemActionPerformed(evt);
            }
        });
        atdmStrategiesMenu.add(stratWeatherManagementMenuItem);

        stratIncidentManagementMenuItem.setText("Traffic Incident Management");
        stratIncidentManagementMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stratIncidentManagementMenuItemActionPerformed(evt);
            }
        });
        atdmStrategiesMenu.add(stratIncidentManagementMenuItem);

        stratWorkZoneManagementMenuItem.setText("Work Zone Traffic Maintenance");
        stratWorkZoneManagementMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stratWorkZoneManagementMenuItemActionPerformed(evt);
            }
        });
        atdmStrategiesMenu.add(stratWorkZoneManagementMenuItem);

        jMenuItem2.setText("Ramp Metering and Hard Shoulder Running");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        atdmStrategiesMenu.add(jMenuItem2);

        jMenuBar1.add(atdmStrategiesMenu);

        atdmPlanMenu.setText("ATDM Plan Designer");

        jMenuItem3.setText("Plan Designer");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        atdmPlanMenu.add(jMenuItem3);

        jMenuBar1.add(atdmPlanMenu);

        scenariosMenu.setText("Scenarios");

        scenShowAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        scenShowAllMenuItem.setText("Show All");
        scenShowAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenShowAllMenuItemActionPerformed(evt);
            }
        });
        scenariosMenu.add(scenShowAllMenuItem);

        scenShowSelectedMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        scenShowSelectedMenuItem.setText("Show Marked Only");
        scenShowSelectedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenShowSelectedMenuItemActionPerformed(evt);
            }
        });
        scenariosMenu.add(scenShowSelectedMenuItem);

        scenShowUnselectedMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        scenShowUnselectedMenuItem.setText("Show Unmarked Only");
        scenShowUnselectedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenShowUnselectedMenuItemActionPerformed(evt);
            }
        });
        scenariosMenu.add(scenShowUnselectedMenuItem);

        scenShowPerFilterMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        scenShowPerFilterMenuItem.setText("Show per Filter");
        scenShowPerFilterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenShowPerFilterMenuItemActionPerformed(evt);
            }
        });
        scenariosMenu.add(scenShowPerFilterMenuItem);

        jMenuBar1.add(scenariosMenu);

        importExportMenu.setText("Import/Export ATDM Database");

        importDatabaseMenu.setText("Import ATDM Database");

        importBinaryMenuItem.setText("Import from binary (.atdmdb)");
        importBinaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBinaryMenuItemActionPerformed(evt);
            }
        });
        importDatabaseMenu.add(importBinaryMenuItem);

        importExportMenu.add(importDatabaseMenu);

        exportDatabaseMenu.setText("Export ATDM Database");

        exportBinaryMenuItem.setText("Export to binary (.atdmdb)");
        exportBinaryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBinaryMenuItemActionPerformed(evt);
            }
        });
        exportDatabaseMenu.add(exportBinaryMenuItem);

        importExportMenu.add(exportDatabaseMenu);

        jMenuBar1.add(importExportMenu);

        jMenu1.setText("Help");

        jMenuItem1.setText("Open Tutorial");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scenarioSelectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusBarLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(scenarioSelectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(cancelButton))
                    .addComponent(statusBarLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void scenShowAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenShowAllMenuItemActionPerformed
        showAllScenarios();
        postUpdate();
    }//GEN-LAST:event_scenShowAllMenuItemActionPerformed

    private void scenShowSelectedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenShowSelectedMenuItemActionPerformed
        hideUnselected();
        postUpdate();
    }//GEN-LAST:event_scenShowSelectedMenuItemActionPerformed

    private void stratDemandMangementMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stratDemandMangementMenuItemActionPerformed
        initATDMStrategyDialog(0);
    }//GEN-LAST:event_stratDemandMangementMenuItemActionPerformed

    private void stratWeatherManagementMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stratWeatherManagementMenuItemActionPerformed
        initATDMStrategyDialog(1);
    }//GEN-LAST:event_stratWeatherManagementMenuItemActionPerformed

    private void stratIncidentManagementMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stratIncidentManagementMenuItemActionPerformed
        initATDMStrategyDialog(2);
    }//GEN-LAST:event_stratIncidentManagementMenuItemActionPerformed

    private void stratWorkZoneManagementMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stratWorkZoneManagementMenuItemActionPerformed
        initATDMStrategyDialog(3);
    }//GEN-LAST:event_stratWorkZoneManagementMenuItemActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (seed.getATDMDatabase().getNumberOfATDMPlans() > 0) {
            atdmCompletedBool = true;
            if (atdmModel.runCheck()) {
                atdmModel.applyPlansToScenarioInfos();
                this.setVisible(false);
            } else {
                // Do nothing as user will return to dialog to finish scenario
                // selection and plan assignment
            }
        } else {
            this.setVisible(false);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void atdmTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_atdmTableMousePressed
        if (evt.isPopupTrigger()) {
            scenarioSelectorTablePopupTriggered(evt);
        }
    }//GEN-LAST:event_atdmTableMousePressed

    private void atdmTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_atdmTableMouseReleased
        if (evt.isPopupTrigger()) {
            scenarioSelectorTablePopupTriggered(evt);
        }
    }//GEN-LAST:event_atdmTableMouseReleased

    private void atdmTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_atdmTableMouseMoved
        tooltip.removeAll();
        tooltip.setVisible(false);
        if (evt.isControlDown()) {
            try {
                int column = atdmTable.columnAtPoint(evt.getPoint());
                int row = atdmTable.rowAtPoint(evt.getPoint());

                JTextArea tip = new JTextArea(atdmModel.getToolTip(row, column));
                tip.setEditable(false);
                tooltip.add(tip);
                tooltip.show(atdmTable, evt.getX() + 15, evt.getY());
            } catch (NullPointerException e) {
                // Catches the chance if there is no plan assigned to scenario
            }
        }
    }//GEN-LAST:event_atdmTableMouseMoved

    private void scenShowUnselectedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenShowUnselectedMenuItemActionPerformed
        hideSelected();
    }//GEN-LAST:event_scenShowUnselectedMenuItemActionPerformed

    private void scenShowPerFilterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenShowPerFilterMenuItemActionPerformed
        fireFilterDialog(-1);
    }//GEN-LAST:event_scenShowPerFilterMenuItemActionPerformed

    private void exportBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBinaryMenuItemActionPerformed
        String result = ATDMIOHelper.exportATDMDatabaseBinary(seed.getATDMDatabase());
        if (result != null) {
            MainWindow.printLog(result);
        }
    }//GEN-LAST:event_exportBinaryMenuItemActionPerformed

    private void importBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBinaryMenuItemActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "<HTML> Importing a new ATDM Database"
                + " will overwrite any plans assigned to scenarios in the current"
                + " ATDM Set.<br> Further, if you wish to save the current ATDM Database"
                + " for future use, please cancel and export the database.",
                "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ATDMDatabase newATDMDatabase = ATDMIOHelper.importATDMDatabaseFromBinary();
            if (newATDMDatabase == null) {
                MainWindow.printLog("Failed to import database.");
            } else {
                seed.setATDMDatabase(newATDMDatabase);
                this.resetForNewATDMDatabase();
            }
        }
    }//GEN-LAST:event_importBinaryMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        ATDMTutorial tutorial = new ATDMTutorial(null, true);

        tutorial.setLocationRelativeTo(this.getRootPane());
        tutorial.setVisible(true);

        tutorial.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        initATDMStrategyDialog(4);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        openATDMPlanMenu();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu atdmPlanMenu;
    private javax.swing.JScrollPane atdmScrollPane;
    private javax.swing.JMenu atdmStrategiesMenu;
    private javax.swing.JTable atdmTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JMenuItem exportBinaryMenuItem;
    private javax.swing.JMenu exportDatabaseMenu;
    private javax.swing.JMenuItem importBinaryMenuItem;
    private javax.swing.JMenu importDatabaseMenu;
    private javax.swing.JMenu importExportMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okButton;
    private javax.swing.JTable planSelectorTable;
    private javax.swing.JScrollPane propertiesScrollPane;
    private javax.swing.JTable propertiesTable;
    private javax.swing.JMenuItem scenShowAllMenuItem;
    private javax.swing.JMenuItem scenShowPerFilterMenuItem;
    private javax.swing.JMenuItem scenShowSelectedMenuItem;
    private javax.swing.JMenuItem scenShowUnselectedMenuItem;
    private javax.swing.JPanel scenarioSelectorPanel;
    private javax.swing.JMenu scenariosMenu;
    private javax.swing.JLabel statusBarLabel1;
    private javax.swing.JMenuItem stratDemandMangementMenuItem;
    private javax.swing.JMenuItem stratIncidentManagementMenuItem;
    private javax.swing.JMenuItem stratWeatherManagementMenuItem;
    private javax.swing.JMenuItem stratWorkZoneManagementMenuItem;
    private javax.swing.JList strategiesList;
    // End of variables declaration//GEN-END:variables
}
