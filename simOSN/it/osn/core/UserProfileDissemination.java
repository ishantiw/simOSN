package it.osn.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

/**
 * <h1>OSN Observer!</h1> This class is to display the results at every cycle.
 * We are using peerSim {@link}IncrementalStats to get the general info about
 * the network
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
public class UserProfileDissemination implements Control {
	/////////////////////////////////////////////////////////////////////////
	// Constants
	/////////////////////////////////////////////////////////////////////////
	/**
	 * The protocol to operate on.
	 */
	private static final String PAR_PROT = "protocol";
	/**
	 * Number of cycles till the graph will be displayed.
	 */
	private static final String PAR_DISPLAY_GRAPH ="displayGraphNumber";
	
	/////////////////////////////////////////////////////////////////////////
	// Fields
	/////////////////////////////////////////////////////////////////////////
	/**
	 * The name of this observer in the configuration file.
	 */
	private final String name;
	/** Protocol identifier, */
	private final int pid;
	/**
	 * Number of cycles till the graph will be displayed identifier
	 */
	private final int displayGraphNumber;
	private static int cycles;
	/////////////////////////////////////////////////////////////////////////
	// Constructor
	/////////////////////////////////////////////////////////////////////////
	public UserProfileDissemination(String name) {
		this.name = name;
		pid = Configuration.getPid(name + "." + PAR_PROT);
		displayGraphNumber = Configuration.getInt(name + "." + PAR_DISPLAY_GRAPH);
	}

	/////////////////////////////////////////////////////////////////////////
	// Methods
	/////////////////////////////////////////////////////////////////////////
	protected double averageFriendCircle(ArrayList<Integer> circleSize, int noOfNodes) {
		double averageCircleSize = 0;
		for (int i = 0; i < circleSize.size(); i++) {
			averageCircleSize += circleSize.get(i);
		}
		return averageCircleSize / noOfNodes;
	}

	protected double averageOneHopAway(ArrayList<Integer> oneHopSize, int noOfNodes) {
		double averageOneHopSize = 0;
		for (int i = 0; i < oneHopSize.size(); i++) {
			averageOneHopSize += oneHopSize.get(i);
		}
		return averageOneHopSize / noOfNodes;
	}

	protected double averageConnectionSpeed(ArrayList<Double> connectionSpeedList, int noOfNodes) {
		double averageconnectionSpeed = 0;
		for (int i = 0; i < connectionSpeedList.size(); i++) {
			averageconnectionSpeed += connectionSpeedList.get(i);
		}
		return averageconnectionSpeed / noOfNodes;
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	public boolean execute() {cycles ++;
		IncrementalStats stats = new IncrementalStats();
		IncrementalStats graphStats = new IncrementalStats();
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
		int noOfNodes = 0;
		boolean foundFriend = false;
		int hopsToReach = 0;
		for (int i = 0; i < Network.size(); i++) {

			SocialNetworkCalculations protocol = (SocialNetworkCalculations) Network.get(i).getProtocol(pid);

			stats.add(protocol.interest);
			if (protocol.User.size != 0) {
				circleSize.add(protocol.User.size);
				oneHopSize.add(protocol.User.userdata.oneHopFriends.size());
				noOfNodes++;
				avgConnectionSpeed.add(protocol.User.userdata.connectionSpeed);
				totalOfflineNodes += protocol.User.userdata.offlineUsers;
				totalNewfriends += protocol.User.userdata.newFriendsList.size();
				totalNewRandomFriends += protocol.User.userdata.newRandomFriends;
				totalDuplicateMessage += protocol.User.userdata.duplicatesMessage;
				totalMessages += protocol.User.userdata.totalMessages;
				totalRemovedOfflineContacts += protocol.User.userdata.removedOfflineContacts;
			}
			// freqHop.add(protocol.User.userdata.oneHopFriends.);
			if (protocol.User.flag == -2) {
				foundFriend = true;
				hopsToReach = protocol.User.userdata.hopCount;
			}
			// System.out.println("Size of
			// neighbor"+protocol.User.userdata.neighbors.size());
			nodeList.put(protocol.User.id, (HashMap) protocol.User.userdata.neighbors);
			newNodeList.addAll(protocol.User.userdata.newFriendsList);
		}

		Date now = new Date(System.currentTimeMillis());
		double avgFriendCircle = averageFriendCircle(circleSize, noOfNodes);
		double avgOneHopAway = averageOneHopAway(oneHopSize, noOfNodes);
		double avgConnSpeed = averageConnectionSpeed(avgConnectionSpeed, noOfNodes);
		double averageDuplicate = totalDuplicateMessage;
		/* Printing statistics */
		System.out.println(name + ": " + stats);
		System.out.println("Average circle size: " + avgFriendCircle + " Average one Hop Away Size: " + avgOneHopAway);
		System.out.println("Average Connection Speed in the system is " + avgConnSpeed
				+ " and Total number of failed Links " + totalOfflineNodes / 4);
		System.out.println("Total new friends " + totalNewfriends / 2);
		System.out.println("Total new random friends " + totalNewRandomFriends / 2);
		System.out.println("Average duplicate messages on nodes is: " + averageDuplicate
				+ ": Average Number of messages on a node: " + totalMessages);
		System.out.println("Total Offline contacts removed: " + totalRemovedOfflineContacts / 8);
		/* Printing a message when everyone received the message */
		SocialGraphDisplay display = new SocialGraphDisplay();
		/* displayGraphNumber*/
		if(cycles < displayGraphNumber && cycles <20)
			display.displayGraph(nodeList, newNodeList);
		if (stats.getMax() == stats.getMin()) {
			System.err.println("Everyone Received the message at " + now);

			// displayStatus =1;
		}
		if (foundFriend)
			System.err.println(
					"friend found status: " + foundFriend + " at->>> " + now + " After " + hopsToReach + " Peers");

		return false;
	}

}
