package uk.ac.ox.oucs.vidaas.data.holder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileContentViewer {
	
	private String fileNameWithURL;
	
	public FileContentViewer(String fileNameWithURLValue){
		this.fileNameWithURL = fileNameWithURLValue;
	}
	
	public String getFileContent(){
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(fileNameWithURL);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader buffered = new BufferedReader(reader);
		String mainStatement = "";
		String line;
		
		try {
			while ((line = buffered.readLine()) != null) {
				mainStatement = mainStatement + line + "\n";
				// System.out.println("Line: \n" + line);
				if (line.endsWith(";") || line.contains(";")) {
					
					// System.out.println("Main Statement \n" +
					// mainStatement);
					mainStatement = mainStatement  + "\n\n";
				}
			}
			buffered.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mainStatement;
	}
}
