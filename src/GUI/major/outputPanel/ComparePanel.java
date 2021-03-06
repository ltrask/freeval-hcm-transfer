package GUI.major.outputPanel;

import GUI.major.MainWindow;
import GUI.major.tableHelper.FREEVAL_JTable;
import GUI.major.tableHelper.FREEVAL_TableModel;
import GUI.major.tableHelper.FREEVAL_TableWithSetting;
import GUI.major.tableHelper.SplitTableJPanel;
import GUI.seedEditAndIOHelper.ExcelAdapter;
import coreEngine.Helper.CEConst;
import coreEngine.Helper.FacilitySummary;
import coreEngine.Seed;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This the compare tab in main window to compare any scenarios from the
 * navigator tree.
 *
 * @author Shu Liu
 */
public class ComparePanel extends javax.swing.JPanel {

    /**
     * Creates new form ComparePanel
     */
    public ComparePanel() {
        initComponents();
        compareTable = new CompareTable();

        compareSplitTable = new SplitTableJPanel(compareTable.getFirstColumnTable(), compareTable.getRestColumnTable());
        compareSplitTable.setDividerLocation(270);
        jSplitPane1.setRightComponent(compareSplitTable);

        compareTable.getRestColumnTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scenarioJList.setModel(scenarioListModel);
    }

    /**
     * Add a scenario to compare
     *
     * @param seed seed instance
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param name description of the scenario
     */
    public void addScenarioToCompare(Seed seed, int scen, int atdm, String name) {
        scenarioIndices.add(new FacilitySummary(seed, scen, atdm));

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(now);

        scenarioListModel.addElement(name + " [added at " + time + "]");
        compareTable.update();
    }

    /**
     * Remove a scenario from compare list
     *
     * @param listIndex index of the scenario in the list
     */
    public void removeScenario(int listIndex) {
        scenarioIndices.remove(listIndex);
        scenarioListModel.removeElementAt(listIndex);
        compareTable.update();
    }

