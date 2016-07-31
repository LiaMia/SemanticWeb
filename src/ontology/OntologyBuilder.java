package ontology;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;

import model.Resource;


public abstract class OntologyBuilder {
	protected static final String IMDB_NAMESPACE = "http://imn.htwk-leipzig.de/jthrando/ontology/imdb#";
	protected static final String MOVIEPILOT_NAMESPACE = "http://imn.htwk-leipzig.de/jthrando/ontology/moviePilot#";
	protected static final String BOXOFFICEMOJO_NAMESPACE = "http://imn.htwk-leipzig.de/jthrando/ontology/boxOfficeMojo#";
	protected static final String SYNCHRONKARTEI_NAMESPACE = "http://imn.htwk-leipzig.de/jthrando/ontology/synchronkartei#";
	protected static final OntModelSpec MODELSPEC = OntModelSpec.OWL_DL_MEM_RULE_INF;
	private static final String format = "Turtle";

	public static String getFormat() {
		return format;
	}

	public static OntModelSpec getModelSpec() {
		return MODELSPEC;
	}

	public abstract void initializeModel();

	public abstract OntModel convertParsedDataToTriples(
			List<Resource> resourceList);

	public void writeOutModel(OntModel model, String fileName) {

		model.write(System.out, format);

		try {
			model.write(new FileOutputStream(fileName), format);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
