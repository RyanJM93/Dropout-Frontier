package com.rjm.dropout.frontier.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rjm.dropout.frontier.FrontierModel;
import com.rjm.dropout.frontier.enums.Terrain;
import com.rjm.dropout.frontier.objects.Civilization;
import com.rjm.dropout.frontier.objects.ICivilization;
import com.rjm.dropout.frontier.objects.Leader;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class XMLParser {

	File dialogToParse;

	public XMLParser(){
	}

	public Set<Civilization> parseCivilizationsXML(String xmlToParse){

		this.dialogToParse = new File("assets/" + xmlToParse);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(dialogToParse);
			doc.getDocumentElement().normalize();

			Element rootElement = (Element) doc.getDocumentElement();
			System.out.println(rootElement.getNodeName() + ": ");

			if(rootElement.getNodeName().equals("Civilizations")){
				return createCivilizationsFromXML(doc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Set<Leader> parseLeadersXML(String xmlToParse){

		this.dialogToParse = new File("assets/" + xmlToParse);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(dialogToParse);
			doc.getDocumentElement().normalize();

			Element rootElement = (Element) doc.getDocumentElement();
			System.out.println(rootElement.getNodeName() + ": ");

			if(rootElement.getNodeName().equals("Leaders")){
				return createLeadersFromXML(doc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Set<Civilization> createCivilizationsFromXML(Document doc) {

		Set<Civilization> civilizations = new HashSet<Civilization>();

		NodeList nList = doc.getElementsByTagName("Civilization");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				Civilization civ = new Civilization(eElement.getAttribute("name"),eElement.getAttribute("adj"));

				NodeList civColors = eElement.getElementsByTagName("Color");
				if(civColors.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element colorElement = (Element) civColors.item(0);

					int r = Integer.parseInt(colorElement.getElementsByTagName("r").item(0).getTextContent());
					int g = Integer.parseInt(colorElement.getElementsByTagName("g").item(0).getTextContent());
					int b = Integer.parseInt(colorElement.getElementsByTagName("b").item(0).getTextContent());

					Color borderColor = Color.rgb(r, g, b);
					civ.setBorderColor(borderColor);
				}

				NodeList civIcons = eElement.getElementsByTagName("Icon");
				if(civIcons.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element iconElement = (Element) civIcons.item(0);

					String iconName = iconElement.getAttribute("name");
					String url = "images/civilizations/"+iconName;

					Image icon = new Image(url);
					civ.setIcon(icon);
				}

				List<String> cities = new ArrayList<String>();
				NodeList civCities = eElement.getElementsByTagName("Cities");
				if(civCities.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element citiesElement = (Element) civCities.item(0);

					NodeList citiesList = citiesElement.getElementsByTagName("City");
					for(int i = 0; i < citiesList.getLength(); i++){
						if(citiesList.item(i).getNodeType() == Node.ELEMENT_NODE){
							Element cityElement = (Element) citiesList.item(i);

							String cityName = cityElement.getAttribute("name");
							cities.add(cityName);
						}
					}
				}
				civ.setCities(cities);

				List<String> counties = new ArrayList<String>();
				NodeList civCounties = eElement.getElementsByTagName("Counties");
				if(civCounties.getLength() > 0 && civCounties.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element countiesElement = (Element) civCounties.item(0);

					NodeList countiesList = countiesElement.getElementsByTagName("County");
					for(int i = 0; i < countiesList.getLength(); i++){
						if(countiesList.item(i).getNodeType() == Node.ELEMENT_NODE){
							Element countyElement = (Element) countiesList.item(i);

							String countyName = countyElement.getAttribute("name");
							counties.add(countyName);
						}
					}
				}
				civ.setCounties(counties);

				List<Terrain> startTerrain = new ArrayList<Terrain>();
				NodeList civStartBiases = eElement.getElementsByTagName("StartBias");
				if(civStartBiases.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element startBiasElement = (Element) civStartBiases.item(0);

					NodeList terrainList = startBiasElement.getElementsByTagName("Terrain");
					for(int i = 0; i < terrainList.getLength(); i++){
						if (terrainList.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element terrainElement = (Element) terrainList.item(i);

							String type = terrainElement.getAttribute("type");

							startTerrain.add(Terrain.valueOf(type.toUpperCase()));
						}
					}
				}
				civ.setStartTerrain(startTerrain);

				civilizations.add(civ);
			}
		}

		System.out.println("Civilizations:");
		civilizations.forEach(civ -> {
			String name = (civ.getName() != null) ? civ.getName() : "null";
			String adj = (civ.getAdj() != null) ? civ.getAdj() : "null";
			String color = (civ.getBorderColor() != null) ? civ.getBorderColor().toString() : "null";
			String icon = (civ.getIcon() != null) ? "found" : "null";	
			StringBuilder cities = new StringBuilder();
			if(civ.getCities() != null){
				for(String city : civ.getCities()){
					cities.append(city + ", ");
				}
			}
			StringBuilder startBias = new StringBuilder();
			if(civ.getStartTerrain() != null){
				for(Terrain terrain : civ.getStartTerrain()){
					startBias.append(terrain.name() + ", ");
				}
			}

			System.out.println("\tName = " + name + ", Adj = " + adj);
			System.out.println("\tColor = " + color);
			System.out.println("\tIcon = " + icon);
			System.out.println("\tCities = " + cities.toString());
			System.out.println("\tStartBias = " + startBias.toString());
		});

		return civilizations;
	}

	private Set<Leader> createLeadersFromXML(Document doc) {

		Set<Leader> leaders = new HashSet<Leader>();

		NodeList nList = doc.getElementsByTagName("Leader");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				Leader leader = new Leader(eElement.getAttribute("name"));

				NodeList leaderColors = eElement.getElementsByTagName("Color");
				if(leaderColors.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element colorElement = (Element) leaderColors.item(0);

					int r = Integer.parseInt(colorElement.getElementsByTagName("r").item(0).getTextContent());
					int g = Integer.parseInt(colorElement.getElementsByTagName("g").item(0).getTextContent());
					int b = Integer.parseInt(colorElement.getElementsByTagName("b").item(0).getTextContent());

					Color territoryColor = Color.rgb(r, g, b);
					leader.setTerritoryColor(territoryColor);
				}

				NodeList leaderCivs = eElement.getElementsByTagName("Civilization");
				if(leaderCivs.item(0).getNodeType() == Node.ELEMENT_NODE){
					Element civElement = (Element) leaderCivs.item(0);
					
					String civName = civElement.getAttribute("name");

					for(ICivilization civ : FrontierModel.getInstance().getAllCivilizations()){
						if(civ.getName().equals(civName)){
							leader.setCivilization(civ);
						}
					}
				}

				leaders.add(leader);
			}
		}

		System.out.println("Leaders:");
		leaders.forEach(leader -> {
			String name = (leader.getName() != null) ? leader.getName() : "null";
			String color = (leader.getTerritoryColor() != null) ? leader.getTerritoryColor().toString() : "null";
			String civ = (leader.getCivilization() != null) ? leader.getCivilization().getName() : "null";

			System.out.println("\tName = " + name);
			System.out.println("\t\tColor = " + color);
			System.out.println("\t\tCivilization = " + civ);
		});

		return leaders;
	}
}
