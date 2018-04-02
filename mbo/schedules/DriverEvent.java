package mbo.schedules;

import java.sql.Time;

/**
 *
 * @author Kaylene Stocking
 */
public class DriverEvent {
	private Time eventTime;
	private EventType event;
	
	public DriverEvent(Time t, EventType e)
	{
		eventTime = t;
		event = e;
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
}
