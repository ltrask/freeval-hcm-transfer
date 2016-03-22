package GUI.ATDMHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Lake Trask
 */
public class TableHeaderRendererWithPopup extends JButton implements TableCellRenderer {

    private static final int BUTTON_WIDTH = 16;

    private static final Color BUTTONBGC = new Color(200, 200, 200, 100);

    private JPopupMenu pop;

    private JPopupMenu[] popList;

    private int rolloverIndex = -1;

    private transient final MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            JTable table = header.getTable();
            TableColumnModel columnModel = table.getColumnModel();
            int vci = columnModel.getColumnIndexAtX(e.getX());
            table.setColumnSelectionInterval(vci, vci);
            if (vci == 0 || (vci > 2 && vci < 9)) {
                if (vci < 2) {
                    pop = popList[vci];
                } else {
                    pop = popList[vci - 2];
                }
                //int mci = table.convertColumnIndexToModel(vci);
                //TableColumn column = table.getColumnModel().getColumn(mci);
                //int w = column.getWidth(); //Nimbus???
                //int h = header.getHeight();
                Rectangle r = header.getHeaderRect(vci);
                Container c = (Container) getTableCellRendererComponent(table, "", true, true, -1, vci);
                //if(!isNimbus) {
                //  Insets i = c.getInsets();
                //  r.translate(r.width-i.right, 0);
                //}else{
                r.translate(r.width - BUTTON_WIDTH, 0);
                r.setSize(BUTTON_WIDTH, r.height);
                Point pt = e.getPoint();
                if (c.getComponentCount() > 0 && r.contains(pt) && pop != null) {
                    pop.show(header, r.x, r.height);
                    JButton b = (JButton) c.getComponent(0);
                    b.doClick();
                    e.consume();
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            rolloverIndex = -1;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            JTable table = header.getTable();
            TableColumnModel columnModel = table.getColumnModel();
            int vci = columnModel.getColumnIndexAtX(e.getX());
            int mci = table.convertColumnIndexToModel(vci);
            rolloverIndex = mci;
        }
    };

    /**
     *
     * @param header
     * @param popList
     */
    public TableHeaderRendererWithPopup(JTableHeader header, JPopupMenu[] popList) {
        super();
        //setOpaque(false);
        //setFont(header.getFont());
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        this.popList = popList;
        header.addMouseListener(ma);
        header.addMouseMotionListener(ma);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (pop == null) {
            pop = new JPopupMenu();
        } else {
            SwingUtilities.updateComponentTreeUI(pop);
        }
    }

//     JButton button = new JButton(new AbstractAction() {
//         @Override public void actionPerformed(ActionEvent e) {
//             System.out.println("clicked");
//         }
//     });
    @Override
    public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
        TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
        JLabel l = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
        if (col > 3) {
            setIcon(new MenuArrowIcon());
            l.removeAll();
            int mci = tbl.convertColumnIndexToModel(col);

            if (rolloverIndex == mci) {
                TableColumn column = tbl.getColumnModel().getColumn(mci);
                int w = column.getWidth();
                int h = tbl.getTableHeader().getHeight();
                //Icon icon = new MenuArrowIcon();
                Border outside = l.getBorder();
                Border inside = BorderFactory.createEmptyBorder(0, 0, 0, BUTTON_WIDTH);
                Border b = BorderFactory.createCompoundBorder(outside, inside);
                l.setBorder(b);
                l.add(this);
                //Insets i = b.getBorderInsets(l);
                //setBounds(w-i.right, 0, BUTTON_WIDTH, h-2);
                setBounds(w - BUTTON_WIDTH, 0, BUTTON_WIDTH, h - 2);
                setBackground(BUTTONBGC);
                setOpaque(true);
                setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
            }
        }
//         if(l.getPreferredSize().height>1000) { //XXX: Nimbus
//             System.out.println(l.getPreferredSize().height);
//             l.setPreferredSize(new Dimension(0, h));
//         }
        return l;
    }
}
