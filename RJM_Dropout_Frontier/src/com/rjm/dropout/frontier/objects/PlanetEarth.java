package com.rjm.dropout.frontier.objects;

import javafx.scene.image.Image;

public class PlanetEarth extends SimpleCosmicBody {

	public PlanetEarth() {
		super(EARTH_RADIUS, ROTATE_SECS);
		setDiffuseMap(new Image(
						DIFFUSE_MAP,
						MAP_WIDTH,
						MAP_HEIGHT,
						true,
						true
						));
	}

}
