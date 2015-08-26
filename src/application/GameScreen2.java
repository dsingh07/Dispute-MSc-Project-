package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameScreen2 {
	
	// Player mode set at initial screen
	public static boolean player;
	// Game mode set at initial screen
	public static int game;
	// Available moves at every level of the dispute tree
	static List<List<String>> move_list;
	
	
	public static void Start(Stage stage, Map<String, List<String>> framework, List<ArrayList<Node<String>>> pdisputes, List<ArrayList<Node<String>>> gdisputes, int flag_efficiency,
			List<ArrayList<Node<String>>> wpd, List<Set<ArrayList<Node<String>>>> cwpd, List<ArrayList<Node<String>>> wgd, List<Set<ArrayList<Node<String>>>> cwgd) {
				
		// List of lists containing all lines of dispute
		List<List<String>> disputes = new LinkedList<List<String>>();
		// Completed disputes
		List<List<String>> done_disps = new ArrayList<List<String>>();
		
		// **Winning strategy lists**
		List<List<String>> wpd_new = new ArrayList<List<String>>();
		List<ArrayList<ArrayList<String>>> cwpd_new = new ArrayList<ArrayList<ArrayList<String>>>();
		List<List<String>> wgd_new = new ArrayList<List<String>>();
		List<ArrayList<ArrayList<String>>> cwgd_new = new ArrayList<ArrayList<ArrayList<String>>>();
				
		List<String> dispute = new ArrayList<String>();		
		List<String> pmoves = new ArrayList<String>();
		List<String> omoves = new ArrayList<String>();
		List<String> legal_pmoves = new LinkedList<String>();
		List<String> legal_omoves = new LinkedList<String>();

		Stage window = stage;
		
		// Initial scene with buttons to choose extension
		//************************************************
		Text head = new Text("Select your role in the game:");
		Button player_pro = new Button("Be the proponent");
		Button player_opp = new Button("Be the opponent");
		
		HBox initial = new HBox(30);
		initial.setPadding(new Insets(20, 20, 20, 20));
		initial.setAlignment(Pos.CENTER);
		initial.getChildren().addAll(player_pro, player_opp);
		
		Text head2 = new Text("Select your extension:");
		Button pref = new Button("Preferred Extension");
		pref.setDisable(true);
		Button ground = new Button("Grounded Extension");
		ground.setDisable(true);
		
		HBox initial2 = new HBox(30);
		initial2.setPadding(new Insets(20, 20, 20, 20));
		initial2.setAlignment(Pos.CENTER);
		initial2.getChildren().addAll(pref, ground);
		
		VBox init_main = new VBox(10);
		init_main.setPadding(new Insets(20, 20, 20, 20));
		init_main.setAlignment(Pos.CENTER);
		init_main.getChildren().addAll(head, initial, head2, initial2);
		
		Scene scene = new Scene(init_main, 850, 500);
		scene.getStylesheets().add("application/styling.css");
		
		// Set the stage
		window.setTitle("Dispute");
        window.setScene(scene);
        window.getIcons().add(new Image("file:icon.png"));
        window.show();
		//************************************************
        
        // ****Main game scene****
        
		Text top = new Text("Argument Game Proof Theories");
		
		// Set up drop-down menus to allow selection of argument
		ComboBox<String> choice = new ComboBox<>();
		for (String i : framework.keySet()){
			choice.getItems().add(i);
			if (i=="A") legal_pmoves.add(i);
		}
		choice.setPromptText("Select an argument");
		
		Button move = new Button("Make your move");
		
		// Clear screen button
		Button clear = new Button("Clear screen and restart dispute");
		
		HBox move_pane = new HBox(10);
		move_pane.setPadding(new Insets(20, 20, 20, 20));
		move_pane.setAlignment(Pos.CENTER);
		move_pane.getChildren().addAll(move, choice);
		
		Text detail = new Text("Dispute so far:");
		
		// Message panel which will display status of game
		TextArea message = new TextArea();
		message.setEditable(false);
		message.appendText("Proponent Moves First\nAvailable moves: " + legal_pmoves);
		
		// Set button actions to execute move
		move.setOnAction(e-> {
			// Set computer responses when player is proponent
			if (player==true) {			
				if (ValidateMove(message, choice, legal_pmoves, dispute, detail)==true) {
					pmoves.add(choice.getValue());
					removeMove(move_list, dispute, choice);
					setOppMoves(framework, dispute, omoves, legal_omoves, game);
					if (legal_omoves.isEmpty()) {
						message.appendText("\nNo more moves, you (proponent) have won this line of dispute!");
						boolean result = checkDispute(message, dispute, disputes, done_disps);
						// Check for winning strategy
						if (result==true) {
							if (game %2==0){
								if (checkWinningSinglePref(framework, wpd, message, dispute, wpd_new)==false) {
									checkWinningCombinedPref(framework, message, dispute, cwpd, cwpd_new);
								}
							}
							else if (game %2==1) {
								if (wgd_new.contains(dispute)) message.appendText("\nExplored a winning strategy for proponent argument!");
								else checkWinningCombinedGround(message, dispute, cwgd, cwgd_new);
							}
						}
					}
					else {
						// Set legal responses for computer and execute move
						message.appendText("\nAvailable moves: " + legal_omoves);
						ComputerMove(message, dispute, omoves, legal_omoves, move_list, detail);
						setProMoves(framework, dispute, pmoves, legal_pmoves, game);
						if (legal_pmoves.isEmpty()) {
							message.appendText("\nNo more moves, the computer (opponent) has won this line of dispute!");
							message.appendText("\nNo winning strategies in this line of dispute.");
							checkDispute(message, dispute, disputes, done_disps);
						}
						else {
							message.appendText("\nAvailable moves: " + legal_pmoves);
						}
					}
				}			
			}
			// Set computer responses when player is opponent
			else {
				if (ValidateMove(message, choice, legal_omoves, dispute, detail)==true) {
					omoves.add(choice.getValue());
					removeMove(move_list, dispute, choice);
					setProMoves(framework, dispute, pmoves, legal_pmoves, game);
					if (legal_pmoves.isEmpty()) {
						message.appendText("\nNo more moves, you (the opponent) have won this line of dispute!");
						message.appendText("\nNo winning strategies in this line of dispute.");
						checkDispute(message, dispute, disputes, done_disps);
					}
					else {
						// Set legal responses for computer and execute move
						message.appendText("\nAvailable moves: " + legal_pmoves);
						ComputerMove(message, dispute, pmoves, legal_pmoves, move_list, detail);
						setOppMoves(framework, dispute, omoves, legal_omoves, game);
						if (legal_omoves.isEmpty()) {
							message.appendText("\nNo more moves, the computer (proponent) has won this line of dispute!");
							boolean result = checkDispute(message, dispute, disputes, done_disps);
							// Check for winning strategy
							if (result==true) {
								if (game %2==0){
									if (checkWinningSinglePref(framework, wpd, message, dispute, wpd_new)==false) {
										checkWinningCombinedPref(framework, message, dispute, cwpd, cwpd_new);
									}
								}
								else if (game %2==1) {
									if (wgd_new.contains(dispute)) message.appendText("\nExplored a winning strategy for proponent argument!");
									else checkWinningCombinedGround(message, dispute, cwgd, cwgd_new);
								}
							}
						}
						else {
							message.appendText("\nAvailable moves: " + legal_omoves);
						}
					}
				}
			}
		});

		
		clear.setOnAction(e-> {
			// End game
			if (disputes.size()==0) {
				message.clear();
				move.setDisable(true);
				detail.setText("Dispute over");
				message.appendText("Game over, no more lines of dispute possible.");
			}
			// Clear and reset all lists for new games and reset screen to default game mode
			else {
				message.clear();
				pmoves.clear();
				omoves.clear();
				dispute.clear();
				legal_pmoves.clear();
				for (String i : framework.keySet()){
					if (i=="A") legal_pmoves.add(i);
				}
				message.appendText("Cleared, new game possible now!");
				message.appendText("\nLines of dispute remaining: " + disputes.size());
				message.appendText("\n\nProponent Moves First\nAvailable moves: " + legal_pmoves);
				detail.setText("Dispute so far: ");
				// **Computer moves first where the player is the opponent**
				if (player==false) {
					ComputerMove(message, dispute, pmoves, legal_pmoves, move_list, detail);
					setOppMoves(framework, dispute, omoves, legal_omoves, game);
					if (legal_omoves.isEmpty()) {
						message.appendText("\nNo more moves, the computer (proponent) has won this line of dispute!");
						boolean result = checkDispute(message, dispute, disputes, done_disps);
						// Check for winning strategy
						if (result==true) {
							if (game %2==0){
								if (checkWinningSinglePref(framework, wpd, message, dispute, wpd_new)==false) {
									checkWinningCombinedPref(framework, message, dispute, cwpd, cwpd_new);
								}
							}
							else if (game %2==1) {
								if (wgd_new.contains(dispute)) message.appendText("\nExplored a winning strategy for proponent argument!");
								else checkWinningCombinedGround(message, dispute, cwgd, cwgd_new);
							}
						}
					}
					else {
						message.appendText("\nAvailable moves: " + legal_omoves);
					}
				}
				// **********************************************************
			}
		});
		
		// Reset application screen button
		// Goes back to start screen
		Button home = new Button("Restart Application");
		home.setOnAction(e-> {
			Parent root = null;
		    try {
				root = FXMLLoader.load(GameScreen2.class.getResource("sample.fxml"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		    Scene start = new Scene(root, 850, 500);
		    start.getStylesheets().add("application/styling.css");
		    window.setScene(start);
		    window.getIcons().add(new Image(GameScreen2.class.getResourceAsStream("icon.png")));
		    window.show();
	    });
		
		// Bottom horizontal layout
		HBox bottom = new HBox(40);
		bottom.setPadding(new Insets(20, 20, 20, 20));
		bottom.setAlignment(Pos.CENTER);
		bottom.getChildren().addAll(clear, home);
		
		// Main vertical layout
		VBox layout_main = new VBox(10);
		layout_main.setPadding(new Insets(20, 20, 20, 20));
		layout_main.getChildren().addAll(top, move_pane, detail, message, bottom);
		
		// Set player based on button actions
		player_pro.setOnAction(e-> {
			player = true;
			pref.setDisable(false);
			ground.setDisable(false);
			player_pro.setDisable(true);
			player_opp.setDisable(true);
		});
		player_opp.setOnAction(e-> {
			player = false;
			pref.setDisable(false);
			ground.setDisable(false);
			player_pro.setDisable(true);
			player_opp.setDisable(true);				
		});
		
		// Transitions to main game scene after choosing extension
		// Preferred
		pref.setOnAction(e-> {
			game = 0 + flag_efficiency;
			convertDispute(disputes, pdisputes);
			convertDispute(wpd_new, wpd);
			convertBigList(cwpd, cwpd_new);
			move_list = getMoves(disputes);
			if (player==false) {
				// Computer moves first where the player is the opponent
				ComputerMove(message, dispute, pmoves, legal_pmoves, move_list, detail);
				setOppMoves(framework, dispute, omoves, legal_omoves, game);
				message.appendText("\nAvailable moves: " + legal_omoves);		
			}
			// Set the stage
			Scene scene1 = new Scene(layout_main, 850, 500);
			scene1.getStylesheets().add("application/styling.css");
			window.setTitle("Preferred Dispute");
	        window.setScene(scene1);
	        window.getIcons().add(new Image("file:icon.png"));
	        window.show();
		});
		// Grounded
		ground.setOnAction(e-> {
			game = 1 + flag_efficiency;
			convertDispute(disputes, gdisputes);
			convertDispute(wgd_new, wgd);
			convertBigList(cwgd, cwgd_new);
			move_list = getMoves(disputes);
			if (player==false) {
				// Computer moves first where the player is the opponent
				ComputerMove(message, dispute, pmoves, legal_pmoves, move_list, detail);
				setOppMoves(framework, dispute, omoves, legal_omoves, game);
				message.appendText("\nAvailable moves: " + legal_omoves);		
			}
			// Set the stage
			Scene scene1 = new Scene(layout_main, 850, 500);
			scene1.getStylesheets().add("application/styling.css");
			window.setTitle("Grounded Dispute");
	        window.setScene(scene1);
	        window.getIcons().add(new Image("file:icon.png"));
	        window.show();
		});
	
	}
	
	//********************************************************************************
	
	// Main function to validate player move and advance game
	public static boolean ValidateMove(TextArea message, ComboBox<String> choice, List<String> legal_moves, List<String> dispute, Text detail){
		//Check legality of move
		if (legal_moves.contains(choice.getValue()) && move_list.get(dispute.size()).contains(choice.getValue())==true) {
			message.appendText("\n\nProponent moved: " + choice.getValue());
			message.appendText("\nOpponent must now move");
			dispute.add(choice.getValue());
			detail.setText("Dispute so far: " + dispute);
			return true;
		}
		else if (legal_moves.contains(choice.getValue()) && move_list.get(dispute.size()).contains(choice.getValue())==false) {
			message.appendText("\n\nAlready moved that in another line of dispute, try again!");
			return false;
		}
		else { 
			message.appendText("\n\nIllegal move, try again!");
			return false;
		}
	}
	
	// Main function to execute a legal move by computer
	public static void ComputerMove(TextArea message, List<String> dispute, List<String> moves, List<String> legal_moves, List<List<String>> move_list, Text detail) {
		int iteration = dispute.size();
		for (String move: legal_moves){
			// Check if legal move has already been moved in another dispute
			if (move_list.get(iteration).contains(move)) {
				message.appendText("\n\nComputer moved: " + move);
				message.appendText("\nYou must now move!");
				dispute.add(move);
				moves.add(move);
				detail.setText("Dispute so far: " + dispute);
				move_list.get(iteration).remove(move);
				break;
			}
		}		
	}
	
	// ***Functions to set legal moves for opp/pro under both extensions***
	
	// Get legal pro moves
	public static void setProMoves(Map<String, List<String>> framework, List<String> dispute, List<String> pmoves, List<String> legal_pmoves, Integer game){
		String last;
		legal_pmoves.clear();
		last = dispute.get(dispute.size()-1);
		// *Set legal moves according to efficient/non-efficient preferred semantics*
		if (game==0 || game==2) {
			for (String x: framework.get(last)) legal_pmoves.add(x);
		}
		// *Set legal moves according to efficient grounded semantics*
		else if (game==1) {
			for (String x: framework.get(last)) legal_pmoves.add(x);
			// remove move if previously moved by pro
			legal_pmoves.removeAll(pmoves);
			// ******EXTRA EFFICIENCY RULES******
			// Remove move if it is counter-attacked by the move which it is against
			// Remove move if it is attacked by itself
			List<String> temp = new LinkedList<String>();
			for (String m : legal_pmoves){
				if ((framework.get(m)).contains(last) || (framework.get(m)).contains(m)) temp.add(m);
			}
			legal_pmoves.removeAll(temp);
			temp.clear();
			// Remove if attacks or is attacked by a move previously moved
			for (String m : legal_pmoves){
				for (String p : pmoves) {
					if ((framework.get(m).contains(p)) || (framework.get(p).contains(m))) temp.add(m);
				}
			}
			legal_pmoves.removeAll(temp);
			temp.clear();
			// ***********************************
		}
		// *Set legal moves according to non-efficient grounded semantics*
		else if (game==3) {
			for (String x: framework.get(last)) legal_pmoves.add(x);
			// remove move if previously moved by pro
			legal_pmoves.removeAll(pmoves);
		}
	}
	
	// Get legal opp moves
		public static void setOppMoves(Map<String, List<String>> framework, List<String> dispute, List<String> omoves, List<String> legal_omoves, Integer game){
			String last;
			legal_omoves.clear();
			last = dispute.get(dispute.size()-1);
			// *Set legal moves according to efficient preferred semantics*
			if (game==0) {
				for (String x: framework.get(last)) legal_omoves.add(x);
				// remove move if previously moved by pro/opp
				legal_omoves.removeAll(dispute);
			}
			// *Set legal moves according to non-efficient preferred semantics*
			if (game==2) {
				for (String x: framework.get(last)) legal_omoves.add(x);
				// remove move if previously moved by pro/opp
				legal_omoves.removeAll(omoves);
			}
			// *Set legal moves according to efficient/non-efficient grounded semantics*
			else if (game==1 || game==3) {
				for (String x: framework.get(last)) legal_omoves.add(x);
			}
		}
	
	// Function to create a list of available moves at each iteration
	public static List<List<String>> getMoves(List<List<String>> disputes) {
		List<List<String>> move_list = new LinkedList<List<String>>();
		// get max number of arguments in any dispute
		int max_cap = 0;
		for (List<String> x: disputes){
			if (x.size() > max_cap) max_cap = x.size();
		}
		// add all arguments in all disputes in order moved	to temp list
		for (int i=0; i<max_cap; i++) {
			List<String> temp = new LinkedList<String>();
			for (List<String> x: disputes) {
				if (x.size() >= i+1) temp.add(x.get(i));
			}
			move_list.add(temp);
		}
		return move_list;
	}
	
	// Function to remove last move from move list
	public static void removeMove(List<List<String>> move_list, List<String> dispute, ComboBox<String> choice) {
		String last = choice.getValue();
		int iteration = dispute.size() - 1;
		(move_list.get(iteration)).remove(last);
	}
	
	// **Function to check traversal progress of dispute tree**
	public static boolean checkDispute(TextArea message, List<String> dispute, List<List<String>> disputes, List<List<String>> done_disps) {
		boolean result = true;
		// if valid dispute, remove from to-do list and add to completed list
		if (disputes.contains(dispute)) {
			disputes.remove(dispute);
			done_disps.add(dispute);
			if (disputes.size()==0) message.appendText("\nComplete dispute tree traversed, well done!");
			else message.appendText("\n" + done_disps.size() + " disputes completed, " + disputes.size() + " remaining");
		}
		else if (done_disps.contains(dispute)) {
			// repeated dispute, prompt to try again
			if (disputes.size()!=0) message.appendText("\nLine of dispute already explored, try again");
			else message.appendText("\nAll lines of dispute already explored!");
			result = false;
		}
		// prompt in case of illegal or partial dispute
		else if ((done_disps.contains(dispute) && disputes.contains(dispute))==false) {
			message.appendText("\nIllegal or partial line of dispute explored! Remember proponent should always start with A!");
			result = false;
		}
		return result;
	}
	
	// Function to convert a list of string nodes to a list of strings
	public static void convertDispute(List<List<String>> disputes, List<ArrayList<Node<String>>> original_disputes) {
		for (int i=0; i<original_disputes.size(); i++){
			disputes.add(new ArrayList<String>());
		}
		for (int i=0; i<original_disputes.size(); i++){
			for (int j=0; j<original_disputes.get(i).size(); j++){
				(disputes.get(i)).add((original_disputes.get(i).get(j).getData()));
			}
		}
	}
	
	// Function to convert multidimensional list of string nodes to list of strings
	public static void convertBigList(List<Set<ArrayList<Node<String>>>> cwd, List<ArrayList<ArrayList<String>>> cwd_new) {
		List<ArrayList<ArrayList<Node<String>>>> cwd_temp = new ArrayList<ArrayList<ArrayList<Node<String>>>>();
		for (int i=0; i<cwd.size(); i++){
			cwd_new.add(new ArrayList<ArrayList<String>>());
			cwd_temp.add(new ArrayList<ArrayList<Node<String>>>());
		}
		for (int i=0; i<cwd.size(); i++){
			for (int k=0; k<cwd.get(i).size(); k++){
				cwd_new.get(i).add(new ArrayList<String>());
			}
		}
		for (int i=0; i<cwd.size(); i++){
			cwd_temp.get(i).addAll(cwd.get(i));
		}
		for (int i=0; i<cwd_temp.size(); i++){
			for (int k=0; k<cwd_temp.get(i).size(); k++){
				for (int j=0; j<cwd_temp.get(i).get(k).size(); j++) {
					(cwd_new.get(i).get(k)).add(cwd_temp.get(i).get(k).get(j).getData());
				}
			}
		}
	}
	
	// Function to check if completed preferred dispute is a single winning strategy
	public static boolean checkWinningSinglePref(Map<String, List<String>> framework, List<ArrayList<Node<String>>> wpd_old, TextArea message, List<String> dispute, List<List<String>> wpd_new) {
		boolean result = false;
		if (wpd_new.contains(dispute)) {
			int index = wpd_new.indexOf(dispute);
			if (Dispute_generator.detectSingleConflict(wpd_old.get(index), framework)==false) {
				message.appendText("\nExplored a conflict-free winning strategy for proponent argument!");
			}
			else {
				message.appendText("\nExplored a conflicted winning strategy for proponent argument.");
			}
			result = true;
		}
		return result;
	}
	
	// Function to check if completed preferred dispute is part of a combined winning strategy
	public static void checkWinningCombinedPref(Map<String, List<String>> framework, TextArea message, List<String> dispute, List<Set<ArrayList<Node<String>>>> cwd, List<ArrayList<ArrayList<String>>> cwd_new) {
		int count = 0;
		int cfws = 0;
		List<Integer> indexes = new ArrayList<Integer>();
		for (ArrayList<ArrayList<String>> ws: cwd_new) {
			if (ws.contains(dispute)) {
				if (ws.size()!=1) {
					count++;
					indexes.add(cwd_new.indexOf(ws));
					message.appendText("\nExplored one part of a winning strategy containing " + cwd.get(cwd_new.indexOf(ws)).size() + " lines of dispute.");
				}
				else {
					message.appendText("\nExplored final part of a winning strategy containing " + cwd.get(cwd_new.indexOf(ws)).size() + " lines of dispute.");
					count++;
					indexes.add(cwd_new.indexOf(ws));
				}
			}
		}
		// Check for conflicts
		for (Integer x: indexes) {
			if (Dispute_generator.detectConflict(cwd.get(x), framework)==false) cfws++;
			cwd_new.get(x).remove(dispute);
		}
		message.appendText("\nWinning strategies containing this line of dispute: " + count + "; of which " + cfws + " are conflict-free.");
	}
	
	// Function to check if completed grounded dispute is part of a combined winning strategy
	public static void checkWinningCombinedGround(TextArea message, List<String> dispute, List<Set<ArrayList<Node<String>>>> cwd, List<ArrayList<ArrayList<String>>> cwd_new) {
		int count = 0;
		List<Integer> indexes = new ArrayList<Integer>();
		for (ArrayList<ArrayList<String>> ws: cwd_new) {
			if (ws.contains(dispute)) {
				if (ws.size()!=1) {
					message.appendText("\nExplored one part of a winning strategy containing " + cwd.get(cwd_new.indexOf(ws)).size() + " lines of dispute.");
					count++;
					indexes.add(cwd_new.indexOf(ws));
				}
				else {
					message.appendText("\nExplored final part of a winning strategy containing " + cwd.get(cwd_new.indexOf(ws)).size() + " lines of dispute.");
					count++;
					indexes.add(cwd_new.indexOf(ws));
				}
			}
		}
		for (Integer x: indexes) {
			cwd_new.get(x).remove(dispute);
		}
		message.appendText("\nWinning strategies containing this line of dispute: " + count + ".");
	}
			
}
