import org.apache.log4j.Logger;
import org.g_node.nix.*;
import org.g_node.nix.File;

import java.io.*;
import java.util.List;

/**
 * Created by ipsita on 7/25/16.
 */
public class DataCreatorImpl {
    static Logger logger = Logger.getLogger(DataCreatorImpl.class.getName());
    String hd5File;

    public DataCreatorImpl(){
        logger.debug("Invalid HDF5 File to read");
    }

    public DataCreatorImpl(String hdf5File){
        this.hd5File = hdf5File;
    }

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
                logger.error("Exception while extracting data from HDF5 file : "+e.getMessage());
            }
        }

    }

}
