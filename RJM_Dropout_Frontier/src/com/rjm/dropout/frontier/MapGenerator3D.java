package com.rjm.dropout.frontier;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.rjm.dropout.frontier.enums.Elevation;
import com.rjm.dropout.frontier.enums.MapSize;
import com.rjm.dropout.frontier.enums.ResourceAnimals;
import com.rjm.dropout.frontier.enums.ResourceFruits;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.enums.WorldAge;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.objects.ResourceAnimal;
import com.rjm.dropout.frontier.objects.ResourceFruit;
import com.rjm.dropout.frontier.objects.TileTooltipController;
import com.rjm.dropout.frontier.utilities.DebugContextMenuController;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.transform.Rotate;

public class MapGenerator3D {
	
	FrontierGameController frontierGameController;

	MapSize continentSize;
	WorldAge worldAge;
	String[][] board;

	int continentMass = 0;
	int numContinents = 0;
	int seperation = 0;
	int coastTotal = 0, desertTotal = 0, forestTotal = 0, freshwaterTotal = 0, grasslandTotal = 0, jungleTotal = 0, marshTotal = 0, oceanTotal = 0, savannahTotal = 0, snowTotal = 0, taigaTotal = 0, tundraTotal = 0, allTerrainTotal = 0;
	
	double equator = FrontierModel.getInstance().BHEIGHT/2;

	int arcticN = (int)Math.ceil(equator - (((0.79*80)/100)*equator)); //       /---------------\
	int tundraN = (int)Math.ceil(equator - (((0.64*80)/100)*equator)); //      /-----------------\
	int taigaN = (int)Math.ceil(equator - (((0.56*80)/100)*equator));  //	  /-------------------\
	int tropicN = (int)Math.ceil(equator - (((0.24*80)/100)*equator)); //	 /---------------------\
	// equator											 			   //	|=======================|
	int tropicS = (int)Math.ceil(equator - (((-0.24*80)/100)*equator));//	 \---------------------/
	int taigaS = (int)Math.ceil(equator - (((-0.56*80)/100)*equator)); //	  \-------------------/
	int tundraS = (int)Math.ceil(equator - (((-0.64*80)/100)*equator));//      \-----------------/
	int arcticS = (int)Math.ceil(equator - (((-0.79*80)/100)*equator));//       \---------------/

	public MapGenerator3D(FrontierGameController frontierGameController, MapSize size, WorldAge age, String[][] board){
		this.frontierGameController = frontierGameController;
		
		this.board = board;
		this.continentSize = size;
		this.worldAge = age;
		switch(size){
		case HUGE:
			numContinents = 3;
			continentMass = 400;
			break;
		case LARGE:
			numContinents = 5;
			continentMass = 300;
			break;
		case STANDARD:
			numContinents = 8;
			continentMass = 200;
			break;
		case SMALL:
			numContinents = 12;
			continentMass = 100;
			break;
		}
		seperation = FrontierModel.getInstance().BWIDTH/numContinents;
	}

	public List<HexView> setup(){
		
		System.out.println("----------------Generating Planet Map----------------");

		List<HexView> HexViews = new ArrayList<HexView>();

		cylinderMethod(HexViews);

		//		while(numContinents > 0){
		//			numContinents = generateContinent(this.continentSize, this.worldAge, numContinents);
		//		}

		while(numContinents > 0){
			numContinents = generateContinent2(numContinents);
		}

		return HexViews;
	}

	private void cylinderMethod(List<HexView> HexViews){

		int midX = FrontierModel.getInstance().BWIDTH/2;
		int midY = FrontierModel.getInstance().BHEIGHT/2;

		System.out.println("Mid Point: (" + midX + "," + midY + ")");

		int hexWidth = 180;
		int hexHeight = 100;

		Integer numOfTilesAroundEquator = FrontierModel.getInstance().BWIDTH;
		Double circumference = new Double(numOfTilesAroundEquator * hexWidth);
		Double radius = (circumference/Math.PI)/2;

		Integer mapHeight = FrontierModel.getInstance().BHEIGHT;

		int counter = 0;

		int currentHeight = 0;

		while(currentHeight < mapHeight){

			double latitude = ((hexHeight/2)) * currentHeight;

			if(numOfTilesAroundEquator != 0)
				while(counter < numOfTilesAroundEquator){

					double newAngleYSize = ((hexWidth) * 360) / circumference;

					double angleY = counter*newAngleYSize + ((currentHeight%2 == 0) ? 0 : (newAngleYSize/2));

					Point3D point3D = findPoint(radius, 0, angleY, latitude);
					HexView hexView = createHexView(Terrain.OCEAN, Elevation.flat, counter, currentHeight, point3D);
					hexView.mapRadius = radius;
					hexView.angleX = 0;
					hexView.angleY = angleY;
					hexView.latitude = latitude;

					Point point = new Point(counter, currentHeight);
					hexView.setPoint(point);

					HexViews.add(hexView);

//					System.out.println("New Tile at: (" + counter + "," + currentHeight + ")");

					FrontierModel.getInstance().oceanMap2.put(point, hexView);
					FrontierModel.getInstance().hexMap2.put(point, hexView);

					double arcLength = circumference/numOfTilesAroundEquator;

					double rotateYSize = (arcLength/circumference)*360;

					double rotateYAngle = counter * rotateYSize;

					hexView.getTransforms().add(new Rotate(rotateYAngle, Rotate.Y_AXIS));

					counter++;
				}

			currentHeight++;
			counter = 0;
		}

		System.out.println("Radius: " + radius);
		FrontierGameController.zoomMin = -radius*4;
		FrontierGameController.zoomMax = -(2*radius)*4;
	}

