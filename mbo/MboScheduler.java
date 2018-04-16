package mbo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.sql.Time;
import java.lang.Math;

import track_model.TrackModel;
import track_model.TrackBlock;
import track_model.Station;
import updater.Updateable;
import mbo.schedules.*;

/**
 *
 * @author Kaylene Stocking
 */

public class MboScheduler implements Updateable
{
	private MboSchedulerUI ui;
	private String lineName;
	private BlockTracker[] line;
	private LineSchedule lineSched;
	private TrackModel trackModel;
	
	// Adjustable parameters
	private int dwellTime = 20; // Seconds
	private int throughputPerTrain = 50;
	private int numTrains = 20;
	private int numDrivers = 20;
	private int minTimeBetweenDispatch = 5*60; // Seconds
	private int schedIncrement = 60; // Seconds
	// TODO: check these times
	private Time shiftStartToBreakStart = new Time(3, 0, 0);
	private Time breakStartToBreakEnd = new Time(0, 30, 0);
	private Time breakEndToShiftEnd = new Time(3, 0, 0);
	
	public MboScheduler(String ln)
	{
		lineName = ln;
	}
	
	public void launchUI()
	{
		ui = new MboSchedulerUI(lineName);
		ui.setVisible(true);
	}
	
	public void hideUI()
	{
		if (ui != null)
			ui.setVisible(false);
	}
	
	public void registerTrackModel(TrackModel tm)
	{
		trackModel = tm;
	}
	
