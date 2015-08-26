package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



@SuppressWarnings("unused")
public class Dispute_generator {
	
	// efficiency flag for game play screen
	static int flag_efficiency;
	
	public static void main(Stage stage, Map<String, List<String>> framework, boolean gameMode, boolean efficiency, boolean cheat){
		
		// Text panel which will display evaluations of both dispute trees
		TextArea detail = new TextArea();
		detail.setStyle("-fx-text-inner-color: #445F7A;");
		detail.setEditable(false);
		
		if (efficiency == true) flag_efficiency = 0;
		else flag_efficiency = 2;
		
		// ********************PREFERRED********************
		
		System.out.println("Framework: " + framework);
		detail.appendText("Framework: " + framework);
		
		// Start a new dispute at root node
		Node<String> root = new Node<String>("A");
		
		//Create a tree, providing the root node
		Tree<String> ptree = new Tree<String>(root);
		
		// Check for efficiency flag
		if (efficiency==true) createPreferredDispute(root, root, ptree, framework);
		else NoEfficiencyPreferred(root, root, ptree, framework);
		
		// Retrieve all paths from root to leaves i.e. all the disputes
		List<ArrayList<Node<String>>> pdisputes = ptree.getPathsFromRootToAnyLeaf();
		System.out.println("Preferred disputes: " + pdisputes);
		detail.appendText("\n\n\nPreferred disputes: " + pdisputes);
		
		// Output winning strategies for proponent under preferred semantics, detecting conflicts
		List<ArrayList<Node<String>>> wpdisputes = new LinkedList<ArrayList<Node<String>>>();
		for (ArrayList<Node<String>> d : pdisputes) {			
			if (d.size() % 2 == 1) {
				wpdisputes.add(d);
			}
		}
		
		// Check each individual proponent dispute if it's a winning strategy on it's own
		List<Boolean> results = new ArrayList<Boolean>();
		for (ArrayList<Node<String>> d : wpdisputes) {
			results.add(checkWinningSingle(d, framework));
		}
		
		// Failed single winning strategies
		List<ArrayList<Node<String>>> fwpd = new LinkedList<ArrayList<Node<String>>>();
		// Single winning strategies
		List<ArrayList<Node<String>>> wpd = new LinkedList<ArrayList<Node<String>>>();
		for (ArrayList<Node<String>> d : wpdisputes) {
			if (results.get(wpdisputes.indexOf(d))==false) {
				fwpd.add(d);
			}
			else wpd.add(d);
		}
		
		List<Set<ArrayList<Node<String>>>> cwpd = checkWinningCombined(fwpd, framework);
		
		// Output results
		if (wpd.isEmpty() && cwpd.get(0).isEmpty()) {
			System.out.println("No winning strategies, opponent always wins");
			detail.appendText("\n\nNo winning strategies, opponent always wins");
		}
		if (cwpd.get(0).isEmpty()==false){
			for (Set<ArrayList<Node<String>>> d: cwpd) {
				boolean conflict = detectConflict(d, framework);
				if (conflict == false){
					System.out.println("\nConflict-free combined winning strategy under preferred semantics: " + d);
					detail.appendText("\n\nConflict-free combined winning strategy under preferred semantics: " + d);
				}
				else {
					System.out.println("\n" + d + " is not conflict-free");
					detail.appendText("\n\n" + d + " is not conflict-free");
				}
			}
		}
		if (wpd.isEmpty()==false){
			for (ArrayList<Node<String>> d : wpd) {
				boolean conflict = detectSingleConflict(d, framework);
				if (conflict == false){
					System.out.println("\nConflict-free single winning strategy under preferred semantics: " + d);
					detail.appendText("\n\nConflict-free single winning strategy under preferred semantics: " + d);
				}
				else {
					System.out.println("\n" + d + " is not conflict-free");
					detail.appendText("\n\n" + d + " is not conflict-free");
				}
			}
		}
		
		// ********************GROUNDED********************
		
		//Hashmap to store argument framework
		Map<String, List<String>> framework2 = framework;
		
		// Start a new dispute at root node
		Node<String> root2 = new Node<String>("A");
		
		//Create a tree, providing the root node
		Tree<String> gtree = new Tree<String>(root2);
		
		// Check for efficiency flag
		if (efficiency==true) createGroundedDispute(root2, root2, gtree, framework2);
		else NoEfficiencyGrounded(root2, root2, gtree, framework2);
		
		// Retrieve all paths from root to leaves i.e. all the disputes
		List<ArrayList<Node<String>>> gdisputes = gtree.getPathsFromRootToAnyLeaf();
		System.out.println("Grounded disputes: " + gdisputes);
		detail.appendText("\n\n\nGrounded disputes: " + gdisputes);
		
		// Output winning strategies for proponent under grounded semantics
		List<ArrayList<Node<String>>> wgdisputes = new LinkedList<ArrayList<Node<String>>>();
		for (ArrayList<Node<String>> d : gdisputes) {
			if (d.size() % 2 == 1) {
				wgdisputes.add(d);
			}
		}
		
		// Check each individual proponent dispute if it's a winning strategy on it's own
		List<Boolean> results2 = new ArrayList<Boolean>();
		for (ArrayList<Node<String>> d : wgdisputes) {
			results.add(checkWinningSingle(d, framework));
		}
		
		// Failed single winning strategies
		List<ArrayList<Node<String>>> fwgd = new LinkedList<ArrayList<Node<String>>>();
		// Single winning strategies
		List<ArrayList<Node<String>>> wgd = new LinkedList<ArrayList<Node<String>>>();
		for (ArrayList<Node<String>> d : wgdisputes) {
			if (results.get(wpdisputes.indexOf(d))==false) {
				fwgd.add(d);
			}
			else wgd.add(d);
		}
		
		List<Set<ArrayList<Node<String>>>> cwgd = checkWinningCombined(fwgd, framework);
				
		if (wgd.isEmpty() && cwgd.get(0).isEmpty()) {
			System.out.println("No winning strategies, opponent always wins");
			detail.appendText("\n\nNo winning strategies, opponent always wins");
		}
		if (cwgd.get(0).isEmpty()==false) {
			for (Set<ArrayList<Node<String>>> d: cwgd) {				
				System.out.println("\nCombined winning strategy under grounded semantics: " + d);
				detail.appendText("\n\nCombined winning strategy under grounded semantics: " + d);
			}
		}
		if (wgd.isEmpty()==false){
			for (ArrayList<Node<String>> d : wgd) {				
				System.out.println("\nSingle winning strategy under grounded semantics: " + d);
				detail.appendText("\n\nSingle winning strategy under grounded semantics: " + d);
			}
		}
		
		// ********************SCENE SET-UP********************
        
        // Launch game screen for player vs player
        if (gameMode==true) {
        	GameScreen1.Start(stage, framework, pdisputes, gdisputes, flag_efficiency,
        			wpd, cwpd, wgd, cwgd);
        }
        // Launch game screen for player vs computer
        else GameScreen2.Start(stage, framework, pdisputes, gdisputes, flag_efficiency,
        		wpd, cwpd, wgd, cwgd);
        
		// Display graph
		showGraph(framework);
		
		// Only display dispute evaluations if cheat mode is on
		if (cheat==true){
			// Set up window
			Stage window = new Stage();
			window.setTitle("Dispute - Cheatsheet");		
			
			// Main vertical layout
			VBox layout_main = new VBox(10);
			layout_main.setPadding(new Insets(20, 20, 20, 20));
			layout_main.getChildren().addAll(detail);
			
			// Set scene parameters and show window
			Scene scene = new Scene(layout_main, 900, 300);
			scene.getStylesheets().add("application/styling.css");
	        window.setScene(scene);
	        window.getIcons().add(new Image("file:icon.png"));
	        window.show();
		}

	}
	
