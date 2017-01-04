package com.rjm.dropout.frontier.objects;

import javafx.scene.paint.Color;

public class Leader implements ILeader {
	
	private String name;
	private Color territoryColor;
	private ICivilization civilization;
	
	public Leader(String name){
		this.name = name;
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public Color getTerritoryColor(){
		return territoryColor;
	}
	
	public void setTerritoryColor(Color color){
		this.territoryColor = color;
	}
	
	@Override
	public ICivilization getCivilization(){
		return civilization;
	}
	
	public void setCivilization(ICivilization civilization){
		this.civilization = civilization;
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
