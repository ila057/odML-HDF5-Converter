package core;

/**
 * Created by ipsita on 25/6/16.
 */
public interface ExperimentParser {
    void parseODML( String convertedFilename,String metadataFile,String dataFile,String headerFile,String markerFile, boolean last, boolean metadataExists);
}
