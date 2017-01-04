package com.rjm.dropout.frontier.enums;

import javafx.scene.image.Image;

public enum UnitButtonType {
	FOUNDCITY("Found a city", new Image("images/frontier/settlerIcon.png")), // TODO Temp icon
	FOUNDCHURCH("Found a church", new Image("images/frontier/foundChurchIcon.png")), // TODO Temp icon
	MOVETO("Move to", new Image("images/frontier/moveToIcon.png")),
	ATTACK("Attack", new Image("images/frontier/attackIcon.png")),
	RANGEDATTACK("Ranged Attack", new Image("images/frontier/attackRangedIcon.png")),
	PILLAGE("Pillage", new Image("images/frontier/pillageIcon.png")),
	PILLAGEROAD("Pillage Road", new Image("images/frontier/pillageIcon.png")),
	RANKUP("Rank-up", new Image("images/frontier/rankUpIcon.png")),
	SKIPTURN("Skip Turn", new Image("images/frontier/skipTurnIcon.png")),
	SLEEP("Sleep", new Image("images/frontier/sleepIcon.png")),
	FORTIFY("Fortify", new Image("images/frontier/fortifyIcon.png")),
	ALERT("Fortify on Alert", new Image("images/frontier/alertIcon.png")),
	AUTOEXPLORE("Auto-explore", new Image("images/frontier/exploreIcon.png")),
	PATROL("Patrol Waypoints", new Image("images/frontier/patrolIcon.png")),	
	WAKE("Wake-up", new Image("images/frontier/wakeIcon.png")),	
	;
	
	Image icon;
	String name;
	
	private UnitButtonType(String name,Image image) {
		this.name = name;
		this.icon = image;
	}
	
	public String getName(){
		return name;
	}
	
	public Image getIcon(){
		return icon;
	}
}
