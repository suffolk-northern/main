/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

/**
 *
 * @author Fenne
 */

import updater.Updateable;

public class MboScheduler implements Updateable
{
	private MboSchedulerUI ui;
	private boolean launchedUI;
	
	public MboScheduler()
	{
		launchedUI = false;
	}
	
	public void launchUI()
	{
		if (!launchedUI)
		{
			ui = new MboSchedulerUI();
			launchedUI = true;
		}
	}
	
	public void showUI()
	{
		if (launchedUI)
			ui.setVisible(true);
	}
	
	public void hideUI()
	{
		if (launchedUI)
			ui.setVisible(false);
	}
	
	public void update(int time)
	{
		
	}
}
