/**
 * Created by lion on 27/7/16.
 */
public class MainClassCreator {
    public static void main(String args[]) {
        String hdf5FileName = "Experiment_208_Driver's_attention_with_visual_stimulation_and_audio_disturbance";
        DataCreatorImpl dc = new DataCreatorImpl(hdf5FileName);
        dc.writeDataFromHdfFile();

        try {
            MetadataCreatorImpl m = new MetadataCreatorImpl(hdf5FileName);
            String odMLMetadataFile = "odML_metadata:" + hdf5FileName;
            m.createOdml(odMLMetadataFile);
        }catch(Exception e){

        }
    }
}
