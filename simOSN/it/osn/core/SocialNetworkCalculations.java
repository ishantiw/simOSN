package it.osn.core;

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
	
	/** Quota amount. Obtained from config property {@link #PAR_INTEREST}. */
	private final double interest_value;
	
	protected double interest;
	
	//public void initfriendSize(int val){
		//	this.friendSize = val;	
	//}
	public SocialNetworkCalculations(String prefix) {
		super(prefix);
		//get interest value from the config value
		interest_value = (Configuration.getInt(prefix + "." + PAR_INTEREST, 1));
		//get interest value from the config value
		exp = Configuration.getString(prefix + "." + param_experiment);
		UserData userdata = new UserData();
		User =  new FriendCircle(userdata);
		interest = interest_value;
		if(exp.equals("find")){//to identify different functions
			System.out.println("Inside find");
		}
		
	}
	
	@Override
	public void nextCycle(Node node, int protocolID) {
		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);
		SocialNetworkCalculations neighbor = null;
		boolean found = false;
		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);
			// The selected peer could be inactive
			if (!peer.isUp())
			continue;
			SocialNetworkCalculations user = (SocialNetworkCalculations)peer.getProtocol(protocolID);
				neighbor = user;
				System.out.println("User details is "+user.interest);
				//System.out.println("####################found contact");
			if(neighbor ==null)
				return;
		}
		foundNeighbor(neighbor);
	}
	
	protected void foundNeighbor(SocialNetworkCalculations neighbor){
		//In this function we can perform other tasks like exchanging the value or updating etc.
		//System.out.println("Inside found Neighbor");
		System.out.println("Size is "+ this.interest+" and peer size is "+neighbor.interest);
		boolean found = this.User.userdata.hobbies.contains(neighbor.User.userdata.hobbies);
		if(found){
			System.out.println("Found Friend");
		}
	}
	
	
	//match the friend
	protected void setInterestVal(double interest){
		this.interest = interest_value;	
	}
}
