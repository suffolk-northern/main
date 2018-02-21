/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import track_model.TrackBlock;
import train_model.Train;
/**
 *
 * @author Fenne
 */
public class TrainTracker 
{
	FakeTrain train;
	TrackBlock block;
	int authority;
	int suggestedSpeed;
	
	public TrainTracker(FakeTrain newTrain, TrackBlock newBlock)
	{
		train = newTrain;
		block = newBlock;
		authority = 0;
		suggestedSpeed = 0;
	}
	
	public void setAuthority(int newAuthority)
	{
		authority = newAuthority;
	}
	
	public void setSuggestedSpeed(int newSpeed)
	{
		suggestedSpeed = newSpeed;
	}
	
	public void setBlock(TrackBlock newBlock)
	{
		block = newBlock;
	}
	
	public int getAuthority()
	{
		return authority;
	}
	
	public int getSuggestedSpeed()
	{
		return suggestedSpeed;
	}
	
	public TrackBlock getBlock()
	{
		return block;
	}
	
}
