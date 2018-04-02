package mbo.schedules;

import java.util.ArrayList;
/**
 *
 * @author Kaylene Stocking
 */
public class DriverSchedule {
	private int driverID;
	private ArrayList<DriverEvent> driverEvents;
		
	public DriverSchedule(int id, ArrayList<DriverEvent> de)
	{
		driverID = id;
		driverEvents = de;
	}
	
	public int getID()
	{
		return driverID;
	}
	
	public ArrayList<DriverEvent> getEvents()
	{
		return driverEvents;
	}
}
