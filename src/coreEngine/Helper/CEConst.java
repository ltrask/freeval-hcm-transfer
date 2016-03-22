package coreEngine.Helper;

/**
 * This class contains constant identifier for core engine.
 *
 * @author Shu Liu
 */
public class CEConst {

    // <editor-fold defaultstate="collapsed" desc="PARAMETER CONSTANTS">
    //constant for urban-rural type
    /**
     * Seed Type Urban
     */
    public static final int SEED_URBAN = 1;
    /**
     * Seed Type Rural
     */
    public static final int SEED_RURAL = 2;

    //constant int for terrain type
    /**
     * Terrain Type Level
     */
    public static final int TERRAIN_LEVEL = 1;
    /**
     * Terrain Type Rolling
     */
    public static final int TERRAIN_ROLLING = 3;
    /**
     * Terrain Type Varying or Other
     */
    public static final int TERRAIN_VARYING_OR_OTHER = 4;

    //constant display String for terrain type
    /**
     * Terrain Type Level
     */
    public static final String STR_TERRAIN_LEVEL = "Level";
    /**
     * Terrain Type Rolling
     */
    public static final String STR_TERRAIN_ROLLING = "Rolling";
    /**
     * Terrain Type Varying or Other
     */
    public static final String STR_TERRAIN_VARYING_OR_OTHER = "Varying";

    //constant int for segment type
    /**
     * Segment Type General Purpose
     */
    public static final int SEG_TYPE_GP = 0;
    /**
     * Segment Type Managed Lane
     */
    public static final int SEG_TYPE_ML = 1;
    /**
     * Segment Type Basic
     */
    public static final int SEG_TYPE_B = 0;
    /**
     * Segment Type On Ramp
     */
    public static final int SEG_TYPE_ONR = 1;
    /**
     * Segment Type Off Ramp
     */
    public static final int SEG_TYPE_OFR = 2;
    /**
     * Segment Type Overlapping
     */
    public static final int SEG_TYPE_R = 3;
    /**
     * Segment Type Weaving
     */
    public static final int SEG_TYPE_W = 4;
    /**
     * Segment Type Access
     */
    public static final int SEG_TYPE_ACS = 8;
    /**
     * Segment Type On Ramp to Basic
     */
    public static final int SEG_TYPE_ONR_B = 5;
    /**
     * Segment Type Off Ramp to Basic
     */
    public static final int SEG_TYPE_OFR_B = 6;
    /**
     * Segment Type Weaving to Basic
     */
    public static final int SEG_TYPE_W_B = 7;

    //constant display String for segment type
    /**
     * Segment Type Basic
     */
    public static final String STR_SEG_TYPE_B = "Basic";
    /**
     * Segment Type On Ramp
     */
    public static final String STR_SEG_TYPE_ONR = "On Ramp";
    /**
     * Segment Type Off Ramp
     */
    public static final String STR_SEG_TYPE_OFR = "Off Ramp";
    /**
     * Segment Type Overlapping
     */
    public static final String STR_SEG_TYPE_R = "Overlap";
    /**
     * Segment Type Weaving
     */
    public static final String STR_SEG_TYPE_W = "Weaving";
    /**
     * Segment Type On Ramp to Basic
     */
    public static final String STR_SEG_TYPE_ONR_B = "ONR-B";
    /**
     * Segment Type Off Ramp to Basic
     */
    public static final String STR_SEG_TYPE_OFR_B = "OFR-B";
    /**
     * Segment Type Weaving to Basic
     */
    public static final String STR_SEG_TYPE_W_B = "W-B";
    /**
     * Segment Type Access
     */
    public static final String STR_SEG_TYPE_ACS = "Access";

    //constant int for ramp side
    /**
     * Ramp Side Left
     */
    public static final int RAMP_SIDE_LEFT = 1;
    /**
     * Ramp Side Right
     */
    public static final int RAMP_SIDE_RIGHT = 0;

    //constant display String for ramp side
    /**
     * Ramp Side Left
     */
    public static final String STR_RAMP_SIDE_LEFT = "Left";
    /**
     * Ramp Side Right
     */
    public static final String STR_RAMP_SIDE_RIGHT = "Right";

    //constants for ramp metering
    /**
     * Ramp metering type - None
     */
    public final static int IDS_RAMP_METERING_TYPE_NONE = 0;

    /**
     * Ramp metering type - Fix
     */
    public final static int IDS_RAMP_METERING_TYPE_FIX = 1;

    /**
     * Ramp metering type - ALINEA
     */
    public final static int IDS_RAMP_METERING_TYPE_ALINEA = 2;

    /**
     * Ramp metering type - Fuzzy
     */
    public final static int IDS_RAMP_METERING_TYPE_FUZZY = 3;

    /**
     * Ramp metering type - None
     */
    public final static String STR_RAMP_METERING_TYPE_NONE = "None";

    /**
     * Ramp metering type - Fix
     */
    public final static String STR_RAMP_METERING_TYPE_FIX = "Fixed";

    /**
     * Ramp metering type - ALINEA
     */
    public final static String STR_RAMP_METERING_TYPE_ALINEA = "ALINEA";

    /**
     * Ramp metering type - Fuzzy
     */
    public final static String STR_RAMP_METERING_TYPE_FUZZY = "Fuzzy";

    //constants for managed lanes
    /**
     * Marking
     */
    public final static int ML_SEPARATION_MARKING = 0;

    /**
     * Buffer
     */
    public final static int ML_SEPARATION_BUFFER = 1;

    /**
     * Barrier
     */
    public final static int ML_SEPARATION_BARRIER = 2;

    /**
     * Marking
     */
    public final static String STR_ML_SEPARATION_MARKING = "Marking";

    /**
     * Buffer
     */
    public final static String STR_ML_SEPARATION_BUFFER = "Buffer";

    /**
     * Barrier
     */
    public final static String STR_ML_SEPARATION_BARRIER = "Barrier";

    //constant for calculation status
    /**
     * Calculation Status for Under Saturated Calculation
     */
    public static final int STATUS_UNDER = 0;
    /**
     * Calculation Status for Over Saturated Background Density Calculation
     */
    public static final int STATUS_BG = 1;
    /**
     * Calculation Status for Over Saturated Calculation using Under Saturated
     * Functions
     */
    public static final int STATUS_OVER_TO_UNDER = 2;

    //constant for compare zero
    /**
     * Tolerance when compare float point number to zero
     */
    public static final float ZERO = 0.001f;
    /**
     * Tolerance when compare float point number to maximum
     */
    public static final float MAX = 1e9f;

    //Scenario status
    /**
     * Scenario has input only
     */
    public static final int SCENARIO_INPUT_ONLY = 0;
    /**
     * Scenario has input and output
     */
    public static final int SCENARIO_HAS_OUTPUT = 1;

