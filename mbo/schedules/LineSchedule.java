package mbo.schedules;

import java.util.ArrayList;
/**
 *
 * @author Kaylene Stocking
 */
public class LineSchedule {
	private ArrayList<DriverSchedule> driverSchedules;
	private ArrayList<Integer> driverIDs;
	private ArrayList<TrainSchedule> trainSchedules;
	private ArrayList<Integer> trainIDs;
	private ArrayList<StationSchedule> stationSchedules;
	private ArrayList<String> stationNames;
	
	public LineSchedule(ArrayList<DriverSchedule> ds, ArrayList<TrainSchedule> ts)
	{
		driverSchedules = ds;
		trainSchedules = ts;
		driverIDs = new ArrayList<Integer>();
		for (DriverSchedule dSched : ds)
			driverIDs.add(dSched.getID());
		trainIDs = new ArrayList<Integer>();
		for (TrainSchedule tSched : ts)
			trainIDs.add(tSched.getID());
	}
	
	public void generateStationSchedules()
	{
		// TODO: implement this
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
	
	public ArrayList<Integer> getTrainIDs()
	{
		return trainIDs;
	}
	
	public ArrayList<Integer> getDriverIDs()
	{
		return driverIDs;
	}
}
