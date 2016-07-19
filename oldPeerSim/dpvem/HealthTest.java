/*
 * Copyright (c) 2003 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
		
package dpvem;

import peersim.core.Node;
import peersim.core.Network;
import peersim.config.Configuration;
import peersim.reports.Observer;

/**
 */
public class HealthTest implements Observer {


// ===================== fields =======================================
// ====================================================================

/** 
*  String name of the parameter used to select the protocol to operate on
*/
public static final String PAR_PROT = "protocol";
  
/** The name of this observer in the configuration */
private final String name;

private final int protocolID;


// ===================== initialization ================================
// =====================================================================


public HealthTest(String name) {

	this.name = name;
	protocolID = Configuration.getPid(name+"."+PAR_PROT);
}


// ====================== methods ======================================
// =====================================================================

/** Method to check the consistency of the state of the network */
public static String test(int protocolID) {
	
	int failOutLinks=0; // out link points to failed node
	int corruptOrder=0; // outViewDate is wrong size or not ordered
	
	for(int i=0; i<Network.size(); ++i)
	{
		Node curr = Network.get(i);
		OrderedDpvem currsc = (OrderedDpvem)
			(curr.getProtocol(protocolID));
		
		// check out view
		for(int j=0; j<currsc.viewlen; ++j)
		{
			Node out = (Node)currsc.view[j];
			if(!out.isUp())
			{
				++failOutLinks;
			}
		}
		
		// check order
		for(int j=1; j<currsc.viewlen; ++j)
		{
			if(currsc.values[j]<currsc.values[j-1])
			{
				++corruptOrder;
				break;
			}
		}
	}
	
	return ("failOutLinks: "+failOutLinks+
		" corruptOrder: "+corruptOrder);
}

// --------------------------------------------------------------------

public boolean analyze() {
	
	System.out.println(name+": "+HealthTest.test(protocolID));
	
	return false;
}

}

