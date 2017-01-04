package com.rjm.dropout.frontier;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class CivilizationItemView {
	
	private Parent civItemParent;
	
	CivilizationItemController civItem;

	public CivilizationItemView(VBox parentVBox) {
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/frontier/CivilizationItemTemplate.fxml"));

			civItemParent = fxmlLoader.load(getClass().getClassLoader().getResourceAsStream("fxml/frontier/CivilizationItemTemplate.fxml"));
			
			civItem = fxmlLoader.getController();

		} catch (IOException e) {
			e.printStackTrace();
		}

		civItem.setup();
		parentVBox.getChildren().add(civItemParent);
	}

	public Parent getParent() {
		return civItemParent;
	}

	public void setParent(Parent parent) {
		this.civItemParent = parent;
	}
	
	public CivilizationItemController getController(){
		return civItem;
	}
}