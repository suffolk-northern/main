/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.mbo;

import mbo.MboController;
import mbo.FakeTrain;
import track_model.GlobalCoordinates;
import track_model.TrackBlock;

/**
 *
 * @author Fenne
 */
public class MBODemo 
{
	private static int maxTrains = 4;
	
	public static void main(String[] args)
	{
		MboController mbo = new MboController();
		
		MBODemoUI ui = new MBODemoUI();
		ui.setVisible(true);
		
		int numTrains = 0;
		GlobalCoordinates origin = new GlobalCoordinates(0, 0);
		while (true)
			numTrains = update(ui, mbo, numTrains, origin);
	}
	
	public static int update(MBODemoUI ui, MboController mbo, int numTrains, GlobalCoordinates origin)
	{
		if (ui.addedTrain)
		{
			if (numTrains == maxTrains)
				ui.addedTrain = false;
			else
			{
				FakeTrain testTrain = new FakeTrain(origin, numTrains+1);
				TrackBlock startBlock = mbo.getDefaultBlock();
				mbo.registerTrain(testTrain, startBlock);
				ui.addTrain(testTrain);
				ui.addedTrain = false;
				numTrains += 1;
			}
		}
		
		if (ui.switch1Flipped)
		{
			mbo.flipSwitch(0);
			ui.switch1Flipped = false;
		}
		
		if (ui.switch2Flipped)
		{
			mbo.flipSwitch(1);
			ui.switch2Flipped = false;
		}
		
		if (ui.trainChangedID > -1)
		{
			double newLat = ui.latChanged;
			double newLon = ui.lonChanged;
			GlobalCoordinates newLoc = origin.addYards(newLat, newLon);
			int error = mbo.changeTrainLocation(ui.trainChangedID, newLoc);
			if (error == 1)
				ui.setError("Not a valid train location, using old location");
			else
				ui.setError(" ");
			ui.trainChangedID = -1;
		}
		
		mbo.update();
		return numTrains;
	}
}
