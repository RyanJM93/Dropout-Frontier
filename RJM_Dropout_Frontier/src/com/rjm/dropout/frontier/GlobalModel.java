package com.rjm.dropout.frontier;

import com.rjm.dropout.frontier.main.FrontierMainMenuController;
import com.rjm.dropout.frontier.objects.HexView;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GlobalModel {

	private Stage stage;
	public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	FrontierMainMenuController frontierMainMenuController;
	
	public SimpleIntegerProperty turn = new SimpleIntegerProperty(1);
	public SimpleIntegerProperty turnProperty(){
		return turn;
	}
	public Integer getTurn(){
		return turn.get();
	}
	
	public GlobalModel() {}
	
	private static volatile GlobalModel _instance;
	private final static Object _syncObject = new Object();

	public static GlobalModel getInstance() {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = new GlobalModel();
				}
			}
		}

		return _instance;
	}

	public FrontierMainMenuController getFrontierMainMenuController() {
		return frontierMainMenuController;
	}

	public void setFrontierMainMenuController(FrontierMainMenuController frontierMainMenuController) {
		this.frontierMainMenuController = frontierMainMenuController;
	}
	
	public Paint contestedFill(Color territoryColor, Color territoryColor2) {
		
		double lStartX = 0.1f;
		double lStartY = 0.1f;
		
		double lEndX = 0.9f;
		double lEndY = 0.9f;
		
		LinearGradient lg = new LinearGradient(
				lStartX, lStartY, 
				lEndX, lEndY, 
				true, 
				CycleMethod.NO_CYCLE,
				new Stop(0, territoryColor), 
				new Stop(0.1, Color.TRANSPARENT),
				new Stop(0.2, territoryColor2),
				new Stop(0.3, Color.TRANSPARENT),
				new Stop(0.4, territoryColor), 
				new Stop(0.5, Color.TRANSPARENT),
				new Stop(0.6, territoryColor2),
				new Stop(0.7, Color.TRANSPARENT),
				new Stop(0.8, territoryColor), 
				new Stop(0.9, Color.TRANSPARENT),
				new Stop(1.0, territoryColor2));
		
		return lg;
	}
	
	public Paint contestedFill(Color territoryColor, Color territoryColor2, Color territoryColor3) {
		
		double lStartX = 0.1f;
		double lStartY = 0.1f;
		
		double lEndX = 0.9f;
		double lEndY = 0.9f;
		
		LinearGradient lg = new LinearGradient(
				lStartX, lStartY, 
				lEndX, lEndY, 
				true, 
				CycleMethod.NO_CYCLE,
				new Stop(0, territoryColor), 
				new Stop(0.1, Color.TRANSPARENT),
				new Stop(0.2, territoryColor2),
				new Stop(0.3, Color.TRANSPARENT),
				new Stop(0.4, territoryColor3), 
				new Stop(0.5, Color.TRANSPARENT),
				new Stop(0.6, territoryColor2),
				new Stop(0.7, Color.TRANSPARENT),
				new Stop(0.8, territoryColor), 
				new Stop(0.9, Color.TRANSPARENT),
				new Stop(1.0, territoryColor2));
		
		return lg;
	}
	
	public String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
	
	public void markTile(HexView tile, String string, Color color, int size){
		
		Text text = new Text(string);
		text.setFont(Font.font("Calibri",FontPosture.ITALIC, size));
		text.setOpacity(0.6);
		text.setFill(color);
		
		tile.addMark(text);
	}

	public Point3D findPoint(double radius, double angleX, double angleY, double latitude){

		double radianX = angleX * (Math.PI / 180);
		double radianY = angleY * (Math.PI / 180);

		Double x = radius * Math.cos(radianX) * Math.sin(radianY);
		Double y = radius * Math.sin(radianX) * Math.sin(radianY);
		Double z = radius * Math.cos(radianY);

		y = new Double(y + latitude);

		return new Point3D(x,y,z);
	}
}
