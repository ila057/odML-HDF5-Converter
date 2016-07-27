/**
 * Created by ipsita on 23/6/16.
 */
public class MainClassParser {
    /**
     *
     * This is the main or testing class. Here we specify the name of eeg, vhdr and metadata file, as well as the name of the HDF5 file to be created.
     *
     */
    public static void main(String args[]){
        ODMLParserImpl odmlParser = new ODMLParserImpl("Experiment_208_Driver's_attention_with_visual_stimulation_and_audio_disturbance", "metadata.xml", "LED_26_3_2014_0004.eeg", "LED_26_3_2014_0004.vhdr");
        odmlParser.parseODML();
    }
}