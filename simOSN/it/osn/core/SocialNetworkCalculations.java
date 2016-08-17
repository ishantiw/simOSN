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
 * <h1>OSN SocialNetworkCalculations!</h1>
 * This class implements the gossip algorithm. Using push strategy, taking first 10 neighbors in the cache to disseminate info and updating oneHopaway friends . 
 * Then we are comparing the {@value}interest with its neighbors and assigning the greater value to the neighbor if it has smaller value.
 * Checking flag every time to see if it has reached the specific node in which we are interested
 * * <p>
 * 
 * @author  Ishan Tiwari
 * @version 1.0
 * @since   04.08.2016 
 */
public class SocialNetworkCalculations extends SingleValueHolder implements CDProtocol{

	/** String to get the initial value */
	protected static final String PAR_INTEREST = "interest";
	/** String to get the aggregate function to use */
	protected static final String param_experiment = "exp";
	/** String to get the push, pull or pushpull strategy */
	protected static final String param_type = "type";

	protected FriendCircle User;

	protected String exp;

	protected String type;


	/** Interest value. Obtained from config property {@link #PAR_INTEREST}. */
	private final int interest_value;
	protected int interest;



	public SocialNetworkCalculations(String prefix) {
		super(prefix);
		//get interest value from the config value
		interest_value = (Configuration.getInt(prefix + "." + PAR_INTEREST, 1));
		//get interest value from the config value
		exp = Configuration.getString(prefix + "." + param_experiment);
		//get the type of dissemination
		type = Configuration.getString(prefix + "." + param_type);
		//UserData userdata = new UserData();
		//User =  new FriendCircle(userdata);
		interest = interest_value;
		if(exp.equals("find")){//to identify different functions
			System.out.println("Inside find");
		}

	}

	public FriendCircle getUser() {
		return User;
	}

	public void setUser(FriendCircle user) {
		User = user;
	}

	@Override
	public void nextCycle(Node node, int protocolID) {
		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		SocialNetworkCalculations neighbor = null;

		boolean found = false;
		String resultDisplay = null;
		this.User.userdata.offlineUsers = 0;
		List<SocialNetworkCalculations> neighborList = new ArrayList<SocialNetworkCalculations>();
		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);
			long peerID =  peer.getID();
			long peerIndex = peer.getIndex();
			// The selected peer could be inactive
			if (!peer.isUp()){
				this.User.userdata.offlineUsers ++;
				continue;
			}
			
