/*
 * Kaylene Stocking
 *
 * Main
 */

package mbo;

import java.util.ArrayList;
import java.lang.Math.*;

import updater.Updateable;
import track_model.GlobalCoordinates;
import track_model.TrackModel;
import track_model.TrackBlock;
import train_model.TrainModel;
import track_model.Orientation;
import controller.ControllerUI;
import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;

// Main MBO model

public class MBO implements Updateable
{
	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private ArrayList<TrackBlock> defaultLine; 
	// private TrackModel myTrack;
	private ControllerUI ui;
	private String lineName = "Green";
	
	public MBO()
	{
		// myTrack = new TrackModel();
		ui = new ControllerUI();
	}
	
	public void showUI()
	{
		ui.setVisible(true);
	}
	
	public void hideUI()
	{
		ui.setVisible(false);
	}
	
	public void initLine()
	{
		if (defaultLine.isEmpty())
		{
			ArrayList<Integer> blockNums = TrackModel.getDefaultLine(lineName);
			for (int blockID : blockNums)
				defaultLine.add(TrackModel.getBlock(lineName, blockID));
		}
	}

	// Updates this object.
	public void update(int time)
	{
		// TODO: parameter time not used

		int index = 0;

		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker trainInfo = trains.get(i);
			
			TrackBlock newBlock = getBlockFromLoc(trainInfo.getLocation());
			if (newBlock != null)
				trainInfo.block = newBlock;
		}
		
		for (TrainTracker trainInfo:trains)
		{
			if (trainInfo == null)
				break;
			double authority = findAuthority(trainInfo);
			trainInfo.setAuthority((int) authority);
			trainInfo.setSuggestedSpeed(trainInfo.block.getSpeedLimit());
			MboMovementCommand com = new MboMovementCommand(trainInfo.getAuthority(), trainInfo.getSuggestedSpeed());
			trainInfo.getRadio().send(com);
			GlobalCoordinates loc = trainInfo.getLocation();
			int trackDist = (int) trainInfo.block.getStart().distanceTo(loc);
			ui.updateTrain(trainInfo.getID(), trainInfo.block.getSection(), trainInfo.block.getBlock(), trackDist, trainInfo.getAuthority(), trainInfo.getSuggestedSpeed());
		}
	}
	
	// TODO: make this more efficient by starting from last known block
	public TrackBlock getBlockFromLoc(GlobalCoordinates location)
	{
		int maxBlockID = TrackModel.getMaxBlock();
		for (int i = 1; i <= maxBlockID; i++)
		{
			TrackBlock curBlock = TrackModel.getBlock(lineName, i);
			if (isOnBlock(location, curBlock))
				return curBlock;
		}
		return null;
	}
	
	public boolean isOnBlock(GlobalCoordinates location, TrackBlock block)
	{
		double tolerance = 5;
		double startDist = location.distanceTo(block.getStart());
		double endDist = location.distanceTo(block.getEnd());
		double distSum = startDist + endDist;
		double blockLength = block.getStart().distanceTo(block.getEnd());
		if (Math.abs(distSum - blockLength) < tolerance)
			return true;
		else
			return false;	
	}
	
	public TrackBlock[] getRoute(TrackBlock block)
	{
		TrackBlock blocks[] = new TrackBlock[defaultLine.size()];
		int blockInd = defaultLine.indexOf(block);
		int maxBlockInd = defaultLine.size();
		TrackBlock curBlock = TrackModel.getBlock(lineName, (blockInd + 1) % maxBlockInd);
		blocks[0] = curBlock;
		while (curBlock != block && curBlock != null)
		{
			blocks[ind] = TrackModel.getBlock(lineName, (blockInd + 1) % maxBlockInd);
			blockInd += 1;
		}
		return blocks;
	}
	
	// Adds a FakeTrain to the set of objects this object communicates with.
	public void registerTrain(TrainModel train, TrackBlock block, int ID)
	{
		MboRadio radio = train.mboRadio();
		TrainTracker trainInfo = new TrainTracker(train, ID, block, radio);
		trains.add(trainInfo);
		ui.addTrain(ID, block.getSection(), block.getBlock(), 0, 0, 0);
	}

	// Removes a train from the set of objects this object communicates
	// with.
	public void unregisterTrain(TrainModel train)
	{
		for (TrainTracker curTrainInfo: trains)
		{
			if (curTrainInfo.train == train)
			{
				trains.remove(curTrainInfo);
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
			authority = route[0].getStart().distanceTo(train.train.location());
			for (int j = 0; j < blockingBlock; j++)
			{
				authority += route[j+1].getLength();
			}
			if (blockingTrain == null)
				authority += route[blockingBlock].getLength();
			else
				authority += route[blockingBlock].getStart().distanceTo(blockingTrain.train.location());
		}
		else
			authority = train.block.getEnd().distanceTo(train.train.location());
		
		// System.out.println(String.format("Train %d, blockingBlock %d", train.train.getID(), route[blockingBlock].ID));
	
		return authority;
	}
	
//	public void flipSwitch(int switchID)
//	{
//		myTrack.sections[0].switches[switchID].flipSwitch();
//	}
	
//	public TrackBlock getDefaultBlock()
//	{
//		return myTrack.sections[0].blocks[0];
//	}
	
//	public int changeTrainLocation(int trainID, GlobalCoordinates location)
//	{
//		int error = 0;
//		for (TrainTracker trainInfo:trains)
//		{
//			if (trainInfo.getID() == trainID)
//			{
//				trainInfo.setLocation(location);
//				TrackBlock newBlock = getBlock(location);
//				if (newBlock != null)
//					train.block = newBlock;
//				else
//					error = 1;
//			}
//		}
//		return error;
//	}
}
