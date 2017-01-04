package com.rjm.dropout.frontier.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class Player {
	static int runningUniqueKey = 0;

	private int uniqueKey = 0;
	public int getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(int uniqueKey) {
		this.uniqueKey = uniqueKey;
	}	
	
	ICivilization civilization;
	ILeader leader;
	
	Color territoryColor;
	Color borderColor;
	
	public Player(Pair<ICivilization,ILeader> civLeaderPair) {
		this.civilization = civLeaderPair.getKey();
		this.leader = civLeaderPair.getValue();
		
		this.territoryColor = leader.getTerritoryColor();
		this.borderColor = civilization.getBorderColor();
		
		this.setUniqueKey(runningUniqueKey);
		runningUniqueKey++;
	}

	public ICivilization getCivilization(){
		return civilization;
	}
	
	public ILeader getLeader(){
		return leader;
	}
	
	public Color getTerritoryColor(){
		return territoryColor;
	}
	
	public String getTerritoryColorCode(){
		return toRGBCode(territoryColor);
	}
	
	public Color getBorderColor(){
		return borderColor;
	}
	
	public String getBorderColorCode(){
		return toRGBCode(borderColor);
	}
	
	@Override
	public String toString(){
		return civilization.getName() + " - " + leader.getName();
	}
	
	public String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
	
	ObservableList<IUnit> units = FXCollections.observableArrayList();
	
	public ObservableList<IUnit> getUnits(){
		return units;
	}
	
	public void newUnit(IUnit newUnit){
		units.addAll(newUnit);
	}
	
	public void deleteUnit(IUnit unit){
		units.remove(unit);
	}
	
	public void clearUnits(){
		units.clear();
	}
	
	ObservableList<IHolding> holdings = FXCollections.observableArrayList();
	
	public List<IHolding> getHoldings(){
		return holdings;
	}
	
	public List<IHolding> getHoldingsConnectedTo(IHolding selectedHolding){
		Set<IHolding> connectedHoldings = new HashSet<IHolding>();
		for(IHolding holding : getHoldings()){

			if (holding != selectedHolding) {
				Set<BorderTile> adjBorders = new HashSet<BorderTile>();
				selectedHolding.getBorders().forEach(border -> {
					adjBorders.addAll(border.getSurroundingBorders());
				});
				Set<BorderTile> trueAdjBorders = adjBorders.stream()
						.filter(adjBorder -> adjBorder.getHolding() != selectedHolding).collect(Collectors.toSet());
				for (BorderTile nextBorder : holding.getBorders()) {
					if (trueAdjBorders.contains(nextBorder)) {
						if (!connectedHoldings.contains(selectedHolding)) {
							connectedHoldings.add(selectedHolding);
						}
						if (!connectedHoldings.contains(holding)) {
							connectedHoldings.add(holding);
						}
					}
				} 
			}
		}
		
		return new ArrayList<IHolding>(connectedHoldings);
	}
	
	public List<IHolding> getCities(){
		return holdings.stream().filter(holding -> holding instanceof City).collect(Collectors.toList());
	}
	
	public List<IHolding> getChurches(){
		return holdings.stream().filter(holding -> holding instanceof Church).collect(Collectors.toList());
	}
	
	public void newHolding(IHolding newHolding){
		holdings.addAll(newHolding);
	}
	
	public void deleteHolding(IHolding city){
		holdings.remove(city);
	}
	
	public void clearHoldings(){
		holdings.clear();
	}
	
	public void clearCities(){
		List<IHolding> cities = getCities();
		holdings.removeAll(cities);
	}
	
	public void clearChurches(){
		List<IHolding> churches = getChurches();
		holdings.removeAll(churches);
	}
	
	ObservableList<CountyTile> counties = FXCollections.observableArrayList();
	
	public ObservableList<CountyTile> getCounties(){
		return counties;
	}
	
	public void newCounty(CountyTile newCounty){
		counties.add(newCounty);
	}
	
	public void deleteCounty(CountyTile city){
		counties.remove(city);
	}
	
	public void clearCounties(){
		counties.clear();
	}
}
