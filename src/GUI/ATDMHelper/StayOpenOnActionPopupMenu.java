package GUI.ATDMHelper;

import javax.swing.JPopupMenu;

/**
 *
 * @author Lake Trask
 */
public class StayOpenOnActionPopupMenu extends JPopupMenu {

    @Override
    public void setVisible(boolean b) {
        //System.out.println("Called for " +b);
        Boolean doCanceled = null;
        if (b == false) {
            doCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
            //System.out.println("Called for " +doCanceled);
        }
        super.setVisible(b);
        if (doCanceled == null || doCanceled == false) {
            super.setVisible(true);
        }
    }

}
