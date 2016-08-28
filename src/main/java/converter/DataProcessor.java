package converter;

import core.ExperimentParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ipsita on 17/8/16.
 */
public class DataProcessor {

    final static Logger logger = Logger.getLogger(DataProcessor.class);

    /**
     * This takes the folder passed and unzips it, and unzips recursively all the zipped folders inside it, and then deletes the zipped files.
     * Basically, it extracts
     * @param baseDataFolder
     */
    public void findAndUnzipAnyZipInsideAndThenDeleteZip(String baseDataFolder){
        logger.info("entered findAndUnzipAnyZipInsideAndThenDeleteZip");
        String basePathToProcess = "";
        if(baseDataFolder.endsWith("zip"))
            try {
                basePathToProcess = ZipHandler.unzipFile(baseDataFolder);
                File deleteZip = new File(baseDataFolder);
                deleteZip.delete();
            } catch (IOException e) {
                logger.error("unable to unzip a zip file found",e);
            }
        else
            basePathToProcess = baseDataFolder;

        traverseFolder(basePathToProcess);
        logger.info("leaving findAndUnzipAnyZipInsideAndThenDeleteZip");
    }

    /**
     * Goes to each file or subfolder inside the given path and checks if it is a zipped folder. If yes, then it unzips it.
     * @param path : Path of the folder to traverse
     */
    public void traverseFolder(String path) {

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                traverseFolder(f.getAbsolutePath());
                logger.debug("Dir:" + f.getAbsoluteFile());
            }
            else {
                logger.debug("File:" + f.getAbsoluteFile());
                if(f.getName().endsWith(".zip")){
                    String toUnzip = f.getAbsolutePath();
                    try {
                        ZipHandler.unzipFile(toUnzip);
                    } catch (IOException e) {
                        logger.error("unable to unzip file "+toUnzip,e);
                    }
                }
            }
        }
    }

    /**
     * counts eeg and avg files in a particular datset of data-package
     * @param DataSetFiles : the list of all files in a dataset
     * @return : returns the count of eeg and avg files in a particular datset of data-package
     */
    public int countEegAvgFilesInDataSetFiles(java.io.File[] DataSetFiles){
        int count = 0;
        for(File file : DataSetFiles){
            if(file.getName().endsWith("eeg") || file.getName().endsWith("avg"))
                count++;
        }
        return count;
    }

    /**
     * checks metadata exists in a particular datset of data-package
     * @param currentDatasetDir : the list of all files in a dataset
     * @return : returns true if metadata exists in a particular datset of data-package
     */
    public Boolean seeIfMetadataExists(java.io.File currentDatasetDir){
        ArrayList<Boolean> metadataExists;
        File [] currentDirectoryContents = getDirectoryContents(currentDatasetDir.getPath());
        for(File file : currentDirectoryContents){
            if(file.getName().equalsIgnoreCase("metadata.xml"))
                return true;
        }
        return false;
    }

    /**
     * checks header file exists in a particular datset of data-package
     * @param currentDatasetDir : the list of all files in a dataset
     * @return : returns true if header file exists in a particular datset of data-package
     */
    public Boolean checkIfVhdrExists(String currentDatasetDir){
        File [] currentDirectoryContents = getDirectoryContents(currentDatasetDir);
        for(File file : currentDirectoryContents){
            if(file.getName().endsWith(".vhdr"))
                return true;
        }
        return false;
    }


    /**
     * checks marker file exists in a particular datset of data-package
     * @param currentDatasetDir : the list of all files in a dataset
     * @return : returns true if marker file exists in a particular datset of data-package
     */
    public Boolean checkIfVmrkExists(String currentDatasetDir){
        File [] currentDirectoryContents = getDirectoryContents(currentDatasetDir);
        for(File file : currentDirectoryContents){
            if(file.getName().endsWith(".vmrk"))
                return true;
        }
        return false;
    }


    public String getDataFolderPath(String datasetDirectoryPath){
        String dataFolderPath=datasetDirectoryPath + "/Data";
        java.io.File[] allContents = getDirectoryContents(dataFolderPath);
        for(File file :allContents){
            if(file.getName().equals("Data"))
                dataFolderPath = file.getPath();
        }
        return dataFolderPath;
    }
    /**
     * The main method which takes the zip folder, derives the dataset files, and calls the parser to read them and convert each into HDF5 file
     * @param baseDataFolder : the base folder which is the entire data-package unzipped
     * @param datasetDirectories : the list of all the directories listed inside the data-package folder
     */
    public void processAllDataSetsFinal(String baseDataFolder, java.io.File[] datasetDirectories) throws Exception {
        logger.info("====== STEP 2 - processAllDataSetsFinal =========");
        int countOfEegAvgFiles=0;
        boolean metadataExists;
        int count=0;
        for(int i=0; i<datasetDirectories.length; i++){
            count = 0;

            //String currentExpDataFolderPath = datasetDirectories[i].getPath()+"/Data/";
            String currentExpDataFolderPath = getDataFolderPath(datasetDirectories[i].getPath());
            logger.debug(">> i=" + i + ", currentExpDataFolderPath : " + currentExpDataFolderPath);

            findAndUnzipAnyZipInsideAndThenDeleteZip(currentExpDataFolderPath);

            java.io.File[] DataSetFiles = getDirectoryContents(currentExpDataFolderPath);
            metadataExists = seeIfMetadataExists(datasetDirectories[i]);
            logger.debug("Going to create hdf5 files directory");
            java.io.File h5FileLocation = new java.io.File(datasetDirectories[i].getPath() + "/H5FileLocation");
            h5FileLocation.mkdir();
            logger.debug("h5FileLocation : " + h5FileLocation.getPath());

            countOfEegAvgFiles = countEegAvgFilesInDataSetFiles(DataSetFiles);

            logger.debug("Total eeg/avg files in this experiment: " + countOfEegAvgFiles);
            for (int j = 0; j < DataSetFiles.length; j++) {

                Boolean vhdrExists;
                Boolean vmrkExists;
                if (DataSetFiles[j].isDirectory()) {
                    logger.debug("Current : " + DataSetFiles[j].getPath() + " -> Found it is a directory. Gonna remap DataSetFiles.");
                    DataSetFiles = getDirectoryContents(DataSetFiles[j].getPath());
                    countOfEegAvgFiles = countEegAvgFilesInDataSetFiles(DataSetFiles);
                    logger.debug("UPDATED > Total eeg/avg files in this experiment: "+countOfEegAvgFiles);
                    j = -1;
                    continue;
                }
                else{
                    logger.debug("current file -> " + DataSetFiles[j].getName());
                    if(DataSetFiles[j].getName().endsWith(".eeg")){
                        count++;

                        String rootFileName = DataSetFiles[j].getPath().replace(".eeg", "");
                        logger.debug("rootFileName : " + rootFileName);

                        String fileName = FilenameUtils.getBaseName(rootFileName);
                        logger.debug("Four Parameters gonna pass : " + "\n>>>>" + h5FileLocation + "/" + fileName.replace(".eeg", "") + ".h5 \n>>>>"
                                + datasetDirectories[i].getPath() + "/metadata.xml \n>>>>"
                                + rootFileName + ".eeg\n>>>>"
                                + rootFileName + ".vhdr\n>>>>"
                                + rootFileName + ".vmrk");

                        vhdrExists = checkIfVhdrExists( DataSetFiles[j].getParent());
                        vmrkExists = checkIfVmrkExists( DataSetFiles[j].getParent());

                        ExperimentParser experimentParser = new ExperimentParserImpl();
                        boolean last = false;
                        if (i == datasetDirectories.length - 1 && count == countOfEegAvgFiles) {
                            last = true;
                        }

                        experimentParser.parseExperiment(h5FileLocation + "/" + fileName.replace(".eeg", "") + ".h5",
                                datasetDirectories[i].getPath() + "/metadata.xml",
                                rootFileName + ".eeg", rootFileName + ".vhdr",
                                rootFileName + ".vmrk", last, metadataExists, vhdrExists, vmrkExists);

                    }
                    else if(DataSetFiles[j].getName().endsWith(".avg")){
                        count++;

                        String rootFileName = DataSetFiles[j].getPath().replace(".avg", "");
                        logger.debug("rootFileName : " + rootFileName);

                        String fileName = FilenameUtils.getBaseName(rootFileName);
                        logger.debug("Four Parameters : " + "\n>>>>" + h5FileLocation + "/" + fileName.replace(".avg", "") + ".h5 \n>>>>"
                                + datasetDirectories[i].getPath() + "/metadata.xml \n>>>>"
                                + rootFileName + ".avg\n>>>>"
                                + rootFileName + ".vhdr\n>>>>"
                                + rootFileName + ".vmrk");

                        vhdrExists = checkIfVhdrExists( DataSetFiles[j].getParent());
                        vmrkExists = checkIfVmrkExists( DataSetFiles[j].getParent());

                        ExperimentParser experimentParser = new ExperimentParserImpl();
                        boolean last = false;
                        if(i==datasetDirectories.length-1 && count == countOfEegAvgFiles){
                            last = true;
                        }

                        experimentParser.parseExperiment(h5FileLocation + "/" + fileName.replace(".avg", "") + ".h5",
                                datasetDirectories[i].getPath() + "/metadata.xml",
                                rootFileName + ".avg", rootFileName + ".vhdr",
                                rootFileName + ".vmrk", last, metadataExists, vhdrExists, vmrkExists);
                    }
                }
            }
            logger.debug("deleting this metadata.xml -> " + datasetDirectories[i].getAbsolutePath() + "/metadata.xml");
            java.io.File toDeleteFile = new java.io.File(datasetDirectories[i].getAbsolutePath()+"/metadata.xml");
            toDeleteFile.delete();
            logger.debug("Deleting files in folder : " + currentExpDataFolderPath);
            deleteRedundantFiles(new java.io.File(currentExpDataFolderPath));
            logger.info("====== STEP 2 done =========");
        }
    }

    /**
     * Deletes files which are no longer required in the folder containing converted h5 files
     * @param file : redundant file to be deleted
     */
    void deleteRedundantFiles(java.io.File file) {
        java.io.File[] contents = file.listFiles();
        if (contents != null) {
            for (java.io.File f : contents) {
                deleteRedundantFiles(f);
            }
        }
        file.delete();
    }

    /**
     * Retrieves the name of all datasets inside the dataset-package folder
     * @param baseDataFolder : the base dataset-package folder
     * @return : the list of datasets inside the dataset-package folder
     */
    public java.io.File[] getDataSetNames(String baseDataFolder){
        java.io.File[] directories = new java.io.File(baseDataFolder).listFiles(new FileFilter() {
            @Override
            public boolean accept(java.io.File file) {
                return file.isDirectory();
            }
        });

        return directories;
    }

    /**
     * Lists out all the directory contents
     * @param baseDataFolder : the name of the folder to list out the directory contents for
     * @return : the list of all the files inside the basedataFolder
     */
    public java.io.File[] getDirectoryContents(String baseDataFolder){
        logger.info("entering getDirectoryContents");
        java.io.File[] directoryFiles = new java.io.File(baseDataFolder).listFiles();
        logger.info("\nleaving getDirectoryContents");
        return directoryFiles;
    }

    /**
     * It initiates the process of conversion of datasets into HDF5 format
     * @param inputZipFile :the zipped input data-package
     * @return the folder name containing the datasets converted into HDF5 format
     * @throws IOException
     */
    public String generateConvertedDataSet(String inputZipFile) throws Exception {
        logger.info("coming in generateConvertedDataSet");
        logger.info("====== STEP 1 - UNZIPPING FILES =======");
        String unzippedFolderName=ZipHandler.unzipFile(inputZipFile);
        logger.info("====== STEP 1 done =======");
        java.io.File[] datasetDirectories = getDataSetNames(unzippedFolderName);
        processAllDataSetsFinal(unzippedFolderName, datasetDirectories);

        return unzippedFolderName;
    }
}
