package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable{

    // Initialise buttons and fields
	@FXML
    private Button btnA1;
	@FXML
    private Button btnA2;
    @FXML
    private Button btnB1;
    @FXML
    private Button btnB2;
    @FXML
    private Button btnB3;
    @FXML
    private Button btnC1;
    @FXML
    private TextField nodeInput;
    @FXML
    private CheckBox cbB1;
    @FXML
    private CheckBox cbB2;
    
    // Intermediate variable for no. of nodes
    public int nodeCount;
    // Player vs player OR player vs computer
    public static boolean gameMode;
    // Efficiency rules on or off
    public static boolean efficiency;
    // Display all possible disputes in advance or not
    public static boolean cheat;
	
    // Test method for buttons
	public void ButtonClicked(){
		System.out.println("Button clicked");
	}
	
	// Method to traverse between different scenes in GUI
	@FXML
	public void handleButtonAction(ActionEvent event) throws IOException{
	    Stage stage; 
	    Parent root;
	    if(event.getSource()==btnA1){
	       //set game mode to play against another player
	       gameMode = true;
	       //get reference to the button's stage         
	       stage=(Stage) btnA1.getScene().getWindow();
	       //load up OTHER FXML document
	       root = FXMLLoader.load(getClass().getResource("sample2.fxml"));
	    }
	    else if (event.getSource()==btnA2){
	       //set game mode to play against computer
	       gameMode = false;
	       stage=(Stage) btnA1.getScene().getWindow();
	       root = FXMLLoader.load(getClass().getResource("sample2.fxml"));	    	
	    }
	    else if(event.getSource()==btnB1){
	    	stage=(Stage) btnB1.getScene().getWindow();
	    	root = FXMLLoader.load(getClass().getResource("sample.fxml"));	    	
	    }
	    else {
	    	stage=(Stage) btnC1.getScene().getWindow();
	    	root = FXMLLoader.load(getClass().getResource("sample2.fxml"));
	    }
	    //create a new scene with root and set the stage
	    Scene scene = new Scene(root, 850, 500);
	    scene.getStylesheets().add("application/styling.css");
	    stage.setScene(scene);
	    stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
	    stage.show();
	}
	
	// Method to launch main game with framework settings
	@FXML
	private void startGame(ActionEvent event) throws IOException{
		// Randomly generate framework, launch game screen
		if(event.getSource()==btnB2){
	    	if (isInt(nodeInput)){
	    		// Set efficiency rules on/off depending on checkbox selection
	    		if (cbB1.isSelected()==true) {
	    			efficiency = true;
	    			System.out.println(efficiency);
	    		}
	    		if (cbB1.isSelected()==false) {
	    			efficiency = false;
	    			System.out.println(efficiency);
	    		}
	    		if (cbB2.isSelected()==true) {
	    			cheat = true;
	    			System.out.println(cheat);
	    		}
	    		if (cbB2.isSelected()==false) {
	    			cheat = false;
	    			System.out.println(cheat);
	    		}
	    		Map<String, List<String>> framework = new HashMap<>();
	    		Stage stage =(Stage) btnB2.getScene().getWindow();
	    		nodeCount = Integer.parseInt(nodeInput.getText());
	    		framework = Random_generator.rand(nodeCount);
	    		Dispute_generator.main(stage, framework, gameMode, efficiency, cheat);
	    	}
	    	else System.out.println("Enter a number");
	    }
		// Customise framework, open new window
	    else if(event.getSource()==btnB3){
	    	if (isInt(nodeInput)){
	    		// Set efficiency rules on/off depending on checkbox selection
	    		if (cbB1.isSelected()==true) {
	    			efficiency = true;
	    			System.out.println(efficiency);
	    		}
	    		if (cbB1.isSelected()==false) {
	    			efficiency = false;
	    			System.out.println(efficiency);
	    		}
	    		if (cbB2.isSelected()==true) {
	    			cheat = true;
	    			System.out.println(cheat);
	    		}
	    		if (cbB2.isSelected()==false) {
	    			cheat = false;
	    			System.out.println(cheat);
	    		}
	    		Stage stage =(Stage) btnB3.getScene().getWindow();
	    		nodeCount = Integer.parseInt(nodeInput.getText());
	    		game_generator(nodeCount, btnB3, stage);
	    	}
	    	else System.out.println("Enter a number");
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void game_generator(int nodeCount, Button previous, Stage window){
		// Set up window
		window.setTitle("Set up framework");
		
		// Table view to show arguments and their attackers
		TableView<Framework> table;
		
		// List of available node labels
		String[] charList = {
				"A", "B", "C", "D", "E", "F", "G", 
				"H", "I", "J", "K", "L", "M", "N", 
				"O", "P", "Q", "R", "S", "Y", "U", 
				"V", "W", "X", "Y", "Z"};
		
		//Hashmap to store argument framework
		Map<String, List<String>> framework = new HashMap<>();
		
		// Set up layout for multiple checkboxes
		HBox layout2 = new HBox(10);
		layout2.setPadding(new Insets(20, 20, 20, 0));
		// List of checkboxes
		List<CheckBox> checklist = new ArrayList<CheckBox>();
		
		// Set up drop-down menu and checklist to allow selection of argument
		ComboBox<String> choice1 = new ComboBox<>();		
		for (int i= 0; i<nodeCount; i++){
			choice1.getItems().add(charList[i]);
			CheckBox x = new CheckBox(charList[i]);
			checklist.add(x);
			layout2.getChildren().add(x);
		}
		choice1.setPromptText("Select an argument");
		
		TextField attackInput = new TextField();
		attackInput.setPromptText("Attackers");
		attackInput.setEditable(false);
		
		// Add checkbox selections to attack list
		for (CheckBox x : checklist) {
			x.setOnAction(e-> attackInput.appendText(x.getText()));
		}
		
		// List to contain all the argument labels
		ObservableList<String> strings = FXCollections.observableArrayList();
		for (int i = 0; i < nodeCount; i++) {
			strings.add(charList[i]);
		}
		
		// Miscellaneous text and buttons
		Text top1 = new Text("Available arguments: " + strings);
		Text top2 = new Text("Select argument from dropdown menu and choose its attackers from the checklist.");
				
		
		Button select = new Button("Add to framework");
		
		Button confirm = new Button("Play the game!");
		
		Button back = new Button("Go Back");
		
		Button delete = new Button("Delete selected row");
		
		//Set up table columns
        TableColumn<Framework, String> argColumn = new TableColumn<>("Argument");
        argColumn.setMinWidth(100);
        argColumn.setCellValueFactory(new PropertyValueFactory<>("argument"));

        TableColumn<Framework, List<String>> attackColumn = new TableColumn<>("Attackers");
        attackColumn.setMinWidth(300);
        attackColumn.setCellValueFactory(new PropertyValueFactory<>("attackers"));
		
        table = new TableView<>();
        table.getColumns().addAll(argColumn, attackColumn);
        
        // Button adds new argument and its attackers to framework
        select.setOnAction(e ->getSelection(checklist, choice1, attackInput, framework, table));
        
        // Button launches game screen with constructed framework
        confirm.setOnAction(e -> {
        	Dispute_generator.main(window, framework, gameMode, efficiency, cheat);
    	});
        
        // Button goes back to the previous screen
        back.setOnAction(e-> {
           try {
			Parent root = FXMLLoader.load(getClass().getResource("sample2.fxml"));
			Scene scene = new Scene(root, 850, 500);
		    window.setScene(scene);
		    window.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		    window.show();
           } catch (Exception e1) {
			// To-do Auto-generated catch block
			e1.printStackTrace();
           }        	
		});
        
        // Button action to delete an attack relation from framework
        delete.setOnAction(e-> deleteButtonClicked(table, framework));
        
		// Set up layout
		HBox layout1 = new HBox(10);
		layout1.setPadding(new Insets(20, 20, 20, 0));
		layout1.getChildren().addAll(choice1, attackInput, select);
		
		// Set up layout
		HBox bottom = new HBox(10);
		bottom.setPadding(new Insets(20, 20, 20, 0));
		bottom.getChildren().addAll(back, delete, confirm);
		
		VBox layout_main = new VBox(10);
		layout_main.setPadding(new Insets(20, 20, 20, 20));
		layout_main.getChildren().addAll(top1, top2, layout1, layout2, table, bottom);
		
		Scene scene = new Scene(layout_main, 850, 500);
		scene.getStylesheets().add("application/styling.css");
        window.setScene(scene);
        window.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        window.show();
	}
	
	// Method to add attack relations to hash map and populate table
	public static void getSelection(List<CheckBox> checklist, ComboBox<String> choice1, TextField attackInput, Map<String, List<String>> framework, TableView<Framework> table){
		// Clear checklist selection
		for (CheckBox x: checklist) {
			x.setSelected(false);
		}
		// Add values to hashmap
		String s = attackInput.getText();	
		List<String> myList = new ArrayList<String>(Arrays.asList(s.split("")));
		// Case where argument is unattacked
		if (attackInput.getText().isEmpty()) {
			framework.put(choice1.getValue(), Arrays.asList());
		}
		else framework.put(choice1.getValue(), myList);
		System.out.println(framework);
		// Add values to table for display
		Framework test = new Framework();
		test.setArgument(choice1.getValue());
		test.setAttackers(myList);
        table.getItems().add(test);
        attackInput.clear();
    }
	
	// Delete button clicked
    public void deleteButtonClicked(TableView<Framework> table, Map<String, List<String>> framework){
        ObservableList<Framework> frameworkSelected, allFrameworks;
        allFrameworks = table.getItems();
        // Remove from table view
        frameworkSelected = table.getSelectionModel().getSelectedItems();
        // Remove from output hashmap
        frameworkSelected.forEach(allFrameworks::remove);
        for (Framework x: frameworkSelected) {
        	framework.remove(x.getArgument(), framework.get(x.getArgument()));
        	System.out.println(framework);
        }
    }
	
	private boolean isInt(TextField input){
        try{
            //int number = Integer.parseInt(input.getText());
            //System.out.println(number + " is a number!");
            Integer.parseInt(input.getText());
            return true;
        }catch(NumberFormatException e){
            System.out.println("It is not a number");
            return false;
        }
    }
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//System.out.println("View is now loaded!");		
	}
	
}
