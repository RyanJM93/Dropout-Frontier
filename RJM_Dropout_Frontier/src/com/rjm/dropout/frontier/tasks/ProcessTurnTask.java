package com.rjm.dropout.frontier.tasks;

import java.util.Comparator;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.objects.BorderTile;
import com.rjm.dropout.frontier.objects.IUnit;
import com.rjm.dropout.frontier.objects.Point;

import javafx.application.Platform;

public class ProcessTurnTask extends FrontierTask<Boolean> {
	
	public ProcessTurnTask() {
		
	}

	@Override
	protected String getTaskTitle() {
		return "Processing Turn";
	}

	@Override
	protected Boolean runInBackground() {

		FrontierModel.getInstance().getPlayers().forEach(player -> {
			System.out.println("Processing turn for " + player.getCivilization().getName());
			
			if (!player.getUnits().isEmpty()) {
				player.getUnits().sort(new Comparator<IUnit>() {

					@Override
					public int compare(IUnit left, IUnit right) {
						if (player.getUnits().indexOf(left) == 0) {
							return -1;
						}
						
						int d0 = distanceBetween(left.getLocation(), player.getUnits().get(0).getLocation());
						int d1 = distanceBetween(right.getLocation(), player.getUnits().get(0).getLocation());
						
						return Integer.compare(d0, d1);
					}
				});
			}
			player.getUnits().forEach(unit -> {
				unit.resetMovement();
				unit.updateVisibleHexs();
			});
			player.getHoldings().forEach(holding -> {
				for(BorderTile border : holding.getBorders()){
					if (border.getCounty() == null) {
						Platform.runLater(() -> {
							border.update();
						});
					}
				}
				holding.updateVisibleHexs();
			});
			
			
			if(player == FrontierModel.getInstance().getFrontierGameController().getPlayer()){
				Platform.runLater(() -> {
					FrontierModel.getInstance().updatePlayerTileYields(player);
				});
			}
		});

		return true;
	}

	int distanceBetween(Point origin, Point current){
		int xAway = Math.abs(current.x - origin.x);
		int yAway = Math.abs(current.y - origin.y);

		if(xAway > yAway){
			return xAway;
		} else {
			return yAway;
		}
	}
}
