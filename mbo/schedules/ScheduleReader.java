package mbo.schedules;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Kaylene Stocking
 */
public class ScheduleReader 
{	
	public static String readScheduleFile(File file)
	{
		try 
		{
			Scanner fileScan = new Scanner(file);
			String fileContent = fileScan.useDelimiter("\\Z").next();
			return fileContent;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static LineSchedule readScheduleString(String strSchedule)
	{
		String[] lines = strSchedule.split("\n");
		ArrayList<TrainSchedule> ts = new ArrayList<>();
		ArrayList<DriverSchedule> ds = new ArrayList<>();
		ArrayList<TrainEvent> curTrainEvents = new ArrayList<>();
		ArrayList<DriverEvent> curDriverEvents = new ArrayList<>();
		int curID = 0;
		String lineName = lines[0].split(" ")[0];
		boolean reachedDrivers = false;
		for (int i = 2; i < lines.length; i++)
		{
			// Reached driver schedules
			if (lines[i].charAt(0) == '-')
			{
				ts.add(new TrainSchedule(curID, curTrainEvents));
				reachedDrivers = true;
			}
			// Reached a new train schedule
			else if (lines[i].substring(0, 2).equals("Tr"))
			{
				if (curTrainEvents.size() > 0)
				{
					ts.add(new TrainSchedule(curID, curTrainEvents));
					curTrainEvents = new ArrayList<>();
				}
				curID = Integer.parseInt(lines[i].substring(10));
			}
			// Reached a new driver schedule
			else if (lines[i].charAt(0) == 'D')
			{
				if (curDriverEvents.size() > 0)
				{
					ds.add(new DriverSchedule(curID, curDriverEvents));
					curDriverEvents = new ArrayList<>();
				}
				curID = Integer.parseInt(lines[i].substring(11));
			}
			// Add train or driver event
			else
			{
				String[] tokens = lines[i].split(", ");
				for (int j = 0; j < tokens.length; j++)
					System.out.println(tokens[j]);
				String[] time = tokens[1].split(":");
				Time eventTime = new Time(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
				// Driver event
				if (reachedDrivers)
				{
					DriverEvent.EventType et = DriverEvent.EventType.DISEMBARK;
					if (tokens[2].equals("EMBARK"))
						et = DriverEvent.EventType.EMBARK;
					int trainID = Integer.parseInt(tokens[4]);
					DriverEvent de = new DriverEvent(eventTime, et, trainID);
					curDriverEvents.add(de);
				}
				// Train event
				else
				{
					TrainEvent.EventType et = TrainEvent.EventType.ARRIVAL;
					if (tokens[2].equals("DEPARTURE"))
						et = TrainEvent.EventType.DEPARTURE;
					String station = tokens[3];
					int stationBlock = Integer.parseInt(tokens[4]);
					TrainEvent te = new TrainEvent(eventTime, et, station, stationBlock);
					curTrainEvents.add(te);
				}
			}
		}
		
		ds.add(new DriverSchedule(curID, curDriverEvents));
		LineSchedule ls = new LineSchedule(ds, ts, lineName);
		return ls;
	}
}
