/*
 * Roger Xue
 *
 * Track Block object.
 */
package track_model;

public class TrackBlock {

	// Track line.
	protected String line;
	// Track section.
	protected char section;
	// Track block id.
	protected int block;
	// Length of block.
	protected double length;
	// Curvature of block. Negative is counterclockwise. Positive is clockwise.
	protected double curvature;
	// Grade of track.
	protected double grade;
	// Speed limit in m/s.
	protected int speedLimit;
	// Is underground.
	protected boolean isUnderground = false;
	// Is the power on.
	protected boolean isPowerOn = true;
	// Is it currently occupied.
	protected boolean isOccupied = false;
	// Is it closed for maintenance.
	protected boolean closedForMaintenance = false;
	// Last message sent. Just for show.
	protected String message = "";
	// Geographic coordinates
	protected GlobalCoordinates start, end;

	// Coordinates in meter. Used for math.
	protected double xStart, xEnd, yStart, yEnd;
	// Center of track with curvature.
	protected double xCenter, yCenter;

	// Id of next block.
	protected int nextBlockId;
	// Can train travel to next block?
	protected int nextBlockDir;
	// Id of previous block.
	protected int prevBlockId;
	// Can train travel to previous block?
	protected int prevBlockDir;

	// Id of switch block.
	protected int switchBlockId;
	// How is switch oriented?
	// Positive number is forward switch.
	// Negative number if backward switch.
	// 1 means switch can be accessed from main line.
	// 2 means switch can not be accessed from main line.
	protected int switchDirection;
	// Current id of switch block position
	protected int switchPosition;

	// Is block a switch?
	protected boolean isSwitch = false;
	// Does block have a station?
	protected boolean isStation = false;
	// Does block have a crossing?
	protected boolean isCrossing = false;

	public static final double METER_TO_YARD_MULTIPLIER = 1.09361;
	public static final double KILOMETER_TO_MILE_MULTIPLIER = 0.621371;

	/**
	 * Initializes a Track Block object.
	 *
	 * @param line
	 * @param block
	 */
	public TrackBlock(String line, int block) {
		this.line = line;
		this.block = block;
	}

	public String getLine() {
		return line;
	}

	protected void setLine(String line) {
		this.line = line;
	}

	public char getSection() {
		return section;
	}

	protected void setSection(char section) {
		this.section = section;
	}

	public int getBlock() {
		return block;
	}

	protected void setBlock(int block) {
		this.block = block;
	}

	public double getLength() {
		return length;
	}

	public double getLengthInYards() {
		return length * METER_TO_YARD_MULTIPLIER;
	}

	protected void setLength(double length) {
		this.length = length;
	}

	public double getCurvature() {
		return curvature;
	}

	protected void setCurvature(double curvature) {
		this.curvature = curvature;
	}

	public double getGrade() {
		return grade;
	}

	protected void setGrade(double grade) {
		this.grade = grade;
	}

	public int getSpeedLimit() {
		return speedLimit;
	}

	public double getSpeedLimitInMph() {
		return speedLimit * KILOMETER_TO_MILE_MULTIPLIER;
	}

	protected void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}

	public boolean isIsUnderground() {
		return isUnderground;
	}

	protected void setIsUnderground(boolean isUnderground) {
		this.isUnderground = isUnderground;
	}

	public boolean isIsPowerOn() {
		return isPowerOn;
	}

	protected void setIsPowerOn(boolean isPowerOn) {
		this.isPowerOn = isPowerOn;
	}

	public boolean isIsSwitch() {
		return isSwitch;
	}

	protected void setIsSwitch(boolean isSwitch) {
		this.isSwitch = isSwitch;
	}

	public boolean isIsStation() {
		return isStation;
	}

	protected void setIsStation(boolean isStation) {
		this.isStation = isStation;
	}

	public boolean isIsCrossing() {
		return isCrossing;
	}

	protected void setIsCrossing(boolean isCrossing) {
		this.isCrossing = isCrossing;
	}

	public boolean isIsOccupied() {
		return isOccupied;
	}

	protected void setIsOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected void setStartCoordinates(double x, double y) {
		xStart = x;
		yStart = y;
		start = GlobalCoordinates.ORIGIN.addYards(y * METER_TO_YARD_MULTIPLIER, x * METER_TO_YARD_MULTIPLIER);
	}

	protected void setEndCoordinates(double x, double y) {
		xEnd = x;
		yEnd = y;
		end = GlobalCoordinates.ORIGIN.addYards(y * METER_TO_YARD_MULTIPLIER, x * METER_TO_YARD_MULTIPLIER);
	}

	protected void setCenterCoordinates(double x, double y) {
		xCenter = x;
		yCenter = y;
	}

	public GlobalCoordinates getStart() {
		return start;
	}

	public GlobalCoordinates getEnd() {
		return end;
	}

	public int getNextBlockId() {
		return nextBlockId;
	}

	protected void setNextBlockId(int nextBlockId) {
		this.nextBlockId = nextBlockId;
	}

	public int getPrevBlockId() {
		return prevBlockId;
	}

	protected void setPrevBlockId(int prevBlockId) {
		this.prevBlockId = prevBlockId;
	}

	public int getNextBlockDir() {
		return nextBlockDir;
	}

	protected void setNextBlockDir(int nextBlockDir) {
		this.nextBlockDir = nextBlockDir;
	}

	public int getPrevBlockDir() {
		return prevBlockDir;
	}

	protected void setPrevBlockDir(int prevBlockDir) {
		this.prevBlockDir = prevBlockDir;
	}

	public int getSwitchBlockId() {
		return switchBlockId;
	}

	protected void setSwitchBlockId(int switchBlockId) {
		this.switchBlockId = switchBlockId;
	}

	public int getSwitchDirection() {
		return switchDirection;
	}

	protected void setSwitchDirection(int switchDirection) {
		this.switchDirection = switchDirection;
	}

	public int getSwitchPosition() {
		return switchPosition;
	}

	protected void setSwitchPosition(int switchPosition) {
		this.switchPosition = switchPosition;
	}

	public boolean isClosedForMaintenance() {
		return closedForMaintenance;
	}

	protected void setClosedForMaintenance(boolean closedForMaintenance) {
		this.closedForMaintenance = closedForMaintenance;
	}

	protected double getRadius() {
		return length * 360 / (Math.abs(curvature) * 2 * Math.PI);
	}

	@Override
	public String toString() {
		return "Hi, I'm Block " + block + " of the " + line + " Line, Section " + section + ".\n"
				+ "\tLength: " + length + "\n"
				+ "\tCurvature: " + curvature + "\n"
				+ "\tGrade: " + grade + "\n"
				+ "\tUnderground: " + isUnderground + "\n"
				+ "\tPower: " + isPowerOn + "\n"
				+ "\tOccupied: " + isOccupied + "\n"
				+ "\tMessage: " + message;
	}
}
