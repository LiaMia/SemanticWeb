package main;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;

import model.Resource;
import ontology.MoviepilotOntologyBuilder;
import ontology.OntologyBuilder;
import parser.MoviepilotParser;


public class MoviepilotMain {

	public static void main(String[] args) {

		MoviepilotParser moviepilot = new MoviepilotParser();

		List<Resource> tvSeriesList = new ArrayList<Resource>();
		tvSeriesList = moviepilot.getTVSeries();
		OntologyBuilder ontologyBuilder = new MoviepilotOntologyBuilder();
		ontologyBuilder.initializeModel();
		OntModel oModel = ontologyBuilder
				.convertParsedDataToTriples(tvSeriesList);
		ontologyBuilder.writeOutModel(oModel, "TVSeries.ttl");

	}

}
