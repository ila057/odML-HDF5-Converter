package converter;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by lion on 18/8/16.
 */
public class ZipHandler {

    final static Logger logger = Logger.getLogger(ZipHandler.class);

    public static void main(String args[]){
        //String finalConvertedFolderToBeZipped = args[0];
        String finalConvertedFolderToBeZipped = "Mice_blindness";

        try {
            ZipHandler.zipDir(finalConvertedFolderToBeZipped+"_h5.zip",finalConvertedFolderToBeZipped);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * unzips a given zip file
     * @param zipFileName : Zip-File name
     * @return the name of the unzipped folder
     * @throws IOException
     */
    public static String unzipFile(String zipFileName) throws IOException {

        byte[] buffer = new byte[1024];

        String tempFolder = zipFileName.replace(".zip","");
        //String tempFolder = "tempFolderForH5Conversion";
        ZipInputStream zis = null;
        try {
            //create output directory is not exists
            java.io.File folder = new java.io.File(tempFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //get the zip file content
            zis = new ZipInputStream(new FileInputStream(zipFileName));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                if (ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }

                String fileName = ze.getName();
                java.io.File newFile = new java.io.File(tempFolder + java.io.File.separator + fileName);

                logger.debug("file unzipped : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new java.io.File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            logger.debug("Unzipped successfully");

        }catch (IOException e) {
            logger.error("could not unzip",e);
            throw new IllegalStateException("could not unzip",e);
        }
        finally {
            zis.closeEntry();
            zis.close();
        }
        return tempFolder;
    }

    /**
     * Zips a given file
     * @param zipFileName : the name of zipped file which is to be created
     * @param dir : the directory to be zipped
     * @throws IOException
     */
    public static void zipDir(String zipFileName, String dir) throws IOException {
        File dirObj = new File(dir);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        System.out.println("Creating : " + zipFileName);
        addDir(dirObj, out);
        out.close();
    }

    /**
     * Adds directories to the zipped file
     * @param dirObj : the directory to be added into the zipped file
     * @param out
     * @throws IOException
     */
    static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out);
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getPath());
            System.out.println(" Adding: " + files[i].getPath());
            out.putNextEntry(new ZipEntry(files[i].getPath()));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }
}
