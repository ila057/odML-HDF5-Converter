package converter;

import core.MetadataParser;
import cz.zcu.kiv.signal.ChannelInfo;
import cz.zcu.kiv.signal.EEGMarker;
import cz.zcu.kiv.signal.VhdrReader;
import odml.core.Reader;
import org.g_node.nix.Block;
import org.g_node.nix.Property;
import org.g_node.nix.Section;
import org.g_node.nix.Value;
import org.apache.log4j.*;


import java.io.*;
import java.util.*;


/**
 * Created by ipsita on 23/6/16.
 */
public class MetadataParserImpl implements MetadataParser {

    final static Logger logger = Logger.getLogger(MetadataParserImpl.class);

    /**
     * Initialises the odML file reader
     * @param metadataFile : the metadata odML file to read
     * @return : the rootSextion of the the odML file
     */
    public odml.core.Section initializeODMLReader(String metadataFile){
        logger.info("Entering initializeODMLReader");
        Reader reader = new Reader();
        odml.core.Section rootSection;
        InputStream inputstream = null;
        try {
            inputstream = new FileInputStream(metadataFile);
            rootSection = reader.load(inputstream, true);
            inputstream.close();
        } catch (Exception e) {
            logger.error("Exception occurred while loading  and reading odML input file. Please check if correct metadata file is provided "+e);
            inputstream = null;
            throw new RuntimeException("Exception occurred while loading  and reading odML input file. Please check if correct metadata file is provided ",e);
        }
        logger.info("leaving initializeODMLReader");
        return rootSection;
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
    public void setMetadata(String metadataFile, Block block, org.g_node.nix.File file, String headerFile, String markerFile, boolean metadataExists) throws IOException {
        logger.info("entering setMetadata");

        // create section and add a property
        Section rootSectionMetadata = file.createSection("metadataSection", "metadata");
        block.setMetadata(rootSectionMetadata);
        logger.info("created a metadataSection and added it to the block.");

        if(metadataExists){

            odml.core.Section rootSection = initializeODMLReader(metadataFile);
            logger.info("rootSection initialized");

            String version = rootSection.getDocumentVersion();
            String date = rootSection.getDocumentDate().toString();

            logger.info("version : " + version);
            logger.info("date : " + date);

            //for parsing
            Vector<odml.core.Section> sectionVector = rootSection.getSections();
            if(sectionVector.size()>0){
                setSection(rootSectionMetadata, sectionVector);
            }
        }
        else{
            logger.warn("no metadata.xml exists for this experiment.");
        }

        addHeaderAndMarkerInfo(rootSectionMetadata, headerFile, markerFile);
        logger.info("added header and marker file info");

        rootSectionMetadata.setNull();
        logger.info("leaving setMetadata method");

    }

    /**
     * Supports adding of channel and marker information to the metadata file
     * @param parentSection : the setion in which header and marker info is to be added
     * @param headerFile : the header file which contains channel info
     * @param markerFile : the marker file which contains marker info
     * @throws IOException
     */
    public void addHeaderAndMarkerInfo(Section parentSection, String headerFile, String markerFile) throws IOException {
        logger.info("entering addHeaderAndMarkerInfo");

        List<ChannelInfo> channelInfo = getChannelInfo(headerFile);
        setChannelInfo(parentSection, channelInfo);

        HashMap<String, EEGMarker> markerInfo = getMarkerInfo(markerFile);
        setMarkerInfo(parentSection, markerInfo);
        logger.info("leaving addHeaderAndMarkerInfo");

    }

    /**
     * Sets channel info in the HDF5 file metadata
     * @param parentSection : parent section in which metadata is set
     * @param channelInfo : the channel info to be set
     */
    public void setChannelInfo( Section parentSection, List<ChannelInfo> channelInfo){
        logger.info("entering setChannelInfo");

        Section channelSection = parentSection.createSection("Channel Infos", "Channel");
        Iterator itr = channelInfo.iterator();
        int i=0;
        while(itr.hasNext()){
            i++;

            ChannelInfo channelInfoItem = (ChannelInfo)itr.next();
            Section channelItemSection = channelSection.createSection("Channel"+channelInfoItem.getNumber(), "Channel");

            Value channelNumber = new Value("");
            channelNumber.setInt(channelInfoItem.getNumber());
            channelItemSection.createProperty("Number", channelNumber);

            Value channelName = new Value("");
            channelName.setString(channelInfoItem.getName());
            channelItemSection.createProperty("Name", channelName);

            Value channelUnits = new Value("");
            channelUnits.setString(channelInfoItem.getUnits());
            channelItemSection.createProperty("Units", channelUnits);

            Value channelResolution = new Value("");
            channelResolution.setDouble(channelInfoItem.getResolution());
            channelItemSection.createProperty("Resolution", channelResolution);

            channelNumber.setNull();
            channelName.setNull();
            channelUnits.setNull();
            channelResolution.setNull();
            channelItemSection.setNull();

            logger.info(i + " Channel: " + channelInfoItem.getNumber()+" "+ channelInfoItem.getName()+" "+channelInfoItem.getUnits()+" "+channelInfoItem.getResolution());
        }
        channelSection.setNull();
        logger.info("leaving setChannelInfo");

    }

    /**
     * Sets marker info in the HDF5 file metadata
     * @param parentSection : parent section in which metadata is set
     * @param markerInfo : the marker info to be set
     */
    public void setMarkerInfo(Section parentSection, HashMap<String, EEGMarker> markerInfo){
        logger.info("entering setMarkerInfo");
        Section markerSection = parentSection.createSection("Marker Infos", "Marker");
        logger.info("\nMarkers Info: ");

        Iterator markerItr = markerInfo.entrySet().iterator();
        int i=0;

        Set<String> markerKey = markerInfo.keySet();

        Iterator keyitr = markerKey.iterator();

        while(markerItr.hasNext() && keyitr.hasNext()){
            i++;
            String key = keyitr.next().toString();
            EEGMarker markerItem = markerInfo.get(key);

            Section markerItemSection = markerSection.createSection(key, "Marker"+i);

            Value markerName = new Value("");
            markerName.setString(markerItem.getName());
            markerItemSection.createProperty("Name", markerName);

            Value markerPosition = new Value("");
            markerPosition.setInt(markerItem.getPosition());
            markerItemSection.createProperty("Position", markerPosition);

            Value markerStimulus = new Value("");
            markerStimulus.setString(markerItem.getName());
            markerItemSection.createProperty("Stimulus", markerStimulus);

//            markerName.setNull();
//            markerPosition.setNull();
//            markerStimulus.setNull();
//            markerItemSection.setNull();

            logger.info(i+" Marker: "+ markerItem.getName()+" "+markerItem.getPosition()+" "+markerItem.getStimulus());
        }
//        markerSection.setNull();
        logger.info("leaving setMarkerInfo");

    }

    /**
     * Sets section related information in metdata of HDF5 file
     * @param parentSection : the parent section in shich sub sections are to be added
     * @param sectionVector : the subsections to be added in the parent section
     */
    public void setSection(Section parentSection, Vector<odml.core.Section> sectionVector){
        logger.info("entering setSection");

        for (int currentSectionIndex = 0; currentSectionIndex < sectionVector.size(); currentSectionIndex++)
        {

            odml.core.Section thisSection = sectionVector.get(currentSectionIndex);
            logger.debug("currentSectionIndex : " + currentSectionIndex + " | Current section name : " + thisSection.getName());

            String typeOfSection = "";
            String nameOfSection = "";
            Vector<odml.core.Property> propertiesList = new Vector<>();

            if(thisSection.getType()!=null){
                typeOfSection = thisSection.getType();
            }

            if(thisSection.getName()!=null){
                nameOfSection = thisSection.getName();
            }
            // hdf5 creating subsection of metadata section (root)
            Section secChild = parentSection.createSection(nameOfSection, typeOfSection);

            //this is the recursive function since a section may have a section inside it, and so on.
            if(thisSection.getSections()!=null && thisSection.getSections().size()>0){
                setSection(secChild, thisSection.getSections());
            }

            if(thisSection.getProperties()!=null){
                propertiesList = thisSection.getProperties();
            }
            setProperties(secChild, propertiesList);
        }

        logger.info("leaving setSection");

    }

    /**
     * Sets Property related information in metdata of HDF5 file
     * @param parentSec : the parent section in shich sub sections are to be added
     * @param propertiesList : the properties to be added in the parent section
     */
    public void setProperties(Section parentSec, Vector<odml.core.Property> propertiesList){
        logger.info("entering setProperties");

        //for each child of section (for property)
        if(propertiesList!=null && !propertiesList.isEmpty()){
            String nameOfProperty = "";
            Value valueOfProperty = new Value("");
            for(int tempProperty = 0; tempProperty < propertiesList.size(); tempProperty++) {
                odml.core.Property thisProperty = propertiesList.get(tempProperty);
                logger.debug("    tempProperty : " + tempProperty + " | Current property : " + thisProperty.getName());
                if(thisProperty.getName()!=null){
                    nameOfProperty = thisProperty.getName();
                }

                //logger.info("-----------type: "+ thisProperty.getWholeValue().getMap().get("type") + " | value " + thisProperty.getValue());
                if(thisProperty.valueCount()>0){
                    odml.core.Value wholeValue = thisProperty.getWholeValue();
                    String value = thisProperty.getValue().toString();
                    String valueType= wholeValue.getMap().get("type").toString();
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
                            logger.error("ERROR. Some wrong valueType. valueType : " + valueType);
                    }
                }
                processGUINamespaces(parentSec, thisProperty);
                Property prop = parentSec.createProperty(nameOfProperty, valueOfProperty);
                prop.setNull();
            }

        }
        logger.info("leaving setProperties");

    }