    //Return value
    /**
     * Analysis is successful
     */
    public static final int SUCCESS = 0;
    /**
     * Analysis failed
     */
    public static final int FAIL = 1;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SPECIAL OUTPUT">
    /**
     * String "N/A"
     */
    public final static String IDS_NA = "N/A";

    /**
     * String "N/A Special"
     */
    public final static String IDS_NA_SPECIAL = "N/A_S";

    /**
     * Blank String " "
     */
    public final static String IDS_BLANK = " ";

    /**
     * Dash String " "
     */
    public final static String IDS_DASH = "*";

    /**
     * Error String "ERROR"
     */
    public final static String IDS_ERROR = "ERROR";

    /**
     * Analysis period heading identifier
     */
    public final static String IDS_ANALYSIS_PERIOD_HEADING = "IDS_ANALYSIS_PERIOD_HEADING";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GENERAL INFORMATION - identifier constant strings">
    /**
     * Seed File Name
     */
    public final static String IDS_SEED_FILE_NAME = "IDS_SEED_FILE_NAME";

    /**
     * Project Name
     */
    public final static String IDS_PROJECT_NAME = "IDS_PROJECT_NAME";

    /**
     * Seed Urban-Rural Type
     */
    public final static String IDS_SEED_URBAN_RURAL_TYPE = "IDS_SEED_URBAN_RURAL_TYPE";

    /**
     * Number of Periods
     */
    public final static String IDS_NUM_PERIOD = "IDS_NUM_PERIOD";

    /**
     * Length of Analysis Period
     */
    public final static String IDS_PERIOD_TIME = "IDS_PERIOD_TIME";

    /**
     * Number of Segments
     */
    public final static String IDS_NUM_SEGMENT = "IDS_NUM_SEGMENT";

    /**
     * Start Time of Analysis Period
     */
    public final static String IDS_START_TIME = "IDS_START_TIME";

    /**
     * End Time of Analysis Period
     */
    public final static String IDS_END_TIME = "IDS_END_TIME";

    /**
     * Facility Wide Jam Density
     */
    public final static String IDS_JAM_DENSITY = "IDS_JAM_DENSITY";

    /**
     * Facility Wide - Average vehicle occupancy in general purpose lanes
     * (passenger / vehicle)
     */
    public final static String IDS_OCCU_GP = "IDS_OCCU_GP";

    /**
     * Facility Wide - Average vehicle occupancy in managed lanes (passenger /
     * vehicle)
     */
    public final static String IDS_OCCU_ML = "IDS_OCCU_ML";

    /**
     * Two Density Capacity Drop Percentage
     */
    public final static String IDS_CAPACITY_ALPHA = "IDS_CAPACITY_ALPHA";

    /**
     * Total Length of the Facility in Mile
     */
    public final static String IDS_TOTAL_LENGTH_MI = "IDS_TOTAL_LENGTH_MI";

    /**
     * Free flow speed known identifier
     */
    public final static String IDS_FFS_KNOWN = "IDS_FFS_KNOWN";

    /**
     * Managed lane used identifier
     */
    public final static String IDS_MANAGED_LANE_USED = "IDS_MANAGED_LANE_USED";

    /**
     * Seed demand date identifier
     */
    public final static String IDS_SEED_DEMAND_DATE = "IDS_SEED_DEMAND_DATE";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RELIABILITY ANALYSIS - identifier constant strings">
    /**
     * Number of Scenarios
     */
    public final static String IDS_NUM_SCEN = "IDS_NUM_SCEN";

    /**
     * Name of a Scenario
     */
    public final static String IDS_SCEN_NAME = "IDS_SCEN_NAME";

    /**
     * Detail of a Scenario
     */
    public final static String IDS_SCEN_DETAIL = "IDS_SCEN_DETAIL";

    /**
     * Status of a Scenario
     */
    public final static String IDS_SCENARIO_STATUS = "IDS_SCENARIO_STATUS";

    /**
     * Probability of a Scenario
     */
    public final static String IDS_SCEN_PROB = "IDS_SCEN_PROB";

    /**
     * Reliability Analysis Scenario CAF for GP segments
     */
    public final static String IDS_GP_RL_CAF = "IDS_GP_RL_CAF";

    /**
     * Reliability Analysis Scenario OAF for GP segments
     */
    public final static String IDS_GP_RL_OAF = "IDS_GP_RL_OAF";

    /**
     * Reliability Analysis Scenario DAF for GP segments
     */
    public final static String IDS_GP_RL_DAF = "IDS_GP_RL_DAF";

    /**
     * Reliability Analysis Scenario SAF for GP segments
     */
    public final static String IDS_GP_RL_SAF = "IDS_GP_RL_SAF";

    /**
     * Reliability Analysis Scenario LAF for GP segments
     */
    public final static String IDS_GP_RL_LAFI = "IDS_GP_RL_LAFI";

    /**
     * Reliability Analysis Scenario LAF for GP segments
     */
    public final static String IDS_GP_RL_LAFWZ = "IDS_GP_RL_LAFWZ";

    /**
     * Reliability Analysis Scenario CAF for ML segments
     */
    public final static String IDS_ML_RL_CAF = "IDS_ML_RL_CAF";

    /**
     * Reliability Analysis Scenario OAF for ML segments
     */
    public final static String IDS_ML_RL_OAF = "IDS_ML_RL_OAF";

    /**
     * Reliability Analysis Scenario DAF for ML segments
     */
    public final static String IDS_ML_RL_DAF = "IDS_ML_RL_DAF";

    /**
     * Reliability Analysis Scenario SAF for ML segments
     */
    public final static String IDS_ML_RL_SAF = "IDS_ML_RL_SAF";

    /**
     * Reliability Analysis Scenario LAF for ML segments
     */
    public final static String IDS_ML_RL_LAF = "IDS_ML_RL_LAF";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ATDM ANALYSIS - identifier constant strings">
    /**
     * Name of a ATDM
     */
    public final static String IDS_ATDM_NAME = "IDS_ATDM_NAME";

    /**
     * Detail of a ATDM
     */
    public final static String IDS_ATDM_DETAIL = "IDS_ATDM_DETAIL";

    /**
     * Number of ATDM in One Scenario
     */
    public final static String IDS_ATDM_NUM_EACH_RL_SCEN = "IDS_ATDM_NUM";

    /**
     * Number of ATDM Sets in One Seed
     */
    public final static String IDS_ATDM_SET_NUM = "IDS_ATDM_SET_NUM";

    /**
     * Number of Scenarios in Each ATDM Set
     */
    public final static String IDS_ATDM_SCEN_IN_EACH_SET = "IDS_ATDM_SCEN_IN_EACH_SET";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_WEATHER = "IDS_ATDM_STRAT_TYPE_WEATHER";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_DEMAND = "IDS_ATDM_STRAT_TYPE_DEMAND";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_INCIDENT = "IDS_ATDM_STRAT_TYPE_INCIDENT";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_WORKZONE = "IDS_ATDM_STRAT_TYPE_WORKZONE";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_RAMP_METERING = "IDS_ATDM_STRAT_TYPE_RAMP_METERING";

