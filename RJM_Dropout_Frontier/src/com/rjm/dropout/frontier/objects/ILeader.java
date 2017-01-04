package com.rjm.dropout.frontier.objects;

import javafx.scene.paint.Color;

public interface ILeader {

	String getName();

	Color getTerritoryColor();

	ICivilization getCivilization();

	String toRGBCode(Color color);

}