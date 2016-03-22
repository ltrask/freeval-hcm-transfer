package coreEngine.Helper.ASCIIAdapter;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CEDate;
import coreEngine.Helper.CETime;
import coreEngine.Seed;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * (Deprecated, used for import old format ASCII only) This class import/export
 * a Seed instance from/to an ASCII file using FREEVAL_RL format. The
 * ASCII(text) input file contains seed input data for GP segments, but ML, RL
 * and ATDM are not included.
 *
 * @author Tristan Dyer
 * @author Shu Liu
 */
public class ASCIISeedFileAdapter_RLFormat {

    private enum HeaderType {

        GENERAL_DATA, FACILITY_GEOMETRY, MAINLINE_DEMAND,
        RAMP_ENTRY_DEMAND, RAMP_EXIT_DEMAND, RAMP_RAMP_DEMAND,
        RAMP_METERING_RATE, CAF, ODAF, DDAF, SAF,
        TRUCK_MAINLINE, RV_MAINLINE, TRUCK_ONR, RV_ONR,
        TRUCK_OFR, RV_OFR, TRUCK_CAR_EQ, RV_CAR_EQ,
        LATERAL_CLEARANCE, WEAVE_LS, WEAVE_LCRF, WEAVE_LCFR,
        WEAVE_LCRR, WEAVE_NW, WEAVE_ONE_SIDED, ERR;
    }

    private enum FieldTypeGen {

        ALPHA, ANALYSIS_YEAR, END_DAY, END_HR, END_MIN, END_MONTH,
        FFS_KNOWN, JAM_DENSITY, NUM_PERIODS, NUM_SEGMENTS,
        RAMP_METERING, SEED_DEMAND_DAY, SEED_DEMAND_MONTH,
        START_DAY, START_HR, START_MIN, START_MONTH,
        TERRAIN_CODE, ERR;
    }

    private enum FieldTypeGeo {

        ACCEL_DECEL_LENGTH, FFS, LANE_WIDTH, NUM_LANES_ML,
        NUM_LANES_RAMP, RAMP_FFS, RAMP_SIDE, SEGMENT_LENGTHS,
        SEGMENT_NUMBER, SEGMENT_TYPES, ERR;
    }

    /* Delimiters to use when splitting strings */
    private final static String delimiters = "[=]|\\s+";

    /* Minimum header equivalences for reading ASCII seed files */
    private final static String CAF_HDR = "Capacity Adjustment Factor";
    private final static String DDAF_HDR = "Destination Demand Adjustment Factor";
    private final static String FACILITY_GEOMETRY_HDR = "Facility Geometry";
    private final static String GENERAL_HDR = "Seed File Data";
    private final static String LATERAL_CLEARANCE_HDR = "Lateral Clearance";
    private final static String MAINLINE_DEMAND_HDR = "Mainline Entry Demand";
    private final static String ODAF_HDR = "Origin Demand Adjustment Factor";
    private final static String RAMP_ENTRY_HDR = "Ramp Entry Demand";
    private final static String RAMP_EXIT_HDR = "Ramp Exit Demand";
    private final static String RAMP_METERING_HDR = "Ramp Metering Rate";
    private final static String RAMP_RAMP_HDR = "Ramp to Ramp Demand";
    private final static String RV_CAR_EQ_HDR = "R.V. Passenger Car Equivalent";
    private final static String RV_MAINLINE_HDR = "% RV on the Mainline";
    private final static String RV_OFR_HDR = "OFR % RV";
    private final static String RV_ONR_HDR = "ONR % RV";
    private final static String SAF_HDR = "Speed Adjustment Factor";
    private final static String TRUCK_CAR_EQ_HDR = "Truck Passenger Car Equivalent";
    private final static String TRUCK_MAINLINE_HDR = "% Truck on the Mainline";
    private final static String TRUCK_OFR_HDR = "OFR % Truck";
    private final static String TRUCK_ONR_HDR = "ONR % Truck";
    private final static String WEAVE_LS_HDR = "Weave Setting (Ls)";
    private final static String WEAVE_LCRF_HDR = "Weave Setting (LCRF)";
    private final static String WEAVE_LCFR_HDR = "Weave Setting (LCFR)";
    private final static String WEAVE_LCRR_HDR = "Weave Setting (LCRR)";
    private final static String WEAVE_NW_HDR = "Weave Setting (NW)";
    private final static String WEAVE_ONE_SIDED_HDR = "Weave Setting (One Sided)";