    /**
     * ATDM parameters identifier
     */
    public final static String IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING = "IDS_ATDM_STRAT_TYPE_HARD_SHOULDER_RUNNING";

    /**
     * ATDM Analysis Scenario CAF for GP segments
     */
    public final static String IDS_GP_ATDM_CAF = "IDS_ATDM_CAF_GP";

    /**
     * ATDM Analysis Scenario OAF for GP segments
     */
    public final static String IDS_GP_ATDM_OAF = "IDS_ATDM_OAF_GP";

    /**
     * ATDM Analysis Scenario DAF for GP segments
     */
    public final static String IDS_GP_ATDM_DAF = "IDS_ATDM_DAF_GP";

    /**
     * ATDM Analysis Scenario SAF for GP segments
     */
    public final static String IDS_GP_ATDM_SAF = "IDS_ATDM_SAF_GP";

    /**
     * ATDM Analysis Scenario LAF for GP segments
     */
    public final static String IDS_GP_ATDM_LAF = "IDS_ATDM_LAF_GP";

    /**
     * ATDM Analysis Scenario Ramp Metering Rate for GP segments
     */
    public final static String IDS_ATDM_RAMP_METERING_RATE_FIX = "IDS_ATDM_RM_GP";

    /**
     * ATDM Analysis Scenario CAF for ML segments
     */
    public final static String IDS_ML_ATDM_CAF = "IDS_ATDM_CAF_ML";

    /**
     * ATDM Analysis Scenario OAF for ML segments
     */
    public final static String IDS_ML_ATDM_OAF = "IDS_ATDM_OAF_ML";

    /**
     * ATDM Analysis Scenario DAF for ML segments
     */
    public final static String IDS_ML_ATDM_DAF = "IDS_ATDM_DAF_ML";

    /**
     * ATDM Analysis Scenario SAF for ML segments
     */
    public final static String IDS_ML_ATDM_SAF = "IDS_ATDM_SAF_ML";

    /**
     * ATDM Analysis Scenario LAF for ML segments
     */
    public final static String IDS_ML_ATDM_LAF = "IDS_ATDM_LAF_ML";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GENERAL PURPOSE SEGMENT - identifier constant strings">
    //Seed infomration
    // Basic Segment Variable Column Text
    /**
     * Segment Type
     */
    public final static String IDS_SEGMENT_TYPE = "IDS_SEGMENT_TYPE";

    /**
     * Segment Name
     */
    public final static String IDS_SEGMENT_NAME = "IDS_SEGMENT_NAME";

    /**
     * Seed Upstream Coordinate
     */
    public final static String IDS_UPSTREAM_COORDINATE = "IDS_UPSTREAM_COORDINATE";

    /**
     * Segment Endpoint Coordinate
     */
    public final static String IDS_SEGMENT_COORDINATE = "IDS_SEGMENT_COORDINATE";

    /**
     * Seed Upstream Latitude Coordinate
     */
    public final static String IDS_UPSTREAM_LATITUDE = "IDS_UPSTREAM_LATITUDE";

    /**
     * Seed Upstream Longitude Coordinate
     */
    public final static String IDS_UPSTREAM_LONGITUDE = "IDS_UPSTREAM_LONGITUDE";

    /**
     * Segment Endpoint Latitude Coordinate
     */
    public final static String IDS_SEGMENT_LATITUDE = "IDS_SEGMENT_LATITUDE";

    /**
     * Segment Endpoint Longitude Coordinate
     */
    public final static String IDS_SEGMENT_LONGITUDE = "IDS_SEGMENT_LONGITUDE";

    /**
     * Segment Length in Feet
     */
    public final static String IDS_SEGMENT_LENGTH_FT = "IDS_SEGMENT_LENGTH_FT";

    /**
     * Segment Length in Mile
     */
    public final static String IDS_SEGMENT_LENGTH_MI = "IDS_SEGMENT_LENGTH_MI";

    /**
     * Lane Width in Feet
     */
    public final static String IDS_LANE_WIDTH = "IDS_WIDT";

    /**
     * Lateral Clearance in Feet
     */
    public final static String IDS_LATERAL_CLEARANCE = "IDS_CLEA";

    /**
     * Terrain Type
     */
    public final static String IDS_TERRAIN = "IDS_TERR";

    /**
     * Truck - Passenger Car Equivalent
     */
    public final static String IDS_TRUCK_CAR_ET = "IDS_TCEQ";

    /**
     * Mainline Number of Lanes
     */
    public final static String IDS_MAIN_NUM_LANES_IN = "IDS_MAIN_NUM_LANES_IN";

    /**
     * Mainline Number of Lanes
     */
    public final static String IDS_MAIN_NUM_LANES_IN_AND_ATDM = "IDS_MAIN_NUM_LANES_IN_AND_ATDM";

    /**
     * Mainline Free Flow Speed
     */
    public final static String IDS_MAIN_FREE_FLOW_SPEED = "IDS_MAIN_FREE_FLOW_SPEED";

    /**
     * Mainline Demand in veh
     */
    public final static String IDS_MAIN_DEMAND_VEH = "IDS_MAIN_DEMAND_VEH";

    /**
     * Mainline Truck Single Unit Percentage
     */
    public final static String IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE = "IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE";

    /**
     * Mainline Truck Trailer Percentage
     */
    public final static String IDS_TRUCK_TRAILER_PCT_MAINLINE = "IDS_TRUCK_TRAILER_PCT_MAINLINE";

    /**
     * On Ramp Truck Single Unit Percentage
     */
    public final static String IDS_TRUCK_SINGLE_UNIT_PCT_ONR = "IDS_TRUCK_SINGLE_UNIT_PCT_ONR";

    /**
     * On Ramp Truck Trailer Percentage
     */
    public final static String IDS_TRUCK_TRAILER_PCT_ONR = "IDS_TRUCK_TRAILER_PCT_ONR";

    /**
     * Off Ramp Truck Single Unit Percentage
     */
    public final static String IDS_TRUCK_SINGLE_UNIT_PCT_OFR = "IDS_TRUCK_SINGLE_UNIT_PCT_OFR";

    /**
     * Off Ramp Truck Trailer Percentage
     */
    public final static String IDS_TRUCK_TRAILER_PCT_OFR = "IDS_TRUCK_TRAILER_PCT_OFR";

    /**
     * Seed/User CAF for GP segments
     */
    public final static String IDS_GP_USER_CAF = "IDS_GP_USER_CAF";

    /**
     * Seed/User OAF for GP segments
     */
    public final static String IDS_GP_USER_OAF = "IDS_GP_USER_OAF";

    /**
     * Seed/User DAF for GP segments
     */
    public final static String IDS_GP_USER_DAF = "IDS_GP_USER_DAF";

