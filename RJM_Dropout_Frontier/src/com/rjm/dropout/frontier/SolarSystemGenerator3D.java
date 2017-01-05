package com.rjm.dropout.frontier;

import java.util.ArrayList;
import java.util.List;

import com.rjm.dropout.frontier.enums.Elevation;
import com.rjm.dropout.frontier.enums.PlanetType;
import com.rjm.dropout.frontier.enums.StarType;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.SimpleCosmicBody;
import com.rjm.dropout.frontier.objects.Star;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.objects.TileTooltipController;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

public class SolarSystemGenerator3D {
	
	SolarSystemController solarSystemController;
	
	public SolarSystemGenerator3D(SolarSystemController solarSystemController){
		this.solarSystemController = solarSystemController;
	}

	public List<HexView> setup(){
		
		System.out.println("----------------Generating Solar System----------------");

		List<HexView> hexViews = new ArrayList<HexView>();

		flatMethod(hexViews);
		
		trimToHexagon(hexViews);
		
		generateSystem();

		return hexViews;
	}

	private void flatMethod(List<HexView> hexViews){

		int midX = FrontierModel.getInstance().BWIDTH/2;
		int midY = FrontierModel.getInstance().BHEIGHT/2;

		System.out.println("Mid Point: (" + midX + "," + midY + ")");

		int hexWidth = 180;
		int hexHeight = 100;

		Integer numOfTilesAroundEquator = FrontierModel.getInstance().BWIDTH;

		Integer mapHeight = FrontierModel.getInstance().BHEIGHT;

		int counter = 0;

		int currentHeight = 0;

		while(currentHeight < mapHeight){

			double latitude = ((hexHeight/2)) * currentHeight;

			if(numOfTilesAroundEquator != 0)
				while(counter < numOfTilesAroundEquator){
					
					int modifier = (counter %2 == 0) ? 0 : hexHeight/2;

					Point3D point3D = new Point3D(counter*(hexWidth/2), currentHeight*hexHeight+modifier,0);
					HexView hexView = createHexView(Terrain.EMPTYSPACE, Elevation.flat, counter, currentHeight, point3D);
					hexView.mapRadius = 0;
					hexView.angleX = 0;
					hexView.angleY = 0;
					hexView.latitude = latitude;

					Point point = new Point(counter, currentHeight);
					hexView.setPoint(point);

					hexViews.add(hexView);

					SolarSystemModel.getInstance().emptySpaceMap.put(point, hexView);
					SolarSystemModel.getInstance().hexMap.put(point, hexView);

					double rotateYAngle = 180;

					hexView.getTransforms().add(new Rotate(rotateYAngle, Rotate.Y_AXIS));

					counter++;
				}

			currentHeight++;
			counter = 0;
		}
	}
	
