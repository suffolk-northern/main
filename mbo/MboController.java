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
	private boolean enabled;
	private CtcRadio ctcRadio;
	
	private static int MAX_AUTHORITY = 3000; // m
	
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
			ui.dispose();
		ui = null;
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
			for (int i = 0; i <= numBlocks; i++)
			{
				TrackBlock curBlock = TrackModel.getBlock(lineName, i);
				double blockLength = curBlock.getLength();
				int nextBlock = curBlock.getNextBlockId();
				int prevBlock = curBlock.getPrevBlockId();
				int speedLimit = curBlock.getSpeedLimit();
				char section = curBlock.getSection();
				line[i] = new BlockTracker(i, nextBlock, prevBlock, blockLength, speedLimit, section, null);
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

		int index = 0;

		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker trainInfo = trains.get(i);
			int newBlock = getBlockFromLoc(trainInfo.getLocation());
			// System.out.printf("Train %d is on block %d%n", trainInfo.getID(), newBlock);
			if (newBlock > 0)
				trainInfo.block = line[newBlock];
		}
		
		updateSwitches();
		
		if (enabled == true)
		{
			for (TrainTracker trainInfo:trains)
			{
				if (trainInfo == null)
					break;
				double authority = findAuthority(trainInfo);
				trainInfo.setAuthority((int) authority);
				trainInfo.setSuggestedSpeed(trainInfo.block.getSpeedLimit());
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
				// TODO: change to distance along block
				int blockID = trainInfo.getBlock().getID();
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
		BlockTracker curBlock = train.getBlock();
		BlockTracker blockingBlock = curBlock;
		GlobalCoordinates trainLoc = train.getCurrentPosition();
		double trainDist = trackModel.getDistanceAlongBlock(lineName, curBlock.getID(), trainLoc); 
		double distLeftInBlock = 0;
		if (train.isGoingForward())
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
		for (int i = 0; i < line.length; i++)
		{
			for (int j = 0; j < trainBlocks.size(); j++)
			{
				if (curBlock == trainBlocks.get(j))
				{
					GlobalCoordinates otherTrainLoc = trainTrains.get(j).getCurrentPosition();
					double otherTrainDist = trackModel.getDistanceAlongBlock(lineName, trainBlocks.get(j).getID(), otherTrainLoc);
					if (train.isGoingForward())
						authority += otherTrainDist;
					else
						authority += trainBlocks.get(j).getLength() - otherTrainDist;
					blocked = true;
				}
			}
			
			if (blocked)
				break;
			
			blocked = false;
			authority += curBlock.getLength();
			
			// TODO: take switches into account
			if (train.isGoingForward() && curBlock.getNext() < 0)
			{
				blocked = true;
			}
			else if (!train.isGoingForward() && curBlock.getPrev() < 0)
				blocked = true;
			
			if (blocked)
				break;
			
			curBlock = line[curBlock.getNext()];
		}
		
		if (authority > MAX_AUTHORITY)
			return MAX_AUTHORITY;
		return authority;
	}
	
	private void updateSwitches()
	{
		if (ctcRadio == null)
			return;
		int[][] switchPos = ctcRadio.getSwitchStates(lineName);
		if (switchPos == null)
			return;
		for (int i = 0; i < switchPos.length; i++)
		{
			SwitchTracker switchSet = null;
			for (SwitchTracker s : switches)
			{
				for (int j = 0; j < switchPos.length; j++)
					if (s.getID() == switchPos[i][j])
						switchSet = s;
			}
			
			if (switchSet != null)
			{
				int connectedBlock1 = switchPos[i][0];
				int connectedBlock2 = switchPos[i][1];
				int unconnectedBlock = -1;
				for (int j = 0; j < switchSet.getBlocks().length; j++)
				{
					int block = switchSet.getBlocks()[j];
					if (block != connectedBlock1 && block != connectedBlock2)
						unconnectedBlock = block;
				}
				
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
				
				// For the block not connected by the switch, make sure whatever
				// end was connected before is now blocked
//				int checkBlock = line[unconnectedBlock].getNext();
//				if (checkBlock == connectedBlock1 || checkBlock == connectedBlock2)
//					line[unconnectedBlock].setNextBlockOpen(false);
//				checkBlock = line[unconnectedBlock].getPrev();
//				if (checkBlock == connectedBlock1 || checkBlock == connectedBlock2)
//					line[unconnectedBlock].setPrevBlockOpen(false);	
//				
//				// For the blocks connected by the switch, make sure the connection
//				// to the unconnected 
//				if (line[connectedBlock1].getNext() == unconnectedBlock)
//					line[connectedBlock1].setNextBlockOpen(false);
//				else
//					line[connectedBlock1].setNextBlockOpen(true);
//				if (line[connectedBlock1].getPrev() == unconnectedBlock)
//					line[connectedBlock1].setPrevBlockOpen(false);
//				else
//					line[connectedBlock1].setPrevBlockOpen(true);				
//				
//				if (line[connectedBlock2].getNext() == unconnectedBlock)
//					line[connectedBlock2].setNextBlockOpen(false);
//				else
//					line[connectedBlock2].setNextBlockOpen(true);
//				if (line[connectedBlock2].getPrev() == unconnectedBlock)
//					line[connectedBlock2].setPrevBlockOpen(false);
//				else
//					line[connectedBlock2].setPrevBlockOpen(true);	
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
