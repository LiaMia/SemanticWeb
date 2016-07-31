package ontology;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import model.Actor;
import model.Dubber;
import model.Resource;


public class SynchronkarteiOntologyBuilder extends OntologyBuilder {

	private OntModel synchOntModel;
	private OntClass dubberClass, characterClass, actorClass;
	private ObjectProperty speaks, actedBy;

	@Override
	public void initializeModel() {
		synchOntModel = ModelFactory.createOntologyModel(MODELSPEC);
		dubberClass = synchOntModel
				.createClass(SYNCHRONKARTEI_NAMESPACE + "dubber");
		characterClass = synchOntModel
				.createClass(SYNCHRONKARTEI_NAMESPACE + "character");
		actorClass = synchOntModel
				.createClass(SYNCHRONKARTEI_NAMESPACE + "actor");
		actedBy = synchOntModel
				.createObjectProperty(SYNCHRONKARTEI_NAMESPACE + "actedBy");
		speaks = synchOntModel
				.createObjectProperty(SYNCHRONKARTEI_NAMESPACE + "speaks");
		synchOntModel.setNsPrefix("synch", SYNCHRONKARTEI_NAMESPACE);

		speaks.addDomain(dubberClass);
		speaks.addRange(characterClass);

		actedBy.addDomain(characterClass);
		actedBy.addRange(actorClass);

	}

	@Override
	public OntModel convertParsedDataToTriples(List<Resource> resourceList) {
		int dubberIndex = 1;
		int actorIndex = 1;
		int characterIndex = 1;
		Map<String, Individual> actorCache = new HashMap<String, Individual>();
		for (Resource resource : resourceList) {
			if (resource instanceof Dubber) {
				Dubber dubber = (Dubber) resource;
				Individual dubberIndividual = synchOntModel.createIndividual(
						SYNCHRONKARTEI_NAMESPACE + "dubber" + dubberIndex,
						dubberClass);
				for (Actor actor : dubber.getSynchronizedActor()) {
					String actorName = actor.getName();
					if (!actorCache.containsKey(actorName)) {
						Individual actorIndividual = synchOntModel
								.createIndividual(SYNCHRONKARTEI_NAMESPACE
										+ "actor" + actorIndex, actorClass);
						actorIndividual.addLabel(actorName, "EN");
						actorCache.put(actorName, actorIndividual);
						actorIndex++;
					}

					Individual characterIndividual = synchOntModel
							.createIndividual(SYNCHRONKARTEI_NAMESPACE
									+ "character" + characterIndex,
									characterClass);
					characterIndividual.addLabel(actor.getCharacter(), "EN");
					characterIndividual.addProperty(actedBy,
							actorCache.get(actorName));
					dubberIndividual.addProperty(speaks, characterIndividual);
					characterIndex++;

				}
				dubberIndividual.addLabel(dubber.getName(), "EN");
				dubberIndex++;
			}
		}

		return synchOntModel;
	}
}
