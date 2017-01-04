package com.rjm.dropout.frontier;

import java.util.ArrayList;
import java.util.List;

import com.rjm.dropout.frontier.enums.DropoutFrontierScene;
import com.rjm.dropout.frontier.enums.MapSize;
import com.rjm.dropout.frontier.enums.MapType;
import com.rjm.dropout.frontier.enums.WorldAge;
import com.rjm.dropout.frontier.main.FrontierMainMenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FrontierSinglePlayerSetupController {
	
	FrontierMainMenuController frontierMainMenuController;
    
    @FXML // fx:id="mainStackPane"
    private StackPane mainStackPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="background"
    private ImageView background; // Value injected by FXMLLoader
    @FXML // fx:id="civilizationVBox"
	private VBox civilizationVBox; // Value injected by FXMLLoader
    
    @FXML
    private ChoiceBox<MapType> mapTypeChoiceBox;
    
    @FXML
    private ChoiceBox<MapSize> mapSizeChoiceBox;

    @FXML
    private ChoiceBox<WorldAge> worldAgeChoiceBox;
    
    @FXML
    private ChoiceBox<?> temperatureChoiceBox;

    @FXML
    private ChoiceBox<?> resourceChoiceBox;

    @FXML
    private ChoiceBox<?> startPositionChoiceBox;
    
    @FXML // fx:id="onlyUniqueCheckBox"
    private CheckBox onlyUniqueCheckBox; // Value injected by FXMLLoader

    @FXML
    void backToMenu(ActionEvent event) {
    	frontierMainMenuController.switchScene(DropoutFrontierScene.MAIN);
    }

    @FXML
    void addPlayer(ActionEvent event) {
    	addPlayer();
    }
    
    void addPlayer() {
    	civList.add(new CivilizationItemView(civilizationVBox).getController());
    }

    @FXML
    void subtractPlayer(ActionEvent event) {
    	subtractPlayer();
    }
    
    void subtractPlayer() {
    	Node node = civilizationVBox.getChildren().get(civilizationVBox.getChildren().size()-1);
    	
    	CivilizationItemController toDelete = null;
    	
    	if(node != null && node instanceof HBox){
    		for(CivilizationItemController civ : civList){
    			if((HBox)node == civ.getCivBox()){
    				toDelete = civ;
    			}
    		}
    		
    		civilizationVBox.getChildren().remove(node);
    	}
    	
    	if(toDelete != null){
    		civList.remove(toDelete);
    	}
    	
    	System.out.println("Civ List: " + civList);
    }
    
    List<CivilizationItemController> civList = new ArrayList<CivilizationItemController>();

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    	background.fitWidthProperty().bind(mainStackPane.widthProperty());
    	background.fitHeightProperty().bind(mainStackPane.heightProperty());
    	
    	mapSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((oldItem,newItem,obs) -> {
    		
    		Integer defaultNumPlayers = 0;
    		
    		switch(mapSizeChoiceBox.getSelectionModel().getSelectedItem()){
				case HUGE:
					defaultNumPlayers = 12;
					break;
				case LARGE:
					defaultNumPlayers = 10;
					break;
				case STANDARD:
					defaultNumPlayers = 8;
					break;
				case SMALL:
					defaultNumPlayers = 6;
					break;
    		}
    		
			FrontierModel.getInstance().BWIDTH = mapSizeChoiceBox.getSelectionModel().getSelectedItem().getX();
			FrontierModel.getInstance().BHEIGHT = mapSizeChoiceBox.getSelectionModel().getSelectedItem().getY();
    		
    		if(civilizationVBox.getChildren().size() < defaultNumPlayers){
    			Integer difference = defaultNumPlayers - civilizationVBox.getChildren().size();
    			
    			while(difference > 0){
    				addPlayer();
    				difference--;
    			}
    		} else if(civilizationVBox.getChildren().size() > defaultNumPlayers){
    			Integer difference = civilizationVBox.getChildren().size() - defaultNumPlayers;
    			
    			while(difference > 0){
    				subtractPlayer();
    				difference--;
    			}
    		}
    	});
    }

    public void setup(){
    	subtractPlayer();
    	
    	mapTypeChoiceBox.getItems().addAll(MapType.values());
    	mapTypeChoiceBox.getSelectionModel().selectFirst();
    	
    	mapSizeChoiceBox.getItems().addAll(MapSize.values());
    	mapSizeChoiceBox.getSelectionModel().selectFirst();
    	
    	worldAgeChoiceBox.getItems().addAll(WorldAge.values());
    	worldAgeChoiceBox.getSelectionModel().selectFirst();
    }
    
    @FXML
    public void launchGame(ActionEvent event){
    	
    	FrontierModel.getInstance().clearPlayers();
    	
    	FrontierModel.getInstance().resetUniqueCivs();
    	
    	for(CivilizationItemController civ : civList){
    		FrontierModel.getInstance().addPlayer(civ.getSelectedCiv(onlyUniqueCheckBox.isSelected()));
    	}
    	
    	frontierMainMenuController.switchScene(DropoutFrontierScene.FRONTIERGAME);
    }

	public FrontierMainMenuController getFrontierMainMenuController() {
		return frontierMainMenuController;
	}

	public void setFrontierMainMenuController(FrontierMainMenuController frontierMainMenuController) {
		this.frontierMainMenuController = frontierMainMenuController;
	}
}
