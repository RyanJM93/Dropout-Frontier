package com.rjm.dropout.frontier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaModel {

	public MediaModel() {}
	
	private static volatile MediaModel _instance;
	private final static Object _syncObject = new Object();

	public static MediaModel getInstance() {
		
		if (_instance == null) {
			synchronized (_syncObject) {
				if (_instance == null) {
					_instance = new MediaModel();
				}
			}
		}

		return _instance;
	}

	static List<MediaPlayer> musicPlayers = new ArrayList<MediaPlayer>();
	
	static String planetMapMusicFile = "assets/sounds/music/bensound-relaxing.mp3";
	static Media planetMapMusic0 = new Media(new File(planetMapMusicFile).toURI().toString());
	static MediaPlayer planetMapMusicPlayer = new MediaPlayer(planetMapMusic0);

	static String solarSystemMusicFile = "assets/sounds/music/bensound-scifi.mp3";
	static Media solarSystemMusic0 = new Media(new File(solarSystemMusicFile).toURI().toString());
	static MediaPlayer solarSystemMusicPlayer = new MediaPlayer(solarSystemMusic0);
	
	static {
		musicPlayers.add(planetMapMusicPlayer);
		musicPlayers.add(solarSystemMusicPlayer);
	}
	
	private void stopAllMusic(){
		musicPlayers.forEach(player -> {
			player.stop();
		});
	}
	
	public void playPlanetMapMusic(){
		stopAllMusic();
		planetMapMusicPlayer.setVolume(0.25);
		planetMapMusicPlayer.play();
	}
	
	public void playSolarSystemMusic(){
		stopAllMusic();
		solarSystemMusicPlayer.setVolume(0.25);
		solarSystemMusicPlayer.play();
	}
}