	// ***************
	// Function to generate children of argument in dispute tree under preferred semantics
	public static void createPreferredDispute(Node<String> root, Node<String> node, Tree<String> tree, Map<String, List<String>> framework){
		// Last move was by proponent, get legal opponent moves
		if ((tree.getNumberOfAncestors(node))%2 == 0) {
			//System.out.println("********Proponent moved********\n" + node);
			List<String> past = new LinkedList<String>(); 
			past.addAll(findPath(root, node));
			//System.out.println("past" + past);
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println("current" + current);
			current.removeAll(past);
			//System.out.println("new current" + current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}
		}
		// Last move was by opponent, get legal proponent moves
		else {
			//System.out.println("********Opponent moved********\n" + node);
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println(current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}			
		}
		// Recursively do the same for each attacker of node
		for (Node<String> child : node.getChildren()) {
			createPreferredDispute(root, child, tree, framework);
		}
	}
	// ***************
	
	// ***************
	// Function to generate children of argument in dispute tree under preferred semantics, without extra efficiency rules
	// Opponent can now repeat moves moved by proponent
	public static void NoEfficiencyPreferred(Node<String> root, Node<String> node, Tree<String> tree, Map<String, List<String>> framework){
		// Last move was by proponent, get legal opponent moves
		if ((tree.getNumberOfAncestors(node))%2 == 0) {
			//System.out.println("********Proponent moved********\n" + node);
			// Get all previous moves in dispute
			List<String> past = new LinkedList<String>();
			List<String> temp = new LinkedList<String>();
			temp.addAll(findPath(root, node));
			if (temp.size() > 10) return;
			//System.out.println("old past: " + temp);
			// Remove proponent moves
			for (int k=0; k<temp.size(); k++) {
				if (k %2==0) past.add(temp.get(k));
			}
			temp.clear();
			//System.out.println("past" + past);
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println("current" + current);
			current.removeAll(past);
			//System.out.println("new current" + current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}
		}
		// Last move was by opponent, get legal proponent moves
		else {
			//System.out.println("********Opponent moved********\n" + node);
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println(current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}			
		}
		// Recursively do the same for each attacker of node
		for (Node<String> child : node.getChildren()) {
			NoEfficiencyPreferred(root, child, tree, framework);
		}
	}
	// ***************
	
	
	
