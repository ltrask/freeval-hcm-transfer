package coreEngine.Helper.ASCIIAdapter;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CEDate;
import coreEngine.Helper.CETime;
import coreEngine.Seed;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import main.FREEVAL_HCM;

/**
 * This class import/export a Seed instance from/to an ASCII file using better
 * format. The ASCII(text) input file contains seed input data for GP and ML
 * segments, but RL and ATDM are not included.
 *
 * @author Shu Liu
 */
public class ASCIISeedFileAdapter_GPMLFormat {

    /**
     * The output formatter
     */
    private static final DecimalFormat formatter = new DecimalFormat("0.0#");

    /**
     * Data type marker
     */
    private static final int TIME_DEPENDENT = 0;

    /**
     * Data type marker
     */
    private static final int TIME_INDEPENDENT = 1;

    /**
     * Data type marker
     */
    private static final int GENERAL_INFO = 2;

    /**
     * Data type marker
     */
    private static final int INTEGER = 0;

    /**
     * Data type marker
     */
    private static final int FLOAT = 1;

    /**
     * Data type marker
     */
    private static final int BOOLEAN = 2;

    /**
     * Data type marker
     */
    private static final int STRING = 3;

    /**
     * Data type marker
     */
    private static final int OTHER = 4;

    /**
     * Format marker
     */
    private static final int ITEM_WIDTH = 6;

    /**
     * Map to convert id to header
     */
    private final HashMap<String, String> idToHeaderMap = new HashMap();

    // <editor-fold defaultstate="collapsed" desc="HEADER ID">
    // <editor-fold defaultstate="collapsed" desc="GLOBAL INPUT">
    /**
     * Item ID constant
     */
    private static final String ID_PROJECT_NAME = "001";

    /**
     * Item ID constant
     */
    private final static String ID_START = "002";

    /**
     * Item ID constant
     */
    private final static String ID_END = "003";

    /**
     * Item ID constant
     */
    private static final String ID_NUM_SEGMENTS = "004";

    /**
     * Item ID constant
     */
    private static final String ID_FFS_KNOWN = "005";

    /**
     * Item ID constant
     */
    private static final String ID_ML_USED = "007";

    /**
     * Item ID constant
     */
    private final static String ID_ALPHA = "008";

    /**
     * Item ID constant
     */
    private final static String ID_JAM_DENSITY = "009";

    /**
     * Item ID constant
     */
    private final static String ID_SEED_DEMAND_DATE = "010";

    /**
     * Item ID constant
     */
    private final static String ID_GP_OCCU = "011";

    /**
     * Item ID constant
     */
    private final static String ID_ML_OCCU = "012";

    /**
     * Item ID constant
     */
    private final static String ID_URBAN_RURAL = "013";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GP HEADER">
    // Basic Segment Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_SEGMENT_TYPE = "101";

    /**
     * Item ID constant
     */
    private final static String ID_SEGMENT_LENGTH = "102";

    /**
     * Item ID constant
     */
    private final static String ID_SEGMENT_WIDTH = "103";

    /**
     * Item ID constant
     */
    private final static String ID_LATERAL_CLEARANCE = "104";

    /**
     * Item ID constant
     */
    private final static String ID_TERRAIN = "105";

    /**
     * Item ID constant
     */
    private final static String ID_TRUCK_CAR_EQ = "106";

    /**
     * Item ID constant
     */
    private final static String ID_NUM_LANES = "108";

    /**
     * Item ID constant
     */
    private final static String ID_FREE_FLOW_SPEED = "109";

    /**
     * Item ID constant
     */
    private final static String ID_DEMAND_VEH = "110";

    /**
     * Item ID constant
     */
    private final static String ID_TRUCK_SINGLE_PERCENTAGE = "111";

    /**
     * Item ID constant
     */
    private final static String ID_TRUCK_TRAILER_PERCENTAGE = "112";

    /**
     * Item ID constant
     */
    private final static String ID_U_CAF = "113";

    /**
     * Item ID constant
     */
    private final static String ID_U_OAF = "114";

    /**
     * Item ID constant
     */
    private final static String ID_U_DAF = "115";

    /**
     * Item ID constant
     */
    private final static String ID_U_SAF = "116";

    // ONR Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_ON_RAMP_SIDE = "117";

    /**
     * Item ID constant
     */
    private final static String ID_ACC_DEC_LANE_LENGTH = "118";

    /**
     * Item ID constant
     */
    private final static String ID_NUM_ON_RAMP_LANES = "119";

    /**
     * Item ID constant
     */
    private final static String ID_ON_RAMP_DEMAND_VEH = "121";

    /**
     * Item ID constant
     */
    private final static String ID_ON_RAMP_FREE_FLOW_SPEED = "120";

    /**
     * Item ID constant
     */
    private final static String ID_ON_RAMP_METERING_RATE = "122";

    // OFR Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_OFF_RAMP_SIDE = "123";

    /**
     * Item ID constant
     */
    private final static String ID_NUM_OFF_RAMP_LANES = "124";

    /**
     * Item ID constant
     */
    private final static String ID_OFF_RAMP_DEMAND_VEH = "126";

    /**
     * Item ID constant
     */
    private final static String ID_OFF_RAMP_FREE_FLOW_SPEED = "125";

    // Weaving Segment Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_LENGTH_OF_WEAVING = "127";

    /**
     * Item ID constant
     */
    private final static String ID_MIN_LANE_CHANGE_ONR_TO_FRWY = "128";

    /**
     * Item ID constant
     */
    private final static String ID_MIN_LANE_CHANGE_FRWY_TO_OFR = "129";

    /**
     * Item ID constant
     */
    private final static String ID_MIN_LANE_CHANGE_ONR_TO_OFR = "130";

    /**
     * Item ID constant
     */
    private final static String ID_NUM_LANES_WEAVING = "131";

    /**
     * Item ID constant
     */
    private final static String ID_RAMP_TO_RAMP_DEMAND_VEH = "132";

    /**
     * Item ID constant
     */
    private static final String ID_RAMP_METERING = "133";

    /**
     * Item ID constant
     */
    private final static String ID_U_DPCAF = "134";

