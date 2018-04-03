package train_controller;

import train_controller.TrainController;
import train_model.TrainModel;
import train_model.communication.Relay;
import train_model.communication.ControllerLink;
import updater.Updateable;
import updater.Updater;
import updater.ClockMultiplier;



public class test
{
	public static void main(String [] args)
	{
		// Start train model, communication, and controller
		TrainModel model = new TrainModel(0);

		Relay relay = new Relay(model);

		ControllerLink link = new ControllerLink(relay, model);

		TrainController controller = new TrainController();
		controller.registerTrain(link);
		controller.launchGUI();

		// Combine model & controller into a 'burst' module (100 cycles)
		Updateable [] burst_arr = {
			model,
			controller
		};
		ClockMultiplier burst = new ClockMultiplier(100, burst_arr);

		// dummy module for testing
		TrainModel dummy = new TrainModel(1);

		Updateable [] arr = {
			dummy,
			burst
		};

		// Update time for each module is 100 ms
		Updater modules = new Updater(100, arr);

		// Time between updates is 100 ms. Start executing
		modules.scheduleAtFixedRate(100);

	}
}
