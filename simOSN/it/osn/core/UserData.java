package it.osn.core;

import java.util.List;
import java.util.Map;

public class UserData {
	protected Map<Integer, Integer> neighbors;
	protected List<String> hobbies;
	
	@Override
	public String toString() {
		return "UserData [neighbors=" + neighbors + ", hobbies=" + hobbies + "]";
	}
	public Map<Integer, Integer> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(Map<Integer, Integer> neighbors) {
		this.neighbors = neighbors;
	}
	public List<String> getHobbies() {
		return hobbies;
	}
	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

}
