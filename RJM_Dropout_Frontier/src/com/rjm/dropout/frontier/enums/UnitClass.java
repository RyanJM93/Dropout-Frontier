package com.rjm.dropout.frontier.enums;

import java.util.ArrayList;
import java.util.List;

public enum UnitClass {

	SETTLER(UnitButtonType.FOUNDCITY,UnitButtonType.MOVETO,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	PROPHET(UnitButtonType.FOUNDCHURCH,UnitButtonType.MOVETO,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	MELEE(UnitButtonType.MOVETO,UnitButtonType.ATTACK,UnitButtonType.ALERT,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	RANGED(UnitButtonType.MOVETO,UnitButtonType.RANGEDATTACK,UnitButtonType.ALERT,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	CAVALRY(UnitButtonType.MOVETO,UnitButtonType.ATTACK,UnitButtonType.ALERT,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	ANITCAVALRY(UnitButtonType.MOVETO,UnitButtonType.ATTACK,UnitButtonType.ALERT,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	SUPPORT(UnitButtonType.MOVETO,UnitButtonType.ALERT,UnitButtonType.SLEEP,UnitButtonType.SKIPTURN),
	;
	
	List<UnitButtonType> buttonTypes = new ArrayList<UnitButtonType>();
	
	UnitClass(UnitButtonType... buttonTypes){
		for(UnitButtonType type : buttonTypes){
			this.buttonTypes.add(type);
		}
	}
	
	public List<UnitButtonType> getButtonTypes(){
		return buttonTypes;
	}
}
