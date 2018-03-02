/*
 * Ryan Matthews
 *
 * Train model subsystem demo
 */

package train_model.subsystem_demo.track_model;

import track_model.GlobalCoordinates;
import track_model.Orientation;
import train_model.TrainModel;

// Steers a train to follow a hard-coded track

public class Steerer
{
	private static GlobalCoordinates origin =
		new GlobalCoordinates(0.0, 0.0);

	public TrainModel train;
	private int block = 0;
	private boolean stopped = false;

	// Constructs a Steerer for a train.
	public Steerer(TrainModel train)
	{
		this.train = train;
	}

	// Sets the orientation of the train for its location.
	public void steer()
	{
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

		double heading;

		switch (block)
		{
			case 0:  heading =   0.0; break;
			case 1:  heading =   0.0; break;
			case 2:  heading =   0.0; break;
			case 3:  heading =  90.0; break;
			case 4:  heading = 180.0; break;
			case 5:  heading = 180.0; break;
			case 6:  heading = 180.0; break;
			default: heading = 180.0; break;
		}

		train.orientation(Orientation.degrees(heading));
	}
}