    /**
     * Item ID constant
     */
    private final static String ID_U_DPSAF = "135";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ML HEADER">
    // Basic Segment Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_ML_SEGMENT_TYPE = "201";

    /**
     * Item ID constant
     */
    private final static String ID_ML_SEPARATION_TYPE = "203";

    /**
     * Item ID constant
     */
    private final static String ID_ML_NUM_LANES = "204";

    /**
     * Item ID constant
     */
    private final static String ID_ML_FREE_FLOW_SPEED = "205";

    /**
     * Item ID constant
     */
    private final static String ID_ML_DEMAND_VEH = "206";

    /**
     * Item ID constant
     */
    private final static String ID_ML_TRUCK_SINGLE_PERCENTAGE = "207";

    /**
     * Item ID constant
     */
    private final static String ID_ML_TRUCK_TRAILER_PERCENTAGE = "208";

    /**
     * Item ID constant
     */
    private final static String ID_ML_UCAF = "209";

    /**
     * Item ID constant
     */
    private final static String ID_ML_UOAF = "210";

    /**
     * Item ID constant
     */
    private final static String ID_ML_UDAF = "211";

    /**
     * Item ID constant
     */
    private final static String ID_ML_USAF = "212";

    /**
     * Item ID constant
     */
    private final static String ID_ML_ACC_DEC_LANE_LENGTH = "213";

    // ONR Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_ML_ON_RAMP_SIDE = "214";

    /**
     * Item ID constant
     */
    private final static String ID_ML_NUM_ON_RAMP_LANES = "215";

    /**
     * Item ID constant
     */
    private final static String ID_ML_ON_RAMP_DEMAND_VEH = "216";

    /**
     * Item ID constant
     */
    private final static String ID_ML_ON_RAMP_FREE_FLOW_SPEED = "217";

    // OFR Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_ML_OFF_RAMP_SIDE = "218";

    /**
     * Item ID constant
     */
    private final static String ID_ML_NUM_OFF_RAMP_LANES = "219";

    /**
     * Item ID constant
     */
    private final static String ID_ML_OFF_RAMP_DEMAND_VEH = "220";

    /**
     * Item ID constant
     */
    private final static String ID_ML_OFF_RAMP_FREE_FLOW_SPEED = "221";

    // Weaving Segment Variable Column Text
    /**
     * Item ID constant
     */
    private final static String ID_ML_LENGTH_SHORT = "222";

    /**
     * Item ID constant
     */
    private final static String ID_ML_MIN_LANE_CHANGE_ONR_TO_FRWY = "223";

    /**
     * Item ID constant
     */
    private final static String ID_ML_MIN_LANE_CHANGE_FRWY_TO_OFR = "224";

    /**
     * Item ID constant
     */
    private final static String ID_ML_MIN_LANE_CHANGE_ONR_TO_OFR = "225";

    /**
     * Item ID constant
     */
    private final static String ID_ML_NUM_LANES_WEAVING = "226";

    /**
     * Item ID constant
     */
    private final static String ID_ML_LC_MIN = "227";

    /**
     * Item ID constant
     */
    private final static String ID_ML_LC_MAX = "228";

    /**
     * Item ID constant
     */
    private final static String ID_ML_RAMP_TO_RAMP_DEMAND_VEH = "229";

    /**
     * Item ID constant
     */
    private final static String ID_ML_HAS_CROSS_WEAVE = "230";

    /**
     * Item ID constant
     */
    private final static String ID_ML_CROSS_WEAVE_LC_MIN = "231";

    /**
     * Item ID constant
     */
    private final static String ID_ML_CROSS_WEAVE_VOLUME = "232";
    // </editor-fold>
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="HEADER CONSTANT">
    // <editor-fold defaultstate="collapsed" desc="GLOBAL INPUT">
    /**
     * Item header constant
     */
    private static final String STR_PROJECT_NAME = "Project Name";

    /**
     * Item header constant
     */
    private final static String STR_START = "Study Period Start Time (HH:MM)";

    /**
     * Item header constant
     */
    private final static String STR_END = "Study Period End Time (HH:MM)";

    /**
     * Item header constant
     */
    private static final String STR_NUM_SEGMENTS = "# of Segments";

    /**
     * Item header constant
     */
    private static final String STR_FFS_KNOWN = "Free Flow Speed Known?";

    /**
     * Item header constant
     */
    private static final String STR_RAMP_METERING = "Ramp Metering Used?";

    /**
     * Item header constant
     */
    private static final String STR_ML_USED = "Managed Lane Used?";

    /**
     * Item header constant
     */
    private final static String STR_ALPHA = "Capacity Drop (%)";

    /**
     * Item header constant
     */
    private final static String STR_JAM_DENSITY = "Jam Density (pc/mi/ln)";

    /**
     * Item header constant
     */
    private final static String STR_SEED_DEMAND_DATE = "Seed Demand Date (YYYY-MM-DD)";

    /**
     * Item header constant
     */
    private final static String STR_GP_OCCU = "GP Segment Vehicle Occupancy (p/veh)";

    /**
     * Item header constant
     */
    private final static String STR_ML_OCCU = "ML Segment Vehicle Occupancy (p/veh)";

    /**
     * Item header constant
     */
    private final static String STR_URBAN_RURAL = "Urban(= 1)/Rural(= 2) Type";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GP HEADER">
    // Basic Segment Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_SEGMENT_TYPE = "General Purpose Segment Type (Basic = 0, ONR = 1, OFR = 2, Overlap = 3, Weaving = 4, Access = 8)";

    /**
     * Item header constant
     */
    private final static String STR_SEGMENT_LENGTH = "Segment Length (ft)";

    /**
     * Item header constant
     */
    private final static String STR_SEGMENT_WIDTH = "Lane Width (ft)";

    /**
     * Item header constant
     */
    private final static String STR_LATERAL_CLEARANCE = "Lateral Clearance (ft)";

    /**
     * Item header constant
     */
    private final static String STR_TERRAIN = "Terrain (Level = 1, Rolling = 3, Varying = 4)";

    /**
     * Item header constant
     */
    private final static String STR_TRUCK_CAR_EQ = "Truck-PC Equivalent (ET)";

