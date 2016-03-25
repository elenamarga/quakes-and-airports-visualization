package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Elena Margaria
 * Date: September 26, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    //TODO: Add code here as appropriate
	    for (int earthquake = 0; earthquake < earthquakes.size(); earthquake++) {
	    	PointFeature f = earthquakes.get(earthquake);
	    	
	    	SimplePointMarker earthquakeMarker = createMarker(f);
	    	markers.add(earthquakeMarker);
	    }
	    map.addMarkers(markers);
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// Create a new marker object with the given location
		SimplePointMarker marker =  new SimplePointMarker(feature.getLocation());
		
		// Read and store the magnitude
		Object magObj = feature.getProperty("magnitude");
    	float mag = Float.parseFloat(magObj.toString());
	    
    	// Set the properties of the marker according to the magnitude
	    // if a minor earthquake: blue and small
    	if (mag < 4.0f) { 
    		marker.setColor(color(0, 0, 255));
    		marker.setRadius(5);
    	}
    	// if a light earthquake: yellow and medium
    	else if (mag < 5.0f) {	
    		marker.setColor(color(255, 255, 0));
    		marker.setRadius(9);
    	}
    	// if a moderate or higher earthquake: red and big
    	else { 
    		marker.setColor(color(255, 0, 0));
    		marker.setRadius(13);
    	}
    	return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		fill(112,128,144);
		rect(25, 50, 150, 250, 7);
		
		textSize(18);
		fill(255, 255, 255);
		text("Earthquake Key", 33, 75);  
		
		textSize(14);
		fill(255, 255, 255);
		text("Magnitude: ", 60, 120);
		
		fill(0, 0, 255);
		ellipse(70, 160, 5, 5);
		text("< 4.0", 90, 165);
		
		fill(255, 255, 0);
		ellipse(70, 200, 9, 9);
		text("4.0 - 4.9", 90, 205);
		
		fill(255, 0, 0);
		ellipse(70, 240, 13, 13);
		text("> 4.9", 90, 245);
	}
}
