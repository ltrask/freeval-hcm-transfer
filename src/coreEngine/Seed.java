package coreEngine;

import coreEngine.Helper.CEConst;
import coreEngine.Helper.CECoordinate;
import coreEngine.Helper.CEDate;
import coreEngine.Helper.CEHelper;
import coreEngine.Helper.CETime;
import coreEngine.Helper.FacilitySummary;
import coreEngine.Helper.RMHelper;
import coreEngine.Helper.RampMeteringData.RampMeteringALINEAData;
import coreEngine.Helper.RampMeteringData.RampMeteringFuzzyData;
import coreEngine.atdm.DataStruct.ATDMDatabase;
import coreEngine.atdm.DataStruct.ATDMScenario;
import coreEngine.reliabilityAnalysis.DataStruct.Scenario;
import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import coreEngine.reliabilityAnalysis.DataStruct.WorkZone;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * This class contains all input and output data, setters, getters, and analysis
 * functions of a seed. This class should be the only interface of the
 * coreEngine package.
 *
 * @author Shu Liu
 */
public class Seed implements Serializable {

    /**
     * serialVersionUID (DO NOT CHANGE)
     */
    private static final long serialVersionUID = 23423423411L;

    // <editor-fold defaultstate="collapsed" desc="GENERAL INPUT DATA - GP AND ML SEGMENTS">
    /**
     * Seed urban-rural type
     */
    int inUrbanRuralType = CEConst.SEED_URBAN;
    /**
     * Number of RL scenarios (NOT including default scenario)
     */
    private int inNumScen = 0;
    /**
     * Number of periods
     */
    private int inNumPeriod = 8;
    /**
     * ArrayList of all general purpose segments
     */
    private ArrayList<GPMLSegment> GPSegments;
    /**
     * Whether managed lane is used;
     */
    private boolean inManagedLaneUsed = false;
    /**
     * ArrayList of all managed lane segments
     */
    private ArrayList<GPMLSegment> MLSegments;

    /**
     * Number of steps per period in over saturated
     */
    private static final int NUM_STEPS = 60; //60

    //parameters from inputGeneralInfo used to generate default segment array
    /**
     * Start time of analysis period
     */
    private CETime inStartTime = new CETime(16, 0);
    /**
     * End time of analysis period
     */
    private CETime inEndTime = new CETime(18, 0);
    /**
     * Length of each analysis period, default 15min
     */
    private static final CETime LENGTH_OF_EACH_PERIOD = new CETime(0, 15);
    /**
     * Whether free flow speed is known
     */
    private boolean inFreeFlowSpeedKnown = true;
    /**
     * User defined ramp metering information for general purpose segments
     */
    private RMHelper rampMeteringGP;
    /**
     * User defined ramp metering information for managed lanes segments
     */
    private RMHelper rampMeteringML;
    /**
     * Facility wide jam density, pc/mi/ln
     */
    private float inJamDensity = 190;
    /**
     * Global Input Truck - Passenger Car Equivalent
     */
    public float inGlobalETDefault = 2.0f;
    /**
     * Capacity drop percentage
     */
    private int inCapacityDropPercentage = 7;
    /**
     * Average vehicle occupancy in general purpose lanes (passenger / vehicle)
     */
    private float inGPOccupancy = 1.0f;
    /**
     * Average vehicle occupancy in managed lanes (passenger / vehicle)
     */
    private float inMLOccupancy = 1.0f;
    /**
     * Project name
     */
    private String inProjectName = "New Project";
    /**
     * Seed file path and name on disk
     */
    private String inFileName = null;
    /**
     * Seed Facility coordinate at the upstream end of the first segment
     */
    private CECoordinate upstreamCoord;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PROCESSING FLAGS">
    /**
     * Whether locate memory is required
     */
    private boolean needMemory = true;
    /**
     * Whether global preprocess is required
     */
    private boolean seedInputModified = true;

    /**
     * Scenario index of the currently buffered result (-1 for null)
     */
    private int bufferScen = -1;

    /**
     * ATDM set index of the currently buffered result (-1 for null)
     */
    private int bufferATDM = -1;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RELIABILITY ANALYSIS INPUT DATA">
    /**
     * ArrayList which contains all reliability analysis scenario information,
     * include default/empty seed scenario.
     */
    private ArrayList<ScenarioInfo> RL_ScenarioInfo = CEHelper.scenInfo_1D(inNumScen + 1);
    /**
     * Reliability scenarios for GP, NOT include default/empty seed scenario
     */
    private Scenario RL_Scenarios_GP;
    /**
     * Reliability scenarios for ML, NOT include default/empty seed scenario
     */
    private Scenario RL_Scenarios_ML;
    /**
     * Seed file date
     */
    private CEDate RL_SeedFileDate = new CEDate(2014, 1, 1);
    /**
     * Reliability analysis start date
     */
    private CEDate RL_RRPStartDate = new CEDate(2014, 1, 1);
    /**
     * Reliability analysis end date
     */
    private CEDate RL_RRPEndDate = new CEDate(2014, 12, 31);
    /**
     * Scenario Generator random number generator
     */
    private Long RL_RngSeed = null;
    /**
     * Specified demand multiplier for GP segments
     */
    private float[][] RL_SpecifiedDemand_GP = new float[12][7];
    /**
     * Specified demand multiplier for GP segments
     */
    private float[][] RL_SpecifiedDemand_ML = new float[12][7];
    /**
     * Week day used for reliability analysis
     */
    private boolean[] RL_WeekdayUsed = new boolean[]{true, true, true, true, true, false, false}; //Mon(0) - Sun(6)
    /**
     * Days excluded in reliability analysis
     */
    private ArrayList<CEDate> RL_DayExcluded = new ArrayList<>();
    /**
     * Weather location
     */
    private String RL_WeatherLocation;
    /**
     * Weather event probability
     */
    private float[][] RL_WeatherProbability;
    /**
     * Weather event average duration
     */
    private float[] RL_WeatherAverageDuration;
    /**
     * Weather event capacity adjustment factors
     */
    private float[] RL_WeatherCAF;
    /**
     * Weather event demand adjustment factors
     */
    private float[] RL_WeatherDAF;
    /**
     * Weather event speed adjustment factors
     */
    private float[] RL_WeatherSAF;
    /**
     * GP Incident frequency
     */
    private float[] RL_IncidentFrequency_GP;
    /**
     * GP Incident duration
     */
    private float[][] RL_IncidentDuration_GP;
    /**
     * GP Incident Severity Distribution
     */
    private float[] RL_IncidentDistribution_GP;
    /**
     * GP Incident CAF. First index is incident type/severity, second is number
     * of lanes (2 lanes is 0, 3 is 1, etc.).
     */
    private float[][] RL_IncidentCAF_GP;
    /**
     * GP Incident DAF. First index is incident type/severity, second is number
     * of lanes (2 lanes is 0, 3 is 1, etc.).
     */
    private float[][] RL_IncidentDAF_GP;
    /**
     * GP Incident SAF. First index is incident type/severity, second is number
     * of lanes (2 lanes is 0, 3 is 1, etc.).
     */
    private float[][] RL_IncidentSAF_GP;
    /**
     * GP Incident LAF. First index is incident type/severity, second is number
     * of lanes (2 lanes is 0, 3 is 1, etc.).
     */
    private int[][] RL_IncidentLAF_GP;
    /**
     * GP Incident over crash ratio
     */
    private float RL_IncidentCrashRatio_GP = 4.9f;

