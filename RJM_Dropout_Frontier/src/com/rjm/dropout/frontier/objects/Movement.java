package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.FrontierModel;

import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.scene.effect.Reflection;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Movement {
	
	Path path;
	PathTransition pathTransition;
	ParallelTransition parallelTransition;

	public Movement(IUnit unit, Point point) {      
		this.path = new Path();
		path.getElements().add(new MoveTo(70,20));
		path.getElements().add(new CubicCurveTo(30, 120, 30, 220, 520, 120));
		path.getElements().add(new CubicCurveTo(350, 20, 250, 240, 320, 240));
		path.setOpacity(0.0);

		FrontierModel.getInstance().getFrontierGameController().getUnitGroup().getChildren().add(path);
		final Reflection reflection = new Reflection();
		reflection.setFraction(1.0);

		this.pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.seconds(8.0));
		pathTransition.setDelay(Duration.seconds(.5));
		pathTransition.setPath(path);
		pathTransition.setNode(unit.getUnit());
		pathTransition.setOrientation(OrientationType.NONE);
		pathTransition.setCycleCount(Timeline.INDEFINITE);
		pathTransition.setAutoReverse(true);

		parallelTransition = new ParallelTransition(pathTransition);
	}
	
	public void play(){
		parallelTransition.play(); 
	}

}
