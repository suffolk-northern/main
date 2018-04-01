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
	private JLabel lineName;
	
	private int numTrains;
	private String[] tableHeader;
	private Object[][] tableContents;
	
	public MboControllerUI()
	{
		frame = new JFrame("MBOController");
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500, 1500);
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		trainPanel = new JScrollPane();
		trainPanel.setPreferredSize(new Dimension(750, 750));
		trainTable = new JTable();
		lineName = new JLabel();
		
		lineName.setText("Blue Line");
		lineName.setPreferredSize(new Dimension(200, 100));
		
		mainPanel.add(lineName, BorderLayout.NORTH);
		mainPanel.add(trainPanel, BorderLayout.CENTER);
		trainPanel.setViewportView(trainTable);
		
		tableHeader = new String[] {"Train ID", "Section", "Block", "Location (yards)", "Authority (yards)", "Suggested speed (mph)"};
		tableContents = new Object[][]
		{
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
			{null, null, null, null, null, null},
		};
		trainTable.setModel(new javax.swing.table.DefaultTableModel(tableContents, tableHeader)
		{
		   boolean[] canEdit = new boolean[] {false, false, false, false, false, false};

		   public boolean isCellEditable(int rowIndex, int columnIndex) 
		   {
			   return canEdit [columnIndex];
		   }
		}
		);
		
		trainTable.getColumn(0).setPreferredWidth(50);
		trainTable.getColumn(1).setPreferredWidth(50);
		trainTable.getColumn(2).setPreferredWidth(50);
		trainTable.getColumn(3).setPreferredWidth(50);
		trainTable.getColumn(4).setPreferredWidth(50);
		trainTable.getColumn(5).setPreferredWidth(50);
		
		frame.add(mainPanel);
		mainPanel.setPreferredSize(new Dimension(1000, 1000));
        frame.pack();
        frame.setVisible(true);
		
		numTrains = 0;
	}
	
	public void addTrain(int trainID, char section, int blockID, int location, int authority, int speed)
	{
		TableModel model = trainTable.getModel();
		model.setValueAt(trainID, numTrains, 0);
		model.setValueAt(section, numTrains, 1);
		model.setValueAt(blockID, numTrains, 2);
		model.setValueAt(location, numTrains, 3);
		model.setValueAt(authority, numTrains, 4);
		model.setValueAt(speed, numTrains, 5);
//		double lat = train.location().latitude();
//		double lon = train.location().longitude();
//		String locString = String.format("%f, %f", lat, lon);
//		model.setValueAt(locString, numTrains, 1);
		numTrains += 1;
	}
	
	public void updateTrain(int trainID, char section, int block, int location, int authority, int speed)
	{
		TableModel model = trainTable.getModel();
		int trainRow = 0;
		for (int i = 0; i < numTrains; i++)
		{
			if(trainID == (int) trainTable.getValueAt(i, 0))
				trainRow = i;
		}
		
		model.setValueAt(section, trainRow, 1);
		model.setValueAt(block, trainRow, 2);
		model.setValueAt(location, trainRow, 3);
		model.setValueAt(authority, trainRow, 4);
		model.setValueAt(speed, trainRow, 5);
	}
		
	public static void main(String args[]) {
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
				new MboControllerUI().setVisible(true);
			}
		});
	}
}