    /**
     * Item header constant
     */
    private final static String STR_NUM_LANES = "# of Lanes: Mainline";

    /**
     * Item header constant
     */
    private final static String STR_FREE_FLOW_SPEED = "Free Flow Speed (mph)";

    /**
     * Item header constant
     */
    private final static String STR_DEMAND_VEH = "Mainline Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_TRUCK_SINGLE_PERCENTAGE = "Single Unit Truck and Bus (%)";

    /**
     * Item header constant
     */
    private final static String STR_TRUCK_TRAILER_PERCENTAGE = "Tractor Trailer (%)";

    /**
     * Item header constant
     */
    private final static String STR_U_CAF = "Seed Capacity Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_U_OAF = "Seed Entering Dem. Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_U_DAF = "Seed Exit Dem. Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_U_SAF = "Seed Free Flow Speed Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_U_DPCAF = "Seed Driver Population Capacity Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_U_DPSAF = "Seed Driver Population Free Flow Speed Adj. Fac.";

    // ONR Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_ON_RAMP_SIDE = "ONR Side (Right = 0, Left = 1)";

    /**
     * Item header constant
     */
    private final static String STR_ACC_DEC_LANE_LENGTH = "Acc/Dec Lane Length (ft)";

    /**
     * Item header constant
     */
    private final static String STR_NUM_ON_RAMP_LANES = "# Lanes: ONR";

    /**
     * Item header constant
     */
    private final static String STR_ON_RAMP_DEMAND_VEH = "ONR/Entering Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_ON_RAMP_FREE_FLOW_SPEED = "ONR Free Flow Speed (mph)";

    /**
     * Item header constant
     */
    private final static String STR_ON_RAMP_METERING_RATE = "ONR Metering Rate (vph)";

    // OFR Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_OFF_RAMP_SIDE = "OFR Side (Right = 0, Left = 1)";

    /**
     * Item header constant
     */
    private final static String STR_NUM_OFF_RAMP_LANES = "# Lanes: OFR";

    /**
     * Item header constant
     */
    private final static String STR_OFF_RAMP_DEMAND_VEH = "OFR/Exit Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_OFF_RAMP_FREE_FLOW_SPEED = "OFR Free Flow Speed (mph)";

    // Weaving Segment Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_LENGTH_OF_WEAVING = "Weave Segment Ls (ft)";

    /**
     * Item header constant
     */
    private final static String STR_MIN_LANE_CHANGE_ONR_TO_FRWY = "Weave Segment LCRF";

    /**
     * Item header constant
     */
    private final static String STR_MIN_LANE_CHANGE_FRWY_TO_OFR = "Weave Segment LCFR";

    /**
     * Item header constant
     */
    private final static String STR_MIN_LANE_CHANGE_ONR_TO_OFR = "Weave Segment LCRR";

    /**
     * Item header constant
     */
    private final static String STR_NUM_LANES_WEAVING = "Weave Segment NW";

    /**
     * Item header constant
     */
    private final static String STR_RAMP_TO_RAMP_DEMAND_VEH = "Ramp to Ramp Dem. (vph)";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ML HEADER">
    // Basic Segment Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_ML_SEGMENT_TYPE = "ML Segment Type (Basic = 0, ONR = 1, OFR = 2, Overlap = 3, Weaving = 4, Access = 8)";

    /**
     * Item header constant
     */
    private final static String STR_ML_METHOD_TYPE = "ML Type of ML (HOV = 0, HOT = 1)";

    /**
     * Item header constant
     */
    private final static String STR_ML_SEPARATION_TYPE = "ML Type of Separation (Marking = 0, Buffer = 1, Barrier = 2)";

    /**
     * Item header constant
     */
    private final static String STR_ML_NUM_LANES = "ML # of Lanes: Mainline";

    /**
     * Item header constant
     */
    private final static String STR_ML_FREE_FLOW_SPEED = "ML Free Flow Speed (mph)";

    /**
     * Item header constant
     */
    private final static String STR_ML_DEMAND_VEH = "ML Mainline Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_ML_TRUCK_SINGLE_PERCENTAGE = "ML Single Unit Truck and Bus (%)";

    /**
     * Item header constant
     */
    private final static String STR_ML_TRUCK_TRAILER_PERCENTAGE = "ML Tractor Trailer (%)";

    /**
     * Item header constant
     */
    private final static String STR_ML_UCAF = "ML Seed Capacity Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_ML_UOAF = "ML Seed Entering Dem. Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_ML_UDAF = "ML Seed Exit Dem. Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_ML_USAF = "ML Seed Free Flow Speed Adj. Fac.";

    /**
     * Item header constant
     */
    private final static String STR_ML_ACC_DEC_LANE_LENGTH = "ML Acc/Dec Lane Length (ft)";

    // ONR Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_ML_ON_RAMP_SIDE = "ML ONR Side (Right = 0, Left = 1)";

    /**
     * Item header constant
     */
    private final static String STR_ML_NUM_ON_RAMP_LANES = "ML # Lanes: ONR";

    /**
     * Item header constant
     */
    private final static String STR_ML_ON_RAMP_DEMAND_VEH = "ML ONR/Entering Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_ML_ON_RAMP_FREE_FLOW_SPEED = "ML ONR Free Flow Speed (mph)";

    // OFR Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_ML_OFF_RAMP_SIDE = "ML OFR Side (Right = 0, Left = 1)";

    /**
     * Item header constant
     */
    private final static String STR_ML_NUM_OFF_RAMP_LANES = "ML # Lanes: OFR";

    /**
     * Item header constant
     */
    private final static String STR_ML_OFF_RAMP_DEMAND_VEH = "ML OFR/Exiting Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_ML_OFF_RAMP_FREE_FLOW_SPEED = "ML OFR Free Flow Speed (mph)";

    // Weaving Segment Variable Column Text
    /**
     * Item header constant
     */
    private final static String STR_ML_LENGTH_SHORT = "ML Length Short (ft)";

    /**
     * Item header constant
     */
    private final static String STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY = "ML Weave Segment LCRF";

    /**
     * Item header constant
     */
    private final static String STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR = "ML Weave Segment LCFR";

    /**
     * Item header constant
     */
    private final static String STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR = "ML Weave Segment LCRR";

