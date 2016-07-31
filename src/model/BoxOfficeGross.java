package model;

public class BoxOfficeGross implements Resource {

	private String title;
	private String boxOfficeURL;
	private long domestic;
	private long worldwide;
	private long germany;
	private String posterURL;

	public BoxOfficeGross(String title, String boxOfficeURL, long domestic,
			long worldwide, long germany, String posterURL) {
		super();
		this.title = title;
		this.boxOfficeURL = boxOfficeURL;
		this.domestic = domestic;
		this.worldwide = worldwide;
		this.germany = germany;
		this.setPosterURL(posterURL);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBoxOfficeURL() {
		return boxOfficeURL;
	}

	public void setBoxOfficeURL(String boxOfficeURL) {
		this.boxOfficeURL = boxOfficeURL;
	}

	public String getDomestic() {
		return Long.toString(domestic);
	}

	public void setDomestic(long domestic) {
		this.domestic = domestic;
	}

	public String getWorldwide() {
		return Long.toString(worldwide);
	}

	public void setWorldwide(long worldwide) {
		this.worldwide = worldwide;
	}

	public String getGermany() {
		return Long.toString(germany);
	}

	public void setGermany(long germany) {
		this.germany = germany;
	}

	public String getPosterURL() {
		return posterURL;
	}

	public void setPosterURL(String posterURL) {
		this.posterURL = posterURL;
	}

}
