package mbo;

import java.sql.Time;
/**
 *
 * @author Kaylene Stocking
 */
public class Driver {
	private Time shiftBegins;
	private Time shiftEnds;
	private Time lunchBegins;
	private Time lunchEnds;
	private int ID;
	private int trainID;
	boolean timesInitialized;
	
	// The driver should really only need to know lunchBegins and shiftEnds 
	// since these are when TrainModel needs to decide where driver gets off
	// But adding other stuff in case we need it later
	public Driver(int id, Time sb, Time se, Time lb, Time le)
	{
		ID = id;
		shiftBegins = sb;
		shiftEnds = se;
		lunchBegins = lb;
		lunchEnds = le;
	}
	
	public Driver(int id)
	{
		ID = id;
		timesInitialized = false;
	}
	
	public void setTimes(Time lunchBegins, Time lunchEnds, Time shiftEnds)
	{
		this.lunchBegins = lunchBegins;
		this.lunchEnds = lunchEnds;
		this.shiftEnds = shiftEnds;
		timesInitialized = true;
	}
	
	public void setTrain(int trainID)
	{
		this.trainID = trainID;
	}
	
	public int getTrain()
	{
		return trainID;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public Time getShiftStart()
	{
		return shiftBegins;
	}
	
	public Time getShiftEnd()
	{
		return shiftEnds;
	}
	
	public Time getLunchStart()
	{
		return lunchEnds;
	}
	
	public Time getLunchEnd()
	{
		return lunchEnds;
	}
	
	public boolean isTimesInitialized()
	{
		return timesInitialized;
	}
}
