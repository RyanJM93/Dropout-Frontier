package com.rjm.dropout.frontier.enums;

import com.sun.xml.internal.ws.util.StringUtils;

public enum MapType {
	CONTINENTS
	
	;
	
	@Override
	public String toString(){
		return StringUtils.capitalize(name().toLowerCase());
	}
}
