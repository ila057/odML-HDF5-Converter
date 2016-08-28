package converter;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by lion on 27/7/16.
 */

/**
 * The main class which is used to test the conversion of HDF5 datset into metadata in odml format and data as double values for various channels
 */
public class MainClassCreator {
    static Logger logger = Logger.getLogger(MainClassCreator.class.getName());

    public static void main(String args[]) throws Exception {
        String hdf5FileName = "Experiment_208_Driver's_attention_with_visual_stimulation_and_audio_disturbance";
        DataCreatorImpl dc = new DataCreatorImpl(hdf5FileName);
        dc.writeDataFromHdfFile();

        MetadataCreatorImpl m = new MetadataCreatorImpl(hdf5FileName);
        String odMLMetadataFile = "odML_metadata:" + hdf5FileName;
        m.createOdml(odMLMetadataFile);

    }
}
