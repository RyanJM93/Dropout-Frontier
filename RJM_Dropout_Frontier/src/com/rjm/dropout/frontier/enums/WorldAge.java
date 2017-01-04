package com.rjm.dropout.frontier.enums;

public enum WorldAge {
	ThreeBillion("3 Billion"), FourBillion("4 Billion"), FiveBillion("5 Billion")
	
	;
	
	String name;
	
	WorldAge(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
