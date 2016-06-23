
import org.g_node.nix.*;

/**
 * Created by ipsita on 23/6/16.
 *<h1>odML Parser</h1>
 *
 * odML parser takes all the information regarding the odML metadata and the raw eeg data and parses the required files based on the information provided.
 * It then creates an HDF5 file with the corresponding metadata information and the raw data as values of double datatype.
 */

public class ODMLParser {

    private String filename;
    private String metadataFile;
    private String dataFile;
    private String headerFile;

    private int numberOfChannels=0;

    public ODMLParser() {
        this.filename = "Default_File_Name";
    }

    /**
     * The constructor which initializes the parameters of the odML parser with the user provided data
     *
     * @param filename the name of the HDF5 file to be created
     * @param metadataFile the name of the odML metadata file (such as metadata.xml)
     * @param dataFile the .eeg or the .avg file which contains the raw eeg data
     * @param headerFile the corresponding header file (with the .vhdr extension) of the raw eeg data
     */
    public ODMLParser(String filename, String metadataFile, String dataFile, String headerFile) {
        this.filename = filename;
        this.metadataFile = metadataFile;
        this.dataFile = dataFile;
        this.headerFile = headerFile;
    }


    /**
     *  The function calls the DataParser and the MetadataParser to set the raw data and metadata in the HDF5 file
     */
    public void parseODML(){


        try{
            //create hdf5 file and create a new file overwriting any existing content
            org.g_node.nix.File file = org.g_node.nix.File.open(this.filename, FileMode.Overwrite);
            // create a block
            Block b = file.createBlock("Root","dataset");

            DataParser dataParser  = new DataParser(headerFile, dataFile);
            if(numberOfChannels!=0)
                dataParser = new DataParser(headerFile, dataFile, numberOfChannels);
            dataParser.setData(b);

            MetadataParser metadataParser = new MetadataParser();
            metadataParser.setMetadata( metadataFile, b, file);

            System.out.println("done.");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}