package mbo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.sql.Time;
import java.lang.Math;
import java.io.StringWriter;
import java.io.File;
import java.util.Arrays;

import track_model.TrackModel;
import track_model.TrackBlock;
import track_model.Station;
import updater.Updateable;
import mbo.schedules.*;
import train_model.communication.TrackMovementCommand;

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
	private CtcRadio ctcRadio;
	private ArrayList<Integer> trainIDs;
	private ArrayList<Integer> driverIDs;
	private LinkedList<TrainDispatch> dispatchQueue;
	
	private boolean dispatchEnabled;
	private boolean mboEnabled;
	
	// Adjustable parameters
	private int dwellTime = 20; // Seconds
	private int throughputPerTrain = 50;
	private int minTimeBetweenDispatch = 5*60; // Seconds
	private int schedIncrement = 1; // Seconds
	// TODO: check these times
	private Time shiftStartToBreakStart = new Time(4, 0, 0);
	private Time breakStartToBreakEnd = new Time(0, 30, 0);
	private Time breakEndToShiftEnd = new Time(3, 0, 0);
	private int accelerationTime = 18; // seconds
	private int decelerationTime = 120; // seconds
	
	public MboScheduler(String ln)
	{
		lineName = ln;
		dispatchEnabled = false;
		mboEnabled = false;
		trainIDs = new ArrayList<>();
		driverIDs = new ArrayList<>();
	}
	
	public void launchUI()
	{
		ui = new MboSchedulerUI(lineName);
		ui.setDispatchEnabled(dispatchEnabled);
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
	
	public void registerCtc(CtcRadio cr)
	{
		ctcRadio = cr;
	}
	
	public void enableDispatch(boolean isEnabled)
	{
		dispatchEnabled = isEnabled;
		ui.setDispatchEnabled(isEnabled);
	}
	
	public void initLine()
	{
		// System.out.println("Got here");
		if (trackModel == null)
		{
			System.out.println("Track model not initialized, could not load track");
			return;
		}
		if (line == null)
		{
			ArrayList<Integer> defaultLine = trackModel.getDefaultLine(lineName);
			int numBlocks = defaultLine.size();
			// System.out.printf("Num blocks: %d%n", numBlocks);
			line = new BlockTracker[numBlocks];
			for (int i = 0; i < numBlocks; i++)
			{
				TrackBlock curBlock = trackModel.getBlock(lineName, defaultLine.get(i));
				// System.out.printf("Checking block %d%n", curBlock.getBlock());
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				
				String stationName = null;
				if (curBlock.isIsStation())
				{
					stationName = trackModel.getStation(lineName, defaultLine.get(i)).getName();
					// System.out.printf("Station name: %s%n", stationName);
				}
					
				line[i] = new BlockTracker(defaultLine.get(i), nextBlock, prevBlock, blockLength, speedLimit, section, stationName, false, false);
			}
		}
	}
	
	public void update(int time)
	{
		// TODO: use time
		if (ui != null)
		{
			MboSchedulerUI.Request requestType = ui.getRequest();
			// System.out.println(requestType);
			if (requestType == MboSchedulerUI.Request.SCHEDULE)
			{
				Time start = ui.getStartTime();
				Time end = ui.getEndTime();
				int[] throughput = ui.getThroughput();
				if (start != null && end != null && throughput != null)
				{
					lineSched = makeSchedule(start, end, throughput);
					makeDispatchQueue();
					ui.setSchedule(lineSched);
					ui.requestCompleted();
					ui.setMessage("Finished generating schedule.");
				}	
			}
			else if (requestType == MboSchedulerUI.Request.EXPORT_TO_CTC)
			{
				if (lineSched != null)
				{
					exportToCtc();
					ui.requestCompleted();
				}
			}
			else if (requestType == MboSchedulerUI.Request.LOAD_FROM_CTC)
			{
				try
				{
					LineSchedule ls = ScheduleReader.readScheduleString(ctcRadio.getSchedule());
					if (ls.getLine().equals(lineName))
					{
						lineSched = ls;
						makeDispatchQueue();
						ui.setSchedule(lineSched);
					}
					else
						ui.setMessage("CTC schedule is for a different line");
				}
				catch(Exception e)
				{
					ui.setMessage("CTC schedule is missing or corrupted");
				}
				ui.requestCompleted();
			}
			else if (requestType == MboSchedulerUI.Request.LOAD_FROM_FILE)
			{
				File loadFile = ui.getFile();
				String schedStr = ScheduleReader.readScheduleFile(loadFile);
				try
				{
					LineSchedule ls = ScheduleReader.readScheduleString(schedStr);
					if (ls.getLine().equals(lineName))
					{
						lineSched = ls;
						makeDispatchQueue();
						ui.setSchedule(lineSched);
					}
					else
						ui.setMessage("This schedule is for a different line.");
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					ui.setMessage("Schedule file cannot be read");
				}
				ui.requestCompleted();
			}
			else if (requestType == MboSchedulerUI.Request.ENABLE_DISPATCH)
			{
				dispatchEnabled = true;
				ctcRadio.enableAutomaticDispatch(true);
				ui.requestCompleted();
			}
			else if (requestType == MboSchedulerUI.Request.DISABLE_DISPATCH)
			{
				dispatchEnabled = false;
				ctcRadio.enableAutomaticDispatch(false);
				ui.requestCompleted();
			}
		}
		if (dispatchEnabled && mboEnabled && lineSched != null)
		{
			Time curTime = ctcRadio.getTime();
			if (dispatchQueue == null)
				makeDispatchQueue();
			if (!dispatchQueue.isEmpty())
			{
			TrainDispatch nextDispatch = dispatchQueue.peek();
				if (curTime.after(nextDispatch.getTime()))
				{
					dispatchTrain(nextDispatch.getTrainID(), nextDispatch.getDriverID());
					dispatchQueue.poll();
				}
			}
		}
	}
	
	private void makeTrainSchedule(ArrayList<TrainSchedule> trainScheds, Time startTime, int trainID)
	{
		// Check if this train already exists and append to its schedule if so
		ArrayList<TrainEvent> te = new ArrayList<>();
		boolean appending = false;
		for (TrainSchedule ts : trainScheds)
		{
			if (trainID == ts.getID())
			{
				te = ts.getEvents();
				appending = true;
			}
		}
		
		TrainEvent.EventType arr = TrainEvent.EventType.ARRIVAL;
		TrainEvent.EventType dep = TrainEvent.EventType.DEPARTURE;
		te.add(new TrainEvent(startTime, dep, "Yard", 0));
		Time curTime = startTime;
		long delay = (long) (accelerationTime * 1000) + (long) (decelerationTime * 1000);
		
		for (BlockTracker curBlock : line)
		{
			// Block length in m
			double blockLength = curBlock.getLength();
			// Speed limit in kph --> m/s
			double speedLimit = curBlock.getSpeedLimit() * (1000.0 / 3600.0);
			// time to travel block in ms
			double travelTime = (blockLength / speedLimit) * 1000;
			if (curBlock.getStation() == null)
			{
				curTime = new Time(curTime.getTime() + (long) travelTime);
			}
			else 
			{
				Time arrTime = new Time(curTime.getTime() + (long) (travelTime / 2) + delay);
				te.add(new TrainEvent(arrTime, arr, curBlock.getStation(), curBlock.getID()));
				Time depTime = new Time((arrTime.getTime()) + (long) dwellTime*1000);
				te.add(new TrainEvent(depTime, dep, curBlock.getStation(), curBlock.getID()));
				curTime = new Time(depTime.getTime() + (long) (travelTime / 2));
			}
		}
		
		Time yardArriveTime = new Time(curTime.getTime() + delay);
		te.add(new TrainEvent(yardArriveTime, arr, "Yard", 0));
		if (!appending)
		{
			TrainSchedule sched = new TrainSchedule(trainID, te);
			trainScheds.add(sched);
		}
		
	}
	
	private Time getLoopTime()
	{
		ArrayList<TrainSchedule> ts = new ArrayList<>();
		makeTrainSchedule(ts, new Time(0), 1);
		int lastEventInd = ts.get(0).getEvents().size();
		TrainEvent lastEvent = ts.get(0).getEvents().get(lastEventInd-1);
		return lastEvent.getTime();
	}
	
	public LineSchedule makeSchedule(Time start, Time end, int[] throughput)
	{
		ArrayList<TrainSchedule> trainScheds = new ArrayList<>();
		ArrayList<DriverSchedule> driverScheds = new ArrayList<>();
		int[] trainsNeeded = new int[throughput.length];
		for (int i = 0; i < throughput.length; i++)
		{
			// System.out.printf("Throughput: %d%n", throughput[i]);
			trainsNeeded[i] = throughput[i] / throughputPerTrain;
			if (throughput[i] % throughputPerTrain != 0)
				trainsNeeded[i] += 1;
			// System.out.printf("Trains needed: %d%n", trainsNeeded[i]);
		}
		
		LinkedList<Integer> freeTrains = new LinkedList<>();
		for (int i = 0; i < trainIDs.size(); i++)
			freeTrains.add(trainIDs.get(i));
		LinkedList<Driver> freeDrivers = new LinkedList<>();
		for (int i = 0; i < driverIDs.size(); i++)
			freeDrivers.add(new Driver(driverIDs.get(i)));
		LinkedList<Driver> trainDrivers = new LinkedList<>();
		
		LinkedList<Integer> trainArr = new LinkedList<>();
		LinkedList<Time> trainArrTimes = new LinkedList<>();
		
		int dispatchedTrains = 0;
		int lastDispatch = 0;
		Time curTime = start;

		while (curTime.before(end))
		{
			int timeSlot = curTime.getHours() - start.getHours();
			// System.out.printf("Time slot: %d%n", timeSlot);
			if (!trainArrTimes.isEmpty() && curTime.after(trainArrTimes.peek()))
			{
				trainArrTimes.poll();
				int arrivingTrainID = trainArr.poll();
				freeTrains.add(arrivingTrainID);
				dispatchedTrains -= 1;
				Driver driverArriving = null;
				for (Driver d : trainDrivers)
				{
					if (d.getTrain() == arrivingTrainID)
						driverArriving = d;
				}
				freeDrivers.add(driverArriving);
				trainDrivers.remove(driverArriving);
			}
			if (dispatchedTrains < trainsNeeded[timeSlot] && lastDispatch >= minTimeBetweenDispatch && !freeTrains.isEmpty())
			{
				int useTrain = freeTrains.poll();
				makeTrainSchedule(trainScheds, curTime, useTrain);
				lastDispatch = 0;
				dispatchedTrains += 1;
				// TODO: add logic for adding to the schedules of repeat trains
				Time arrTime = new Time(curTime.getTime() + getLoopTime().getTime());
				trainArrTimes.add(arrTime);
				trainArr.add(useTrain);
				Driver useDriver = null;
				for (Driver d : freeDrivers)
				{
					boolean useThisDriver = false;
					// Driver won't have times initialized if they haven't been used yet
					if (!d.isTimesInitialized())
						useThisDriver = true;
					else if (arrTime.before(d.getLunchStart()))
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
						useDriver = d;
					}		
				}
				if (useDriver != null)
				{
					trainDrivers.add(useDriver);
					freeDrivers.remove(useDriver);
					makeDriverSchedule(driverScheds, useDriver.getID(), curTime, arrTime, useTrain);
				}	
			}
			curTime = new Time(curTime.getTime() + (long) (schedIncrement * 1000));
			lastDispatch += schedIncrement;
		}
		LineSchedule ls = new LineSchedule(driverScheds, trainScheds, lineName);
		return ls;
	}
	
	private void makeDriverSchedule(ArrayList<DriverSchedule> ds, int dID, Time embarkTime, Time disembarkTime, int tID)
	{
		DriverSchedule appendSched = null;
		for (DriverSchedule curSched : ds)
		{
			if (curSched.getID() == dID)
				appendSched = curSched;
		}
		
		DriverEvent.EventType embark = DriverEvent.EventType.EMBARK;
		DriverEvent.EventType disembark = DriverEvent.EventType.DISEMBARK;
		DriverEvent embarkEvent = new DriverEvent(embarkTime, embark, tID);
		DriverEvent disembarkEvent = new DriverEvent(disembarkTime, disembark, tID);
		
		if (appendSched == null)
		{
			ArrayList<DriverEvent> driverEvents = new ArrayList<>();
			driverEvents.add(embarkEvent);
			driverEvents.add(disembarkEvent);
			DriverSchedule newDS = new DriverSchedule(dID, driverEvents);
			ds.add(newDS);
		}
		else
		{
			ArrayList<DriverEvent> driverEvents = appendSched.getEvents();
			driverEvents.add(embarkEvent);
			driverEvents.add(disembarkEvent);
		}
	}
	
	public void exportToCtc()
	{
		if (lineSched != null && ctcRadio != null)
		{
			StringWriter sched = new StringWriter();
			ScheduleWriter schedWrite = new ScheduleWriter(lineSched);
			schedWrite.writeSchedule(sched, lineName);
			String schedStr = sched.toString();
			ctcRadio.setSchedule(schedStr);
		}
	}
	
	public void registerTrain(int trainID)
	{
		trainIDs.add(trainID);
	}
	
	public void registerDriver(int driverID)
	{
		driverIDs.add(driverID);
	}
	
	private void dispatchTrain(int trainID, int driverID)
	{
		System.out.println("Dispatched a train!");
		// Set these to 0 since train should be following MBO commands
		TrackMovementCommand tmc = new TrackMovementCommand(0, 0);
		trackModel.setYardMessage(trainID, lineName, driverID, tmc);
		ctcRadio.tellCtcDispatch(lineName, trainID);
	}
	
	private void makeDispatchQueue()
	{
		dispatchQueue = new LinkedList<>();
		if (!lineSched.stationSchedulesExist())
			lineSched.generateStationSchedules();
		StationSchedule yardSchedule = null;
		for (String stationName : lineSched.getStationNames())
		{
			if (stationName.equalsIgnoreCase("Yard"))
				yardSchedule = lineSched.getStationSchedule(stationName);
		}	
		ArrayList<StationEvent> yardEvents = yardSchedule.getEvents();
		ArrayList<Integer> driverIDs = lineSched.getDriverIDs();
		for (StationEvent yardEvent : yardEvents)
		{
			if (yardEvent.getEvent() == TrainEvent.EventType.DEPARTURE)
			{
				Time departTime = yardEvent.getTime();
				int trainID = yardEvent.getTrainID();
				for (int driverID : driverIDs)
				{
					ArrayList<DriverEvent> driverEvents = lineSched.getDriverSchedule(driverID).getEvents();
					for (DriverEvent driverEvent : driverEvents)
					{
						if (driverEvent.getEvent() == DriverEvent.EventType.EMBARK && driverEvent.getTime() == departTime && driverEvent.getTrainID() == trainID)
						{
							TrainDispatch td = new TrainDispatch(trainID, driverID, departTime);
							dispatchQueue.add(td);
						}
					}
				}
			}
		}
		dispatchQueue.sort(TrainDispatch.TrainDispatchComparator);
	}
	
	public void enableMboController(boolean isEnabled)
	{
		mboEnabled = isEnabled;
	}
}