	// ***************
	// Function to generate children of argument in dispute tree under grounded semantics
	public static void createGroundedDispute(Node<String> root, Node<String> node, Tree<String> tree, Map<String, List<String>> framework){
		// Last move was by proponent, get legal opponent moves
		if ((tree.getNumberOfAncestors(node))%2 == 0) {
			//System.out.println("********Proponent moved********\n" + node);
			// Add legal attack to current
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println("current: " + current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}
		}
		// Last move was by opponent, get legal proponent moves
		else {
			//System.out.println("********Opponent moved********\n" + node);
			// Get all previous moves in dispute
			List<String> past = new LinkedList<String>();
			List<String> temp = new LinkedList<String>();
			temp.addAll(findPath(root, node));
			//System.out.println("old past: " + temp);
			// Remove opponent moves
			for (String p : temp){
				if (temp.indexOf(p) %2 == 0) past.add(p);
			}
			temp.clear();
			//System.out.println("Past: " + past);
			// Add available attacks
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			// Remove if previously moved
			current.removeAll(past);
			//System.out.println("current: " + current);
			// ******EXTRA EFFICIENCY RULES******
			// Remove move if it is counter-attacked by the move which it is against
			// Remove move if it is attacked by itself
			for (String m : current){
				if ((framework.get(m)).contains(node.getData()) || (framework.get(m)).contains(m)) 
					temp.add(m);
			}
			current.removeAll(temp);
			temp.clear();
			// Remove if attacks or is attacked by a move previously moved
			for (String m : current){
				for (String p : past) {
					if ((framework.get(m).contains(p)) || (framework.get(p).contains(m))) temp.add(m);
				}
			}
			current.removeAll(temp);
			temp.clear();
			// ******EXTRA EFFICIENCY RULES******
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}			
		}
		// Recursively do the same for each attacker of node
		for (Node<String> child : node.getChildren()) {
			createGroundedDispute(root, child, tree, framework);
		}
	}
	// ***************
	
	// ***************
	// Function to generate children of argument in dispute tree under grounded semantics
	public static void NoEfficiencyGrounded(Node<String> root, Node<String> node, Tree<String> tree, Map<String, List<String>> framework){
		// Last move was by proponent, get legal opponent moves
		if ((tree.getNumberOfAncestors(node))%2 == 0) {
			//System.out.println("********Proponent moved********\n" + node);
			// Add legal attack to current
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			//System.out.println("current: " + current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}
		}
		// Last move was by opponent, get legal proponent moves
		else {
			//System.out.println("********Opponent moved********\n" + node);
			// Get all previous moves in dispute
			List<String> past = new LinkedList<String>();
			List<String> temp = new LinkedList<String>();
			temp.addAll(findPath(root, node));
			//System.out.println("old past: " + temp);
			// Remove opponent moves
			for (int k=0; k<temp.size(); k++) {
				if (k %2==0) past.add(temp.get(k));
			}
			temp.clear();
			//System.out.println("Past: " + past);
			// Add available attacks
			List<String> current = new LinkedList<String>(); 
			current.addAll(framework.get(node.getData()));
			// Remove if previously moved
			current.removeAll(past);
			//System.out.println("current: " + current);
			if (current.isEmpty()) return;
			for (String x : current){
				node.addChild(new Node<String>(x));
			}			
		}
		// Recursively do the same for each attacker of node
		for (Node<String> child : node.getChildren()) {
			NoEfficiencyGrounded(root, child, tree, framework);
		}
	}
	// ***************
	
	// Method to validate single dispute and check if it is a winning strategy
	public static boolean checkWinningSingle(ArrayList<Node<String>> dispute, Map<String, List<String>> framework) {
		boolean result = true;
		// Create list of all moves by proponent (and otherwise) across all disputes
		List<String> pmoves = new LinkedList<String>();
		List<String> allmoves = new LinkedList<String>();
		for (Node<String> move : dispute) {
			if (dispute.indexOf(move) %2 == 0) pmoves.add(move.getData());
			allmoves.add(move.getData());
		}
		// Remove duplicate elements by copying to a Set
		Set<String> proset = new HashSet<>();
		proset.addAll(pmoves);
		Set<String> allset = new HashSet<>();
		allset.addAll(allmoves);
		// If each attack on pro move, is not contained within all moves, not a winning strategy
		for (String pmove : proset){
			for (String attacker: framework.get(pmove)){
				if (allset.contains(attacker) != true) result = false;
			}
		}
		return result;
	}
	
	// Method to check for existence of winning strategy by combining multiple proponent disputes
	public static List<Set<ArrayList<Node<String>>>> checkWinningCombined(List<ArrayList<Node<String>>> disputes, Map<String, List<String>> framework) {
		// Final list of winning strategies
		List<Set<ArrayList<Node<String>>>> wsc = new LinkedList<Set<ArrayList<Node<String>>>>();
		// Potentially winnig strategies
		List<Set<ArrayList<Node<String>>>> pwsc = new LinkedList<Set<ArrayList<Node<String>>>>();
		Set<ArrayList<Node<String>>> dset = new HashSet<>();
		dset.addAll(disputes);
		// Create a power set to try out different combinations of dispute lines to form winning strategy
		for (Set<ArrayList<Node<String>>> set: powerSet(dset)){
			if (set.size()>1 && set.size()<disputes.size()) pwsc.add(set);
		}
		// Check if each combination is indeed winning and add to final set
		if (pwsc.isEmpty()==false){
			for (Set<ArrayList<Node<String>>> pws: pwsc) {
				if (checkWinningMultiple(pws, framework)==true) wsc.add(pws);
			}
		}
		// If no winning strategy so far, try entire set of disputes and see if it's winning
		if (wsc.isEmpty()) {
			if (checkWinningMultiple(dset, framework)==true) wsc.add(dset);
		}
		return wsc;
	}
	
	// Method to validate multiple winning disputes and check if winning strategy exists
	public static boolean checkWinningMultiple(Set<ArrayList<Node<String>>> pws, Map<String, List<String>> framework){
		boolean result = true;
		// Create list of all moves by proponent (and otherwise) across all disputes
		List<String> pmoves = new LinkedList<String>();
		List<String> allmoves = new LinkedList<String>();
		for (ArrayList<Node<String>> d : pws){
			for (Node<String> move : d) {
				if (d.indexOf(move) %2 == 0) pmoves.add(move.getData());
				allmoves.add(move.getData());
			}
		}
		// Remove duplicate elements by copying to a Set
		Set<String> proset = new HashSet<>();
		proset.addAll(pmoves);
		Set<String> allset = new HashSet<>();
		allset.addAll(allmoves);
		// If each attack on pro move, is not contained within all moves, not a winning strategy
		for (String pmove : proset){
			for (String attacker: framework.get(pmove)){
				if (allset.contains(attacker) != true) result = false;
			}
		}
		return result;
	}
	
	public static boolean detectSingleConflict(ArrayList<Node<String>> dispute, Map<String, List<String>> framework) {
		boolean conflict_flag = false;
		// Create list of all moves by proponent across dispute
		List<String> pmoves = new LinkedList<String>();
		for (Node<String> move: dispute){
			if (dispute.indexOf(move) %2 == 0) {
				pmoves.add(move.getData());
			}
		}
		// Check for conflicting attacks
		for (String pmove : pmoves){
			for (String attack: framework.get(pmove))
				if (pmoves.contains(attack))
					conflict_flag = true;
		}
		return conflict_flag;
	}
	
	// Method to check whether preferred arguments moved by proponent are conflict-free
	public static boolean detectConflict(Set<ArrayList<Node<String>>> d, Map<String, List<String>> framework) {
		// Create list of all moves by proponent across all disputes
		List<String> pmoves = new LinkedList<String>();
		boolean conflict_flag = false;
		for (ArrayList<Node<String>> wd: d){
			for (Node<String> move: wd){
				if (wd.indexOf(move) %2 == 0) {
					pmoves.add(move.getData());
				}
			}
		}
		// Check for conflicting attacks
		for (String pmove : pmoves){
			for (String attack: framework.get(pmove))
				if (pmoves.contains(attack))
					conflict_flag = true;
		}
		return conflict_flag;
	}
	
	// ***************
	
	// Function to obtain a powerset of set of elements
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }		
	    return sets;
	}
	
	// Method to return a path from a node back to the root
	public static List<String> findPath(Node<String> root, Node<String> leaf) {
	    List<String> path = new ArrayList<>();
	    Node<String> node = leaf;
	    if (node==root){
	    	return path;
	    }
	    else {
		    do {
		        path.add(node.getParent().getData());
		        node = node.getParent();
		    } while (node != root);	
		    return path;
	    }
	}
	
	// Function to convert convert framework into a JUNG graph and display it in a Swing panel
	private static void showGraph(Map<String, List<String>> framework) {
		
        // Directed graph with string nodes and string edges
        DirectedSparseGraph<String, String> g = new DirectedSparseGraph<String, String>();
       	
        // Add all vertices to graph
       	for (String x: framework.keySet()) {
       		g.addVertex(x);
       	}
       	// Add edges, depending on attack relations specified in framework
       	int count = 0;
       	for (String x: framework.keySet()) {            		
       		for (String y: framework.get(x)){
       			g.addEdge("Edge " + count, y, x);
       			count++;
       		}
       	}
       	
       	// Get visualisation and layout
	    VisualizationImageServer<String, String> vs =
	      new VisualizationImageServer<String, String>(
	        new CircleLayout<String, String>(g), new Dimension(700, 500));
	    
	    // Add labels to each vertex
	    vs.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>() {
	      @Override
	      public String transform(String arg0) {
	        return arg0;
	      }
	    });    	    
	    // Transformer maps the vertex number to a vertex property
	    // Set the colour of each vertex
	    Transformer<String, Paint> vertexColor = new Transformer<String, Paint>() {
           public Paint transform(String i) {
               return Color.GRAY;
           }
	    };
        vs.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        
        // Add visualisation to Swing panel
        JFrame frame = new JFrame();
        frame.setBackground(Color.WHITE);
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);    	        	    
	            
    }
	
}
