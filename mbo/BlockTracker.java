/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

/**
 *
 * @author Kaylene Stocking
 */
public class BlockTracker {
	private int ID;
	private int nextBlockID;
	private int prevBlockID;
	private double length;
	private int speedLimit;
	
	public BlockTracker(int id, int nextId, int prevId, double len, int limit)
	{
		ID = id;
		nextBlockID = nextId;
		prevBlockID = prevId;
		length = len;
		speedLimit = limit;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getNext()
	{
		return nextBlockID;
	}
	
	public int getPrev()
	{
		return prevBlockID;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public int getSpeedLimit()
	{
		return speedLimit;
	}
	
	public void setNext(int nextId)
	{
		nextBlockID = nextId;
	}

	public void setPrev(int prevId)
	{
		prevBlockID = prevId;
	}
}
