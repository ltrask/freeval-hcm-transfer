package main;

import GUI.major.MainWindow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.File;

/**
 * This is the main class of FREEVAL.
 *
 * @author Shu Liu
 */
public class FREEVAL_HCM {

    /**
     * Version of the FREEVAL
     */
    public static String VERSION = "20160318";

    /**
     * Version of GUI and restrictions
     */
    public static final String LICENSE_VERSION = "FREEVAL_HCM";
    //FREEVAL_BETA, FREEVAL_HCM, FREEVAL_FULL, FREEVAL_DSS, FREEVAL_WZ, FREEVAL_FIU, DEVELOP
    // FREEVAL_BETA and FREEVAL_HCM elimates many features, but attaching BETA to another
    // verions type will not do the same;

    /**
     * Expire Date
     */
    //public static final Date EXPIRE = new Date(1462017600000L); // Expire on 04/30/2016 1462017600000
    /**
     * Time server list
     */
    private static final String[] TIME_SERVER = {"time-c.nist.gov", "nist.time.nosc.us", "nist1-lv.ustiming.org", "utcnist.colorado.edu"};

    private static boolean returnStatus = true;

    private static boolean cancelled = false;

    private static boolean retry = true;

    /**
     * main method of FREEVAL create a main window
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        //read version infomation
        try {
            java.io.File file = new java.io.File(FREEVAL_HCM.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            java.util.jar.JarFile jar = new java.util.jar.JarFile(file);
            java.util.jar.Manifest manifest = jar.getManifest();

            VERSION = manifest.getMainAttributes().getValue("Bundle-Version");

            jar.close();

        } catch (Exception e) {
            //VERSION = "Unknown Version";
        }

        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();
            if (g == null) {
                System.out.println("g is null");
            }
            g.setComposite(AlphaComposite.Clear);
            g.setPaintMode();
            g.setColor(Color.BLACK);
            g.drawRect(210, 570, 290, 20);
            g.setPaintMode();
            g.drawString("Loading Version: " + LICENSE_VERSION + " " + VERSION, 220, 585);
            splash.update();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
            for (int i = 0; i < 24; i++) {

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(211, 571, Math.min((i + 1) * 12, 289), 19);
                g.setColor(Color.BLACK);
                g.drawString("Loading Version: " + LICENSE_VERSION + " " + VERSION, 220, 585);
                splash.update();
                try {
                    Thread.sleep(42);
                } catch (InterruptedException e) {

                }
            }
        }

//        Calendar cal = Calendar.getInstance();
//        if (LICENSE_VERSION.contains("DEVELOP") || LICENSE_VERSION.contains("WZ")) {
//            if (splash != null) {
//                splash.close();
//            }
        MainWindow mainWindow = new MainWindow();
//        } else {
//            Date time = cal.getTime();
//            if (time.compareTo(EXPIRE) > 0) {
//                JOptionPane.showMessageDialog(null, "This FREEVAL is expired.", "Expired", JOptionPane.ERROR_MESSAGE);
//            } else {
//                //if (returnStatus) {
//                if (splash != null) {
//                    splash.close();
//                }
//                MainWindow mainWindow = new MainWindow();
//                //} else {
//                //    break;
//                //}
//                //verified = true;
//                long days = (EXPIRE.getTime() - time.getTime()) / 86400000;
//                if (days <= 15) {
//                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
//
//                    JOptionPane.showMessageDialog(null, "This FREEVAL will be expired after " + days + " days at " + format.format(EXPIRE)
//                            + " EST. Please keep copies of ASCII input files for your projects.", "Expire Soon", JOptionPane.INFORMATION_MESSAGE);
//                }
//            }
//        }
    }

    /**
     * Getter for initial directory for file choose
     *
     * @return initial directory as File
     */
    public static File getInitialDirectory() {
        try {
            return new File(FREEVAL_HCM.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            MainWindow.printLog(e.toString());
            return null;
        }
    }

    public static Boolean getAdaptiveRampMeteringAvailable() {
        return false;
    }
}
