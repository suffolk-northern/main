/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

import train_model.communication.BeaconMessage;
import train_model.communication.Relay;

// Train-Beacon wireless communication simulation
//
// From the POV of the beacon
//
// Is associated with a TrainModel.
//
// Is stateless, so any number of instances may be used.

public class BeaconRadio
{
	private final Relay relay;

	// Constructs a BeaconRadio that interacts with a Relay.
	public BeaconRadio(Relay relay)
	{
		this.relay = relay;
	}

	public void send(BeaconMessage message)
	{
		relay.onRXBeacon(message);
	}
}
