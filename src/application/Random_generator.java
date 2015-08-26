package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Random_generator {
	
	public static Map<String, List<String>> rand(int NodeCount){
		
		// Compute average degree of each node as half of node count rounded down
		int degree = (int) Math.floor(NodeCount/2);
		
		// Set up output hashmap
		Map<String, List<String>> framework = new HashMap<>();
		
		List<String> nodes = new ArrayList<String>();		
		
		String[] charList = {
				"A", "B", "C", "D", "E", "F", "G", 
				"H", "I", "J", "K", "L", "M", "N", 
				"O", "P", "Q", "R", "S", "Y", "U", 
				"V", "W", "X", "Y", "Z"};
		
		// Add n=nodeCount node labels
		for (int i=0; i<NodeCount; i++){
			nodes.add(charList[i]);
		}
		
		// Decay factor lambda of individual degree depending on node priority
		double lambda = (double) degree/(nodes.size()-1);
		
		// Compute attackers for each node
		for (int k=0; k<nodes.size(); k++) {
			// Compute individual degree based on decay factor
			int new_degree = (int) Math.round(degree - (lambda*k));
			
			// List of viable attackers
			List<String> temp = new ArrayList<String>();
			for (int l =k+1; l<nodes.size(); l++) {
				temp.add(nodes.get(l));
			}
			
			if (new_degree == 0) framework.put(nodes.get(k), Arrays.asList());
			else {
				List<String> attackers = new ArrayList<String>();
				// Randomise attackers and add to framework with node label
				for (int j=0; j<new_degree; j++){
					Collections.shuffle(temp);
					attackers.add(temp.get(0));
					temp.remove(0);
				}
				framework.put(nodes.get(k), attackers);
			}
		}
				
		System.out.println(framework);
		
		return framework;
		
	}

}
