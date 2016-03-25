package module6;

import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker implements Comparable<AirportMarker> {
	public static List<SimpleLinesMarker> routes;
	
	// ADDED
	private Integer id;
	private int numRoutes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		setNumRoutes(0);
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(0, 0, 255);
		pg.ellipse(x, y, 5, 5);	
	}

	@Override
	/** Show the title of the airport if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y) {
		String title = getTitle();
		pg.pushStyle();
			
		pg.rectMode(PConstants.CORNER);
			
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(title) +6, 18, 5);
			
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
				
		pg.popStyle();
		
		// show routes		
	}
	
	
	/*
	 * getters for airport properties
	 */
	
	public String getTitle() {		
		return (getName() + " - " + getCode() + " - " + getCountry() + " - " + numRoutes);
	}
	
	public String getName() {
		return (String) getProperty("name");
	}
	
	public String getCity() {
		return (String) getProperty("city");
	}
	
	public String getCountry() {
		return (String) getProperty("country");
	}
	
	public String getCode() {
		return (String) getProperty("code");
	}
	
	public void setID(Integer id) {
		this.id = id;
	}
	
	public Integer getID() {
		return id;
	}

	public int getNumRoutes() {
		return numRoutes;
	}

	public void setNumRoutes(int numRoutes) {
		this.numRoutes = numRoutes;
	}

	@Override
	public int compareTo(AirportMarker a) {
		if (this.getNumRoutes() > a.numRoutes) {
			return -1;
		}
		else if (this.getNumRoutes() < a.numRoutes) {
			return 1;
		}
		
		return 0;
	}
	
}