			SocialNetworkCalculations user = (SocialNetworkCalculations)peer.getProtocol(protocolID);
			neighbor = user;
			neighborList.add(neighbor);
			if(neighbor ==null)
				return;
		}
		if(node.isUp()){
			initializeCircle(neighborList, linkable);
			oneHopAwayCalculations(neighborList);
			addingNewFriends(linkable, protocolID);
			if(type.equals("push")){
				disseminateInfoPush(neighborList);
			} else if(type.equals("pull")){
				disseminateInfoPull(neighborList);
			} else if(type.equals("pushpull")){
				disseminateInfoPushPull(neighborList);
			} else {
				disseminateInfoPush(neighborList);
			}
		}
	}

	//Adding neighbors to friend circle
	protected void initializeCircle(List neighborList, Linkable linkable){
		this.User.size = linkable.degree();
		for(int k = 0; k <neighborList.size(); k++){
			SocialNetworkCalculations user = (SocialNetworkCalculations) neighborList.get(k);
			for(int j = 0; j< linkable.degree(); j++){
				if(linkable.getNeighbor(j).isUp()){
					long neighborID = linkable.getNeighbor(j).getID();
					user.getUser().userdata.neighbors.put((int) neighborID, 0);
				} 
			}
		}
	}
	//Adding all the users which are one hop away or "mutual friends"
	protected void oneHopAwayCalculations(List<SocialNetworkCalculations> neighborList){
		//adding neighbors friends to one hop away, ignoring the ones the user already have
		for (int i = 0; i < neighborList.size(); i++){
			SocialNetworkCalculations neighbor = neighborList.get(i);

			//Important step: putting the one hop away friend if not present otherwise updating its frequency

			for(Iterator<Entry<Integer, Integer>> it1 = neighbor.User.userdata.neighbors.entrySet().iterator(); it1.hasNext(); ) {
				Entry<Integer, Integer> entryPeer = it1.next();
				if(!this.User.userdata.oneHopFriends.containsKey(entryPeer.getKey())){
					//System.out.println("hereeee"+entryPeer.getValue());
					this.User.userdata.oneHopFriends.put(entryPeer.getKey(), entryPeer.getValue());
				}
				else {
					//System.out.println("in Her"+entryPeer.getValue()+1);
					this.User.userdata.oneHopFriends.replace(entryPeer.getKey(), (entryPeer.getValue()+1));
				}
			}

			//removing the entries which are already neighbor
			this.User.userdata.neighbors.forEach(this.User.userdata.oneHopFriends:: remove);				
		}
		//sorting them according their frequency
		sortByValue(this.User.userdata.oneHopFriends);
		//trimming the one hop away list top 5 neighbors
		int count = 0;
		for(Iterator<Entry<Integer, Integer>> itOneHop = this.User.userdata.oneHopFriends.entrySet().iterator(); itOneHop.hasNext(); ) {
			Entry<Integer, Integer> entryPeer = itOneHop.next();
			count ++;
			if(count > 5)
				itOneHop.remove();
		}
	}
	//adding new friends based on frequency factor
	protected void addingNewFriends(Linkable linkable, int protocolID){
		for(Iterator<Entry<Integer, Integer>> it = this.User.userdata.oneHopFriends.entrySet().iterator(); it.hasNext(); ) {
			Entry<Integer, Integer> entryfind = it.next();
			Node node = Network.get(entryfind.getKey());
			if(node != null && node.isUp()){
				SocialNetworkCalculations tempNode = (SocialNetworkCalculations) node.getProtocol(protocolID);

				List tempList = tempNode.User.userdata.hobbies;
				tempList.retainAll(this.User.userdata.hobbies);
				//System.out.println("Size is "+entryfind.getValue());
				if(entryfind.getValue() > 0 && tempList.size() > 2){
					//System.out.println("In here");
					linkable.addNeighbor(Network.get((Integer)entryfind.getKey()));
					this.User.userdata.neighbors.put(entryfind.getKey(), entryfind.getValue());
					it.remove();
					this.User.userdata.newFriends ++;
				} 
			}
		}
	}
	//Disseminate info to a neighbor using push strategy
	protected void disseminateInfoPush(List neighborList){
		//Take the list 
		//System.out.println("Inside");
		//Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size()/4; i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			//update the value of interest of neighbor if less
			if(friend.interest < this.interest){
				friend.interest = this.interest;
				friend.User.userdata.hopCount = this.User.userdata.hopCount + 1;
			}
			if(friend.User.flag == -1 && friend.interest == 555 ){
				friend.User.flag = -2;
			}
			if(this.User.flag == -1 && this.interest == 555 ){
				this.User.flag = -2;
			}
		}
	}

	//Disseminate info to a neighbor using pull strategy
	protected void disseminateInfoPull(List neighborList){
		//Take the list 
		//System.out.println("Inside");
		//Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size()/4; i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			//update the value of interest of neighbor if less
			if(friend.interest > this.interest){
				this.interest = friend.interest;
				this.User.userdata.hopCount = friend.User.userdata.hopCount + 1;
			}
			if(friend.User.flag == -1 && friend.interest == 555 ){
				friend.User.flag = -2;
			}
			if(this.User.flag == -1 && this.interest == 555 ){
				this.User.flag = -2;
			}
		}
	}
	//Disseminate info to a neighbor using pushpull strategy
	protected void disseminateInfoPushPull(List neighborList){
		//Take the list 
		//System.out.println("Inside");
		//Taking top half neighbors
		Collections.shuffle(neighborList);
		for (int i = 0; i < neighborList.size()/4; i++) {
			SocialNetworkCalculations friend = (SocialNetworkCalculations) neighborList.get(i);
			//update the value of interest of neighbor if less
			if(friend.interest > this.interest){
				this.interest = friend.interest;
				this.User.userdata.hopCount = friend.User.userdata.hopCount + 1;
			} else if(friend.interest < this.interest){
				friend.interest = this.interest;
				friend.User.userdata.hopCount = this.User.userdata.hopCount + 1;
			}
			if(friend.User.flag == -1 && friend.interest == 555 ){
				friend.User.flag = -2;
			}
			if(this.User.flag == -1 && this.interest == 555 ){
				this.User.flag = -2;
			}
		}
	}
	//match the friend
	protected void setInterestVal(double interest){
		this.interest = interest_value;	
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