    /**
     * Extracts channel info from vhdr file
     * @param vhdrFile : th vhdr file from which channel info is to be extracted
     * @return : the channel info extracted from vhdr file
     * @throws IOException
     */
    public List<ChannelInfo> getChannelInfo(String vhdrFile) throws IOException {
        logger.info("entering getChannelInfo");
        byte[] inputHeaderFIle = convertToByteArray(vhdrFile);
        VhdrReader vhdrReader = new VhdrReader();
        vhdrReader.readVhdr(inputHeaderFIle);
        List<ChannelInfo> channelInfo = vhdrReader.getChannels();
        logger.info("leaving getChannelInfo");
        return channelInfo;

    }

    /**
     * Extracts marker info from vhdr file
     * @param vmrkFile : th vmrk file from which marker info is to be extracted
     * @return : the marker info extracted from vmrk file
     * @throws IOException
     */
    public HashMap<String, EEGMarker> getMarkerInfo(String vmrkFile) throws IOException {
        logger.info("entering getMarkerInfo");
        byte[] inputMarkerFIle = convertToByteArray(vmrkFile);
        VhdrReader vhdrReader = new VhdrReader();
        vhdrReader.readVmrk(inputMarkerFIle);
        HashMap<String, EEGMarker> markers = vhdrReader.getMarkers();
        logger.info("leaving getMarkerInfo");
        return markers;

    }

