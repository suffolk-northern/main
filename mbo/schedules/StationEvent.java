package mbo.schedules;

import java.sql.Time;

/**
 *
 * @author Kaylene Stocking
 */
public class StationEvent {
	private Time eventTime;
	private int trainID;
	private int driverID;
	private EventType event;
	
	public StationEvent(Time et, int tID, int dID, EventType e)
	{
		eventTime = et;
		trainID = tID;
		driverID = dID;
		event = e;
	}
	
	public enum EventType
	{
		ARRIVAL, DEPARTURE
	}
	
	public Time getTime()
	{
		return eventTime;
	}
	
	public int getTrainID()
	{
		return trainID;
	}
	
	public int getDriverID()
	{
		return driverID;
	}
	
	public EventType getEvent()
	{
		return event;
	}
}
