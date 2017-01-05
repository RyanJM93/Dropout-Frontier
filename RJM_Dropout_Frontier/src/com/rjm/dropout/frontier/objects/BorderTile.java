package com.rjm.dropout.frontier.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.GlobalModel;
import com.rjm.dropout.frontier.utilities.FXMLUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class BorderTile {
	
	private Group exploredGroup;
	public Group getExploredGroup() {
		return exploredGroup;
	}
	public void setExploredGroup(Group exploredGroup) {
		this.exploredGroup = exploredGroup;
	}

	private IHolding holding;
	public void setHolding(IHolding holding){
		this.holding = holding;
	}
	public IHolding getHolding(){
		return holding;
	}

	public boolean isResource = false;
	BooleanProperty explored;

	public double x = 0.0;

	public double y = 0.0;

	private Color fill = null;
	private Color tFill = null;
	private Color grid = null;
	private double defaultWidth = 5.0;
	private double borderWidth = defaultWidth;

	private Point point = null;
	private List<Point> adjacentPoints = new ArrayList<Point>();

	@FXML // fx:id="hexagon"
	private Shape hexagon; // Value injected by FXMLLoader

	private HexView hexView;

	enum Position { N,NE,SE,S,SW,NW;}
	enum HexPosition { NW,NE,E,SE,SW,W;}

	HashMap<Position,BorderTile> borderMap = new HashMap<Position,BorderTile>();
	public List<BorderTile> getSurroundingBorders(){
		return new ArrayList<BorderTile>(borderMap.values());
	}

	Point3D point3D = null;

	Integer turnCreated;

	boolean isGridBorder = false;
	static double gridOffset = 10.0;
	
	boolean isMoveBorder = true;
	boolean isSurrounded = false;

	private CountyTile county;

	SimpleBooleanProperty deFacto = new SimpleBooleanProperty(false);
	public boolean isDeFacto(){
		return deFacto.get();
	}
	static List<Double> deFactoBorder = new ArrayList<Double>();
	static {
		deFactoBorder.add(10d);
		deFactoBorder.add(30d);
	}

	List<Line> borderLines = new ArrayList<Line>();
	public List<Line> getBorderLines(){
		return borderLines;
	}

	private Player owner;
	public Player getOwner() {
		return owner;
	}
	public void setOwner(Player owner) {
		this.owner = owner;
	}


	public static BorderTile createBorderTile(HexView hex, boolean isMoveBorder, Group exploredGroup){
		BorderTile borderTile = null;
		if (borderTile == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_BORDER_TILE));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_BORDER_TILE));

				borderTile = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}

			double newRadiusModifier = (isMoveBorder)?20:5;

			Point3D tilePoint3D = findPoint(hex.mapRadius+newRadiusModifier, hex.angleX, hex.angleY, hex.latitude);

			borderTile.setExploredGroup(exploredGroup);
			
			borderTile.isMoveBorder = isMoveBorder;
			if(!isMoveBorder){
				borderTile.defaultWidth = 0/*5.0*/;
				hex.setBorderTile(borderTile);
				borderTile.turnCreated = GlobalModel.getInstance().turn.get();
				borderTile.hexagon.getStrokeDashArray().addAll(deFactoBorder);
				borderTile.deFacto.set(true);
				borderTile.setPoint(hex.getPoint());
			} else {
				borderTile.defaultWidth = 0/*2.0*/;
			}

			borderTile.setHexView(hex);
			borderTile.moveTo(tilePoint3D);
			if(!isMoveBorder){
				borderTile.setup(hex.getOwner());
			} else {
				borderTile.setup(null);
			}
			borderTile.getHexagon().setMouseTransparent(!isMoveBorder);
			borderTile.getHexagon().getTransforms().addAll(hex.getTransforms());
			exploredGroup.getChildren().add(borderTile.getHexagon());
			exploredGroup.toFront();
		}
		borderTile.update();

		return borderTile;
	}
	public static BorderTile createGridTile(HexView hex, Group exploredGroup){
		BorderTile borderTile = null;
		if (borderTile == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_BORDER_TILE));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_BORDER_TILE));

				borderTile = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}

			double newRadiusModifier = 20;

			Point3D tilePoint3D = findPoint(hex.mapRadius+newRadiusModifier, hex.angleX, hex.angleY, hex.latitude);

			borderTile.setExploredGroup(exploredGroup);
			
			borderTile.isGridBorder = true;
			
			borderTile.defaultWidth = 0/*2.0*/;

			borderTile.setHexView(hex);
			borderTile.moveTo(tilePoint3D);

			borderTile.setupGridTile();
				
			borderTile.getHexagon().setMouseTransparent(true);
			borderTile.getHexagon().getTransforms().addAll(hex.getTransforms());
			exploredGroup.getChildren().add(borderTile.getHexagon());
			exploredGroup.toFront();
		}
		borderTile.update();

		return borderTile;
	}
	
	public static BorderTile createGridTileFlat(HexView hex, Group exploredGroup){
		BorderTile borderTile = null;
		if (borderTile == null) {

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(
						BorderTile.class.getClassLoader().getResource(FXMLUtils.FXML_BORDER_TILE));

				fxmlLoader.load(BorderTile.class.getClassLoader().getResourceAsStream(FXMLUtils.FXML_BORDER_TILE));

				borderTile = fxmlLoader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println(hex.getPoint3D());
			Point3D tilePoint3D = new Point3D(hex.getPoint3D().getX(), hex.getPoint3D().getY(), hex.getPoint3D().getZ()-gridOffset);
			System.out.println(tilePoint3D);

			borderTile.setExploredGroup(exploredGroup);
			
			borderTile.isGridBorder = true;
			
			borderTile.defaultWidth = 0/*2.0*/;

			borderTile.setHexView(hex);
			borderTile.moveTo(tilePoint3D);

			borderTile.setupGridTile();
				
			borderTile.getHexagon().setMouseTransparent(true);
			borderTile.getHexagon().getTransforms().addAll(hex.getTransforms());
			exploredGroup.getChildren().add(borderTile.getHexagon());
			exploredGroup.toFront();
		}
		borderTile.update();

		return borderTile;
	}

	@FXML
	void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			StringBuilder sb = new StringBuilder();
			sb.append("Surrounding Tiles: ");
			for(Position pos : borderMap.keySet()){
				sb.append(pos.name() + ", ");
			}
			System.out.println(sb.toString());
			//			drawBorder();
		} else if(event.getButton() == MouseButton.SECONDARY){
			FrontierModel.getInstance().moveUnitTo(getHexView());
		}
	}

	@FXML
	void dragDetected(MouseEvent event) {

	}

	@FXML
	void dragOver(DragEvent de){

	}

	@FXML
	void mouseEntered(MouseEvent event) {

	}

	@FXML
	void mouseExited(MouseEvent event) {

	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		this.hexagon.setMouseTransparent(true);
	}

	public void setup(Player owner) {
		if(owner != null){
			setOwner(owner);
			setFillColor(owner.getTerritoryColor());
			setGridColor(owner.getBorderColor());
		} else {
			setFillColor(Color.TRANSPARENT);
			setGridColor(Color.LIGHTBLUE);
		}
	}
	
	public void setupGridTile(){
		setFillColor(Color.TRANSPARENT);
		setGridColor(Color.WHITE);
	}

	public void setLocation(double x, double y){
		this.x = x;
		this.y = y;

		if(point3D == null){
			this.getHexagon().relocate(x, y);
		}
	}

	public void moveTo(Point3D point){
		point3D = point;
		this.getHexagon().setTranslateX(point.getX());
		this.getHexagon().setTranslateY(point.getY());
		this.getHexagon().setTranslateZ(point.getZ());
	}

	public Point3D getPoint3D(){
		return point3D;
	}

	public Color getFillColor() {
		return fill;
	}

	public Color getGridColor() {
		return grid;
	}

	public void setFillColor(Color color){
		this.fill = color;
		tFill = (color == Color.TRANSPARENT) ? color : Color.web(toRGBCode(color) + "80");
		this.hexagon.setFill(determineGradient(tFill));
	}

	public void setGridColor(Color color){
		this.grid = color;
		this.hexagon.setStroke(determineGradient(grid));
		this.hexagon.setStrokeWidth(borderWidth);
	}
	
	boolean isMapTile = false;
	public void setIsMapTile(boolean value){
		isMapTile = value;
	}
	public boolean isMapTile(){
		return isMapTile;
	}

	public Paint  determineGradient(Color color){

		if(isMapTile()){
			return getFillColor();
		}
		
		if(isContested()){
			System.out.println("Determining fill color as contested");
			if(contestedBy.size() == 1){
				return FrontierModel.getInstance().contestedFill(
						getOwner().getTerritoryColor(), 
						contestedBy.get(0).getTerritoryColor()
						);
			} else if(contestedBy.size() == 2){
				return FrontierModel.getInstance().contestedFill(
						getOwner().getTerritoryColor(), 
						contestedBy.get(0).getTerritoryColor(), 
						contestedBy.get(1).getTerritoryColor()
						);
			}
		}

		StringBuilder sb = new StringBuilder();
		boolean n = borderMap.getOrDefault(Position.N,null) != null ? true : false; 	if(n)sb.append("N,");
		boolean ne = borderMap.getOrDefault(Position.NE,null) != null ? true : false; 	if(ne)sb.append("NE,");
		boolean se = borderMap.getOrDefault(Position.SE,null) != null ? true : false; 	if(se)sb.append("SE,");
		boolean s = borderMap.getOrDefault(Position.S,null) != null ? true : false; 	if(s)sb.append("S,");
		boolean sw = borderMap.getOrDefault(Position.SW,null) != null ? true : false; 	if(sw)sb.append("SW,");
		boolean nw = borderMap.getOrDefault(Position.NW,null) != null ? true : false; 	if(nw)sb.append("NW,");

		boolean useLgLeft = false;
		boolean useLgRight = false;
		boolean useLgBoth = false;

		double lStartX = 0.0;
		double lStartY = 0.5f;
		double lEndX = 1.0;
		double lEndY = 0.5f;

		RadialGradient rg = null;
		LinearGradient lg = null;

		double focusAngle = 0;
		double centerX = 0.5;
		double centerY = 0.5;
		double radius = 0.5;

		double midStop = 0.3;

		isSurrounded = false;

		borderWidth = defaultWidth;
		if(n && !ne && !se && !s && !sw && !nw){			// N only
			centerY -= 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && s && !sw && !nw){		// S only
			centerY += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(n && !ne && !se && s && !sw && !nw){		// N & S
			useLgBoth = true;

			midStop = 0.5;
		} else if(!n && ne && !se && !s && !sw && !nw){		// NE only
			centerX -= 0.15;
			centerY -= 0.1;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && !s && sw && !nw){		// SW only
			centerX += 0.15;
			centerY += 0.1;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && !s && !sw && nw){		// NW only
			centerX += 0.15;
			centerY -= 0.1;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && se && !s && !sw && !nw){		// SE only
			centerX -= 0.15;
			centerY += 0.1;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && !s && sw && nw){		// NW & SW
			centerX += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && s && sw && nw){		// NW & SW & S
			centerX += 0.2;
			centerY += 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(n && !ne && !se && s && sw && nw){		// NW & SW & S & N
			useLgRight = true;

			lEndX = 0.8f;
			lEndY = 0.4f;

			midStop = 0.3;
		} else if(!n && ne && se && s && !sw && !nw){		// NE & SE & S
			centerX -= 0.2;
			centerY += 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(!n && ne && !se && s && !sw && nw){		// NW & NE & S
			radius = 0.7;
			midStop = 0.5;
		} else if(n && !ne && se && !s && sw && !nw){		// SW & SE & N
			radius = 0.7;
			midStop = 0.5;
		} else if(n && ne && se && s && !sw && !nw){		// NE & SE & S & N
			useLgLeft = true;

			midStop = 0.5;
		} else if(n && ne && se && s && !sw && nw){		// NE & SE & S & N & NW
			useLgLeft = true;

			lStartX = 0.1f;
			lStartY = 0.25f;

			lEndX = 0.9f;
			lEndY = 0.75f;

			midStop = 0.7;
		} else if(!n && ne && se && !s && !sw && !nw){		// NE & SE
			centerX -= 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && ne && !se && !s && !sw && nw){		// NE & NW
			centerY -= 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(!n && !ne && se && !s && sw && !nw){		// SE & SW
			centerY += 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(n && ne && se && !s && !sw && !nw){		// NE & SE & N
			centerX -= 0.2;
			centerY -= 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(n && !ne && !se && !s && sw && nw){		// NW & SW & N
			centerX += 0.2;
			centerY -= 0.1;
			radius = 0.7;
			midStop = 0.5;
		} else if(n && ne && !se && !s && !sw && !nw){		// N & NE
			centerX -= 0.1;
			centerY -= 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(n && !ne && se && !s && !sw && !nw){		// N & SE
			centerX -= 0.15;
			centerY -= 0.1;
			radius = 0.6;
			midStop = 0.5;
		} else if(n && !ne && !se && !s && sw && !nw){		// N & SW
			centerX += 0.15;
			centerY -= 0.1;
			radius = 0.6;
			midStop = 0.5;
		} else if(!n && ne && !se && s && !sw && !nw){		// S & NE
			centerX -= 0.15;
			centerY += 0.1;
			radius = 0.6;
			midStop = 0.5;
		} else if(!n && !ne && !se && s && !sw && nw){		// S & NW
			centerX += 0.15;
			centerY += 0.1;
			radius = 0.6;
			midStop = 0.5;
		} else if(n && !ne && !se && !s && !sw && nw){		// N & NW
			centerX += 0.1;
			centerY -= 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && se && s && !sw && !nw){		// S & SE
			centerX -= 0.1;
			centerY += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && !se && s && sw && !nw){		// S & SW
			centerX += 0.1;
			centerY += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(n && !ne && !se && s && sw && !nw){		// S & SW & N
			useLgBoth = true;

			lEndX = 0.9f;
			lEndY = 0.2f;

			midStop = 0.4;
		} else if(n && !ne && se && s && !sw && !nw){		// S & SE & N
			useLgBoth = true;

			lEndX = 0.9f;
			lEndY = 0.8f;

			midStop = 0.4;
		} else if(!n && !ne && se && s && sw && !nw){		// S & SW & SE
			centerY += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(!n && !ne && se && s && sw && nw){		// S & SW & SE & NW // TODO
			centerY += 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(n && ne && !se && !s && !sw && nw){		// N & NW & NE
			centerY -= 0.2;
			radius = 0.5;
			midStop = 0.5;
		} else if(n && ne && !se && s && !sw && nw){		// N & NW & NE & S
			useLgBoth = true;

			midStop = 0.2;
		} else if(n && ne && se && !s && sw && !nw){		// N & NE & SE & SW
			useLgBoth = true;

			lStartX = 0.2f;
			lStartY = 1.0f;

			lEndX = 0.8f;
			lEndY = 0.1f;

			midStop = 0.2;
		} else if(n && !ne && se && !s && sw && nw){		// N & NW & SW & SE
			useLgBoth = true;

			lStartX = 0.2f;
			lStartY = 0.1f;

			lEndX = 0.8f;
			lEndY = 1.0f;

			midStop = 0.2;
		} else if(n && !ne && se && s && sw && nw){		// N & NW & SW & SE & S
			useLgRight = true;

			lStartX = 0.1f;
			lStartY = 0.25f;

			lEndX = 0.9f;
			lEndY = 0.75f;

			midStop = 0.7;
		} else if(n && ne && se && s && sw && !nw){		// N & NE & SW & SE & S
			useLgLeft = true;

			lStartX = 0.1f;
			lStartY = 0.75f;

			lEndX = 0.9f;
			lEndY = 0.25f;

			midStop = 0.7;
		} else if(n && !ne && se && s && sw && !nw){		// S & SW & SE & N
			useLgBoth = true;

			midStop = 0.2;
		} else if(n && ne && !se && s && !sw && !nw){		// N & S & NE
			useLgBoth = true;

			lEndX = 0.8f;
			lEndY = 0.4f;

			midStop = 0.3;
		} else if(n && !ne && !se && s && !sw && nw){		// N & S & NW
			useLgBoth = true;

			lStartX = 0.2f;
			lStartY = 0.3f;

			midStop = 0.3;
		} else if(n && !ne && se && s && !sw && nw){		// N & S & NW & SE
			useLgBoth = true;

			lStartX = 0.1f;
			lStartY = 0.25f;

			lEndX = 0.9f;
			lEndY = 0.75f;

			midStop = 0.3;
		} else if(n && ne && !se && s && sw && !nw){		// N & S & SW & NE
			useLgBoth = true;

			lStartX = 0.1f;
			lStartY = 0.75f;

			lEndX = 0.9f;
			lEndY = 0.25f;

			midStop = 0.3;
		} else if(n && ne && se && s && sw && !nw){		// N & S & SW & NE & SE
			useLgLeft = true;

			lStartX = 0.1f;
			lStartY = 0.75f;

			lEndX = 0.9f;
			lEndY = 0.25f;

			midStop = 0.7;
		} else if(n && ne && se && s && sw && nw){			// Surrounded
			useLgBoth = false;
			useLgLeft = false;
			useLgRight = false;

			focusAngle = 0;
			centerX = 0.5;
			centerY = 0.5;
			radius = 2;
			borderWidth = 0.0;
			isSurrounded = true;
		} else if(!n && !ne && !se && !s && !sw && !nw){
			// No surrounding tiles
		} else {
			System.out.println("Could not find gradient for: " + sb.toString());
		}

		this.hexagon.setStrokeWidth(borderWidth);

		if (useLgBoth) {
			lg = new LinearGradient(
					lStartX, lStartY, 
					lEndX, lEndY, 
					true, 
					CycleMethod.NO_CYCLE,
					new Stop(0, color), 
					new Stop(midStop, Color.TRANSPARENT),
					new Stop(1-midStop, Color.TRANSPARENT),
					new Stop(1, color));
			return lg;
		} else if (useLgLeft) {
			lg = new LinearGradient(
					lStartX, lStartY, 
					lEndX, lEndY, 
					true, 
					CycleMethod.NO_CYCLE,
					new Stop(0, Color.TRANSPARENT), 
					new Stop(midStop, Color.TRANSPARENT),
					new Stop(1, color));
			return lg;
		} else if (useLgRight) {
			lg = new LinearGradient(
					lStartX, lStartY, 
					lEndX, lEndY, 
					true, 
					CycleMethod.NO_CYCLE,
					new Stop(0, color), 
					new Stop(1-midStop, Color.TRANSPARENT),
					new Stop(1, Color.TRANSPARENT));
			return lg;
		} else {
			rg = new RadialGradient(0,
					focusAngle, // focus angle
					centerX, // center x
					centerY, // center y
					radius, // radius
					true, 
					CycleMethod.NO_CYCLE, 
					new Stop(0, Color.TRANSPARENT), 
					new Stop(midStop, Color.TRANSPARENT),
					new Stop(1, color));
			return rg;
		}
	}

	public Shape getHexagon() {
		return hexagon;
	}

	public void setHexagon(Shape hexagon) {
		this.hexagon = hexagon;
	}

	public List<Point> getAdjacentPoints() {
		return adjacentPoints;
	}

	public void setAdjacentPoints(List<Point> adjacentPoints) {
		this.adjacentPoints = adjacentPoints;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public HexView getHexView() {
		return hexView;
	}

	private boolean sameDeFacto(BorderTile otherBorder){
		boolean isSame = false;

		if(otherBorder != null){
			isSame = (this.isDeFacto() == otherBorder.isDeFacto());
		}

		return isSame;
	}

	public List<Line> drawBorder(){
		Polygon hex = ((Polygon) this.getHexagon());
		List<Double> points = hex.getPoints();
		Point2D pointSE = new Point2D(points.get(0),points.get(1));
		Point2D pointSW = new Point2D(points.get(2),points.get(3));
		Point2D pointW = new Point2D(points.get(4),points.get(5));
		Point2D pointNW = new Point2D(points.get(6),points.get(7));
		Point2D pointNE = new Point2D(points.get(8),points.get(9));
		Point2D pointE = new Point2D(points.get(10),points.get(11));

		boolean n = borderMap.getOrDefault(Position.N,null) != null ? true : false;
		boolean nDF = sameDeFacto(borderMap.getOrDefault(Position.N,null));
		boolean ne = borderMap.getOrDefault(Position.NE,null) != null ? true : false;
		boolean neDF = sameDeFacto(borderMap.getOrDefault(Position.NE,null));
		boolean se = borderMap.getOrDefault(Position.SE,null) != null ? true : false;
		boolean seDF = sameDeFacto(borderMap.getOrDefault(Position.SE,null));
		boolean s = borderMap.getOrDefault(Position.S,null) != null ? true : false;
		boolean sDF = sameDeFacto(borderMap.getOrDefault(Position.S,null));
		boolean sw = borderMap.getOrDefault(Position.SW,null) != null ? true : false;
		boolean swDF = sameDeFacto(borderMap.getOrDefault(Position.SW,null));
		boolean nw = borderMap.getOrDefault(Position.NW,null) != null ? true : false;
		boolean nwDF = sameDeFacto(borderMap.getOrDefault(Position.NW,null));

		List<Line> lines = new ArrayList<Line>();

		if (!isContested()) {
			if (!n || !nDF) {
				lines.add(drawLine(pointNW, pointNE));
			}
			if (!ne || !neDF) {
				lines.add(drawLine(pointNE, pointE));
			}
			if (!se || !seDF) {
				lines.add(drawLine(pointE, pointSE));
			}
			if (!s || !sDF) {
				lines.add(drawLine(pointSE, pointSW));
			}
			if (!sw || !swDF) {
				lines.add(drawLine(pointSW, pointW));
			}
			if (!nw || !nwDF) {
				lines.add(drawLine(pointW, pointNW));
			} 
		}
		return lines;
	}
	
	public Line drawLine(Point2D start, Point2D end){
		
		if(isGridBorder){
			return drawLineFlat(start, end);
		}

		Line line = new Line(start.getX(),start.getY(),end.getX(),end.getY());
		line.setStrokeWidth((isGridBorder) ? 2.0 : 5.0);
		line.setStroke(getGridColor());

		if(isDeFacto()){
			line.getStrokeDashArray().addAll(BorderTile.deFactoBorder);
		} else {
			line.getStrokeDashArray().clear();
		}

		line.getTransforms().addAll(getHexagon().getTransforms());

		Point3D point3d = findPoint(hexView.mapRadius+10, hexView.angleX, hexView.angleY, hexView.latitude);

		line.setTranslateX(point3d.getX());
		line.setTranslateY(point3d.getY());
		line.setTranslateZ(point3d.getZ());

		return line;
	}
	
	public Line drawLineFlat(Point2D start, Point2D end){

		Line line = new Line(start.getX(),start.getY(),end.getX(),end.getY());
		line.setStrokeWidth((isGridBorder) ? 1.0 : 5.0);
		line.setStroke(getGridColor());

		if(isDeFacto()){
			line.getStrokeDashArray().addAll(BorderTile.deFactoBorder);
		} else {
			line.getStrokeDashArray().clear();
		}

		line.getTransforms().addAll(getHexagon().getTransforms());

		Point3D point3d = new Point3D(
				getHexView().getPoint3D().getX(), 
				getHexView().getPoint3D().getY(), 
				getHexView().getPoint3D().getZ()-gridOffset);

		line.setTranslateX(point3d.getX());
		line.setTranslateY(point3d.getY());
		line.setTranslateZ(point3d.getZ());

		return line;
	}

	public void update(){

		if(!isMoveBorder && !isGridBorder){
			enforce(GlobalModel.getInstance().getTurn());
		}

		Point thisPoint = hexView.getPoint();

		boolean yIsEven = (thisPoint.y%2 == 0) ? true : false;

		hexView.getAdjacentPoints().forEach(point -> {
			HexView adjHex = FrontierModel.getInstance().hexMap2.getOrDefault(point, null);

			if(adjHex != null && adjHex.getBorderTile() != null){
				adjHex.getBorderTile().enforce(GlobalModel.getInstance().getTurn());
			}

			if(adjHex != null && adjHex.getBorderTile() != null 
					&& adjHex.getBorderTile().isMoveBorder == this.isMoveBorder
					&& adjHex.getBorderTile().deFacto.get() == this.deFacto.get()){
				BorderTile adjTile = adjHex.getBorderTile();
				Point thatPoint = adjHex.getPoint();

				boolean stillGood = true;

				if(!this.isMoveBorder && this.getHexView().getOwner().getUniqueKey() 
						!= adjTile.getHexView().getOwner().getUniqueKey()){
					stillGood = false;
				}

				if (stillGood) {
					if (yIsEven) {
						if (thatPoint.x == thisPoint.x) {
							if (thatPoint.y == thisPoint.y - 2) { // N
								borderMap.put(Position.N, adjTile);
								adjTile.borderMap.put(Position.S, this);
							}
							if (thatPoint.y == thisPoint.y - 1) { // NE
								borderMap.put(Position.NW, adjTile);
								adjTile.borderMap.put(Position.SE, this);
							}
							if (thatPoint.y == thisPoint.y + 1) { // SE
								borderMap.put(Position.SW, adjTile);
								adjTile.borderMap.put(Position.NE, this);
							}
							if (thatPoint.y == thisPoint.y + 2) { // S
								borderMap.put(Position.S, adjTile);
								adjTile.borderMap.put(Position.N, this);
							}
						} else if (thatPoint.x == thisPoint.x - 1) {
							if (thatPoint.y == thisPoint.y + 1) { // SW
								borderMap.put(Position.SE, adjTile);
								adjTile.borderMap.put(Position.NW, this);
							}
							if (thatPoint.y == thisPoint.y - 1) { // NW
								borderMap.put(Position.NE, adjTile);
								adjTile.borderMap.put(Position.SW, this);
							}
						}
					} else {
						if (thatPoint.x == thisPoint.x) {
							if (thatPoint.y == thisPoint.y - 2) { // N
								borderMap.put(Position.N, adjTile);
								adjTile.borderMap.put(Position.S, this);
							}
							if (thatPoint.y == thisPoint.y - 1) { // NE
								borderMap.put(Position.NE, adjTile);
								adjTile.borderMap.put(Position.SW, this);
							}
							if (thatPoint.y == thisPoint.y + 1) { // SE
								borderMap.put(Position.SE, adjTile);
								adjTile.borderMap.put(Position.NW, this);
							}
							if (thatPoint.y == thisPoint.y + 2) { // S
								borderMap.put(Position.S, adjTile);
								adjTile.borderMap.put(Position.N, this);
							}
						} else if (thatPoint.x == thisPoint.x + 1) {
							if (thatPoint.y == thisPoint.y + 1) { // SW
								borderMap.put(Position.SW, adjTile);
								adjTile.borderMap.put(Position.NE, this);
							}
							if (thatPoint.y == thisPoint.y - 1) { // NW
								borderMap.put(Position.NW, adjTile);
								adjTile.borderMap.put(Position.SE, this);
							}
						}
					} 
				}
			}
		});

		this.hexagon.setFill(determineGradient(tFill));
		//		this.hexagon.setStroke(determineGradient(grid));
		this.hexagon.setStrokeWidth(0.0);

		if(!this.borderLines.isEmpty()){
			getExploredGroup().getChildren().removeAll(borderLines);
			borderLines.clear();
		}

		this.borderLines = drawBorder();
		getExploredGroup().getChildren().addAll(borderLines);
	}

	public void setHexView(HexView hexView) {
		this.hexView = hexView;

		hexView.getAdjacentPoints().forEach(point -> {
			HexView adjHex = FrontierModel.getInstance().hexMap2.getOrDefault(point, null);

			if(adjHex != null && adjHex.getBorderTile() != null && !adjHex.getBorderTile().isMoveBorder
					&& adjHex.getBorderTile().getCounty() == null){
				adjHex.getBorderTile().update();
			}
		});

		update();
	}

	public void enforce(int currentTurn){
		int delta = currentTurn - turnCreated;

		if(deFacto.get() && delta == 2 && holding instanceof City /*getCounty() == null*/){  // TODO Change delta to match game speed
			deFacto.set(false);
			System.out.println("Now Permanent");
		} else if(deFacto.get() && getCounty() != null){
			deFacto.set(false);
			System.out.println("Now Permanent");
		}
	}

	@Override
	public String toString(){
		return "[" + hexView.getPoint().x + "," + hexView.getPoint().y + "]";
	}

	public String toRGBCode(Color color) {
		return String.format( "#%02X%02X%02X",
				(int)( color.getRed() * 255 ),
				(int)( color.getGreen() * 255 ),
				(int)( color.getBlue() * 255 ) );
	}

	public static Point3D findPoint(double radius, double angleX, double angleY, double latitude){

		double radianX = angleX * (Math.PI / 180);
		double radianY = angleY * (Math.PI / 180);

		Double x = radius * Math.cos(radianX) * Math.sin(radianY);
		Double y = radius * Math.sin(radianX) * Math.sin(radianY);
		Double z = radius * Math.cos(radianY);

		y = new Double(y + latitude);

		return new Point3D(x,y,z);
	}

	public CountyTile getCounty() {
		return county;
	}

	public void setCounty(CountyTile county) {
		this.county = county;
	}

	List<Player> contestedBy;

	boolean isContested(){
		return (contestedBy != null);
	}
	public void addAnotherOwner(Player newOwner) {
		if(contestedBy == null){
			contestedBy = new ArrayList<Player>();
		}

		contestedBy.add(newOwner);

		update();
	}
}
