/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Fenne
 */
public class MBODemoUI extends JFrame
{
	private JPanel mainPanel;
	private JScrollPane trainPanel;
	private JTable trainTable;
	private JButton addTrainButton;
	private JButton switch1;
	private JButton switch2;
	
	private MBODemoUI()
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		trainPanel = new JScrollPane();
		addTrainButton = new JButton();
		trainTable = new JTable();
		addTrainButton.setText("Add new train");
		switch1.setText("Flip switch 1");
		switch2.setText("Flip switch 2");
		
		mainPanel.add(addTrainButton);
		mainPanel.add(switch1);
		mainPanel.add(switch2);
		mainPanel.add(trainPanel);
		trainPanel.add(trainTable);
		
		trainTable.setModel(new javax.swing.table.DefaultTableModel(
		   new Object [][] {
			   {null, null, null, null},
			   {null, null, null, null},
			   {null, null, null, null},
			   {null, null, null, null}
		   },
		   new String [] {
			   "Train ID", "Location", "Authority", "Suggested Speed"
		   }
	   ) {
		   boolean[] canEdit = new boolean [] {
			   false, false, false, false
		   };

		   public boolean isCellEditable(int rowIndex, int columnIndex) {
			   return canEdit [columnIndex];
		   }
	   });
	}
}
