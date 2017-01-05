package com.rjm.dropout.frontier;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.rjm.dropout.frontier.enums.DropoutFrontierScene;
import com.rjm.dropout.frontier.enums.MapSize;
import com.rjm.dropout.frontier.enums.WorldAge;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.HexView;
import com.rjm.dropout.frontier.objects.IUnit;
import com.rjm.dropout.frontier.objects.Player;
import com.rjm.dropout.frontier.objects.Point;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class FrontierGameController implements IGameController {

	SolarSystemController solarSystemController;
	
	private Scene scene;

	private Player player;

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
		GlobalModel.getInstance().getFrontierMainMenuController().switchScene(DropoutFrontierScene.MAIN);
	}
	@FXML
	void quitToDesktop(ActionEvent ae) {
		Platform.exit();
	}
	
	// Load Screen

    @FXML // fx:id="loadStackPane"
    private StackPane loadStackPane; // Value injected by FXMLLoader

    @FXML // fx:id="loadScreenImageView"
    private ImageView loadScreenImageView; // Value injected by FXMLLoader

    @FXML // fx:id="bannerHBox"
    private HBox bannerHBox; // Value injected by FXMLLoader

    @FXML // fx:id="civilizationLabel"
    private Label civilizationLabel; // Value injected by FXMLLoader
    
    @FXML // fx:id="introductionLabel"
    private Label introductionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="leaderLabel"
    private Label leaderLabel; // Value injected by FXMLLoader

    @FXML // fx:id="leaderDescriptionLabel"
    private Label leaderDescriptionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="startButton"
    private Button startButton; // Value injected by FXMLLoader
    
    // HUD

	@FXML // fx:id="miniMap"
	private ImageView miniMap; // Value injected by FXMLLoader

	@FXML // fx:id="gameStackPane"
	private StackPane gameStackPane; // Value injected by FXMLLoader
	@FXML // fx:id="background"
	private ImageView background; // Value injected by FXMLLoader

	public MapGenerator3D generator3D;

	public PerspectiveCamera camera;
	public Camera mapCamera;

	static boolean isGridOn = true;	

	public SubScene subScene;

	private static int SUBSCENEWIDTH = 0;
	private static int SUBSCENEHEIGHT = 0;

	Rectangle ground;

	double startX = 0.0;
	double startY = 0.0;

	private Rotate rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

	private Group guiGroup = new Group();

	private Group resourceGroup = new Group();
	private Group tilegroup = new Group();
	private Group exploredGroup = new Group();
	private Group unitGroup = new Group();

	public List<Node> nodesToAddToScene = new ArrayList<>();
	
	public static FrontierGameController createNewPlanetMap(boolean switchToScene){
		
		// Setup Planet Map Scene
		
		FrontierGameController frontierGameController = null;
		
		Parent frontierGameRoot = null;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					FrontierGameController.class.getClassLoader().getResource(FXMLUtils.FXML_DROPOUT_FRONTIER_GAME));

			frontierGameRoot = fxmlLoader.load(
					FrontierGameController.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_DROPOUT_FRONTIER_GAME));

			frontierGameController = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene frontierGameScene = new Scene(frontierGameRoot);
		frontierGameScene.getStylesheets().add("css/Frontier.css");
		frontierGameController.setScene(frontierGameScene);

		frontierGameController.setPlayer(FrontierModel.getInstance().getPlayers().get(0));
		
		if(switchToScene){
			frontierGameController.setupLoadScreen();
			FrontierModel.getInstance().setFrontierGameController(frontierGameController);
			GlobalModel.getInstance().getStage().setScene(frontierGameScene);
		}
		

		if(FrontierModel.getInstance().BWIDTH > 0 && FrontierModel.getInstance().BHEIGHT > 0){
			FrontierModel.getInstance().BIOMEWEIGHT = FrontierModel.getInstance().BWIDTH - FrontierModel.getInstance().BHEIGHT;
			String[][] board = new String[FrontierModel.getInstance().BHEIGHT][FrontierModel.getInstance().BWIDTH];
			frontierGameController.launchMapGen(board);
		}
		return frontierGameController;
	}
	
	public void enterPlanetMap(){
		FrontierModel.getInstance().setFrontierGameController(this);
		MediaModel.getInstance().playPlanetMapMusic();
		GlobalModel.getInstance().getStage().setScene(getScene());
	}

	private double mousePosX, mousePosY = 0;
	private void handleMouseEvents() {
		subScene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		subScene.setOnMouseReleased((MouseEvent me) -> {
			getResourceGroup().toFront();
		});

		subScene.setOnMouseDragged((MouseEvent me) -> {
			double dx = (mousePosX - me.getSceneX());
			double dy = (mousePosY - me.getSceneY());

			if (me.isMiddleButtonDown()) {
				double rotation = (rotateX.getAngle() - (dy / ground.getHeight() * 360) * (12*Math.PI / 180));

				//				System.out.println("Angle: " + rotation);

				if(rotation > 0){
					//					rotateX.setAngle(Math.min(rotation, -5));
					if(rotation < -5){
						rotateX.setAngle(rotation);
					}
				} else {
					//					rotateX.setAngle(Math.max(rotation, -30));
					if(rotation > -15){
						rotateX.setAngle(rotation);
					}
				}
				//				rotateZ.setAngle(rotateZ.getAngle() - (dx / ground.getWidth() * -360) * (24*Math.PI / 180));
			}

			if(me.isPrimaryButtonDown()) {
				Camera camera = subScene.getCamera();

				double oldTranslateY = camera.getTranslateY();

				camera.setTranslateY(oldTranslateY + (2*dy*(1/scale.get())));

				Bounds tileGroupBoundsAfter = tilegroup.localToScreen(
						tilegroup.getBoundsInLocal());
				Bounds statsHBoxBoundsAfter = statsHBox.localToScreen(
						statsHBox.getBoundsInLocal());
				Bounds turnHBoxBoundsAfter = turnHBox.localToScreen(
						turnHBox.getBoundsInLocal());

				if(dy > 0 && tileGroupBoundsAfter.getMaxY() < turnHBoxBoundsAfter.getMaxY()){
					camera.setTranslateY(oldTranslateY);
				} else if(dy < 0 && tileGroupBoundsAfter.getMinY() > statsHBoxBoundsAfter.getMinY()){
					camera.setTranslateY(oldTranslateY);
				}

				//				System.out.println("Camera Y: " + camera.getTranslateY());

				rotateY.setAngle(rotateY.getAngle() + ((dx*(1/scale.get()))) / 16);

				if(camera.getTranslateX() >= 0){

				}
			}

			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});
	}

	public class AnimatedZoomOperator {

		private Timeline timeline;

		public AnimatedZoomOperator() {         
			this.timeline = new Timeline(60);
		}

		public void zoom(Node node, double factor, double x, double y) {    
			// determine scale
			double oldScale = node.getScaleX();
			double scale = oldScale * factor;
			double f = (scale / oldScale) - 1;

			// determine offset that we will have to move the node
			Bounds bounds = node.localToScene(node.getBoundsInLocal());
			double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
			double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

			// timeline that scales and moves the node
			timeline.getKeyFrames().clear();
			timeline.getKeyFrames().addAll(
					new KeyFrame(Duration.millis(5), new KeyValue(node.translateXProperty(), node.getTranslateX() - f * dx)),
					new KeyFrame(Duration.millis(5), new KeyValue(node.translateYProperty(), node.getTranslateY() - f * dy)),
					new KeyFrame(Duration.millis(5), new KeyValue(node.scaleXProperty(), scale)),
					new KeyFrame(Duration.millis(5), new KeyValue(node.scaleYProperty(), scale))
					);
			timeline.play();
		}
	}

	AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

	@FXML
	void cameraRotate(MouseEvent event) {
		System.out.println(event.getButton().toString() + " Mouse Button Dragged");
	}

	boolean isBloom = false;

	public static DoubleProperty scale = new SimpleDoubleProperty(1.0);
	public static Bounds boundsInScene;

	public Point3D worldCenterPoint;

	public static double zoomMin = -1000;
	public static double zoomMax = -8000;

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
		getStartButton().setOnAction((ae) -> {
			loadStackPane.setVisible(false);
		});
		
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

		MapGenTextures.loadTextures();

		AtomicInteger scrollCount = new AtomicInteger(0);
		gameStackPane.setOnScroll((ScrollEvent se) -> {
			double zoomFactor = 1.0005;
			double deltaY = se.getDeltaY();
			
			double newZ = camera.getTranslateZ();

			if (deltaY < 0){
				zoomFactor = 2.0 - zoomFactor;

				//				if(camera.getTranslateZ() > zoomMax){
				newZ = camera.getTranslateZ() - 1000;
				//				}
				//				camera.setFieldOfView(camera.getFieldOfView() + 5);
			} else {
				if(camera.getTranslateZ() < -4000){
					newZ = camera.getTranslateZ() + 1000;
				}
				//				camera.setFieldOfView(camera.getFieldOfView() - 5);
			}

			//			System.out.println("FOV: " + camera.getFieldOfView());

			final double z = new Double(newZ);

			//			System.out.println(camera.getTranslateZ());
			
			Timeline toSpaceTimeline = new Timeline(new KeyFrame(
					Duration.millis(1000),
					ae -> {
						System.out.println("Times scrolled = " + scrollCount.get());
						if(scrollCount.get() >= 15){
							// TODO transition to solar system scene
							System.out.println("Traveling to Solar System");
							
							try {
								SnapshotParameters param = new SnapshotParameters();
								param.setDepthBuffer(true);
								param.setFill(Color.TRANSPARENT);
								WritableImage snapshot = getMapGroup().snapshot(param, null);
								
								SolarSystemModel.getInstance().planetMap.values().forEach(planet -> {
									planet.setDiffuseMap(snapshot);
								});
								solarSystemController.enterSystem();
							} catch (IllegalArgumentException e) {

								String errMessage = "Map too big to screenshot";
								System.out.println(errMessage);
								
								Alert alert = new Alert(AlertType.ERROR);
								alert.setHeaderText("Asset loading error");
								alert.setContentText(errMessage);

								alert.showAndWait()
								.filter(response -> response == ButtonType.OK)
								.ifPresent(response -> alert.close());
							}
						}
						scrollCount.set(0);
					}));

			Timeline zoomTimeline = new Timeline(new KeyFrame(
					Duration.millis(1),
					ae -> {

						double oldTranslateZ = camera.getTranslateZ();

						camera.setTranslateZ(z);

						Bounds tileGroupBoundsAfter = tilegroup.localToScreen(
								tilegroup.getBoundsInLocal());
						Bounds statsHBoxBoundsAfter = statsHBox.localToScreen(
								statsHBox.getBoundsInLocal());
						Bounds turnHBoxBoundsAfter = turnHBox.localToScreen(
								turnHBox.getBoundsInLocal());

						if(z < 1 && tileGroupBoundsAfter.getMinY() > statsHBoxBoundsAfter.getMinY() 
								|| tileGroupBoundsAfter.getMaxY() < turnHBoxBoundsAfter.getMaxY()){
							camera.setTranslateZ(oldTranslateZ);
							System.out.println("Space View!");
							if(toSpaceTimeline.getStatus() == Status.STOPPED){
								toSpaceTimeline.playFromStart();
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

		ground = new Rectangle(2160, 1080);

		String oceanHexColor = "2E4172";
		gameStackPane.setStyle("-fx-background-color: #" + oceanHexColor);

		Background img = new Background(new BackgroundImage(
				MapGenTextures.OCEANTEXTURE, 
				BackgroundRepeat.REPEAT, 
				BackgroundRepeat.REPEAT, 
				BackgroundPosition.CENTER, 
				new BackgroundSize(0, 0, true, true, true, true)
				));
		gameStackPane.setBackground(img);
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
				menuButton.fire();
			}
			if(ke.getCode() == KeyCode.F12){
				exportAsPNG();
			}
		});
	}

	static File defaultDirectory = new File("C:/IPDE Development/Dropout/RJM_Dropout_Frontier/assets/images/screenshots");
	void exportAsPNG(){
		
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Export Map As PNG");
			fileChooser.setInitialFileName("map.png");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
			fileChooser.setInitialDirectory(defaultDirectory);

			final File savedPNGFile = fileChooser.showSaveDialog(GlobalModel.getInstance().getStage());

			if(savedPNGFile != null){
				Platform.runLater(new Runnable() {
					public void run() {
						saveMiniMapAsPNG(savedPNGFile);
					}
				});
				defaultDirectory = new File(savedPNGFile.getParent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMiniMapAsPNG(File savedPNGFile) {
		try {
			SnapshotParameters param = new SnapshotParameters();
			param.setDepthBuffer(true);
			param.setFill(Color.TRANSPARENT);
			WritableImage snapshot = getMapGroup().snapshot(param, null);
			BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);

			ImageIO.write(tempImg, "png", savedPNGFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	void nextTurn(ActionEvent ae) {
		System.out.println("Next Turn");
		nextTurn();
	}

	public void nextTurn(){
		FrontierModel.getInstance().processNextTurn(this);
	}

	public void turnComplete(){
		System.out.println("Turn Complete!");

//		getTurnStackPane().setVisible(true); XXX temporary

		GlobalModel.getInstance().turnProperty().set(GlobalModel.getInstance().turnProperty().get()+1);

		if(!turnContent.getChildren().isEmpty()){
			turnContent.getChildren().clear();
		}
	}

	public void launchMapGen(final String[][] board) {
		
		FrontierGameController thisController = this;

		Platform.runLater(new Runnable() {
			public void run() {
				
				solarSystemController = SolarSystemController.createNewSolarSystem(thisController);
				
				FrontierModel.getInstance().resetMaps();
				for (int i = 0; i < FrontierModel.getInstance().BHEIGHT; i++) {
					for (int j = 0; j < FrontierModel.getInstance().BWIDTH; j++) {
						board[i][j] = "";
					}
				}
				generator3D = new MapGenerator3D(thisController, MapSize.HUGE, WorldAge.FourBillion, board);
				List<HexView> tiles = generator3D.setup();
				System.out.println("Num of Terrain Tiles: " + tiles.size());
				//		for(HexView tile : tiles){
				//			getTilegroup().getChildren().add(tile);
				//		}
				getTilegroup().getChildren().addAll(tiles);
				getTilegroup().setId("tileGroup");
				FrontierModel.getInstance().createSpawnMaps();
				FrontierModel.getInstance().findCoastlines3D();
				FrontierModel.getInstance().widenCoastlines3D();
				FrontierModel.getInstance().findMountains3D();
				FrontierModel.getInstance().placeIceCaps3D();
				List<HexView> resources = generator3D.placeResources(board);
				System.out.println("Num of Resource Tiles: " + resources.size());
				//		for(HexTile hexTile : resources){
				//			if (hexTile.getHexagon() != null) {
				//				if (!getResourceGroup().getChildren().contains(hexTile.getHexagon())) {
				//					getResourceGroup().getChildren().add(hexTile.getHexagon());
				//				}
				//			}
				//		}
				getResourceGroup().setId("resourceGroup");
				getExploredGroup().setId("exploredGroup");
				getUnitGroup().setId("unitGroup");
				getResourceGroup().toFront();
				
				rotateX.setAngle(/*-5*/0);
				setPlayer(FrontierModel.getInstance().placePlayers().get(0));
				getPlayer().getUnits().addListener((ListChangeListener.Change<? extends IUnit> c) -> {
					Platform.runLater(new Runnable() {
						public void run() {
							if (!getPlayer().getUnits().isEmpty() && !turnTasksTabPane.getTabs().contains(unitsTab)) {
								turnTasksTabPane.getTabs().add(unitsTab);
							} else {
								if (turnTasksTabPane.getTabs().contains(unitsTab)) {
									turnTasksTabPane.getTabs().remove(unitsTab);
								}
							}
						}
					});
				});
				finalizeMap();
			}
		});
	}

	private void finalizeMap(){

		getTilegroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		nodesToAddToScene.add(getTilegroup());

		getResourceGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		nodesToAddToScene.add(getResourceGroup());

		getExploredGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getExploredGroup().minWidth(0.0);
		getExploredGroup().minHeight(0.0);
		nodesToAddToScene.add(getExploredGroup());

		getUnitGroup().getTransforms().addAll(rotateX, rotateY, rotateZ);
		getUnitGroup().minWidth(0.0);
		getUnitGroup().minHeight(0.0);
		nodesToAddToScene.add(getUnitGroup());


		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
		getMapGroup().getChildren().add(ambientLight);

		camera = new PerspectiveCamera();
		camera.setTranslateX(-725);
		camera.setTranslateY(0);
		camera.setTranslateZ(0);

		camera.setFieldOfView(5);
		//		camera.setFarClip(3.2);

		//		System.out.println("Camera: " + camera.localToScene(camera.getBoundsInLocal()));

		if(boundsInScene != null){
			camera.setTranslateX(boundsInScene.getMinX()-600);
			camera.setTranslateY(boundsInScene.getMinY()-400);
			camera.setTranslateZ(boundsInScene.getMinZ());
		}

		mapCamera = new PerspectiveCamera();
		mapCamera.getTransforms().addAll(rotateX, rotateY, rotateZ);

		SUBSCENEWIDTH = /*92*BWIDTH*/ /*1280*/ new Double(gameStackPane.getWidth()).intValue();
		SUBSCENEHEIGHT = /*105*BHEIGHT*/ /*720*/ new Double(gameStackPane.getHeight()).intValue();

		subScene = createSubScene(nodesToAddToScene, Color.TRANSPARENT, camera, true, /*8160*/SUBSCENEWIDTH, /*4080*/SUBSCENEHEIGHT);
		subScene.widthProperty().bind(gameStackPane.widthProperty());
		subScene.heightProperty().bind(gameStackPane.heightProperty());

		gameStackPane.getChildren().add(1, subScene);

		handleMouseEvents();
	}

	public SubScene createSubScene(List<Node> nodes, Paint fillPaint, Camera camera, boolean msaa, double width, double height) {
		Group root = new Group();

		PointLight light = new PointLight(Color.WHITE);
		light.translateXProperty().bind(camera.translateXProperty());
		light.translateYProperty().bind(camera.translateYProperty());
		light.translateZProperty().bind(camera.translateZProperty());

		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
		
		root.getChildren().addAll(ambientLight,	light);
		root.getChildren().addAll(nodes);

		SubScene subScene = new SubScene(root, width, height, true, msaa ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
		subScene.setFill(fillPaint);
		subScene.setCamera(camera);

		return subScene;
	}

	@FXML
	public void saveMapAsPNG() {

		WritableImage image = tilegroup.snapshot(new SnapshotParameters(), null);
		BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
		long systemTime = System.currentTimeMillis();

		try {
			if (ImageIO.write(bImage, "png", new File("C:/Users/rmelvil9/Documents/Saved Hex Maps/map" + systemTime + ".png")))
			{
				System.out.println("-- saved");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void toggleGrid(ActionEvent event){

		double strokeWidth = 0.0;

		if(!isGridOn){
			strokeWidth = 1.0;
			isGridOn = true;
		} else {
			strokeWidth = 0.0;
			isGridOn = false;
		}

		for(Node node : getTilegroup().getChildren()){
			Polygon hexagon = (Polygon) node;
			hexagon.setStrokeWidth(strokeWidth);

			((Polygon)getExploredGroup().getChildren().get(getTilegroup().getChildren().indexOf(node))).setStrokeWidth(strokeWidth);
		}
	}

	public void toggleGrid(){

//		double strokeWidth = 0.0;
//
//		if(!isGridOn){
//			strokeWidth = 1.0;
//			isGridOn = true;
//		} else {
//			strokeWidth = 0.0;
//			isGridOn = false;
//		}
//
//		for(Node node : getExploredGroup().getChildren()){
//			Polygon hexagon = (Polygon) node;
//			hexagon.setStrokeWidth(strokeWidth);
//		}
	}

	@FXML
	public void exploreAllTiles(ActionEvent event){

		// HexView
		FrontierModel.getInstance().hexMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().hexMap2.get(key).setExplored(true);
		});		

		FrontierModel.getInstance().resourceMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().resourceMap2.get(key).setExplored(true);
		});

		//		updateMiniMap();
	}

	public void exploreAllTiles(){

		// HexView
		FrontierModel.getInstance().hexMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().hexMap2.get(key).setExplored(true);
		});		

		FrontierModel.getInstance().resourceMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().resourceMap2.get(key).setExplored(true);
		});
	}

	public Group getTilegroup() {
		return tilegroup;
	}

	public void setTilegroup(Group tilegroup) {
		this.tilegroup = tilegroup;
	}

	public Group getGuiGroup() {
		return guiGroup;
	}

	public void setGuiGroup(Group guiGroup) {
		this.guiGroup = guiGroup;
	}

	public Group getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(Group resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	Group mapGroup = new Group();
	public Group getMapGroup(){
		return mapGroup;
	}

	HashMap<HexView, BorderTile> tileToMapTile = new HashMap<HexView, BorderTile>();
	private BorderTile selectedMapTile;
	public BorderTile getSelectedMapTile() {
		return selectedMapTile;
	}
	public void setSelectedMapTile(BorderTile selectedMapTile) {
		this.selectedMapTile = selectedMapTile;
	}
	
	public HashMap<HexView, BorderTile> getTileToMapTileMap(){
		return tileToMapTile;
	}
	public void addToMapGroup(HexView hex){
		BorderTile mapTile = createMapTile(hex);
		
		BorderTile existingTile = tileToMapTile.getOrDefault(hex, null);
		
		if(existingTile == null){
			tileToMapTile.put(hex, mapTile);
			
			mapGroup.getChildren().add(mapTile.getHexagon());
		}
	}

	public BorderTile createMapTile(HexView hex){
		BorderTile mapTile = null;
		if (mapTile == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_BORDER_TILE));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_BORDER_TILE));

				mapTile = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mapTile.setIsMapTile(true);

		mapTile.setExploredGroup(exploredGroup);
		
		Point point = hex.getPoint();
		
		boolean yIsEven = (point.y%2 == 0) ? true : false;

		double newX = 0.0;
		double newY = 0.0;

		newX = 180.0 * point.x;
		newY = 50.0 * point.y;
		
		if(!yIsEven){
			newX += 90.0;
		}
		mapTile.getHexagon().setTranslateX(-newX);
		mapTile.getHexagon().setTranslateY(newY);
		
		mapTile.setHexView(hex);
		
		mapTile.setFillColor(hex.getFillColor());

		return mapTile;
	}

//	public void updateMiniMap() {
//
//		SnapshotParameters params = new SnapshotParameters();
//				
//		Image img = new Image(getInstance().getClass().getClassLoader().getResourceAsStream(FXMLUtils.IMG_BLANK_MAP_PARCHMENT));
//		ImagePattern pattern = new ImagePattern(img);
//		params.setFill(pattern);
//
//		miniMap.setImage(getInstance().getMapGroup().snapshot(params, null));
//	}
	
	public void updateMiniMap(Image image){
		miniMap.setImage(image);
	}

	public Group getExploredGroup() {
		return exploredGroup;
	}

	public void setExploredGroup(Group exploredGroup) {
		this.exploredGroup = exploredGroup;
	}
	public Group getUnitGroup() {
		return unitGroup;
	}
	public void setUnitGroup(Group unitGroup) {
		this.unitGroup = unitGroup;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
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

	public StackPane getTurnStackPane() {
		return turnStackPane;
	}
	public void setTurnStackPane(StackPane turnStackPane) {
		this.turnStackPane = turnStackPane;
	}

	public String toRGBCode(Color color) {
		return String.format( "#%02X%02X%02X",
				(int)( color.getRed() * 255 ),
				(int)( color.getGreen() * 255 ),
				(int)( color.getBlue() * 255 ) );
	}
	
	public void setupLoadScreen(){
		
		String hexColor = toRGBCode(player.getTerritoryColor()) + "80";
		
		bannerHBox.setStyle("-fx-background-color: " + hexColor + ";");
		
		String leader = player.getLeader().getName().trim();
		leader = leader.replaceAll("\\s","");
		
		String url = "images/load/" + leader + ".png";
		System.out.println(url);
		
		Image image = null;
		try {
			image = new Image(url);
		} catch (IllegalArgumentException iae) {
			String errMessage = "Could not find leader screen in \"assets/images/load\" directory!";
			System.out.println(errMessage);
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Asset loading error");
			alert.setContentText(errMessage);

			alert.showAndWait()
			.filter(response -> response == ButtonType.OK)
			.ifPresent(response -> alert.close());
		}
		loadScreenImageView.setImage(image);
		
		civilizationLabel.setText(player.getCivilization().getName());
		
		introductionLabel.setText("Welcome to Dropout Frontier!");
		
		leaderLabel.setText(player.getLeader().getName());
		
		leaderDescriptionLabel.setText("Diarmait mac Cerbaill (died c. 565) was King of Tara or High King of Ireland. According to traditions, he was the last High King to follow the pagan rituals of inauguration, the ban-feis or marriage to goddess of the land."
				+ "While many later stories were attached to Diarmait, he was a historical ruler and his descendants were of great significance in Medieval Ireland. He is not to be confused with the later Diarmait mac Cerbaill (King of Osraige), son of king Cerball mac Dúnlainge.");
		
	}
	public Button getStartButton() {
		return startButton;
	}
	public void setStartButton(Button startButton) {
		this.startButton = startButton;
	}
}