    /**
     * Item header constant
     */
    private final static String STR_ML_NUM_LANES_WEAVING = "ML Weave Segment NW";

    /**
     * Item header constant
     */
    private final static String STR_ML_LC_MIN = "ML Min Lane Change";

    /**
     * Item header constant
     */
    private final static String STR_ML_LC_MAX = "ML Max Lane Change";

    /**
     * Item header constant
     */
    private final static String STR_ML_RAMP_TO_RAMP_DEMAND_VEH = "ML Ramp to Ramp Dem. (vph)";

    /**
     * Item header constant
     */
    private final static String STR_ML_HAS_CROSS_WEAVE = "Analysis of Cross Weave Effect";

    /**
     * Item header constant
     */
    private final static String STR_ML_CROSS_WEAVE_LC_MIN = "Cross Weave LC-Min";

    /**
     * Item header constant
     */
    private final static String STR_ML_CROSS_WEAVE_VOLUME = "Cross Weave Volume";
    // </editor-fold>
    // </editor-fold>

    /**
     * Item list for general purpose segments
     */
    private final ArrayList<Item> itemListGP = new ArrayList();

    /**
     * Item list for managed lanes segments
     */
    private final ArrayList<Item> itemListML = new ArrayList();

    /**
     * Constructor
     */
    public ASCIISeedFileAdapter_GPMLFormat() {
        buildIDMap();
        buildGPList();
        buildMLList();
    }

