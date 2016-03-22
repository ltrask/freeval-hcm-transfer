package coreEngine.reliabilityAnalysis.DataStruct;

import coreEngine.Helper.CEDate;
import coreEngine.Seed;
import javax.swing.JOptionPane;

/**
 *
 * @author Lake and tristan
 */
public class IncidentData {

    /**
     * 2D Float Array of Incident Probabilities.
     */
    private final float[][] incidentProbabilities;

    /**
     * Float Array incident frequencies for each month.
     */
    private final float[] incidentFreqMonth;  // Total number of incidents in a month

    /**
     * Incident Distribution by incident type.
     */
    private final float[] incidentDistribution;

    /**
     * 2D Float Array containing the incident duration information.
     */
    private final float[][] incidentDurationInfo;

    /**
     * 2D Array of Incident Free Flow Speed Adjustment Factors based on the
     * incident severity (first index) and the number of lanes in a segment
     * (second index).
     */
    private final float[][] incidentFFSAFs;

    /**
     * 2D Array of Incident Capacity Adjustment Factors based on the incident
     * severity (first index) and the number of lanes in a segment (second
     * index).
     */
    private final float[][] incidentCAFs;

    /**
     * 2D Array of Incident Demand (Destination and Origin) Adjustment Factors
     * based on the incident severity (first index) and the number of lanes in a
     * segment (second index).
     */
    private final float[][] incidentDAFs;

    /**
     * 2D Array of Incident Lane Adjustment Factors based on the incident
     * severity (first index) and the number of lanes in a segment (second
     * index).
     */
    private final int[][] incidentLAFs;

    /**
     * Crash Rate Ratio Value.
     */
    private float crashRateRatio;

    /**
     * Percentage of AADT.
     */
    private float pctOfAADT;

    /**
     * Seed instance with which the IncidentData is associated.
     */
    private Seed seed;

    /**
     * General Purpose (TYPE_GP) vs Managed Lane (TYPE_ML).
     */
    private final int modelType;

    /**
     * General Purpose Lanes Identifier.
     */
    public final static int TYPE_GP = 0;

    /**
     * Managed Lanes Identifier.
     */
    public final static int TYPE_ML = 1;

    /**
     * Constructor to create and IncidentData object for use in a HCM
     * Reliability Analysis. This object holds information about incident
     * durations, frequencies, distributions, and the resulting adjustment
     * factors applied when an incident occurs.
     *
     * @param type General Purpose (TYPE_GP) or Managed Lanes (TYPE_ML).
     */
    public IncidentData(int type) {
        if (type == TYPE_GP || type == TYPE_ML) {
            this.modelType = type;
        } else {
            throw new RuntimeException("Invalid IncidentData type.");
        }

        incidentProbabilities = new float[12][6];
        incidentFreqMonth = new float[12];

        incidentDistribution = new float[5];
        incidentDurationInfo = new float[5][4];

        // 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 - 3 lane closure, 4 - 4 lane closure
        incidentFFSAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes
        incidentCAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes
        incidentDAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes
        incidentLAFs = new int[5][7]; // 5 is incident type, 7 is number of lanes

        crashRateRatio = 4.9f;
        pctOfAADT = 25.0f;

        useDefaultFrequencies();
        if (type == TYPE_GP) {
            useNationalDefaultDistribution();
            useDefaultAdjFactors();
        } else {
            useMLDefaultDistribution();
            useMLDefaultAdjFactors();
        }

    }

