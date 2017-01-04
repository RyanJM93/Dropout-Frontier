package com.rjm.dropout.frontier.utilities;

import java.io.IOException;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.enums.CivilianUnitType;
import com.rjm.dropout.frontier.enums.CombatUnitType;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.Player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;

public class DebugContextMenuController {

    @FXML // fx:id="debugContextMenu"
    private ContextMenu debugContextMenu; // Value injected by FXMLLoader
    public ContextMenu getContextMenu(){
    	return debugContextMenu;
    }

    @FXML // fx:id="playerChoiceBox"
    private ChoiceBox<Player> playerChoiceBox; // Value injected by FXMLLoader
    public Player getSelectedPlayer(){
    	return playerChoiceBox.getSelectionModel().getSelectedItem();
    }
	
	public static DebugContextMenuController createDebugContextMenu(){
		DebugContextMenuController debugContextMenu = null;
		if (debugContextMenu == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						DebugContextMenuController.class.getClassLoader().getResource(FXMLUtils.FXML_DEBUG_CONTEXT_MENU));

				fxmlLoader.load(DebugContextMenuController.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_DEBUG_CONTEXT_MENU));

				debugContextMenu = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			debugContextMenu.populatePlayerChoiceBox();
		}
		
		return debugContextMenu;
	}
	
	private void populatePlayerChoiceBox() {
		playerChoiceBox.getItems().addAll(FrontierModel.getInstance().getPlayers());
		playerChoiceBox.getSelectionModel().select(FrontierModel.getInstance().getFrontierGameController().getPlayer());
	}

	private HexView focusedHexView;
	protected void setFocusedHexView(HexView hex){
		this.focusedHexView = hex;
	}
	protected HexView getFocusedHexView(){
		return focusedHexView;
	}
	
	private static volatile DebugContextMenuController _instance;
	private final static Object _syncObject = new Object();

	public static DebugContextMenuController getInstance(HexView hex) {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = createDebugContextMenu();
				}
			}
		}
		
		_instance.setFocusedHexView(hex);

		return _instance;
	}

    @FXML
    void spawnChurch(ActionEvent event) {

    }

    @FXML
    void spawnCity(ActionEvent event) {

    }

    @FXML
    void spawnProphet(ActionEvent event) {
    	FrontierModel.getInstance().spawnCivilianUnit(
    			focusedHexView, 
    			playerChoiceBox.getSelectionModel().getSelectedItem(), 
    			CivilianUnitType.PROPHET
    	);
    }

    @FXML
    void spawnSettler(ActionEvent event) {
    	FrontierModel.getInstance().spawnCivilianUnit(
    			focusedHexView, 
    			playerChoiceBox.getSelectionModel().getSelectedItem(), 
    			CivilianUnitType.SETTLER
    	);
    }

    @FXML
    void spawnWarrior(ActionEvent event) {
    	FrontierModel.getInstance().spawnCombatUnit(
    			focusedHexView, 
    			playerChoiceBox.getSelectionModel().getSelectedItem(), 
    			CombatUnitType.WARRIOR
    	);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
    }
}
