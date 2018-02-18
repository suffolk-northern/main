/*
 * Kaylene
 */
package track_model;

/**
 *
 * @author Fenne
 */
public class Track 
{
	public int numSections;
	public TrackSection sections[];
	
	public Track()
	{
		numSections = 1;
		int numBlocks = 8;
		TrackBlock blocks[] = new TrackBlock[numBlocks]; 
		GlobalCoordinates origin = new GlobalCoordinates(0, 0);
		blocks[0] = new TrackBlock(1, origin, origin.addYards(50, 0), 4);
		blocks[1] = new TrackBlock(2, origin.addYards(50, 0), origin.addYards(100, 0), 1);
		blocks[2] = new TrackBlock(3, origin.addYards(100, 0), origin.addYards(150, 0), 2);
		blocks[3] = new TrackBlock(4, origin.addYards(150, 0), origin.addYards(150, 50), 0);
		blocks[4] = new TrackBlock(5, origin.addYards(150, 50), origin.addYards(100, 50), 3);
		blocks[5] = new TrackBlock(6, origin.addYards(100, 50), origin.addYards(50, 50), 1);
		blocks[6] = new TrackBlock(7, origin.addYards(50, 50), origin.addYards(50, 0), 0);
		blocks[7] = new TrackBlock(8, origin.addYards(50, 50), origin.addYards(0, 50), 4);
		sections = new TrackSection[1];
		sections[0] = new TrackSection(numBlocks, blocks);
	}
	
	public Track(int newNum, TrackSection newSections[])
	{
		numSections = newNum;
		sections = newSections;
	}
}
