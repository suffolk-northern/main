package track_model.tables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StationTable extends TrackModelTable {

	public StationTable() {
		super();
		setRenderer();
	}

	private void setRenderer() {
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int col) {
				try {
					super.getTableCellRendererComponent(table, value, false, hasFocus, row, col);
				} catch (Throwable t) {
//					System.out.println("StationTable error.");
				}
				return this;
			}
		});

	}
}
