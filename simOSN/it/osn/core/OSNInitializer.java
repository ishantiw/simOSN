package it.osn.core;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * <h1>OSN Initializer!</h1> This class initialize the graph with assigning
 * values to the nodes between {@value}min and {@value}max. Also, it assigns a
 * peak value more than {@value}min and {@value}max values to a random node. It
 * assigns flags to all the nodes to be 1 and assign a random node (other than
 * the node having the peak value) a value -1.
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */
public class OSNInitializer implements Control {

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
	// private final FriendCircle friendCircle;
	// private final UserData userdata;

	public OSNInitializer(String prefix) {
		super();
		max = Configuration.getInt(prefix + "." + PAR_MAX);
		min = Configuration.getInt(prefix + "." + PAR_MIN);
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		// userdata = new UserData();
		// friendCircle = new FriendCircle(userdata);
	}

	public FriendCircle circleInitializer() {

		return null;
	}

	@Override
	public boolean execute() {

		for (int i = 0; i < Network.size(); i++) {
			SocialNetworkCalculations prot = (SocialNetworkCalculations) Network.get(i).getProtocol(pid);
			// Linkable linkable = (Linkable)Network.get(i).getProtocol(pid);
			UserData data = new UserData();
			int val = (int) (Math.random() * (max - min));
			val += min;// System.out.println("Valus are "+val);
			if (val % 2 == 0)
				data.hobbies.add("BasketBall");
			if (val % 3 == 0)
				data.hobbies.add("Soccer");
			FriendCircle circle = new FriendCircle(data);
			prot.interest = val;
			prot.User = circle;
			if (i == 2) {
				prot.interest = 555;
				prot.User.flag = 1;
			} else if (i == 3800) {
				prot.interest = val;
				prot.User.flag = -1;
			} else {
				prot.interest = val;
				prot.User.flag = 1;
			}
		}
		return false;
	}

}
