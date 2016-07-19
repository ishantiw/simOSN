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

/**
* This class is the frame for the dynamic partial view based epidemic
* membership protocol.
*/
public class OrderedDpvem extends Dpvem {


// =============== static fields =======================================
// =====================================================================

/**
* Working area for manipulation of views. It is initializaed to be
* twice as long as the view.
*/
protected static int[] valbuffer;

/**
* A mask to be able to speed up the ordering of the new views.
*/
protected static boolean[] bufferMask;

// ---------------------------------------------------------------------

/**
* config parameter name specifiying the order type (semantics of the
* values according to which we order. Defaults to {@link #ORDERTYPES}[0].
*/
public static final String PAR_ORDER = "order";

/**
* Known order type names. Any extension should leave the indeces of these
* strings unchanged.
*/
public static final String[] ORDERTYPES = { "birth", "hops" };

/** index of "birth" in {@link #ORDERTYPES}. */
protected static final int ORDER_BIRTH = 0;

/** index of "hops" in {@link #ORDERTYPES}. */
protected static final int ORDER_HOPS = 1;

/**
* Defines how to select the peer from the view for communication.
*/
public static final String PAR_PEERSELECT = "peerselect";

/**
* Defines how to create the new view from the merged two views. 
*/
public static final String PAR_VIEWSELECT = "viewselect";

/** type of ordering in effect */
protected static int order;


// =================== fields ==========================================
// =====================================================================


/**
* Holds the values accroding to which the view is ordered.
* This array is always ordered in increasing order. The meaning of a value
* is assumed to be age-related. The larger the value, the older it is.
* values[i] is an attribute of view[i]. That is,
* an additional restriction is introduced for view, ie it has to be ordered
* according to this array.
*/
protected int[] values = null;


// ====================== initialization ===============================
// =====================================================================


public OrderedDpvem(String n) {
	
	super(n);
	
	String tmp = Configuration.getString(n+"."+PAR_ORDER,ORDERTYPES[0]);
	int i=0;
	while( i < ORDERTYPES.length && !tmp.equals(ORDERTYPES[i]) ) i++;
	if( i < ORDERTYPES.length ) order = i;
	else
	{
		throw new IllegalArgumentException(
		   n+"."+PAR_ORDER+": order type '"+tmp+"' is unknown");
	}
	
	values = new int[Dpvem.c];
	OrderedDpvem.valbuffer = new int[Dpvem.c*2+2];
	OrderedDpvem.bufferMask = new boolean[Dpvem.c*2+2];
	
	peerIterator = OrderedDpvem.createIterator(
		Configuration.getString(n+"."+PAR_PEERSELECT) );
	bufferIterator = OrderedDpvem.createIterator(
		Configuration.getString(n+"."+PAR_VIEWSELECT) );
}

// ---------------------------------------------------------------------

public Object clone() throws CloneNotSupportedException {

	OrderedDpvem sn = (OrderedDpvem)super.clone();
	sn.values = new int[values.length];
	System.arraycopy(values,0,sn.values,0,viewlen);
	return sn;
}


// ====================== helper and protocol methods =================
// ====================================================================


/** Factory method to create iterators based on a name string.
* The names currently known are the following
* <ul>
* <li>"rand": iterates in random order</li>
* <li>"head": sequantial order from head to tail</li>
* <li>"tail": sequantial order from tail to head</li>
* </ul>
* @return A new iterator, or null if it could not be created because of
* an incorrect name.
*/
public static IndexIterator createIterator(String name) {

	if( name == null ) return null;
	if(name.equals("rand")) return new RandPermutation();
	if(name.equals("head")) return new LinearIterator();
	if(name.equals("tail")) return new LinearIterator(true);
	return null;
}

// ----------------------------------------------------------------------

/**
* Merges the two views into a buffer, removing duplicate elements and
* maintaining the ordering. 
*/
protected void merge( Node thisNode, Node peerNode, int protocolID ) {
	
	OrderedDpvem peer = (OrderedDpvem)peerNode.getProtocol(protocolID);

	// --- copy views to buffer
	int i1=0, i2=0;
	bufferlen=0;
	while( i1<viewlen || i2<peer.viewlen )
	{
		boolean fromPeer = false;
		
		// selecting next element
		if( i1==viewlen ) fromPeer = true;
		else if( i2!=peer.viewlen )
		{
			if( values[i1] > peer.values[i2] ) fromPeer=true;
			else if( values[i1] == peer.values[i2] )
				fromPeer = CommonRandom.r.nextBoolean();
		}
		
		// appending next element
		if( fromPeer )
		{
			buffer[bufferlen]=peer.view[i2];
			valbuffer[bufferlen]=peer.values[i2];
			i2++;
		}
		else
		{
			buffer[bufferlen]=view[i1];
			valbuffer[bufferlen]=values[i1];
			i1++;
		}
		
		// verifying appended element
		if(thisNode==buffer[bufferlen] || peerNode==buffer[bufferlen])
			continue;
		int j=bufferlen-1;
		for(; j>=0; --j) if( buffer[j]==buffer[bufferlen] ) break;
		if( j < 0 ) bufferlen++;
	}
	
	// --- update old values
	updateValues(bufferlen);
	
	// --- inserting thisNode and peerNode with fresh values
	OrderedDpvem.insert(
		thisNode, getValue(thisNode), buffer, valbuffer, bufferlen );
	OrderedDpvem.insert(
		peerNode, getValue(peerNode), buffer, valbuffer, bufferlen+1 );
	bufferlen+=2;
}

// ------------------------------------------------------------------------

/**
* The same as in {@link Dpvem} only it updates values too.
*/
protected void setViews( Node thisNode, Node peerNode, int protocolID ) {
	
	OrderedDpvem peer = (OrderedDpvem)peerNode.getProtocol(protocolID);	

	// bufferMask is all false here

	// --- create new views
	if( push )
	{
		bufferIterator.reset(bufferlen);
		int len=0;
		while(bufferIterator.hasNext() && len<peer.view.length)
		{
			int pos = bufferIterator.next();
			if( buffer[pos] != peerNode )
			{
				bufferMask[pos]=true;
				len++;
			}
		}
		peer.viewlen=0;
		for(int i=0; i<bufferMask.length; ++i)
		{
			if(bufferMask[i])
			{
				peer.view[peer.viewlen] = buffer[i];
				peer.values[peer.viewlen] = valbuffer[i];
				peer.viewlen++;
				bufferMask[i] = false;
			}
		}
	}
	if( pull )
	{
		bufferIterator.reset(bufferlen);
		int len=0;
		while( bufferIterator.hasNext() && len<view.length )
		{
			int pos = bufferIterator.next();
			if( buffer[pos] != thisNode )
			{
				bufferMask[pos]=true;
				len++;
			}
		}
		viewlen=0;
		for(int i=0; i<bufferMask.length; ++i)
		{
			if(bufferMask[i])
			{
				view[viewlen] = buffer[i];
				values[viewlen] = valbuffer[i];
				viewlen++;
				bufferMask[i] = false;
			}
		}
	}
}

// --------------------------------------------------------------------

/** After merging the two views, but before inserting the freshest entries
* about the participating two peers, updates the values of the nodes. */
protected void updateValues(int bufferlen) {
	
	switch(order)
	{
		case ORDER_HOPS:
			// increment hop count. Hop count is value mod 10
			// so this increments it by one
			for(int i=0; i<bufferlen; ++i) valbuffer[i]+=10;
			break;
		case ORDER_BIRTH:
		default:
			// no update necessary
	}
}

// --------------------------------------------------------------------

/** When a new node is inserted, it returns its initial value */
protected int getValue(Node n) {

	switch(order)
	{
		case ORDER_HOPS:
			// hop count is value mod 10, thus this value is zero.
			// a trick to randomize order among nodes with same
			// hop count
			return CommonRandom.r.nextInt(10);
		case ORDER_BIRTH:
		default:
			// this is a trick to randomize the order among
			// the elements with the same value. Negative to get
			// ordering right
			return 
			-(10*CommonState.getCycle()+CommonRandom.r.nextInt(10));
	}
}

// --------------------------------------------------------------------

protected static void insert( Node n, int val, Node[] nb, int[] vb, int len) {
	
	int pos = len;
	while( pos > 0 && vb[pos-1] > val )
	{
		vb[pos]=vb[pos-1];
		nb[pos]=nb[pos-1];
		pos--;
	}
	vb[pos]=val;
	nb[pos]=n;
}

// ====================== Linkable implementation =====================
// ====================================================================
 

/**
* Adds the element if it is not contained in the view already and
* throws an IndexOutOfBoundsException is the view is full.
*/
public boolean addNeighbor(Node node) {

	if( contains(node) ) return false;
	
	if( viewlen >= Dpvem.c )
	{
		throw new IndexOutOfBoundsException();
	}
	
	// --- insterting new node into the ordered view array
	OrderedDpvem.insert( node, getValue(node), view, values, viewlen );
	viewlen++;

	return true;
}


// ===================== other public methods =========================
// ====================================================================


/** Returns the value associated with given neighbor */
public int getValue( int i ) {
	
	if(i<0 || i>=viewlen) throw new IndexOutOfBoundsException();

	return values[i];
}

// --------------------------------------------------------------------

public String toString() {

	StringBuffer sb = new StringBuffer();
	sb.append(viewlen).append(" [");
	for(int i=0; i<viewlen; ++i)
	{
		sb.append("(").append(view[i].getIndex()).append(",").append(
			values[i]).append(") ");
	}
	return sb.append("]").toString();
}

}




