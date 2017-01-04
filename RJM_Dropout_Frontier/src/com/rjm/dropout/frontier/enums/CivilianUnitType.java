package com.rjm.dropout.frontier.enums;

import javafx.scene.image.Image;

public enum CivilianUnitType {

	SETTLER(new Image("images/units/settlerIcon.png"), UnitClass.SETTLER),
	PROPHET(new Image("images/units/prophetIcon.png"), UnitClass.PROPHET),
	;
	
	private Image icon;
	private UnitClass unitClass;
	
	CivilianUnitType(Image icon, UnitClass unitClass) {
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
