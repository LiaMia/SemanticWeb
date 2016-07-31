package model;


import java.util.List;


public class Dubber implements Resource {

	private String name;
	private List<Actor> synchronizedActor;

	public Dubber(String name, List<Actor> synchronizedActor) {
		super();
		this.name = name;
		this.synchronizedActor = synchronizedActor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Actor> getSynchronizedActor() {
		return synchronizedActor;
	}

	public void setSynchronizedActor(List<Actor> synchronizedActor) {
		this.synchronizedActor = synchronizedActor;
	}

}
