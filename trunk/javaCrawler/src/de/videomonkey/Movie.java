package de.videomonkey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.MatchResult;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import de.videomonkey.exceptions.MovieNotFoundException;
import de.videomonkey.utils.GenXML;
import de.videomonkey.utils.Utils;
import de.videomonkey.webinfos.WebInfoIMDB;
import de.videomonkey.webinfos.WebInfoOFDB;
import de.videomonkey.webinfos.abstracts.WebInfosAbs;


public class Movie extends Thread {

	private File movie;
	private File movieNfo;
	private static Logger log = Logger.getLogger(Movie.class);
	public boolean isMovieReady = false;

	public boolean isMovieSearchReady = false;

	String fullFileName;
	String fileNameExt;
	String fileName;

	String searchName;
	String searchURL = "http://www.imdb.com/find?s=tt&q=";
	String searchProxy = "http://www.videomonkey.de/proxy.php?url=";

	String searchURLB64;
	String searchNameB64;

	String nfoFileName;
	
	public WebInfoIMDB wiIMDB;
	public WebInfoOFDB wiOFDB;
	
	private String movieIMDBId = "";

	Movie(File file) {

		setMovie(file);

		log.debug("Initialisiert für " + getMovie().getAbsolutePath());
		fullFileName = getMovie().getName();
		fileNameExt = fullFileName.substring(fullFileName.lastIndexOf("."));
		fileName = fullFileName.substring(0, fullFileName.lastIndexOf("."));
		searchName = Utils.cleanMovieFileName(fileName);
		searchURLB64 = Base64.encodeBase64URLSafeString(searchURL.getBytes());
		searchNameB64 = Base64.encodeBase64URLSafeString(searchName.getBytes());

		findNfosIMDBId();
	}

