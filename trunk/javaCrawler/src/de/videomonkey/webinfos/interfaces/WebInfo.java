/**
 * 
 */
package de.videomonkey.webinfos.interfaces;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Azrail
 *
 */
public interface WebInfo {
		
	public String get_title();
	public String get_subtitle();
	public String get_id();
	public String get_year();
	public String get_releasedate();
	public String get_rating();
	public String get_certification();
	public ArrayList<String> get_genre();
	public ArrayList<String> get_countrys();
	public String get_studio();
	public HashMap<String, String> get_directors();
	public HashMap<String, String> get_credits();
	public String get_tagline();
	public String get_outline();
	public String get_plot();
	public int get_runtime();
	public ArrayList<HashMap<String,String>> get_actors();
	public HashMap<String, String> get_akas();
	
	public String toString();
}
