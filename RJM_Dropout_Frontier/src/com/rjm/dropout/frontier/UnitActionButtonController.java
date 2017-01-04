package com.rjm.dropout.frontier;

import java.io.IOException;

import com.rjm.dropout.frontier.enums.UnitButtonType;
import com.rjm.dropout.frontier.enums.UnitClass;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.IUnit;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UnitActionButtonController {
	
    @FXML // fx:id="icon"
    private ImageView icon; // Value injected by FXMLLoader

    @FXML // fx:id="actionButton"
    private Button actionButton; // Value injected by FXMLLoader
    
    @FXML // fx:id="tooltip"
    private Tooltip tooltip; // Value injected by FXMLLoader
    
    UnitButtonType buttonType;

    @FXML
    void buttonPressed(ActionEvent event) {
    	switch (buttonType) {
    	case ALERT:
			break;
		case ATTACK:
			break;
		case AUTOEXPLORE:
			break;
		case FORTIFY:
			break;
		case FOUNDCITY:
	    	IUnit selectedSettler = FrontierModel.getInstance().getSelectedUnit();
	    	if(selectedSettler.getUnitClass() == UnitClass.SETTLER){
		    	FrontierModel.getInstance().spawnCity(selectedSettler.getOwner());
	    		FrontierModel.getInstance().deleteUnit(selectedSettler);
	    	}
	    	break;
		case FOUNDCHURCH:
	    	IUnit selectedProphet = FrontierModel.getInstance().getSelectedUnit();
	    	if(selectedProphet.getUnitClass() == UnitClass.PROPHET){
		    	FrontierModel.getInstance().spawnChurch(selectedProphet.getOwner());
		    	FrontierModel.getInstance().deleteUnit(selectedProphet);
	    	}
	    	break;
		case MOVETO:
			break;
		case PATROL:
			break;
		case PILLAGE:
			break;
		case PILLAGEROAD:
			break;
		case RANGEDATTACK:
			break;
		case RANKUP:
			break;
		case SKIPTURN:
			break;
		case SLEEP:
			break;
		default:
			break;
		}
    }
	
	public static UnitActionButtonController createButton(IUnit unit, UnitButtonType buttonType){
		UnitActionButtonController unitActionButton = null;
		if (unitActionButton == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_UNIT_ACTION_BUTTON));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_UNIT_ACTION_BUTTON));

				unitActionButton = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			unitActionButton.setup(unit,buttonType);
		}
		
		return unitActionButton;
	}

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        
    }
    
    public void setup(IUnit unit, UnitButtonType buttonType){
    	this.buttonType = buttonType; 
    	this.setIcon(buttonType.getIcon());
    	this.tooltip.setText(buttonType.getName());
    	
    	switch(buttonType){
		case ALERT:
			break;
		case ATTACK:
			if(unit.getMovementLeft() <= 0){
				getButton().setDisable(true);
			}
			break;
		case AUTOEXPLORE:
			break;
		case FORTIFY:
			break;
		case FOUNDCITY:
			break;
		case FOUNDCHURCH:
			break;
		case MOVETO:
			if(unit.getMovementLeft() <= 0){
				getButton().setDisable(true);
			}
			break;
		case PATROL:
			break;
		case PILLAGE:
			if(unit.getMovementLeft() < unit.getPillageCost()){
				getButton().setDisable(true);
			}
			break;
		case PILLAGEROAD:
			if(unit.getMovementLeft() < unit.getPillageCost()){
				getButton().setDisable(true);
			}
			break;
		case RANGEDATTACK:
			break;
		case RANKUP:
			break;
		case SKIPTURN:
			break;
		case SLEEP:
			break;
		default:
			break;
    	}
    }
    
    public void setIcon(Image image){
    	icon.setImage(image);
    }
    
    public Button getButton(){
    	return actionButton;
    }
}
