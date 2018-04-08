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
import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;

// Main MBO model

public class MboController implements Updateable
{
	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private BlockTracker[] line; 
	// private TrackModel myTrack;
	private MboControllerUI ui;
	private String lineName;
	private boolean enabled = false;
	private CtcRadio ctcRadio;
	
	public MboController(String ln)
	{
		lineName = ln;
	}
	
	public void launchUI()
	{
		if (ui == null)
		{
			ui = new MboControllerUI();
			for (TrainTracker trainInfo : trains)
			{
				int ID = trainInfo.getID();
				char section = trainInfo.getBlock().getSection();
				int blockID = trainInfo.getBlock().getID();
				GlobalCoordinates pos = trainInfo.getLocation();
				// TODO: replace with real distance along block
				int distance = 0;
				int authority = trainInfo.getAuthority();
				int speed = trainInfo.getSuggestedSpeed();
				ui.addTrain(ID, section, blockID, distance, authority, speed);
			}
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
	
	public void registerCtc(CtcRadio cr)
	{
		ctcRadio = cr;
	}
	
	public void enableMboController()
	{
		enabled = true;
	}
	
	public void disableMboController()
	{
		enabled = false;
	}
	
	public void initLine()
	{
		if (line == null)
		{
			System.out.println("Initializing line");
			int numBlocks = TrackModel.getBlockCount(lineName);
			System.out.printf("Num blocks initalized: %d", numBlocks);
			line = new BlockTracker[numBlocks+1];
			for (int i = 0; i < numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, i+1);
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				line[i+1] = new BlockTracker(i+1, nextBlock, prevBlock, blockLength, speedLimit, section, null);
			}
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
			
			int newBlock = getBlockFromLoc(trainInfo.getLocation());
			if (newBlock > 0)
				trainInfo.block = line[newBlock];
		}
		
		updateSwitches();
		
		for (TrainTracker trainInfo:trains)
		{
			if (trainInfo == null)
				break;
			double authority = findAuthority(trainInfo);
			trainInfo.setAuthority((int) authority);
			// trainInfo.setSuggestedSpeed(trainInfo.block.getSpeedLimit());
			trainInfo.setSuggestedSpeed(20);
			MboMovementCommand com;
			try
			{
				com = new MboMovementCommand(trainInfo.getSuggestedSpeed(), trainInfo.getAuthority());
			}
			catch (IllegalArgumentException e)
			{
				System.out.println("MBO tried to send an invalid speed / authority.");
				System.out.println("Sending 0 speed / authority instead.");
				com = new MboMovementCommand(0, 0);
			}
			System.out.println(trainInfo.getAuthority());
			System.out.println(trainInfo.getSuggestedSpeed());
			trainInfo.getRadio().send(com);
			System.out.println("Sent");
			GlobalCoordinates loc = trainInfo.getLocation();
			// TODO: change to distance along block
			// int trackDist = (int) trainInfo.block.getStart().distanceTo(loc);
			int trackDist = 0;
			if (ui != null)
				ui.updateTrain(trainInfo.getID(), trainInfo.block.getSection(), trainInfo.block.getID(), trackDist, trainInfo.getAuthority(), trainInfo.getSuggestedSpeed());
		}
	}
	
	// TODO: make this more efficient by starting from last known block
	private int getBlockFromLoc(GlobalCoordinates location)
	{
		for (int i = 0; i < line.length; i++)
		{
			// TODO: change once we have position stuff implemented
//			TrackBlock curBlock = TrackModel.getBlock(lineName, i);
//			if (isOnBlock(location, curBlock))
//				return curBlock;
		}
		// return -1;
		return 1;
	}
	
	private boolean isOnBlock(GlobalCoordinates location, TrackBlock block)
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
	
//	private TrackBlock[] getRoute(TrackBlock block)
//	{
//		BlockTracker blocks[] = new BlockTracker[line.length];
//		int blockInd = defaultLine.indexOf(block);
//		int maxBlockInd = defaultLine.size();
//		TrackBlock curBlock = TrackModel.getBlock(lineName, (blockInd + 1) % maxBlockInd);
//		blocks[0] = curBlock;
//		int ind = 1;
//		while (curBlock != block && curBlock != null)
//		{
//			blocks[ind] = TrackModel.getBlock(lineName, (blockInd + 1) % maxBlockInd);
//			blockInd += 1;
//			ind += 1;
//		}
//		return blocks;
//	}
	
	// Adds a train to the set of objects this object communicates with.
	public void registerTrain(int ID, MboRadio radio)
	{
		int blockNum = getBlockFromLoc(radio.receive());
		BlockTracker block;
		if (blockNum > 0)
			block = line[blockNum];
		else
			block = null;
		
		TrainTracker trainInfo = new TrainTracker(ID, block, radio);
		trains.add(trainInfo);
		if (ui != null)
			ui.addTrain(ID, block.getSection(), block.getID(), 0, 0, 0);
	}

	// Removes a train from the set of objects this object communicates
	// with.
	public void unregisterTrain(int ID)
	{
		for (TrainTracker curTrainInfo: trains)
		{
			if (curTrainInfo.getID() == ID)
			{
				trains.remove(curTrainInfo);
				break;
			}
		}
	}
        
	private double findAuthority(TrainTracker train)
	{
		return 50;
//		BlockTracker curBlock = train.getBlock();
//		BlockTracker blockingBlock = curBlock;
//		
//		// TODO: move this logic to update loop for more efficient
//		ArrayList<BlockTracker> trainBlocks = new ArrayList<BlockTracker>();
//		for (int i = 0; i < trains.size(); i++)
//		{
//			trainBlocks.add(trains.get(i).getBlock());
//		}
//		
//		double authority = 0;
//		boolean trainBlocking = false;
//		for (int i = 0; i < line.length; i++)
//		{
//			for (BlockTracker trainBlock : trainBlocks)
//			{
//				if (curBlock == trainBlock)
//				{
//					// TODO: Change this to the real distance once that's in the track model
//					double distanceAlongBlock = 0;
//					authority += distanceAlongBlock;
//				}
//				trainBlocking = true;
//			}
//			
//			if (trainBlocking)
//				break;
//			
//			if (curBlock.getNext() < 0)
//			{
//				authority += curBlock.getLength();
//			}
//		}
//		
//		return authority;
	}
	
	private void updateSwitches()
	{
		if (ctcRadio == null)
			return;
		int[][] switches = ctcRadio.getSwitchStates(lineName);
		// TODO: change next and prev blocks based on switch changes
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
