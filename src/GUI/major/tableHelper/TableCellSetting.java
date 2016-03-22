package GUI.major.tableHelper;

import java.awt.Color;
import java.io.Serializable;

/**
 * This class is the cell setting for segment input/output table One cell
 * setting is used for one row
 *
 * @author Shu Liu
 */
public class TableCellSetting implements Serializable {

    private static final long serialVersionUID = 8759107156142468463L;

    /**
     * Header of a row
     */
    public String header;

    /**
     * Identifier of a row
     */
    public String identifier;

    /**
     * Whether this row is shown in input
     */
    public boolean showInInput;

    /**
     * Whether this row is shown in output
     */
    public boolean showInOutput;

    /**
     * Whether this row is shown in GP display
     */
    public boolean showInGP;

    /**
     * Whether this row is shown in ML display
     */
    public boolean showInML;

    /**
     * Whether this row is editable
     */
    public boolean editable;

    /**
     * Background color of this row
     */
    public Color bgColor;

    /**
     * Constructor of CellSetting class
     *
     * @param header first column for this row
     * @param identifier identifier String of this row
     * @param showInInput whether this row should display in input
     * @param showInOutput whether this row should display in output
     * @param color cell background color of this row
     * @param showInGP whether this row is shown in GP display
     * @param showInML whether this row is shown in ML display
     * @param editable whether this row is editable
     */
    public TableCellSetting(String header, String identifier,
            boolean showInInput, boolean showInOutput, Color color, boolean showInGP, boolean showInML, boolean editable) {
        this.header = header;
        this.identifier = identifier;
        this.showInInput = showInInput;
        this.showInOutput = showInOutput;
        this.bgColor = color;
        this.showInGP = showInGP;
        this.showInML = showInML;
        this.editable = editable;
    }

    @Override
    public TableCellSetting clone() {
        return new TableCellSetting(header, identifier, showInInput, showInOutput, bgColor, showInGP, showInML, editable);
    }
}
