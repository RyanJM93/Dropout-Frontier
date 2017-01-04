package com.rjm.dropout.frontier;

import java.util.ArrayList;
import java.util.List;

import com.rjm.dropout.frontier.enums.Elevation;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.Planet;
import com.rjm.dropout.frontier.objects.PlanetEarth;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.objects.TileTooltipController;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;

public class SolarSystemGenerator3D {
	
	SolarSystemController solarSystemController;
	
	public SolarSystemGenerator3D(SolarSystemController solarSystemController){
		this.solarSystemController = solarSystemController;
	}

	public List<HexView> setup(){
		
		System.out.println("----------------Generating Solar System----------------");

		List<HexView> HexViews = new ArrayList<HexView>();

		flatMethod(HexViews);

		//		while(numContinents > 0){
		//			numContinents = generateContinent(this.continentSize, this.worldAge, numContinents);
		//		}

		generateSystem();

		return HexViews;
	}

	private void flatMethod(List<HexView> HexViews){

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

					HexViews.add(hexView);

//					System.out.println("New Tile at: (" + counter + "," + currentHeight + ")");

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
		HexView centerHex = SolarSystemModel.getInstance().hexMap.getOrDefault(
				new Point(FrontierModel.getInstance().BWIDTH/2,FrontierModel.getInstance().BHEIGHT/2), null);
		
		PlanetEarth star = new PlanetEarth();
		star.placeAt(centerHex.getPoint3D());
		
		SolarSystemModel.getInstance().planetMap.put(centerHex.getPoint(), star);
		solarSystemController.getPlanetGroup().getChildren().add(star);
		
		Pair<? extends Planet, Pair<Ellipse,PathTransition>> planetPair = addPlanetToStar(star, centerHex.getPoint3D());
		
		Pair<? extends Planet, Pair<Ellipse,PathTransition>> moonPair = addMoonToPlanet(planetPair.getKey(), centerHex.getPoint3D());
		
		planetPair.getValue().getValue().play();
		moonPair.getValue().getValue().play();
		
        StackPane moonPane = new StackPane();
        moonPane.translateXProperty().bind(planetPair.getKey().translateXProperty());
        moonPane.translateYProperty().bind(planetPair.getKey().translateYProperty());
        moonPane.getChildren().add(moonPair.getKey());
		
        StackPane planetPane = new StackPane();
        planetPane.translateXProperty().bind(star.translateXProperty());
        planetPane.translateYProperty().bind(star.translateYProperty());
        planetPane.getChildren().add(moonPane);
        planetPane.getChildren().add(planetPair.getKey());

		solarSystemController.getPlanetGroup().getChildren().add(planetPane);
        
	}
	
	private Pair<? extends Planet, Pair<Ellipse,PathTransition>> addPlanetToStar(Node star, Point3D startHex){
		
		Planet planet = new Planet(50,24);
		planet.setDiffuseMap(MapGenTextures.DESERTTEXTURE);
		planet.placeAt(startHex);
		
//		SolarSystemModel.getInstance().planetMap.put(centerHex.getPoint(), earth);
//		solarSystemController.getPlanetGroup().getChildren().add(planet);
		
		Ellipse ellipsePlanet = new Ellipse();
//        ellipsePlanet.setCenterX(star.getTranslateX());
//        ellipsePlanet.setCenterY(star.getTranslateY());
//        ellipsePlanet.translateXProperty().bind(star.translateXProperty());
//        ellipsePlanet.translateYProperty().bind(star.translateYProperty());
        ellipsePlanet.setRadiusX(star.getBoundsInLocal().getWidth() / 2.0 + 1.01671388 * 170);
        ellipsePlanet.setRadiusY(star.getBoundsInLocal().getHeight() / 2.0 + 0.98329134 * 170);

        ellipsePlanet.setStrokeWidth(5);
        ellipsePlanet.setStroke(Color.GREEN);

//        ellipsePlanet.setRadiusX(270);
//        ellipsePlanet.setRadiusY(270);        

//		solarSystemController.getPlanetGroup().getChildren().add(ellipsePlanet);
         
        PathTransition transitionPlanet = new PathTransition();
        transitionPlanet.setPath(ellipsePlanet);
        transitionPlanet.setNode(planet);
        transitionPlanet.setInterpolator(Interpolator.LINEAR);
        transitionPlanet.setDuration(Duration.seconds(10.000017421));
        transitionPlanet.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transitionPlanet.setCycleCount(Timeline.INDEFINITE);
         
//        transitionPlanet.play();
        
        return new Pair<Planet,  Pair<Ellipse,PathTransition>>(planet, new Pair<Ellipse,PathTransition>(ellipsePlanet,transitionPlanet));
	}
	
	private Pair<? extends Planet, Pair<Ellipse,PathTransition>> addMoonToPlanet(Node planet, Point3D startHex){
		
		Planet moon = new Planet(15,24);
		moon.setDiffuseMap(MapGenTextures.TUNDRATEXTURE);
		moon.placeAt(startHex);
		
//		SolarSystemModel.getInstance().planetMap.put(centerHex.getPoint(), moon);
//		solarSystemController.getPlanetGroup().getChildren().add(moon);
        
         
        Ellipse ellipseMoon = new Ellipse();
//        ellipseMoon.setCenterX(planet.getTranslateX());
//        ellipseMoon.setCenterY(planet.getTranslateY());
//        ellipseMoon.translateXProperty().bind(planet.translateXProperty());
//        ellipseMoon.translateYProperty().bind(planet.translateYProperty());
        ellipseMoon.setRadiusX(planet.getBoundsInLocal().getWidth() / 2.0 + 1.01671388 * 70);
        ellipseMoon.setRadiusY(planet.getBoundsInLocal().getHeight() / 2.0 + 0.98329134 * 70);

        ellipseMoon.setStrokeWidth(5);
        ellipseMoon.setStroke(Color.BLUE);
        
//        ellipseMoon.setRadiusX(270);
//        ellipseMoon.setRadiusY(270);

//		solarSystemController.getPlanetGroup().getChildren().add(ellipseMoon);
         
        PathTransition transitionMoon = new PathTransition();
        transitionMoon.setPath(ellipseMoon);
        transitionMoon.setNode(moon);
        transitionMoon.setInterpolator(Interpolator.LINEAR);
        transitionMoon.setDuration(Duration.seconds(1.000017421));
        transitionMoon.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transitionMoon.setCycleCount(Timeline.INDEFINITE);
//        transitionMoon.play();
        
        return new Pair<Planet, Pair<Ellipse,PathTransition>>(moon, new Pair<Ellipse,PathTransition>(ellipseMoon,transitionMoon));
	}
}