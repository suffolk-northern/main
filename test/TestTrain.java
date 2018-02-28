/*
 * Ryan Matthews
 *
 * Basic demonstration of Train and related inter-module communication
 */

package test;

import track_controller.communication.Authority;
import train_model.DoorLocation;
import train_model.Train;
import train_model.communication.BeaconMessage;
import train_model.communication.BeaconRadio;
import train_model.communication.ControllerLink;
import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;
import train_model.communication.TrackCircuit;
import train_model.communication.TrackMovementCommand;

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
		radio.send(new MboMovementCommand(35, 500));
	}

	// from perspective of track
	public static void sendTrackMessage(TrackCircuit circuit)
	{
		circuit.send(
			new TrackMovementCommand(
				25,
				new Authority(new boolean[] { false }),
				new Authority(new boolean[] { false }),
				new Authority(new boolean[] { false })
			)
		);
	}

	// from perspective of train controller
	public static void receiveMessages(ControllerLink link)
	{
		BeaconMessage beacon = link.receiveFromBeacons();

		  MboMovementCommand   mboCommand = link.receiveFromMbo();
		TrackMovementCommand trackCommand = link.receiveFromTrack();

		double speed = link.speed();

		System.out.printf(
			"Train Controller received:\n" +
			"  beacon string \"%s\"\n" +
			"  MBO   command [%d, %d]\n" +
			"  track command [%d, %p, %p, %p]\n" +
			"  speedometer %f\n",
			beacon.string,
			  mboCommand.speed,   mboCommand.authority,
			trackCommand.speed,
				trackCommand.commonAuthority,
				trackCommand.  leftAuthority,
				trackCommand. rightAuthority,
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
