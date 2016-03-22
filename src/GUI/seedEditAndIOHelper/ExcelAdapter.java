package GUI.seedEditAndIOHelper;

import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;

/**
 * This class copy a JTable to clipboard with Excel format
 *
 * @author Shu Liu
 */
public class ExcelAdapter {

    /**
     * Copy a JTable to clipboard with Excel format, including header and data
     *
     * @param jTable JTable to be copied
     * @return whether copy is successful
     */
    public static String copySingleTable(JTable jTable) {
        try {
            //copy whole table, including header and data
            StringBuffer buffer = new StringBuffer();

            for (int col = 0; col < jTable.getColumnCount(); col++) {
                buffer.append(jTable.getColumnName(col) + "\t");
            }
            buffer.append("\n");

            for (int row = 0; row < jTable.getRowCount(); row++) {
                for (int col = 0; col < jTable.getColumnCount(); col++) {
                    buffer.append(jTable.getValueAt(row, col));
                    if (col < jTable.getColumnCount() - 1) {
                        buffer.append("\t");
                    }
                }
                buffer.append("\n");
            }

            StringSelection stsel = new StringSelection(buffer.toString());
            Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
            system.setContents(stsel, stsel);
            return "Table copied to clipboard";
        } catch (Exception e) {
            return "Error when copy table";
        }
    }

    /**
     * Copy a JTable to clipboard with Excel format, including header and data
     *
     * @param firstColumnTable first column of the split table
     * @param restColumnTable rest columns of the split table
     * @return whether copy is successful
     */
    public static String copySplitTable(JTable firstColumnTable, JTable restColumnTable) {
        try {
            //copy whole table, including header and data
            StringBuffer buffer = new StringBuffer();

            //add header of first column table
            buffer.append(firstColumnTable.getColumnName(0) + "\t");

            //add headers of rest column table
            for (int col = 0; col < restColumnTable.getColumnCount(); col++) {
                buffer.append(restColumnTable.getColumnName(col));
                if (col < restColumnTable.getColumnCount() - 1) {
                    buffer.append("\t");
                }
            }
            buffer.append("\n");

            //add each data row
            for (int row = 0; row < firstColumnTable.getRowCount(); row++) {
                buffer.append(firstColumnTable.getValueAt(row, 0) + "\t");

                for (int col = 0; col < restColumnTable.getColumnCount(); col++) {
                    buffer.append(restColumnTable.getValueAt(row, col));
                    if (col < restColumnTable.getColumnCount() - 1) {
                        buffer.append("\t");
                    }
                }
                buffer.append("\n");
            }

            //copy to clipboard
            StringSelection stsel = new StringSelection(buffer.toString());
            Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
            system.setContents(stsel, stsel);
            return "Table copied to clipboard";
        } catch (Exception e) {
            return "Error when copy table";
        }
    }
}
