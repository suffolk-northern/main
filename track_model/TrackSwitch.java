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
		possibleStates[state][0].setNextBlock(possibleStates[state][1]);
		possibleStates[state][1].setPrevBlock(possibleStates[state][0]);
	}
	
	public void flipSwitch()
	{
		possibleStates[state][0].setNextBlock(possibleStates[state][1]);
		possibleStates[state][1].setPrevBlock(possibleStates[state][0]);
		if (state == 0)
			state = 1;
		if (state == 1)
			state = 0;
	}
	
	public TrackBlock[] getState()
	{
		return possibleStates[state];
	}
}
