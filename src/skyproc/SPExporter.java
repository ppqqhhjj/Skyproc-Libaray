/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import lev.LFileChannel;
import lev.Ln;

/**
 * A somewhat unnecessary class.  It is NOT used for exporting patches.
 * Use Mod's export() functionality for that.
 * Its main functionality is to offer the same background processing capability
 * found in SPImporter.  This class will
 * also contain any export related functions such as validate().
 * @see Mod
 * @author Justin Swanson
 */
public class SPExporter {

    private static String header = "Export Control";

    /**
     * An empty shell function meant to be overwritten.
     * Override this function and add custom code to it for use with runBackgroundExport().
     */
    public void exportControl() {
    }

    /**
     * Creates a new thread and runs any code in exportControl in the background.
     * This is useful for GUI programs where you don't want the program to freeze while
     * it processes. <br>
     * <br>
     * NOTE:  You MUST override exportControl() with custom code telling the program
     * what you want it to do in the background.
     */
    public void runBackgroundExport() {
        (new Thread(new StartExportThread())).start();
    }

    private class StartExportThread implements Runnable {

        @Override
        public void run() {
            exportControl();
        }

        public void main(String args[]) {
            (new Thread(new StartExportThread())).start();
        }
    }

    /**
     * Reads in the file and confirms that all GRUPs and Major Records have correct lengths.  It does not explicitly
     * check subrecord lengths, but due to the recursive nature of SkyProc, these will be implicitly checked as well by confirming
     * Major Record length.
     * @param testFile Path to the file to test.
     * @param numErrorsToPrint Number of error messages to print before stopping.
     * @return True if the file had correct record lengths.
     */
    public static boolean validateRecordLengths(String testFile, int numErrorsToPrint) {
        String funcTitle = "Validate Record Lengths: ";
        boolean correct = true;
        int numErrors = 0;
        try {
            LFileChannel input = new LFileChannel(testFile);

            // Check header
            String inputStr;
            int length = input.readInInt(4, 4);
            input.offset(length + 16);
            if (input.available() > 0) {
                // Next should be a GRUP
                inputStr = input.readInString(0, 4);
                if (!"GRUP".equals(inputStr)) {
                    System.out.println(funcTitle + "Header length is wrong.");
                    numErrors++;
                    correct = false;
                }
                input.offset(-4);
            } else if (input.available() < 0) {
                System.out.println(funcTitle + "Header length is wrong.");
                numErrors++;
                correct = false;
            } else {
                System.out.println(funcTitle + "File header was correct, but there were no GRUPS.  Validated.");
                return true;
            }

            //Test GRUPs
            String majorRecordType = "NULL";
            long start;
            long startOffset;
            while (input.available() > 0 && (numErrors < numErrorsToPrint || numErrorsToPrint == 0)) {
                inputStr = input.scanTo(majorRecordType, "GRUP");
                if (inputStr.equals("")) {
                    break;
                }
                startOffset = input.pos();
                start = startOffset - 4;
                length = input.readInInt(0, 4);
                if ("GRUP".equals(inputStr)) {
                    majorRecordType = input.readInString(0, 4);
                    input.offset(length - 12);  // End of GRUP
                    if (input.available() > 0) {
                        inputStr = input.readInString(0, 4);
                        if (!"GRUP".equals(inputStr)) {
                            System.out.println(funcTitle + "GRUP " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
                            numErrors++;
                            correct = false;
                        }
                    } else if (input.available() < 0) {
                        System.out.println(funcTitle + "GRUP " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
                        numErrors++;
                        correct = false;
                    }
                    startOffset += 12;
                } else {
                    input.offset(length + 16);  // End of Major
//                    System.out.println("TEST POS " + Ln.prettyPrintHex(input.pos()));
                    if (input.available() > 0) {
                        inputStr = input.readInString(0, 4);
                        if (!"GRUP".equals(inputStr) && !majorRecordType.equals(inputStr)) {
                            System.out.println(funcTitle + "MajorRecord " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
                            numErrors++;
                            correct = false;
                        }
                    } else if (input.available() < 0) {
                        System.out.println(funcTitle + "MajorRecord " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
                        numErrors++;
                        correct = false;
                    }
                }
                input.pos(startOffset);
            }


        } catch (FileNotFoundException ex) {
            System.out.println(funcTitle + "File could not be found.");
        } catch (IOException ex) {
            System.out.println(funcTitle + "File I/O error.");
        }


        if (correct) {
            System.out.println(funcTitle + "Validated.");
        } else {
            System.out.println(funcTitle + "NOT Validated.");
        }
        return correct;
    }
}
