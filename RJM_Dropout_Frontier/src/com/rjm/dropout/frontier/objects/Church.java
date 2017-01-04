package com.rjm.dropout.frontier.objects;

import com.rjm.dropout.frontier.FrontierModel;

import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Church extends AbsHolding {

    @FXML // fx:id="city"
    private StackPane city; // Value injected by FXMLLoader

    @FXML // fx:id="religiousBuilding"
    private ImageView religiousBuilding; // Value injected by FXMLLoader

    @FXML // fx:id="religiousSymbol"
    private ImageView religiousSymbol; // Value injected by FXMLLoader

    @FXML // fx:id="namePlate"
    private Rectangle namePlate; // Value injected by FXMLLoader

    @FXML
    void mouseSelected(MouseEvent event) {
    	if(event.getButton() == MouseButton.PRIMARY) {
			System.out.println("Church Selected");
			select();
			if (event.isControlDown()) {
				FrontierModel.getInstance().createCounty(getOwner(), this);
			} 
		}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	setCity(city);
        setPolygon(namePlate);
        setNameLabel(new Label());
    }
    
    public void colorizeSymbol(Color color){
		
		Image image = religiousSymbol.getImage();
		ImageView clip = new ImageView(image);
		clip.setFitWidth(30);
		clip.setFitHeight(30);
		
		religiousSymbol.setClip(clip);

    	ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blush = new Blend(
                BlendMode.SRC_OVER,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        religiousSymbol.getImage().getWidth(),
                        religiousSymbol.getImage().getHeight(),
                        color	
                )
        );
        religiousSymbol.setEffect(blush);
        
        religiousSymbol.setCache(true);
        religiousSymbol.setCacheHint(CacheHint.SPEED);
    }
}
