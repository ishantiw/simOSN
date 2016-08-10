package it.osn.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class UserData {
	double connectionSpeed;
	int hopCount;
	int offlineUsers;
	protected Map<Long, Integer> neighbors = new HashMap<>();
	protected List hobbies = new ArrayList();
	protected Map<Long, Integer> oneHopFriends= new HashMap<>();
	public Map<Long, Integer> getOneHopFriends() {
		return oneHopFriends;
	}
	public void setOneHopFriends(Map<Long, Integer> oneHopFriends) {
		this.oneHopFriends = oneHopFriends;
	}
	@Override
	public String toString() {
		return "UserData [neighbors=" + neighbors + ", hobbies=" + hobbies + "]";
	}
	public Map<Long, Integer> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(Map<Long, Integer> neighbors) {
		this.neighbors = neighbors;
	}
	public List<String> getHobbies() {
		return hobbies;
	}
	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

}
