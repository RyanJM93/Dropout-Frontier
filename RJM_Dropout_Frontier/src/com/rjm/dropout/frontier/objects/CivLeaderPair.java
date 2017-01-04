package com.rjm.dropout.frontier.objects;

import javafx.util.Pair;

public class CivLeaderPair extends Pair<ICivilization, ILeader> {
	
	private static final long serialVersionUID = 1L;

	public CivLeaderPair(ICivilization civ, ILeader leader) {
		super(civ, leader);
	}
	
	@Override
	public String toString(){
		return new String(getKey() + " - " + getValue()); 
	}
}
