package GUI.ATDMHelper.IO;

import coreEngine.Helper.CEConst;
import coreEngine.atdm.DataStruct.ATDMDatabase;
import coreEngine.atdm.DataStruct.ATDMStrategy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JFileChooser;
import main.FREEVAL_HCM;

/**
 *
 * @author Lake Trask
 */
public class ATDMIOHelper {

    /**
     * Not finished, DO NOT USE
     *
     * @param atdmDatabase
     */
    private static void exportATDMDatabaseASCII(ATDMDatabase atdmDatabase) {
        BufferedWriter writer = null;
        try {
            ATDMDatabaseFileChooser chooser = new ATDMDatabaseFileChooser(FREEVAL_HCM.getInitialDirectory());
            //chooser.setFileFilter(new ASCIIFileFilter());
            File databaseFile = null;
            //ExampleFileFilter filter  = new ExampleFileFilter();
            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == ATDMDatabaseFileChooser.APPROVE_OPTION) {
                databaseFile = new File(chooser.getSelectedFile().getName());
            }
            if (databaseFile != null) {
                writer = new BufferedWriter(new FileWriter(databaseFile));
                // Begin writing file here
                writer.write("ATDM Strategies:\n");

                // Writing demand strategies
                writer.write(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND);
                ArrayList<ATDMStrategy> strategies = atdmDatabase.getStrategy(CEConst.IDS_ATDM_STRAT_TYPE_DEMAND);
                for (ATDMStrategy strat : strategies) {
                    writer.write(strat.getId() + "," + strat.getDescription() + "," + strat.getCategory() + ",");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @param atdmDatabase
     * @return
     */
    public static String exportATDMDatabaseBinary(ATDMDatabase atdmDatabase) {
        ATDMDatabaseFileChooser chooser = new ATDMDatabaseFileChooser(FREEVAL_HCM.getInitialDirectory());
        File databaseFile = null;
        //ExampleFileFilter filter  = new ExampleFileFilter();
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == ATDMDatabaseFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getAbsolutePath();
            if (!fileName.endsWith(".atdmdb")) {
                fileName = fileName + ".atdmdb";
            }
            databaseFile = new File(fileName);
        }
        if (databaseFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(databaseFile);
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(gzos);
                oos.writeObject(atdmDatabase);
                oos.close();
                return "ATDMDatabase saved to " + databaseFile.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to save ATDMDatabase " + e.toString();
            }
        } else {
            return null;
        }
    }

    /**
     * Open a .seed file
     *
     * @return a Seed class object contains the .seed file
     */
    public static ATDMDatabase importATDMDatabaseFromBinary() {
        ATDMDatabase atdmDatabase = null;
        ATDMDatabaseFileChooser atdmDatabaseFileChooser = new ATDMDatabaseFileChooser(FREEVAL_HCM.getInitialDirectory());
        int option = atdmDatabaseFileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            String openFileName = atdmDatabaseFileChooser.getSelectedFile().getAbsolutePath();
            if (!openFileName.endsWith(".atdm")) {
                //TODO warning user
            }
            //TODO check already open

            //open seed from file
            try {
                FileInputStream fis = new FileInputStream(openFileName);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(gzis);
                atdmDatabase = (ATDMDatabase) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.toString());
            }
        }
        return atdmDatabase;
    }

}
