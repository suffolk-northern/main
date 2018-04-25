/*
 * Kaylene Stocking
 *
 * Main
 */

package mbo;

import java.util.ArrayList;
import java.lang.Math.*;
import java.util.HashSet;
import java.util.Set;

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
	private boolean enabled;
	private CtcRadio ctcRadio;
	private TrackModel trackModel;
	
	private ArrayList<SwitchTracker> switches;
	
	private static double MAX_AUTHORITY = 3000; // m
	private static double LOCATION_MARGIN = 1; // m
	
	public MboController(String ln)
	{
		lineName = ln;
		enabled = false;
		switches = new ArrayList<>();
	}
	
	public void launchUI()
	{
		if (ui == null)
		{
			ui = new MboControllerUI(lineName);
			for (TrainTracker trainInfo : trains)
			{
				int ID = trainInfo.getID();
				char section = '0';
				int blockID = 0;
				if (trainInfo.getBlock() != null)
				{
					section = trainInfo.getBlock().getSection();
					blockID = trainInfo.getBlock().getID();
				}
				// int blockID = trainInfo.getBlock().getID();
				GlobalCoordinates pos = trainInfo.getLocation();
				// TODO: replace with real distance along block
				int distance = 0;
				int authority = trainInfo.getAuthority();
				int speed = trainInfo.getSuggestedSpeed();
				ui.addTrain(ID, section, blockID, distance, authority, speed);
			}
		}
	}
	
	public void hideUI()
	{	
		if (ui != null)
			ui.dispose();
		ui = null;
	}
	
	public void registerCtc(CtcRadio cr)
	{
		ctcRadio = cr;
	}
	
	public void registerTrackModel(TrackModel tm)
	{
		trackModel = tm;
	}
	
	public void enableMboController(boolean isEnabled)
	{
		if (isEnabled)
		{
			enabled = true;
			if (ui != null)
				ui.setMboEnabled(true);
		}
		else
		{
			enabled = false;
			if (ui != null)
				ui.setMboEnabled(false);
		}
	}
	
	public void initLine()
	{
		ArrayList<Integer> switchIDs = new ArrayList<>();
		if (line == null)
		{
			int numBlocks = TrackModel.getBlockCount(lineName);
			line = new BlockTracker[numBlocks+1];
			for (int i = 0; i <= numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, i);
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				boolean forwardDir = false;
				boolean backwardDir = false;
				if (curBlock.getNextBlockDir() == 1)
					forwardDir = true;
				if (curBlock.getPrevBlockDir() == 1)
					backwardDir = true;
				line[i] = new BlockTracker(i, nextBlock, prevBlock, blockLength, speedLimit, section, null, forwardDir, backwardDir);
				if (curBlock.isIsSwitch())
				{
					int switchID = curBlock.getBlock();
					switchIDs.add(switchID);
				}
			}
			
			for (int switchID : switchIDs)
			{
				// System.out.printf("Trying to load switch %d%n", switchID);
				int[] otherSwitchBlocks = trackModel.getBranchesOfSwitch(lineName, switchID);
				int[] switchBlocks = new int[3];
				boolean[] switchNext = {false, false, false};
				switchBlocks[0] = switchID;
				switchBlocks[1] = otherSwitchBlocks[0];
				switchBlocks[2] = otherSwitchBlocks[1];
				if (line[switchBlocks[0]].getNext() == switchBlocks[1] || line[switchBlocks[0]].getNext() == switchBlocks[2])
					switchNext[0] = true;
				if (line[switchBlocks[1]].getNext() == switchID)
					switchNext[1] = true;
				if (line[switchBlocks[2]].getNext() == switchID)
					switchNext[2] = true;
				SwitchTracker st = new SwitchTracker(switchID, switchBlocks, switchNext);
				switches.add(st);
				
//				
//				System.out.printf("Switch on block: %d%n", switchID);
//				int block = st.getBlocks()[1];
//				System.out.printf("First other block: %d, nextBlock is switch: %s%n", block, st.nextIsSwitch(block));
//				block = st.getBlocks()[2];
//				System.out.printf("First other block: %d, nextBlock is switch: %s%n", block, st.nextIsSwitch(block));
			}
		}
	}

	// Updates this object.
	public void update(int time)
	{
		// TODO: parameter time not used
		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker trainInfo = trains.get(i);
			int newBlock = getBlockFromLoc(trainInfo.getLocation());
			// System.out.printf("Train %d is on block %d%n", trainInfo.getID(), newBlock);
			if (newBlock > 0)
				trainInfo.block = line[newBlock];
		}
		
		updateSwitches();
		
		for (TrainTracker trainInfo : trains)
		{
			if (trainInfo == null)
				continue;
			updateLocation(trainInfo);
		}
		
		if (ui != null)
		{
			MboControllerUI.Request request = ui.getRequest();
			if (request == MboControllerUI.Request.ENABLE_MBO)
			{
				if (!enabled)
					ctcRadio.enableMovingBlock();
			}
			else if (request == MboControllerUI.Request.DISABLE_MBO)
			{
				if (enabled)
					ctcRadio.disableMovingBlock();
			}
		}
		
