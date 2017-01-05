package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.MapGenTextures;
import com.rjm.dropout.frontier.enums.Terrain;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class SimpleCosmicBody extends Sphere {

	private double dayLength = 0;

	protected static final double EARTH_RADIUS  = 90;
	protected static final double ROTATE_SECS   = 30;

	protected static final double MAP_WIDTH  = 8192 / 2d;
	protected static final double MAP_HEIGHT = 4092 / 2d;

	protected static final String DIFFUSE_MAP =
			"images/planets/earth_gebco8_texture_8192x4096.jpg";
	protected static final String NORMAL_MAP =
			"images/planets/earth_normalmap_flat_8192x4096.jpg";
	protected static final String SPECULAR_MAP =
			"images/planets/earth_specularmap_flat_8192x4096.jpg";

	PhongMaterial material = new PhongMaterial();

	private Ellipse path;
	private PathTransition pathAnimation;
	
	InnerShadow is;

	public SimpleCosmicBody(double radius, double dayLength) {
		super(radius);

		setDayLength(dayLength);

		getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		setMaterial(material);

		rotateAroundYAxis(this).play();
		
		is = new InnerShadow();
		is.setOffsetX(2.0f);
		is.setOffsetY(2.0f);
		
		setOnMouseEntered((me) -> {
			
			material.setSpecularColor(Color.WHITE);
			material.setSpecularPower(1);
			
			setEffect(is);
		});

		setOnMouseExited((me) -> {
			
			material.setSpecularColor(null);

			setEffect(null);
		});
	}

	public void setDiffuseMap(Image image){
		material.setDiffuseMap(image);
	}

	public void placeAt(Point3D point) {
		setTranslateX(point.getX());
		setTranslateY(point.getY());
		setTranslateZ(point.getZ());
	}

	public double getDayLength() {
		return dayLength;
	}

	public void setDayLength(double dayLength) {
		this.dayLength = dayLength;
	}

	protected RotateTransition rotateAroundYAxis(Node node) {
		RotateTransition rotate = new RotateTransition(
				Duration.seconds(getDayLength()), 
				node
				);
		rotate.setAxis(Rotate.Z_AXIS);
		rotate.setFromAngle(360);
		rotate.setToAngle(0);
		rotate.setInterpolator(Interpolator.LINEAR);
		rotate.setCycleCount(RotateTransition.INDEFINITE);

		return rotate;
	}

	public Ellipse getPath() {
		return path;
	}

	public void setPath(Ellipse path, double yearLength) {
		this.path = path;

		PathTransition transitionPlanet = new PathTransition();
		transitionPlanet.setPath(path);
		transitionPlanet.setNode(this);
		transitionPlanet.setInterpolator(Interpolator.LINEAR);
		transitionPlanet.setDuration(Duration.seconds(yearLength));
		transitionPlanet.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		transitionPlanet.setCycleCount(Timeline.INDEFINITE);
		
		setPathAnimation(transitionPlanet);
	}
	
	public void playOrbit(){
		getPathAnimation().play();
	}

	private PathTransition getPathAnimation() {
		return pathAnimation;
	}

	private void setPathAnimation(PathTransition pathAnimation) {
		this.pathAnimation = pathAnimation;
	}

	public void setBiome(Terrain terrain){
		switch (terrain) {
			case COAST:
				setDiffuseMap(MapGenTextures.COASTTEXTURE);
				break;
	
			case OCEAN:
				setDiffuseMap(MapGenTextures.OCEANTEXTURE);
				break;
	
			case FRESHWATER:
				setDiffuseMap(null);
				break;
	
			case GRASSLAND:
				setDiffuseMap(MapGenTextures.GRASSLANDTEXTURE);
				break;
	
			case FOREST:
				setDiffuseMap(MapGenTextures.FORESTTEXTURE);
				break;
	
			case TAIGA:
				setDiffuseMap(MapGenTextures.TAIGATEXTURE);
				break;
	
			case JUNGLE:
				setDiffuseMap(MapGenTextures.JUNGLETEXTURE);
				break;
	
			case SAVANNAH:
				setDiffuseMap(MapGenTextures.SAVANNAHTEXTURE);
				break;
	
			case DESERT:
				setDiffuseMap(MapGenTextures.DESERTTEXTURE);
				break;
	
			case TUNDRA:
				setDiffuseMap(MapGenTextures.TUNDRATEXTURE);
				break;
			case SNOW:
				setDiffuseMap(MapGenTextures.SNOWTEXTURE);
				break;
			case MARSH:
				setDiffuseMap(MapGenTextures.MARSHTEXTURE);
				break;
			case ICECAP:
				setDiffuseMap(MapGenTextures.ICECAPTEXTURE);
				break;
			
			// Space
				
			case EMPTYSPACE:
				setDiffuseMap(MapGenTextures.EMPTYSPACETEXTURE);
				break;
				
			default:
				setDiffuseMap(MapGenTextures.EMPTYSPACETEXTURE);
				break;
		}
	}
}