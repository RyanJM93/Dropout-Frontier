package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.rjm.dropout.frontier.enums.Terrain;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Civilization implements ICivilization {
	
	private String name;
	private String adj;
	private Color borderColor;
	private Image icon;
	private Stack<String> cityNames = new Stack<String>();
	private String capitalCity;
	private Stack<String> countyNames = new Stack<String>();
	private String capitalCounty;
	private List<Terrain> startTerrain = new ArrayList<Terrain>();

	public Civilization(String name, String adj) {
		this.name = name;
		this.adj = adj;
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public String getAdj(){
		return adj;
	}
	
	@Override
	public Color getBorderColor(){
		return borderColor;
	}
	
	public void setBorderColor(Color color){
		this.borderColor = color;
	}
	
	@Override
	public Image getIcon(){
		return icon;
	}
	
	public void setIcon(Image icon){
		this.icon = icon;
	}
	
	@Override
	public String getCapitalCity(){
		return capitalCity;
	}
	
	@Override
	public String getNextCityName(){
		String name = "";
		
		if(!cityNames.isEmpty()){
			name = cityNames.pop();
		}
		
		return name;
	}
	
	public void setCities(List<String> cities){
		capitalCity = cities.get(0);
		cities.remove(capitalCity);
		
		Collections.shuffle(cities);
		
		for(String cityName : cities){
			this.cityNames.push(cityName);
		}
		
		this.cityNames.push(capitalCity);
	}

	@Override
	public List<String> getCities() {
		return new ArrayList<String>(cityNames);
	}
	
	@Override
	public String getCapitalCounty(){
		return capitalCounty;
	}
	
	@Override
	public String getNextCountyName(){
		String name = "None";
		
		if(!countyNames.isEmpty()){
			name = countyNames.pop();
		}
		
		return name;
	}
	
	public void setCounties(List<String> counties){
		if (!counties.isEmpty()) {
			capitalCounty = counties.get(0);
			counties.remove(capitalCounty);
			Collections.shuffle(counties);
			for (String countyName : counties) {
				this.countyNames.push(countyName);
			}
			this.countyNames.push(capitalCounty);
		} else {
			capitalCounty = "null";
			this.countyNames.push("null");
			this.countyNames.push("null");
			this.countyNames.push("null");
		}
	}

	@Override
	public List<String> getCounties() {
		return new ArrayList<String>(countyNames);
	}
	
	@Override
	public List<Terrain> getStartTerrain(){
		return startTerrain;
	}
	
	public void setStartTerrain(List<Terrain> startTerrain){
		this.startTerrain = startTerrain;
	}
	
	@Override
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
