package mbo;

import java.util.ArrayList;

import track_model.TrackModel;
import track_model.TrackBlock;
import updater.Updateable;

/**
 *
 * @author Kaylene Stocking
 */

public class MboScheduler implements Updateable
{
	private MboSchedulerUI ui;
	private boolean launchedUI;
	private String lineName;
	BlockTracker[] line;
	
	public MboScheduler(String ln)
	{
		lineName = ln;
		launchedUI = false;
	}
	
	public void launchUI()
	{
		if (!launchedUI)
		{
			ui = new MboSchedulerUI();
			launchedUI = true;
		}
	}
	
	public void showUI()
	{
		if (launchedUI)
			ui.setVisible(true);
	}
	
	public void hideUI()
	{
		if (launchedUI)
			ui.setVisible(false);
	}
	
	public void initLine()
	{
		if (line == null)
		{
			ArrayList<Integer> defaultLine = TrackModel.getDefaultLine(lineName);
			int numBlocks = defaultLine.size();
			line = new BlockTracker[numBlocks+1];
			for (int i = 0; i < numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, i+1);
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				line[i+1] = new BlockTracker(i+1, nextBlock, prevBlock, blockLength, speedLimit, section);
			}
		}
		
		// TODO: stations (call curBlock.isStation(), then TrackModel.getStation() and station.getName()
		
	}
	
	public void update(int time)
	{
		
	}
}
