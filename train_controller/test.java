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
		TrainModel model = new TrainModel();

		Relay relay = new Relay(model);

		ControllerLink link = new ControllerLink(relay, model);

		TrainController controller = new TrainController(link);

		// Combine model & controller into a 'burst' module (100 cycles)
		Updateable [] burst_arr = {
			model,
			controller
		};
		ClockMultiplier burst = new ClockMultiplier(100, burst_arr);

		// dummy module for testing
		TrainModel dummy = new TrainModel();

		Updateable [] arr = {
			dummy,
			burst
		};

		// Update time for each module is 100 ms
		System.out.println("got here");
		Updater modules = new Updater(100, arr);

		// Time between updates is 100 ms. Start executing
		modules.scheduleAtFixedRate(100);

	}
}
