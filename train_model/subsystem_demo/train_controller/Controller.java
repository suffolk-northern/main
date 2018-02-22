/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo.train_controller;

import track_model.GlobalCoordinates;
import track_model.Orientation;
import train_model.DoorLocation;
import train_model.Train;
import train_model.communication.ControllerLink;
import updater.Updateable;

public class Controller implements Updateable
{
	private final ControllerLink link;

	private final int DOOR_MOD = 25;
	private int doorCounter = 0;
	private boolean doorState = false;

	// real implementation won't have this
	private final Train train;
	private static GlobalCoordinates origin =
		new GlobalCoordinates(0.0, 0.0);
	private int block = 0;
	private boolean stopped = false;

	// Constructs a Controller linked to a train.
	//
	// Real implementation won't have a Train reference.
	public Controller(ControllerLink link, Train train)
	{
		this.link = link;
		this.train = train;
	}

	// Updates this object.
	public void update()
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
	}
}
