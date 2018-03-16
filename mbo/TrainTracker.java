/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import track_model.TrackBlock;
import train_model.TrainModel;
import train_model.communication.MboRadio;
import track_model.GlobalCoordinates;
// import train_model.Train;
/**
 *
 * @author Fenne
 */
public class TrainTracker 
{
	TrainModel train;
	TrackBlock block;
	int authority;
	int suggestedSpeed;
	MboRadio radio;
	
	public TrainTracker(TrainModel newTrain, TrackBlock newBlock, MboRadio newRadio)
	{
		train = newTrain;
		block = newBlock;
		authority = 0;
		suggestedSpeed = 0;
		radio = newRadio;
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
	
	public GlobalCoordinates getLocation()
	{
		return radio.receive();
	}
	
}
