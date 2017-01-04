package com.rjm.dropout.frontier;

import javafx.scene.image.Image;

public class MapGenTextures {
	
	public static Image UNEXPLOREDTEXTURE = null;
	
	// Terrain
	public static Image COASTTEXTURE = null;
	public static Image DESERTTEXTURE = null;
	public static Image GRASSLANDTEXTURE = null;
	public static Image FORESTTEXTURE = null;
	public static Image JUNGLETEXTURE = null;
	public static Image MARSHTEXTURE = null;
	public static Image OCEANTEXTURE = null;
	public static Image SAVANNAHTEXTURE = null;
	public static Image SNOWTEXTURE = null;
	public static Image TAIGATEXTURE = null;
	public static Image TUNDRATEXTURE = null;
	public static Image ICECAPTEXTURE = null;

	public static Image EMPTYSPACETEXTURE = null;

	// Resources - Animals
	public static Image BEAR = null;
	public static Image CAMEL = null;
	public static Image CHICKEN = null;
	public static Image COD = null;
	public static Image DOLPHIN = null;
	public static Image ELEPHANT = null;
	public static Image FERRET = null;
	public static Image FOX = null;
	public static Image GOAT = null;
	public static Image HORSE = null;
	public static Image LEOPARDSEAL = null;
	public static Image LIONFISH = null;
	public static Image LOBSTER = null;
	public static Image MANTA = null;
	public static Image NESSY = null;
	public static Image PIG = null;
	public static Image RABBIT = null;
	public static Image SHARK = null;
	public static Image SHEEP = null;
	public static Image SHELLFISH = null;
	public static Image SQUID = null;
	public static Image TIGER = null;
	public static Image TUNA = null;
	public static Image WHALE = null;

	// Resources - Fruits
	public static Image BLUEAPRICOTgrassland = null;
	public static Image BLUEAPRICOTjungle = null;
	public static Image CITRONforest = null;
	public static Image CITRONgrassland = null;
	public static Image GAFFERdesert = null;
	public static Image GAFFERsavannah = null;
	public static Image GOREMELONgrassland = null;
	public static Image GOREMELONmarsh = null;
	public static Image MANDARINforest = null;
	public static Image MANDARINjungle = null;
	public static Image POMELOforest = null;
	public static Image POMELOmarsh = null;
	public static Image PORUMforest = null;
	public static Image PORUMjungle = null;
	public static Image UVATgrassland = null;
	public static Image UVATsavannah = null;
	
	public static void loadTextures(){
		if (UNEXPLOREDTEXTURE == null) {
			try {

				//			UNEXPLOREDTEXTURE = new Image("images/frontier/blankMapParchment.jpg");
				UNEXPLOREDTEXTURE = new Image("images/frontier/clouds.jpg");
				//			UNEXPLOREDTEXTURE = new Image("images/tileable-cloud-patterns-4.jpg");

				// Terrain
				COASTTEXTURE = new Image("images/terrain/coast.bmp");
				DESERTTEXTURE = new Image("images/terrain/desert.jpg");
				GRASSLANDTEXTURE = new Image("images/terrain/grassland.jpg");
				FORESTTEXTURE = new Image("images/terrain/forest.jpg");
				JUNGLETEXTURE = new Image("images/terrain/jungle.jpg");
				MARSHTEXTURE = new Image("images/terrain/marsh.jpg");
				OCEANTEXTURE = new Image("images/terrain/ocean.bmp");
				SAVANNAHTEXTURE = new Image("images/terrain/savannah.jpg");
				SNOWTEXTURE = new Image("images/terrain/snow.jpg");
				TAIGATEXTURE = new Image("images/terrain/taiga.jpg");
				TUNDRATEXTURE = new Image("images/terrain/tundra3.jpg");
				ICECAPTEXTURE = new Image("images/terrain/icecap.jpg");
				
				EMPTYSPACETEXTURE = new Image("images/terrain/emptyspace.png");

				// Resources - Animals
				BEAR = new Image("images/resources/animals/bearResource.png");
				CAMEL = new Image("images/resources/animals/camelResource.png");
				CHICKEN = new Image("images/resources/animals/chickenResource.png");
				COD = new Image("images/resources/animals/codResource.png");
				DOLPHIN = new Image("images/resources/animals/dolphinResource.png");
				ELEPHANT = new Image("images/resources/animals/elephantResource.png");
				FERRET = new Image("images/resources/animals/ferretResource.png");
				FOX = new Image("images/resources/animals/foxResource.png");
				GOAT = new Image("images/resources/animals/goatResource.png");
				HORSE = new Image("images/resources/animals/horseResource.png");
				LEOPARDSEAL = new Image("images/resources/animals/leopardsealResource.png");
				LIONFISH = new Image("images/resources/animals/lionfishResource.png");
				LOBSTER = new Image("images/resources/animals/lobsterResource.png");
				MANTA = new Image("images/resources/animals/mantaResource.png");
				NESSY = new Image("images/resources/animals/nessyResource.png");
				PIG = new Image("images/resources/animals/pigResource.png");
				RABBIT = new Image("images/resources/animals/rabbitResource.png");
				SHARK = new Image("images/resources/animals/sharkResource.png");
				SHEEP = new Image("images/resources/animals/sheepResource.png");
				SHELLFISH = new Image("images/resources/animals/shellfishResource.png");
				SQUID = new Image("images/resources/animals/squidResource.png");
				TIGER = new Image("images/resources/animals/tigerResource.png");
				TUNA = new Image("images/resources/animals/tunaResource.png");
				WHALE = new Image("images/resources/animals/whaleResource.png");

				// Resources - Fruits
				BLUEAPRICOTgrassland = new Image("images/resources/fruits/blueapricot-g.png");
				BLUEAPRICOTjungle = new Image("images/resources/fruits/blueapricot-j.png");
				CITRONforest = new Image("images/resources/fruits/citron-f.png");
				CITRONgrassland = new Image("images/resources/fruits/citron-g.png");
				//			GAFFERdesert = new Image("images/resources/fruits/gaffer-d.png");
				//			GAFFERsavannah = new Image("images/resources/fruits/gaffer-s.png");
				//			GOREMELONgrassland = new Image("images/resources/fruits/goremelon-g.png");
				//			GOREMELONmarsh = new Image("images/resources/fruits/goremelon-m.png");
				MANDARINforest = new Image("images/resources/fruits/mandarin-f.png");
				MANDARINjungle = new Image("images/resources/fruits/mandarin-j.png");
				POMELOforest = new Image("images/resources/fruits/pomelo-f.png");
				POMELOmarsh = new Image("images/resources/fruits/pomelo-m.png");
				PORUMforest = new Image("images/resources/fruits/porum-f.png");
				PORUMjungle = new Image("images/resources/fruits/porum-j.png");
				UVATgrassland = new Image("images/resources/fruits/uvat-g.png");
				UVATsavannah = new Image("images/resources/fruits/uvat-s.png");

			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}
