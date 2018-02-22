/*
 * Ryan Matthews
 *
 * Basic demonstration of Train and related inter-module communication
 */

package test;

import train_model.DoorLocation;
import train_model.Train;
import train_model.communication.BeaconMessage;
import train_model.communication.BeaconRadio;
import train_model.communication.ControllerLink;
import train_model.communication.MboRadio;
import train_model.communication.MovementCommand;
import train_model.communication.TrackCircuit;

public class TestTrain
{
	public static void main(String[] args)
	{
		Train train = new Train();

		sendBeacon(train.beaconRadio());
		sendMboMessage(train.mboRadio());
		sendTrackMessage(train.trackCircuit());

		commandTrain(train.controllerLink());

		move(train);

		receiveMessages(train.controllerLink());
	}

	// from perspective of beacons
	public static void sendBeacon(BeaconRadio radio)
	{
		radio.send(new BeaconMessage("hello world"));
	}

	// from perspective of MBO
	public static void sendMboMessage(MboRadio radio)
	{
		radio.send(new MovementCommand(35, 500));
	}

	// from perspective of track
	public static void sendTrackMessage(TrackCircuit circuit)
	{
		circuit.send(new MovementCommand(25, 1000));
	}

	// from perspective of train controller
	public static void receiveMessages(ControllerLink link)
	{
		BeaconMessage beacon = link.receiveFromBeacons();

		MovementCommand   mboCommand = link.receiveFromMbo();
		MovementCommand trackCommand = link.receiveFromTrack();

		double speed = link.speed();

		System.out.printf(
			"Train Controller received:\n" +
			"  beacon string \"%s\"\n" +
			"  MBO   command [%d, %d]\n" +
			"  track command [%d, %d]\n" +
			"  speedometer %f\n",
			beacon.string,
			  mboCommand.speed,   mboCommand.authority,
			trackCommand.speed, trackCommand.authority,
			speed
		);
	}

	// from perspective of train controller
	public static void commandTrain(ControllerLink link)
	{
		link.power(0.5);
		link.serviceBrake(0.25);
		link.applyEmergencyBrake();

		link.openDoor(DoorLocation.left);
		link.closeDoor(DoorLocation.right);
	}

	// Demonstrates movement as a PointMass.
	public static void move(Train train)
	{
		final int iterations = 10 * 10;

		for (int i = 0; i < iterations; ++i)
			train.update();
	}
}
