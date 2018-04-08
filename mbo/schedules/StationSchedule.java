package mbo.schedules;

import java.util.ArrayList;

/**
 *
 * @author Kaylene Stocking
 */
public class StationSchedule {
	private ArrayList<StationEvent> stationEvents;
	private String stationName;
	
	public StationSchedule(ArrayList<StationEvent> se, String sn)
	{
		stationEvents = se;
		stationName = sn;
	}
	
	public ArrayList<StationEvent> getEvents()
	{
		return stationEvents;
	}
	
	public String getName()
	{
		return stationName;
	}
	
}
