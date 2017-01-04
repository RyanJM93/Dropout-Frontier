package com.rjm.dropout.frontier.tasks;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class UpdateMinimapTask extends FrontierTask<Boolean> {
	
	public UpdateMinimapTask() {
		setWait(false);
	}

	@Override
	protected String getTaskTitle() {
		return "Updating Minimap";
	}

	@Override
	protected Boolean runInBackground() {
		
		SnapshotParameters params = new SnapshotParameters();
				
		Image img = new Image(getClass().getClassLoader().getResourceAsStream(FXMLUtils.IMG_BLANK_MAP_PARCHMENT));
		ImagePattern pattern = new ImagePattern(img);
		params.setFill(pattern);
		
		Platform.runLater(() -> {
			Image miniMapImage = FrontierModel.getInstance().getFrontierGameController().getMapGroup().snapshot(params, null);
			FrontierModel.getInstance().getFrontierGameController().updateMiniMap(miniMapImage);
		});

		return true;
	}
}
