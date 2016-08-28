package core;

import cz.zcu.kiv.signal.DataTransformer;
import org.g_node.nix.Block;

import java.io.IOException;

/**
 * Created by ipsita on 25/6/16.
 */
public interface DataParser {

    int getNumberOfChannels(Block block, DataTransformer dataTransformer) throws IOException;
    double[] convertBinaryData(DataTransformer dataTransformer, int size) throws IOException;
    void setData(Block b) throws IOException;

}
