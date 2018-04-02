package mbo.schedules;

import java.util.ArrayList;

/**
 *
 * @author Kaylene Stocking
 */
public class TrainSchedule {
	private int trainID;
	private ArrayList<TrainEvent> trainEvents;
		
	public TrainSchedule(int id, ArrayList<TrainEvent> te)
	{
		trainID = id;
		trainEvents = te;
	}
	
	public int getID()
	{
		return trainID;
	}
	
	public ArrayList<TrainEvent> getEvents()
	{
		return trainEvents;
	}
}
