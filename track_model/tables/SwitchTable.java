package track_model.tables;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SwitchTable extends TrackModelTable {

	private final Color DARK_GREEN = new Color(0, 153, 0);

	public SwitchTable() {
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
					int straight = (table.getModel().getValueAt(row, 4)) instanceof Integer ? (int) table.getModel().getValueAt(row, 4) : 0;
					int current = (table.getModel().getValueAt(row, 6)) instanceof Integer ? (int) table.getModel().getValueAt(row, 6) : 0;

					if (straight == current && col == 4) {
						setBackground(DARK_GREEN);
						setForeground(Color.WHITE);
					} else if (straight != current && col == 4) {
						setBackground(Color.RED);
						setForeground(Color.WHITE);
					} else if (straight != current && col == 5) {
						setBackground(DARK_GREEN);
						setForeground(Color.WHITE);
					} else if (straight == current && col == 5) {
						setBackground(Color.RED);
						setForeground(Color.WHITE);
					} else {
						setBackground(Color.WHITE);
						setForeground(Color.BLACK);
					}
				} catch (Throwable t) {
//					System.out.println("SwitchTable error.");
				}
				return this;
			}
		}
		);
	}

}
