/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

/**
 *
 * @author Kaylene Stocking
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 *
 * @author Fenne
 */
public class MboControllerUI extends JFrame
{
	private JFrame frame;
	private JPanel mainPanel;
	private JScrollPane trainPanel;
	private JTable trainTable;
	private JLabel titleLabel;
	
	private int numTrains;
	private String lineName;
	ArrayList<Integer> trainIDs;
	private String[] tableHeader;
	private Object[][] tableContents;
	
	public MboControllerUI(String ln)
	{
		lineName = ln;
		trainIDs = new ArrayList<>();
		
		frame = new JFrame("MBOController");
		frame.setLayout(null);
		frame.setTitle("MBO Controller");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// frame.setSize(1500, 1500);
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new GridBagLayout());
		trainPanel = new JScrollPane();
		trainTable = new JTable();
		titleLabel = new JLabel();
		
		GridBagConstraints c = new GridBagConstraints();
		
		titleLabel.setFont(new Font("Tahoma", 0, 18));
		titleLabel.setText(String.format("%s Line", lineName));
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(titleLabel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(trainPanel, c);
		trainPanel.setViewportView(trainTable);
		
		tableHeader = new String[] {"Train ID", "Section", "Block", "Location (yards)", "Authority (yards)", "Suggested speed (mph)"};
		// TODO: extend to extra rows once we start routing lots of trains
		tableContents = new Object[][]
		{
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
		};
		trainTable.setModel(new javax.swing.table.DefaultTableModel(tableContents, tableHeader)
		{
		   public boolean isCellEditable(int rowIndex, int columnIndex) 
		   {
			   return false;
		   }
		}
		);

		int width = 600;
		trainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		trainTable.getColumnModel().getColumn(0).setPreferredWidth(width / 10);
		trainTable.getColumnModel().getColumn(1).setPreferredWidth(width / 10);
		trainTable.getColumnModel().getColumn(2).setPreferredWidth(width / 10);
		trainTable.getColumnModel().getColumn(3).setPreferredWidth(width / 5);
		trainTable.getColumnModel().getColumn(4).setPreferredWidth(width / 5);
		trainTable.getColumnModel().getColumn(5).setPreferredWidth(3*width / 10);
		
		trainPanel.setPreferredSize(new Dimension(width, 200));
        frame.pack();
        frame.setVisible(true);
		
		numTrains = 0;
	}
	
	public void addTrain(int trainID, char section, int block, int location, int authority, int speed)
	{
		for (int tID : trainIDs)
		{
			if (tID == trainID)
				return;
		}
		trainIDs.add(trainID);
		int row = trainIDs.indexOf(trainID);
		// Convert meters to yards
		int customAuthority = (int) ((double) authority * 1.0936);
		// Convert kph to mph
		int customSpeed = (int) ((double) speed * 0.621371);
		// System.out.printf("Adding train %d to row %d%n", trainID, row);
		updateRow(row, trainID, section, block, location, customAuthority, customSpeed);
		numTrains += 1;
	}
	
	public void updateTrain(int trainID, char section, int block, int location, int authority, int speed)
	{
		int row = trainIDs.indexOf(trainID);
		// Convert meters to yards
		int customAuthority = (int) ((double) authority * 1.0936);
		// Convert kph to mph
		int customSpeed = (int) ((double) speed * 0.621371);
		// System.out.printf("Updatating train %d in row %d%n", trainID, row);
		if (row == -1)
		{
			addTrain(trainID, section, block, location, customAuthority, customSpeed);
			row = trainIDs.indexOf(trainID);
		}
		
		updateRow(row, trainID, section, block, location, customAuthority, customSpeed);
	}
	
	public void removeTrain(int trainID)
	{
		int row = trainIDs.indexOf(trainID);
		// System.out.printf("Removing train %d from row %d%n", trainID, row);
		clearRow(row);
		trainIDs.remove(row);
	}
	
	private void updateRow(int row, int trainID, char section, int block, int location, int authority, int speed)
	{
		TableModel model = trainTable.getModel();
		model.setValueAt(trainID, row, 0);
		model.setValueAt(section, row, 1);
		model.setValueAt(block, row, 2);
		model.setValueAt(location, row, 3);
		model.setValueAt(authority, row, 4);
		model.setValueAt(speed, row, 5);
	}
	
	private void clearRow(int row)
	{
		TableModel model = trainTable.getModel();
		model.setValueAt(null, row, 0);
		model.setValueAt(null, row, 1);
		model.setValueAt(null, row, 2);
		model.setValueAt(null, row, 3);
		model.setValueAt(null, row, 4);
		model.setValueAt(null, row, 5);		
	}
		
	public static void main(String args[]) 
	{
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MboControllerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MboControllerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MboControllerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MboControllerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MboControllerUI("Blue");
			}
		});
	}
}
