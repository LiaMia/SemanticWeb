package parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import model.Actor;
import model.Resource;
import model.TVSerie;


public class MoviepilotParser {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; en-US; rv:46.0) Gecko/20100101 Firefox/46.0";

	public List<Resource> getTVSeries() {
		List<Resource> tvSeriesList = new ArrayList<Resource>();
		List<String> tvSeriesURLList = this.getListOfTVSeriesURLs();
		Document doc;
		try {
			for (int i = 0; i < tvSeriesURLList.size(); i++) {
				doc = Jsoup.connect(tvSeriesURLList.get(i))
						.userAgent(USER_AGENT).timeout(0).get();

				Elements titleEl = doc
						.select("span[class='h1'][itemprop='name']");
				String title = "";
				if (!titleEl.isEmpty()) {
					title = titleEl.get(0).text();
				}

				Elements yearEl = doc.select("span[itemprop='copyrightYear']");
				String year = "";
				if (!yearEl.isEmpty()) {
					year = yearEl.get(0).text();
				}

				Elements ratingEl = doc.select("span[itemprop='ratingValue']");
				double rating = 0;
				if (!ratingEl.isEmpty()) {
					String r = ratingEl.get(0).attr("title");
					if (!r.equals("?")) {
						rating = Double.parseDouble(r);
					} else {
						rating = 0;
					}
				}

				Elements numberOfRatingEL = doc
						.select("span[itemprop='ratingCount']");
				int numberOfRatings = 0;
				if (!numberOfRatingEL.isEmpty()) {
					numberOfRatings = Integer.parseInt(
							numberOfRatingEL.get(0).childNode(0).toString());
				}

				Elements actorsEl = doc
						.select("div[class='person_img_portrait']");
				List<Actor> actors = new ArrayList<Actor>();
				if (!actorsEl.isEmpty()) {
					Node actorElement0 = actorsEl.get(0);
					for (Node navNode : actorElement0.childNodes()) {
						for (Node n : navNode.childNodes()) {
							if (n.hasAttr("itemtype")) {
								if (n.childNode(3).attr("itemprop")
										.equals("actor")) {
									getActorAndCharacter(actors, n);
								}
							}
						}
					}
					if (actorsEl.size() > 1) {
						Node actorElement1 = actorsEl.get(1);
						for (Node navNode : actorElement1.childNodes()) {
							for (Node n : navNode.childNodes()) {
								if (n.hasAttr("itemtype")) {
									if (n.childNode(3).attr("itemprop")
											.equals("actor")) {
										getActorAndCharacter(actors, n);
									}
								}
							}
						}
					}
				}

				if (!actors.isEmpty()) {
					TVSerie serie = new TVSerie(title, year,
							tvSeriesURLList.get(i), rating, numberOfRatings);
					serie.setActors(actors);
					tvSeriesList.add(serie);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tvSeriesList;
	}

	private List<String> getListOfTVSeriesURLs() {
		List<String> tvSeriesURLList = new ArrayList<String>();
		int numberOfPagesFound = 0;
		Document doc;

		// first step: find out how many results (pages) the request returns
		try {
			doc = Jsoup.connect("http://www.moviepilot.de/serien/beste?page=1")
					.userAgent(USER_AGENT).get();
			Elements numberOfResults = doc
					.select("a[class='pagination--last']");
			String[] splited = numberOfResults.get(0).attributes().get("href")
					.split("\\=");
			numberOfPagesFound = Integer.parseInt(splited[1]);

			// second step: repeat the request for each results page and get the
			// URLs to the detailed pages (only for not german tvSeries)
			for (int page = 1; page <= numberOfPagesFound; page++) {
				System.out.println(page);
				doc = Jsoup.connect(
						"http://www.moviepilot.de/serien/beste?page=" + page)
						.userAgent(USER_AGENT).timeout(0).get();
				Elements elements = doc
						.select("div[class='list-item-info-poster']");
				for (Element element : elements) {
					// check, that only not-german series get caught
					String checkForNotGerman = element.childNode(5).childNode(0)
							.toString();
					if (!checkForNotGerman.contains("DE")) {
						String url = element.childNode(3).attributes()
								.get("href");
						url = "http://www.moviepilot.de" + url;
						tvSeriesURLList.add(url);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tvSeriesURLList;
	}

	private void getActorAndCharacter(List<Actor> actors, Node n) {
		String actorName;
		String characterName;
		actorName = n.childNode(3).childNode(1).attr("title");
		actorName = actorName.trim();
		characterName = n.childNode(5).childNode(3).childNode(0).toString();
		characterName = characterName.trim();
		actors.add(new Actor(actorName, characterName));
	}

}
