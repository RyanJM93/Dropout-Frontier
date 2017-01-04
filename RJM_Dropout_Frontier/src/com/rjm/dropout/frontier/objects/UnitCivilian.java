package com.rjm.dropout.frontier.objects;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

public class UnitCivilian extends UnitAbs {
	
    @FXML // fx:id="unit"
    private StackPane unit; // Value injected by FXMLLoader

    @FXML // fx:id="polygon"
    private Polygon polygon; // Value injected by FXMLLoader
    
    @FXML // fx:id="icon"
    private ImageView icon; // Value injected by FXMLLoader

    @FXML
    void mouseSelected() {
    	System.out.println("Selected");
    	select();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	setUnit(unit);
        setPolygon(polygon);
        setIcon(icon);
        
        unit.disableProperty().bind(movementLeft.lessThanOrEqualTo(0));
    }
}
