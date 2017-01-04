package com.rjm.dropout.frontier.objects;

import javafx.scene.image.Image;

public class CivilizationIcons {
	
	public static Image RANDOMICON = null;
	public static Image ROME0ICON = null;
	public static Image PHOENICIA0ICON = null;
	public static Image ISRAEL0ICON = null;
	public static Image IRELAND0ICON = null;
	
	public static void loadTextures(){
		try {
			RANDOMICON = new Image("images/frontier/citizenIcon.png");
			ROME0ICON = new Image("images/civilizations/rome0.png");
			PHOENICIA0ICON = new Image("images/civilizations/phoenicia0.png");
			ISRAEL0ICON = new Image("images/civilizations/israel0.png");
			IRELAND0ICON = new Image("images/civilizations/ireland0.png");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
