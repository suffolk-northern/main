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
	private int stationBlock;
	
	public TrainEvent(Time t, EventType e, String s, int stationBlock)
	{
		eventTime = t;
		event = e;
		station = s;
		this.stationBlock = stationBlock;
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
	
	public int getStationBlock()
	{
		return stationBlock;
	}
}
