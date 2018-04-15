package mbo.schedules;

import java.io.*;
/**
 *
 * @author Kaylene Stocking
 */
public class ScheduleWriter {
	private LineSchedule sched;
	
	public ScheduleWriter(LineSchedule newSched)
	{
		sched = newSched;
	}
	
	public void writeSchedule(File file) 
	{
		if (sched == null)
		{
			// TODO: display a real error message in the gui
			System.out.println("No schedule to write");
			return;
		}	
		
		try 
		{
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println("---Train Schedules---");
			for (int trainID : sched.getTrainIDs())
			{
				System.out.println(trainID);
				TrainSchedule ts = sched.getTrainSchedule(trainID);
				writer.printf("Train ID: %d\n", trainID);
				for (TrainEvent te : ts.getEvents())
				{
					writer.printf("Time: %s, ", te.getTime().toString());
					if (te.getEvent() == TrainEvent.EventType.ARRIVAL)
						writer.printf("ARRIVAL, ");
					else
						writer.printf("DEPARTURE, ");
					writer.printf("%s\n", te.getStation());
				}
			}
			
			writer.println("---Driver Schedules---");
			for (int driverID : sched.getDriverIDs())
			{
				DriverSchedule ds = sched.getDriverSchedule(driverID);
				writer.printf("Driver ID: %d\n", driverID);
				for (DriverEvent de : ds.getEvents())
				{
					writer.printf("Time: %s, ", de.getTime().toString());
					if (de.getEvent() == DriverEvent.EventType.EMBARK)
						writer.printf("EMBARK, ");
					else
						writer.printf("DISEMBARK, ");
					writer.printf("Train %d\n", de.getTrainID());
				}			
			}
			writer.close();
		}
		
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error writing to file");
		}
	}
}
