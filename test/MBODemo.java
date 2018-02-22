/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import mbo.MBO;
import mbo.FakeTrain;
import track_model.GlobalCoordinates;
import track_model.TrackBlock;

/**
 *
 * @author Fenne
 */
public class MBODemo 
{
	public static void main(String[] args)
	{
		MBO mbo = new MBO();

		
		MBODemoUI ui = new MBODemoUI();
		ui.setVisible(true);
		
		while (true)
			update(ui, mbo);
	}
	
	public static void update(MBODemoUI ui, MBO mbo)
	{
		if (ui.addedTrain)
		{
			GlobalCoordinates startPoint = new GlobalCoordinates(0, 0);
			FakeTrain testTrain= new FakeTrain(startPoint, 1);
			TrackBlock startBlock = mbo.getDefaultBlock();
			mbo.registerTrain(testTrain, startBlock);
			ui.addedTrain = false;
		}
	}
}
