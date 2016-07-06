package com.example.pdf;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件相关
 * @author mengxc
 *
 */
public class FileUtils {

	public static String SDPATH;
    
    public String getSDPATH() {
        return SDPATH;  
    }  
    public FileUtils() {  
        //得到当前外部存储设备的目录  
        SDPATH = Environment.getExternalStorageDirectory() + "/";
        if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
		// || !Environment.isExternalStorageRemovable()
		) {
			try {
				SDPATH = Environment.getExternalStorageDirectory() + "/";
			} catch (Exception e) {
				SDPATH = Environment.getRootDirectory()+"/";
			}
		} else {
			SDPATH = Environment.getRootDirectory()+"/";
		}
          
    }  
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();  
        return file;  
    }  
      
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();  
        return dir;  
    }  
      
      
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();  
    }  
      
    public File write2SDFromInput(String path, String fileName, InputStream inputStream) {
        // TODO Auto-generated method stub  
        File file = null;
        OutputStream output = null;
        try {  
            creatSDDir(path);  
            file = creatSDFile(path + fileName);  
            output = new FileOutputStream(file);
            byte buffer [] = new byte[4 * 1024];  
            while((inputStream.read(buffer)) != -1) {  
                output.write(buffer);  
            }  
            output.flush();  
        }  
        catch(Exception e) {
            e.printStackTrace();  
        }  
        finally{  
            try{  
                output.close();  
            } catch(Exception e){
                e.printStackTrace();  
            }  
        }  
        return file;  
    }  
      
}
