package it.osn.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

/**
 * <h1>OSN SocialNetworkCalculations!</h1> This class implements the gossip
 * algorithm. Using push strategy, taking first 10 neighbors in the cache to
 * disseminate info and updating oneHopaway friends . Then we are comparing the
 * {@value}interest with its neighbors and assigning the greater value to the
 * neighbor if it has smaller value. Checking flag every time to see if it has
 * reached the specific node in which we are interested *
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
public class SocialNetworkCalculations extends SingleValueHolder implements CDProtocol {

	/** String to get the initial value */
	protected static final String PAR_INTEREST = "peak_interest";

	/** String to get the aggregate function to use */
	protected static final String param_experiment = "exp";

	/** String to get the push, pull or pushpull strategy */
	protected static final String param_type = "type";

	/**
	 * String to get the one hop list which will be maintained by every node in
	 * the system
	 */
	protected static final String PAR_ONEHOPSIZE = "oneHopSize";

	/**
	 * String to get the size of the peer list which will be exchanged by every
	 * node in the system
	 */
	protected static final String PAR_PEER_SIZE = "peerListSize";

	// attributes related to friend circle
	protected FriendCircle User;

	protected String exp;

	protected String type;

	protected final int oneHopSize;

	protected final int peerListSize;

	/** Interest value. Obtained from config property {@link #PAR_INTEREST}. */
	protected final int interest_value;

	protected int interest;
	/**
	 * static list that contains all the pssoble locations that will be assigned
	 * to nodes
	 */
	private static String[] locations = { "berlin", "frankfurt", "paris", "grenoble", "oslo", "london", "barcelona",
			"madrid", "rome", "pisa", "florence", "naples", "rome", "moscow", "delhi", "mumbai" };

	private static String[] hobbies = { "Basketball", "Tennis", "Movies", "Gaming", "Cricket", "Chess", "Soccer",
			"Golf", "Travelling", "Polo", "Music", "Football", "Meeting" };
	
	public SocialNetworkCalculations(String prefix) {
		super(prefix);
		// get interest value from the config value
		interest_value = (Configuration.getInt(prefix + "." + PAR_INTEREST, 1));
		// get interest value from the config value
		exp = Configuration.getString(prefix + "." + param_experiment);
		// get the type of dissemination
		type = Configuration.getString(prefix + "." + param_type);
		// get the size of one hop size you want to maintain
		oneHopSize = Configuration.getInt(prefix + "." + PAR_ONEHOPSIZE);
		// get the size of neighbor size you want to exchange
		peerListSize = Configuration.getInt(prefix + "." + PAR_PEER_SIZE);
		// the max value which you want to send to a random node in the network
		interest = interest_value;
		if (exp.equals("find")) {// to identify different functions
			System.out.println("Inside find");
		}
	}

	public FriendCircle getUser() {
		return User;
	}

	public void setUser(FriendCircle user) {
		User = user;
	}

	/** This is overridden method of PeerSim Simulator */
	@Override
	public void nextCycle(Node node, int protocolID) {
		
		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		if(this.User == null) {
			newNodesJoiningInitializer(linkable);
		}
		SocialNetworkCalculations neighbor = null;


		this.User.userdata.offlineUsers = 0;
		List<SocialNetworkCalculations> neighborList = new ArrayList<SocialNetworkCalculations>();
		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);
			this.User.size = linkable.degree();
			SocialNetworkCalculations user = (SocialNetworkCalculations) peer.getProtocol(protocolID);

			// The selected peer could be inactive and then removing these
			// inactive neighbors
			if (!peer.isUp()) {
				this.User.userdata.offlineUsers++;
				int ID = (int) peer.getID();

				if (this.User.userdata.offlineContacts.containsKey(ID)) {
					for (Iterator<Entry<Integer, Integer>> iterator = this.User.userdata.offlineContacts.entrySet()
							.iterator(); iterator.hasNext();) {
						Entry<Integer, Integer> entryPeer = iterator.next();
						if (entryPeer.getKey() == (int) peer.getID()) {
							entryPeer.setValue(entryPeer.getValue() + 1);
						}
						if (entryPeer.getValue() > 4) {
							iterator.remove();
							if (!this.User.userdata.removedContactIDs.contains((int) linkable.getNeighbor(i).getID())) {
								this.User.userdata.removedContactIDs.add((int) linkable.getNeighbor(i).getID());
								this.User.userdata.removedOfflineContacts++;
							}
						}
					}
				} else {
					this.User.userdata.offlineContacts.put(ID, 0);
				}
				continue;
			}

			neighbor = user;
			if (neighbor == null)
				return;
			neighborList.add(neighbor);

		}
		int randomFriendPicker = (int) (Math.random() * 10);

		if (node.isUp()) {

			oneHopAwayCalculations(neighborList);
			neighborList = addingNewFriends(linkable, protocolID, neighborList);
			if (exp.equals("find")) {
				if (type.equals("push")) {
					disseminateInfoPush(neighborList);
				} else if (type.equals("pull")) {
					disseminateInfoPull(neighborList);
				} else if (type.equals("pushpull")) {
					disseminateInfoPushPull(neighborList);
				} else {
					disseminateInfoPush(neighborList);
				}
			} else if (exp.equals("speed")) {
				disseminateBasedOnSpeed(neighborList);
			} else if (exp.equals("random")) {
				if (type.equals("push")) {
					disseminateInfoPush(neighborList);
				} else if (type.equals("pull")) {
					disseminateInfoPull(neighborList);
				} else if (type.equals("pushpull")) {
					disseminateInfoPushPull(neighborList);
				} else {
					disseminateInfoPush(neighborList);
				}
				if (node.getID() != 0 && (randomFriendPicker % node.getID() == 0)) {
					String location1 = locations[(int) (Math.random() * locations.length)];
					String location2 = locations[(int) (Math.random() * locations.length)];
					String location3 = locations[(int) (Math.random() * locations.length)];
					String location4 = locations[(int) (Math.random() * locations.length)];
					this.User.locations.add(location1);
					this.User.locations.add(location2);
					this.User.locations.add(location3);
					this.User.locations.add(location4);
					findRandomFriends(linkable, protocolID);
				}
			}
		}
	}

	// Adding all the users which are one hop away or "mutual friends"
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	protected void oneHopAwayCalculations(List<SocialNetworkCalculations> neighborList) {
		// adding neighbors friends to one hop away, ignoring the ones the user
		// already have
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size() / 4; i++) {
			SocialNetworkCalculations neighbor = neighborList.get(i);

			// Important step: putting the one hop away friend if not present
			// otherwise updating its frequency

			for (Iterator<Entry<Integer, Integer>> it1 = neighbor.User.userdata.neighbors.entrySet().iterator(); it1
					.hasNext();) {
				Entry<Integer, Integer> entryPeer = it1.next();
				if (!this.User.userdata.oneHopFriends.containsKey(entryPeer.getKey())) {
					this.User.userdata.oneHopFriends.put(entryPeer.getKey(), 0);
				} else {
					int freqVal = this.User.userdata.oneHopFriends.get(entryPeer.getKey());
					this.User.userdata.oneHopFriends.replace(entryPeer.getKey(), (freqVal + 1));
				}
			}

			// removing the entries which are already neighbor
			this.User.userdata.neighbors.forEach(this.User.userdata.oneHopFriends::remove);
		}
		// sorting them according their frequency
		// sortByValue(this.User.userdata.oneHopFriends);
		MyComparator comp = new MyComparator(this.User.userdata.oneHopFriends);
		Map<Integer, Integer> temp1Hop = new TreeMap(comp);
		temp1Hop.putAll(this.User.userdata.oneHopFriends);
		// trimming the one hop away list top 5 neighbors
		int count = 0;
		for (Iterator<Entry<Integer, Integer>> itOneHop = this.User.userdata.oneHopFriends.entrySet()
				.iterator(); itOneHop.hasNext();) {
			Entry<Integer, Integer> entryPeer = itOneHop.next();
			count++;
			if (count > oneHopSize)
				itOneHop.remove();
		}
	}

	// adding new friends based on frequency factor
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<SocialNetworkCalculations> addingNewFriends(Linkable linkable, int protocolID,
			List<SocialNetworkCalculations> neighborList) {
		for (Iterator<Entry<Integer, Integer>> it = this.User.userdata.oneHopFriends.entrySet().iterator(); it
				.hasNext();) {
			Entry<Integer, Integer> entryfind = it.next();
			Node node = Network.get(entryfind.getKey());
			if (node != null && node.isUp()) {
				SocialNetworkCalculations tempNode = (SocialNetworkCalculations) node.getProtocol(protocolID);
				if(tempNode.User== null)
					continue;
				
				List tempList = tempNode.User.userdata.hobbies;
				List<String> commonHobbies = new ArrayList<String>(tempList);
				commonHobbies.retainAll(this.User.userdata.hobbies);

				if (!this.User.userdata.newFriendsList.contains(entryfind.getKey())
						&& !(this.User.userdata.neighbors.containsKey(entryfind.getKey())) && entryfind.getValue() > 5
						&& commonHobbies.size() > 5) {

					linkable.addNeighbor(Network.get((Integer) entryfind.getKey()));
					neighborList.add(tempNode);
					this.User.userdata.neighbors.put(entryfind.getKey(), entryfind.getValue());
					this.User.userdata.newFriendsList.add(entryfind.getKey());
					it.remove();
					this.User.userdata.newFriends++;
				}
			}
		}
		return neighborList;
	}

	// Disseminate info to a neighbor using push strategy
	@SuppressWarnings("rawtypes")
	protected void disseminateInfoPush(List neighborList) {

		// Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size(); i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			friend.User.userdata.totalMessages++;
			if (friend.interest != interest_value) {
				if (friend.interest < this.interest) {
					friend.interest = this.interest;
					friend.User.userdata.hopCount = this.User.userdata.hopCount + 1;
				}
				if (friend.User.flag == -1 && friend.interest == interest_value) {
					friend.User.flag = -2;
				}
				if (this.User.flag == -1 && this.interest == interest_value) {
					this.User.flag = -2;
				}
			} else {
				friend.User.userdata.duplicatesMessage++;
			}
		}
	}

	// Disseminate info to a neighbor using pull strategy
	@SuppressWarnings("rawtypes")
	protected void disseminateInfoPull(List neighborList) {
		// Take the list
		// Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size() / 4; i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			friend.User.userdata.totalMessages++;
			if (this.interest != interest_value) {
				if (friend.interest > this.interest) {
					this.interest = friend.interest;
					this.User.userdata.hopCount = friend.User.userdata.hopCount + 1;
				}
				/*
				 * if(friend.User.flag == -1 && friend.interest ==
				 * interest_value){ friend.User.flag = -2; }
				 */
				if (this.User.flag == -1 && this.interest == interest_value) {
					this.User.flag = -2;
				}
			} else {
				this.User.userdata.duplicatesMessage++;
			}
		}
	}

	// Disseminate info to a neighbor using pushpull strategy
	@SuppressWarnings("rawtypes")
	protected void disseminateInfoPushPull(List neighborList) {
		// Take the list
		// System.out.println("Inside");
		// Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size(); i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			friend.User.userdata.totalMessages++;
			if (friend.interest != interest_value) {
				if (friend.interest > this.interest) {
					this.interest = friend.interest;
					this.User.userdata.hopCount = friend.User.userdata.hopCount + 1;
				} else if (friend.interest < this.interest) {
					friend.interest = this.interest;
					friend.User.userdata.hopCount = this.User.userdata.hopCount + 1;
				}
				if (friend.User.flag == -1 && friend.interest == interest_value) {
					friend.User.flag = -2;
				}
				if (this.User.flag == -1 && this.interest == interest_value) {
					this.User.flag = -2;
				}
			} else {
				friend.User.userdata.duplicatesMessage++;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void findRandomFriends(Linkable linkable, int protocolID) {
		int randomID = (int) (Math.random() * Network.size());
		Node randomFriend = Network.get(randomID);
		if (randomFriend != null && randomFriend.isUp()) {
			SocialNetworkCalculations tempNode = (SocialNetworkCalculations) randomFriend.getProtocol(protocolID);
			if (!this.User.userdata.neighbors.containsKey(randomID)
					&& !this.User.userdata.oneHopFriends.containsKey(randomID)) {
				if(tempNode.User == null)
					return;
				List tempList = tempNode.User.userdata.hobbies;

				List<String> commonHobbies = new ArrayList<String>(tempList);
				commonHobbies.retainAll(this.User.userdata.hobbies);

				List<String> locationList = tempNode.User.locations;

				List<String> commonLocations = new ArrayList<String>(locationList);
				commonLocations.retainAll(this.User.locations);

				if (commonHobbies.size() > 2 && commonLocations.size() > 0) {
					linkable.addNeighbor(Network.get(randomID));
					this.User.userdata.neighbors.put(randomID, 0);
					this.User.userdata.newFriendsList.add(randomID);
					this.User.userdata.newRandomFriends++;
				}
			}
		}
	}

	// Disseminating information based on the connection speed in the friend
	// circle
	@SuppressWarnings("rawtypes")
	protected void disseminateBasedOnSpeed(List neighborList) {
		// Take the list
		// Taking top half neighbors
		Collections.shuffle(neighborList);
		double averageSpeed = averageConnectionSpeed(neighborList);
		for (int i = 0; i < neighborList.size() / 4; i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			// if(friend.User.userdata.connectionSpeed > )
			// update the value of interest of neighbor if less
			if (friend.interest > this.interest && (this.User.userdata.connectionSpeed > (averageSpeed / 2))) {
				this.interest = friend.interest;
				this.User.userdata.hopCount = friend.User.userdata.hopCount + 1;
			} else if (friend.interest < this.interest && (friend.User.userdata.connectionSpeed > (averageSpeed / 2))) {
				friend.interest = this.interest;
				friend.User.userdata.hopCount = this.User.userdata.hopCount + 1;
			}
			if (friend.User.flag == -1 && friend.interest == interest_value) {
				friend.User.flag = -2;
			}
			if (this.User.flag == -1 && this.interest == interest_value) {
				this.User.flag = -2;
			}
		}

	}

	// calculating the average connection speed in the friend circle
	@SuppressWarnings("rawtypes")
	protected double averageConnectionSpeed(List neighborList) {
		double averageSpeed = 0;
		int size = neighborList.size();
		for (int i = 0; i < size; i++) {
			SocialNetworkCalculations tempNode = (SocialNetworkCalculations) neighborList.get(i);
			averageSpeed += tempNode.User.userdata.connectionSpeed;
		}
		averageSpeed = averageSpeed / size;
		return averageSpeed;
	}
	
	
	protected void newNodesJoiningInitializer(Linkable linkable) {
		
		UserData data = new UserData();
		int val = 0;
		String hobbie1 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie2 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie3 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie4 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie5 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie6 = hobbies[(int) (Math.random() * hobbies.length)];
		String hobbie7 = hobbies[(int) (Math.random() * hobbies.length)];
		data.hobbies.add(hobbie1);
		data.hobbies.add(hobbie2);
		data.hobbies.add(hobbie3);
		data.hobbies.add(hobbie4);
		data.hobbies.add(hobbie5);
		data.hobbies.add(hobbie6);
		data.hobbies.add(hobbie7);
		FriendCircle circle = new FriendCircle(data);

		this.setUser(circle);
		for (int k = 0; k < linkable.degree(); k++) {
			if (linkable.getNeighbor(k).isUp()) {
				long neighborID = linkable.getNeighbor(k).getID();
				this.User.userdata.neighbors.put((int) neighborID, 0);
			}
		}
		this.User.flag = 1;
		this.interest = val;
		this.User.userdata.connectionSpeed = val * 10 * (Math.random() * (10 - 2));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}

@SuppressWarnings("rawtypes")
class ValueComparator implements Comparator {
	Map map;

	public ValueComparator(Map map) {
		this.map = map;
	}

	@SuppressWarnings("unchecked")
	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}

@SuppressWarnings("rawtypes")
class MyComparator implements Comparator {

	Map map;

	public MyComparator(Map map) {
		this.map = map;
	}

	public int compare(Object o1, Object o2) {

		return ((Integer) map.get(o2)).compareTo((Integer) map.get(o1));

	}
}
