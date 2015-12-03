package directorycleanuputility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.*;

/*
 *
 * @author Archie Gunasekara
 * @date 2015.06.22
 * 
 */
public class StoreObject implements Serializable{
    
    //To store File data
    private HashMap<String, DateTime> files;
    //to store directory data
    private HashMap<String, DateTime> directories;
    private static Log log;
    
    public StoreObject() {
    
        files = new HashMap<>();
        directories = new HashMap<>();
    }
    
    public void addFile(String fileName, DateTime date) {
        
        files.put(fileName, date);
    }
    
    public void addDirectory(String directoryName, DateTime date) {
        
        directories.put(directoryName, date);
    }
    
    public void removeFile(String fileName) {
        
        files.remove(fileName);
    }
    
    public void removeDirectory(String directoryName) {
        
        directories.remove(directoryName);
    }
    
    public DateTime getFile(String fileName) {
        
        return files.get(fileName);
    }
    
    public DateTime getDirectory(String directoryName) {
        
        return directories.get(directoryName);
    }
    
    public void cleanUpDataStore(ArrayList<String> allFilesAndDirs) {

        log = LogFactory.getLog(StoreObject.class);
        log.info("Running clean up DB for files list");
        cleanUpHashMap(files, allFilesAndDirs);
        log.info("Running clean up DB for directories list");
        cleanUpHashMap(directories, allFilesAndDirs);
    }
    
    private void cleanUpHashMap(HashMap<String, DateTime> map, ArrayList<String> allFilesAndDirs) {
        
        ArrayList<String> toBeRemoved = new ArrayList<String>();
        Iterator it = map.entrySet().iterator();
        
        while (it.hasNext()) {
            
            Map.Entry pair = (Map.Entry)it.next();
            
            //System.out.println(pair.getKey());
            
            if(!allFilesAndDirs.contains(pair.getKey())) {
            
                toBeRemoved.add(pair.getKey().toString());
            }
        }
        
        //remove the items from the map
        for(String s : toBeRemoved) {
            
            //log.info(s + " not found in the directory. Removing from the DB");
            map.remove(s);
        }
    }
}