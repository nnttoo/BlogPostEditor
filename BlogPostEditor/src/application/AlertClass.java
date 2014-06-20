package application;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertClass {
	
	public AlertClass(String msg,Stage stage){
		Text mytext = new Text(msg);
		StackPane mystactk = new StackPane();
		mystactk.getChildren().add(mytext);
		
		mystactk.setPrefHeight(100);
		mystactk.setPrefWidth(400);
		
		Stage mystage = new Stage();
		mystage.initOwner(stage);
		mystage.initStyle(StageStyle.UTILITY);
		mystage.initModality(Modality.WINDOW_MODAL);
		
		mystage.setScene(new Scene(mystactk));
		mystage.showAndWait();	
		
	}
}
