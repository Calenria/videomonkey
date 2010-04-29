package de.videomonkey.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

public class WebFetcher extends Thread {

	public String name = "";
	public String fetchurl = "";
	public StringBuffer data = new StringBuffer();
	public String state = "Gestartet";
	public boolean proxy = true;
	public static HashMap<String, String> info = new HashMap<String, String>(); 
	
	public String search = "";
	
	public WebFetcher(String s, String url) {
		name = s;
		fetchurl = url;
	}
	
	public WebFetcher(String s, String url, boolean proxy) {
		name = s;
		fetchurl = url;
		this.proxy = proxy;
	}

	public WebFetcher(String s, String url, String search, boolean proxy) {
		name = s;
		fetchurl = url;
		this.proxy = proxy;
		this.search = search;
	}
	
	public void fetchHTML() {
		
		try {
			
			//System.out.println("fetch " + fetchurl + search);
			
			URL url;
			
			if (proxy) {
				
				String b64url = Base64.encodeBase64URLSafeString(fetchurl.getBytes()); 
				String b64search = Base64.encodeBase64URLSafeString(search.getBytes()); 
				
				if (search.length() < 2) {
					url = new URL("http://www.videomonkey.de/proxy.php?url=" + b64url);
				} else {
					url = new URL("http://www.videomonkey.de/proxy.php?url=" + b64url + "&search=" + b64search);
				}
				
				
			} else {
				url = new URL(fetchurl);
			}

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF8"));

            int buffer;
            while ((buffer=in.read())!=-1)
              {
                  data.append((char)buffer);
              }
            in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void run() {
		
		fetchHTML();
		WebFetcher.info.put(name, data.toString());
		state = "Beendet";
		//System.out.println("Thread " + name + " ist fertig");
	}
}