    /* Text values for general data */
    private final static String STR_ALPHA = "% Alpha";
    private final static String STR_ANALYSIS_YEAR = "Analysis Year";
    private final static String STR_END_DAY = "RRP End Day";
    private final static String STR_END_HR = "SP End Hour";
    private final static String STR_END_MIN = "SP End Min";
    private final static String STR_END_MONTH = "RRP End Month";
    private final static String STR_FFS_KNOWN = "FFS Known?";
    private final static String STR_JAM_DENSITY = "Jam Density";
    private final static String STR_NUM_PERIODS = "# of APs";
    private final static String STR_NUM_SEGMENTS = "# of HCM Segments";
    private final static String STR_RAMP_METERING = "Ramp Metering?";
    private final static String STR_SEED_DEMAND_DAY = "Seed Demand Day";
    private final static String STR_SEED_DEMAND_MONTH = "Seed Demand Month";
    private final static String STR_START_DAY = "RRP Start Day";
    private final static String STR_START_HR = "SP Start Hour";
    private final static String STR_START_MIN = "SP Start Min";
    private final static String STR_START_MONTH = "RRP Start Month";
    private final static String STR_TERRAIN_CODE = "Terrain Code";

    /* Text values for facility geometry */
    private final static String STR_ACCEL_DECEL_LENGTH = "Acc/ Dec Lane Length (ft)";
    private final static String STR_FFS = "FFS";
    private final static String STR_LANE_WIDTH = "Lane Width (ft)";
    private final static String STR_NUM_LANES_ML = "Segment # of Ln(Mainline)";
    private final static String STR_NUM_LANES_RAMP = "# of Lanes on the Ramp";
    private final static String STR_RAMP_FFS = "Ramp FFS";
    private final static String STR_RAMP_SIDE = "Ramp Left or Right";
    private final static String STR_SEGMENT_LENGTHS = "Segment Lengths";
    private final static String STR_SEGMENT_NUMBER = "Segment Number";
    private final static String STR_SEGMENT_TYPES = "Segment Types";

    /* Other strings */
    private final static String STR_HEADER_STYLE = "-----------------------------";

    private HashMap<String, HeaderType> headerMap;
    private HashMap<HeaderType, String> headerMapInverse;
    private HashMap<String, FieldTypeGen> stringMapGen;
    private HashMap<FieldTypeGen, String> stringMapGenInverse;
    private HashMap<String, FieldTypeGeo> stringMapGeo;
    private HashMap<FieldTypeGeo, String> stringMapGeoInverse;

    private HeaderType[] outputOrder;
    private FieldTypeGen[] generalDataOrder;
    private FieldTypeGeo[] geometryDataOrder;

    /**
     * The output formatter
     */
    private final DecimalFormat formatter = new DecimalFormat("#.##");

    /**
     * Constructor
     */
    public ASCIISeedFileAdapter_RLFormat() {
        buildMaps();
        buildOutputOrdering();
    }