    /**
     * Converts stream to byte array to be written into metadata file
     * @param inputFile : file to convert to byte array
     * @return : the byte array which is  input file
     * @throws IOException
     */
    public byte[] convertToByteArray(String inputFile) throws IOException {
        logger.info("entering convertToByteArray");

        FileInputStream fileInputStream=null;

        File file = new File(inputFile);

        byte[] bFile = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
           logger.info("File converted to Byte Array");
        }catch(IOException e){
            logger.error("Error while opening/reading file.",e);
            throw new IOException("Error while opening/reading file.");
        }
        logger.info("leaving convertToByteArray");
        return bFile;
    }

    /**
     * Processes and adds GUI namespaces into the metadata file
     * @param parentSec : The section to which GUI namespaces are to be added
     * @param property : The property for which GUI namespaces are to be added
     */
    public void processGUINamespaces(Section parentSec, odml.core.Property property){
        logger.info("entering processGUINamespaces");

        Section guiSection = parentSec.createSection(property.getName(), "GUI:Namespace");

        List list = property.getGuiHelper().getGUINamespaceTags();
        for(int i=0; i<list.size(); i++){
            org.jdom.Element guiElement = (org.jdom.Element) list.get(i);
            String elementName = new String("gui_"+guiElement.getName());
            String elementValue = guiElement.getValue();
            logger.debug("->"+list.get(i));

            Value value = new Value(elementValue);
            guiSection.createProperty(elementName, value);
            value.setNull();
        }
        guiSection.setNull();
        logger.info("leaving processGUINamespaces");

    }

}