    public void removeAllScenarios() {
        scenarioIndices = new ArrayList<>();
        scenarioListModel.removeAllElements();
        compareTable.update();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scenarioJList = new javax.swing.JList();
        removeSelectedButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(210);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPane1.setBorder(null);

        scenarioJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Scenarios"));
        jScrollPane1.setViewportView(scenarioJList);

        removeSelectedButton.setText("Remove Selected");
        removeSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelectedButtonActionPerformed(evt);
            }
        });

        removeAllButton.setText("Remove All");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(removeSelectedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(removeSelectedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeAllButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSelectedButtonActionPerformed
        for (int index : scenarioJList.getSelectedIndices()) {
            removeScenario(scenarioJList.getSelectedIndex());
        }
    }//GEN-LAST:event_removeSelectedButtonActionPerformed

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        removeAllScenarios();
    }//GEN-LAST:event_removeAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeSelectedButton;
    private javax.swing.JList scenarioJList;
    // End of variables declaration//GEN-END:variables

    /**
     * Setter for mainWindow connection
     *
     * @param mainWindow main window instance
     */
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Setter for table font
     *
     * @param newTableFont new table font
     */
    public void setTableFont(Font newTableFont) {
        compareTable.getFirstColumnTable().setFont(newTableFont);
        compareTable.getFirstColumnTable().setRowHeight(newTableFont.getSize() + 2);
        compareTable.getRestColumnTable().setFont(newTableFont);
        compareTable.getRestColumnTable().setRowHeight(newTableFont.getSize() + 2);
    }

    /**
     * Copy table to clipboard in excel format
     *
     * @return whether copy is successful
     */
    public String copyTable() {
        try {
            return (ExcelAdapter.copySplitTable(compareTable.getFirstColumnTable(), compareTable.getRestColumnTable()));
        } catch (Exception e) {
            return "Error when copy contour table " + e.toString();
        }
    }

    private final CompareTable compareTable;

    private final SplitTableJPanel compareSplitTable;

    private final DefaultListModel scenarioListModel = new DefaultListModel();

    //data
    private ArrayList<FacilitySummary> scenarioIndices = new ArrayList<>();

    private MainWindow mainWindow;

    private class CompareTable implements FREEVAL_TableWithSetting {

        /**
         * Constructor
         */
        public CompareTable() {
            firstColumnModel = new CompareTableModel(true);
            restColumnModel = new CompareTableModel(false);

            firstColumnTable = new FREEVAL_JTable(firstColumnModel);
            restColumnTable = new FREEVAL_JTable(restColumnModel);

            restColumnTable.getTableHeader().setReorderingAllowed(true);
        }

        @Override
        public FREEVAL_JTable getFirstColumnTable() {
            return firstColumnTable;
        }

        @Override
        public FREEVAL_JTable getRestColumnTable() {
            return restColumnTable;
        }

        /**
         * Update
         */
        public void update() {
            firstColumnModel.fireTableStructureChanged();
            restColumnModel.fireTableStructureChanged();
        }

        //table display
        private final FREEVAL_JTable firstColumnTable;

        private final FREEVAL_TableModel firstColumnModel;

        private final FREEVAL_JTable restColumnTable;

        private final FREEVAL_TableModel restColumnModel;
    }

    private class CompareTableModel extends FREEVAL_TableModel {

        private final boolean isFirstColumn;

        private final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());

        /**
         * Constructor
         *
         * @param isFirstColumn whether the model is used for the first column
         */
        public CompareTableModel(boolean isFirstColumn) {
            this.isFirstColumn = isFirstColumn;
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            return defaultCellEditor;
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            return new TableNumAndStringRenderer();
        }

        @Override
        public int getRowCount() {
            return 12;
        }

        @Override
        public int getColumnCount() {
            return isFirstColumn ? 1 : scenarioIndices.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (isFirstColumn) {
                try {
                    switch (rowIndex) {
                        case 0:
                            return FacilitySummary.HEADER_totalLength;
                        case 1:
                            return FacilitySummary.HEADER_actualTravelTime;
                        case 2:
                            return FacilitySummary.HEADER_VMTD;
                        case 3:
                            return FacilitySummary.HEADER_VMTV;
                        case 4:
                            return FacilitySummary.HEADER_PMTD;
                        case 5:
                            return FacilitySummary.HEADER_PMTV;
                        case 6:
                            return FacilitySummary.HEADER_VHT;
                        case 7:
                            return FacilitySummary.HEADER_VHD;
                        case 8:
                            return FacilitySummary.HEADER_spaceMeanSpeed;
                        case 9:
                            return FacilitySummary.HEADER_reportDensity;
                        case 10:
                            return FacilitySummary.HEADER_maxDC;
                        case 11:
                            return FacilitySummary.HEADER_maxVC;
                        default:
                            return "Error";
                    }
                } catch (Exception e) {
                    return "Error";
                }
            } else {
                switch (rowIndex) {
                    case 0:
                        return scenarioIndices.get(columnIndex).totalLength;
                    case 1:
                        return scenarioIndices.get(columnIndex).actualTravelTime;
                    case 2:
                        return scenarioIndices.get(columnIndex).VMTD;
                    case 3:
                        return scenarioIndices.get(columnIndex).VMTV;
                    case 4:
                        return scenarioIndices.get(columnIndex).PMTD;
                    case 5:
                        return scenarioIndices.get(columnIndex).PMTV;
                    case 6:
                        return scenarioIndices.get(columnIndex).VHT;
                    case 7:
                        return scenarioIndices.get(columnIndex).VHD;
                    case 8:
                        return scenarioIndices.get(columnIndex).spaceMeanSpeed;
                    case 9:
                        return scenarioIndices.get(columnIndex).reportDensity;
                    case 10:
                        return scenarioIndices.get(columnIndex).maxDC;
                    case 11:
                        return scenarioIndices.get(columnIndex).maxVC;
                    default:
                        return "Error";
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * Getter for column header
         *
         * @param col column index
         * @return column header
         */
        @Override
        public String getColumnName(int col) {
            if (isFirstColumn) {
                return " ";
            } else {
                return scenarioListModel.getElementAt(col).toString().substring(0,
                        scenarioListModel.getElementAt(col).toString().indexOf(" ["));
            }
        }
    }

    private class TableNumAndStringRenderer extends DefaultTableCellRenderer {

        /**
         * Constructor
         */
        public TableNumAndStringRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            setForeground(null);
            setBackground(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            setHorizontalAlignment(JLabel.CENTER);
            try {
                switch (row) {
                    case 0:
                    case 1:
                        tryFloat_2f(value.toString());
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        tryFloat_0f(value.toString());
                        break;
                    case 8:
                        tryFloat_1f(value.toString());
                        break;
                    case 9:
                        tryFloat_1f(value.toString());
                        break;
                    case 10:
                        tryFloat_2f(value.toString());
                        break;
                    case 11:
                        tryFloat_2f(value.toString());
                        break;
                    default:
                        tryFloat_1f(value.toString());
                }
            } catch (IllegalArgumentException e2) {
                setText(value.toString());
            }
            return this;
        }

        private void tryPercentage(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.0");
            if (Float.parseFloat(value) > CEConst.ZERO) {
                setText(formatter.format(Float.parseFloat(value) * 100) + "%");
            } else {
                setText("");
            }
        }

        private void tryInt(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Integer.parseInt(value)));
        }

        private void tryFloat_2f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_1f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0.0");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_0f(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            setText(formatter.format(Float.parseFloat(value)));
        }

        private void tryFloat_0f_pos(String value) {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            if (Float.parseFloat(value) > CEConst.ZERO) {
                setText(formatter.format(Float.parseFloat(value)));
            } else {
                setText("");
            }
        }
    }
}
