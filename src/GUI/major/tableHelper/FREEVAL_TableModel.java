package GUI.major.tableHelper;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Abstract class of table model with getter for cell editor and cell renderer
 *
 * @author Shu Liu
 */
public abstract class FREEVAL_TableModel extends AbstractTableModel {

    /**
     * Getter for cell editor
     *
     * @param row row index
     * @param column column index
     * @return cell editor
     */
    public abstract TableCellEditor getCellEditor(int row, int column);

    /**
     * Getter for cell render
     *
     * @param row row index
     * @param column column index
     * @return cell render
     */
    public abstract TableCellRenderer getCellRenderer(int row, int column);
}
