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
import updater.Clock;
import track_model.GlobalCoordinates;
import track_model.TrackModel;
import track_model.TrackBlock;
import train_model.TrainModel;
import track_model.Orientation;
import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;
import train_model.communication.TrackMovementCommand;
import mbo.schedules.*;

// Main MBO model

public class MboController implements Updateable
{
	private ArrayList<TrainTracker> trains = new ArrayList<TrainTracker>();
	private BlockTracker[] line; 
	private MboControllerUI ui;
	private String lineName;
	private boolean enabled;
	private boolean automaticDispatch;
	private CtcRadio ctcRadio;
	private TrackModel trackModel;
	
	private ArrayList<SwitchTracker> switches;
	
	private static double MAX_AUTHORITY = 3000; // meter
	private static double LOCATION_MARGIN = 1; // meter
	private static int DWELL_TIME = 20 * 1000; // ms
	
	public MboController(String ln)
	{
		lineName = ln;
		enabled = false;
		automaticDispatch = false;
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
			if (enabled)
				ui.setMboEnabled(true);
			else
				ui.setMboEnabled(false);
			ui.requestCompleted();
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
			{
				ui.setMboEnabled(false);
			}
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
				String stationName = null;
				if (curBlock.isIsStation())
					stationName = trackModel.getStation(lineName, i).getName();
				line[i] = new BlockTracker(i, nextBlock, prevBlock, blockLength, speedLimit, section, stationName, forwardDir, backwardDir);
				if (curBlock.isIsSwitch())
				{
					int switchID = curBlock.getBlock();
					switchIDs.add(switchID);
				}
			}
			
			for (int switchID : switchIDs)
			{
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
			}
		}
	}

	// Updates this object.
	public void update(int time)
	{
		for (int i = 0; i < trains.size(); i++) 
		{
			TrainTracker trainInfo = trains.get(i);
			int newBlock = getBlockFromLoc(trainInfo.getLocation());
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
				ctcRadio.enableMovingBlock();
				ui.requestCompleted();
			}
			else if (request == MboControllerUI.Request.DISABLE_MBO)
			{
				ctcRadio.disableMovingBlock();
				ui.requestCompleted();
			}
		}

		if (enabled)
		{
			for (TrainTracker trainInfo:trains)
			{
				if (trainInfo == null)
					continue;
				double authority = findAuthority(trainInfo);
				if (trainAtStation(trainInfo))
				{
					if (!trainInfo.isStoppedAtStation())
					{
						trainInfo.stopAtStation(true);
						trainInfo.resetTimeStopped();
					}
					else if (trainInfo.getTimeStopped() < DWELL_TIME)
					{
						trainInfo.incrementTimeStopped(time);
						authority = 0;
					}
					else
					{
						trainInfo.incrementTimeStopped(time);
						authority = 100;		
					}
				}
				else if (trainInfo.isStoppedAtStation())
				{
					trainInfo.stopAtStation(false);
				}
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
		else if (ui != null)
		{
			for (TrainTracker trainInfo : trains)
			{
				int trackDist = (int) trackModel.getDistanceAlongBlock(lineName, trainInfo.block.getID(), trainInfo.getCurrentPosition());
				ui.updateTrain(trainInfo.getID(), trainInfo.block.getSection(), trainInfo.block.getID(), trackDist, 0, 0);
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
		// BlockTracker blockingBlock = curBlock;
		GlobalCoordinates trainLoc = train.getCurrentPosition();
		double trainDist = trackModel.getDistanceAlongBlock(lineName, curBlock.getID(), trainLoc); 
		double distInBlock = 0;
		if (!train.isGoingForward())
			distInBlock = train.getBlock().getLength() - trainDist;
		else
			distInBlock = trainDist;
		
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
		double authority = -1*distInBlock;
		boolean blocked = false;
		boolean forward = train.isGoingForward();
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
			
			// Stop at a station
			if (curBlock.getStation() != null)
			{
				authority += curBlock.getLength() / 2;
				blocked = true;
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
				if (curBlock.getPrev() < 0)
				{
					blocked = true;
					authority -= curBlock.getLength();
				}
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
		int newBlockCounter = train.getNewBlockCounter();
		if (newBlockID != oldBlockID && newBlockCounter == 0)
		{
			train.incrementNewBlockCounter();
		}
		else if (newBlockCounter > 0 || newBlockCounter < 10)
			train.incrementNewBlockCounter();	
		else 
		{
			double distanceAlongBlock = trackModel.getDistanceAlongBlock(lineName, newBlockID, newLocation);
			if (distanceAlongBlock < 1)
				train.setGoingForward(true);
			else
				train.setGoingForward(false);
		}
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
	
	private boolean trainAtStation(TrainTracker train)
	{
		if (train.getBlock().getStation() != null)
		{
			GlobalCoordinates location = train.getCurrentPosition();
			double distAlongBlock = trackModel.getDistanceAlongBlock(lineName, train.getBlock().getID(), location);
			double stationLocation = train.getBlock().getLength() / 2;
			double margin = 3; // 3 meters within station
			if (distAlongBlock > stationLocation - margin || distAlongBlock < stationLocation + margin)
				return true;
		}
		return false;
	}
}
