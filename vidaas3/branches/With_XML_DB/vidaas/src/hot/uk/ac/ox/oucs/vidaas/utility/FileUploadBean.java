package uk.ac.ox.oucs.vidaas.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.jboss.seam.annotations.Name;

/**
 * @author Ilya Shaikovsky
 *
 */
@Name("fileUploadBean")
public class FileUploadBean{
    
    private ArrayList<File> files = new ArrayList<File>();
    private int uploadsAvailable = 5;
    private boolean autoUpload = false;
    private boolean useFlash = false;
    public int getSize() {
        if (getFiles().size()>0){
            return getFiles().size();
        }else {
            return 0;
        }
    }

    public FileUploadBean() {
    }
    
    public void listener(UploadEvent event) throws Exception{
        UploadItem item = event.getUploadItem();
        if(item != null){
         File file = item.getFile();
        System.out.println(item.getFileName());
        
        //byte[] itemBytes = item.getData();
        
        System.out.println(file.length());
        System.out.println(file.getAbsolutePath());
        /*
        String FILENAME = "binary.dat";
        DataOutputStream os = new DataOutputStream(new FileOutputStream(
            FILENAME));
        os.write(item.getData());
        os.close();*/
        /*Attachment file = new AttachmentHome().getInstance();
        file.setSize(item.getData().length);
        file.setName(item.getFileName());
        file.setData(item.getData());
        files.add(file);
        uploadsAvailable--;*/
        } else {
         System.out.println("Item is null .... !");
        }
    }  
      
    public String clearUploadData() {
        files.clear();
        setUploadsAvailable(5);
        return null;
    }
    
    public long getTimeStamp(){
        return System.currentTimeMillis();
    }
    
    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) { 
        this.files = files;
    }

    public int getUploadsAvailable() {
        return uploadsAvailable;
    }

    public void setUploadsAvailable(int uploadsAvailable) {
        this.uploadsAvailable = uploadsAvailable;
    }

    public boolean isAutoUpload() {
        return autoUpload;
    }

    public void setAutoUpload(boolean autoUpload) {
        this.autoUpload = autoUpload;
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public void setUseFlash(boolean useFlash) {
        this.useFlash = useFlash;
    }

}