    /**
     * Build ID map
     */
    private void buildIDMap() {
        // <editor-fold defaultstate="collapsed" desc="GLOBAL INPUT">
        idToHeaderMap.put(ID_PROJECT_NAME, STR_PROJECT_NAME);
        idToHeaderMap.put(ID_NUM_SEGMENTS, STR_NUM_SEGMENTS);
        idToHeaderMap.put(ID_START, STR_START);
        idToHeaderMap.put(ID_END, STR_END);
        idToHeaderMap.put(ID_FFS_KNOWN, STR_FFS_KNOWN);
        idToHeaderMap.put(ID_RAMP_METERING, STR_RAMP_METERING);
        idToHeaderMap.put(ID_ML_USED, STR_ML_USED);
        idToHeaderMap.put(ID_ALPHA, STR_ALPHA);
        idToHeaderMap.put(ID_JAM_DENSITY, STR_JAM_DENSITY);
        idToHeaderMap.put(ID_SEED_DEMAND_DATE, STR_SEED_DEMAND_DATE);
        idToHeaderMap.put(ID_GP_OCCU, STR_GP_OCCU);
        idToHeaderMap.put(ID_ML_OCCU, STR_ML_OCCU);
        idToHeaderMap.put(ID_URBAN_RURAL, STR_URBAN_RURAL);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="GP HEADER">
        // Basic Segment Variable Column Text
        idToHeaderMap.put(ID_SEGMENT_TYPE, STR_SEGMENT_TYPE);
        idToHeaderMap.put(ID_SEGMENT_LENGTH, STR_SEGMENT_LENGTH);
        idToHeaderMap.put(ID_SEGMENT_WIDTH, STR_SEGMENT_WIDTH);
        idToHeaderMap.put(ID_LATERAL_CLEARANCE, STR_LATERAL_CLEARANCE);
        idToHeaderMap.put(ID_TERRAIN, STR_TERRAIN);
        idToHeaderMap.put(ID_TRUCK_CAR_EQ, STR_TRUCK_CAR_EQ);

        idToHeaderMap.put(ID_NUM_LANES, STR_NUM_LANES);
        idToHeaderMap.put(ID_FREE_FLOW_SPEED, STR_FREE_FLOW_SPEED);
        idToHeaderMap.put(ID_DEMAND_VEH, STR_DEMAND_VEH);
        idToHeaderMap.put(ID_TRUCK_SINGLE_PERCENTAGE, STR_TRUCK_SINGLE_PERCENTAGE);
        idToHeaderMap.put(ID_TRUCK_TRAILER_PERCENTAGE, STR_TRUCK_TRAILER_PERCENTAGE);
        idToHeaderMap.put(ID_U_CAF, STR_U_CAF);
        idToHeaderMap.put(ID_U_OAF, STR_U_OAF);
        idToHeaderMap.put(ID_U_DAF, STR_U_DAF);
        idToHeaderMap.put(ID_U_SAF, STR_U_SAF);
        idToHeaderMap.put(ID_U_DPCAF, STR_U_DPCAF);
        idToHeaderMap.put(ID_U_DPSAF, STR_U_DPSAF);

        // ONR Variable Column Text
        idToHeaderMap.put(ID_ON_RAMP_SIDE, STR_ON_RAMP_SIDE);
        idToHeaderMap.put(ID_ACC_DEC_LANE_LENGTH, STR_ACC_DEC_LANE_LENGTH);

        idToHeaderMap.put(ID_NUM_ON_RAMP_LANES, STR_NUM_ON_RAMP_LANES);
        idToHeaderMap.put(ID_ON_RAMP_DEMAND_VEH, STR_ON_RAMP_DEMAND_VEH);
        idToHeaderMap.put(ID_ON_RAMP_FREE_FLOW_SPEED, STR_ON_RAMP_FREE_FLOW_SPEED);
        idToHeaderMap.put(ID_ON_RAMP_METERING_RATE, STR_ON_RAMP_METERING_RATE);

        // OFR Variable Column Text
        idToHeaderMap.put(ID_OFF_RAMP_SIDE, STR_OFF_RAMP_SIDE);

        idToHeaderMap.put(ID_NUM_OFF_RAMP_LANES, STR_NUM_OFF_RAMP_LANES);
        idToHeaderMap.put(ID_OFF_RAMP_DEMAND_VEH, STR_OFF_RAMP_DEMAND_VEH);
        idToHeaderMap.put(ID_OFF_RAMP_FREE_FLOW_SPEED, STR_OFF_RAMP_FREE_FLOW_SPEED);

        // Weaving Segment Variable Column Text
        idToHeaderMap.put(ID_LENGTH_OF_WEAVING, STR_LENGTH_OF_WEAVING);
        idToHeaderMap.put(ID_MIN_LANE_CHANGE_ONR_TO_FRWY, STR_MIN_LANE_CHANGE_ONR_TO_FRWY);
        idToHeaderMap.put(ID_MIN_LANE_CHANGE_FRWY_TO_OFR, STR_MIN_LANE_CHANGE_FRWY_TO_OFR);
        idToHeaderMap.put(ID_MIN_LANE_CHANGE_ONR_TO_OFR, STR_MIN_LANE_CHANGE_ONR_TO_OFR);
        idToHeaderMap.put(ID_NUM_LANES_WEAVING, STR_NUM_LANES_WEAVING);

        idToHeaderMap.put(ID_RAMP_TO_RAMP_DEMAND_VEH, STR_RAMP_TO_RAMP_DEMAND_VEH);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="ML HEADER">
        // Basic Segment Variable Column Text
        idToHeaderMap.put(ID_ML_SEGMENT_TYPE, STR_ML_SEGMENT_TYPE);
        idToHeaderMap.put(ID_ML_SEPARATION_TYPE, STR_ML_SEPARATION_TYPE);

        idToHeaderMap.put(ID_ML_NUM_LANES, STR_ML_NUM_LANES);
        idToHeaderMap.put(ID_ML_FREE_FLOW_SPEED, STR_ML_FREE_FLOW_SPEED);
        idToHeaderMap.put(ID_ML_DEMAND_VEH, STR_ML_DEMAND_VEH);
        idToHeaderMap.put(ID_ML_TRUCK_SINGLE_PERCENTAGE, STR_ML_TRUCK_SINGLE_PERCENTAGE);
        idToHeaderMap.put(ID_ML_TRUCK_TRAILER_PERCENTAGE, STR_ML_TRUCK_TRAILER_PERCENTAGE);
        idToHeaderMap.put(ID_ML_UCAF, STR_ML_UCAF);
        idToHeaderMap.put(ID_ML_UOAF, STR_ML_UOAF);
        idToHeaderMap.put(ID_ML_UDAF, STR_ML_UDAF);
        idToHeaderMap.put(ID_ML_USAF, STR_ML_USAF);
        idToHeaderMap.put(ID_ML_ACC_DEC_LANE_LENGTH, STR_ML_ACC_DEC_LANE_LENGTH);

        // ONR Variable Column Text
        idToHeaderMap.put(ID_ML_ON_RAMP_SIDE, STR_ML_ON_RAMP_SIDE);

        idToHeaderMap.put(ID_ML_NUM_ON_RAMP_LANES, STR_ML_NUM_ON_RAMP_LANES);
        idToHeaderMap.put(ID_ML_ON_RAMP_DEMAND_VEH, STR_ML_ON_RAMP_DEMAND_VEH);
        idToHeaderMap.put(ID_ML_ON_RAMP_FREE_FLOW_SPEED, STR_ML_ON_RAMP_FREE_FLOW_SPEED);

        // OFR Variable Column Text
        idToHeaderMap.put(ID_ML_OFF_RAMP_SIDE, STR_ML_OFF_RAMP_SIDE);

        idToHeaderMap.put(ID_ML_NUM_OFF_RAMP_LANES, STR_ML_NUM_OFF_RAMP_LANES);
        idToHeaderMap.put(ID_ML_OFF_RAMP_DEMAND_VEH, STR_ML_OFF_RAMP_DEMAND_VEH);
        idToHeaderMap.put(ID_ML_OFF_RAMP_FREE_FLOW_SPEED, STR_ML_OFF_RAMP_FREE_FLOW_SPEED);

        // Weaving Segment Variable Column Text
        idToHeaderMap.put(ID_ML_LENGTH_SHORT, STR_ML_LENGTH_SHORT);
        idToHeaderMap.put(ID_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, STR_ML_MIN_LANE_CHANGE_ONR_TO_FRWY);
        idToHeaderMap.put(ID_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, STR_ML_MIN_LANE_CHANGE_FRWY_TO_OFR);
        idToHeaderMap.put(ID_ML_MIN_LANE_CHANGE_ONR_TO_OFR, STR_ML_MIN_LANE_CHANGE_ONR_TO_OFR);
        idToHeaderMap.put(ID_ML_NUM_LANES_WEAVING, STR_ML_NUM_LANES_WEAVING);
        idToHeaderMap.put(ID_ML_LC_MIN, STR_ML_LC_MIN);
        idToHeaderMap.put(ID_ML_LC_MAX, STR_ML_LC_MAX);
        idToHeaderMap.put(ID_ML_RAMP_TO_RAMP_DEMAND_VEH, STR_ML_RAMP_TO_RAMP_DEMAND_VEH);

        idToHeaderMap.put(ID_ML_HAS_CROSS_WEAVE, STR_ML_HAS_CROSS_WEAVE);
        idToHeaderMap.put(ID_ML_CROSS_WEAVE_LC_MIN, STR_ML_CROSS_WEAVE_LC_MIN);
        idToHeaderMap.put(ID_ML_CROSS_WEAVE_VOLUME, STR_ML_CROSS_WEAVE_VOLUME);
        // </editor-fold>
    }

