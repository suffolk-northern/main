package mbo;

/**
 *
 * @author Kaylene Stocking
 */
public class SwitchTracker {
	private int blockID;
	private int[] blocks;
	private boolean[] connected;
	
	public SwitchTracker(int blockID, int[] blocks)
	{
		this.blockID = blockID;
		this.blocks = blocks;
		boolean[] connected = {false, false, false};
		this.connected = connected;
	}
	
	public int getID()
	{
		return blockID;
	}
	
	public int[] getBlocks()
	{
		return blocks;
	}
	
	public boolean isConnected(int block)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i] == block)
				return connected[i];
		}
		return false;
	}
	
	public void setConnected(int block, boolean isConnected)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i] == block)
				connected[i] = isConnected;
		}
	}
}
