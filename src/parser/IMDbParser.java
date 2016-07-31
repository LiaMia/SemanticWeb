package parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import model.Actor;
import model.Movie;
import model.Resource;


public class IMDbParser {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; de-DE ;rv:46.0) Gecko/20100101 Firefox/46.0";

	public List<Resource> getMovies() {

		List<Resource> moviesList = new ArrayList<Resource>();

		try {
			// get a list of all movies from 1980 to 2015 and the URLs to the
			// movies
			List<String> movieURLList = this.getListOfMovieURLs("1980", "2015");

			// get detailed information about the movies from the movieURLList
			// and save them as Movie in the moviesList
			Document doc;

			for (int i = 0; i < movieURLList.size(); i++) {
				System.out.println(i);
				List<Actor> actorsList = new ArrayList<Actor>();
				doc = Jsoup.connect("http://www.imdb.com" + movieURLList.get(i))
						.userAgent(USER_AGENT).header("Accept-Language", "de")
						.header("Accept-Encoding", "gzip,deflate,sdch")
						.maxBodySize(0).timeout(0).get();
				Elements check = doc.select("body");
				if (check.get(0) == null)
					continue;
				Elements titleElement = doc
						.select("div[class='title_wrapper']");
				String title = titleElement.get(0).childNode(1).childNode(0)
						.toString();
				title = title.trim();
				title = title.replace("&nbsp;", "");
				Elements yearElement = doc.select("span[id='titleYear']");
				String year = yearElement.get(0).childNode(1).childNode(0)
						.toString();
				Elements posterElement = doc.select("div[class='poster']");
				String posterURL;
				if (!posterElement.isEmpty()) {
					posterURL = posterElement.get(0).childNode(1).childNode(1)
							.attr("src");
					posterURL = posterURL.split("_")[0];
				} else {
					posterURL = "n/a";
				}

				Movie movie = new Movie(title, year, movieURLList.get(i),
						posterURL);
				Elements castElement = doc.select("table[class='cast_list']");
				List<Node> nodes = castElement.get(0).childNode(1).childNodes();
				for (Node node : nodes) {
					if (node.hasAttr("class")) {
						String actorName = node.childNode(1).childNode(1)
								.childNode(0).attributes().get("title");
						actorName = actorName.trim();
						String role;
						if (node.childNode(7).childNode(1).childNodes()
								.size() > 1) {
							role = node.childNode(7).childNode(1).childNode(1)
									.childNode(0).toString();
							role = role.trim();
						} else {
							role = node.childNode(7).childNode(1).childNode(0)
									.toString();
							role = role.trim();
						}
						Actor actor = new Actor(actorName, role);
						actorsList.add(actor);
					}

				}
				movie.setActorsList(actorsList);
				moviesList.add(movie);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return moviesList;

	}

	private List<String> getListOfMovieURLs(String yearFrom, String yearTo)
			throws IOException {

		List<String> movieURLList = new ArrayList<String>();

		Document doc;
		for (int page = 1; page < 21; page++) {
			try {
				doc = Jsoup
						.connect(
								"http://www.imdb.com/search/title?count=250&languages=en&release_date="
										+ yearFrom + "," + yearTo
										+ "&start=1&title_type=feature&view=simple&page="
										+ page + "&ref_=adv_nxt")
						.userAgent(USER_AGENT).header("Accept-Language", "de")
						.timeout(0).maxBodySize(0).get();
				Elements titles = doc.select("div[class='lister-item-image']");

				for (Element element : titles) {
					String url = element.childNode(1).attributes().get("href");
					url = url.replace("?ref_=adv_li_i", "");
					movieURLList.add(url);
				}
			} catch (HttpStatusException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return movieURLList;
	}
}
