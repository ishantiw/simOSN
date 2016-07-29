package it.osn.core;

import peersim.cdsim.CDProtocol;
import peersim.core.Node;

public class SocialNetworkCalculations implements CDProtocol{
	
	/** String to get the initial value */
	protected static final String param_value = "value";
	/** String to get the aggregate function to use */
	protected static final String param_experiment = "find";
	
	
	protected FriendCircle User;
	
	protected int friendSize;
	
	
	public void initfriendSize(int val){
			this.friendSize = val;	
	}
	public SocialNetworkCalculations(String prefix) {
		
		
	}
	
	@Override
	public void nextCycle(Node node, int protocolID) {
		
	}
	@Override
	public Object clone(){

		return null;
	}
}
