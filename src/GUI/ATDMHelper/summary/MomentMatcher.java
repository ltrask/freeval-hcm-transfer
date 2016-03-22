package GUI.ATDMHelper.summary;

import GUI.major.MainWindow;
import coreEngine.Helper.CEConst;
import coreEngine.Seed;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JOptionPane;

/**
 *
 * @author Lake Trask
 */
public class MomentMatcher {

    private final Seed seed;

    /**
     * Array to hold the probabilities of the sample.
     */
    private final float[] pi;

    /**
     * Array holding the TTI values of the population.
     */
    private final float[] vS;

    private float[] atdmTTI;

    private final int atdmSetIndex;

    /**
     * Array holding the probabilities of the population.
     */
    private float[] pP;

    /**
     * Array holding cumulative probabilities of the population
     */
    //private final float cpP;
    /**
     * Array holding the TTI values of the population.
     */
    private final float[] vP;

    /**
     * Array to hold the computed moments of the population.
     */
    private final float[] populationMoments;

    /**
     * Number of moments to try and match.
     */
    private final int numMoments;

    private final int k;

    /**
     * Equality constraint matrix
     */
    private final float[][] E;

    private final float[] d;

    /**
     * Inequality constraint matrix
     */
    private final float[][] A;

    private final float[] b;

    private int[] sample;

    /**
     *
     * @param seed
     * @param sample
     * @param numMoments
     * @param atdmSetIndex
     */
    public MomentMatcher(Seed seed, int[] sample, int numMoments, int atdmSetIndex) {
        this.seed = seed;
        this.numMoments = numMoments;
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        this.atdmSetIndex = atdmSetIndex;
        this.sample = new int[sample.length * numPeriods];
        for (int i = 0; i < sample.length; i++) {
            for (int j = 0; j < numPeriods; j++) {
                this.sample[i * numPeriods + j] = (sample[i] - 1) * numPeriods + j;
            }
        }
        //printVec(this.sample);

        // TTI values and probabilities of the Poputlation
        this.vP = extractTTIValues(seed);
        this.pP = extractProbabilities(seed);

        // TTI values of the sample and creating array to hold adjusted sample probabilities
        //this.vS = extractTTIValues(seed, sample);
        this.atdmTTI = extractTTIValues(seed, sample, atdmSetIndex);
        this.vS = new float[this.sample.length];
        for (int i = 0; i < vS.length; i++) {
            this.vS[i] = this.vP[this.sample[i]];
        }
        this.pi = new float[vS.length];

        // Populating model
        this.populationMoments = computePopulationMoments();
        String mom1 = "";
        for (int i = 0; i < numMoments; i++) {
            mom1 = mom1 + populationMoments[i] + " ";
        }
        //System.out.println(mom1);

        //<editor-fold defaultstate="collapsed" desc="Deprecated - To be deleted">
        this.k = sample.length;

        // Creating and filling linear inequality and equality constraint arrays
        A = new float[2 * k][k + numMoments];
        b = new float[2 * k];
        E = new float[numMoments + 1][k + numMoments];
        d = new float[numMoments + 1];
        //</editor-fold>

        createConstraints();
    }

    /**
     *
     * @return
     */
    public float[] getPi() {
        return pi;
    }

    /**
     *
     * @return
     */
    public float[] getvS() {
        return vS;
    }

    /**
     *
     * @return
     */
    public float[] getSampleATDMTTI() {
        return this.atdmTTI;
    }

