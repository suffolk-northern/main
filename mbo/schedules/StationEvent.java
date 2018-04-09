package mbo.schedules;

import java.sql.Time;

/**
 *
 * @author Kaylene Stocking
 */
public class StationEvent {
	private Time eventTime;
	private int trainID;
	private TrainEvent.EventType event;
	
	public StationEvent(Time et, int tID, TrainEvent.EventType e)
	{
		eventTime = et;
		trainID = tID;
		event = e;
	}
	
	public Time getTime()
	{
		return eventTime;
	}
	
	public int getTrainID()
	{
		return trainID;
	}
	
	public TrainEvent.EventType getEvent()
	{
		return event;
	}
}
