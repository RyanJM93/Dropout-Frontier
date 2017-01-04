package com.rjm.dropout.frontier.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.rjm.dropout.frontier.objects.CivilizationIcons;
import com.rjm.dropout.frontier.objects.ICivilization;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public enum Civilization implements ICivilization {
	
	RANDOM("*Random Civilization", "Random", Color.LIGHTBLUE, CivilizationIcons.RANDOMICON, new String[]{"null"}),
	
	PHOENICIA("Phoenician Empire", "Phoenician", Color.WHITESMOKE, CivilizationIcons.PHOENICIA0ICON, 
			new String[]{"Tyre","Byblos"},
			Terrain.GRASSLAND, Terrain.DESERT),
	ROME("Roman Empire", "Roman", Color.GOLDENROD, CivilizationIcons.ROME0ICON,
			new String[]{"Rome","Atrium"},
			Terrain.GRASSLAND),
	UNITED_MONARCHY("United Monarchy", "Jewish", Color.STEELBLUE, CivilizationIcons.ISRAEL0ICON,
			new String[]{"Jerusalem","Hebron","Mahanaim","Gibeah"},
			Terrain.GRASSLAND, Terrain.DESERT),
	
	;
	
	private String name;
	private String adj;
	private Color borderColor;
	private Image icon;
	private Stack<String> cityNames = new Stack<String>();
	private String capitalCity;
	private List<Terrain> startTerrain = new ArrayList<Terrain>();
	
	Civilization(String name, String adj, Color borderColor, Image icon, String[] cityNames, Terrain... terrains){
		this.name = name;
		this.adj = adj;
		this.borderColor = borderColor;
		this.icon = icon;
		
		this.capitalCity = cityNames[0];
		
		List<String> cities = new ArrayList<String>(Arrays.asList(cityNames));
		cities.remove(capitalCity);
		
		Collections.shuffle(cities);
		
		for(String cityName : cities){
			this.cityNames.push(cityName);
		}
		
		this.cityNames.push(capitalCity);
		
		for(Terrain terrain : terrains){
			startTerrain.add(terrain);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getAdj(){
		return adj;
	}
	
	public Color getBorderColor(){
		return borderColor;
	}
	
	public Image getIcon(){
		return icon;
	}
	
	public String getCapitalCity(){
		return capitalCity;
	}
	
	public String getNextCityName(){
		String name = "";
		
		if(!cityNames.isEmpty()){
			name = cityNames.pop();
		}
		
		return name;
	}
	
	public List<String> getCities(){
		return new ArrayList<String>(cityNames);
	}
	
	public String getCapitalCounty(){
		return "null";
	}
	
	public String getNextCountyName(){		
		return "null";
	}
	
	public List<String> getCounties(){
		return Arrays.asList("null","null","null");
	}
	
	public List<Terrain> getStartTerrain(){
		return startTerrain;
	}
	
	public String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
	
	@Override
	public String toString(){
		return name;
	}
}
