package parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import model.BoxOfficeGross;
import model.Resource;


public class BoxOfficeMojoParser {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; en-US; rv:46.0) Gecko/20100101 Firefox/46.0";
	private String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	public List<Resource> getBoxOfficeMojo() {
		List<Resource> boxOfficeMojoList = new ArrayList<Resource>();
		List<String> boxOfficeMojoURLList = this.getBoxOfficeMojoURLList();
		long domestic, worldwide, germany;
		Document doc;

		Connection.Response response;
		for (String link : boxOfficeMojoURLList) {
			if (link.contains(
					"http://www.boxofficemojo.com/movies/?id=trickrtreat.htm")) {
				continue;
			}
			try {
				response = Jsoup.connect(link).userAgent(USER_AGENT)
						.header("Accept-Language", "en").timeout(0)
						.ignoreHttpErrors(true).execute();
				int statusCode = response.statusCode();
				if (statusCode == 200) {
					doc = Jsoup.connect(link).userAgent(USER_AGENT)
							.header("Accept-Language", "en").timeout(0).get();

					Elements year = doc.select(
							"table [bgcolor='#dcdcdc'][border='0'][cellpadding='4'][cellspacing='1'][width='95%']");
					boolean yearOK = false;
					if (year.isEmpty()) {
						continue;
					}
					for (Node node : year.get(0).childNode(0).childNodes()) {
						if (node.toString().contains("Release Date")) {
							String y;
							if (node.childNode(1).childNode(1).childNode(0)
									.childNode(0).childNodes().isEmpty()) {
								y = node.childNode(1).childNode(1).childNode(0)
										.childNode(0).toString();
							} else {
								y = node.childNode(1).childNode(1).childNode(0)
										.childNode(0).childNode(0).toString();
							}
							String[] split = y.split(" ");
							y = split[split.length - 1];
							y = y.replaceAll("\n", "");
							if (!y.contains("TBD") && !y.contains("N/A")) {
								int i = Integer.parseInt(y);
								if (i >= 1980 && i <= 2015) {
									System.out.println(i);
									yearOK = true;
									break;
								} else {
									yearOK = false;
									break;
								}
							} else {
								yearOK = false;
							}

						}
					}
					if (yearOK == false) {
						continue;
					}
					// Title
					Elements titleAndPic = doc.select(
							"table [style='padding-top: 5px;'][border='0'][cellpadding='0'][cellspacing='0'][width='100%']");
					String title = titleAndPic.get(0).childNode(0).childNode(0)
							.childNode(2).childNode(1).childNode(0).childNode(0)
							.toString();
					if (titleAndPic.get(0).childNode(0).childNode(0)
							.childNode(0).childNodes().size() == 1) {
						continue;
					}
					String posterURL = titleAndPic.get(0).childNode(0)
							.childNode(0).childNode(0).childNode(1).childNode(0)
							.attr("src");
					posterURL = posterURL.split("_")[0];

					// Domestic
					Elements domesticElem = doc.select(
							"tr:not(:has(tr)):has(td:has(b:contains(Domestic:)))");
					if (!domesticElem.isEmpty()) {
						String domesticString = domesticElem.get(0).childNode(3)
								.childNode(1).childNode(0).toString();
						if (domesticString.equals("n/a")) {
							domestic = 0;
						} else {
							domesticString = domesticString.replace("$", "");
							domesticString = domesticString.replace(",", "");
							domestic = Long.parseLong(domesticString);
						}

					} else {
						domestic = 0;
					}

					// Worldwide
					Elements worldwideElem = doc.select(
							"tr:not(:has(tr)):has(td:has(b:contains(Worldwide:)))");
					if (!worldwideElem.isEmpty()) {
						String worldwideString = worldwideElem.get(0)
								.childNode(3).childNode(1).childNode(0)
								.toString();
						if (worldwideString.equals("n/a")) {
							worldwide = 0;
						} else {
							worldwideString = worldwideString.replace("$", "");
							worldwideString = worldwideString.replace(",", "");
							worldwide = Long.parseLong(worldwideString);
						}
					} else {
						worldwide = 0;
					}

					// Germany
					Elements foreignLink = doc
							.select("li:has(a[href]:contains(Foreign))");
					if (!foreignLink.isEmpty()) {
						String linkToForeign = foreignLink.get(0).childNode(0)
								.attr("href");
						doc = Jsoup
								.connect("http://www.boxofficemojo.com"
										+ linkToForeign)
								.userAgent(USER_AGENT)
								.header("Accept-Language", "en").timeout(0)
								.get();
						Elements columnsElem = doc.select(
								"tr:not(:has(tr)):has(td:has(a:contains(click to view weekend breakdown)))");
						Elements germanyElem = doc.select(
								"tr:not(:has(tr)):has(td:has(b:contains(Germany)))");

						if (!germanyElem.isEmpty()) {
							int index = 0;
							int counter = 0;
							for (Node elem : columnsElem.get(0).childNodes()) {
								if (elem.toString().contains("Total Gross")) {
									index = counter * 2;
									break;
								}
								counter++;
							}
							String germanyString = germanyElem.get(0)
									.childNode(index).childNode(0).childNode(0)
									.childNode(0).toString();
							if (germanyString.equals("n/a")) {
								germany = 0;
							} else {
								germanyString = germanyString.replace("$", "");
								germanyString = germanyString.replace(",", "");
								germany = Long.parseLong(germanyString);
							}
						} else {
							germany = 0;
						}
					} else {
						germany = 0;
					}
					if (!(domestic == 0 && worldwide == 0 && germany == 0)) {
						BoxOfficeGross boxOffice = new BoxOfficeGross(title,
								link, domestic, worldwide, germany, posterURL);
						boxOfficeMojoList.add(boxOffice);
						System.out.println(boxOffice.getTitle());
						System.out.println(boxOffice.getPosterURL());
						System.out.println(
								"Domestic: " + boxOffice.getDomestic());
						System.out.println(
								"Wordlwide: " + boxOffice.getWorldwide());
						System.out
								.println("Germany: " + boxOffice.getGermany());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return boxOfficeMojoList;

	}

	private List<String> getBoxOfficeMojoURLList() {
		List<String> boxOfficeMojoURLList = new ArrayList<String>();
		String link = null;
		int x = 0;
		boolean success = false;
		Document doc;

		// first step: get the links to all movies (because they are sorted
		// alphabetically)
		try {
			doc = Jsoup
					.connect(
							"http://www.boxofficemojo.com/movies/alphabetical.htm?letter=NUM&p=.htm")
					.userAgent(USER_AGENT).header("Accept-Language", "en")
					.timeout(0).maxBodySize(0).get();

			Elements linkTable = doc.select(
					"table[cellspace='1'][bgcolor='#ffffff'][border='0'][cellpadding='7'][width='100%']");
			Element element = linkTable.get(0).child(0);
			for (int i = 1; i < element.childNodes().size(); i++) {
				link = element.childNode(i).childNode(0).childNode(0)
						.childNode(0).attr("href");
				link = "http://www.boxofficemojo.com" + link;
				boxOfficeMojoURLList.add(link);
			}
		} catch (IOException e) {

		}

		try {
			for (int i = 0; i < alphabet.length; i++) {
				List<String> pageOverview = new ArrayList<String>();
				pageOverview
						.add("http://www.boxofficemojo.com/movies/alphabetical.htm?letter="
								+ alphabet[i] + "&p=.htm");
				doc = Jsoup
						.connect(
								"http://www.boxofficemojo.com/movies/alphabetical.htm?letter="
										+ alphabet[i] + "&p=.htm")
						.userAgent(USER_AGENT).header("Accept-Language", "en")
						.timeout(0).maxBodySize(0).get();
				Elements navHolder = doc
						.select("div[class='alpha-nav-holder']");
				Node navElement = navHolder.get(0).childNode(0);
				for (Node navNode : navElement.childNodes()) {
					if (!navNode.childNodes().isEmpty()
							&& navNode.childNode(0).hasAttr("href")) {
						String overViewPageLink = navNode.childNode(0)
								.attr("href");
						pageOverview.add("http://www.boxofficemojo.com"
								+ overViewPageLink);
					}
				}
				for (String pageLink : pageOverview) {
					while (x < 3) {
						try {
							doc = Jsoup.connect(pageLink).userAgent(USER_AGENT)
									.header("Accept-Language", "en").timeout(0)
									.get();
							success = true;
							x = 0;
							break;
						} catch (HttpStatusException h) {
							System.out.println(
									"-----------------------RETRY---------------------");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException j) {
								j.printStackTrace();
							}
						} catch (IOException e) {

						}
						x++;
					}
					if (success) {
						Elements linkTable2 = doc.select(
								"table[cellspace='1'][bgcolor='#ffffff'][border='0'][cellpadding='7'][width='100%']");
						Element tableElement = linkTable2.get(0).child(0);
						for (int j = 1; j < tableElement.childNodes()
								.size(); j++) {
							link = tableElement.childNode(j).childNode(0)
									.childNode(0).childNode(0).attr("href");
							link = "http://www.boxofficemojo.com" + link;
							boxOfficeMojoURLList.add(link);
						}
						success = false;
					}

				}
			}
		} catch (IOException e) {

		}

		return boxOfficeMojoURLList;
	}
}
