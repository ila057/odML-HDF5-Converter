import org.g_node.nix.*;
import org.g_node.nix.File;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public class ParseMetadata {

    public static void main(String[] args){

        String filename = "metadata.xml";
        java.io.File inputFile = new java.io.File(filename);

        try {
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :"
                    + doc.getDocumentElement().getNodeName());

            //create hdf5 file
            String fileName = "file_create_example.h5";
            // create a new file overwriting any existing content
            org.g_node.nix.File file = org.g_node.nix.File.open(fileName, FileMode.Overwrite);
            // create a block
            Block b = file.createBlock("test block","test");


            NodeList versionList = doc.getElementsByTagName("version");
            String version = versionList.item(0).getTextContent();

            NodeList dateList = doc.getElementsByTagName("date");
            String date = dateList.item(0).getTextContent();

            System.out.println("version : "+version);
            System.out.println("date : "+date);

            // create section and add a property
            Section sectionMetadata = file.createSection("metadataSection", "metadata");
            b.setMetadata(sectionMetadata);

            //for parsing
            NodeList sectionList = doc.getElementsByTagName("section");

            for(int temp = 0; temp < sectionList.getLength(); temp++){

                Node thisSection = sectionList.item(temp);
                System.out.println("temp : " + temp + " | Current section : " + thisSection.getNodeName());

                NodeList sectionChildren = thisSection.getChildNodes();

                String typeOfSection = "";
                String nameOfSection = "";

                System.out.println("Entering the loop for section children...");
                //do for each child of this section :
                for(int i = 0; i < sectionChildren.getLength(); i++){

                    Node sectionChild = sectionChildren.item(i);

                    if(sectionChild.getNodeName().equalsIgnoreCase("type")){
                        //assign type to the section
                        typeOfSection =  sectionChild.getTextContent();
                    }
                    else if(sectionChild.getNodeName().equalsIgnoreCase("name")){
                        //assign name to the section
                        nameOfSection = sectionChild.getTextContent();
                    }
                }
                System.out.println("Exiting the loop for section children..., typeOfSection : "+typeOfSection+" && nameOfSection : "+nameOfSection);

                // hdf5 creating subsection of metadata section (root)
                Section secChild = sectionMetadata.createSection(nameOfSection,typeOfSection);
                System.out.println("Added this section into metadataSection");

                System.out.println("Entering loop again for section properties...");
                for(int i=0; i<sectionChildren.getLength(); i++){
                    Node sectionChild = sectionChildren.item(i);

                    if(sectionChild.getNodeName().equalsIgnoreCase("property")){
                        System.out.println("For i = "+i+", Houston, we have a property!");
                        String nameOfProperty = "";
                        Value valueOfProperty = new Value(-9999);  // default value since creating property requires a value and some properties don't have one

                        NodeList propertyChildren = sectionChild.getChildNodes();

                        System.out.println("-- Entering loop for property children...");
                        for(int j=0; j < propertyChildren.getLength(); j++){
                            Node propertyChild = propertyChildren.item(j);

                            if(propertyChild.getNodeName().equalsIgnoreCase("name")){

                                nameOfProperty = propertyChild.getTextContent();
                                System.out.println("Name of property found = "+nameOfProperty);

                            }
                            else if(propertyChild.getNodeName().equalsIgnoreCase("value")){

                                NodeList valueDetails = propertyChild.getChildNodes();
                                String value = valueDetails.item(0).getTextContent().split("\n")[1].trim();
                                String valueType = valueDetails.item(1).getTextContent();
                                System.out.println("Value = "+value+", valueType = "+valueType);
                                valueOfProperty = new Value();
                                switch (valueType){
                                    case "datetime":    valueOfProperty.setString(value);
                                                        break;
                                    case "int":         valueOfProperty.setInt(Integer.parseInt(value));
                                                        break;
                                    case "float":      valueOfProperty.setDouble(Double.parseDouble(value));
                                                        break;
                                    case "boolean":        valueOfProperty.setBoolean(Boolean.parseBoolean(value));
                                                        break;
                                    case "string":      valueOfProperty.setString(value);
                                                        break;
                                    case "long":        valueOfProperty.setLong(Long.parseLong(value));
                                                        break;
                                    default:    System.out.println("Some wrong valueType. valueType : "+valueType);
                                }
                            }
                        }
                        System.out.println("-- Exiting loop for property children...");

                        secChild.createProperty(nameOfProperty,valueOfProperty);


                    }
                }
                System.out.println("Exiting loop for section properties...");


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


/*

    //each iteration will be a new property
                        //make a property
                        //add it into that array with index i
                        //each property will have attributes
//                        System.out.print("found property : ");
                        NodeList propertyChildren = sectionChild.getChildNodes();

                        String nameOfProperty = "";
                        String valueOfProperty = "";

                        for(int j=0;j<propertyChildren.getLength();j++){


                            Node childTag = propertyChildren.item(j);

                            if(childTag.getNodeName().equalsIgnoreCase("name")){
                                //assign name to property
                                nameOfProperty = childTag.getNodeName();
//                                System.out.print(childTag.getTextContent()+" -> ");
                            }
                            else if(childTag.getNodeName().equalsIgnoreCase("value")){
                                //assign value to property
                                valueOfProperty = childTag.getNodeName();
//                                System.out.print(childTag.getTextContent().split("\n")[1]);
                            }
                            else{
                                //extra attributes...donno what to do.
                            }
 */