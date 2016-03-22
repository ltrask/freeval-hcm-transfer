package GUI.major.tableHelper;

/**
 * Interface for two table model with same setting
 *
 * @author Shu Liu
 */
public interface FREEVAL_TableWithSetting {

    /**
     * Getter for first column table
     *
     * @return first column table
     */
    public FREEVAL_JTable getFirstColumnTable();

    /**
     * Getter for rest column table
     *
     * @return rest column table
     */
    public FREEVAL_JTable getRestColumnTable();
}
