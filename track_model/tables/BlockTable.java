package track_model.tables;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BlockTable extends TrackModelTable {

	public BlockTable() {
		super();
		setRenderer();
	}

	private void setRenderer() {
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int col) {
				try {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
					boolean occupied = ((String) table.getModel().getValueAt(row, 9)).equalsIgnoreCase("OCCUPIED");
					boolean maintain = ((String) table.getModel().getValueAt(row, 9)).equalsIgnoreCase("CLOSED");
					boolean powerOut = ((String) table.getModel().getValueAt(row, 8)).equalsIgnoreCase("OUTAGE");
					boolean underground = ((String) table.getModel().getValueAt(row, 7)).equalsIgnoreCase("UNDERGROUND");

					if (maintain) {
						setBackground(Color.GRAY);
						setForeground(Color.WHITE);
					} else if (powerOut) {
						setBackground(Color.ORANGE);
						setForeground(Color.BLACK);
					} else if (occupied) {
						setBackground(Color.RED);
						setForeground(Color.WHITE);
					} else {
						setBackground(table.getBackground());
						setForeground(table.getForeground());
					}

					if (underground && col == 7) {
						setBackground(Color.BLACK);
						setForeground(Color.WHITE);
					}

					if (col >= 3 && col <= 6) {
						DecimalFormat dFormat = new DecimalFormat("#0.00");
						String s = dFormat.format((double) value);
						super.getTableCellRendererComponent(table, s, false, hasFocus, row, col);
					}
				} catch (Throwable t) {
//					System.out.println("BlockTable error.");
				}
				return this;
			}
		});
	}

}
