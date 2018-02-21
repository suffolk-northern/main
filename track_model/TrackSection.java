/*
 * Kaylene
 */
package track_model;

/**
 *
 * @author Fenne
 */
public class TrackSection {
	public int numBlocks;
	public TrackBlock blocks[];
	public TrackSwitch switches[];
	
	public TrackSection(int newNum, TrackBlock newBlocks[], TrackSwitch newSwitches[])
	{
		numBlocks = newNum;
		blocks = newBlocks;
		switches = newSwitches;
	}
}
