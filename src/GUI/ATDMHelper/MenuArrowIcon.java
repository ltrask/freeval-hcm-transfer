package GUI.ATDMHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 *
 * @author Lake Trask
 */
public class MenuArrowIcon implements Icon {

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.BLACK);
        g2.translate(x, y);
        g2.drawLine(2, 3, 6, 3);
        g2.drawLine(3, 4, 5, 4);
        g2.drawLine(4, 5, 4, 5);
        g2.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
        return 10;
    }
}
