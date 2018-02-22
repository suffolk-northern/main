/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import mbo.FakeTrain;
/**
 *
 * @author Fenne
 */
public class MBODemoUI extends JFrame
{
	private JFrame frame;
	private JPanel mainPanel;
	private JScrollPane trainPanel;
	private JTable trainTable;
	private JButton addTrainButton;
	private JButton switch1;
	private JButton switch2;
	
	private int numTrains;
	private String[] tableHeader;
	private Object[][] tableContents;
	
	public boolean addedTrain;
	
	public MBODemoUI()
	{
		frame = new JFrame("MBODemoUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel = new JPanel();
		// mainPanel.setLayout(null);
		trainPanel = new JScrollPane();
		addTrainButton = new JButton();
		trainTable = new JTable();
		switch1 = new JButton();
		switch2 = new JButton();
		
		addTrainButton.setText("Add new train");
		addTrainButton.setPreferredSize(new Dimension(200, 100));
        addTrainButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent evt) 
			{
                addTrainPressed(evt);
            }
        });
		
		switch1.setText("Flip switch 1");
		switch1.setPreferredSize(new Dimension(200, 100));		
		switch2.setText("Flip switch 2");
		switch2.setPreferredSize(new Dimension(200, 100));
		
		mainPanel.add(addTrainButton, BorderLayout.CENTER);
		mainPanel.add(switch1, BorderLayout.CENTER);
		mainPanel.add(switch2, BorderLayout.CENTER);
		
		mainPanel.add(trainPanel);
		trainPanel.setViewportView(trainTable);
		
		tableHeader = new String[] {"Train ID", "Location"};
		tableContents = new Object[][]
		{
		   {null, null}, 
		   {null, null}, 
		   {null, null}, 
		   {null, null}, 
		};
		table.TableModel newModel = table.DefaultTableModel(tableContents, tableHeader);
		javax.swing.table.TableModel
		
		trainTable.setModel(new javax.swing.table.DefaultTableModel
		(
		   new Object [][] 
			{
			   {null, null}, 
			   {null, null}, 
			   {null, null}, 
			   {null, null}, 
			},
		   new String [] {"Train ID", "Location"}
		) 
		{
		   boolean[] canEdit = new boolean[] {false, true};

		   public boolean isCellEditable(int rowIndex, int columnIndex) 
		   {
			   return canEdit [columnIndex];
		   }
		}
		);
		
		frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
	}
	
	private void addTrainPressed(ActionEvent evt) 
	{                                         
		addedTrain = true;
    }          
	
	public void addTrain(FakeTrain train)
	{
		trainTable.set
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
			java.util.logging.Logger.getLogger(MBODemoUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MBODemoUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MBODemoUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MBODemoUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MBODemoUI().setVisible(true);
			}
		});
	}
}

