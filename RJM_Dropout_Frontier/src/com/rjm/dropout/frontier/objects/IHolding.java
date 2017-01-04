package com.rjm.dropout.frontier.objects;

import java.util.List;

import javafx.scene.Node;

public interface IHolding {
	
	public Node getCity();
	
	public Point getLocation();

	void select();

	void deselect();

	int getSight();

	void updateVisibleHexs();
	
	void relocate(HexView hex);
	
	List<BorderTile> getBorders();
	
	List<BorderTile> getUnclaimedBorders();

	void addBorder(BorderTile border);

	BorderTile getCenterBorder();

	double getFoodYield();

	void addFoodYield(double value);

	double getGoldYield();

	void addGoldYield(double value);

	double getScienceYield();

	void addScienceYield(double value);
}
