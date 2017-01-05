package com.rjm.dropout.frontier;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.rjm.dropout.frontier.enums.DropoutFrontierScene;
import com.rjm.dropout.frontier.main.FrontierMainMenuController;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.Player;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class SolarSystemController implements IGameController {
	
	private FrontierGameController frontierGameController;

	private Scene scene;

	private Player player;

	private FrontierMainMenuController frontierMainMenuController;

	@FXML // fx:id="statsBorderHBox"
	private HBox statsBorderHBox; // Value injected by FXMLLoader

	@FXML // fx:id="statsTitledPane"
	private TitledPane statsTitledPane; // Value injected by FXMLLoader

	@FXML // fx:id="statsHBox"
	private HBox statsHBox; // Value injected by FXMLLoader

	// Gold

	@FXML // fx:id="goldStat"
	private Label goldStat; // Value injected by FXMLLoader

	@FXML // fx:id="goldAssetVBox"
	private VBox goldAssetVBox; // Value injected by FXMLLoader

	@FXML // fx:id="templateGoldAssetLabel"
	private Label templateGoldAssetLabel; // Value injected by FXMLLoader

	@FXML // fx:id="goldDebtVBox"
	private VBox goldDebtVBox; // Value injected by FXMLLoader

	@FXML // fx:id="templateGoldDebtLabel"
	private Label templateGoldDebtLabel; // Value injected by FXMLLoader

	@FXML // fx:id="goldTotal"
	private Label goldTotal; // Value injected by FXMLLoader

	// Science

	@FXML // fx:id="scienceStat"
	private Label scienceStat; // Value injected by FXMLLoader

	@FXML // fx:id="scienceAssetVBox"
	private VBox scienceAssetVBox; // Value injected by FXMLLoader

	@FXML // fx:id="templateScienceAssetLabel"
	private Label templateScienceAssetLabel; // Value injected by FXMLLoader

	@FXML // fx:id="scienceDebtVBox"
	private VBox scienceDebtVBox; // Value injected by FXMLLoader

	@FXML // fx:id="templateScienceDebtLabel"
	private Label templateScienceDebtLabel; // Value injected by FXMLLoader

	@FXML // fx:id="scienceTotal"
	private Label scienceTotal; // Value injected by FXMLLoader

	// HUD

	@FXML // fx:id="currentTime"
	private Label currentTime; // Value injected by FXMLLoader

	@FXML // fx:id="turnLabel"
	private Label turnLabel; // Value injected by FXMLLoader

	@FXML // fx:id="menuButton"
	private Button menuButton; // Value injected by FXMLLoader

	@FXML // fx:id="turnHBox"
	private HBox turnHBox; // Value injected by FXMLLoader

	@FXML // fx:id="turnTasksTabPane"
	private TabPane turnTasksTabPane; // Value injected by FXMLLoader

	@FXML // fx:id="unitsTab"
	private Tab unitsTab; // Value injected by FXMLLoader

	// Next Turn

	@FXML // fx:id="turnStackPane"
	private StackPane turnStackPane; // Value injected by FXMLLoader

	@FXML // fx:id="nextTurnLabel"
	private Label nextTurnLabel; // Value injected by FXMLLoader

	@FXML // fx:id="turnContent"
	private HBox turnContent; // Value injected by FXMLLoader

	@FXML
	void closeTurnDialog(){
		getTurnStackPane().setVisible(false);
	}

	// Pause Menu

	@FXML // fx:id="pauseStackPane"
	StackPane pauseStackPane; // Value injected by FXMLLoader

	@FXML
	void resumeGame(ActionEvent ae) {
		pauseStackPane.setVisible(false);
	}
	@FXML
	void quitToMainMenu(ActionEvent ae) {
		frontierMainMenuController.switchScene(DropoutFrontierScene.MAIN);
	}
	@FXML
	void quitToDesktop(ActionEvent ae) {
		Platform.exit();
	}
    
    // HUD

	@FXML // fx:id="gameStackPane"
	private StackPane gameStackPane; // Value injected by FXMLLoader
	@FXML // fx:id="background"
	private ImageView background; // Value injected by FXMLLoader

	Rectangle plain;

	private Rotate rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

	public PerspectiveCamera camera;
	public Camera mapCamera;

	private Group tilegroup = new Group();
	private Group planetGroup = new Group();
	private Group exploredGroup = new Group();
	private Group gridGroup = new Group();

	public List<Node> nodesToAddToScene = new ArrayList<>();
	
	public SubScene subScene;
	
	public static SolarSystemController createNewSolarSystem(FrontierGameController frontierGameController){
		
		// Setup Solar System Scene
		
		SolarSystemController solarSystemController = null;
		
		Parent solarSystemRoot = null;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					SolarSystemController.class.getClassLoader().getResource(FXMLUtils.FXML_SOLAR_SYSTEM));

			solarSystemRoot = fxmlLoader.load(
					SolarSystemController.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_SOLAR_SYSTEM));

			solarSystemController = fxmlLoader.getController();
			solarSystemController.setFrontierGameController(frontierGameController);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene frontierGameScene = new Scene(solarSystemRoot);
		frontierGameScene.getStylesheets().add("css/Frontier.css");
		solarSystemController.setScene(frontierGameScene);
		
		if(frontierGameController != null){
			solarSystemController.setPlayer(frontierGameController.getPlayer());
		}
		
		solarSystemController.launchSolarSystemGen();
		
		return solarSystemController;
	}
	
	public void enterSystem(){
		MediaModel.getInstance().playSolarSystemMusic();
		GlobalModel.getInstance().getStage().setScene(getScene());
	}

	// Yields

	SimpleDoubleProperty goldTreasury = new SimpleDoubleProperty(0);

	SimpleDoubleProperty goldAssets = new SimpleDoubleProperty(0);
	public void addGoldAsset(double value, String description){
		Label asset = new Label(new Double(value).toString() + " - " + description);
		asset.setTextFill(Color.web("#558300"));
		asset.setContentDisplay(ContentDisplay.LEFT);

		ImageView graphic = new ImageView(new Image("images/frontier/goldAssetIcon.png"));
		graphic.setFitWidth(16);
		graphic.setFitHeight(16);
		asset.setGraphic(graphic);

		goldAssetVBox.getChildren().add(asset);
	}
	SimpleDoubleProperty goldDebts = new SimpleDoubleProperty(0);
	public void addGoldDebt(double value, String description){
		Label debt = new Label(new Double(value).toString() + " - " + description);
		debt.setTextFill(Color.web("#bf4545"));
		debt.setContentDisplay(ContentDisplay.LEFT);

		ImageView graphic = new ImageView(new Image("images/frontier/goldDebtIcon.png"));
		graphic.setFitWidth(16);
		graphic.setFitHeight(16);
		debt.setGraphic(graphic);

		goldDebtVBox.getChildren().add(debt);
	}
	public void addGoldToTreasury(){
		goldTreasury.set(goldTreasury.get() + Double.parseDouble(goldTotal.getText()));

		goldAssetVBox.getChildren().clear();
		goldDebtVBox.getChildren().clear();

		scienceAssetVBox.getChildren().clear();
		scienceDebtVBox.getChildren().clear();
	}

	SimpleDoubleProperty scienceAssets = new SimpleDoubleProperty(0);
	public void addScienceAsset(double value, String description){
		Label asset = new Label(new Double(value).toString() + " - " + description);
		asset.setTextFill(Color.web("#558300"));
		asset.setContentDisplay(ContentDisplay.LEFT);

		ImageView graphic = new ImageView(new Image("images/frontier/scienceAssetIcon.png"));
		graphic.setFitWidth(16);
		graphic.setFitHeight(16);
		asset.setGraphic(graphic);

		scienceAssetVBox.getChildren().add(asset);
	}
	SimpleDoubleProperty scienceDebts = new SimpleDoubleProperty(0);
	public void addScienceDebt(double value, String description){
		Label debt = new Label(new Double(value).toString() + " - " + description);
		debt.setTextFill(Color.web("#bf4545"));
		debt.setContentDisplay(ContentDisplay.LEFT);

		ImageView graphic = new ImageView(new Image("images/frontier/scienceDebtIcon.png"));
		graphic.setFitWidth(16);
		graphic.setFitHeight(16);
		debt.setGraphic(graphic);

		scienceDebtVBox.getChildren().add(debt);
	}

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
		
		background.fitWidthProperty().bind(gameStackPane.widthProperty());
		background.fitHeightProperty().bind(gameStackPane.heightProperty());
		
		goldStat.textProperty().bind(goldTreasury.asString());

		goldAssetVBox.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
			goldAssets.set(0);
			double runningTotal = 0;
			for(Node node : goldAssetVBox.getChildren()){
				if(node instanceof Label){
					Label goldAsset = (Label) node;
					String[] text = goldAsset.getText().split("-");

					runningTotal += Double.parseDouble(text[0]);
				}
			}
			goldAssets.set(runningTotal);
		});
		goldDebtVBox.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
			goldDebts.set(0);
			double runningTotal = 0;
			for(Node node : goldDebtVBox.getChildren()){
				if(node instanceof Label){
					Label goldDebt = (Label) node;
					String[] text = goldDebt.getText().split("-");

					runningTotal += Double.parseDouble(text[0]);
				}
			}
			goldDebts.set(runningTotal);
		});

		scienceAssetVBox.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
			scienceAssets.set(0);
			double runningTotal = 0;
			for(Node node : scienceAssetVBox.getChildren()){
				if(node instanceof Label){
					Label scienceAsset = (Label) node;
					String[] text = scienceAsset.getText().split("-");

					runningTotal += Double.parseDouble(text[0]);
				}
			}
			scienceAssets.set(runningTotal);
		});

		scienceDebtVBox.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
			scienceDebts.set(0);
			double runningTotal = 0;
			for(Node node : scienceDebtVBox.getChildren()){
				if(node instanceof Label){
					Label scienceDebt = (Label) node;
					String[] text = scienceDebt.getText().split("-");

					runningTotal += Double.parseDouble(text[0]);
				}
			}
			scienceDebts.set(runningTotal);
		});

		goldTotal.textProperty().bind(goldAssets.subtract(goldDebts).asString());
		scienceTotal.textProperty().bind(scienceAssets.subtract(scienceDebts).asString());

		goldAssetVBox.getChildren().remove(templateGoldAssetLabel);
		goldDebtVBox.getChildren().remove(templateGoldDebtLabel);
		scienceAssetVBox.getChildren().remove(templateScienceAssetLabel);
		scienceDebtVBox.getChildren().remove(templateScienceDebtLabel);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd hh:mm aaa");
		System.out.println( sdf.format(cal.getTime()) );
		System.out.println(cal.getTime());

		currentTime.setText(sdf.format(cal.getTime()));

		Timeline timeline = new Timeline(new KeyFrame(
				Duration.millis(5000),
				ae -> {
					Calendar newCal = Calendar.getInstance();

					//                    System.out.println( sdf.format(newCal.getTime()) );
					//                    System.out.println(newCal.getTime());

					currentTime.setText(sdf.format(newCal.getTime()));
				}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		turnLabel.textProperty().bind(GlobalModel.getInstance().turnProperty().asString());
		nextTurnLabel.textProperty().bind(GlobalModel.getInstance().turnProperty().asString());

		//		hudParent.setPickOnBounds(false);
		//		hudBottom.setPickOnBounds(false);
		//		miniMap.setOnMouseClicked((MouseEvent) -> {
		//			if (isBloom) {
		//				miniMap.setEffect(null);
		//				isBloom = false;
		//			} else {
		//				miniMap.setEffect(new Bloom());
		//				isBloom = true;
		//			}
		//		});

		menuButton.setOnAction(ae -> {
			pauseStackPane.setVisible(true);
		});

		AtomicInteger scrollCount = new AtomicInteger(0);
		gameStackPane.setOnScroll((ScrollEvent se) -> {
			double zoomFactor = 1.0005;
			double deltaY = se.getDeltaY();
			
			double newZ = camera.getTranslateZ();

			if (deltaY < 0){
				System.out.println("Zooming Out");
				
				zoomFactor = 2.0 - zoomFactor;

				//				if(camera.getTranslateZ() > zoomMax){
				newZ = camera.getTranslateZ() - 1000;
				//				}
				//				camera.setFieldOfView(camera.getFieldOfView() + 5);
			} else {
				System.out.println("Zooming In");
				
//				if(camera.getTranslateZ() < -4000){
					newZ = camera.getTranslateZ() + 1000;
//				}
				//				camera.setFieldOfView(camera.getFieldOfView() - 5);
			}

			//			System.out.println("FOV: " + camera.getFieldOfView());

			final double z = new Double(newZ);

			//			System.out.println(camera.getTranslateZ());
			
			Timeline toPlanetTimeline = new Timeline(new KeyFrame(
					Duration.millis(1000),
					ae -> {
						System.out.println("Times scrolled = " + scrollCount.get());
						if(scrollCount.get() >= 15){
							// TODO transition to solar system scene
							System.out.println("Traveling to Planet Map");
							scrollCount.set(0);
							frontierGameController.enterPlanetMap();
						}
						scrollCount.set(0);
					}));

			Timeline zoomTimeline = new Timeline(new KeyFrame(
					Duration.millis(1),
					ae -> {

//						double oldTranslateZ = camera.getTranslateZ();

						camera.setTranslateZ(z);

						Bounds tileGroupBoundsAfter = tilegroup.localToScreen(
								tilegroup.getBoundsInLocal());
						Bounds statsHBoxBoundsAfter = statsHBox.localToScreen(
								statsHBox.getBoundsInLocal());
						Bounds turnHBoxBoundsAfter = turnHBox.localToScreen(
								turnHBox.getBoundsInLocal());
						
//						if(z > oldTranslateZ && camera.getBoundsInLocal().intersects(tilegroup.getBoundsInLocal())){
//							camera.setTranslateZ(oldTranslateZ);
//						}

						if(z < 1 && tileGroupBoundsAfter.getMinY() > statsHBoxBoundsAfter.getMinY() 
								|| tileGroupBoundsAfter.getMaxY() < turnHBoxBoundsAfter.getMaxY()){
//							camera.setTranslateZ(oldTranslateZ);
							System.out.println("Space View!");
							if(toPlanetTimeline.getStatus() == Status.STOPPED){
								toPlanetTimeline.playFromStart();
							}
							scrollCount.set(scrollCount.get() + 1);
						}

					}));
			zoomTimeline.play();

			final double finalZoom = new Double(zoomFactor);
			Platform.runLater(new Runnable() {
				public void run() {
					scale.set(scale.get() * finalZoom);
				}
			});

			se.consume();
		});

		plain = new Rectangle(2160, 1080);
    }
    
	public SolarSystemGenerator3D generator3D;
	public void launchSolarSystemGen() {

		SolarSystemController thisController = this;
		
		Platform.runLater(new Runnable() {
			public void run() {
				SolarSystemModel.getInstance().resetMaps();
				generator3D = new SolarSystemGenerator3D(thisController);
				List<HexView> tiles = generator3D.setup();
				System.out.println("Num of Terrain Tiles: " + tiles.size());
				//		for(HexView tile : tiles){
				//			getTilegroup().getChildren().add(tile);
				//		}
				getTilegroup().getChildren().addAll(tiles);
				getTilegroup().setId("tileGroup");
				
				// Tweak Generation
//				FrontierModel.getInstance().createSpawnMaps();
//				FrontierModel.getInstance().findCoastlines3D();
//				FrontierModel.getInstance().widenCoastlines3D();
//				FrontierModel.getInstance().findMountains3D();
//				FrontierModel.getInstance().placeIceCaps3D();
				
//				List<HexView> resources = generator3D.placeResources(board);
//				System.out.println("Num of Resource Tiles: " + resources.size());
				
//				getResourceGroup().setId("resourceGroup");
//				getExploredGroup().setId("exploredGroup");
//				getUnitGroup().setId("unitGroup");
//				getResourceGroup().toFront();
				
				rotateX.setAngle(/*-5*/0);
				
				finalizeSystem();
				
				if (getFrontierGameController() != null) {
					getFrontierGameController().getStartButton().setDisable(false);
				}
			}
		});
	}

	public static DoubleProperty scale = new SimpleDoubleProperty(1.0);
	public static Bounds boundsInScene;

	private static int SUBSCENEWIDTH = 0;
	private static int SUBSCENEHEIGHT = 0;

	private void finalizeSystem(){

		getTilegroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getTilegroup().minWidth(0.0);
		getTilegroup().minHeight(0.0);
		nodesToAddToScene.add(getTilegroup());
		
		getPlanetGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getPlanetGroup().minWidth(0.0);
		getPlanetGroup().minHeight(0.0);
		nodesToAddToScene.add(getPlanetGroup());
		
		getExploredGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getExploredGroup().minWidth(0.0);
		getExploredGroup().minHeight(0.0);
		nodesToAddToScene.add(getExploredGroup());
		
		getGridGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getGridGroup().minWidth(0.0);
		getGridGroup().minHeight(0.0);
		nodesToAddToScene.add(getGridGroup());


//		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
//		getMapGroup().getChildren().add(ambientLight);

		camera = new PerspectiveCamera();
		camera.setTranslateX(-725);
		camera.setTranslateY(0);
		camera.setTranslateZ(0);

//		camera.setFieldOfView(5);

		if(boundsInScene != null){
			camera.setTranslateX(boundsInScene.getMinX()-600);
			camera.setTranslateY(boundsInScene.getMinY()-400);
			camera.setTranslateZ(boundsInScene.getMinZ());
		}

		mapCamera = new PerspectiveCamera();
		mapCamera.getTransforms().addAll(rotateX, rotateY, rotateZ);

		SUBSCENEWIDTH = /*92*BWIDTH*/ /*1280*/ new Double(gameStackPane.getWidth()).intValue();
		SUBSCENEHEIGHT = /*105*BHEIGHT*/ /*720*/ new Double(gameStackPane.getHeight()).intValue();

		subScene = createSolarSystemSubScene(nodesToAddToScene, Color.TRANSPARENT, camera, true, SUBSCENEWIDTH, SUBSCENEHEIGHT);
		subScene.widthProperty().bind(gameStackPane.widthProperty());
		subScene.heightProperty().bind(gameStackPane.heightProperty());

		gameStackPane.getChildren().add(1, subScene);
		
		rotateX.setAngle(-45);

		handleMouseEvents();
	}
	
	private double mousePosX, mousePosY = 0;
	private void handleMouseEvents() {
		subScene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		subScene.setOnMouseReleased((MouseEvent me) -> {
			
		});

		subScene.setOnMouseDragged((MouseEvent me) -> {
			double dx = (mousePosX - me.getSceneX());
			double dy = (mousePosY - me.getSceneY());

			if (me.isMiddleButtonDown()) {
				double rotation = (rotateX.getAngle() - (dy / plain.getHeight() * 360) * (12*Math.PI / 180));

				//				System.out.println("Angle: " + rotation);

				if(rotation > 0){
					//					rotateX.setAngle(Math.min(rotation, -5));
					if(rotation < -5){
						rotateX.setAngle(rotation);
					}
				} else {
					//					rotateX.setAngle(Math.max(rotation, -30));
					if(rotation > -60){
						rotateX.setAngle(rotation);
					}
				}
				//				rotateZ.setAngle(rotateZ.getAngle() - (dx / ground.getWidth() * -360) * (24*Math.PI / 180));
			}

			if(me.isPrimaryButtonDown()) {
				Camera camera = subScene.getCamera();

				double oldTranslateX = camera.getTranslateX();
				double oldTranslateY = camera.getTranslateY();

				camera.setTranslateX(oldTranslateX + (2*dx*(1/scale.get())));
				camera.setTranslateY(oldTranslateY + (2*dy*(1/scale.get())));

//				Bounds tileGroupBoundsAfter = getTilegroup().localToScreen(
//						getTilegroup().getBoundsInLocal());
//				Bounds statsHBoxBoundsAfter = statsHBox.localToScreen(
//						statsHBox.getBoundsInLocal());
//				Bounds turnHBoxBoundsAfter = turnHBox.localToScreen(
//						turnHBox.getBoundsInLocal());
//
//				if(dy > 0 && tileGroupBoundsAfter.getMaxY() < turnHBoxBoundsAfter.getMaxY()){
//					camera.setTranslateY(oldTranslateY);
//				} else if(dy < 0 && tileGroupBoundsAfter.getMinY() > statsHBoxBoundsAfter.getMinY()){
//					camera.setTranslateY(oldTranslateY);
//				}

				//				System.out.println("Camera Y: " + camera.getTranslateY());

//				rotateY.setAngle(rotateY.getAngle() + ((dx*(1/scale.get()))) / 16);

				if(camera.getTranslateX() >= 0){

				}
			}

			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});
	}

	@FXML
	void cameraRotate(MouseEvent event) {
		System.out.println(event.getButton().toString() + " Mouse Button Dragged");
	}

	private SubScene createSolarSystemSubScene(List<Node> nodes, Paint fillPaint, Camera camera, boolean msaa, double width, double height) {
		Group root = new Group();

		PointLight light = new PointLight(Color.WHITE);
		light.translateXProperty().bind(camera.translateXProperty());
		light.translateYProperty().bind(camera.translateYProperty());
		light.translateZProperty().bind(camera.translateZProperty());

//		AmbientLight ambientLight = new AmbientLight(Color.WHITE);

		root.getChildren().addAll(/*ambientLight,	*/light);
		root.getChildren().addAll(nodes);

		SubScene subScene = new SubScene(root, width, height, true, msaa ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
		subScene.setFill(fillPaint);
		subScene.setCamera(camera);

		return subScene;
	}

	public Group getTilegroup() {
		return tilegroup;
	}
	public void setTileGroup(Group tilegroup) {
		this.tilegroup = tilegroup;
	}

	public Group getPlanetGroup() {
		return planetGroup;
	}
	public void setPlanetGroup(Group planetGroup) {
		this.planetGroup = planetGroup;
	}

	public Group getExploredGroup() {
		return exploredGroup;
	}
	public void setExploredGroup(Group exploredGroup) {
		this.exploredGroup = exploredGroup;
	}

	public Group getGridGroup() {
		return gridGroup;
	}
	public void setGridGroup(Group gridGroup) {
		this.gridGroup = gridGroup;
	}
	
	public StackPane getTurnStackPane() {
		return turnStackPane;
	}
	public void setTurnStackPane(StackPane turnStackPane) {
		this.turnStackPane = turnStackPane;
	}
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public FrontierGameController getFrontierGameController() {
		return frontierGameController;
	}
	public void setFrontierGameController(FrontierGameController frontierGameController) {
		this.frontierGameController = frontierGameController;
	}
	public Scene getScene() {
		return scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;

		scene.setOnKeyPressed((KeyEvent ke) -> {
			if(ke.getCode() == KeyCode.A){
				exploreAllTiles();
			}
			if(ke.getCode() == KeyCode.G){
				toggleGrid();
			}
			if(ke.getCode() == KeyCode.ESCAPE){
//				menuButton.fire();
			}
			if(ke.getCode() == KeyCode.F12){
//				exportAsPNG();
			}
		});
	}

	private void exploreAllTiles() {
		
		// HexView
		SolarSystemModel.getInstance().hexMap.keySet().forEach(key -> {
			SolarSystemModel.getInstance().hexMap.get(key).setExplored(true);
		});	
	}

	boolean isGridOn = false;	
	private void toggleGrid() {
		
		if(getGridGroup().getChildren().isEmpty()){
			SolarSystemModel.getInstance().hexMap.keySet().forEach(key -> {
				BorderTile.createGridTileFlat(SolarSystemModel.getInstance().hexMap.get(key), getGridGroup());
			});
		}
		

		if(!isGridOn){
			isGridOn = true;
		} else {
			isGridOn = false;
		}
		
		getGridGroup().setVisible(isGridOn);
		
	}

	@FXML // fx:id="buttonToolbar"
	private HBox buttonToolbar; // Value injected by FXMLLoader

	public void setupUnitTab(List<Button> buttons){
		buttonToolbar.getChildren().clear();
		buttonToolbar.getChildren().addAll(buttons);

		if(!turnTasksTabPane.getTabs().contains(unitsTab)){
			turnTasksTabPane.getTabs().add(unitsTab);
		}
		turnTasksTabPane.getSelectionModel().select(unitsTab);
	}

	public void removeUnitTab(){
		if(turnTasksTabPane.getTabs().contains(unitsTab)){
			turnTasksTabPane.getTabs().remove(unitsTab);
		}
	}
	
	@FXML
	void nextTurn(ActionEvent ae) {
		System.out.println("Next Turn");
		nextTurn();
	}

	void nextTurn(){
		SolarSystemModel.getInstance().processNextTurn(this);
	}

	void turnComplete(){
		System.out.println("Turn Complete!");

//		getTurnStackPane().setVisible(true); XXX temporary

		GlobalModel.getInstance().turnProperty().set(GlobalModel.getInstance().turnProperty().get()+1);

		if(!turnContent.getChildren().isEmpty()){
			turnContent.getChildren().clear();
		}
	}

	public String toRGBCode(Color color) {
		return String.format( "#%02X%02X%02X",
				(int)( color.getRed() * 255 ),
				(int)( color.getGreen() * 255 ),
				(int)( color.getBlue() * 255 ) );
	}
}