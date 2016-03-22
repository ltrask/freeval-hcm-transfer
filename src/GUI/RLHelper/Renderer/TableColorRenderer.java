package GUI.RLHelper.Renderer;

import GUI.major.MainWindow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author tristan
 */
public class TableColorRenderer implements TableCellRenderer {

    private int firstColumnAlignment = JTextField.CENTER;

    private boolean firstColumnBold = true;

    private boolean greyOut = false;

    private float minVal = 0.0f;

    private float midVal = 0.0f;

    private float maxVal = 0.0f;

    private final Color minColor = Color.green;

    private final Color midColor = Color.yellow;

    private final Color maxColor = Color.red;

    private final float rMin = minColor.getRed() / 255.0f;

    private final float rMid = midColor.getRed() / 255.0f;

    private final float rMax = maxColor.getRed() / 255.0f;

    private final float gMin = minColor.getGreen() / 255.0f;

    private final float gMid = midColor.getGreen() / 255.0f;

    private final float gMax = maxColor.getGreen() / 255.0f;

    private final float bMin = minColor.getBlue() / 255.0f;

    private final float bMid = midColor.getBlue() / 255.0f;

    private final float bMax = maxColor.getBlue() / 255.0f;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JTextField editor = new JTextField();
        if (value != null) {
            editor.setText(value.toString());
        }

        if (column != 0) {
            try {
                float cellValue = Float.parseFloat(value.toString());
                editor.setBackground(getColor(cellValue));
            } catch (NumberFormatException e) {
                editor.setBackground(Color.white);
            }
            editor.setFont(MainWindow.getTableFont());
            editor.setHorizontalAlignment(JTextField.CENTER);
            editor.setBorder(null);
        } else {
            if (firstColumnBold) {
                editor.setBackground(table.getTableHeader().getBackground());
                editor.setBorder(table.getTableHeader().getBorder());
                //Font headerFont = table.getTableHeader().getFont();
                Font globalFont = MainWindow.getTableFont();
                //Font boldFont = new Font(headerFont.getFamily(), Font.BOLD, headerFont.getSize());
                editor.setHorizontalAlignment(firstColumnAlignment);
                editor.setFont(globalFont);
            }
        }

        if (hasFocus && column != 0) {
            editor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }

        if (greyOut) {
            editor.setBackground(new Color(238, 238, 238));
            editor.setForeground(new Color(238, 238, 238));
        }

        return editor;

    }

    private Color getColor(float cellValue) {

        if (minVal == maxVal) {
            return Color.white;
        } else {

            if (cellValue < minVal) {
                return Color.WHITE;
            } else if (cellValue < midVal) {
                float p = (cellValue - minVal) / (midVal - minVal);
                return new Color(rMin * (1.0f - p) + rMid * p,
                        gMin * (1.0f - p) + gMid * p,
                        bMin * (1.0f - p) + bMid * p,
                        0.7f);
            } else if (cellValue <= maxVal) {
                float p = (cellValue - midVal) / (maxVal - midVal);
                return new Color(rMid * (1.0f - p) + rMax * p,
                        gMid * (1.0f - p) + gMax * p,
                        bMid * (1.0f - p) + bMax * p,
                        0.7f);
            } else {
                return maxColor;
            }
        }
    }

    /**
     *
     * @param newMin
     * @param newMax
     */
    public void setColorRange(float newMin, float newMax) {
        if (newMin <= newMax) {
            minVal = newMin;
            midVal = (newMin + newMax) / 2.0f;
            maxVal = newMax;
        }
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
     * @param val
     */
    public void greyOut(boolean val) {
        greyOut = val;
    }

}
