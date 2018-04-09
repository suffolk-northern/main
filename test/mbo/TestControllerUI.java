package test.mbo;

import java.lang.Thread;
import mbo.MboControllerUI;

/**
 *
 * @author Kaylene Stocking
 */
public class TestControllerUI 
{
	public static void main(String[] args) throws InterruptedException
	{
		MboControllerUI ui = new MboControllerUI("Yellow"); 
		ui.addTrain(1, 'A', 0, 0, 0, 0);
		Thread.sleep(2000);
		ui.updateTrain(1, 'B', 0, 0, 0, 5);
		Thread.sleep(2000);
		ui.addTrain(3, 'C', 0, 0, 0, 0);
		Thread.sleep(2000);
		ui.removeTrain(1);
		Thread.sleep(2000);
		ui.addTrain(4, 'D', 0, 0, 0, 0);
	}
}
