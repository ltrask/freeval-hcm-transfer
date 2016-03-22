package GUI.major;

import GUI.major.graphicHelper.GraphicColorSetting;
import GUI.major.graphicHelper.GraphicDisplayController;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import coreEngine.reliabilityAnalysis.DataStruct.IncidentEvent;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * This class provides graphic display for freeway.
 *
 * @author Shu Liu
 */
public class GraphicDisplay extends javax.swing.JPanel {

    // <editor-fold defaultstate="collapsed" desc="data and drawing control parameters">
    private MainWindow mainWindow;
    private GraphicDisplayController controller;

    //input data
    private Seed seed;

    private int scen;

    private int period;

    private int atdm;

    private boolean isShowInput = false;

    private int highlightSegIndex;

    private boolean paintLock = false;

    //draw parameters - base - generally in pixel
    private final static int HOR_EDGE = 20;

    private final static int VER_EDGE = 20;

    private final static int HEIGHT_PER_LANE = 15;

    private final static int GPML_SPACE = 5;

    private final static int WORK_ZONE_SHADE_SPACE = 7;

    private final static int WIDTH_PER_MILE = 150;

    private final static int SEPARATION_EXTENTION = 5;

    private final static int WIDTH_PER_RAMP = 15;

    private final static int DIAMOND_HEIGHT = 10;

    private final static int DIAMOND_WIDTH = 15;

    private final static int DIAMOND_SPACE = 30;

    private final static int ARROW_LENGTH = 20;

    private final static int ARROW_SIZE = 8;

    private final static int ZOOM_MAX = 500;

    private final static int ZOOM_MIN = 20;

    private final static float TILT_SCALE = 0.707f;

    //draw parameters - scaled
    private int horEdge;

    private int verEdge;

    private int heightPerLane;

    private int GPMLSpace;

    private int workZoneShadeSpace;

    private int widthPerMile;

    private int separationExtention;

    private int widthPerRamp;

    private int diamondHeight;

    private int diamondWidth;

    private int diamondSpace;

    private int arrowLength;

    private int arrowSize;

    private int zoomFactor = 0;

    private ArrayList<GraphicColorSetting> graphicColorSettings;

    //other parameters TBC
    private static BasicStroke LANE_SOLID = new BasicStroke(2.0f);

    private final static BasicStroke DIAMOND_SOLID = new BasicStroke(2.0f);

