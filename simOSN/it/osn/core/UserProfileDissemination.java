package it.osn.core;


import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

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
	public boolean execute() {
		IncrementalStats stats = new IncrementalStats();
		for (int i = 0; i < Network.size(); i++) {
			SocialNetworkCalculations protocol = (SocialNetworkCalculations) Network.get(i).getProtocol(
					pid);
			stats.add(protocol.interest);
		}
		/* Printing statistics */
		System.out.println(name + ": " + stats);
		return false;
	}
}
