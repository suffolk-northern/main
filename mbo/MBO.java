/*
 * Ryan Matthews
 *
 * Main
 */

package mbo;

import java.util.ArrayList;

import updater.Updateable;
import track_model.GlobalCoordinates;
import track_model.Track;
import track_model.TrackBlock;
import track_model.TrackSection;
import train_model.Train;

// Main MBO model

public class MBO implements Updateable
{
	private static final GlobalCoordinates pittsburgh =
		new GlobalCoordinates(40.0, 80.0);

	private ArrayList<Train> trains = new ArrayList<Train>();
	
	private Track myTrack;

	// Updates this object.
	public void update()
	{
		int index = 0;

		for (Train train : trains) 
		{
			
			// double distance = location.distanceTo(pittsburgh);

			System.out.printf(
				"MBO: train %d is %d yards from Pittsburgh\n",
				index++,
				Math.round(distance)
			);
		}
	}
	
	// Adds a Train to the set of objects this object communicates with.
	public void registerTrain(Train train)
	{
		trains.add(train);
	}

	// Removes a train from the set of objects this object communicates
	// with.
	public void unregisterTrain(Train train)
	{
		trains.remove(train);
	}
        
	public void findAuthority(Train train)
	{
		GlobalCoordinates curLoc = train.location();
		for (Train otherTrain: trains)
		{
			
		}
			
	}
}
