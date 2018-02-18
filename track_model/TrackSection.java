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
	
	public TrackSection(int newNum, TrackBlock newBlocks[])
	{
		numBlocks = newNum;
		blocks = newBlocks;
	}
}
