/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo;

import java.awt.EventQueue;

import track_model.GlobalCoordinates;
import train_model.TrainModel;
import train_model.subsystem_demo.UI;
import train_model.subsystem_demo.train_controller.Controller;
import train_model.subsystem_demo.mbo.MBO;
import train_model.subsystem_demo.track_model.TrackModel;
import updater.Updateable;
import updater.Updater;

public class Main
{
	private static boolean print = false;

	public static void main(String[] args)
	{
		TrackModel track = new TrackModel();
		TrainModel train = new TrainModel();
		MBO mbo = new MBO();

		// in real implemenation, won't be passing the train here
		Controller controller =
			new Controller(train.controllerLink(), train);

		track.registerTrain(train);

		mbo.registerTrainRadio(train.mboRadio());

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new UI(train).setVisible(true);
			}
		});

		Updateable[] objects = {
			track,
			train,
			mbo,
			controller
		};

		Updater updater = new Updater(objects);

		for (;;)
		{
			for (int i = 0; i < objects.length; ++i)
				updater.iteration();

			printTrainState(train);

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// let it be
			}
		}
	}

	private static void printTrainState(TrainModel train)
	{
		if (!print)
			return;

		GlobalCoordinates origin = new GlobalCoordinates(0.0, 0.0);

		System.out.printf(
			"position (%5.1f %5.1f) y  " +
			"speed %4.1f m/s  " +
			"heading %3.1f\n",
			origin.yDistanceTo(train.location()),
			origin.xDistanceTo(train.location()),
			train.speed(),
			train.orientation().degrees()
		);
	}
}
