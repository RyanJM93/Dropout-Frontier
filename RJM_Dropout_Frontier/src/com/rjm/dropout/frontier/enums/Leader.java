package com.rjm.dropout.frontier.enums;

import com.rjm.dropout.frontier.objects.ILeader;

import javafx.scene.paint.Color;

public enum Leader implements ILeader {
	
	RANDOM("*Random Leader", Color.STEELBLUE, Civilization.RANDOM),
	
	HIRAM_I("Hiram I", Color.rgb(102, 2, 60) /*Tyrian Purple*/, Civilization.PHOENICIA),
	HADRIAN("Hadrian", Color.MAROON, Civilization.ROME),
	SOLOMON("Solomon", Color.WHITESMOKE, Civilization.UNITED_MONARCHY),
	
	;
	
	private String name;
	private Color territoryColor;
	private Civilization civilization;
	
	Leader(String name, Color territoryColor, Civilization civilization){
		this.name = name;
		this.territoryColor = territoryColor;
		this.civilization = civilization;
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public Color getTerritoryColor(){
		return territoryColor;
	}
	
	@Override
	public Civilization getCivilization(){
		return civilization;
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
