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
import peersim.util.IncrementalStats;

/**
 */
public class ValueDistribution implements Observer {


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


public ValueDistribution(String name) {

	this.name = name;
	protocolID = Configuration.getPid(name+"."+PAR_PROT);
}


// ====================== methods ======================================
// =====================================================================

public boolean analyze() {
	
	IncrementalStats stats[] = new IncrementalStats[Dpvem.c];
	for(int i=0; i<stats.length; ++i) stats[i] = new IncrementalStats();
	
	for(int i=0; i<Network.size(); ++i)
	{
		Node n=Network.get(i);
		OrderedDpvem od = (OrderedDpvem)n.getProtocol(protocolID);
		for(int j=0; j<od.degree(); ++j)
			stats[j].add(od.getValue(j));
		
	}
	
	System.out.print(name+": ");
	for(int i=0; i<stats.length; ++i)
		System.out.print( stats[i].getAverage()+" ");
	System.out.println();

	return false;
}

}

