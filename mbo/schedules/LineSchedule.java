package mbo.schedules;

import java.util.ArrayList;
import java.sql.Time;
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
	private boolean stationsGenerated;
	
	public LineSchedule(ArrayList<DriverSchedule> ds, ArrayList<TrainSchedule> ts)
	{
		driverSchedules = ds;
		trainSchedules = ts;
		driverIDs = new ArrayList<Integer>();
		for (DriverSchedule dSched : ds)
		{
			driverIDs.add(dSched.getID());
			System.out.printf("Line Schedule: driver ID %d%n", dSched.getID());
		}
		trainIDs = new ArrayList<Integer>();
		for (TrainSchedule tSched : ts)
		{
			trainIDs.add(tSched.getID());
			System.out.printf("Line Schedule: train ID %d%n", tSched.getID());
		}
		stationsGenerated = false;
	}
	
	public void generateStationSchedules()
	{
		stationSchedules = new ArrayList<>();
		stationNames = new ArrayList<>();
		for (TrainSchedule ts : trainSchedules)
		{
			for (TrainEvent te : ts.getEvents())
			{
				String stationName = te.getStation();
				int stationIdx = -1;
				for (StationSchedule ss : stationSchedules)
				{
					if (stationName.equals(ss.getName()))
						stationIdx = stationNames.indexOf(ss);
				}
				
				TrainEvent.EventType et = te.getEvent();
				int tID = ts.getID();
				Time eventTime = te.getTime();
				StationEvent se = new StationEvent(eventTime, tID, et);
				
				if (stationIdx >= 0)
				{
					StationSchedule ss = stationSchedules.get(stationIdx);
					ss.getEvents().add(se);
				}
				else
				{
					ArrayList<StationEvent> newEvents = new ArrayList<>();
					newEvents.add(se);
					StationSchedule ss = new StationSchedule(newEvents, stationName);
					stationSchedules.add(ss);
					stationNames.add(stationName);
				}
			}
		}
		stationsGenerated = true;
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
	
	public StationSchedule getStationSchedule(String stationName)
	{
		for (StationSchedule ss : stationSchedules)
		{
			System.out.printf("line schedule: %s", ss.getName());
			if (ss.getName().equals(stationName))
				return ss;
		}
		return null;
	}
	
	public ArrayList<String> getStationNames()
	{
		return stationNames;
	}
	
	public boolean stationSchedulesExist()
	{
		return stationsGenerated;
	}
}
