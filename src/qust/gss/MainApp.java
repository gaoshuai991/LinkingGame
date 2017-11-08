package qust.gss;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import qust.gss.view.GameLayoutController;
import qust.gss.view.RootLayoutController;

public class MainApp extends Application {
	public static MainApp mainApp;
	private Stage stage;

	@Override
	public void start(Stage stage) {
		mainApp = this;
		this.stage = stage;
		stage.setTitle("Linking Game");
		stage.getIcons().add(new Image("file:Resource/Images/qust.png"));
		stage.setOnCloseRequest(v -> {
			System.exit(0);
		});
		showIndexLayout();
	}

	public void beginGame() {
		showGameLayout();
	}

	private void showGameLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			BorderPane page = loader.load();

			FXMLLoader gameLoader = new FXMLLoader();
			gameLoader.setLocation(MainApp.class.getResource("view/GameLayout.fxml"));
			AnchorPane gamePage = gameLoader.load();

			RootLayoutController rootController = loader.getController();
			rootController.setGameLayoutController((GameLayoutController) gameLoader.getController());

			page.setCenter(gamePage);

			stage.setScene(new Scene(page));
			stage.setResizable(false);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showIndexLayout() {
		try {
			AnchorPane page = (AnchorPane) FXMLLoader.load(MainApp.class.getResource("view/IndexLayout.fxml"));
			stage.setScene(new Scene(page));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
