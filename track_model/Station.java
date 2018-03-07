package track_model;

public class Station {

    private String line;
    private char section;
    private int block;
    private String name;
    private String beacon;

    public Station(String line, int block) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeacon() {
        return beacon;
    }

    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }

    @Override
    public String toString() {
        return "Hi, I'm Station " + name + " of the " + line + " Line, Section " + section + ".\n"
                + "\tBlock: " + block + "\n"
                + "\tBeacon Message: " + beacon;
    }
}
