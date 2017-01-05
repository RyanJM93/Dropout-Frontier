package com.rjm.dropout.frontier.utilities;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.GlobalModel;
import com.rjm.dropout.frontier.MapGenTextures;
import com.rjm.dropout.frontier.SolarSystemController;
import com.rjm.dropout.frontier.objects.CivilizationIcons;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SolarSystemTestUtility extends Application {

	public static void main(String[] args) {		
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/menu/DropoutIcon.png")));

		stage.setY(720);
		
		GlobalModel.getInstance().setStage(stage);
		
		int size = 5;
		
		FrontierModel.getInstance().BWIDTH = (12*size) + 1;
		FrontierModel.getInstance().BHEIGHT = (10*size);
		
		MapGenTextures.loadTextures();
		
		stage.setScene(SolarSystemController.createNewSolarSystem(null).getScene());

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
}
