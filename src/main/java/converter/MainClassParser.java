package converter;
import java.io.File;
import java.io.IOException;

/**
 * Created by ipsita on 23/6/16.
 */
public class MainClassParser {
    /**
     *
     * This is the main or testing class. Here we specify the location or the name(if it is in project home) of the data-package zip file
     *
     */
    public static void main(String args[]) throws Exception {

        DataProcessor dataProcessor = new DataProcessor();
            String finalConvertedFolderToBeZipped =
                    dataProcessor.
                            generateConvertedDataSet(
                                    "/home/lion/Downloads/EEG_ERP.zip");

        System.out.println("Done");

    }
}