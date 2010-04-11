package de.videomonkey.webinfos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.MatchResult;

import de.videomonkey.utils.WebFetcher;
import de.videomonkey.webinfos.abstracts.WebInfosAbs;

public class WebInfoIMDB extends WebInfosAbs {

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
	protected StringBuffer releasedata = new StringBuffer();
	protected StringBuffer plotdata = new StringBuffer();

	public WebInfoIMDB(String imdbid) {
		id = imdbid;

		WebFetcher mainMovie = new WebFetcher("mainMovie",
				"http://www.imdb.com/title/" + get_id());
		WebFetcher releaseMovie = new WebFetcher("releaseMovie",
				"http://www.imdb.com/title/" + get_id() + "/releaseinfo");
		WebFetcher plotMovie = new WebFetcher("plotMovie",
				"http://www.imdb.com/title/" + get_id() + "/plotsummary");
		mainMovie.start();
		releaseMovie.start();
		plotMovie.start();


		try {
			
			releaseMovie.join();
			releasedata = releaseMovie.data;
			set_releaseinfo();

			plotMovie.join();
			plotdata = plotMovie.data;
			set_plot();
			
			mainMovie.join();
			data = mainMovie.data;
			set_title();
			set_runtime();
			set_countrys();
			set_certification();
			set_studio();
			set_rating();
			set_directors();
			set_credits();
			set_genres();
			set_outline();
			set_tagline();
			set_actors();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		
	}

	@Override
	public String toString() {

		String out = "";
		out += "\n\n***************** IMDB.com *****************\n";
		out += "Title: " + get_title() + "\n";
		out += "SubTitle: " + get_subtitle() + "\n";
		out += "ID: " + get_id() + "\n";
		out += "Year: " + get_year() + "\n";
		out += "Releasedate: " + get_releasedate() + "\n";
		out += "Rating: " + get_rating() + "\n";
		out += "Certification: " + get_certification() + "\n";

		out += "Directors: " + get_directors() + "\n";
		out += "Credits: " + get_credits() + "\n";
		out += "Genres: " + get_genre() + "\n";
		out += "Plot: " + get_plot() + "\n";
		out += "Outline: " + get_outline() + "\n";
		out += "Tagline: " + get_tagline() + "\n";

		out += "Actors: " + get_actors() + "\n";

		out += "Runtime: " + get_runtime() + "\n";
		out += "Countrys: " + get_countrys() + "\n";

		out += "Studio: " + get_studio() + "\n";
		out += "Akas: " + get_akas() + "\n";
		out += "***************** IMDB.com *****************\n\n";
		return out;
	}

	protected void set_plot() {
		String pattern = "(?s)<p class=\"plotpar\">(.*?)<i>";
		String s = plotdata.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			plot = r.group(1).replace("|", "").trim();
		}
		
		if (plot.isEmpty()) {
			plot = "Kein Plot gefunden";
		}
		
	}
	
	
	protected void set_actors() {
		String pattern = "(?s)<table class=\"cast\">(.*?)<\\/table>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String secpattern = "<tr.*?>(.*?)<\\/tr>";
			String submatch = r.group(1).replaceAll("\n", "");

			for (MatchResult sr : findMatches(secpattern, submatch)) {
				String thirdpattern = "<td.*?>(.*?)<\\/td>";
				String thirdmatch = sr.group(1);

				HashMap<String, String> actor = new HashMap<String, String>();
				Integer cnt = 0;

				for (MatchResult tr : findMatches(thirdpattern, thirdmatch)) {
					String column = tr.group(1);

					if (!column.contains("...")) {
						if (cnt == 0) {
							actor.put("thumb","");
							String tmppattern = "<img src=\"(.*?)\"";
							for (MatchResult tmp : findMatches(tmppattern,
									column)) {
								if (!tmp.group(1).contains("no_photo.png")) {
									actor.put("thumb", tmp.group(1).replace(
											"SY30_SX23", "_SX400_SY275_"));
								}
							}
						}

						if (cnt == 1) {
							actor.put("link", "");
							actor.put("name", "");
							String tmppattern = "<a href=\"(.*?)\".*?>(.*?)<\\/a>";
							for (MatchResult tmp : findMatches(tmppattern,
									column)) {
								actor.put("link", tmp.group(1));
								actor.put("name", tmp.group(2));
							}
						}

						if (cnt == 3) {

							actor.put("role", "");
							actor.put("role_link", "");
							String tmppattern = "<a href=\"(.*?)\".*?>(.*?)<\\/a>";
							for (MatchResult tmp : findMatches(tmppattern,
									column)) {
								actor.put("role_link", tmp.group(1));
								actor.put("role", tmp.group(2));
							}
						}
					}
					cnt++;
				}
				if (actor.containsKey("name")) {
					actors.add(actor);
				}

			}
		}
	}

	protected void set_tagline() {
		String pattern = "(?s)Plot Keywords:<\\/h5>.*?<div class=\"info-content\">.*?<span>(.*?)<\\/span><\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String secpattern = "<a.*?>(.*?)<\\/a>";
			String submatch = r.group(1).replaceAll("\n", "");

			for (MatchResult sr : findMatches(secpattern, submatch)) {
				tagline += sr.group(1) + " ";
			}
		}

		tagline = tagline.trim();
	}

	protected void set_outline() {
		String pattern = "(?s)Plot:<\\/h5>.*?<div class=\"info-content\">(.*?)<a.*?<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			outline = r.group(1).replace("|", "").trim();
		}
	}

	protected void set_genres() {
		String pattern = "(?s)Genre:<\\/h5>.*?<div class=\"info-content\">(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String secpattern = "<a.*?>(.*?)<\\/a>";
			String submatch = r.group(1).replaceAll("\n", "");

			for (MatchResult sr : findMatches(secpattern, submatch)) {
				if (!sr.group(1).contains("See more")) {
					genre.add(sr.group(1));
				}

			}
		}
	}

	protected void set_credits() {
		String pattern = "(?s)Writer.*?:<\\/h5>.*?<div class=\"info-content\">(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String creditpattern = "<a href=\"(.*?)\".*?>(.*?)<\\/a>";
			String submatch = r.group(1).replaceAll("\n", "");

			for (MatchResult sr : findMatches(creditpattern, submatch)) {
				credits.put(sr.group(2), sr.group(1));
			}
		}
	}

	protected void set_directors() {
		String pattern = "(?s)Director.*?:<\\/h5>.*?<div class=\"info-content\">(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String directorpattern = "<a href=\"(.*?)\".*?>(.*?)<\\/a>";
			String submatch = r.group(1).replaceAll("\n", "");

			for (MatchResult sr : findMatches(directorpattern, submatch)) {
				directors.put(sr.group(2), sr.group(1));
			}
		}
	}

	protected void set_rating() {
		String pattern = "(?s)User Rating:<\\/h5>.*?<div class=\"starbar-meta\">.*?<b>(.*?)/10<\\/b>.*?<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			rating = r.group(1);
		}
	}

	protected void set_releaseinfo() {
		Integer cnt = 0;
		Hashtable<Integer, String> releasetmp = new Hashtable<Integer, String>();
		String pattern = "(?s)<table border=\"0\" cellpadding=\"2\">(.*?)<\\/table>";
		String s = releasedata.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			releasetmp.put(cnt++, r.group(1));
		}

		pattern = "(?s)<tr><td>(.*?)<\\/td><\\/tr>";
		s = releasetmp.get(0);

		Hashtable<Integer, ArrayList<String>> releasedates = new Hashtable<Integer, ArrayList<String>>();

		try {
			

		Integer cnt2 = 0;
		for (MatchResult r : findMatches(pattern, s)) {
			String releasepattern = "<a.*?>(.*?)<\\/a>";
			String submatch = r.group(1).replaceAll("\n", "");

			ArrayList<String> releasedate = new ArrayList<String>();
			for (MatchResult sr : findMatches(releasepattern, submatch)) {
				releasedate.add(sr.group(1));
			}

			releasedates.put(cnt2++, releasedate);
		}
		} catch (NullPointerException e) {
			// TODO: Scheint was nicht sauber zu sein, bitte prüfen
		}
		Hashtable<String, String> releases = new Hashtable<String, String>();
		Enumeration<ArrayList<String>> e = releasedates.elements();
		while (e.hasMoreElements()) {
			ArrayList<String> tmp = e.nextElement();
			//System.out.println(tmp);
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"d MMMMM yyyy", Locale.ENGLISH);
				Date parsedDate = dateFormat.parse(tmp.get(1) + " "
						+ tmp.get(2));

				SimpleDateFormat newdateFormat = new SimpleDateFormat(
						"dd.MM.yyyy", Locale.GERMAN);

				releases.put(tmp.get(0), newdateFormat.format(parsedDate));
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IndexOutOfBoundsException e2) {
				// TODO: Scheint was nicht sauber zu sein, bitte prüfen
			}
		}

		if (releases.containsKey("Germany")) {
			this.releasedate = releases.get("Germany");
		} else {
			this.releasedate = releases.get("USA");
		}

		//Hashtable<String, String> akas = new Hashtable<String, String>();
		pattern = "(?s)<tr>.*?<td>(.*?)<\\/td>.*?<td>(.*?)<\\/td>.*?<\\/tr>";
		s = releasetmp.get(1);
		try {
			

		for (MatchResult r : findMatches(pattern, s)) {

			String land = r.group(2);
			String titel = r.group(1);

			if (land.contains("/")) {
				String[] lands = land.split("/");

				if (lands[0].contains("(")) {
					String[] tmp = lands[0].split("\\(");
					lands[0] = tmp[0];
				}

				akas.put(lands[0], titel);
				akas.put(lands[1], titel);
			} else if (land.contains("(")) {
				String[] tmp = land.split("\\(");
				land = tmp[0];
				akas.put(land, titel);
			} else {
				akas.put(land, titel);
			}
		}
		} catch (NullPointerException ex) {
			// TODO: Scheint was nicht sauber zu sein, bitte prüfen
		}
		if (akas.containsKey("Germany")) {
			this.subtitle = akas.get("Germany");
		}

	}

	protected void set_studio() {
		String pattern = "(?s)Company:<\\/h5>(.*?)<div class=\"info-content\">(.*?)<a.*?>(.*?)<\\/a>(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			studio = r.group(3);
		}
	}

	protected void set_certification() {
		String pattern = "(?s)Certification:<\\/h5>(.*?)<div class=\"info-content\">(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String certificationpattern = "<a.*?>(.*?)<\\/a>";
			String submatch = r.group(2).replaceAll("\n", "");

			Hashtable<String, String> certification = new Hashtable<String, String>();

			for (MatchResult sr : findMatches(certificationpattern, submatch)) {
				String cert = sr.group(1);

				if (cert.matches("USA(.*?)")) {
					cert = cert.replace("USA:", "");
					certification.put("USA", cert);
				} else if (cert.matches("Germany(.*?)")
						|| cert.matches("(.*?)Germany(.*?)")) {
					cert = cert.replace("Germany:", "");
					cert = cert.replace("West Germany:", "");

					if (cert.contains("o.Al.")) {
						cert = "0";
					}

					certification.put("Germany", cert);
				}
			}

			if (certification.containsKey("Germany")) {
				this.certification = certification.get("Germany");
			} else if (certification.containsKey("USA")) {
				this.certification = certification.get("USA");
			} else {
				this.certification = "";
			}
		}
	}

	protected void set_countrys() {
		String pattern = "(?s)Country:<\\/h5>(.*?)<div class=\"info-content\">(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			String countrypattern = "<a.*?>(.*?)<\\/a>";
			String submatch = r.group(2).replaceAll("\n", "");

			ArrayList<String> country = new ArrayList<String>();

			for (MatchResult sr : findMatches(countrypattern, submatch)) {
				country.add(sr.group(1));
			}

			countrys = country;
		}
	}

	protected void set_runtime() {
		String pattern = "(?s)Runtime:<\\/h5>(.*?)<div class=\"info-content\">(.*?)([0-9]{2,3})(.*?)<\\/div>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			runtime = new Integer(r.group(3).trim());
		}
	}

	protected void set_title() {
		String pattern = "<title>(.*?) \\(([1-2][0-9][0-9][0-9]).*?\\)</title>";
		String s = data.toString();

		for (MatchResult r : findMatches(pattern, s)) {
			title = r.group(1);
			year = r.group(2);
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
