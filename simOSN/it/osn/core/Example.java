package it.osn.core;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;



public class Example {

	
	public void displayGraph(HashMap<Integer, HashMap> nodeList) {
		
		//Configuration Parameters***********************
		//int min_node = 20;
		///int max_node = 60;
		String graphName = "reThink";
		//***********************************************
		
		//RandomGen randomNumGen = new RandomGenImpl();
		//GraphManipulator graphMan = new GraphManipulatorImpl();
		/* creating graph */
		Graph graph = new SingleGraph(graphName);
		//graph.addAttribute("ui.stylesheet", " node:clicked {fill-color: red;}");
		
		/* Generating Nodes */
		for(Iterator<Entry<Integer, HashMap>> iterator = nodeList.entrySet().iterator(); iterator.hasNext(); ) {
			Entry<Integer, HashMap> entryPeer = iterator.next();
			String nodeName = ""+entryPeer.getKey()+"";
			if(graph.getNode(nodeName) == null)
			graph.addNode(nodeName);
			for(Iterator<Entry<Integer, Integer>> iterator2 = entryPeer.getValue().entrySet().iterator(); iterator2.hasNext(); ) {
				Entry<Integer, Integer> entryPeer2 = iterator2.next();
				String tempName = ""+entryPeer2.getKey()+"";
				if(graph.getNode(tempName) == null)
				graph.addNode(tempName).addAttribute("ui.label", tempName);
				if(graph.getEdge(nodeName+tempName)== null && graph.getEdge(tempName+nodeName)==null)
				graph.addEdge(nodeName+tempName, nodeName, tempName);
			}
		}
		
		graph.display();
	}
}
