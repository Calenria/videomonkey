package de.videomonkey.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ToolStarter extends Thread {

	protected String name = "";
	protected String cmd = "";
	protected String path = "";
	protected StringBuffer data = new StringBuffer();
	public static HashMap<String, String> mediainfo = new HashMap<String, String>();
	

	public ToolStarter(String name, String cmd, String path) {
		this.name = name;
		this.cmd = cmd;
		this.path = path;
	}

	public void fetchToolOutput() {

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));

			for (String s; (s = in.readLine()) != null;) {
				data.append(s + "\n");
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		fetchToolOutput();
		ToolStarter.mediainfo.put(path, data.toString());
	}
}