    /**
     * Build GP list
     */
    private void buildGPList() {
        //Global Input
        itemListGP.add(new Item(ID_PROJECT_NAME, CEConst.IDS_PROJECT_NAME, GENERAL_INFO, STRING));
        itemListGP.add(new Item(ID_START, CEConst.IDS_START_TIME, GENERAL_INFO, OTHER));
        itemListGP.add(new Item(ID_END, CEConst.IDS_END_TIME, GENERAL_INFO, OTHER));
        itemListGP.add(new Item(ID_NUM_SEGMENTS, CEConst.IDS_NUM_SEGMENT, GENERAL_INFO, INTEGER));
        itemListGP.add(new Item(ID_FFS_KNOWN, CEConst.IDS_FFS_KNOWN, GENERAL_INFO, BOOLEAN));

        itemListGP.add(new Item(ID_ML_USED, CEConst.IDS_MANAGED_LANE_USED, GENERAL_INFO, BOOLEAN));
        itemListGP.add(new Item(ID_ALPHA, CEConst.IDS_CAPACITY_ALPHA, GENERAL_INFO, INTEGER));
        itemListGP.add(new Item(ID_JAM_DENSITY, CEConst.IDS_JAM_DENSITY, GENERAL_INFO, FLOAT));
        itemListGP.add(new Item(ID_SEED_DEMAND_DATE, CEConst.IDS_SEED_DEMAND_DATE, GENERAL_INFO, OTHER));
        itemListGP.add(new Item(ID_GP_OCCU, CEConst.IDS_OCCU_GP, GENERAL_INFO, FLOAT));
        itemListGP.add(new Item(ID_ML_OCCU, CEConst.IDS_OCCU_ML, GENERAL_INFO, FLOAT));
        itemListGP.add(new Item(ID_URBAN_RURAL, CEConst.IDS_SEED_URBAN_RURAL_TYPE, GENERAL_INFO, INTEGER));

        //GP Segment Input
        itemListGP.add(new Item(ID_SEGMENT_TYPE, CEConst.IDS_SEGMENT_TYPE, TIME_INDEPENDENT, OTHER));
        itemListGP.add(new Item(ID_SEGMENT_LENGTH, CEConst.IDS_SEGMENT_LENGTH_FT, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_SEGMENT_WIDTH, CEConst.IDS_LANE_WIDTH, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_LATERAL_CLEARANCE, CEConst.IDS_LATERAL_CLEARANCE, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_TERRAIN, CEConst.IDS_TERRAIN, TIME_INDEPENDENT, OTHER));
        itemListGP.add(new Item(ID_TRUCK_CAR_EQ, CEConst.IDS_TRUCK_CAR_ET, TIME_INDEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_NUM_LANES, CEConst.IDS_MAIN_NUM_LANES_IN, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_FREE_FLOW_SPEED, CEConst.IDS_MAIN_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_DEMAND_VEH, CEConst.IDS_MAIN_DEMAND_VEH, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_TRUCK_SINGLE_PERCENTAGE, CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_TRUCK_TRAILER_PERCENTAGE, CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, TIME_DEPENDENT, FLOAT));

        itemListGP.add(new Item(ID_U_CAF, CEConst.IDS_GP_USER_CAF, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_U_OAF, CEConst.IDS_GP_USER_OAF, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_U_DAF, CEConst.IDS_GP_USER_DAF, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_U_SAF, CEConst.IDS_GP_USER_SAF, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_U_DPCAF, CEConst.IDS_GP_USER_DPCAF, TIME_DEPENDENT, FLOAT));
        itemListGP.add(new Item(ID_U_DPSAF, CEConst.IDS_GP_USER_DPSAF, TIME_DEPENDENT, FLOAT));

        itemListGP.add(new Item(ID_ACC_DEC_LANE_LENGTH, CEConst.IDS_ACC_DEC_LANE_LENGTH, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_ON_RAMP_SIDE, CEConst.IDS_ON_RAMP_SIDE, TIME_INDEPENDENT, OTHER));
        itemListGP.add(new Item(ID_NUM_ON_RAMP_LANES, CEConst.IDS_NUM_ON_RAMP_LANES, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_ON_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_ON_RAMP_DEMAND_VEH, CEConst.IDS_ON_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_ON_RAMP_METERING_RATE, CEConst.IDS_ON_RAMP_METERING_RATE_FIX, TIME_DEPENDENT, INTEGER));

        itemListGP.add(new Item(ID_OFF_RAMP_SIDE, CEConst.IDS_OFF_RAMP_SIDE, TIME_INDEPENDENT, OTHER));
        itemListGP.add(new Item(ID_NUM_OFF_RAMP_LANES, CEConst.IDS_NUM_OFF_RAMP_LANES, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_OFF_RAMP_FREE_FLOW_SPEED, CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_OFF_RAMP_DEMAND_VEH, CEConst.IDS_OFF_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));

        itemListGP.add(new Item(ID_LENGTH_OF_WEAVING, CEConst.IDS_LENGTH_OF_WEAVING, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_MIN_LANE_CHANGE_ONR_TO_FRWY, CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_MIN_LANE_CHANGE_FRWY_TO_OFR, CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_MIN_LANE_CHANGE_ONR_TO_OFR, CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_NUM_LANES_WEAVING, CEConst.IDS_NUM_LANES_WEAVING, TIME_INDEPENDENT, INTEGER));
        itemListGP.add(new Item(ID_RAMP_TO_RAMP_DEMAND_VEH, CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));

        itemListGP.add(new Item(ID_RAMP_METERING, CEConst.IDS_RAMP_METERING_TYPE, TIME_DEPENDENT, INTEGER));
    }

