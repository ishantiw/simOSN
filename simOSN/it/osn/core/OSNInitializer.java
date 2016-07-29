package it.osn.core;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

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
    
    
	public OSNInitializer(String prefix) {
		super();
		max = Configuration.getInt(prefix + "." + PAR_MAX);
        min = Configuration.getInt(prefix + "." + PAR_MIN);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}


	@Override
	public boolean execute() {
		boolean flag = false;
		for (int i = 0; i < Network.size(); i++) {
            SocialNetworkCalculations prot = (SocialNetworkCalculations) Network.get(i).getProtocol(pid);
            int val = (int) (Math.random() * (max - min));
            if(!flag){
            	flag =true;
            }else{
            	val += min;
            }
            prot.initfriendSize(val);
        }
        return false;
	}

}
