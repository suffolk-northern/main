/*
 * Roger Xue
 *
 * Railway station object.
 */
package track_model;

public class Station {

    // Track line
    protected String line;
    // Track section
    protected char section;
    // Track block
    protected int block;
    // Station name
    protected String name;
    // Passengers at the station
    protected int passengers;
    // Beacon message
    protected Beacon beaconPrev;
    protected Beacon beaconNext;
    // Location
    protected GlobalCoordinates location;

    /**
     * Initializes Station object.
     *
     * @param line
     * @param block
     */
    public Station(String line, int block) {
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

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public GlobalCoordinates getLocation() {
        return location;
    }

    protected void setLocation(GlobalCoordinates location) {
        this.location = location;
    }

    public Beacon getBeaconPrev() {
        return beaconPrev;
    }

    protected void setBeaconPrev(Beacon beaconPrev) {
        this.beaconPrev = beaconPrev;
    }

    public Beacon getBeaconNext() {
        return beaconNext;
    }

    protected void setBeaconNext(Beacon beaconNext) {
        this.beaconNext = beaconNext;
    }

    /**
     * Generates passengers at the station.
     *
     * @return passengers
     */
    public static int generatePassengers() {
        return (int) (Math.random() * 20);
    }

    @Override
    public String toString() {
        return "Hi, I'm Station " + name + " of the " + line + " Line, Section " + section + ".\n"
                + "\tBlock: " + block + "\n"
                + "\tPassengers: " + passengers;
    }
}