    /**
     * Build ML list
     */
    private void buildMLList() {
        //ML Segment Input
        itemListML.add(new Item(ID_ML_SEGMENT_TYPE, CEConst.IDS_ML_SEGMENT_TYPE, TIME_INDEPENDENT, OTHER));
        itemListML.add(new Item(ID_ML_SEPARATION_TYPE, CEConst.IDS_ML_SEPARATION_TYPE, TIME_INDEPENDENT, OTHER));
        itemListML.add(new Item(ID_ML_NUM_LANES, CEConst.IDS_ML_NUM_LANES, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_FREE_FLOW_SPEED, CEConst.IDS_ML_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_DEMAND_VEH, CEConst.IDS_ML_DEMAND_VEH, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_TRUCK_SINGLE_PERCENTAGE, CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE, TIME_DEPENDENT, FLOAT));
        itemListML.add(new Item(ID_ML_TRUCK_TRAILER_PERCENTAGE, CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE, TIME_DEPENDENT, FLOAT));
        itemListML.add(new Item(ID_ML_UCAF, CEConst.IDS_ML_USER_CAF, TIME_DEPENDENT, FLOAT));
        itemListML.add(new Item(ID_ML_UOAF, CEConst.IDS_ML_USER_OAF, TIME_DEPENDENT, FLOAT));
        itemListML.add(new Item(ID_ML_UDAF, CEConst.IDS_ML_USER_DAF, TIME_DEPENDENT, FLOAT));
        itemListML.add(new Item(ID_ML_USAF, CEConst.IDS_ML_USER_SAF, TIME_DEPENDENT, FLOAT));

        itemListML.add(new Item(ID_ML_ACC_DEC_LANE_LENGTH, CEConst.IDS_ML_ACC_DEC_LANE_LENGTH, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_ON_RAMP_SIDE, CEConst.IDS_ML_ON_RAMP_SIDE, TIME_INDEPENDENT, OTHER));
        itemListML.add(new Item(ID_ML_NUM_ON_RAMP_LANES, CEConst.IDS_ML_NUM_ON_RAMP_LANES, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_ON_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_ON_RAMP_DEMAND_VEH, CEConst.IDS_ML_ON_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));

        itemListML.add(new Item(ID_ML_OFF_RAMP_SIDE, CEConst.IDS_ML_OFF_RAMP_SIDE, TIME_INDEPENDENT, OTHER));
        itemListML.add(new Item(ID_ML_NUM_OFF_RAMP_LANES, CEConst.IDS_ML_NUM_OFF_RAMP_LANES, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_OFF_RAMP_FREE_FLOW_SPEED, CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED, TIME_DEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_OFF_RAMP_DEMAND_VEH, CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));

        itemListML.add(new Item(ID_ML_LENGTH_SHORT, CEConst.IDS_ML_LENGTH_SHORT, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_MIN_LANE_CHANGE_ONR_TO_OFR, CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_NUM_LANES_WEAVING, CEConst.IDS_ML_NUM_LANES_WEAVING, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_RAMP_TO_RAMP_DEMAND_VEH, CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH, TIME_DEPENDENT, INTEGER));

        itemListML.add(new Item(ID_ML_LC_MIN, CEConst.IDS_ML_MIN_LANE_CHANGE_ML, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_LC_MAX, CEConst.IDS_ML_MAX_LANE_CHANGE_ML, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_HAS_CROSS_WEAVE, CEConst.IDS_HAS_CROSS_WEAVE, TIME_INDEPENDENT, BOOLEAN));
        itemListML.add(new Item(ID_ML_CROSS_WEAVE_LC_MIN, CEConst.IDS_CROSS_WEAVE_LC_MIN, TIME_INDEPENDENT, INTEGER));
        itemListML.add(new Item(ID_ML_CROSS_WEAVE_VOLUME, CEConst.IDS_CROSS_WEAVE_VOLUME, TIME_DEPENDENT, INTEGER));
    }

    // <editor-fold defaultstate="collapsed" desc="Import Functions">
    /**
     * Import a Seed instance from an ASCII file (selected by user using file
     * chooser)
     *
     * @return a Seed instance
     */
    public Seed importFromASCII() {
        final JFileChooser fc = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return importFromASCII(fc.getSelectedFile().getAbsolutePath());
        }

