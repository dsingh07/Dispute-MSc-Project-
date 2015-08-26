package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	Stage window;
	Scene scene1, scene2, scene3;
	
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	window = primaryStage;
    	
    	// Scene 1    	
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        scene1 = new Scene(root, 850, 500);
        scene1.getStylesheets().add("application/styling.css");
        
        window.setTitle("Dispute");
        window.setScene(scene1);
        window.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        window.show();
        
    }

}
