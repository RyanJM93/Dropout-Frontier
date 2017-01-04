package com.rjm.dropout.frontier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.rjm.dropout.frontier.enums.CivilianUnitType;
import com.rjm.dropout.frontier.enums.Civilization;
import com.rjm.dropout.frontier.enums.CombatUnitType;
import com.rjm.dropout.frontier.enums.Elevation;
import com.rjm.dropout.frontier.enums.Leader;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.main.FrontierMainMenuController;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.Church;
import com.rjm.dropout.frontier.objects.City;
import com.rjm.dropout.frontier.objects.CivLeaderPair;
import com.rjm.dropout.frontier.objects.CountyTile;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.ICivilization;
import com.rjm.dropout.frontier.objects.IHolding;
import com.rjm.dropout.frontier.objects.ILeader;
import com.rjm.dropout.frontier.objects.IUnit;
import com.rjm.dropout.frontier.objects.Player;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.objects.UnitCivilian;
import com.rjm.dropout.frontier.objects.UnitCombat;
import com.rjm.dropout.frontier.tasks.ProcessTurnTask;
import com.rjm.dropout.frontier.utilities.FXMLUtils;
import com.rjm.dropout.frontier.utilities.XMLParser;
import com.rjm.dropout.frontier.utilities.XMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class FrontierModel {

	FrontierMainMenuController frontierMainMenuController;
	
	private FrontierGameController frontierGameController;
	public FrontierGameController getFrontierGameController() {
		return frontierGameController;
	}
	public void setFrontierGameController(FrontierGameController frontierGameController) {
		this.frontierGameController = frontierGameController;
	}

	public FrontierModel() {}
	
	private static volatile FrontierModel _instance;
	private final static Object _syncObject = new Object();

	public static FrontierModel getInstance() {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = new FrontierModel();
					_instance.resetMaps();
				}
			}
		}

		return _instance;
	}

	// Map sizes (ratio 2.44 h to w)

	public int BWIDTH = 0;
	public int BHEIGHT = 0;

	//Map Generation Heuristics
	public int BIOMEWEIGHT = BWIDTH - BHEIGHT;
	public int MOUNTAINCHANCE = 50;

	int allTerrainTotal = 0;
	int coastTotal = 0;
	int desertTotal = 0;
	int forestTotal = 0;
	int freshwaterTotal = 0;
	int grasslandTotal = 0;
	int jungleTotal = 0;
	int marshTotal = 0;
	int oceanTotal = 0;
	int savannahTotal = 0;
	int snowTotal = 0;
	int taigaTotal = 0;
	int tundraTotal = 0;
	
	// 3D

	public HashMap<Point, HexView> hexMap2;
	
	public HashMap<Point, HexView> resourceMap2;

	public HashMap<Point, HexView> coastMap2;
	public HashMap<Point, HexView> desertMap2;
	public HashMap<Point, HexView> forestMap2;
	public HashMap<Point, HexView> freshwaterMap2;
	public HashMap<Point, HexView> grasslandMap2;
	public HashMap<Point, HexView> jungleMap2;
	public HashMap<Point, HexView> marshMap2;
	public HashMap<Point, HexView> oceanMap2;
	public HashMap<Point, HexView> savannahMap2;
	public HashMap<Point, HexView> snowMap2;
	public HashMap<Point, HexView> taigaMap2;
	public HashMap<Point, HexView> tundraMap2;
	
	public HashMap<Point, HexView> desertSpawnMap2;
	public HashMap<Point, HexView> grasslandSpawnMap2;
	public HashMap<Point, HexView> forestSpawnMap2;
	public HashMap<Point, HexView> jungleSpawnMap2;
	public HashMap<Point, HexView> marshSpawnMap2;
	public HashMap<Point, HexView> savannahSpawnMap2;
	public HashMap<Point, HexView> snowSpawnMap2;
	public HashMap<Point, HexView> taigaSpawnMap2;
	public HashMap<Point, HexView> tundraSpawnMap2;
	
	public HashMap<Terrain, HashMap<Point,HexView>> terrainToSpawnMap;

	public HashMap<Point, HexView> icecapMap2;
	
	List<Player> players = new ArrayList<>();
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public void addPlayer(Pair<ICivilization,ILeader> player){
		players.add(new Player(player));
	}
	
	public void clearPlayers(){
		players.clear();
	}
	
	public List<Player> placePlayers(){
		
		System.out.println("Placing " + players.size() + " players");
		
		for(Player player : getPlayers()){
			
			boolean isGood = false;
			int attempts = 0;
			
			while (!isGood && attempts < 10) {
				List<Terrain> startTerrain = player.getCivilization().getStartTerrain();
				
				HashMap<Point,HexView> spawnMap = new HashMap<Point,HexView>();
				
				for(Terrain terrain : startTerrain) {
					HashMap<Point,HexView> terrainMap = new HashMap<Point,HexView>(terrainToSpawnMap.get(terrain));
					spawnMap.putAll(terrainMap);
				}
				
				isGood = startPoint(spawnMap, player);
				
				attempts++;
			}
			
			if(attempts >= 10){
				System.out.println("----------------Regenerating Planet Map----------------");
				FrontierGameController.createNewPlanetMap(false);
			}
		}
		
		return players;
	}
	
	public void processNextTurn(final FrontierGameController frontierGameController){
		ProcessTurnTask task = new ProcessTurnTask();
		task.setOnTaskComplete(() -> {
			Platform.runLater(new Runnable() {
				public void run() {
					frontierGameController.turnComplete();
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
	
	private List<Point> collectAdjacentPoints(Point point, int range){
		List<Point> adjPoints = new ArrayList<Point>();
		
		HexView hex = hexMap2.getOrDefault(point, null);
		
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
		
		HexView hex = hexMap2.getOrDefault(point, null);
		
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
	
	private boolean startPoint(HashMap<Point,HexView> choiceMap, Player player){
		Random rand = new Random();
		List<Point> possiblePoints = new ArrayList<Point>(choiceMap.keySet());
		if(possiblePoints.isEmpty()){
			return false;
		}
		int listSize = possiblePoints.size(); System.out.println("ListSize: " + listSize);
		int pointIndex = rand.nextInt(listSize); System.out.println("PointIndex: " + pointIndex);
		Point randomPoint = possiblePoints.get(pointIndex);
		HexView startTile = choiceMap.get(randomPoint);
		startTile.setOwner(player);
		
		startTile.exploreTiles(1);
		
		// Remove area surrounding from spawnlist
		
		List<Point> pointsToRemove = collectAdjacentPoints(randomPoint, 2);
		pointsToRemove.add(randomPoint);
		
		pointsToRemove.forEach(point -> {
			HexView hex = hexMap2.get(point);
			
			if (hex != null) {
				HashMap<Point, HexView> spawnMap = terrainToSpawnMap.getOrDefault(hex.getTerrain(), null);
				if (spawnMap != null && spawnMap.containsKey(point)) {
					spawnMap.remove(point);
				} 
			}
		});
		
		placeStartingUnits(startTile);
		startTile.setOwner(null);
		
		return true;
	}

	private void placeStartingUnits(HexView startTile) {
		
		// Place Settler //TODO currently Warrior
//		spawnCombatUnit(startTile, startTile.getOwner(), CombatUnitType.WARRIOR).getSymbol().setText("S");
		spawnCivilianUnit(startTile, startTile.getOwner(), CivilianUnitType.SETTLER);	
		
		List<Point> adjPoints = new ArrayList<Point>(startTile.getAdjacentPoints());
		
		Point warriorPoint = null;
		findWarriorStart:
		for(Point point : adjPoints) {
			HexView adjHex = hexMap2.getOrDefault(point,null);
			
			boolean isPassable = true;
			
			if(adjHex != null){
				switch (adjHex.getTerrain()) {
				case COAST:
					isPassable = false;
					break;
				case ICECAP:
					isPassable = false;
					break;
				case OCEAN:
					isPassable = false;
					break;
				default:
					if(adjHex.getElevation() != Elevation.mountain){
						isPassable = true;
					} else {
						isPassable = false;
					}
					break;
				}
				
				if(isPassable){
					spawnCombatUnit(adjHex, startTile.getOwner(), CombatUnitType.WARRIOR);
					warriorPoint = point;
					break findWarriorStart;
				}
			}
		}
		if(warriorPoint != null){
			adjPoints.remove(warriorPoint);
		}
		
		findProphetStart:			// TODO Temporary for debug
		for(Point point : adjPoints) {
			HexView adjHex = hexMap2.getOrDefault(point,null);
			
			boolean isPassable = true;
			
			if(adjHex != null){
				switch (adjHex.getTerrain()) {
				case COAST:
					isPassable = false;
					break;
				case ICECAP:
					isPassable = false;
					break;
				case OCEAN:
					isPassable = false;
					break;
				default:
					if(adjHex.getElevation() != Elevation.mountain){
						isPassable = true;
					} else {
						isPassable = false;
					}
					break;
				}
				
				if(isPassable){
					spawnCivilianUnit(adjHex, startTile.getOwner(), CivilianUnitType.PROPHET);	
					break findProphetStart;
				}
			}
		}
	}

	public void resetMaps() {
		
		// Hex View
		
		hexMap2 = new HashMap<Point, HexView>();
		resourceMap2 = new HashMap<Point, HexView>();
		
		coastMap2 = new HashMap<Point, HexView>();
		oceanMap2 = new HashMap<Point, HexView>();
		freshwaterMap2 = new HashMap<Point, HexView>();
		grasslandMap2 = new HashMap<Point, HexView>();
		forestMap2 = new HashMap<Point, HexView>();
		taigaMap2 = new HashMap<Point, HexView>();
		jungleMap2 = new HashMap<Point, HexView>();
		savannahMap2 = new HashMap<Point, HexView>();
		desertMap2 = new HashMap<Point, HexView>();
		tundraMap2 = new HashMap<Point, HexView>();
		snowMap2 = new HashMap<Point, HexView>();
		marshMap2 = new HashMap<Point, HexView>();

		icecapMap2 = new HashMap<Point, HexView>();
	}

	public void createSpawnMaps() {
		
		// HexView
		
		desertSpawnMap2 = new HashMap<Point, HexView>();
		desertSpawnMap2.putAll(desertMap2);
		System.out.println("Desert Spawn Map Size = " + desertSpawnMap2.keySet().size());
		grasslandSpawnMap2 = new HashMap<Point, HexView>();
		grasslandSpawnMap2.putAll(grasslandMap2);
		System.out.println("Grassland Spawn Map Size = " + grasslandSpawnMap2.keySet().size());
		forestSpawnMap2 = new HashMap<Point, HexView>();
		forestSpawnMap2.putAll(forestMap2);
		System.out.println("Forest Spawn Map Size = " + forestSpawnMap2.keySet().size());
		jungleSpawnMap2 = new HashMap<Point, HexView>();
		jungleSpawnMap2.putAll(jungleMap2);
		System.out.println("Jungle Spawn Map Size = " + jungleSpawnMap2.keySet().size());
		marshSpawnMap2 = new HashMap<Point, HexView>();
		marshSpawnMap2.putAll(marshMap2);
		System.out.println("Marsh Spawn Map Size = " + marshSpawnMap2.keySet().size());
		savannahSpawnMap2 = new HashMap<Point, HexView>();
		savannahSpawnMap2.putAll(savannahMap2);
		System.out.println("Savannah Spawn Map Size = " + savannahSpawnMap2.keySet().size());
		snowSpawnMap2 = new HashMap<Point, HexView>();
		snowSpawnMap2.putAll(snowMap2);
		System.out.println("Snow Spawn Map Size = " + snowSpawnMap2.keySet().size());
		taigaSpawnMap2 = new HashMap<Point, HexView>();
		taigaSpawnMap2.putAll(taigaMap2);
		System.out.println("Taiga Spawn Map Size = " + taigaSpawnMap2.keySet().size());
		tundraSpawnMap2 = new HashMap<Point, HexView>();
		tundraSpawnMap2.putAll(tundraMap2);
		System.out.println("Tundra Spawn Map Size = " + tundraSpawnMap2.keySet().size());
		
		terrainToSpawnMap = new HashMap<Terrain,HashMap<Point, HexView>>();
		terrainToSpawnMap.put(Terrain.DESERT, desertSpawnMap2);
		terrainToSpawnMap.put(Terrain.GRASSLAND, grasslandSpawnMap2);
		terrainToSpawnMap.put(Terrain.FOREST, forestSpawnMap2);
		terrainToSpawnMap.put(Terrain.JUNGLE, jungleSpawnMap2);
		terrainToSpawnMap.put(Terrain.MARSH, marshSpawnMap2);
		terrainToSpawnMap.put(Terrain.SAVANNAH, savannahSpawnMap2);
		terrainToSpawnMap.put(Terrain.SNOW, snowSpawnMap2);
		terrainToSpawnMap.put(Terrain.TAIGA, taigaSpawnMap2);
		terrainToSpawnMap.put(Terrain.TUNDRA, tundraSpawnMap2);
	}

	public void clearBoard(String[][] board){
		for(int i=0; i<BHEIGHT; i++){
			for(int j=0; j<BWIDTH; j++){
				board[i][j] = "";
			}
		}
	}

	int distanceFromOrigin(Point origin, Point current){
		int xAway = Math.abs(current.x - origin.x);
		int yAway = Math.abs(current.y - origin.y);

		if(xAway > yAway){
			return xAway;
		} else {
			return yAway;
		}
	}

	int distanceFromPole(Point current){
		Point pole = new Point(-1,-1);
		pole.x = current.x;
		if(current.x < (BHEIGHT - 1)){
			pole.y = 0;
		} else {
			pole.y = BHEIGHT - 1;			
		}

		int distance = distanceFromOrigin(pole, current);

		return distance;
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

	public void findCoastlines3D(){

		ArrayList<HashMap<Point, HexView>> terrainMapList = new ArrayList<HashMap<Point, HexView>>();

		terrainMapList.add(desertMap2);
		terrainMapList.add(forestMap2);
		terrainMapList.add(grasslandMap2);
		terrainMapList.add(jungleMap2);
		terrainMapList.add(marshMap2);
		terrainMapList.add(savannahMap2);
		terrainMapList.add(taigaMap2);
		terrainMapList.add(tundraMap2);

		for(HashMap<Point, HexView> map : terrainMapList){
			for(Point point : map.keySet()){
				List<Point> adjacentPoints = getAdjacentPoints(point);
				for(Point p : adjacentPoints){
					if(oceanMap2.containsKey(p)){
						HexView coastTile = oceanMap2.get(p);
						coastTile.setTerrain(Terrain.COAST);
						coastMap2.put(p, coastTile);
						oceanMap2.remove(p);
					}
				}			
			}
		}
		//		panel.repaint();
	}

	public void widenCoastlines3D(){

		HashMap<Point, HexView> newCoastMap = new HashMap<Point, HexView>();
		newCoastMap.putAll(coastMap2);

		for(Point point : coastMap2.keySet()){
			Random widenChanceGen = new Random();
			int widenChance = widenChanceGen.nextInt(5);
			if(widenChance <= 1){
				List<Point> adjacentPoints = getAdjacentPoints(point);
				for(Point p : adjacentPoints){
					if(oceanMap2.containsKey(p)){
						HexView coastTile = oceanMap2.get(p);
						coastTile.setTerrain(Terrain.COAST);
						newCoastMap.put(p, coastTile);
						oceanMap2.remove(p);
					}
				}
			}
		}

		coastMap2 = newCoastMap;
	}
	
	public void placeIceCaps3D(){
		Random rand = new Random();
		
		Set<Point> caps = hexMap2.keySet().stream()
				.filter(point -> (point.y == 0 || point.y == BHEIGHT-1 
					|| ((point.y == 1 || point.y == BHEIGHT-2) && (rand.nextInt(5) == 1))))
				.collect(Collectors.toSet());

		for(Point point : caps){
			List<Point> adjacentPoints = getAdjacentPoints(point);
			adjacentPoints.add(point);
			for(Point p : adjacentPoints){
				if(snowMap2.containsKey(p)){
					HexView tile = snowMap2.get(p);
					tile.setTerrain(Terrain.ICECAP);
					icecapMap2.put(p, tile);
					snowMap2.remove(p);
				} else if(oceanMap2.containsKey(p)){
					HexView tile = oceanMap2.get(p);
					tile.setTerrain(Terrain.ICECAP);
					icecapMap2.put(p, tile);
					oceanMap2.remove(p);
				} else if(tundraMap2.containsKey(p)){
					HexView tile = tundraMap2.get(p);
					tile.setTerrain(Terrain.ICECAP);
					icecapMap2.put(p, tile);
					tundraMap2.remove(p);
				} else if(coastMap2.containsKey(p)){
					HexView tile = coastMap2.get(p);
					tile.setTerrain(Terrain.ICECAP);
					icecapMap2.put(p, tile);
					coastMap2.remove(p);
				}
			}
		}
	}

	public void findMountains3D(){

		ArrayList<HashMap<Point, HexView>> terrainMapList = new ArrayList<HashMap<Point, HexView>>();

		terrainMapList.add(desertMap2);
		terrainMapList.add(forestMap2);
		terrainMapList.add(grasslandMap2);
		terrainMapList.add(jungleMap2);
		terrainMapList.add(savannahMap2);
		terrainMapList.add(taigaMap2);

		for(HashMap<Point, HexView> map : terrainMapList){
			for(Point point : map.keySet()){
				List<Point> adjacentPoints = getAdjacentPoints(point);
				for(Point p : adjacentPoints){
					if(desertMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							desertMap2.get(p).setElevation(Elevation.mountain);
							if(desertSpawnMap2.containsKey(p)){
								desertSpawnMap2.remove(p);
							}
						}
					} else if(forestMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							forestMap2.get(p).setElevation(Elevation.mountain);
							if(forestSpawnMap2.containsKey(p)){
								forestSpawnMap2.remove(p);
							}
						}
					} else if(grasslandMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							grasslandMap2.get(p).setElevation(Elevation.mountain);
							if(grasslandSpawnMap2.containsKey(p)){
								grasslandSpawnMap2.remove(p);
							}
						}
					} else if(jungleMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							jungleMap2.get(p).setElevation(Elevation.mountain);
							if(jungleSpawnMap2.containsKey(p)){
								jungleSpawnMap2.remove(p);
							}
						}
					} else if(savannahMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							savannahMap2.get(p).setElevation(Elevation.mountain);
							if(savannahSpawnMap2.containsKey(p)){
								savannahSpawnMap2.remove(p);
							}
						}
					} else if(taigaMap2.containsKey(p)){
						Random rand = new Random();
						int mountainChance = rand.nextInt(MOUNTAINCHANCE);
						if(mountainChance <= 1){
							taigaMap2.get(p).setElevation(Elevation.mountain);
							if(taigaSpawnMap2.containsKey(p)){
								taigaSpawnMap2.remove(p);
							}
						}
					}
				}			
			}
		}
		//		panel.repaint();
	}

	public ArrayList<Integer> getTotals(){
		ArrayList<Integer> totals = new ArrayList<Integer>();

		totals.add(coastTotal);
		totals.add(desertTotal);
		totals.add(forestTotal);
		totals.add(freshwaterTotal);
		totals.add(grasslandTotal);
		totals.add(jungleTotal);
		totals.add(marshTotal);
		totals.add(oceanTotal);
		totals.add(savannahTotal);
		totals.add(snowTotal);
		totals.add(taigaTotal);
		totals.add(tundraTotal);

		return totals;
	}
	
	public UnitCombat spawnCombatUnit(HexView hex, Player owner, CombatUnitType type){
		
		Image icon = type.getIcon();
		
		UnitCombat unit = null;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource(FXMLUtils.FXML_UNIT_COMBAT));

			fxmlLoader.load(getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_UNIT_COMBAT));

			unit = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		unit.getIcon().setImage(icon);
		
		unit.setup(owner);
		unit.relocate(hex);
		unit.setUnitClass(type.getUnitClass());
		
		owner.newUnit(unit);
		
		return unit;
	}
	
	public UnitCivilian spawnCivilianUnit(HexView hex, Player owner, CivilianUnitType type){
		
		Image icon = type.getIcon();
		
		UnitCivilian unit = null;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource(FXMLUtils.FXML_UNIT_CIVILIAN));

			fxmlLoader.load(getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_UNIT_CIVILIAN));

			unit = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		unit.getIcon().setImage(icon);
		
		unit.setup(owner);
		unit.relocate(hex);
		unit.setUnitClass(type.getUnitClass());
		
		owner.newUnit(unit);
		
		return unit;
	}
	
	private IUnit selectedUnit;

	public IUnit getSelectedUnit() {
		return selectedUnit;
	}
	
	List<BorderTile> borders = new ArrayList<BorderTile>();
	
	List<Point> movablePoints = new ArrayList<Point>();
	public void setSelectedUnit(IUnit selectedUnit) {
		
		FrontierModel.getInstance().deselectUnit();
		FrontierModel.getInstance().deselectCity();
		
		this.selectedUnit = selectedUnit;
		
		movablePoints = collectAdjacentPoints(selectedUnit.getLocation(), selectedUnit.getMovement()-1, 1);
		for(Point point : movablePoints){
			HexView movableHex = hexMap2.getOrDefault(point, null);
			
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
				BorderTile moveBorder = BorderTile.createBorderTile(movableHex, true, frontierGameController.getExploredGroup());
				borders.add(moveBorder);
			}
		}
	}
	
	public City spawnCity(Player owner){
		
		String symbol = owner.getCivilization().getNextCityName();
		
		City city = null;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource(FXMLUtils.FXML_CITY));

			fxmlLoader.load(getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_CITY));

			city = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HexView hex = hexMap2.get(getSelectedUnit().getLocation());
		hex.setOwner(owner);
		
		for(Point point : hex.getAdjacentPoints()){
			HexView adjHex = hexMap2.getOrDefault(point, null);
			
			if(adjHex != null && (adjHex.getBorderTile() == null || adjHex.getBorderTile().isDeFacto())){
				if(adjHex.getBorderTile() != null){
					System.out.println("Removing existing DeFacto border");
					getFrontierGameController().getExploredGroup().getChildren().remove(adjHex.getBorderTile().getHexagon());
					getFrontierGameController().getExploredGroup().getChildren().remove(adjHex.getBorderTile().getBorderLines());
				}
				adjHex.setOwner(owner);
				BorderTile cityBorder = BorderTile.createBorderTile(adjHex, false, frontierGameController.getExploredGroup());
				cityBorder.setHolding(city);
				city.addBorder(cityBorder);
			}
		}
		BorderTile cityCenterBorder = BorderTile.createBorderTile(hex, false, frontierGameController.getExploredGroup());
		cityCenterBorder.setHolding(city);
		city.addBorder(cityCenterBorder);
		city.setCenterBorder(cityCenterBorder);
		
		System.out.println("City Borders: " + city.getBorders().size());
		
		city.setup(owner);
		city.relocate(hex);
		city.getNameLabel().setText(symbol);
		
		city.addFoodYield(1);
		city.addGoldYield(1);
		city.addScienceYield(1);
		
		city.addPopulation(1);
		
		owner.newHolding(city);
		
		return city;
	}
	
	public Church spawnChurch(Player owner){
		
		String symbol = owner.getCivilization().getNextCityName();
		
		Church church = null;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource(FXMLUtils.FXML_CHURCH));

			fxmlLoader.load(getClass().getClassLoader().getResourceAsStream(FXMLUtils.FXML_CHURCH));

			church = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HexView hex = hexMap2.get(getSelectedUnit().getLocation());
		hex.setOwner(owner);
		
		for(Point point : hex.getAdjacentPoints()){
			HexView adjHex = hexMap2.getOrDefault(point, null);
			
			if(adjHex != null && (adjHex.getBorderTile() == null || adjHex.getBorderTile().isDeFacto())){
				if(adjHex.getBorderTile() != null){
					System.out.println("Contesting Border");
					BorderTile adjBorder = adjHex.getBorderTile();
					if (!(adjBorder.getHolding() instanceof City)) {
						adjBorder.getHexagon().setFill(
								contestedFill(adjHex.getOwner().getTerritoryColor(), owner.getTerritoryColor()));
						adjBorder.addAnotherOwner(owner);
					}
				} else {
					System.out.println("Creating Border");
					adjHex.setOwner(owner);
					BorderTile churchBorder = BorderTile.createBorderTile(adjHex, false, frontierGameController.getExploredGroup());
					churchBorder.setHolding(church);
					church.addBorder(churchBorder);
				}
			}
		}
		BorderTile churchCenterBorder = BorderTile.createBorderTile(hex, false, frontierGameController.getExploredGroup());
		churchCenterBorder.setHolding(church);
		church.addBorder(churchCenterBorder);
		church.setCenterBorder(churchCenterBorder);
		
		System.out.println("City Borders: " + church.getBorders().size());
		
		church.setup(owner);
		church.colorizeSymbol(owner.getBorderColor());
		church.relocate(hex);
		church.getNameLabel().setText(symbol);
		
		owner.newHolding(church);
		
		return church;
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
		
		FrontierModel.getInstance().deselectUnit();
		FrontierModel.getInstance().deselectCity();
		
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
				getFrontierGameController().getExploredGroup().getChildren().remove(border.getHexagon());
				getFrontierGameController().getExploredGroup().getChildren().removeAll(border.getBorderLines());
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
	
	Set<ICivilization> civilizations = new HashSet<ICivilization>();
	public void setAllCivilizations() {
		civilizations = new HashSet<ICivilization>();
		for(Civilization civ : Civilization.values()){
			civilizations.add(civ);
		}
		
		XMLParser parser = new XMLParser();
		Set<com.rjm.dropout.frontier.objects.Civilization> parserResults = parser.parseCivilizationsXML(XMLUtils.XML_CIVILIZATIONS);
		civilizations.addAll(parserResults);
	}
	
	public Set<ICivilization> getAllCivilizations(){
		return civilizations;
	}
	
	Set<ILeader> leaders = new HashSet<ILeader>();
	public void setAllLeaders() {
		leaders = new HashSet<ILeader>();
		for(Leader leader : Leader.values()){
			leaders.add(leader);
		}
		
		XMLParser parser = new XMLParser();
		Set<com.rjm.dropout.frontier.objects.Leader> parserResults = parser.parseLeadersXML(XMLUtils.XML_LEADERS);
		leaders.addAll(parserResults);
	}
	
	public Set<ILeader> getAllLeaders(){
		return leaders;
	}
	
	Set<CivLeaderPair> civLeaderPairs;	
	public void setAllCivLeaderPairs() {
		civLeaderPairs = new HashSet<CivLeaderPair>();
		
		for(ICivilization civ : getAllCivilizations()){
			leaders.stream().filter(leader -> leader.getCivilization() == civ).collect(Collectors.toList()).forEach(leader -> {
				civLeaderPairs.add(new CivLeaderPair(civ, leader));
			});
		}
	}
	
	public Set<CivLeaderPair> getAllCivLeaderPairs(){
		if(civLeaderPairs == null){
			civLeaderPairs = new HashSet<CivLeaderPair>();
			setAllCivilizations();
	    	setAllLeaders();
	    	setAllCivLeaderPairs();
		}
		
		return civLeaderPairs;
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

	public void createCounty(Player owner, IHolding selectedHolding) {
		List<IHolding> connectedHoldings = owner.getHoldingsConnectedTo(selectedHolding);
		System.out.println("Found " + connectedHoldings.size() + " connected Holdings");
		List<BorderTile> unclaimedBorders = new ArrayList<BorderTile>();
		for(IHolding holding : connectedHoldings){
			System.out.println("Found Connected " + holding.getClass().getSimpleName());
			unclaimedBorders.addAll(holding.getUnclaimedBorders());
		}
		if(unclaimedBorders.size() > 15){
			CountyTile.createCountyTile(unclaimedBorders,connectedHoldings);
		} else {
			System.out.println("Area not large enough: " + unclaimedBorders.size());
		}
	}

	Set<CivLeaderPair> uniqueCivs;
	public void resetUniqueCivs(){
		uniqueCivs = null;
	}
	public CivLeaderPair getNextRandomCiv() {
		if (uniqueCivs == null) {
			List<CivLeaderPair> civs = new ArrayList<CivLeaderPair>(getAllCivLeaderPairs());
			civs.sort(new Comparator<CivLeaderPair>() {

				@Override
				public int compare(CivLeaderPair left, CivLeaderPair right) {
					return left.toString().compareTo(right.toString());
				}
			});
			System.out.println("Removing " + civs.get(0) + " from Civs");
			civs.remove(0);
			
			uniqueCivs = new HashSet<CivLeaderPair>(civs);
		}
		
		System.out.println(uniqueCivs);
		
		List<CivLeaderPair> uniqueList = new ArrayList<CivLeaderPair>(uniqueCivs);
		Collections.shuffle(uniqueList);
		
		CivLeaderPair choice = null;
		
		if(!uniqueList.isEmpty()){
			choice = uniqueList.get(0);
			removeFromUnique(choice);
		}
		
		return choice;
	}
	
	public void removeFromUnique(CivLeaderPair choice){
		if (uniqueCivs != null && uniqueCivs.contains(choice)) {
			uniqueCivs.remove(choice);
		}
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
		
		getFrontierGameController().addGoldToTreasury();
		
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

		getFrontierGameController().addGoldAsset(holdingGold, "from Holdings");
		getFrontierGameController().addScienceAsset(holdingScience, "from Holdings");

		System.out.println("Tiles: Gold = " + tileGold + ", Science = " + tileScience);
		
		getFrontierGameController().addGoldAsset(tileGold, "from Tile Yields");
		getFrontierGameController().addScienceAsset(tileScience, "from Tile Yields");
	}
}
