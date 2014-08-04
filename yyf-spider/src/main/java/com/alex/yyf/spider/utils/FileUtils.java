package com.alex.yyf.spider.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class FileUtils {
	public static final Logger logger = Logger.getLogger(FileUtils.class);
	private static final int BLOCK_SIZE = 4096;
	
	public static File createFile(String filepath, String filename) {				
		File file = new File(filepath, filename);
		return file;
	}
	
	public static File createFile(String filename) {			
		File file = new File(filename);
		return file;		
	}
	
	  /**
	   * 拷贝文件
	   * @param fromFileType String 从那个路径下拷贝（只取路径串的第一个目录）
	   * @param toFileType String 拷贝到哪个目录
	   * @param fileName String 源文件名
	   * @param targetFileName String 目标文件名
	   * @param isCut boolean 表示是否剪切
	   */
	  public static void copyFile(String fromFileType, String toFileType, String fileName,
	                       String targetFileName, boolean isCut) throws
	      FileNotFoundException, IOException {

	  //读源文件
	    List fromPaths = paresFilePath(fromFileType);
	    /**@todo 只取第一个 path*/
	    String fromPath = fromPaths.get(0).toString();
	    File in = new File(fromPath + "/" + fileName);

	    //写到目标文件
	    List paths = paresFilePath(toFileType);
	    Iterator it = paths.iterator();
	    ArrayList outFiles = new ArrayList();
	    while (it.hasNext()) {
	      String path = it.next().toString();
	      FileOutputStream out = new FileOutputStream(path + "/" + targetFileName);
	      outFiles.add(out);
	    }
	    copy(new FileInputStream(in),outFiles);
	    if (isCut) in.delete();
	  }
	  
	  private static List paresFilePath(String path) {
		    StringTokenizer token = new StringTokenizer(path, ";");
		    ArrayList result = new ArrayList();
		    while (token.hasMoreTokens()) {
		      String temp = token.nextToken();
		      if(!temp.endsWith("\\")&&!temp.endsWith("/"))
		      	temp = temp + "/";
		      result.add(temp);
		    }
		    return result;
		  }
	  
	  private static void copy(InputStream in, List outputStreamList) throws IOException {

          try {
                  byte[] buffer = new byte[BLOCK_SIZE];
                  int nrOfBytes = -1;
                  while ((nrOfBytes = in.read(buffer)) != -1) {
                     for(int i=0;i<outputStreamList.size();i++){
                       OutputStream out = (OutputStream)outputStreamList.get(i);
                       out.write(buffer, 0, nrOfBytes);
                     }

                  }
                  for(int i=0;i<outputStreamList.size();i++){
                    ((OutputStream)outputStreamList.get(i)).flush();
                  }
          }
          finally {
                  try {
                          in.close();
                  }
                  catch (IOException ex) {
                          logger.warn("Could not close InputStream", ex);
                  }
                  try {
                    for (int i = 0; i < outputStreamList.size(); i++) {
                      ( (OutputStream) outputStreamList.get(i)).close();
                    }

                  }
                  catch (IOException ex) {
                          logger.warn("Could not close OutputStream", ex);
                  }
          }
  }
	  
	  public static void write(List<String> infos, String filePath) throws IOException{
			File file = new File(filePath);
			if(file.exists()){
				deleteFile(file);
			}
			file.createNewFile();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			for(String info : infos){
				writer.write(info);
			}
			writer.flush();
			writer.close();
	  }
	  
	  public static void writeInc(List<String> infos, String filePath) throws IOException{
			File file = new File(filePath);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			for(String info : infos){
				writer.write(info);
			}
			writer.flush();
			writer.close();
	  }
	  
	  public static void write(String info, String filePath) throws IOException{
			File file = new File(filePath);
			if(file.exists()){
				deleteFile(file);
			}
			file.createNewFile();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			
			writer.write(info);
			
			writer.flush();
			writer.close();
	  }
	  
	  public static boolean deleteFile(File file){      
	        if(file.isFile() && file.exists()){     
	            file.delete();        
	            return true;     
	        }else{   
	            return false;     
	        }     
	    } 
}
