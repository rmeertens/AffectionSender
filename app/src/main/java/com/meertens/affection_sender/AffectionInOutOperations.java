package com.meertens.affection_sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

public class AffectionInOutOperations {
	
	/**
	 * Writes a string to a file on the SD card in the Klef folder 
	 * @param filename
	 * @param toWrite
	 */
	static void writeToFile(String filename,String toWrite)
	{
		// Get or create the folder
	    String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + "/klef");    
	    myDir.mkdirs();
	    
	    // Check if the file exists, it it is: delete him!
	    // TODO: don't delete the file
	    File file = new File (myDir, filename);
	    if (file.exists ()){ 
	    	file.delete (); 
	    }
	    
	    // Create and write the bytes
	    try {
           	FileOutputStream out = new FileOutputStream(file);
			byte[] contentInBytes = toWrite.getBytes();
			out.write(contentInBytes);
			out.flush();
			out.close();
	    } 
	    catch (Exception e) {
	           e.printStackTrace();
	    }
	}
	
	/**
	 * Reads a string from a file on the SD card in the Klef folder 
	 * @param filename
	 * @param toWrite
	 */
	static String readFromFile(String filename) throws FileNotFoundException
	{
		String readString = ""; 
		String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + "/klef");    
	    myDir.mkdirs();
	    
	    File file = new File (myDir, filename);
	    if (!file.exists ()){ 
	    	 throw new FileNotFoundException("File "  + filename + " does not exist yet"); 
	    }
	    else
	    {
			  //Read text from file
			  try {
			      BufferedReader br = new BufferedReader(new FileReader(file));
			      String line;
		
			      while ((line = br.readLine()) != null) {
			    	  readString += line;
			    	  readString += "\n";
 
			      }
			      br.close();
			  }
			  catch (IOException e) {
			      // TODO add proper error handling here
				  
			  }
	    }
	    return readString;
	}
	
}
