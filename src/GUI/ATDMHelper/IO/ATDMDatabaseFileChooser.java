package GUI.ATDMHelper.IO;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Lake Trask
 */
public class ATDMDatabaseFileChooser extends JFileChooser {

    private class ATDMDatabaseFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".atdmdb");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "ATDMDatabase files (*.atdmdb)";
        }
    }

    /**
     * Constructor
     *
     * @param initial initial directory
     */
    public ATDMDatabaseFileChooser(File initial) {
        super(initial);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setFileFilter(new ATDMDatabaseFileFilter());
    }
}
