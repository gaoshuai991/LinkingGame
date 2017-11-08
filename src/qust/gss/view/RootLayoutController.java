package qust.gss.view;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class RootLayoutController {
	private GameLayoutController game;
	
	
	public void setGameLayoutController(GameLayoutController game){
		this.game = game;
	}
	
	@FXML
	private void handleRestart(){
		this.game.restart();
	}
	@FXML
	private void handleEasy(){
		GameLayoutController.LEVEL = 50;
		this.game.restart();
	}
	@FXML
	private void handleNormal(){
		GameLayoutController.LEVEL = 35;
		this.game.restart();
	}
	@FXML
	private void handleHard(){
		GameLayoutController.LEVEL = 20;
		this.game.restart();
	}
	@FXML
	private void handleAbout() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("About");
		dialog.setContentText("This program is created by JavaFX 2.0\nAuthor:308国道");
		dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
		dialog.show();
	}
	@FXML
	private void handleExit(){
		System.exit(0);
	}
}
