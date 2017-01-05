package com.rjm.dropout.frontier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.main.FrontierMainMenuController;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.IHolding;
import com.rjm.dropout.frontier.objects.IUnit;
import com.rjm.dropout.frontier.objects.PlanetEarth;
import com.rjm.dropout.frontier.objects.Player;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.tasks.ProcessTurnTask;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class SolarSystemModel {

	FrontierMainMenuController frontierMainMenuController;
	
	private SolarSystemController solarSystemController;
	public SolarSystemController getSolarSystemController() {
		return solarSystemController;
	}
	public void setSolarSystemController(SolarSystemController solarSystemController) {
		this.solarSystemController = solarSystemController;
	}

	public SolarSystemModel() {}
	
	private static volatile SolarSystemModel _instance;
	private final static Object _syncObject = new Object();

	public static SolarSystemModel getInstance() {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = new SolarSystemModel();
				}
			}
		}

		return _instance;
	}

	// Map sizes (ratio 2.44 h to w)

	public int BWIDTH = 0;
	public int BHEIGHT = 0;

	//Map Generation Heuristics

	int allTerrainTotal = 0;
	int emptySpaceTotal = 0;
	
	// 3D

	public HashMap<Point, HexView> hexMap;
	
	public HashMap<Point, HexView> resourceMap;

	public HashMap<Point, HexView> emptySpaceMap;
	
	public HashMap<Point, Node> planetMap;
	
	public void processNextTurn(final SolarSystemController solarSystemController){
		ProcessTurnTask task = new ProcessTurnTask();
		task.setOnTaskComplete(() -> {
			Platform.runLater(new Runnable() {
				public void run() {
					solarSystemController.turnComplete();
				}
			});
		});
		
		task.execute();
	}

	public FrontierMainMenuController getFrontierMainMenuController() {
		return frontierMainMenuController;
	}

	public void setFrontierMainMenuController(FrontierMainMenuController frontierMainMenuController) {
		this.frontierMainMenuController = frontierMainMenuController;
	}
	
	public List<Point> collectAdjacentPoints(Point point, int range){
		List<Point> adjPoints = new ArrayList<Point>();
		
		HexView hex = hexMap.getOrDefault(point, null);
		
		if(range != 0){
			if(hex != null){
				for(Point adjPoint : hex.getAdjacentPoints()){
					adjPoints.addAll(collectAdjacentPoints(adjPoint,range-1));
				}
			}
		}
			
		if(hex != null){
			adjPoints.addAll(hex.getAdjacentPoints());
		}
			
		return adjPoints;
	}
	
	private List<Point> collectAdjacentPoints(Point point, int range, Integer distance){
		List<Point> adjPoints = new ArrayList<Point>();
		
		HexView hex = hexMap.getOrDefault(point, null);
		
		if(range != 0){
			if(hex != null){
				for(Point adjPoint : hex.getAdjacentPoints()){
					adjPoints.addAll(collectAdjacentPoints(adjPoint,range-1,new Integer(distance+1)));
				}
			}
		}
			
		if(hex != null){
//			adjPoints.addAll(hex.getAdjacentPoints());
			hex.getAdjacentPoints().forEach(hexPoint -> {
				Point newPoint = new Point(hexPoint);
				newPoint.setDistance(distance);
				
				adjPoints.add(newPoint);
			});
		}
			
		return adjPoints;
	}

	public void resetMaps() {
		
		// Hex View
		
		hexMap = new HashMap<Point, HexView>();
		resourceMap = new HashMap<Point, HexView>();
		
		emptySpaceMap = new HashMap<Point, HexView>();
		
		planetMap = new HashMap<Point, Node>();
	}

	ArrayList<Point> getAdjacentPoints(Point point){
		ArrayList<Point> adjacentPoints = new ArrayList<Point>();
		if(point.x%2 == 0){
			// Top Left
			adjacentPoints.add(new Point(point.x-1, point.y-1));
			// Top Mid
			adjacentPoints.add(new Point(point.x, point.y-1));
			// Top Right
			adjacentPoints.add(new Point(point.x+1, point.y-1));
			// Bottom Right
			adjacentPoints.add(new Point(point.x+1, point.y));
			// Bottom Mid
			adjacentPoints.add(new Point(point.x, point.y+1));
			// Bottom Left
			adjacentPoints.add(new Point(point.x-1, point.y));
		} else {
			// Top Left
			adjacentPoints.add(new Point(point.x-1, point.y));
			// Top Mid
			adjacentPoints.add(new Point(point.x, point.y-1));
			// Top Right
			adjacentPoints.add(new Point(point.x+1, point.y));
			// Bottom Right
			adjacentPoints.add(new Point(point.x+1, point.y+1));
			// Bottom Mid
			adjacentPoints.add(new Point(point.x, point.y+1));
			// Bottom Left
			adjacentPoints.add(new Point(point.x-1, point.y+1));
		}

		/*
		// Prune away points that exist outside the bounds of the map
		for(Point p : adjacentPoints){
			if(p.x < 0 || p.x > BWIDTH-1 || p.y < 0 || p.y > BHEIGHT-1){
				adjacentPoints.remove(p);
			}
		}
		 */

		return new ArrayList<Point>(adjacentPoints);
	}

	public ArrayList<Integer> getTotals(){
		ArrayList<Integer> totals = new ArrayList<Integer>();

		totals.add(emptySpaceTotal);

		return totals;
	}
	
	private IUnit selectedUnit;

	public IUnit getSelectedUnit() {
		return selectedUnit;
	}
	
	List<BorderTile> borders = new ArrayList<BorderTile>();
	
	List<Point> movablePoints = new ArrayList<Point>();
	public void setSelectedUnit(IUnit selectedUnit) {
		
		SolarSystemModel.getInstance().deselectUnit();
		SolarSystemModel.getInstance().deselectCity();
		
		this.selectedUnit = selectedUnit;
		
		movablePoints = collectAdjacentPoints(selectedUnit.getLocation(), selectedUnit.getMovement()-1, 1);
		for(Point point : movablePoints){
			HexView movableHex = hexMap.getOrDefault(point, null);
			
			int movementCost = (movableHex != null && movableHex.getMovementCost() > 1) ? movableHex.getMovementCost() : 0;
			
			int distance = point.getDistance();
			System.out.println("Distance: " + distance);

			int distanceModifier = (distance > 1) ? distance : 0;
			
			int totalCost = movementCost + distanceModifier;
			
			boolean passable = true;
			
			if (movableHex != null) {
				if (movableHex.isImpassable()) {
					passable = false;
				}
				if (movableHex.getTerrain() == Terrain.COAST) {
					if (!selectedUnit.canEmbark()) {
						passable = false;
					}
				} else if (movableHex.getTerrain() == Terrain.OCEAN) {
					if (!selectedUnit.canCrossOcean()) {
						passable = false;
					}
				} 
			}
			if(movableHex != null && passable && totalCost <= selectedUnit.getMovementLeft()){
				BorderTile moveBorder = BorderTile.createBorderTile(movableHex, true, solarSystemController.getExploredGroup());
				borders.add(moveBorder);
			}
		}
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

	private IHolding selectedCity;

	public IHolding getSelectedCity() {
		return selectedCity;
	}

	public void setHoldingCity(IHolding selectedCity) {
		
		SolarSystemModel.getInstance().deselectUnit();
		SolarSystemModel.getInstance().deselectCity();
		
		this.selectedCity = selectedCity;
	}	
	
	public void deselectCity(){
		if (selectedCity != null) {
			selectedCity.deselect();
			selectedCity = null;
		}
	}

	public int distanceBetween(Point origin, Point current){
		int xAway = Math.abs(current.x - origin.x);
		int yAway = Math.abs(current.y - origin.y);
		
		System.out.println("X distance = " + xAway);
		System.out.println("Y distance = " + yAway);
		
		return Math.max(xAway, yAway);
	}

	public int absDistanceBetween(Point origin, Point current){
		
		int currentRange = 0;
		boolean foundPoint = false;		
		while(currentRange < 32 && !foundPoint){
			foundPoint = distanceBetween2(origin, current, currentRange) < 99999;
			currentRange++;
		}
		
//		System.out.println("Radial distance = " + currentRange);
		
		return currentRange;
	}
	
	int distanceBetween2(Point origin, Point destination, int range){

		boolean yIsEven = (origin.y%2 == 0) ? true : false;
		
		int distance = 99999;
		
		if(yIsEven){
			
			for(int i=0;i<range;i++){
				if(destination.x == origin.x){
					if(destination.y == origin.y-i-1){	// N
						distance = i+1;
					}
					if(destination.y == origin.y-i){	// NE
						distance = i+1;
					}
					if(destination.y == origin.y+i){	// SE
						distance = i+1;
					}
					if(destination.y == origin.y+i+1){	// S
						distance = i+1;
					}
				} else if(destination.x == origin.x-i){
					if(destination.y == origin.y+i){	// SW
						distance = i+1;
					}
					if(destination.y == origin.y-i){	// NW
						distance = i+1;
					}
				}
			}
		} else {
			
			for(int i=0;i<range;i++){
				if(destination.x == origin.x){
					if(destination.y == origin.y-i-1){	// N
						distance = i+1;
					}
					if(destination.y == origin.y-i){	// NE
						distance = i+1;
					}
					if(destination.y == origin.y+i){	// SE
						distance = i+1;
					}
					if(destination.y == origin.y+i+1){	// S
						distance = i+1;
					}
				} else if(destination.x == origin.x+i){
					if(destination.y == origin.y+i){	// SW
						distance = i+1;
					}
					if(destination.y == origin.y-i){	// NW
						distance = i+1;
					}
				}
			}
		}
		
		return distance;
	}
	
	public void deselectUnit(){
		if (selectedUnit != null) {
			selectedUnit.deselect();
			selectedUnit = null;
			
			borders.forEach(border -> {
//				FrontierGameController.getInstance().getExploredGroup().getChildren().remove(border.getHexagon());
//				FrontierGameController.getInstance().getExploredGroup().getChildren().removeAll(border.getBorderLines());
			});
			borders.clear();
			movablePoints.clear();
		}
	}

	public void moveUnitTo(HexView hex) {
		if (getSelectedUnit() != null && movablePoints.contains(hex.getPoint())) {
			getSelectedUnit().relocate(hex);
			
			int movementCost = hex.getMovementCost();
			
			Point pointToMoveTo = hex.getPoint();
			for(Point point : movablePoints){
				if(point.equals(hex.getPoint())){
					pointToMoveTo = point;
				}
			}
			
			int distanceModifier = (pointToMoveTo.getDistance() > 1) ? pointToMoveTo.getDistance() : 0;
			
			System.out.println("Movement Cost: " + movementCost + ", Distance Modifier: " + distanceModifier);
			
			getSelectedUnit().decreaseMovement(movementCost+distanceModifier);
			System.out.println("Moves left: " + getMovementLeft());
		}
	}

	private int getMovementLeft() {
		IUnit unit = getSelectedUnit();
		
		deselectUnit();
		
		if(unit.getMovementLeft() > 0){
			unit.select();
			return unit.getMovementLeft();
		}
		
		return 0;
	}
	
	public List<Button> getUnitButtons(IUnit unit){
		List<UnitActionButtonController> buttonControllers = unit.getUnitClass().getButtonTypes().stream()
				.map(buttonType -> UnitActionButtonController.createButton(unit,buttonType))
				.collect(Collectors.toList());
		
		List<Button> buttons = buttonControllers.stream()
				.map(buttonController -> buttonController.getButton())
				.collect(Collectors.toList());
		
		return buttons;
	}

	public void deleteUnit(IUnit selectedUnit) {
		selectedUnit.getOwner().getUnits().remove(selectedUnit);
		deselectUnit();
		selectedUnit.delete();
	}
	
	public void updatePlayerVisibility(Player player){
		
		player.getUnits().forEach(unit -> {
			if(unit != this.getSelectedUnit()){
				unit.updateVisibleHexs();
			}
		});
		player.getHoldings().forEach(holding -> {
			holding.updateVisibleHexs();
		});
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
	
	public void updatePlayerTileYields(Player player){
		
		getSolarSystemController().addGoldToTreasury();
		
		double tileGold = 0;
		double tileScience = 0;
		
		double holdingGold = 0;
		double holdingScience = 0;
		
		for(IHolding holding : player.getHoldings()){
			for(BorderTile border : holding.getBorders()){
				HexView tile = border.getHexView();

//				double singleTileFood = tile.getFoodYield();
				double singleTileGold = tile.getGoldYield();
				double singleTileScience = tile.getScienceYield();

//				XXX: Turned off tile marking
//				tile.clearMarks();
//				
//				markTile(tile, new String(tileGold + ", "), Color.web("#d0aa00"), 18);
//				markTile(tile, new String(tileScience + ", "), Color.web("#00da78"), 18);
				
				tileGold += singleTileGold;
				tileScience += singleTileScience;
			}

			holdingGold += holding.getGoldYield();
			holdingScience += holding.getScienceYield();
		}
		
		System.out.println("Holdings: Gold = " + holdingGold + ", Science = " + holdingScience);

		getSolarSystemController().addGoldAsset(holdingGold, "from Holdings");
		getSolarSystemController().addScienceAsset(holdingScience, "from Holdings");

		System.out.println("Tiles: Gold = " + tileGold + ", Science = " + tileScience);
		
		getSolarSystemController().addGoldAsset(tileGold, "from Tile Yields");
		getSolarSystemController().addScienceAsset(tileScience, "from Tile Yields");
	}
}
