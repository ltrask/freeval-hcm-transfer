import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 * Used to format JavaDoc
 *
 * @author Shu Liu
 */
public class JavaDocTool {

    private static final HashSet<String> EXCLUDE = new HashSet();

    /**
     * Choose a task
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EXCLUDE.add("JavaDocTool.java");
        checkCodes();
        //countCodes();
        //formatCodes();
    }

    private static void countCodes() {
        System.out.println("Total number of lines: " + countFolder(new File("src")));
    }

    private static int countFolder(File folder) {
        File[] listOfFiles = folder.listFiles();
        int total = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().endsWith(".java")) {
                    total += countFile(listOfFiles[i]);
                }
            } else if (listOfFiles[i].isDirectory()) {
                total += countFolder(listOfFiles[i]);
            }
        }
        return total;
    }

    private static int countFile(File file) {
        int lineCount = 0;
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                in.nextLine();
                lineCount++;
            }
            in.close();
            return lineCount;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private static void checkCodes() {
        //JFileChooser fc = new JFileChooser();
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //int returnVal = fc.showOpenDialog(null);
        //if (returnVal == JFileChooser.APPROVE_OPTION) {
        //    System.out.println(fc.getSelectedFile().getAbsolutePath());
        //    checkFolder(fc.getSelectedFile(), "");
        //}
        checkFolder(new File("src"), "");
    }

    private static void checkFolder(File folder, String indent) {
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().endsWith(".java") && !EXCLUDE.contains(listOfFiles[i].getName())) {
                    System.out.println(indent + listOfFiles[i].getName() + " - " + checkFile(listOfFiles[i]));
                }
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println(indent + "[" + listOfFiles[i].getName() + "]");
                checkFolder(listOfFiles[i], "    " + indent);
            }
        }
    }

    private static String checkFile(File file) {
        try {
            Scanner in = new Scanner(file);
            int lineCount = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                lineCount++;
                if (line.contains("/**")) {
                    boolean finish = false;
                    boolean discFound = false;

                    while (in.hasNextLine() && !finish) {
                        String doc = in.nextLine();
                        lineCount++;
                        if (doc.contains("*/")) {
                            finish = true;
                        } else {
                            if ((doc.lastIndexOf("*") >= doc.length() - 2 && !discFound)
                                    || (doc.contains("@param") && doc.trim().split(" ").length <= 3)
                                    || (doc.contains("@return") && doc.trim().split(" ").length <= 2)) {
                                return "First empty JavaDoc found at line " + lineCount;
                            }
                            if (doc.lastIndexOf("*") < doc.length() - 2) {
                                discFound = true;
                            }
                        }
                    }
                }
            }

            return "Done";

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return "Fail";
        }
    }

    private static void formatCodes() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println(fc.getSelectedFile().getAbsolutePath());
            formatFolder(fc.getSelectedFile(), "");
        }
    }

    private static void formatFolder(File folder, String indent) {
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().endsWith(".java") && !EXCLUDE.contains(listOfFiles[i].getName())) {
                    System.out.println(indent + "File " + listOfFiles[i].getName() + " - " + formatFile(listOfFiles[i]));
                }
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println(indent + "Directory " + listOfFiles[i].getName());
                formatFolder(listOfFiles[i], indent + "    ");
            }
        }
    }

    /**
     * Delete empty JavaDoc for private and package variables and functions
     * (doesn't remove empty JavaDoc with @param or @return and so on for now)
     *
     * @param file file to be formatted
     * @return whether format is successful
     */
    private static String formatFile(File file) {
        try {
            String filename = file.getAbsolutePath();
            File backup = new File("temp.bk");
            Scanner in = new Scanner(file);
            PrintStream out = new PrintStream(backup);

            while (in.hasNextLine()) {
                out.println(in.nextLine());
            }

            in.close();
            out.close();

            in = new Scanner(backup);
            out = new PrintStream(file);

            ArrayList<String> buffer = new ArrayList();

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.contains("/**")) {
                    buffer.add(line);
                    boolean valid = false;
                    boolean finish = false;

                    while (in.hasNextLine() && !finish) {
                        String doc = in.nextLine();
                        buffer.add(doc);
                        if (doc.contains("public")) {
                            finish = true;
                            valid = true;
                        } else {
                            //doc.replaceAll(" @param", "");
                            //doc.replaceAll(" @return", "");
                            if (doc.contains("*")) {
                                if (doc.lastIndexOf("*") < doc.length() - 2
                                        && !(doc.contains("@param") && doc.trim().split(" ").length <= 3)
                                        && !(doc.contains("@return") && doc.trim().split(" ").length <= 2)) {
                                    finish = true;
                                    valid = true;
                                }
                            } else {
                                finish = true;
                                out.println(doc);
                            }
                        }
                    }

                    if (valid) {
                        for (String l : buffer) {
                            out.println(l);
                        }
                    }
                    buffer.clear();

                } else {
                    out.println(line);
                }
            }

            return "Done";
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return "Fail";
        }
    }
}
