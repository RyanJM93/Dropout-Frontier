package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.FrontierModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class City extends AbsHolding {

    @FXML // fx:id="city"
    private StackPane city; // Value injected by FXMLLoader

    @FXML // fx:id="cityInfoPane"
    private HBox cityInfoPane; // Value injected by FXMLLoader

    @FXML // fx:id="namePlate"
    private Rectangle namePlate; // Value injected by FXMLLoader

    @FXML // fx:id="nameLabel"
    private Label nameLabel; // Value injected by FXMLLoader
    
    @FXML // fx:id="populationLabel"
    private Label populationLabel; // Value injected by FXMLLoader
    
    IntegerProperty population = new SimpleIntegerProperty(0);

    @FXML
    void mouseSelected(MouseEvent event) {
    	if(event.getButton() == MouseButton.PRIMARY) {
			System.out.println(nameLabel.getText() + " Selected");
			select();
			if (event.isControlDown()) {
				FrontierModel.getInstance().createCounty(getOwner(), this);
			} 
		}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	setCity(city);
        setPolygon(namePlate);
        setNameLabel(nameLabel);
        
        populationLabel.textFillProperty().bind(nameLabel.textFillProperty());
        populationLabel.textProperty().bind(population.asString());
    }
    
    public void addPopulation(int amount){
    	population.set(population.get()+amount);
    }
    
    public void subtractPopulation(int amount){
    	population.set(population.get()-amount);
    }
}