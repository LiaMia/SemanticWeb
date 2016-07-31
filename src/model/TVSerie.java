package model;


import java.util.List;


public class TVSerie implements Resource {

	private String title;
	private String year;
	private String url;
	private double rating;
	private int numberOfRatings;
	private List<Actor> actors;

	public TVSerie(String title, String year) {
		super();
		this.title = title;
		this.year = year;
	}

	public TVSerie(String title, String year, String url, double rating,
			int numberOfRatings) {
		super();
		this.title = title;
		this.year = year;
		this.url = url;
		this.rating = rating;
		this.numberOfRatings = numberOfRatings;
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

	public String getRating() {
		return Double.toString(rating);
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getNumberOfRatings() {
		return Integer.toString(numberOfRatings);
	}

	public void setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

}
