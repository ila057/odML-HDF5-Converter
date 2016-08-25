package converter;

import core.ExperimentParser;
import org.apache.log4j.Logger;
import org.g_node.nix.*;

import java.io.IOException;

/**
 * Created by ipsita on 23/6/16.
 *<h1>odML Parser</h1>
 *
 * odML parser takes all the information regarding the odML metadata and the raw eeg data and parses the required files based on the information provided.
 * It then creates an HDF5 file with the corresponding metadata information and the raw data as values of double datatype.
 */

public class ExperimentParserImpl implements ExperimentParser {


    final static Logger logger = Logger.getLogger(ExperimentParserImpl.class);


    /**
     *  The function calls the converter.DataParserImpl and the converter.MetadataParserImpl to set the raw data and metadata in the HDF5 file
     */
    public void parseODML( String convertedFilename,String metadataFile,String dataFile,String headerFile,String markerFile, boolean last){
        logger.info("create hdf5 file and create a new file overwriting any existing content");
        org.g_node.nix.File nixFile = org.g_node.nix.File.open(convertedFilename, FileMode.ReadWrite);

        Block b = nixFile.createBlock("Root","dataset");

        MetadataParserImpl metadataParser = new MetadataParserImpl();
        try {
            metadataParser.setMetadata( metadataFile, b, nixFile, headerFile, markerFile);
        } catch (IOException e) {
            logger.error("could not set metadata. Parsing metadata failed.",e);
            throw new RuntimeException("could not set metadata, parsing metadata failed.",e);
        }

        DataParserImpl dataParser  = new DataParserImpl(headerFile, dataFile);
        dataParser.setData(b);

        logger.info("done.");
        if(last){
            b.setNull();
            nixFile.close();
        }
        else{
            b.setNull();
            nixFile.setNull();
        }

    }


}
