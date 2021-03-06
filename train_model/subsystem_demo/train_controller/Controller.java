/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo.train_controller;

import track_model.GlobalCoordinates;
import track_model.Orientation;
import train_model.DoorLocation;
import train_model.TrainModel;
import train_model.communication.ControllerLink;
import updater.Updateable;

public class Controller implements Updateable
{
	private final ControllerLink link;

	private final int DOOR_MOD = 25;
	private int doorCounter = 0;
	private boolean doorState = false;

	private final int HEATER_MOD = 100;
	private int heaterCounter = 0;
	private boolean heaterState = false;

	private final int LIGHTS_MOD = 100;
	private int lightsCounter = 50;
	private boolean lightsState = false;

	// real implementation won't have this
	private final TrainModel train;
	private static GlobalCoordinates origin =
		new GlobalCoordinates(0.0, 0.0);
	private int block = 0;
	private boolean stopped = false;

	// Constructs a Controller linked to a train.
	//
	// Real implementation won't have a TrainModel reference.
	public Controller(ControllerLink link, TrainModel train)
	{
		this.link = link;
		this.train = train;
	}

	// Updates this object.
	public void update(int time)
	{
		// real implementation won't have this information
		double trainX = origin.xDistanceTo(train.location());
		double trainY = origin.yDistanceTo(train.location());

		if (train.location().latitude() < origin.latitude())
			trainY = -trainY;

		double compareXLow  = -1000.0;
		double compareXHigh =  1000.0;
		double compareYLow  = -1000.0;
		double compareYHigh =  1000.0;

		switch (block)
		{
			case 0: compareYHigh =  50.0; break;
			case 1: compareYHigh = 100.0; break;
			case 2: compareYHigh = 150.0; break;
			case 3: compareXHigh =  50.0; break;
			case 4: compareYLow  = 100.0; break;
			case 5: compareYLow  =  50.0; break;
			case 6: compareYLow  =   0.0; break;
		}

		if (trainX < compareXLow || trainX > compareXHigh ||
		    trainY < compareYLow || trainY > compareYHigh    )
			++block;

		if (block == 1 && train.speed() == 0.0)
			stopped = true;

		if (block == 1 && !stopped)
		{
			link.power(0.0);
			link.serviceBrake(0.5);
		}
		else if (block == 7)
		{
			link.power(0.0);
			link.serviceBrake(0.5);
		}
		else
		{
			link.power(0.1);
			link.serviceBrake(0.0);
		}

		if (doorCounter == 0)
			doorState = !doorState;

		if (doorState)
			link.openDoor(DoorLocation.right);
		else
			link.closeDoor(DoorLocation.right);

		doorCounter = (doorCounter + 1) % DOOR_MOD;

		if (heaterCounter == 0)
			heaterState = !heaterState;

		if (heaterState)
			link.heaterOn();
		else
			link.heaterOff();

		heaterCounter = (heaterCounter + 1) % HEATER_MOD;

		if (lightsCounter == 0)
			lightsState = !lightsState;

		if (lightsState)
			link.lightsOn();
		else
			link.lightsOff();

		lightsCounter = (lightsCounter + 1) % HEATER_MOD;
	}
}
