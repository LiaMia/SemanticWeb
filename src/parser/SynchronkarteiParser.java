package parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import model.Actor;
import model.Dubber;
import model.Resource;


public class SynchronkarteiParser {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; en-US; rv:46.0) Gecko/20100101 Firefox/46.0";

	public List<Resource> getDubbers() {
		List<Resource> dubberList = new ArrayList<Resource>();
		List<String> dubberURLList = this.getDubberURLList();
		Document doc = null;
		int i = 0;
		boolean success = false;

		for (String url : dubberURLList) {
			// workaround: sometimes the connection fails due to a huge
			// html-site or due to too fast requests --> wait and retry
			while (i < 3) {
				try {
					doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(0)
							.validateTLSCertificates(false).get();
					success = true;
					i = 0;
					break;
				} catch (SSLException s) {
					System.out.println(
							"-----------------------RETRY---------------------");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException j) {
						j.printStackTrace();
					}
				} catch (IOException e) {

				}
				i++;
			}
			if (success) {
				Elements nameEl = doc.select("div[class='spr_name']");
				String name = nameEl.get(0).childNode(0).toString();
				name = name.trim();
				name = name.replace("&nbsp;", "");
				System.out.println(
						"--------------------------------------------------------------------------"
								+ name);

				Elements synchEL = doc.select("div[class='spr_itm']");
				List<Actor> actors = new ArrayList<Actor>();
				String synchronizedActor;
				String syncrhonizedCharacter;
				for (Element el : synchEL) {
					// case: a film -> check if the release date is between 1980
					// and 2015
					if (el.childNodes().size() > 1) {
						if (el.childNode(1).toString().contains("(als")) {
							if (el.childNode(4).attr("href")
									.contains("type=film")) {
								String year = el.childNode(5).toString();
								year = year.replaceAll("\" \\(|\\)&nbsp;|\\) ",
										"");
								int y = 0;
								try {
									y = Integer.parseInt(year);
								} catch (NumberFormatException n) {
									continue;
								}

								if (y >= 1980 && y <= 2015) {
									if (!el.childNode(0).toString()
											.equals("\nals ")) {
										synchronizedActor = el.childNode(0)
												.childNode(0).toString();
										syncrhonizedCharacter = el.childNode(2)
												.childNode(0).toString();
										syncrhonizedCharacter = syncrhonizedCharacter
												.replaceAll("'", "");
									}

								}
							} else {
								// case: a tvSerie
								// keine einzelnen Episoden berücksichtigen
								// (sonst
								// zu viele Charaktere)
								if (el.childNode(5).toString()
										.contains("in Ep.")) {
									System.out.println("passt");
									continue;
								}
								if (!el.childNode(0).toString()
										.equals("\nals ")) {
									synchronizedActor = el.childNode(0)
											.childNode(0).toString();
									syncrhonizedCharacter = el.childNode(2)
											.childNode(0).toString();
									syncrhonizedCharacter = syncrhonizedCharacter
											.replaceAll("'", "");
									actors.add(new Actor(synchronizedActor,
											syncrhonizedCharacter));
								}
							}
						}
					}
				}
				if (!actors.isEmpty()) {
					Dubber dubber = new Dubber(name, actors);
					dubberList.add(dubber);
				}
				success = false;
			}

		}

		return dubberList;
	}

	private List<String> getDubberURLList() {
		List<String> dubberURLList = new ArrayList<String>();
		List<String> alphabet = new ArrayList<String>(Arrays.asList("A", "B",
				"C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
		int numberOfPagesFound = 0;
		Document doc;

		try {
			// first step: find out how many results (pages) every letter
			// returns
			for (String letter : alphabet) {
				doc = Jsoup
						.connect(
								"https://www.synchronkartei.de/?type=talker&action=list&letter="
										+ letter)
						.userAgent(USER_AGENT).timeout(0)
						.validateTLSCertificates(false).get();
				Elements numberOfResults = doc.select(
						"table[style='margin-left: 100px; vertical-align: top; border: 0; padding: 5px;']");
				Node node = numberOfResults.get(1).childNode(1).childNode(0)
						.childNode(1);
				String lastpage;
				for (Node n : node.childNodes()) {
					for (Node m : node.childNodes()) {
						if (m.toString().contains("Letzte")) {
							lastpage = m.attr("href");
							String[] split = lastpage.split("page=");
							lastpage = split[1];
							numberOfPagesFound = Integer.parseInt(lastpage);
						}
					}
				}

				// second step: repeat the request for each results page and get
				// the URLs to the detailed pages (only for dubbers with at
				// least 300 speaking parts)
				for (int page = 1; page <= numberOfPagesFound; page++) {
					doc = Jsoup
							.connect(
									"https://www.synchronkartei.de/?type=talker&action=list&letter="
											+ letter + "&page=" + page)
							.userAgent(USER_AGENT).timeout(0)
							.validateTLSCertificates(false).get();
					Elements dubbers = doc.select(
							"table[style='margin-left: 100px; vertical-align: top; border: 1px solid #ccc; padding: 5px;']");
					for (Element element : dubbers) {
						String speakCounter = element.childNode(1).childNode(0)
								.childNode(1).childNode(1).childNode(1)
								.childNode(0).toString();
						speakCounter = speakCounter.replaceAll("\\(|\\)", "");
						int i = Integer.parseInt(speakCounter);
						if (i >= 300) {
							Node n = element.childNode(1).childNode(0)
									.childNode(3);
							for (Node m : n.childNodes()) {
								if (m.toString().contains("mehr")) {
									String url = m.attr("href");
									url = "https://www.synchronkartei.de/"
											+ url;
									dubberURLList.add(url);
								}
							}

						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dubberURLList;
	}

}