	private void findNfosIMDBId() {

		String imdbid = "";
		String basename = movie.getName();
		// String ext = basename.substring(basename.lastIndexOf("."));
		String name = basename.substring(0, basename.lastIndexOf("."));

		movieNfo = new File(movie.getParent() + File.separatorChar + name
				+ ".nfo");

		if (movieNfo.exists()) {
			;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(movieNfo.getAbsolutePath())));
				StringBuffer contentOfFile = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					contentOfFile.append(line);
				}
				String content = contentOfFile.toString();

				String pattern = "(tt[0-9]{7})";
				for (MatchResult r : Utils.findMatches(pattern, content)) {
					imdbid = r.group(1);
				}

			} catch (FileNotFoundException e) {
				log.fatal("Datei nicht gefunden" + e.getLocalizedMessage());
			} catch (IOException e) {
				log.fatal("IOException" + e.getLocalizedMessage());
			}

			if (imdbid.length()==0) {
				log.debug("IMDB ID in (" + movieNfo.getAbsolutePath()
						+ ") nicht gefunden");
			} else {
				setMovieIMDBId(imdbid);
				log.debug("IMDB ID: " + imdbid + " in nfo ("
						+ movieNfo.getAbsolutePath() + ") gefunden");
			}
		}
	}
	
	public void getWebData() {
		while (fetchIMDB(getMovieIMDBId())) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			while (fetchOFDB(getMovieIMDBId())) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (MovieNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Keine ofdb.de daten für " + getMovie() + " (" + getMovieIMDBId() + ") gefunden");
		}
	}
	
	private boolean fetchOFDB(String imdbid) throws MovieNotFoundException {
		if (imdbid == null) {
			return false;
		}
		
		WebInfoOFDB ofdb = new WebInfoOFDB(imdbid); 
		//if (!ofdb.get_title().isEmpty()) {
		if (ofdb.get_title().length()>0) {
			wiOFDB = ofdb;
			return false;
		}
		return true;
	}
	
	private boolean fetchIMDB(String imdbid) {
		WebInfoIMDB imdb = new WebInfoIMDB(imdbid); 
		//if (!imdb.get_title().isEmpty() && !imdb.get_plot().isEmpty() && !imdb.get_releasedate().isEmpty()) {
		if (imdb.get_title() != null || imdb.get_title().length()>0 && imdb.get_plot().length()>0 && imdb.get_releasedate().length()>0) {
			wiIMDB = imdb;
			return false;
		}
		return true;
	}
	
	public void createNFO() {
		nfoFileName = movie.getAbsolutePath().replace(fileNameExt, ".nfo");		
		GenXML gxml = new GenXML((WebInfosAbs) wiIMDB,(WebInfosAbs) wiOFDB);
		gxml.buildXML(nfoFileName,getMovie());
	}
	
	public void renameMovieFile() {
		File newname = new File(movie.getParent() + File.separatorChar + clean(wiIMDB.get_title() + " (" + wiIMDB.get_year() + ")" + fileNameExt));
		
		if (!newname.exists()) {
			log.debug("Versuche datei von " + movie.getAbsolutePath() + " zu " + newname.getAbsolutePath() + " umzubennenen");
			movie.renameTo(newname);
			setMovie(newname);
			log.info("Movie wurde in " + movie.getAbsolutePath() + " umbenannt");
		} else {
			log.debug(movie.getAbsolutePath() + " entspricht schon der Bennenungs Methode");
		}
	}
	
	private static String clean(String name) {

		name = name.replaceAll("\\:", "");
		name = name.replaceAll("'", "");
		name = name.replaceAll("\\?", "");
		return name;
	}

	public void searchIMDB() throws MovieNotFoundException {
		if (getMovieIMDBId().length()==0) {
			String result = fetchHTML();
			parseHTML(result);
		}
	}

	private void parseHTML(String result) throws MovieNotFoundException {

		ArrayList<HashMap<String, String>> popularTitles = new ArrayList<HashMap<String, String>>();

		String pattern = "<input name=\"q\".*?value=\"(.*?)\"";
		String suche = "";
		for (MatchResult r : Utils.findMatches(pattern, result)) {
			suche = r.group(1);
		}

		pattern = "Popular Titles<\\/b> \\(Displaying ([0-9]{1,3}) Result\\)";
		String popTitles = "";
		for (MatchResult r : Utils.findMatches(pattern, result)) {
			popTitles = r.group(1);
		}
		pattern = "(?s)Popular Titles.*?<table>(.*?)<\\/table>";
		String popTitleTable = "";
		for (MatchResult r : Utils.findMatches(pattern, result)) {
			popTitleTable = r.group(1);

			String ptepattern = "(?s)<\\/td><td valign=\"top\">.*?<a href=\"(.*?)\".*?>(.*?)<\\/a>";
			HashMap<String, String> popTitleEntry = new HashMap<String, String>();
			for (MatchResult pter : Utils
					.findMatches(ptepattern, popTitleTable)) {
				popTitleEntry.put("name", pter.group(2));
				popTitleEntry.put("link", pter.group(1));
				popularTitles.add(popTitleEntry);
			}
		}

		String uniquetitle = "";
		String uniqueyear = "";
		pattern = "<title>(.*?) \\(([1-2][0-9][0-9][0-9]).*?\\)</title>";
		for (MatchResult r : Utils.findMatches(pattern, result)) {
			uniquetitle = r.group(1);
			uniqueyear = r.group(2);
		}
		if (uniquetitle.length() > 1 && uniqueyear.length() == 4) {
			String id = "";
			pattern = "<link rel=\"canonical\" href=\"http://www.imdb.com/title/(.*?)/\" \\/>";
			for (MatchResult r : Utils.findMatches(pattern, result)) {
				id = r.group(1);
				setMovieIMDBId(id);
			}
		} else if (popTitles.length() == 0) {

			String imdbid = javax.swing.JOptionPane.showInputDialog(null,
					"IMDBid für " + getMovie().getName() + " eingeben",
					"Keine ergebnisse für " + suche + " gefunden :(",
					JOptionPane.QUESTION_MESSAGE);
			if (imdbid != null) {
				setMovieIMDBId(imdbid);
			} else {
				throw new MovieNotFoundException();
			}

		} else {
			for (HashMap<String, String> popTitlecnt : popularTitles) {
				int use = javax.swing.JOptionPane.showConfirmDialog(null,
						"Film \"" + popTitlecnt.get("name") + "\" benutzen?",
						"Suche: " + suche, JOptionPane.YES_NO_CANCEL_OPTION);
				if (use == JOptionPane.YES_OPTION) {

					setMovieIMDBId(popTitlecnt.get("link")
							.replaceAll("\\/", "").replace("title", ""));
					break;
				}
				if (use == JOptionPane.CANCEL_OPTION) {
					break;
				}
			}
		}

	}

	private String fetchHTML() {
		StringBuffer data = new StringBuffer();
		try {
			URL url = new URL(searchProxy + searchURLB64 + "&search="
					+ searchNameB64);
			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream(), "UTF8"));

			int buffer;
			while ((buffer = in.read()) != -1) {
				data.append((char) buffer);
			}
			in.close();
		} catch (Exception e) {
			log.fatal("Probleme beim abrufen der Webinfos: "
					+ e.getLocalizedMessage());
		}

		return data.toString();
	}

	public String getMovieIMDBId() {
		return movieIMDBId;
	}

	private void setMovieIMDBId(String movieIMDBId) {
		this.movieIMDBId = movieIMDBId;
	}

	private void setMovie(File movie) {
		this.movie = movie;
	}

	public File getMovie() {
		return movie;
	}

	public static ArrayList<String> getWantedExtensions() {

		ArrayList<String> extension = new ArrayList<String>();
		extension.add(".avi");
		extension.add(".mkv");
		extension.add(".mpeg");
		extension.add(".mpg");
		extension.add(".mp4");
		extension.add(".ts");
		extension.add(".m2ts");

		return extension;
	}

}
