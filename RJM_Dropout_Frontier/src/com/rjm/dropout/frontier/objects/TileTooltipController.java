package com.rjm.dropout.frontier.objects;

import java.io.IOException;

import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TileTooltipController {

    @FXML // fx:id="tileTooltip"
    private Tooltip tileTooltip; // Value injected by FXMLLoader

    @FXML // fx:id="tileTooltipVBox"
    private VBox tileTooltipVBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="terrainLabel"
    private Label terrainLabel; // Value injected by FXMLLoader
    
    private HexView hexTile;

    public static TileTooltipController createTileTooltip(HexView hex){
    	TileTooltipController tooltip = null;
		if (tooltip == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_TILE_TOOLTIP));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_TILE_TOOLTIP));

				tooltip = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tooltip.hexTile = hex;
		hex.setTooltip(tooltip);
		
		Tooltip.install(hex, tooltip.getTileTooltip());
		
//		tooltip.getTileTooltip().setX(me.getSceneX());
//		tooltip.getTileTooltip().setY(me.getSceneY());
		
		return tooltip;
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	
    }
    
    public void update(){
    	String terrain = hexTile.getTerrain().name().toLowerCase();
    	String caseCorrected = terrain.substring(0, 1).toUpperCase() + terrain.substring(1);
    	terrainLabel.setText(caseCorrected);
    	
    	getYields();
    }

	public Tooltip getTileTooltip() {
		return tileTooltip;
	}

	public void setTileTooltip(Tooltip tileTooltip) {
		this.tileTooltip = tileTooltip;
	}
	
	private void clearYields(){
		if(foodYield != null){
			tileTooltipVBox.getChildren().remove(foodYield);
		}
		if(goldYield != null){
			tileTooltipVBox.getChildren().remove(goldYield);
		}
		if(scienceYield != null){
			tileTooltipVBox.getChildren().remove(scienceYield);
		}
	}

	Label foodYield;
	Label goldYield;
	Label scienceYield;
	private void getYields(){
		clearYields();
		
		foodYield = createFoodYield();
		if(foodYield != null){
			tileTooltipVBox.getChildren().add(foodYield);
		}
		
		goldYield = createGoldYield();
		if(goldYield != null){
			tileTooltipVBox.getChildren().add(goldYield);
		}
		
		scienceYield = createScienceYield();
		if(scienceYield != null){
			tileTooltipVBox.getChildren().add(scienceYield);
		}
		
	}
	
	private Label createFoodYield(){
		double foodYield = hexTile.getFoodYield();
		
		Label foodYieldLabel = null;
		if (foodYield > 0) {
			foodYieldLabel = new Label(new Double(foodYield).toString());
			foodYieldLabel.setTextFill(Color.web("#d0aa00"));
			foodYieldLabel.setContentDisplay(ContentDisplay.LEFT);
			ImageView graphic = new ImageView(new Image("images/frontier/foodIcon.png"));
			graphic.setFitWidth(16);
			graphic.setFitHeight(16);
			foodYieldLabel.setGraphic(graphic);
		}
		
		return foodYieldLabel;
	}
	
	private Label createGoldYield(){
		double goldYield = hexTile.getGoldYield();
		
		Label goldYieldLabel = null;
		if (goldYield > 0) {
			goldYieldLabel = new Label(new Double(goldYield).toString());
			goldYieldLabel.setTextFill(Color.web("#008000"));
			goldYieldLabel.setContentDisplay(ContentDisplay.LEFT);
			ImageView graphic = new ImageView(new Image("images/frontier/goldIcon.png"));
			graphic.setFitWidth(16);
			graphic.setFitHeight(16);
			goldYieldLabel.setGraphic(graphic);
		}
		
		return goldYieldLabel;
	}
	
	private Label createScienceYield(){
		double scienceYield = hexTile.getScienceYield();
		
		Label scienceYieldLabel = null;
		if (scienceYield > 0) {
			scienceYieldLabel = new Label(new Double(scienceYield).toString());
			scienceYieldLabel.setTextFill(Color.web("#00da78"));
			scienceYieldLabel.setContentDisplay(ContentDisplay.LEFT);
			ImageView graphic = new ImageView(new Image("images/frontier/scienceIcon.png"));
			graphic.setFitWidth(16);
			graphic.setFitHeight(16);
			scienceYieldLabel.setGraphic(graphic);
		}
		
		return scienceYieldLabel;
	}
}