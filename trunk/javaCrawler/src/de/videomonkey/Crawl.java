package de.videomonkey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.videomonkey.utils.FileUtils;
import de.videomonkey.utils.WebFetcher;

public class Crawl {

	protected StringBuffer data = new StringBuffer();
	protected static ArrayList<String> replacements = new ArrayList<String>();
	public static HashMap<String, String> info = new HashMap<String, String>();
	public static HashMap<String, String> films = new HashMap<String, String>();

	public static HashMap<String, HashMap<String, String>> registy = new HashMap<String, HashMap<String, String>>();
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ArrayList<String> extension = new ArrayList<String>();
		extension.add(".avi");
		extension.add(".mkv");
		extension.add(".mpeg");
		extension.add(".mpg");
		extension.add(".mp4");
		extension.add(".ts");
		extension.add(".m2ts");

		Crawl.create_replacements();

		ArrayList<File> fileList = FileUtils.searchFile(new File(
				"M:\\media1\\sheeting"), extension);

		File nfo;
		
		for (File file : fileList) {

			String basename = file.getName();
			String ext = basename.substring(basename.lastIndexOf("."));
			String name = basename.substring(0, basename.lastIndexOf("."));
			// System.out.println(ext);
			// System.out.println(name);
			
			nfo = new File(file.getParent()+ File.separatorChar + name + ".nfo");
			String imdbidsuche = "";
			if (nfo.exists()) {
				
				//System.out.println("Info File ("+nfo.getAbsolutePath()+") gefunden, versuche IMDB Id zu ermitteln");
				
				
				BufferedReader br = new BufferedReader(
				           new InputStreamReader(
				           new FileInputStream(nfo.getAbsolutePath()))); 
				StringBuffer contentOfFile = new StringBuffer();
				String line; 
				while ((line = br.readLine()) != null) {
				    contentOfFile.append(line);
				}
				String content = contentOfFile.toString();
				
				
				String pattern = "(tt[0-9]{7})";
				for (MatchResult r : findMatches(pattern, content)) {
					imdbidsuche = r.group(1);
				}
				if (imdbidsuche.length() > 2) {
					//System.out.println("IMDB ID ("+imdbidsuche+") gefunden");
				}
				
				
			}

			String searchname = Crawl.clean(name);

			if (imdbidsuche.length() == 9) {
				searchname = imdbidsuche;
			}
			String url = "http://www.imdb.com/find?s=tt&q=";
			
			WebFetcher fetcher = new WebFetcher(file.getName(), url, searchname, true);
			fetcher.start();
			
			
		}
		while (WebFetcher.activeCount() > 1) {
		}

		for (File file : fileList) {

			// System.out.println(WebFetcher.info.get(file.getName()));
			String searchdata = WebFetcher.info.get(file.getName());

			ArrayList<HashMap<String, String>> popularTitles = new ArrayList<HashMap<String, String>>();

			// <input name="q" type="text" value="zurueck in die zukunft iii"/>
			String pattern = "<input name=\"q\".*?value=\"(.*?)\"";
			String suche = "";
			for (MatchResult r : findMatches(pattern, searchdata)) {
				suche = r.group(1);
			}

			pattern = "Popular Titles<\\/b> \\(Displaying ([0-9]{1,3}) Result\\)";
			String popTitles = "";
			for (MatchResult r : findMatches(pattern, searchdata)) {
				popTitles = r.group(1);
			}
			pattern = "(?s)Popular Titles.*?<table>(.*?)<\\/table>";
			String popTitleTable = "";
			for (MatchResult r : findMatches(pattern, searchdata)) {
				popTitleTable = r.group(1);

				String ptepattern = "(?s)<\\/td><td valign=\"top\">.*?<a href=\"(.*?)\".*?>(.*?)<\\/a>";
				HashMap<String, String> popTitleEntry = new HashMap<String, String>();
				for (MatchResult pter : findMatches(ptepattern, popTitleTable)) {
					popTitleEntry.put("name", pter.group(2));
					popTitleEntry.put("link", pter.group(1));
					popularTitles.add(popTitleEntry);
				}
			}

			String uniquetitle = "";
			String uniqueyear = "";
			pattern = "<title>(.*?) \\(([1-2][0-9][0-9][0-9]).*?\\)</title>";
			for (MatchResult r : findMatches(pattern, searchdata)) {
				uniquetitle = r.group(1);
				uniqueyear = r.group(2);
			}
			
			if (uniquetitle.length() > 1 && uniqueyear.length() == 4) {
				String uniquelink = "";
				pattern = "<link rel=\"canonical\" href=\"http://www.imdb.com(.*?)\" \\/>";
				for (MatchResult r : findMatches(pattern, searchdata)) {
					uniquelink = r.group(1);
				}
				
				FetchData fetcher = new FetchData(file.getName(), uniquelink.replaceAll("\\/", "").replace("title", ""),file);
				fetcher.start();
				
				films.put(file.getName(), uniquelink.replaceAll("\\/", "").replace("title", ""));
			} else if (popTitles.length() == 0) {
//				int use = javax.swing.JOptionPane.showConfirmDialog(null,
//						"Keine ergebnisse für " + suche + " gefunden :(", "Suche: " + file,
//						JOptionPane.YES_NO_CANCEL_OPTION);
				
				String imdbid = "";
				
				imdbid = javax.swing.JOptionPane.showInputDialog(null, "IMDBid für " + file.getName() + " eingeben", "Keine ergebnisse für " + suche + " gefunden :(", JOptionPane.QUESTION_MESSAGE);
				
				//System.out.println(imdbid);
				
				if (imdbid.length() > 1) {
					FetchData fetcher = new FetchData(file.getName(), imdbid,file);
					fetcher.start();
					films.put(file.getName(), imdbid);
				}
				
				
			} else {
				for (HashMap<String, String> popTitlecnt : popularTitles) {
					int use = javax.swing.JOptionPane.showConfirmDialog(null,
							"Film \"" + popTitlecnt.get("name")
									+ "\" benutzen?", "Suche: " + suche,
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (use == JOptionPane.YES_OPTION) {
						FetchData fetcher = new FetchData(file.getName(), popTitlecnt.get("link").replaceAll("\\/", "").replace("title", ""),file);
						fetcher.start();
						films.put(file.getName(), popTitlecnt.get("link").replaceAll("\\/", "").replace("title", ""));
						break;
					}
					if (use == JOptionPane.CANCEL_OPTION) {
						break;
					}
				}
			}
			// int ok =javax.swing.JOptionPane.showConfirmDialog(null,
			// popTitles, "Suche: " + suche, JOptionPane.YES_NO_CANCEL_OPTION);
			//			
			//			
			// if (ok == JOptionPane.YES_OPTION) {
			// javax.swing.JOptionPane.showMessageDialog(null, "Yes!");
			// }
			// if (ok == JOptionPane.NO_OPTION) {
			// javax.swing.JOptionPane.showMessageDialog(null, "No!");
			// }
			// if (ok == JOptionPane.CANCEL_OPTION) {
			// break;
			// }
		}

		System.out.println(films);
		System.out.println(FetchData.title);
		// System.out.println(WebFetcher.info);
		// for (File file : fileList) {
		// ToolStarter tool = new
		// ToolStarter(file.getName(),"./externals/MediaInfo.exe --OUTPUT=xml \""
		// + file + "\"",file.getAbsolutePath());
		// tool.start();
		// }
		//		
		// //Warte darauf das alle Prozesse abgeschlossen sind
		// while (ToolStarter.activeCount() > 1) {}
		//		
		// for (File file : fileList) {
		// //System.out.println(ToolStarter.mediainfo.get(file.getAbsolutePath()));
		//			
		// DocumentBuilderFactory dbfactory =
		// DocumentBuilderFactory.newInstance();
		// dbfactory.setNamespaceAware(true); // never forget this!
		// DocumentBuilder builder = dbfactory.newDocumentBuilder();
		//
		// InputStream bais = new
		// ByteArrayInputStream(ToolStarter.mediainfo.get(file.getAbsolutePath()).getBytes());
		// Document doc = builder.parse(bais);
		//
		// XPathFactory factory = XPathFactory.newInstance();
		// XPath xpath = factory.newXPath();
		//			
		// Crawl.info.put("file", file.getAbsolutePath());
		// Crawl.get_format(xpath,doc);
		// Crawl.get_video(xpath,doc);
		//			
		// System.out.println(Crawl.info);
		// System.out.println(ToolStarter.mediainfo.get(file.getAbsolutePath()));
		//			
		// }
	}

