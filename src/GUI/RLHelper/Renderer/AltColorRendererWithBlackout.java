package GUI.RLHelper.Renderer;

import GUI.major.MainWindow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jlaketrask
 */
public class AltColorRendererWithBlackout extends JLabel implements TableCellRenderer {

    private final Color alternateColor = new Color(205, 255, 255, 100);

    private int firstColumnAlignment = JTextField.LEFT;

    private boolean firstColumnBold = true;

    private boolean useGlobalFont = false;

    private boolean greyOut = false;

    /**
     *
     */
    public AltColorRendererWithBlackout() {
        super();
    }

    /**
     *
     * @param useGlobalFont
     */
    public AltColorRendererWithBlackout(boolean useGlobalFont) {
        super();
        this.useGlobalFont = useGlobalFont;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //System.out.println("Fired " + row);
        JTextField editor = new JTextField();
        if (value != null) {
            editor.setText(value.toString());
        }

        if (column > row + 2) {
            editor.setBackground(Color.DARK_GRAY);
            editor.setForeground(Color.DARK_GRAY);
        } else if (row % 2 == 0) {
            editor.setBackground(alternateColor);
        }
        if (column == 0) {
            editor.setHorizontalAlignment(firstColumnAlignment);
            editor.setBackground(table.getTableHeader().getBackground());
            if (firstColumnBold) {
                Font headerFont = table.getTableHeader().getFont();
                Font boldFont = new Font(headerFont.getFamily(), Font.BOLD, headerFont.getSize());
                editor.setFont(boldFont);
            }
        } else {
            if (useGlobalFont) {
                editor.setFont(MainWindow.getTableFont());
            }
        }

        editor.setBorder(null);
        editor.setHorizontalAlignment(JTextField.CENTER);

        if (hasFocus && column != 0) {
            editor.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        }

        if (greyOut) {
            editor.setBackground(new Color(238, 238, 238));
            editor.setForeground(new Color(238, 238, 238));
        }

        return editor;

    }

    /**
     *
     * @param alignment
     */
    public void setRowHeaderAlignment(int alignment) {
        firstColumnAlignment = alignment;
    }

    /**
     *
     * @param useBold
     */
    public void setRowHeaderBoldFont(boolean useBold) {
        firstColumnBold = useBold;
    }

    /**
     *
     */
    public void setDisabled() {
        greyOut = true;
    }

    /**
     *
     */
    public void setEnabled() {
        greyOut = false;
    }

}
