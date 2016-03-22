package GUI.RLHelper.Renderer;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lake Trask
 */
public class DecimalFormatRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter;

    /**
     *
     */
    public DecimalFormatRenderer() {
        formatter = new DecimalFormat("#.0000");
    }

    /**
     *
     * @param numDecimalsDisplayed
     */
    public DecimalFormatRenderer(int numDecimalsDisplayed) {
        String tempString = "#.";
        for (int i = 0; i < numDecimalsDisplayed; i++) {
            tempString = tempString + "0";
        }
        formatter = new DecimalFormat(tempString);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // First format the cell value as required
        value = formatter.format((Number) value);
        // And pass it on to parent class
        JLabel tempRenderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        tempRenderer.setHorizontalAlignment(JLabel.CENTER);
        return tempRenderer;
    }
}
