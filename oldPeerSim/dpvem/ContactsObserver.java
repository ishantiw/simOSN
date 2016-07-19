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

import peersim.reports.*;
import peersim.core.*;
import peersim.config.Configuration;
import peersim.util.IncrementalStats;

/**
*/
public class ContactsObserver {


// ===================== fields =======================================
// ====================================================================

/** 
*  String name of the parameter used to select the protocol to operate on
*/
public static final String PAR_PROT = "protocol";

private final int protocolID;

/** The name of this observer in the configuration */
private final String name;


// ===================== initialization ================================
// =====================================================================


public ContactsObserver(String name) {

	this.name = name;
	protocolID = Configuration.getPid(name+"."+PAR_PROT);
}


// ====================== methods ======================================
// =====================================================================


public boolean analyze() {
	
	IncrementalStats contacts = new IncrementalStats();
	for(int i=0; i<Network.size(); ++i)
	{
		Dpvem r = (Dpvem)Network.get(i).getProtocol(protocolID);
		contacts.add(r.contacts);
	}
	return false;
}

}