	private static void create_replacements() {
		replacements.add("dvdrip.*");
		replacements.add("german.*");
		replacements.add("english.*");
		replacements.add("by.*");
		replacements.add("\\(.*\\)");
		replacements.add("gerac3.*");
		replacements.add("engmp3.*");
		replacements.add("xvid.*");
		replacements.add("ac3.*");
		replacements.add("hdtvrip.*");
	}

	private static String clean(String name) {

		name = name.replaceAll("\\.", " ");
		name = name.replaceAll("_", " ");

		// try to save the year
		String pattern = "([0-9][0-9][0-9][0-9])";
		String year = "";

		String backup = name;

		for (MatchResult r : findMatches(pattern, name)) {
			if (r.groupCount() > 0) {
				year = r.group(1);
				name = name.replaceAll(pattern, "").trim();
			}
		}

		if (name.trim().length() < 3) {
			name = backup;
		}

		for (String replace : Crawl.replacements) {
			name = name.replaceAll(replace, "").trim();
		}

		if (year.length() > 0) {
			name += " (" + year + ")";
		}

		name = name.replaceAll("  ", " ");
		return name;
	}

	protected static void get_format(XPath xpath, Document doc)
			throws Exception {
		XPathExpression expr = xpath
				.compile("//track[@type='Video']/Format/text()");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		String codec = "";

		if (nodes.getLength() == 1) {
			codec = nodes.item(0).getNodeValue().toString();
			if (codec.contains("AVC")) {
				codec = "MKV";
			} else if (codec.contains("MPEG-4")) {
				codec = "mpeg4";
			}
		} else {
			for (int i = 0; i < nodes.getLength();) {
				System.out.println(nodes.item(i).getNodeValue());
				throw new Exception("To Many Codecs found!");
			}
		}
		Crawl.info.put("format", codec);
	}

	protected static void get_video(XPath xpath, Document doc) throws Exception {
		XPathExpression expr = xpath
				.compile("//track[@type='Video']/Codec_ID/text()");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		String codec = "";

		if (nodes.getLength() == 1) {
			codec = nodes.item(0).getNodeValue().toString();
			if (codec.contains("V_MPEG4") || codec.contains("ISO")
					|| codec.contains("AVC")) {
				codec = "AVC";
			}
		} else {
			for (int i = 0; i < nodes.getLength();) {
				System.out.println(nodes.item(i).getNodeValue());
				throw new Exception("To Many Codecs found!");
			}
		}
		Crawl.info.put("video", codec);
	}

	protected static Iterable<MatchResult> findMatches(String pattern,
			CharSequence s) {
		List<MatchResult> results = new ArrayList<MatchResult>();

		for (Matcher m = Pattern.compile(pattern).matcher(s); m.find();)
			results.add(m.toMatchResult());

		return results;
	}

}
