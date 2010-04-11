package de.videomonkey.utils;

import de.videomonkey.Movie;
import de.videomonkey.Search;

public class ThreadInfos extends Thread {
	
	protected String path = "";
	protected Movie reg;

	public ThreadInfos(String path, Movie reg) {
		this.path = path;
		this.reg = reg;
	}

	public void run() {
		this.setName(reg.getMovie().getName());
		reg.searchIMDB();
		reg.getWebData();
		reg.renameMovieFile();
		reg.createNFO();
		Search.movie.put(path,reg);
	}
}
