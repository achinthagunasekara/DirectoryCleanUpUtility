package directorycleanuputility;

import Config.ConfigFileReader;
import Config.CleanUpAfterUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.*;

/*
 *
 * @author Archie Gunasekara
 * @date 2015.06.22
 * 
 */
public class DirectoryCleanUpUtility {
    
    private StoreObject storedData;
    private final DateTime today;
    private final String path;
    private final boolean dryRun;
    private static Log log;

    public static void main(String[] args) {
        
        try {
            
            DirectoryCleanUpUtility dcu = new DirectoryCleanUpUtility();
            dcu.go();
        }
        catch(IOException ioEx) {

            System.out.println(ioEx.getMessage());
        }
        catch(Exception ex) {

            System.out.println(ex);
        }        
    }
    
    public DirectoryCleanUpUtility() throws IOException, Exception {
        
        log = LogFactory.getLog(DirectoryCleanUpUtility.class);
        today = new DateTime();
        path = ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("directory");
        dryRun = Boolean.parseBoolean(ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("dry_run"));
    }
    
    public void go() throws IOException, Exception {
        
        if(dryRun) {
            
            log.info("ONLY A DRY RUN, NO FILES WILL BE DELETED!");
        }
        
        ArrayList<File> files = new ArrayList<File>();
        files = listFiles(path, files);

        storedData = getSavedData();

        //data file is not found. We'll create one
        if(storedData == null) {
                
            storedData = new StoreObject();
        }
        
        CleanUp(files);
        //clean up datastore
        ArrayList<String> allFilesAndDirs = new ArrayList<String>();
        allFilesAndDirs = getAllFilesAndDirs(path, allFilesAndDirs);
        storedData.cleanUpDataStore(allFilesAndDirs);
        //save current directory/file state to a file
        storeData(storedData);
        
    }
    
    private void CleanUp(ArrayList<File> files) throws IOException, Exception {
        
        for (File file : files) {

            String fileName = file.getAbsoluteFile().toString();
            ArrayList<File> subFile = new ArrayList<File>();
            DateTime createdDate;
            
            if (file.isDirectory()) {
                
                //get the created date for the directory
                createdDate = storedData.getDirectory(fileName);
                
                //only clean up if the directory details are in the data file
                if (createdDate instanceof DateTime) {
                    
                    //do nothing
                }
                else {
                    
                    log.info("Adding Dir to the DB - " + file);
                    storedData.addDirectory(fileName, today);
                }
                //catalog sub directories
                log.info("Calling CleanUp method on - " + fileName);
                CleanUp(listFiles(fileName, subFile));
            }
            else {
                
                //get the created date for the file
                createdDate = storedData.getFile(fileName);
                
                if (createdDate instanceof DateTime) {
                    
                    if (getDeleteByDate(createdDate).isBefore(today)) {

                        log.info("Deleting File - " + file);
                        
                        //only delete files if this isn't a dry run
                        if(!dryRun) {
                        
                            file.delete();
                            //remove the entry from stored data
                            storedData.removeFile(fileName);
                        }
                    }
                }
                else {
                    
                    //file not found in the stored data. It needs to be added
                    log.info("Adding File to the DB - " + file);
                    storedData.addFile(fileName, today);
                }
            }
            //check for empty directories and delete them if they are older than N number of units
            if (file.isDirectory()) {
                
                if (createdDate instanceof DateTime) {
                    
                    if (getDeleteByDate(createdDate).isBefore(today) && file.list().length == 0) {
                        
                        log.info("Deleting Dir - " + file + " (Item count in dir is " + file.list().length + ". This should be 0)");
                        
                        if(!dryRun) {
                        
                            file.delete();
                            //remove the entry from stored data
                            storedData.removeDirectory(fileName);
                        }
                    }
                }
            }
        }
    }
    
    private ArrayList<File> listFiles(String directoryName, ArrayList<File> files) {
               
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        files.addAll(Arrays.asList(fList));
        return files;
    }
        
    private void storeData(StoreObject data) throws IOException {
        
        String dataFile = ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("data_file");
        FileOutputStream fileOutStream = new FileOutputStream(dataFile);
        ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
        objOutStream.writeObject(data);
        objOutStream.close();
    }
    
    private StoreObject getSavedData() throws IOException, ClassNotFoundException {
        
        StoreObject data = null;
        String dataFile = ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("data_file");
        FileInputStream fileInStream;

        try {
            
            fileInStream = new FileInputStream(dataFile);
            ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
            data = (StoreObject)objInStream.readObject();
            objInStream.close();
        }
        catch (IOException ioEx) {
            
            //Data file not found
            return null;
        }
        
        return data;
    }
    
    private DateTime getDeleteByDate(DateTime createdDate) throws IOException, Exception {
     
        CleanUpAfterUnit.Units cleanUpAfterUnit = CleanUpAfterUnit.Units.valueOf(ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("clean_up_after_unit")); 
        int cleanUpAfterValue = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyFor("clean_up_after_value"));
        
        if(cleanUpAfterUnit == CleanUpAfterUnit.Units.MINUTES) {
            
            return createdDate.plusMinutes(cleanUpAfterValue);
        }
        else if(cleanUpAfterUnit == CleanUpAfterUnit.Units.HOURS) {
            
            return createdDate.plusHours(cleanUpAfterValue);     
        }
        else if(cleanUpAfterUnit == CleanUpAfterUnit.Units.DAYS) {
            
            return createdDate.plusDays(cleanUpAfterValue);
        }
        else if(cleanUpAfterUnit == CleanUpAfterUnit.Units.MONTHS) {
            
            return createdDate.plusMonths(cleanUpAfterValue);
        }
        else {
            
            throw new Exception("Unknown clean_up_after_unit value in the configration. Only MINUTES, HOURS, DAYS and MONTHS are accepted");
        }
    }
    
    // get all the files from a directory
    private ArrayList<String> getAllFilesAndDirs(String path, ArrayList<String> allFilesAndDirs) {
               
        File directory = new File(path);
        File[] fList = directory.listFiles();
        
        for(File file : fList) {

            if(file.isDirectory()) {
                
                allFilesAndDirs.add(file.getAbsolutePath());
                getAllFilesAndDirs(file.getPath(), allFilesAndDirs);
            }
            else {
                
                allFilesAndDirs.add(file.getAbsolutePath());
            }
        }
        
        return allFilesAndDirs;
    }
}
