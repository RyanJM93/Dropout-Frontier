package com.rjm.dropout.frontier;

import java.util.Comparator;
import java.util.Random;

import com.rjm.dropout.frontier.enums.Civilization;
import com.rjm.dropout.frontier.objects.CivLeaderPair;
import com.rjm.dropout.frontier.objects.ICivilization;
import com.rjm.dropout.frontier.objects.ILeader;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

public class CivilizationItemController {

    @FXML // fx:id="civilizationTemplate"
    private HBox civilizationTemplate; // Value injected by FXMLLoader
    
    @FXML // fx:id="civIcon"
    private ImageView civIcon; // Value injected by FXMLLoader
    
    @FXML // fx:id="civChoiceBox"
    private ChoiceBox<CivLeaderPair> civChoiceBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="civAbilities"
    private Label civAbilities; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	civChoiceBox.getSelectionModel().selectedItemProperty().addListener((oldSelection,newSelection,obs) -> {
    		
    		Pair<ICivilization,ILeader> selectedPair = civChoiceBox.getSelectionModel().getSelectedItem();
    		
    		civilizationTemplate.setStyle("-fx-background-color: " 
    				+ selectedPair.getValue().toRGBCode(selectedPair.getValue().getTerritoryColor()) + ";"
					+ "-fx-border-color: " + selectedPair.getKey().toRGBCode(selectedPair.getKey().getBorderColor()) + ";");
    		
    		civIcon.setImage(selectedPair.getKey().getIcon());
    	});
    }
    
    public void setup(){

    	FrontierModel.getInstance().getAllCivLeaderPairs().forEach(civLeaderPair -> {
    		civChoiceBox.getItems().add(civLeaderPair);
    	});
    	
    	civChoiceBox.getItems().sort(new Comparator<CivLeaderPair>() {

			@Override
			public int compare(CivLeaderPair left, CivLeaderPair right) {
				return left.toString().compareTo(right.toString());
			}
		});
    	
    	civChoiceBox.getSelectionModel().selectFirst();
    }
    
    public HBox getCivBox(){
    	return civilizationTemplate;
    }
    
    public CivLeaderPair getSelectedCiv(boolean isUnique){
    	
    	CivLeaderPair selection = civChoiceBox.getSelectionModel().getSelectedItem();
    	
    	if(selection.getKey() == Civilization.RANDOM){
    		
    		System.out.println("Choosing Random Civ");
    		
    		if(isUnique){
    			selection = FrontierModel.getInstance().getNextRandomCiv();
    		} else {
				Random rand = new Random();
				int randIndex = rand.nextInt(civChoiceBox.getItems().size() - 1);
				selection = civChoiceBox.getItems().get(randIndex + 1);
			}
    		
			System.out.println("Selected " + selection.getKey().getName());
    	} else {
    		FrontierModel.getInstance().removeFromUnique(selection);
    	}
    	
    	return selection;
    }
}
