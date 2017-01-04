package com.rjm.dropout.frontier.enums;

import javafx.scene.image.Image;

public enum CombatUnitType {

	WARRIOR(new Image("images/units/warriorIcon.png"), UnitClass.MELEE);
	;

	private Image icon;
	private UnitClass unitClass;
	
	CombatUnitType(Image icon, UnitClass unitClass) {
		this.icon = icon;
		this.unitClass = unitClass;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public UnitClass getUnitClass() {
		return unitClass;
	}

	public void setUnitClass(UnitClass unitClass) {
		this.unitClass = unitClass;
	}

}
