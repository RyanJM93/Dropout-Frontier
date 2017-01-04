package com.rjm.dropout.frontier.objects;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Planet extends Sphere {

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
	
	public Planet(double radius, double dayLength) {
		super(radius);
		
		setDayLength(dayLength);
		
		getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		// TODO temporary
		setMaterial(material);
		
		rotateAroundYAxis(this).play();
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

}