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
	private int nextBlockID; // -1 if there is no next block
	private int prevBlockID; // -1 if there is no previous block
	private double length;
	private int speedLimit;
	private char section;
	private String station;
	
	public BlockTracker(int id, int nextId, int prevId, double len, int limit, char sec, String sta)
	{
		ID = id;
		nextBlockID = nextId;
		prevBlockID = prevId;
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
	
	public void setNext(int nextId)
	{
		nextBlockID = nextId;
	}

	public void setPrev(int prevId)
	{
		prevBlockID = prevId;
	}
	
}
