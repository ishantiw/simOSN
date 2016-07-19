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

import peersim.util.*;
import peersim.core.*;
import peersim.config.*;
import peersim.cdsim.*;

/**
* This class is the frame for the dynamic partial view based epidemic
* membership protocol.
*/
public class Dpvem implements CDProtocol, Linkable {


// =============== static fields =======================================
// =====================================================================


/**
* Working area for manipulation of views. It is initializaed to be
* twice as long as the view
*/
protected static Node[] buffer;

/** the length of the used part of the buffer */
protected static int bufferlen;


// ---------------------------------------------------------------------

/** config parameter name for the max view size. */
public static final String PAR_C = "c";

/** config parameter name for F. Defaults to 1. */
public static final String PAR_F = "F";

/** config parameter name for the push behaviour. Not set by default. */
public static final String PAR_PUSH = "push";

/** config parameter name for the pull behaviour. Not set by default. */
public static final String PAR_PULL = "pull";

/** config parameter name for the balance behaviour.
* If set then the balancing extension (which ensures that in each cycle
* there is approx one contact) will not be switched on. 
* Not set by default.
*/
public static final String PAR_BALANCE = "balance";

protected static int c;
 
protected static int F;
 
protected static boolean push;
 
protected static boolean pull;

protected static boolean balance;

/**
* The iterator that is used to select peers to communicate with.
*/
protected static IndexIterator peerIterator;

/**
* The iterator that defines the creation of new views from the merged view.
*/
protected static IndexIterator bufferIterator;


// =================== fields ==========================================
// =====================================================================


/**
* The array holding the nodes known by this. The contract of this array
* is that it does not contain this node, ie there are no loop edges.
* Extending classes can introduce more restrictions.
*/
protected Node[] view = null;

protected int viewlen = 0;

/** counts how many times the node was contacted */
protected int contacts = 0;


// ====================== initialization ===============================
// =====================================================================


public Dpvem(String n) {
	
	Dpvem.c = Configuration.getInt(n+"."+PAR_C);
	Dpvem.buffer = new Node[Dpvem.c*2+2];
	Dpvem.F = Configuration.getInt(n+"."+PAR_F, 1);
	Dpvem.balance = Configuration.contains(n+"."+PAR_BALANCE);
	Dpvem.push = Configuration.contains(n+"."+PAR_PUSH);
	Dpvem.pull = Configuration.contains(n+"."+PAR_PULL);
	if( !push && !pull )
	{
		throw new IllegalArgumentException(
			"push and pull are both undefined");
	}
	view = new Node[Dpvem.c];
	Dpvem.peerIterator = new RandPermutation();
	Dpvem.bufferIterator = new RandPermutation();
}

// ---------------------------------------------------------------------

public Object clone() throws CloneNotSupportedException {

	Dpvem sn = (Dpvem)super.clone();
	sn.view = new Node[view.length];
	System.arraycopy(view,0,sn.view,0,viewlen);
	return sn;
}


// ====================== helper and protocol methods =================
// ====================================================================


/**
* Merges the views of thisNode and peerNode into the {@link #buffer}.
* It is guaranteed that all nodes are present only at most once in the buffer
* and that thisNode and peerNode are also present.
* Overriding implementations can add additional constraints but are not
* allowed to remove these constraints.
*/
protected void merge( Node thisNode, Node peerNode, int protocolID ) {
	
	Dpvem peer = (Dpvem)peerNode.getProtocol(protocolID);
	
	buffer[0] = thisNode; // peerNode is included in view
	System.arraycopy(view,0,buffer,1,viewlen);
	bufferlen = viewlen+1;
	// at this point there are no duplicates in the buffer yet
	for(int i=0; i<peer.viewlen; ++i)
	{
		// we have to look at the view of thisNode and thisNode only
		int j=viewlen+1;
		for(; j>=0; --j) if( buffer[j]==peer.view[i] ) break;
		if( j < 0 )
		{
			buffer[bufferlen]=peer.view[i];
			bufferlen++;
		}
	}
}

// --------------------------------------------------------------------

protected void setViews( Node thisNode, Node peerNode, int protocolID ) {
	
	Dpvem peer = (Dpvem)peerNode.getProtocol(protocolID);	

	// --- create new views
	if( push )
	{
		resetBufferIterator(peerNode);
		peer.viewlen=0;
		while(bufferIterator.hasNext() && peer.viewlen<peer.view.length)
		{
			int pos = bufferIterator.next();
			if( buffer[pos] != peerNode )
			{
				peer.view[peer.viewlen] = buffer[pos];
				peer.viewlen++;
			}
		}
	}
	if( pull )
	{
		resetBufferIterator(thisNode);
		viewlen=0;
		while( bufferIterator.hasNext() && viewlen<view.length )
		{
			int pos = bufferIterator.next();
			if( buffer[pos] != thisNode )
			{
				view[viewlen] = buffer[pos];
				viewlen++;
			}
		}
	}
}

// --------------------------------------------------------------------

/**
* Initializes the buffer iterator for use to create the new view of
* given node. The default implementation is simply
*	<code>bufferIterator.reset(bufferlen);</code>
* independently of the target node.
*/
protected void resetBufferIterator( Node node ) {

	bufferIterator.reset(bufferlen);
}

// --------------------------------------------------------------------

/**
* Returns true if accepts incoming connection.
*/
protected boolean acceptConnect() {

	if( !balance || contacts <= CommonState.getTime() )
	{
		contacts++;
		return true;
	}
	else
		return false;

}

// ====================== Linkable implementation =====================
// ====================================================================
 

public Node getNeighbor(int i) { return view[i]; }

// --------------------------------------------------------------------

public int degree() { return viewlen; }

// --------------------------------------------------------------------

/**
* Adds the element if it is not contained in the view already and
* throws an IndexOutOfBoundsException if the view is full.
*/
public boolean addNeighbor(Node node) {

	if( contains(node) ) return false;
	
	if( viewlen >= Dpvem.c )
	{
		throw new IndexOutOfBoundsException();
	}
	
	view[viewlen++]=node;
	return true;
}

// --------------------------------------------------------------------

public void pack() {}

// --------------------------------------------------------------------

public boolean contains(Node node) {

	for(int i=0; i<viewlen; ++i)
		if( view[i]==node) return true;

	return false;	
}


// ===================== CDProtocol implementations ===================
// ====================================================================


public void nextCycle( Node thisNode, int protocolID ) {

	peerIterator.reset(viewlen);
	int i=0;
	while( i<Dpvem.F && peerIterator.hasNext() &&
		(!balance || contacts <= CommonState.getTime()) )
	{
		int peer = peerIterator.next();
		if( !view[peer].isUp() || 
			!((Dpvem)view[peer].getProtocol(protocolID)
				).acceptConnect()) continue;
		if( thisNode == view[peer] )
		{
			System.err.println(
			"dpvem: something is not ok, loop edge detected");
			continue;
		}
		contacts++;
		merge( thisNode, view[peer], protocolID );
		setViews( thisNode, view[peer], protocolID );
		i++;
	}
	
	if( i < Dpvem.F )
	{
		System.err.println("Dpvem: only "+i+" accessible peers");
	}
}


// ===================== other public methods =========================
// ====================================================================


public static int getC() { return c; }

// --------------------------------------------------------------------

public String toString() {

	StringBuffer sb = new StringBuffer();
	sb.append(viewlen).append(" [");
	for(int i=0; i<viewlen; ++i)
	{
		sb.append(view[i].getIndex()).append(" ");
	}
	return sb.append("]").toString();
}


}




