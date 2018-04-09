package mbo;

import java.util.ArrayList;
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
	
	// Adjustable parameters
	private int dwellTime = 20; // Seconds
	private int throughputPerTrain = 50;
	private int numTrains = 20;
	private int minTimeBetweenDispatch = 5*60; // Seconds
	private int schedIncrement = 60; // Seconds
	
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
	
	public void initLine()
	{
		if (line == null)
		{
			ArrayList<Integer> defaultLine = TrackModel.getDefaultLine(lineName);
			int numBlocks = defaultLine.size();
			line = new BlockTracker[numBlocks];
			for (int i = 0; i < numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, defaultLine.get(i));
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				
				String stationName = null;
				if (curBlock.isIsStation())
					stationName = TrackModel.getStation(lineName, defaultLine.get(i)).getName();
					
				line[i] = new BlockTracker(defaultLine.get(i), nextBlock, prevBlock, blockLength, speedLimit, section, stationName);
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
				lineSched = makeSchedule(start, end, throughput);
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
	
	public ArrayList<TrainSchedule> makeTrainSchedules(Time start, Time end, int[] throughput)
	{
		ArrayList<TrainSchedule> trainScheds = new ArrayList<>();
		int[] trainsNeeded = new int[throughput.length];
		for (int i = 0; i < throughput.length; i++)
		{
			trainsNeeded[i] = throughput[i] / throughputPerTrain;
			if (throughput[i] % throughputPerTrain != 0)
				trainsNeeded[i] += 1;
		}
		
		ArrayList<Integer> freeTrains = new ArrayList<>();
		for (int i = 1; i <= numTrains; i++)
			freeTrains.add(i);
		
		ArrayList<Integer> trainArr = new ArrayList<>();
		ArrayList<Time> trainArrTimes = new ArrayList<>();
		
		int dispatchedTrains = 0;
		int lastDispatch = 0;
		Time curTime = start;
		
		// TODO: These ArrayLists should really be queues, oops
		while (curTime.before(end))
		{
			int timeSlot = curTime.getHours() - start.getHours();
			if (curTime.after(trainArrTimes.get(0)))
			{
				trainArrTimes.remove(0);
				int trainID = trainArr.get(0);
				trainArr.remove(0);
				freeTrains.add(trainID);
				dispatchedTrains -= 1;
			}
			if (dispatchedTrains < trainsNeeded[timeSlot] && lastDispatch >= minTimeBetweenDispatch)
			{
				TrainSchedule ts = makeTrainSchedule(curTime, freeTrains.get(0));
				trainScheds.add(ts);
				lastDispatch = 0;
				dispatchedTrains += 1;
				// TODO: add logic for adding to the schedules of repeat trains
				trainArrTimes.add(new Time(curTime.getTime() + getLoopTime().getTime()));
				freeTrains.remove(0);
			}
			curTime = new Time(curTime.getTime() + (long) (schedIncrement * 1000));
			lastDispatch += schedIncrement;
		}
		return trainScheds;
	}
	
	private ArrayList<DriverSchedule> makeDriverSchedules(ArrayList<TrainSchedule> trainScheds)
	{
		// TODO: implement this
		
		return null;
	}
	
	public LineSchedule makeSchedule(Time start, Time end, int[] throughput)
	{
		ArrayList<TrainSchedule> ts = makeTrainSchedules(start, end, throughput);
		ArrayList<DriverSchedule> ds = makeDriverSchedules(ts);
		LineSchedule ls = new LineSchedule(ds, ts);
		return ls;
	}
}