    /**
     * ML Incident frequency
     */
    private float[] RL_IncidentFrequency_ML;
    /**
     * ML Incident duration
     */
    private float[][] RL_IncidentDuration_ML;
    /**
     * ML Incident Severity Distribution
     */
    private float[] RL_IncidentDistribution_ML;
    /**
     * ML Incident CAF. First index is incident type/severity, second is number
     * of lanes (1 lanes is 0, 2 lanes is 1).
     */
    private float[][] RL_IncidentCAF_ML;
    /**
     * ML Incident DAF. First index is incident type/severity, second is number
     * of lanes (1 lanes is 0, 2 lanes is 1).
     */
    private float[][] RL_IncidentDAF_ML;
    /**
     * ML Incident SAF. First index is incident type/severity, second is number
     * of lanes (1 lanes is 0, 2 lanes is 1).
     */
    private float[][] RL_IncidentSAF_ML;
    /**
     * ML Incident LAF. First index is incident type/severity, second is number
     * of lanes (1 lanes is 0, 2 lanes is 1).
     */
    private int[][] RL_IncidentLAF_ML;
    /**
     * ML Incident over crash ratio
     */
    private float RL_IncidentCrashRatio_ML = 4.9f;
    /**
     * List of work zones
     */
    private ArrayList<WorkZone> RL_WorkZones;
    /**
     * Field Measured TTI Distribution Value (from 0.05 to 0.95 with 0.05
     * increment, and 0.99)
     */
    private float[] TTI_Value = new float[]{
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 1.0f};
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Toolbox Input Data">
    //<editor-fold defaultstate="collapsed" desc="DSS Fields">
    /**
     * Indicates whether the oversaturated module is forced to be used in all
     * periods. If true the oversat module will be used in all periods, if false
     * (default) the CoreEngine will switch between undersaturated and
     * oversaturated analysis as needed.
     */
    private boolean forceOversat = false;
    //</editor-fold>
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GETTER FOR RL AND ATDM ADJUSTMENT FACTORS">
    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    private float getRLOAF(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 1;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.OAF().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.OAF().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    private float getRLDAF(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 1;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.DAF().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.DAF().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    private float getRLSAF(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 1;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.SAF().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.SAF().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    public float getRLCAF(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 1;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.CAF().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.CAF().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    public int getRLLAFI(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 0;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.LAFI().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.LAFI().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for reliability analysis adjustment factor
     *
     * @param scen scenario index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return reliability analysis adjustment factor
     */
    public int getRLLAFWZ(int scen, int seg, int period, int segType) {
        if (scen == 0) {
            return 0;
        } else {
            return segType == CEConst.SEG_TYPE_GP
                    ? RL_Scenarios_GP.LAFWZ().get(scen - 1, seg, period)
                    : RL_Scenarios_ML.LAFWZ().get(scen - 1, seg, period);
        }
    }

    /**
     * Getter for ATDM adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return ATDM adjustment factor
     */
    private float getATDMOAF(int scen, int atdm, int seg, int period, int segType) {
        return atdm < 0 ? 1
                : segType == CEConst.SEG_TYPE_GP
                        ? ATDMSets.get(atdm).get(scen)[0].OAF().get(seg, period)
                        : ATDMSets.get(atdm).get(scen)[1].OAF().get(seg, period);
    }

    /**
     * Getter for ATDM adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return ATDM adjustment factor
     */
    private float getATDMDAF(int scen, int atdm, int seg, int period, int segType) {
        return atdm < 0 ? 1
                : segType == CEConst.SEG_TYPE_GP
                        ? ATDMSets.get(atdm).get(scen)[0].DAF().get(seg, period)
                        : ATDMSets.get(atdm).get(scen)[1].DAF().get(seg, period);
    }

    /**
     * Getter for ATDM adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return ATDM adjustment factor
     */
    private float getATDMSAF(int scen, int atdm, int seg, int period, int segType) {
        return atdm < 0 ? 1
                : segType == CEConst.SEG_TYPE_GP
                        ? ATDMSets.get(atdm).get(scen)[0].SAF().get(seg, period)
                        : ATDMSets.get(atdm).get(scen)[1].SAF().get(seg, period);
    }

    /**
     * Getter for ATDM adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return ATDM adjustment factor
     */
    private float getATDMCAF(int scen, int atdm, int seg, int period, int segType) {
        return atdm < 0 ? 1
                : segType == CEConst.SEG_TYPE_GP
                        ? ATDMSets.get(atdm).get(scen)[0].CAF().get(seg, period)
                        : ATDMSets.get(atdm).get(scen)[1].CAF().get(seg, period);
    }

    /**
     * Getter for ATDM adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return ATDM adjustment factor
     */
    private int getATDMLAF(int scen, int atdm, int seg, int period, int segType) {
        return atdm < 0 ? 0
                : segType == CEConst.SEG_TYPE_GP
                        ? ATDMSets.get(atdm).get(scen)[0].LAF().get(seg, period)
                        : ATDMSets.get(atdm).get(scen)[1].LAF().get(seg, period);
    }

    /**
     * Getter for RL and ATDM combined adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return RL and ATDM combined adjustment factor
     */
    float getRLAndATDMOAF(int scen, int atdm, int seg, int period, int segType) {
        return getRLOAF(scen, seg, period, segType) * getATDMOAF(scen, atdm, seg, period, segType);
    }

    /**
     * Getter for RL and ATDM combined adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return RL and ATDM combined adjustment factor
     */
    float getRLAndATDMDAF(int scen, int atdm, int seg, int period, int segType) {
        return getRLDAF(scen, seg, period, segType) * getATDMDAF(scen, atdm, seg, period, segType);
    }

    /**
     * Getter for RL and ATDM combined adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return RL and ATDM combined adjustment factor
     */
    float getRLAndATDMSAF(int scen, int atdm, int seg, int period, int segType) {
        return getRLSAF(scen, seg, period, segType) * getATDMSAF(scen, atdm, seg, period, segType);
    }

    /**
     * Getter for RL and ATDM combined adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return RL and ATDM combined adjustment factor
     */
    float getRLAndATDMCAF(int scen, int atdm, int seg, int period, int segType) {
        return getRLCAF(scen, seg, period, segType) * getATDMCAF(scen, atdm, seg, period, segType);
    }

    /**
     * Getter for RL and ATDM combined adjustment factor
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     * @param seg segment index
     * @param period period index
     * @param segType segment GP/ML type
     * @return RL and ATDM combined adjustment factor
     */
    int getRLAndATDMLAF(int scen, int atdm, int seg, int period, int segType) {
        return getRLLAFI(scen, seg, period, segType) + getRLLAFWZ(scen, seg, period, segType) + getATDMLAF(scen, atdm, seg, period, segType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ATDM SCENARIOS DATA, SETTER, GETTER, AND HELPER CLASS">
    /**
     * This simple class is used to find thread
     */
    private class ScenATDM implements Serializable {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 3267333236245L;

        /**
         * Scenario Index
         */
        int scen;

        /**
         * ATDM set index
         */
        int atdm;

        /**
         * Create a new scenario and atdm pair
         *
         * @param scen scenario index
         * @param atdm ATDM set index
         */
        private ScenATDM(int scen, int atdm) {
            this.scen = scen;
            this.atdm = atdm;
        }

        @Override
        public boolean equals(Object obj) {
            return scen == ((ScenATDM) obj).scen && atdm == ((ScenATDM) obj).atdm;
        }

        @Override
        public String toString() {
            return "scen" + scen + "atdm" + atdm;
        }
    }

    /**
     * ATDM Sets, each set contains a HashMap of related scenarios
     */
    private ArrayList<HashMap<Integer, ATDMScenario[]>> ATDMSets = new ArrayList();

    /**
     * Add a new ATDM set
     *
     * @param newATDMSet new ATDM set
     */
    public void addATDMSet(HashMap<Integer, ATDMScenario[]> newATDMSet) {
        ATDMSets.add(newATDMSet);
        fireDataChanged(CHANGE_ATDM);
    }

    /**
     * ATDM database (strategies and plans)
     */
    private ATDMDatabase atdmDatabase;

    /**
     * Delete all atdm sets
     */
    public void deleteAllATDM() {
        ATDMSets = new ArrayList();
        fireDataChanged(CHANGE_ATDM);
    }

    /**
     * Delete one atdm set
     *
     * @param ATDMSetIndex Index of the ATDM set to be deleted (start with 0)
     */
    public void deleteATDMSet(int ATDMSetIndex) {
        ATDMSets.remove(ATDMSetIndex);
        fireDataChanged(CHANGE_ATDM);
    }

    /**
     * Count the number of ATDM analysis for a particular RL scenario
     *
     * @param scen scenario index
     * @return number of ATDM analysis for a particular RL scenario
     */
    private int countATDM(int scen) {
        int result = 0;
        for (HashMap<Integer, ATDMScenario[]> ATDMSet : ATDMSets) {
            if (ATDMSet.get(scen) != null) {
                result++;
            }
        }
        return result;
    }

    /**
     * Getter for ATDM sets
     *
     * @return ATDM sets
     */
    public ArrayList<HashMap<Integer, ATDMScenario[]>> getATDMSets() {
        return ATDMSets;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="STATUS CHECK">
    /**
     * Check whether a scenario has valid output
     *
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index (-1 for RL scenario)
     * @return whether a scenario has valid output
     */
    public boolean hasValidOutput(int scen, int atdm) {
        try {
            return !needMemory && !seedInputModified
                    && (atdm >= 0
                            ? ATDMSets.get(atdm).get(scen)[0].getStatus() == CEConst.SCENARIO_HAS_OUTPUT
                            : RL_ScenarioInfo.get(scen).statusRL == CEConst.SCENARIO_HAS_OUTPUT);
        } catch (Exception e) {
            return false;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GP SUMMARY OUTPUT DATA">
    //summary results
    //summary for each period [scen][period]
    /**
     * Summary output, CEConst.IDS_P_ACTUAL_TIME:
     */
    transient private HashMap<String, float[]> pOutActualTravelTime;
    /**
     * Summary output, CEConst.IDS_P_FFS_TIME:
     */
    transient private HashMap<String, float[]> pOutFreeFlowTravelTime;
    /**
     * Summary output, CEConst.IDS_P_ML_DELAY:
     */
    transient private HashMap<String, float[]> pOutMainlineDelay;
    /**
     * Summary output, CEConst.IDS_P_ONR_DELAY:
     */
    transient private HashMap<String, float[]> pOutOnRampDelay;
    /**
     * Summary output, CEConst.IDS_P_SYS_DELAY:
     */
    //transient private HashMap<String, float[]> pOutSystemDelay;
    /**
     * Summary output, CEConst.IDS_P_VMTD:
     */
    transient private HashMap<String, float[]> pOutVMTD;
    /**
     * Summary output, CEConst.IDS_P_VMTV:
     */
    transient private HashMap<String, float[]> pOutVMTV;
    /**
     * Summary output, CEConst.IDS_P_VHT:
     */
    transient private HashMap<String, float[]> pOutVHT;
    /**
     * Summary output, CEConst.IDS_P_VHD:
     */
    transient private HashMap<String, float[]> pOutVHD;
    /**
     * Summary output, CEConst.IDS_P_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, float[]> pOutSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_P_TOTAL_DENSITY_VEH:
     */
    transient private HashMap<String, float[]> pOutDensityTotal_veh;
    /**
     * Summary output, CEConst.IDS_P_TOTAL_DENSITY_PC:
     */
    transient private HashMap<String, float[]> pOutDensityTotal_pc;
    /**
     * Summary output, CEConst.IDS_P_REPORT_LOS:
     */
    transient private HashMap<String, String[]> pOutReportLOS;
    /**
     * Summary output, CEConst.IDS_P_TTI:
     */
    transient private HashMap<String, float[]> pOutTravelTimeIndex;
    /**
     * Summary output, CEConst.IDS_P_MAX_DC:
     */
    transient private HashMap<String, float[]> pOutMaxDC;
    /**
     * Summary output, CEConst.IDS_P_MAX_VC:
     */
    transient private HashMap<String, float[]> pOutMaxVC;
    /**
     * Summary output, CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH:
     */
    transient private HashMap<String, float[]> pOutDenyVeh;
    /**
     * Summary output, CEConst.IDS_P_TOTAL_ML_QUEUE_LENGTH_FT:
     */
    transient private HashMap<String, float[]> pOutMainlineQueueLengthFt;
    /**
     * Summary output, CEConst.IDS_P_TOTAL_ON_QUEUE_LENGTH_FT:
     */
    transient private HashMap<String, float[]> pOutOnQueueVeh;

    //summary for each segment [scen][segment]
    /**
     * Summary output, CEConst.IDS_S_ACTUAL_TIME:
     */
    transient private HashMap<String, float[]> sOutActualTravelTime;
    /**
     * Summary output, CEConst.IDS_S_VMTD:
     */
    transient private HashMap<String, float[]> sOutVMTD;
    /**
     * Summary output, CEConst.IDS_S_VMTV:
     */
    transient private HashMap<String, float[]> sOutVMTV;
    /**
     * Summary output, CEConst.IDS_S_VHT:
     */
    transient private HashMap<String, float[]> sOutVHT;
    /**
     * Summary output, CEConst.IDS_S_VHD:
     */
    transient private HashMap<String, float[]> sOutVHD;
    /**
     * Summary output, CEConst.IDS_S_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, float[]> sOutSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_S_REPORT_DENSITY_PC:
     */
    transient private HashMap<String, float[]> sOutReportDensity_IA_pc;
    /**
     * Summary output, CEConst.IDS_S_MAX_DC:
     */
    transient private HashMap<String, float[]> sOutMaxDC;
    /**
     * Summary output, CEConst.IDS_S_MAX_VC:
     */
    transient private HashMap<String, float[]> sOutMaxVC;

    //summary for each scenario [scen]
    /**
     * Summary output, CEConst.IDS_SP_ACTUAL_TIME:
     */
    transient private HashMap<String, Float> spOutActualTravelTime;
    /**
     * Summary output, CEConst.IDS_SP_VMTD:
     */
    transient private HashMap<String, Float> spOutVMTD;
    /**
     * Summary output, CEConst.IDS_SP_VMTV:
     */
    transient private HashMap<String, Float> spOutVMTV;
    /**
     * Summary output, CEConst.IDS_SP_VHT:
     */
    transient private HashMap<String, Float> spOutVHT;
    /**
     * Summary output, CEConst.IDS_SP_VHD:
     */
    transient private HashMap<String, Float> spOutVHD;
    /**
     * Summary output, CEConst.IDS_SP_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, Float> spOutSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_SP_REPORT_DENSITY_PC:
     */
    transient private HashMap<String, Float> spOutReportDensity_IA_pc;
    /**
     * Summary output, CEConst.IDS_SP_MAX_DC:
     */
    transient private HashMap<String, Float> spOutMaxDC;
    /**
     * Summary output, CEConst.IDS_SP_MAX_VC:
     */
    transient private HashMap<String, Float> spOutMaxVC;
    /**
     * For calculation only
     */
    transient private HashMap<String, float[]> pReportDensityFactor;
    /**
     * For calculation only
     */
    transient private HashMap<String, float[]> sReportDensityFactor;
    /**
     * For calculation only
     */
    transient private HashMap<String, Float> spReportDensityFactor;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ML SUMMARY OUTPUT DATA">
    //summary results
    //summary for each period [scen][period]
    /**
     * Summary output, CEConst.IDS_ML_P_ACTUAL_TIME:
     */
    transient private HashMap<String, float[]> pOutMLActualTravelTime;
    /**
     * Summary output, CEConst.IDS_ML_P_FFS_TIME:
     */
    transient private HashMap<String, float[]> pOutMLFreeFlowTravelTime;
    /**
     * Summary output, CEConst.IDS_ML_P_ML_DELAY:
     */
    transient private HashMap<String, float[]> pOutMLMainlineDelay;
    /**
     * Summary output, CEConst.IDS_ML_P_ONR_DELAY:
     */
    transient private HashMap<String, float[]> pOutMLOnRampDelay;
    /**
     * Summary output, CEConst.IDS_ML_P_SYS_DELAY:
     */
    //transient private HashMap<String, float[]> pOutMLSystemDelay;
    /**
     * Summary output, CEConst.IDS_ML_P_VMTD:
     */
    transient private HashMap<String, float[]> pOutMLVMTD;
    /**
     * Summary output, CEConst.IDS_ML_P_VMTV:
     */
    transient private HashMap<String, float[]> pOutMLVMTV;
    /**
     * Summary output, CEConst.IDS_ML_P_VHT:
     */
    transient private HashMap<String, float[]> pOutMLVHT;
    /**
     * Summary output, CEConst.IDS_ML_P_VHD:
     */
    transient private HashMap<String, float[]> pOutMLVHD;
    /**
     * Summary output, CEConst.IDS_ML_P_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, float[]> pOutMLSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_ML_P_TOTAL_DENSITY_VEH:
     */
    transient private HashMap<String, float[]> pOutMLDensityTotal_veh;
    /**
     * Summary output, CEConst.IDS_ML_P_TOTAL_DENSITY_PC:
     */
    transient private HashMap<String, float[]> pOutMLDensityTotal_pc;
    /**
     * Summary output, CEConst.IDS_ML_P_REPORT_LOS:
     */
    transient private HashMap<String, String[]> pOutMLReportLOS;
    /**
     * Summary output, CEConst.IDS_ML_P_TTI:
     */
    transient private HashMap<String, float[]> pOutMLTravelTimeIndex;
    /**
     * Summary output, CEConst.IDS_ML_P_MAX_DC:
     */
    transient private HashMap<String, float[]> pOutMLMaxDC;
    /**
     * Summary output, CEConst.IDS_ML_P_MAX_VC:
     */
    transient private HashMap<String, float[]> pOutMLMaxVC;
    /**
     * Summary output, CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_LENGTH_FT:
     */
    transient private HashMap<String, float[]> pOutMLDenyLengthFt;
    /**
     * Summary output, CEConst.IDS_ML_P_TOTAL_ML_QUEUE_LENGTH_FT:
     */
    transient private HashMap<String, float[]> pOutMLMainlineQueueLengthFt;
    /**
     * Summary output, CEConst.IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT:
     */
    transient private HashMap<String, float[]> pOutMLOnQueueLengthFt;

    //summary for each segment [scen][segment]
    /**
     * Summary output, CEConst.IDS_ML_S_ACTUAL_TIME:
     */
    transient private HashMap<String, float[]> sOutMLActualTravelTime;
    /**
     * Summary output, CEConst.IDS_ML_S_VMTD:
     */
    transient private HashMap<String, float[]> sOutMLVMTD;
    /**
     * Summary output, CEConst.IDS_ML_S_VMTV:
     */
    transient private HashMap<String, float[]> sOutMLVMTV;
    /**
     * Summary output, CEConst.IDS_ML_S_VHT:
     */
    transient private HashMap<String, float[]> sOutMLVHT;
    /**
     * Summary output, CEConst.IDS_ML_S_VHD:
     */
    transient private HashMap<String, float[]> sOutMLVHD;
    /**
     * Summary output, CEConst.IDS_ML_S_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, float[]> sOutMLSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_ML_S_REPORT_DENSITY_PC:
     */
    transient private HashMap<String, float[]> sOutMLReportDensity_IA_pc;
    /**
     * Summary output, CEConst.IDS_ML_S_MAX_DC:
     */
    transient private HashMap<String, float[]> sOutMLMaxDC;
    /**
     * Summary output, CEConst.IDS_ML_S_MAX_VC:
     */
    transient private HashMap<String, float[]> sOutMLMaxVC;

    //summary for each scenario [scen]
    /**
     * Summary output, CEConst.IDS_ML_SP_ACTUAL_TIME:
     */
    transient private HashMap<String, Float> spOutMLActualTravelTime;
    /**
     * Summary output, CEConst.IDS_ML_SP_VMTD:
     */
    transient private HashMap<String, Float> spOutMLVMTD;
    /**
     * Summary output, CEConst.IDS_ML_SP_VMTV:
     */
    transient private HashMap<String, Float> spOutMLVMTV;
    /**
     * Summary output, CEConst.IDS_ML_SP_VHT:
     */
    transient private HashMap<String, Float> spOutMLVHT;
    /**
     * Summary output, CEConst.IDS_ML_SP_VHD:
     */
    transient private HashMap<String, Float> spOutMLVHD;
    /**
     * Summary output, CEConst.IDS_ML_SP_SPACE_MEAN_SPEED:
     */
    transient private HashMap<String, Float> spOutMLSpaceMeanSpeed;
    /**
     * Summary output, CEConst.IDS_ML_SP_REPORT_DENSITY_PC:
     */
    transient private HashMap<String, Float> spOutMLReportDensity_IA_pc;
    /**
     * Summary output, CEConst.IDS_ML_SP_MAX_DC:
     */
    transient private HashMap<String, Float> spOutMLMaxDC;
    /**
     * Summary output, CEConst.IDS_ML_SP_MAX_VC:
     */
    transient private HashMap<String, Float> spOutMLMaxVC;
    /**
     * For calculation only
     */
    transient private HashMap<String, float[]> pMLReportDensityFactor;
    /**
     * For calculation only
     */
    transient private HashMap<String, float[]> sMLReportDensityFactor;
    /**
     * For calculation only
     */
    transient private HashMap<String, Float> spMLReportDensityFactor;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CONSTRUCTOR AND GENERATE SEGMENT">
    /**
     * Constructor of Seed class
     */
    public Seed() {
        //initialize default data for RL demand
        for (float[] demand : RL_SpecifiedDemand_GP) {
            Arrays.fill(demand, 1f);
        }
        for (float[] demand : RL_SpecifiedDemand_ML) {
            Arrays.fill(demand, 1f);
        }
        //add default adaptive ramp metering parameters
        rmALINEA.put("Default", new RampMeteringALINEAData());
        rmFuzzy.put("Default", new RampMeteringFuzzyData());
    }

    /**
     * Generate GPSegments, calculate number of analysis periods based on start
     * And end time
     *
     * @param numSegment number of GPSegments
     */
    public void generateSegments(int numSegment) {
        calNumPeriods();

        GPSegments = generateSegments(numSegment, inNumPeriod, CEConst.SEG_TYPE_GP);
        rampMeteringGP = new RMHelper(this);

        if (inManagedLaneUsed) {
            MLSegments = generateSegments(numSegment, inNumPeriod, CEConst.SEG_TYPE_ML);
            rampMeteringML = new RMHelper(this);
            connectGPAndMLSegments();
        } else {
            MLSegments = null;
            rampMeteringML = null;
        }

        fireDataChanged(CHANGE_SEED);
    }

    /**
     * Calculate number of periods based on start and end times
     */
    private void calNumPeriods() {
        if (inEndTime.hour == inStartTime.hour && inEndTime.minute == inStartTime.minute) {
            inNumPeriod = 96;
        } else {
            inNumPeriod = ((inEndTime.toMinute() - inStartTime.toMinute()) / LENGTH_OF_EACH_PERIOD.toMinute() + 96) % 96;
        }
    }

    /**
     * Helper for generate segment
     *
     * @param numSegment number of segments
     * @param numPeriod number of periods
     * @param GPMLType segment GP/ML type
     * @return ArrayList of generated segments
     */
    private ArrayList<GPMLSegment> generateSegments(int numSegment, int numPeriod, int GPMLType) {
        ArrayList<GPMLSegment> segments = new ArrayList<>();
        for (int seg = 0; seg < numSegment; seg++) {
            segments.add(new GPMLSegment(this, numPeriod));
            if (seg > 0) {
                segments.get(seg).inUpSeg = segments.get(seg - 1);
                segments.get(seg - 1).inDownSeg = segments.get(seg);
            }
            segments.get(seg).inIndex = seg;
            segments.get(seg).KJ = inJamDensity;
            segments.get(seg).inCapacityDropPercentage = inCapacityDropPercentage / 100f;
            segments.get(seg).inGPMLType = GPMLType;
            if (GPMLType == CEConst.SEG_TYPE_ML) {
                segments.get(seg).inMainlineNumLanes = CEHelper.int_1D(numPeriod, 1);
                segments.get(seg).inSegLength_ft = GPSegments.get(seg).inSegLength_ft;
            }
        }
        segments.trimToSize();
        return segments;
    }

    /**
     * Connect parallel GP and ML segments
     */
    private void connectGPAndMLSegments() {
        try {
            for (int seg = 0; seg < GPSegments.size(); seg++) {
                GPSegments.get(seg).inParallelSeg = MLSegments.get(seg);
                MLSegments.get(seg).inParallelSeg = GPSegments.get(seg);
            }
        } catch (Exception e) {
            System.out.println("Error when connect GP and ML segments " + e.toString());
        }
    }

    /**
     * Disconnect parallel GP and ML segments
     */
    private void disconnectGPAndMLSegments() {
        try {
            for (GPMLSegment GPSegment : GPSegments) {
                GPSegment.inParallelSeg = null;
                if (GPSegment.inType == CEConst.SEG_TYPE_ACS) {
                    GPSegment.inType = CEConst.SEG_TYPE_B;
                }
            }
        } catch (Exception e) {
            System.out.println("Error when connect GP and ML segments " + e.toString());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PERIOD AND SEGMENT MODIFIER">
    /**
     * Add multiple segments
     *
     * @param index index of the segment to be added, start from 0
     * @param num number of segments to be added
     * @return whether add segment is successful
     */
    public String addSegment(int index, int num) {
        _addSegment(index, num, GPSegments, CEConst.SEG_TYPE_GP);
        getRampMetering(0, -1, CEConst.SEG_TYPE_GP).addSegment(index, num);

        if (inManagedLaneUsed) {
            _addSegment(index, num, MLSegments, CEConst.SEG_TYPE_ML);
            getRampMetering(0, -1, CEConst.SEG_TYPE_ML).addSegment(index, num);
            connectGPAndMLSegments();
        }
        fireDataChanged(CHANGE_SEED);
        return num + (num > 1 ? " segments " : " segment ") + "added";
    }

    /**
     * Helper for add segments
     *
     * @param index index of the segment to be added, start from 0
     * @param num number of segments to be added
     * @param segments original segments ArrayList
     * @param GPMLType segment GP/ML type
     */
    private void _addSegment(int index, int num, ArrayList<GPMLSegment> segments, int GPMLType) {
        try {
            int originNumSegment = segments.size();

            for (int i = 0; i < num; i++) {
                GPMLSegment newSeg = new GPMLSegment(this, inNumPeriod);
                newSeg.inGPMLType = GPMLType;
                newSeg.inCapacityDropPercentage = inCapacityDropPercentage / 100f;
                newSeg.KJ = inJamDensity;
                newSeg.inET = this.inGlobalETDefault;
                segments.add(index, newSeg);

                if (GPMLType == CEConst.SEG_TYPE_ML) {
                    newSeg.inMainlineNumLanes = CEHelper.int_1D(inNumPeriod, 1);
                }
            }

            for (int i = index; i < index + num - 1; i++) {
                segments.get(i).inDownSeg = segments.get(i + 1);
                segments.get(i + 1).inUpSeg = segments.get(i);
            }

            //not at head
            if (index > 0) {
                segments.get(index - 1).inDownSeg = segments.get(index);
                segments.get(index).inUpSeg = segments.get(index - 1);
            }

            //not at tail
            if (index < originNumSegment) {
                segments.get(index + num).inUpSeg = segments.get(index + num - 1);
                segments.get(index + num - 1).inDownSeg = segments.get(index + num);
            }

            reindexSegment(segments);
            segments.trimToSize();
        } catch (Exception e) {
            System.out.println("Error when add segments " + e.toString());
        }
    }

    /**
     * Delete multiple segments
     *
     * @param fromIndex index of the first segment (inclusive) to be deleted,
     * start from 0
     * @param toIndex index of the last segment (inclusive) to be deleted, start
     * from 0
     * @return whether delete segment is successful
     */
    public String delSegment(int fromIndex, int toIndex) {
        int num = toIndex - fromIndex + 1;
        _delSegment(fromIndex, toIndex, GPSegments);
        getRampMetering(0, -1, CEConst.SEG_TYPE_GP).removeSegment(fromIndex, toIndex - fromIndex + 1);
        if (inManagedLaneUsed) {
            _delSegment(fromIndex, toIndex, MLSegments);
            getRampMetering(0, -1, CEConst.SEG_TYPE_ML).removeSegment(fromIndex, toIndex - fromIndex + 1);
        }
        fireDataChanged(CHANGE_SEED);
        return num + (num > 1 ? " segments " : " segment ") + "deleted";
    }

    /**
     * Helper for delete segments
     *
     * @param fromIndex index of the first segment (inclusive) to be deleted,
     * start from 0
     * @param toIndex index of the last segment (inclusive) to be deleted, start
     * from 0
     * @param segments original segments ArrayList
     */
    private void _delSegment(int fromIndex, int toIndex, ArrayList<GPMLSegment> segments) {
        try {
            int originNumSegment = segments.size();
            int num = toIndex - fromIndex + 1;

            if (fromIndex < 0 || toIndex >= originNumSegment
                    || fromIndex > toIndex) {
                throw new IllegalArgumentException("Invalid segment index");
            } else {
                for (int i = 0; i < num; i++) {
                    segments.remove(fromIndex);
                }
            }

            if (fromIndex > 0 && toIndex < originNumSegment - 1) {
                //original head and tail are not deleted
                segments.get(fromIndex - 1).inDownSeg = segments.get(fromIndex);
                segments.get(fromIndex).inUpSeg = segments.get(fromIndex - 1);
            } else {
                if (fromIndex == 0) {
                    segments.get(0).inUpSeg = null;
                }
                if (toIndex == originNumSegment - 1) {
                    segments.get(segments.size() - 1).inDownSeg = null;
                }
            }

            reindexSegment(segments);
            segments.trimToSize();
        } catch (Exception e) {
            System.out.println("Fail to delete segment " + e.toString());
        }
    }

    /**
     * Renumbering segments
     *
     * @param segments original segments ArrayList
     */
    private void reindexSegment(ArrayList<GPMLSegment> segments) {
        for (int index = 0; index < segments.size(); index++) {
            segments.get(index).inIndex = index;
        }
    }

    /**
     * Add one or more analysis periods
     *
     * @param numPeriodToBeAdded number of analysis periods to be added
     * @param isAtBeginning whether add these new analysis periods at the
     * beginning (true) or at the end (false)
     * @return whether add period is successful
     */
    public String addPeriod(int numPeriodToBeAdded, boolean isAtBeginning) {
        if (numPeriodToBeAdded > 0) {
            try {
                if (isAtBeginning) {
                    inStartTime = CETime.addTime(inStartTime, LENGTH_OF_EACH_PERIOD, -numPeriodToBeAdded);
                } else {
                    inEndTime = CETime.addTime(inEndTime, LENGTH_OF_EACH_PERIOD, numPeriodToBeAdded);
                }
                calNumPeriods();

                for (GPMLSegment segment : GPSegments) {
                    _addPeriod(segment, numPeriodToBeAdded, isAtBeginning);
                }

                if (isAtBeginning) {
                    getRampMetering(0, -1, CEConst.SEG_TYPE_GP).addPeriod(0, numPeriodToBeAdded);
                } else {
                    getRampMetering(0, -1, CEConst.SEG_TYPE_GP).addPeriod(inNumPeriod - numPeriodToBeAdded, numPeriodToBeAdded);
                }

                if (inManagedLaneUsed) {
                    for (GPMLSegment segment : MLSegments) {
                        _addPeriod(segment, numPeriodToBeAdded, isAtBeginning);
                    }

                    if (isAtBeginning) {
                        getRampMetering(0, -1, CEConst.SEG_TYPE_ML).addPeriod(0, numPeriodToBeAdded);
                    } else {
                        getRampMetering(0, -1, CEConst.SEG_TYPE_ML).addPeriod(inNumPeriod - numPeriodToBeAdded, numPeriodToBeAdded);
                    }
                }

                fireDataChanged(CHANGE_SEED);
                return numPeriodToBeAdded + (numPeriodToBeAdded == 1 ? " period " : " periods ")
                        + "added at the " + (isAtBeginning ? "beginning" : "end");
            } catch (Exception e) {
                return "Fail to add period " + e.toString();
            }
        } else {
            return "Fail to add period, invalid number of periods";
        }
    }

    /**
     * Helper for add one or more analysis periods
     *
     * @param segment original segment
     * @param numPeriodToBeAdded number of analysis periods to be added
     * @param isAtBeginning whether add these new analysis periods at the
     * beginning (true) or at the end (false)
     */
    private void _addPeriod(GPMLSegment segment, int numPeriodToBeAdded, boolean isAtBeginning) {
        if (isAtBeginning) {
            for (int count = 0; count < numPeriodToBeAdded; count++) {
                segment.P = inNumPeriod;

                segment.inMainlineNumLanes.add(0, segment.inMainlineNumLanes.get(0));
                //segment.inOnNumLanes.add(0, segment.inOnNumLanes.get(0));
                //segment.inOffNumLanes.add(0, segment.inOffNumLanes.get(0));

                segment.inMainlineDemand_veh.add(0, segment.inMainlineDemand_veh.get(0));
                segment.inOnDemand_veh.add(0, segment.inOnDemand_veh.get(0));
                segment.inOffDemand_veh.add(0, segment.inOffDemand_veh.get(0));
                segment.inRRDemand_veh.add(0, segment.inRRDemand_veh.get(0));

                segment.inMainlineFFS.add(0, segment.inMainlineFFS.get(0));
                segment.inMainlineTruckSingle.add(0, segment.inMainlineTruckSingle.get(0));
                segment.inMainlineTruckTrailer.add(0, segment.inMainlineTruckTrailer.get(0));
                segment.inONRTruckSingle.add(0, segment.inONRTruckSingle.get(0));
                segment.inONRTruckTrailer.add(0, segment.inONRTruckTrailer.get(0));
                segment.inOFRTruckSingle.add(0, segment.inOFRTruckSingle.get(0));
                segment.inOFRTruckTrailer.add(0, segment.inOFRTruckTrailer.get(0));
                segment.inOnFFS.add(0, segment.inOnFFS.get(0));
                segment.inOffFFS.add(0, segment.inOffFFS.get(0));

                segment.inUCAF.add(0, segment.inUCAF.get(0));
                segment.inUOAF.add(0, segment.inUOAF.get(0));
                segment.inUDAF.add(0, segment.inUDAF.get(0));
                segment.inUSAF.add(0, segment.inUSAF.get(0));
                segment.inUDPSAF.add(0, segment.inUDPSAF.get(0));
                segment.inUDPCAF.add(0, segment.inUDPCAF.get(0));
            }
        } else {
            for (int count = 0; count < numPeriodToBeAdded; count++) {
                segment.P = inNumPeriod;

                segment.inMainlineNumLanes.add(segment.inMainlineNumLanes.get(0));
                //segment.inOnNumLanes.add(segment.inOnNumLanes.get(0));
                //segment.inOffNumLanes.add(segment.inOffNumLanes.get(0));

                segment.inMainlineDemand_veh.add(segment.inMainlineDemand_veh.get(0));
                segment.inOnDemand_veh.add(segment.inOnDemand_veh.get(0));
                segment.inOffDemand_veh.add(segment.inOffDemand_veh.get(0));
                segment.inRRDemand_veh.add(segment.inRRDemand_veh.get(0));

                segment.inMainlineFFS.add(segment.inMainlineFFS.get(0));
                segment.inMainlineTruckSingle.add(segment.inMainlineTruckSingle.get(0));
                segment.inMainlineTruckTrailer.add(segment.inMainlineTruckTrailer.get(0));
                segment.inONRTruckSingle.add(segment.inONRTruckSingle.get(0));
                segment.inONRTruckTrailer.add(segment.inONRTruckTrailer.get(0));
                segment.inOFRTruckSingle.add(segment.inOFRTruckSingle.get(0));
                segment.inOFRTruckTrailer.add(segment.inOFRTruckTrailer.get(0));
                segment.inOnFFS.add(segment.inOnFFS.get(0));
                segment.inOffFFS.add(segment.inOffFFS.get(0));

                segment.inUCAF.add(segment.inUCAF.get(0));
                segment.inUOAF.add(segment.inUOAF.get(0));
                segment.inUDAF.add(segment.inUDAF.get(0));
                segment.inUSAF.add(segment.inUSAF.get(0));
                segment.inUDPSAF.add(segment.inUDPSAF.get(0));
                segment.inUDPCAF.add(segment.inUDPCAF.get(0));
            }
        }
    }

    /**
     * Delete one or more analysis periods
     *
     * @param numPeriodToBeDeleted number of analysis periods to be deleted
     * @param isFromBeginning whether delete these new analysis periods from the
     * beginning (true) or from the end (false)
     * @return whether delete period is successful
     */
    public String delPeriod(int numPeriodToBeDeleted, boolean isFromBeginning) {
        if (numPeriodToBeDeleted > 0) {
            try {
                if (isFromBeginning) {
                    inStartTime = CETime.addTime(inStartTime, LENGTH_OF_EACH_PERIOD, numPeriodToBeDeleted);
                } else {
                    inEndTime = CETime.addTime(inEndTime, LENGTH_OF_EACH_PERIOD, -numPeriodToBeDeleted);
                }
                calNumPeriods();

                for (GPMLSegment segment : GPSegments) {
                    _delPeriod(segment, numPeriodToBeDeleted, isFromBeginning);
                }

                if (isFromBeginning) {
                    getRampMetering(0, -1, CEConst.SEG_TYPE_GP).removePeriod(0, numPeriodToBeDeleted);
                } else {
                    getRampMetering(0, -1, CEConst.SEG_TYPE_GP).removePeriod(inNumPeriod, numPeriodToBeDeleted);
                }

                if (inManagedLaneUsed) {
                    for (GPMLSegment segment : MLSegments) {
                        _delPeriod(segment, numPeriodToBeDeleted, isFromBeginning);
                    }

                    if (isFromBeginning) {
                        getRampMetering(0, -1, CEConst.SEG_TYPE_ML).removePeriod(0, numPeriodToBeDeleted);
                    } else {
                        getRampMetering(0, -1, CEConst.SEG_TYPE_ML).removePeriod(inNumPeriod, numPeriodToBeDeleted);
                    }
                }

                fireDataChanged(CHANGE_SEED);
                return numPeriodToBeDeleted + (numPeriodToBeDeleted == 1 ? " period " : " periods ")
                        + "deleted from the " + (isFromBeginning ? "beginning" : "end");
            } catch (Exception e) {
                return "Fail to delete period " + e.toString();
            }
        } else {
            return "Fail to delete period, invalid number of periods";
        }
    }

    /**
     * Delete one or more analysis periods
     *
     * @param segment original segment
     *
     * @param numPeriodToBeDeleted number of analysis periods to be deleted
     * @param isFromBeginning whether delete these new analysis periods from the
     * beginning (true) or from the end (false)
     */
    private void _delPeriod(GPMLSegment segment, int numPeriodToBeDeleted, boolean isFromBeginning) {
        if (isFromBeginning) {
            for (int count = 0; count < numPeriodToBeDeleted; count++) {
                segment.P = inNumPeriod;

                segment.inMainlineNumLanes.remove(0);
                //segment.inOnNumLanes.remove(0);
                //segment.inOffNumLanes.remove(0);

                segment.inMainlineDemand_veh.remove(0);
                segment.inOnDemand_veh.remove(0);
                segment.inOffDemand_veh.remove(0);
                segment.inRRDemand_veh.remove(0);

                segment.inMainlineFFS.remove(0);
                segment.inMainlineTruckSingle.remove(0);
                segment.inMainlineTruckTrailer.remove(0);
                segment.inOffFFS.remove(0);

                segment.inUCAF.remove(0);
                segment.inUOAF.remove(0);
                segment.inUDAF.remove(0);
                segment.inUSAF.remove(0);
                segment.inUDPSAF.remove(0);
                segment.inUDPCAF.remove(0);
            }
        } else {
            for (int count = 0; count < numPeriodToBeDeleted; count++) {
                segment.P = inNumPeriod;

                segment.inMainlineNumLanes.remove(inNumPeriod);
                //segment.inOnNumLanes.remove(inNumPeriod);
                //segment.inOffNumLanes.remove(inNumPeriod);

                segment.inMainlineDemand_veh.remove(inNumPeriod);
                segment.inOnDemand_veh.remove(inNumPeriod);
                segment.inOffDemand_veh.remove(inNumPeriod);
                segment.inRRDemand_veh.remove(inNumPeriod);

                segment.inMainlineFFS.remove(inNumPeriod);
                segment.inMainlineTruckSingle.remove(inNumPeriod);
                segment.inMainlineTruckTrailer.remove(inNumPeriod);
                segment.inOnFFS.remove(inNumPeriod);
                segment.inOffFFS.remove(inNumPeriod);

                segment.inUCAF.remove(inNumPeriod);
                segment.inUOAF.remove(inNumPeriod);
                segment.inUDAF.remove(inNumPeriod);
                segment.inUSAF.remove(inNumPeriod);
                segment.inUDPSAF.remove(inNumPeriod);
                segment.inUDPCAF.remove(inNumPeriod);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SINGLE RUN FUNCTIONS">
    /**
     * Perform a single run for the base case or a particular RL scenario or an
     * ATDM scenario
     *
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index (0 is the first one, -1 means run without
     * ATDM)
     * @return whether analysis is successful
     */
    public int singleRun(int scen, int atdm) {
        synchronized (this) {
            if (scen >= 0 && scen <= inNumScen && GPSegments != null
                    && GPSegments.size() >= 1
                    && GPSegments.get(0).inType == CEConst.SEG_TYPE_B
                    && GPSegments.get(GPSegments.size() - 1).inType == CEConst.SEG_TYPE_B) {
                preprocess(scen, atdm);
                analyze(scen, atdm);
                summary(scen, atdm);
                debugCheckOutput();
                if (atdm < 0) {
                    RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_HAS_OUTPUT;
                } else {
                    ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_HAS_OUTPUT);
                }

                seedInputModified = false;
                bufferATDM = atdm;
                bufferScen = scen;
                return CEConst.SUCCESS;
            } else {
                return CEConst.FAIL;
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PREPROCESS FUNCTIONS">
    /**
     * Preprocess for a particular scenario, will trigger memory location and
     * global preprocess if necessary
     *
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)ƒ
     * @param atdm ATDM set index
     */
    private void preprocess(int scen, int atdm) {
        //required only once for all scenarios
        synchronized (this) {
            if (needMemory) {
                locateMemory();
            }
            if (seedInputModified && inManagedLaneUsed) {
                for (GPMLSegment seg : GPSegments) {
                    configAccessSegment(seg);
                }
                for (GPMLSegment seg : MLSegments) {
                    configAccessSegment(seg);
                }
            }
        }

        //required for each scenario
        for (int seg = 0; seg < GPSegments.size(); seg++) {
            if (!inFreeFlowSpeedKnown) {
                GPSegments.get(seg).estimateFFS();
                if (inManagedLaneUsed) {
                    MLSegments.get(seg).estimateFFS();
                }
            }
            GPSegments.get(seg).scenPreprocess(scen, atdm);
            if (inManagedLaneUsed) {
                MLSegments.get(seg).scenPreprocess(scen, atdm);
            }
        }
    }

    /**
     * Locate memory for seed and segments
     */
    private void locateMemory() {
        for (GPMLSegment segment : GPSegments) {
            segment.initMemory();
        }
        if (inManagedLaneUsed) {
            for (GPMLSegment segment : MLSegments) {
                segment.initMemory();
            }
        }
        locateSummaryMemory();
        needMemory = false;
    }

    /**
     * Create memory space for summary result
     */
    private void locateSummaryMemory() {
        //create memory space
        pOutMaxDC = new HashMap();
        pOutMaxVC = new HashMap();
        pOutDenyVeh = new HashMap();
        pOutMainlineQueueLengthFt = new HashMap();
        pOutOnQueueVeh = new HashMap();
        pOutActualTravelTime = new HashMap(); //min
        pOutFreeFlowTravelTime = new HashMap(); //min
        pOutMainlineDelay = new HashMap(); //min
        pOutOnRampDelay = new HashMap(); //min
        //pOutSystemDelay = new HashMap(); //min
        pOutVMTD = new HashMap(); //veh-miles / interval
        pOutVMTV = new HashMap(); //veh-miles / interval
        pOutVHT = new HashMap(); // travel / interval (hrs)
        pOutVHD = new HashMap(); // delay / interval (hrs)
        pOutSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
        pOutDensityTotal_veh = new HashMap(); //veh/mi/lane
        pOutDensityTotal_pc = new HashMap(); //pc/mi/lane
        pReportDensityFactor = new HashMap();
        pOutReportLOS = new HashMap();
        pOutTravelTimeIndex = new HashMap();

        //all periods summary for each segment [numScen][GPSegments.size()]
        sOutActualTravelTime = new HashMap(); //min
        sOutVMTD = new HashMap(); //veh-miles / interval
        sOutVMTV = new HashMap(); //veh-miles / interval
        sOutVHT = new HashMap(); // travel / interval (hrs)
        sOutVHD = new HashMap(); // delay / interval (hrs)
        sOutSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
        sOutReportDensity_IA_pc = new HashMap(); //pc/mi/lane
        sReportDensityFactor = new HashMap();
        sOutMaxDC = new HashMap();
        sOutMaxVC = new HashMap();

        spOutActualTravelTime = new HashMap(); //min
        spOutVMTD = new HashMap(); //veh-miles / interval
        spOutVMTV = new HashMap(); //veh-miles / interval
        spOutVHT = new HashMap(); // travel / interval (hrs)
        spOutVHD = new HashMap(); // delay / interval (hrs)
        spOutSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
        spOutReportDensity_IA_pc = new HashMap(); //pc/mi/lane

        spOutMaxDC = new HashMap(); //maximum demand / capacity
        spOutMaxVC = new HashMap(); //maximum volume / capacity

        spReportDensityFactor = new HashMap();

        if (inManagedLaneUsed) {
            //create memory space
            pOutMLMaxDC = new HashMap();
            pOutMLMaxVC = new HashMap();
            pOutMLDenyLengthFt = new HashMap();
            pOutMLMainlineQueueLengthFt = new HashMap();
            pOutMLOnQueueLengthFt = new HashMap();
            pOutMLActualTravelTime = new HashMap(); //min
            pOutMLFreeFlowTravelTime = new HashMap(); //min
            pOutMLMainlineDelay = new HashMap(); //min
            pOutMLOnRampDelay = new HashMap(); //min
            //pOutMLSystemDelay = new HashMap(); //min
            pOutMLVMTD = new HashMap(); //veh-miles / interval
            pOutMLVMTV = new HashMap(); //veh-miles / interval
            pOutMLVHT = new HashMap(); // travel / interval (hrs)
            pOutMLVHD = new HashMap(); // delay / interval (hrs)
            pOutMLSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
            pOutMLDensityTotal_veh = new HashMap(); //veh/mi/lane
            pOutMLDensityTotal_pc = new HashMap(); //pc/mi/lane
            pMLReportDensityFactor = new HashMap();
            pOutMLReportLOS = new HashMap();
            pOutMLTravelTimeIndex = new HashMap();

            //all periods summary for each segment [numScen][GPSegments.size()]
            sOutMLActualTravelTime = new HashMap(); //min
            sOutMLVMTD = new HashMap(); //veh-miles / interval
            sOutMLVMTV = new HashMap(); //veh-miles / interval
            sOutMLVHT = new HashMap(); // travel / interval (hrs)
            sOutMLVHD = new HashMap(); // delay / interval (hrs)
            sOutMLSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
            sOutMLReportDensity_IA_pc = new HashMap(); //pc/mi/lane
            sMLReportDensityFactor = new HashMap();
            sOutMLMaxDC = new HashMap();
            sOutMLMaxVC = new HashMap();

            spOutMLActualTravelTime = new HashMap(); //min
            spOutMLVMTD = new HashMap(); //veh-miles / interval
            spOutMLVMTV = new HashMap(); //veh-miles / interval
            spOutMLVHT = new HashMap(); // travel / interval (hrs)
            spOutMLVHD = new HashMap(); // delay / interval (hrs)
            spOutMLSpaceMeanSpeed = new HashMap(); //mph = VMTV / VHT
            spOutMLReportDensity_IA_pc = new HashMap(); //pc/mi/lane

            spOutMLMaxDC = new HashMap(); //maximum demand / capacity
            spOutMLMaxVC = new HashMap(); //maximum volume / capacity

            spMLReportDensityFactor = new HashMap();
        } else {
            //create memory space
            pOutMLMaxDC = null;
            pOutMLMaxVC = null;
            pOutMLDenyLengthFt = null;
            pOutMLMainlineQueueLengthFt = null;
            pOutMLOnQueueLengthFt = null;
            pOutMLActualTravelTime = null; //min
            pOutMLFreeFlowTravelTime = null; //min
            pOutMLMainlineDelay = null; //min
            pOutMLOnRampDelay = null; //min
            //pOutMLSystemDelay = null; //min
            pOutMLVMTD = null; //veh-miles / interval
            pOutMLVMTV = null; //veh-miles / interval
            pOutMLVHT = null; // travel / interval (hrs)
            pOutMLVHD = null; // delay / interval (hrs)
            pOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
            pOutMLDensityTotal_veh = null; //veh/mi/lane
            pOutMLDensityTotal_pc = null; //pc/mi/lane
            pMLReportDensityFactor = null;
            pOutMLReportLOS = null;
            pOutMLTravelTimeIndex = null;

            //all periods summary for each segment [numScen][GPSegments.size()]
            sOutMLActualTravelTime = null; //min
            sOutMLVMTD = null; //veh-miles / interval
            sOutMLVMTV = null; //veh-miles / interval
            sOutMLVHT = null; // travel / interval (hrs)
            sOutMLVHD = null; // delay / interval (hrs)
            sOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
            sOutMLReportDensity_IA_pc = null; //pc/mi/lane
            sMLReportDensityFactor = null;
            sOutMLMaxDC = null;
            sOutMLMaxVC = null;

            spOutMLActualTravelTime = null; //min
            spOutMLVMTD = null; //veh-miles / interval
            spOutMLVMTV = null; //veh-miles / interval
            spOutMLVHT = null; // travel / interval (hrs)
            spOutMLVHD = null; // delay / interval (hrs)
            spOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
            spOutMLReportDensity_IA_pc = null; //pc/mi/lane

            spOutMLMaxDC = null; //maximum demand / capacity
            spOutMLMaxVC = null; //maximum volume / capacity

            spMLReportDensityFactor = null;
        }
    }

    /**
     * Configure parameters for access segment
     *
     * @param seg access segment
     */
    private void configAccessSegment(GPMLSegment seg) {
        //may need to change default parameters
        if (seg.inType == CEConst.SEG_TYPE_ACS) {
            seg.inShort_ft = seg.inSegLength_ft;
            seg.inLCFR = 1;
            seg.inLCRF = 1;
            seg.inLCRR = 0;
            seg.inNWL = 2;
            seg.inOnSide = CEConst.RAMP_SIDE_RIGHT;
            seg.inOffSide = CEConst.RAMP_SIDE_RIGHT;
            for (int period = 0; period < inNumPeriod; period++) {
                seg.inOnNumLanes = 1; //.set(period, 1);
                seg.inOnFFS.set(period, 45);
                seg.inOffNumLanes = 1; //.set(period, 1);
                seg.inOffFFS.set(period, 45);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ANALYZE FUNCTIONS">
    /**
     * Analyze a particular scenario using under saturated or over saturated
     * method
     *
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index
     */
    private void analyze(int scen, int atdm) {
        //For debug only
        //DebugOutput.startOutput();
        /**
         * Track how long (in periods) does a scenario in over sat calculation
         */
        int inOverModeGP = 0;
        int inOverModeML = 0;

        for (int period = 0; period < inNumPeriod; period++) {

            if (!forceOversat && isUnderSatGP(scen, atdm, period)) {
                //System.out.println("GP Under " + period);
                //run under sat for this period
                for (GPMLSegment segment : GPSegments) {
                    segment.runUndersaturated(scen, atdm, period, inOverModeGP);
                }
                inOverModeGP = 0; //reset track of over saturated mode
            } else {
                //System.out.println("GP Over " + period);
                inOverModeGP++;
                //run over sat for this period
                for (int step = 0; step < NUM_STEPS; step++) {
                    //run over sat for each 15-sec step
                    for (GPMLSegment segment : GPSegments) {
                        segment.runOversaturated(scen, atdm, period, step, inOverModeGP);
                    }
                }
                //For debug only
//                try {
//                    DebugOutput.write(period, GPSegments);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

            if (inManagedLaneUsed) {
                if (!forceOversat && isUnderSatML(scen, atdm, period)) {
                    //System.out.println("ML Under " + period);
                    //run under sat for this period
                    for (GPMLSegment segment : MLSegments) {
                        segment.runUndersaturated(scen, atdm, period, inOverModeML);
                    }
                    inOverModeML = 0;  //reset track of over saturated mode
                } else {
                    //System.out.println("ML Over " + period);
                    inOverModeML++;
                    //run over sat for this period
                    for (int step = 0; step < NUM_STEPS; step++) {
                        //run over sat for each 15-sec step
                        for (GPMLSegment segment : MLSegments) {
                            segment.runOversaturated(scen, atdm, period, step, inOverModeML);
                        }
                    }
                    //For debug only
//                    try {
//                        DebugOutput.write(period, MLSegments);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }

        //For debug only
        //DebugOutput.finish();
    }

    /**
     * Check whether a particular period in a particular scenario is under
     * saturated or over saturated
     *
     * @param period analysis period index (0 is the first analysis period)
     * @return whether a particular period in a particular scenario is under
     * saturated (true) or over saturated (false)
     */
    private boolean isUnderSatGP(int scen, int atdm, int period) {
        for (GPMLSegment segment : GPSegments) {
            if (!isSegmentUnderSat(segment, scen, atdm, period)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether a particular period in a particular scenario is under
     * saturated or over saturated
     *
     * @param period analysis period index (0 is the first analysis period)
     * @return whether a particular period in a particular scenario is under
     * saturated (true) or over saturated (false)
     */
    private boolean isUnderSatML(int scen, int atdm, int period) {
        for (GPMLSegment segment : MLSegments) {
            if (!isSegmentUnderSat(segment, scen, atdm, period)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper for check whether a particular segment in a particular period in a
     * particular scenario is under saturated or over saturated
     *
     * @param segment segment to be checked
     * @param period analysis period index (0 is the first analysis period)
     * @return whether a particular period in a particular scenario is under
     * saturated (true) or over saturated (false)
     */
    private boolean isSegmentUnderSat(GPMLSegment segment, int scen, int atdm, int period) {
        if (segment.scenMainlineCapacity_veh[period] < segment.scenMainlineDemand_veh[period] //+ ((segment.inUpSeg == null && period > 0) ? segment.scenDenyQ[period - 1] : 0)
                || segment.scenOnCapacity_veh[period] < segment.scenOnDemand_veh[period]
                || (segment.inGPMLType == CEConst.SEG_TYPE_GP
                && (segment.scenRM_veh[period] < segment.scenOnDemand_veh[period]
                || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(segment.inIndex, period) == CEConst.IDS_RAMP_METERING_TYPE_ALINEA
                || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(segment.inIndex, period) == CEConst.IDS_RAMP_METERING_TYPE_FUZZY))) {
            return false;
        }
        if (period > 0
                && (segment.Q[period - 1] > CEConst.ZERO || segment.scenONRQ_End_veh[period - 1] > CEConst.ZERO)) {
            return false;
        }
        return true;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ATDM TEST FUNCTIONS">
    /**
     * Test an ATDM plans on a particular scenario
     *
     * @param atdmScenario the set of ATDM plans to be tested
     * @param scen scenario index (0 for seed, RL scenarios start with 1)
     * @return Facility summary of each ATDM plan
     */
    public FacilitySummary testATDM(ATDMScenario[] atdmScenario, int scen) {
        if (atdmScenario == null || scen > this.inNumScen) {
            return null;
        }
        int testATDMIndex = ATDMSets.size();
        HashMap<Integer, ATDMScenario[]> testATDMSet = new HashMap();
        testATDMSet.put(scen, atdmScenario);
        ATDMSets.add(testATDMSet);

        singleRun(scen, testATDMIndex);
        FacilitySummary result = new FacilitySummary(this, scen, testATDMIndex);

        ATDMSets.remove(testATDMIndex);

        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SUMMARY FUNCTIONS">
    /**
     * generate summary result for a particular scenario
     *
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index
     */
    private void summary(int scen, int atdm) {
        for (GPMLSegment segment : GPSegments) {
            segment.calExtendedResults();
        }
        if (inManagedLaneUsed) {
            for (GPMLSegment segment : MLSegments) {
                segment.calExtendedResults();
            }
        }

        String scenATDM = new ScenATDM(scen, atdm).toString();

        synchronized (this) {
            resetSummary(scenATDM);

            // <editor-fold defaultstate="collapsed" desc="GP Period and Facility-Level Summary">
            //p-   s|
            for (int period = 0; period < inNumPeriod; period++) {
                for (int seg = 0; seg < GPSegments.size(); seg++) {
                    pOutMaxDC.get(scenATDM)[period]
                            = Math.max(GPSegments.get(seg).getScenDC(period), pOutMaxDC.get(scenATDM)[period]);
                    pOutMaxVC.get(scenATDM)[period]
                            = Math.max(GPSegments.get(seg).scenVC[period], pOutMaxVC.get(scenATDM)[period]);

                    spOutMaxDC.put(scenATDM,
                            Math.max(GPSegments.get(seg).getScenDC(period), spOutMaxDC.get(scenATDM)));
                    spOutMaxVC.put(scenATDM,
                            Math.max(GPSegments.get(seg).scenVC[period], spOutMaxVC.get(scenATDM)));

                    pOutActualTravelTime.get(scenATDM)[period] += GPSegments.get(seg).getScenActualTime(period); //min
                    pOutFreeFlowTravelTime.get(scenATDM)[period] += GPSegments.get(seg).getScenFFSTime(period); //min
                    pOutMainlineDelay.get(scenATDM)[period] += GPSegments.get(seg).getScenMainlineDelay(period); //min
                    pOutOnRampDelay.get(scenATDM)[period] += GPSegments.get(seg).scenVHD_R[period]; //min

                    //pOutSystemDelay.get(scenATDM)[period] += GPSegments.get(seg).scenSysDelay[period]; //min
                    pOutVMTD.get(scenATDM)[period] += GPSegments.get(seg).scenVMTD[period]; //veh-miles / interval
                    pOutVMTV.get(scenATDM)[period] += GPSegments.get(seg).scenVMTV[period]; //veh-miles / interval
                    pOutVHT.get(scenATDM)[period] += GPSegments.get(seg).scenVHT[period]; // travel / interval (hrs)
                    pOutVHD.get(scenATDM)[period] += GPSegments.get(seg).scenVHD[period]; //getScenTTI(period); // delay / interval (hrs)

                    sOutVMTD.get(scenATDM)[seg] += GPSegments.get(seg).scenVMTD[period]; //veh-miles / interval
                    sOutVMTV.get(scenATDM)[seg] += GPSegments.get(seg).scenVMTV[period]; //veh-miles / interval
                    sOutVHT.get(scenATDM)[seg] += GPSegments.get(seg).scenVHT[period]; // travel / interval (hrs)
                    sOutVHD.get(scenATDM)[seg] += GPSegments.get(seg).scenVHD[period]; // delay / interval (hrs)

                    pOutDensityTotal_veh.get(scenATDM)[period] += GPSegments.get(seg).scenAllDensity_veh[period]
                            * GPSegments.get(seg).inSegLength_ft * GPSegments.get(seg).scenMainlineNumLanes[period];
                    pOutDensityTotal_pc.get(scenATDM)[period] += GPSegments.get(seg).getScenAllDensity_pc(period)
                            * GPSegments.get(seg).inSegLength_ft * GPSegments.get(seg).scenMainlineNumLanes[period];
                    pReportDensityFactor.get(scenATDM)[period] += GPSegments.get(seg).inSegLength_ft * GPSegments.get(seg).scenMainlineNumLanes[period];

                    if (GPSegments.get(seg).inType == CEConst.SEG_TYPE_ONR || GPSegments.get(seg).inType == CEConst.SEG_TYPE_OFR) {
                        sOutReportDensity_IA_pc.get(scenATDM)[seg] += GPSegments.get(seg).scenIADensity_pc[period]
                                * GPSegments.get(seg).scenMainlineNumLanes[period]; //pc/mi/lane
                    } else {
                        sOutReportDensity_IA_pc.get(scenATDM)[seg] += GPSegments.get(seg).getScenAllDensity_pc(period)
                                * GPSegments.get(seg).scenMainlineNumLanes[period]; //pc/mi/lane
                    }
                    sReportDensityFactor.get(scenATDM)[seg] += GPSegments.get(seg).scenMainlineNumLanes[period];
                }
            }

            for (int period = 0; period < inNumPeriod; period++) {
                pOutSpaceMeanSpeed.get(scenATDM)[period] = pOutVMTV.get(scenATDM)[period] / pOutVHT.get(scenATDM)[period]; //mph = VMTV / VHT
                pOutTravelTimeIndex.get(scenATDM)[period] = pOutActualTravelTime.get(scenATDM)[period] / pOutFreeFlowTravelTime.get(scenATDM)[period];

                pOutDensityTotal_veh.get(scenATDM)[period] /= pReportDensityFactor.get(scenATDM)[period];
                pOutDensityTotal_pc.get(scenATDM)[period] /= pReportDensityFactor.get(scenATDM)[period];

                pOutDenyVeh.get(scenATDM)[period] = GPSegments.get(0).scenDenyQ[period];
                pOutMainlineQueueLengthFt.get(scenATDM)[period] = sumMLQueue(period);
                pOutOnQueueVeh.get(scenATDM)[period] = sumONRQueue(period);

                pOutReportLOS.get(scenATDM)[period] = funcFacilityDensityLOS(pOutDensityTotal_pc.get(scenATDM)[period], pOutMaxDC.get(scenATDM)[period]);
            }

            for (int seg = 0; seg < GPSegments.size(); seg++) {
                sOutSpaceMeanSpeed.get(scenATDM)[seg] = sOutVMTV.get(scenATDM)[seg] / sOutVHT.get(scenATDM)[seg]; //mph = VMTV / VHT
                sOutActualTravelTime.get(scenATDM)[seg] = GPSegments.get(seg).inSegLength_ft / 5280f / sOutSpaceMeanSpeed.get(scenATDM)[seg] * 60;

                sOutMaxDC.get(scenATDM)[seg] = GPSegments.get(seg).scenMaxDC;
                sOutMaxVC.get(scenATDM)[seg] = GPSegments.get(seg).scenMaxVC;

                sOutReportDensity_IA_pc.get(scenATDM)[seg] /= sReportDensityFactor.get(scenATDM)[seg];
            }

            for (int seg = 0; seg < GPSegments.size(); seg++) {

                spOutActualTravelTime.put(scenATDM,
                        spOutActualTravelTime.get(scenATDM)
                        + sOutActualTravelTime.get(scenATDM)[seg]); //min
                spOutVMTD.put(scenATDM,
                        spOutVMTD.get(scenATDM)
                        + sOutVMTD.get(scenATDM)[seg]); //veh-miles / interval
                spOutVMTV.put(scenATDM,
                        spOutVMTV.get(scenATDM)
                        + sOutVMTV.get(scenATDM)[seg]); //veh-miles / interval
                spOutVHT.put(scenATDM,
                        spOutVHT.get(scenATDM)
                        + sOutVHT.get(scenATDM)[seg]); // travel / interval (hrs)
                spOutVHD.put(scenATDM,
                        spOutVHD.get(scenATDM)
                        + sOutVHD.get(scenATDM)[seg]); // delay / interval (hrs)

                //Warning: Number of lanes may change over time
                spOutReportDensity_IA_pc.put(scenATDM,
                        spOutReportDensity_IA_pc.get(scenATDM)
                        + sOutReportDensity_IA_pc.get(scenATDM)[seg]
                        * GPSegments.get(seg).inSegLength_ft * GPSegments.get(seg).scenMainlineNumLanes[0]); //pc/mi/lane
                spReportDensityFactor.put(scenATDM,
                        spReportDensityFactor.get(scenATDM)
                        + GPSegments.get(seg).inSegLength_ft * GPSegments.get(seg).scenMainlineNumLanes[0]);

            }
            spOutSpaceMeanSpeed.put(scenATDM, spOutVMTV.get(scenATDM) / spOutVHT.get(scenATDM));
            spOutReportDensity_IA_pc.put(scenATDM,
                    spOutReportDensity_IA_pc.get(scenATDM) / spReportDensityFactor.get(scenATDM));
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="ML Period and Facility-Level Summary">
            if (inManagedLaneUsed) {
                //p-   s|
                for (int period = 0; period < inNumPeriod; period++) {
                    for (int seg = 0; seg < MLSegments.size(); seg++) {
                        pOutMLMaxDC.get(scenATDM)[period] = Math.max(MLSegments.get(seg).getScenDC(period), pOutMLMaxDC.get(scenATDM)[period]);
                        pOutMLMaxVC.get(scenATDM)[period] = Math.max(MLSegments.get(seg).scenVC[period], pOutMLMaxVC.get(scenATDM)[period]);

                        spOutMLMaxDC.put(scenATDM, Math.max(MLSegments.get(seg).getScenDC(period), spOutMLMaxDC.get(scenATDM)));
                        spOutMLMaxVC.put(scenATDM, Math.max(MLSegments.get(seg).scenVC[period], spOutMLMaxVC.get(scenATDM)));

                        pOutMLActualTravelTime.get(scenATDM)[period] += MLSegments.get(seg).getScenActualTime(period); //min
                        pOutMLFreeFlowTravelTime.get(scenATDM)[period] += MLSegments.get(seg).getScenFFSTime(period); //min
                        pOutMLMainlineDelay.get(scenATDM)[period] += MLSegments.get(seg).getScenMainlineDelay(period); //min
                        pOutMLOnRampDelay.get(scenATDM)[period] += MLSegments.get(seg).scenVHD_R[period]; //min

                        //pOutMLSystemDelay.get(scenATDM)[period] += MLSegments.get(seg).scenSysDelay[period]; //min
                        pOutMLVMTD.get(scenATDM)[period] += MLSegments.get(seg).scenVMTD[period]; //veh-miles / interval
                        pOutMLVMTV.get(scenATDM)[period] += MLSegments.get(seg).scenVMTV[period]; //veh-miles / interval
                        pOutMLVHT.get(scenATDM)[period] += MLSegments.get(seg).scenVHT[period]; // travel / interval (hrs)
                        pOutMLVHD.get(scenATDM)[period] += MLSegments.get(seg).getScenTTI(period); // delay / interval (hrs)

                        sOutMLVMTD.get(scenATDM)[seg] += MLSegments.get(seg).scenVMTD[period]; //veh-miles / interval
                        sOutMLVMTV.get(scenATDM)[seg] += MLSegments.get(seg).scenVMTV[period]; //veh-miles / interval
                        sOutMLVHT.get(scenATDM)[seg] += MLSegments.get(seg).scenVHT[period]; // travel / interval (hrs)
                        sOutMLVHD.get(scenATDM)[seg] += MLSegments.get(seg).scenVHD[period]; // delay / interval (hrs)

                        pOutMLDensityTotal_veh.get(scenATDM)[period] += MLSegments.get(seg).scenAllDensity_veh[period]
                                * MLSegments.get(seg).inSegLength_ft * MLSegments.get(seg).scenMainlineNumLanes[period];
                        pOutMLDensityTotal_pc.get(scenATDM)[period] += MLSegments.get(seg).getScenAllDensity_pc(period)
                                * MLSegments.get(seg).inSegLength_ft * MLSegments.get(seg).scenMainlineNumLanes[period];
                        pMLReportDensityFactor.get(scenATDM)[period] += MLSegments.get(seg).inSegLength_ft * MLSegments.get(seg).scenMainlineNumLanes[period];

                        if (MLSegments.get(seg).inType == CEConst.SEG_TYPE_ONR || MLSegments.get(seg).inType == CEConst.SEG_TYPE_OFR) {
                            sOutMLReportDensity_IA_pc.get(scenATDM)[seg] += MLSegments.get(seg).scenIADensity_pc[period]
                                    * MLSegments.get(seg).scenMainlineNumLanes[period]; //pc/mi/lane
                        } else {
                            sOutMLReportDensity_IA_pc.get(scenATDM)[seg] += MLSegments.get(seg).getScenAllDensity_pc(period)
                                    * MLSegments.get(seg).scenMainlineNumLanes[period]; //pc/mi/lane
                        }
                        sMLReportDensityFactor.get(scenATDM)[seg] += MLSegments.get(seg).scenMainlineNumLanes[period];
                    }
                }

                for (int period = 0; period < inNumPeriod; period++) {
                    pOutMLSpaceMeanSpeed.get(scenATDM)[period] = pOutMLVMTV.get(scenATDM)[period] / pOutMLVHT.get(scenATDM)[period]; //mph = VMTV / VHT
                    pOutMLTravelTimeIndex.get(scenATDM)[period] = pOutMLActualTravelTime.get(scenATDM)[period] / pOutMLFreeFlowTravelTime.get(scenATDM)[period];

                    pOutMLDensityTotal_veh.get(scenATDM)[period] /= pMLReportDensityFactor.get(scenATDM)[period];
                    pOutMLDensityTotal_pc.get(scenATDM)[period] /= pMLReportDensityFactor.get(scenATDM)[period];

                    pOutMLDenyLengthFt.get(scenATDM)[period] = MLSegments.get(0).scenDenyQ[period];
                    pOutMLMainlineQueueLengthFt.get(scenATDM)[period] = sumMLQueue(period);
                    pOutMLOnQueueLengthFt.get(scenATDM)[period] = sumONRQueue(period);

                    pOutMLReportLOS.get(scenATDM)[period] = funcFacilityDensityLOS(pOutMLDensityTotal_pc.get(scenATDM)[period], pOutMLMaxDC.get(scenATDM)[period]);
                }

                for (int seg = 0; seg < MLSegments.size(); seg++) {
                    sOutMLSpaceMeanSpeed.get(scenATDM)[seg] = sOutMLVMTV.get(scenATDM)[seg] / sOutMLVHT.get(scenATDM)[seg]; //mph = VMTV / VHT
                    sOutMLActualTravelTime.get(scenATDM)[seg] = MLSegments.get(seg).inSegLength_ft / 5280f / sOutMLSpaceMeanSpeed.get(scenATDM)[seg] * 60;

                    sOutMLMaxDC.get(scenATDM)[seg] = MLSegments.get(seg).scenMaxDC;
                    sOutMLMaxVC.get(scenATDM)[seg] = MLSegments.get(seg).scenMaxVC;

                    sOutMLReportDensity_IA_pc.get(scenATDM)[seg] /= sMLReportDensityFactor.get(scenATDM)[seg];
                }

                for (int seg = 0; seg < MLSegments.size(); seg++) {

                    spOutMLActualTravelTime.put(scenATDM,
                            spOutMLActualTravelTime.get(scenATDM) + sOutMLActualTravelTime.get(scenATDM)[seg]); //min
                    spOutMLVMTD.put(scenATDM,
                            spOutMLVMTD.get(scenATDM) + sOutMLVMTD.get(scenATDM)[seg]); //veh-miles / interval
                    spOutMLVMTV.put(scenATDM,
                            spOutMLVMTV.get(scenATDM) + sOutMLVMTV.get(scenATDM)[seg]); //veh-miles / interval
                    spOutMLVHT.put(scenATDM,
                            spOutMLVHT.get(scenATDM) + sOutMLVHT.get(scenATDM)[seg]); // travel / interval (hrs)
                    spOutMLVHD.put(scenATDM,
                            spOutMLVHD.get(scenATDM) + sOutMLVHD.get(scenATDM)[seg]); // delay / interval (hrs)

                    //Warning: number of lanes may change over time
                    spOutMLReportDensity_IA_pc.put(scenATDM,
                            spOutMLReportDensity_IA_pc.get(scenATDM)
                            + sOutMLReportDensity_IA_pc.get(scenATDM)[seg]
                            * MLSegments.get(seg).inSegLength_ft * MLSegments.get(seg).scenMainlineNumLanes[0]); //pc/mi/lane
                    spMLReportDensityFactor.put(scenATDM,
                            spMLReportDensityFactor.get(scenATDM)
                            + MLSegments.get(seg).inSegLength_ft * MLSegments.get(seg).scenMainlineNumLanes[0]);

                }
                spOutMLSpaceMeanSpeed.put(scenATDM, spOutMLVMTV.get(scenATDM) / spOutMLVHT.get(scenATDM));
                spOutMLReportDensity_IA_pc.put(scenATDM,
                        spOutMLReportDensity_IA_pc.get(scenATDM) / spMLReportDensityFactor.get(scenATDM));
            }
            // </editor-fold>
        }
    }

    /**
     * Delete previous summary result for a particular scenario
     *
     * @param scenATDM scenario index and ATDM set index in String
     */
    private void resetSummary(String scenATDM) {
        //create memory space
        pOutMaxDC.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutMaxVC.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutDenyVeh.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutMainlineQueueLengthFt.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutOnQueueVeh.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutActualTravelTime.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
        pOutFreeFlowTravelTime.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
        pOutMainlineDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
        pOutOnRampDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
        //pOutSystemDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
        pOutVMTD.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh-miles / interval
        pOutVMTV.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh-miles / interval
        pOutVHT.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); // travel / interval (hrs)
        pOutVHD.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); // delay / interval (hrs)
        pOutSpaceMeanSpeed.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //mph = VMTV / VHT
        pOutDensityTotal_veh.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh/mi/lane
        pOutDensityTotal_pc.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //pc/mi/lane
        pReportDensityFactor.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
        pOutReportLOS.put(scenATDM, CEHelper.str_1D_normal(inNumPeriod, ""));
        pOutTravelTimeIndex.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));

        //all periods summary for each segment [numScen][GPSegments.size()]
        sOutActualTravelTime.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //min
        sOutVMTD.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //veh-miles / interval
        sOutVMTV.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //veh-miles / interval
        sOutVHT.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); // travel / interval (hrs)
        sOutVHD.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); // delay / interval (hrs)
        sOutSpaceMeanSpeed.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //mph = VMTV / VHT
        sOutReportDensity_IA_pc.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //pc/mi/lane
        sReportDensityFactor.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));
        sOutMaxDC.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));
        sOutMaxVC.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));

        spOutActualTravelTime.put(scenATDM, 0f); //min
        spOutVMTD.put(scenATDM, 0f); //veh-miles / interval
        spOutVMTV.put(scenATDM, 0f); //veh-miles / interval
        spOutVHT.put(scenATDM, 0f); // travel / interval (hrs)
        spOutVHD.put(scenATDM, 0f); // delay / interval (hrs)
        spOutSpaceMeanSpeed.put(scenATDM, 0f); //mph = VMTV / VHT
        spOutReportDensity_IA_pc.put(scenATDM, 0f); //pc/mi/lane

        spOutMaxDC.put(scenATDM, 0f); //maximum demand / capacity
        spOutMaxVC.put(scenATDM, 0f); //maximum volume / capacity

        spReportDensityFactor.put(scenATDM, 0f);

        if (inManagedLaneUsed) {
            //create memory space
            pOutMLMaxDC.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLMaxVC.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLDenyLengthFt.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLMainlineQueueLengthFt.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLOnQueueLengthFt.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLActualTravelTime.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
            pOutMLFreeFlowTravelTime.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
            pOutMLMainlineDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
            pOutMLOnRampDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
            //pOutMLSystemDelay.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //min
            pOutMLVMTD.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh-miles / interval
            pOutMLVMTV.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh-miles / interval
            pOutMLVHT.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); // travel / interval (hrs)
            pOutMLVHD.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); // delay / interval (hrs)
            pOutMLSpaceMeanSpeed.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //mph = VMTV / VHT
            pOutMLDensityTotal_veh.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //veh/mi/lane
            pOutMLDensityTotal_pc.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0)); //pc/mi/lane
            pMLReportDensityFactor.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));
            pOutMLReportLOS.put(scenATDM, CEHelper.str_1D_normal(inNumPeriod, ""));
            pOutMLTravelTimeIndex.put(scenATDM, CEHelper.float_1D_normal(inNumPeriod, 0));

            //all periods summary for each segment [numScen][GPSegments.size()]
            sOutMLActualTravelTime.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //min
            sOutMLVMTD.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //veh-miles / interval
            sOutMLVMTV.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //veh-miles / interval
            sOutMLVHT.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); // travel / interval (hrs)
            sOutMLVHD.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); // delay / interval (hrs)
            sOutMLSpaceMeanSpeed.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //mph = VMTV / VHT
            sOutMLReportDensity_IA_pc.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0)); //pc/mi/lane
            sMLReportDensityFactor.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));
            sOutMLMaxDC.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));
            sOutMLMaxVC.put(scenATDM, CEHelper.float_1D_normal(GPSegments.size(), 0));

            spOutMLActualTravelTime.put(scenATDM, 0f); //min
            spOutMLVMTD.put(scenATDM, 0f); //veh-miles / interval
            spOutMLVMTV.put(scenATDM, 0f); //veh-miles / interval
            spOutMLVHT.put(scenATDM, 0f); // travel / interval (hrs)
            spOutMLVHD.put(scenATDM, 0f); // delay / interval (hrs)
            spOutMLSpaceMeanSpeed.put(scenATDM, 0f); //mph = VMTV / VHT
            spOutMLReportDensity_IA_pc.put(scenATDM, 0f); //pc/mi/lane

            spOutMLMaxDC.put(scenATDM, 0f); //maximum demand / capacity
            spOutMLMaxVC.put(scenATDM, 0f); //maximum volume / capacity

            spMLReportDensityFactor.put(scenATDM, 0f);
        }
    }

    /**
     * Sum the length of mainline queue for a particular period in a particular
     * scenario
     *
     * @param period analysis period index (0 is the first analysis period)
     * @return sum of the length of mainline queue in ft
     */
    private float sumMLQueue(int period) {
        float sum = 0;
        for (GPMLSegment segment : GPSegments) {
            sum += segment.Q[period];
        }
        return sum;
    }

    /**
     * Sum the length of on ramp queue for a particular period in a particular
     * scenario
     *
     * @param period analysis period index (0 is the first analysis period)
     * @return sum of the length of on ramp queue in ft
     */
    private float sumONRQueue(int period) {
        float sum = 0;
        for (GPMLSegment segment : GPSegments) {
            sum += segment.scenONRQ_End_veh[period];
        }
        return sum;
    }

    /**
     * Calculate facility density based level of service in each period.
     *
     * @param density_pc facility density in pc/mi/ln
     * @param demandCapacityRatio demand over capacity ratio
     * @return density based level of service
     */
    private String funcFacilityDensityLOS(float density_pc, float demandCapacityRatio) {
        if (demandCapacityRatio > 1) {
            return "F";
        }
        if (density_pc <= 11.5) {
            return "A";
        } else {
            if (density_pc <= 18.5) {
                return "B";
            } else {
                if (density_pc <= 26.5) {
                    return "C";
                } else {
                    if (density_pc <= 35.5) {
                        return "D";
                    } else {
                        if (density_pc <= 45.5) {
                            return "E";
                        } else {
                            return "F";
                        }
                    }
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UNIVERSAL SETTER FOR int/float/String DATA FIELD">
    /**
     * Set value to a data field in this Seed object or in one of the GPSegments
     *
     * @param ID identifier of the data field
     * @param value value to be assigned
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @param scen scenario index
     * @param atdm ATDM set index
     */
    public void setValue(String ID, Object value, int seg, int period, int scen, int atdm) {
        try {
            //check already exits same value
            if (getValueString(ID, seg, period, scen, atdm) != null && value != null && getValueString(ID, seg, period, scen, atdm).equals(value.toString())) {
                return;
            }

            switch (ID) {
                case CEConst.IDS_SEED_FILE_NAME:
                    inFileName = value == null ? null : value.toString();
                    break;
                case CEConst.IDS_PROJECT_NAME:
                    inProjectName = value == null ? null : value.toString();
                    break;
                case CEConst.IDS_SCEN_NAME:
                    RL_ScenarioInfo.get(scen).name = (value == null ? null : value.toString());
                    break;
                case CEConst.IDS_SEED_URBAN_RURAL_TYPE:
                    inUrbanRuralType = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_NUM_SEGMENT:
                    generateSegments(Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MANAGED_LANE_USED:
                    setManagedLaneUsed(Boolean.parseBoolean(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_OCCU_GP:
                    inGPOccupancy = Float.parseFloat(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_OCCU_ML:
                    inMLOccupancy = Float.parseFloat(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_SEGMENT_TYPE:
                    if (seg != 0 && seg != this.getValueInt(CEConst.IDS_NUM_SEGMENT) - 1) {
                        GPSegments.get(seg).inType = Integer.parseInt(value.toString());
                        if (Integer.parseInt(value.toString()) == CEConst.SEG_TYPE_ACS) {
                            MLSegments.get(seg).inType = CEConst.SEG_TYPE_ACS;
                            MLSegments.get(seg).inOffDemand_veh = (ArrayList<Integer>) (GPSegments.get(seg).inOnDemand_veh.clone());
                            MLSegments.get(seg).inOnDemand_veh = (ArrayList<Integer>) (GPSegments.get(seg).inOffDemand_veh.clone());
                        } else {
                            if (MLSegments != null && MLSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                                MLSegments.get(seg).inType = CEConst.SEG_TYPE_B;
                            }
                        }
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;
                case CEConst.IDS_SEGMENT_NAME:
                    if (!value.toString().equalsIgnoreCase(CEConst.IDS_NA)) {
                        GPSegments.get(seg).segmentName = value.toString();
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;
                case CEConst.IDS_UPSTREAM_COORDINATE:
                    if (!value.toString().equalsIgnoreCase(CEConst.IDS_NA)) {
                        upstreamCoord = (CECoordinate) value;
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;
                case CEConst.IDS_SEGMENT_COORDINATE:
                    if (!value.toString().equalsIgnoreCase(CEConst.IDS_NA)) {
                        GPSegments.get(seg).coord = (CECoordinate) value;
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;
                case CEConst.IDS_SEGMENT_LENGTH_FT:
                    GPSegments.get(seg).inSegLength_ft = Integer.parseInt(value.toString());
                    if (MLSegments != null) {
                        MLSegments.get(seg).inSegLength_ft = Integer.parseInt(value.toString());
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_LANE_WIDTH:
                    GPSegments.get(seg).inLaneWidth_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_LATERAL_CLEARANCE:
                    GPSegments.get(seg).inLateralClearance_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TERRAIN:
                    GPSegments.get(seg).inTerrain = Integer.parseInt(value.toString());
                    switch (GPSegments.get(seg).inTerrain) {
                        case CEConst.TERRAIN_LEVEL:
                            GPSegments.get(seg).inET = 2.0f;
                            break;
                        case CEConst.TERRAIN_ROLLING:
                            GPSegments.get(seg).inET = 2.5f;
                            break;
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;

                case CEConst.IDS_MAIN_NUM_LANES_IN:
                    GPSegments.get(seg).inMainlineNumLanes.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MAIN_DEMAND_VEH:
                    GPSegments.get(seg).inMainlineDemand_veh.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MAIN_FREE_FLOW_SPEED:
                    GPSegments.get(seg).inMainlineFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_SIDE:
                    GPSegments.get(seg).inOnSide = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ACC_DEC_LANE_LENGTH:
                    GPSegments.get(seg).inAccDecLength_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_NUM_ON_RAMP_LANES:
                    GPSegments.get(seg).inOnNumLanes = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL:
                    GPSegments.get(seg).inOnQueueCapacity = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                    GPSegments.get(seg).inOnDemand_veh.set(period, Integer.parseInt(value.toString()));
                    if (GPSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                        MLSegments.get(seg).inOffDemand_veh.set(period, Integer.parseInt(value.toString()));
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED:
                    GPSegments.get(seg).inOnFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_METERING_RATE_FIX:
                    getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringFixRate().set(Integer.parseInt(value.toString()), seg, period);
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_METERING_RATE_ALINEA_KEY:
                    getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringALINEAKey().set(value.toString(), seg, period);
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ON_RAMP_METERING_RATE_FUZZY_KEY:
                    getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringFuzzyKey().set(value.toString(), seg, period);
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_RAMP_METERING_TYPE:
                    getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringType().set(Integer.parseInt(value.toString()), seg, period);
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_OFF_RAMP_SIDE:
                    GPSegments.get(seg).inOffSide = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_NUM_OFF_RAMP_LANES:
                    GPSegments.get(seg).inOffNumLanes = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                    GPSegments.get(seg).inOffDemand_veh.set(period, Integer.parseInt(value.toString()));
                    if (GPSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                        MLSegments.get(seg).inOnDemand_veh.set(period, Integer.parseInt(value.toString()));
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED:
                    GPSegments.get(seg).inOffFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_LENGTH_OF_WEAVING:
                    GPSegments.get(seg).inShort_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                    GPSegments.get(seg).inLCRF = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                    GPSegments.get(seg).inLCFR = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                    GPSegments.get(seg).inLCRR = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_NUM_LANES_WEAVING:
                    GPSegments.get(seg).inNWL = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                    GPSegments.get(seg).inRRDemand_veh.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_HAS_CROSS_WEAVE:
                    MLSegments.get(seg).inGPHasCrossWeave = Boolean.parseBoolean(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                    MLSegments.get(seg).inGPCrossWeaveLCMin = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_CROSS_WEAVE_VOLUME:
                    MLSegments.get(seg).inGPCrossWeaveVolume.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_JAM_DENSITY:
                    inJamDensity = Float.parseFloat(value.toString());
                    if (GPSegments != null) {
                        for (GPMLSegment segment : GPSegments) {
                            segment.KJ = inJamDensity;
                        }
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_CAPACITY_ALPHA:
                    inCapacityDropPercentage = Math.max(0, Math.min(30, Integer.parseInt(value.toString())));
                    if (GPSegments != null) {
                        for (GPMLSegment segment : GPSegments) {
                            segment.inCapacityDropPercentage = inCapacityDropPercentage / 100f;
                        }
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_CAR_ET:
                    GPSegments.get(seg).inET = Float.parseFloat(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE:
                    GPSegments.get(seg).inMainlineTruckSingle.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE:
                    GPSegments.get(seg).inMainlineTruckTrailer.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR:
                    GPSegments.get(seg).inONRTruckSingle.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_TRAILER_PCT_ONR:
                    GPSegments.get(seg).inONRTruckTrailer.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR:
                    GPSegments.get(seg).inOFRTruckSingle.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_TRUCK_TRAILER_PCT_OFR:
                    GPSegments.get(seg).inOFRTruckTrailer.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_CAF:
                    GPSegments.get(seg).inUCAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_OAF:
                    GPSegments.get(seg).inUOAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_DAF:
                    GPSegments.get(seg).inUDAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_SAF:
                    GPSegments.get(seg).inUSAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_DPCAF:
                    GPSegments.get(seg).inUDPCAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_USER_DPSAF:
                    GPSegments.get(seg).inUDPSAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_GP_RL_CAF:
                    if (scen > 0) {
                        RL_Scenarios_GP.CAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_RL_OAF:
                    if (scen > 0) {
                        RL_Scenarios_GP.OAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_RL_DAF:
                    if (scen > 0) {
                        RL_Scenarios_GP.DAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_RL_SAF:
                    if (scen > 0) {
                        RL_Scenarios_GP.SAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_RL_LAFI:
                    if (scen > 0) {
                        RL_Scenarios_GP.LAFI().set(-Math.abs(Integer.parseInt(value.toString())), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_RL_LAFWZ:
                    if (scen > 0) {
                        RL_Scenarios_GP.LAFWZ().set(-Math.abs(Integer.parseInt(value.toString())), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_GP_ATDM_CAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[0].CAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_GP_ATDM_SAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[0].SAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_GP_ATDM_OAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[0].OAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_GP_ATDM_DAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[0].DAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_GP_ATDM_LAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[0].LAF().set(Integer.parseInt(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:
                    if (atdm >= 0) {
                        getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringFixRate().set(Integer.parseInt(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                    if (atdm >= 0) {
                        getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().set(Integer.parseInt(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ML_ATDM_CAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[1].CAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ML_ATDM_SAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[1].SAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ML_ATDM_OAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[1].OAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ML_ATDM_DAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[1].DAF().set(Float.parseFloat(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;
                case CEConst.IDS_ML_ATDM_LAF:
                    if (atdm >= 0) {
                        ATDMSets.get(atdm).get(scen)[1].LAF().set(Integer.parseInt(value.toString()), seg, period);
                        ATDMSets.get(atdm).get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                        fireDataChanged(CHANGE_ATDM);
                    }
                    break;

                case CEConst.IDS_START_TIME:
                    if (GPSegments == null && MLSegments == null) {
                        inStartTime = (CETime) value;
                        calNumPeriods();
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;
                case CEConst.IDS_END_TIME:
                    if (GPSegments == null && MLSegments == null) {
                        inEndTime = (CETime) value;
                        calNumPeriods();
                        fireDataChanged(CHANGE_SEED);
                    }
                    break;

                //Managed Lanes Input Parameters
                case CEConst.IDS_ML_SEGMENT_TYPE:
                    MLSegments.get(seg).inType = Integer.parseInt(value.toString());
                    if (Integer.parseInt(value.toString()) == CEConst.SEG_TYPE_ACS) {
                        GPSegments.get(seg).inType = CEConst.SEG_TYPE_ACS;
                        GPSegments.get(seg).inOffDemand_veh = (ArrayList<Integer>) (MLSegments.get(seg).inOnDemand_veh.clone());
                        GPSegments.get(seg).inOnDemand_veh = (ArrayList<Integer>) (MLSegments.get(seg).inOffDemand_veh.clone());
                    } else {
                        if (GPSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                            GPSegments.get(seg).inType = CEConst.SEG_TYPE_B;
                        }
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_SEPARATION_TYPE:
                    MLSegments.get(seg).inMLSeparation = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_SEGMENT_LENGTH_FT:
                    MLSegments.get(seg).inSegLength_ft = Integer.parseInt(value.toString());
                    GPSegments.get(seg).inSegLength_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_NUM_LANES:
                    MLSegments.get(seg).inMainlineNumLanes.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_DEMAND_VEH:
                    MLSegments.get(seg).inMainlineDemand_veh.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_FREE_FLOW_SPEED:
                    MLSegments.get(seg).inMainlineFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_ACC_DEC_LANE_LENGTH:
                    MLSegments.get(seg).inAccDecLength_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                    MLSegments.get(seg).inOnSide = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_NUM_ON_RAMP_LANES:
                    MLSegments.get(seg).inOnNumLanes = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                    MLSegments.get(seg).inOnDemand_veh.set(period, Integer.parseInt(value.toString()));
                    if (MLSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                        GPSegments.get(seg).inOffDemand_veh.set(period, Integer.parseInt(value.toString()));
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED:
                    MLSegments.get(seg).inOnFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                    MLSegments.get(seg).inOffSide = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_NUM_OFF_RAMP_LANES:
                    MLSegments.get(seg).inOffNumLanes = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                    MLSegments.get(seg).inOffDemand_veh.set(period, Integer.parseInt(value.toString()));
                    if (MLSegments.get(seg).inType == CEConst.SEG_TYPE_ACS) {
                        GPSegments.get(seg).inOnDemand_veh.set(period, Integer.parseInt(value.toString()));
                    }
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED:
                    MLSegments.get(seg).inOffFFS.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_LENGTH_SHORT:
                    MLSegments.get(seg).inShort_ft = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                    MLSegments.get(seg).inLCRF = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                    MLSegments.get(seg).inLCFR = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                    MLSegments.get(seg).inLCRR = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_NUM_LANES_WEAVING:
                    MLSegments.get(seg).inNWL = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                    MLSegments.get(seg).inRRDemand_veh.set(period, Integer.parseInt(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE:
                    MLSegments.get(seg).inMainlineTruckSingle.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE:
                    MLSegments.get(seg).inMainlineTruckTrailer.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_USER_CAF:
                    MLSegments.get(seg).inUCAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_USER_OAF:
                    MLSegments.get(seg).inUOAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_USER_DAF:
                    MLSegments.get(seg).inUDAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_USER_SAF:
                    MLSegments.get(seg).inUSAF.set(period, Float.parseFloat(value.toString()));
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_RL_CAF:
                    if (scen > 0) {
                        RL_Scenarios_ML.CAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_ML_RL_OAF:
                    if (scen > 0) {
                        RL_Scenarios_ML.OAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_ML_RL_DAF:
                    if (scen > 0) {
                        RL_Scenarios_ML.DAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_ML_RL_SAF:
                    if (scen > 0) {
                        RL_Scenarios_ML.SAF().set(Float.parseFloat(value.toString()), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_ML_RL_LAF:
                    if (scen > 0) {
                        RL_Scenarios_ML.LAFI().set(-Math.abs(Integer.parseInt(value.toString())), scen - 1, seg, period);
                        RL_ScenarioInfo.get(scen).statusRL = CEConst.SCENARIO_INPUT_ONLY;
                        fireDataChanged(CHANGE_SCEN);
                    }
                    break;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ML:
                    MLSegments.get(seg).inMLMinLC = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;
                case CEConst.IDS_ML_MAX_LANE_CHANGE_ML:
                    MLSegments.get(seg).inMLMaxLC = Integer.parseInt(value.toString());
                    fireDataChanged(CHANGE_SEED);
                    break;

                //case CEConst.IDS_ON_RAMP_TRUCK_SINGLE_PERCENTAGE:
                //case CEConst.IDS_ON_RAMP_TRUCK_TRAILER_PERCENTAGE:
                //case CEConst.IDS_OFF_RAMP_TRUCK_SINGLE_PERCENTAGE:
                //case CEConst.IDS_OFF_RAMP_TRUCK_TRAILER_PERCENTAGE:
                //ignore these parameters for now
                //break;
                default:
                    System.out.println("Error in setValue: " + ID + " is not supported");
            }
        } catch (Exception e) {
            System.out.println("Error in setValue: " + ID + " " + e.toString());
        }
    }

    /**
     * Set value to a data field in this Seed object or in one of the GPSegments
     *
     * @param ID identifier of the data field
     * @param value value to be assigned
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     */
    public void setValue(String ID, Object value, int seg, int period) {
        setValue(ID, value, seg, period, 0, -1);
    }

    /**
     * Set value to a data field in this Seed object or in one of the GPSegments
     *
     * @param ID identifier of the data field
     * @param value value to be assigned
     * @param seg segment index (0 is the first segment)
     */
    public void setValue(String ID, Object value, int seg) {
        setValue(ID, value, seg, 0, 0, -1);
    }

    /**
     * Set value to a data field in this Seed object or in one of the GPSegments
     *
     * @param ID identifier of the data field
     * @param value value to be assigned
     */
    public void setValue(String ID, Object value) {
        setValue(ID, value, 0, 0, 0, -1);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UNIVERSAL GETTER FOR int/float/String DATA FIELD">
    // <editor-fold defaultstate="collapsed" desc="Universal Getter For Integer">
    /**
     * Get an integer value from a data field in this Seed object or in one of
     * the GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index
     * @return an integer value from a data field in this Seed object or in one
     * of the GPSegments
     */
    public int getValueInt(String ID, int seg, int period, int scen, int atdm) {
        if (ID.startsWith("IDS_ML") && !inManagedLaneUsed) {
            return 0;
        }

        try {
            switch (ID) {
                case CEConst.IDS_SEED_URBAN_RURAL_TYPE:
                    return inUrbanRuralType;
                case CEConst.IDS_NUM_PERIOD:
                    return inNumPeriod;
                case CEConst.IDS_NUM_SCEN:
                    return inNumScen;
                case CEConst.IDS_ATDM_NUM_EACH_RL_SCEN:
                    return countATDM(scen);
                case CEConst.IDS_ATDM_SCEN_IN_EACH_SET:
                    return ATDMSets == null || ATDMSets.get(atdm) == null ? 0 : ATDMSets.get(atdm).size();
                case CEConst.IDS_ATDM_SET_NUM:
                    return ATDMSets == null ? 0 : ATDMSets.size();
                case CEConst.IDS_NUM_SEGMENT:
                    return GPSegments == null ? 0 : GPSegments.size();
                case CEConst.IDS_CAPACITY_ALPHA:
                    return inCapacityDropPercentage;
                case CEConst.IDS_SEGMENT_TYPE:
                    return GPSegments.get(seg).inType;
                case CEConst.IDS_SEGMENT_LENGTH_FT:
                    return GPSegments.get(seg).inSegLength_ft;
                case CEConst.IDS_LANE_WIDTH:
                    return GPSegments.get(seg).inLaneWidth_ft;
                case CEConst.IDS_LATERAL_CLEARANCE:
                    return GPSegments.get(seg).inLateralClearance_ft;
                case CEConst.IDS_TERRAIN:
                    return GPSegments.get(seg).inTerrain;
                case CEConst.IDS_MAIN_NUM_LANES_IN:
                    return GPSegments.get(seg).inMainlineNumLanes.get(period);
                case CEConst.IDS_MAIN_NUM_LANES_IN_AND_ATDM:
                    return GPSegments.get(seg).inMainlineNumLanes.get(period) + getATDMLAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_MAIN_DEMAND_VEH:
                    return (seg == 0) ? GPSegments.get(seg).inMainlineDemand_veh.get(period) : GPSegments.get(seg).unadjustedSegDemand[period];
                case CEConst.IDS_MAIN_FREE_FLOW_SPEED:
                    return GPSegments.get(seg).inMainlineFFS.get(period);
                case CEConst.IDS_ON_RAMP_SIDE:
                    return GPSegments.get(seg).inOnSide;
                case CEConst.IDS_ACC_DEC_LANE_LENGTH:
                    return GPSegments.get(seg).inAccDecLength_ft;
                case CEConst.IDS_NUM_ON_RAMP_LANES:
                    return GPSegments.get(seg).inOnNumLanes;
                case CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL:
                    return GPSegments.get(seg).inOnQueueCapacity;
                case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                    return GPSegments.get(seg).inOnDemand_veh.get(period);
                case CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED:
                    return GPSegments.get(seg).inOnFFS.get(period);
                case CEConst.IDS_RAMP_METERING_TYPE:
                    return getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period);
                case CEConst.IDS_ON_RAMP_METERING_RATE_FIX:
                    return getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringFixRate().get(seg, period);
                case CEConst.IDS_OFF_RAMP_SIDE:
                    return GPSegments.get(seg).inOffSide;
                case CEConst.IDS_NUM_OFF_RAMP_LANES:
                    return GPSegments.get(seg).inOffNumLanes;
                case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                    return GPSegments.get(seg).inOffDemand_veh.get(period);
                case CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED:
                    return GPSegments.get(seg).inOffFFS.get(period);
                case CEConst.IDS_LENGTH_OF_WEAVING:
                    return GPSegments.get(seg).inShort_ft;
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                    return GPSegments.get(seg).inLCRF;
                case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                    return GPSegments.get(seg).inLCFR;
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                    return GPSegments.get(seg).inLCRR;
                case CEConst.IDS_NUM_LANES_WEAVING:
                    return GPSegments.get(seg).inNWL;
                case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                    return GPSegments.get(seg).inRRDemand_veh.get(period);
                case CEConst.IDS_TYPE_USED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenType[period];
                case CEConst.IDS_SCENARIO_STATUS:
                    return RL_ScenarioInfo.get(scen).statusRL;
                case CEConst.IDS_GP_RL_LAFI:
                    return getRLLAFI(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_RL_LAFWZ:
                    return getRLLAFWZ(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_ML_RL_LAF:
                    return getRLLAFI(scen, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_GP_ATDM_LAF:
                    return getATDMLAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                    return getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period);
                case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:
                    return getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringFixRate().get(seg, period);

                //maganged lane parameters
                case CEConst.IDS_ML_SEGMENT_TYPE:
                    return MLSegments.get(seg).inType;
                case CEConst.IDS_ML_SEPARATION_TYPE:
                    return MLSegments.get(seg).inMLSeparation;
                case CEConst.IDS_ML_SEGMENT_LENGTH_FT:
                    return MLSegments.get(seg).inSegLength_ft;
                case CEConst.IDS_ML_NUM_LANES:
                    return MLSegments.get(seg).inMainlineNumLanes.get(period);
                case CEConst.IDS_ML_DEMAND_VEH:
//                    return MLSegments.get(seg).inMainlineDemand_veh.get(period);
                    return (seg == 0) ? MLSegments.get(seg).inMainlineDemand_veh.get(period) : MLSegments.get(seg).unadjustedSegDemand[period];
                case CEConst.IDS_ML_ATDM_LAF:
                    return getATDMLAF(scen, atdm, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_FREE_FLOW_SPEED:
                    return MLSegments.get(seg).inMainlineFFS.get(period);
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                    return MLSegments.get(seg).inOnSide;
                case CEConst.IDS_ML_ACC_DEC_LANE_LENGTH:
                    return MLSegments.get(seg).inAccDecLength_ft;
                case CEConst.IDS_ML_NUM_ON_RAMP_LANES:
                    return MLSegments.get(seg).inOnNumLanes;
                case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                    return MLSegments.get(seg).inOnDemand_veh.get(period);
                case CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED:
                    return MLSegments.get(seg).inOnFFS.get(period);

                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                    return MLSegments.get(seg).inOffSide;
                case CEConst.IDS_ML_NUM_OFF_RAMP_LANES:
                    return MLSegments.get(seg).inOffNumLanes;
                case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                    return MLSegments.get(seg).inOffDemand_veh.get(period);
                case CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED:
                    return MLSegments.get(seg).inOffFFS.get(period);
                case CEConst.IDS_ML_LENGTH_SHORT:
                    return MLSegments.get(seg).inShort_ft;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                    return MLSegments.get(seg).inLCRF;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                    return MLSegments.get(seg).inLCFR;
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                    return MLSegments.get(seg).inLCRR;
                case CEConst.IDS_ML_NUM_LANES_WEAVING:
                    return MLSegments.get(seg).inNWL;
                case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                    return MLSegments.get(seg).inRRDemand_veh.get(period);
                case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                    return MLSegments.get(seg).inGPCrossWeaveLCMin;
                case CEConst.IDS_CROSS_WEAVE_VOLUME:
                    return MLSegments.get(seg).inGPCrossWeaveVolume.get(period);
                case CEConst.IDS_ML_TYPE_USED:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenType[period];
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ML:
                    return MLSegments.get(seg).inMLMinLC;
                case CEConst.IDS_ML_MAX_LANE_CHANGE_ML:
                    return MLSegments.get(seg).inMLMaxLC;

                //Float
                case CEConst.IDS_SEGMENT_LENGTH_MI:
                case CEConst.IDS_TOTAL_LENGTH_MI:
                case CEConst.IDS_JAM_DENSITY:
                case CEConst.IDS_TRUCK_CAR_ET:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE:
                case CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_ONR:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_OFR:
                case CEConst.IDS_GP_USER_CAF:
                case CEConst.IDS_GP_USER_OAF:
                case CEConst.IDS_GP_USER_DAF:
                case CEConst.IDS_GP_USER_SAF:
                case CEConst.IDS_GP_USER_DPCAF:
                case CEConst.IDS_GP_USER_DPSAF:
                case CEConst.IDS_GP_RL_CAF:
                case CEConst.IDS_GP_RL_OAF:
                case CEConst.IDS_GP_RL_DAF:
                case CEConst.IDS_GP_RL_SAF:
                case CEConst.IDS_GP_ATDM_CAF:
                case CEConst.IDS_GP_ATDM_OAF:
                case CEConst.IDS_GP_ATDM_DAF:
                case CEConst.IDS_GP_ATDM_SAF:
                case CEConst.IDS_SPEED:
                case CEConst.IDS_TOTAL_DENSITY_VEH:
                case CEConst.IDS_TOTAL_DENSITY_PC:
                case CEConst.IDS_INFLUENCED_DENSITY_PC:
                case CEConst.IDS_ADJUSTED_MAIN_DEMAND:
                case CEConst.IDS_DC:
                case CEConst.IDS_MAIN_CAPACITY:
                case CEConst.IDS_MAIN_VOLUME_SERVED:
                case CEConst.IDS_VC:
                case CEConst.IDS_QUEUE_LENGTH:
                case CEConst.IDS_QUEUE_PERCENTAGE:
                case CEConst.IDS_ON_QUEUE_VEH:
                case CEConst.IDS_ACTUAL_TIME:
                case CEConst.IDS_FFS_TIME:
                case CEConst.IDS_MAINLINE_DELAY:
                //case CEConst.IDS_SYSTEM_DELAY:
                case CEConst.IDS_VMTD:
                case CEConst.IDS_VMTV:
                case CEConst.IDS_VHT:
                case CEConst.IDS_VHD_M:
                case CEConst.IDS_VHD_R:
                case CEConst.IDS_VHD_ACCESS:
                case CEConst.IDS_VHD:
                case CEConst.IDS_SPACE_MEAN_SPEED:
                case CEConst.IDS_TRAVEL_TIME_INDEX:
                case CEConst.IDS_ON_RAMP_CAPACITY:
                case CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ON_RAMP_VOLUME_SERVED:
                case CEConst.IDS_OFF_RAMP_CAPACITY:
                case CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_OFF_RAMP_VOLUME_SERVED:

                case CEConst.IDS_P_ACTUAL_TIME:
                case CEConst.IDS_P_FFS_TIME:
                case CEConst.IDS_P_MAIN_DELAY:
                case CEConst.IDS_P_ONR_DELAY:
//                case CEConst.IDS_P_SYS_DELAY:
                case CEConst.IDS_P_VMTD:
                case CEConst.IDS_P_VMTV:
                case CEConst.IDS_P_VHT:
                case CEConst.IDS_P_VHD:
                case CEConst.IDS_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_P_TTI:
                case CEConst.IDS_P_MAX_DC:
                case CEConst.IDS_P_MAX_VC:
                case CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_P_TOTAL_ON_QUEUE_VEH:

                case CEConst.IDS_S_ACTUAL_TIME:
                case CEConst.IDS_S_VMTD:
                case CEConst.IDS_S_VMTV:
                case CEConst.IDS_S_VHT:
                case CEConst.IDS_S_VHD:
                case CEConst.IDS_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_S_REPORT_DENSITY_PC:
                case CEConst.IDS_S_MAX_DC:
                case CEConst.IDS_S_MAX_VC:

                case CEConst.IDS_SP_ACTUAL_TIME:
                case CEConst.IDS_SP_VMTD:
                case CEConst.IDS_SP_VMTV:
                case CEConst.IDS_SP_VHT:
                case CEConst.IDS_SP_VHD:
                case CEConst.IDS_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_SP_MAX_DC:
                case CEConst.IDS_SP_MAX_VC:

                case CEConst.IDS_SCEN_PROB:

                //Managed Lane Parameters
                case CEConst.IDS_ML_SEGMENT_LENGTH_MI:
                case CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE:
                case CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE:
                case CEConst.IDS_ML_USER_CAF:
                case CEConst.IDS_ML_USER_OAF:
                case CEConst.IDS_ML_USER_DAF:
                case CEConst.IDS_ML_USER_SAF:
                case CEConst.IDS_ML_SPEED:
                case CEConst.IDS_ML_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_TOTAL_DENSITY_VEH:
                case CEConst.IDS_ML_TOTAL_DENSITY_PC:
                case CEConst.IDS_ML_INFLUENCED_DENSITY_PC:
                case CEConst.IDS_ML_ADJUSTED_MAIN_DEMAND:
                case CEConst.IDS_ML_MAIN_CAPACITY:
                case CEConst.IDS_ML_MAIN_VOLUME_SERVED:
                case CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ML_ON_RAMP_CAPACITY:
                case CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_ML_OFF_RAMP_CAPACITY:
                case CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ML_DC:
                case CEConst.IDS_ML_VC:
                case CEConst.IDS_ML_QUEUE_LENGTH:
                case CEConst.IDS_ML_QUEUE_PERCENTAGE:
                case CEConst.IDS_ML_ON_QUEUE_VEH:
                case CEConst.IDS_ML_ACTUAL_TIME:
                case CEConst.IDS_ML_FFS_TIME:
                case CEConst.IDS_ML_MAINLINE_DELAY:

                case CEConst.IDS_ML_VMTD:
                case CEConst.IDS_ML_VMTV:
                case CEConst.IDS_ML_VHT:
                case CEConst.IDS_ML_VHD_M:
                case CEConst.IDS_ML_VHD_R:
                case CEConst.IDS_ML_VHD_ACCESS:
                case CEConst.IDS_ML_VHD:
                case CEConst.IDS_ML_TRAVEL_TIME_INDEX:
                case CEConst.IDS_ML_P_ACTUAL_TIME:
                case CEConst.IDS_ML_P_FFS_TIME:
                case CEConst.IDS_ML_P_MAIN_DELAY:
                case CEConst.IDS_ML_P_ONR_DELAY:

                case CEConst.IDS_ML_P_VMTD:
                case CEConst.IDS_ML_P_VMTV:
                case CEConst.IDS_ML_P_VHT:
                case CEConst.IDS_ML_P_VHD:
                case CEConst.IDS_ML_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_ML_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_ML_P_TTI:
                case CEConst.IDS_ML_P_MAX_DC:
                case CEConst.IDS_ML_P_MAX_VC:
                case CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT:
                case CEConst.IDS_ML_S_ACTUAL_TIME:
                case CEConst.IDS_ML_S_VMTD:
                case CEConst.IDS_ML_S_VMTV:
                case CEConst.IDS_ML_S_VHT:
                case CEConst.IDS_ML_S_VHD:
                case CEConst.IDS_ML_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_S_REPORT_DENSITY_PC:
                case CEConst.IDS_ML_S_MAX_DC:
                case CEConst.IDS_ML_S_MAX_VC:

                case CEConst.IDS_ML_SP_ACTUAL_TIME:
                case CEConst.IDS_ML_SP_VMTD:
                case CEConst.IDS_ML_SP_VMTV:
                case CEConst.IDS_ML_SP_VHT:
                case CEConst.IDS_ML_SP_VHD:
                case CEConst.IDS_ML_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_ML_SP_MAX_DC:
                case CEConst.IDS_ML_SP_MAX_VC:

                //Combined Parameters
                case CEConst.IDS_CB_P_ACTUAL_TIME:
                case CEConst.IDS_CB_P_FFS_TIME:
                case CEConst.IDS_CB_P_MAIN_DELAY:
                case CEConst.IDS_CB_P_ONR_DELAY:

                case CEConst.IDS_CB_P_VMTD:
                case CEConst.IDS_CB_P_VMTV:
                case CEConst.IDS_CB_P_VHT:
                case CEConst.IDS_CB_P_VHD:
                case CEConst.IDS_CB_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_CB_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_CB_P_TTI:
                case CEConst.IDS_CB_P_MAX_DC:
                case CEConst.IDS_CB_P_MAX_VC:
                case CEConst.IDS_CB_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_CB_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_CB_P_TOTAL_ON_QUEUE_LENGTH_FT:
                case CEConst.IDS_CB_S_ACTUAL_TIME:
                case CEConst.IDS_CB_S_VMTD:
                case CEConst.IDS_CB_S_VMTV:
                case CEConst.IDS_CB_S_VHT:
                case CEConst.IDS_CB_S_VHD:
                case CEConst.IDS_CB_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_S_REPORT_DENSITY_PC:
                case CEConst.IDS_CB_S_MAX_DC:
                case CEConst.IDS_CB_S_MAX_VC:

                case CEConst.IDS_CB_SP_ACTUAL_TIME:
                case CEConst.IDS_CB_SP_VMTD:
                case CEConst.IDS_CB_SP_VMTV:
                case CEConst.IDS_CB_SP_VHT:
                case CEConst.IDS_CB_SP_VHD:
                case CEConst.IDS_CB_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_CB_SP_MAX_DC:
                case CEConst.IDS_CB_SP_MAX_VC:

                //parameters added for passenger measurement
                case CEConst.IDS_OCCU_GP:
                case CEConst.IDS_OCCU_ML:
                case CEConst.IDS_PMTV:
                case CEConst.IDS_PMTD:
                case CEConst.IDS_ML_PMTV:
                case CEConst.IDS_ML_PMTD:
                case CEConst.IDS_P_PMTD:
                case CEConst.IDS_P_PMTV:
                case CEConst.IDS_ML_P_PMTD:
                case CEConst.IDS_ML_P_PMTV:
                case CEConst.IDS_CB_P_PMTD:
                case CEConst.IDS_CB_P_PMTV:
                case CEConst.IDS_S_PMTD:
                case CEConst.IDS_S_PMTV:
                case CEConst.IDS_ML_S_PMTD:
                case CEConst.IDS_ML_S_PMTV:
                case CEConst.IDS_CB_S_PMTD:
                case CEConst.IDS_CB_S_PMTV:
                case CEConst.IDS_SP_PMTD:
                case CEConst.IDS_SP_PMTV:
                case CEConst.IDS_ML_SP_PMTD:
                case CEConst.IDS_ML_SP_PMTV:
                case CEConst.IDS_CB_SP_PMTD:
                case CEConst.IDS_CB_SP_PMTV:
                    return (int) getValueFloat(ID, seg, period, scen, atdm);

                default:
                    System.out.println(ID + " not recognized in getValueInt");
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in getValueInt " + ID + " " + e.toString());
            return 0;
        }
    }

    /**
     * Get an integer value from a data field in this Seed object or in one of
     * the GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @return an integer value from a data field in this Seed object or in one
     * of the GPSegments
     */
    public int getValueInt(String ID, int seg, int period) {
        return getValueInt(ID, seg, period, 0, -1);
    }

    /**
     * Get an integer value from a data field in this Seed object or in one of
     * the GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @return an integer value from a data field in this Seed object or in one
     * of the GPSegments
     */
    public int getValueInt(String ID, int seg) {
        return getValueInt(ID, seg, 0, 0, -1);
    }

    /**
     * Get an integer value from a data field in this Seed object or in one of
     * the GPSegments
     *
     * @param ID identifier of the data field
     * @return an integer value from a data field in this Seed object or in one
     * of the GPSegments
     */
    public int getValueInt(String ID) {
        return getValueInt(ID, 0, 0, 0, -1);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Universal Getter For Float">
    /**
     * Get a float value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM scenario
     * @return a float value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public float getValueFloat(String ID, int seg, int period, int scen, int atdm) {
        if (ID.startsWith("IDS_ML") && !inManagedLaneUsed) {
            return 0;
        }

        try {

            int length;

            switch (ID) {
                case CEConst.IDS_SEGMENT_LENGTH_MI:
                    return getValueInt(CEConst.IDS_SEGMENT_LENGTH_FT, seg) / 5280f;
                case CEConst.IDS_UPSTREAM_LATITUDE:
                    return upstreamCoord != null ? upstreamCoord.latitude : 0.0f;
                case CEConst.IDS_UPSTREAM_LONGITUDE:
                    return upstreamCoord != null ? upstreamCoord.longitude : 0.0f;
                case CEConst.IDS_SEGMENT_LATITUDE:
                    return GPSegments.get(seg).coord != null ? GPSegments.get(seg).coord.latitude : 0.0f;
                case CEConst.IDS_SEGMENT_LONGITUDE:
                    return GPSegments.get(seg).coord != null ? GPSegments.get(seg).coord.longitude : 0.0f;
                case CEConst.IDS_TOTAL_LENGTH_MI:
                    length = 0;
                    for (GPMLSegment segment : GPSegments) {
                        length += segment.inSegLength_ft;
                    }
                    return length / 5280f;
                case CEConst.IDS_JAM_DENSITY:
                    return inJamDensity;
                case CEConst.IDS_TRUCK_CAR_ET:
                    return GPSegments.get(seg).inET;
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE:
                    return GPSegments.get(seg).inMainlineTruckSingle.get(period);
                case CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE:
                    return GPSegments.get(seg).inMainlineTruckTrailer.get(period);
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR:
                    return GPSegments.get(seg).inONRTruckSingle.get(period);
                case CEConst.IDS_TRUCK_TRAILER_PCT_ONR:
                    return GPSegments.get(seg).inONRTruckTrailer.get(period);
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR:
                    return GPSegments.get(seg).inOFRTruckSingle.get(period);
                case CEConst.IDS_TRUCK_TRAILER_PCT_OFR:
                    return GPSegments.get(seg).inOFRTruckTrailer.get(period);
                case CEConst.IDS_GP_USER_CAF:
                    return GPSegments.get(seg).inUCAF.get(period);
                case CEConst.IDS_GP_USER_OAF:
                    return GPSegments.get(seg).inUOAF.get(period);
                case CEConst.IDS_GP_USER_DAF:
                    return GPSegments.get(seg).inUDAF.get(period);
                case CEConst.IDS_GP_USER_SAF:
                    return GPSegments.get(seg).inUSAF.get(period);
                case CEConst.IDS_GP_USER_DPCAF:
                    return GPSegments.get(seg).inUDPCAF.get(period);
                case CEConst.IDS_GP_USER_DPSAF:
                    return GPSegments.get(seg).inUDPSAF.get(period);
                case CEConst.IDS_GP_RL_CAF:
                    return getRLCAF(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_RL_OAF:
                    return getRLOAF(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_RL_DAF:
                    return getRLDAF(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_RL_SAF:
                    return getRLSAF(scen, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_ATDM_CAF:
                    return getATDMCAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_ATDM_OAF:
                    return getATDMOAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_ATDM_DAF:
                    return getATDMDAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_GP_ATDM_SAF:
                    return getATDMSAF(scen, atdm, seg, period, CEConst.SEG_TYPE_GP);
                case CEConst.IDS_CROSS_WEAVE_CAF:
                    return GPSegments.get(seg).inCrossCAF[period];
                case CEConst.IDS_SPEED:
                case CEConst.IDS_SPACE_MEAN_SPEED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenSpeed[period];
                case CEConst.IDS_TOTAL_DENSITY_VEH:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenAllDensity_veh[period];
                case CEConst.IDS_TOTAL_DENSITY_PC:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenAllDensity_pc(period);
                case CEConst.IDS_INFLUENCED_DENSITY_PC:
                    checkInBuffer(scen, atdm);
//                    return Math.max(GPSegments.get(seg).scenIADensity_pc[period], GPSegments.get(seg).getScenAllDensity_pc(period));
                    return GPSegments.get(seg).scenIADensity_pc[period];
                case CEConst.IDS_OCCUPANCY_PCT:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenAllDensity_veh[period] / 2.04f;  //100*(L_D+L_V)/5280 = 2.04, L_D = 6 ft (Average length of sensor), L_V = 20 (Average length of vehicles)

                case CEConst.IDS_ADJUSTED_MAIN_DEMAND:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenMainlineDemand_veh[period];
                case CEConst.IDS_MAIN_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenMainlineCapacity_veh[period];
                case CEConst.IDS_MAIN_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenMainlineVolume_veh[period];

                case CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOnDemand_veh[period];
                case CEConst.IDS_ON_RAMP_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOnCapacity_veh[period];
                case CEConst.IDS_ON_RAMP_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOnVolume_veh[period];
                case CEConst.IDS_ON_RAMP_AVG_METERING_RATE:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenStepsRampMetered[period] > 0 ? GPSegments.get(seg).scenTotalRM[period] / GPSegments.get(seg).scenStepsRampMetered[period] : -1;
                case CEConst.IDS_ON_RAMP_TIME_METERED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenStepsRampMetered[period] * 0.25f; // numSteps * 15seconds / 60 seconds = # minutes
                case CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOffDemand_veh[period];
                case CEConst.IDS_OFF_RAMP_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOffCapacity_veh[period];
                case CEConst.IDS_OFF_RAMP_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenOffVolume_veh[period];

                case CEConst.IDS_DC:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenDC(period);
                case CEConst.IDS_VC:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVC[period];
                case CEConst.IDS_QUEUE_LENGTH:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).Q[period];
                case CEConst.IDS_QUEUE_PERCENTAGE:
                    checkInBuffer(scen, atdm);
                    return Math.min(GPSegments.get(seg).Q[period] / GPSegments.get(seg).inSegLength_ft, 100);
                case CEConst.IDS_ON_QUEUE_VEH:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenONRQ_End_veh[period];
                case CEConst.IDS_ACTUAL_TIME:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenActualTime(period);
                case CEConst.IDS_FFS_TIME:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenFFSTime(period);
                case CEConst.IDS_MAINLINE_DELAY:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenMainlineDelay(period);
                case CEConst.IDS_VHD_R:
                case CEConst.IDS_ML_VHD_ACCESS:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVHD_R[period];
                case CEConst.IDS_VMTD:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVMTD[period];
                case CEConst.IDS_VMTV:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVMTV[period];
                case CEConst.IDS_VHT:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVHT[period];
                case CEConst.IDS_VHD_M:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVHD_M[period];
                case CEConst.IDS_VHD_MDE:
                    checkInBuffer(scen, atdm);
                    return seg == 0 ? GPSegments.get(0).scenDenyQ[period] * 0.25f * 0.5f * 0.25f : 0; // Divide by 4 since in hourly demand volume, divide by 2 for average
                case CEConst.IDS_VHD:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).scenVHD[period];
                case CEConst.IDS_TRAVEL_TIME_INDEX:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).getScenTTI(period);

                case CEConst.IDS_P_ACTUAL_TIME:
                    return pOutActualTravelTime.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_FFS_TIME:
                    return pOutFreeFlowTravelTime.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_MAIN_DELAY:
                    return pOutMainlineDelay.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_ONR_DELAY:
                    return pOutOnRampDelay.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_VMTD:
                    return pOutVMTD.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_VMTV:
                    return pOutVMTV.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_VHT:
                    return pOutVHT.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_VHD:
                    return pOutVHD.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_SPACE_MEAN_SPEED:
                    return pOutSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TOTAL_DENSITY_VEH:
                    return pOutDensityTotal_veh.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TOTAL_DENSITY_PC:
                    return pOutDensityTotal_pc.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TTI:
                    return pOutTravelTimeIndex.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_MAX_DC:
                    return pOutMaxDC.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_MAX_VC:
                    return pOutMaxVC.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH:
                    return pOutDenyVeh.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                    return pOutMainlineQueueLengthFt.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_P_TOTAL_ON_QUEUE_VEH:
                    return pOutOnQueueVeh.get(new ScenATDM(scen, atdm).toString())[period];

                case CEConst.IDS_S_ACTUAL_TIME:
                    return sOutActualTravelTime.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_VMTD:
                    return sOutVMTD.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_VMTV:
                    return sOutVMTV.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_VHT:
                    return sOutVHT.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_VHD:
                    return sOutVHD.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_SPACE_MEAN_SPEED:
                    return sOutSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_REPORT_DENSITY_PC:
                    return sOutReportDensity_IA_pc.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_MAX_DC:
                    return sOutMaxDC.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_S_MAX_VC:
                    return sOutMaxVC.get(new ScenATDM(scen, atdm).toString())[seg];

                case CEConst.IDS_SP_ACTUAL_TIME:
                    return spOutActualTravelTime.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_VMTD:
                    return spOutVMTD.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_VMTV:
                    return spOutVMTV.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_VHT:
                    return spOutVHT.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_VHD:
                    return spOutVHD.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_SPACE_MEAN_SPEED:
                    return spOutSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_REPORT_DENSITY_PC:
                    return spOutReportDensity_IA_pc.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_MAX_DC:
                    return spOutMaxDC.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_SP_MAX_VC:
                    return spOutMaxVC.get(new ScenATDM(scen, atdm).toString());

                case CEConst.IDS_SCEN_PROB:
                    return RL_ScenarioInfo.get(scen).prob;

                //Managed Lane Parameters
                case CEConst.IDS_ML_SEGMENT_LENGTH_MI:
                    return getValueInt(CEConst.IDS_ML_SEGMENT_LENGTH_FT, seg) / 5280f;
                case CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE:
                    return MLSegments.get(seg).inMainlineTruckSingle.get(period);
                case CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE:
                    return MLSegments.get(seg).inMainlineTruckTrailer.get(period);
                case CEConst.IDS_ML_USER_CAF:
                    return MLSegments.get(seg).inUCAF.get(period);
                case CEConst.IDS_ML_USER_OAF:
                    return MLSegments.get(seg).inUOAF.get(period);
                case CEConst.IDS_ML_USER_DAF:
                    return MLSegments.get(seg).inUDAF.get(period);
                case CEConst.IDS_ML_USER_SAF:
                    return MLSegments.get(seg).inUSAF.get(period);
                case CEConst.IDS_ML_RL_CAF:
                    return getRLCAF(scen, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_RL_OAF:
                    return getRLOAF(scen, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_RL_DAF:
                    return getRLDAF(scen, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_RL_SAF:
                    return getRLSAF(scen, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_ATDM_CAF:
                    return getATDMCAF(scen, atdm, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_ATDM_OAF:
                    return getATDMOAF(scen, atdm, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_ATDM_DAF:
                    return getATDMDAF(scen, atdm, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_ATDM_SAF:
                    return getATDMSAF(scen, atdm, seg, period, CEConst.SEG_TYPE_ML);
                case CEConst.IDS_ML_SPEED:
                case CEConst.IDS_ML_SPACE_MEAN_SPEED:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenSpeed[period];
                case CEConst.IDS_ML_TOTAL_DENSITY_VEH:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenAllDensity_veh[period];
                case CEConst.IDS_ML_TOTAL_DENSITY_PC:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenAllDensity_pc(period);
                case CEConst.IDS_ML_INFLUENCED_DENSITY_PC:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenIADensity_pc[period];
                case CEConst.IDS_ML_OCCUPANCY_PCT:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenAllDensity_veh[period] * 2.04f;  //100*(L_D+L_V)/5280 = 2.04, L_D = 6 ft (Average length of sensor), L_V = 20 (Average length of vehicles)

                case CEConst.IDS_ML_ADJUSTED_MAIN_DEMAND:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenMainlineDemand_veh[period];
                case CEConst.IDS_ML_MAIN_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenMainlineCapacity_veh[period];
                case CEConst.IDS_ML_MAIN_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenMainlineVolume_veh[period];

                case CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOnDemand_veh[period];
                case CEConst.IDS_ML_ON_RAMP_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOnCapacity_veh[period];
                case CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOnVolume_veh[period];

                case CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOffDemand_veh[period];
                case CEConst.IDS_ML_OFF_RAMP_CAPACITY:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOffCapacity_veh[period];
                case CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenOffVolume_veh[period];

                case CEConst.IDS_ML_DC:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenDC(period);
                case CEConst.IDS_ML_VC:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVC[period];
                case CEConst.IDS_ML_QUEUE_LENGTH:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).Q[period];
                case CEConst.IDS_ML_QUEUE_PERCENTAGE:
                    checkInBuffer(scen, atdm);
                    return Math.min(MLSegments.get(seg).Q[period] / MLSegments.get(seg).inSegLength_ft, 100);
                case CEConst.IDS_ML_ON_QUEUE_VEH:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenONRQ_End_veh[period];
                case CEConst.IDS_ML_ACTUAL_TIME:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenActualTime(period);
                case CEConst.IDS_ML_FFS_TIME:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenFFSTime(period);
                case CEConst.IDS_ML_MAINLINE_DELAY:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenMainlineDelay(period);
                case CEConst.IDS_ML_VHD_R:
                case CEConst.IDS_VHD_ACCESS:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVHD_R[period];
                case CEConst.IDS_ML_VMTD:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVMTD[period];
                case CEConst.IDS_ML_VMTV:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVMTV[period];
                case CEConst.IDS_ML_VHT:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVHT[period];
                case CEConst.IDS_ML_VHD_M:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVHD_M[period];
                case CEConst.IDS_ML_VHD_MDE:
                    checkInBuffer(scen, atdm);
                    return seg == 0 ? MLSegments.get(0).scenDenyQ[period] * 0.25f : 0;
                case CEConst.IDS_ML_VHD:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).scenVHD[period];
                case CEConst.IDS_ML_TRAVEL_TIME_INDEX:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).getScenTTI(period);

                case CEConst.IDS_ML_P_ACTUAL_TIME:
                    return pOutMLActualTravelTime.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_FFS_TIME:
                    return pOutMLFreeFlowTravelTime.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_MAIN_DELAY:
                    return pOutMLMainlineDelay.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_ONR_DELAY:
                    return pOutMLOnRampDelay.get(new ScenATDM(scen, atdm).toString())[period];
//                case CEConst.IDS_ML_P_SYS_DELAY:
//                    return pOutMLSystemDelay.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_VMTD:
                    return pOutMLVMTD.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_VMTV:
                    return pOutMLVMTV.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_VHT:
                    return pOutMLVHT.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_VHD:
                    return pOutMLVHD.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_SPACE_MEAN_SPEED:
                    return pOutMLSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TOTAL_DENSITY_VEH:
                    return pOutMLDensityTotal_veh.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TOTAL_DENSITY_PC:
                    return pOutMLDensityTotal_pc.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TTI:
                    return pOutMLTravelTimeIndex.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_MAX_DC:
                    return pOutMLMaxDC.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_MAX_VC:
                    return pOutMLMaxVC.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_VEH:
                    return pOutMLDenyLengthFt.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                    return pOutMLMainlineQueueLengthFt.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT:
                    return pOutMLOnQueueLengthFt.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_S_ACTUAL_TIME:
                    return sOutMLActualTravelTime.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_VMTD:
                    return sOutMLVMTD.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_VMTV:
                    return sOutMLVMTV.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_VHT:
                    return sOutMLVHT.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_VHD:
                    return sOutMLVHD.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_SPACE_MEAN_SPEED:
                    return sOutMLSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_REPORT_DENSITY_PC:
                    return sOutMLReportDensity_IA_pc.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_MAX_DC:
                    return sOutMLMaxDC.get(new ScenATDM(scen, atdm).toString())[seg];
                case CEConst.IDS_ML_S_MAX_VC:
                    return sOutMLMaxVC.get(new ScenATDM(scen, atdm).toString())[seg];

                case CEConst.IDS_ML_SP_ACTUAL_TIME:
                    return spOutMLActualTravelTime.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_VMTD:
                    return spOutMLVMTD.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_VMTV:
                    return spOutMLVMTV.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_VHT:
                    return spOutMLVHT.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_VHD:
                    return spOutMLVHD.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_SPACE_MEAN_SPEED:
                    return spOutMLSpaceMeanSpeed.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_REPORT_DENSITY_PC:
                    return spOutMLReportDensity_IA_pc.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_MAX_DC:
                    return spOutMLMaxDC.get(new ScenATDM(scen, atdm).toString());
                case CEConst.IDS_ML_SP_MAX_VC:
                    return spOutMLMaxVC.get(new ScenATDM(scen, atdm).toString());

                //Combined Parameters
                case CEConst.IDS_CB_P_ACTUAL_TIME:
                    return (getValueFloat(CEConst.IDS_P_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_FFS_TIME:
                    return (getValueFloat(CEConst.IDS_P_FFS_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_FFS_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_MAIN_DELAY:
                    return (getValueFloat(CEConst.IDS_P_MAIN_DELAY, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_MAIN_DELAY, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_ONR_DELAY:
                    return (getValueFloat(CEConst.IDS_P_ONR_DELAY, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_ONR_DELAY, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_VMTD:
                    return (getValueFloat(CEConst.IDS_P_VMTD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTD, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_VMTV:
                    return (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_VHT:
                    return (getValueFloat(CEConst.IDS_P_VHT, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VHT, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_VHD:
                    return (getValueFloat(CEConst.IDS_P_VHD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VHD, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_SPACE_MEAN_SPEED:
                    return (getValueFloat(CEConst.IDS_P_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TOTAL_DENSITY_VEH:
                    return (getValueFloat(CEConst.IDS_P_TOTAL_DENSITY_VEH, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TOTAL_DENSITY_VEH, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TOTAL_DENSITY_PC:
                    return (getValueFloat(CEConst.IDS_P_TOTAL_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TOTAL_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TTI:
                    return (getValueFloat(CEConst.IDS_P_TTI, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TTI, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_MAX_DC:
                    return Math.max(getValueFloat(CEConst.IDS_P_MAX_DC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_P_MAX_DC, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_MAX_VC:
                    return Math.max(getValueFloat(CEConst.IDS_P_MAX_VC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_P_MAX_VC, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TOTAL_DENY_QUEUE_VEH:
                    return (getValueFloat(CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_VEH, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                    return (getValueFloat(CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_P_TOTAL_ON_QUEUE_LENGTH_FT:
                    return (getValueFloat(CEConst.IDS_P_TOTAL_ON_QUEUE_VEH, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm));

                case CEConst.IDS_CB_S_ACTUAL_TIME:
                    return (getValueFloat(CEConst.IDS_S_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_S_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_VMTD:
                    return (getValueFloat(CEConst.IDS_S_VMTD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VMTD, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_VMTV:
                    return (getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_VHT:
                    return (getValueFloat(CEConst.IDS_S_VHT, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VHT, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_VHD:
                    return (getValueFloat(CEConst.IDS_S_VHD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VHD, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_SPACE_MEAN_SPEED:
                    return (getValueFloat(CEConst.IDS_S_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_S_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_REPORT_DENSITY_PC:
                    return (getValueFloat(CEConst.IDS_S_REPORT_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_S_REPORT_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_MAX_DC:
                    return Math.max(getValueFloat(CEConst.IDS_S_MAX_DC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_S_MAX_DC, seg, period, scen, atdm));
                case CEConst.IDS_CB_S_MAX_VC:
                    return Math.max(getValueFloat(CEConst.IDS_S_MAX_VC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_S_MAX_VC, seg, period, scen, atdm));

                case CEConst.IDS_CB_SP_ACTUAL_TIME:
                    return (getValueFloat(CEConst.IDS_SP_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_SP_ACTUAL_TIME, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_VMTD:
                    return (getValueFloat(CEConst.IDS_SP_VMTD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VMTD, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_VMTV:
                    return (getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_VHT:
                    return (getValueFloat(CEConst.IDS_SP_VHT, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VHT, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_VHD:
                    return (getValueFloat(CEConst.IDS_SP_VHD, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VHD, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_SPACE_MEAN_SPEED:
                    return (getValueFloat(CEConst.IDS_SP_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_SP_SPACE_MEAN_SPEED, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_REPORT_DENSITY_PC:
                    return (getValueFloat(CEConst.IDS_SP_REPORT_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm)
                            + getValueFloat(CEConst.IDS_ML_SP_REPORT_DENSITY_PC, seg, period, scen, atdm) * getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm))
                            / (getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) + getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_MAX_DC:
                    return Math.max(getValueFloat(CEConst.IDS_SP_MAX_DC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_SP_MAX_DC, seg, period, scen, atdm));
                case CEConst.IDS_CB_SP_MAX_VC:
                    return Math.max(getValueFloat(CEConst.IDS_SP_MAX_VC, seg, period, scen, atdm), getValueFloat(CEConst.IDS_ML_SP_MAX_VC, seg, period, scen, atdm));

                //parameters added for passenger measurement
                case CEConst.IDS_OCCU_GP:
                    return inGPOccupancy;
                case CEConst.IDS_OCCU_ML:
                    return inMLOccupancy;

                case CEConst.IDS_PMTV:
                    return getValueFloat(CEConst.IDS_VMTV, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_PMTV:
                    return getValueFloat(CEConst.IDS_ML_VMTV, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_P_PMTV:
                    return getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_P_PMTV:
                    return getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_P_PMTV:
                    return getValueFloat(CEConst.IDS_P_VMTV, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_P_VMTV, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_S_PMTV:
                    return getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_S_PMTV:
                    return getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_S_PMTV:
                    return getValueFloat(CEConst.IDS_S_VMTV, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_S_VMTV, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_SP_PMTV:
                    return getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_SP_PMTV:
                    return getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_SP_PMTV:
                    return getValueFloat(CEConst.IDS_SP_VMTV, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_SP_VMTV, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_PMTD:
                    return getValueFloat(CEConst.IDS_VMTD, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_PMTD:
                    return getValueFloat(CEConst.IDS_ML_VMTD, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_P_PMTD:
                    return getValueFloat(CEConst.IDS_P_VMTD, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_P_PMTD:
                    return getValueFloat(CEConst.IDS_ML_P_VMTD, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_P_PMTD:
                    return getValueFloat(CEConst.IDS_P_VMTD, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_P_VMTD, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_S_PMTD:
                    return getValueFloat(CEConst.IDS_S_VMTD, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_S_PMTD:
                    return getValueFloat(CEConst.IDS_ML_S_VMTD, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_S_PMTD:
                    return getValueFloat(CEConst.IDS_S_VMTD, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_S_VMTD, seg, period, scen, atdm) * inMLOccupancy;

                case CEConst.IDS_SP_PMTD:
                    return getValueFloat(CEConst.IDS_SP_VMTD, seg, period, scen, atdm) * inGPOccupancy;
                case CEConst.IDS_ML_SP_PMTD:
                    return getValueFloat(CEConst.IDS_ML_SP_VMTD, seg, period, scen, atdm) * inMLOccupancy;
                case CEConst.IDS_CB_SP_PMTD:
                    return getValueFloat(CEConst.IDS_SP_VMTD, seg, period, scen, atdm) * inGPOccupancy + getValueFloat(CEConst.IDS_ML_SP_VMTD, seg, period, scen, atdm) * inMLOccupancy;

                //Integer
                case CEConst.IDS_NUM_PERIOD:
                case CEConst.IDS_NUM_SCEN:
                case CEConst.IDS_ATDM_NUM_EACH_RL_SCEN:
                case CEConst.IDS_ATDM_SCEN_IN_EACH_SET:
                case CEConst.IDS_ATDM_SET_NUM:
                case CEConst.IDS_NUM_SEGMENT:
                case CEConst.IDS_SEGMENT_TYPE:
                case CEConst.IDS_SEGMENT_LENGTH_FT:
                case CEConst.IDS_LANE_WIDTH:
                case CEConst.IDS_LATERAL_CLEARANCE:
                case CEConst.IDS_TERRAIN:
                case CEConst.IDS_MAIN_NUM_LANES_IN:
                case CEConst.IDS_MAIN_DEMAND_VEH:
                case CEConst.IDS_MAIN_FREE_FLOW_SPEED:
                case CEConst.IDS_ON_RAMP_SIDE:
                case CEConst.IDS_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL:
                case CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ON_RAMP_METERING_RATE_FIX:
                case CEConst.IDS_OFF_RAMP_SIDE:
                case CEConst.IDS_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_LENGTH_OF_WEAVING:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_NUM_LANES_WEAVING:
                case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                case CEConst.IDS_TYPE_USED:
                case CEConst.IDS_SCENARIO_STATUS:
                case CEConst.IDS_GP_RL_LAFI:
                case CEConst.IDS_GP_RL_LAFWZ:
                case CEConst.IDS_GP_ATDM_LAF:
                case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:

                //maganged lane parameters
                case CEConst.IDS_ML_SEGMENT_TYPE:
                case CEConst.IDS_ML_SEPARATION_TYPE:
                case CEConst.IDS_ML_SEGMENT_LENGTH_FT:
                case CEConst.IDS_ML_NUM_LANES:
                case CEConst.IDS_ML_DEMAND_VEH:
                case CEConst.IDS_ML_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                case CEConst.IDS_ML_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_ML_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                case CEConst.IDS_ML_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_LENGTH_SHORT:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_ML_NUM_LANES_WEAVING:
                case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                case CEConst.IDS_CROSS_WEAVE_VOLUME:
                case CEConst.IDS_ML_TYPE_USED:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ML:
                case CEConst.IDS_ML_MAX_LANE_CHANGE_ML:
                    return (float) getValueInt(ID, seg, period, scen, atdm);

                default:
                    System.out.println(ID + " not recognized in getValueFloat");
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in getValueFloat " + ID + " " + e.toString());
            return 0;
        }
    }

    /**
     * Get a float value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @return a float value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public float getValueFloat(String ID, int seg, int period) {
        return getValueFloat(ID, seg, period, 0, -1);
    }

    /**
     * Get a float value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @return a float value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public float getValueFloat(String ID, int seg) {
        return getValueFloat(ID, seg, 0, 0, -1);
    }

    /**
     * Get a float value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @return a float value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public float getValueFloat(String ID) {
        return getValueFloat(ID, 0, 0, 0, -1);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Universal Getter For String">
    /**
     * Get a String value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @param scen scenario index (0 is the default scenario, 1 is the first
     * generated scenario)
     * @param atdm ATDM set index
     * @return a String value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public String getValueString(String ID, int seg, int period, int scen, int atdm) {

        try {
            //return N/A if ML is not used
            if (!inManagedLaneUsed && ID.startsWith("IDS_ML")) {
                return CEConst.IDS_NA;
            }

            //Return N/A if inapplicable
            switch (ID) {
                //parameters only related to ONR and W GPSegments
                case CEConst.IDS_ON_RAMP_SIDE:
                case CEConst.IDS_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL:
                case CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_ONR:
                case CEConst.IDS_RAMP_METERING_TYPE:
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:
                case CEConst.IDS_ON_RAMP_CAPACITY:
                case CEConst.IDS_VHD_R:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_ON_QUEUE_VEH:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    } else {
                        checkInBuffer(scen, atdm);
                        if (GPSegments.get(seg).scenONRQ_Max_veh[period] > GPSegments.get(seg).inOnQueueCapacity) {
                            DecimalFormat formatter = new DecimalFormat("#,##0");
                            return formatter.format(GPSegments.get(seg).scenONRQ_End_veh[period])
                                    + "("
                                    + formatter.format(GPSegments.get(seg).scenONRQ_Max_veh[period])
                                    + ")";
                        }
                    }
                    break;

                case CEConst.IDS_ON_RAMP_METERING_RATE_FIX:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W
                            || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period) != CEConst.IDS_RAMP_METERING_TYPE_FIX) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_ON_RAMP_METERING_RATE_ALINEA_KEY:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W
                            || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period) != CEConst.IDS_RAMP_METERING_TYPE_ALINEA) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_ON_RAMP_METERING_RATE_FUZZY_KEY:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W
                            || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period) != CEConst.IDS_RAMP_METERING_TYPE_FUZZY) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W
                            || getRampMetering(scen, atdm, CEConst.SEG_TYPE_GP).getRampMeteringType().get(seg, period) != CEConst.IDS_RAMP_METERING_TYPE_FIX) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_ON_RAMP_AVG_METERING_RATE:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    } else {
                        if (getValueFloat(ID, seg, period, scen, atdm) >= 0.0f) {
                            return Float.toString(getValueFloat(ID, seg, period, scen, atdm));
                        } else {
                            return "N/A";
                        }
                    }

                case CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ON_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ON_RAMP_TIME_METERED:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W && GPSegments.get(seg).inType != CEConst.SEG_TYPE_ACS) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to ONR and OFR GPSegments
                case CEConst.IDS_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_INFLUENCED_DENSITY_PC:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_OFR) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to OFR and W GPSegments
                case CEConst.IDS_OFF_RAMP_SIDE:
                case CEConst.IDS_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_OFF_RAMP_CAPACITY:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_OFR:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_OFR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;
                case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_OFF_RAMP_VOLUME_SERVED:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_OFR && GPSegments.get(seg).inType != CEConst.SEG_TYPE_W && GPSegments.get(seg).inType != CEConst.SEG_TYPE_ACS) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to W GPSegments
                case CEConst.IDS_LENGTH_OF_WEAVING:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_NUM_LANES_WEAVING:
                case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_VHD_ACCESS:
                case CEConst.IDS_ML_VHD_ACCESS:
                    if (GPSegments.get(seg).inType != CEConst.SEG_TYPE_ACS) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to the first mainline segment
                //case CEConst.IDS_ML_DEMAND_VEH:
                case CEConst.IDS_VHD_MDE:
                case CEConst.IDS_ML_VHD_MDE:
                    if (seg > 0) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_MAIN_DEMAND_VEH:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE:
                case CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE:
                case CEConst.IDS_ML_DEMAND_VEH:
                    if (seg > 0) {
                        return CEConst.IDS_NA_SPECIAL;
                    }
                    break;

                //parameters only related to ONR and W GPSegments
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                case CEConst.IDS_ML_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_ON_RAMP_CAPACITY:
                case CEConst.IDS_ML_VHD_R:
                case CEConst.IDS_ML_ON_QUEUE_VEH:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && MLSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;
                case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && MLSegments.get(seg).inType != CEConst.SEG_TYPE_W && MLSegments.get(seg).inType != CEConst.SEG_TYPE_ACS) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to ONR and OFR GPSegments
                case CEConst.IDS_ML_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_ML_INFLUENCED_DENSITY_PC:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_ONR && MLSegments.get(seg).inType != CEConst.SEG_TYPE_OFR) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to OFR and W GPSegments
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                case CEConst.IDS_ML_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_OFF_RAMP_CAPACITY:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_OFR && MLSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;
                case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_OFR && MLSegments.get(seg).inType != CEConst.SEG_TYPE_W && MLSegments.get(seg).inType != CEConst.SEG_TYPE_ACS) {
                        return CEConst.IDS_NA;
                    }
                    break;

                //parameters only related to W GPSegments
                case CEConst.IDS_ML_LENGTH_SHORT:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_ML_NUM_LANES_WEAVING:
                case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                    if (MLSegments.get(seg).inType != CEConst.SEG_TYPE_W) {
                        return CEConst.IDS_NA;
                    }
                    break;

                case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                case CEConst.IDS_CROSS_WEAVE_VOLUME:
                case CEConst.IDS_CROSS_WEAVE_CAF:
                    if (!MLSegments.get(seg).inGPHasCrossWeave) {
                        return CEConst.IDS_NA;
                    }
                    break;
            }

            switch (ID) {
                //String
                case CEConst.IDS_SEED_FILE_NAME:
                    return inFileName;
                case CEConst.IDS_PROJECT_NAME:
                    return inProjectName;
                case CEConst.IDS_UPSTREAM_COORDINATE:
                    return upstreamCoord != null ? upstreamCoord.toString() : CEConst.IDS_NA;
                case CEConst.IDS_SEGMENT_COORDINATE:
                    return GPSegments.get(seg).coord != null ? GPSegments.get(seg).coord.toString() : CEConst.IDS_NA;
                case CEConst.IDS_DENSITY_BASED_LOS:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).funcDensityLOS(period);
                case CEConst.IDS_DEMAND_BASED_LOS:
                    checkInBuffer(scen, atdm);
                    return GPSegments.get(seg).funcDemandLOS(period);
                case CEConst.IDS_ML_DENSITY_BASED_LOS:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).funcDensityLOS(period);
                case CEConst.IDS_ML_DEMAND_BASED_LOS:
                    checkInBuffer(scen, atdm);
                    return MLSegments.get(seg).funcDemandLOS(period);
                case CEConst.IDS_START_TIME:
                    return inStartTime.toString();
                case CEConst.IDS_END_TIME:
                    return inEndTime.toString();
                case CEConst.IDS_SEED_DEMAND_DATE:
                    return RL_SeedFileDate.toString();
                case CEConst.IDS_HAS_CROSS_WEAVE:
                    return Boolean.toString(MLSegments.get(seg).inGPHasCrossWeave);

                case CEConst.IDS_ON_RAMP_METERING_RATE_ALINEA_KEY:
                    return getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringALINEAKey().get(seg, period);
                case CEConst.IDS_ON_RAMP_METERING_RATE_FUZZY_KEY:
                    return getRampMetering(scen, -1, CEConst.SEG_TYPE_GP).getRampMeteringFuzzyKey().get(seg, period);

                case CEConst.IDS_ANALYSIS_PERIOD_HEADING:
                    return "#" + (period + 1)
                            + (period >= 9 ? " " : "  ") + getValueString(CEConst.IDS_PERIOD_TIME, 0, period);
                case CEConst.IDS_SCEN_NAME:
                    return RL_ScenarioInfo.get(scen).name;
                case CEConst.IDS_SCEN_DETAIL:
                    return RL_ScenarioInfo.get(scen).getDetail();
                case CEConst.IDS_ATDM_NAME:
                    return ATDMSets.get(atdm).get(scen)[0].getName();
                case CEConst.IDS_ATDM_DETAIL:
                    return ATDMSets.get(atdm).get(scen)[0].getDiscription();
                case CEConst.IDS_PERIOD_TIME:
                    return CETime.addTime(inStartTime, LENGTH_OF_EACH_PERIOD, period).toString() + " - "
                            + CETime.addTime(inStartTime, LENGTH_OF_EACH_PERIOD, period + 1).toString();
                case CEConst.IDS_P_REPORT_LOS:
                    return pOutReportLOS.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_ML_P_REPORT_LOS:
                    return pOutMLReportLOS.get(new ScenATDM(scen, atdm).toString())[period];
                case CEConst.IDS_CB_P_REPORT_LOS:
                    return CEHelper.worseLOS(getValueString(CEConst.IDS_P_REPORT_LOS, seg, period, scen, atdm), getValueString(CEConst.IDS_ML_P_REPORT_LOS, seg, period, scen, atdm));
                case CEConst.IDS_SEGMENT_NAME:
                    return GPSegments.get(seg).segmentName;

                //Integer
                case CEConst.IDS_SEED_URBAN_RURAL_TYPE:
                case CEConst.IDS_NUM_PERIOD:
                case CEConst.IDS_NUM_SCEN:
                case CEConst.IDS_ATDM_NUM_EACH_RL_SCEN:
                case CEConst.IDS_ATDM_SCEN_IN_EACH_SET:
                case CEConst.IDS_ATDM_SET_NUM:
                case CEConst.IDS_NUM_SEGMENT:
                case CEConst.IDS_SEGMENT_TYPE:
                case CEConst.IDS_SEGMENT_LENGTH_FT:
                case CEConst.IDS_LANE_WIDTH:
                case CEConst.IDS_LATERAL_CLEARANCE:
                case CEConst.IDS_TERRAIN:
                case CEConst.IDS_MAIN_NUM_LANES_IN:
                case CEConst.IDS_MAIN_DEMAND_VEH:
                case CEConst.IDS_MAIN_FREE_FLOW_SPEED:
                case CEConst.IDS_ON_RAMP_SIDE:
                case CEConst.IDS_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ON_RAMP_QUEUE_CAPACITY_VPL:
                case CEConst.IDS_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ON_RAMP_METERING_RATE_FIX:
                case CEConst.IDS_OFF_RAMP_SIDE:
                case CEConst.IDS_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_LENGTH_OF_WEAVING:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_NUM_LANES_WEAVING:
                case CEConst.IDS_RAMP_TO_RAMP_DEMAND_VEH:
                case CEConst.IDS_CROSS_WEAVE_LC_MIN:
                case CEConst.IDS_CROSS_WEAVE_VOLUME:
                case CEConst.IDS_RAMP_METERING_TYPE:
                case CEConst.IDS_ATDM_RAMP_METERING_TYPE:

                case CEConst.IDS_TYPE_USED:
                case CEConst.IDS_SCENARIO_STATUS:
                case CEConst.IDS_GP_RL_LAFI:
                case CEConst.IDS_GP_RL_LAFWZ:
                case CEConst.IDS_GP_ATDM_LAF:
                case CEConst.IDS_ML_ATDM_LAF:
                case CEConst.IDS_ATDM_RAMP_METERING_RATE_FIX:
                case CEConst.IDS_CAPACITY_ALPHA:

                case CEConst.IDS_ML_SEGMENT_TYPE:
                case CEConst.IDS_ML_SEPARATION_TYPE:
                case CEConst.IDS_ML_SEGMENT_LENGTH_FT:
                case CEConst.IDS_ML_NUM_LANES:
                case CEConst.IDS_ML_DEMAND_VEH:
                case CEConst.IDS_ML_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_ON_RAMP_SIDE:
                case CEConst.IDS_ML_ACC_DEC_LANE_LENGTH:
                case CEConst.IDS_ML_NUM_ON_RAMP_LANES:
                case CEConst.IDS_ML_ON_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_ON_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_OFF_RAMP_SIDE:
                case CEConst.IDS_ML_NUM_OFF_RAMP_LANES:
                case CEConst.IDS_ML_OFF_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_OFF_RAMP_FREE_FLOW_SPEED:
                case CEConst.IDS_ML_LENGTH_SHORT:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_FRWY:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_FRWY_TO_OFR:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ONR_TO_OFR:
                case CEConst.IDS_ML_NUM_LANES_WEAVING:
                case CEConst.IDS_ML_RAMP_TO_RAMP_DEMAND_VEH:
                case CEConst.IDS_ML_TYPE_USED:
                case CEConst.IDS_ML_RL_LAF:
                case CEConst.IDS_ML_MIN_LANE_CHANGE_ML:
                case CEConst.IDS_ML_MAX_LANE_CHANGE_ML:
                    return Integer.toString(getValueInt(ID, seg, period, scen, atdm));

                //Float
                case CEConst.IDS_SEGMENT_LENGTH_MI:
                case CEConst.IDS_TOTAL_LENGTH_MI:
                case CEConst.IDS_UPSTREAM_LATITUDE:
                case CEConst.IDS_UPSTREAM_LONGITUDE:
                case CEConst.IDS_SEGMENT_LATITUDE:
                case CEConst.IDS_SEGMENT_LONGITUDE:
                case CEConst.IDS_JAM_DENSITY:
                case CEConst.IDS_TRUCK_CAR_ET:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE:
                case CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_ONR:
                case CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR:
                case CEConst.IDS_TRUCK_TRAILER_PCT_OFR:
                case CEConst.IDS_GP_USER_CAF:
                case CEConst.IDS_GP_USER_OAF:
                case CEConst.IDS_GP_USER_DAF:
                case CEConst.IDS_GP_USER_SAF:
                case CEConst.IDS_GP_USER_DPCAF:
                case CEConst.IDS_GP_USER_DPSAF:
                case CEConst.IDS_GP_RL_CAF:
                case CEConst.IDS_GP_RL_OAF:
                case CEConst.IDS_GP_RL_DAF:
                case CEConst.IDS_GP_RL_SAF:
                case CEConst.IDS_GP_ATDM_CAF:
                case CEConst.IDS_GP_ATDM_OAF:
                case CEConst.IDS_GP_ATDM_DAF:
                case CEConst.IDS_GP_ATDM_SAF:
                case CEConst.IDS_CROSS_WEAVE_CAF:
                case CEConst.IDS_SPEED:
                case CEConst.IDS_TOTAL_DENSITY_VEH:
                case CEConst.IDS_TOTAL_DENSITY_PC:
                case CEConst.IDS_INFLUENCED_DENSITY_PC:
                case CEConst.IDS_OCCUPANCY_PCT:
                case CEConst.IDS_ADJUSTED_MAIN_DEMAND:
                case CEConst.IDS_DC:
                case CEConst.IDS_MAIN_CAPACITY:
                case CEConst.IDS_MAIN_VOLUME_SERVED:
                case CEConst.IDS_VC:
                case CEConst.IDS_QUEUE_LENGTH:
                case CEConst.IDS_QUEUE_PERCENTAGE:
                case CEConst.IDS_ON_QUEUE_VEH:
                case CEConst.IDS_ACTUAL_TIME:
                case CEConst.IDS_FFS_TIME:
                case CEConst.IDS_MAINLINE_DELAY:
                case CEConst.IDS_VMTD:
                case CEConst.IDS_VMTV:
                case CEConst.IDS_VHT:
                case CEConst.IDS_VHD_M:
                case CEConst.IDS_VHD_R:
                case CEConst.IDS_VHD_ACCESS:
                case CEConst.IDS_VHD_MDE:
                case CEConst.IDS_VHD:
                case CEConst.IDS_SPACE_MEAN_SPEED:
                case CEConst.IDS_TRAVEL_TIME_INDEX:
                case CEConst.IDS_ON_RAMP_CAPACITY:
                case CEConst.IDS_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ON_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ON_RAMP_AVG_METERING_RATE:
                case CEConst.IDS_ON_RAMP_TIME_METERED:
                case CEConst.IDS_OFF_RAMP_CAPACITY:
                case CEConst.IDS_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_OFF_RAMP_VOLUME_SERVED:

                case CEConst.IDS_P_ACTUAL_TIME:
                case CEConst.IDS_P_FFS_TIME:
                case CEConst.IDS_P_MAIN_DELAY:
                case CEConst.IDS_P_ONR_DELAY:
                case CEConst.IDS_P_VMTD:
                case CEConst.IDS_P_VMTV:
                case CEConst.IDS_P_VHT:
                case CEConst.IDS_P_VHD:
                case CEConst.IDS_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_P_TTI:
                case CEConst.IDS_P_MAX_DC:
                case CEConst.IDS_P_MAX_VC:
                case CEConst.IDS_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_P_TOTAL_ON_QUEUE_VEH:

                case CEConst.IDS_S_ACTUAL_TIME:
                case CEConst.IDS_S_VMTD:
                case CEConst.IDS_S_VMTV:
                case CEConst.IDS_S_VHT:
                case CEConst.IDS_S_VHD:
                case CEConst.IDS_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_S_REPORT_DENSITY_PC:
                case CEConst.IDS_S_MAX_DC:
                case CEConst.IDS_S_MAX_VC:

                case CEConst.IDS_SP_ACTUAL_TIME:
                case CEConst.IDS_SP_VMTD:
                case CEConst.IDS_SP_VMTV:
                case CEConst.IDS_SP_VHT:
                case CEConst.IDS_SP_VHD:
                case CEConst.IDS_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_SP_MAX_DC:
                case CEConst.IDS_SP_MAX_VC:

                case CEConst.IDS_SCEN_PROB:

                //Managed Lane Parameters
                case CEConst.IDS_ML_SEGMENT_LENGTH_MI:
                case CEConst.IDS_ML_TRUCK_SINGLE_PERCENTAGE:
                case CEConst.IDS_ML_TRUCK_TRAILER_PERCENTAGE:
                case CEConst.IDS_ML_USER_CAF:
                case CEConst.IDS_ML_USER_OAF:
                case CEConst.IDS_ML_USER_DAF:
                case CEConst.IDS_ML_USER_SAF:
                case CEConst.IDS_ML_RL_CAF:
                case CEConst.IDS_ML_RL_OAF:
                case CEConst.IDS_ML_RL_DAF:
                case CEConst.IDS_ML_RL_SAF:
                case CEConst.IDS_ML_ATDM_CAF:
                case CEConst.IDS_ML_ATDM_OAF:
                case CEConst.IDS_ML_ATDM_DAF:
                case CEConst.IDS_ML_ATDM_SAF:
                case CEConst.IDS_ML_SPEED:
                case CEConst.IDS_ML_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_TOTAL_DENSITY_VEH:
                case CEConst.IDS_ML_TOTAL_DENSITY_PC:
                case CEConst.IDS_ML_INFLUENCED_DENSITY_PC:
                case CEConst.IDS_ML_OCCUPANCY_PCT:
                case CEConst.IDS_ML_ADJUSTED_MAIN_DEMAND:
                case CEConst.IDS_ML_MAIN_CAPACITY:
                case CEConst.IDS_ML_MAIN_VOLUME_SERVED:
                case CEConst.IDS_ML_ADJUSTED_ON_RAMP_DEMAND:
                case CEConst.IDS_ML_ON_RAMP_CAPACITY:
                case CEConst.IDS_ML_ON_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ML_ADJUSTED_OFF_RAMP_DEMAND:
                case CEConst.IDS_ML_OFF_RAMP_CAPACITY:
                case CEConst.IDS_ML_OFF_RAMP_VOLUME_SERVED:
                case CEConst.IDS_ML_DC:
                case CEConst.IDS_ML_VC:
                case CEConst.IDS_ML_QUEUE_LENGTH:
                case CEConst.IDS_ML_QUEUE_PERCENTAGE:
                case CEConst.IDS_ML_ON_QUEUE_VEH:
                case CEConst.IDS_ML_ACTUAL_TIME:
                case CEConst.IDS_ML_FFS_TIME:
                case CEConst.IDS_ML_MAINLINE_DELAY:

                case CEConst.IDS_ML_VMTD:
                case CEConst.IDS_ML_VMTV:
                case CEConst.IDS_ML_VHT:
                case CEConst.IDS_ML_VHD_M:
                case CEConst.IDS_ML_VHD_R:
                case CEConst.IDS_ML_VHD_ACCESS:
                case CEConst.IDS_ML_VHD_MDE:
                case CEConst.IDS_ML_VHD:
                case CEConst.IDS_ML_TRAVEL_TIME_INDEX:
                case CEConst.IDS_ML_P_ACTUAL_TIME:
                case CEConst.IDS_ML_P_FFS_TIME:
                case CEConst.IDS_ML_P_MAIN_DELAY:
                case CEConst.IDS_ML_P_ONR_DELAY:
                case CEConst.IDS_ML_P_VMTD:
                case CEConst.IDS_ML_P_VMTV:
                case CEConst.IDS_ML_P_VHT:
                case CEConst.IDS_ML_P_VHD:
                case CEConst.IDS_ML_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_ML_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_ML_P_TTI:
                case CEConst.IDS_ML_P_MAX_DC:
                case CEConst.IDS_ML_P_MAX_VC:
                case CEConst.IDS_ML_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_ML_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_ML_P_TOTAL_ON_QUEUE_LENGTH_FT:
                case CEConst.IDS_ML_S_ACTUAL_TIME:
                case CEConst.IDS_ML_S_VMTD:
                case CEConst.IDS_ML_S_VMTV:
                case CEConst.IDS_ML_S_VHT:
                case CEConst.IDS_ML_S_VHD:
                case CEConst.IDS_ML_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_S_REPORT_DENSITY_PC:
                case CEConst.IDS_ML_S_MAX_DC:
                case CEConst.IDS_ML_S_MAX_VC:

                case CEConst.IDS_ML_SP_ACTUAL_TIME:
                case CEConst.IDS_ML_SP_VMTD:
                case CEConst.IDS_ML_SP_VMTV:
                case CEConst.IDS_ML_SP_VHT:
                case CEConst.IDS_ML_SP_VHD:
                case CEConst.IDS_ML_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_ML_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_ML_SP_MAX_DC:
                case CEConst.IDS_ML_SP_MAX_VC:

                //Combined Parameters
                case CEConst.IDS_CB_P_ACTUAL_TIME:
                case CEConst.IDS_CB_P_FFS_TIME:
                case CEConst.IDS_CB_P_MAIN_DELAY:
                case CEConst.IDS_CB_P_ONR_DELAY:
                case CEConst.IDS_CB_P_VMTD:
                case CEConst.IDS_CB_P_VMTV:
                case CEConst.IDS_CB_P_VHT:
                case CEConst.IDS_CB_P_VHD:
                case CEConst.IDS_CB_P_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_P_TOTAL_DENSITY_VEH:
                case CEConst.IDS_CB_P_TOTAL_DENSITY_PC:
                case CEConst.IDS_CB_P_TTI:
                case CEConst.IDS_CB_P_MAX_DC:
                case CEConst.IDS_CB_P_MAX_VC:
                case CEConst.IDS_CB_P_TOTAL_DENY_QUEUE_VEH:
                case CEConst.IDS_CB_P_TOTAL_MAIN_QUEUE_LENGTH_FT:
                case CEConst.IDS_CB_P_TOTAL_ON_QUEUE_LENGTH_FT:
                case CEConst.IDS_CB_S_ACTUAL_TIME:
                case CEConst.IDS_CB_S_VMTD:
                case CEConst.IDS_CB_S_VMTV:
                case CEConst.IDS_CB_S_VHT:
                case CEConst.IDS_CB_S_VHD:
                case CEConst.IDS_CB_S_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_S_REPORT_DENSITY_PC:
                case CEConst.IDS_CB_S_MAX_DC:
                case CEConst.IDS_CB_S_MAX_VC:

                case CEConst.IDS_CB_SP_ACTUAL_TIME:
                case CEConst.IDS_CB_SP_VMTD:
                case CEConst.IDS_CB_SP_VMTV:
                case CEConst.IDS_CB_SP_VHT:
                case CEConst.IDS_CB_SP_VHD:
                case CEConst.IDS_CB_SP_SPACE_MEAN_SPEED:
                case CEConst.IDS_CB_SP_REPORT_DENSITY_PC:
                case CEConst.IDS_CB_SP_MAX_DC:
                case CEConst.IDS_CB_SP_MAX_VC:

                //parameters added for passenger measurement
                case CEConst.IDS_OCCU_GP:
                case CEConst.IDS_OCCU_ML:
                case CEConst.IDS_PMTV:
                case CEConst.IDS_PMTD:
                case CEConst.IDS_ML_PMTV:
                case CEConst.IDS_ML_PMTD:
                case CEConst.IDS_P_PMTD:
                case CEConst.IDS_P_PMTV:
                case CEConst.IDS_ML_P_PMTD:
                case CEConst.IDS_ML_P_PMTV:
                case CEConst.IDS_CB_P_PMTD:
                case CEConst.IDS_CB_P_PMTV:
                case CEConst.IDS_S_PMTD:
                case CEConst.IDS_S_PMTV:
                case CEConst.IDS_ML_S_PMTD:
                case CEConst.IDS_ML_S_PMTV:
                case CEConst.IDS_CB_S_PMTD:
                case CEConst.IDS_CB_S_PMTV:
                case CEConst.IDS_SP_PMTD:
                case CEConst.IDS_SP_PMTV:
                case CEConst.IDS_ML_SP_PMTD:
                case CEConst.IDS_ML_SP_PMTV:
                case CEConst.IDS_CB_SP_PMTD:
                case CEConst.IDS_CB_SP_PMTV:
                    return Float.toString(getValueFloat(ID, seg, period, scen, atdm));

                //Boolean
                case CEConst.IDS_FFS_KNOWN:
                    return Boolean.toString(inFreeFlowSpeedKnown);
                case CEConst.IDS_MANAGED_LANE_USED:
                    return Boolean.toString(inManagedLaneUsed);

                //Others
                case CEConst.IDS_DASH:
                    return CEConst.IDS_DASH;

                default:
                    System.out.println(ID + " not recognized in getValueString");
                    return CEConst.IDS_NA;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in getValueString " + ID + " " + e.toString());
            return CEConst.IDS_NA;
        }
    }

    /**
     * Get a String value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @param period analysis period index (0 is the first analysis period)
     * @return a String value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public String getValueString(String ID, int seg, int period) {
        return getValueString(ID, seg, period, 0, -1);
    }

    /**
     * Get a String value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @param seg segment index (0 is the first segment)
     * @return a String value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public String getValueString(String ID, int seg) {
        return getValueString(ID, seg, 0, 0, -1);
    }

    /**
     * Get a String value from a data field in this Seed object or in one of the
     * GPSegments
     *
     * @param ID identifier of the data field
     * @return a String value from a data field in this Seed object or in one of
     * the GPSegments
     */
    public String getValueString(String ID) {
        return getValueString(ID, 0, 0, 0, -1);
    }
    // </editor-fold>
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OTHER SETTER AND GETTER">
    /**
     * Assign generated 2-D RL scenarios to GPSegments
     *
     * @param scenariosGP generated 2-D RL scenarios for GP segments
     * [numSegments][numScenarios] (Do NOT include default scenario)
     * @param scenariosML generated 2-D RL scenarios for ML segments (null if
     * not applicable) [numSegments][numScenarios] (Do NOT include default
     * scenario)
     * @param scenarioInfo reliability analysis scenario information
     * @return whether setting scenarios is successful
     */
    public String setRLScenarios(Scenario scenariosGP, Scenario scenariosML, ArrayList<ScenarioInfo> scenarioInfo) {
        try {
            fireDataChanged(CHANGE_SEED);

            inNumScen = scenariosGP.size();
            this.RL_Scenarios_GP = scenariosGP;
            this.RL_Scenarios_ML = scenariosML;

            scenarioInfo.add(0, new ScenarioInfo());
            this.RL_ScenarioInfo = scenarioInfo;

            return "Scenarios assigned";
        } catch (Exception e) {
            return "Fail to set scenarios " + e.toString();
        }
    }

    /**
     * Get random number seed for RL scenario generation
     *
     * @return random number seed for RL scenario generation
     */
    public Long getRLRNGSeed() {
        return RL_RngSeed;
    }

    /**
     * Sets the value of the random number generator seed as newVal.
     *
     * @param newVal new random number seed for RL scenario generation
     */
    public void setRLRNGSeed(long newVal) {
        RL_RngSeed = newVal;
    }

    /**
     * Getter for analysis period start time
     *
     * @return analysis period start time
     */
    public CETime getStartTime() {
        return inStartTime;
    }

    /**
     * Getter for analysis period end time
     *
     * @return analysis period end time
     */
    public CETime getEndTime() {
        return inEndTime;
    }

    /**
     * Getter for seed file date
     *
     * @return seed file date
     */
    public CEDate getSeedFileDate() {
        return RL_SeedFileDate;
    }

    /**
     * Setter for seed file date
     *
     * @param seedFileDate seed file date
     */
    public void setSeedFileDate(CEDate seedFileDate) {
        RL_SeedFileDate = seedFileDate;
        RL_RRPStartDate.year = RL_SeedFileDate.year;
        RL_RRPEndDate.year = RL_SeedFileDate.year;
    }

    /**
     * Getter for reliability analysis start date
     *
     * @return reliability analysis start date
     */
    public CEDate getRRPStartDate() {
        return RL_RRPStartDate;
    }

    /**
     * Setter for reliability analysis start date
     *
     * @param RRPStartDate reliability analysis start date
     */
    public void setRRPStartDate(CEDate RRPStartDate) {
        this.RL_RRPStartDate = RRPStartDate;
    }

    /**
     * Getter for reliability analysis end date
     *
     * @return reliability analysis end date
     */
    public CEDate getRRPEndDate() {
        return RL_RRPEndDate;
    }

    /**
     * Setter for reliability analysis end date
     *
     * @param RRPEndDate reliability analysis end date
     */
    public void setRRPEndDate(CEDate RRPEndDate) {
        this.RL_RRPEndDate = RRPEndDate;
    }

    /**
     * Getter for reliability analysis scenario information
     *
     * @return reliability analysis scenario information
     */
    public ArrayList<ScenarioInfo> getRLScenarioInfo() {
        return RL_ScenarioInfo;
    }

    /**
     * Getter for reliability analysis GP Scenarios
     *
     * @return Reliability analysis GP scenarios
     */
    public Scenario getRLScenarioGP() {
        return RL_Scenarios_GP;
    }

    /**
     * Getter for reliability analysis ML Scenarios
     *
     * @return Reliability analysis ML scenarios
     */
    public Scenario getRLScenarioML() {
        return RL_Scenarios_ML;
    }

    /**
     * Getter for specified demand
     *
     * @return specified demand
     */
    public float[][] getSpecifiedGPDemand() {
        return RL_SpecifiedDemand_GP;
    }

    /**
     * Setter for specified demand
     *
     * @param specifiedDemand specified demand
     */
    public void setSpecifiedGPDemand(float[][] specifiedDemand) {
        this.RL_SpecifiedDemand_GP = specifiedDemand;
    }

    /**
     * Getter for specified demand
     *
     * @return specified demand
     */
    public float[][] getSpecifiedMLDemand() {
        return RL_SpecifiedDemand_ML;
    }

    /**
     * Setter for specified demand
     *
     * @param specifiedDemand specified demand
     */
    public void setSpecifiedMLDemand(float[][] specifiedDemand) {
        this.RL_SpecifiedDemand_ML = specifiedDemand;
    }

    /**
     * Getter for weekday used in reliability analysis
     *
     * @return weekday used in reliability analysis
     */
    public boolean[] getWeekdayUsed() {
        return RL_WeekdayUsed;
    }

    /**
     * Setter for weekday used in reliability analysis
     *
     * @param weekdayUsed weekday used in reliability analysis
     */
    public void setWeekdayUsed(boolean[] weekdayUsed) {
        this.RL_WeekdayUsed = weekdayUsed;
    }

    /**
     * Getter for weekday excluded in reliability analysis
     *
     * @return weekday excluded in reliability analysis
     */
    public ArrayList<CEDate> getDayExcluded() {
        return RL_DayExcluded;
    }

    /**
     * Setter for weekday excluded in reliability analysis
     *
     * @param dayExcluded weekday excluded in reliability analysis
     */
    public void setDayExcluded(ArrayList<CEDate> dayExcluded) {
        this.RL_DayExcluded = dayExcluded;
    }

    /**
     * Getter for weather probability
     *
     * @return weather probability
     */
    public float[][] getWeatherProbability() {
        return RL_WeatherProbability;
    }

    /**
     * Setter for weather probability
     *
     * @param weatherProbability weather probability
     */
    public void setWeatherProbability(float[][] weatherProbability) {
        this.RL_WeatherProbability = weatherProbability;
    }

    /**
     * Getter for weather average duration
     *
     * @return weather average duration
     */
    public float[] getWeatherAverageDuration() {
        return RL_WeatherAverageDuration;
    }

    /**
     * Setter for weather average duration
     *
     * @param weatherAverageDuration weather average duration
     */
    public void setWeatherAverageDuration(float[] weatherAverageDuration) {
        this.RL_WeatherAverageDuration = weatherAverageDuration;
    }

    /**
     * Getter for weather capacity adjustment factors.
     *
     * @return Array of weather capacity adjustment factors.
     */
    public float[] getWeatherCAF() {
        return RL_WeatherCAF;
    }

    /**
     * Setter for weather capacity adjustment factors.
     *
     * @param RL_WeatherCAF Array of weather capacity adjustment factors.
     */
    public void setWeatherCAF(float[] RL_WeatherCAF) {
        this.RL_WeatherCAF = RL_WeatherCAF;
    }

    /**
     * Getter for weather demand adjustment factors.
     *
     * @return Array of weather demand adjustment factors.
     */
    public float[] getWeatherDAF() {
        return RL_WeatherDAF;
    }

    /**
     * Setter for weather demand adjustment factors.
     *
     * @param RL_WeatherDAF Array of weather demand adjustment factors.
     */
    public void setWeatherDAF(float[] RL_WeatherDAF) {
        this.RL_WeatherDAF = RL_WeatherDAF;
    }

    /**
     * Getter for weather speed adjustment factors.
     *
     * @return Array of weather speed adjustment factors.
     */
    public float[] getWeatherSAF() {
        return RL_WeatherSAF;
    }

    /**
     * Setter for weather speed adjustment factors.
     *
     * @param RL_WeatherSAF Array of weather speed adjustment factors.
     */
    public void setWeatherSAF(float[] RL_WeatherSAF) {
        this.RL_WeatherSAF = RL_WeatherSAF;
    }

    /**
     * Getter for whether free flow speed is known
     *
     * @return whether free flow speed is known
     */
    public boolean isFreeFlowSpeedKnown() {
        return inFreeFlowSpeedKnown;
    }

    /**
     * Setter for whether free flow speed is known
     *
     * @param freeFlowSpeedKnown whether free flow speed is known
     */
    public void setFreeFlowSpeedKnown(boolean freeFlowSpeedKnown) {
        if (inFreeFlowSpeedKnown != freeFlowSpeedKnown) {
            inFreeFlowSpeedKnown = freeFlowSpeedKnown;
            fireDataChanged(CHANGE_SEED);
        }
    }

    /**
     * Getter for weather location
     *
     * @return weather location
     */
    public String getWeatherLocation() {
        return RL_WeatherLocation;
    }

    /**
     * Setter for weather location
     *
     * @param weatherLocation weather location
     */
    public void setWeatherLocation(String weatherLocation) {
        this.RL_WeatherLocation = weatherLocation;
    }

    /**
     * Getter for GP incident frequency
     *
     * @return incident frequency
     */
    public float[] getGPIncidentFrequency() {
        return RL_IncidentFrequency_GP;
    }

    /**
     * Setter for GP incident frequency
     *
     * @param incidentFrequency incident frequency
     */
    public void setGPIncidentFrequency(float[] incidentFrequency) {
        if (this.RL_IncidentFrequency_GP == null) {
            this.RL_IncidentFrequency_GP = new float[incidentFrequency.length];
        }
        for (int month = 0; month < incidentFrequency.length; month++) {
            this.RL_IncidentFrequency_GP[month] = incidentFrequency[month];
        }
    }

    /**
     * Getter for GP incident duration
     *
     * @return incident duration
     */
    public float[][] getGPIncidentDuration() {
        return RL_IncidentDuration_GP;
    }

    /**
     * Setter for GP incident duration
     *
     * @param incidentDuration incident duration
     */
    public void setGPIncidentDuration(float[][] incidentDuration) {
        this.RL_IncidentDuration_GP = incidentDuration;
    }

    /**
     * Getter for GP incident severity distribution
     *
     * @return GP incident severity distribution
     */
    public float[] getGPIncidentDistribution() {
        return RL_IncidentDistribution_GP;
    }

    /**
     * Setter for GP incident severity distribution.
     *
     * @param incidentDistribution GP incident severity distribution
     */
    public void setGPIncidentDistribution(float[] incidentDistribution) {
        this.RL_IncidentDistribution_GP = incidentDistribution;
    }

    /**
     * Getter for GP incident CAF
     *
     * @return incident CAF
     */
    public float[][] getGPIncidentCAF() {
        return RL_IncidentCAF_GP;
    }

    /**
     * Setter for GP incident CAF
     *
     * @param incidentCAF incident CAF
     */
    public void setGPIncidentCAF(float[][] incidentCAF) {
        this.RL_IncidentCAF_GP = incidentCAF;
    }

    /**
     * Getter for GP incident DAF
     *
     * @return incident DAF
     */
    public float[][] getGPIncidentDAF() {
        return RL_IncidentDAF_GP;
    }

    /**
     * Setter for GP incident DAF
     *
     * @param incidentDAF incident DAF
     */
    public void setGPIncidentDAF(float[][] incidentDAF) {
        this.RL_IncidentDAF_GP = incidentDAF;
    }

    /**
     * Getter for GP incident SAF
     *
     * @return incident SAF
     */
    public float[][] getGPIncidentSAF() {
        return RL_IncidentSAF_GP;
    }

    /**
     * Setter for GP incident SAF
     *
     * @param incidentSAF incident SAF
     */
    public void setGPIncidentSAF(float[][] incidentSAF) {
        this.RL_IncidentSAF_GP = incidentSAF;
    }

    /**
     * Getter for GP incident LAF
     *
     * @return incident LAF
     */
    public int[][] getGPIncidentLAF() {
        return RL_IncidentLAF_GP;
    }

    /**
     * Setter for GP incident LAF
     *
     * @param incidentLAF incident LAF
     */
    public void setGPIncidentLAF(int[][] incidentLAF) {
        this.RL_IncidentLAF_GP = incidentLAF;
    }

    /**
     * Getter for GP incident crash ratio
     *
     * @return incident crash ratio
     */
    public float getGPIncidentCrashRatio() {
        return RL_IncidentCrashRatio_GP;
    }

    /**
     * Setter for GP incident crash ratio
     *
     * @param incidentCrashRatio incident crash ratio
     */
    public void setGPIncidentCrashRatio(float incidentCrashRatio) {
        this.RL_IncidentCrashRatio_GP = incidentCrashRatio;
    }

    /**
     * Getter for ML incident frequency
     *
     * @return incident frequency
     */
    public float[] getMLIncidentFrequency() {
        return RL_IncidentFrequency_ML;
    }

    /**
     * Setter for ML incident frequency
     *
     * @param incidentFrequency incident frequency
     */
    public void setMLIncidentFrequency(float[] incidentFrequency) {
        if (this.RL_IncidentFrequency_ML == null) {
            this.RL_IncidentFrequency_ML = new float[incidentFrequency.length];
        }
        for (int month = 0; month < incidentFrequency.length; month++) {
            this.RL_IncidentFrequency_ML[month] = incidentFrequency[month];
        }
    }

    /**
     * Getter for ML incident duration
     *
     * @return incident duration
     */
    public float[][] getMLIncidentDuration() {
        return RL_IncidentDuration_ML;
    }

    /**
     * Setter for ML incident duration
     *
     * @param incidentDuration incident duration
     */
    public void setMLIncidentDuration(float[][] incidentDuration) {
        this.RL_IncidentDuration_ML = incidentDuration;
    }

    /**
     * Getter for ML incident severity distribution
     *
     * @return ML incident severity distribution
     */
    public float[] getMLIncidentDistribution() {
        return RL_IncidentDistribution_ML;
    }

    /**
     * Setter for ML incident severity distribution.
     *
     * @param incidentDistribution ML incident severity distribution
     */
    public void setMLIncidentDistribution(float[] incidentDistribution) {
        this.RL_IncidentDistribution_ML = incidentDistribution;
    }

    /**
     * Getter for ML incident CAF
     *
     * @return incident CAF
     */
    public float[][] getMLIncidentCAF() {
        return RL_IncidentCAF_ML;
    }

    /**
     * Setter for ML incident CAF
     *
     * @param incidentCAF incident CAF
     */
    public void setMLIncidentCAF(float[][] incidentCAF) {
        this.RL_IncidentCAF_ML = incidentCAF;
    }

    /**
     * Getter for ML incident DAF
     *
     * @return incident DAF
     */
    public float[][] getMLIncidentDAF() {
        return RL_IncidentDAF_ML;
    }

    /**
     * Setter for ML incident DAF
     *
     * @param incidentDAF incident DAF
     */
    public void setMLIncidentDAF(float[][] incidentDAF) {
        this.RL_IncidentDAF_ML = incidentDAF;
    }

    /**
     * Getter for ML incident SAF
     *
     * @return incident SAF
     */
    public float[][] getMLIncidentSAF() {
        return RL_IncidentSAF_ML;
    }

    /**
     * Setter for ML incident SAF
     *
     * @param incidentSAF incident SAF
     */
    public void setMLIncidentSAF(float[][] incidentSAF) {
        this.RL_IncidentSAF_ML = incidentSAF;
    }

    /**
     * Getter for ML incident LAF
     *
     * @return incident LAF
     */
    public int[][] getMLIncidentLAF() {
        return RL_IncidentLAF_ML;
    }

    /**
     * Setter for ML incident LAF
     *
     * @param incidentLAF incident LAF
     */
    public void setMLIncidentLAF(int[][] incidentLAF) {
        this.RL_IncidentLAF_ML = incidentLAF;
    }

    /**
     * Getter for ML incident crash ratio
     *
     * @return incident crash ratio
     */
    public float getMLIncidentCrashRatio() {
        return RL_IncidentCrashRatio_ML;
    }

    /**
     * Setter for ML crash ration
     *
     * @param incidentCrashRatio incident crash ratio
     */
    public void setMLIncidentCrashRatio(float incidentCrashRatio) {
        this.RL_IncidentCrashRatio_ML = incidentCrashRatio;
    }

    /**
     * Getter for list of Work Zones
     *
     * @return list of Work Zones
     */
    public ArrayList<WorkZone> getRLWorkZones() {
        return RL_WorkZones;
    }

    /**
     * Setter for list of Work Zones
     *
     * @param RL_WorkZones list of Work Zones
     */
    public void setRLWorkZones(ArrayList<WorkZone> RL_WorkZones) {
        this.RL_WorkZones = RL_WorkZones;
    }

    /**
     * Setter for ATDM database
     *
     * @param newDB ATDM database
     */
    public void setATDMDatabase(ATDMDatabase newDB) {
        if (newDB != null) {
            newDB.validateFacilitySpecificStrategies(this);
            this.atdmDatabase = newDB;
        }
    }

    /**
     * Getter for ATDM database
     *
     * @return ATDM database
     */
    public ATDMDatabase getATDMDatabase() {
        return this.atdmDatabase;
    }

    /**
     * Getter for TTI
     *
     * @return TTI
     */
    public float[] getTTI_Value() {
        return TTI_Value;
    }

    /**
     * Setter for TTI
     *
     * @param TTI_Value TTI
     */
    public void setTTI_Value(float[] TTI_Value) {
        this.TTI_Value = TTI_Value;
    }

    /**
     * Getter for comprehensive ramp metering information
     *
     * @param scen scenario index
     * @param atdm ATDM index
     * @param segType segment GP/ML type
     * @return comprehensive ramp metering information
     */
    public RMHelper getRampMetering(int scen, int atdm, int segType) {
        if (atdm < 0 || getATDMSets().get(atdm) == null || getATDMSets().get(atdm).get(scen) == null || ATDMSets.get(atdm).get(scen)[segType].RM() == null) {
            return segType == CEConst.SEG_TYPE_GP ? rampMeteringGP : rampMeteringML;
        } else {
            return ATDMSets.get(atdm).get(scen)[segType].RM();
        }
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="TOOLBOX SETTER AND GETTER">
    //<editor-fold defaultstate="collapsed" desc="DSS RELATED SETTER AND GETTER">
    /**
     * Sets whether the oversaturated module is forced to be used for all
     * periods. If the value is true, the oversaturated module will be used for
     * the analysis of all periods. If the value is false, the CoreEngine will
     * switch between undersaturated and oversaturated as needed.
     *
     * @param val True if the CoreEngine should force the use of the
     * oversaturated module for all periods, false if the CoreEngine is allowed
     * to switch between under and oversaturated as needed.
     */
    public void enableForceOversat(boolean val) {
        this.forceOversat = val;
    }
    //</editor-fold>
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MANAGED LANE FUNCTIONS">
    /**
     * Getter for whether managed lane is used
     *
     * @return whether managed lane is used
     */
    public boolean isManagedLaneUsed() {
        return inManagedLaneUsed;
    }

    /**
     * Setter for whether managed lane is used
     *
     * @param managedLaneUsed whether managed lane is used
     */
    public void setManagedLaneUsed(boolean managedLaneUsed) {
        if (this.inManagedLaneUsed != managedLaneUsed) {
            this.inManagedLaneUsed = managedLaneUsed;

            if (GPSegments != null) {
                if (managedLaneUsed) {
                    if (MLSegments == null || MLSegments.size() != GPSegments.size()) {
                        MLSegments = generateSegments(GPSegments.size(), inNumPeriod, CEConst.SEG_TYPE_ML);
                        rampMeteringML = new RMHelper(this);
                        connectGPAndMLSegments();
                    }
                } else {
                    MLSegments = null;
                    rampMeteringML = new RMHelper(this);
                    RL_Scenarios_ML = null;
                    reduceMLMemory();
                    disconnectGPAndMLSegments();
                }
            }

            fireDataChanged(CHANGE_SEED);
        }
    }

    /**
     * Free managed lane memory
     */
    private void reduceMLMemory() {
        pOutMLMaxDC = null;
        pOutMLMaxVC = null;
        pOutMLDenyLengthFt = null;
        pOutMLMainlineQueueLengthFt = null;
        pOutMLOnQueueLengthFt = null;
        pOutMLActualTravelTime = null; //min
        pOutMLFreeFlowTravelTime = null; //min
        pOutMLMainlineDelay = null; //min
        pOutMLOnRampDelay = null; //min
//        pOutMLSystemDelay = null; //min
        pOutMLVMTD = null; //veh-miles / interval
        pOutMLVMTV = null; //veh-miles / interval
        pOutMLVHT = null; // travel / interval (hrs)
        pOutMLVHD = null; // delay / interval (hrs)
        pOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
        pOutMLDensityTotal_veh = null; //veh/mi/lane
        pOutMLDensityTotal_pc = null; //pc/mi/lane
        pOutMLReportLOS = null;
        pOutMLTravelTimeIndex = null;

        //all periods summary for each segment [numScen][GPSegments.size()]
        sOutMLActualTravelTime = null; //min
        sOutMLVMTD = null; //veh-miles / interval
        sOutMLVMTV = null; //veh-miles / interval
        sOutMLVHT = null; // travel / interval (hrs)
        sOutMLVHD = null; // delay / interval (hrs)
        sOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
        sOutMLReportDensity_IA_pc = null; //pc/mi/lane
        sMLReportDensityFactor = null;
        sOutMLMaxDC = null;
        sOutMLMaxVC = null;

        spOutMLActualTravelTime = null; //min
        spOutMLVMTD = null; //veh-miles / interval
        spOutMLVMTV = null; //veh-miles / interval
        spOutMLVHT = null; // travel / interval (hrs)
        spOutMLVHD = null; // delay / interval (hrs)
        spOutMLSpaceMeanSpeed = null; //mph = VMTV / VHT
        spOutMLReportDensity_IA_pc = null; //pc/mi/lane

        spOutMLMaxDC = null; //maximum demand / capacity
        spOutMLMaxVC = null; //maximum volume / capacity

        spMLReportDensityFactor = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OTHER DATA AND FUNCTIONS">
    @Override
    public String toString() {
        return inProjectName;
    }

    /**
     * Check whether output is valid (for debug only)
     */
    private void debugCheckOutput() {
        for (GPMLSegment segment : GPSegments) {
            for (int period = 0; period < inNumPeriod; period++) {
                if (segment.scenSpeed[period] <= CEConst.ZERO) {
                    System.out.println("Speed Error: value" + segment.scenSpeed[period] + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
                }
                if (segment.getScenAllDensity_pc(period) <= CEConst.ZERO) {
                    //System.out.println("Density Error: value" + segment.getScenAllDensity_pc(period) + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
                }
//                if (segment.scenSysDelay[period] < 0) {
//                    System.out.println("Delay Error: value" + segment.scenSysDelay[period] + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
//                }
                if (segment.getScenTTI(period) < 1) {
                    System.out.println("TTI Error: value" + segment.getScenTTI(period) + ", scen: " + ", period: " + period + ", seg: " + segment.inIndex);
                }
            }
        }
    }

    /**
     * Reset seed status (must be called) after read from file since output data
     * in not stored on disk
     */
    public void resetSeedToInputOnly() {
        fireDataChanged(CHANGE_RESET_TO_INPUT);
    }

    /**
     * Check whether a scenario/ATDM is in output buffer
     *
     * @param scen scenario index
     * @param atdm ATDM set index
     */
    private void checkInBuffer(int scen, int atdm) {
        if (scen != bufferScen || atdm != bufferATDM) {
            singleRun(scen, atdm);
        }
    }

    /**
     * Seed level change mark
     */
    private static final int CHANGE_SEED = 0;

    /**
     * Scenario level change mark
     */
    private static final int CHANGE_SCEN = 1;

    /**
     * ATDM level change mark
     */
    private static final int CHANGE_ATDM = 2;

    /**
     * Reset to input mark
     */
    private static final int CHANGE_RESET_TO_INPUT = 3;

    /**
     * Modify seed when seed data changes
     *
     * @param changeType change type
     */
    private void fireDataChanged(int changeType) {
        _resetBuffer();

        switch (changeType) {
            case CHANGE_SEED:
                needMemory = true;
                seedInputModified = true;
                _deleteRLScenarios();
                deleteAllATDM();
                break;
            case CHANGE_SCEN:
                deleteAllATDM();
                break;
            case CHANGE_ATDM:
                break;
            case CHANGE_RESET_TO_INPUT:
                needMemory = true;
                seedInputModified = true;
                for (ScenarioInfo info : RL_ScenarioInfo) {
                    info.statusRL = CEConst.SCENARIO_INPUT_ONLY;
                }
                for (HashMap<Integer, ATDMScenario[]> ATDMSet : ATDMSets) {
                    for (int scen : ATDMSet.keySet()) {
                        ATDMSet.get(scen)[0].setStatus(CEConst.SCENARIO_INPUT_ONLY);
                    }
                }
                break;
        }
    }

    /**
     * Reset result buffer to null
     */
    private void _resetBuffer() {
        bufferScen = -1;
        bufferATDM = -1;
    }

    /**
     * Helper for delete RL scenarios
     */
    private void _deleteRLScenarios() {
        inNumScen = 0;
        RL_Scenarios_GP = null;
        RL_Scenarios_ML = null;
        RL_ScenarioInfo = CEHelper.scenInfo_1D(inNumScen + 1);
    }

    /**
     * Delete all generated scenarios (not include default scenario)
     */
    public void cleanScenarios() {
        fireDataChanged(CHANGE_SEED);
    }
    // </editor-fold>

    public void extrapolateRampTruckPCT() {
        float calcSUT, calcTT;
        for (GPMLSegment seg : GPSegments) {
            for (int period = 0; period < getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                if (seg.inUpSeg == null) {
                    seg.unadjustedSegDemand[period] = seg.inMainlineDemand_veh.get(period);
                } else {
                    seg.unadjustedSegDemand[period] = seg.inUpSeg.unadjustedSegDemand[period];
                    if (seg.inType == CEConst.SEG_TYPE_ONR || seg.inType == CEConst.SEG_TYPE_ONR_B
                            || seg.inType == CEConst.SEG_TYPE_W || seg.inType == CEConst.SEG_TYPE_W_B) {
                        seg.unadjustedSegDemand[period] += seg.inOnDemand_veh.get(period);
                    }
                    if (seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR || seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR_B
                            || seg.inUpSeg.inType == CEConst.SEG_TYPE_W || seg.inUpSeg.inType == CEConst.SEG_TYPE_W_B) {
                        seg.unadjustedSegDemand[period] -= seg.inUpSeg.inOffDemand_veh.get(period);
                    }
                    if ((seg.inUpSeg.inType != CEConst.SEG_TYPE_OFR
                            && seg.inUpSeg.inType != CEConst.SEG_TYPE_OFR_B
                            && seg.inUpSeg.inType != CEConst.SEG_TYPE_W)
                            && (seg.inType == CEConst.SEG_TYPE_ONR
                            || seg.inType == CEConst.SEG_TYPE_ONR_B
                            || seg.inType == CEConst.SEG_TYPE_W
                            || seg.inType == CEConst.SEG_TYPE_W)) {
                        // If the upstream segment is not an OFR/Weave and the
                        // current segment is an ONR/Weave segment
                        calcSUT = ((seg.unadjustedSegDemand[period] * seg.inMainlineTruckSingle.get(period))
                                - (seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckSingle.get(period))
                                + (seg.inUpSeg.inOffDemand_veh.get(period) * seg.inUpSeg.inOFRTruckSingle.get(period))) / Math.max(seg.inOnDemand_veh.get(period), 1);
                        calcTT = ((seg.unadjustedSegDemand[period] * seg.inMainlineTruckTrailer.get(period))
                                - (seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckTrailer.get(period))
                                + (seg.inUpSeg.inOffDemand_veh.get(period) * seg.inUpSeg.inOFRTruckTrailer.get(period))) / Math.max(seg.inOnDemand_veh.get(period), 1);
                        calcSUT = Math.min(Math.max(0.0f, calcSUT), 100.0f);
                        calcTT = Math.min(Math.max(0.0f, calcTT), 100.0f);
                        setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR, calcSUT, seg.inIndex, period);
                        setValue(CEConst.IDS_TRUCK_TRAILER_PCT_ONR, calcTT, seg.inIndex, period);
                        //seed.setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, seg);

                    } else if ((seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR
                            || seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR_B
                            || seg.inUpSeg.inType == CEConst.SEG_TYPE_W)
                            && !(seg.inType == CEConst.SEG_TYPE_ONR
                            || seg.inType == CEConst.SEG_TYPE_ONR_B
                            || seg.inType == CEConst.SEG_TYPE_W)) {
                        // If the upstream segment is an OFR/Weave segment and
                        // the current segment is not an ONR/Weave segment
                        calcSUT = ((seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckSingle.get(period))
                                - (seg.unadjustedSegDemand[period] * seg.inMainlineTruckSingle.get(period))
                                + (seg.inOnDemand_veh.get(period) * seg.inONRTruckSingle.get(period))) / Math.max(seg.inUpSeg.inOffDemand_veh.get(period), 1);
                        calcTT = ((seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckTrailer.get(period))
                                - (seg.unadjustedSegDemand[period] * seg.inMainlineTruckTrailer.get(period))
                                + (seg.inOnDemand_veh.get(period) * seg.inONRTruckTrailer.get(period))) / Math.max(seg.inUpSeg.inOffDemand_veh.get(period), 1);
                        calcSUT = Math.min(Math.max(0.0f, calcSUT), 100.0f);
                        calcTT = Math.min(Math.max(0.0f, calcTT), 100.0f);
                        setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR, calcSUT, seg.inUpSeg.inIndex, period);
                        setValue(CEConst.IDS_TRUCK_TRAILER_PCT_OFR, calcTT, seg.inUpSeg.inIndex, period);
                    } else if (seg.inType == CEConst.SEG_TYPE_OFR
                            || seg.inType == CEConst.SEG_TYPE_OFR_B) {

                    } else if ((seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR
                            || seg.inUpSeg.inType == CEConst.SEG_TYPE_OFR_B
                            || seg.inUpSeg.inType == CEConst.SEG_TYPE_W)
                            && (seg.inType == CEConst.SEG_TYPE_ONR
                            || seg.inType == CEConst.SEG_TYPE_ONR_B
                            || seg.inType == CEConst.SEG_TYPE_W)) {
                        // The upstream ramp is an OFR/Weave segment and the
                        // current segment is on ONR/Weave segment.  In this
                        // case the ONR and OFR SUT/TT percentages must be
                        // determined in conjunction and are assumed to be equal
                        calcSUT = (seg.unadjustedSegDemand[period] * seg.inMainlineTruckSingle.get(period));
                        calcSUT -= (seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckSingle.get(period));
                        calcSUT /= Math.max((seg.inOnDemand_veh.get(period) - seg.inUpSeg.inOffDemand_veh.get(period)), 1);
                        calcSUT = Math.min(Math.max(0.0f, calcSUT), 100.0f);
                        calcTT = (seg.unadjustedSegDemand[period] * seg.inMainlineTruckTrailer.get(period));
                        calcTT -= (seg.inUpSeg.unadjustedSegDemand[period] * seg.inUpSeg.inMainlineTruckTrailer.get(period));
                        calcTT /= Math.max((seg.inOnDemand_veh.get(period) - seg.inUpSeg.inOffDemand_veh.get(period)), 1);
                        calcTT = Math.min(Math.max(0.0f, calcTT), 100.0f);
                        setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_ONR, calcSUT, seg.inIndex, period);
                        setValue(CEConst.IDS_TRUCK_TRAILER_PCT_ONR, calcTT, seg.inIndex, period);
                        setValue(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_OFR, calcSUT, seg.inUpSeg.inIndex, period);
                        setValue(CEConst.IDS_TRUCK_TRAILER_PCT_OFR, calcTT, seg.inUpSeg.inIndex, period);
                    }
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="ADAPTIVE RAMP METERING DATA AND FUNCTIONS">
    /**
     * ALINEA Adaptive Ramp Metering Data
     */
    private final TreeMap<String, RampMeteringALINEAData> rmALINEA = new TreeMap();

    /**
     * Fuzzy Adaptive Ramp Metering Data
     */
    private final TreeMap<String, RampMeteringFuzzyData> rmFuzzy = new TreeMap();

    /**
     * Add ALINEA Adaptive Ramp Metering Data
     *
     * @param key key string for the scheme
     * @param newData data for the scheme
     */
    public void addRampMeteringALINEAData(String key, RampMeteringALINEAData newData) {
        rmALINEA.put(key, newData);
    }

    /**
     * Add Fuzzy Adaptive Ramp Metering Data
     *
     * @param key key string for the scheme
     * @param newData data for the scheme
     */
    public void addRampMeteringFuzzyData(String key, RampMeteringFuzzyData newData) {
        rmFuzzy.put(key, newData);
    }

    /**
     * Remove ALINEA Adaptive Ramp Metering Data
     *
     * @param key key string for the scheme
     */
    public void removeRampMeteringALINEAData(String key) {
        rmALINEA.remove(key);
    }

    /**
     * Remove Fuzzy Adaptive Ramp Metering Data
     *
     * @param key key string for the scheme
     */
    public void removeRampMeteringFuzzyData(String key) {
        rmFuzzy.remove(key);
    }

    /**
     * Getter for ALINEA Adaptive Ramp Metering Data
     *
     * @return data for the scheme
     */
    public TreeMap<String, RampMeteringALINEAData> getRampMeteringALINEAData() {
        return rmALINEA;
    }

    /**
     * Getter for Fuzzy Adaptive Ramp Metering Data
     *
     * @return data for the scheme
     */
    public TreeMap<String, RampMeteringFuzzyData> getRampMeteringFuzzyData() {
        return rmFuzzy;
    }
    /**
     * Debug toggle for Fuzzy RM
     */
    public boolean printRM = false;

    /**
     * File in which to save the debug output for Fuzzy RM
     */
    public String fuzzyRMDebugOutputFileName;

    /**
     * For Debugging Purposes Only
     */
    public final boolean printDiss = true;
    public boolean printDiss2 = false;

    /**
     * For Debugging Purposes Only
     */
    public final String printDissOutputFileName = "C:\\Users\\ltrask\\Documents\\GitHub\\Freeval_HCM\\";
    public String printDissOutputFileName2 = "C:\\Users\\ltrask\\Documents\\GitHub\\Freeval_HCM\\Notes\\";
    // </editor-fold>
}