    //<editor-fold defaultstate="collapsed" desc="Value and Probability Extractors">
    private float[] extractTTIValues(Seed seed) {
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        float[] ttiArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN) * numPeriods];

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                ttiArray[(scen - 1) * numPeriods + period] = TTI;

            }
        }

        return ttiArray;
    }

    private float[] extractTTIValues(Seed seed, int[] sample) {
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        float[] ttiArray = new float[sample.length * numPeriods];

        for (int scen = 0; scen < sample.length; scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, sample[scen], -1);
                ttiArray[scen * numPeriods + period] = TTI;

            }
        }

        return ttiArray;
    }

    private float[] extractTTIValues(Seed seed, int[] sample, int atdmSetIndex) {
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        float[] ttiArray = new float[sample.length * numPeriods];

        for (int scen = 0; scen < sample.length; scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, sample[scen], atdmSetIndex);
                ttiArray[scen * numPeriods + period] = TTI;
            }
        }

        return ttiArray;
    }

    private float[] extractProbabilities(Seed seed) {
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        float[] probArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN) * numPeriods];

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < numPeriods; period++) {
                probArray[(scen - 1) * numPeriods + period] = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / numPeriods;
            }
        }

        return probArray;
    }

    private float[] extractProbabilities(Seed seed, int[] sample) {
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);
        float[] probArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN) * numPeriods];

        for (int scen = 0; scen < sample.length; scen++) {
            for (int period = 0; period < numPeriods; period++) {
                probArray[scen * numPeriods + period] = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, sample[scen], -1) / numPeriods;
            }
        }

        return probArray;
    }

    //<editor-fold defaultstate="collapsed" desc="Deprecated">
    private float[] extractTTIValuesDeprecated(Seed seed) {
        float[] ttiArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN)];
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                ttiArray[scen - 1] += TTI / numPeriods;

            }
        }

        return ttiArray;
    }

    private float[] extractTTIValuesDeprecated(Seed seed, int[] sample) {
        float[] ttiArray = new float[sample.length];
        int numPeriods = seed.getValueInt(CEConst.IDS_NUM_PERIOD);

        for (int scen = 0; scen < sample.length; scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, sample[scen], -1);
                ttiArray[scen] += TTI / numPeriods;

            }
        }

        return ttiArray;
    }

    private float[] extractProbabilitiesDeprecated(Seed seed) {
        float[] probArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN)];

        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            probArray[scen - 1] = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1);
        }

        return probArray;
    }

    private float[] extractProbabilitiesDeprecated(Seed seed, int[] sample) {
        float[] probArray = new float[seed.getValueInt(CEConst.IDS_NUM_SCEN)];

        for (int scen = 0; scen < sample.length; scen++) {
            probArray[scen] = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, sample[scen], -1);
        }

        return probArray;
    }
