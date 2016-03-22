package GUI.seedEditAndIOHelper;

import java.io.File;
import javax.swing.JFileChooser;

/**
 * This class is a .csv file chooser
 *
 * @author Shu Liu
 */
public class CSVFileChooser extends JFileChooser {

    private class CSVFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".csv");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "CSV files (*.csv)";
        }
    }

    /**
     * Constructor
     *
     * @param initial initial directory
     */
    public CSVFileChooser(File initial) {
        super(initial);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setFileFilter(new CSVFileFilter());
    }
}
