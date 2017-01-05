package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.MapGenTextures;
import com.rjm.dropout.frontier.enums.StarType;

import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Star extends SimpleCosmicBody {

	private StarType type;
	
	public Star(StarType type, double rotationLength) {
		super(type.getRadius(), rotationLength);
		
		this.type = type;
		
		Image initTexture = MapGenTextures.STARTEXTURE;
		
        ImageView imageView = new ImageView(initTexture);
        imageView.setClip(new ImageView(initTexture));
		
		ColorAdjust starTexture = new ColorAdjust();
		starTexture.setSaturation(-0.25);
		
		Blend blush = new Blend(
                BlendMode.MULTIPLY,
                starTexture,
                new ColorInput(
                        0,
                        0,
                        imageView.getImage().getWidth(),
                        imageView.getImage().getHeight(),
                        type.getColor()
                )
        );
		
		imageView.setEffect(blush);

		imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);

		setDiffuseMap(imageView.snapshot(new SnapshotParameters(), null));
//		setDiffuseMap(initTexture);
		
//		material.setSpecularPower(1.0);
		material.setDiffuseColor(type.getColor());
		
		DropShadow borderGlow= new DropShadow();
		borderGlow.setOffsetY(0f);
		borderGlow.setOffsetX(0f);
		borderGlow.setColor(type.getGlow());
		borderGlow.setWidth(70);
		borderGlow.setHeight(70);
		
		setEffect(borderGlow);
	}
	
	public StarType getType(){
		return type;
	}

}
