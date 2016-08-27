package core;

import org.g_node.nix.Block;
import org.g_node.nix.Section;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by ipsita on 25/6/16.
 */
public interface MetadataParser {
    odml.core.Section initializeODMLReader(String metadataFile);
    void setMetadata(String metadataFile, Block block, org.g_node.nix.File file, String headerFile, String markerFile, boolean metadataExists, boolean hdrExists, boolean vmrkExists) throws IOException;
    void setSection(Section parentSection, Vector<odml.core.Section> sectionVector);
    void setProperties(Section parentSec, Vector<odml.core.Property> propertiesList);
    void processGUINamespaces(Section parentSec, odml.core.Property property);
}
