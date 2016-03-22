package GUI.seedEditAndIOHelper;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.FREEVAL_HCM;

/**
 * This class contains static helper functions for .seed file input and output
 *
 * @author Shu Liu
 */
public class SeedIOHelper {

    private static int newProjectIndexCounter = 1;

    /**
     * Save seed to original .seed file
     *
     * @param seed seed to be saved
     * @return whether save seed is successful
     */
    public static String saveSeed(Seed seed) {
        if (seed == null) {
            JOptionPane.showMessageDialog(null, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return "Fail to save seed";
        }

        if (seed.getValueString(CEConst.IDS_SEED_FILE_NAME) == null) {
            return saveAsSeed(seed);
        } else {
            //save seed to file
            try {
                FileOutputStream fos = new FileOutputStream(seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(gzos);
                oos.writeObject(seed);
                oos.close();
                return "Seed saved to " + seed.getValueString(CEConst.IDS_SEED_FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                return "Fail to save seed " + e.toString();
            }
        }
    }

    /**
     * Save seed to original .seed file
     *
     * @param parent Component on which to open the dialog
     * @param seed seed to be saved
     * @return whether save seed is successful
     */
    public static String saveSeed(JFrame parent, Seed seed) {
        if (seed == null) {
            JOptionPane.showMessageDialog(parent, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return "Fail to save seed";
        }

        if (seed.getValueString(CEConst.IDS_SEED_FILE_NAME) == null) {
            return saveAsSeed(parent, seed);
        } else {
            //save seed to file
            try {
                FileOutputStream fos = new FileOutputStream(seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(gzos);
                oos.writeObject(seed);
                oos.close();
                return "Seed saved to " + seed.getValueString(CEConst.IDS_SEED_FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                return "Fail to save seed " + e.toString();
            }
        }
    }

    /**
     * Save seed to another .seed file
     *
     * @param seed seed to be saved
     * @return whether save seed is successful
     */
    public static String saveAsSeed(Seed seed) {
        if (seed == null) {
            JOptionPane.showMessageDialog(null, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return "Fail to save seed";
        }
        try {
            SeedFileChooser seedFileChooser = new SeedFileChooser(FREEVAL_HCM.getInitialDirectory());
            int option = seedFileChooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                String saveFileName = seedFileChooser.getSelectedFile().getAbsolutePath();
                if (!saveFileName.endsWith(".seed")) {
                    saveFileName += ".seed";
                }
                //save seed to file

                FileOutputStream fos = new FileOutputStream(saveFileName);
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(gzos);
                oos.writeObject(seed);
                oos.close();
                seed.setValue(CEConst.IDS_SEED_FILE_NAME, saveFileName);
                return "Seed saved to " + seed.getValueString(CEConst.IDS_SEED_FILE_NAME);
            } else {
                return "Save cancelled by user";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail to save seed " + e.toString();
        }
    }

    /**
     * Save seed to another .seed file
     *
     * @param parent Component on which to open the dialog
     * @param seed seed to be saved
     * @return whether save seed is successful
     */
    public static String saveAsSeed(JFrame parent, Seed seed) {
        if (seed == null) {
            JOptionPane.showMessageDialog(parent, "No seed is selected", "Error", JOptionPane.ERROR_MESSAGE);
            return "Fail to save seed";
        }
        try {
            SeedFileChooser seedFileChooser = new SeedFileChooser(FREEVAL_HCM.getInitialDirectory());
            int option = seedFileChooser.showSaveDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION) {
                String saveFileName = seedFileChooser.getSelectedFile().getAbsolutePath();
                if (!saveFileName.endsWith(".seed")) {
                    saveFileName += ".seed";
                }
                //save seed to file

                FileOutputStream fos = new FileOutputStream(saveFileName);
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(gzos);
                oos.writeObject(seed);
                oos.close();
                seed.setValue(CEConst.IDS_SEED_FILE_NAME, saveFileName);
                return "Seed saved to " + seed.getValueString(CEConst.IDS_SEED_FILE_NAME);
            } else {
                return "Save cancelled by user";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail to save seed " + e.toString();
        }
    }

    /**
     * Open a .seed file
     *
     * @return a Seed class object contains the .seed file
     */
    public static Seed openSeed() {
        Seed seed = null;
        try {
            SeedFileChooser seedFileChooser = new SeedFileChooser(FREEVAL_HCM.getInitialDirectory());
            int option = seedFileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                String openFileName = seedFileChooser.getSelectedFile().getAbsolutePath();

                //open seed from file
                FileInputStream fis = new FileInputStream(openFileName);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                seed = (Seed) ois.readObject();
                seed.resetSeedToInputOnly();
                ois.close();
                seed.setValue(CEConst.IDS_SEED_FILE_NAME, openFileName);

            }
        } catch (Exception e) {
            e.printStackTrace();
            MainWindow.printLog(e.toString());
        }
        return seed;
    }

    /**
     * Open a .seed file
     *
     * @param parent Component on which to open the dialog
     * @return a Seed class object contains the .seed file
     */
    public static Seed openSeed(JFrame parent) {
        Seed seed = null;
        try {
            SeedFileChooser seedFileChooser = new SeedFileChooser(FREEVAL_HCM.getInitialDirectory());
            int option = seedFileChooser.showOpenDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION) {
                String openFileName = seedFileChooser.getSelectedFile().getAbsolutePath();

                //open seed from file
                FileInputStream fis = new FileInputStream(openFileName);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                seed = (Seed) ois.readObject();
                seed.resetSeedToInputOnly();
                ois.close();
                seed.setValue(CEConst.IDS_SEED_FILE_NAME, openFileName);

            }
        } catch (Exception e) {
            e.printStackTrace();
            MainWindow.printLog(e.toString());
        }
        return seed;
    }

    /**
     * Open a .seed file
     *
     * @param parent Component on which to open the dialog
     * @return an array of Seed class object contains the .seed file
     */
    public static Seed[] openMultiSeed(JFrame parent) {
        Seed[] seeds = null;
        try {
            SeedFileChooser seedFileChooser = new SeedFileChooser(FREEVAL_HCM.getInitialDirectory(), true);
            int option = seedFileChooser.showOpenDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION) {
                File[] files = seedFileChooser.getSelectedFiles();
                seeds = new Seed[files.length];
                for (int fid = 0; fid < files.length; fid++) {
                    String openFileName = files[fid].getAbsolutePath();

                    //open seed from file
                    FileInputStream fis = new FileInputStream(openFileName);
                    GZIPInputStream gzis = new GZIPInputStream(fis);
                    ObjectInputStream ois = new ObjectInputStream(gzis);
                    seeds[fid] = (Seed) ois.readObject();
                    seeds[fid].resetSeedToInputOnly();
                    ois.close();
                    seeds[fid].setValue(CEConst.IDS_SEED_FILE_NAME, openFileName);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            MainWindow.printLog(e.toString());
        }
        return seeds;
    }

    public static int getNewProjectCounterIndex() {
        return newProjectIndexCounter++;
    }
}
