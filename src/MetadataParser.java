import org.g_node.nix.Block;
import org.g_node.nix.Property;
import org.g_node.nix.Section;
import org.g_node.nix.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by ipsita on 23/6/16.
 */
public class MetadataParser {

    String metadataFile;

    public MetadataParser(String metadatFile) {
        this.metadataFile = metadatFile;
    }

    public MetadataParser() {
        this.metadataFile = "metadata.xml";
    }

    /**
     *
     * The method takes in an input file and creates a DOM document which
     * can be parsed like a tree using XML parser.
     *
     * @param inputFile : The file used to create a DOM parsing tree of the same
     * @return Document : a DOM Model document is returned which can then be parsed
     */
    private Document initializeDOMParser(java.io.File inputFile){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        return doc;

    }

    /**
     *
     * This method takes in a nix file, a nix Block and a odml metadata file, parses the odml
     * metadata file and creates a metadata for the Block in the Nix file.
     *
     * @param metadataFile : File name of the metadata.xml odml file
     * @param block : Block whose metadata will be set
     * @param file : HDF5 (Specifically nix) file in which this metadata will be written
     */
    public void setMetadata(String metadataFile, Block block, org.g_node.nix.File file) {
        java.io.File inputFile = new java.io.File(metadataFile);

        try {
            Document doc = initializeDOMParser(inputFile);
            NodeList versionList = doc.getElementsByTagName("version");
            String version = versionList.item(0).getTextContent();

            NodeList dateList = doc.getElementsByTagName("date");
            String date = dateList.item(0).getTextContent();

            System.out.println("version : " + version);
            System.out.println("date : " + date);

            // create section and add a property
            Section sectionMetadata = file.createSection("metadataSection", "metadata");
            block.setMetadata(sectionMetadata);

            //for parsing
            NodeList sectionList = doc.getElementsByTagName("section");

            for (int temp = 0; temp < sectionList.getLength(); temp++) {

                Node thisSection = sectionList.item(temp);
                System.out.println("temp : " + temp + " | Current section : " + thisSection.getNodeName());

                NodeList sectionChildren = thisSection.getChildNodes();

                String typeOfSection = "";
                String nameOfSection = "";

                //do for each child of this section (for type and name)
                for (int i = 0; i < sectionChildren.getLength(); i++) {

                    Node sectionChild = sectionChildren.item(i);

                    if (sectionChild.getNodeName().equalsIgnoreCase("type")) {
                        //assign type to the section
                        typeOfSection = sectionChild.getTextContent();
                    } else if (sectionChild.getNodeName().equalsIgnoreCase("name")) {
                        //assign name to the section
                        nameOfSection = sectionChild.getTextContent();
                    }
                }

                // hdf5 creating subsection of metadata section (root)
                Section secChild = sectionMetadata.createSection(nameOfSection, typeOfSection);

                //for each child of section (for property)
                for (int i = 0; i < sectionChildren.getLength(); i++) {
                    Node sectionChild = sectionChildren.item(i);

                    if (sectionChild.getNodeName().equalsIgnoreCase("property")) {
                        String nameOfProperty = "";
                        Value valueOfProperty = new Value("");  // default value since creating property requires a value and some properties don't have one

                        NodeList propertyChildren = sectionChild.getChildNodes();

                        for (int j = 0; j < propertyChildren.getLength(); j++) {
                            Node propertyChild = propertyChildren.item(j);

                            if (propertyChild.getNodeName().equalsIgnoreCase("name")) {
                                nameOfProperty = propertyChild.getTextContent();
                            }
                            else if (propertyChild.getNodeName().equalsIgnoreCase("value")) {

                                NodeList valueDetails = propertyChild.getChildNodes();
                                String value = valueDetails.item(0).getTextContent().split("\n")[1].trim();
                                String valueType = valueDetails.item(1).getTextContent();
                                System.out.println("Value = " + value + ", valueType = " + valueType);
                                valueOfProperty = new Value();
                                switch (valueType) {
                                    case "datetime":
                                        valueOfProperty.setString(value);
                                        break;
                                    case "int":
                                        valueOfProperty.setInt(Integer.parseInt(value));
                                        break;
                                    case "float":
                                        valueOfProperty.setDouble(Double.parseDouble(value));
                                        break;
                                    case "boolean":
                                        valueOfProperty.setBoolean(Boolean.parseBoolean(value));
                                        break;
                                    case "string":
                                        valueOfProperty.setString(value);
                                        break;
                                    case "long":
                                        valueOfProperty.setLong(Long.parseLong(value));
                                        break;
                                    default:
                                        System.out.println("ERROR. Some wrong valueType. valueType : " + valueType);
                                }
                            }
                            else {

                            }
                        }

                        Property prop = secChild.createProperty(nameOfProperty, valueOfProperty);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

}