    /* Public Methods */
    /**
     * Import a Seed instance from an ASCII file
     *
     * @param newFilePath path of the ASCII file
     * @return a Seed instance
     */
    public Seed importFromFile(String newFilePath) {

        Seed newSeed = new Seed();
        newSeed.setValue(CEConst.IDS_SEED_FILE_NAME, newFilePath);

        FileInputStream f = null;
        BufferedReader reader = null;

        try {
            f = new FileInputStream(newFilePath);
            reader = new BufferedReader(new InputStreamReader(f));

            /* Read the first separator and header lines */
            String currLine = reader.readLine();
            currLine = reader.readLine();

            while (currLine != null) {

                /* Found a header starter, so read the header */
                HeaderType header = parseHeader(currLine);

                /* If we've got a valid header type, read the section's data */
                if (header != HeaderType.ERR) {

                    switch (header) {
                        case CAF:
                            float[][] caf = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_GP_USER_CAF, caf[i][j], i, j);
                                }
                            }
                            break;
                        case DDAF:
                            float[][] ddaf = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_GP_USER_DAF, ddaf[i][j], i, j);
                                }
                            }
                            break;
                        case FACILITY_GEOMETRY:
                            readFacitiltyGeometry(reader, newSeed);
                            break;
                        case GENERAL_DATA:
                            readGeneralData(reader, newSeed);
                            break;
                        case LATERAL_CLEARANCE:
                            int[][] clearance = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_LATERAL_CLEARANCE, clearance[i][j], i, j);
                                }
                            }
                            break;
                        case MAINLINE_DEMAND:
                            int[] mainlineDemand = read1DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++i) {
                                newSeed.setValue(CEConst.IDS_MAIN_DEMAND_VEH, mainlineDemand[i], 0, i);
                            }
                            break;
                        case ODAF:
                            float[][] odaf = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_GP_USER_OAF, odaf[i][j], i, j);
                                }
                            }
                            break;
                        case RAMP_ENTRY_DEMAND:
                            int[][] entryDemand = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_ON_RAMP_DEMAND_VEH, entryDemand[i][j], i, j);
                                }
                            }
                            break;
                        case RAMP_EXIT_DEMAND:
                            int[][] exitDemand = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_OFF_RAMP_DEMAND_VEH, exitDemand[i][j], i, j);
                                }
                            }
                            break;
                        case RAMP_METERING_RATE:
                            int[][] rate = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_ON_RAMP_METERING_RATE_FIX, rate[i][j], i, j);
                                }
                            }
                            break;
                        case RAMP_RAMP_DEMAND:
                            int[][] rampDemand = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH, rampDemand[i][j], i, j);
                                }
                            }
                            break;
                        case RV_CAR_EQ:
                            //not in use
                            float[][] rvEQ = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            /*for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                             for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                             newSeed.setValue(CEConst.IDS_RV_CAR_ER, rvEQ[i][j], i, j);
                             }
                             }*/
                            break;
                        case RV_MAINLINE:
                            float[][] rv = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, rv[i][j], i, j);
                                }
                            }
                            break;
                        case RV_OFR:
                            float[][] rvOFR = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            /*for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                             for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                             newSeed.setValue(CEConst.IDS_OFF_RAMP_RV_PERCENTAGE, rvOFR[i][j], i, j);
                             }
                             }*/
                            break;
                        case RV_ONR:
                            float[][] rvONR = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            /*for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                             for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                             newSeed.setValue(CEConst.IDS_ON_RAMP_RV_PERCENTAGE, rvONR[i][j], i, j);
                             }
                             }*/
                            break;
                        case SAF:
                            float[][] saf = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_GP_USER_SAF, saf[i][j], i, j);
                                }
                            }
                            break;
                        case TRUCK_CAR_EQ:
                            float[][] truckEQ = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_TRUCK_CAR_ET, truckEQ[i][j], i, j);
                                }
                            }
                            break;
                        case TRUCK_MAINLINE:
                            float[][] truck = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                                    newSeed.setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, truck[i][j], i, j);
                                }
                            }
                            break;
                        case TRUCK_OFR:
                            float[][] truckOFR = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            /*for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                             for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                             newSeed.setValue(CEConst.IDS_OFF_RAMP_TRUCK_PERCENTAGE, truckOFR[i][j], i, j);
                             }
                             }*/
                            break;
                        case TRUCK_ONR:
                            float[][] truckONR = read2DSectionFloat(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            /*for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                             for (int j = 0; j < newSeed.getValueInt(CEConst.IDS_NUM_PERIOD); ++j) {
                             newSeed.setValue(CEConst.IDS_ON_RAMP_TRUCK_PERCENTAGE, truckONR[i][j], i, j);
                             }
                             }*/
                            break;
                        case WEAVE_LS:
                            int[][] weaveLength = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                int j = 0;
                                newSeed.setValue(CEConst.IDS_LENGTH_OF_WEAVING, weaveLength[i][j], i);
                            }
                            break;
                        case WEAVE_LCRF:
                            int[][] weaveLCRF = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                int j = 0;
                                newSeed.setValue(CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY, weaveLCRF[i][j], i);
                            }
                            break;
                        case WEAVE_LCFR:
                            int[][] weaveLCFR = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                int j = 0;
                                newSeed.setValue(CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR, weaveLCFR[i][j], i);
                            }
                            break;
                        case WEAVE_LCRR:
                            int[][] weaveLCRR = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                int j = 0;
                                newSeed.setValue(CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR, weaveLCRR[i][j], i);
                            }
                            break;
                        case WEAVE_NW:
                            int[][] weaveNW = read2DSectionInt(reader, newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT), newSeed.getValueInt(CEConst.IDS_NUM_PERIOD));
                            for (int i = 0; i < newSeed.getValueInt(CEConst.IDS_NUM_SEGMENT); ++i) {
                                int j = 0;
                                newSeed.setValue(CEConst.IDS_NUM_LANES_WEAVING, weaveNW[i][j], i);
                            }
                            break;
                        default:
                            break;
                    }

                }

                /* Read the next header text */
                currLine = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                f.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        newSeed.extrapolateRampTruckPCT();
        return newSeed;
    }

    /* Reading Methods */
    private void readGeneralData(BufferedReader reader, Seed seed) {

        try {

            /* Read the separator line */
            if (isSeparatorLine(reader.readLine())) {

                /* Data that spans multiple lines */
                CETime startTime = new CETime();
                CETime endTime = new CETime();
                CEDate RRPstartDate = new CEDate();
                CEDate RRPendDate = new CEDate();
                CEDate seedDemandDate = new CEDate();
                int numSegments = 1;

                /* Read the text of the current line */
                String currLine = reader.readLine();
                while (currLine != null && !isSeparatorLine(currLine)) {

                    /* Get the field type */
                    FieldTypeGen fieldType = parseFieldGen(currLine);
                    if (fieldType != FieldTypeGen.ERR) {

                        /* Split the string to get the value */
                        String[] data = currLine.split(delimiters);

                        try {
                            /* Convert the value to an integer */
                            int val = readInt(data[data.length - 1]);

                            /* Save the data in the seed file */
                            switch (fieldType) {
                                case ALPHA:
                                    seed.setValue(CEConst.IDS_CAPACITY_ALPHA, val);
                                    break;
                                case ANALYSIS_YEAR:
                                    RRPstartDate.year = val;
                                    RRPendDate.year = val;
                                    seedDemandDate.year = val;
                                    break;
                                case END_DAY:
                                    RRPendDate.day = val;
                                    break;
                                case END_HR:
                                    endTime.hour = val;
                                    break;
                                case END_MIN:
                                    endTime.minute = val;
                                    break;
                                case END_MONTH:
                                    RRPendDate.month = val;
                                    break;
                                case FFS_KNOWN:
                                    seed.setFreeFlowSpeedKnown(val == 1);
                                    break;
                                case JAM_DENSITY:
                                    seed.setValue(CEConst.IDS_JAM_DENSITY, val);
                                    break;
                                case NUM_PERIODS:
                                    //seed.setValue(CEConst.IDS_NUM_PERIOD, val);
                                    break;
                                case NUM_SEGMENTS:
                                    numSegments = val;
                                    break;
                                case RAMP_METERING:
                                    //seed.setRampMeteringUsed(val == 1);
                                    break;
                                case SEED_DEMAND_DAY:
                                    seedDemandDate.day = val;
                                    break;
                                case SEED_DEMAND_MONTH:
                                    seedDemandDate.month = val;
                                    break;
                                case START_DAY:
                                    RRPstartDate.day = val;
                                    break;
                                case START_HR:
                                    startTime.hour = val;
                                    break;
                                case START_MIN:
                                    startTime.minute = val;
                                    break;
                                case START_MONTH:
                                    RRPstartDate.month = val;
                                    break;
                                case TERRAIN_CODE:
                                    //seed.setValue(CEConst.IDS_TERRAIN, val);
                                    break;
                                default:
                                    break;
                            }
                        } catch (NumberFormatException numErr) {
                            System.out.println("WARNING: Error reading value of " + stringMapGenInverse.get(fieldType));
                        }
                    }
                    currLine = reader.readLine();
                }

                /* Finish setting seed data */
                seed.setValue(CEConst.IDS_START_TIME, startTime);
                seed.setValue(CEConst.IDS_END_TIME, endTime);
                seed.setSeedFileDate(seedDemandDate);
                seed.setRRPStartDate(RRPstartDate);
                seed.setRRPEndDate(RRPendDate);
                seed.generateSegments(numSegments);

            } else {
                System.out.println("ERROR: Missing Separator");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readFacitiltyGeometry(BufferedReader reader, Seed seed) {

        try {
            /* Read separator line */
            if (isSeparatorLine(reader.readLine())) {

                /* Read the text of the current line */
                String currLine = reader.readLine();
                while (currLine != null && !isSeparatorLine(currLine)) {

                    /* Get the field type */
                    FieldTypeGeo fieldType = parseFieldGeo(currLine);
                    if (fieldType != FieldTypeGeo.ERR) {

                        String[] data = currLine.split(delimiters);
                        int i = data.length - seed.getValueInt(CEConst.IDS_NUM_SEGMENT);
                        int seg = 0;

                        switch (fieldType) {
                            case ACCEL_DECEL_LENGTH:
                                for (; i < data.length; ++i, ++seg) {
                                    seed.setValue(CEConst.IDS_ACC_DEC_LANE_LENGTH, data[i], seg);
                                }
                                break;
                            case FFS:
                                for (; i < data.length; ++i, ++seg) {
                                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                                        seed.setValue(CEConst.IDS_MAIN_FREE_FLOW_SPEED, data[i], seg, period);
                                    }
                                }
                                break;
                            case LANE_WIDTH:
                                for (; i < data.length; ++i, ++seg) {
                                    seed.setValue(CEConst.IDS_LANE_WIDTH, data[i], seg);
                                }
                                break;
                            case NUM_LANES_ML:
                                for (; i < data.length; ++i, ++seg) {
                                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                                        seed.setValue(CEConst.IDS_MAIN_NUM_LANES_IN, data[i], seg, period);
                                    }
                                }
                                break;
                            case NUM_LANES_RAMP:
                                for (; i < data.length; ++i, ++seg) {
                                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                                        seed.setValue(CEConst.IDS_NUM_ON_RAMP_LANES, data[i], seg, period);
                                        seed.setValue(CEConst.IDS_NUM_OFF_RAMP_LANES, data[i], seg, period);
                                    }
                                }
                                break;
                            case RAMP_FFS:
                                for (; i < data.length; ++i, ++seg) {
                                    for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                                        seed.setValue(CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED, data[i], seg, period);
                                        seed.setValue(CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED, data[i], seg, period);
                                    }
                                }
                                break;
                            case RAMP_SIDE:
                                for (; i < data.length; ++i, ++seg) {
                                    int side;
                                    if (data[i].toLowerCase().startsWith("l")) {
                                        side = CEConst.RAMP_SIDE_LEFT;
                                    } else {
                                        side = CEConst.RAMP_SIDE_RIGHT;
                                    }
                                    seed.setValue(CEConst.IDS_ON_RAMP_SIDE, side, seg);
                                    seed.setValue(CEConst.IDS_OFF_RAMP_SIDE, side, seg);
                                }
                                break;
                            case SEGMENT_LENGTHS:
                                for (; i < data.length; ++i, ++seg) {
                                    seed.setValue(CEConst.IDS_SEGMENT_LENGTH_FT, data[i], seg);
                                }
                                break;
                            case SEGMENT_NUMBER:
                                break;
                            case SEGMENT_TYPES:
                                for (; i < data.length; ++i, ++seg) {
                                    seed.setValue(CEConst.IDS_SEGMENT_TYPE, getSegmentTypeInt(data[i]), seg);
                                }
                                break;
                            default:
                                System.out.println("Unable to determine data field type: " + fieldType);
                                break;
                        }
                    }
                    currLine = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readInt(String dat) {
        try {
            return (int) Float.parseFloat(dat);
        } catch (NumberFormatException e) {
            if (!dat.equals("N/A")) {
                System.out.println("WARNING - Could not parse the following string: " + dat);
            }
            return 0;
        }
    }

    private float readFloat(String dat) {
        try {
            return Float.parseFloat(dat);
        } catch (NumberFormatException e) {
            if (!dat.equals("N/A")) {
                System.out.println("WARNING - Could not parse the following string: " + dat);
            }
            return 0.0f;
        }
    }

    private int[] read1DSectionInt(BufferedReader reader, int numAnalysisPeriods) {

        try {

            /* Read separator line */
            if (isSeparatorLine(reader.readLine())) {

                int[] newData = new int[numAnalysisPeriods];
                int currIndex = 0;

                /* Read the text of the current line */
                String currLine = reader.readLine();
                while (currLine != null && !isSeparatorLine(currLine) && currIndex < numAnalysisPeriods) {

                    String[] currData = currLine.split(delimiters);

                    newData[currIndex] = readInt(currData[currData.length - 1]);

                    currLine = reader.readLine();
                    ++currIndex;
                }

                return newData;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int[][] read2DSectionInt(BufferedReader reader, int numSegments, int numAnalysisPeriods) {

        try {

            /* Read separator line */
            if (isSeparatorLine(reader.readLine())) {

                /* Read table header line */
                reader.readLine();

                int newData[][] = new int[numSegments][numAnalysisPeriods];
                int currIndex = 0;

                /* Begin reading the table */
                String currLine = reader.readLine();
                while (currLine != null && !isSeparatorLine(currLine) && currIndex < numAnalysisPeriods) {

                    String[] currData = currLine.split(delimiters);
                    for (int i = currData.length - numSegments, j = 0; i < currData.length; ++i, ++j) {
                        newData[j][currIndex] = readInt(currData[i]);
                    }

                    currLine = reader.readLine();
                    ++currIndex;
                }

                return newData;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private float[][] read2DSectionFloat(BufferedReader reader, int numSegments, int numAnalysisPeriods) {

        try {

            /* Read separator line */
            if (isSeparatorLine(reader.readLine())) {

                /* Read table header line */
                reader.readLine();

                float newData[][] = new float[numSegments][numAnalysisPeriods];
                int currIndex = 0;

                /* Begin reading the table */
                String currLine = reader.readLine();
                while (currLine != null && !isSeparatorLine(currLine) && currIndex < numAnalysisPeriods) {

                    String[] currData = currLine.split(delimiters);
                    for (int i = currData.length - numSegments, j = 0; i < currData.length; ++i, ++j) {
                        newData[j][currIndex] = readFloat(currData[i]);
                    }

                    currLine = reader.readLine();
                    ++currIndex;
                }

                return newData;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    // </editor-fold>

    /* Parsing Methods */
    private HeaderType parseHeader(String header) {

        Iterator<Map.Entry<String, HeaderType>> it = headerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, HeaderType> pairs = (Map.Entry<String, HeaderType>) it.next();

            String keyText = (String) pairs.getKey();
            if (header.toLowerCase().contains(keyText.toLowerCase())) {
                return (HeaderType) pairs.getValue();
            }
        }

        return HeaderType.ERR;
    }

    private FieldTypeGen parseFieldGen(String field) {

        Iterator<Map.Entry<String, FieldTypeGen>> it = stringMapGen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, FieldTypeGen> pairs = (Map.Entry<String, FieldTypeGen>) it.next();

            String keyText = (String) pairs.getKey();
            if (field.toLowerCase().contains(keyText.toLowerCase())) {
                return (FieldTypeGen) pairs.getValue();
            }
        }

        return FieldTypeGen.ERR;
    }

    private FieldTypeGeo parseFieldGeo(String field) {

        List<FieldTypeGeo> possibleMatches = new ArrayList<FieldTypeGeo>();

        Iterator<Map.Entry<String, FieldTypeGeo>> it = stringMapGeo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, FieldTypeGeo> pairs = (Map.Entry<String, ASCIISeedFileAdapter_RLFormat.FieldTypeGeo>) it.next();

            String keyText = (String) pairs.getKey();

            if (field.toLowerCase().contains(keyText.toLowerCase())) {
                possibleMatches.add((FieldTypeGeo) pairs.getValue());
            }
        }

        if (!possibleMatches.isEmpty() && possibleMatches.size() == 1) {
            return possibleMatches.get(0);
        } else if (!possibleMatches.isEmpty()) {

            /* Check for an exact match */
            String exactTest = field.split("=")[0];

            if (stringMapGeo.containsKey(exactTest)) {
                return stringMapGeo.get(exactTest);
            }

        }

        return FieldTypeGeo.ERR;

    }

    private boolean isSeparatorLine(String line) {
        int charCount = 0;
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) == '-' || line.charAt(i) == '=') {
                ++charCount;
            }
        }
        return charCount >= 5;
    }

    /* Helper Methods */
    private int getSegmentTypeInt(String type) {
        if (type.equalsIgnoreCase("B")) {
            return CEConst.SEG_TYPE_B;
        } else if (type.equalsIgnoreCase("OFR")) {
            return CEConst.SEG_TYPE_OFR;
        } else if (type.equalsIgnoreCase("ONR")) {
            return CEConst.SEG_TYPE_ONR;
        } else if (type.equalsIgnoreCase("R")) {
            return CEConst.SEG_TYPE_R;
        } else if (type.equalsIgnoreCase("W")) {
            return CEConst.SEG_TYPE_W;
        } else {
            return 0;
        }
    }

    private String getSegmentTypeStr(int type) {
        if (type == CEConst.SEG_TYPE_B) {
            return "B";
        } else if (type == CEConst.SEG_TYPE_OFR) {
            return "OFR";
        } else if (type == CEConst.SEG_TYPE_ONR) {
            return "ONR";
        } else if (type == CEConst.SEG_TYPE_R) {
            return "R";
        } else if (type == CEConst.SEG_TYPE_W) {
            return "W";
        } else {
            return "ERR";
        }
    }

    private String formatValue(float val) {
        return formatter.format(val);
    }

    /* Initialization Methods */
    private void buildMaps() {
        headerMap = new HashMap<String, HeaderType>();
        headerMap.put(CAF_HDR, HeaderType.CAF);
        headerMap.put(DDAF_HDR, HeaderType.DDAF);
        headerMap.put(FACILITY_GEOMETRY_HDR, HeaderType.FACILITY_GEOMETRY);
        headerMap.put(GENERAL_HDR, HeaderType.GENERAL_DATA);
        headerMap.put(LATERAL_CLEARANCE_HDR, HeaderType.LATERAL_CLEARANCE);
        headerMap.put(MAINLINE_DEMAND_HDR, HeaderType.MAINLINE_DEMAND);
        headerMap.put(ODAF_HDR, HeaderType.ODAF);
        headerMap.put(RAMP_ENTRY_HDR, HeaderType.RAMP_ENTRY_DEMAND);
        headerMap.put(RAMP_EXIT_HDR, HeaderType.RAMP_EXIT_DEMAND);
        headerMap.put(RAMP_METERING_HDR, HeaderType.RAMP_METERING_RATE);
        headerMap.put(RAMP_RAMP_HDR, HeaderType.RAMP_RAMP_DEMAND);
        headerMap.put(RV_CAR_EQ_HDR, HeaderType.RV_CAR_EQ);
        headerMap.put(RV_MAINLINE_HDR, HeaderType.RV_MAINLINE);
        headerMap.put(RV_OFR_HDR, HeaderType.RV_OFR);
        headerMap.put(RV_ONR_HDR, HeaderType.RV_ONR);
        headerMap.put(SAF_HDR, HeaderType.SAF);
        headerMap.put(TRUCK_CAR_EQ_HDR, HeaderType.TRUCK_CAR_EQ);
        headerMap.put(TRUCK_MAINLINE_HDR, HeaderType.TRUCK_MAINLINE);
        headerMap.put(TRUCK_OFR_HDR, HeaderType.TRUCK_OFR);
        headerMap.put(TRUCK_ONR_HDR, HeaderType.TRUCK_ONR);
        headerMap.put(WEAVE_LS_HDR, HeaderType.WEAVE_LS);
        headerMap.put(WEAVE_LCFR_HDR, HeaderType.WEAVE_LCFR);
        headerMap.put(WEAVE_LCRF_HDR, HeaderType.WEAVE_LCRF);
        headerMap.put(WEAVE_LCRR_HDR, HeaderType.WEAVE_LCRR);
        headerMap.put(WEAVE_LS_HDR, HeaderType.WEAVE_LS);
        headerMap.put(WEAVE_NW_HDR, HeaderType.WEAVE_NW);
        headerMap.put(WEAVE_ONE_SIDED_HDR, HeaderType.WEAVE_ONE_SIDED);

        headerMapInverse = new HashMap<HeaderType, String>();
        Iterator<Map.Entry<String, HeaderType>> it1 = headerMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<String, HeaderType> pairs = (Map.Entry<String, HeaderType>) it1.next();
            headerMapInverse.put((HeaderType) pairs.getValue(), (String) pairs.getKey());
        }

        stringMapGen = new HashMap<String, FieldTypeGen>();
        stringMapGen.put(STR_ALPHA, FieldTypeGen.ALPHA);
        stringMapGen.put(STR_ANALYSIS_YEAR, FieldTypeGen.ANALYSIS_YEAR);
        stringMapGen.put(STR_END_DAY, FieldTypeGen.END_DAY);
        stringMapGen.put(STR_END_HR, FieldTypeGen.END_HR);
        stringMapGen.put(STR_END_MIN, FieldTypeGen.END_MIN);
        stringMapGen.put(STR_END_MONTH, FieldTypeGen.END_MONTH);
        stringMapGen.put(STR_FFS_KNOWN, FieldTypeGen.FFS_KNOWN);
        stringMapGen.put(STR_JAM_DENSITY, FieldTypeGen.JAM_DENSITY);
        stringMapGen.put(STR_NUM_PERIODS, FieldTypeGen.NUM_PERIODS);
        stringMapGen.put(STR_NUM_SEGMENTS, FieldTypeGen.NUM_SEGMENTS);
        stringMapGen.put(STR_RAMP_METERING, FieldTypeGen.RAMP_METERING);
        stringMapGen.put(STR_SEED_DEMAND_DAY, FieldTypeGen.SEED_DEMAND_DAY);
        stringMapGen.put(STR_SEED_DEMAND_MONTH, FieldTypeGen.SEED_DEMAND_MONTH);
        stringMapGen.put(STR_START_DAY, FieldTypeGen.START_DAY);
        stringMapGen.put(STR_START_HR, FieldTypeGen.START_HR);
        stringMapGen.put(STR_START_MIN, FieldTypeGen.START_MIN);
        stringMapGen.put(STR_START_MONTH, FieldTypeGen.START_MONTH);
        stringMapGen.put(STR_TERRAIN_CODE, FieldTypeGen.TERRAIN_CODE);

        stringMapGenInverse = new HashMap<FieldTypeGen, String>();
        Iterator<Map.Entry<String, FieldTypeGen>> it2 = stringMapGen.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<String, FieldTypeGen> pairs = (Map.Entry<String, FieldTypeGen>) it2.next();
            stringMapGenInverse.put((FieldTypeGen) pairs.getValue(), (String) pairs.getKey());
        }

        stringMapGeo = new HashMap<String, FieldTypeGeo>();
        stringMapGeo.put(STR_ACCEL_DECEL_LENGTH, FieldTypeGeo.ACCEL_DECEL_LENGTH);
        stringMapGeo.put(STR_FFS, FieldTypeGeo.FFS);
        stringMapGeo.put(STR_LANE_WIDTH, FieldTypeGeo.LANE_WIDTH);
        stringMapGeo.put(STR_NUM_LANES_ML, FieldTypeGeo.NUM_LANES_ML);
        stringMapGeo.put(STR_NUM_LANES_RAMP, FieldTypeGeo.NUM_LANES_RAMP);
        stringMapGeo.put(STR_RAMP_FFS, FieldTypeGeo.RAMP_FFS);
        stringMapGeo.put(STR_RAMP_SIDE, FieldTypeGeo.RAMP_SIDE);
        stringMapGeo.put(STR_SEGMENT_LENGTHS, FieldTypeGeo.SEGMENT_LENGTHS);
        stringMapGeo.put(STR_SEGMENT_NUMBER, FieldTypeGeo.SEGMENT_NUMBER);
        stringMapGeo.put(STR_SEGMENT_TYPES, FieldTypeGeo.SEGMENT_TYPES);

        stringMapGeoInverse = new HashMap<FieldTypeGeo, String>();
        Iterator<Map.Entry<String, FieldTypeGeo>> it3 = stringMapGeo.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry<String, FieldTypeGeo> pairs = (Map.Entry<String, FieldTypeGeo>) it3.next();
            stringMapGeoInverse.put((FieldTypeGeo) pairs.getValue(), (String) pairs.getKey());
        }

    }

    private void buildOutputOrdering() {

        outputOrder = new HeaderType[]{
            HeaderType.GENERAL_DATA, // This section must always come first
            HeaderType.FACILITY_GEOMETRY, // This section must always come second
            HeaderType.MAINLINE_DEMAND,
            HeaderType.RAMP_ENTRY_DEMAND,
            HeaderType.RAMP_EXIT_DEMAND,
            HeaderType.RAMP_RAMP_DEMAND,
            HeaderType.RAMP_METERING_RATE,
            HeaderType.CAF,
            HeaderType.ODAF,
            HeaderType.DDAF,
            HeaderType.SAF,
            HeaderType.TRUCK_MAINLINE,
            HeaderType.RV_MAINLINE,
            HeaderType.TRUCK_ONR,
            HeaderType.RV_ONR,
            HeaderType.TRUCK_OFR,
            HeaderType.RV_OFR,
            HeaderType.TRUCK_CAR_EQ,
            HeaderType.RV_CAR_EQ,
            HeaderType.LATERAL_CLEARANCE,
            HeaderType.WEAVE_LS,
            HeaderType.WEAVE_LCFR,
            HeaderType.WEAVE_LCRF,
            HeaderType.WEAVE_LCRR,
            HeaderType.WEAVE_LS,
            HeaderType.WEAVE_NW,
            HeaderType.WEAVE_ONE_SIDED
        };

        generalDataOrder = new FieldTypeGen[]{
            FieldTypeGen.NUM_PERIODS, // This field must always come first
            FieldTypeGen.NUM_SEGMENTS, // This field must always come second
            FieldTypeGen.FFS_KNOWN,
            FieldTypeGen.RAMP_METERING,
            FieldTypeGen.TERRAIN_CODE,
            FieldTypeGen.ALPHA,
            FieldTypeGen.START_HR,
            FieldTypeGen.START_MIN,
            FieldTypeGen.END_HR,
            FieldTypeGen.END_MIN,
            FieldTypeGen.ANALYSIS_YEAR,
            FieldTypeGen.START_DAY,
            FieldTypeGen.START_MONTH,
            FieldTypeGen.END_DAY,
            FieldTypeGen.END_MONTH,
            FieldTypeGen.SEED_DEMAND_DAY,
            FieldTypeGen.SEED_DEMAND_MONTH,
            FieldTypeGen.JAM_DENSITY
        };

        geometryDataOrder = new FieldTypeGeo[]{
            FieldTypeGeo.SEGMENT_NUMBER, // This field not optional
            FieldTypeGeo.SEGMENT_TYPES, // This field must always come first (or second if previous is included)
            FieldTypeGeo.SEGMENT_LENGTHS,
            FieldTypeGeo.ACCEL_DECEL_LENGTH,
            FieldTypeGeo.NUM_LANES_ML,
            FieldTypeGeo.NUM_LANES_RAMP,
            FieldTypeGeo.RAMP_SIDE,
            FieldTypeGeo.LANE_WIDTH,
            FieldTypeGeo.FFS,
            FieldTypeGeo.RAMP_FFS
        };
    }

    /* Static main method for testing purposes only */
    /**
     * Unit test for this class
     *
     * @param args command line arguments (not in use)
     */
    public static void main(String[] args) {
        System.out.println("Starting Test");
        ASCIISeedFileAdapter_RLFormat seedFile = new ASCIISeedFileAdapter_RLFormat();
        Seed i40 = seedFile.importFromFile("/Users/Shu/I-40.txt");
        //seedFile.exportToFile("/Users/Shu/out.txt", i40);
        System.out.println("Finished");
    }

}
