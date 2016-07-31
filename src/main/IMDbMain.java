package main;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;

import model.Resource;
import ontology.IMDbOntologyBuilder;
import ontology.OntologyBuilder;
import parser.IMDbParser;


public class IMDbMain {

	public static void main(String[] args) {

		IMDbParser imdb = new IMDbParser();
		List<Resource> movieList = new ArrayList<Resource>();

		movieList = imdb.getMovies();

		OntologyBuilder ontologyBuilder = new IMDbOntologyBuilder();
		ontologyBuilder.initializeModel();
		OntModel oModel = ontologyBuilder.convertParsedDataToTriples(movieList);
		ontologyBuilder.writeOutModel(oModel, "Movie.ttl");

	}

}
