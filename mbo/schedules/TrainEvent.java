package mbo.schedules;

import java.sql.Time;
/**
 *
 * @author Kaylene Stocking
 */
public class TrainEvent 
{
	private Time eventTime;
	private EventType event;
	private String station;
	
	public TrainEvent(Time t, EventType e, String s)
	{
		eventTime = t;
		event = e;
		station = s;
	}
	
	public enum EventType
	{
		ARRIVAL, DEPARTURE
	}
	
	public Time getTime()
	{
		return eventTime;
	}
	
	public EventType getEvent()
	{
		return event;
	}
	
	public String getStation()
	{
		return station;
	}
}
