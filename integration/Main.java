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
import mbo.Driver;

public class Main
{
	private static ArrayList<Updateable> updateables =
		new ArrayList<Updateable>();

	private static Ctc ctc;
	private static TrackModel trackModel;
	private static TrainModel[] trainModels;
	private static TrainController[] trainControllers;
	private static MboController mboController;
	private static Driver[] drivers;

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
		
		String[] lineNames = {"Green", "Red"};
		MboController[] mboCont = new MboController[lineNames.length];
		MboScheduler[] mboSched = new MboScheduler[lineNames.length];
		for (int i = 0; i < lineNames.length; i++)
		{
			mboCont[i] = new MboController(lineNames[i]);
			mboSched[i] = new MboScheduler(lineNames[i]);
		}

		final int numberOfTrainsGreen = 3;
		final int numberOfTrainsRed = 3;
		final int numberOfTrains = numberOfTrainsGreen + numberOfTrainsRed;

		trainControllers = new TrainController[numberOfTrains];

		trainModels = new TrainModel[numberOfTrains];
		
		final int numberOfDriversGreen = 10;
		final int numberOfDriversRed = 10;
		final int numberOfDrivers = numberOfDriversGreen + numberOfDriversRed;
		
		drivers = new Driver[numberOfDrivers];

		for (int i = 0; i < numberOfTrains; ++i)
		{
			trainControllers[i] = new TrainController();
			trainModels[i] = new TrainModel(i, trackModel);
		}
		
		for (int i = 0; i < numberOfDrivers; i++)
		{
			drivers[i] = new Driver(i);
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
		for (int i = 0; i < lineNames.length; i++)
		{
			mboCont[i].registerTrackModel(trackModel);
			mboSched[i].registerTrackModel(trackModel);	
			mboCont[i].initLine();
			mboSched[i].initLine();
		}

		// train model <---> MBO
		for (TrainModel trainModel : trainModels)
		{
			if (trainModel.id() < numberOfTrainsGreen)
			{
				mboCont[0].registerTrain(trainModel.id(), trainModel.mboRadio());
				mboSched[0].registerTrain(trainModel.id());
			}
			else
			{
				mboCont[1].registerTrain(trainModel.id(), trainModel.mboRadio());
				mboSched[1].registerTrain(trainModel.id());
			}
		}
		
		// CTC <--> MBO
		CtcRadio[] ctcRadio = new CtcRadio[lineNames.length];
		for (int i = 0; i < lineNames.length; i++)
		{
			ctcRadio[i] = new CtcRadio(mboCont[i], mboSched[i], ctc);
			mboCont[i].registerCtc(ctcRadio[i]);
			mboSched[i].registerCtc(ctcRadio[i]);
		}
		ctc.setCtcRadios(ctcRadio[0], ctcRadio[1]);
		
		for (Driver driver : drivers)
		{
			if (driver.getID() < numberOfDriversGreen)
				mboSched[0].registerDriver(driver.getID());
			else
				mboSched[1].registerDriver(driver.getID());
		}

		//
		// fill updateables
		//

		updateables.add(ctc);
		for (int i = 0; i < lineNames.length; i++)
		{
			updateables.add(mboCont[i]);
			updateables.add(mboSched[i]);
		}
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
