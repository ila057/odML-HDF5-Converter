//import core.MetadataCreator;
import core.MetadataParser;
import odml.core.Reader;
import odml.core.Writer;
import odml.core.GUIHelper;
import odml.core.Property;
import odml.core.Section;
import odml.core.Value;
import org.apache.log4j.Logger;
import org.g_node.nix.*;
import org.g_node.nix.File;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Created by ipsita on 7/13/16.
 */
public class MetadataCreatorImpl {

    String hd5File;
    Map<Integer,String> dataTypeMap = new HashMap<>();

    final static Logger logger = Logger.getLogger(MetadataCreatorImpl.class);
    public MetadataCreatorImpl(String hdf5File) {
        this.hd5File = hdf5File;
        //data type mapping as in nix
        dataTypeMap.put(0,"boolean");
        dataTypeMap.put(1,"character");
        dataTypeMap.put(2,"float");
        dataTypeMap.put(3,"double");
        dataTypeMap.put(4,"int8");
        dataTypeMap.put(5,"int16");
        dataTypeMap.put(6,"int");   //32 bit integer as default
        dataTypeMap.put(7,"int64");
        dataTypeMap.put(8,"uint8");
        dataTypeMap.put(9,"uint16");
        dataTypeMap.put(10,"uint32");
        dataTypeMap.put(11,"uint64");
        dataTypeMap.put(12,"string");
        dataTypeMap.put(13,"opaque");
        dataTypeMap.put(14,"nothing");
    }

    public File initializeHDF5Reader(){

        File file = File.open(hd5File, FileMode.ReadOnly);
        return file;

    }

    public void createOdml(String odmlFileName) throws IOException {

        File fileRead = initializeHDF5Reader();
        logger.debug(fileRead.getBlockCount() + " | " + fileRead.getBlock(0).getName() + " | " + fileRead.getSectionCount() + " | "+fileRead.getSection(0));

        Section rootSection = new Section();
        rootSection.setName("metadataSection");

        OutputStream outputstream = new FileOutputStream(odmlFileName);


        //parsing hdf5 file
        org.g_node.nix.Section metaDataSection = fileRead.getSection(0);
        rootSection.setDocumentVersion(String.valueOf(fileRead.getVersion()[0]));

        List<org.g_node.nix.Section> ChildSections = metaDataSection.findSections();
        for(org.g_node.nix.Section childSection:ChildSections){

            List<org.g_node.nix.Property> propertyList = childSection.getProperties();

            //writing name of new section
            Section childSectionOdml = new Section();
            childSectionOdml.setName(childSection.getName());
            childSectionOdml.setType(childSection.getType());

            rootSection.add(childSectionOdml);

            for(org.g_node.nix.Property property : propertyList){
                String propertyName = property.getName();
                int dataTypeIndex = property.getDataType();

                org.g_node.nix.Value propertyValue = property.getValues().get(0);
                String valueType = dataTypeMap.get(dataTypeIndex);

                try {
                    Property propertyChildOdml;

                    switch (valueType) {
                        case "string":
                            propertyChildOdml = new Property(propertyName,propertyValue.getString(),"string");
                            propertyChildOdml.setType("string");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        case "int":
                            propertyChildOdml = new Property(propertyName,propertyValue.getInt(),"int");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        case "float":
                            propertyChildOdml = new Property(propertyName,propertyValue.getDouble(),"float");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        case "double":
                            propertyChildOdml = new Property(propertyName,propertyValue.getDouble(),"double");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        case "boolean":
                            propertyChildOdml = new Property(propertyName,propertyValue.getBoolean(),"boolean");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        case "long":
                            propertyChildOdml = new Property(propertyName,propertyValue.getLong(),"long");
                            childSectionOdml.add(propertyChildOdml);
                            break;
                        default:logger.error("ERROR. Some wrong valueType that is not supported.");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }

        }
        Writer writer = new Writer(rootSection, true, true);
        writer.write(outputstream);

    }



}