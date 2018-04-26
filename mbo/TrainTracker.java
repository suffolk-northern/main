/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import train_model.communication.MboRadio;
import track_model.GlobalCoordinates;
// import train_model.Train;
/**
 *
 * @author Kaylene Stocking
 */
public class TrainTracker 
{
	int trainID;
	BlockTracker block;
	int authority;
	int suggestedSpeed;
	MboRadio radio;
	boolean goingForward;
	boolean enteredNewBlock;
//	GlobalCoordinates lastPosition;
	GlobalCoordinates currentPosition;
	
	public TrainTracker(int newID, BlockTracker newBlock, MboRadio newRadio)
	{
		trainID = newID;
		block = newBlock;
		authority = 0;
		suggestedSpeed = 0;
		radio = newRadio;
		goingForward = true;
		enteredNewBlock = false;
//		lastPosition = GlobalCoordinates.ORIGIN;
		currentPosition = GlobalCoordinates.ORIGIN;
	}
	
	public void setAuthority(int newAuthority)
	{
		authority = newAuthority;
	}
	
	public void setSuggestedSpeed(int newSpeed)
	{
		suggestedSpeed = newSpeed;
	}
	
	public void setBlock(BlockTracker newBlock)
	{
		block = newBlock;
	}
	
	public void setGoingForward(boolean direction)
	{
		goingForward = direction;
	}
	
//	public void setLastPosition(GlobalCoordinates gc)
//	{
//		lastPosition = gc;
//	}
//	
	public void setCurrentPosition(GlobalCoordinates gc)
	{
		currentPosition = gc;
	}
	
	public int getAuthority()
	{
		return authority;
	}
	
	public int getSuggestedSpeed()
	{
		return suggestedSpeed;
	}
	
	public BlockTracker getBlock()
	{
		return block;
	}
	
	public GlobalCoordinates getLocation()
	{
		return radio.receive();
	}
	
	public int getID()
	{
		return trainID;
	}
	
	public MboRadio getRadio()
	{
		return radio;
	}
	
	public boolean isGoingForward()
	{
		return goingForward;
	}
	
	public boolean isOnNewBlock()
	{
		return enteredNewBlock;
	}
	
//	public GlobalCoordinates getLastPosition()
//	{
//		return lastPosition;
//	}
//	
	public GlobalCoordinates getCurrentPosition()
	{
		return currentPosition;
	}
}