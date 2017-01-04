package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.List;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.enums.UnitClass;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape;

public abstract class UnitAbs implements IUnit {
	
	private Player owner;
	
	private Point location;
	
	private UnitClass unitClass;
	
	private Node unit;
	private Shape shape;
	private ImageView icon;
	
	private boolean ableToMove = true;
	private int sight = 2;
	private int movement = 2;
	private double pillageCost = 1;
	private boolean canEmbark = false;
	private boolean canCrossOcean = false;
	
	private List<HexView> visibleHexs = new ArrayList<HexView>();
	
	protected SimpleIntegerProperty movementLeft = new SimpleIntegerProperty(movement);
	
	public UnitAbs() {}
    
    public void setup(Player owner){
    	
    	setOwner(owner);
		
		setFillColor(owner.getTerritoryColor());
		setBorderColor(owner.getBorderColor());
    }
	
	public void setBorderColor(Color color){
		shape.setStroke(color);
		
		Image image = icon.getImage();
		ImageView clip = new ImageView(image);
		clip.setFitWidth(50);
		clip.setFitHeight(50);
		
		icon.setClip(clip);

    	ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blush = new Blend(
                BlendMode.SRC_OVER,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        icon.getImage().getWidth(),
                        icon.getImage().getHeight(),
                        color	
                )
        );
        icon.setEffect(blush);       
        
//        icon.setCache(true);
//        icon.setCacheHint(CacheHint.SPEED);
	}
	
	public void setFillColor(Color color){
		shape.setFill(color);
	}
	
	void setPolygon(Shape shape){
		this.shape = shape;
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
			if(tile.getExplored()){
				((PhongMaterial)tile.getMaterial()).setDiffuseColor(Color.web("#555555"));
				System.out.println("Tile no longer visible");
			}
		});
		
		FrontierModel.getInstance().updatePlayerVisibility(owner);
		
		setLocation(hex.getPoint());
		
		Point3D point = findPoint(hex.mapRadius+5, hex.angleX, hex.angleY, hex.latitude);
		
		unit.setTranslateX(point.getX());
		unit.setTranslateY(point.getY());
		unit.setTranslateZ(point.getZ());
		
		unit.getTransforms().clear();
		unit.getTransforms().addAll(hex.getTransforms());
		
		if(!FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().contains(unit)){
			FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().add(unit);
		}
		
		FrontierModel.getInstance().getFrontierGameController().getUnitGroup().toFront();
		
		setVisibleHexs(hex.exploreTiles(getSight()-1));
		
		updateVisibleHexs();
	}
	
	public void updateVisibleHexs(){
		getVisibleHexs().forEach(tile -> {
			if(tile.getExplored()){
				((PhongMaterial)tile.getMaterial()).setDiffuseColor(tile.getFillColor());
			}
		});
	}

	public ImageView getIcon() {
		return icon;
	}

	public void setIcon(ImageView icon) {
		this.icon = icon;
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

	public Node getUnit() {
		return unit;
	}

	public void setUnit(Node unit) {
		this.unit = unit;
	}

	public void select(){
		if (ableToMove) {
			FrontierModel.getInstance().setSelectedUnit(this);
			unit.setEffect(new Glow(0.66));
		}
		
		FrontierModel.getInstance().getFrontierGameController().setupUnitTab(FrontierModel.getInstance().getUnitButtons(this));
	}
	
	public void deselect(){
		unit.setEffect(null);
		
		FrontierModel.getInstance().getFrontierGameController().removeUnitTab();
	}

	public boolean isAbleToMove() {
		return ableToMove;
	}

	public void setAbleToMove(boolean ableToMove) {
		this.ableToMove = ableToMove;
	}

	public int getSight() {
		return sight;
	}

	public void setSight(int sight) {
		this.sight = sight;
	}

	public int getMovement() {
		return movement;
	}

	public void setMovement(int movement) {
		this.movement = movement;
	}
	
	public int getMovementLeft() {
		return movementLeft.get();
	}
	
	public void decreaseMovement(int amount){
		movementLeft.set(movementLeft.get()-amount);
		
		if(movementLeft.get() <= 0){
			setAbleToMove(false);
			setBorderColor(owner.getBorderColor().darker());
			setFillColor(owner.getTerritoryColor().darker());
		}
	}
	
	public void resetMovement(){
		movementLeft.set(movement);
		setAbleToMove(true);
		setBorderColor(owner.getBorderColor());
		setFillColor(owner.getTerritoryColor());
	}

	public List<HexView> getVisibleHexs() {
		return visibleHexs;
	}

	public void setVisibleHexs(List<HexView> visibleHexs) {
		this.visibleHexs = visibleHexs;
	}

	public UnitClass getUnitClass() {
		return unitClass;
	}

	public void setUnitClass(UnitClass unitClass) {
		this.unitClass = unitClass;
	}

	public double getPillageCost() {
		return pillageCost;
	}

	public void setPillageCost(double pillageCost) {
		this.pillageCost = pillageCost;
	}

	public boolean canEmbark() {
		return canEmbark;
	}

	public void setCanEmbark(boolean canEmbark) {
		this.canEmbark = canEmbark;
	}

	public boolean canCrossOcean() {
		return canCrossOcean;
	}

	public void setCanCrossOcean(boolean canCrossOcean) {
		this.canCrossOcean = canCrossOcean;
	}
	
	public void delete(){
		getVisibleHexs().forEach(tile -> {
			((PhongMaterial)tile.getMaterial()).setDiffuseColor(Color.web("#555555"));
		});
		
		if(FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().contains(unit)){
			FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().remove(unit);
		}
		
		FrontierModel.getInstance().updatePlayerVisibility(owner);
	}
}
