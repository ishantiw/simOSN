package it.osn.core;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.vector.SingleValue;

public class OSNInitializer implements Control{
	
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
    /**  Minimum value of the random */
    private final int min;
    //private final FriendCircle friendCircle;
    //private final UserData userdata;
    
	public OSNInitializer(String prefix) {
		super();
		max = Configuration.getInt(prefix + "." + PAR_MAX);
        min = Configuration.getInt(prefix + "." + PAR_MIN);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        //userdata = new UserData();
        //friendCircle = new FriendCircle(userdata);
	}

	public FriendCircle circleInitializer(){
		
		return null;
	}

	@Override
	public boolean execute() {
		boolean flag = false;
		for (int i = 0; i < Network.size(); i++) {
            SocialNetworkCalculations prot = (SocialNetworkCalculations) Network.get(i).getProtocol(pid);
            UserData data = new UserData();
            int val = (int) (Math.random() * (max - min));
        	val += min;//System.out.println("Valus are "+val);
            if(val%2==0)
            	data.hobbies.add("BasketBall");
            if(val%3==0)
            	data.hobbies.add("Soccer");
            FriendCircle circle = new FriendCircle(data);                
                prot.interest = val;
                if(i == 2){
                	circle.setSize(555);
                } else if(i == 38){
                	circle.setSize(555);
                } else {
                	circle.setSize(val);
                }
                prot.setUser(circle);
        }
        return false;
	}

}
