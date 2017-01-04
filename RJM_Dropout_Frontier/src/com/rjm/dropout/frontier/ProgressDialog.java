package com.rjm.dropout.frontier;

import com.rjm.dropout.frontier.tasks.FrontierTask;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ProgressDialog {
	
	private Stage stage;
	
	private FrontierTask<?> task;

    @FXML // fx:id="progressBar"
    private ProgressBar progressBar; // Value injected by FXMLLoader

    @FXML // fx:id="progressLabel"
    private Label progressLabel; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	
    }

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public FrontierTask<?> getTask() {
		return task;
	}

	public void setTask(FrontierTask<?> frontierTask) {
		this.task = frontierTask;

        progressLabel.textProperty().bind(frontierTask.messageProperty);

        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(frontierTask.progressProperty);
	}
}
