package de.videomonkey.webinfos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.MatchResult;

import de.videomonkey.utils.WebFetcher;
import de.videomonkey.webinfos.abstracts.WebInfosAbs;

public class WebInfoOFDB extends WebInfosAbs {

	protected String title = "";
	protected String subtitle = "";
	protected String id = "";
	protected String year = "";
	protected String releasedate = "";
	protected String rating = "";
	protected String certification = "";

	protected ArrayList<String> genre = new ArrayList<String>();
	protected ArrayList<String> countrys = new ArrayList<String>();

	protected String studio = "";

	protected HashMap<String, String> directors = new HashMap<String, String>();
	protected HashMap<String, String> credits = new HashMap<String, String>();

	protected HashMap<String, String> akas = new HashMap<String, String>();
	
	protected String tagline = "";
	protected String outline = "";
	protected String plot = "";

	protected int runtime = 0;

	protected ArrayList<HashMap<String, String>> actors = new ArrayList<HashMap<String, String>>();

	protected StringBuffer data = new StringBuffer();

	protected StringBuffer plotdata = new StringBuffer();
	
	public WebInfoOFDB(String imdbid) {
		id = imdbid;

		WebFetcher mainMovie = new WebFetcher("mainMovie","http://www.ofdb.de/view.php?page=suchergebnis&Kat=IMDb&SText=" + get_id(),false);

		mainMovie.start();

		try {
			mainMovie.join();
			data = mainMovie.data;			
			set_search_movie();

			mainMovie = new WebFetcher("mainMovie","http://www.ofdb.de/film/" + get_id() + "," + subtitle,false);
			mainMovie.start();
			mainMovie.join();
			data = mainMovie.data;	

			set_title();
			set_subtitle();
			set_plot();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {

		String out = "";
		out += "\n\n***************** OFDB.de *****************\n";
		out += "Title: " + get_title() + "\n";
		out += "SubTitle: " + get_subtitle() + "\n";
		out += "ID: " + get_id() + "\n";
		out += "Year: " + get_year() + "\n";
		out += "Plot: " + get_plot() + "\n";
		out += "***************** OFDB.de *****************\n\n";
		
		return out;
	}
	
	
	protected void set_search_movie() {
		String url = "";
		String pattern = "(?s)Filme:<\\/b>.*?<a href=\"(.*?)\".*?\">(.*?)<\\/a>";
		String s = data.toString();
		
		for (MatchResult r : findMatches(pattern, s)) {			
			url = r.group(1);
			String movie = r.group(2).replaceAll("<font size=\"1\">", "").replaceAll("<\\/font>", "");
			
			pattern = "(.*?)/(.*?)\\((.*?)\\)";
			for (MatchResult sr : findMatches(pattern, movie)) {
				subtitle = sr.group(1).trim();
				title = sr.group(2).trim();
				year = sr.group(3).trim();
			}
			
		}
		String[] tmp = url.split("/");
		tmp = tmp[1].split(",");
		id = tmp[0];
	}
	
	
	protected void set_plot() {
		String pattern = "(?s)Inhalt:.*?<a href=\"(.*?)\"><b>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			try {
				WebFetcher plotMovie = new WebFetcher("plotMovie","http://www.ofdb.de/" + r.group(1),false);
				plotMovie.start();
				plotMovie.join();
				plotdata = plotMovie.data;
				
				String pdpattern = "(?s)<p class=\"Blocksatz\">.*?<\\/b><\\/b>(.*?)<\\/font><\\/p>";
				String pd = plotdata.toString();

				for (MatchResult sr : findMatches(pdpattern, pd)) {
					plot = sr.group(1).replaceAll("<br \\/>", "").replaceAll("<br>", "").replaceAll("\n", "").replaceAll("\r", "");
				}
				if (plot.length() < 5) {
					plot = "Kein Plot gefunden";
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	

	protected void set_subtitle() {
		String pattern = "(?s)Originaltitel:.*?<font.*?class=\"Daten\"><b>(.*?)<\\/b>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			subtitle = r.group(1);
		}
	}
	
	
	protected void set_title() {
		String pattern = "<h2><font.*?><b>(.*?)<\\/b><\\/font><\\/h2>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			title = r.group(1);
		}
	}

	@Override
	public ArrayList<String> get_countrys() {
		return countrys;
	}

	@Override
	public ArrayList<HashMap<String, String>> get_actors() {
		return actors;
	}

	@Override
	public String get_certification() {
		return certification;
	}

	@Override
	public HashMap<String, String> get_credits() {
		return credits;
	}
	
	@Override
	public HashMap<String, String> get_akas() {
		return akas;
	}
	
	@Override
	public int get_runtime() {
		return runtime;
	}

	@Override
	public HashMap<String, String> get_directors() {
		return directors;
	}

	@Override
	public ArrayList<String> get_genre() {
		return genre;
	}

	@Override
	public String get_id() {
		return id;
	}

	@Override
	public String get_outline() {
		return outline;
	}

	@Override
	public String get_plot() {
		return plot;
	}

	@Override
	public String get_rating() {
		return rating;
	}

	@Override
	public String get_releasedate() {
		return releasedate;
	}

	@Override
	public String get_studio() {
		return studio;
	}

	@Override
	public String get_subtitle() {
		return subtitle;
	}

	@Override
	public String get_tagline() {
		return tagline;
	}

	@Override
	public String get_title() {
		return title;
	}

	@Override
	public String get_year() {
		return year;
	}

}
