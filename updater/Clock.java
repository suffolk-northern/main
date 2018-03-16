/*
 * Ryan Matthews
 *
 * Module time tracking
 */

package updater;

import java.util.Date;

// Simulation clock which advances manually

public class Clock
{
	// milliseconds
	private long elapsedTime = 0;

	private Date date;

	// Constructs a Clock which starts at the current system time.
	public Clock()
	{
		date = new Date();
	}

	// Constructs a Clock which starts at the given date.
	//
	// Throws IllegalArgumentException if date is null.
	public Clock(Date date)
		throws IllegalArgumentException
	{
		if (date == null)
			throw new IllegalArgumentException("date is null");

		this.date = date;
	}

	// Advances the current time.
	//
	// Parameter time is amount to advance in milliseconds.
	//
	// Throws IllegalArgumentException if time is negative.
	public void advance(int time)
	{
		if (time < 0)
			throw new IllegalArgumentException("time is negative");

		date = new Date(date.getTime() + time);
	}

	// Returns the current time.
	public Date time()
	{
		return date;
	}
}