//		for (SwitchTracker st : switches)
//			st.printInfo();
//		System.out.println(line[100].getNext());

		if (enabled)
		{
			for (TrainTracker trainInfo:trains)
			{
				if (trainInfo == null)
					break;
				updateLocation(trainInfo);
				double authority = findAuthority(trainInfo);
				trainInfo.setAuthority((int) authority);
				if (trainInfo.getBlock() != null)
					trainInfo.setSuggestedSpeed(trainInfo.getBlock().getSpeedLimit());
				else
					trainInfo.setSuggestedSpeed(0);
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
				trainInfo.getRadio().send(com);
				GlobalCoordinates loc = trainInfo.getLocation();
				int blockID = 0;
				if (trainInfo.getBlock() != null)
					blockID = trainInfo.getBlock().getID();
				int trackDist = (int) trackModel.getDistanceAlongBlock(lineName, blockID, trainInfo.getCurrentPosition());
				if (ui != null)
					ui.updateTrain(trainInfo.getID(), trainInfo.block.getSection(), trainInfo.block.getID(), trackDist, trainInfo.getAuthority(), trainInfo.getSuggestedSpeed());
			}
		}
	}
	
	// TODO: make this more efficient by starting from last known block
	private int getBlockFromLoc(GlobalCoordinates location)
	{
		if (location == null || line == null)
			return -1;
		TrackBlock closestBlock = trackModel.getClosestBlock(location, lineName);
		if (closestBlock == null)
			return -1;
		double dist = trackModel.getDistanceTo(lineName, closestBlock.getBlock(), location);
		if (dist < 1)
			return closestBlock.getBlock();
		return -1;
	}
	
	// Adds a train to the set of objects this object communicates with.
	public void registerTrain(int ID, MboRadio radio)
	{
		int blockNum = getBlockFromLoc(radio.receive());
		BlockTracker block;
		if (blockNum >= 0)
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
		BlockTracker curBlock = train.getBlock();
		// Train has no authority if it hasn't been dispatched yet
		if (curBlock == null)
			return 0;
		BlockTracker blockingBlock = curBlock;
		GlobalCoordinates trainLoc = train.getCurrentPosition();
		double trainDist = trackModel.getDistanceAlongBlock(lineName, curBlock.getID(), trainLoc); 
		double distLeftInBlock = 0;
		if (!train.isGoingForward())
			distLeftInBlock = train.getBlock().getLength() - trainDist;
		else
			distLeftInBlock = trainDist;
		
		// TODO: move this logic to update loop for more efficiency
		ArrayList<BlockTracker> trainBlocks = new ArrayList<>();
		ArrayList<TrainTracker> trainTrains = new ArrayList<>();
		for (int i = 0; i < trains.size(); i++)
		{
			if (trains.get(i) != train)
			{
				trainBlocks.add(trains.get(i).getBlock());
				trainTrains.add(trains.get(i));
			}
		}
		
		// Start with negative authority since loop takes current block into account
		double authority = -1*distLeftInBlock;
		boolean blocked = false;
		boolean forward = train.isGoingForward();
//		if (forward)
//			System.out.printf("In block %d, going forward%n", curBlock.getID());
//		else
//			System.out.printf("In block %d, going backward%n", curBlock.getID());
		for (int i = 0; i < line.length; i++)
		{
			for (int j = 0; j < trainBlocks.size(); j++)
			{
				if (curBlock == trainBlocks.get(j))
				{
					GlobalCoordinates otherTrainLoc = trainTrains.get(j).getCurrentPosition();
					double otherTrainDist = trackModel.getDistanceAlongBlock(lineName, trainBlocks.get(j).getID(), otherTrainLoc);
					if (forward)
						authority += otherTrainDist;
					else
						authority += trainBlocks.get(j).getLength() - otherTrainDist;
					blocked = true;
				}
			}
			
			if (blocked)
			{
				// Safety margin for error in other train's location
				authority -= LOCATION_MARGIN;
				break;
			}
			
			blocked = false;
			authority += curBlock.getLength();
			
			if (forward)
			{
				// Check if we are traveling forwards or backwards along the next block
				System.out.printf("About to check block %d going forwards%n", curBlock.getNext());
				if (curBlock.getNext() < 0)
					blocked = true;
				else
				{
					BlockTracker nextBlock = line[curBlock.getNext()];
					GlobalCoordinates endCurBlock = trackModel.getBlock(lineName, curBlock.getID()).getEnd();
					GlobalCoordinates startNextBlock = trackModel.getBlock(lineName, nextBlock.getID()).getStart();
					if (startNextBlock.distanceTo(endCurBlock) > 1)
					{
						forward = false;
						if (!nextBlock.canGoBackward())
							blocked = true;
					}
					else
					{
						if (!nextBlock.canGoForward())
							blocked = true;
					}
					curBlock = nextBlock;
				}
			}
			else
			{
				System.out.printf("About to check block %d going backwards%n", curBlock.getNext());
				if (curBlock.getPrev() < 0)
					blocked = true;
				else
				{
					BlockTracker prevBlock = line[curBlock.getPrev()];
					GlobalCoordinates startCurBlock = trackModel.getBlock(lineName, curBlock.getID()).getStart();
					GlobalCoordinates endPrevBlock = trackModel.getBlock(lineName, prevBlock.getID()).getEnd();
					if (endPrevBlock.distanceTo(startCurBlock) > 1)
					{
						forward = true;
						if (!prevBlock.canGoForward())
							blocked = true;
					}
					else
					{
						if (!prevBlock.canGoBackward())
							blocked = true;
					}
					curBlock = prevBlock;
				}
			}
			
//			if (forward)
//			{
//				if (curBlock.getNext() < 0)
//					blocked = true;
//				else if (line[curBlock.getNext()].getNext() == curBlock.getID())
//				{
//					if (!line[curBlock.getNext()].canGoBackward())
//						blocked = true;
//				}
//			}
//			else 
//			{
//				if (curBlock.getPrev() < 0 || !line[curBlock.getPrev()].canGoBackward())
//					blocked = true;
//			}
			
			// System.out.printf("Current authority: %f%n", authority);
			
			if (blocked)
				break;

		}
		
		// Safety margin for error in this train's location
		authority = authority - LOCATION_MARGIN;
		
		if (authority > MAX_AUTHORITY)
			return MAX_AUTHORITY;
		if (authority < 0)
			return 0;
		return authority;
	}
	
	private void updateLocation(TrainTracker train)
	{
		if (train.getBlock() == null)
			return;
		GlobalCoordinates newLocation = train.getRadio().receive();
		int oldBlockID = train.getBlock().getID();
		int newBlockID = trackModel.getClosestBlock(newLocation, lineName).getBlock();
		if (newBlockID != oldBlockID)
		{
			double distanceAlongBlock = trackModel.getDistanceAlongBlock(lineName, newBlockID, newLocation);
			if (distanceAlongBlock < 1)
				train.setGoingForward(true);
			else
				train.setGoingForward(false);
		}
//		double newDistance = trackModel.getDistanceAlongBlock(lineName, blockID, newLocation);
//		double oldDistance = trackModel.getDistanceAlongBlock(lineName, blockID, train.getLastPosition());
//		if (newDistance > oldDistance)
//			train.setGoingForward(true);
//		else
//			train.setGoingForward(false);
		// train.setLastPosition(train.getCurrentPosition());
		train.setCurrentPosition(newLocation);
	}
	
	private void updateSwitches()
	{
		if (ctcRadio == null)
			return;
		int[][] switchPos = ctcRadio.getSwitchStates(lineName);
		if (switchPos == null)
		{
			switchPos = new int[6][2];
			switchPos[0][0] = 11;
			switchPos[0][1] = 12;
			switchPos[1][0] = 28;
			switchPos[1][1] = 29;
			switchPos[2][0] = 0;
			switchPos[2][1] = 57;
			switchPos[3][0] = 0;
			switchPos[3][1] = 62;
			switchPos[4][0] = 76;
			switchPos[4][1] = 77;
			switchPos[5][0] = 85;
			switchPos[5][1] = 100;
		}
		for (int i = 0; i < switchPos.length; i++)
		{
			SwitchTracker switchSet = null;
			for (SwitchTracker s : switches)
			{
				for (int j = 0; j < switchPos[i].length; j++)
					if (s.getID() == switchPos[i][j])
						switchSet = s;
			}
			
			if (switchSet != null)
			{
				int connectedBlock1 = switchPos[i][0];
				switchSet.setConnected(connectedBlock1, true);
				int connectedBlock2 = switchPos[i][1];
				switchSet.setConnected(connectedBlock2, true);
				int unconnectedBlock = -1;
				for (int j = 0; j < switchSet.getBlocks().length; j++)
				{
					int block = switchSet.getBlocks()[j];
					if (block != connectedBlock1 && block != connectedBlock2)
						unconnectedBlock = block;
				}
				switchSet.setConnected(unconnectedBlock, false);
				
				if (switchSet.nextIsSwitch(connectedBlock1))
					line[connectedBlock1].setNextBlock(connectedBlock2);
				else
					line[connectedBlock1].setPrevBlock(connectedBlock2);
				
				if (switchSet.nextIsSwitch(connectedBlock2))
					line[connectedBlock2].setNextBlock(connectedBlock1);
				else
					line[connectedBlock2].setPrevBlock(connectedBlock1);
				
				if (switchSet.nextIsSwitch(unconnectedBlock))
					line[unconnectedBlock].setNextBlock(-1);
				else
					line[unconnectedBlock].setPrevBlock(-1);
			}
 		}
	}
	
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
