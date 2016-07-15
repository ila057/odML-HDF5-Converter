package core;

import cz.zcu.kiv.signal.DataTransformer;
import org.g_node.nix.Block;

/**
 * Created by ipsita on 25/6/16.
 */
public interface DataParser {

    int getNumberOfChannels(Block block, DataTransformer dataTransformer);
    double[] convertBinaryData(DataTransformer dataTransformer, int size);
    void setData(Block b);

}
