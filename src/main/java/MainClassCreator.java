import org.apache.log4j.Logger;

/**
 * Created by lion on 27/7/16.
 */
public class MainClassCreator {
    static Logger logger = Logger.getLogger(MainClassCreator.class.getName());

    public static void main(String args[]) {
        String hdf5FileName = "Experiment_208_Driver's_attention_with_visual_stimulation_and_audio_disturbance";
        DataCreatorImpl dc = new DataCreatorImpl(hdf5FileName);
        dc.writeDataFromHdfFile();

        try {
            MetadataCreatorImpl m = new MetadataCreatorImpl(hdf5FileName);
            String odMLMetadataFile = "odML_metadata:" + hdf5FileName;
            m.createOdml(odMLMetadataFile);
        }catch(Exception e){
            logger.error("Error occurred while converting HDF5 file to odML metadata and Raw data: "+e);
            throw new RuntimeException("context",e);
        }
    }
}
