package GUI.adaptiveRampMetering;

import GUI.major.MainWindow;
import coreEngine.Helper.RampMeteringData.RampMeteringData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Shu Liu
 */
public class AdaptiveRampMeteringEditPanel extends javax.swing.JPanel {

    private final TreeMap<String, RampMeteringData> data;
    private String currentKey = "Default";
    private String[] keyList;
    private final AdaptiveRampMeteringListModel listModel;
    private final AdaptiveRampMeteringEditModel editModel;

    /**
     * Creates new form AdaptiveRampMeteringEditPanel
     *
     * @param data Ramp metering data to be edited
     */
    public AdaptiveRampMeteringEditPanel(TreeMap data) {
        initComponents();

        this.data = data;

        configureKeyList();

        listModel = new AdaptiveRampMeteringListModel();
        listTable.setModel(listModel);
        listTable.setFont(MainWindow.getTableFont());
        listTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        editModel = new AdaptiveRampMeteringEditModel();
        editTable.setModel(editModel);
        editTable.setFont(MainWindow.getTableFont());
        editTable.setRowHeight(MainWindow.getTableFont().getSize() + 2);

        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (listTable.getSelectedRow() >= 0) {
                    currentKey = listTable.getValueAt(listTable.getSelectedRow(), 0).toString();
                    editModel.fireTableStructureChanged();
                    setUpTableLayout();
                }
            }
        };

        listTable.getSelectionModel().addListSelectionListener(selectionListener);
        listTable.setRowSelectionInterval(0, 0);

        setUpTableLayout();
    }

    private void setUpTableLayout() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setFont(MainWindow.getTableFont());
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        listTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        editTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        editTable.getColumnModel().getColumn(0).setPreferredWidth(300);

        JTextField textFieldForCellEditor1 = new JTextField();
        textFieldForCellEditor1.setHorizontalAlignment(JTextField.CENTER);
        textFieldForCellEditor1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldForCellEditor1.selectAll();
            }
        });
        textFieldForCellEditor1.setBorder(null);
        textFieldForCellEditor1.setFont(MainWindow.getTableFont());
        DefaultCellEditor defaultCellEditor1 = new DefaultCellEditor(textFieldForCellEditor1);

        JTextField textFieldForCellEditor2 = new JTextField();
        textFieldForCellEditor2.setHorizontalAlignment(JTextField.CENTER);
        textFieldForCellEditor2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldForCellEditor2.selectAll();
            }
        });
        textFieldForCellEditor2.setBorder(null);
        textFieldForCellEditor2.setFont(MainWindow.getTableFont());
        DefaultCellEditor defaultCellEditor2 = new DefaultCellEditor(textFieldForCellEditor2);

        listTable.getColumnModel().getColumn(0).setCellEditor(defaultCellEditor1);
        editTable.getColumnModel().getColumn(1).setCellEditor(defaultCellEditor2);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        editTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(0.2);

        editTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Parameter", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        editTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(editTable);

        jSplitPane1.setRightComponent(jScrollPane1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        listTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Default"}
            },
            new String [] {
                "Scheme"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        listTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(listTable);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout());

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addButton);

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(removeButton);

        jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(now);
        String newKey = "New Scheme " + time;
        try {
            data.put(newKey, data.get("Default").getClass().newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        listModel.update(newKey);
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (currentKey.equals("Default")) {
            JOptionPane.showMessageDialog(AdaptiveRampMeteringEditPanel.this, "Cannot remove default scheme", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            data.remove(currentKey);
            listModel.update(currentKey);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTable editTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable listTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    private void configureKeyList() {
        keyList = new String[data.size()];
        keyList[0] = "Default";
        int count = 1;
        for (String key : data.navigableKeySet()) {
            if (!key.equals("Default")) {
                keyList[count++] = key;
            }
        }
    }

    private class AdaptiveRampMeteringListModel extends AbstractTableModel {

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return rowIndex > 0;
        }

        @Override
        public int getRowCount() {
            return keyList.length;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return keyList[rowIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return "Scheme";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                String newKey = aValue.toString();
                String oldKey = keyList[rowIndex];

                if (data.containsKey(newKey) && !newKey.equals(oldKey)) {
                    JOptionPane.showMessageDialog(AdaptiveRampMeteringEditPanel.this, "The scheme name has to be unique. Duplicated scheme name found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (oldKey.equals("Default")) {
                    JOptionPane.showMessageDialog(AdaptiveRampMeteringEditPanel.this, "Cannot rename default scheme", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                data.put(newKey, data.remove(oldKey));

                update(newKey);
            }
        }

        public void update(String newKey) {
            configureKeyList();
            fireTableStructureChanged();
            boolean found = false;
            for (int row = 0; row < getRowCount(); row++) {
                if (getValueAt(row, 0).toString().equals(newKey)) {
                    listTable.setRowSelectionInterval(row, row);
                    found = true;
                    break;
                }
            }
            if (!found) {
                listTable.setRowSelectionInterval(0, 0);
            }
            setUpTableLayout();
        }
    }

    private class AdaptiveRampMeteringEditModel extends AbstractTableModel {

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0;
        }

        @Override
        public int getRowCount() {
            return data.get(currentKey).getItemCount();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return data.get(currentKey).getItemName(rowIndex);
                case 1:
                    return data.get(currentKey).getItemValue(rowIndex);
            }
            return "Error";
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Parameter";
                case 1:
                    return "Value";
            }
            return "Error";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 1) {
                try {
                    data.get(currentKey).setItemValue(rowIndex, Double.parseDouble(aValue.toString()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdaptiveRampMeteringEditPanel.this, "Value has to be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}