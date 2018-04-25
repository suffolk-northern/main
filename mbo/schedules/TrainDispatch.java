package mbo.schedules;

import java.sql.Time;

/**
 *
 * @author Kaylene Stocking
 */
public class TrainDispatch 
{
	private int trainID;
	private int driverID;
	private Time time;
	
	public TrainDispatch(int trainID, int driverID, Time time)
	{
		this.trainID = trainID;
		this.driverID = driverID;
		this.time = time;
	}
	
	public int getTrainID()
	{
		return trainID;
	}
	
	public int getDriverID()
	{
		return driverID;
	}
	
	public Time getTime()
	{
		return time;
	}
}