	private HexView createHexView(Terrain terrain, Elevation elevation, int x, int y, Point3D point3D){

		boolean yIsEven = (y%2 == 0) ? true : false;

		HexView hex = new HexView(point3D, frontierGameController);

		hex.setup(terrain, elevation);

		double newX = 0.0;
		double newY = 0.0;

		newX = 90.0 * x;

		if(yIsEven){
			newY = 100.0 * y;

			// Top
			hex.getAdjacentPoints().add(new Point(x, y-2));

			// Top Right
			hex.getAdjacentPoints().add(new Point(x, y-1));

			// Bottom Right
			hex.getAdjacentPoints().add(new Point(x, y+1));

			// Bottom
			hex.getAdjacentPoints().add(new Point(x, y+2));

			// Bottom Left
			hex.getAdjacentPoints().add(new Point(x-1, y+1));

			// Top Left
			hex.getAdjacentPoints().add(new Point(x-1, y-1));
		} else if (!yIsEven){
			newY = (100.0 * y)+50;

			// Top
			hex.getAdjacentPoints().add(new Point(x, y-2));

			// Top Right
			hex.getAdjacentPoints().add(new Point(x, y-1));

			// Bottom Right
			hex.getAdjacentPoints().add(new Point(x, y+1));

			// Bottom
			hex.getAdjacentPoints().add(new Point(x, y+2));

			// Bottom Left
			hex.getAdjacentPoints().add(new Point(x+1, y+1));	

			// Top Left
			hex.getAdjacentPoints().add(new Point(x+1, y-1));
		}

		hex.getAdjacentPoints().forEach(point -> {
			if(point.x < 0){
				point.x = (FrontierModel.getInstance().BWIDTH-1);
			} else if(point.x > (FrontierModel.getInstance().BWIDTH-1)){
				point.x = 0;
			}
		});

		if(point3D != null){
			hex.moveTo(point3D);
		}
		hex.setLocation(newX, newY);
		hex.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				if (FrontierModel.getInstance().getSelectedUnit() == null 
						&& FrontierModel.getInstance().getSelectedCity() == null) {
					DebugContextMenuController.getInstance(hex).getContextMenu().show(hex, Side.BOTTOM, 0, 0);
				}
			}
		});
		
		TileTooltipController.createTileTooltip(hex);

		return hex;
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

	public int generateContinent2(int numContinents){
		/* Map Generation Heuristics
		 *  - Jungle & Desert must be between tropics
		 *  - Savannah must start between tropics, but can continue until taiga
		 *  - Grassland & Marsh can start anywhere from equator to taiga, but can't continue into taiga
		 *  - Temperate Forest must start between taigas, but can continue until tundra
		 */

		// Default land/water ratio => 30/70 percent

		equator = FrontierModel.getInstance().BHEIGHT/2;

		arcticN = (int)Math.ceil(equator - (((0.79*80)/100)*equator)); //       /---------------\
		tundraN = (int)Math.ceil(equator - (((0.64*80)/100)*equator)); //      /-----------------\
		taigaN = (int)Math.ceil(equator - (((0.56*80)/100)*equator));  //	  /-------------------\
		tropicN = (int)Math.ceil(equator - (((0.24*80)/100)*equator)); //	 /---------------------\
		// equator											 		   //	|=======================|
		tropicS = (int)Math.ceil(equator - (((-0.24*80)/100)*equator));//	 \---------------------/
		taigaS = (int)Math.ceil(equator - (((-0.56*80)/100)*equator)); //	  \-------------------/
		tundraS = (int)Math.ceil(equator - (((-0.64*80)/100)*equator));//      \-----------------/
		arcticS = (int)Math.ceil(equator - (((-0.79*80)/100)*equator));//       \---------------/

		System.out.println("Arctic North: " + arcticN);
		System.out.println("Tundra North: " + tundraN);
		System.out.println("Taiga North: " + taigaN);
		System.out.println("Tropic North: " + tropicN);
		System.out.println("Equator: " + equator);
		System.out.println("Tropic South: " + tropicS);
		System.out.println("Taiga South: " + taigaS);
		System.out.println("Tundra South: " + tundraS);
		System.out.println("Arctic South: " + arcticS);
		System.out.println();

		switch(worldAge){
		case FiveBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 25;
			break;
		case FourBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 50;
			break;
		case ThreeBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 75;
			break;
		default:
			FrontierModel.getInstance().MOUNTAINCHANCE = 50;
			break;
		}

		int cycles = numContinents;

		List<Point> continentOrigins = new ArrayList<Point>();

		Point originPoint = new Point(FrontierModel.getInstance().BWIDTH/2, FrontierModel.getInstance().BHEIGHT/2);
		Random originRand = new Random();
		originPoint.x = originRand.nextInt(FrontierModel.getInstance().BWIDTH);
		originPoint.y = originRand.nextInt(FrontierModel.getInstance().BHEIGHT);

		while(cycles > 0){

			Point newPoint = new Point(FrontierModel.getInstance().BWIDTH/2, FrontierModel.getInstance().BHEIGHT/2);

			newPoint.x = originPoint.x + seperation;
			if(newPoint.x > FrontierModel.getInstance().BWIDTH-1){
				newPoint.x = (0 + (newPoint.x - (FrontierModel.getInstance().BWIDTH-1)));
			}
			newPoint.y = originRand.nextInt(FrontierModel.getInstance().BHEIGHT);

			originPoint = newPoint;

			continentOrigins.add(originPoint);

			List<Point> continentPoints = new ArrayList<Point>();

			HexView origin = FrontierModel.getInstance().hexMap2.get(originPoint);
			List<Point> adjacentPoints = origin.getAdjacentPoints();

			adjacentPoints.forEach(point -> {

				int continentSize = continentMass;

				Point currentPoint = point;

				continentPoints.add(currentPoint);

				HexView prevTile = null;

				while (continentSize > 0) {

					HexView tile = FrontierModel.getInstance().hexMap2.get(currentPoint);
					if (tile != null) {
						//						tile.setTerrain(Terrain.GRASSLAND);

						determineBiome(prevTile,tile);

//						FrontierModel.getInstance().grasslandMap2.put(currentPoint, tile);

//						if(FrontierModel.getInstance().oceanMap2.keySet().contains(currentPoint)){
//							FrontierModel.getInstance().oceanMap2.remove(currentPoint);
//						}

						List<Point> newAdjacentPoints = tile.getAdjacentPoints();
						currentPoint = newAdjacentPoints.get(new Random().nextInt(newAdjacentPoints.size()));

						continentPoints.add(currentPoint);
					}

					prevTile = tile;

					continentSize--;
				}

			});

			cycles--;
		}

		return cycles;
	}

	public void determineBiome(HexView prevTile, HexView tile){
		int biomeWeight = FrontierModel.getInstance().BIOMEWEIGHT;

		Random sameBiome = new Random();
		double biomeChance = sameBiome.nextInt(10);

		if(biomeChance < biomeWeight && prevTile != null){

			// Remove the tile being replaced from its previous Map
			Terrain terrainToReplace = tile.getTerrain();
			switch(terrainToReplace){
			case DESERT:
				desertTotal--;
				FrontierModel.getInstance().desertMap2.remove(tile.getPoint());
				break;
			case FOREST:
				forestTotal--;
				FrontierModel.getInstance().forestMap2.remove(tile.getPoint());
				break;
			case FRESHWATER:
				freshwaterTotal--;
				FrontierModel.getInstance().freshwaterMap2.remove(tile.getPoint());
				break;
			case GRASSLAND:
				grasslandTotal--;
				FrontierModel.getInstance().grasslandMap2.remove(tile.getPoint());
				break;
			case JUNGLE:
				jungleTotal--;
				FrontierModel.getInstance().jungleMap2.remove(tile.getPoint());
				break;
			case MARSH:
				marshTotal--;
				FrontierModel.getInstance().marshMap2.remove(tile.getPoint());
				break;
			case OCEAN:
				oceanTotal--;
				FrontierModel.getInstance().oceanMap2.remove(tile.getPoint());
				break;
			case SAVANNAH:
				savannahTotal--;
				FrontierModel.getInstance().savannahMap2.remove(tile.getPoint());
				break;
			case SNOW:
				snowTotal--;
				FrontierModel.getInstance().snowMap2.remove(tile.getPoint());
				break;
			case TAIGA:
				taigaTotal--;
				FrontierModel.getInstance().taigaMap2.remove(tile.getPoint());
				break;
			case TUNDRA:
				tundraTotal--;
				FrontierModel.getInstance().tundraMap2.remove(tile.getPoint());
				break;
			case COAST:
				coastTotal--;
				FrontierModel.getInstance().coastMap2.remove(tile.getPoint());
				break;
			default:
				break;					
			}

			// Add tile to new map
			Terrain terrain = prevTile.getTerrain();
			switch(terrain){
			case DESERT:
				if(!FrontierModel.getInstance().desertMap2.containsKey(tile.getPoint())){
					desertTotal++;
					FrontierModel.getInstance().desertMap2.put(tile.getPoint(), tile);
				}
				break;
			case FOREST:
				if(!FrontierModel.getInstance().forestMap2.containsKey(tile.getPoint())){
					forestTotal++;
					FrontierModel.getInstance().forestMap2.put(tile.getPoint(), tile);
				}
				break;
			case FRESHWATER:
				if(!FrontierModel.getInstance().freshwaterMap2.containsKey(tile.getPoint())){
					freshwaterTotal++;
					FrontierModel.getInstance().freshwaterMap2.put(tile.getPoint(), tile);
				}
				break;
			case GRASSLAND:
				if(!FrontierModel.getInstance().grasslandMap2.containsKey(tile.getPoint())){
					grasslandTotal++;
					FrontierModel.getInstance().grasslandMap2.put(tile.getPoint(), tile);
				}
				break;
			case JUNGLE:
				if(!FrontierModel.getInstance().jungleMap2.containsKey(tile.getPoint())){
					jungleTotal++;
					FrontierModel.getInstance().jungleMap2.put(tile.getPoint(), tile);
				}
				break;
			case MARSH:
				if(!FrontierModel.getInstance().marshMap2.containsKey(tile.getPoint())){
					marshTotal++;
					FrontierModel.getInstance().marshMap2.put(tile.getPoint(), tile);
				}
				break;
			case OCEAN:
				if(!FrontierModel.getInstance().oceanMap2.containsKey(tile.getPoint())){
					oceanTotal++;
					FrontierModel.getInstance().oceanMap2.put(tile.getPoint(), tile);
				}
				break;
			case SAVANNAH:
				if(!FrontierModel.getInstance().savannahMap2.containsKey(tile.getPoint())){
					savannahTotal++;
					FrontierModel.getInstance().savannahMap2.put(tile.getPoint(), tile);
				}
				break;
			case SNOW:
				if(!FrontierModel.getInstance().snowMap2.containsKey(tile.getPoint())){
					snowTotal++;
					FrontierModel.getInstance().snowMap2.put(tile.getPoint(), tile);
				}
				break;
			case TAIGA:
				if(!FrontierModel.getInstance().taigaMap2.containsKey(tile.getPoint())){
					taigaTotal++;
					FrontierModel.getInstance().taigaMap2.put(tile.getPoint(), tile);
				}
				break;
			case TUNDRA:
				if(!FrontierModel.getInstance().tundraMap2.containsKey(tile.getPoint())){
					tundraTotal++;
					FrontierModel.getInstance().tundraMap2.put(tile.getPoint(), tile);
				}
				break;
			case COAST:
				if(!FrontierModel.getInstance().coastMap2.containsKey(tile.getPoint())){
					coastTotal++;
					FrontierModel.getInstance().coastMap2.put(tile.getPoint(), tile);
				}
				break;
			default:
				break;					
			}

			tile.setTerrain(terrain);
			biomeWeight--;
		} else {
			biomeWeight = FrontierModel.getInstance().BIOMEWEIGHT;

			// Remove the tile being replaced from its previous Map
			Terrain terrainToReplace = tile.getTerrain();
			switch(terrainToReplace){
			case DESERT:
				desertTotal--;
				FrontierModel.getInstance().desertMap2.remove(tile.getPoint());
				break;
			case FOREST:
				forestTotal--;
				FrontierModel.getInstance().forestMap2.remove(tile.getPoint());
				break;
			case FRESHWATER:
				freshwaterTotal--;
				FrontierModel.getInstance().freshwaterMap2.remove(tile.getPoint());
				break;
			case GRASSLAND:
				grasslandTotal--;
				FrontierModel.getInstance().grasslandMap2.remove(tile.getPoint());
				break;
			case JUNGLE:
				jungleTotal--;
				FrontierModel.getInstance().jungleMap2.remove(tile.getPoint());
				break;
			case MARSH:
				marshTotal--;
				FrontierModel.getInstance().marshMap2.remove(tile.getPoint());
				break;
			case OCEAN:
				oceanTotal--;
				FrontierModel.getInstance().oceanMap2.remove(tile.getPoint());
				break;
			case SAVANNAH:
				savannahTotal--;
				FrontierModel.getInstance().savannahMap2.remove(tile.getPoint());
				break;
			case SNOW:
				snowTotal--;
				FrontierModel.getInstance().snowMap2.remove(tile.getPoint());
				break;
			case TAIGA:
				taigaTotal--;
				FrontierModel.getInstance().taigaMap2.remove(tile.getPoint());
				break;
			case TUNDRA:
				tundraTotal--;
				FrontierModel.getInstance().tundraMap2.remove(tile.getPoint());
				break;
			case COAST:
				coastTotal--;
				FrontierModel.getInstance().coastMap2.remove(tile.getPoint());
				break;
			default:
				break;					
			}
			
			Random rand = new Random();

			if (tropicN < tile.getPoint().y && tile.getPoint().y < tropicS) {
				// If between tropic and equator
				int terrainChoice = rand.nextInt(6);
				//Grassland, Forest, Jungle, Savannah, Desert, Marsh
				switch (terrainChoice) {
				case 0:
					tile.setTerrain(Terrain.GRASSLAND);
					if(!FrontierModel.getInstance().grasslandMap2.containsKey(tile.getPoint())){
						grasslandTotal++;
						FrontierModel.getInstance().grasslandMap2.put(tile.getPoint(), tile);
					}
					break;
				case 1:
					tile.setTerrain(Terrain.FOREST);
					if(!FrontierModel.getInstance().forestMap2.containsKey(tile.getPoint())){
						forestTotal++;
						FrontierModel.getInstance().forestMap2.put(tile.getPoint(), tile);
					}
					break;
				case 2:
					tile.setTerrain(Terrain.JUNGLE);
					if(!FrontierModel.getInstance().jungleMap2.containsKey(tile.getPoint())){
						jungleTotal++;
						FrontierModel.getInstance().jungleMap2.put(tile.getPoint(), tile);
					}
					break;
				case 3:
					tile.setTerrain(Terrain.SAVANNAH);
					if(!FrontierModel.getInstance().savannahMap2.containsKey(tile.getPoint())){
						savannahTotal++;
						FrontierModel.getInstance().savannahMap2.put(tile.getPoint(), tile);
					}
					break;
				case 4:
					tile.setTerrain(Terrain.DESERT);
					if(!FrontierModel.getInstance().desertMap2.containsKey(tile.getPoint())){
						desertTotal++;
						FrontierModel.getInstance().desertMap2.put(tile.getPoint(), tile);
					}
					break;
				case 5:
					tile.setTerrain(Terrain.MARSH);
					if(!FrontierModel.getInstance().marshMap2.containsKey(tile.getPoint())){
						marshTotal++;
						FrontierModel.getInstance().marshMap2.put(tile.getPoint(), tile);
					}
					break;
				}
			} else if ((tile.getPoint().y <= tropicN && tile.getPoint().y >= taigaN)
					|| (tile.getPoint().y >= tropicS && tile.getPoint().y <= taigaS)) {
				// If between taiga and tropic
				int terrainChoice = rand.nextInt(3);
				//Grassland, Forest, Savannah
				switch (terrainChoice) {
				case 0:
					tile.setTerrain(Terrain.GRASSLAND);
					if(!FrontierModel.getInstance().grasslandMap2.containsKey(tile.getPoint())){
						grasslandTotal++;
						FrontierModel.getInstance().grasslandMap2.put(tile.getPoint(), tile);
					}
					break;
				case 1:
					tile.setTerrain(Terrain.FOREST);
					if(!FrontierModel.getInstance().forestMap2.containsKey(tile.getPoint())){
						forestTotal++;
						FrontierModel.getInstance().forestMap2.put(tile.getPoint(), tile);
					}
					break;
				case 2:
					tile.setTerrain(Terrain.SAVANNAH);
					if(!FrontierModel.getInstance().savannahMap2.containsKey(tile.getPoint())){
						savannahTotal++;
						FrontierModel.getInstance().savannahMap2.put(tile.getPoint(), tile);
					}
					break;
				}
			} else if ((tile.getPoint().y <= taigaN && tile.getPoint().y >= tundraN)
					|| (tile.getPoint().y >= taigaS && tile.getPoint().y <= tundraS)) {
				// If between tundra and taiga
				tile.setTerrain(Terrain.TAIGA);
				if(!FrontierModel.getInstance().taigaMap2.containsKey(tile.getPoint())){
					taigaTotal++;
					FrontierModel.getInstance().taigaMap2.put(tile.getPoint(), tile);
				}
			} else if ((tile.getPoint().y <= tundraN && tile.getPoint().y >= arcticN)
					|| (tile.getPoint().y >= tundraS && tile.getPoint().y <= arcticS)) {
				// If between arctic and tundra
				tile.setTerrain(Terrain.TUNDRA);
				if(!FrontierModel.getInstance().tundraMap2.containsKey(tile.getPoint())){
					tundraTotal++;
					FrontierModel.getInstance().tundraMap2.put(tile.getPoint(), tile);
				}
			} else if ((tile.getPoint().y <= arcticN && tile.getPoint().y >= 0)
					|| (tile.getPoint().y >= arcticS && tile.getPoint().y <= FrontierModel.getInstance().BHEIGHT)) {
				// if between pole and arctic
				tile.setTerrain(Terrain.SNOW);
				if(!FrontierModel.getInstance().snowMap2.containsKey(tile.getPoint())){
					snowTotal++;
					FrontierModel.getInstance().snowMap2.put(tile.getPoint(), tile);
				}
			}
		}
	}

	public int generateContinent(MapSize size, WorldAge age, int numContinents){
		/* Map Generation Heuristics
		 *  - Jungle & Desert must be between tropics
		 *  - Savannah must start between tropics, but can continue until taiga
		 *  - Grassland & Marsh can start anywhere from equator to taiga, but can't continue into taiga
		 *  - Temperate Forest must start between taigas, but can continue until tundra
		 */

		// Default land/water ratio => 30/70 percent

		double equator = FrontierModel.getInstance().BHEIGHT/2;

		//		int arcticN = (int)Math.ceil(equator - (((0.64*80)/100)*equator)); //       /---------------\
		//		int tundraN = (int)Math.ceil(equator - (((0.58*80)/100)*equator)); //      /-----------------\
		//		int taigaN = (int)Math.ceil(equator - (((0.44*80)/100)*equator));  //	  /-------------------\
		//		int tropicN = (int)Math.ceil(equator - (((0.24*80)/100)*equator)); //	 /---------------------\
		//		// equator											 			   //	|=======================|
		//		int tropicS = (int)Math.ceil(equator - (((-0.24*80)/100)*equator));//	 \---------------------/
		//		int taigaS = (int)Math.ceil(equator - (((-0.56*80)/100)*equator)); //	  \-------------------/
		//		int tundraS = (int)Math.ceil(equator - (((-0.64*80)/100)*equator));//      \-----------------/
		//		int arcticS = (int)Math.ceil(equator - (((-0.72*80)/100)*equator));//       \---------------/

		int arcticN = (int)Math.ceil(equator - (((0.79*80)/100)*equator)); //       /---------------\
		int tundraN = (int)Math.ceil(equator - (((0.64*80)/100)*equator)); //      /-----------------\
		int taigaN = (int)Math.ceil(equator - (((0.56*80)/100)*equator));  //	  /-------------------\
		int tropicN = (int)Math.ceil(equator - (((0.24*80)/100)*equator)); //	 /---------------------\
		// equator											 			   //	|=======================|
		int tropicS = (int)Math.ceil(equator - (((-0.24*80)/100)*equator));//	 \---------------------/
		int taigaS = (int)Math.ceil(equator - (((-0.56*80)/100)*equator)); //	  \-------------------/
		int tundraS = (int)Math.ceil(equator - (((-0.64*80)/100)*equator));//      \-----------------/
		int arcticS = (int)Math.ceil(equator - (((-0.79*80)/100)*equator));//       \---------------/

		System.out.println("Arctic North: " + arcticN);
		System.out.println("Tundra North: " + tundraN);
		System.out.println("Taiga North: " + taigaN);
		System.out.println("Tropic North: " + tropicN);
		System.out.println("Equator: " + equator);
		System.out.println("Tropic South: " + tropicS);
		System.out.println("Taiga South: " + taigaS);
		System.out.println("Tundra South: " + tundraS);
		System.out.println("Arctic South: " + arcticS);
		System.out.println();

		switch(age){
		case FiveBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 25;
			break;
		case FourBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 50;
			break;
		case ThreeBillion:
			FrontierModel.getInstance().MOUNTAINCHANCE = 75;
			break;
		default:
			FrontierModel.getInstance().MOUNTAINCHANCE = 50;
			break;
		}

		int cycles = numContinents;
		while(cycles > 0){
			Random rand = new Random();

			int landMin = 0;
			int landMax = 100;

			switch(age){
			case ThreeBillion:
				landMin = 40;
				landMax = 50;
				break;
			case FourBillion:
				landMin = 30;
				landMax = 40;
				break;
			case FiveBillion:
				landMin = 20;
				landMax = 30;
				break;
			}

			int landAreaPercent = rand.nextInt(landMax-landMin) + landMin;
			double landArea = (double)landAreaPercent / 100;
			int totalTiles = FrontierModel.getInstance().BWIDTH * FrontierModel.getInstance().BHEIGHT; System.out.println("Total Map Tiles: " + totalTiles);
			int totalLandTiles = (int)Math.ceil(totalTiles * landArea); System.out.println("Total Land Tiles: " + totalLandTiles);
			int i = rand.nextInt(FrontierModel.getInstance().BWIDTH);
			int j = rand.nextInt(FrontierModel.getInstance().BHEIGHT);
			Point point = new Point(i,j);
			HexView HexViewAtPoint = FrontierModel.getInstance().hexMap2.get(point);

			/*while(!HexViewAtPoint.getColor().equals(OCEAN)){
			i = rand.nextInt(FrontierGameController.BWIDTH);
			j = rand.nextInt(FrontierGameController.BHEIGHT);
		}*/

//			Point continentOrigin = point;
//			Point previousPoint = point;
			Point currentPoint = point;

			/* Size Heurisitics
			 *  - SMALL: 5 - 11 percent of total land area
			 *  - MEDIUM: 12 - 24 percent of total land area
			 *  - LARGE: 25 - 35 percent of total land area
			 *  - HUGE: 36 - 50 percent of total land area
			 */
			int continentSize = totalLandTiles;
			Random sizeRand = new Random();
			int sizeRandInt = 0;
			double sizePercent = 0.0;
			switch(size){
			case HUGE:
				sizeRandInt = sizeRand.nextInt(15) + 36;
				sizePercent = (double)sizeRandInt / 100;
				continentSize = (int)Math.ceil(totalLandTiles * sizePercent);
				System.out.println("Continent Size (in tiles): " + continentSize);
				System.out.println();
				break;
			case LARGE:
				sizeRandInt = sizeRand.nextInt(11) + 25;
				sizePercent = (double)sizeRandInt / 100;
				continentSize = (int)Math.ceil(totalLandTiles * sizePercent);
				System.out.println("Continent Size (in tiles): " + continentSize);
				System.out.println();
				break;
			case STANDARD:
				sizeRandInt = sizeRand.nextInt(13) + 12;
				sizePercent = (double)sizeRandInt / 100;
				continentSize = (int)Math.ceil(totalLandTiles * sizePercent);
				System.out.println("Continent Size (in tiles): " + continentSize);
				System.out.println();
				break;
			case SMALL:
				sizeRandInt = sizeRand.nextInt(7) + 5;
				sizePercent = (double)sizeRandInt / 100;
				continentSize = (int)Math.ceil(totalLandTiles * sizePercent);
				System.out.println("Continent Size (in tiles): " + continentSize);
				System.out.println();
				break;
			}
			int biomeWeight = FrontierModel.getInstance().BIOMEWEIGHT;
			boolean firstTile = true;
			while(continentSize > 0){
				HexView HexViewAtPreviousPoint = HexViewAtPoint;
				HexViewAtPoint = FrontierModel.getInstance().hexMap2.get(currentPoint);

				Random sameBiome = new Random();
				double biomeChance = sameBiome.nextInt(10);

				if(biomeChance < biomeWeight && !firstTile){

					// Remove the tile being replaced from its previous Map
					Terrain terrainToReplace = HexViewAtPoint.getTerrain();
					switch(terrainToReplace){
					case DESERT:
						desertTotal--;
						FrontierModel.getInstance().desertMap2.remove(currentPoint);
						break;
					case FOREST:
						forestTotal--;
						FrontierModel.getInstance().forestMap2.remove(currentPoint);
						break;
					case FRESHWATER:
						freshwaterTotal--;
						FrontierModel.getInstance().freshwaterMap2.remove(currentPoint);
						break;
					case GRASSLAND:
						grasslandTotal--;
						FrontierModel.getInstance().grasslandMap2.remove(currentPoint);
						break;
					case JUNGLE:
						jungleTotal--;
						FrontierModel.getInstance().jungleMap2.remove(currentPoint);
						break;
					case MARSH:
						marshTotal--;
						FrontierModel.getInstance().marshMap2.remove(currentPoint);
						break;
					case OCEAN:
						oceanTotal--;
						FrontierModel.getInstance().oceanMap2.remove(currentPoint);
						break;
					case SAVANNAH:
						savannahTotal--;
						FrontierModel.getInstance().savannahMap2.remove(currentPoint);
						break;
					case SNOW:
						snowTotal--;
						FrontierModel.getInstance().snowMap2.remove(currentPoint);
						break;
					case TAIGA:
						taigaTotal--;
						FrontierModel.getInstance().taigaMap2.remove(currentPoint);
						break;
					case TUNDRA:
						tundraTotal--;
						FrontierModel.getInstance().tundraMap2.remove(currentPoint);
						break;
					case COAST:
						coastTotal--;
						FrontierModel.getInstance().coastMap2.remove(currentPoint);
						break;
					default:
						break;					
					}

					// Add tile to new map
					Terrain terrain = HexViewAtPreviousPoint.getTerrain();
					switch(terrain){
					case DESERT:
						if(!FrontierModel.getInstance().desertMap2.containsKey(currentPoint)){
							desertTotal++;
							FrontierModel.getInstance().desertMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case FOREST:
						if(!FrontierModel.getInstance().forestMap2.containsKey(currentPoint)){
							forestTotal++;
							FrontierModel.getInstance().forestMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case FRESHWATER:
						if(!FrontierModel.getInstance().freshwaterMap2.containsKey(currentPoint)){
							freshwaterTotal++;
							FrontierModel.getInstance().freshwaterMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case GRASSLAND:
						if(!FrontierModel.getInstance().grasslandMap2.containsKey(currentPoint)){
							grasslandTotal++;
							FrontierModel.getInstance().grasslandMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case JUNGLE:
						if(!FrontierModel.getInstance().jungleMap2.containsKey(currentPoint)){
							jungleTotal++;
							FrontierModel.getInstance().jungleMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case MARSH:
						if(!FrontierModel.getInstance().marshMap2.containsKey(currentPoint)){
							marshTotal++;
							FrontierModel.getInstance().marshMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case OCEAN:
						if(!FrontierModel.getInstance().oceanMap2.containsKey(currentPoint)){
							oceanTotal++;
							FrontierModel.getInstance().oceanMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case SAVANNAH:
						if(!FrontierModel.getInstance().savannahMap2.containsKey(currentPoint)){
							savannahTotal++;
							FrontierModel.getInstance().savannahMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case SNOW:
						if(!FrontierModel.getInstance().snowMap2.containsKey(currentPoint)){
							snowTotal++;
							FrontierModel.getInstance().snowMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case TAIGA:
						if(!FrontierModel.getInstance().taigaMap2.containsKey(currentPoint)){
							taigaTotal++;
							FrontierModel.getInstance().taigaMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case TUNDRA:
						if(!FrontierModel.getInstance().tundraMap2.containsKey(currentPoint)){
							tundraTotal++;
							FrontierModel.getInstance().tundraMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					case COAST:
						if(!FrontierModel.getInstance().coastMap2.containsKey(currentPoint)){
							coastTotal++;
							FrontierModel.getInstance().coastMap2.put(currentPoint, HexViewAtPoint);
						}
						break;
					default:
						break;					
					}

					HexViewAtPoint.setTerrain(terrain);
					biomeWeight--;
				} else {
					biomeWeight = FrontierModel.getInstance().BIOMEWEIGHT;

					// Remove the tile being replaced from its previous Map
					Terrain terrainToReplace = HexViewAtPoint.getTerrain();
					switch(terrainToReplace){
					case DESERT:
						desertTotal--;
						FrontierModel.getInstance().desertMap2.remove(currentPoint);
						break;
					case FOREST:
						forestTotal--;
						FrontierModel.getInstance().forestMap2.remove(currentPoint);
						break;
					case FRESHWATER:
						freshwaterTotal--;
						FrontierModel.getInstance().freshwaterMap2.remove(currentPoint);
						break;
					case GRASSLAND:
						grasslandTotal--;
						FrontierModel.getInstance().grasslandMap2.remove(currentPoint);
						break;
					case JUNGLE:
						jungleTotal--;
						FrontierModel.getInstance().jungleMap2.remove(currentPoint);
						break;
					case MARSH:
						marshTotal--;
						FrontierModel.getInstance().marshMap2.remove(currentPoint);
						break;
					case OCEAN:
						oceanTotal--;
						FrontierModel.getInstance().oceanMap2.remove(currentPoint);
						break;
					case SAVANNAH:
						savannahTotal--;
						FrontierModel.getInstance().savannahMap2.remove(currentPoint);
						break;
					case SNOW:
						snowTotal--;
						FrontierModel.getInstance().snowMap2.remove(currentPoint);
						break;
					case TAIGA:
						taigaTotal--;
						FrontierModel.getInstance().taigaMap2.remove(currentPoint);
						break;
					case TUNDRA:
						tundraTotal--;
						FrontierModel.getInstance().tundraMap2.remove(currentPoint);
						break;
					case COAST:
						coastTotal--;
						FrontierModel.getInstance().coastMap2.remove(currentPoint);
						break;
					default:
						break;					
					}

					if (tropicN < currentPoint.y && currentPoint.y < tropicS) {
						// If between tropic and equator
						int terrainChoice = rand.nextInt(6);
						//Grassland, Forest, Jungle, Savannah, Desert, Marsh
						switch (terrainChoice) {
						case 0:
							HexViewAtPoint.setTerrain(Terrain.GRASSLAND);
							if(!FrontierModel.getInstance().grasslandMap2.containsKey(currentPoint)){
								grasslandTotal++;
								FrontierModel.getInstance().grasslandMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 1:
							HexViewAtPoint.setTerrain(Terrain.FOREST);
							if(!FrontierModel.getInstance().forestMap2.containsKey(currentPoint)){
								forestTotal++;
								FrontierModel.getInstance().forestMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 2:
							HexViewAtPoint.setTerrain(Terrain.JUNGLE);
							if(!FrontierModel.getInstance().jungleMap2.containsKey(currentPoint)){
								jungleTotal++;
								FrontierModel.getInstance().jungleMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 3:
							HexViewAtPoint.setTerrain(Terrain.SAVANNAH);
							if(!FrontierModel.getInstance().savannahMap2.containsKey(currentPoint)){
								savannahTotal++;
								FrontierModel.getInstance().savannahMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 4:
							HexViewAtPoint.setTerrain(Terrain.DESERT);
							if(!FrontierModel.getInstance().desertMap2.containsKey(currentPoint)){
								desertTotal++;
								FrontierModel.getInstance().desertMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 5:
							HexViewAtPoint.setTerrain(Terrain.MARSH);
							if(!FrontierModel.getInstance().marshMap2.containsKey(currentPoint)){
								marshTotal++;
								FrontierModel.getInstance().marshMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						}
					} else if ((currentPoint.y <= tropicN && currentPoint.y >= taigaN)
							|| (currentPoint.y >= tropicS && currentPoint.y <= taigaS)) {
						// If between taiga and tropic
						int terrainChoice = rand.nextInt(3);
						//Grassland, Forest, Savannah
						switch (terrainChoice) {
						case 0:
							HexViewAtPoint.setTerrain(Terrain.GRASSLAND);
							if(!FrontierModel.getInstance().grasslandMap2.containsKey(currentPoint)){
								grasslandTotal++;
								FrontierModel.getInstance().grasslandMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 1:
							HexViewAtPoint.setTerrain(Terrain.FOREST);
							if(!FrontierModel.getInstance().forestMap2.containsKey(currentPoint)){
								forestTotal++;
								FrontierModel.getInstance().forestMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						case 2:
							HexViewAtPoint.setTerrain(Terrain.SAVANNAH);
							if(!FrontierModel.getInstance().savannahMap2.containsKey(currentPoint)){
								savannahTotal++;
								FrontierModel.getInstance().savannahMap2.put(currentPoint, HexViewAtPoint);
							}
							break;
						}
					} else if ((currentPoint.y <= taigaN && currentPoint.y >= tundraN)
							|| (currentPoint.y >= taigaS && currentPoint.y <= tundraS)) {
						// If between tundra and taiga
						HexViewAtPoint.setTerrain(Terrain.TAIGA);
						if(!FrontierModel.getInstance().taigaMap2.containsKey(currentPoint)){
							taigaTotal++;
							FrontierModel.getInstance().taigaMap2.put(currentPoint, HexViewAtPoint);
						}
					} else if ((currentPoint.y <= tundraN && currentPoint.y >= arcticN)
							|| (currentPoint.y >= tundraS && currentPoint.y <= arcticS)) {
						// If between arctic and tundra
						HexViewAtPoint.setTerrain(Terrain.TUNDRA);
						if(!FrontierModel.getInstance().tundraMap2.containsKey(currentPoint)){
							tundraTotal++;
							FrontierModel.getInstance().tundraMap2.put(currentPoint, HexViewAtPoint);
						}
					} else if ((currentPoint.y <= arcticN && currentPoint.y >= 0)
							|| (currentPoint.y >= arcticS && currentPoint.y <= FrontierModel.getInstance().BHEIGHT)) {
						// if between pole and arctic
						HexViewAtPoint.setTerrain(Terrain.SNOW);
						if(!FrontierModel.getInstance().snowMap2.containsKey(currentPoint)){
							snowTotal++;
							FrontierModel.getInstance().snowMap2.put(currentPoint, HexViewAtPoint);
						}
					} 

					firstTile = false;
				}

				//System.out.println("Changed tile at coordinate: " + currentPoint.x + "," + currentPoint.y + " to " + HexViewAtPoint.getTerrain().toString());

//				previousPoint = currentPoint;

				int newX = currentPoint.x;
				int newY = currentPoint.y;
				double x0or1;
				double y0or1;
				double negOrPosX;
				double negOrPosY;

				Random random = new Random();

				x0or1 = random.nextInt(2);
				y0or1 = random.nextInt(2);
				negOrPosX = random.nextInt(2);
				negOrPosY = random.nextInt(2);
				if (negOrPosX >= 0.5) {
					if (x0or1 >= 0.5) {
						newX = currentPoint.x + 1;
					} else {
						newX = currentPoint.x + 0;
					}
				} else {
					if (x0or1 >= 0.5) {
						newX = currentPoint.x - 1;
					} else {
						newX = currentPoint.x - 0;
					}
				}
				if (negOrPosY >= 0.5) {
					if (y0or1 >= 0.5) {
						newY = currentPoint.y + 1;
					} else {
						newY = currentPoint.y + 0;
					}
				} else {
					if (y0or1 >= 0.5) {
						newY = currentPoint.y - 1;
					} else {
						newY = currentPoint.y - 0;
					}
				} 

				if(newX < 0){
					newX = FrontierModel.getInstance().BWIDTH -1;
				} else if(newX >= FrontierModel.getInstance().BWIDTH){
					newX = 0;
				}

				if(newY < 0){
					newY = 0;
				} else if(newY >= FrontierModel.getInstance().BHEIGHT){
					newY = FrontierModel.getInstance().BHEIGHT -1;
				}

				Point newPoint = new Point(newX, newY);
				currentPoint = newPoint;

				continentSize--;

			}

			cycles--;
		}


		//		FrontierGameController.findCoastlines();
		//		FrontierGameController.widenCoastlines();
		//		FrontierGameController.findMountains();

		return cycles;
	}

	public List<HexView> placeResources(String [][] board){

		List<HexView> resourceTiles = new ArrayList<HexView>();

		// Resources - Animals
		HashMap<Point, HexView> bearMap = FrontierModel.getInstance().forestMap2;
		HashMap<Point, HexView> camelMap = FrontierModel.getInstance().desertMap2;
		HashMap<Point, HexView> chickenMap = FrontierModel.getInstance().grasslandMap2;
		HashMap<Point, HexView> elephantMap = new HashMap<Point, HexView> (FrontierModel.getInstance().savannahMap2);
		elephantMap.putAll(FrontierModel.getInstance().jungleMap2);
		HashMap<Point, HexView> ferretMap = FrontierModel.getInstance().forestMap2;
		HashMap<Point, HexView> foxMap = FrontierModel.getInstance().forestMap2;
		HashMap<Point, HexView> goatMap = FrontierModel.getInstance().grasslandMap2;
		HashMap<Point, HexView> horseMap = FrontierModel.getInstance().grasslandMap2;
		HashMap<Point, HexView> pigMap = FrontierModel.getInstance().marshMap2;
		HashMap<Point, HexView> rabbitMap = new HashMap<Point, HexView> (FrontierModel.getInstance().grasslandMap2);
		rabbitMap.putAll(FrontierModel.getInstance().forestMap2);
		HashMap<Point, HexView> sheepMap = FrontierModel.getInstance().grasslandMap2;
		HashMap<Point, HexView> tigerMap = FrontierModel.getInstance().jungleMap2;

		HashMap<Point, HexView> seaAnimalMap = FrontierModel.getInstance().coastMap2;

		// Resources - Fruits
		HashMap<Point, HexView> blueapricotMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().grasslandMap2);
		blueapricotMap.putAll(FrontierModel.getInstance().jungleMap2);
		HashMap<Point, HexView> citronMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().forestMap2);
		citronMap.putAll(FrontierModel.getInstance().grasslandMap2);
		HashMap<Point, HexView> gafferMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().desertMap2);
		gafferMap.putAll(FrontierModel.getInstance().savannahMap2);
		HashMap<Point, HexView> goremelonMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().grasslandMap2);
		goremelonMap.putAll(FrontierModel.getInstance().marshMap2);
		HashMap<Point, HexView> mandarinMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().forestMap2);
		mandarinMap.putAll(FrontierModel.getInstance().jungleMap2);
		HashMap<Point, HexView> pomeloMap =  new HashMap<Point, HexView> (FrontierModel.getInstance().forestMap2);
		pomeloMap.putAll(FrontierModel.getInstance().marshMap2);
		HashMap<Point, HexView> porumMap = new HashMap<Point, HexView> (FrontierModel.getInstance().forestMap2);
		porumMap.putAll(FrontierModel.getInstance().jungleMap2);
		HashMap<Point, HexView> uvatMap = new HashMap<Point, HexView> (FrontierModel.getInstance().grasslandMap2);
		uvatMap.putAll(FrontierModel.getInstance().savannahMap2);

		/*
		// Sea small/
		HashMap<Point, HexView> lionfishMap = coastMap;
		HashMap<Point, HexView> codMap = coastMap;
		HashMap<Point, HexView> lobsterMap = coastMap;
		HashMap<Point, HexView> shellfishMap = coastMap;
		// Sea medium
		HashMap<Point, HexView> leopardsealMap = coastMap;
		HashMap<Point, HexView> sharkMap = coastMap;
		HashMap<Point, HexView> tunaMap = coastMap;
		HashMap<Point, HexView> dolphinMap = coastMap;
		// Sea large
		HashMap<Point, HexView> bluewhaleMap = coastMap;
		HashMap<Point, HexView> mantaMap = coastMap;
		HashMap<Point, HexView> giantsquidMap = coastMap;
		HashMap<Point, HexView> nessyMap = coastMap;
		 */

		bearMap = pruneMountains(bearMap);
		camelMap = pruneMountains(camelMap);
		chickenMap = pruneMountains(chickenMap);
		elephantMap = pruneMountains(elephantMap);
		ferretMap = pruneMountains(ferretMap);
		foxMap = pruneMountains(foxMap);
		goatMap = pruneMountains(goatMap);
		horseMap = pruneMountains(horseMap);
		pigMap = pruneMountains(pigMap);
		rabbitMap = pruneMountains(rabbitMap);
		sheepMap = pruneMountains(sheepMap);
		tigerMap = pruneMountains(tigerMap);

		blueapricotMap = pruneMountains(blueapricotMap);
		citronMap = pruneMountains(citronMap);
		gafferMap = pruneMountains(gafferMap);
		goremelonMap = pruneMountains(goremelonMap);
		mandarinMap = pruneMountains(mandarinMap);
		pomeloMap = pruneMountains(pomeloMap);
		porumMap = pruneMountains(porumMap);
		uvatMap = pruneMountains(uvatMap);

		int numBears = (int) Math.ceil(bearMap.size()/15); System.out.println("Number of Bears: " + numBears);
		int numCamels = (int) Math.ceil(camelMap.size()/15); System.out.println("Number of Camels: " + numCamels);
		int numChickens = (int) Math.ceil(chickenMap.size()/15); System.out.println("Number of Chickens: " + numChickens);
		int numElephants = (int) Math.ceil(elephantMap.size()/15); System.out.println("Number of Elephants: " + numElephants);
		int numFerrets = (int) Math.ceil(ferretMap.size()/15); System.out.println("Number of Ferrets: " + numFerrets);
		int numFoxes = (int) Math.ceil(foxMap.size()/15); System.out.println("Number of Foxes: " + numFoxes);
		int numGoats = (int) Math.ceil(goatMap.size()/15); System.out.println("Number of Goats: " + numGoats);
		int numHorses = (int) Math.ceil(horseMap.size()/15); System.out.println("Number of Horses: " + numHorses);
		int numPigs = (int) Math.ceil(pigMap.size()/15); System.out.println("Number of Pigs: " + numPigs);
		int numRabbits = (int) Math.ceil(rabbitMap.size()/15); System.out.println("Number of Rabbits: " + numRabbits);
		int numSheep = (int) Math.ceil(sheepMap.size()/15); System.out.println("Number of Sheep: " + numSheep);
		int numTigers = (int) Math.ceil(tigerMap.size()/15); System.out.println("Number of Tigers: " + numTigers);

		int numSeaAnimals = (int) Math.ceil(seaAnimalMap.size()/5);

		int numBlueapricots = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Blue Apricots: " + numBlueapricots);
		int numCitrons = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Citrons: " + numCitrons);
		int numGaffers = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Gaffers: " + numGaffers);
		int numGoremelons = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Gore Melons: " + numGoremelons);
		int numMandarins = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Mandarins: " + numMandarins);
		int numPomelos = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Pomelos: " + numPomelos);
		int numPorums = (int) Math.ceil(porumMap.size()/15); System.out.println("Number of Porums: " + numPorums);
		int numUvats = (int) Math.ceil(uvatMap.size()/15); System.out.println("Number of Uvats: " + numUvats);

		while(numBears > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(bearMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.bear, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.bear.toString()));

			numBears--;
		}
		while(numCamels > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(camelMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.camel, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.camel.toString()));

			numCamels--;
		}
		while(numChickens > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(chickenMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.chicken, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.chicken.toString()));

			numChickens--;
		}
		while(numElephants > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(elephantMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.elephant, board);		
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.elephant.toString()));

			numElephants--;
		}
		while(numFerrets > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(ferretMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.ferret, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.ferret.toString()));

			numFerrets--;
		}
		while(numFoxes > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(foxMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.fox, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.fox.toString()));

			numFoxes--;
		}
		while(numGoats > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(goatMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.goat, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.goat.toString()));

			numGoats--;
		}
		while(numHorses > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(horseMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.horse, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.horse.toString()));

			numHorses--;
		}
		while(numPigs > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(pigMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.pig, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.pig.toString()));

			numPigs--;
		}
		while(numRabbits > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(rabbitMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.rabbit, board);		
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.rabbit.toString()));


			numRabbits--;
		}
		while(numSheep > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(sheepMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.sheep, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.sheep.toString()));
			numSheep--;
		}
		while(numTigers > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(tigerMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			new ResourceAnimal(randomPoint, ResourceAnimals.tiger, board);
			resourceTiles.add(showResources(randomPoint, ResourceAnimals.tiger.toString()));

			numTigers--;
		}

		while(numSeaAnimals > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(seaAnimalMap.keySet());
			Random genRandomPoint = new Random();
			Point randomPoint = new Point(-1,-1);
			randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));

			Random genRandomSeaAnimal = new Random();
			int randomSeaAnimal = genRandomSeaAnimal.nextInt(12);

			switch(randomSeaAnimal){
			case 0:
				new ResourceAnimal(randomPoint, ResourceAnimals.bluewhale, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.bluewhale.toString()));
				break;
			case 1:
				new ResourceAnimal(randomPoint, ResourceAnimals.cod, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.cod.toString()));
				break;
			case 2:
				new ResourceAnimal(randomPoint, ResourceAnimals.dolphin, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.dolphin.toString()));
				break;
			case 3:
				new ResourceAnimal(randomPoint, ResourceAnimals.giantsquid, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.giantsquid.toString()));
				break;
			case 4:
				new ResourceAnimal(randomPoint, ResourceAnimals.leopardseal, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.leopardseal.toString()));
				break;
			case 5:
				new ResourceAnimal(randomPoint, ResourceAnimals.lionfish, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.lionfish.toString()));
				break;
			case 6:
				new ResourceAnimal(randomPoint, ResourceAnimals.lobster, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.lobster.toString()));
				break;
			case 7:
				new ResourceAnimal(randomPoint, ResourceAnimals.manta, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.manta.toString()));
				break;
			case 8:
				new ResourceAnimal(randomPoint, ResourceAnimals.nessy, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.nessy.toString()));
				break;
			case 9:
				new ResourceAnimal(randomPoint, ResourceAnimals.shark, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.shark.toString()));
				break;
			case 10:
				new ResourceAnimal(randomPoint, ResourceAnimals.shellfish, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.shellfish.toString()));
				break;
			case 11:
				new ResourceAnimal(randomPoint, ResourceAnimals.tuna, board);
				resourceTiles.add(showResources(randomPoint, ResourceAnimals.tuna.toString()));
				break;

			}

			numSeaAnimals--;
		}

		while(numBlueapricots > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(blueapricotMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (blueapricotMap.get(randomPoint).getTerrain().equals(Terrain.GRASSLAND)) {
					new ResourceFruit(randomPoint, ResourceFruits.blueapricotg, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.blueapricotg.toString()));
				} else if (blueapricotMap.get(randomPoint).getTerrain().equals(Terrain.JUNGLE)) {
					new ResourceFruit(randomPoint, ResourceFruits.blueapricotj, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.blueapricotj.toString()));
				} 
			}
			numBlueapricots--;
		}
		while(numCitrons > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(citronMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (citronMap.get(randomPoint).getTerrain().equals(Terrain.FOREST)) {
					new ResourceFruit(randomPoint, ResourceFruits.citronf, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.citronf.toString()));
				} else if (citronMap.get(randomPoint).getTerrain().equals(Terrain.GRASSLAND)) {
					new ResourceFruit(randomPoint, ResourceFruits.citrong, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.citrong.toString()));
				} 
			}
			numCitrons--;
		}
		while(numGaffers > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(gafferMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (gafferMap.get(randomPoint).getTerrain().equals(Terrain.DESERT)) {
					new ResourceFruit(randomPoint, ResourceFruits.gafferd, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.gafferd.toString()));
				} else if (gafferMap.get(randomPoint).getTerrain().equals(Terrain.SAVANNAH)) {
					new ResourceFruit(randomPoint, ResourceFruits.gaffers, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.gaffers.toString()));
				} 
			}
			numGaffers--;
		}
		while(numGoremelons > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(goremelonMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (goremelonMap.get(randomPoint).getTerrain().equals(Terrain.GRASSLAND)) {
					new ResourceFruit(randomPoint, ResourceFruits.goremelong, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.goremelong.toString()));
				} else if (goremelonMap.get(randomPoint).getTerrain().equals(Terrain.MARSH)) {
					new ResourceFruit(randomPoint, ResourceFruits.goremelonm, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.goremelonm.toString()));
				} 
			}
			numGoremelons--;
		}
		while(numMandarins > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(mandarinMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (mandarinMap.get(randomPoint).getTerrain().equals(Terrain.FOREST)) {
					new ResourceFruit(randomPoint, ResourceFruits.mandarinf, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.mandarinf.toString()));
				} else if (mandarinMap.get(randomPoint).getTerrain().equals(Terrain.JUNGLE)) {
					new ResourceFruit(randomPoint, ResourceFruits.mandarinj, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.mandarinj.toString()));
				} 
			}
			numMandarins--;
		}
		while(numPomelos > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(pomeloMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (pomeloMap.get(randomPoint).getTerrain().equals(Terrain.FOREST)) {
					new ResourceFruit(randomPoint, ResourceFruits.pomelof, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.pomelof.toString()));
				} else if (pomeloMap.get(randomPoint).getTerrain().equals(Terrain.MARSH)) {
					new ResourceFruit(randomPoint, ResourceFruits.pomelom, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.pomelom.toString()));
				} 
			}
			numPomelos--;
		}
		while(numPorums > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(porumMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (porumMap.get(randomPoint).getTerrain().equals(Terrain.FOREST)) {
					new ResourceFruit(randomPoint, ResourceFruits.porumf, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.porumf.toString()));
				} else if (porumMap.get(randomPoint).getTerrain().equals(Terrain.JUNGLE)) {
					new ResourceFruit(randomPoint, ResourceFruits.porumj, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.porumj.toString()));
				} 
			}
			numPorums--;
		}
		while(numUvats > 0){
			List<Point> mapKeysAsArray = new ArrayList<Point>(uvatMap.keySet());
			if (mapKeysAsArray.size() > 0) {
				Random genRandomPoint = new Random();
				Point randomPoint = new Point(-1, -1);
				randomPoint = mapKeysAsArray.get(genRandomPoint.nextInt(mapKeysAsArray.size()));
				if (uvatMap.get(randomPoint).getTerrain().equals(Terrain.GRASSLAND)) {
					new ResourceFruit(randomPoint, ResourceFruits.uvatg, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.uvatg.toString()));
				} else if (uvatMap.get(randomPoint).getTerrain().equals(Terrain.SAVANNAH)) {
					new ResourceFruit(randomPoint, ResourceFruits.uvats, board);
					resourceTiles.add(showResources(randomPoint, ResourceFruits.uvats.toString()));
				} 
			}
			numUvats--;
		}

		return resourceTiles;
	}

	static HashMap<Point, HexView> pruneMountains(HashMap<Point,HexView> map){
		HashMap<Point, HexView> newMap = map;
		ArrayList<Point> markedForDelete = new ArrayList<Point>();

		for(Point point : newMap.keySet()){
			if(newMap.get(point).getElevation().equals(Elevation.mountain)){
				markedForDelete.add(point);
			}
		}

		for(Point point : markedForDelete){
			newMap.remove(point);
		}

		return newMap;
	}

	public HexView showResources(Point point, String text) {

		Image resource = null;

		if(text.equals(ResourceAnimals.bear.toString())){
			resource = MapGenTextures.BEAR;
		} else if(text.equals(ResourceAnimals.bluewhale.toString())){
			resource = MapGenTextures.WHALE;
		} else if(text.equals(ResourceAnimals.camel.toString())){
			resource = MapGenTextures.CAMEL;
		} else if(text.equals(ResourceAnimals.chicken.toString())){
			resource = MapGenTextures.CHICKEN;
		} else if(text.equals(ResourceAnimals.cod.toString())){
			resource = MapGenTextures.COD;
		} else if(text.equals(ResourceAnimals.dolphin.toString())){
			resource = MapGenTextures.DOLPHIN;
		} else if(text.equals(ResourceAnimals.elephant.toString())){
			resource = MapGenTextures.ELEPHANT;
		}else if(text.equals(ResourceAnimals.ferret.toString())){
			resource = MapGenTextures.FERRET;
		} else if(text.equals(ResourceAnimals.fox.toString())){
			resource = MapGenTextures.FOX;
		} else if(text.equals(ResourceAnimals.giantsquid.toString())){
			resource = MapGenTextures.SQUID;
		} else if(text.equals(ResourceAnimals.goat.toString())){
			resource = MapGenTextures.GOAT;
		} else if(text.equals(ResourceAnimals.horse.toString())){
			resource = MapGenTextures.HORSE;
		} else if(text.equals(ResourceAnimals.leopardseal.toString())){
			resource = MapGenTextures.LEOPARDSEAL;
		} else if(text.equals(ResourceAnimals.lionfish.toString())){
			resource = MapGenTextures.LIONFISH;
		} else if(text.equals(ResourceAnimals.lobster.toString())){
			resource = MapGenTextures.LOBSTER;
		} else if(text.equals(ResourceAnimals.manta.toString())){
			resource = MapGenTextures.MANTA;
		} else if(text.equals(ResourceAnimals.nessy.toString())){
			resource = MapGenTextures.NESSY;
		} else if(text.equals(ResourceAnimals.pig.toString())){
			resource = MapGenTextures.PIG;
		} else if(text.equals(ResourceAnimals.rabbit.toString())){
			resource = MapGenTextures.RABBIT;
		} else if(text.equals(ResourceAnimals.shark.toString())){
			resource = MapGenTextures.SHARK;
		} else if(text.equals(ResourceAnimals.sheep.toString())){
			resource = MapGenTextures.SHEEP;
		} else if(text.equals(ResourceAnimals.shellfish.toString())){
			resource = MapGenTextures.SHELLFISH;
		} else if(text.equals(ResourceAnimals.tiger.toString())){
			resource = MapGenTextures.TIGER;
		} else if(text.equals(ResourceAnimals.tuna.toString())){
			resource = MapGenTextures.TUNA;
		}

		else if(text.equals(ResourceFruits.blueapricotg.toString())){
			resource = MapGenTextures.BLUEAPRICOTgrassland;
		} else if(text.equals(ResourceFruits.blueapricotj.toString())){
			resource = MapGenTextures.BLUEAPRICOTjungle;
		} else if(text.equals(ResourceFruits.citronf.toString())){
			resource = MapGenTextures.CITRONforest;
		} else if(text.equals(ResourceFruits.citrong.toString())){
			resource = MapGenTextures.CITRONgrassland;
		} else if(text.equals(ResourceFruits.gafferd.toString())){
			resource = MapGenTextures.GAFFERdesert;
		} else if(text.equals(ResourceFruits.gaffers.toString())){
			resource = MapGenTextures.GAFFERsavannah;
		} else if(text.equals(ResourceFruits.goremelong.toString())){
			resource = MapGenTextures.GOREMELONgrassland;
		} else if(text.equals(ResourceFruits.goremelonm.toString())){
			resource = MapGenTextures.GOREMELONmarsh;
		} else if(text.equals(ResourceFruits.mandarinf.toString())){
			resource = MapGenTextures.MANDARINforest;
		} else if(text.equals(ResourceFruits.mandarinj.toString())){
			resource = MapGenTextures.MANDARINjungle;
		} else if(text.equals(ResourceFruits.pomelof.toString())){
			resource = MapGenTextures.POMELOforest;
		} else if(text.equals(ResourceFruits.pomelom.toString())){
			resource = MapGenTextures.POMELOmarsh;
		} else if(text.equals(ResourceFruits.porumf.toString())){
			resource = MapGenTextures.PORUMforest;
		} else if(text.equals(ResourceFruits.porumj.toString())){
			resource = MapGenTextures.PORUMjungle;
		} else if(text.equals(ResourceFruits.uvatg.toString())){
			resource = MapGenTextures.UVATgrassland;
		} else if(text.equals(ResourceFruits.uvats.toString())){
			resource = MapGenTextures.UVATsavannah;
		}

		if (resource != null) {
			//			HexView HexView = createHexView(Terrain.TUNDRA, Elevation.flat, point.x, point.y);
			HexView HexView = FrontierModel.getInstance().hexMap2.get(point);

			if(!HexView.isResource){
				HexView.setTexture(layerImages(HexView.getTexture(), resource));
				//				HexView.getHexagon().setMouseTransparent(true);
				HexView.isResource = true;

				FrontierModel.getInstance().resourceMap2.put(point, HexView);
			}

			return HexView;
		} else {			
			HexView HexView = FrontierModel.getInstance().hexMap2.get(point);

			//			HexView newHexView = createHexView(HexView.getTerrain(), HexView.getElevation(), point.x, point.y);
			//			newHexView.getHexagon().setMouseTransparent(true);

			FrontierModel.getInstance().resourceMap2.put(point, /*newHexView*/HexView);

			return HexView;
		}

		//		mapGeneratorUtility.getResourceGroup().getChildren().add(resource);
	}

	public Image layerImages(Image oldImg, Image newLayer){
		BufferedImage a = SwingFXUtils.fromFXImage(oldImg,null);
		BufferedImage b = SwingFXUtils.fromFXImage(newLayer,null);
		BufferedImage c = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics g = c.getGraphics();
		g.drawImage(a, 0, 0, null);
		g.drawImage(b, (b.getWidth()/3), (b.getHeight()/3), null);

		return SwingFXUtils.toFXImage(c, null);
	}
}