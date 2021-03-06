package it.osn.core;
/**
 * <h1>User Data!</h1> 
 * This class holds all the local variables which a node maintains locally
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {
	double connectionSpeed;
	int hopCount;
	int offlineUsers;
	int newFriends;
	int newRandomFriends;
	int duplicatesMessage;
	int totalMessages;
	int removedOfflineContacts;
	protected List<Integer> removedContactIDs = new ArrayList<Integer>();
	protected Map<Integer, Integer> offlineContacts = new HashMap<>();
	protected Map<Integer, Integer> neighbors = new HashMap<>();
	protected List<String> hobbies = new ArrayList<String>();
	protected Map<Integer, Integer> oneHopFriends = new HashMap<>();
	protected List<Integer> newFriendsList = new ArrayList<Integer>();

	public Map<Integer, Integer> getOneHopFriends() {
		return oneHopFriends;
	}

	public void setOneHopFriends(Map<Integer, Integer> oneHopFriends) {
		this.oneHopFriends = oneHopFriends;
	}

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
