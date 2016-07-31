package main;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;

import model.Resource;
import ontology.OntologyBuilder;
import ontology.SynchronkarteiOntologyBuilder;
import parser.SynchronkarteiParser;


public class SynchronkarteiMain {

	public static void main(String[] args) {

		SynchronkarteiParser synchro = new SynchronkarteiParser();
		List<Resource> dubberList = new ArrayList<Resource>();
		dubberList = synchro.getDubbers();
		OntologyBuilder ontologyBuilder = new SynchronkarteiOntologyBuilder();
		ontologyBuilder.initializeModel();
		OntModel oModel = ontologyBuilder
				.convertParsedDataToTriples(dubberList);
		ontologyBuilder.writeOutModel(oModel, "Dubber.ttl");

	}

}