    private final static BasicStroke SEGMENT_DIVIDE_DASH = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f);
    //private final static BasicStroke SEPARATION_BARRIER_SOLID = new BasicStroke(3.0f);
    //private final static BasicStroke SEPARATION_BUFFER_DASH = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f, 10.0f}, 5.0f);

    private final static BasicStroke GP_ML_ACCESS_DASH = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 5.0f);

    //auto calculated parameters
    private int drawHeight;

    private int drawWidth;

    private int maxNumOfLanes = 0;

    private int maxNumOfMLLanes = 0;

    // Display Option Parameters
    private int[] shoulderOffset;
    //private boolean[][] sensorAtSegment;
    //private String[] sensorNames;
    private String SEGMENT_LABEL_STYLE = STYLE_SEGMENT_NUMBER;
    private boolean drawSegmentLabel = true;
    public static final String STYLE_SEGMENT_NUMBER = "STYLE_SEGMENT_NUMBER";
    //public static final String STYLE_SENSOR_NAME = "STYLE_SENSOR_NAME";
    // private boolean fillRampByQueue = false;
    private boolean showIncidents = true;
    //private boolean showWeather = true;

    private String SEGMENT_COLOR_STYLE = COLOR_BY_LOS;
    //private String SENSOR_COLOR_STYLE = COLOR_BY_LOS;
    public static final String COLOR_BY_LOS = "COLOR_BY_LOS";
    public static final String COLOR_BY_SPEED = "COLOR_BY_SPEED";
    public static final String COLOR_BY_USER_SPECIFIED_SOLID = "COLOR_BY_USER_SPECIFIED_SOLID";

    private Color userSpecifiedSegmentFillColor = Color.GRAY;
    private Color userSpecifiedIncidentFillColor = Color.DARK_GRAY;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="constructor and display control functions">
    /**
     * Creates new form GraphicDisplay
     */
    public GraphicDisplay() {
        initComponents();

        setZoom(100);

        setDefaultColorScheme();

        initComponents();
        drawHeight = 0;
        drawWidth = 0;
        highlightSegIndex = -1;
        //isShowInput = true;
        scen = 0;
        period = 0;
        atdm = -1;
    }

    /**
     * Reset the color scheme to default
     */
    public final void setDefaultColorScheme() {
        this.graphicColorSettings = new ArrayList<>();

        graphicColorSettings.add(new GraphicColorSetting("BG", "Background", Color.white));
        graphicColorSettings.add(new GraphicColorSetting("TEXT", "Text", Color.black));
        graphicColorSettings.add(new GraphicColorSetting("HL", "Highlight Segment", Color.lightGray));

        graphicColorSettings.add(new GraphicColorSetting("GP", "General Purpose Lane", Color.black));
        graphicColorSettings.add(new GraphicColorSetting("ML", "Managed Lane - line", Color.MAGENTA));
        graphicColorSettings.add(new GraphicColorSetting("DIAMOND", "Managed Lane - diamond", Color.MAGENTA));
        graphicColorSettings.add(new GraphicColorSetting("GPMLSP", "Separation - Buffer & Barrier", Color.black));
        graphicColorSettings.add(new GraphicColorSetting("WZ", "Work Zone Lane Closure", Color.orange));
        graphicColorSettings.add(new GraphicColorSetting("SHADE", "Work Zone Shade", Color.black));
        graphicColorSettings.add(new GraphicColorSetting("INC", "Incident Lane Closure", Color.darkGray));
        graphicColorSettings.add(new GraphicColorSetting("INC-US", "Incident Lane Closure - User Specified Color", userSpecifiedIncidentFillColor));
        graphicColorSettings.add(new GraphicColorSetting("SHOULDER_CLOSURE", "Shoulder Closure", Color.RED));
        graphicColorSettings.add(new GraphicColorSetting("SHOULDER_OPENING", "Hard Shoulder Open", new Color(0, 102, 0)));
        graphicColorSettings.add(new GraphicColorSetting("ARROW", "Entry Arrow", Color.black));

        graphicColorSettings.add(new GraphicColorSetting("A", "Level of Service: A", new Color(0f, 1f, 0f)));
        graphicColorSettings.add(new GraphicColorSetting("B", "Level of Service: B", new Color(0.33f, 1f, 0f)));
        graphicColorSettings.add(new GraphicColorSetting("C", "Level of Service: C", new Color(0.66f, 1f, 0f)));
        graphicColorSettings.add(new GraphicColorSetting("D", "Level of Service: D", new Color(1f, 1f, 0f)));
        graphicColorSettings.add(new GraphicColorSetting("E", "Level of Service: E", new Color(1f, 0.5f, 0f)));
        graphicColorSettings.add(new GraphicColorSetting("F", "Level of Service: F", new Color(1f, 0f, 0f)));
    }

    /**
     * Show data for a particular seed, scenario and period
     *
     * @param seed seed to be displayed
     * @param scen index of scenario to be displayed
     * @param atdm index of ATDM set to be displayed
     * @param period index of period to be displayed
     */
    public void selectSeedScenATDMPeriod(Seed seed, int scen, int atdm, int period) {
        this.seed = seed;
        this.scen = scen;
        this.atdm = atdm;
        this.period = period;
        calculateDrawSize();
        repaint();
    }

    /**
     * Highlight a particular segment
     *
     * @param seg segment index (start with 0) to be highlighted
     */
    public void setHighlight(int seg) {
        //set which segment to be highlighted
        if (highlightSegIndex != seg) {
            highlightSegIndex = seg;
            repaint();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="draw helper functions">
    @Override
    public void paint(Graphics g) {
        if (!paintLock) {
            paintLock = true;
            super.paint(g);

            g.setFont(new Font("Arial", Font.PLAIN, 17));

            setVisible(true);
            setBackground(findColor("BG"));

            if (seed != null) {
                //drawWeather((Graphics2D) g);
                drawFreeway((Graphics2D) g);
            }
            paintLock = false;
            //g.setColor(findColor("TEXT"));
            //g.drawString("Zoom " + zoomFactor + "%", 5, 20);
        }
    }

    /**
     * Refresh the graphic display
     */
    public void update() {
        calculateDrawSize();
        repaint();
    }

    private void calculateDrawSize() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            parent.revalidate();
        }

        if (seed != null) {
            float totalLength = seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI);

            maxNumOfLanes = 0;
            for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                maxNumOfLanes = Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm), maxNumOfLanes);
            }

            if (seed.isManagedLaneUsed()) {
                maxNumOfMLLanes = 0;
                for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    maxNumOfMLLanes = Math.max(seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm), maxNumOfMLLanes);
                }
                maxNumOfLanes += maxNumOfMLLanes;
            } else {
                maxNumOfMLLanes = 0;
            }

            drawHeight = maxNumOfLanes * heightPerLane + widthPerRamp + verEdge * 2;
            drawWidth = (int) (totalLength * widthPerMile) + horEdge * 2;

            if (maxNumOfMLLanes > 0) {
                drawHeight += GPMLSpace;
            }
        } else {
            drawHeight = 0;
            drawWidth = 0;
        }

        setPreferredSize(new Dimension(drawWidth, drawHeight));
    }

    private void fillWorkZone(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(findColor("WZ"));
        g.fillRect(x, y, width, height);

        //45 degree line
        g.setColor(findColor("SHADE"));
        g.setStroke(LANE_SOLID);
        for (int xDis = -height + workZoneShadeSpace; xDis <= width; xDis += workZoneShadeSpace) {
            g.drawLine(Math.max(x, x + xDis),
                    Math.max(y, y - xDis),
                    Math.min(x + xDis + height, x + width),
                    Math.min(y + height, y + width - xDis));
        }
    }

    private void fillBuffer(Graphics2D g, int x, int y, int width, int height) {
        //45 degree line
        g.setColor(findColor("GPMLSP"));
        g.setStroke(LANE_SOLID);
        for (int xDis = -height + workZoneShadeSpace; xDis <= width; xDis += workZoneShadeSpace) {
            g.drawLine(Math.max(x, x + xDis),
                    Math.max(y, y - xDis),
                    Math.min(x + xDis + height, x + width),
                    Math.min(y + height, y + width - xDis));
        }
    }

    private void drawFreeway(Graphics2D g) {

        //drawing sequence (from buttom to top)
        //Highlight ->
        //ML Output -> Separation -> ML Lanes ->
        //GP Output -> Work Zone / Incident Lane Closure -> GP Lanes ->
        //Segment Index -> Arrow
        int currX = (getSize().width - drawWidth) / 2 + horEdge;
        int currY = (getSize().height - drawHeight) / 2 + verEdge;
        shoulderOffset = new int[seed.getValueInt(CEConst.IDS_NUM_SEGMENT)];

        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {

            //draw highlight
            if (seg == highlightSegIndex) {
                g.setColor(findColor("HL"));
                g.fillRect(currX + 1, 0, (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1, getSize().height);
            }

            if (maxNumOfMLLanes > 0) {
                //draw ML output color
                if (!isShowInput) {
                    drawMLOutput(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                }

                //draw incident lane closure for ML segment
                if (showIncidents) {

                    if (seed.getValueInt(CEConst.IDS_ML_RL_LAF, seg, period, scen, atdm) < 0) {
                        g.setColor(findColor(SEGMENT_COLOR_STYLE.equals(COLOR_BY_USER_SPECIFIED_SOLID) ? "INC-US" : "INC"));
                        g.fillRect(currX + 1,
                                currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane,
                                (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                                -heightPerLane * seed.getValueInt(CEConst.IDS_ML_RL_LAF, seg, period, scen, atdm));
                    } else if (false) {
                        g.setColor(findColor("SHOULDER_CLOSURE"));
                        g.fillRect(currX + 1,
                                currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane,
                                (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                                -heightPerLane * Math.round(1 / 4.0f * heightPerLane));
                    }
                }

                //draw separation
                if (seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period) > 0) {
                    g.setColor(findColor("GPMLSP"));
                    switch (seed.getValueInt(CEConst.IDS_ML_SEPARATION_TYPE, seg)) {
                        case CEConst.ML_SEPARATION_BARRIER:
                            g.fillRect(currX,
                                    currY + maxNumOfMLLanes * heightPerLane,
                                    (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                                    GPMLSpace);
                            break;
                        case CEConst.ML_SEPARATION_BUFFER:
                            fillBuffer(g,
                                    currX,
                                    currY + maxNumOfMLLanes * heightPerLane,
                                    (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                                    GPMLSpace);
                            break;
                    }
                }

                //draw ML lanes
                g.setColor(findColor("ML"));//MAGENTA);
                switch (seed.getValueInt(CEConst.IDS_ML_SEGMENT_TYPE, seg)) {
                    case CEConst.SEG_TYPE_B:
                        drawMLBasicSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                    case CEConst.SEG_TYPE_R:
                        drawMLBasicSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                    case CEConst.SEG_TYPE_ONR:
                        drawMLOnRampSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                    case CEConst.SEG_TYPE_OFR:
                        drawMLOffRampSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                    case CEConst.SEG_TYPE_W:
                        drawMLWeavingSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                    case CEConst.SEG_TYPE_ACS:
                        drawMLAccessSegment(g, currX, currY + (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period)) * heightPerLane, seg);
                        break;
                }

                currY += maxNumOfMLLanes * heightPerLane + GPMLSpace;
            }

            //draw GP output color
            if (!isShowInput) {
                drawGPOutput(g, currX, currY, seg);
            }

            //draw work zone lane closure
            if (seed.getValueInt(CEConst.IDS_GP_RL_LAFWZ, seg, period, scen, atdm) < 0) {
                fillWorkZone(g,
                        currX + 1,
                        currY,
                        (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                        -heightPerLane * seed.getValueInt(CEConst.IDS_GP_RL_LAFWZ, seg, period, scen, atdm));
            } else {
                // Check if active work zone in seg/period
                for (coreEngine.reliabilityAnalysis.DataStruct.WorkZone wz : seed.getRLScenarioInfo().get(scen).getWorkZoneEventList()) {
                    if (wz.isActiveIn(seg, period) && wz.getSeverity() == 0) {
                        fillWorkZone(g,
                                currX + 1,
                                currY + Math.round(-heightPerLane * 1 / 2.0f),
                                (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                                Math.round(heightPerLane * 1 / 2.0f));
                    }
                }
            }

            //draw incident lane closure for GP segment
            if (showIncidents) {
                if (seed.getValueInt(CEConst.IDS_GP_RL_LAFI, seg, period, scen, atdm) < 0) {
                    g.setColor(findColor(SEGMENT_COLOR_STYLE.equals(COLOR_BY_USER_SPECIFIED_SOLID) ? "INC-US" : "INC"));
                    g.fillRect(currX + 1,
                            currY - heightPerLane * seed.getValueInt(CEConst.IDS_GP_RL_LAFWZ, seg, period, scen, atdm),
                            (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                            -heightPerLane * seed.getValueInt(CEConst.IDS_GP_RL_LAFI, seg, period, scen, atdm));
                } else {
                    boolean hasShoulderClosure = false;
                    for (IncidentEvent event : ((ScenarioInfo) seed.getRLScenarioInfo().get(scen)).getGPIncidentEventList()) {
                        if (event.severity == 0 && event.getSegment() == seg && event.checkActiveInPeriod(period)) {
                            hasShoulderClosure = true;
                            break;
                        }
                    }
                    if (hasShoulderClosure) {
                        g.setColor(findColor("SHOULDER_CLOSURE"));
                        g.fillRect(currX + 1,
                                currY - heightPerLane * seed.getValueInt(CEConst.IDS_GP_RL_LAFWZ, seg, period, scen, atdm) + Math.round(-heightPerLane * 1 / 2.0f) + shoulderOffset[seg],
                                (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                                Math.round(heightPerLane * 1 / 2.0f));
                        shoulderOffset[seg] += Math.round(-heightPerLane * 1 / 4.0f);
                    }
                }
            }

            // draw any shoulder opening (ATDM only)
            if (atdm >= 0) {
                if (seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN_AND_ATDM, seg, period, scen, atdm) > seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm)) {
                    g.setColor(findColor("SHOULDER_OPENING"));
                    g.fillRect(currX + 1,
                            currY - heightPerLane * seed.getValueInt(CEConst.IDS_GP_RL_LAFWZ, seg, period, scen, atdm) + Math.round(-heightPerLane * 1 / 2.0f) + shoulderOffset[seg],
                            (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                            Math.round(heightPerLane * 1 / 2.0f));
                    shoulderOffset[seg] += Math.round(-heightPerLane * 1 / 4.0f);
                }
            }

            //draw GP lanes
            g.setColor(findColor("GP"));
            switch (seed.getValueInt(CEConst.IDS_SEGMENT_TYPE, seg)) {
                case CEConst.SEG_TYPE_B:
                    drawBasicSegment(g, currX, currY, seg);
                    break;
                case CEConst.SEG_TYPE_R:
                    drawBasicSegment(g, currX, currY, seg);
                    break;
                case CEConst.SEG_TYPE_ONR:
                    drawOnRampSegment(g, currX, currY, seg);
                    break;
                case CEConst.SEG_TYPE_OFR:
                    drawOffRampSegment(g, currX, currY, seg);
                    break;
                case CEConst.SEG_TYPE_W:
                    drawWeavingSegment(g, currX, currY, seg);
                    break;
                case CEConst.SEG_TYPE_ACS:
                    drawAccessSegment(g, currX, currY, seg);
                    break;
            }

            //draw segment index
            g.setColor(findColor("TEXT"));
            if (drawSegmentLabel) {
                switch (SEGMENT_LABEL_STYLE) {
                    default:
                    case STYLE_SEGMENT_NUMBER:
                        g.drawString(Integer.toString(seg + 1),
                                currX + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile / 2) - (seg + 1 < 10 ? 4 : 8),
                                (getSize().height - drawHeight) / 2 + verEdge - 7 + shoulderOffset[seg]);
                        break;
                }
            }

            //move pen location
            currX += (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile);
            if (maxNumOfMLLanes > 0) {
                currY -= maxNumOfMLLanes * heightPerLane + GPMLSpace;
            }
        }

        //draw ML diamond mark
        if (maxNumOfMLLanes > 0) {
            currY = (getSize().height - drawHeight) / 2 + verEdge;

            for (int lane = maxNumOfMLLanes; lane >= 1; lane--) {
                currX = (getSize().width - drawWidth) / 2 + horEdge;

                int start = -1;
                int startX = 0;
                int length = 0;

                for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {

                    if (seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period) >= lane) {
                        if (start == -1) {
                            start = seg;
                            startX = currX;
                        }
                        length += seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg);
                    }

                    if (start > -1 && (seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period) < lane || seg == seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1)) {
                        drawDiamonds(g,
                                startX,
                                currY,
                                (int) (length / 5280.0 * widthPerMile),
                                startX % (diamondSpace + diamondWidth));
                        start = -1;
                        length = 0;
                    }

                    currX += (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile);
                }

                currY += heightPerLane;
            }
        }

        currX = (getSize().width - drawWidth) / 2 + horEdge;
        currY = (getSize().height - drawHeight) / 2 + verEdge;
        drawArrows(g, currX, currY);
    }

    private void drawArrows(Graphics2D g, int x, int y) {
        int currY = y;

        if (maxNumOfMLLanes > 0) {
            currY += (maxNumOfMLLanes - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, 0)) * heightPerLane;

            for (int i = 0; i < seed.getValueInt(CEConst.IDS_ML_NUM_LANES, 0); i++) {
                drawArrow(g, x, currY);
                currY += heightPerLane;
            }

            currY += GPMLSpace;
        }

        for (int i = 0; i < seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, 0); i++) {
            drawArrow(g, x, currY);
            currY += heightPerLane;
        }
    }

    private void drawArrow(Graphics2D g, int x, int y) {
        g.setStroke(LANE_SOLID);
        g.setColor(findColor("ARROW"));

        g.drawLine(x + arrowLength, y + heightPerLane / 2, x, y + heightPerLane / 2);

        int[] xPoints = new int[]{x + arrowLength, x - arrowSize + arrowLength, x - arrowSize + arrowLength};
        int[] yPoints = new int[]{y + heightPerLane / 2, y + heightPerLane / 2 + arrowSize / 2, y + heightPerLane / 2 - arrowSize / 2};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawDiamonds(Graphics2D g, int x, int y, int width, int offset) {
        g.setColor(findColor("DIAMOND"));
        g.setStroke(DIAMOND_SOLID);

        for (int currX = x + (diamondSpace + diamondWidth / 2) - offset; currX <= x + width - diamondWidth; currX += diamondSpace + diamondWidth) {
            drawSingleDiamond(g, currX, y);
        }
    }

    private void drawSingleDiamond(Graphics2D g, int x, int y) {
        int[] xPoints = new int[]{x, x + diamondWidth / 2, x + diamondWidth, x + diamondWidth / 2};
        int[] yPoints = new int[]{y + heightPerLane / 2, y + (heightPerLane - diamondHeight) / 2, y + heightPerLane / 2, y + (heightPerLane + diamondHeight) / 2};
        g.drawPolygon(xPoints, yPoints, 4);
    }

    private void highlightClickedSegment(java.awt.event.MouseEvent evt) {
        highlightSegIndex = -1;

        if (seed != null && seed.getValueInt(CEConst.IDS_NUM_SEGMENT) >= 1) {
            int highlightPositionX = evt.getX();

            if (highlightPositionX >= (getSize().width - drawWidth) / 2 + horEdge && highlightPositionX <= (getSize().width - drawWidth) / 2 + drawWidth - horEdge) {
                int currX = (getSize().width - drawWidth) / 2 + horEdge;
                int nextX;
                for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    nextX = currX + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile);
                    if (highlightPositionX >= currX && highlightPositionX <= nextX) {
                        highlightSegIndex = seg;
                        //mainWindow.printLog("Segment " + highlightSegIndex + " highlighted in graph");
                        break;
                    }
                    currX = nextX;
                }
            }
            mainWindow.segmentSelectedByGraph(highlightSegIndex);
            if (controller != null) {
                controller.segmentHighlightedByGraphicDisplay(highlightSegIndex);
            }
        }

        repaint();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="general purpose drawing helper functions">
    private void drawBasicSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention,
                    x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm), seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg + 1, period, scen, atdm)) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //mainline
        for (int laneIndex = 0; laneIndex <= seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
    }

    private void drawAccessSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention,
                    x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm), seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg + 1, period, scen, atdm)) + separationExtention);
        }

        //access dash line
        g.setStroke(GP_ML_ACCESS_DASH);
        g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
        y += heightPerLane;

        //mainline
        g.setStroke(LANE_SOLID);
        for (int laneIndex = 1; laneIndex <= seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
    }

    private void drawOnRampSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention,
                    x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm),
                            seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg + 1, period, scen, atdm)) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //mainline
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
        g.drawLine(x + widthPerRamp, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);

        //on ramp
        g.drawLine(x, y,
                x - widthPerRamp, y + widthPerRamp);
        g.drawLine(x + widthPerRamp, y,
                x, y + widthPerRamp);
    }

    private void drawOffRampSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention,
                    x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm), seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg + 1, period, scen, atdm)) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //mainline
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
        g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y);

        //off ramp
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y,
                x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                y + widthPerRamp);
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y,
                x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) + widthPerRamp,
                y + widthPerRamp);
    }

    private void drawWeavingSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention,
                    x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * Math.max(seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm), seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg + 1, period, scen, atdm)) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //mainline
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
        g.drawLine(x + widthPerRamp, y, x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y);

        //on ramp
        g.drawLine(x, y,
                x - widthPerRamp, y + widthPerRamp);
        g.drawLine(x + widthPerRamp, y,
                x, y + widthPerRamp);

        //off ramp
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y,
                x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                y + widthPerRamp);
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y,
                x + (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) + widthPerRamp,
                y + widthPerRamp);
    }

    private void drawGPOutput(Graphics2D g, int currX, int currY, int seg) {
        if (scen < 0 || scen > seed.getValueInt(CEConst.IDS_NUM_SCEN)) {
            mainWindow.printLog("Debug: Error when draw output");
        } else {
            switch (SEGMENT_COLOR_STYLE) {
                case COLOR_BY_SPEED:
                    g.setColor(getSegmentColorBySpeed(seg));
                    break;
                default:
                case COLOR_BY_LOS:
                    g.setColor(
                            findColor(seed.getValueString(CEConst.IDS_DENSITY_BASED_LOS, seg, period, scen, atdm).toUpperCase().substring(0, 1)));
                    break;
                case COLOR_BY_USER_SPECIFIED_SOLID:
                    g.setColor(userSpecifiedSegmentFillColor);
                    break;
            }
            g.fillRect(currX + 1, currY, (int) (seed.getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                    seed.getValueInt(CEConst.IDS_MAIN_NUM_LANES_IN, seg, period, scen, atdm) * heightPerLane);
        }
    }

    private Color findColor(String header) {
        for (GraphicColorSetting setting : graphicColorSettings) {
            if (setting.header.equals(header)) {
                return setting.bgColor;
            }
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="managed lanes drawing helper functions">
    private void drawMLBasicSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention - heightPerLane * Math.max(0,
                            seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg + 1, period, scen, atdm) - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm)),
                    x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //mainline
        for (int laneIndex = 0; laneIndex <= seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }
    }

    private void drawMLAccessSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention - heightPerLane * Math.max(0,
                            seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg + 1, period, scen, atdm) - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm)),
                    x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) + separationExtention);
        }

        //mainline
        g.setStroke(LANE_SOLID);
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm); laneIndex++) {
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
            y += heightPerLane;
        }

        //access dash line
        g.setStroke(GP_ML_ACCESS_DASH);
        g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
    }

    private void drawMLOnRampSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention - heightPerLane * Math.max(0,
                            seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg + 1, period, scen, atdm) - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm)),
                    x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //on ramp
        g.drawLine(x, y,
                x - widthPerRamp, y - widthPerRamp);
        g.drawLine(x + widthPerRamp, y,
                x, y - widthPerRamp);

        //mainline
        g.drawLine(x + widthPerRamp, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm); laneIndex++) {
            y += heightPerLane;
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
        }
    }

    private void drawMLOffRampSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention - heightPerLane * Math.max(0,
                            seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg + 1, period, scen, atdm) - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm)),
                    x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //off ramp
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y,
                x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                y - widthPerRamp);
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y,
                x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) + widthPerRamp,
                y - widthPerRamp);

        //mainline
        g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y);
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm); laneIndex++) {
            y += heightPerLane;
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
        }
    }

    private void drawMLWeavingSegment(Graphics2D g, int currX, int currY, int seg) {
        int x = currX;
        int y = currY;

        //separation
        g.setStroke(SEGMENT_DIVIDE_DASH);
        if (seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
            g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y - separationExtention - heightPerLane * Math.max(0,
                            seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg + 1, period, scen, atdm) - seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm)),
                    x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                    y + heightPerLane * seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) + separationExtention);
        }

        g.setStroke(LANE_SOLID);
        //on ramp
        g.drawLine(x, y,
                x - widthPerRamp, y - widthPerRamp);
        g.drawLine(x + widthPerRamp, y,
                x, y - widthPerRamp);

        //off ramp
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y,
                x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile),
                y - widthPerRamp);
        g.drawLine(x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y,
                x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) + widthPerRamp,
                y - widthPerRamp);

        //mainline
        g.drawLine(x + widthPerRamp, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - widthPerRamp, y);
        for (int laneIndex = 0; laneIndex < seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm); laneIndex++) {
            y += heightPerLane;
            g.drawLine(x, y, x + (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile), y);
        }

    }

    private void drawMLOutput(Graphics2D g, int currX, int currY, int seg) {
        if (scen < 0 || scen > seed.getValueInt(CEConst.IDS_NUM_SCEN)) {
            mainWindow.printLog("Debug: Error when draw output");
        } else {
            //g.setColor(findColor(seed.getValueString(CEConst.IDS_ML_DENSITY_BASED_LOS, seg, period, scen, atdm).toUpperCase().substring(0, 1)));
            switch (SEGMENT_COLOR_STYLE) {
                case COLOR_BY_SPEED:
                    g.setColor(getSegmentColorBySpeed(seg));
                    break;
                default:
                case COLOR_BY_LOS:
                    g.setColor(
                            findColor(seed.getValueString(CEConst.IDS_DENSITY_BASED_LOS, seg, period, scen, atdm).toUpperCase().substring(0, 1)));
                    break;
                case COLOR_BY_USER_SPECIFIED_SOLID:
                    g.setColor(userSpecifiedSegmentFillColor);
                    break;
            }
            g.fillRect(currX + 1, currY, (int) (seed.getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280.0 * widthPerMile) - 1,
                    seed.getValueInt(CEConst.IDS_ML_NUM_LANES, seg, period, scen, atdm) * heightPerLane);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setter and getters">
    /**
     * Setter for mainWindow connection
     *
     * @param mainWindow main window instance
     */
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Setter for a non mainWindow controller connection that will be updated
     * along with the MainWindow for things like segment highlighting, etc.
     *
     * @param controller
     */
    public void setGraphicDisplayController(GraphicDisplayController controller) {
        this.controller = controller;
    }

    /**
     * Getter for horizontal edge (pixel)
     *
     * @return horizontal edge (pixel)
     */
    public int getHorEdge() {
        return horEdge;
    }

    /**
     * Setter for horizontal edge (pixel)
     *
     * @param horEdge horizontal edge (pixel)
     */
    public void setHorEdge(int horEdge) {
        this.horEdge = horEdge;
    }

    /**
     * Getter for vertical edge (pixel)
     *
     * @return vertical edge (pixel)
     */
    public int getVerEdge() {
        return verEdge;
    }

    /**
     * Setter for vertical edge (pixel)
     *
     * @param verEdge vertical edge (pixel)
     */
    public void setVerEdge(int verEdge) {
        this.verEdge = verEdge;
    }

    /**
     * Getter for height per lane (pixel)
     *
     * @return height per lane (pixel)
     */
    public int getHeightPerLane() {
        return heightPerLane;
    }

    /**
     * Setter for height per lane (pixel)
     *
     * @param heightPerLane height per lane (pixel)
     */
    public void setHeightPerLane(int heightPerLane) {
        this.heightPerLane = heightPerLane;
    }

    /**
     * Getter for width per lane (pixel)
     *
     * @return width per lane (pixel)
     */
    public int getWidthPerMile() {
        return widthPerMile;
    }

    /**
     * Setter for width per lane (pixel)
     *
     * @param widthPerMile width per lane (pixel)
     */
    public void setWidthPerMile(int widthPerMile) {
        this.widthPerMile = widthPerMile;
    }

    /**
     * Getter for extention of segment separation dash line (pixel)
     *
     * @return extention of segment separation dash line (pixel)
     */
    public int getSeparationExtention() {
        return separationExtention;
    }

    /**
     * Setter for extention of segment separation dash line (pixel)
     *
     * @param separationExtention extention of segment separation dash line
     * (pixel)
     */
    public void setSeparationExtention(int separationExtention) {
        this.separationExtention = separationExtention;
    }

    /**
     * Getter for width per ramp (pixel)
     *
     * @return width per ramp (pixel)
     */
    public int getWidthPerRamp() {
        return widthPerRamp;
    }

    /**
     * Setter for width per ramp (pixel)
     *
     * @param widthPerRamp width per ramp (pixel)
     */
    public void setWidthPerRamp(int widthPerRamp) {
        this.widthPerRamp = widthPerRamp;
    }

    /**
     * Getter for tile scale ratio
     *
     * @return tile scale ratio
     */
    public float getTiltScale() {
        return TILT_SCALE;
    }

    /**
     * Getter for level of service scale colors
     *
     * @return level of service scale colors
     */
    public ArrayList<GraphicColorSetting> getScaleColors() {
        return graphicColorSettings;
    }

    /**
     * Setter for level of service scale colors
     *
     * @param scaleColors level of service scale colors
     */
    public void setScaleColors(ArrayList<GraphicColorSetting> scaleColors) {
        if (scaleColors != null) {
            this.graphicColorSettings = scaleColors;
        }
    }

    /**
     * Getter for space between GP and ML
     *
     * @return space between GP and ML
     */
    public int getGPMLSpace() {
        return GPMLSpace;
    }

    /**
     * Setter for space between GP and ML
     *
     * @param GPMLSpace space between GP and ML
     */
    public void setGPMLSpace(int GPMLSpace) {
        this.GPMLSpace = GPMLSpace;
    }

    /**
     * Get zoom factor
     *
     * @return zoom factor (%)
     */
    public int getZoomFactor() {
        return zoomFactor;
    }

    /**
     * Set zoom factor of the graphic display
     *
     * @param zoomFactor new zoom factor (%)
     */
    public final void setZoom(int zoomFactor) {
        if (this.zoomFactor != zoomFactor) {
            this.zoomFactor = Math.min(Math.max(ZOOM_MIN, zoomFactor), ZOOM_MAX);
            horEdge = (int) (HOR_EDGE * this.zoomFactor / 100f);
            verEdge = (int) (VER_EDGE * this.zoomFactor / 100f);
            heightPerLane = (int) (HEIGHT_PER_LANE * this.zoomFactor / 100f);
            GPMLSpace = (int) (GPML_SPACE * this.zoomFactor / 100f);
            workZoneShadeSpace = (int) (WORK_ZONE_SHADE_SPACE * this.zoomFactor / 100f);
            widthPerMile = (int) (WIDTH_PER_MILE * this.zoomFactor / 100f);
            separationExtention = (int) (SEPARATION_EXTENTION * this.zoomFactor / 100f);
            widthPerRamp = (int) (WIDTH_PER_RAMP * this.zoomFactor / 100f);
            diamondHeight = (int) (DIAMOND_HEIGHT * this.zoomFactor / 100f);
            diamondWidth = (int) (DIAMOND_WIDTH * this.zoomFactor / 100f);
            diamondSpace = (int) (DIAMOND_SPACE * this.zoomFactor / 100f);
            arrowLength = (int) (ARROW_LENGTH * this.zoomFactor / 100f);
            arrowSize = (int) (ARROW_SIZE * this.zoomFactor / 100f);
            calculateDrawSize();
            repaint();
        }
    }

    /**
     * Sets whether incidents lane closures are displayed as a black blocked
     * lane.
     *
     * @param showIncidents true if incidents are shown, false otherwise.
     */
    public void showIncidents(boolean showIncidents) {
        this.showIncidents = showIncidents;
    }

    /**
     * Sets the segment color scheme for traffic congestion on the facility.
     *
     * @param colorStyle Style to use when coloring the segments.
     */
    public void setSegmentColorStyle(String colorStyle) {
        SEGMENT_COLOR_STYLE = colorStyle;
    }

    /**
     * Sets the user specified segment fill color for the
     * COLOR_BY_USER_SPECIFIED_SOLID color style.
     *
     * @param color
     */
    public void setUserSpecifiedSegmentFillColor(Color color) {
        userSpecifiedSegmentFillColor = color;
    }

    /**
     * Sets the user specified incident fill color for the
     * COLOR_BY_USER_SPECIFIED_SOLID color style.
     *
     * @param color
     */
    public void setUserSpecifiedIncidentFillColor(Color color) {
        userSpecifiedIncidentFillColor = color;
        for (GraphicColorSetting setting : graphicColorSettings) {
            if (setting.header.equals("INC-US")) {
                setting.bgColor = userSpecifiedIncidentFillColor;
                break;
            }
        }
    }

    /**
     * Returns the segment color based on the speed in the segment.
     *
     * @param segment
     * @return
     */
    private Color getSegmentColorBySpeed(int segment) {
        float segSpeed = seed.getValueFloat(CEConst.IDS_SPEED, segment, period, scen, atdm);
        float maxValFFS = seed.getValueInt(CEConst.IDS_MAIN_FREE_FLOW_SPEED, segment);
        float minValFFS = (1 / 3.0f) * maxValFFS;
        float midValFFS = (maxValFFS + minValFFS) / 2.0f;
        if (segSpeed < midValFFS) {
            float p = (Math.max(segSpeed - minValFFS, 0.0f)) / (midValFFS - minValFFS);
            return new Color((Color.red.getRed() / 255.0f) * (1.0f - p) + (Color.yellow.getRed() / 255.0f) * p,
                    (Color.red.getGreen() / 255.0f) * (1.0f - p) + (Color.yellow.getGreen() / 255.0f) * p,
                    (Color.red.getBlue() / 255.0f) * (1.0f - p) + (Color.yellow.getBlue() / 255.0f) * p,
                    0.7f);
        } else {
            float p = (Math.min(segSpeed, maxValFFS) - midValFFS) / (maxValFFS - midValFFS);
            return new Color((Color.yellow.getRed() / 255.0f) * (1.0f - p) + (Color.green.getRed() / 255.0f) * p,
                    (Color.yellow.getGreen() / 255.0f) * (1.0f - p) + (Color.green.getGreen() / 255.0f) * p,
                    (Color.yellow.getBlue() / 255.0f) * (1.0f - p) + (Color.green.getBlue() / 255.0f) * p,
                    0.7f);
        }
    }

    public void setSegmentLabelStyle(String segmentLabelStyle) {
        this.SEGMENT_LABEL_STYLE = segmentLabelStyle;
    }

    public void setDrawSegmentLabel(boolean drawSegmentLabel) {
        this.drawSegmentLabel = drawSegmentLabel;
    }

// </editor-fold>
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 628, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            highlightClickedSegment(evt);
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            final java.awt.event.ActionListener settingActionListener = new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MainWindow.showGraphicSettings();
                }
            };

            JPopupMenu menu = new JPopupMenu();
            JMenuItem settingMenuItem = new JMenuItem("Graphic Settings");
            settingMenuItem.addActionListener(settingActionListener);
            menu.add(settingMenuItem);
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        setZoom(zoomFactor + evt.getWheelRotation() * 10);
    }//GEN-LAST:event_formMouseWheelMoved

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public static void setLaneStrokeSize(float strokeSize) {
        LANE_SOLID = new BasicStroke(strokeSize);
    }

}
