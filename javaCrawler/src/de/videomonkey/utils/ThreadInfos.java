package de.videomonkey.utils;

import de.videomonkey.Movie;
import de.videomonkey.Search;

public class ThreadInfos extends Thread {
	
	protected String path = "";
	protected Movie movie;
	protected String currentStatus;

	public ThreadInfos(String path, Movie movie) {
		this.path = path;
		this.movie = movie;
		this.currentStatus = "just started";
	}

	public void run() {
		this.setName(movie.getMovie().getName());
		
		this.currentStatus = "searching IMDb";
		movie.searchIMDB();
		
		this.currentStatus = "gathering Data";
		movie.getWebData();
		
		this.currentStatus = "renaming movie file";
		movie.renameMovieFile();
		
		this.currentStatus = "creating nfo";
		movie.createNFO();
		movie.isMovieReady = true;
		Search.movies.put(path,movie);
		
		this.currentStatus = "finished";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	
}