	private void trimToHexagon(List<HexView> hexViews){
		
		int x;
		int y;
		
		// Trim NW Corner
		
		x = 0;
		y = FrontierModel.getInstance().BHEIGHT/2-1;

		while(y >= 0){
			
			// Remove hex at point along line
			
			Point point = new Point(x,y);
			
			HexView hex = SolarSystemModel.getInstance().hexMap.get(point);
			SolarSystemModel.getInstance().hexMap.remove(hex);
			SolarSystemModel.getInstance().emptySpaceMap.remove(hex);
			hexViews.remove(hex);
			
			// Remove hexs up to that point
			
			int recursiveX = 0;
			while(recursiveX < x){
				
				boolean xIsEven = x%2 == 0;
				
				Point point2 = new Point(recursiveX,y);
				
				HexView hex2 = SolarSystemModel.getInstance().hexMap.get(point2);
				SolarSystemModel.getInstance().hexMap.remove(hex2);
				SolarSystemModel.getInstance().emptySpaceMap.remove(hex2);
				hexViews.remove(hex2);
				
				if(!xIsEven){
					Point point3 = new Point(recursiveX,y+1);
					
					HexView hex3 = SolarSystemModel.getInstance().hexMap.get(point3);
					SolarSystemModel.getInstance().hexMap.remove(hex3);
					SolarSystemModel.getInstance().emptySpaceMap.remove(hex3);
					hexViews.remove(hex3);
				}
				
				recursiveX++;
			}
			
			int yMod = (x%2 == 0) ? 2 : 1;
			
			x+=1;
			y-=yMod;
		}
		
		// Trim NE Corner
		
		x = FrontierModel.getInstance().BWIDTH-1;
		y = FrontierModel.getInstance().BHEIGHT/2-1;

		while(y >= 0){
			
			// Remove hex at point along line
			
			Point point = new Point(x,y);
			
			HexView hex = SolarSystemModel.getInstance().hexMap.get(point);
			SolarSystemModel.getInstance().hexMap.remove(hex);
			SolarSystemModel.getInstance().emptySpaceMap.remove(hex);
			hexViews.remove(hex);
			
			// Remove hexs up to that point
			
			int recursiveX = FrontierModel.getInstance().BWIDTH-1;
			while(recursiveX > x){
				
				boolean xIsEven = x%2 == 0;
				
				Point point2 = new Point(recursiveX,y);
				
				HexView hex2 = SolarSystemModel.getInstance().hexMap.get(point2);
				SolarSystemModel.getInstance().hexMap.remove(hex2);
				SolarSystemModel.getInstance().emptySpaceMap.remove(hex2);
				hexViews.remove(hex2);
				
				if(!xIsEven){
					Point point3 = new Point(recursiveX,y+1);
					
					HexView hex3 = SolarSystemModel.getInstance().hexMap.get(point3);
					SolarSystemModel.getInstance().hexMap.remove(hex3);
					SolarSystemModel.getInstance().emptySpaceMap.remove(hex3);
					hexViews.remove(hex3);
				}
				
				recursiveX--;
			}
			
			int yMod = (x%2 == 0) ? 2 : 1;
			
			x-=1;
			y-=yMod;
		}
		
		// Trim SW Corner
		
		x = 0;
		y = (FrontierModel.getInstance().BHEIGHT/2)+2;

		while(y < FrontierModel.getInstance().BHEIGHT){
			
			// Remove hex at point along line
			
			Point point = new Point(x,y);
			
			HexView hex = SolarSystemModel.getInstance().hexMap.get(point);
			SolarSystemModel.getInstance().hexMap.remove(hex);
			SolarSystemModel.getInstance().emptySpaceMap.remove(hex);
			hexViews.remove(hex);
			
			// Remove hexs up to that point
			
			int recursiveX = 0;
			while(recursiveX < x){
				
				boolean xIsEven = x%2 == 0;
				
				Point point2 = new Point(recursiveX,y);
				
				HexView hex2 = SolarSystemModel.getInstance().hexMap.get(point2);
				SolarSystemModel.getInstance().hexMap.remove(hex2);
				SolarSystemModel.getInstance().emptySpaceMap.remove(hex2);
				hexViews.remove(hex2);
				
				if(xIsEven){
					Point point3 = new Point(recursiveX,y-1);
					
					HexView hex3 = SolarSystemModel.getInstance().hexMap.get(point3);
					SolarSystemModel.getInstance().hexMap.remove(hex3);
					SolarSystemModel.getInstance().emptySpaceMap.remove(hex3);
					hexViews.remove(hex3);
				}
				
				recursiveX++;
			}
			
			int yMod = (x%2 == 0) ? 1 : 2;
			
			x+=1;
			y+=yMod;
		}
		
		// Trim SE Corner
		
		x = FrontierModel.getInstance().BWIDTH-1;
		y = (FrontierModel.getInstance().BHEIGHT/2)+2;

		while(y < FrontierModel.getInstance().BHEIGHT){
			
			// Remove hex at point along line
			
			Point point = new Point(x,y);
			
			HexView hex = SolarSystemModel.getInstance().hexMap.get(point);
			SolarSystemModel.getInstance().hexMap.remove(hex);
			SolarSystemModel.getInstance().emptySpaceMap.remove(hex);
			hexViews.remove(hex);
			
			// Remove hexs up to that point
			
			int recursiveX = FrontierModel.getInstance().BWIDTH-1;
			while(recursiveX > x){
				
				boolean xIsEven = x%2 == 0;
				
				Point point2 = new Point(recursiveX,y);
				
				HexView hex2 = SolarSystemModel.getInstance().hexMap.get(point2);
				SolarSystemModel.getInstance().hexMap.remove(hex2);
				SolarSystemModel.getInstance().emptySpaceMap.remove(hex2);
				hexViews.remove(hex2);
				
				if(xIsEven){
					Point point3 = new Point(recursiveX,y-1);
					
					HexView hex3 = SolarSystemModel.getInstance().hexMap.get(point3);
					SolarSystemModel.getInstance().hexMap.remove(hex3);
					SolarSystemModel.getInstance().emptySpaceMap.remove(hex3);
					hexViews.remove(hex3);
				}
				
				recursiveX--;
			}
			
			int yMod = (x%2 == 0) ? 1 : 2;
			
			x-=1;
			y+=yMod;
		}
		
	}

