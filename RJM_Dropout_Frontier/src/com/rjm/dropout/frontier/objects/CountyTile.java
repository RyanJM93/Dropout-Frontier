package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.rjm.dropout.frontier.FrontierModel;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class CountyTile {
	
	private Player owner;
	
	Integer turnCreated;
	
	List<Line> borderLines = new ArrayList<Line>();
	public List<Line> getBorderLines(){
		return borderLines;
	}
	List<BorderTile> borders = new ArrayList<BorderTile>();
	public List<BorderTile> getBorderTiles(){
		return borders;
	}
	
	private String name;
	private Text nameText;
	
	public static CountyTile createCountyTile(List<BorderTile> borders, List<IHolding> connectedHoldings){
		System.out.println("City Borders: " + borders.size());
		
		CountyTile countyTile = new CountyTile();
		
		BorderTile firstBorder = borders.get(0);
		countyTile.setOwner(firstBorder.getHexView().getOwner());
		
		Color countyColor = countyTile.getOwner().getTerritoryColor();
		Random rand = new Random();
		int shadeDifference = rand.nextInt(6);
		boolean isDarker = rand.nextInt(2) == 0 ? true : false;
		int counter = 0;
		while(counter < shadeDifference){
			if(isDarker){
				countyColor = countyColor.darker();
			} else {
				countyColor = countyColor.brighter();
			}
			counter++;
		}
		
		for(BorderTile border : borders){
			if (border.getCounty() == null) {
				border.deFacto.set(false);
				border.setCounty(countyTile);
				border.setFillColor(countyColor);
			}
		}

		countyTile.borders = new ArrayList<BorderTile>(borders);
		
		String countyName = (countyTile.getOwner().getCivilization().getNextCountyName().equals("")) ? "null" :
			countyTile.getOwner().getCivilization().getNextCountyName();
		
		countyTile.setName(countyName);
		Text text = new Text(countyName);
		text.setFont(Font.font("Calibri",FontPosture.ITALIC, 60));
//		text.setOpacity(0.6);
		text.setFill(countyTile.getOwner().borderColor.darker());
		text.setStroke(Color.LIGHTGRAY);
		text.setStrokeWidth(0.5);
		text.setMouseTransparent(true);
		
		@SuppressWarnings("unused")
		Group tempGroup = new Group(text);
		double textWidth = text.getLayoutBounds().getWidth();
		double textHeight = text.getLayoutBounds().getHeight();
		text.setX(-(textWidth/2));
		text.setY(textHeight/2);
		
		HexView center = null;
		int currentWinner = -1;
		
		for(BorderTile availableBorder : borders){
			if(availableBorder.isSurrounded){
				int surroundingSurroundedTiles = availableBorder.borderMap.values().stream()
						.filter(adjBorder -> adjBorder.isSurrounded)
						.collect(Collectors.toList())
						.size();
				
				FrontierModel.getInstance().markTile(
						availableBorder.getHexView(),new Integer(surroundingSurroundedTiles).toString(), Color.RED.brighter(), 48);
				
				if(surroundingSurroundedTiles >= currentWinner){
					center = availableBorder.getHexView();
					currentWinner = surroundingSurroundedTiles;
				}
			}
		}
		
		if (center != null) {
			Point3D textPoint = findPoint(center.mapRadius + 50, center.angleX, center.angleY, center.latitude);
			text.setTranslateX(textPoint.getX());
			text.setTranslateY(textPoint.getY());
			text.setTranslateZ(textPoint.getZ());
			text.getTransforms().addAll(center.getTransforms());
			text.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
			text.getTransforms().add(new Rotate(-30, Rotate.Z_AXIS));
			
			countyTile.setNameText(text);
			
			FrontierModel.getInstance().getFrontierGameController().getExploredGroup().getChildren().add(new HBox(text));
		} else {
			System.out.println("Center is null");
		}
		
		countyTile.getOwner().newCounty(countyTile);
		
		return countyTile;
	}

	@FXML
	void mouseClicked(MouseEvent event) {
		
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
		
	}

	public void setup(Player owner) {
		
	}

	public void update(){
		
//		if(!this.borderLines.isEmpty()){
//			FrontierGameController.getInstance().getExploredGroup().getChildren().removeAll(borderLines);
//			borderLines.clear();
//		}
//		
//		this.borderLines = drawBorder();
//		FrontierGameController.getInstance().getExploredGroup().getChildren().addAll(borderLines);
	}
	
	@Override
	public String toString(){
		return "[" + getName() + " county]";
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

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Text getNameText() {
		return nameText;
	}

	public void setNameText(Text nameText) {
		this.nameText = nameText;
	}
}
