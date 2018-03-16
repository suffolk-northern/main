/*
 * Ryan Matthews
 *
 * Main
 */

package track_controller;

import java.util.Observable;

import track_controller.communication.Authority;
import track_controller.communication.Branch;
import track_controller.communication.ControllerLink;
import track_controller.communication.CtcLink;
import track_controller.communication.Program;
import track_controller.communication.Speed;
import train_model.communication.TrackMovementCommand;
import updater.Updateable;

// A configurable (programmable logic) track controller box which controls
// track blocks surrounding a single switch
//
// There are 3 branches of track blocks. A common branch exists at the
// converging end of the switch. Left and right branches exist at either
// diverging end of the switch.
//
// Communication links:
//   - to CTC
//   - to other track controllers down common, left, and right branches
//
// The default program is safe. Zero speed and authority are sent over all
// track circuits.

public class TrackController implements Updateable
{
	// in blocks
	public static int MAX_BRANCH_SIZE = 10;

	private static Speed[] defaultSpeeds = {
		new Speed(Speed.run(0)), new Speed(Speed.run(0)),
		new Speed(Speed.run(0)), new Speed(Speed.run(0)),
		new Speed(Speed.run(0)), new Speed(Speed.run(0)),
		new Speed(Speed.run(0)), new Speed(Speed.run(0)),
		new Speed(Speed.run(0)), new Speed(Speed.run(0))
	};

	private Authority[] authorities = {
		new Authority(new boolean[MAX_BRANCH_SIZE]),
		new Authority(new boolean[MAX_BRANCH_SIZE]),
		new Authority(new boolean[MAX_BRANCH_SIZE])
	};

	private Speed[][] speeds = {
		defaultSpeeds,
		defaultSpeeds,
		defaultSpeeds
	};

	private CtcLink ctcLink = new CtcLink(this);

	private ControllerLink[] thisControllerLinks = {
		new ControllerLink(this, Branch.common),
		new ControllerLink(this, Branch.left),
		new ControllerLink(this, Branch.right)
	};

	private ControllerLink[] otherControllerLinks = new ControllerLink[3];

	public static int branchToIndex(Branch branch)
	{
		int index = -1;

		switch (branch)
		{
			case common: index = 0; break;
			case   left: index = 1; break;
			case  right: index = 2; break;
		}

		return index;
	}

	// Returns the track_controller.communication.CtcLink to be used for
	// communicating with this controller.
	public CtcLink ctcLink()
	{
		return ctcLink;
	}

	// Sets up two-way communication between this controller and another
	// controller for the specified adjacent branches.
	public void linkToController(Branch thisBranch,
	                             TrackController other, Branch otherBranch)
	{
		ControllerLink otherLink = other.controllerLink(otherBranch);
		ControllerLink  thisLink =  this.controllerLink( thisBranch);

		 this.registerControllerLink( thisBranch, otherLink);
		other.registerControllerLink(otherBranch, thisLink);
	}

	// Returns the track_controller.communication.ControllerLink to be used
	// for communicating with this controller concerning the specified
	// branch.
	public ControllerLink controllerLink(Branch branch)
	{
		int index = branchToIndex(branch);

		return thisControllerLinks[index];
	}

	// Sets the TrackController link at the specified branch.
	//
	// Throws IllegalArgumentException if one has already been set.
	void registerControllerLink(Branch branch, ControllerLink link)
	{
		int index = branchToIndex(branch);

		if (otherControllerLinks[index] != null)
			throw new IllegalArgumentException("already set");

		otherControllerLinks[index] = link;
	}

	// Loads a program.
	//
	// If the load is successful, the program immediately takes effect.
	// Else if there are load errors, the program is discarded and no
	// changes are made.
	//
	// Returns true if load was successful.
	public boolean loadProgram(Program program)
	{
		int commonBranchLength;
		int   leftBranchLength;
		int  rightBranchLength;

		try
		{
			commonBranchLength =
				Program.parseRun(program.commonBranchLength);

			leftBranchLength =
				Program.parseRun(program.  leftBranchLength);

			rightBranchLength =
				Program.parseRun(program. rightBranchLength);
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}

		if (commonBranchLength == 0)
			return false;

		authorities = new Authority[] {
			new Authority(new boolean[commonBranchLength]),

			 leftBranchLength == 0 ? null :
				new Authority(new boolean[  leftBranchLength]),

			rightBranchLength == 0 ? null :
				new Authority(new boolean[ rightBranchLength])
		};

		speeds = new Speed[][] {
			new Speed[commonBranchLength],
			new Speed[  leftBranchLength],
			new Speed[ rightBranchLength]
		};

		for (int i = 0; i < 2; ++i)
			for (int j = 0; j < speeds[j].length; ++j)
				speeds[i][j] = new Speed(Speed.run(0));

		return true;
	}

	// Returns the effective Authority for the specified branch.
	public Authority authority(Branch branch)
	{
		int index = branchToIndex(branch);

		return authorities[index];
	}

	// Sets the authority at the specified branch.
	//
	// Throws IllegalArgumentException if specified branch is length zero,
	// or if authority is null, or if its size does not match the specified
	// branch length.
	public void authority(Branch branch, Authority authority)
		throws IllegalArgumentException
	{
		int index = branchToIndex(branch);

		if (authorities[index] == null)
			throw new IllegalArgumentException("zero branch");

		if (authority.blocks.length != authorities[index].blocks.length)
			throw new IllegalArgumentException("authority size");

		authorities[index] = new Authority(authority);
	}

	// Returns the effective Speeds for the specified branch.
	public Speed[] speeds(Branch branch)
	{
		int index = branchToIndex(branch);

		return speeds[index];
	}

	// Sets the speeds at all blocks in the specified branch.
	//
	// Throws IllegalArgumentException if specified branch is length zero,
	// or if speeds array is null, or if its length is not equal to the
	// specified branch length.
	public void speed(Branch branch, Speed[] speeds)
		throws IllegalArgumentException
	{
		int index = branchToIndex(branch);

		if (this.speeds[index].length == 0)
			throw new IllegalArgumentException("zero branch");

		if (speeds.length != this.speeds[index].length)
			throw new IllegalArgumentException("speeds size");

		Speed[] copy = new Speed[speeds.length];

		for (int i = 0; i < speeds.length; ++i)
			copy[i] = new Speed(speeds[i]);

		this.speeds[index] = copy;
	}

	// Updates this object.
	public void update(int time)
	{
		// TODO: unimplemented
		//
		// Check occupancy from track model.
		//
		// Send TrackMovementCommands to track blocks.
		//
		// Flip switches.
	}
}
