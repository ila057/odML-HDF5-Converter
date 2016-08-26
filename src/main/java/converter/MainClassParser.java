package converter;
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
    public static void main(String args[]) throws IOException {

        DataProcessor dataProcessor = new DataProcessor();
            String finalConvertedFolderToBeZipped =
                    dataProcessor.
                            generateConvertedDataSet(
                                    "/home/lion/incf/data/Readiness_Potential.zip");


        System.out.println("Done.");

    }
}