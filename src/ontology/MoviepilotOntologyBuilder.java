package ontology;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import model.Actor;
import model.Resource;
import model.TVSerie;


public class MoviepilotOntologyBuilder extends OntologyBuilder {

	private OntModel moviepilotOntModel;
	private OntClass tvSerieClass, actorClass, characterClass;
	private DatatypeProperty rating, numberOfRatings, releasedIn;
	private ObjectProperty cast, characters, actedBy;

	@Override
	public void initializeModel() {
		moviepilotOntModel = ModelFactory.createOntologyModel(MODELSPEC);
		tvSerieClass = moviepilotOntModel
				.createClass(MOVIEPILOT_NAMESPACE + "tvSerie");
		actorClass = moviepilotOntModel
				.createClass(MOVIEPILOT_NAMESPACE + "actor");
		characterClass = moviepilotOntModel
				.createClass(MOVIEPILOT_NAMESPACE + "character");

		rating = moviepilotOntModel
				.createDatatypeProperty(MOVIEPILOT_NAMESPACE + "rating");
		numberOfRatings = moviepilotOntModel.createDatatypeProperty(
				MOVIEPILOT_NAMESPACE + "numberOfRatings");
		releasedIn = moviepilotOntModel
				.createDatatypeProperty(MOVIEPILOT_NAMESPACE + "releasedIn");
		cast = moviepilotOntModel
				.createObjectProperty(MOVIEPILOT_NAMESPACE + "cast");
		characters = moviepilotOntModel
				.createObjectProperty(MOVIEPILOT_NAMESPACE + "characters");
		actedBy = moviepilotOntModel
				.createObjectProperty(MOVIEPILOT_NAMESPACE + "actedBy");

		moviepilotOntModel.setNsPrefix("mp", MOVIEPILOT_NAMESPACE);

		rating.addDomain(tvSerieClass);

		numberOfRatings.addDomain(tvSerieClass);

		releasedIn.addDomain(tvSerieClass);

		cast.addDomain(tvSerieClass);
		cast.addRange(actorClass);

		characters.addDomain(tvSerieClass);
		characters.addRange(characterClass);

		actedBy.addDomain(characterClass);
		actedBy.addRange(actorClass);

	}

	@Override
	public OntModel convertParsedDataToTriples(List<Resource> resourceList) {
		int serieIndex = 1;
		int actorIndex = 1;
		int characterIndex = 1;
		Map<String, Individual> actorCache = new HashMap<String, Individual>();
		for (Resource resource : resourceList) {
			if (resource instanceof TVSerie) {
				TVSerie serie = (TVSerie) resource;
				Individual tvSerieIndividual = moviepilotOntModel
						.createIndividual(
								MOVIEPILOT_NAMESPACE + "tvSerie" + serieIndex,
								tvSerieClass);
				for (Actor actor : serie.getActors()) {
					String actorName = actor.getName();
					if (!actorCache.containsKey(actorName)) {
						Individual actorIndividual = moviepilotOntModel
								.createIndividual(MOVIEPILOT_NAMESPACE + "actor"
										+ actorIndex, actorClass);
						actorIndividual.addLabel(actor.getName(), "EN");
						actorCache.put(actorName, actorIndividual);
						actorIndex++;
					}

					Individual characterIndividual = moviepilotOntModel
							.createIndividual(MOVIEPILOT_NAMESPACE + "character"
									+ characterIndex, characterClass);
					characterIndividual.addLabel(actor.getCharacter(), "EN");
					tvSerieIndividual.addProperty(cast,
							actorCache.get(actorName));
					tvSerieIndividual.addProperty(characters,
							characterIndividual);
					characterIndividual.addProperty(actedBy,
							actorCache.get(actorName));
					characterIndex++;
				}
				tvSerieIndividual.addLabel(serie.getTitle(), "EN");
				tvSerieIndividual.addProperty(releasedIn, serie.getYear());
				tvSerieIndividual.addProperty(rating, serie.getRating(),
						XSDDatatype.XSDdouble);
				tvSerieIndividual.addProperty(numberOfRatings,
						serie.getNumberOfRatings(), XSDDatatype.XSDint);

				serieIndex++;
			}
		}
		return moviepilotOntModel;
	}

}
