package coreEngine.Helper;

import coreEngine.reliabilityAnalysis.DataStruct.ScenarioInfo;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains static methods to create various type of ArrayList or
 * normal array, to sum and average of Array/ArrayList, to compare LOS, and to
 * convert between veh and pc. Only used in coreEngine package.
 *
 * @author Shu Liu
 */
public class CEHelper {

    // <editor-fold defaultstate="collapsed" desc="CREATE ARRAYLIST">
    /**
     * Create 2D Integer ArrayList
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Integer ArrayList (prefilled with default value)
     */
    public static ArrayList<ArrayList<Integer>> int_2D(int row, int col, int value) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>(row);
        for (int i = 0; i < row; i++) {
            result.add(int_1D(col, value));
        }
        return result;
    }

    /**
     * Create 2D Float ArrayList
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Float ArrayList (prefilled with default value)
     */
    public static ArrayList<ArrayList<Float>> float_2D(int row, int col, float value) {
        ArrayList<ArrayList<Float>> result = new ArrayList<>(row);
        for (int i = 0; i < row; i++) {
            result.add(float_1D(col, value));
        }
        return result;
    }

    /**
     * Create 2D String ArrayList
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D String ArrayList (prefilled with default value)
     */
    public static ArrayList<ArrayList<String>> str_2D(int row, int col, String value) {
        ArrayList<ArrayList<String>> result = new ArrayList<>(row);
        for (int i = 0; i < row; i++) {
            result.add(str_1D(col, value));
        }
        return result;
    }

    /**
     * Create 2D Boolean ArrayList
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Float ArrayList (prefilled with default value)
     */
    public static ArrayList<ArrayList<Boolean>> bool_2D(int row, int col, boolean value) {
        ArrayList<ArrayList<Boolean>> result = new ArrayList<>(row);
        for (int i = 0; i < row; i++) {
            result.add(bool_1D(col, value));
        }
        return result;
    }

    /**
     * Create 1D Integer ArrayList
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Integer ArrayList (prefilled with default value)
     */
    public static ArrayList<Integer> int_1D(int length, int value) {
        ArrayList<Integer> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(value);
        }
        result.trimToSize();
        return result;
    }

    /**
     * Create 1D Float ArrayList
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Float ArrayList (prefilled with default value)
     */
    public static ArrayList<Float> float_1D(int length, float value) {
        ArrayList<Float> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(value);
        }
        result.trimToSize();
        return result;
    }

    /**
     * Create 1D String ArrayList
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D String ArrayList (prefilled with default value)
     */
    public static ArrayList<String> str_1D(int length, String value) {
        ArrayList<String> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(value);
        }
        result.trimToSize();
        return result;
    }

    /**
     * Create 1D Boolean ArrayList
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Float ArrayList (prefilled with default value)
     */
    public static ArrayList<Boolean> bool_1D(int length, boolean value) {
        ArrayList<Boolean> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(value);
        }
        result.trimToSize();
        return result;
    }

    /**
     * Create 1D ScenarioInfo ArrayList
     *
     * @param length length of array
     * @return 1D ScenarioInfo ArrayList (prefilled with default ScenarioInfo)
     */
    public static ArrayList<ScenarioInfo> scenInfo_1D(int length) {
        ArrayList<ScenarioInfo> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(new ScenarioInfo());
        }
        result.trimToSize();
        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CREATE ARRAY">
    /**
     * Create 2D Integer Array
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Integer Array (prefilled with default value)
     */
    public static int[][] int_2D_normal(int row, int col, int value) {
        int[][] result = new int[row][col];
        if (value != 0) {
            for (int i = 0; i < row; i++) {
                Arrays.fill(result[i], value);
            }
        }
        return result;
    }

    /**
     * Create 2D Float Array
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Float Array (prefilled with default value)
     */
    public static float[][] float_2D_normal(int row, int col, float value) {
        float[][] result = new float[row][col];
        if (value != 0) {
            for (int i = 0; i < row; i++) {
                Arrays.fill(result[i], value);
            }
        }
        return result;
    }

    /**
     * Create 2D String Array
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D String Array (prefilled with default value)
     */
    public static String[][] str_2D_normal(int row, int col, String value) {
        String[][] result = new String[row][col];
        for (int i = 0; i < row; i++) {
            Arrays.fill(result[i], value);
        }
        return result;
    }

    /**
     * Create 2D Boolean Array
     *
     * @param row number of rows
     * @param col number of columns
     * @param value default value of array
     * @return 2D Float Array (prefilled with default value)
     */
    public static boolean[][] bool_2D_normal(int row, int col, boolean value) {
        boolean[][] result = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            Arrays.fill(result[i], value);
        }
        return result;
    }

    /**
     * Create 1D Integer Array
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Integer Array (prefilled with default value)
     */
    public static int[] int_1D_normal(int length, int value) {
        int[] result = new int[length];
        if (value != 0) {
            Arrays.fill(result, value);
        }
        return result;
    }

    /**
     * Create 1D Float Array
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Float Array (prefilled with default value)
     */
    public static float[] float_1D_normal(int length, float value) {
        float[] result = new float[length];
        if (value != 0) {
            Arrays.fill(result, value);
        }
        return result;
    }

    /**
     * Create 1D String Array
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D String Array (prefilled with default value)
     */
    public static String[] str_1D_normal(int length, String value) {
        String[] result = new String[length];
        Arrays.fill(result, value);
        return result;
    }

    /**
     * Create 1D Boolean Array
     *
     * @param length length of array
     * @param value default value of array
     * @return 1D Float Array (prefilled with default value)
     */
    public static boolean[] bool_1D_normal(int length, boolean value) {
        boolean[] result = new boolean[length];
        Arrays.fill(result, value);
        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="HELPER FUNCTIONS">
    /**
     * Sum the array
     *
     * @param array data array
     * @return sum of the array
     */
    public static float sum(float[] array) {
        float result = 0;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result;
    }

    /**
     * Average the array
     *
     * @param array data array
     * @return average of the array
     */
    public static float average(float[] array) {
        return sum(array) / array.length;
    }

    /**
     * Sum the array
     *
     * @param array data array
     * @return sum of the array
     */
    public static int sum(int[] array) {
        int result = 0;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result;
    }

    /**
     * Average the array
     *
     * @param array data array
     * @return average of the array
     */
    public static float average(int[] array) {
        return (float) sum(array) / array.length;
    }

    /**
     * Sum the array
     *
     * @param array data array
     * @return sum of the array
     */
    public static float sum(ArrayList<Float> array) {
        float result = 0;
        for (float num : array) {
            result += num;
        }
        return result;
    }

    /**
     * Average the array
     *
     * @param array data array
     * @return average of the array
     */
    public static float average(ArrayList<Float> array) {
        return sum(array) / array.size();
    }

    /**
     * Convert veh to pc
     *
     * @param value value to be converted
     * @param coe coefficient
     * @return value in pc
     */
    public static float veh_to_pc(float value, float coe) {
        return value / coe;// / inPeak / inDriver;
    }

    /**
     * Convert pc to veh
     *
     * @param value value to be converted
     * @param coe coefficient
     * @return value in veh
     */
    public static float pc_to_veh(float value, float coe) {
        return value * coe;
    }

    /**
     * Compare two level of service
     *
     * @param LOS1 first level of service
     * @param LOS2 second level of service
     * @return worse level of service
     */
    public static String worseLOS(String LOS1, String LOS2) {
        if (LOS1.length() < 1) {
            return LOS2;
        } else {
            if (LOS2.length() < 1) {
                return LOS1;
            } else {
                return LOS1.charAt(0) > LOS2.charAt(0) ? LOS1 : LOS2;
            }
        }
    }
    // </editor-fold>
}
