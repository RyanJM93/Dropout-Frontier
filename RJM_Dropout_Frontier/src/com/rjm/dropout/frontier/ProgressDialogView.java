package com.rjm.dropout.frontier;
import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ProgressDialogView {
	
	private Parent progressDialogParent;
	
	ProgressDialog progressDialog;

	public ProgressDialogView() {
		
		Stage progressDialogStage = new Stage();
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/frontier/ProgressDialog.fxml"));

			progressDialogParent = fxmlLoader.load(getClass().getClassLoader().getResourceAsStream("fxml/frontier/ProgressDialog.fxml"));
			
			progressDialog = fxmlLoader.getController();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	Scene progressDialogScene = new Scene(progressDialogParent);
		progressDialogStage.setScene(progressDialogScene);
		progressDialogStage.initStyle(StageStyle.UNDECORATED);
		progressDialogStage.setAlwaysOnTop(true);
		progressDialogStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent we) {
				if (progressDialogStage != null)
					progressDialogStage.close();
				System.exit(0);
			}
		});

		// Set the Dialog stage
		progressDialog.setStage(progressDialogStage);
	}

	public Parent getParent() {
		return progressDialogParent;
	}

	public void setParent(Parent parent) {
		this.progressDialogParent = parent;
	}
	
	public ProgressDialog getController(){
		return progressDialog;
	}
}