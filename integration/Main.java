/*
 * Ryan Matthews
 *
 * Integration
 */

package integration;

import java.util.ArrayList;
import java.util.Arrays;

import java.awt.EventQueue;

import ctc.Ctc;
import java.util.logging.Level;
import java.util.logging.Logger;
import mbo.MboController;
import mbo.MboScheduler;
import mbo.CtcRadio;
import track_model.TrackModel;
import train_controller.TrainController;
import train_model.TrainModel;
import updater.Updateable;
import updater.Updater;
import updater.ClockMultiplier;

public class Main
{
	private static ArrayList<Updateable> updateables =
		new ArrayList<Updateable>();

	private static Ctc ctc;
	private static TrackModel trackModel;
	private static TrainModel[] trainModels;
	private static TrainController[] trainControllers;
	private static MboController mboController;

	public static void main(String[] args)
	{
		initializeModules();
		launchUI();
		scheduleUpdates();
	}

	// Initializes/links modules and fills updateables.
	private static void initializeModules()
	{
		//
		// instantiate modules
		//

		ctc = new Ctc();

		trackModel = new TrackModel();

		//
		// Forces user to load track database into program.
		// Will exit otherwise.
		//
		if (!trackModel.doTablesExist()) {
			try {
				trackModel.launchInitialUI();
				while (trackModel.getBlockCount() < 3) {
					Thread.sleep(1000);
				}
				Thread.sleep(1000);
				trackModel.closeInitialUI();
			} catch (InterruptedException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		MboController mboCont = new MboController("Green");
		MboScheduler mboSched = new MboScheduler("Green");

		final int numberOfTrainsGreen = 3;
		final int numberOfTrainsRed = 3;
		final int numberOfTrains = numberOfTrainsGreen + numberOfTrainsRed;

		trainControllers = new TrainController[numberOfTrains];

		trainModels = new TrainModel[numberOfTrains];

		for (int i = 0; i < numberOfTrains; ++i)
		{
			trainControllers[i] = new TrainController();
			trainModels[i] = new TrainModel(i, trackModel);
		}

		ArrayList<Updateable> trainObjects =
			new ArrayList<Updateable>();

		trainObjects.addAll(Arrays.asList(trainControllers));
		trainObjects.addAll(Arrays.asList(trainModels));

		ClockMultiplier trainMultiplier = new ClockMultiplier(
			10,
			trainObjects.toArray(new Updateable[0])
		);

		//
		// link modules
		//

		// CTC <---> track controller
		ctc.setTrackModel(trackModel);
		
		// CTC <---> trains
		for(int i = 0; i < numberOfTrains; i++)
		{
			if(i < numberOfTrainsGreen)
				ctc.setTrain("green",i);
			else
				ctc.setTrain("red",i);
		}
				
		
		
		
		// track model <---> CTC
		trackModel.registerCtc(ctc);

		// track model <---> train model

		for (TrainModel trainModel : trainModels)
		{
			if(trainModel.id() < numberOfTrainsGreen)
				trackModel.registerTrain(trainModel, "Green");
			else
				trackModel.registerTrain(trainModel, "Red");
		}

		// train model <---> train controller
		for (int i = 0; i < trainControllers.length; ++i)
			trainControllers[i].registerTrain(
				trainModels[i].controllerLink()
			);
		
		
		// Track Model <---> MBO
		mboCont.registerTrackModel(trackModel);

		mboCont.initLine();
		// mboSched.initLine();

		// train model <---> MBO
		for (TrainModel trainModel : trainModels)
			mboCont.registerTrain(trainModel.id(), trainModel.mboRadio());
		
		// CTC <--> MBO
		CtcRadio ctcRadio = new CtcRadio(mboCont, mboSched, ctc);
		ctc.setCtcRadios(ctcRadio,null);
		mboCont.registerCtc(ctcRadio);

		//
		// fill updateables
		//

		updateables.add(ctc);
		updateables.add(mboCont);
		//updateables.add(mboSched);
		updateables.add(trackModel);
		updateables.add(trainMultiplier);
	}

	// Starts the user interface.
	private static void launchUI()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new UI(ctc, trackModel,
				       trainModels,
				       trainControllers)
				          .setVisible(true);
			}
		});
	}

	// Runs an updater on updateables.
	//
	// Returns immediately. Updates occur in a worker thread.
	//
	// Currently there's no way to stop this other than
	// interrupting/killing the program.
	private static void scheduleUpdates()
	{
		// milliseconds
		final int simulationUpdatePeriod = 100;
		final int       wallUpdatePeriod = 100;

		Updater updater = new Updater(
			simulationUpdatePeriod,
			updateables.toArray(new Updateable[0])
		);
		
		ctc.setUpdater(simulationUpdatePeriod, updater);

		updater.scheduleAtFixedRate(wallUpdatePeriod);
	}
}
