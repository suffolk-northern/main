/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import track_model.GlobalCoordinates;

/**
 *
 * @author Fenne
 */
public class FakeTrain {
	GlobalCoordinates location;
	int trainID;
	
	public FakeTrain(GlobalCoordinates newLocation, int newID)
	{
		location = newLocation;
		trainID = newID;
	}
	
	public GlobalCoordinates location()
	{
		return location;
	}
	
	public int getID()
	{
		return trainID;
	}
}
