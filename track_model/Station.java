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
    protected String beacon;

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

    public String getBeacon() {
        return beacon;
    }

    protected void setBeacon(String beacon) {
        this.beacon = beacon;
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
                + "\tPassengers: " + passengers + "\n"
                + "\tBeacon Message: " + beacon;
    }
}
