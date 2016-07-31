package ontology;


import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import model.BoxOfficeGross;
import model.Resource;


public class BoxOfficeMojoOntologyBuilder extends OntologyBuilder {

	private OntModel boxOfficeOntModel;
	private OntClass boxOfficeGrossClass;
	private DatatypeProperty belongsTo, domestic, worldwide, germany;

	@Override
	public void initializeModel() {
		boxOfficeOntModel = ModelFactory.createOntologyModel(MODELSPEC);
		boxOfficeGrossClass = boxOfficeOntModel
				.createClass(BOXOFFICEMOJO_NAMESPACE + "boxOfficeGross");
		domestic = boxOfficeOntModel
				.createDatatypeProperty(BOXOFFICEMOJO_NAMESPACE + "domestic");
		worldwide = boxOfficeOntModel
				.createDatatypeProperty(BOXOFFICEMOJO_NAMESPACE + "worldwide");
		germany = boxOfficeOntModel
				.createDatatypeProperty(BOXOFFICEMOJO_NAMESPACE + "germany");
		belongsTo = boxOfficeOntModel
				.createDatatypeProperty(BOXOFFICEMOJO_NAMESPACE + "belongsTo");

		boxOfficeOntModel.setNsPrefix("box", BOXOFFICEMOJO_NAMESPACE);

		belongsTo.addDomain(boxOfficeGrossClass);

		domestic.addDomain(boxOfficeGrossClass);

		worldwide.addDomain(boxOfficeGrossClass);

		germany.addDomain(boxOfficeGrossClass);

	}

	@Override
	public OntModel convertParsedDataToTriples(List<Resource> resourceList) {
		int i = 1;
		for (Resource resource : resourceList) {
			if (resource instanceof BoxOfficeGross) {
				BoxOfficeGross boxOffice = (BoxOfficeGross) resource;
				Individual boxOfficeGrossIndividual = boxOfficeOntModel
						.createIndividual(
								BOXOFFICEMOJO_NAMESPACE + "boxOfficeGross" + i,
								boxOfficeGrossClass);
				boxOfficeGrossIndividual.addLabel(boxOffice.getTitle(), "EN");
				boxOfficeGrossIndividual.addProperty(domestic,
						boxOffice.getDomestic(), XSDDatatype.XSDlong);
				boxOfficeGrossIndividual.addProperty(worldwide,
						boxOffice.getWorldwide(), XSDDatatype.XSDlong);
				boxOfficeGrossIndividual.addProperty(germany,
						boxOffice.getGermany(), XSDDatatype.XSDlong);
				boxOfficeGrossIndividual.addProperty(belongsTo,
						boxOffice.getPosterURL());

				i++;
			}
		}
		return boxOfficeOntModel;
	}

}
