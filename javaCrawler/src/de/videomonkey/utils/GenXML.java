/**
 * 
 */
package de.videomonkey.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.videomonkey.webinfos.abstracts.WebInfosAbs;

/**
 * @author Azrail
 * 
 */
public class GenXML {

	WebInfosAbs data;
	WebInfosAbs optdata;
	protected StringBuffer mediaInfo = new StringBuffer();
	
	private static Logger log = Logger.getLogger(GenXML.class);
	
	public GenXML(WebInfosAbs data) {
		this.data = data;
	}

	public GenXML(WebInfosAbs data, WebInfosAbs optdata) {
		this.data = data;
		this.optdata = optdata;
	}

	public void buildXML(String filename, File movieFile) {
		try {
			// ///////////////////////////
			// Creating an empty XML Document

			// We need a Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			log.info("Generiere nfo für " + data.get_title());
			//log.info(data.toString());
			// //////////////////////
			// Creating the XML tree

			// create the root element and add it to the document
			Element movie = doc.createElement("movie");
			movie.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			movie.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			doc.appendChild(movie);

			// //create a comment and put it in the root element
			// Comment comment = doc.createComment("Just a thought");
			// movie.appendChild(comment);

			// create child element, add an attribute, and add to root
			Element child = doc.createElement("title");
			movie.appendChild(child);
			// add a text element to the child
			Text text = doc.createTextNode(data.get_title());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("originaltitle");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_subtitle());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("id");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_id());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("year");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_year());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("releasedate");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_releasedate());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("rating");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_rating());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("certification");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(data.get_certification());
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("plot");
			movie.appendChild(child);
			// add a text element to the child
			String plot = "";
			if (optdata.get_plot().equals("Kein Plot gefunden") || optdata.get_plot().length() == 0 ) {
				plot = data.get_plot();
			} else {
				plot = optdata.get_plot();
			}
			if (plot.equals("Kein Plot gefunden")) {
				plot = data.get_outline();
			}

			text = doc.createTextNode(plot);
			child.appendChild(text);

			// create child element, add an attribute, and add to root
			child = doc.createElement("runtime");
			movie.appendChild(child);
			// add a text element to the child
			text = doc.createTextNode(new Integer(data.get_runtime())
					.toString());
			child.appendChild(text);

			Element genres = doc.createElement("genre");
			for (String genre : data.get_genre()) {
				Element name = doc.createElement("name");
				text = doc.createTextNode(genre);
				name.appendChild(text);
				genres.appendChild(name);
			}
			movie.appendChild(genres);

			// create child element, add an attribute, and add to root
			Element studio = doc.createElement("studio");
			Element name = doc.createElement("name");
			// add a text element to the child
			text = doc.createTextNode(data.get_studio());
			name.appendChild(text);
			studio.appendChild(name);
			movie.appendChild(studio);

			for (String director : data.get_directors().keySet()) {
				Element directors = doc.createElement("director");
				name = doc.createElement("name");
				text = doc.createTextNode(director);
				name.appendChild(text);

				Element url = doc.createElement("url");
				text = doc.createTextNode(data.get_directors().get(director));
				url.appendChild(text);

				directors.appendChild(name);
				directors.appendChild(url);
				movie.appendChild(directors);
			}

			for (String countrys : data.get_countrys()) {
				Element directors = doc.createElement("country");
				name = doc.createElement("name");
				text = doc.createTextNode(countrys);
				name.appendChild(text);
				directors.appendChild(name);
				movie.appendChild(directors);
			}

			for (HashMap<String, String> actor : data.get_actors()) {
				Element actors = doc.createElement("actor");
				name = doc.createElement("name");
				text = doc.createTextNode(actor.get("name"));
				name.appendChild(text);

				Element url = doc.createElement("url");
				text = doc.createTextNode(actor.get("link"));
				url.appendChild(text);

				Element thumb = doc.createElement("thumb");
				text = doc.createTextNode(actor.get("thumb"));
				thumb.appendChild(text);

				Element role = doc.createElement("role");
				text = doc.createTextNode(actor.get("role"));
				role.appendChild(text);

				actors.appendChild(name);
				actors.appendChild(url);
				actors.appendChild(thumb);
				actors.appendChild(role);

				movie.appendChild(actors);
			}

			
			Process p = Runtime.getRuntime().exec("./externals/MediaInfo.exe --OUTPUT=xml \"" + movieFile.getAbsolutePath() + "\"");
//			InputStreamReader is = new InputStreamReader(p.getInputStream(), "UTF8");

			
			DocumentBuilderFactory midbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder midocBuilder = midbfac.newDocumentBuilder();
			Document midoc = midocBuilder.parse(p.getInputStream());

			//doc.importNode(midoc.getChildNodes(), true);
			doc.getFirstChild().appendChild(doc.importNode(midoc.getFirstChild(),true));
			
			
			// ///////////////
			// Output the XML

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();

			// print xml
			//System.out.println("Here's the xml:\n\n" + xmlString);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "UTF8"));

			out.write(xmlString);

			out.close();

			log.debug(".nfo nach " + filename + " geschrieben");
			
			//System.out.println("Written Process Completed.");

		} catch (Exception e) {
			log.error("Fehler beim erzeugen der nfo: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
