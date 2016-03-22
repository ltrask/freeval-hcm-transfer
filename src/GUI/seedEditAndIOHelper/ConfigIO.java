package GUI.seedEditAndIOHelper;

import GUI.major.FREEVALProject;
import GUI.major.MainWindow;
import GUI.major.graphicHelper.GraphicColorSetting;
import GUI.major.tableHelper.TableCellSetting;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class is the configuration dialog
 *
 * @author Shu Liu
 */
public class ConfigIO {

    /**
     * Save opened seed list to disk
     *
     * @param seedList opened seed list
     * @return whether saving opened seed list to disk is successful
     */
    public static String saveSeedListToConfig(ArrayList<Seed> seedList, HashMap<Seed, FREEVALProject> toolboxAuxHash) {
        try {
            File seedListConfigFile = new File(getCfgFolderLocation() + "seedlist.cfg");
            PrintStream printStream = new PrintStream(seedListConfigFile);

            for (Seed seed : seedList) {
                if (seed.getValueString(CEConst.IDS_SEED_FILE_NAME) != null && toolboxAuxHash.get(seed) == null) {
                    printStream.println(seed.getValueString(CEConst.IDS_SEED_FILE_NAME));
                }
            }

            printStream.close();

            return "Seed list saved.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail to save seed list.";
        }
    }

    /**
     * Load opened seed list from disk
     *
     * @param mainWindow MainWindow instance used to store opened seed list
     */
    public static void loadSeedListFromConfig(MainWindow mainWindow) {
        //open seed from file
        int count = 0;
        try {
            //read file outside jar
            String settingFileName = getCfgFolderLocation() + "seedlist.cfg";
            Scanner fileScanner = new Scanner(new File(settingFileName));

            while (fileScanner.hasNextLine()) {
                String openFileName = fileScanner.nextLine();
                try {
                    if (!openFileName.equals("null")) {
                        FileInputStream fis = new FileInputStream(openFileName);
                        GZIPInputStream gzis = new GZIPInputStream(fis);
                        ObjectInputStream ois = new ObjectInputStream(gzis);
                        Seed seed = (Seed) ois.readObject();
                        seed.resetSeedToInputOnly();
                        ois.close();
                        fis.close();
                        seed.setValue(CEConst.IDS_SEED_FILE_NAME, openFileName);
                        mainWindow.addSeed(seed);
                        count++;
                    }
                } catch (IOException | ClassNotFoundException e1) {
                    //skip this file
                }
            }

            fileScanner.close();
            if (count > 0) {
                mainWindow.printLog("Last opened seed files loaded.");
            }
        } catch (FileNotFoundException e2) {
            //mainWindow.printLog("Fail to load default setting.");
        }
    }

    /**
     * Save table display item and font setting to disk
     *
     * @param cellSettings table display items
     * @param font table display font
     * @return whether saving table display setting to disk is successful
     */
    public static String saveTableConfig(ArrayList<TableCellSetting> cellSettings, Font font) {
        try {
            File tableConfigFile = new File(getCfgFolderLocation() + "tableItem.cfg");
            FileOutputStream fos = new FileOutputStream(tableConfigFile);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gzos);
            oos.writeObject(cellSettings);
            oos.close();
            fos.close();

            tableConfigFile = new File(getCfgFolderLocation() + "tableFont.cfg");
            fos = new FileOutputStream(tableConfigFile);
            gzos = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gzos);
            oos.writeObject(font);
            oos.close();
            fos.close();

            return "Table default setting saved.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail to save table default setting.";
        }
    }

    /**
     * Save table display item and font setting to disk
     *
     * @param colorSettings table display items
     * @return whether saving table display setting to disk is successful
     */
    public static String saveGraphicConfig(ArrayList<GraphicColorSetting> colorSettings) {
        try {
            //save file outside jar

            File graphicConfigFile = new File(getCfgFolderLocation() + "graphicColor.cfg");
            FileOutputStream fos = new FileOutputStream(graphicConfigFile);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gzos);
            oos.writeObject(colorSettings);
            oos.close();
            fos.close();

            return "Graphic default setting saved.";
        } catch (Exception e) {
            MainWindow.printLog(e.toString());
            return "Fail to save Graphic default setting.";
        }
    }

    /**
     * Load table display setting from disk
     *
     * @param mainWindow MainWindow instance used to store table display setting
     * @return table display setting
     */
    public static ArrayList<TableCellSetting> loadTableConfig(MainWindow mainWindow) {
        ArrayList<TableCellSetting> cellSettings;
        Font font;

        try {
            //read file outside jar
            String settingFileName = getCfgFolderLocation() + "tableItem.cfg";
            FileInputStream fis = new FileInputStream(settingFileName);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gzis);
            cellSettings = (ArrayList<TableCellSetting>) ois.readObject();
            ois.close();
            fis.close();

            try {
                settingFileName = getCfgFolderLocation() + "tableFont.cfg";
                fis = new FileInputStream(settingFileName);
                gzis = new GZIPInputStream(fis);
                ois = new ObjectInputStream(gzis);
                font = (Font) ois.readObject();
                ois.close();
                fis.close();

                mainWindow.setTableFont(font);
            } catch (Exception e) {
                //skip load default font
            }
            mainWindow.printLog("User default table settings loaded.");
            return cellSettings;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load graphic display setting from disk
     *
     * @param mainWindow MainWindow instance used to store graphic display
     * setting
     * @return graphic display setting
     */
    public static ArrayList<GraphicColorSetting> loadGraphicConfig(MainWindow mainWindow) {
        ArrayList<GraphicColorSetting> colorSettings;

        try {
            //read file outside jar
            String settingFileName = getCfgFolderLocation() + "graphicColor.cfg";
            FileInputStream fis = new FileInputStream(settingFileName);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gzis);
            colorSettings = (ArrayList<GraphicColorSetting>) ois.readObject();
            ois.close();
            fis.close();

            mainWindow.printLog("User default graphic settings loaded.");
            return colorSettings;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCfgFolderLocation() {
        String location = ConfigIO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        location = location.replaceAll("%20", " ");
        if (location.contains("/build/classes")) {
            location = location.substring(0, location.lastIndexOf("/build")) + "/" + "resources" + "/";
        }
        location = location.substring(0, location.lastIndexOf("/")) + "/" + "cfg";
        File cfgFolder = new File(location);
        if (!cfgFolder.exists()) {
            cfgFolder.mkdirs();
        }
        return location + "/";
    }
}
