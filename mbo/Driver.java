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
}
