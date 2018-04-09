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
import java.util.logging.Level;
import java.util.logging.Logger;
//import mbo.Mbo;
import track_controller.TrackController;
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

	// FIXME: see imports and initialize()
	private static Ctc ctc;
	private static TrackModel trackModel;

	// temporary single references for initial integration
	private static TrainModel singleTrainModel;
	private static TrainController singleTrainController;
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

		// FIXME: launches UI in constructor
		ctc = new Ctc();

		trackModel = new TrackModel();
                
                //
                // Forces user to load track database into program.
                // Will exit otherwise.
                //
                if (!trackModel.doTablesExist())    {
                    try{
                        trackModel.launchInitialUI();
                        while(trackModel.getBlockCount() < 3)  {
                            Thread.sleep(1000);
                        }
                        Thread.sleep(1000);
                        trackModel.resetInitialUICloseOperation();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
		
		MboController mboCont = new MboController("Green");
		MboScheduler mboSched = new MboScheduler("Green");

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

		singleTrainController = trainControllers[0];
		singleTrainModel = trainModels[0];

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
		ctc.setTrackModel(trackModel);

		// track model <---> train model
		for (TrainModel trainModel : trainModels)
			trackModel.registerTrain(trainModel, "Green");

		// train model <---> train controller
		for (int i = 0; i < trainControllers.length; ++i)
			trainControllers[i].registerTrain(
				trainModels[i].controllerLink()
			);
		
		
		// Track Model <---> MBO
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

		// FIXME: see instantiations above
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
				       singleTrainModel,
				       singleTrainController)
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

		updater.scheduleAtFixedRate(wallUpdatePeriod);
	}
}
