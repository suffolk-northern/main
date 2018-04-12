package mbo;

/**
 *
 * @author Kaylene Stocking
 */
public class SwitchTracker {
	private int blockID;
	private int[] blocks;
	private boolean[] connected;
	private boolean[] switchIsNext;
	
	public SwitchTracker(int blockID, int[] blocks, boolean[] switchIsNext)
	{
		this.blockID = blockID;
		this.blocks = blocks;
		boolean[] connected = {false, false, false};
		this.connected = connected;
		this.switchIsNext = switchIsNext;
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
	
	public boolean nextIsSwitch(int block)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i] == block)
				return switchIsNext[i];
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
