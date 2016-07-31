package main;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;

import model.Resource;
import ontology.BoxOfficeMojoOntologyBuilder;
import ontology.OntologyBuilder;
import parser.BoxOfficeMojoParser;


public class BoxOfficeMojoMain {

	public static void main(String[] args) {

		BoxOfficeMojoParser box = new BoxOfficeMojoParser();
		List<Resource> boxOfficeGrossList = new ArrayList<Resource>();
		boxOfficeGrossList = box.getBoxOfficeMojo();
		OntologyBuilder ontologyBuilder = new BoxOfficeMojoOntologyBuilder();
		ontologyBuilder.initializeModel();
		OntModel oModel = ontologyBuilder
				.convertParsedDataToTriples(boxOfficeGrossList);
		ontologyBuilder.writeOutModel(oModel, "BoxOffice.ttl");

	}

}