        return null;
    }

    /**
     * Import a Seed instance from an ASCII file
     *
     * @param fileName path of the ASCII file
     * @return a Seed instance
     */
    public Seed importFromASCII(String fileName) {

        Seed seed = new Seed();
        seed.setValue(CEConst.IDS_SEED_FILE_NAME, fileName);

        try {
            FileInputStream f = new FileInputStream(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(f));

            /* Read the first separator and header lines */
            String header = in.readLine();
            while (header != null) {
                //skip invalid line until find a header or end of file
                while (header != null && !header.startsWith("<")) {
                    header = in.readLine();
                }

                if (header != null) {
                    String headerID = header.substring(1, header.indexOf(">"));
                    Item item = findItem(headerID);
                    if (item == null) {
                        System.out.println("Can not find header ID " + headerID);
                    } else {
                        String value;
                        switch (item.coreEngineID) {
                            case CEConst.IDS_START_TIME:
                            case CEConst.IDS_END_TIME:
                                value = in.readLine();
                                seed.setValue(item.coreEngineID,
                                        new CETime(Integer.parseInt(value.substring(0, value.indexOf(":"))),
                                                Integer.parseInt(value.substring(value.indexOf(":") + 1, value.length()))));
                                break;
                            case CEConst.IDS_SEED_DEMAND_DATE:
                                value = in.readLine();
                                seed.setSeedFileDate(new CEDate(value));
                                break;
                            default:
                                switch (item.property) {
                                    case GENERAL_INFO:
                                        readGeneralInfo(seed, item, in);
                                        break;
                                    case TIME_DEPENDENT:
                                        readTimeDependent(seed, item, in);
                                        break;
                                    case TIME_INDEPENDENT:
                                        readTimeIndependent(seed, item, in);
                                        break;
                                    default:
                                        System.out.println("Error in import");
                                }
                        }
                    }
                }

                header = in.readLine();
            }

            in.close();
            f.close();
        } catch (Exception e) {
            System.out.println("Error in importFromASCII");
            e.printStackTrace();
            return null;
        }
        seed.extrapolateRampTruckPCT();
        return seed;
    }

    /**
     * Find item from list
     *
     * @param headerID header ID
     * @return item from list
     */
    private Item findItem(String headerID) {
        for (Item item : itemListGP) {
            if (item.headerID.equals(headerID)) {
                return item;
            }
        }
        for (Item item : itemListML) {
            if (item.headerID.equals(headerID)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Read general info
     *
     * @param seed seed instance
     * @param item item to be read
     * @param in input reader
     * @throws IOException
     */
    private void readGeneralInfo(Seed seed, Item item, BufferedReader in) throws IOException {
        String value = in.readLine();
        seed.setValue(item.coreEngineID, value);
    }

    /**
     * Read time independent data
     *
     * @param seed seed instance
     * @param item item to be read
     * @param in input reader
     * @throws IOException
     */
    private void readTimeIndependent(Seed seed, Item item, BufferedReader in) throws IOException {
        in.readLine(); //skip segment index line
        Scanner line = new Scanner(in.readLine());
        for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
            String value = line.next();
            if (!value.equals(CEConst.IDS_NA)) {
                seed.setValue(item.coreEngineID, value, seg);
            }
        }
        line.close();
    }

    /**
     * Read time dependent data
     *
     * @param seed seed instance
     * @param item item to be read
     * @param in input reader
     * @throws IOException
     */
    private void readTimeDependent(Seed seed, Item item, BufferedReader in) throws IOException {
        in.readLine(); //skip segment index line
        for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
            Scanner line = new Scanner(in.readLine());
            line.next(); //skip period token
            for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                String value = line.next();
                if (!value.equals(CEConst.IDS_NA) && !value.equals(CEConst.IDS_NA_SPECIAL)) {
                    seed.setValue(item.coreEngineID, value, seg, period);
                }
            }
            line.close();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Export Functions">
    /**
     * Export input of seed instance to an ASCII(text) file
     *
     * @param seed seed instance
     * @param parent For location of fileChooser. Can be null.
     * @return file path and name
     */
    public String exportToASCII(Seed seed, JFrame parent) {
        final JFileChooser fc = new JFileChooser(FREEVAL_HCM.getInitialDirectory());
        fc.setDialogTitle("Export to ASCII File");
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            exportToASCII(seed, fc.getSelectedFile().getAbsolutePath());
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Export input of seed instance to an ASCII(text) file
     *
     * @param seed seed instance
     * @param newFileName file path and name
     */
    public void exportToASCII(Seed seed, String newFileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(newFileName));

            for (Item item : itemListGP) {
                try {
                    writeItem(seed, item, out);
                } catch (Exception e) {
                    System.out.println("Error in exportToASCII - GP");
                }
            }

            if (seed.isManagedLaneUsed()) {
                for (Item item : itemListML) {
                    try {
                        writeItem(seed, item, out);
                    } catch (Exception e) {
                        System.out.println("Error in exportToASCII- ML");
                    }
                }
            }

            out.close();
        } catch (Exception e) {
            System.out.println("Error in exportToASCII");
        }
    }

    /**
     * Write data
     *
     * @param seed seed instance
     * @param item item to be written
     * @param out output writer
     * @throws IOException
     */
    private void writeItem(Seed seed, Item item, BufferedWriter out) throws IOException {
        switch (item.property) {
            case GENERAL_INFO:
                writeGeneralInfo(seed, item, out);
                break;
            case TIME_DEPENDENT:
                writeTimeDependent(seed, item, out);
                break;
            case TIME_INDEPENDENT:
                writeTimeIndependent(seed, item, out);
                break;
            default:
                System.out.println("Error in writeItem");
        }
    }

    /**
     * Write general information data
     *
     * @param seed seed instance
     * @param item item to be written
     * @param out output writer
     * @throws IOException
     */
    private void writeGeneralInfo(Seed seed, Item item, BufferedWriter out) throws IOException {
        writeHeader(item.headerID, out);
        switch (item.dataType) {
            case FLOAT:
                out.write(formatter.format(seed.getValueFloat(item.coreEngineID)));
                break;
            default:
                out.write(seed.getValueString(item.coreEngineID));
                break;
        }

        out.newLine();
    }

    /**
     * Write time dependent data
     *
     * @param seed seed instance
     * @param item item to be written
     * @param out output writer
     * @throws IOException
     */
    private void writeTimeDependent(Seed seed, Item item, BufferedWriter out) throws IOException {
        writeHeader(item.headerID, out);
        writeSegmentIndex(seed, out);

        switch (item.dataType) {
            case FLOAT:
                for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    out.write(alignString("t=" + (period + 1)));
                    for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                        out.write(alignString(formatter.format(seed.getValueFloat(item.coreEngineID, seg, period))));
                    }
                    out.newLine();
                }
                break;
            default:
                for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                    out.write(alignString("t=" + (period + 1)));
                    for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                        out.write(alignString(seed.getValueString(item.coreEngineID, seg, period)));
                    }
                    out.newLine();
                }
                break;
        }
    }

    /**
     * Write time independent data
     *
     * @param seed seed instance
     * @param item item to be written
     * @param out output writer
     * @throws IOException
     */
    private void writeTimeIndependent(Seed seed, Item item, BufferedWriter out) throws IOException {
        writeHeader(item.headerID, out);
        writeSegmentIndex(seed, out);

        switch (item.dataType) {
            case FLOAT:
                out.write(alignString(""));
                for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    out.write(alignString(formatter.format(seed.getValueFloat(item.coreEngineID, seg))));
                }
                out.newLine();
                break;
            default:
                out.write(alignString(""));
                for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                    out.write(alignString(seed.getValueString(item.coreEngineID, seg)));
                }
                out.newLine();
                break;
        }
    }

    /**
     * Write header
     *
     * @param headerID header ID
     * @param out output writer
     * @throws IOException
     */
    private void writeHeader(String headerID, BufferedWriter out) throws IOException {
        out.write("<" + headerID + "> " + idToHeaderMap.get(headerID));
        out.newLine();
    }

    /**
     * Write segment index
     *
     * @param seed seed instance
     * @param out output writer
     * @throws IOException
     */
    private void writeSegmentIndex(Seed seed, BufferedWriter out) throws IOException {
        out.write(alignString("Seg."));
        for (int index = 1; index <= seed.getValueInt(CEConst.IDS_NUM_SEGMENT); index++) {
            out.write(alignString("#" + index));
        }
        out.newLine();
    }

    /**
     * Align outputs by adding spaces
     *
     * @param str output string
     * @return formatted string
     */
    private String alignString(String str) {
        if (str.length() < ITEM_WIDTH) {
            String result = "";
            for (int i = 0; i < ITEM_WIDTH - str.length(); i++) {
                result += " ";
            }
            return result + str;
        } else {
            return " " + str;
        }
    }
    // </editor-fold>

    /**
     * Privet helper class for output item parameters
     */
    private class Item {

        /**
         * Header ID
         */
        final String headerID;

        /**
         * coreEngine ID
         */
        final String coreEngineID;

        /**
         * Item property
         */
        final int property;

        /**
         * Item data type
         */
        final int dataType;

        /**
         * Constructor
         *
         * @param headerID Header ID
         * @param coreEngineID coreEngine ID
         * @param property Item property
         * @param dataType Item data type
         */
        private Item(String headerID, String coreEngineID, int property, int dataType) {
            this.headerID = headerID;
            this.coreEngineID = coreEngineID;
            this.property = property;
            this.dataType = dataType;
        }
    }
}
