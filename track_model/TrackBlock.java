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
public class TrackBlock 
{
	public int ID;
	public GlobalCoordinates startPoint;
	public GlobalCoordinates endPoint;
	public int hasStructure;
	double length;
	TrackBlock nextBlock;
	TrackBlock prevBlock;
	
	public TrackBlock(int newID, GlobalCoordinates start, 
		GlobalCoordinates end, int structure)
	{
		startPoint = start;
		endPoint = end;
		// 0 = nothing, 1 = switch, 2 = station, 3 = crossing, 4 = yard
		hasStructure = structure;
		ID = newID;
		length = startPoint.distanceTo(endPoint);
	}
	
	public TrackBlock getNextBlock()
	{
		return nextBlock;
	}
	
	public double getLength()
	{
		return length;
	}
		
	public void setNextBlock(TrackBlock block)
	{
		nextBlock = block;
	}
	
	public TrackBlock getPrevBlock()
	{
		return prevBlock;
	}
	
	public void setPrevBlock(TrackBlock block)
	{
		prevBlock = block;
	}
}