//</editor-fold>

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Moment Computation">
    private static float[] computeMomentArray(int numMoments, float[] prob, float[] val) {
        float[] momentArray = new float[numMoments];
        for (int i = 1; i < numMoments + 1; i++) {
            momentArray[i] = computeMoment(i, prob, val);
        }

        return momentArray;
    }

    /**
     *
     * @param moment
     * @param probArray
     * @param distValues
     * @return
     */
    public static float computeMoment(int moment, float[] probArray, float[] distValues) {
        double val = 0.0f;
        for (int i = 0; i < probArray.length; i++) {
            val += probArray[i] * Math.pow(distValues[i], moment);
        }
        return (float) val;
    }

    private float[] computePopulationMoments() {
        float[] momentArray = new float[numMoments];

        //extract data from seed, and modify probability to match per period
        for (int scen = 1; scen <= seed.getValueInt(CEConst.IDS_NUM_SCEN); scen++) {
            for (int period = 0; period < seed.getValueInt(CEConst.IDS_NUM_PERIOD); period++) {
                float prob = seed.getValueFloat(CEConst.IDS_SCEN_PROB, 0, 0, scen, -1) / seed.getValueInt(CEConst.IDS_NUM_PERIOD);
                float TTI = seed.getValueFloat(CEConst.IDS_P_TTI, 0, period, scen, -1);
                for (int moment = 1; moment <= numMoments; moment++) {
                    momentArray[moment - 1] += prob * ((float) Math.pow(TTI, moment));
                }
            }
        }
        return momentArray;
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Model Creation">>
    private void createConstraints() {

        // Equality constraint arrays
        for (int moment = 0; moment < numMoments; moment++) {
            for (int j = 0; j < k; j++) {
                E[moment][j] = (float) Math.pow(vS[j], moment + 1);
            }
            E[moment][k + moment] = -1.0f;
            d[moment] = 0.0f;
        }
        for (int j = 0; j < k; j++) {
            E[numMoments][j] = 1.0f;
        }
        d[numMoments] = 1.0f;
        // Inequality constraints (multipliers must be between 0 and 1)
        for (int j = 0; j < k; j++) {
            A[j][j] = 1.0f;
            b[j] = 1.0f;
            A[j + k][j] = -1.0f;
            b[j + k] = 0.0f;
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nonlinear Optimization Algorithms - Does not converge correctly">
    /**
     * @deprecated @return
     */
    private float[] quadProgMin() {
        int hn = (k + numMoments);
        //int n = 2*hn;

        // Creating quadratic coefficient matrix P and linear coefficient vector q
        double[][] P = new double[hn][hn];
        double[] q = new double[hn];
        for (int moment = 0; moment < numMoments; moment++) {
            P[k + moment][k + moment] = 1.0f;
            q[k + moment] = -2 * populationMoments[moment];
        }

        return new float[]{0.0f};
    }

    /**
     * @deprecated @param x0
     * @param gamma
     * @param tol
     * @return
     */
    public float[] steepestDescent(float[] x0, float gamma, float tol) {
        int maxIter = 10000;
        int hn = (k + numMoments);
        //int n = 2*hn;

        if (x0 == null) {
            x0 = new float[k];
            for (int i = 0; i < k; i++) {
                x0[i] = 1.0f / k;
            }
        }

        // Creating quadratic coefficient matrix P and linear coefficient vector q
        float[][] P = new float[hn][hn];
        float[] gradPcoeff = new float[hn];
        float[] q = new float[hn];
        for (int moment = 0; moment < numMoments; moment++) {
            P[k + moment][k + moment] = 1.0f;
            q[k + moment] = -2 * populationMoments[moment];
            gradPcoeff[k + moment] = 2.0f;
        }

        float[] prevX = x0;
        float[] currX = nnGradUpdate(x0, grad(x0), gamma, "EG");
        normalize(currX);
        float[] resid = vectorSubtract(currX, prevX);
        int iterationCount = 0;
        while (norm(resid) > tol && iterationCount <= maxIter) {
            prevX = currX.clone();
            currX = nnGradUpdate(prevX, grad(prevX), gamma, "EG");
            normalize(currX);
            //System.out.println(sum(currX));
            if (allGEQ0(currX) == false || allLEQ1(currX) == false) {
                System.out.println("Terminated after " + iterationCount + " iterations. Reached boundary. Residual = " + norm(resid));
                return prevX;
            }
            iterationCount++;
        }
        System.out.println("Num iterations: " + iterationCount);
        System.out.println("1st moment: " + populationMoments[0] + " " + dotProduct(currX, vS));
        System.out.println("1st moment: " + populationMoments[1] + " " + dotProduct(currX, vectorPower(vS, 2)));
        return currX;
    }

    /**
     * @deprecated @param x
     * @param gradient
     * @param step
     * @param type
     * @return
     */
    private float[] nnGradUpdate(float[] x, float[] gradient, float step, String type) {
        if (type.equalsIgnoreCase("EG")) {
            float[] newX = new float[x.length];
            for (int i = 0; i < x.length; i++) {
                newX[i] = x[i] * ((float) Math.exp(-1 * step * gradient[i]));
            }
            return newX;
        } else {
            throw new RuntimeException("Invalid Gradient Update Type");
        }
    }

    /**
     * @deprecated @return
     */
    public float[] nmfqp() {
        //printVec(vS);
        float[] x0 = new float[vS.length];
        Arrays.fill(x0, 1.0f / x0.length);
        //for (int i = 0; i < x0.length; i++) {
        //    x0[i]=((float)Math.random());
        //}
        normalize(x0);
        // creating system pi^t*A*pi + b^t*pi
        float[][] A = new float[vS.length][vS.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                A[i][j] = 2.0f * (vS[i] * vS[j] + (vS[i] * vS[i] * vS[j] * vS[j]));
                //A[i][j] = vS[i]*vS[j];
            }
        }

        float[] b = new float[vS.length];
        for (int i = 0; i < vS.length; i++) {
            b[i] = -2 * (populationMoments[0] * vS[i] + populationMoments[1] * vS[i] * vS[i]);
            //b[i] = -2*(populationMoments[0]*vS[i]);
        }
        //printVec(b);
        float[] prevX = x0.clone();
        //printVec(x0);
        float[] currX = new float[x0.length];
        float prevObj = obj(x0, A, b);
        float[] denomVec = matVecMult(A, prevX);
        //printVec(denomVec);
        for (int i = 0; i < prevX.length; i++) {
            currX[i] = (Math.abs(b[i]) / denomVec[i]) * prevX[i];
        }
        normalize(currX);
        float newObj = obj(currX, A, b);
        float residual = (newObj - prevObj) * (newObj - prevObj);
        int iter = 0;
        int maxIter = 10000;
        //System.out.println(iter+": "+residual);
        while (iter < maxIter) {
            prevX = currX.clone();
            prevObj = newObj;
            denomVec = matVecMult(A, prevX);
            for (int i = 0; i < prevX.length; i++) {
                currX[i] = (Math.abs(b[i]) / denomVec[i]) * prevX[i];
            }
            normalize(currX);
//            printVec(currX);
//            System.out.println("1st moment: "+populationMoments[0]+" "+dotProduct(currX,vS));
//            System.out.println("2nd moment: "+populationMoments[1]+" "+dotProduct(currX,vectorPower(vS,2)));
//            System.out.println(sum(currX));
            newObj = obj(currX, A, b);
            residual = (newObj - prevObj) * (newObj - prevObj);
//            System.out.println("1st moment: "+populationMoments[0]+" "+dotProduct(currX,vS));
//            System.out.println("1st moment: "+populationMoments[1]+" "+dotProduct(currX,vectorPower(vS,2)));
//            System.out.println(iter+": "+residual);
            iter++;
        }
        //printVec(currX);
        System.out.println("1st moment: " + populationMoments[0] + " " + dotProduct(currX, vS));
        System.out.println("2nd moment: " + populationMoments[1] + " " + dotProduct(currX, vectorPower(vS, 2)));
        return currX;

        //int[] sample = new int[] {29,30,2,6,7,124,217,118,157,139};
        //int[] sample = new int[] {29,28,105,69,123,219,200,207,124};
        //int[] sample = new int[] {29,28,105,69,123,219,200,207,124,30,2,6,7,217,118,157,139,80,81,82,83,84,85,86,87,88};
        //int[] sample = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        //MomentMatcher mm = new MomentMatcher(2,sample, seed);
        //mm.nmfqp();
    }

    /**
     * @deprecated @param x
     * @param A
     * @param b
     * @return
     */
    private float obj(float[] x, float[][] A, float[] b) {
        float[] c = vecMatMult(x, A);
        return dotProduct(c, x) + dotProduct(b, x);
    }

    /**
     * @deprecated
     */
    private void kkt() {
        int hn = (2 * k + numMoments);
        int n = 2 * hn;

        // Creating quadratic coefficient matrix P and linear coefficient vector q
        float[][] P = new float[hn][hn];
        float[] q = new float[hn];
        for (int moment = 0; moment < numMoments; moment++) {
            P[2 * k + moment][2 * k + moment] = 1.0f;
            q[2 * k + moment] = -2 * populationMoments[moment];
        }

        // Creating system
        double[][] kktArr = new double[n][n];
        double[][] kktbArr = new double[n][1];
        for (int i = 0; i < hn; i++) {
            for (int j = 0; j < hn; j++) {
                kktArr[i][j] = P[i][j];
                kktArr[hn + i][j] = A[i][j];
                kktArr[i][hn + j] = A[j][i];
            }
            kktbArr[i][0] = -1 * q[i];
            kktbArr[hn + i][0] = b[i];
        }
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Heuristic Method">
    private void setupMMHeuristic() {
        // Reording vP and pP so vP is ascending
        float[] tempvP = vP.clone();
        int[] s1 = argsort(vP);
        Arrays.sort(vP);
        pP = reorder(pP, s1);

        int[] mapper = new int[vP.length];
        for (int i = 0; i < mapper.length; i++) {
            mapper[s1[i]] = i;
        }

        // Reordering vS and sample so vS is ascending
        float[] tempvS = vS.clone();
        int[] s2 = argsort(vS);
        Arrays.sort(vS);
        sample = reorder(sample, s2);
        //System.out.println("here2");
        //System.out.println(sample.length);
        //System.out.println(atdmTTI.length);
        atdmTTI = reorder(atdmTTI, s2);
        //System.out.println("here3");
        //printVec(vS);
        //printVec(sample);
//        for (int i = 0; i < sample.length; i++) {
//            System.out.println(vS[i]+" "+tempvS[s2[i]]+" "+tempvP[sample[i]]);
//        }

        //atdmTTI = extractTTIValues(seed, sample, atdmSetIndex);
        int[] mappedSample = new int[sample.length];
        for (int j = 0; j < sample.length; j++) {
            mappedSample[j] = mapper[sample[j]];
        }

        //printVec(mappedSample);
        //
        int[] s3 = argsort(mappedSample);
        Arrays.sort(mappedSample);
        sample = mappedSample;
        //System.out.println("here4");
        atdmTTI = reorder(atdmTTI, s3);
        //System.out.println("here5");
    }

    /**
     *
     */
    public void MMHeuristic() {
        //printVec(vS);
        //printVec(sample);
        //System.out.println("here1");
        setupMMHeuristic();
        //System.out.println("here");
        //printVec(vS);
        //printVec(sample);

        // Creating initial probability vector
        pi[0] = sum(pP, 0, sample[0]);
        for (int i = 0; i < pi.length - 1; i++) {
            pi[i] = sum(pP, sample[i] + 1, sample[i + 1]);
            //System.out.println(pi[i]+" "+(sample[i]+1)+" "+sample[i+1]);
        }

        float increment = 1.0f / (seed.getValueInt(CEConst.IDS_NUM_SCEN) * seed.getValueInt(CEConst.IDS_NUM_PERIOD)); // Multiply by number of periods
        //System.out.println("here");
        //printVec(pi);

        float theta = 1.0f - sum(pi);
        float currProbAssignment;
        float currMu1;
        float delta;
        float candDelta;
        float minHarmCand = 9999.0f;
        int minHarmIdx = -1;
        boolean probAssigned;
        int iter = 0;
        //System.out.println(iter+": theta - "+theta);
        while (theta > 0.0f) {
            // Calculating amount of probability to be assigned
            currProbAssignment = Math.min(increment, theta);
            theta -= currProbAssignment;
            //currMu1 = dotProduct(pi, vS);
            currMu1 = dotProduct(pi, vS);
            delta = currMu1 - populationMoments[0];
            probAssigned = false;

            // If current mean is greater than desired mean, start at beginning
            if (delta > 0) {
                for (int i = 0; i < pi.length; i++) {
                    pi[i] += increment;
                    //candDelta = dotProduct(pi, vS) - populationMoments[0];
                    candDelta = dotProduct(pi, vS) - populationMoments[0];
                    // If assignment yields improvement, make assignment final
                    if (Math.abs(candDelta) < delta) {
                        probAssigned = true;
                        //System.out.println(iter+": Improvement Found at index: "+i);
                        break;
                    } else { // Keep track of least harmful assignment
                        //System.out.println("Min harm check for idx: "+ i);
                        if (Math.abs(candDelta) - delta < minHarmCand) {
                            minHarmCand = Math.abs(candDelta) - delta;
                            minHarmIdx = i;
                        }
                        // Undo Assignment
                        pi[i] -= increment;
                    }
                }
                if (!probAssigned) {
                    pi[minHarmIdx] += increment;
                    //System.out.println(iter+": Minimum Harm Assignment at index " + minHarmIdx);
                }

                // Else, if current mean is less than the desired mean, start at the end
            } else {
                for (int i = pi.length - 1; i >= 0; i--) {
                    pi[i] += increment;
                    //candDelta = dotProduct(pi, vS) - populationMoments[0];
                    candDelta = dotProduct(pi, vS) - populationMoments[0];
                    // If assignment yields improvement, make assignment final
                    if (Math.abs(candDelta) < Math.abs(delta)) {
                        probAssigned = true;
                        //System.out.println(iter+": Improvement Found at index: "+i);
                        break;
                    } else { // Keep track of least harmful assignment
                        if (Math.abs(candDelta) - Math.abs(delta) < minHarmCand) {
                            minHarmCand = Math.abs(candDelta) - Math.abs(delta);
                            minHarmIdx = i;
                        }
                        // Undo Assignment
                        pi[i] -= increment;
                    }
                }
                if (!probAssigned) {
                    pi[minHarmIdx] += increment;
                    //System.out.println(iter+": Minimum Harm Assignment at index " + minHarmIdx);
                }
            }
            iter++;
            //System.out.println(iter+": theta - "+theta);
        }

        //printVec(pi);
        //MainWindow.printLog("1st moment: " + populationMoments[0] + " " + dotProduct(pi, vS));
        //MainWindow.printLog("2nd moment: " + populationMoments[1] + " " + dotProduct(pi, vectorPower(vS, 2)));
        MainWindow.printLog("1st moment: " + populationMoments[0] + " " + dotProduct(pi, vS));
        MainWindow.printLog("2nd moment: " + populationMoments[1] + " " + dotProduct(pi, vectorPower(vS, 2)));

        float convergePctMean = Math.abs(populationMoments[0] - dotProduct(pi, vS)) / populationMoments[0];
        float convergePctVar = Math.abs(populationMoments[1] - dotProduct(pi, vectorPower(vS, 2))) / populationMoments[1];

        if (convergePctMean > 0.15) {
            String messageString = "<HTML><CENTER>WARNING<br>&nbsp<br>Could not converge to within 15&#37 of the population mean.<br>&nbsp<br>"
                    + "Please consider choosing another ATDM After Set before using this comparison method<br>"
                    + "as the results for this After Set are likely biased.<br>&nbsp<br>"
                    + "Population mean: " + populationMoments[0] + "&nbsp&nbsp&nbsp&nbsp&nbsp Mapped After Set mean: " + dotProduct(pi, vS) + "&nbsp&nbsp&nbsp&nbsp&nbsp&#37 Diff: " + convergePctMean + "<br>"
                    + "Population variance: " + populationMoments[1] + "&nbsp&nbsp&nbsp&nbsp&nbsp Mapped After Set variance: " + dotProduct(pi, vectorPower(vS, 2)) + "&nbsp&nbsp&nbsp&nbsp&nbsp&#37 Diff: " + convergePctVar + "<br>";
            JOptionPane.showMessageDialog(null, messageString, "Warning: Poor Convergence", JOptionPane.WARNING_MESSAGE);
        }

    }

    //<</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Vector Operations">
    private float[] elmtWiseMult(float[] arr1, float[] arr2) {
        float[] newArr = new float[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            newArr[i] = arr1[i] * arr2[i];
        }
        return newArr;
    }

    private float[] vectorSubtract(float[] v1, float[] v2) {
        float[] newVec = new float[v1.length];
        for (int i = 0; i < newVec.length; i++) {
            newVec[i] = v1[i] - v2[i];
        }
        return newVec;
    }

    private float dotProduct(float[] arr1, float[] arr2) {
        float val = 0.0f;
        for (int i = 0; i < arr1.length; i++) {
            val += arr1[i] * arr2[i];
        }
        return val;
    }

    private float[] vectorPower(float[] arr1, int pow) {
        float[] newArr = new float[arr1.length];
        for (int i = 0; i < newArr.length; i++) {
            newArr[i] = (float) Math.pow(arr1[i], pow);
        }
        return newArr;
    }

    private float[] grad(float[] x) {
        float[] gradX = new float[x.length];
        float[] projectedMoments = new float[numMoments];
        for (int i = 0; i < numMoments; i++) {
            projectedMoments[i] = dotProduct(x, vectorPower(vS, i + 1));
        }
        // Caculating gradiant at current x
        float x_in;
        for (int i = 0; i < k; i++) {
            for (int m = 0; m < numMoments; m++) {
                x_in = ((float) Math.pow(vS[i], m + 1));
                gradX[i] += 2 * x[i] * x_in * projectedMoments[m] - 2 * populationMoments[m] * x_in;
            }
        }

        return gradX;
    }

    private float[] scalarVectorMult(float scalar, float[] arr) {
        float[] newArr = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i] * scalar;
        }
        return newArr;
    }

    private boolean allGEQ0(float[] x) {
        for (int i = 0; i < x.length; i++) {
            if (x[i] < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allLEQ1(float[] x) {
        for (int i = 0; i < x.length; i++) {
            if (x[i] > 1) {
                return false;
            }
        }
        return true;
    }

    private float norm(float[] x) {
        float val = 0.0f;
        for (int i = 0; i < x.length; i++) {
            val += x[i] * x[i];
        }
        return val;
    }

    private void normalize(float[] x) {
        float normFactor = sum(x);
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i] / normFactor;
        }
    }

    private float sum(float[] x) {
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        return sum;
    }

    private float sum(float[] vector, int idx1, int idx2) {
        float sum = 0;
        for (int i = idx1; i <= idx2; i++) {
            sum += vector[i];
        }
        return sum;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int[] argsort(final int[] a) {
        return argsort(a, true);
    }

    /**
     *
     * @param a
     * @param ascending
     * @return
     */
    public static int[] argsort(final int[] a, final boolean ascending) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Integer.compare(a[i1], a[i2]);
            }
        });
        return asArray(indexes);
    }

    /**
     *
     * @param a
     * @return
     */
    public static int[] argsort(final float[] a) {
        return argsort(a, true);
    }

    /**
     *
     * @param a
     * @param ascending
     * @return
     */
    public static int[] argsort(final float[] a, final boolean ascending) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Float.compare(a[i1], a[i2]);
            }
        });
        return asArray(indexes);
    }

    /**
     *
     * @param <T>
     * @param a
     * @return
     */
    public static <T extends Number> int[] asArray(final T... a) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i].intValue();
        }
        return b;
    }

    private float[] vecMatMult(float[] x, float[][] A) {
        float[] newVec = new float[A[0].length];
        for (int j = 0; j < newVec.length; j++) {
            newVec[j] = 0;
            for (int i = 0; i < x.length; i++) {
                newVec[j] += x[i] * A[i][j];
            }
        }
        return newVec;
    }

    private float[] matVecMult(float[][] A, float[] x) {
        float[] newVec = new float[A.length];
        for (int j = 0; j < newVec.length; j++) {
            newVec[j] = 0;
            for (int i = 0; i < A[j].length; i++) {
                newVec[j] += x[i] * A[j][i];
            }
        }
        return newVec;
    }

    private void printVec(float[] x) {
        System.out.println("-----------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println("-----------------------");
    }

    private void printVec(int[] x) {
        System.out.println("-----------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println("-----------------------");
    }

    /**
     *
     * @param vector
     * @param newOrder
     * @return
     */
    public int[] reorder(int[] vector, int[] newOrder) {
        int[] newVec = new int[vector.length];
        for (int i = 0; i < newVec.length; i++) {
            newVec[i] = vector[newOrder[i]];
        }
        return newVec;
    }

    /**
     *
     * @param vector
     * @param newOrder
     * @return
     */
    public float[] reorder(float[] vector, int[] newOrder) {
        float[] newVec = new float[vector.length];
        for (int i = 0; i < newVec.length; i++) {
            newVec[i] = vector[newOrder[i]];
        }
        return newVec;
    }

//</editor-fold>
}
