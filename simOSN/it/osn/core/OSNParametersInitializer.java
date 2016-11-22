package it.osn.core;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;

/**
 * <h1>OSN Parameters Initializer!</h1> This class initialize the graph with
 * assigning values to the nodes between {@value}min and {@value}max. Also, it
 * assigns a peak value more than {@value}min and {@value}max values to a random
 * node. It assigns flags to all the nodes to be 1 and assign a random node
 * (other than the node having the peak value) a value -1.
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
public class OSNParametersInitializer implements Control {

	/** String to retrieve the name of the protocol by the conf file */
	private static final String PAR_PROT = "protocol";
	/** String to recover the maximum value from the conf file */
	private static final String PAR_MAX = "max";
	/** String to retrieve the minimum value from conf file */
	private static final String PAR_MIN = "min";

	/** Pid protocol */
	private final int pid;
	/** Maximum random value */
	private final int max;
	/** Minimum value of the random */
	private final int min;

	private static String[] hobbies = { "Basketball", "Tennis", "Movies", "Gaming", "Cricket", "Chess", "Soccer",
			"Golf", "Travelling", "Polo", "Music", "Football", "Meeting" };

	public OSNParametersInitializer(String prefix) {
		super();
		max = Configuration.getInt(prefix + "." + PAR_MAX);
		min = Configuration.getInt(prefix + "." + PAR_MIN);
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		// userdata = new UserData();
		// friendCircle = new FriendCircle(userdata);
	}

	public void circleInitializer(Linkable linkable, SocialNetworkCalculations owner) {
		owner.User.size = linkable.degree();
		// System.out.println("Friend circle size"+owner.User.size);
		for (int k = 0; k < linkable.degree(); k++) {
			if (linkable.getNeighbor(k).isUp()) {
				long neighborID = linkable.getNeighbor(k).getID();
				owner.User.userdata.neighbors.put((int) neighborID, 0);
			}
		}

	}

	@Override
	public boolean execute() {
		boolean flag = false;
		/* Choosing two random nodes to find each other*/
		Double firstNode = Math.random() * Network.size();
		Integer firstNodeID = firstNode.intValue();
		Double secondNode = Math.random() * Network.size();
		Integer secondNodeID = secondNode.intValue();
		for (int i = 0; i < Network.size(); i++) {
			SocialNetworkCalculations prot = (SocialNetworkCalculations) Network.get(i).getProtocol(pid);
			int linkableID = FastConfig.getLinkable(pid);
			Linkable linkable = (Linkable) Network.get(i).getProtocol(linkableID);

			int peak_interest = prot.interest_value;
			// Initializing User Data class
			UserData data = new UserData();

			// Calculating random values which will be assigned to all the
			// nodes. note: "interest" value
			int val = (int) (Math.random() * (max - min));
			val += min;

			// Assigning 7 hobbies randomly to all the nodes
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

			prot.setUser(circle);
			if (prot != null)
				prot.User.id = i;
			circleInitializer(linkable, prot);
			// Assigning a node with a peak value and another node with a flag
			// different than others to identify them specifically

			if (i == firstNodeID) {
				prot.interest = peak_interest;
				prot.User.flag = 1;
				prot.User.userdata.connectionSpeed = val * 10 * (Math.random() * (max - min));
			} else if (i == secondNodeID) {
				prot.interest = val;
				prot.User.flag = -1;
				prot.User.userdata.connectionSpeed = val * 10 * (Math.random() * (max - min));
			} else {
				prot.interest = val;
				prot.User.flag = 1;
				prot.User.userdata.connectionSpeed = val * 10 * (Math.random() * (max - min));
			}
		}
		return false;
	}

}