    /**
     * Constructor to create and IncidentData object for use in a HCM
     * Reliability Analysis. This object holds information about incident
     * durations, frequencies, distributions, and the resulting adjustment
     * factors applied when an incident occurs.
     *
     * @param seed Seed facility with which the data is associated.
     * @param type General Purpose (TYPE_GP) or Managed Lanes (TYPE_ML).
     */
    public IncidentData(Seed seed, int type) {

        if (type == TYPE_GP || type == TYPE_ML) {
            this.modelType = type;
        } else {
            throw new RuntimeException("Invalid IncidentData type.");
        }

        this.seed = seed;
        incidentProbabilities = new float[12][6];

        // Reading in data from seed (if data exists)
        incidentFreqMonth = new float[12];
        if (type == TYPE_GP) {
            setIncidentFrequency(seed.getGPIncidentFrequency());

            if (seed.getGPIncidentDistribution() == null) {
                incidentDistribution = new float[5];
                useNationalDefaultDistribution();
            } else {
                incidentDistribution = new float[5];
                useSeedFileDistribution();
            }
            if (seed.getGPIncidentDuration() == null) {
                incidentDurationInfo = new float[5][4];
                useDefaultDuration();
            } else {
                incidentDurationInfo = new float[5][4];
                useSeedFileDurations();
            }

            // 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 - 3 lane closure, 4 - 4 lane closure
            if (seed.getGPIncidentSAF() == null) {
                incidentFFSAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(0);
            } else {
                incidentFFSAFs = seed.getGPIncidentSAF();
            }
            if (seed.getGPIncidentCAF() == null) {
                incidentCAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(1);
            } else {
                incidentCAFs = seed.getGPIncidentCAF();
            }
            if (seed.getGPIncidentDAF() == null) {
                incidentDAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(2);
            } else {
                incidentDAFs = seed.getGPIncidentDAF();
            }
            if (seed.getGPIncidentLAF() == null) {
                incidentLAFs = new int[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(3);
            } else {
                incidentLAFs = seed.getGPIncidentLAF();
            }

            if (seed.getGPIncidentCrashRatio() == 0.0f) {
                crashRateRatio = 4.9f; // National default
            } else {
                crashRateRatio = seed.getGPIncidentCrashRatio();
            }
        } else {
            setIncidentFrequency(seed.getMLIncidentFrequency());

            if (seed.getMLIncidentDistribution() == null) {
                incidentDistribution = new float[5];
                useMLDefaultDistribution();
            } else {
                incidentDistribution = seed.getMLIncidentDistribution();
            }
            if (seed.getMLIncidentDuration() == null) {
                incidentDurationInfo = new float[5][4];
                useDefaultDuration();
            } else {
                incidentDurationInfo = seed.getMLIncidentDuration();
            }

            // 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 - 3 lane closure, 4 - 4 lane closure
            if (seed.getMLIncidentSAF() == null) {
                incidentFFSAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(0);
            } else {
                incidentFFSAFs = seed.getMLIncidentSAF();
            }
            if (seed.getMLIncidentCAF() == null) {
                incidentCAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(1);
            } else {
                incidentCAFs = seed.getMLIncidentCAF();
            }
            if (seed.getMLIncidentDAF() == null) {
                incidentDAFs = new float[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(2);
            } else {
                incidentDAFs = seed.getMLIncidentDAF();
            }
            if (seed.getMLIncidentLAF() == null) {
                incidentLAFs = new int[5][7]; // 5 is incident type, 7 is number of lanes (2 lanes to 8 lanes)
                useDefaultAdjFactors(3);
            } else {
                incidentLAFs = seed.getMLIncidentLAF();
            }

            if (seed.getMLIncidentCrashRatio() == 0.0f) {
                crashRateRatio = 4.9f; // National default
            } else {
                crashRateRatio = seed.getMLIncidentCrashRatio();
            }
        }

        if (type == TYPE_GP) {
            //useNationalDefaultDistribution();
            //useDefaultAdjFactors();
        } else {
            //useMLDefaultDistribution();
            //useMLDefaultAdjFactors();
        }

    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get method for the crash rate ratio for incident scenario generation.
     *
     * @return float crashRateRatio
     */
    public float getCrashRateRatio() {
        return crashRateRatio;
    }

    /**
     * Get method for Percent of AADT Parameter for the HERS Model for the
     * IncidentData instance. generation
     *
     * @return float crashRateRatio
     */
    public float getPercentOfAADT() {
        return pctOfAADT;
    }

    /**
     * Getter for the incident probabilities (deprecated).
     *
     * @param incidentType
     * @param month
     * @return
     */
    public float getIncidentProbability(int incidentType, int month) {
        return incidentProbabilities[month][incidentType];
    }

    /**
     * Getter for the expected frequency of incidents in a given month.
     *
     * @param month 0 - January, 1 - February, etc.
     * @return
     */
    public float getIncidentFrequencyMonth(int month) {
        return incidentFreqMonth[month];
    }

    /**
     * Getter for the array of Incident Frequencies.
     *
     * @return Float array of incident frequencies.
     */
    public float[] getIncidentFrequencyArr() {
        return incidentFreqMonth;
    }

    /**
     * Getter for the distribution value of a particular incident type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDistribution(int incidentType) {
        return incidentDistribution[incidentType];
    }

    /**
     * Getter for the decimal value of the distribution for a specific incident
     * type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDistributionDecimal(int incidentType) {
        return incidentDistribution[incidentType] / 100.0f;
    }

    /**
     * Getter for the array of incident distributions.
     *
     * @return
     */
    public float[] getIncidentDistribution() {
        return incidentDistribution;
    }

    /**
     * Getter for the minimum incident duration of a specific incident type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDurMin(int incidentType) {
        return incidentDurationInfo[incidentType][2];
    }

    /**
     * Getter for the maximum incident duration of a specific incident type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDurMax(int incidentType) {
        return incidentDurationInfo[incidentType][3];
    }

    /**
     * Getter to check that the sum of the incident distribution is 100%.
     *
     * @return Sum of the incident distribution array.
     */
    public float getIncidentDistributionSum() {
        float sum = 0.0f;
        for (int i = 0; i < incidentDistribution.length; i++) {
            sum += incidentDistribution[i];
        }

        return sum;
    }

    /**
     * Getter for the Average incident duration of a specific incident type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDuration(int incidentType) {
        return incidentDurationInfo[incidentType][0];
    }

    /**
     * Getter for the full incident duration array.
     *
     * @return
     */
    public float[][] getIncidentDurationInfo() {
        return incidentDurationInfo;
    }

    /**
     * Getter for the incident duration standard deviation of a specific
     * incident type.
     *
     * @param incidentType
     * @return
     */
    public float getIncidentDurationStdDev(int incidentType) {
        return incidentDurationInfo[incidentType][1];
    }

    /**
     * Getter for the adjustment factor for a specific incident type.
     *
     * @param adjFacType 0 - FFSAF, 1 - CAF, 2 - DAF, 3 - LAF
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment in which the incident
     * occurs.
     * @return
     */
    public float getIncidentAdjFactor(int adjFacType, int incType, int numLanes) {
        switch (adjFacType) {
            case 0: // FFSAF
                return getIncidentFFSAF(incType, numLanes);
            case 1: // CAF
                return getIncidentCAF(incType, numLanes);
            case 2: // DAF
                return getIncidentDAF(incType, numLanes);
            case 3: // LAF - cast int to float
                return (float) getIncidentLAF(incType, numLanes);
            default:
                return -1.0f;
        }
    }

    /**
     * Getter for an incident type's free flow speed adjustment factor.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment in which the incident
     * occurs.
     * @return Float adjustment factor.
     */
    public float getIncidentFFSAF(int incType, int numLanes) {
        return incidentFFSAFs[incType][numLanes];
    }

    /**
     * Get method to return the float[][] of incident FFSAFs
     *
     * @return float[][] incidentFFSAFs
     */
    public float[][] getIncidentFFSAF() {
        return incidentFFSAFs;
    }

    /**
     * Getter for an incident type's capacity adjustment factor.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment in which the incident
     * occurs.
     * @return Float adjustment factor.
     */
    public float getIncidentCAF(int incType, int numLanes) {
        return incidentCAFs[incType][numLanes];
    }

    /**
     * Get method to return the float[][] of incident CAFs
     *
     * @return float[][] incidentCAFs
     */
    public float[][] getIncidentCAF() {
        return incidentCAFs;
    }

    /**
     * Getter for an incident type's demand (destination and origin) adjustment
     * factor.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment in which the incident
     * occurs.
     * @return Float adjustment factor.
     */
    public float getIncidentDAF(int incType, int numLanes) {
        return incidentDAFs[incType][numLanes];
    }

    /**
     * Get method to return the float[][] of incident DAFs
     *
     * @return float[][] incidentDAFs
     */
    public float[][] getIncidentDAF() {
        return incidentDAFs;
    }

    /**
     * Getter for an incident type's lane adjustment factor.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment in which the incident
     * occurs.
     * @return Float adjustment factor.
     */
    public int getIncidentLAF(int incType, int numLanes) {
        return incidentLAFs[incType][numLanes];
    }

    /**
     * Get method to get the int[][] array of incident LAFs.
     *
     * @return int[][] incident LAFs
     */
    public int[][] getIncidentLAF() {
        return incidentLAFs;
    }

    /**
     * Getter for the String name of an incident type (for display purposes).
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @return String name of an incident type.
     */
    public static String getIncidentTypeFull(int incType) {
        switch (incType) {
            case 0:
                return "Shoulder closure";
            case 1:
                return "One lane closure";
            case 2:
                return "Two lane closure";
            case 3:
                return "Three lane closure";
            default:
                return "Four or more lane closure";
        }
    }

    /**
     * Extracts any saved incident frequencies from the seed file.
     */
    public void useFrequenciesFromSeed() {
        if (this.seed != null) {
            float[] tempFreqArr;
            if (modelType == TYPE_GP) {
                tempFreqArr = seed.getGPIncidentFrequency();
            } else {
                tempFreqArr = seed.getMLIncidentFrequency();
            }
            if (tempFreqArr != null) {
                for (int month = 0; month < tempFreqArr.length; month++) {
                    this.incidentFreqMonth[month] = tempFreqArr[month];
                    //System.out.println(incidentFreqMonth[month]);
                }
            } else {
                System.out.println("Seed frequency array is null");
            }
        } else {
            System.out.println("Seed is null");
        }
    }

    /**
     * Returns the model type of the object (general purpose vs managed lanes).
     *
     * @return
     */
    public int getModelType() {
        return modelType;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Setter for the crash rate ratio value.
     *
     * @param newVal new crash rate ratio.
     */
    public void setCrashRateRatio(float newVal) {
        this.crashRateRatio = newVal;
    }

    /**
     * Setter for the percentage of the AADT to be use by the HERS method.
     *
     * @param newVal Percentage of AADT.
     */
    public void setPercentOfAADT(float newVal) {
        this.pctOfAADT = newVal;
    }

    /**
     * Setter for the probability of a specific incident type for a given demand
     * pattern.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure
     * @param demandPattern
     * @param newValue new Incident Probability.
     */
    public void setIncidentProbability(int incidentType, int demandPattern, float newValue) {
        incidentProbabilities[demandPattern][incidentType] = newValue;
    }

    /**
     * Setter for the incident frequency of a particular month.
     *
     * @param month 0 - Jan, 1 - Feb, etc.
     * @param newValue new Incident Frequency.
     */
    public void setIncidentFrequencyMonth(int month, float newValue) {
        incidentFreqMonth[month] = newValue;
    }

    /**
     * Setter for the entire incident frequency array.
     *
     * @param incidentFrequency Incident frequency for each month of the year.
     * The array should be of length 12.
     */
    public void setIncidentFrequency(float[] incidentFrequency) {
        if (incidentFrequency != null) {
            for (int month = 0; month < incidentFrequency.length; month++) {
                this.incidentFreqMonth[month] = incidentFrequency[month];
            }
        }
    }

    /**
     * Setter for the incident distribution value for a specific incident type.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure
     * @param newValue incident distribution value.
     */
    public void setIncidentDistribution(int incidentType, float newValue) {
        incidentDistribution[incidentType] = newValue;
    }

    /**
     * Setter for the minimum incident duration for a specific type.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure.
     * @param newValue new duration value.
     */
    public void setIncidentDurMin(int incidentType, float newValue) {
        incidentDurationInfo[incidentType][2] = newValue;
    }

    /**
     * Setter for the maximum incident duration for a specific type.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure.
     * @param newValue new duration value.
     */
    public void setIncidentDurMax(int incidentType, float newValue) {
        incidentDurationInfo[incidentType][3] = newValue;
    }

    /**
     * Setter for the average incident duration for a specific type.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure.
     * @param newValue new duration value.
     */
    public void setIncidentDuration(int incidentType, float newValue) {
        incidentDurationInfo[incidentType][0] = newValue;
    }

    /**
     * Setter for the incident duration standard deviation for a specific type.
     *
     * @param incidentType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure,
     * 3 - 3 lane closure, 4 - 4 lane closure.
     * @param newValue new duration value.
     */
    public void setIncidentDurationStdDev(int incidentType, float newValue) {
        incidentDurationInfo[incidentType][1] = newValue;
    }

    /**
     * Setter for the adjustment factor a specific incident type. The adjustment
     * factor is based on the severity of the incident (incident type) and the
     * number of lanes of the segment in which the incident occurs.
     *
     * @param adjFacType 0 - FFSAF, 1 - CAF, 2 - DAF, 3 - LAF
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure
     * @param numLanes Number of lanes of the segment.
     * @param newVal Incident Adjustment Factor.
     */
    public void setIncidentAdjFactor(int adjFacType, int incType, int numLanes, float newVal) {
        switch (adjFacType) {
            case 0: // FFSAF
                setIncidentFFSAF(incType, numLanes, newVal);
                break;
            case 1: // CAF
                setIncidentCAF(incType, numLanes, newVal);
                break;
            case 2: // DAF
                setIncidentDAF(incType, numLanes, newVal);
                break;
            case 3: // LAF - cast int to float
                setIncidentLAF(incType, numLanes, (int) newVal);
                break;
            default:
                System.err.println("Invalid adjustment factor type specified. Value not set.");
                break;

        }
    }

    /**
     * Setter for the Free Flow Speed Adjustment factor for a specific incident
     * type. The value is also dependent on the number of lanes of the segment
     * in which it occurs.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure.
     * @param numLanes Number of Lanes.
     * @param newVal new Adjustment Factor Value.
     */
    public void setIncidentFFSAF(int incType, int numLanes, float newVal) {
        incidentFFSAFs[incType][numLanes] = newVal;
    }

    /**
     * Setter for the Capacity Adjustment factor for a specific incident type.
     * The value is also dependent on the number of lanes of the segment in
     * which it occurs.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure.
     * @param numLanes Number of Lanes.
     * @param newVal new Adjustment Factor Value.
     */
    public void setIncidentCAF(int incType, int numLanes, float newVal) {
        incidentCAFs[incType][numLanes] = newVal;
    }

    /**
     * Setter for the Demand (Destination and Origin) Adjustment factor for a
     * specific incident type. The value is also dependent on the number of
     * lanes of the segment in which it occurs.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure.
     * @param numLanes Number of Lanes.
     * @param newVal new Adjustment Factor Value.
     */
    public void setIncidentDAF(int incType, int numLanes, float newVal) {
        incidentDAFs[incType][numLanes] = newVal;
    }

    /**
     * Setter for the Lane Adjustment factor for a specific incident type. The
     * value is also dependent on the number of lanes of the segment in which it
     * occurs.
     *
     * @param incType 0 - Shoulder, 1 - 1 lane closure, 2 - 2 lane closure, 3 -
     * 3 lane closure, 4 - 4 lane closure.
     * @param numLanes Number of Lanes.
     * @param newVal new Adjustment Factor Value.
     */
    public void setIncidentLAF(int incType, int numLanes, int newVal) {
        incidentLAFs[incType][numLanes] = newVal;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Fill presets">
    /**
     * Default (0.0) incident frequency values.
     */
    public void useDefaultFrequencies() {
        incidentFreqMonth[0] = 0.0f;
        incidentFreqMonth[1] = 0.0f;
        incidentFreqMonth[2] = 0.0f;
        incidentFreqMonth[3] = 0.0f;
        incidentFreqMonth[4] = 0.0f;
        incidentFreqMonth[5] = 0.0f;
        incidentFreqMonth[6] = 0.0f;
        incidentFreqMonth[7] = 0.0f;
        incidentFreqMonth[8] = 0.0f;
        incidentFreqMonth[9] = 0.0f;
        incidentFreqMonth[10] = 0.0f;
        incidentFreqMonth[11] = 0.0f;
    }

    /**
     * Old I-40 NC default incident frequencies.
     */
    public void useOldI40DefaultFrequencies() {
        incidentFreqMonth[0] = 3.267957f;
        incidentFreqMonth[1] = 3.38f;
        incidentFreqMonth[2] = 3.67f;
        incidentFreqMonth[3] = 3.89f;
        incidentFreqMonth[4] = 3.88f;
        incidentFreqMonth[5] = 4.07f;
        incidentFreqMonth[6] = 4.51f;
        incidentFreqMonth[7] = 4.11f;
        incidentFreqMonth[8] = 4.24f;
        incidentFreqMonth[9] = 3.97f;
        incidentFreqMonth[10] = 3.97f;
        incidentFreqMonth[11] = 3.90f;
    }

    /**
     * Default Incident Distribution.
     */
    public void useDefaultDistribution() {

        // Setting default distribution
        incidentDistribution[0] = 75.0f;
        incidentDistribution[1] = 20.0f;
        incidentDistribution[2] = 5.0f;
        incidentDistribution[3] = 0.0f;
        incidentDistribution[4] = 0.0f;

        // Setting default durations and duration standard deviations
        //useDefaultDuration();
    }

    /**
     * Extracts incident distribution from the associated seed file.
     */
    public void useSeedFileDistribution() {
        // Setting distribution from values stored in Seed file
        if (seed.getGPIncidentDistribution() != null) {
            incidentDistribution[0] = seed.getGPIncidentDistribution()[0];
            incidentDistribution[1] = seed.getGPIncidentDistribution()[1];
            incidentDistribution[2] = seed.getGPIncidentDistribution()[2];
            incidentDistribution[3] = seed.getGPIncidentDistribution()[3];
            incidentDistribution[4] = seed.getGPIncidentDistribution()[4];
        } else {
            JOptionPane.showMessageDialog(null, "<HTML><CENTER>No incident distribution is stored in the current seed file.<br>"
                    + "These values will not exist unless reliability analysis has previously<br>"
                    + "been run on this seed file.",
                    "Error: No Values Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Extracts the incident durations from the associated seed file.
     */
    public void useSeedFileDurations() {
        // Setting distribution from values stored in Seed file
        if (seed.getGPIncidentDistribution() != null) {
            incidentDurationInfo[0][0] = seed.getGPIncidentDuration()[0][0];
            incidentDurationInfo[1][0] = seed.getGPIncidentDuration()[1][0];
            incidentDurationInfo[2][0] = seed.getGPIncidentDuration()[2][0];
            incidentDurationInfo[3][0] = seed.getGPIncidentDuration()[3][0];
            incidentDurationInfo[4][0] = seed.getGPIncidentDuration()[4][0];

            incidentDurationInfo[0][1] = seed.getGPIncidentDuration()[0][1];
            incidentDurationInfo[1][1] = seed.getGPIncidentDuration()[1][1];
            incidentDurationInfo[2][1] = seed.getGPIncidentDuration()[2][1];
            incidentDurationInfo[3][1] = seed.getGPIncidentDuration()[3][1];
            incidentDurationInfo[4][1] = seed.getGPIncidentDuration()[4][1];

            incidentDurationInfo[0][2] = seed.getGPIncidentDuration()[0][2];
            incidentDurationInfo[1][2] = seed.getGPIncidentDuration()[1][2];
            incidentDurationInfo[2][2] = seed.getGPIncidentDuration()[2][2];
            incidentDurationInfo[3][2] = seed.getGPIncidentDuration()[3][2];
            incidentDurationInfo[4][2] = seed.getGPIncidentDuration()[4][2];

            incidentDurationInfo[0][3] = seed.getGPIncidentDuration()[0][3];
            incidentDurationInfo[1][3] = seed.getGPIncidentDuration()[1][3];
            incidentDurationInfo[2][3] = seed.getGPIncidentDuration()[2][3];
            incidentDurationInfo[3][3] = seed.getGPIncidentDuration()[3][3];
            incidentDurationInfo[4][3] = seed.getGPIncidentDuration()[4][3];
        } else {
            JOptionPane.showMessageDialog(null,
                    "<HTML><CENTER>No incident distribution is stored in the current seed file.<br>"
                    + "These values will not exist unless reliability analysis has previously<br>"
                    + "been run on this seed file.",
                    "Error: No Values Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Use the nation default incident distribution.
     */
    public void useNationalDefaultDistribution() {

        // Setting default distribution
        incidentDistribution[0] = 75.4f;
        incidentDistribution[1] = 19.60f;
        incidentDistribution[2] = 3.10f;
        incidentDistribution[3] = 1.90f;
        incidentDistribution[4] = 0.0f;

        // Setting default durations and duration standard deviations
        //useDefaultDuration();
    }

    /**
     * Use the Managed Lanes Default Distribution.
     */
    public void useMLDefaultDistribution() {

        // Setting default distribution
        incidentDistribution[0] = 75.0f;
        incidentDistribution[1] = 25.0f;
        incidentDistribution[2] = 0.0f;
        incidentDistribution[3] = 0.0f;
        incidentDistribution[4] = 0.0f;

        // Setting default durations and duration standard deviations
        //useDefaultDuration();
    }

    /**
     * Use the default incident duration information.
     */
    public void useDefaultDuration() {
        incidentDurationInfo[0][0] = 34.0f;
        incidentDurationInfo[1][0] = 34.6f;
        incidentDurationInfo[2][0] = 53.6f;
        incidentDurationInfo[3][0] = 67.9f;
        incidentDurationInfo[4][0] = 67.9f;

        incidentDurationInfo[0][1] = 15.1f;
        incidentDurationInfo[1][1] = 13.8f;
        incidentDurationInfo[2][1] = 13.9f;
        incidentDurationInfo[3][1] = 21.9f;
        incidentDurationInfo[4][1] = 21.9f;

        incidentDurationInfo[0][2] = 8.7f;
        incidentDurationInfo[1][2] = 16.0f;
        incidentDurationInfo[2][2] = 30.5f;
        incidentDurationInfo[3][2] = 36.0f;
        incidentDurationInfo[4][2] = 36.0f;

        incidentDurationInfo[0][3] = 58.0f;
        incidentDurationInfo[1][3] = 58.2f;
        incidentDurationInfo[2][3] = 66.9f;
        incidentDurationInfo[3][3] = 93.3f;
        incidentDurationInfo[4][3] = 93.3f;

    }

    /**
     * Use the default general purpose incident adjustment factors.
     */
    private void useDefaultAdjFactors() {
        useDefaultFFSAFs();
        useDefaultCAFs();
        useDefaultDAFs();
        useDefaultLAFs();
    }

    /**
     * Use the default managed lanes incident adjustment factors.
     */
    private void useMLDefaultAdjFactors() {
        useMLDefaultFFSAFs();
        useMLDefaultCAFs();
        useMLDefaultDAFs();
        useMLDefaultLAFs();
    }

    /**
     * Method to set default values for a particular adjustment factor type. 0 -
     * SAF (FFSAF), 1 - CAF, 2 - DAF, 3 - LAF
     *
     * @param adjFactor Integer of adjustment factor to fill with default
     * values.
     */
    private void useDefaultAdjFactors(int adjFactor) {
        switch (adjFactor) {
            case 0:
                useDefaultFFSAFs();
                break;
            case 1:
                useDefaultCAFs();
                break;
            case 2:
                useDefaultDAFs();
                break;
            case 3:
                useDefaultLAFs();
                break;
            default:
                System.err.println("Invalid Adjustment Factor type specified.");
                break;
        }
    }

    /**
     * Use the default general purpose incident free flow speed adjustment
     * factors.
     */
    private void useDefaultFFSAFs() {
        float[][] tempFFSAFs = {{1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f}
        };
        //System.out.println(incidentFFSAFs[0].length);
        //System.out.println(incidentFFSAFs.length);
        for (int incType = 0; incType < incidentFFSAFs.length; incType++) {
            for (int lane = 0; lane < incidentFFSAFs[0].length; lane++) {
                //System.out.println("Lane: "+lane+"   incType:" +incType);
                incidentFFSAFs[incType][lane] = tempFFSAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default general purpose incident capacity adjustment factors.
     */
    private void useDefaultCAFs() {
        float[][] tempCAFs = {{0.81f, 0.70f, 0.70f, 0.70f, 0.70f},
        {0.83f, 0.74f, 0.51f, 0.51f, 0.51f},
        {0.85f, 0.77f, 0.50f, 0.52f, 0.52f},
        {0.87f, 0.81f, 0.67f, 0.50f, 0.50f},
        {0.89f, 0.85f, 0.75f, 0.52f, 0.52f},
        {0.91f, 0.88f, 0.80f, 0.63f, 0.63f},
        {0.93f, 0.89f, 0.84f, 0.66f, 0.66f}
        };

        for (int incType = 0; incType < incidentCAFs.length; incType++) {
            for (int lane = 0; lane < incidentCAFs[0].length; lane++) {
                incidentCAFs[incType][lane] = tempCAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default general purpose incident demand (destination and origin)
     * adjustment factors.
     */
    private void useDefaultDAFs() {
        float[][] tempDAFs = {{1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f}
        };

        for (int incType = 0; incType < incidentDAFs.length; incType++) {
            for (int lane = 0; lane < incidentDAFs[0].length; lane++) {
                incidentDAFs[incType][lane] = tempDAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default general purpose incident lane adjustment factors.
     */
    private void useDefaultLAFs() {
        int[][] tempLAFs = {{0, -1, -1, -1, -1},
        {0, -1, -2, -2, -2},
        {0, -1, -2, -3, -3},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4}
        };
//
//        int[][] tempLAFs =     {{0,-1,-2,-2,-2},
//                                {0,-1,-2,-3,-3},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4}
//                               };

//        int[][] tempLAFs =     {{0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0}
//                               };
        for (int incType = 0; incType < incidentLAFs.length; incType++) {
            for (int lane = 0; lane < incidentLAFs[0].length; lane++) {
                incidentLAFs[incType][lane] = tempLAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default managed lane incident free flow speed adjustment factors.
     */
    private void useMLDefaultFFSAFs() {
        float[][] tempFFSAFs = {{1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f}
        };
        //System.out.println(incidentFFSAFs[0].length);
        //System.out.println(incidentFFSAFs.length);
        for (int incType = 0; incType < incidentFFSAFs.length; incType++) {
            for (int lane = 0; lane < incidentFFSAFs[0].length; lane++) {
                //System.out.println("Lane: "+lane+"   incType:" +incType);
                incidentFFSAFs[incType][lane] = tempFFSAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default managed lane incident capacity adjustment factors.
     */
    private void useMLDefaultCAFs() {
        float[][] tempCAFs = {{0.81f, 0.70f, 0.70f, 0.70f, 0.70f},
        {0.83f, 0.74f, 0.51f, 0.51f, 0.51f},
        {0.85f, 0.77f, 0.50f, 0.52f, 0.52f},
        {0.87f, 0.81f, 0.67f, 0.50f, 0.50f},
        {0.89f, 0.85f, 0.75f, 0.52f, 0.52f},
        {0.91f, 0.88f, 0.80f, 0.63f, 0.63f},
        {0.93f, 0.89f, 0.84f, 0.66f, 0.66f}
        };

        for (int incType = 0; incType < incidentCAFs.length; incType++) {
            for (int lane = 0; lane < incidentCAFs[0].length; lane++) {
                incidentCAFs[incType][lane] = tempCAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default managed lane incident demand (destination and origin)
     * adjustment factors.
     */
    private void useMLDefaultDAFs() {
        float[][] tempDAFs = {{1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f}
        };

        for (int incType = 0; incType < incidentDAFs.length; incType++) {
            for (int lane = 0; lane < incidentDAFs[0].length; lane++) {
                incidentDAFs[incType][lane] = tempDAFs[lane][incType];
            }
        }
    }

    /**
     * Use the default managed lane incident lane adjustment factors.
     */
    private void useMLDefaultLAFs() {
        int[][] tempLAFs = {{0, -1, -1, -1, -1},
        {0, -1, -2, -2, -2},
        {0, -1, -2, -3, -3},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4},
        {0, -1, -2, -3, -4}
        };
//
//        int[][] tempLAFs =     {{0,-1,-2,-2,-2},
//                                {0,-1,-2,-3,-3},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4},
//                                {0,-1,-2,-3,-4}
//                               };

//        int[][] tempLAFs =     {{0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0},
//                                {0,0,0,0,0}
//                               };
        for (int incType = 0; incType < incidentLAFs.length; incType++) {
            for (int lane = 0; lane < incidentLAFs[0].length; lane++) {
                incidentLAFs[incType][lane] = tempLAFs[lane][incType];
            }
        }
    }

    // </editor-fold>
    /**
     * Method to create the weighted average demand multipliers used in
     * calculating the expected frequency of all incidents per study period in
     * month j.
     *
     * @param demandData
     * @return float[12] containing weighted average demand multipliers of each
     * month.
     */
    private float[] createWAvgDemandMultipliers(DemandData demandData) {

        // Initializing variables
        int sumActiveDays;
        float wSumActiveDays;
        float[] wAvgDemandMult = new float[12];

        // Getting array consiting of number of active days per month in analysis period
        int[] activeDaysPerMonthInAp = CEDate.numDayOfWeekInMonthAP(demandData);  // int[84] (12x7, i.e. first 7 are january)
        for (int month = 0; month < 12; month++) {
            sumActiveDays = 0;
            wSumActiveDays = 0.0f;
            for (int day = 0; day < 7; day++) {
                if (demandData.getDayActive(day)) {
                    sumActiveDays += activeDaysPerMonthInAp[month * 7 + day];
                    wSumActiveDays += activeDaysPerMonthInAp[month * 7 + day] * demandData.getValue(month, day);
                }
            }
            //System.out.println(wSumActiveDays);
            //System.out.println(sumActiveDays);
            if (wSumActiveDays == 0.0f || sumActiveDays == 0) {
                wAvgDemandMult[month] = 0.0f;
            } else {
                wAvgDemandMult[month] = wSumActiveDays / sumActiveDays;
            }
            //System.out.println(wAvgDemandMult[month]);
        }

        return wAvgDemandMult;
    }

    /**
     * Calculates the incident frequencies using crash rates, the incident/crash
     * rate ratio, and the underlying demand rate.
     *
     * @param crashRates 12 by 1 array of month crash rates
     * @param demandData Demand data on which the frequencies are based
     * @param isCrashRate True if crash rates, false if incident rates
     */
    public void calcIncidentFrequenciesCR(float[] crashRates, DemandData demandData, boolean isCrashRate) {

        float[] adjCrashRate = new float[12];

        for (int i = 0; i < 12; i++) {
            adjCrashRate[i] = isCrashRate ? crashRates[i] * crashRateRatio : crashRates[i];
        }

        float[] wAvgDemandMult = createWAvgDemandMultipliers(demandData);

        float nj = 0.0f;
        for (int month = 0; month < 12; month++) {
            nj = adjCrashRate[month] * 1e-8f * wAvgDemandMult[month] * demandData.getSeedTotalVMT();
            //System.out.println(nj);
            //System.out.println(crashRate[month]+" "+wAvgDemandMult[month]+" "+demandData.getSeedTotalVMT());
            //System.out.println(nj);
            setIncidentFrequencyMonth(month, nj);
        }
    }
}
