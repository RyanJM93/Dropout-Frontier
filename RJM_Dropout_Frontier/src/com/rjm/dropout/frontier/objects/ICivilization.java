package com.rjm.dropout.frontier.objects;

import java.util.List;

import com.rjm.dropout.frontier.enums.Terrain;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface ICivilization {

	String getName();
	
	String getAdj();

	Color getBorderColor();

	Image getIcon();

	String getCapitalCity();

	String getNextCityName();
	
	List<String> getCities();
	
	String getCapitalCounty();
	
	String getNextCountyName();
	
	List<String> getCounties();

	List<Terrain> getStartTerrain();

	String toRGBCode(Color color);

}