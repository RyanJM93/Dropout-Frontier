package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.rjm.dropout.frontier.FrontierModel;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape;

public abstract class AbsHolding implements IHolding {
	
	// Yields
	double food = 0;
	double gold = 0;
	double science = 0;
	
	private Player owner;
	
	private Point location;
	
	private Node city;
	private Shape namePlate;
	private Label nameLabel;
	
	private int sight = 2;
	
	private Set<HexView> visibleHexs = new HashSet<HexView>();
	
	private List<BorderTile> borders = new ArrayList<BorderTile>();
	private BorderTile centerBorder;
	
	public AbsHolding() {}
    
    public void setup(Player owner){
    	
    	setOwner(owner);
		
		setFillColor(owner.getTerritoryColor());
		setBorderColor(owner.getBorderColor());
    }
	
	public void setBorderColor(Color color){
		namePlate.setStroke(color);
		nameLabel.setTextFill(color);
	}
	
	public void setFillColor(Color color){
		namePlate.setFill(color);
	}
	
	void setPolygon(Shape shape){
		this.namePlate = shape;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void relocate(HexView hex){
		
		getVisibleHexs().forEach(tile -> {
			((PhongMaterial)tile.getMaterial()).setDiffuseColor(Color.web("#555555"));
		});
		
		FrontierModel.getInstance().updatePlayerVisibility(owner);
		
		setLocation(hex.getPoint());
		
		Point3D point = findPoint(hex.mapRadius+30, hex.angleX, hex.angleY, hex.latitude);
		
		city.setTranslateX(point.getX());
		city.setTranslateY(point.getY());
		city.setTranslateZ(point.getZ());
		
		city.getTransforms().clear();
		city.getTransforms().addAll(hex.getTransforms());
		
		if(!FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().contains(city)){
			FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().add(city);
		}
		
		FrontierModel.getInstance().getFrontierGameController().getUnitGroup().toFront();
		
		setVisibleHexs(new HashSet<HexView>(hex.exploreTiles(getSight()-1)));
		
		updateVisibleHexs();
	}
	
	public void updateVisibleHexs(){
		getVisibleHexs().forEach(tile -> {
			if(tile.getExplored()){
				((PhongMaterial)tile.getMaterial()).setDiffuseColor(tile.getFillColor());
			}
		});
	}

	public Label getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(Label symbol) {
		this.nameLabel = symbol;
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

	public Node getCity() {
		return city;
	}

	public void setCity(Node unit) {
		this.city = unit;
	}

	public void select(){
		FrontierModel.getInstance().setHoldingCity(this);
		namePlate.setEffect(new Glow(0.66));
	}
	
	public void deselect(){
		namePlate.setEffect(null);
	}

	public int getSight() {
		return sight;
	}

	public void setSight(int sight) {
		this.sight = sight;
	}
	
	public Set<HexView> getVisibleHexs() {
		return visibleHexs;
	}

	public void setVisibleHexs(Set<HexView> visibleHexs) {
		this.visibleHexs = visibleHexs;
	}

	public List<BorderTile> getBorders() {
		return borders;
	}

	public List<BorderTile> getUnclaimedBorders() {
		return borders.stream().filter(border -> border.getCounty() == null).collect(Collectors.toList());
	}

	public void setBorders(List<BorderTile> borders) {
		this.borders = borders;
	}
	
	public void addBorder(BorderTile border){
		this.borders.add(border);
		
		border.getHexView().getAdjacentPoints().forEach(point -> {
			HexView adjHex = FrontierModel.getInstance().hexMap2.getOrDefault(point, null);
			
			if(adjHex != null){
				getVisibleHexs().add(adjHex);
			}
		});
	}

	public BorderTile getCenterBorder() {
		return centerBorder;
	}

	public void setCenterBorder(BorderTile centerBorder) {
		this.centerBorder = centerBorder;
	}
	
	// Yields
	
	public double getFoodYield() {
		return food;
	}
	public void addFoodYield(double value){
		this.food += value;
	}
	
	public double getGoldYield() {
		return gold;
	}
	public void addGoldYield(double value){
		this.gold += value;
	}
	
	public double getScienceYield() {
		return science;
	}
	public void addScienceYield(double value){
		this.science += value;
	}
}