    /**
     * Seed/User SAF for GP segments
     */
    public final static String IDS_GP_USER_SAF = "IDS_GP_USER_SAF";

    /**
     * Seed/User DPCAF for GP segments
     */
    public final static String IDS_GP_USER_DPCAF = "IDS_GP_USER_DPCAF";

    /**
     * Seed/User DPSAF for GP segments
     */
    public final static String IDS_GP_USER_DPSAF = "IDS_GP_USER_DPSAF";

    /**
     * Acceleration/Deceleration Lane Length in Feet
     */
    public final static String IDS_ACC_DEC_LANE_LENGTH = "IDS_ACC_DEC_LANE_LENGTH";

    // On Ramp Variable Column Text
    /**
     * On Ramp Side
     */
    public final static String IDS_ON_RAMP_SIDE = "IDS_ON_RAMP_SIDE";

    /**
     * On Ramp Number of Lanes
     */
    public final static String IDS_NUM_ON_RAMP_LANES = "IDS_NUM_ON_RAMP_LANES";

    /**
     * On Ramp Demand in veh
     */
    public final static String IDS_ON_RAMP_DEMAND_VEH = "IDS_ON_RAMP_DEMAND_VEH";

    /**
     * On Ramp Queue Capacity in veh/lane
     */
    public final static String IDS_ON_RAMP_QUEUE_CAPACITY_VPL = "IDS_ON_RAMP_QUEUE_CAPACITY_VPL";

    /**
     * On Ramp Free Flow Speed
     */
    public final static String IDS_ON_RAMP_FREE_FLOW_SPEED = "IDS_ON_RAMP_FREE_FLOW_SPEED";

    /**
     * On Ramp Metering Fix Rate
     */
    public final static String IDS_ON_RAMP_METERING_RATE_FIX = "IDS_ON_RAMP_METERING_RATE_FIX";

    /**
     * On Ramp ALINEA Adaptive Metering
     */
    public final static String IDS_ON_RAMP_METERING_RATE_ALINEA_KEY = "IDS_ON_RAMP_METERING_RATE_ALINEA";

    /**
     * On Ramp Fuzzy Adaptive Metering
     */
    public final static String IDS_ON_RAMP_METERING_RATE_FUZZY_KEY = "IDS_ON_RAMP_METERING_RATE_FUZZY";

    // Off Ramp Variable Column Text
    /**
     * Off Ramp Side
     */
    public final static String IDS_OFF_RAMP_SIDE = "IDS_OFFRS";

    /**
     * Off Ramp Number of Lanes
     */
    public final static String IDS_NUM_OFF_RAMP_LANES = "IDS_NUM_OFF_RAMP_LANES";

    /**
     * Off Ramp Demand in veh
     */
    public final static String IDS_OFF_RAMP_DEMAND_VEH = "IDS_OFF_RAMP_DEMAND_VEH";

    /**
     * Off Ramp Free Flow Speed
     */
    public final static String IDS_OFF_RAMP_FREE_FLOW_SPEED = "IDS_OFF_RAMP_FREE_FLOW_SPEED";

    // Weaving Segment Variable Column Text
    /**
     * Length Short / Weaving Length in Feet
     */
    public final static String IDS_LENGTH_OF_WEAVING = "IDS_LOW";

    /**
     * Minimum Lane Change From On Ramp to Mainline
     */
    public final static String IDS_MIN_LANE_CHANGE_ONR_TO_FRWY = "IDS_MAINCOTF";

    /**
     * Minimum Lane Change From Mainline to Off Ramp
     */
    public final static String IDS_MIN_LANE_CHANGE_FRWY_TO_OFR = "IDS_MAINCFTO";

    /**
     * Minimum Lane Change From On Ramp to Off Ramp
     */
    public final static String IDS_MIN_LANE_CHANGE_ONR_TO_OFR = "IDS_MAINCOTO";

    /**
     * Number of Lanes Weaving
     */
    public final static String IDS_NUM_LANES_WEAVING = "IDS_NLW";

    /**
     * Ramp to Ramp Demand in veh
     */
    public final static String IDS_RAMP_TO_RAMP_DEMAND_VEH = "IDS_RAMP_TO_RAMP_DEMAND_VEH";

    /**
     * User defined ramp metering type
     */
    public final static String IDS_RAMP_METERING_TYPE = "IDS_RAMP_METERING_TYPE";

    /**
     * ATDM ramp metering type
     */
    public final static String IDS_ATDM_RAMP_METERING_TYPE = "IDS_ATDM_RAMP_METERING_TYPE";

    //Cross Weave Parameters
    /**
     * Whether The Segment Has Cross Weave Effect
     */
    public final static String IDS_HAS_CROSS_WEAVE = "IDS_HAS_CROSS_WEAVE";

    /**
     * Managed Lane Cross Weave Minium Lane Change
     */
    public final static String IDS_CROSS_WEAVE_LC_MIN = "IDS_CROSS_WEAVE_LC_MIN";

    /**
     * Managed Lane Cross Weave Volume
     */
    public final static String IDS_CROSS_WEAVE_VOLUME = "IDS_CROSS_WEAVE_VOLUME";

    /**
     * Cross Weave Capacity Adjustment Factor (Apply on GP when ML is used and
     * has cross weave effect)
     */
    public final static String IDS_CROSS_WEAVE_CAF = "IDS_CROSS_WEAVE_CAF";

    // Basic Segment Output Column Text
    /**
     * Segment Type Used
     */
    public final static String IDS_TYPE_USED = "IDS_TYPE_USED";

    /**
     * Output Speed in mph
     */
    public final static String IDS_SPEED = "IDS_SPEED";

    /**
     * Output Density in veh/mi/ln
     */
    public final static String IDS_TOTAL_DENSITY_VEH = "IDS_TOTAL_DENSITY_VEH";

    /**
     * Output Density in pc/mi/ln
     */
    public final static String IDS_TOTAL_DENSITY_PC = "IDS_TOTAL_DENSITY_PC";

    /**
     * Output Influenced Area Density pc/mi/ln
     */
    public final static String IDS_INFLUENCED_DENSITY_PC = "IDS_INFLUENCED_DENSITY_PC";

    /**
     * Output Occupancy Percent
     */
    public final static String IDS_OCCUPANCY_PCT = "IDS_OCCUPANCY_PCT";

    /**
     * Output Adjusted Mainline Demand in vph
     */
    public final static String IDS_ADJUSTED_MAIN_DEMAND = "IDS_ADJUSTED_DEMAND";

    /**
     * Output Mainline Capacity in vph
     */
    public final static String IDS_MAIN_CAPACITY = "IDS_CAPACITY";

    /**
     * Output Mainline Volume Served
     */
    public final static String IDS_MAIN_VOLUME_SERVED = "IDS_MAIN_VOLUME_SERVED";