	public void initLine()
	{
		System.out.println("Got here");
		if (line == null)
		{
			ArrayList<Integer> defaultLine = trackModel.getDefaultLine(lineName);
			int numBlocks = defaultLine.size();
			System.out.printf("Num blocks: %d%n", numBlocks);
			line = new BlockTracker[numBlocks];
			for (int i = 0; i < numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, defaultLine.get(i));
				System.out.printf("Checking block %d%n", curBlock);
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				
				String stationName = null;
				if (curBlock.isIsStation())
				{
					stationName = TrackModel.getStation(lineName, defaultLine.get(i)).getName();
					System.out.printf("Station name: %s%n", stationName);
				}
					
				line[i] = new BlockTracker(defaultLine.get(i), nextBlock, prevBlock, blockLength, speedLimit, section, stationName, false, false);
			}
		}
	}
	
	public void update(int time)
	{
		// TODO: use time
		if (ui != null && ui.scheduleRequested())
		{
			Time start = ui.getStartTime();
			Time end = ui.getEndTime();
			int[] throughput = ui.getThroughput();
			if (start != null && end != null && throughput != null)
			{
				lineSched = makeSchedule(start, end, throughput);
				ui.setSchedule(lineSched);
				System.out.println("Got here");
			}
			
		}
	}
	
	private TrainSchedule makeTrainSchedule(Time startTime, int trainID)
	{
		ArrayList<TrainEvent> te = new ArrayList<TrainEvent>();
		TrainEvent.EventType arr = TrainEvent.EventType.ARRIVAL;
		TrainEvent.EventType dep = TrainEvent.EventType.DEPARTURE;
		te.add(new TrainEvent(startTime, dep, "Yard"));
		Time curTime = startTime;
		
		for (BlockTracker curBlock : line)
		{
			double travelTime = 0;
			if (curBlock.getStation() == null)
			{
				// In seconds
				// TODO: check units
				travelTime = curBlock.getSpeedLimit() * curBlock.getLength();
				long t = curTime.getTime();
				curTime = new Time(t + (long) travelTime);
			}
			else 
			{
				System.out.printf("Making events at station %s%n", curBlock.getStation());
				travelTime = curBlock.getSpeedLimit() * curBlock.getLength();
				long t = curTime.getTime();
				Time arrTime = new Time(t + (long) (travelTime / 2));
				te.add(new TrainEvent(arrTime, arr, curBlock.getStation()));
				Time depTime = new Time(t + dwellTime + (long) (travelTime / 2));
				te.add(new TrainEvent(depTime, dep, curBlock.getStation()));
			}
		}
		TrainSchedule ts = new TrainSchedule(trainID, te);
		return ts;
	}
	
	private Time getLoopTime()
	{
		TrainSchedule ts = makeTrainSchedule(new Time(0), 1);
		int lastEventInd = ts.getEvents().size();
		TrainEvent lastEvent = ts.getEvents().get(lastEventInd);
		return lastEvent.getTime();
	}
	
	public LineSchedule makeSchedule(Time start, Time end, int[] throughput)
	{
		ArrayList<TrainSchedule> trainScheds = new ArrayList<>();
		ArrayList<DriverSchedule> driverScheds = new ArrayList<>();
		int[] trainsNeeded = new int[throughput.length];
		for (int i = 0; i < throughput.length; i++)
		{
			System.out.printf("Throughput: %d%n", throughput[i]);
			trainsNeeded[i] = throughput[i] / throughputPerTrain;
			if (throughput[i] % throughputPerTrain != 0)
				trainsNeeded[i] += 1;
			System.out.printf("Trains needed: %d%n", trainsNeeded[i]);
		}
		
		LinkedList<Integer> freeTrains = new LinkedList<>();
		for (int i = 1; i <= numTrains; i++)
			freeTrains.add(i);
		LinkedList<Driver> freeDrivers = new LinkedList<>();
		for (int i = 1; i <= numDrivers; i++)
			freeDrivers.add(new Driver(i));
		LinkedList<Driver> trainDrivers = new LinkedList<>();
		
		LinkedList<Integer> trainArr = new LinkedList<>();
		LinkedList<Time> trainArrTimes = new LinkedList<>();
		
		int dispatchedTrains = 0;
		int lastDispatch = 0;
		Time curTime = start;
		
		// TODO: These ArrayLists should really be queues, oops
		while (curTime.before(end))
		{
			int timeSlot = curTime.getHours() - start.getHours();
			System.out.printf("Time slot: %d%n", timeSlot);
			if (!trainArrTimes.isEmpty() && curTime.after(trainArrTimes.peek()))
			{
				trainArrTimes.poll();
				int trainID = trainArr.poll();
				freeTrains.add(trainID);
				dispatchedTrains -= 1;
				for (Driver d : trainDrivers)
				{
					if (d.getTrain() == trainID)
					{
						freeDrivers.add(d);
						trainDrivers.remove(d);
					}
				}
			}
			if (dispatchedTrains < trainsNeeded[timeSlot] && lastDispatch >= minTimeBetweenDispatch)
			{
				int useTrain = freeTrains.poll();
				TrainSchedule ts = makeTrainSchedule(curTime, useTrain);
				trainScheds.add(ts);
				lastDispatch = 0;
				dispatchedTrains += 1;
				// TODO: add logic for adding to the schedules of repeat trains
				Time arrTime = new Time(curTime.getTime() + getLoopTime().getTime());
				trainArrTimes.add(arrTime);
				Driver useDriver = null;
				for (Driver d : freeDrivers)
				{
					boolean useThisDriver = false;
					if (arrTime.before(d.getLunchStart()))
						useThisDriver = true;
					else if (curTime.after(d.getLunchEnd()) && arrTime.before(d.getShiftEnd()))
						useThisDriver = true;
					else if (arrTime.after(d.getShiftEnd()))
					{
						freeDrivers.remove(d);
					}
					if (useThisDriver)
					{
						d.setTrain(useTrain);
						if (!d.isTimesInitialized())
						{
							Time lunchStarts = new Time(curTime.getTime() + shiftStartToBreakStart.getTime());
							Time lunchEnds = new Time(lunchStarts.getTime() + breakStartToBreakEnd.getTime());
							Time shiftEnds = new Time(lunchEnds.getTime() + breakEndToShiftEnd.getTime());
							d.setTimes(lunchStarts, lunchEnds, shiftEnds);
						}
					}
						
				}
				if (useDriver != null)
				{
					trainDrivers.add(useDriver);
					freeDrivers.remove(useDriver);
					DriverSchedule ds = makeDriverSchedule(null, useDriver.getID(), curTime, arrTime, useTrain);
					driverScheds.add(ds);
				}
				
			}
			curTime = new Time(curTime.getTime() + (long) (schedIncrement * 1000));
			lastDispatch += schedIncrement;
		}
		LineSchedule ls = new LineSchedule(driverScheds, trainScheds);
		return ls;
	}
	
	public DriverSchedule makeDriverSchedule(DriverSchedule ds, int dID, Time embarkTime, Time disembarkTime, int tID)
	{
		// TODO: append to exisiting driver schedule if it exists
		DriverEvent.EventType embark = DriverEvent.EventType.EMBARK;
		DriverEvent.EventType disembark = DriverEvent.EventType.DISEMBARK;
		DriverEvent embarkEvent = new DriverEvent(embarkTime, embark, tID);
		DriverEvent disembarkEvent = new DriverEvent(disembarkTime, disembark, tID);
		ArrayList<DriverEvent> driverEvents = new ArrayList<>();
		driverEvents.add(embarkEvent);
		driverEvents.add(disembarkEvent);
		DriverSchedule newDS = new DriverSchedule(dID, driverEvents);
		return newDS;
	}
}
