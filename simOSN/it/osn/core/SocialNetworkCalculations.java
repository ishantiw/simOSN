package it.osn.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

public class SocialNetworkCalculations extends SingleValueHolder implements CDProtocol{
	
	/** String to get the initial value */
	protected static final String PAR_INTEREST = "interest";
	/** String to get the aggregate function to use */
	protected static final String param_experiment = "exp";
	
	
	protected FriendCircle User;
	
	//protected int friendSize;
	
	protected String exp;
	
	/** Interest value. Obtained from config property {@link #PAR_INTEREST}. */
	private final int interest_value;
	
	protected int interest;
	

	public SocialNetworkCalculations(String prefix) {
		super(prefix);
		//get interest value from the config value
		interest_value = (Configuration.getInt(prefix + "." + PAR_INTEREST, 1));
		//get interest value from the config value
		exp = Configuration.getString(prefix + "." + param_experiment);
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
		List neighborList = new ArrayList();
		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);
			long peerID =  peer.getID();
			long peerIndex = peer.getIndex();
			//System.out.println("peer ID is "+ peerID+ " peer index is "+peerIndex);
			// The selected peer could be inactive
			if (!peer.isUp())
			continue;
			SocialNetworkCalculations user = (SocialNetworkCalculations)peer.getProtocol(protocolID);
				neighbor = user;
				neighborList.add(neighbor);
			if(neighbor ==null)
				return;
			//if(i<5)
			//oneHopAwayCalculations(neighbor);
			//resultDisplay = foundNeighbor(neighbor, peerID);
			//resultDisplay.concat(" at Cycle ")
			//System.err.println(resultDisplay);
		}
		initializeCircle(neighborList, linkable);
		disseminateInfoPush(neighborList);
		//System.err.print(resultDisplay);
		//System.err.println("friend list is "+neighborList.size());
		
	}
	
	protected String foundNeighbor(SocialNetworkCalculations neighbor, long peerID){
		//In this function we can perform other tasks like exchanging the value or updating etc.
		//System.out.println("Inside found Neighbor");
		//System.out.println("Size is "+ this.interest+" and peer size is "+neighbor.interest);
		//System.out.println("-->>>Size of the hobbiees"+ this.User.userdata.hobbies);
		//boolean found = this.User.userdata.hobbies.contains(neighbor.User.userdata.hobbies);
		boolean found =  false;//System.out.println("usr sizze is "+this.User.size+"neighbor size is"+ neighbor.User.size);
		boolean alreadyFound = false;
		boolean isOneHopAway = false;
		String result = "";
		//System.out.println("Intereest "+this.interest+" interest 2 "+neighbor.interest);
		if((this.interest == 555 && neighbor.interest==555)){//System.out.println("inside1"+this.User.userdata.neighbors.containsKey(peerID));
			if(!(this.User.userdata.neighbors.containsKey(peerID))){
				//System.out.println("inside2");
				this.User.userdata.neighbors.put(peerID, neighbor.interest);
				found = true;	
			} else if (!(this.User.userdata.oneHopFriends.containsKey(peerID))){
				isOneHopAway = true;
			}
		} 
		
		if(found){
			result = "\n**Found Friend "+peerID;//System.out.println(result);
		} 
		if (isOneHopAway) {
			result = "\n*&&&&&&*Found one Hop Away "+peerID;
		}
		return result;
	}
	//Adding neighbors to friend circle
	protected void initializeCircle(List neighborList, Linkable linkable){
		this.User.size = linkable.degree();
        for(int k = 0; k <linkable.degree(); k++){
        	SocialNetworkCalculations user = (SocialNetworkCalculations) neighborList.get(k);
        	for(int j = 0; j< linkable.degree(); j++){
        		long neighborID = linkable.getNeighbor(j).getID();
        		user.getUser().userdata.neighbors.put(neighborID, user.interest);//System.out.println("Degree is "+linkable.degree()+ "ID is "+linkable.getNeighbor(j).getID());
        	//System.out.println("neighbor id added is "+neighborID+ "user value is "+user.getValue());
        	}
        }
	}
	//Adding all the users which are one hop away or "mutual friends"
	protected void oneHopAwayCalculations(SocialNetworkCalculations neighbor){
		//adding neighbors friends to one hop away, ignoring the ones the user already have
		neighbor.User.userdata.neighbors.forEach(this.User.userdata.oneHopFriends::putIfAbsent);
		/*for(Iterator<Entry<Long, Integer>> it = this.User.userdata.oneHopFriends.entrySet().iterator(); it.hasNext(); ) {
		      if(this.User.userdata.oneHopFriends.size() > 10){
		       it.remove();
		      }
		  }*/
		for (int j=0; j<this.User.userdata.oneHopFriends.size(); j++){
			if(j>5){
				this.User.userdata.oneHopFriends.remove(j);
			}
		}
		//System.out.println("Before "+this.User.userdata.oneHopFriends.size());
		//this.User.userdata.neighbors.entrySet().removeAll(this.User.userdata.oneHopFriends.entrySet());
		
		//removing the entries which are already neighbor
		for(Iterator<Entry<Long, Integer>> it1 = this.User.userdata.oneHopFriends.entrySet().iterator(); it1.hasNext(); ) {
			Entry<Long, Integer> entry1 = it1.next();
		for(Iterator<Entry<Long, Integer>> it2 = this.User.userdata.neighbors.entrySet().iterator(); it2.hasNext(); ) {
		      Entry<Long, Integer> entry2 = it2.next();
		      if(entry1.getKey().equals(entry2.getKey())) {
		        it1.remove();
		      }
		    }
		}
		
		//System.out.println("After "+this.User.userdata.oneHopFriends.size());
	}
	//Disseminate info to a neighbor 
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
			}
			if(friend.User.flag == -1 && friend.interest == 555 ){
				friend.User.flag = -2;
			}
		}
		
	}
	
	//match the friend
	protected void setInterestVal(double interest){
		this.interest = interest_value;	
	}
}
