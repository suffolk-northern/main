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
		catch (IOException e)
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
		if (!lines[0].equals("---Train Schedules---"))
			return null;
		boolean reachedDrivers = false;
		for (int i = 1; i < lines.length; i++)
		{
			if (lines[i].charAt(0) == '-')
			{
				ts.add(new TrainSchedule(curID, curTrainEvents));
				reachedDrivers = true;
			}
			else if (lines[i].substring(0, 2).equals("Tr"))
			{
				if (curTrainEvents.size() > 0)
				{
					ts.add(new TrainSchedule(curID, curTrainEvents));
					curTrainEvents = new ArrayList<>();
				}
				curID = Integer.parseInt(lines[i].substring(10));
			}
			else if (lines[i].charAt(0) == 'D')
			{
				if (curDriverEvents.size() > 0)
				{
					ds.add(new DriverSchedule(curID, curDriverEvents));
					curDriverEvents = new ArrayList<>();
				}
				curID = Integer.parseInt(lines[i].substring(11));
			}
			else
			{
				String[] tokens = lines[i].split(". ");
				String[] time = tokens[1].split(":");
				Time eventTime = new Time(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
				if (reachedDrivers)
				{
					DriverEvent.EventType et = DriverEvent.EventType.DISEMBARK;
					if (tokens[2].equals("EMBARK"))
						et = DriverEvent.EventType.EMBARK;
					int trainID = Integer.parseInt(tokens[4]);
					DriverEvent de = new DriverEvent(eventTime, et, trainID);
					curDriverEvents.add(de);
				}
				else
				{
					TrainEvent.EventType et = TrainEvent.EventType.ARRIVAL;
					if (tokens[2].equals("DEPARTURE"))
						et = TrainEvent.EventType.DEPARTURE;
					String station = tokens[3];
					TrainEvent te = new TrainEvent(eventTime, et, station);
					curTrainEvents.add(te);
				}
			}
		}
		
		ds.add(new DriverSchedule(curID, curDriverEvents));
		LineSchedule ls = new LineSchedule(ds, ts);
		return ls;
	}
}
