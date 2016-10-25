package it.osn.core;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;
/**
* <h1>OSN Observer!</h1>
* This class is to display the results at every cycle. We are using 
* peerSim {@link}IncrementalStats to get the general info about the network
* <p>
* 
* @author  Ishan Tiwari
* @version 1.0
* @since   04.08.2016 
*/
public class UserProfileDissemination implements Control{
	/////////////////////////////////////////////////////////////////////////
	//Constants
	/////////////////////////////////////////////////////////////////////////
	/**
	 * The protocol to operate on.
	 */
	private static final String PAR_PROT = "protocol";
	/////////////////////////////////////////////////////////////////////////
	//Fields
	/////////////////////////////////////////////////////////////////////////
	/**
	 * The name of this observer in the configuration file.
	 */
	private final String name;
	/** Protocol identifier,*/
	private final int pid;
	/////////////////////////////////////////////////////////////////////////
	//Constructor
	/////////////////////////////////////////////////////////////////////////
	public UserProfileDissemination(String name) {
		this.name = name;
		pid = Configuration.getPid(name + "." + PAR_PROT);
	}
	/////////////////////////////////////////////////////////////////////////
	//Methods
	/////////////////////////////////////////////////////////////////////////
	protected double averageFriendCircle(ArrayList<Integer> circleSize){
		double averageCircleSize = 0;
		for (int i = 0; i < circleSize.size(); i++) {
			averageCircleSize += circleSize.get(i); 
		}
		return averageCircleSize/Network.size();
	}
	
	protected double averageOneHopAway(ArrayList<Integer> oneHopSize){
		double averageOneHopSize = 0;
		for (int i = 0; i < oneHopSize.size(); i++) {
			averageOneHopSize += oneHopSize.get(i); 
		}
		return averageOneHopSize/Network.size();
	}
	
	protected double averageConnectionSpeed(ArrayList<Double> connectionSpeedList){
		double averageconnectionSpeed = 0;
		for (int i = 0; i < connectionSpeedList.size(); i++) {
			averageconnectionSpeed += connectionSpeedList.get(i); 
		}
		return averageconnectionSpeed/Network.size();
	}

	public boolean execute() {
		IncrementalStats stats = new IncrementalStats();
		ArrayList<Integer> circleSize = new ArrayList<Integer>();
		ArrayList<Integer> oneHopSize = new ArrayList<Integer>();
		ArrayList<Double> avgConnectionSpeed = new ArrayList<Double>();
		HashMap<Integer, HashMap> nodeList = new HashMap<>();
		ArrayList<Integer> newNodeList = new ArrayList<Integer>();
		int displayStatus = 0;
		int totalOfflineNodes = 0;
		int totalNewfriends = 0;
		int totalNewRandomFriends = 0;
		int totalDuplicateMessage = 0;
		int totalMessages = 0;
		int totalRemovedOfflineContacts = 0;
		boolean foundFriend  = false;
		int hopsToReach = 0;
		for (int i = 0; i < Network.size(); i++) {
			
			SocialNetworkCalculations protocol = (SocialNetworkCalculations) Network.get(i).getProtocol(
					pid);
			
			stats.add(protocol.interest);
			circleSize.add(protocol.User.size);
			oneHopSize.add(protocol.User.userdata.oneHopFriends.size());
			avgConnectionSpeed.add(protocol.User.userdata.connectionSpeed);
			totalOfflineNodes += protocol.User.userdata.offlineUsers; 
			totalNewfriends += protocol.User.userdata.newFriends;
			totalNewRandomFriends += protocol.User.userdata.newRandomFriends;
			totalDuplicateMessage += protocol.User.userdata.duplicatesMessage;
			totalMessages += protocol.User.userdata.totalMessages;
			totalRemovedOfflineContacts += protocol.User.userdata.removedOfflineContacts;
			//freqHop.add(protocol.User.userdata.oneHopFriends.);
			if(protocol.User.flag == -2){
				foundFriend = true;
				hopsToReach = protocol.User.userdata.hopCount;
			}
			//System.out.println("Size of neighbor"+protocol.User.userdata.neighbors.size());
			nodeList.put(protocol.User.id, (HashMap) protocol.User.userdata.neighbors);
			newNodeList.addAll(protocol.User.userdata.newFriendsList);
		}
		
		Date now = new Date(System.currentTimeMillis());
		double avgFriendCircle = averageFriendCircle(circleSize);
		double avgOneHopAway = averageOneHopAway(oneHopSize);
		double avgConnSpeed = averageConnectionSpeed(avgConnectionSpeed);
		double averageDuplicate = totalDuplicateMessage / Network.size();
		/* Printing statistics */
		System.out.println(name + ": " + stats);
		System.out.println("Average circle size: "+ avgFriendCircle + " Average one Hop Away Size: " + avgOneHopAway);
		System.out.println("Average Connection Speed in the system is " + avgConnSpeed+ " and Total number of failed Links "+totalOfflineNodes);
		System.out.println("Total new friends " + totalNewfriends);
		System.out.println("Total new random friends " + totalNewRandomFriends);
		System.out.println("Average duplicate messages on nodes is: " + averageDuplicate + ": Average Number of messages on a node: "+totalMessages/Network.size());
		System.out.println("Total Offline contacts removed: "+totalRemovedOfflineContacts);
		/* Printing a message when everyone received the message */
		SocialGraphDisplay display = new SocialGraphDisplay();
		//if(displayStatus ==0)
		//display.displayGraph(nodeList, newNodeList);
		if(stats.getMax() == stats.getMin()) {
			System.err.println("Everyone Received the message at "+now);
			
			
			//displayStatus =1;
		}
		if(foundFriend)
			System.err.println("friend found status: "+foundFriend + " at->>> "+now + " After "+ hopsToReach +" Peers");
		
		return false;
	}
	
}
