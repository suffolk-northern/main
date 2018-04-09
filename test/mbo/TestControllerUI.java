package test.mbo;

import mbo.MboControllerUI;

/**
 *
 * @author Kaylene Stocking
 */
public class TestControllerUI 
{
	public static void main(String[] args)
	{
		MboControllerUI ui = new MboControllerUI("Yellow"); 
		ui.addTrain(1, 'A', 0, 0, 0, 0);
	}
}
