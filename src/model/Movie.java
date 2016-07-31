package model;


import java.util.List;


public class Movie implements Resource {

	private String title;
	private String year;
	private String imdbURL;
	private String posterURL;
	private List<Actor> actorsList;

	public Movie(String title, String year, String url, String posterURL) {
		this.title = title;
		this.year = year;
		this.imdbURL = url;
		this.posterURL = posterURL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getImdbURL() {
		return imdbURL;
	}

	public void setImdbURL(String imdbURL) {
		this.imdbURL = imdbURL;
	}

	public List<Actor> getActorsList() {
		return actorsList;
	}

	public void setActorsList(List<Actor> actorsList) {
		this.actorsList = actorsList;
	}

	public String getPosterURL() {
		return posterURL;
	}

	public void setPosterURL(String posterURL) {
		this.posterURL = posterURL;
	}

}
