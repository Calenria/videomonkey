package de.videomonkey.utils;

import de.videomonkey.Movie;
import de.videomonkey.Search;
import de.videomonkey.exceptions.MovieNotFoundException;

public class ThreadInfos extends Thread {
	
	protected String path = "";
	protected Movie movie;
	protected String currentStatus;
	protected Boolean isMovieReady = false;
	protected Boolean renameMovieFile = false;
	protected Boolean createMovieNfo = false;

	public Boolean getCreateMovieNfo() {
		return createMovieNfo;
	}

	public void setCreateMovieNfo(Boolean createMovieNfo) {
		this.createMovieNfo = createMovieNfo;
	}

	public Boolean getRenameMovieFile() {
		return renameMovieFile;
	}

	public void setRenameMovieFile(Boolean renameMovieFile) {
		this.renameMovieFile = renameMovieFile;
	}

	public ThreadInfos(String path, Movie movie) {
		this.path = path;
		this.movie = movie;
		this.currentStatus = "just started";
	}

	public void run() {
		this.setName(movie.getMovie().getName());
		
		this.currentStatus = "searching IMDb";
		try {
			movie.searchIMDB();
			
			this.currentStatus = "gathering Data";
			movie.getWebData();
			
			//this.currentStatus = "renaming movie file";
			//movie.renameMovieFile();
			
			//this.currentStatus = "creating nfo";
			//movie.createNFO();
			movie.isMovieReady = true;
			this.setMovieReady(true);
			Search.movies.put(path,movie);
			
			this.currentStatus = "finished";
		} catch (MovieNotFoundException e) {
			// TODO Auto-generated catch block
			
			this.currentStatus = "canceled";
		}


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

	public Boolean isMovieReady() {
		return isMovieReady;
	}

	public void setMovieReady(boolean isMovieReady) {
		this.isMovieReady = isMovieReady;
	}
	
	
}
