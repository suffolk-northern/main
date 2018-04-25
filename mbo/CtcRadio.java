/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import ctc.Ctc;

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
	
	// Returns a 2D array of integer block IDs, each row is a switch
	// 2 ints in each row, one for each block id
	public int[][] getSwitchStates(String lineName)
	{
		return ctc.requestSwitches(lineName);
	}
	
	public void enableMovingBlock()
	{
		mboCont.enableMboController(true);
		// TODO: add corresponding CTC function
		ctc.toMovingBlock(line);
	}
	
	public void disableMovingBlock()
	{
		mboCont.enableMboController(false);
		// TODO: add corresponding CTC function
		ctc.toFixedBlock(line);
	}
	
	public void enableAutomaticDispatch(boolean isEnabled)
	{
		// TODO: Add CTC function
		mboSched.enableDispatch(isEnabled);
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
}
