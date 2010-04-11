/**
 * 
 */
package de.videomonkey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.videomonkey.utils.FileUtils;
import de.videomonkey.utils.ThreadInfos;

/**
 * @author Azrail
 *
 */
public class Search {
	
	public static HashMap<String, Movie> movie = new HashMap<String, Movie>();
	//private static Logger log = Logger.getRootLogger();
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configureAndWatch( "log.properties", 60*1000 );
		ArrayList<File> fileList = FileUtils.searchFile(new File("M:\\media1\\sheeting"), Movie.get_wantedExtensions());
		
		for (File file : fileList) {
			ThreadInfos thread = new ThreadInfos(file.getAbsolutePath(), new Movie(file));
			thread.start();
		}
	}

}
