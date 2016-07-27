///**
// * Created by ipsita on 22/6/16.
// */
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.Vector;
//import javax.swing.tree.TreeNode;
//
//import odml.core.Reader;
//import odml.core.Section;
//import odml.core.Property;
////import odml.core.
//
//
//public class OdMLReader {
//    public static void main(String[] args) {
//
//
//
//
//
//        //System.out.println("..."+rootSection.getChildCount());
//        System.out.println("DATE NAHI DIKHAT KYA? : " + rootSection.getDocumentVersion());
//
//        Vector<Section> sections = rootSection.getSections();
//        for (int i=0; i<sections.size(); i++){
//            Section section = sections.get(i);
//            System.out.println("------------------");
//            System.out.println(section.getName());
//            Vector<Property> properties = section.getProperties();
//            for (int p =0; p<properties.size(); p++){
//                Property property = properties.elementAt(p);
//                System.out.print("Name: " + property.getName());
//                boolean required = property.getGuiHelper().getRequired();
//                System.out.println("..."+required);
////                Vector<Object> vec = property.getPropertyAsVector();
////                for(int u=0;u<vec.size();u++){
////                    System.out.print("| "+vec.get(u));
////                }
//            }
//            System.out.println("------------------");
//        }
//    }
//}