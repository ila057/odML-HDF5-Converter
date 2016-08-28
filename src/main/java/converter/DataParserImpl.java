package converter;

import core.DataParser;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import org.apache.log4j.Logger;
import org.g_node.nix.Block;
import org.g_node.nix.DataArray;
import org.g_node.nix.DataType;
import org.g_node.nix.NDSize;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by ipsita on 23/6/16.
 */
public class DataParserImpl implements DataParser{

    private String headerFile;
    private String dataFile;
    final static Logger logger = Logger.getLogger(DataParserImpl.class);

    /**
     *
     * This is a constructor for converter.DataParserImpl
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
    public double[] convertBinaryData(DataTransformer dataTransformer, int size) throws IOException {
        logger.info("entering convertBinaryData");
        double[] convertedBinaryData = null;
        try {
            convertedBinaryData = dataTransformer.readBinaryData(headerFile, dataFile, size, ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
            logger.error("Could not read and convert binary data : ",e);
            throw new IOException("Could not read and convert binary data",e);
        }
        logger.info("leaving convertBinaryData");

        return convertedBinaryData;
    }

    public int getNumberOfChannels(Block block, DataTransformer dataTransformer) throws IOException {
        logger.info("entering getNumberOfChannels");

        List channelInfo = new ArrayList<>();

        try {
            channelInfo = dataTransformer.getChannelInfo(headerFile);
        } catch (IOException e) {
            logger.error("Could not get number of channels successfully : ",e);
            throw new IOException("Could not get number of channels successfully",e);
        }
        logger.info("leaving getNumberOfChannels");

        if(channelInfo!=null)
            return channelInfo.size();
        else
            return 0;
    }

    /**
     *
     * This method uses nix java library to create a DataArray inside
     * the Block specified as argument and fills it with data from eeg file.
     *
     * @param b : Block of the nix file which will contain the data
     */
    public void setData(Block b) throws IOException {
        logger.info("entering setData");
        DataTransformer dt = new EEGDataTransformer();
        Integer noOfChannels = getNumberOfChannels(b, dt);
        if(noOfChannels!=null) {
            for (int i = 1; i < noOfChannels + 1; i++) {
                double[] convertedBinaryData = convertBinaryData(dt, i);
                if(convertedBinaryData!=null) {
                    int sizeOfData = convertedBinaryData.length;
                    logger.info("Gonna try to create a data array with these args : " + findNameOfDataArray() + i + "," + "DataArray" + i + "," + DataType.Double + "," + "4th ND Size argument");
                    DataArray dataArray = b.createDataArray(findNameOfDataArray() + i, "DataArray" + i, DataType.Double, new NDSize(new int[]{sizeOfData}));
                    dataArray.setData(convertedBinaryData, new NDSize(new int[]{sizeOfData}), new NDSize(new int[]{0}));
                    logger.debug("Block's name: " + b.getName() + ",   Block's DataArray Count:  " + b.getDataArrayCount());
                    dataArray.setNull();
                }
            }
        }
        logger.info("leaving setData");
    }

    /**
     * This method returns the name of the DataArray containing eeg data
     *
     * @return : Name of the DataArray containing eeg datae
     */
    private String findNameOfDataArray(){
        logger.info("name of dataArray (base name of header file) : " + FilenameUtils.getBaseName(headerFile).replaceAll(" ","_"));
        return  (FilenameUtils.getBaseName(headerFile)).replaceAll(" ","_");
    }
}