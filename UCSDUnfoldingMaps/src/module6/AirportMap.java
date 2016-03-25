package module6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import parsing.ParseFeed;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 * @author Elena Margaria
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<AirportMarker> airportList;
	List<Marker> routeList;
	
	//ADDED
	List<ShapeFeature> routes;
	HashMap<Integer, Location> airports;
	HashMap<Integer, Integer> airportRoutes;
	final int AIRPORTS_TO_DISPLAY = 100;
	
	private Marker lastSelected;
	private Marker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<AirportMarker>();
		airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			m.setID(Integer.parseInt(feature.getId()));
			
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
						
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
			
		}
	
		// sort airportList based on the number of in/out routes for each airport
		sortAirports();
				
		// hide all the routes
		hideRoutes();
				
		// display the first AIRPORTS_TO_DISPLAY airports, and hide the other ones
		unhideMarkers();
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
	
		for (int i = 0; i < airportList.size(); i++) {
			Marker m = (Marker) airportList.get(i);
			map.addMarker(m);
		}	
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<AirportMarker> airportList)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : airportList) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an airport and its routes.
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			hideRoutes();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkAirportsForClick();		
		}
	}
	
	// check which airport was clicked and hide all the others
	private void checkAirportsForClick() {
		
		if (lastClicked != null) return;
		// Loop over the airport markers to see if one of them is selected
		for (Marker marker : airportList) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				
				for (Marker mhide : airportList) {
					if (mhide != lastClicked) { //  && !isRoute(mhide)) {
						mhide.setHidden(true);
					}
				}
				displayRoutes();
				break;
			}
		}	
	}
	
	// display the routes of lastClicked airport and both their source and destination
	private void displayRoutes() {
		
		AirportMarker airport = (AirportMarker)lastClicked;
		int id = airport.getID();
		int source;
		int dest;
	
		for (Marker sl : routeList) {
			source = Integer.parseInt((String) sl.getProperty("source"));
			dest = Integer.parseInt((String) sl.getProperty("destination"));
			
			if ((source == id) || (dest == id)) {
				sl.setHidden(false);
				
				for (Marker mhide : airportList) {
					AirportMarker ahide = (AirportMarker) mhide;
					
					if ((((int)ahide.getID() == source) || ((int)ahide.getID() == dest)) && (ahide != airport)) {
						ahide.setHidden(false);
					}
				}
			}
		}
	}	

	// loop over and unhide all markers
	private void unhideMarkers() {
		for (int i = 0; i < airportList.size(); i++) {
			if (i < AIRPORTS_TO_DISPLAY) {
				airportList.get(i).setHidden(false);
			}
			else {
				airportList.get(i).setHidden(true);
			}
		}
	}
	
	// loop over and unhide the first N markers
	private void hideRoutes() {
		for (Marker m : routeList) {
			m.setHidden(true);
		}
	}
	
	// sort airportList based on the number of in/out routes for each airport
	private void sortAirports() {
		setAirportRoutes();
		Collections.sort(airportList);
	}
	
	// count and set the number of in/out routes for each airport
	private void setAirportRoutes() {
		airportRoutes = new HashMap<Integer, Integer> ();
		int source;
		int dest;
		
		// count the number of in/out routes for each airport
		for (Marker sl : routeList) {
			source = Integer.parseInt((String) sl.getProperty("source"));
			dest = Integer.parseInt((String) sl.getProperty("destination"));
			
			for (Marker m : airportList) {
				AirportMarker a = (AirportMarker) m;
				if (((int)a.getID() == source) || ((int)a.getID() == dest)) {
					a.setNumRoutes(a.getNumRoutes() + 1);
				}
			}
		}
	}
}