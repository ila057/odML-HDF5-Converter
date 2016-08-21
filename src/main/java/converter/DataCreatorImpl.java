package converter;

import core.DataCreator;
import org.apache.log4j.Logger;
import org.g_node.nix.*;
import org.g_node.nix.File;

import java.io.*;
import java.util.List;

/**
 * Created by ipsita on 7/25/16.
 */
public class DataCreatorImpl implements DataCreator {
    static Logger logger = Logger.getLogger(DataCreatorImpl.class.getName());
    String hd5File;

    /**
     * Default constructor, when no HDF5 file to convert is passed
     */
    public DataCreatorImpl(){
        logger.debug("Invalid HDF5 File to read");
    }

    /**
     * The constructor initializes the name of the hdf5 file which is to be converted so as to extrat odml metadata, and data files with data as double values
     * @param hdf5File : the hdf5 file which is to be converted to odml metadata and data
     */
    public DataCreatorImpl(String hdf5File){
        this.hd5File = hdf5File;
    }

    /**
     * It uses the hdf5 file passed and extracts the metadata odml file, and extracts the raw data as double values for various channels
     */
    public void writeDataFromHdfFile() {
        File file = File.open(hd5File, FileMode.ReadOnly);
        Block block = file.getBlock(0);
        List<DataArray> dataArrays = block.getDataArrays();
        logger.debug("DataArray Size:"+block.getDataArrays().size());
        for (int i=0; i<dataArrays.size(); i++){
            try {
                double[] d = new double[dataArrays.size()];
                dataArrays.get(i).getData(d, new NDSize(new int[]{0}), new NDSize(new int[]{0}));
                dataArrays.get(i).getData(d, new NDSize(new int[]{d.length}), new NDSize(new int[]{0}));
                PrintWriter out = new PrintWriter("dataArray_" + i + "_" + hd5File);
                for(double da:d){
                    out.println(da);
                }
                out.close();
            }catch(Exception e){
                logger.error("Exception while extracting data from HDF5 file : "+e);
                throw new RuntimeException("context",e);
            }
        }

    }

}
