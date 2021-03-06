package bgu.spl.a2;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far
 */
public abstract class PrivateState implements Serializable {

	// holds the actions' name what were executed
	private List<String> history = new LinkedList<>();

	public List<String> getLogger(){
		return history;
	}

	
	public void addRecord(String actionName){
		history.add(actionName);
	}


}
