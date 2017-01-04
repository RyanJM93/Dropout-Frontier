package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.enums.UnitClass;

import javafx.scene.Node;

public interface IUnit {
	
	public UnitClass getUnitClass();
	
	public Node getUnit();
	
	public Player getOwner();
	
	public Point getLocation();

	void select();

	void deselect();

	int getSight();

	int getMovement();
	
	int getMovementLeft();
	void decreaseMovement(int amount);
	void resetMovement();
	double getPillageCost();
	boolean canEmbark();
	boolean canCrossOcean();

	void updateVisibleHexs();
	
	void relocate(HexView hex);

	public void delete();
}
