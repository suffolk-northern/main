package track_model;

public class TrackBlock {

    protected String line;
    protected char section;
    protected int block;
    protected double length;
    protected double curvature;
    protected double grade;
    protected int direction;
    protected int speedLimit;
    protected boolean isUnderground;
    protected boolean isPowerOn;
    protected boolean isOccupied;
    protected boolean isHeaterOn;
    protected boolean closedForMaintenance;
    protected String message;
    protected GlobalCoordinates start, end;

    private double xStart, xEnd, yStart, yEnd;
    private double xCenter, yCenter;

    protected int nextBlockId;
    protected int prevBlockId;

    protected int switchBlockId;
    protected int switchDirection;
    protected int switchPosition;

    protected boolean isSwitch;
    protected boolean isStation;
    protected boolean isCrossing;

    public static final double METER_TO_YARD_MULTIPLIER = 1.09361;
    public static final double KILOMETER_TO_MILE_MULTIPLIER = 0.621371;

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

    public int getDirection() {
        return direction;
    }

    protected void setDirection(int direction) {
        this.direction = direction;
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

    public boolean isIsHeaterOn() {
        return isHeaterOn;
    }

    protected void setIsHeaterOn(boolean isHeaterOn) {
        this.isHeaterOn = isHeaterOn;
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

    public double getDistanceTo(GlobalCoordinates gc) {
        double minDist = 9999;
        double latDiff, lonDiff;
        for (int i = 0; i < length; i += 5) {
            latDiff = gc.latitude() - getPositionAlongBlock(i).latitude();
            lonDiff = gc.longitude() - getPositionAlongBlock(i).longitude();
            double tempDist = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
            if (tempDist < minDist) {
                minDist = tempDist;
            }
        }
        return minDist;
    }

    public GlobalCoordinates getPositionAlongBlock(double meters) {
        if (meters > length) {
            return null;
        }
        double newX, newY;

        if (curvature == 0) {
            double xDiff = xEnd - xStart;
            double yDiff = yEnd - yStart;
            double xDist = xDiff * meters / length;
            double yDist = yDiff * meters / length;

            newX = xStart + xDist;
            newY = yStart + yDist;
        } else {
            boolean clockwise = curvature > 0;
            double radius = Math.sqrt(Math.pow(xStart - xCenter, 2) + Math.pow(yStart - yCenter, 2));
            double angle = Math.atan2(yStart - yCenter, xStart - xCenter);
            angle = clockwise ? angle - meters / radius : angle + meters / radius;

            newX = xCenter + radius * Math.cos(angle);
            newY = yCenter + radius * Math.sin(angle);
        }

        return GlobalCoordinates.ORIGIN.addYards(newY * METER_TO_YARD_MULTIPLIER, newX * METER_TO_YARD_MULTIPLIER);
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
