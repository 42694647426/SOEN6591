package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Filewalker {

	public static void listOfFiles(File dirPath, List<String> files){
	      File filesList[] = dirPath.listFiles();
	      
	      for(File file : filesList) {
	    	  String filename = file.getName();
	         if(file.isFile()) {
	        	 if(filename.endsWith("java")) {
	        		 files.add(file.getPath());
	        		 //System.out.println("File path: "+file.getPath());
	        	 }
	            
	         } else {
	            listOfFiles(file, files);
	         }
	      }
	      
	   }
	   public static void main(String args[]) throws IOException {
	      //Creating a File object for directory
	      File file = new File("C:\\Users\\hanyi\\Desktop\\static-analysis-demo\\hadoop");
	      //List of all files and directories
	      List<String> files = new ArrayList<String>();
	      listOfFiles(file, files);
	      //System.out.println(files);
	   }

}