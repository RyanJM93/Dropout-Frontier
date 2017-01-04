package com.rjm.dropout.frontier.enums;

import com.sun.xml.internal.ws.util.StringUtils;

public enum MapSize {
	SMALL(66, 42), STANDARD(80, 52), LARGE(104, 64), HUGE(128, 80)
	
	;
	
	int x, y = 0;
	
	MapSize(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	@Override
	public String toString(){
		return StringUtils.capitalize(name().toLowerCase());
	}
}
