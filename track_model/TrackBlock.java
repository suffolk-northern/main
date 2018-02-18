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
public class TrackBlock {
	public int ID;
	public GlobalCoordinates startPoint;
	public GlobalCoordinates endPoint;
	public int hasStructure;
	
	public TrackBlock(int newID, GlobalCoordinates start, GlobalCoordinates end, int structure)
	{
		startPoint = start;
		endPoint = end;
		// 0 = nothing, 1 = switch, 2 = station, 3 = crossing
		hasStructure = structure;
		ID = newID;
	}
		
}
