package track_model;

public class TrackBlock {

    private String line;
    private char section;
    private int block;
    private double length;
    private double curvature;
    private double grade;
    private int direction;
    private int speedLimit;
    private boolean isUnderground;
    private boolean isPowerOn;
    private boolean isOccupied;
    private boolean isHeaterOn;
    private String message;
    private GlobalCoordinates start, end;

    private boolean isSwitch;
    private boolean isStation;
    private boolean isCrossing;

    public TrackBlock nextBlock;
    public TrackBlock prevBlock;

    private static final double YARD_MULTIPLIER = 1.09361;

    public TrackBlock(String line, int block) {
        this.line = line;
        this.block = block;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public char getSection() {
        return section;
    }

    public void setSection(char section) {
        this.section = section;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getCurvature() {
        return curvature;
    }

    public void setCurvature(double curvature) {
        this.curvature = curvature;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public boolean isIsUnderground() {
        return isUnderground;
    }

    public void setIsUnderground(boolean isUnderground) {
        this.isUnderground = isUnderground;
    }

    public boolean isIsPowerOn() {
        return isPowerOn;
    }

    public void setIsPowerOn(boolean isPowerOn) {
        this.isPowerOn = isPowerOn;
    }

    public boolean isIsHeaterOn() {
        return isHeaterOn;
    }

    public void setIsHeaterOn(boolean isHeaterOn) {
        this.isHeaterOn = isHeaterOn;
    }

    public boolean isIsSwitch() {
        return isSwitch;
    }

    public void setIsSwitch(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }

    public boolean isIsStation() {
        return isStation;
    }

    public void setIsStation(boolean isStation) {
        this.isStation = isStation;
    }

    public boolean isIsCrossing() {
        return isCrossing;
    }

    public void setIsCrossing(boolean isCrossing) {
        this.isCrossing = isCrossing;
    }

    public TrackBlock getNextBlock() {
        return nextBlock;
    }

    public void setNextBlock(TrackBlock nextBlock) {
        this.nextBlock = nextBlock;
    }

    public TrackBlock getPrevBlock() {
        return prevBlock;
    }

    public void setPrevBlock(TrackBlock prevBlock) {
        this.prevBlock = prevBlock;
    }

    public boolean isIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStartCoordinates(double x, double y) {
        start = GlobalCoordinates.ORIGIN.addYards(y * YARD_MULTIPLIER, x * YARD_MULTIPLIER);
        System.out.println(start.latitude() + " " + start.longitude());
    }

    public void setEndCoordinates(double x, double y) {
        end = GlobalCoordinates.ORIGIN.addYards(y * YARD_MULTIPLIER, x * YARD_MULTIPLIER);
                System.out.println(end.latitude() + " " + end.longitude());

    }

    public GlobalCoordinates getStart() {
        return start;
    }

    public GlobalCoordinates getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Hi, I'm Block " + block + " of the " + line + " Line, Section " + section + ".\n"
                + "\tLength: " + length + "\n"
                + "\tCurvature: " + curvature + "\n"
                + "\tGrade: " + grade + "\n"
                + "\tDirection: " + direction + "\n"
                + "\tUnderground: " + isUnderground + "\n"
                + "\tPower: " + isPowerOn + "\n"
                + "\tOccupied: " + isOccupied + "\n"
                + "\tHeater: " + isHeaterOn + "\n"
                + "\tMessage: " + message;
    }
}
