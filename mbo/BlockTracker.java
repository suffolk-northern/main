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
	private char section;
	private String station;
	private boolean canGoForward;
	private boolean canGoBackward;
	
	public BlockTracker(int id, int nextId, int prevId, double len, int limit, char sec, String sta, boolean forward, boolean backward)
	{
		ID = id;
		nextBlockID = nextId;
		prevBlockID = prevId;
		length = len;
		speedLimit = limit;
		section = sec;
		station = sta;
		canGoForward = forward;
		canGoBackward = backward;
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
	
	public char getSection()
	{
		return section;
	}
	
	public String getStation()
	{
		return station;
	}
	
	public boolean canGoForward()
	{
		return canGoForward;
	}
	
	public boolean canGoBackward()
	{
		return canGoBackward;
	}
	
	public void setNextBlock(int nextBlock)
	{
		nextBlockID = nextBlock;
	}

	public void setPrevBlock(int prevBlock)
	{
		prevBlockID = prevBlock;
	}
	
}