    /**
     * Output Demand/Capacity Ratio
     */
    public final static String IDS_DC = "IDS_DC";

    /**
     * Output Volume/Capacity Ratio
     */
    public final static String IDS_VC = "IDS_VC";

    /**
     * Output Density Based LOS
     */
    public final static String IDS_DENSITY_BASED_LOS = "IDS_DENSITY_BASED_LOS";

    /**
     * Output Demand Based LOS
     */
    public final static String IDS_DEMAND_BASED_LOS = "IDS_DEMAND_BASED_LOS";

    /**
     * Output Queue Length In Feet
     */
    public final static String IDS_QUEUE_LENGTH = "IDS_QUEUE_LENGTH";

    /**
     * Output Queue Percentage
     */
    public final static String IDS_QUEUE_PERCENTAGE = "IDS_QUEUE_PERCENTAGE";

    /**
     * Output On Ramp Queue In Number Of Vehicles
     */
    public final static String IDS_ON_QUEUE_VEH = "IDS_ON_QUEUE_VEH";

    /**
     * Output Actual Travel Time in Minute
     */
    public final static String IDS_ACTUAL_TIME = "IDS_ACTUAL_TIME";

    /**
     * Output Free Flow Speed Travel Time in Minute
     */
    public final static String IDS_FFS_TIME = "IDS_FFS_TIME";

    /**
     * Output Mainline Delay in Minute
     */
    public final static String IDS_MAINLINE_DELAY = "IDS_MAINLINE_DELAY";

    /**
     * Output VMTD
     */
    public final static String IDS_VMTD = "IDS_VMTD";

    /**
     * Output VMTV
     */
    public final static String IDS_VMTV = "IDS_VMTV";

    /**
     * Output PMTD
     */
    public final static String IDS_PMTD = "IDS_PMTD";

    /**
     * Output PMTV
     */
    public final static String IDS_PMTV = "IDS_PMTV";

    /**
     * Output VHT
     */
    public final static String IDS_VHT = "IDS_VHT";

    /**
     * Output VHD-M
     */
    public final static String IDS_VHD_M = "IDS_VHD_M";

    /**
     * Output VHD-R
     */
    public final static String IDS_VHD_R = "IDS_VHD_R";

    /**
     * Output VHD-ACCESS
     */
    public final static String IDS_VHD_ACCESS = "IDS_VHD_ACCESS";

    /**
     * Output VHD
     */
    public final static String IDS_VHD_MDE = "IDS_VHD_MDE";

    /**
     * Output VHD
     */
    public final static String IDS_VHD = "IDS_VHD";

    /**
     * Output Space Mean Speed
     */
    public final static String IDS_SPACE_MEAN_SPEED = "IDS_SPACE_MEAN_SPEED";

    /**
     * Output Travel Time Index
     */
    public final static String IDS_TRAVEL_TIME_INDEX = "IDS_TRAVEL_TIME_INDEX";

    // Special Output Column Text
    /**
     * Output On/Off Ramp Capacity in vph
     */
    public final static String IDS_ON_RAMP_CAPACITY = "IDS_ON_RAMP_CAPACITY";

    /**
     * Output Adjusted On Ramp Demand in vph
     */
    public final static String IDS_ADJUSTED_ON_RAMP_DEMAND = "IDS_ADJUSTED_ON_RAMP_DEMAND";

    /**
     * Output On Ramp Volume Served in vph
     */
    public final static String IDS_ON_RAMP_VOLUME_SERVED = "IDS_ON_RAMP_VOLUME_SERVED";

    /**
     * Output On Ramp Time Metered in a Period
     */
    public final static String IDS_ON_RAMP_TIME_METERED = "IDS_ON_RAMP_TIME_METERED";

    /**
     * Output On Ramp Average Metering Rate in a Period
     */
    public final static String IDS_ON_RAMP_AVG_METERING_RATE = "IDS_ON_RAMP_AVG_METERING_RATE";

    /**
     * Output Off Ramp Capacity in vph
     */
    public final static String IDS_OFF_RAMP_CAPACITY = "IDS_OFF_RAMP_CAPACITY";

    /**
     * Output Adjusted Off Ramp Demand in vph
     */
    public final static String IDS_ADJUSTED_OFF_RAMP_DEMAND = "IDS_ADJUSTED_OFF_RAMP_DEMAND";

    /**
     * Output Off Ramp Volume Served in vph
     */
    public final static String IDS_OFF_RAMP_VOLUME_SERVED = "IDS_OFF_RAMP_VOLUME_SERVED";

    // Facility Summary Output Text
    /**
     * Period Summary
     */
    public final static String IDS_P_ACTUAL_TIME = "IDS_P_ACTUAL_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_P_FFS_TIME = "IDS_P_FFS_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_P_MAIN_DELAY = "IDS_P_MAIN_DELAY";

    /**
     * Period Summary
     */
    public final static String IDS_P_ONR_DELAY = "IDS_P_ONR_DELAY";

    /**
     * Period Summary
     */
    //public final static String IDS_P_SYS_DELAY = "IDS_P_SYS_DELAY";
    /**
     * Period Summary
     */
    public final static String IDS_P_VMTD = "IDS_P_VMTD";

    /**
     * Period Summary
     */
    public final static String IDS_P_VMTV = "IDS_P_VMTV";

    /**
     * Period Summary
     */
    public final static String IDS_P_PMTD = "IDS_P_PMTD";

    /**
     * Period Summary
     */
    public final static String IDS_P_PMTV = "IDS_P_PMTV";

    /**
     * Period Summary
     */
    public final static String IDS_P_VHT = "IDS_P_VHT";

    /**
     * Period Summary
     */
    public final static String IDS_P_VHD = "IDS_P_VHD";

    /**
     * Period Summary
     */
    public final static String IDS_P_SPACE_MEAN_SPEED = "IDS_P_SPACE_MEAN_SPEED";

    /**
     * Period Summary
     */
    public final static String IDS_P_TOTAL_DENSITY_VEH = "IDS_P_TOTAL_DENSITY_VEH";

    /**
     * Period Summary
     */
    public final static String IDS_P_TOTAL_DENSITY_PC = "IDS_P_TOTAL_DENSITY_PC";

    /**
     * Period Summary
     */
    public final static String IDS_P_REPORT_LOS = "IDS_P_REPORT_LOS";

    /**
     * Period Summary
     */
    public final static String IDS_P_TTI = "IDS_P_TTI";

    /**
     * Period Summary
     */
    public final static String IDS_P_MAX_DC = "IDS_P_MAX_DC";

    /**
     * Period Summary
     */
    public final static String IDS_P_MAX_VC = "IDS_P_MAC_VC";

    /**
     * Period Summary
     */
    public final static String IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT = "IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_P_TOTAL_DENY_QUEUE_VEH = "IDS_P_TOTAL_DENY_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_P_TOTAL_ON_QUEUE_VEH = "IDS_P_TOTAL_ON_QUEUE_VEH";

