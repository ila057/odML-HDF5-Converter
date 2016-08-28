package core;

import org.g_node.nix.Block;
import org.g_node.nix.File;
import org.g_node.nix.Section;

import java.util.Vector;

/**
 * Created by ipsita on 7/13/16.
 */
public interface MetadataCreator {
    public File initializeHDF5Reader();
    public void createOdml(String odmlFileName) throws Exception;


}
