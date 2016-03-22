package GUI.RLHelper;

import GUI.major.MainWindow;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author tristan
 */
public class TableSelectionCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JTextField editor;

    private boolean useGlobalFont = false;

    /**
     *
     */
    public TableSelectionCellEditor() {
        editor = new JTextField();
        if (useGlobalFont) {
            editor.setFont(MainWindow.getTableFont());
        }
    }

    /**
     *
     * @param useGlobalFont
     */
    public TableSelectionCellEditor(boolean useGlobalFont) {
        editor = new JTextField();
        this.useGlobalFont = useGlobalFont;
        if (useGlobalFont) {
            editor.setFont(MainWindow.getTableFont());
        }
    }

    @Override
    public Object getCellEditorValue() {
        return editor.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (value != null) {
            editor.setText(value.toString().replace("%", ""));
            editor.setBorder(null);
        }

        if (isSelected) {
            editor.selectAll();
        }

        return editor;
    }

    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {

            if (((MouseEvent) evt).getClickCount() >= 2) {

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        editor.selectAll();
                    }
                });

                return true;
            }
            return false;
        }

        //if (evt instanceof KeyEvent) {
        //    editor.selectAll();
        //}
        return true;
    }

}
