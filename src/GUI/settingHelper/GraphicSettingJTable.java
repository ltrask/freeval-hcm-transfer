package GUI.settingHelper;

import GUI.major.graphicHelper.GraphicColorSetting;
import GUI.major.GraphicDisplay;
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
public class GraphicSettingJTable extends JTable {

    private GraphicSettingTableModel graphicSettingTableModel;

    /**
     * Constructor
     */
    public GraphicSettingJTable() {
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
     * Setter for graphic table display settings
     *
     * @param graphicDisplay existing graphic display settings
     */
    public void setCellSettings(GraphicDisplay graphicDisplay) {
        graphicSettingTableModel = new GraphicSettingTableModel(graphicDisplay);
        this.setModel(graphicSettingTableModel);
        this.getColumnModel().getColumn(0).setPreferredWidth(200);
    }

    /**
     * Getter for modified table display settings
     *
     * @return modified table display settings
     */
    public ArrayList<GraphicColorSetting> getCellSettings() {
        return graphicSettingTableModel.colorSettings;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 1:
                return Color.class;
            default:
                return String.class;
        }
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
    
    private class GraphicSettingTableModel extends AbstractTableModel {

        private final ArrayList<GraphicColorSetting> colorSettings;

        /**
         * Constructor
         *
         * @param graphicDisplay graphic display that contains existing color settings
         */
        public GraphicSettingTableModel(GraphicDisplay graphicDisplay) {
            //use deep copy to allow cancel
            colorSettings = new ArrayList<>();
            for (GraphicColorSetting colorSetting : graphicDisplay.getScaleColors()) {
                colorSettings.add(colorSetting.clone());
            }
        }

        @Override
        public int getRowCount() {
            return colorSettings.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (column) {
                case 0:
                    return colorSettings.get(row).displayName;
                case 1:
                    return colorSettings.get(row).bgColor;
                default:
                    return CEConst.IDS_ERROR;
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Item";
                case 1:
                    return "Color";
                default:
                    return CEConst.IDS_ERROR;
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            switch (column) {
                case 1:
                    colorSettings.get(row).bgColor = (Color) value;
                    break;
            }
            fireTableCellUpdated(row, column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }
    }
}
