package mbo.schedules;

import java.util.ArrayList;
/**
 *
 * @author Kaylene Stocking
 */
public class LineSchedule {
	private ArrayList<DriverSchedule> driverSchedules;
	private ArrayList<TrainSchedule> trainSchedules;
	
	public LineSchedule(ArrayList<DriverSchedule> ds, ArrayList<TrainSchedule> ts)
	{
		driverSchedules = ds;
		trainSchedules = ts;
	}
	
	public DriverSchedule getDriverSchedule(int driverID)
	{
		for (DriverSchedule ds : driverSchedules)
		{
			if (ds.getID() == driverID)
				return ds;
		}
		return null;
	}
	
	public TrainSchedule getTrainSchedule(int trainID)
	{
		for (TrainSchedule ts : trainSchedules)
		{
			if (ts.getID() == trainID)
				return ts;
		}
		return null;
	}
	
}
