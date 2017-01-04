package com.rjm.dropout.frontier;

import java.util.List;

import com.rjm.dropout.frontier.main.FrontierMainMenuController;
import com.rjm.dropout.frontier.objects.Player;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Bounds;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class AbsGameController implements IGameController {
	
	private Scene scene;
	@Override
	public Scene getScene() {
		return scene;
	}
	@Override
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
		});
	}
	
	private Stage stage;
	@Override
	public Stage getStage() {
		return stage;
	}
	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private Player player;
	@Override
	public Player getPlayer() {
		return player;
	}
	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	private FrontierMainMenuController frontierMainMenuController;
	@Override
	public FrontierMainMenuController getFrontierMainMenuController() {
		return frontierMainMenuController;
	}
	@Override
	public void setFrontierMainMenuController(FrontierMainMenuController frontierMainMenuController) {
		this.frontierMainMenuController = frontierMainMenuController;
	}
	
	// Common groups

	private Group exploredGroup = new Group();
	@Override
	public Group getExploredGroup() {
		return exploredGroup;
	}
	@Override
	public void setExploredGroup(Group exploredGroup) {
		this.exploredGroup = exploredGroup;
	}
	
	// Setters for injected objects

	// HUD

	private Button menuButton;
	public void setMenuButton(Button button){
		this.menuButton = button;
	}
	
	// Next Turn
	
	private HBox turnContent;
	public void setTurnContentHBox(HBox hBox){
		this.turnContent = hBox;
	}

	public PerspectiveCamera camera;
	public Camera mapCamera;

	Rotate rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
	Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
	Rotate rotateZ = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

	public SimpleIntegerProperty turn = new SimpleIntegerProperty(1);

	public static DoubleProperty scale = new SimpleDoubleProperty(1.0);
	public static Bounds boundsInScene;

	public SubScene subScene;

	static int SUBSCENEWIDTH = 0;
	static int SUBSCENEHEIGHT = 0;

	public AbsGameController() {
		// TODO Auto-generated constructor stub
	}

	@Override
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

	@Override
	public void nextTurn(){
//		FrontierModel.getInstance().processNextTurn(this);
	}
	
	@Override
	public void turnComplete(){
		System.out.println("Turn Complete!");

//		getTurnStackPane().setVisible(true); XXX temporary

		turn.set(turn.get()+1);

		if(!turnContent.getChildren().isEmpty()){
			turnContent.getChildren().clear();
		}
	}

	static boolean isGridOn = true;	

	@Override
	public void toggleGrid(){

		double strokeWidth = 0.0;

		if(!isGridOn){
			strokeWidth = 1.0;
			isGridOn = true;
		} else {
			strokeWidth = 0.0;
			isGridOn = false;
		}

		for(Node node : getExploredGroup().getChildren()){
			Polygon hexagon = (Polygon) node;
			hexagon.setStrokeWidth(strokeWidth);
		}
	}

	@Override
	public void exploreAllTiles(){

		// HexView
		FrontierModel.getInstance().hexMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().hexMap2.get(key).setExplored(true);
		});		

		FrontierModel.getInstance().resourceMap2.keySet().forEach(key -> {
			FrontierModel.getInstance().resourceMap2.get(key).setExplored(true);
		});
	}

	@Override
	public String toRGBCode(Color color) {
		return String.format( "#%02X%02X%02X",
				(int)( color.getRed() * 255 ),
				(int)( color.getGreen() * 255 ),
				(int)( color.getBlue() * 255 ) );
	}

}
