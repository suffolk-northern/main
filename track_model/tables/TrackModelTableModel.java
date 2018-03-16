package track_model.tables;

import javax.swing.table.DefaultTableModel;

public class TrackModelTableModel extends DefaultTableModel {

    private static final Object[] BLOCK_COLUMNS = {"Line", "Section", "Block #", "Length (yd)", "Curvature", "Grade", "Speed Limit (mph)", "Underground", "Power", "Occupied", "Heater", "Message"};
    private static final Object[] SWITCH_COLUMNS = {"Line", "Section In", "Block In", "Prev Block", "Prev Direction", "Next Block", "Next Direction", "Switch Block", "Switch Direction", "Current Setting"};
    private static final Object[] CROSSING_COLUMNS = {"Line", "Section", "Block #", "Length", "Occupied", "Signal"};
    private static final Object[] STATION_COLUMNS = {"Line", "Section", "Block #", "Name", "Passengers Embarking", "Beacon Message"};

    public TrackModelTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public static TrackModelTableModel getBlockTableModel() {
        return new TrackModelTableModel(BLOCK_COLUMNS, 0);
    }

    public static TrackModelTableModel getSwitchTableModel() {
        return new TrackModelTableModel(SWITCH_COLUMNS, 0);
    }

    public static TrackModelTableModel getCrossingTableModel() {
        return new TrackModelTableModel(CROSSING_COLUMNS, 0);
    }

    public static TrackModelTableModel getStationTableModel() {
        return new TrackModelTableModel(STATION_COLUMNS, 0);
    }

}
