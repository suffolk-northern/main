package track_model;

public class Crossing {

    private String line;
    private char section;
    private int block;
    private boolean signal;

    public Crossing(String line, int block) {
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

    public boolean isSignal() {
        return signal;
    }

    protected void setSignal(boolean signal) {
        this.signal = signal;
    }

}
