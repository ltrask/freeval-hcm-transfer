package GUI.RLHelper.Renderer;

import GUI.ATDMHelper.TableModels.ATDMModel;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lake Trask
 */
public class ToolTipRenderer extends DefaultTableCellRenderer {

	//private final Color alternateColor = new Color(205, 255, 255, 100);
    //private int firstColumnAlignment = JTextField.LEFT;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel editor = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        ATDMModel model = (ATDMModel) table.getModel();

        editor.setHorizontalAlignment(JTextField.CENTER);
        editor.setToolTipText(model.getToolTip(row, column));

        return editor;

    }

}