	private HexView createHexView(Terrain terrain, Elevation elevation, int x, int y, Point3D point3D){

		boolean yIsEven = (y%2 == 0) ? true : false;

		HexView hex = new HexView(point3D, solarSystemController);

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
//		hex.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
//
//			@Override
//			public void handle(ContextMenuEvent event) {
//				if (FrontierModel.getInstance().getSelectedUnit() == null 
//						&& FrontierModel.getInstance().getSelectedCity() == null) {
//					DebugContextMenuController.getInstance(hex).getContextMenu().show(hex, Side.BOTTOM, 0, 0);
//				}
//			}
//		});
		
		TileTooltipController.createTileTooltip(hex);

		return hex;
	}

	private void generateSystem() {
		
		List<Node> objectsToAddToScene = new ArrayList<Node>();
		
		HexView centerHex = SolarSystemModel.getInstance().hexMap.getOrDefault(
				new Point((FrontierModel.getInstance().BWIDTH/2),(FrontierModel.getInstance().BHEIGHT/2)), null);
		
		Point3D centerPoint = new Point3D(centerHex.getPoint3D().getX(), centerHex.getPoint3D().getY()+50, centerHex.getPoint3D().getZ());
				
//		Pair<Star,Group> starGroup = addStarToSystem(centerPoint, StarType.YELLOW);		
//		SolarSystemModel.getInstance().planetMap.put(centerHex.getPoint(), starGroup.getKey());
//		
//		objectsToAddToScene.add(starGroup.getValue());
//        
//        List<StackPane> planets = new ArrayList<StackPane>();
//        planets.add(addPlanetToStar(starGroup.getKey()));
//        
//        objectsToAddToScene.addAll(planets);
		
		generateSolSystem(objectsToAddToScene, centerHex);

		solarSystemController.getPlanetGroup().getChildren().addAll(objectsToAddToScene);
        
	}
	
