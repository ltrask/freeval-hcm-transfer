/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major.ClipboardHelper;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Clipboard helper class to transfer data between programs. Adapted from
 * http://www.javapractices.com/topic/TopicAction.do?Id=82
 *
 * @author jltrask
 */
public class ClipboardHelper implements ClipboardOwner {

    @Override
    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
        //do nothing
    }

    public static void setClipboardContents(String contents) {
        StringSelection stringSelection = new StringSelection(contents);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        //clipboard.setContents(stringSelection, this); // If non-static or ownership is needed
    }

    public static String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor));
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                System.out.println(e);
                //e.printStackTrace();
            }
        }
        return result;
    }

}
