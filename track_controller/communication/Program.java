/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package track_controller.communication;

import track_controller.TrackController;
import track_controller.communication.Branch;

// Track controller program
//
// Simulates a "file" that a programmer loads onto the device.

public class Program
{
	// Common, left, and right branch lengths
	//
	// Representation: unary "tally marks"
	//
	// Each array SHOULD be TrackController.MAX_BRANCH_SIZE in length. The
	// "syntax" check for that occurs at load time, NOT at the time of
	// creation of this object.
	//
	// The integer length is represented as a run of 1s followed by a run
	// of 0s, e.g.,
	//
	//   length 2 = [ 1, 1, 0, 0, 0, 0, 0, 0, ... 0 ]
	//   length 5 = [ 1, 1, 1, 1, 1, 0, 0, 0, ... 0 ]
	//
	// Length 0 is illegal and results in an error at load time.
	public boolean[] commonBranchLength;
	public boolean[]   leftBranchLength;
	public boolean[]  rightBranchLength;

	// Returns a boolean array of size TrackController.MAX_BRANCH_SIZE,
	// with the first length elements set to true, and the remaining
	// elements set to false.
	//
	// Makes it easier to write a Program.
	//
	// Throws IllegalArgumentException if length not in range
	// [0, TrackController.MAX_BRANCH_SIZE].
	public static boolean[] run(int length)
		throws IllegalArgumentException
	{
		if (length < 0 || length > TrackController.MAX_BRANCH_SIZE)
			throw new IllegalArgumentException("length bounds");

		boolean[] array = new boolean[TrackController.MAX_BRANCH_SIZE];

		for (int i = 0; i < length; ++i)
			array[i] = true;

		return array;
	}

	// Returns the number of leading ones in a boolean array.
	//
	// Throws IllegalArgumentException if array is not length
	// TrackController.MAX_BRANCH_SIZE, or if it does not contain a run
	// leading ones (of any length) followed by all zeros.
	//
	// Makes it easier to parse a Program.
	public static int parseRun(boolean[] array)
		throws IllegalArgumentException
	{
		if (array.length != TrackController.MAX_BRANCH_SIZE)
			throw new IllegalArgumentException("length");

		int i = 0;

		for (; i < array.length; ++i)
			if (array[i] == false)
				break;

		for (int j = i; j < array.length; ++j)
			if (array[j] == true)
				throw new IllegalArgumentException("format");

		return i;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member is null.
	public Program(boolean[] commonBranchLength,
	               boolean[]   leftBranchLength,
	               boolean[]  rightBranchLength)
		throws IllegalArgumentException
	{
		if (commonBranchLength == null ||
		      leftBranchLength == null ||
		     rightBranchLength == null)
			throw new IllegalArgumentException("member is null");

		this.commonBranchLength = commonBranchLength;
		this.  leftBranchLength =   leftBranchLength;
		this. rightBranchLength =  rightBranchLength;
	}
}
