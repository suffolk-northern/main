package mbo.schedules;

import java.sql.Time;
import java.lang.Comparable;
import java.util.Comparator;

/**
 *
 * @author Kaylene Stocking
 */
public class TrainDispatch implements Comparable<TrainDispatch>
{
	private int trainID;
	private int driverID;
	private Time time;
	
	public TrainDispatch(int trainID, int driverID, Time time)
	{
		this.trainID = trainID;
		this.driverID = driverID;
		this.time = time;
	}
	
	public int getTrainID()
	{
		return trainID;
	}
	
	public int getDriverID()
	{
		return driverID;
	}
	
	public Time getTime()
	{
		return time;
	}
	
	// Allow dispatches to be sorted by departure time
	public int compareTo(TrainDispatch other)
	{
		if (time.before(other.getTime()))
			return -1;
		else if (time.after(other.getTime()))
			return 1;
		else
			return 0;
	}
	
	public static Comparator<TrainDispatch> TrainDispatchComparator = new Comparator<TrainDispatch>()
	{
		public int compare(TrainDispatch td1, TrainDispatch td2)
		{
			return td1.compareTo(td2);
		}
	};
}
