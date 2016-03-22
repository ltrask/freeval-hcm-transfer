package coreEngine.Helper;

import coreEngine.Seed;
import java.text.DecimalFormat;

/**
 * This class is used to store output of a seed single run.
 *
 * @author Shu Liu
 */
public class FacilitySummary {

    /**
     * Display Header Constant
     */
    public final static String HEADER_totalLength = "Length (mi)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_actualTravelTime = "Average Travel Time (min)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_VMTD = "VMTD (veh-miles / interval)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_VMTV = "VMTV (veh-miles / interval)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_PMTD = "PMTD (p-miles / interval)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_PMTV = "PMTV (p-miles / interval)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_VHT = "VHT (travel / interval (hrs))";

    /**
     * Display Header Constant
     */
    public final static String HEADER_VHD = "VHD (delay / interval (hrs))";

    /**
     * Display Header Constant
     */
    public final static String HEADER_spaceMeanSpeed = "Space Mean Speed (mph)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_reportDensity = "Reported Density (pc/mi/ln)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_maxDC = "Max D/C";

    /**
     * Display Header Constant
     */
    public final static String HEADER_maxVC = "Max V/C";

    /**
     * Display Header Constant
     */
    public final static String HEADER_userCost = "User Cost ($)";

    /**
     * Display Header Constant
     */
    public final static String HEADER_maxHourlyUserCost = "Max Hourly User Cost ($)";

    /**
     * Facility Seed Object
     */
    public final Seed seed;

    /**
     * Scenario index
     */
    public final int scen;

    /**
     * ATDM index
     */
    public final int atdm;

    /**
     * Summary Data
     */
    public float totalLength;

    /**
     * Summary Data
     */
    public float actualTravelTime;

    /**
     * Summary Data
     */
    public float VMTD;

    /**
     * Summary Data
     */
    public float VMTV;

    /**
     * Summary Data
     */
    public float PMTD;

    /**
     * Summary Data
     */
    public float PMTV;

    /**
     * Summary Data
     */
    public float VHT;

    /**
     * Summary Data
     */
    public float VHD;

    /**
     * Summary Data
     */
    public float spaceMeanSpeed;

    /**
     * Summary Data
     */
    public float reportDensity;

    /**
     * Summary Data
     */
    public float maxDC;

    /**
     * Summary Data
     */
    public float maxVC;

    /**
     * Summary Data
     */
    public float userDelayCost;

    /**
     * Summary Data
     */
    public float vehicleOperatingCost;

    /**
     * Summary Data
     */
    public float userCost;

    /**
     * Summary data
     */
    public float maxHourlyUserCost;

    /**
     * Summary data
     */
    public String maxHourlyUserCostSring;

    /**
     * User Delay Cost for Cars
     */
    public static float udcCars = 21.07f;

    /**
     * User Delay Cost for Trucks
     */
    public static float udcTrucks = 26.08f;

    /**
     * Vehicle Operating Cost for Cars
     */
    public static float vocCars = 22.85f;

    /**
     * Vehicle Operating Cost for Trucks
     */
    public static float vocTrucks = 157.73f;

    /**
     * Constructor
     *
     * @param seed seed
     * @param scen scenario index
     */
    public FacilitySummary(Seed seed, int scen) {
        this(seed, scen, -1);
    }

    /**
     * Constructor
     *
     * @param seed seed
     * @param scen scenario index
     * @param atdm ATDM set index
     */
    public FacilitySummary(Seed seed, int scen, int atdm) {
        this.seed = seed;
        this.scen = scen;
        this.atdm = atdm;
        totalLength = seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI);
        actualTravelTime = seed.getValueFloat(CEConst.IDS_CB_SP_ACTUAL_TIME, 0, 0, scen, atdm);
        VMTD = seed.getValueFloat(CEConst.IDS_CB_SP_VMTD, 0, 0, scen, atdm);
        VMTV = seed.getValueFloat(CEConst.IDS_CB_SP_VMTV, 0, 0, scen, atdm);
        PMTD = seed.getValueFloat(CEConst.IDS_CB_SP_PMTD, 0, 0, scen, atdm);
        PMTV = seed.getValueFloat(CEConst.IDS_CB_SP_PMTV, 0, 0, scen, atdm);
        VHT = seed.getValueFloat(CEConst.IDS_CB_SP_VHT, 0, 0, scen, atdm);
        VHD = seed.getValueFloat(CEConst.IDS_CB_SP_VHD, 0, 0, scen, atdm);
        spaceMeanSpeed = seed.getValueFloat(CEConst.IDS_CB_SP_SPACE_MEAN_SPEED, 0, 0, scen, atdm);
        reportDensity = seed.getValueFloat(CEConst.IDS_CB_SP_REPORT_DENSITY_PC, 0, 0, scen, atdm);
        maxDC = seed.getValueFloat(CEConst.IDS_CB_SP_MAX_DC, 0, 0, scen, atdm);
        maxVC = seed.getValueFloat(CEConst.IDS_CB_SP_MAX_VC, 0, 0, scen, atdm);
        maxHourlyUserCost = -1.0f;
        DecimalFormat formatter = new DecimalFormat("00");
        maxHourlyUserCostSring = "";

