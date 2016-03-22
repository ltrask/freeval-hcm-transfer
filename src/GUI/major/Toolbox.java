package GUI.major;

/**
 *
 * @author Shu Liu
 */
public abstract class Toolbox extends javax.swing.JPanel {

    public static final int SEED_CHANGE_NUM_PERIODS = 0;

    public static final int SEED_CHANGE_NUM_SEGMENTS = 1;

    public static final int SEED_CHANGE_INPUT_FIELD = 2;

    abstract public void setNullSeed();

    abstract public void setNonNullSeed();

    abstract public void setMainWindow(MainWindow mainWindow);

    abstract public void enableML();

    abstract public void disableML();

    abstract public void enableRL();

    abstract public void disableRL();

    abstract public void enableATDM();

    abstract public void disableATDM();

    abstract public void turnOnML();

    abstract public void turnOffML();

    abstract public void seedDataChanged(int changeType);

    /**
     * Method to help facilitate changes made to the GEOMETRY of the seed.
     * Allows for more control by providing more information about segment
     * addition or deletion.
     *
     * @param changeType Should be Toolbox.SEED_CHANGE_NUM_SEGMENTS
     * @param segChangeStart index of first segment changed (added/deleted)
     * @param segChangeEnd index of last segment changed (added/deleted)
     */
    abstract public void seedDataChanged(int changeType, int segChangeStart, int segChangeEnd);

    /**
     * Method provided to allow for certain actions to take place when exiting a
     * toolbox
     */
    abstract public void onLeavingToolbox();

    /**
     * Method provided to allow for certain actions to take place when entering
     * a toolbox
     */
    abstract public void onEnteringToolbox();

    abstract public void fillData();

    abstract public String getAddSegmentWarningString();

    abstract public String getDelSegmentWarningString();

    abstract public String getAddPeriodWarningString();

    abstract public String getDelPeriodWarningString();

}
