/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo.mbo;

import java.util.ArrayList;

import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;
import updater.Updateable;

public class MBO implements Updateable
{
	private static int COMMAND_MOD = 50;
	private int commandCounter = 0;
	private boolean commandState = false;

	private ArrayList<MboRadio> radios = new ArrayList<MboRadio>();

	// Updates this object.
	public void update(int time)
	{
		for (MboRadio radio : radios) {
			radio.send(new MboMovementCommand(
				commandState ? 35 : 25,
				50
			));
		}

		if (commandCounter == 0)
			commandState = !commandState;

		commandCounter = (commandCounter + 1) % COMMAND_MOD;
	}

	// Adds a radio to the set of objects this object communicates with.
	public void registerTrainRadio(MboRadio radio)
	{
		radios.add(radio);
	}
}
