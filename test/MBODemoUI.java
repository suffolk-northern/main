/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.event.*;

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
	
	public boolean addedTrain = false;
	public boolean switch1Flipped = false;
	public boolean switch2Flipped = false;
	public int trainChangedID = -1;
	public double latChanged = 0;
	public double lonChanged = 0;
	
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
		switch1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				switch1Pressed(evt);
			}
		});
		
		switch2.setText("Flip switch 2");
		switch2.setPreferredSize(new Dimension(200, 100));
		switch1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				switch2Pressed(evt);
			}
		});
		
		mainPanel.add(addTrainButton, BorderLayout.CENTER);
		mainPanel.add(switch1, BorderLayout.CENTER);
		mainPanel.add(switch2, BorderLayout.CENTER);
		
		mainPanel.add(trainPanel);
		trainPanel.setViewportView(trainTable);
		
		tableHeader = new String[] {"Train ID", "Latitude", "Longitude"};
		tableContents = new Object[][]
		{
		   {null, null, null}, 
		   {null, null, null}, 
		   {null, null, null}, 
		   {null, null, null}, 
		};
		trainTable.setModel(new javax.swing.table.DefaultTableModel(tableContents, tableHeader)
		{
		   boolean[] canEdit = new boolean[] {false, true, true};

		   public boolean isCellEditable(int rowIndex, int columnIndex) 
		   {
			   return canEdit [columnIndex];
		   }
		}
		);
		
		trainTable.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent evt)
			{
				updateLocation(evt);
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
	
	private void switch1Pressed(ActionEvent evt)
	{
		switch1Flipped = true;
	}
	
	private void switch2Pressed(ActionEvent evt)
	{
		switch2Flipped = true;
	}
	
	private void updateLocation(TableModelEvent evt)
	{
		int rowChanged = evt.getFirstRow();
		System.out.println(rowChanged);
		TableModel model = trainTable.getModel();
		Object trainID = model.getValueAt(rowChanged, 0);
		Object lat = model.getValueAt(rowChanged, 1);
		Object lon = model.getValueAt(rowChanged, 2);
		if (trainID != null && lat != null && lon != null)
		{
			trainChangedID = Integer.parseInt(trainID.toString());
			latChanged = Double.parseDouble(lat.toString());
			lonChanged = Double.parseDouble(lon.toString());
		}
	}
	
	public void addTrain(FakeTrain train)
	{
		TableModel model = trainTable.getModel();
		model.setValueAt(train.getID(), numTrains, 0);
		double lat = train.location().latitude();
		double lon = train.location().longitude();
		// String locString = String.format("%f, %f", lat, lon);
		model.setValueAt(String.format("%f", lat), numTrains, 1);
		model.setValueAt(String.format("%f", lon), numTrains, 2);
		numTrains += 1;
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

