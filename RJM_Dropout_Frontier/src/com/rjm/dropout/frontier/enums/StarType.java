package com.rjm.dropout.frontier.enums;

import javafx.scene.paint.Color;

public enum StarType {
	// From smallest to largest
	
	BLACKDWARF(60, Color.BLACK, Color.DARKVIOLET), WHITEDWARF(80, Color.WHITE, Color.BLUEVIOLET), BLUEDWARF(100, Color.BLUE, Color.LIGHTBLUE),
	REDDWARF(120, Color.RED, Color.ORANGERED), YELLOW(150, Color.YELLOW, Color.ORANGE), REDGIANT(200, Color.RED, Color.DARKRED),
	MASSIVESTAR(300, Color.WHITE, Color.LIGHTGRAY), REDSUPERGIANT(400, Color.RED, Color.INDIANRED);
	
	private double radius;
	private Color color;
	private Color glow;
	
	StarType(double radius, Color color, Color glow){
		this.radius = radius;
		this.color = color;
		this.glow = glow;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public Color getColor(){
		return color;
	}
	
	public Color getGlow(){
		return glow;
	}
}
