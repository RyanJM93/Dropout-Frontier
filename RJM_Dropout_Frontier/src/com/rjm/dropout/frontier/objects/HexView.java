package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.List;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.IGameController;
import com.rjm.dropout.frontier.MapGenColors;
import com.rjm.dropout.frontier.MapGenTextures;
import com.rjm.dropout.frontier.enums.Elevation;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.tasks.UpdateMinimapTask;
import com.rjm.dropout.frontier.utilities.DebugContextMenuController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class HexView extends MeshView {
	
	// Yields
	double food = 0;
	double gold = 0;
	double science = 0;
	
	public boolean isResource = false;
	BooleanProperty explored;
	
	public double x = 0.0;
	public double y = 0.0;
	
	public double mapRadius = 0.0;
	public double angleX = 0.0;
	public double angleY = 0.0;
	public double latitude = 0.0;
	
	private Color color = null;
	private Color cityColor = null;
	private Terrain terrain = null;
	private Color grid = null;
	private Elevation elevation = null;
	private Color mountainColor = null;
	private Image texture = null;
	
	private int movementCost = 1;
	private boolean impassable = false;
	
	private Point point = null;
	private List<Point> adjacentPoints = new ArrayList<Point>();
	
	private Point3D point3D;
	
	private PhongMaterial texturedMaterial = new PhongMaterial(MapGenColors.COAST);
	
	private Player owner = null;
	
	InnerShadow is;
	
	private BorderTile borderTile;
	
	private TileTooltipController tooltip;
	
	public HexView(Point3D point, IGameController controller){
		super(new Shape3DHexagon(120, 100));
		
		setTerrain(Terrain.OCEAN);
		
		setMaterial(texturedMaterial);
		
//		texturedMaterial.setSpecularColor(Color.WHITE);
		texturedMaterial.setSpecularPower(0);
		
		setTranslateX(point.getX());
		setTranslateY(point.getY());
		setTranslateZ(point.getZ());
		
		point3D = point;
		
		setCullFace(CullFace.BACK);
		
		is = new InnerShadow();
		is.setOffsetX(2.0f);
		is.setOffsetY(2.0f);
		
		setOnMouseClicked(event -> {
			if(event.getButton() == MouseButton.PRIMARY){
				String ownerString = (owner != null)?owner.getCivilization().getName():"null";
				System.out.println("Terrain: " + terrain.name() + ", Owner: " + ownerString + ", Point: " + getPoint());
				
				BorderTile mapTile = null;
				if (FrontierModel.getInstance().getFrontierGameController() != null) {
					mapTile = FrontierModel.getInstance().getFrontierGameController().getTileToMapTileMap()
							.getOrDefault(this, null);
				}
				
				if(mapTile != null){
					BorderTile lastMapTile = FrontierModel.getInstance().getFrontierGameController().getSelectedMapTile(); 
					if(lastMapTile != null){
						lastMapTile.setFillColor(lastMapTile.getHexView().getFillColor());
					}
					
					mapTile.setFillColor(Color.LIGHTPINK);
					FrontierModel.getInstance().getFrontierGameController().setSelectedMapTile(mapTile);
				}
				
				FrontierModel.getInstance().deselectUnit();
				FrontierModel.getInstance().deselectCity();
			}
			if (event.getButton() == MouseButton.PRIMARY && event.isControlDown()) {
								
				Player player = DebugContextMenuController.getInstance(this).getSelectedPlayer();
				System.out.println(player + " is now owner of this tile");
				
				this.setOwner(player);
				
				BorderTile existingBorder = this.getBorderTile();
				if(existingBorder != null){
					controller.getExploredGroup().getChildren().remove(existingBorder.getHexagon());
					controller.getExploredGroup().getChildren().removeAll(existingBorder.getBorderLines());
				}
				
				BorderTile newBorder = BorderTile.createBorderTile(this, false, controller.getExploredGroup());
				
				IHolding closestHolding = null;
				int closestDistance = 999;
				List<IHolding> ownersHoldings = getOwner().getHoldings();
				System.out.println("Owner has " + ownersHoldings.size() + " holdings");
				for(IHolding holding : ownersHoldings){
					int distanceBetween = FrontierModel.getInstance().distanceBetween(
							this.getPoint(), holding.getCenterBorder().getPoint());
					System.out.println("Distance to " + holding.getClass().getSimpleName() + ": " + distanceBetween);
					
					if(distanceBetween < closestDistance){
						closestHolding = holding;
						closestDistance = distanceBetween;
					}
				}
				
				if(closestHolding != null && !closestHolding.getBorders().contains(newBorder)){
					System.out.println("Added Border to nearest " + closestHolding.getClass().getSimpleName());
					
					closestHolding.addBorder(newBorder);
				}
				
			} else if(event.getButton() == MouseButton.SECONDARY){
				
			}
		});
		
		setOnDragDetected(event -> {
			
		});
		
		setOnDragOver(de -> {
			
		});
		
		HexView tempHex = this;
		
		setOnMouseEntered((me) -> {

			getTooltip().update();
			
			tempHex.texturedMaterial.setSpecularColor(Color.WHITE);
			tempHex.texturedMaterial.setSpecularPower(1);
			
			setEffect(is);
		});

		setOnMouseExited((me) -> {
			
			this.texturedMaterial.setSpecularColor(null);
			
			this.setEffect(null);
		});
	}
    
    public List<HexView> exploreTiles(int sight){
    	/*System.out.println("[" + this.x + "," + this.y + "]");*/
    	
    	List<HexView> visibleTiles = new ArrayList<HexView>();
    	
		if (!getExplored()) {
			setExplored(true);

			HexView resourceTile = FrontierModel.getInstance().resourceMap2.get(point);
			if (resourceTile != null) {
				resourceTile.setExplored(true);
			}
		}
		adjacentPoints.forEach(point -> {
			HexView adjacentTile = FrontierModel.getInstance().hexMap2.get(point);
			if (adjacentTile != null) {
				if (!adjacentTile.getExplored()) {
					adjacentTile.setExplored(true);

					HexView adjResourceTile = FrontierModel.getInstance().resourceMap2.get(adjacentTile.getPoint());
					if (adjResourceTile != null) {
						adjResourceTile.setExplored(true);
					}
				}

				if(sight > 0 && adjacentTile.getTerrain() != Terrain.JUNGLE
						&& adjacentTile.getTerrain() != Terrain.FOREST
						&& adjacentTile.getTerrain() != Terrain.TAIGA){
					visibleTiles.addAll(adjacentTile.exploreTiles(sight-1));
				}
				visibleTiles.add(adjacentTile);
			}
		});
//		FrontierGameController.getInstance().updateMiniMap();
		
		UpdateMinimapTask updateMinimapTask = new UpdateMinimapTask();
		updateMinimapTask.execute();
		
		return visibleTiles;
    }
	
	public void OLDsetTerrain(Terrain terrain){
		switch(terrain){
			case COAST:
				texturedMaterial.setDiffuseMap(MapGenTextures.COASTTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.COAST);
				break;
			case DESERT:
				texturedMaterial.setDiffuseMap(MapGenTextures.DESERTTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.DESERT);
				break;
			case FOREST:
				texturedMaterial.setDiffuseMap(MapGenTextures.FORESTTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.FOREST);
				break;
			case FRESHWATER:
				texturedMaterial.setDiffuseMap(MapGenTextures.COASTTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.COAST);
				break;
			case GRASSLAND:
				texturedMaterial.setDiffuseMap(MapGenTextures.GRASSLANDTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.GRASSLAND);
				break;
			case JUNGLE:
				texturedMaterial.setDiffuseMap(MapGenTextures.JUNGLETEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.JUNGLE);
				break;
			case MARSH:
				texturedMaterial.setDiffuseMap(MapGenTextures.MARSHTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.MARSH);
				break;
			case OCEAN:
				texturedMaterial.setDiffuseMap(MapGenTextures.OCEANTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.OCEAN);
				break;
			case SAVANNAH:
				texturedMaterial.setDiffuseMap(MapGenTextures.SAVANNAHTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.SAVANNAH);
				break;
			case SNOW:
				texturedMaterial.setDiffuseMap(MapGenTextures.SNOWTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.SNOW);
				break;
			case TAIGA:
				texturedMaterial.setDiffuseMap(MapGenTextures.TAIGATEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.TAIGA);
				break;
			case TUNDRA:
				texturedMaterial.setDiffuseMap(MapGenTextures.TUNDRATEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.TUNDRA);
				break;
			default:
				texturedMaterial.setDiffuseMap(MapGenTextures.OCEANTEXTURE);
				texturedMaterial.setDiffuseColor(MapGenColors.OCEAN);
				break;
		}
		
		texturedMaterial.setDiffuseColor(MapGenColors.SNOW);
		this.terrain = terrain;
	}
    
	public void setExplored(boolean bool) { 
    	exploredProperty().set(bool);
    	
    	if(FrontierModel.getInstance().getFrontierGameController() != null){
    		FrontierModel.getInstance().getFrontierGameController().addToMapGroup(this);
    	}
    }
    
    public boolean getExplored() { 
    	return exploredProperty().get(); 
    }
    
    public BooleanProperty exploredProperty() { 
        if (explored == null) {
        	explored = new SimpleBooleanProperty(this, "explored");
        }
        return explored; 
    } 

	public void setup(Terrain terrain, Elevation elevation) {
		this.setTerrain(terrain);
		this.setElevation(elevation);
		
		this.exploredProperty().addListener((obs, oldValue, newValue) -> {
			
			if(getExplored() == false){
//				hexagon.setFill(Color.TRANSPARENT);
				texturedMaterial.setDiffuseMap(MapGenTextures.UNEXPLOREDTEXTURE);
				texturedMaterial.setDiffuseColor(Color.DIMGRAY);
			} else {
				if(isResource){
					showTexture();
				} else {
					setColor();
				}
			}
		});
		
		exploredProperty().set(false);
	}
	
	public void setLocation(double x, double y){
		this.x = x;
		this.y = y;
		
		if(point3D == null){
			relocate(x, y);
		}
	}
	
	public void moveTo(Point3D point){
		point3D = point;
		setTranslateX(point.getX());
		setTranslateY(point.getY());
		setTranslateZ(point.getZ());
	}
	
	public Point3D getPoint3D(){
		return point3D;
	}

	public Color getFillColor() {
		return color;
	}
	
	public Color getGridColor() {
		return grid;
	}

	public void setColor() {
		if (this.terrain != null) {
			switch (this.terrain) {
			case COAST:
				this.color = MapGenColors.COAST;
				this.cityColor = MapGenColors.OCEANCITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.COASTTEXTURE);
				break;

			case OCEAN:
				this.color = MapGenColors.OCEAN;
				this.cityColor = MapGenColors.OCEANCITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.OCEANTEXTURE);
				break;

			case FRESHWATER:
				this.color = MapGenColors.FRESHWATER;
				this.cityColor = MapGenColors.FRESHWATERCITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(null);
				break;

			case GRASSLAND:
				this.color = MapGenColors.GRASSLAND;
				this.cityColor = MapGenColors.GRASSLANDCITY;
				this.setMountainColor(MapGenColors.GRASSLANDMTN);
				this.setTexture(MapGenTextures.GRASSLANDTEXTURE);
				break;

			case FOREST:
				this.color = MapGenColors.FOREST;
				this.cityColor = MapGenColors.FORESTCITY;
				this.setMountainColor(MapGenColors.FORESTMTN);
				this.setTexture(MapGenTextures.FORESTTEXTURE);
				break;

			case TAIGA:
				this.color = MapGenColors.TAIGA;
				this.cityColor = MapGenColors.TAIGACITY;
				this.setMountainColor(MapGenColors.TAIGAMTN);
				this.setTexture(MapGenTextures.TAIGATEXTURE);
				break;

			case JUNGLE:
				this.color = MapGenColors.JUNGLE;
				this.cityColor = MapGenColors.JUNGLECITY;
				this.setMountainColor(MapGenColors.JUNGLEMTN);
				this.setTexture(MapGenTextures.JUNGLETEXTURE);
				break;

			case SAVANNAH:
				this.color = MapGenColors.SAVANNAH;
				this.cityColor = MapGenColors.SAVANNAHCITY;
				this.setMountainColor(MapGenColors.SAVANNAHMTN);
				this.setTexture(MapGenTextures.SAVANNAHTEXTURE);
				break;

			case DESERT:
				this.color = MapGenColors.DESERT;
				this.cityColor = MapGenColors.DESERTCITY;
				this.setMountainColor(MapGenColors.DESERTMTN);
				this.setTexture(MapGenTextures.DESERTTEXTURE);
				break;

			case TUNDRA:
				this.color = MapGenColors.TUNDRA;
				this.cityColor = MapGenColors.TUNDRACITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.TUNDRATEXTURE);
				break;
			case SNOW:
				this.color = MapGenColors.SNOW;
				this.cityColor = MapGenColors.SNOWCITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.SNOWTEXTURE);
				break;
			case MARSH:
				this.color = MapGenColors.MARSH;
				this.cityColor = MapGenColors.MARSHCITY;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.MARSHTEXTURE);
				break;
			case ICECAP:
				this.color = MapGenColors.ICECAP;
				this.cityColor = MapGenColors.ICECAP;
				this.setMountainColor(Color.GRAY);
				this.setTexture(MapGenTextures.ICECAPTEXTURE);
				this.setImpassable(true);
				break;
			
			// Space
				
			case EMPTYSPACE:
				this.color = Color.TRANSPARENT;
				this.cityColor = Color.TRANSPARENT;
				this.setMountainColor(Color.TRANSPARENT);
				this.setTexture(MapGenTextures.EMPTYSPACETEXTURE);
				break;
				
			default:
				this.color = MapGenColors.SNOW;
				this.cityColor = MapGenColors.SNOW;
				this.setMountainColor(Color.GRAY);
				this.setTexture(null);
				break;
			}
		} else {
			this.color = MapGenColors.SNOW;
			this.cityColor = MapGenColors.SNOW;
			this.setMountainColor(Color.GRAY);
			this.setTexture(null);
		}
		
		this.grid = MapGenColors.OCEANCITY;
		
		if(getExplored()){
			texturedMaterial.setDiffuseMap(texture);
			texturedMaterial.setDiffuseColor(color);
		} else {
			texturedMaterial.setDiffuseMap(MapGenTextures.UNEXPLOREDTEXTURE);
			texturedMaterial.setDiffuseColor(Color.DIMGRAY);
		}