	private void generateSolSystem(List<Node> objectsToAddToScene, HexView centerHex){
		Point3D centerPoint = new Point3D(centerHex.getPoint3D().getX(), centerHex.getPoint3D().getY()+50, centerHex.getPoint3D().getZ());
		
		Pair<Star,Group> starGroup = addStarToSystem(centerPoint, StarType.YELLOW);		
		SolarSystemModel.getInstance().planetMap.put(centerHex.getPoint(), starGroup.getKey());
		
		objectsToAddToScene.add(starGroup.getValue());
        
        List<StackPane> planets = new ArrayList<StackPane>();
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.MERCURY));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.VENUS));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.EARTH));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.MARS));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.JUPITER));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.SATURN));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.URANUS));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.NEPTUNE));
        planets.add(addPlanetToStar(starGroup.getKey(), PlanetType.PLUTO));
        
        objectsToAddToScene.addAll(planets);
	}
	
	private Pair<Star,Group> addStarToSystem(Point3D centerPoint, StarType type){
		Star star = new Star(type, 1000);
		star.placeAt(centerPoint);
		
		PointLight pointLight = new PointLight(star.getType().getGlow());
		pointLight.translateXProperty().bind(star.translateXProperty());
		pointLight.translateYProperty().bind(star.translateYProperty());
		pointLight.translateZProperty().bind(star.translateZProperty());
		pointLight.setMouseTransparent(true);
		
		Group group = new Group(star,pointLight);
		group.setPickOnBounds(false);
		
		return new Pair<Star,Group>(star,group);
	}
	
	private StackPane addPlanetToStar(Node star, PlanetType type){
		
		Terrain terrain = null;
		double distance = 0;
		
		switch (type) {
			case EARTH:
				terrain = Terrain.MARSH;
				distance = 600;
				break;
			case GAS:
				break;
			case GASGIANT:
				break;
			case JUPITER:
				terrain = Terrain.COAST;
				distance = 1500;
				break;
			case LARGEROCK:
				break;
			case MARS:
				terrain = Terrain.DESERT;
				distance = 800;
				break;
			case MERCURY:
				terrain = Terrain.TUNDRA;
				distance = 200;
				break;
			case NEPTUNE:
				terrain = Terrain.OCEAN;
				distance = 2400;
				break;
			case PLUTO:
				terrain = Terrain.SNOW;
				distance = 2800;
				break;
			case SATURN:
				terrain = Terrain.SAVANNAH;
				distance = 1800;
				break;
			case SMALLROCK:
				break;
			case URANUS:
				terrain = Terrain.COAST;
				distance = 2100;
				break;
			case VENUS:
				terrain = Terrain.SAVANNAH;
				distance = 400;
				break;
			default:
			break;
		}
		
		SimpleCosmicBody planet = createPlanet(star, terrain, type.getRadius(), type.getDayLength(), distance);
		planet.playOrbit();
		
		List<StackPane> moons = new ArrayList<StackPane>();
		moons.add(addMoonToPlanet(planet));
		
        StackPane planetPane = new StackPane();
        planetPane.setPickOnBounds(false);
        planetPane.getChildren().addAll(moons);
        planetPane.getChildren().add(planet);
        planetPane.getChildren().add(planet.getPath());

        double planetPaneWidth = planetPane.getBoundsInLocal().getWidth();
        double planetPaneHeight = planetPane.getBoundsInLocal().getHeight();
        
        System.out.println(planetPaneWidth + ", " + planetPaneHeight);
        planetPane.translateXProperty().bind(star.translateXProperty().subtract(planetPaneWidth/2));
        planetPane.translateYProperty().bind(star.translateYProperty().subtract(planetPaneHeight/2));
        
        return planetPane;
	}
	
	private SimpleCosmicBody createPlanet(Node star, Terrain biome, double size, double yearLength, double distance){
		
		SimpleCosmicBody planet = new SimpleCosmicBody(size,24);
		planet.setBiome(biome);
		
		Ellipse ellipsePlanet = new Ellipse();
        ellipsePlanet.setRadiusX(star.getBoundsInLocal().getWidth() / 2.0 + 1.01671388 * distance);
        ellipsePlanet.setRadiusY(star.getBoundsInLocal().getHeight() / 2.0 + 0.98329134 * distance);
        ellipsePlanet.setStrokeWidth(1);
        ellipsePlanet.setStroke(Color.STEELBLUE);
        ellipsePlanet.setFill(Color.TRANSPARENT);
        ellipsePlanet.setMouseTransparent(true);
        
        planet.setPath(ellipsePlanet, yearLength);
        
        return planet;
	}
	
	private StackPane addMoonToPlanet(Node planet){
		SimpleCosmicBody moon = createMoon(planet, Terrain.ICECAP, 30, 100);
		moon.playOrbit();
		
        StackPane moonPane = new StackPane();
        moonPane.setPickOnBounds(false);
        moonPane.translateXProperty().bind(planet.translateXProperty());
        moonPane.translateYProperty().bind(planet.translateYProperty());
        moonPane.getChildren().add(moon);
        moonPane.getChildren().add(moon.getPath());
        
        return moonPane;
	}
	
	private SimpleCosmicBody createMoon(Node planet, Terrain biome, double monthLength, double distance){
		
		SimpleCosmicBody moon = new SimpleCosmicBody(15,24);
		moon.setBiome(biome);        
         
        Ellipse ellipseMoon = new Ellipse();
        ellipseMoon.setRadiusX(planet.getBoundsInLocal().getWidth() / 2.0 + 1.01671388 * distance);
        ellipseMoon.setRadiusY(planet.getBoundsInLocal().getHeight() / 2.0 + 0.98329134 * distance);
        ellipseMoon.setStrokeWidth(1);
        ellipseMoon.setStroke(Color.LIGHTGRAY);
        ellipseMoon.setFill(Color.TRANSPARENT);
        ellipseMoon.setMouseTransparent(true);
        
        moon.setPath(ellipseMoon, monthLength);
        
        return moon;
	}
}