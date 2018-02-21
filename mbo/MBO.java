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

// Main MBO model

public class MBO implements Updateable
{
	private static final GlobalCoordinates pittsburgh =
		new GlobalCoordinates(40.0, 80.0);

	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private Track myTrack;

	// Updates this object.
	public void update()
	{
		int index = 0;

		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker train = trains.get(i);
			train.setBlock(getBlock(train.train.location()));
		}
		
		for (int i = 0; i < trains.size(); i++)
		{
			TrackBlock[] route = getRoute(trains.get(i).getBlock());
			int blockingBlock = Integer.MAX_VALUE;
			TrainTracker blockingTrain = null;
			for (int j = 0; j < trains.size(); j++)
			{
				TrackBlock occupiedBlock = trains.get(i).getBlock();
				for (int k = 0; k < route.length; k++)
				{
					if (occupiedBlock == route[k] && j < blockingBlock)
					{
						blockingBlock = k;
						blockingTrain = trains.get(i);
					}
				}
				if (blockingBlock == Integer.MAX_VALUE)
				{
					// Ugh				
				}
			}
			int authority = 0;
			for (int j = 0; j < blockingBlock; j++)
			{
				authority += route[j].getLength();
			}
			authority += route[blockingBlock].startPoint.distanceTo(blockingTrain.train.location());
			trains.get(i).setAuthority(authority);
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
	
	// Adds a Train to the set of objects this object communicates with.
	public void registerTrain(Train train)
	{
		trains.add(train);
	}

	// Removes a train from the set of objects this object communicates
	// with.
	public void unregisterTrain(Train train)
	{
		trains.remove(train);
	}
        
	public void findAuthority(Train train)
	{
		GlobalCoordinates curLoc = train.location();
		for (Train otherTrain: trains)
		{
			
		}
			
	}
}
