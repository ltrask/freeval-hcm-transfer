package GUI.major.tableHelper;

import GUI.major.MainWindow;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

/**
 * Modified JTable
 *
 * @author Shu Liu
 */
public class FREEVAL_JTable extends JTable {

    private FREEVAL_TableModel tableModel;

    /**
     * Constructor
     *
     * @param tableModel table model used in this table
     */
    public FREEVAL_JTable(FREEVAL_TableModel tableModel) {
        super();
        this.tableModel = tableModel;
        this.setModel(tableModel);
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.setFont(MainWindow.getTableFont());
        setRowHeight(19);
        resetModel();
    }

    private void resetModel() {
        getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Highlight a column
     *
     * @param col column to be highlighted
     */
    public void setHighlightCol(int col) {
        try {
            setColumnSelectionInterval(col, col);
        } catch (Exception e) {
            //skip
        }
    }

    /**
     * Highlight a row
     *
     * @param row row to be highlighted
     */
    public void setHighlightRow(int row) {
        try {
            setRowSelectionInterval(row, row);
        } catch (Exception e) {
            //skip
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return tableModel.getCellEditor(row, column);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return tableModel.getCellRenderer(row, column);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);
        selectAll(e);
        return result;
    }

    /*
     * Select the text when editing on a text related cell is started
     */
    private void selectAll(EventObject e) {
        final Component editor = getEditorComponent();

        if (editor == null
                || !(editor instanceof JTextComponent)) {
            return;
        }

        if (e == null) {
            ((JTextComponent) editor).selectAll();
            return;
        }

        //  Typing in the cell was used to activate the editor
        if (e instanceof KeyEvent) {
            ((JTextComponent) editor).selectAll();
            return;
        }

        //  F2 was used to activate the editor
        if (e instanceof ActionEvent) {
            ((JTextComponent) editor).selectAll();
            return;
        }

        //  A mouse click was used to activate the editor.
        //  Generally this is a double click and the second mouse click is
        //  passed to the editor which would remove the text selection unless
        //  we use the invokeLater()
        if (e instanceof MouseEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((JTextComponent) editor).selectAll();
                }
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="setter and getters">
    /**
     * Getter for table model
     *
     * @return table model
     */
    public FREEVAL_TableModel getTableModel() {
        return tableModel;
    }

    /**
     * Setter for table model
     *
     * @param tableModel table model
     */
    public void setTableModel(FREEVAL_TableModel tableModel) {
        this.tableModel = tableModel;
    }
    // </editor-fold>
}
