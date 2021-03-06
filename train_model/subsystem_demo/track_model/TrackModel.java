/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo.track_model;

import java.util.ArrayList;

import track_controller.communication.Authority;
import train_model.TrainModel;
import train_model.communication.BeaconMessage;
import train_model.communication.BeaconRadio;
import train_model.communication.TrackMovementCommand;
import train_model.communication.TrackCircuit;
import train_model.subsystem_demo.track_model.Steerer;
import updater.Updateable;

// BS hard-coded track model

public class TrackModel implements Updateable
{
	private static int BEACON_MOD = 100;
	private int beaconCounter = 0;
	private int helloCounter = 0;

	private static int TRACK_CIRCUIT_MOD = 50;
	private int trackCircuitCounter = 25;
	private boolean trackCircuitState = false;

	private ArrayList<Steerer> steerers = new ArrayList<Steerer>();

	// Updates this object.
	public void update(int time)
	{
		for (Steerer steerer : steerers) {
			steerer.steer();
			updateBeacon(steerer.train.beaconRadio());
			sendCommand(steerer.train.trackCircuit());
		}

		updateCounters();
	}

	// Adds a TrainModel to get orientation updates.
	public void registerTrain(TrainModel train)
	{
		steerers.add(new Steerer(train));
	}

	private void updateBeacon(BeaconRadio radio)
	{
		if (beaconCounter != 0)
			return;

		radio.send(
			new BeaconMessage(String.format(
				"Hello world! %03d",
				++helloCounter
			))
		);

	}

	private void sendCommand(TrackCircuit circuit)
	{
		if (trackCircuitCounter != 0)
			return;

		circuit.send(
			new TrackMovementCommand(
				10,
				trackCircuitState ? 10 : 20
			)
		);
	}

	private void updateCounters()
	{
		if (trackCircuitCounter == 0)
			trackCircuitState = !trackCircuitState;

		beaconCounter = (beaconCounter + 1) % BEACON_MOD;

		trackCircuitCounter =
			(trackCircuitCounter + 1) % TRACK_CIRCUIT_MOD;
	}
}