    /**
     * Segment Summary
     */
    public final static String IDS_S_ACTUAL_TIME = "IDS_S_ACTUAL_TIME";

    /**
     * Segment Summary
     */
    public final static String IDS_S_VMTD = "IDS_S_VMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_S_VMTV = "IDS_S_VMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_S_PMTD = "IDS_S_PMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_S_PMTV = "IDS_S_PMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_S_VHT = "IDS_S_VHT";

    /**
     * Segment Summary
     */
    public final static String IDS_S_VHD = "IDS_S_VHD";

    /**
     * Segment Summary
     */
    public final static String IDS_S_SPACE_MEAN_SPEED = "IDS_S_SPACE_MEAN_SPEED";

    /**
     * Segment Summary
     */
    public final static String IDS_S_REPORT_DENSITY_PC = "IDS_S_REPORT_DENSITY_PC";

    /**
     * Segment Summary
     */
    public final static String IDS_S_MAX_DC = "IDS_S_MAX_DC";

    /**
     * Segment Summary
     */
    public final static String IDS_S_MAX_VC = "IDS_S_MAC_VC";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_ACTUAL_TIME = "IDS_SP_ACTUAL_TIME";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_VMTD = "IDS_SP_VMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_VMTV = "IDS_SP_VMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_PMTD = "IDS_SP_PMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_PMTV = "IDS_SP_PMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_VHT = "IDS_SP_VHT";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_VHD = "IDS_SP_VHD";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_SPACE_MEAN_SPEED = "IDS_SP_SPACE_MEAN_SPEED";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_REPORT_DENSITY_PC = "IDS_SP_REPORT_DENSITY_PC";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_MAX_DC = "IDS_SP_MAX_DC";

    /**
     * Facility Summary
     */
    public final static String IDS_SP_MAX_VC = "IDS_SP_MAC_VC";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MANAGED LANES SEGMENT - identifier constant strings">
    // Basic Segment Variable Column Text
    /**
     * Segment Type
     */
    public final static String IDS_ML_SEGMENT_TYPE = "IDS_ML_SEGMENT_TYPE";

    /**
     * Segment Separation Type
     */
    public final static String IDS_ML_SEPARATION_TYPE = "IDS_ML_SEPARATION_TYPE";

    /**
     * Segment Length in Feet
     */
    public final static String IDS_ML_SEGMENT_LENGTH_FT = "IDS_ML_SEGMENT_LENGTH_FT";

    /**
     * Segment Length in Mile
     */
    public final static String IDS_ML_SEGMENT_LENGTH_MI = "IDS_ML_SEGMENT_LENGTH_MI";

    /**
     * Mainline Number of Lanes
     */
    public final static String IDS_ML_NUM_LANES = "IDS_ML_NUM_LANES";

    /**
     * Mainline Free Flow Speed
     */
    public final static String IDS_ML_FREE_FLOW_SPEED = "IDS_ML_FREE_FLOW_SPEED";

    /**
     * Mainline Demand in veh
     */
    public final static String IDS_ML_DEMAND_VEH = "IDS_ML_DEMAND_VEH";

    /**
     * Mainline/On Ramp/Off Ramp Truck Single Percentage
     */
    public final static String IDS_ML_TRUCK_SINGLE_PERCENTAGE = "IDS_ML_TRUCK_SINGLE_PERCENTAGE";

    /**
     * Mainline/On Ramp/Off Ramp Truck Trailer Percentage
     */
    public final static String IDS_ML_TRUCK_TRAILER_PERCENTAGE = "IDS_ML_TRUCK_TRAILER_PERCENTAGE";

    /**
     * Seed/User CAF
     */
    public final static String IDS_ML_USER_CAF = "IDS_ML_USER_CAF";

    /**
     * Seed/User OAF
     */
    public final static String IDS_ML_USER_OAF = "IDS_ML_USER_OAF";

    /**
     * Seed/User DAF
     */
    public final static String IDS_ML_USER_DAF = "IDS_ML_USER_DAF";

    /**
     * Seed/User SAF
     */
    public final static String IDS_ML_USER_SAF = "IDS_ML_USER_SAF";

    /**
     * Acceleration/Deceleration Lane Length in Feet
     */
    public final static String IDS_ML_ACC_DEC_LANE_LENGTH = "IDS_ML_ACC_DEC_LANE_LENGTH";

    // On Ramp Variable Column Text
    /**
     * On Ramp Side
     */
    public final static String IDS_ML_ON_RAMP_SIDE = "IDS_ML_ON_RAMP_SIDE";

    /**
     * On Ramp Number of Lanes
     */
    public final static String IDS_ML_NUM_ON_RAMP_LANES = "IDS_ML_NUM_ON_RAMP_LANES";

    /**
     * On Ramp Demand in veh
     */
    public final static String IDS_ML_ON_RAMP_DEMAND_VEH = "IDS_ML_ON_RAMP_DEMAND_VEH";

    /**
     * On Ramp Free Flow Speed
     */
    public final static String IDS_ML_ON_RAMP_FREE_FLOW_SPEED = "IDS_ML_ON_RAMP_FREE_FLOW_SPEED";

    // Off Ramp Variable Column Text
    /**
     * Off Ramp Side
     */
    public final static String IDS_ML_OFF_RAMP_SIDE = "IDS_ML_OFFRS";

    /**
     * Off Ramp Number of Lanes
     */
    public final static String IDS_ML_NUM_OFF_RAMP_LANES = "IDS_ML_NUM_OFF_RAMP_LANES";

    /**
     * Off Ramp Demand in veh
     */
    public final static String IDS_ML_OFF_RAMP_DEMAND_VEH = "IDS_ML_OFF_RAMP_DEMAND_VEH";

    /**
     * Off Ramp Free Flow Speed
     */
    public final static String IDS_ML_OFF_RAMP_FREE_FLOW_SPEED = "IDS_ML_OFF_RAMP_FREE_FLOW_SPEED";

    // Weaving Segment Variable Column Text
    /**
     * Length Short / Weaving Length in Feet
     */
    public final static String IDS_ML_LENGTH_SHORT = "IDS_ML_LENGTH_SHORT";

    /**
     * Minimum Lane Change From On Ramp to Mainline
     */
    public final static String IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY = "IDS_ML_MAINCOTF";

    /**
     * Minimum Lane Change From Mainline to Off Ramp
     */
    public final static String IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR = "IDS_ML_MAINCFTO";

    /**
     * Minimum Lane Change From On Ramp to Off Ramp
     */
    public final static String IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR = "IDS_ML_MAINCOTO";

    /**
     * Number of Lanes Weaving
     */
    public final static String IDS_ML_NUM_LANES_WEAVING = "IDS_ML_NLW";

    /**
     * Ramp to Ramp Demand in veh
     */
    public final static String IDS_ML_RAMP_TO_RAMP_DEMAND_VEH = "IDS_ML_RAMP_TO_RAMP_DEMAND_VEH";

