/*
 * Roger Xue
 *
 * Railway crossing object.
 */
package track_model;

public class Crossing {

	// Track line
	protected String line;
	// Track block
	protected int block;
	// Status of railway crossing signal
	protected boolean signal;

	/**
	 * Initializes Crossing object.
	 *
	 * @param line
	 * @param block
	 */
	public Crossing(String line, int block) {
		this.line = line;
		this.block = block;
	}

	/**
	 * Gets line.
	 *
	 * @return line
	 */
	public String getLine() {
		return line;
	}

	/**
	 * Sets line.
	 *
	 * @param line
	 */
	protected void setLine(String line) {
		this.line = line;
	}

	/**
	 * Gets block
	 *
	 * @return block
	 */
	public int getBlock() {
		return block;
	}

	/**
	 * Sets block.
	 *
	 * @param block
	 */
	protected void setBlock(int block) {
		this.block = block;
	}

	/**
	 * Checks if signal is on or off.
	 *
	 * @return signal
	 */
	public boolean isSignal() {
		return signal;
	}

	/**
	 * Sets status of signal.
	 *
	 * @param signal
	 */
	protected void setSignal(boolean signal) {
		this.signal = signal;
	}

}
