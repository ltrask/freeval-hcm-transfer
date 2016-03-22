package GUI.settingHelper;

import GUI.major.tableHelper.TableCellSetting;
import coreEngine.Helper.CEConst;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the JTable for table display settings
 *
 * @author Shu Liu
 */
public class TableSettingJTable extends JTable {

    private TabelSettingTableModel tabelSettingTableModel;

    private boolean hideInputColumn = false;

    /**
     * Constructor
     */
    public TableSettingJTable() {
        super();
        this.getTableHeader().setReorderingAllowed(false);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(true);

        //Set up renderer and editor for the Favorite Color column.
        this.setDefaultRenderer(Color.class, new TableSettingColorRenderer(true));
        this.setDefaultEditor(Color.class, new TableSettingColorEditor());
    }

    /**
     * Setter for existing table display settings
     *
     * @param cellSettings existing table display settings
     */
    public void setCellSettings(ArrayList<TableCellSetting> cellSettings) {
        tabelSettingTableModel = new TabelSettingTableModel(cellSettings);
        this.setModel(tabelSettingTableModel);
        this.getColumnModel().getColumn(0).setPreferredWidth(200);
    }

    @Override
    public Class getColumnClass(int column) {
        column = (column >= 1 && hideInputColumn) ? column + 1 : column;
        switch (column) {
            case 1:
                return Boolean.class;
            case 2:
                return Boolean.class;
            case 3:
                return Color.class;
            default:
                return String.class;
        }
    }

    /**
     * Getter for modified table display settings
     *
     * @return modified table display settings
     */
    public ArrayList<TableCellSetting> getCellSettings() {
        return tabelSettingTableModel.getCellSettings();
    }

    private class TableSettingColorEditor extends AbstractCellEditor
            implements TableCellEditor,
            ActionListener {

        Color currentColor;

        JButton button;

        JColorChooser colorChooser;

        JDialog dialog;

        protected static final String EDIT = "edit";

        /**
         * Constructor
         */
        public TableSettingColorEditor() {
            //Set up the editor (from the table's point of view),
            //which is a button.
            //This button brings up the color chooser dialog,
            //which is the editor from the user's point of view.
            button = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            colorChooser = new JColorChooser();
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true, //modal
                    colorChooser,
                    this, //OK button handler
                    null); //no CANCEL button handler
        }

        /**
         * Handles events from the editor button and from the dialog's OK
         * button.
         *
         * @param e events
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (EDIT.equals(e.getActionCommand())) {
                //The user has clicked the cell, so
                //bring up the dialog.
                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);

                //Make the renderer reappear.
                fireEditingStopped();

            } else { //User pressed dialog's "OK" button.
                currentColor = colorChooser.getColor();
            }
        }

        //Implement the one CellEditor method that AbstractCellEditor doesn't.
        @Override
        public Object getCellEditorValue() {
            return currentColor;
        }

        //Implement the one method defined by TableCellEditor.
        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            currentColor = (Color) value;
            return button;
        }
    }

    private class TableSettingColorRenderer extends JLabel
            implements TableCellRenderer {

        Border unselectedBorder = null;

        Border selectedBorder = null;

        boolean isBordered = true;

        /**
         * Constructor
         *
         * @param isBordered whether this is bordered
         */
        public TableSettingColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object color,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            try {
                Color newColor = (Color) color;
                setBackground(newColor);
                if (isBordered) {
                    if (isSelected) {
                        if (selectedBorder == null) {
                            selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                    table.getSelectionBackground());
                        }
                        setBorder(selectedBorder);
                    } else {
                        if (unselectedBorder == null) {
                            unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                    table.getBackground());
                        }
                        setBorder(unselectedBorder);
                    }
                }

                setToolTipText("RGB value: " + newColor.getRed() + ", "
                        + newColor.getGreen() + ", "
                        + newColor.getBlue());
                return this;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private class TabelSettingTableModel extends AbstractTableModel {

        private final ArrayList<TableCellSetting> cellSettings;

        /**
         * Constructor
         *
         * @param cellSettings existing cell settings
         */
        public TabelSettingTableModel(ArrayList<TableCellSetting> cellSettings) {
            //use deep copy to allow cancel
            this.cellSettings = new ArrayList<>();
            for (TableCellSetting cellSetting : cellSettings) {
                this.cellSettings.add(cellSetting.clone());
            }
        }

        @Override
        public int getRowCount() {
            return cellSettings.size();
        }

        @Override
        public int getColumnCount() {
            return hideInputColumn ? 3 : 4;
        }

        @Override
        public Object getValueAt(int row, int column) {
            column = (column >= 1 && hideInputColumn) ? column + 1 : column;
            switch (column) {
                case 0:
                    return cellSettings.get(row).header;
                case 1:
                    return cellSettings.get(row).showInInput;
                case 2:
                    return cellSettings.get(row).showInOutput;
                case 3:
                    return cellSettings.get(row).bgColor;
                default:
                    return CEConst.IDS_ERROR;
            }
        }

        @Override
        public String getColumnName(int column) {
            column = (column >= 1 && hideInputColumn) ? column + 1 : column;
            switch (column) {
                case 0:
                    return "Header";
                case 1:
                    return "Show In Input";
                case 2:
                    return "Show In Output";
                case 3:
                    return "Color";
                default:
                    return CEConst.IDS_ERROR;
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            column = (column >= 1 && hideInputColumn) ? column + 1 : column;
            switch (column) {
                case 0:
                    cellSettings.get(row).header = value.toString();
                    break;
                case 1:
                    cellSettings.get(row).showInInput = Boolean.parseBoolean(value.toString());
                    break;
                case 2:
                    cellSettings.get(row).showInOutput = Boolean.parseBoolean(value.toString());
                    break;
                case 3:
                    cellSettings.get(row).bgColor = (Color) value;
                    break;
            }
            fireTableCellUpdated(row, column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }

        /**
         * Getter for modified cell settings
         *
         * @return modified cell settings
         */
        public ArrayList<TableCellSetting> getCellSettings() {
            return cellSettings;
        }
    }

    public void hideInputColumn(boolean setHidden) {
        this.hideInputColumn = setHidden;
        tabelSettingTableModel.fireTableStructureChanged();
    }
}
