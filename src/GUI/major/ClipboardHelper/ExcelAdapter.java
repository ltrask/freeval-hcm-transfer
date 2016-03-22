/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major.ClipboardHelper;

import com.sun.glass.events.KeyEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 * Class allowing transfer of data between Java JTables and Excel spreadsheets.
 * Adapted from
 * http://www.javaworld.com/article/2077579/learn-java/java-tip-77--enable-copy-and-paste-functionality-between-swing-s-jtables-and-excel.html
 *
 * @author jltrask
 */
public class ExcelAdapter implements ActionListener {

    private String rowString, value;
    private Clipboard system;
    private StringSelection stsel;
    private JTable jTable1;

    public ExcelAdapter(JTable myJTable) {
        jTable1 = myJTable;
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
        jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);

        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public JTable getJTable() {
        return jTable1;
    }

    public void setJTable(JTable jTable1) {
        this.jTable1 = jTable1;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().compareTo("Copy") == 0) {
            //System.out.println("Event Fired - Copy");
            StringBuffer sbf = new StringBuffer();
            int numCols = jTable1.getSelectedColumnCount();
            int numRows = jTable1.getSelectedRowCount();
            int[] rowsSelected = jTable1.getSelectedRows();
            int[] colsSelected = jTable1.getSelectedColumns();
            if (!((numRows - 1 == rowsSelected[rowsSelected.length - 1] - rowsSelected[0]
                    && numRows == rowsSelected.length)
                    && (numCols - 1 == colsSelected[colsSelected.length - 1] - colsSelected[0]
                    && numCols == colsSelected.length))) {
                JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selction", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCols; j++) {
                        sbf.append(jTable1.getValueAt(rowsSelected[i], colsSelected[j]));
                        if (j < numCols - 1) {
                            sbf.append("\t");
                        }
                    }
                    sbf.append("\n");
                }
                stsel = new StringSelection(sbf.toString());
                system = Toolkit.getDefaultToolkit().getSystemClipboard();
                system.setContents(stsel, stsel);
            }
        }
        if (evt.getActionCommand().compareTo("Paste") == 0) {
            System.out.println("Event Fired - Paste");
            int startRow = (jTable1.getSelectedRows())[0];
            int startCol = (jTable1.getSelectedColumns())[0];
            try {
                String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
                String sep = System.getProperty("line.separator");
                if (!trstring.contains("\n")) {
                    sep = "\r";
                }
                StringTokenizer st1 = new StringTokenizer(trstring, sep);
                for (int i = 0; st1.hasMoreTokens(); i++) {
                    rowString = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(rowString, "\t");
                    for (int j = 0; st2.hasMoreTokens(); j++) {
                        value = (String) st2.nextToken();
                        if (startRow + i < jTable1.getRowCount()
                                && startCol + j < jTable1.getColumnCount()) {
                            if (jTable1.isCellEditable(startRow + i, startCol + j)) {
                                jTable1.setValueAt(value, startRow + i, startCol + j);
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
                System.err.println(e);
            }
        }
    }
}
