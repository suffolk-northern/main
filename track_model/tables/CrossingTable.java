package track_model.tables;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CrossingTable extends TrackModelTable {

    public CrossingTable() {
        super();
        setRenderer();
    }

    private void setRenderer() {
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, false, hasFocus, row, col);
                boolean occupied = ((String) table.getModel().getValueAt(row, 4)).equalsIgnoreCase("OCCUPIED");
                boolean maintain = ((String) table.getModel().getValueAt(row, 4)).equalsIgnoreCase("CLOSED");
                if (occupied) {
                    setBackground(Color.RED);
                    setForeground(Color.WHITE);
                } else if (maintain) {
                    setBackground(Color.GRAY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });
    }

}
