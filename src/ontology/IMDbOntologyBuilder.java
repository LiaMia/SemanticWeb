package ontology;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import model.Actor;
import model.Movie;
import model.Resource;


public class IMDbOntologyBuilder extends OntologyBuilder {

	private OntModel imdbOntModel;
	private OntClass actorClass, characterClass, movieClass, posterClass;
	private DatatypeProperty releasedIn, url;
	private ObjectProperty actedBy, advertises, cast, characters;

	@Override
	public void initializeModel() {
		imdbOntModel = ModelFactory.createOntologyModel(MODELSPEC);
		actorClass = imdbOntModel.createClass(IMDB_NAMESPACE + "actor");
		characterClass = imdbOntModel.createClass(IMDB_NAMESPACE + "character");
		movieClass = imdbOntModel.createClass(IMDB_NAMESPACE + "movie");
		posterClass = imdbOntModel.createClass(IMDB_NAMESPACE + "poster");
		releasedIn = imdbOntModel
				.createDatatypeProperty(IMDB_NAMESPACE + "releasedIn");
		url = imdbOntModel.createDatatypeProperty(IMDB_NAMESPACE + "url");
		actedBy = imdbOntModel.createObjectProperty(IMDB_NAMESPACE + "actedBy");
		advertises = imdbOntModel
				.createObjectProperty(IMDB_NAMESPACE + "advertises");
		cast = imdbOntModel.createObjectProperty(IMDB_NAMESPACE + "cast");
		characters = imdbOntModel
				.createObjectProperty(IMDB_NAMESPACE + "characters");

		imdbOntModel.setNsPrefix("imdb", IMDB_NAMESPACE);

		actedBy.addDomain(characterClass);
		actedBy.addRange(actorClass);

		advertises.addDomain(movieClass);
		advertises.addRange(posterClass);

		cast.addDomain(movieClass);
		cast.addRange(actorClass);

		characters.addDomain(movieClass);
		characters.addRange(characterClass);

		releasedIn.addDomain(movieClass);

		url.addDomain(posterClass);

	}

	@Override
	public OntModel convertParsedDataToTriples(List<Resource> resourceList) {
		int movieIndex = 1;
		int actorIndex = 1;
		int characterIndex = 1;
		Map<String, Individual> actorCache = new HashMap<String, Individual>();
		for (Resource resource : resourceList) {
			if (resource instanceof Movie) {
				Movie movie = (Movie) resource;
				Individual movieIndividual = imdbOntModel.createIndividual(
						IMDB_NAMESPACE + "movie" + movieIndex, movieClass);
				Individual posterIndividual = imdbOntModel.createIndividual(
						IMDB_NAMESPACE + "poster" + movieIndex, posterClass);
				for (Actor actor : movie.getActorsList()) {
					String actorName = actor.getName();
					if (!actorCache.containsKey(actorName)) {
						Individual actorIndividual = imdbOntModel
								.createIndividual(
										IMDB_NAMESPACE + "actor" + actorIndex,
										actorClass);
						actorIndividual.addLabel(actorName, "EN");
						actorCache.put(actorName, actorIndividual);
						actorIndex++;
					}

					Individual characterIndividual = imdbOntModel
							.createIndividual(IMDB_NAMESPACE + "character"
									+ characterIndex, characterClass);
					characterIndividual.addLabel(actor.getCharacter(), "EN");
					movieIndividual.addProperty(cast,
							actorCache.get(actorName));
					characterIndividual.addProperty(actedBy,
							actorCache.get(actorName));
					movieIndividual.addProperty(characters,
							characterIndividual);
					characterIndex++;
				}
				movieIndividual.addLabel(movie.getTitle(), "EN");
				movieIndividual.addProperty(releasedIn, movie.getYear());
				movieIndividual.addProperty(advertises, posterIndividual);
				posterIndividual.addProperty(url, movie.getPosterURL());

				movieIndex++;
			}
		}
		return imdbOntModel;
	}

}
