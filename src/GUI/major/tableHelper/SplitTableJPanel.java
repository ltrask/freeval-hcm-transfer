package GUI.major.tableHelper;

import GUI.major.MainWindow;
import GUI.seedEditAndIOHelper.ExcelAdapter;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class is used to display two JTable as a split table
 *
 * @author Shu Liu
 */
public class SplitTableJPanel extends javax.swing.JPanel {

    private boolean displayOptionsPopupMenuEnabled = true;

    private String menuStyle = MainWindow.TOOLBOX_RL_ATDM;

    public int diffDisplayStyle = 1;

    //private final JTable firstColumnTable;
    //private final JTable restColumnTable;
    /**
     * Creates new form SplitTableJPanel
     *
     * @param firstColumnTable table for first column
     * @param restColumnTable table for rest columns
     */
    public SplitTableJPanel(final JTable firstColumnTable, final JTable restColumnTable) {
        initComponents();

        final java.awt.event.ActionListener copyActionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainWindow.printLog(ExcelAdapter.copySplitTable(firstColumnTable, restColumnTable));
            }
        };

        final java.awt.event.ActionListener settingActionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainWindow.showTableSettings();
            }
        };

        final java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3 && displayOptionsPopupMenuEnabled) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem copyMenuItem = new JMenuItem("Copy Table to Clipboard");
                    copyMenuItem.addActionListener(copyActionListener);
                    menu.add(copyMenuItem);
                    JMenuItem settingMenuItem = new JMenuItem("Table Settings");
                    settingMenuItem.addActionListener(settingActionListener);
                    menu.add(settingMenuItem);
                    if (menuStyle.equalsIgnoreCase(MainWindow.TOOLBOX_WZ)) {
                        JMenu diffDisplayStyleMenu = new JMenu("Show Differences");
                        JCheckBoxMenuItem diffDisplayOff = new JCheckBoxMenuItem("Off");
                        diffDisplayOff.addActionListener(new java.awt.event.ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                diffDisplayStyle = SplitTableJPanel.DISPLAY_DIFF_OFF;
                                ((FREEVAL_TableModel) restColumnTable.getModel()).fireTableDataChanged();
                            }
                        });
                        diffDisplayOff.setSelected(diffDisplayStyle == SplitTableJPanel.DISPLAY_DIFF_OFF);
                        JCheckBoxMenuItem diffDisplayColor = new JCheckBoxMenuItem("With Colors");
                        diffDisplayColor.addActionListener(new java.awt.event.ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                diffDisplayStyle = SplitTableJPanel.DISPLAY_DIFF_COLOR;
                                ((FREEVAL_TableModel) restColumnTable.getModel()).fireTableDataChanged();
                            }
                        });
                        diffDisplayColor.setSelected(diffDisplayStyle == SplitTableJPanel.DISPLAY_DIFF_COLOR);
                        JCheckBoxMenuItem diffDisplaySymbol = new JCheckBoxMenuItem("With Symbols");
                        diffDisplaySymbol.addActionListener(new java.awt.event.ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                diffDisplayStyle = SplitTableJPanel.DISPLAY_DIFF_SYMBOL;
                                ((FREEVAL_TableModel) restColumnTable.getModel()).fireTableDataChanged();
                            }
                        });
                        diffDisplaySymbol.setSelected(diffDisplayStyle == SplitTableJPanel.DISPLAY_DIFF_SYMBOL);
                        ButtonGroup bg = new ButtonGroup();
                        bg.add(diffDisplayOff);
                        bg.add(diffDisplayColor);
                        bg.add(diffDisplaySymbol);
                        diffDisplayStyleMenu.add(diffDisplayOff);
                        diffDisplayStyleMenu.add(diffDisplayColor);
                        diffDisplayStyleMenu.add(diffDisplaySymbol);
                        menu.add(diffDisplayStyleMenu);
                        menu.remove(settingMenuItem);

                    }
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        };

        firstColumnTable.addMouseListener(mouseAdapter);
        restColumnTable.addMouseListener(mouseAdapter);

        jScrollPane1.setViewportView(firstColumnTable);
        jScrollPane2.setViewportView(restColumnTable);

        restColumnTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        jScrollPane2.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Point position = jScrollPane2.getViewport().getViewPosition();
                position.x = 0;
                jScrollPane1.getViewport().setViewPosition(position);
            }
        });

        //set up for row highlight
        ListSelectionListener firstTableListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = firstColumnTable.getSelectedRow();
                try {
                    if (row >= 0) {
                        restColumnTable.setRowSelectionInterval(row, row);
                    }
                } catch (Exception ex) {
                    System.out.println(row + ex.toString());
                }
            }
        };
        firstColumnTable.getSelectionModel().addListSelectionListener(firstTableListener);

        //set up for row highlight
        ListSelectionListener restTableListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = restColumnTable.getSelectedRow();
                try {
                    if (row >= 0) {
                        firstColumnTable.setRowSelectionInterval(row, row);
                    }
                } catch (Exception ex) {
                    System.out.println(row + ex.toString());
                }
            }
        };
        restColumnTable.getSelectionModel().addListSelectionListener(restTableListener);
    }

    public void enableDisplayOptionsPopupMenu(boolean enabled) {
        displayOptionsPopupMenuEnabled = enabled;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridLayout(1, 0));

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(6);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jSplitPane1.setRightComponent(jScrollPane2);

        add(jSplitPane1);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Set divide location
     *
     * @param firstColumnSize width of first column table
     */
    public void setDividerLocation(int firstColumnSize) {
        jSplitPane1.setDividerLocation(firstColumnSize);
    }

    /**
     * Sets the style of the popupmenu (for different toolboxes)
     *
     * @param menuStyle
     */
    public void setPopupMenuStyle(String menuStyle) {
        this.menuStyle = menuStyle;
    }

    public int getDifferenceDisplayStyle() {
        return diffDisplayStyle;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * Do not display any differences
     */
    public final static int DISPLAY_DIFF_OFF = 0;

    /**
     * Display differences with colors to indicate positive/negative
     */
    public final static int DISPLAY_DIFF_COLOR = 1;

    /**
     * Display differences with symbols (+/-) to indicate positive/negative
     */
    public final static int DISPLAY_DIFF_SYMBOL = 2;
}