    /**
     * Minimum Lane Change between GP and ML
     */
    public final static String IDS_ML_MIN_LANE_CHANGE_ML = "IDS_ML_MIN_LANE_CHANGE_ML";

    /**
     * Maximum Lane Change between GP and ML
     */
    public final static String IDS_ML_MAX_LANE_CHANGE_ML = "IDS_ML_MAX_LANE_CHANGE_ML";

    // Basic Segment Output Column Text
    /**
     * Segment Type Used
     */
    public final static String IDS_ML_TYPE_USED = "IDS_ML_TYPE_USED";

    /**
     * Output Speed in mph
     */
    public final static String IDS_ML_SPEED = "IDS_ML_SPEED";

    /**
     * Output Density in veh/mi/ln
     */
    public final static String IDS_ML_TOTAL_DENSITY_VEH = "IDS_ML_TOTAL_DENSITY_VEH";

    /**
     * Output Density in pc/mi/ln
     */
    public final static String IDS_ML_TOTAL_DENSITY_PC = "IDS_ML_TOTAL_DENSITY_PC";

    /**
     * Output Influenced Area Density pc/mi/ln
     */
    public final static String IDS_ML_INFLUENCED_DENSITY_PC = "IDS_ML_INFLUENCED_DENSITY_PC";

    /**
     * Output ML Occupancy Percent
     */
    public final static String IDS_ML_OCCUPANCY_PCT = "IDS_ML_OCCUPANCY_PCT";

    /**
     * Output Adjusted Mainline Demand in vph
     */
    public final static String IDS_ML_ADJUSTED_MAIN_DEMAND = "IDS_ML_ADJUSTED_DEMAND";

    /**
     * Output Mainline Capacity in vph
     */
    public final static String IDS_ML_MAIN_CAPACITY = "IDS_ML_CAPACITY";

    /**
     * Output Mainline Volume Served
     */
    public final static String IDS_ML_MAIN_VOLUME_SERVED = "IDS_ML_MAIN_VOLUME_SERVED";

    /**
     * Output Demand/Capacity Ratio
     */
    public final static String IDS_ML_DC = "IDS_ML_DC";

    /**
     * Output Volume/Capacity Ratio
     */
    public final static String IDS_ML_VC = "IDS_ML_VC";

    /**
     * Output Density Based LOS
     */
    public final static String IDS_ML_DENSITY_BASED_LOS = "IDS_ML_DENSITY_BASED_LOS";

    /**
     * Output Demand Based LOS
     */
    public final static String IDS_ML_DEMAND_BASED_LOS = "IDS_ML_DEMAND_BASED_LOS";

    /**
     * Output Queue Length In Feet
     */
    public final static String IDS_ML_QUEUE_LENGTH = "IDS_ML_QUEUE_LENGTH";

    /**
     * Output Queue Percentage
     */
    public final static String IDS_ML_QUEUE_PERCENTAGE = "IDS_ML_QUEUE_PERCENTAGE";

    /**
     * Output On Ramp Queue In Number Of Vehicles
     */
    public final static String IDS_ML_ON_QUEUE_VEH = "IDS_ML_ON_QUEUE_VEH";

    /**
     * Output Actual Travel Time in Minute
     */
    public final static String IDS_ML_ACTUAL_TIME = "IDS_ML_ACTUAL_TIME";

    /**
     * Output Free Flow Speed Travel Time in Minute
     */
    public final static String IDS_ML_FFS_TIME = "IDS_ML_FFS_TIME";

    /**
     * Output Mainline Delay in Minute
     */
    public final static String IDS_ML_MAINLINE_DELAY = "IDS_ML_MAINLINE_DELAY";

    /**
     * Output VMTD
     */
    public final static String IDS_ML_VMTD = "IDS_ML_VMTD";

    /**
     * Output VMTV
     */
    public final static String IDS_ML_VMTV = "IDS_ML_VMTV";

    /**
     * Output PMTD
     */
    public final static String IDS_ML_PMTD = "IDS_ML_PMTD";

    /**
     * Output PMTV
     */
    public final static String IDS_ML_PMTV = "IDS_ML_PMTV";

    /**
     * Output VHT
     */
    public final static String IDS_ML_VHT = "IDS_ML_VHT";

    /**
     * Output VHD
     */
    public final static String IDS_ML_VHD_M = "IDS_ML_VHD_M";

    /**
     * Output VHD
     */
    public final static String IDS_ML_VHD_R = "IDS_ML_VHD_R";

    /**
     * Output VHD
     */
    public final static String IDS_ML_VHD_ACCESS = "IDS_ML_VHD_ACCESS";

    /**
     * Output VHD
     */
    public final static String IDS_ML_VHD_MDE = "IDS_ML_VHD_MDE";

    /**
     * Output VHD
     */
    public final static String IDS_ML_VHD = "IDS_ML_VHD";

    /**
     * Output Space Mean Speed
     */
    public final static String IDS_ML_SPACE_MEAN_SPEED = "IDS_ML_SPACE_MEAN_SPEED";

    /**
     * Output Travel Time Index
     */
    public final static String IDS_ML_TRAVEL_TIME_INDEX = "IDS_ML_TRAVEL_TIME_INDEX";

    // Special Output Column Text
    /**
     * Output On/Off Ramp Capacity in vph
     */
    public final static String IDS_ML_ON_RAMP_CAPACITY = "IDS_ML_ON_RAMP_CAPACITY";

    /**
     * Output Adjusted On Ramp Demand in vph
     */
    public final static String IDS_ML_ADJUSTED_ON_RAMP_DEMAND = "IDS_ML_ADJUSTED_ON_RAMP_DEMAND";

    /**
     * Output On Ramp Volume Served in vph
     */
    public final static String IDS_ML_ON_RAMP_VOLUME_SERVED = "IDS_ML_ON_RAMP_VOLUME_SERVED";

    /**
     * Output Off Ramp Capacity in vph
     */
    public final static String IDS_ML_OFF_RAMP_CAPACITY = "IDS_ML_OFF_RAMP_CAPACITY";

    /**
     * Output Adjusted Off Ramp Demand in vph
     */
    public final static String IDS_ML_ADJUSTED_OFF_RAMP_DEMAND = "IDS_ML_ADJUSTED_OFF_RAMP_DEMAND";

    /**
     * Output Off Ramp Volume Served in vph
     */
    public final static String IDS_ML_OFF_RAMP_VOLUME_SERVED = "IDS_ML_OFF_RAMP_VOLUME_SERVED";

    // Facility Summary Output Text
    /**
     * Period Summary
     */
    public final static String IDS_ML_P_ACTUAL_TIME = "IDS_ML_P_ACTUAL_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_FFS_TIME = "IDS_ML_P_FFS_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_MAIN_DELAY = "IDS_ML_P_MAIN_DELAY";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_ONR_DELAY = "IDS_ML_P_ONR_DELAY";

