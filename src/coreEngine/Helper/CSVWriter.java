package coreEngine.Helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * CSV file writer
 *
 * @author Shu Liu
 */
public class CSVWriter {

    /**
     * File Writer
     */
    private final FileWriter fw;

    /**
     * Buffered Writer
     */
    private final BufferedWriter bw;

    /**
     * Column Separation
     */
    private final String separation;

    /**
     * Constructor
     *
     * @param writer File Writer
     * @param separation Column Separation
     */
    public CSVWriter(FileWriter writer, char separation) {
        this.fw = writer;
        this.separation = Character.toString(separation);
        bw = new BufferedWriter(fw);
    }

    /**
     * Write next line of data
     *
     * @param data a line of data
     * @throws IOException
     */
    public void writeNext(String[] data) throws IOException {
        for (String str : data) {
            bw.write(str);
            bw.write(separation);
        }
        bw.newLine();
    }

    /**
     * Finish and close file writer
     *
     * @throws IOException
     */
    public void close() throws IOException {
        bw.close();
        fw.close();
    }
}
