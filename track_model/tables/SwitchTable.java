package track_model.tables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SwitchTable extends TrackModelTable {

    public SwitchTable() {
        super();
        setRenderer();
    }

    private void setRenderer() {
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, false, hasFocus, row, col);
                return this;
            }
        });
    }

}
