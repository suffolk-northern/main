package mbo.schedules;

import java.sql.Time;

/**
 *
 * @author Kaylene Stocking
 */
public class DriverEvent {
	private Time eventTime;
	private EventType event;
	private int trainID;
	
	public DriverEvent(Time t, EventType e, int tID)
	{
		eventTime = t;
		event = e;
		trainID = tID;
	}
	
	public enum EventType
	{
		EMBARK, DISEMBARK
	}
	
	public Time getTime()
	{
		return eventTime;
	}
	
	public EventType getEvent()
	{
		return event;
	}
	
	public int getTrainID()
	{
		return trainID;
	}
}