//		this.getHexagon().setFill(this.color);
	}

	public void setFillColor(Color color){
		this.color = color;
	}
	
	public void setGridColor(Color color){
		this.grid = color;
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
		setColor();
		
		int cost = 1;
		
		if(terrain == Terrain.FOREST || terrain == Terrain.JUNGLE || terrain == Terrain.MARSH || terrain == Terrain.TAIGA){
			cost+=1;
		}
		
		if(elevation == Elevation.hill){
			cost+=1;
		}
		
		setBaseYields();
		
		setMovementCost(cost);
		
//		if(point != null){
//			if(point.x == (FrontierModel.getInstance().BWIDTH/2) && point.y == (FrontierModel.getInstance().BHEIGHT/2)){
//				texturedMaterial.setDiffuseMap(MapGenTextures.GRASSLANDTEXTURE);
//				texturedMaterial.setDiffuseColor(MapGenColors.GRASSLAND);
//				exploreTiles(1);
//			}
//		}
	}

	public Color getCityColor() {
		return cityColor;
	}

	public void setCityColor(Color cityColor) {
		this.cityColor = cityColor;
	}

	public Elevation getElevation() {
		return elevation;
	}

	public void setElevation(Elevation elevation) {
		this.elevation = elevation;
	}

	public Color getMountainColor() {
		return mountainColor;
	}

	public void setMountainColor(Color mountainColor) {
		this.mountainColor = mountainColor;
	}

	public Image getTexture() {
		return texture;
	}
	
	public void setTexture(Image texture) {
		this.texture = texture;
	}
	
	public void showTexture(){
		texturedMaterial.setDiffuseMap(texture);
		texturedMaterial.setDiffuseColor(color);
	}

	public List<Point> getAdjacentPoints() {
		return adjacentPoints;
	}

	public void setAdjacentPoints(List<Point> adjacentPoints) {
		this.adjacentPoints = adjacentPoints;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
		
//		if(point != null){
//			if(point.x == (FrontierModel.getInstance().BWIDTH/2) && point.y == (FrontierModel.getInstance().BHEIGHT/2)){
//				texturedMaterial.setDiffuseMap(MapGenTextures.GRASSLANDTEXTURE);
//				texturedMaterial.setDiffuseColor(MapGenColors.GRASSLAND);
//				
//				Bounds boundsInScene = localToScene(this.getBoundsInLocal());
//				
//				FrontierGameController.boundsInScene = boundsInScene;
//				
//				System.out.println(boundsInScene);				
//			}
//		}
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
		
		if(owner != null){
			is.setColor(owner.getBorderColor());
		}
	}
	
	public String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }

	public Point3D findPoint(double radius, double angleX, double angleY, double latitude){

		double radianX = angleX * (Math.PI / 180);
		double radianY = angleY * (Math.PI / 180);

		Double x = radius * Math.cos(radianX) * Math.sin(radianY);
		Double y = radius * Math.sin(radianX) * Math.sin(radianY);
		Double z = radius * Math.cos(radianY);

		y = new Double(y + latitude);

		return new Point3D(x,y,z);
	}

	public int getMovementCost() {
		return movementCost;
	}

	public void setMovementCost(int movementCost) {
		this.movementCost = movementCost;
	}

	public BorderTile getBorderTile() {
		return borderTile;
	}

	public void setBorderTile(BorderTile borderTile) {
		this.borderTile = borderTile;
	}

	public boolean isImpassable() {
		return impassable;
	}

	public void setImpassable(boolean impassable) {
		this.impassable = impassable;
	}
	
	// Marks
	
	List<Node> tileMarks = new ArrayList<Node>();
	HBox markHBox = new HBox();
	public void addMark(Node mark){
		tileMarks.add(mark);
		markHBox.getChildren().add(mark);
		
		if(!tileMarks.isEmpty() && !FrontierModel.getInstance().getFrontierGameController().getExploredGroup().getChildren().contains(markHBox)){
			markHBox.setMouseTransparent(true);
			markHBox.setSpacing(4);
			markHBox.setAlignment(Pos.CENTER);
			Point3D textPoint = findPoint(this.mapRadius + 50, this.angleX-1, this.angleY+1, this.latitude);
			markHBox.setTranslateX(textPoint.getX());
			markHBox.setTranslateY(textPoint.getY());
			markHBox.setTranslateZ(textPoint.getZ());
			markHBox.getTransforms().clear();
			markHBox.getTransforms().addAll(this.getTransforms());
			markHBox.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
			markHBox.getTransforms().add(new Rotate(-30, Rotate.Z_AXIS));
			FrontierModel.getInstance().getFrontierGameController().getExploredGroup().getChildren().add(markHBox);
		}
	}
	public void clearMarks(){
		FrontierModel.getInstance().getFrontierGameController().getExploredGroup().getChildren().remove(markHBox);
		tileMarks.clear();
		markHBox.getChildren().clear();
	}

	// Yields

	private void setBaseYields() {
		food = 0;
		gold = 0;
		science = 0;
		
		switch(terrain){
			case COAST:
				addFoodYield(1);
				addGoldYield(1.5);
				break;
			case DESERT:
				break;
			case FOREST:
				addFoodYield(1);
				break;
			case FRESHWATER:
				addFoodYield(2);
				break;
			case GRASSLAND:
				addFoodYield(2);
				break;
			case ICECAP:
				break;
			case JUNGLE:
				addFoodYield(1);
				addScienceYield(1.5);
				break;
			case MARSH:
				addFoodYield(3);
				break;
			case OCEAN:
				addFoodYield(1);
				addGoldYield(2);
				break;
			case SAVANNAH:
				addFoodYield(1);
				break;
			case SNOW:
				break;
			case TAIGA:
				addFoodYield(1);
				break;
			case TUNDRA:
				break;
			default:
				break;
		}
	}
	
	public double getFoodYield() {
		return food;
	}
	private void addFoodYield(double value){
		this.food += value;
	}
	
	public double getGoldYield() {
		return gold;
	}
	private void addGoldYield(double value){
		this.gold += value;
	}
	
	public double getScienceYield() {
		return science;
	}
	private void addScienceYield(double value){
		this.science += value;
	}

	public TileTooltipController getTooltip() {
		return tooltip;
	}

	public void setTooltip(TileTooltipController tooltip) {
		this.tooltip = tooltip;
	}
}