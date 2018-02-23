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
import controller.ControllerUI;

// Main MBO model

public class MBO implements Updateable
{
	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private Track myTrack;
	private ControllerUI ui;
	
	public MBO()
	{
		myTrack = new Track();
		ui = new ControllerUI();
		ui.setVisible(true);
	}

	// Updates this object.
	public void update()
	{
		int index = 0;

		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker train = trains.get(i);
			TrackBlock newBlock = getBlock(train.train.location());
			if (newBlock != null)
				train.block = newBlock;
		}
		
		for (TrainTracker train: trains)
		{
			if (train == null)
				break;
			double authority = findAuthority(train);
			train.setAuthority((int) authority);
			train.setSuggestedSpeed(train.block.getSpeedLimit());
			GlobalCoordinates loc = train.train.location();
			int trackDist = (int) train.block.startPoint.distanceTo(loc);
			ui.updateTrain(train.train.trainID, 'A', train.block.ID, trackDist, train.getAuthority(), train.getSuggestedSpeed());
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
		ui.addTrain(train.getID(), 'A', block.ID, 0, 0, 0);
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
		int blockingBlock = route.length-1;
		for (int i = 0; i < route.length; i++)
		{
			if (route[i] == null)
			{
				blockingBlock = i-1;
				break;
			}
			// else
				// System.out.print(route[i].ID);
		}
		// System.out.println(" ");
		TrainTracker blockingTrain = null;
		for (int j = 0; j < trains.size(); j++)
		{
			TrackBlock occupiedBlock = trains.get(j).getBlock();
			for (int k = 0; k < route.length; k++)
			{
				if (occupiedBlock == route[k] && k < blockingBlock)
				{
					blockingBlock = k;
					blockingTrain = trains.get(j);
				}
			}
		}
		double authority = 0;
		if (route[0] != null)
		{
			authority = route[0].startPoint.distanceTo(train.train.location());
			for (int j = 0; j < blockingBlock; j++)
			{
				authority += route[j+1].getLength();
			}
			if (blockingTrain == null)
				authority += route[blockingBlock].getLength();
			else
				authority += route[blockingBlock].startPoint.distanceTo(blockingTrain.train.location());
		}
		else
			authority = train.block.endPoint.distanceTo(train.train.location());
		
		// System.out.println(String.format("Train %d, blockingBlock %d", train.train.getID(), route[blockingBlock].ID));
	
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
	
	public int changeTrainLocation(int trainID, GlobalCoordinates location)
	{
		int error = 0;
		for (TrainTracker train: trains)
		{
			if (train.train.trainID == trainID)
			{
				train.train.setLocation(location);
				TrackBlock newBlock = getBlock(location);
				if (newBlock != null)
					train.block = newBlock;
				else
					error = 1;
			}
		}
		return error;
	}
}