        float pctTrucks = 0.0f;
        float pctCars;
        float tempUserDelayCost = 0.0f;
        float tempVehicleOperatingCost = 0.0f;
        CETime currTime = seed.getStartTime();
        float tempMaxHourlyUserCost = 0.0f;
        for (int per = 0; per < seed.getValueInt(CEConst.IDS_NUM_PERIOD); per++) {
            for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                pctTrucks += seed.getValueFloat(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, seg, per, scen, atdm)
                        + seed.getValueFloat(CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, seg, per, scen, atdm);
            }
            pctTrucks /= seed.getValueInt(CEConst.IDS_NUM_SEGMENT);
            pctTrucks /= 100.0f;
            pctCars = 1.0f - pctTrucks;
            tempUserDelayCost = seed.getValueFloat(CEConst.IDS_P_VHD, 0, per, scen, atdm)
                    * (pctCars * udcCars + pctTrucks * udcTrucks);
            tempVehicleOperatingCost = seed.getValueFloat(CEConst.IDS_P_VHD, 0, per, scen, atdm)
                    * (pctCars * vocCars + pctTrucks * vocTrucks);
            userDelayCost += tempUserDelayCost;
            vehicleOperatingCost += tempVehicleOperatingCost;
            currTime = CETime.addTime(currTime, new CETime(0, 15));
            tempMaxHourlyUserCost += tempUserDelayCost;
            tempMaxHourlyUserCost += tempVehicleOperatingCost;
            if (currTime.minute == 0) {
                if (tempMaxHourlyUserCost > maxHourlyUserCost) {
                    maxHourlyUserCost = tempMaxHourlyUserCost;
                    maxHourlyUserCostSring = (currTime.hour - 1 > 0 ? String.valueOf(currTime.hour - 1) : String.valueOf(24 + (currTime.hour - 1)))
                            + ":" + formatter.format(currTime.minute) + "-" + currTime.toString();
                }
                tempMaxHourlyUserCost = 0.0f;
            }
        }
        userCost = userDelayCost + vehicleOperatingCost;
    }

    /**
     * Recalculates the summary information from the underlying seed instance.
     */
    public void reload() {
        totalLength = seed.getValueFloat(CEConst.IDS_TOTAL_LENGTH_MI);
        actualTravelTime = seed.getValueFloat(CEConst.IDS_CB_SP_ACTUAL_TIME, 0, 0, scen, atdm);
        VMTD = seed.getValueFloat(CEConst.IDS_CB_SP_VMTD, 0, 0, scen, atdm);
        VMTV = seed.getValueFloat(CEConst.IDS_CB_SP_VMTV, 0, 0, scen, atdm);
        PMTD = seed.getValueFloat(CEConst.IDS_CB_SP_PMTD, 0, 0, scen, atdm);
        PMTV = seed.getValueFloat(CEConst.IDS_CB_SP_PMTV, 0, 0, scen, atdm);
        VHT = seed.getValueFloat(CEConst.IDS_CB_SP_VHT, 0, 0, scen, atdm);
        VHD = seed.getValueFloat(CEConst.IDS_CB_SP_VHD, 0, 0, scen, atdm);
        spaceMeanSpeed = seed.getValueFloat(CEConst.IDS_CB_SP_SPACE_MEAN_SPEED, 0, 0, scen, atdm);
        reportDensity = seed.getValueFloat(CEConst.IDS_CB_SP_REPORT_DENSITY_PC, 0, 0, scen, atdm);
        maxDC = seed.getValueFloat(CEConst.IDS_CB_SP_MAX_DC, 0, 0, scen, atdm);
        maxVC = seed.getValueFloat(CEConst.IDS_CB_SP_MAX_VC, 0, 0, scen, atdm);
        maxHourlyUserCost = -1.0f;
        DecimalFormat formatter = new DecimalFormat("00");
        maxHourlyUserCostSring = "";

        userDelayCost = 0;
        vehicleOperatingCost = 0;
        float pctTrucks = 0.0f;
        float pctCars;
        float tempUserDelayCost = 0.0f;
        float tempVehicleOperatingCost = 0.0f;
        CETime currTime = seed.getStartTime();
        float tempMaxHourlyUserCost = 0.0f;
        for (int per = 0; per < seed.getValueInt(CEConst.IDS_NUM_PERIOD); per++) {
            for (int seg = 0; seg < seed.getValueInt(CEConst.IDS_NUM_SEGMENT); seg++) {
                pctTrucks += seed.getValueFloat(CEConst.IDS_TRUCK_SINGLE_UNIT_PCT_MAINLINE, seg, per, scen, atdm)
                        + seed.getValueFloat(CEConst.IDS_TRUCK_TRAILER_PCT_MAINLINE, seg, per, scen, atdm);
            }
            pctTrucks /= seed.getValueInt(CEConst.IDS_NUM_SEGMENT);
            pctTrucks /= 100.0f;
            pctCars = 1.0f - pctTrucks;
            tempUserDelayCost = seed.getValueFloat(CEConst.IDS_P_VHD, 0, per, scen, atdm)
                    * (pctCars * udcCars + pctTrucks * udcTrucks);
            tempVehicleOperatingCost = seed.getValueFloat(CEConst.IDS_P_VHD, 0, per, scen, atdm)
                    * (pctCars * vocCars + pctTrucks * vocTrucks);
            userDelayCost += tempUserDelayCost;
            vehicleOperatingCost += tempVehicleOperatingCost;
            currTime = CETime.addTime(currTime, new CETime(0, 15));
            tempMaxHourlyUserCost += tempUserDelayCost;
            tempMaxHourlyUserCost += tempVehicleOperatingCost;
            if (currTime.minute == 0) {
                if (tempMaxHourlyUserCost > maxHourlyUserCost) {
                    maxHourlyUserCost = tempMaxHourlyUserCost;
                    maxHourlyUserCostSring = (currTime.hour - 1 > 0 ? String.valueOf(currTime.hour - 1) : String.valueOf(24 + (currTime.hour - 1)))
                            + ":" + formatter.format(currTime.minute) + "-" + currTime.toString();
                }
                tempMaxHourlyUserCost = 0.0f;
            }
        }
        userCost = userDelayCost + vehicleOperatingCost;
    }

    /**
     * Getter for the User Delay Cost for Cars Value.
     *
     * @return User Delay Cost for Cars in dollars.
     */
    public static float getUdcCars() {
        return udcCars;
    }

    /**
     * Setter for the User Delay Cost for Cars Value.
     *
     * @param udcCars New User Delay Cost for Cars in dollars.
     */
    public static void setUdcCars(float udcCars) {
        FacilitySummary.udcCars = udcCars;
    }

    /**
     * Getter for the User Delay Cost for Trucks Value.
     *
     * @return User Delay Cost for Trucks in dollars.
     */
    public static float getUdcTrucks() {
        return udcTrucks;
    }

    /**
     * Setter for the User Delay Cost for Trucks Value.
     *
     * @param udcTrucks New User Delay Cost for Trucks in dollars.
     */
    public static void setUdcTrucks(float udcTrucks) {
        FacilitySummary.udcTrucks = udcTrucks;
    }

    /**
     * Getter for the Vehicle Operating Cost for Cars Value.
     *
     * @return Vehicle Operating Cost for Cars in dollars.
     */
    public static float getVocCars() {
        return vocCars;
    }

    /**
     * Setter for the Vehicle Operating Cost for Cars Value.
     *
     * @param vocCars New Vehicle Operating Cost for Cars in dollars.
     */
    public static void setVocCars(float vocCars) {
        FacilitySummary.vocCars = vocCars;
    }

    /**
     * Getter for the Vehicle Operating Cost for Trucks Value.
     *
     * @return Vehicle Operating Cost for Trucks in dollars.
     */
    public static float getVocTrucks() {
        return vocTrucks;
    }

    /**
     * Setter for the Vehicle Operating Cost for Trucks Value.
     *
     * @param vocTrucks New Vehicle Operating Cost for Trucks in dollars.
     */
    public static void setVocTrucks(float vocTrucks) {
        FacilitySummary.vocTrucks = vocTrucks;
    }

}
