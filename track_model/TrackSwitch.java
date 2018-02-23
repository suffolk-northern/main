/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

/**
 *
 * @author Fenne
 */
public class TrackSwitch 
{
	TrackBlock possibleStates[][];
	int state;
	
	public TrackSwitch(TrackBlock[][] newPossibleStates, int initState)
	{
		possibleStates = newPossibleStates;
		state = initState;
		int otherState;
		if (initState == 0)
			otherState = 1;
		else
			otherState = 0;
		possibleStates[otherState][0].setNextBlock(null);
		possibleStates[otherState][1].setPrevBlock(null);
		possibleStates[state][0].setNextBlock(possibleStates[state][1]);
		possibleStates[state][1].setPrevBlock(possibleStates[state][0]);

	}
	
	public void flipSwitch()
	{
		int oldState = state;
		possibleStates[oldState][0].setNextBlock(null);
		possibleStates[oldState][1].setPrevBlock(null);
		if (oldState == 0)
			state = 1;
		else
			state = 0;
		possibleStates[state][0].setNextBlock(possibleStates[state][1]);
		possibleStates[state][1].setPrevBlock(possibleStates[state][0]);
	}
	
	public TrackBlock[] getState()
	{
		return possibleStates[state];
	}
}
