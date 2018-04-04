package mbo;

import java.util.ArrayList;
import java.sql.Time;

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
	
	public MboScheduler(String ln)
	{
		lineName = ln;
	}
	
	public void launchUI()
	{
		if (ui == null)
		{
			ui = new MboSchedulerUI();
		}
	}
	
	public void showUI()
	{
		if (ui != null)
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
		
	}
	
	private LineSchedule genericSched()
	{
		ArrayList<TrainEvent> te = new ArrayList<TrainEvent>();
		TrainEvent.EventType arr = TrainEvent.EventType.ARRIVAL;
		TrainEvent.EventType dep = TrainEvent.EventType.DEPARTURE;
		te.add(new TrainEvent(new Time(0, 0, 0), dep, "Yard"));
		
		for (BlockTracker curBlock : line)
		{
			double travelTime = 0;
			if (curBlock.getStation() == null)
			{
				travelTime = curBlock.getSpeedLimit() * curBlock.getLength();
			}
			else 
			{
				// TODO: calculate time if there's a station, then add arrival and departure events to te
			}
			
		}
		// TODO: return line schedule of the train
		return null;
	}
	
//	public LineSchedule makeSchedule(Time start, Time end, int[][] throughput)
//	{
//		// TODO: implement this
//		return null;
//	}
}
