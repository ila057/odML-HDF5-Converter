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
public class DataParser {

    private String headerFile;
    private String dataFile;
    private int NumberOfChannels;

    /**
     *
     * This is a constructor for DataParser
     *
     * @param headerFile : The vhdr header file
     * @param dataFile : eeg binary data file
     */
    public DataParser(String headerFile, String dataFile) {
        this.headerFile = headerFile;
        this.dataFile = dataFile;
    }

    /**
     *
     * Another constructor of DataParser which takes number of channels
     *
     * @param headerFile : The vhdr header file
     * @param dataFile : The eeg file containing the binary data
     * @param numberOfChannels : The number of channels used for obtaining data
     */
    public DataParser(String headerFile, String dataFile, int numberOfChannels) {
        this.headerFile = headerFile;
        this.dataFile = dataFile;
        NumberOfChannels = numberOfChannels;
    }

    /**
     *
     * This uses the eegloader library to convert eeg binary data file
     * to a double array containing all the values
     *
     * @return A double array containing the experiment values
     */
    public double[] convertBinaryData(){
        DataTransformer dt = new EEGDataTransformer();
        List channelInfo = new ArrayList<>();
        double[] convertedBinaryData = null;
        try {
            channelInfo = dt.getChannelInfo(headerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            convertedBinaryData = dt.readBinaryData(headerFile, dataFile, channelInfo.size(), ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedBinaryData;
    }

    /**
     *
     * This method uses nix java library to create a DataArray inside
     * the Block specified as argument and fills it with data from eeg file.
     *
     * @param b : Block of the nix file which will contain the data
     */
    public void setData(Block b){
        double[] convertedBinaryData = convertBinaryData();
        int sizeOfData = convertedBinaryData.length;
        DataArray dataArray = b.createDataArray(findNameOfDataArray(), "DataArray", DataType.Double, new NDSize(new int[]{sizeOfData}));
        dataArray.setData(convertedBinaryData,new NDSize(new int[]{sizeOfData}),new NDSize(new int[]{0}));
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