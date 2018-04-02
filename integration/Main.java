/*
 * Ryan Matthews
 *
 * Integration
 */

package integration;

import java.util.ArrayList;
import java.util.Arrays;

import java.awt.EventQueue;

// FIXME: package names don't match directory names: ctc, train_controller
//
// FIXME: naming convention: trainController should be TrainController
//
// FIXME: naming convention: pick Ctc and Mbo or CTC and MBO
//
import ctc.Ctc;
//import mbo.Mbo;
import track_controller.TrackController;
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

	// FIXME: see imports and initialize()
	private static Ctc ctc;
	//private static Mbo mbo;
	private static TrackModel trackModel;

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

		// FIXME: launches UI in constructor
		ctc = new Ctc();

		TrackController[] trackControllers = {
			new TrackController(),
			new TrackController(),
		};

		trackModel = new TrackModel();
		
		// mboCont = new MboController("green");
		// mboSched = new MboScheduler("green");

		final int numberOfTrains = 1;

		TrainController[] trainControllers =
			new TrainController[numberOfTrains];

		TrainModel[] trainModels =
			new TrainModel[numberOfTrains];

		for (int i = 0; i < numberOfTrains; ++i)
		{
			trainControllers[i] = new TrainController();
			trainModels[i] = new TrainModel(i);
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

		// FIXME: none of this linking works

		// CTC <---> track controller
		//for (TrackController trackController : trackControllers)
		//	ctc.registerTrackController(trackController.ctcLink());

		// track controller <---> track model
		//for (TrackController trackController : trackControllers)
		//	trackModel.configureTrackController(trackController);

		// track model <---> train model
		//for (TrainModel trainModel : trainModels)
		//	trackModel.registerTrain(trainModel);

		// train model <---> train controller
		for (int i = 0; i < trainControllers.length; ++i)
			trainControllers[i].registerTrain(
				trainModels[i].controllerLink()
			);

		// train model <---> MBO
		//for (TrainModel trainModel : trainModels)
		//	mboCont.registerTrain(trainModel.id(), trainModel.mboRadio());
		
		// CTC <--> MBO
		// ctcRadio = new CtcRadio(mboCont, mboSched, ctc);
		// ctc.registerMbo(ctcRadio);
		// mboCont.registerCtc(ctcRadio);
		
		// Track Model <---> MBO
		// mboCont.initLine();
		// mboSched.initLine();

		//
		// fill updateables
		//

		// FIXME: see instantiations above
		updateables.add(ctc);
		//updateables.add(mbo);
		updateables.addAll(Arrays.asList(trackControllers));
		//updateables.add(trackModel);
		updateables.add(trainMultiplier);
	}

	// Starts the user interface.
	private static void launchUI()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// FIXME: see imports and initialize()
				//new UI(ctc, mbo, trackModel);
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

		updater.scheduleAtFixedRate(wallUpdatePeriod);
	}
}
