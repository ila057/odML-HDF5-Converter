import core.DataParser;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import org.g_node.nix.Block;
import org.g_node.nix.DataArray;
import org.g_node.nix.DataType;
import org.g_node.nix.NDSize;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ipsita on 23/6/16.
 */
public class DataParserImpl implements DataParser{

    private String headerFile;
    private String dataFile;

    /**
     *
     * This is a constructor for DataParserImpl
     *
     * @param headerFile : The vhdr header file
     * @param dataFile : eeg binary data file
     */
    public DataParserImpl(String headerFile, String dataFile) {
        this.headerFile = headerFile;
        this.dataFile = dataFile;
    }


    /**
     *
     * This uses the eegloader library to convert eeg binary data file
     * to a double array containing all the values
     *
     * @return A double array containing the experiment values
     */
    public double[] convertBinaryData(DataTransformer dataTransformer, int size){

        double[] convertedBinaryData = null;
        try {
            convertedBinaryData = dataTransformer.readBinaryData(headerFile, dataFile, size, ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedBinaryData;
    }

    public int getNumberOfChannels(Block block, DataTransformer dataTransformer){
        List channelInfo = new ArrayList<>();

        try {
            channelInfo = dataTransformer.getChannelInfo(headerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelInfo.size();
    }

    /**
     *
     * This method uses nix java library to create a DataArray inside
     * the Block specified as argument and fills it with data from eeg file.
     *
     * @param b : Block of the nix file which will contain the data
     */
    public void setData(Block b){
        DataTransformer dt = new EEGDataTransformer();
        int noOfChannels = getNumberOfChannels(b, dt);
        for(int i=1; i<noOfChannels+1; i++) {
            double[] convertedBinaryData = convertBinaryData(dt, i);
            int sizeOfData = convertedBinaryData.length;
            DataArray dataArray = b.createDataArray(findNameOfDataArray()+":"+i, "DataArray"+i, DataType.Double, new NDSize(new int[]{sizeOfData}));
            dataArray.setData(convertedBinaryData, new NDSize(new int[]{sizeOfData}), new NDSize(new int[]{0}));
        }
    }

    /**
     * This method returns the name of the DataArray containing eeg data
     *
     * @return : Name of the DataArray containing eeg data
     */
    private String findNameOfDataArray(){
        return headerFile.substring(0,headerFile.length()-5);
    }
}