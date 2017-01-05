package com.rjm.dropout.frontier.main;

import java.io.IOException;

import com.rjm.dropout.frontier.FrontierGameController;
import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.FrontierSinglePlayerSetupController;
import com.rjm.dropout.frontier.GlobalModel;
import com.rjm.dropout.frontier.enums.DropoutFrontierScene;
import com.rjm.dropout.frontier.objects.CivilizationIcons;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FrontierMainMenuController extends Application {

	private Scene mainScene;
	
    @FXML // fx:id="mainStackPane"
    private StackPane mainStackPane; // Value injected by FXMLLoader
    @FXML // fx:id="background"
    private ImageView background; // Value injected by FXMLLoader
	
    @FXML
    void singleplayerSetup(ActionEvent event) {
    	this.switchScene(DropoutFrontierScene.FRONTIERSINGLESETUP);
    }
    
    @FXML
    void multiplayerSetup(ActionEvent event) {
    	// Add multiplay scene
    }
    
    @FXML
    void quitToDesktop(ActionEvent event) {
    	Platform.exit();
    }

	
	private static volatile FrontierMainMenuController _instance;
	private final static Object _syncObject = new Object();
    public static FrontierMainMenuController getInstance() {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = new FrontierMainMenuController();
				}
			}
		}

		return _instance;
	}
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	background.fitWidthProperty().bind(mainStackPane.widthProperty());
    	background.fitHeightProperty().bind(mainStackPane.heightProperty());
    }

	public static void main(String[] args) {		
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		
		GlobalModel.getInstance().setFrontierMainMenuController(this);
		
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/menu/DropoutIcon.png")));

		stage.setY(720);
		
		GlobalModel.getInstance().setStage(stage);
		
		switchScene(DropoutFrontierScene.MAIN);
		
		mainScene = stage.getScene();

		if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
			throw new RuntimeException("*** ERROR: common conditional SCENE3D is not supported");
		}

		stage.show();

		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent we)
			{
				if (stage != null)
					stage.close();
				System.exit(0);
			}
		});
		
		CivilizationIcons.loadTextures();
	}

	public void switchScene(DropoutFrontierScene scene){
		switch(scene){
		case MAIN:

			// Setup Main Menu

			if (mainScene == null) {
				Parent mainMenuRoot = null;
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(
							getClass().getClassLoader().getResource(FXMLUtils.FXML_DROPOUT_FRONTIER_MAIN_MENU));

					mainMenuRoot = fxmlLoader.load(
							getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_DROPOUT_FRONTIER_MAIN_MENU));
					_instance = fxmlLoader.getController();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mainScene = new Scene(mainMenuRoot);
				mainScene.getStylesheets().add("css/Frontier.css");

				_instance.mainScene = this.mainScene;
			}
			
			GlobalModel.getInstance().getStage().setScene(mainScene);
			GlobalModel.getInstance().getStage().setTitle("Dropout Frontier");
//			getStage().centerOnScreen();

			break;
		case FRONTIERGAME:

			FrontierGameController.createNewPlanetMap(true);
			
			break;
		case FRONTIERSINGLESETUP:

			// Setup Map Dimensions Dialog

			Parent fssRoot = null;
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						getClass().getClassLoader().getResource(FXMLUtils.FXML_DROPOUT_FRONTIERSINGLESETUP));

				fssRoot = fxmlLoader.load(
						getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_DROPOUT_FRONTIERSINGLESETUP));

				FrontierSinglePlayerSetupController fssController = fxmlLoader.getController();
				fssController.setFrontierMainMenuController(this);
				fssController.setup();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Scene fssScene = new Scene(fssRoot);
			fssScene.getStylesheets().add("css/Frontier.css");
			GlobalModel.getInstance().getStage().setScene(fssScene);
			GlobalModel.getInstance().getStage().setTitle("Dropout Frontier - Singleplayer Setup");
//			getStage().centerOnScreen();

			break;
		}
	}
}
