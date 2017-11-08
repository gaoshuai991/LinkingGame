package qust.gss.view;

import javafx.fxml.FXML;
import qust.gss.MainApp;

public class IndexLayoutController {
	public IndexLayoutController(){
	}
	@FXML
	private void initialize(){
	}
	@FXML
	private void handleBegin() {
		MainApp.mainApp.beginGame();
	}

}
