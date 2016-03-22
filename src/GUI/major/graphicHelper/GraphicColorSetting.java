package GUI.major.graphicHelper;

import java.awt.Color;
import java.io.Serializable;

/**
 * This class is used for graphic color setting
 *
 * @author Shu Liu
 */
public class GraphicColorSetting implements Serializable {

    private static final long serialVersionUID = 645235423423362169L;

    /**
     * Header of a row
     */
    public String header;

    /**
     * Display name
     */
    public String displayName;

    /**
     * Background color of this row
     */
    public Color bgColor;

    /**
     * Constructor of CellSetting class
     *
     * @param header first column for this row
     * @param displayName display name for this row
     * @param color cell background color of this row
     */
    public GraphicColorSetting(String header, String displayName, Color color) {
        this.header = header;
        this.displayName = displayName;
        this.bgColor = color;
    }

    @Override
    public GraphicColorSetting clone() {
        return new GraphicColorSetting(header, displayName, bgColor);
    }
}
