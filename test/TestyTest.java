/*
 * Ryan Matthews
 *
 * Demonstration of basic Updater.
 */

package test;

import mbo.MBO;
import patrick.Patrick;
import spongebob.Spongebob;
import train_model.TrainModel;
import updater.Updateable;
import updater.Updater;

public class TestyTest
{
	public static void main(String[] args)
	{
		Spongebob spongebob = new Spongebob();
		Patrick patrick = new Patrick();
		TrainModel train0 = new TrainModel();
		TrainModel train1 = new TrainModel();
		MBO mbo = new MBO();

		patrick.registerSpongebob(spongebob);
		mbo.registerTrain(train0);
		mbo.registerTrain(train1);

		Updateable[] objects = {
			spongebob,
			patrick,
			train0,
			train1,
			mbo
		};

		Updater updater = new Updater(objects);

		for (;;)
		{
			updater.iteration();

			// hand-wave the timing
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				// let it be
			}
		}
	}
}
