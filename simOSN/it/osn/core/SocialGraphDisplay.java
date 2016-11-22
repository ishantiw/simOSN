package it.osn.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 * <h1>OSN Social Graph Display!</h1> This class integrates the PeerSim and
 * graph-stream library. It takes the list of nodes and edges connected from the
 * Social Gossip's {@link UserProfileDissemination} class. It checks which are
 * the new edges formed and give them green color so that it can be easily
 * identified *
 * <p>
 * 
 * @author Ishan Tiwari
 * @version 1.0
 * @since 04.08.2016
 * @modified 21.11.2016
 */

public class SocialGraphDisplay {

	public void displayGraph(HashMap<Integer, HashMap> nodeList, List<Integer> newNodeList) {

		// Configuration Parameters***********************
		String graphName = "SocialGossip";
		// ***********************************************

		/* creating graph */
		Graph graph = new SingleGraph(graphName);
		graph.addAttribute("ui.stylesheet", " node:clicked {fill-color: red;}");

		/* Generating Nodes */
		for (Iterator<Entry<Integer, HashMap>> iterator = nodeList.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, HashMap> entryPeer = iterator.next();
			String nodeName = "" + entryPeer.getKey() + "";
			if (graph.getNode(nodeName) == null)
				graph.addNode(nodeName);
			for (Iterator<Entry<Integer, Integer>> iterator2 = entryPeer.getValue().entrySet().iterator(); iterator2
					.hasNext();) {
				Entry<Integer, Integer> entryPeer2 = iterator2.next();
				String tempName = "" + entryPeer2.getKey() + "";
				if (graph.getNode(tempName) == null)
					graph.addNode(tempName).addAttribute("ui.label", tempName);
				if (graph.getEdge(nodeName + tempName) == null && graph.getEdge(tempName + nodeName) == null) {
					graph.addEdge(nodeName + tempName, nodeName, tempName);
					if (newNodeList.contains(entryPeer2.getKey())) {
						graph.getEdge(nodeName + tempName).addAttribute("ui.style", "fill-color: green;");
					}
				}
			}
		}

		graph.display();
	}
}
