package GUI.seedEditAndIOHelper;

import java.io.File;
import javax.swing.JFileChooser;

/**
 * This class is a .seed file chooser
 *
 * @author Shu Liu
 */
public class SeedFileChooser extends JFileChooser {

    private class SeedFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".seed" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".seed");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            return "Seed files (*.seed)";
        }
    }

    /**
     * Constructor
     *
     * @param currentDirectory initial directory
     */
    public SeedFileChooser(File currentDirectory) {
        super(currentDirectory);
        //setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setFileFilter(new SeedFileFilter());
        this.setMultiSelectionEnabled(true);
    }

    /**
     * Constructor
     *
     * @param currentDirectory initial directory
     * @param allowMultiSelect boolean indicating whether or not multiple files
     * can be selected.
     */
    public SeedFileChooser(File currentDirectory, boolean allowMultiSelect) {
        super(currentDirectory);
        //setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setFileFilter(new SeedFileFilter());
        this.setMultiSelectionEnabled(allowMultiSelect);
    }
}
