/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import java.sql.Time;

import ctc.Ctc;
import java.util.Date;

/**
 *
 * @author Kaylene Stocking
 */
public class CtcRadio {
	private MboController mboCont;
	private MboScheduler mboSched;
	private Ctc ctc;
	private String schedule;
	private String line;
	
	public CtcRadio(String l, MboController mc, MboScheduler ms, Ctc c)
	{
		mboCont = mc;
		mboSched = ms;
		ctc = c;
		line = l;
	}
	
	public void tellCtcDispatch(String line, int tid)
	{
		ctc.mboDispatch(line,tid);
	}
	
	// Returns a 2D array of integer block IDs, each row is a switch
	// 2 ints in each row, one for each block id
	public int[][] getSwitchStates(String lineName)
	{
		return ctc.requestSwitches(lineName);
	}
	
	public void enableMovingBlock()
	{
		mboCont.enableMboController(true);
		mboSched.enableMboController(true);
		ctc.toMovingBlock(line);
	}
	
	public void disableMovingBlock()
	{
		mboCont.enableMboController(false);
		mboSched.enableMboController(false);
		ctc.toFixedBlock(line);
	}
	
	public void enableAutomaticDispatch(boolean isEnabled)
	{
		mboSched.enableDispatch(isEnabled);
		if(isEnabled)
			ctc.autoMode(line);
		else
			ctc.manMode(line);
	}
	
	public void showController()
	{
		mboCont.launchUI();
	}
	
	public void hideController()
	{
		mboCont.hideUI();
	}
	
	public void showScheduler()
	{
		mboSched.launchUI();
	}
	
	public void hideScheduler()
	{
		mboSched.hideUI();
	}
	
	public void setSchedule(String schedule)
	{
		this.schedule = schedule;
		ctc.strToSched(schedule);
	}
	
	public String getSchedule()
	{
		return schedule;
	}
	
	public Time getTime()
	{
		// TODO: CTC method to get the current time
		Date date = ctc.getCurrentTime();
		return null;
	}
}
