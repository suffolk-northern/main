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
	
	public CtcRadio(MboController mc, MboScheduler ms, Ctc c)
	{
		mboCont = mc;
		mboSched = ms;
		ctc = c;
	}
	
	// Returns a 2D array of integer block IDs, each row is a switch
	// 2 ints in each row, one for each block id
	public int[][] getSwitchStates(String lineName)
	{
		// TODO: make this method public in CTC
		// return ctc.requestSwitches(lineName);
		return null;
	}
	
	public void enableMovingBlock()
	{
		mboCont.enableMboController();
	}
	
	public void disableMovingBlock()
	{
		mboCont.disableMboController();
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
}
