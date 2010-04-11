/**
 * 
 */
package de.videomonkey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.videomonkey.utils.GenXML;
import de.videomonkey.webinfos.WebInfoIMDB;
import de.videomonkey.webinfos.WebInfoOFDB;
import de.videomonkey.webinfos.abstracts.WebInfosAbs;

/**
 * @author Azrail
 *
 */
public class FetchData extends Thread {

	protected String name = "";
	protected String imdbid = "";
	protected File file;
	public static HashMap<File, String> title = new HashMap<File, String>();
	protected static ArrayList<String> replacements = new ArrayList<String>();
	
	public FetchData(String name, String imdbid, File file) {
		this.name = name;
		this.imdbid = imdbid;
		this.file = file;
	}

	public void run() {
		
		String basename = file.getAbsolutePath();
		String ext = basename.substring(basename.lastIndexOf("."));
		String name = basename.substring(0, basename.lastIndexOf("."));
		
		
		WebInfoIMDB imdb = new WebInfoIMDB(imdbid); 
		//System.out.println(imdb.toString());
		WebInfoOFDB ofdb = new WebInfoOFDB(imdbid);
		//System.out.println(ofdb.toString());
		
		
		File newname = new File(file.getParent() + File.separatorChar + clean(imdb.get_title() + " (" + imdb.get_year() + ")" + ext));
		
		if (!newname.exists()) {
			System.out.println("Versuche datei von " + file.getAbsolutePath() + " zu " + newname.getAbsolutePath() + " umzubennenen");
			
			file.renameTo(newname);
			
		} else {
			System.out.println(file.getAbsolutePath() + " entspricht schon der Bennenungs Methode");
		}
		
		
		title.put(newname, imdb.get_title());
		
//		System.out.println(name + ext);
//		
//		System.out.println(file.getParent() + File.separatorChar + imdb.get_title() + " (" + imdb.get_year() + ")" + ext);
//		
//		GenXML gxml = new GenXML((WebInfosAbs) imdb,(WebInfosAbs) ofdb);
//		gxml.buildXML(newname.getAbsolutePath().replace(ext, ".nfo"));
	}

	private static String clean(String name) {

		name = name.replaceAll("\\:", "");
		name = name.replaceAll("\\?", "");
		return name;
	}
	
	
}
	
//	protected StringBuffer data = new StringBuffer();
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		
//		
//
//		
//		String imdbid = "tt0892769";
//		
//		WebInfoIMDB imdb = new WebInfoIMDB(imdbid); 
//		//System.out.println(imdb);
//		WebInfoOFDB ofdb = new WebInfoOFDB(imdbid); 
//		//System.out.println(ofdb);
//		
//		
//		GenXML gxml = new GenXML((WebInfosAbs) imdb,(WebInfosAbs) ofdb);
//		gxml.buildXML();
//		
//	}
//
//}
