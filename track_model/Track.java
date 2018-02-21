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
		blocks[0].setNextBlock(blocks[1]);
		blocks[7].setPrevBlock(blocks[6]);
		for (int i = 1; i < numBlocks-1; i++)
		{
			blocks[i].setNextBlock(blocks[i + 1]);
			blocks[i].setPrevBlock(blocks[i - 1]);
		}
		int numSwitches = 2;
		TrackSwitch switches[] = new TrackSwitch[numSwitches];
		TrackBlock switch1[][] = 
		{
			{blocks[0], blocks[1]},
			{blocks[6], blocks[1]}
		};
		switches[0] = new TrackSwitch(switch1, 0);
		TrackBlock switch2[][] = 
		{
			{blocks[5], blocks[6]},
			{blocks[5], blocks[7]}
		};
		switches[1] = new TrackSwitch(switch2, 0);
		sections = new TrackSection[1];
		sections[0] = new TrackSection(numBlocks, blocks, switches);
	}
	
	public Track(int newNum, TrackSection newSections[])
	{
		numSections = newNum;
		sections = newSections;
	}
}
