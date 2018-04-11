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
	private boolean prevOpen;
	private boolean nextOpen;
	private double length;
	private int speedLimit;
	private char section;
	private String station;
	
	public BlockTracker(int id, int nextId, int prevId, double len, int limit, char sec, String sta)
	{
		ID = id;
		nextBlockID = nextId;
		prevBlockID = prevId;
		prevOpen = true;
		nextOpen = true;
		length = len;
		speedLimit = limit;
		section = sec;
		station = sta;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getNext()
	{	
		if (nextOpen)
			return nextBlockID;
		else
			return -1;
	}
	
	public int getPrev()
	{
		if (prevOpen)
			return prevBlockID;
		else
			return -1;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public int getSpeedLimit()
	{
		return speedLimit;
	}
	
	public char getSection()
	{
		return section;
	}
	
	public String getStation()
	{
		return station;
	}
	
	public void setNextBlockOpen(boolean isOpen)
	{
		nextOpen = isOpen;
	}

	public void setPrevBlockOpen(boolean isOpen)
	{
		prevOpen = isOpen;
	}
	
}