    /**
     * Period Summary
     */
    //public final static String IDS_ML_P_SYS_DELAY = "IDS_ML_P_SYS_DELAY";
    /**
     * Period Summary
     */
    public final static String IDS_ML_P_VMTD = "IDS_ML_P_VMTD";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_VMTV = "IDS_ML_P_VMTV";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_PMTD = "IDS_ML_P_PMTD";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_PMTV = "IDS_ML_P_PMTV";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_VHT = "IDS_ML_P_VHT";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_VHD = "IDS_ML_P_VHD";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_SPACE_MEAN_SPEED = "IDS_ML_P_SPACE_MEAN_SPEED";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TOTAL_DENSITY_VEH = "IDS_ML_P_TOTAL_DENSITY_VEH";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TOTAL_DENSITY_PC = "IDS_ML_P_TOTAL_DENSITY_PC";

    /**
     * Period Summary
     */
    //public final static String IDS_ML_P_INFLUENCED_DENSITY_PC = "IDS_ML_P_INFLUENCED_DENSITY_PC";
    /**
     * Period Summary
     */
    public final static String IDS_ML_P_REPORT_LOS = "IDS_ML_P_REPORT_LOS";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TTI = "IDS_ML_P_TTI";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_MAX_DC = "IDS_ML_P_MAX_DC";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_MAX_VC = "IDS_ML_P_MAC_VC";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT = "IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TOTAL_DENY_QUEUE_VEH = "IDS_ML_P_TOTAL_DENY_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT = "IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_ACTUAL_TIME = "IDS_ML_S_ACTUAL_TIME";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_VMTD = "IDS_ML_S_VMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_VMTV = "IDS_ML_S_VMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_PMTD = "IDS_ML_S_PMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_PMTV = "IDS_ML_S_PMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_VHT = "IDS_ML_S_VHT";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_VHD = "IDS_ML_S_VHD";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_SPACE_MEAN_SPEED = "IDS_ML_S_SPACE_MEAN_SPEED";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_REPORT_DENSITY_PC = "IDS_ML_S_REPORT_DENSITY_PC";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_MAX_DC = "IDS_ML_S_MAX_DC";

    /**
     * Segment Summary
     */
    public final static String IDS_ML_S_MAX_VC = "IDS_ML_S_MAC_VC";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_ACTUAL_TIME = "IDS_ML_SP_ACTUAL_TIME";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_VMTD = "IDS_ML_SP_VMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_VMTV = "IDS_ML_SP_VMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_PMTD = "IDS_ML_SP_PMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_PMTV = "IDS_ML_SP_PMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_VHT = "IDS_ML_SP_VHT";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_VHD = "IDS_ML_SP_VHD";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_SPACE_MEAN_SPEED = "IDS_ML_SP_SPACE_MEAN_SPEED";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_REPORT_DENSITY_PC = "IDS_ML_SP_REPORT_DENSITY_PC";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_MAX_DC = "IDS_ML_SP_MAX_DC";

    /**
     * Facility Summary
     */
    public final static String IDS_ML_SP_MAX_VC = "IDS_ML_SP_MAC_VC";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="COMBINED REPORT - identifier constant strings">
    // Facility Summary Output Text
    /**
     * Period Summary
     */
    public final static String IDS_CB_P_ACTUAL_TIME = "IDS_CB_P_ACTUAL_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_FFS_TIME = "IDS_CB_P_FFS_TIME";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_MAIN_DELAY = "IDS_CB_P_MAIN_DELAY";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_ONR_DELAY = "IDS_CB_P_ONR_DELAY";

    /**
     * Period Summary
     */
    //public final static String IDS_CB_P_SYS_DELAY = "IDS_CB_P_SYS_DELAY";
    /**
     * Period Summary
     */
    public final static String IDS_CB_P_VMTD = "IDS_CB_P_VMTD";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_VMTV = "IDS_CB_P_VMTV";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_PMTD = "IDS_CB_P_PMTD";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_PMTV = "IDS_CB_P_PMTV";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_VHT = "IDS_CB_P_VHT";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_VHD = "IDS_CB_P_VHD";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_SPACE_MEAN_SPEED = "IDS_CB_P_SPACE_MEAN_SPEED";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TOTAL_DENSITY_VEH = "IDS_CB_P_TOTAL_DENSITY_VEH";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TOTAL_DENSITY_PC = "IDS_CB_P_TOTAL_DENSITY_PC";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_REPORT_LOS = "IDS_CB_P_REPORT_LOS";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TTI = "IDS_CB_P_TTI";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_MAX_DC = "IDS_CB_P_MAX_DC";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_MAX_VC = "IDS_CB_P_MAC_VC";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TOTAL_MAIN_QUEUE_LENGTH_FT = "IDS_CB_P_TOTAL_MAIN_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TOTAL_DENY_QUEUE_VEH = "IDS_CB_P_TOTAL_DENY_QUEUE_LENGTH_FT";

    /**
     * Period Summary
     */
    public final static String IDS_CB_P_TOTAL_ON_QUEUE_LENGTH_FT = "IDS_CB_P_TOTAL_ON_QUEUE_LENGTH_FT";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_ACTUAL_TIME = "IDS_CB_S_ACTUAL_TIME";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_VMTD = "IDS_CB_S_VMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_VMTV = "IDS_CB_S_VMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_PMTD = "IDS_CB_S_PMTD";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_PMTV = "IDS_CB_S_PMTV";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_VHT = "IDS_CB_S_VHT";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_VHD = "IDS_CB_S_VHD";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_SPACE_MEAN_SPEED = "IDS_CB_S_SPACE_MEAN_SPEED";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_REPORT_DENSITY_PC = "IDS_CB_S_REPORT_DENSITY_PC";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_MAX_DC = "IDS_CB_S_MAX_DC";

    /**
     * Segment Summary
     */
    public final static String IDS_CB_S_MAX_VC = "IDS_CB_S_MAC_VC";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_ACTUAL_TIME = "IDS_CB_SP_ACTUAL_TIME";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_VMTD = "IDS_CB_SP_VMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_VMTV = "IDS_CB_SP_VMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_PMTD = "IDS_CB_SP_PMTD";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_PMTV = "IDS_CB_SP_PMTV";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_VHT = "IDS_CB_SP_VHT";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_VHD = "IDS_CB_SP_VHD";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_SPACE_MEAN_SPEED = "IDS_CB_SP_SPACE_MEAN_SPEED";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_REPORT_DENSITY_PC = "IDS_CB_SP_REPORT_DENSITY_PC";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_MAX_DC = "IDS_CB_SP_MAX_DC";

    /**
     * Facility Summary
     */
    public final static String IDS_CB_SP_MAX_VC = "IDS_CB_SP_MAC_VC";
    // </editor-fold>
}
