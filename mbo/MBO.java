/*
 * Ryan Matthews
 *
 * Main
 */

package mbo;

import java.util.ArrayList;
import java.lang.Math.*;

import updater.Updateable;
import track_model.GlobalCoordinates;
import track_model.Track;
import track_model.TrackBlock;
import track_model.TrackSection;
import train_model.Train;
import track_model.Orientation;
import controller.MBOController;

// Main MBO model

public class MBO implements Updateable
{
	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private Track myTrack;
	private MBOController ui;
	
	public MBO()
	{
		myTrack = new Track();
		ui = new MBOController();
		ui.setVisible(true);
	}

	// Updates this object.
	public void update()
	{
		int index = 0;

		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker train = trains.get(i);
			train.setBlock(getBlock(train.train.location()));
		}
		
		for (TrainTracker train: trains)
		{
			double authority = findAuthority(train);
			train.setAuthority((int) authority);
			train.setSuggestedSpeed(30);
		}
	}
	
	public TrackBlock getBlock(GlobalCoordinates location)
	{
		for (TrackBlock block: myTrack.sections[0].blocks)
		{
			if (isOnBlock(location, block))
				return block;
		}
		return null;
	}
	
	public boolean isOnBlock(GlobalCoordinates location, TrackBlock block)
	{
		double tolerance = 5;
		double startDist = location.distanceTo(block.startPoint);
		double endDist = location.distanceTo(block.endPoint);
		double distSum = startDist + endDist;
		double blockLength = block.startPoint.distanceTo(block.endPoint);
		if (Math.abs(distSum - blockLength) < tolerance)
			return true;
		else
			return false;	
	}
	
	public TrackBlock[] getRoute(TrackBlock block)
	{
		TrackBlock blocks[] = new TrackBlock[myTrack.sections[0].numBlocks];
		TrackBlock curBlock = block.getNextBlock();
		blocks[0] = curBlock;
		int ind = 1;
		while (curBlock != block && curBlock != null)
		{
			blocks[ind] = curBlock.getNextBlock();
			curBlock = blocks[ind];
			ind += 1;
		}
		return blocks;
	}
	
	// Adds a FakeTrain to the set of objects this object communicates with.
	public void registerTrain(FakeTrain train, TrackBlock block)
	{
		TrainTracker trainTracking = new TrainTracker(train, block);
		trains.add(trainTracking);
	}

	// Removes a train from the set of objects this object communicates
	// with.
	public void unregisterTrain(FakeTrain train)
	{
		for (TrainTracker curTrain: trains)
		{
			if (curTrain.train == train)
			{
				trains.remove(curTrain);
				break;
			}
		}
	}
        
	public double findAuthority(TrainTracker train)
	{
		TrackBlock[] route = getRoute(train.getBlock());
		int blockingBlock = Integer.MAX_VALUE;
		TrainTracker blockingTrain = null;
		for (int j = 0; j < trains.size(); j++)
		{
			TrackBlock occupiedBlock = train.getBlock();
			for (int k = 0; k < route.length; k++)
			{
				if (occupiedBlock == route[k] && j < blockingBlock)
				{
					blockingBlock = k;
					blockingTrain = train;
				}
			}
			if (blockingBlock == Integer.MAX_VALUE)
			{
				// Ugh				
			}
		}
		double authority = 0;
		for (int j = 0; j < blockingBlock; j++)
		{
			authority += route[j].getLength();
		}
		authority += route[blockingBlock].startPoint.distanceTo(blockingTrain.train.location());
		return authority;
	}
	
	public void flipSwitch(int switchID)
	{
		myTrack.sections[0].switches[switchID].flipSwitch();
	}
	
	public TrackBlock getDefaultBlock()
	{
		return myTrack.sections[0].blocks[0];
	}
}
