package com.rjm.dropout.frontier.enums;

import javafx.scene.paint.Color;

public enum PlanetType {
	
	SMALLROCK(30, Color.DIMGRAY, 120), LARGEROCK(45, Color.DIMGRAY, 120), GAS(70, Color.GOLDENROD, 120), GASGIANT(100, Color.ORANGE, 120),
	
	MERCURY(30, Color.DIMGRAY, 87.969), VENUS(60, Color.DARKOLIVEGREEN, 224.7), EARTH(65, Color.STEELBLUE,  365.2564), MARS(50, Color.RED, 687),
	JUPITER(100, Color.ORANGE, 4332.59), SATURN(70, Color.GOLDENROD, 10759), URANUS(70, Color.SEAGREEN, 30688.5), NEPTUNE(70, Color.BLUE, 60182),
	PLUTO(15, Color.LIGHTGRAY, 90583.5872)
	
	;
	
	private double radius;
	private Color color;
	private double dayLength;
	
	PlanetType(double radius, Color color, double dayLength){
		this.radius = radius;
		this.color = color;
		this.dayLength = dayLength;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public Color getColor(){
		return color;
	}
	
	public double getDayLength(){
		return dayLength;
	}
}
