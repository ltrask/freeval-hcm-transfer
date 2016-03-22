package coreEngine;

import coreEngine.Helper.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to output detailed 15-sec over saturated calculation
 *
 * @author Shu Liu
 */
public class DebugOutput {

    /**
     * csv file writer
     */
    private static CSVWriter writer;

    /**
     * Total number of items in each row
     */
    private static final int NUM_ITEMS = 17;

    /**
     * Decimal formatter for integer
     */
    private static final DecimalFormat f0 = new DecimalFormat("0");

    /**
     * Decimal formatter for floating number
     */
    private static final DecimalFormat f2 = new DecimalFormat("0.00");

    /**
     * Initialize debug output
     */
    public static void startOutput() {
        try {
            writer = new CSVWriter(new FileWriter("DebugOutput.csv"), '\t');
        } catch (IOException ex) {
            Logger.getLogger(DebugOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write results for a period (60 steps) into the csv file
     *
     * @param period analysis period index
     * @param Segments ArrayList of segments (GP or ML segments)
     * @throws IOException
     */
    public static void write(int period, ArrayList<GPMLSegment> Segments) throws IOException {
        //Save data to csv file
        String[] empty = new String[]{" "};
        writer.writeNext(empty);
        writer.writeNext(empty);

        String[] data = new String[]{"Time Period: " + (period + 1)};
        writer.writeNext(data);

        writer.writeNext(empty);

        String[] title1 = new String[Segments.size() * NUM_ITEMS];
        for (int seg = 0; seg < Segments.size(); seg++) {
            for (int i = 0; i < NUM_ITEMS; i++) {
                switch (i) {
                    case 0:
                        title1[seg * NUM_ITEMS + i] = "ED";
                        break;
                    case 1:
                        title1[seg * NUM_ITEMS + i] = "SC";
                        break;
                    case 2:
                        title1[seg * NUM_ITEMS + i] = "KB";
                        break;
                    case 3:
                        title1[seg * NUM_ITEMS + i] = "WS";
                        break;
                    case 4:
                        title1[seg * NUM_ITEMS + i] = "WTT";
                        break;
                    default:
                        title1[seg * NUM_ITEMS + i] = " ";
                        break;
                }
            }
        }
        writer.writeNext(title1);

        data = new String[Segments.size() * NUM_ITEMS];
        for (int seg = 0; seg < Segments.size(); seg++) {
            for (int i = 0; i < NUM_ITEMS; i++) {
                switch (i) {
                    case 0:
                        data[seg * NUM_ITEMS + i] = f0.format(Segments.get(seg).ED);
                        break;
                    case 1:
                        data[seg * NUM_ITEMS + i] = f0.format(Segments.get(seg).scenMainlineCapacity_veh[period]);
                        break;
                    case 2:
                        data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).KB);
                        break;
                    case 3:
                        data[seg * NUM_ITEMS + i] = " ";
                        break;
                    case 4:
                        data[seg * NUM_ITEMS + i] = f0.format(Segments.get(seg).WTT);
                        break;
                    default:
                        data[seg * NUM_ITEMS + i] = " ";
                        break;
                }
            }
        }
        writer.writeNext(data);

        writer.writeNext(empty);

        String[] title2 = new String[Segments.size() * NUM_ITEMS];
        for (int seg = 0; seg < Segments.size(); seg++) {
            for (int i = 0; i < NUM_ITEMS; i++) {
                switch (i) {
                    case 0:
                        title2[seg * NUM_ITEMS + i] = "Seg " + (seg + 1);
                        break;
                    default:
                        title2[seg * NUM_ITEMS + i] = " ";
                        break;
                }
            }
        }
        writer.writeNext(title2);

        String[] title3 = new String[Segments.size() * NUM_ITEMS];
        for (int seg = 0; seg < Segments.size(); seg++) {
            for (int i = 0; i < NUM_ITEMS; i++) {
                switch (i) {
                    case 0:
                        title3[seg * NUM_ITEMS + i] = "Time Step";
                        break;

                    case 1:
                        title3[seg * NUM_ITEMS + i] = "DEF";
                        break;
                    case 2:
                        title3[seg * NUM_ITEMS + i] = "OFRF";
                        break;
                    case 3:
                        title3[seg * NUM_ITEMS + i] = "MI";
                        break;
                    case 4:
                        title3[seg * NUM_ITEMS + i] = "ONRI";
                        break;
                    case 5:
                        title3[seg * NUM_ITEMS + i] = "ONRO";
                        break;
                    case 6:
                        title3[seg * NUM_ITEMS + i] = "ONRF";
                        break;
                    case 7:
                        title3[seg * NUM_ITEMS + i] = "ONRQ";
                        break;
                    case 8:
                        title3[seg * NUM_ITEMS + i] = "MO1";
                        break;
                    case 9:
                        title3[seg * NUM_ITEMS + i] = "MO3";
                        break;
                    case 10:
                        title3[seg * NUM_ITEMS + i] = "KQ";
                        break;
                    case 11:
                        title3[seg * NUM_ITEMS + i] = "MO2";
                        break;
                    case 12:
                        title3[seg * NUM_ITEMS + i] = "MF";
                        break;
                    case 13:
                        title3[seg * NUM_ITEMS + i] = "SF";
                        break;
                    case 14:
                        title3[seg * NUM_ITEMS + i] = "NV";
                        break;
                    case 15:
                        title3[seg * NUM_ITEMS + i] = "UV";
                        break;
                    case 16:
                        title3[seg * NUM_ITEMS + i] = "QLenFT";
                        break;
                    default:
                        title3[seg * NUM_ITEMS + i] = "ERROR";
                        break;
                }
            }
        }
        writer.writeNext(title3);

        data = new String[]{"0"};
        writer.writeNext(data);

        for (int step = 0; step < 60; step++) {
            data = new String[Segments.size() * NUM_ITEMS];

            for (int seg = 0; seg < Segments.size(); seg++) {
                for (int i = 0; i < NUM_ITEMS; i++) {
                    switch (i) {
                        case 0:
                            data[seg * NUM_ITEMS + i] = Integer.toString(step + 1);
                            break;
                        case 1:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).DEF[step]);
                            break;
                        case 2:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).OFRF[step]);
                            break;
                        case 3:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).MI[step]);
                            break;
                        case 4:
                            data[seg * NUM_ITEMS + i] = " ";//f2.format(GPSegments.get(seg).ONRI[step]);
                            break;
                        case 5:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).ONRO[step]);
                            break;
                        case 6:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).ONRF[step]);
                            break;
                        case 7:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).ONRQ[step]);
                            break;
                        case 8:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).MO1[step] > 1E10 ? 0 : Segments.get(seg).MO1[step]);
                            break;
                        case 9:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).MO3[step] > 1E10 ? 0 : Segments.get(seg).MO3[step]);
                            break;
                        case 10:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).KQ[step]);
                            break;
                        case 11:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).MO2[step] > 1E10 ? 0 : Segments.get(seg).MO2[step]);
                            break;
                        case 12:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).MF[step]);
                            break;
                        case 13:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).SF[step]);
                            break;
                        case 14:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).NV[step]);
                            break;
                        case 15:
                            data[seg * NUM_ITEMS + i] = f2.format(Segments.get(seg).UV[step]);
                            break;
                        case 16:
                            data[seg * NUM_ITEMS + i] = step == 59 ? f2.format(Segments.get(seg).Q[period]) : " ";
                            break;
                        default:
                            data[seg * NUM_ITEMS + i] = "ERROR";
                            break;
                    }
                }
            }
            writer.writeNext(data);
        }
    }

    /**
     * Finish debug output
     */
    public static void finish() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DebugOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
