/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Container;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Font;
import java.awt.Image;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayDeque;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author missm
 */
public class CtcUI extends javax.swing.JFrame {

	public static final int TRACKCOLS = 9;
	public Ctc ctc;
	private JFrame frame;
	private static javax.swing.table.DefaultTableModel trackModel;
	private static String[] greenBlocks;
	private static String[] redBlocks;

	public CtcUI(Ctc ctc) {
		this.ctc = ctc;
		frame = this;
		initialize();
	}

	private void initialize() {
		jLabel7 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		dispatchButton = new javax.swing.JButton();
		jLabel16 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		trackTable = new javax.swing.JTable();
		blockSelect = new javax.swing.JComboBox<>();
		blueThrough = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		jLabel19 = new javax.swing.JLabel();
		multSelect = new javax.swing.JComboBox<>();
		speedSelect = new javax.swing.JTextField();
		jLabel10 = new javax.swing.JLabel();
		manualMode = new javax.swing.JRadioButton();
		jLabel11 = new javax.swing.JLabel();
		autoMode = new javax.swing.JRadioButton();
		trainSelect = new javax.swing.JComboBox<>();
		jLabel12 = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		lineSelect = new javax.swing.JComboBox<>();
		jLabel2 = new javax.swing.JLabel();
		jTextField4 = new javax.swing.JTextField();
		jLabel13 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		trainTable = new javax.swing.JTable();
		blueMaintenance = new javax.swing.JComboBox<>();
		fixedBlock = new javax.swing.JRadioButton();
		jTextField5 = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		jLabel17 = new javax.swing.JLabel();
		MovingBlock = new javax.swing.JRadioButton();
		jLabel8 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		openTrack = new javax.swing.JRadioButton();
		jLabel5 = new javax.swing.JLabel();
		scheduleButton = new javax.swing.JButton();
		closeTrack = new javax.swing.JRadioButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

		jLabel7.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel7.setText("Train Select");

		jLabel6.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel6.setText("Blue Line");

		dispatchButton.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		dispatchButton.setText("Dispatch");
		dispatchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispatchButtonActionPerformed(evt);
			}
		});

		jLabel16.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel16.setText("Red Line");
		jLabel16.setVisible(false);

		trackTable.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		
		trackModel = new javax.swing.table.DefaultTableModel(
				new Object[][]{
					{"", "", "", "", null, "", "", null, null},
					{"", "", "", "", null, "", "", null, null},
					{"", "", "", "", null, "", "", null, null},
					{"", "", "", "", null, "", "", null, null},
					{"", "", "", "", null, null, null, null, null},
					{"", "", "", "", null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null, null}
				},
				new String[]{
					"Line", "Section", "Number", "Occupancy", "Switch", "Signals", "Status", "RR Xing", "Station"
				}
		);
		trackTable.setModel(trackModel);
		jScrollPane1.setViewportView(trackTable);

		trackTable.getModel().addTableModelListener(trainTable);
		trackTable.setRowHeight(30);
		trackTable.getColumnModel().getColumn(0).setMinWidth(80);
		trackTable.getColumnModel().getColumn(3).setMinWidth(90);
		trackTable.getColumnModel().getColumn(4).setMinWidth(95);
		trackTable.getColumnModel().getColumn(6).setMinWidth(90);
		trackTable.getColumnModel().getColumn(7).setMinWidth(80);
		trackTable.getColumnModel().getColumn(8).setMinWidth(80);

		blockSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		blockSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{}));

		blueThrough.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		blueThrough.setText("               ");

		jLabel9.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel9.setText("Maintenance");

		jLabel19.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel19.setText("Multiplier");

		multSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		multSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"x1", "x2", "x5", "x10"}));
		multSelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				multSelectActionPerformed(evt);
			}
		});

		speedSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N

		jLabel10.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel10.setText("Throughput");

		manualMode.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		manualMode.setText("Manual");
		manualMode.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				manualModeActionPerformed(evt);
			}
		});

		jLabel11.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel11.setText("Clock");

		autoMode.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		autoMode.setText("Automatic");
		autoMode.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				autoModeActionPerformed(evt);
			}
		});

		trainSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		trainSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"0"}));

		jLabel12.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel12.setText("Line");

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
		jLabel1.setText("Trains");

		lineSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		lineSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Green","Red"}));
		lineSelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				lineSelectActionPerformed(evt);
			}
		});
		
		
		jLabel2.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
		jLabel2.setText("Track");

		jTextField4.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jTextField4.setText("               ");
		jTextField4.setVisible(false);

		jLabel13.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel13.setText("Control Transfer");

		trainTable.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		trainTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][]{
					{"", "", "", "", "", "", "", ""},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null},
					{null, null, null, null, null, null, null, null}
				},
				new String[]{
					"Line", "Train ID", "Current Block", "Destination", "Deadline", "Authority (yd)", "Suggested Speed (mph)", "Passengers"
				}
		));

		trainTable.getModel().addTableModelListener(trainTable);
		trainTable.setRowHeight(30);

		jScrollPane2.setViewportView(trainTable);

		blueMaintenance.setEditable(true);
		blueMaintenance.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		blueMaintenance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8"}));

		fixedBlock.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		fixedBlock.setText("Fixed Block");
		fixedBlock.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fixedBlockActionPerformed(evt);
			}
		});

		jTextField5.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jTextField5.setText("10:02");

		jLabel3.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel3.setText("Block");

		jLabel17.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel17.setText("passengers/hr");

		MovingBlock.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		MovingBlock.setText("Moving Block");
		MovingBlock.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MovingBlockActionPerformed(evt);
			}
		});

		jLabel8.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel8.setText("Blue Line");

		jLabel18.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel18.setText("passengers/hr");
		jLabel18.setVisible(false);

		jLabel14.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
		jLabel14.setText("CTC");

		openTrack.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		openTrack.setText("Open Track");
		openTrack.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openTrackActionPerformed(evt);
			}
		});

		jLabel5.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		jLabel5.setText("Suggested Speed");

		scheduleButton.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		scheduleButton.setText("Schedule");
		scheduleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scheduleButtonActionPerformed(evt);
			}
		});

		closeTrack.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
		closeTrack.setText("Close Track");
		closeTrack.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeTrackActionPerformed(evt);
			}
		});

		ButtonGroup maintenance = new ButtonGroup();
		maintenance.add(closeTrack);
		maintenance.add(openTrack);

		ButtonGroup fixedmoving = new ButtonGroup();
		fixedmoving.add(fixedBlock);
		fixedmoving.add(MovingBlock);

		ButtonGroup manOrAuto = new ButtonGroup();
		manOrAuto.add(manualMode);
		manOrAuto.add(autoMode);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGap(77, 77, 77)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(closeTrack)
														.addComponent(jLabel9)
														.addComponent(jLabel2)
														.addComponent(openTrack)
														.addGroup(layout.createSequentialGroup()
																.addComponent(fixedBlock)
																.addGap(94, 94, 94)
																.addComponent(manualMode))
														.addGroup(layout.createSequentialGroup()
																.addComponent(MovingBlock)
																.addGap(72, 72, 72)
																.addComponent(autoMode))
														.addComponent(jLabel13)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
																.addComponent(blueMaintenance, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
																.addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
												.addContainerGap(37, Short.MAX_VALUE)
												.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(38, 38, 38)))
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jLabel1)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel10)
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabel6)
																		.addComponent(jLabel16))
																.addGap(50, 50, 50)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGap(18, 18, 18)
																				.addComponent(jLabel18))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(blueThrough, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGap(18, 18, 18)
																				.addComponent(jLabel17))))
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabel3)
																		.addComponent(jLabel5)
																		.addComponent(jLabel7)
																		.addGroup(layout.createSequentialGroup()
																				.addGap(9, 9, 9)
																				.addComponent(jLabel12)))
																.addGap(38, 38, 38)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
																		.addComponent(lineSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(blockSelect, 0, 205, Short.MAX_VALUE)
																		.addComponent(trainSelect, 0, 205, Short.MAX_VALUE)
																		.addComponent(speedSelect)))
														.addGroup(layout.createSequentialGroup()
																.addGap(165, 165, 165)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(scheduleButton)
																		.addComponent(dispatchButton))))
												.addGap(52, 52, 52)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(jLabel11)
																.addGap(35, 35, 35)
																.addComponent(jLabel19))
														.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 857, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(layout.createSequentialGroup()
																.addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(18, 18, 18)
																.addComponent(multSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
								.addGap(79, 79, 79))
						.addGroup(layout.createSequentialGroup()
								.addGap(810, 810, 810)
								.addComponent(jLabel14)
								.addGap(0, 0, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGap(42, 42, 42)
								.addComponent(jLabel14)
								.addGap(87, 87, 87)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addGap(98, 98, 98)
																.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(43, 43, 43)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(jLabel10)
																		.addComponent(jLabel11)
																		.addComponent(jLabel19)))
														.addGroup(layout.createSequentialGroup()
																.addComponent(jLabel1)
																.addGap(59, 59, 59)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
																		.addComponent(jLabel12)
																		.addComponent(lineSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGap(42, 42, 42)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(trainSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jLabel7))
																.addGap(18, 18, 18)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(jLabel3)
																		.addComponent(blockSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(jLabel5)
																		.addComponent(speedSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGap(135, 135, 135)
																.addComponent(dispatchButton)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(scheduleButton)))
												.addGap(1, 1, 1)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel6)
														.addComponent(blueThrough, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel17)
														.addComponent(multSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGap(3, 3, 3)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel16)
														.addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel18)))
										.addGroup(layout.createSequentialGroup()
												.addComponent(jLabel2)
												.addGap(56, 56, 56)
												.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18)
												.addComponent(jLabel9)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jLabel8)
												.addGap(4, 4, 4)
												.addComponent(blueMaintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(openTrack)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(closeTrack)
												.addGap(56, 56, 56)
												.addComponent(jLabel13)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(fixedBlock)
														.addComponent(manualMode))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(MovingBlock)
														.addComponent(autoMode))))
								.addContainerGap())
		);

		pack();
	}

	/**
	 * Creates new form MyCtcUI
	 */
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	/*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dispatchButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        trackTable = new javax.swing.JTable();
        blockSelect = new javax.swing.JComboBox<>();
        jTextField3 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        multSelect = new javax.swing.JComboBox<>();
        speedSelect = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        manualMode = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        autoMode = new javax.swing.JRadioButton();
        trainSelect = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lineSelect = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        trainTable = new javax.swing.JTable();
        blueMaintenance = new javax.swing.JComboBox<>();
        fixedBlock = new javax.swing.JRadioButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        MovingBlock = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        openTrack = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        scheduleButton = new javax.swing.JButton();
        closeTrack = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel7.setText("Train Select");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel6.setText("Green Line");

        dispatchButton.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        dispatchButton.setText("Dispatch");
        dispatchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchButtonActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel16.setText("Red Line");

        trackTable.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        trackTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", null, "", "", null},
                {"", "", "", "", null, "", "", null},
                {"", "", "", "", null, "", "", null},
                {"", "", "", "", null, "", "", null},
                {"", "", "", "", null, null, null, null},
                {"", "", "", "", null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Section", "Number", "Occupancy", "Switch", "Signals", "Status", "RR Xing"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(trackTable);

        blockSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        blockSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A1", "A2", "A3", "A4", "A5", "A6", "A7", "YARD" }));

        jTextField3.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jTextField3.setText("               ");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel9.setText("Maintenance");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel19.setText("Multiplier");

        multSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        multSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "x1", "x2", "x4", "x10" }));
        multSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multSelectActionPerformed(evt);
            }
        });

        speedSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel10.setText("Throughput");

        manualMode.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        manualMode.setText("Manual");
        manualMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualModeActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel11.setText("Clock");

        autoMode.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        autoMode.setText("Automatic");
        autoMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoModeActionPerformed(evt);
            }
        });

        trainSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        trainSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2" }));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel12.setText("Line");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
        jLabel1.setText("Trains");

        lineSelect.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lineSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Blue" }));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
        jLabel2.setText("Track");

        jTextField4.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jTextField4.setText("               ");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel13.setText("Control Transfer");

        trainTable.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        trainTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", "", "", "", ""},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Train ID", "Current Block", "Destination", "Deadline", "Authority (yd)", "Suggested Speed (mph)", "Passengers"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(trainTable);

        blueMaintenance.setEditable(true);
        blueMaintenance.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        blueMaintenance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8" }));

        fixedBlock.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        fixedBlock.setText("Fixed Block");
        fixedBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedBlockActionPerformed(evt);
            }
        });

        jTextField5.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jTextField5.setText("10:02");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel3.setText("Block");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel17.setText("passengers/hr");

        MovingBlock.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        MovingBlock.setText("Moving Block");
        MovingBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MovingBlockActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel8.setText("Blue Line");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel18.setText("passengers/hr");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel14.setText("CTC");

        openTrack.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        openTrack.setText("Open Track");
        openTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openTrackActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel5.setText("Suggested Speed");

        scheduleButton.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        scheduleButton.setText("Schedule");
        scheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleButtonActionPerformed(evt);
            }
        });

        closeTrack.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        closeTrack.setText("Close Track");
        closeTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTrackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(closeTrack)
                            .addComponent(jLabel9)
                            .addComponent(jLabel2)
                            .addComponent(openTrack)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fixedBlock)
                                .addGap(94, 94, 94)
                                .addComponent(manualMode))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(MovingBlock)
                                .addGap(72, 72, 72)
                                .addComponent(autoMode))
                            .addComponent(jLabel13)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(blueMaintenance, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(37, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel16))
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel17))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(9, 9, 9)
                                        .addComponent(jLabel12)))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lineSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(blockSelect, 0, 205, Short.MAX_VALUE)
                                    .addComponent(trainSelect, 0, 205, Short.MAX_VALUE)
                                    .addComponent(speedSelect)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(165, 165, 165)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scheduleButton)
                                    .addComponent(dispatchButton))))
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(35, 35, 35)
                                .addComponent(jLabel19))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 857, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(multSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(79, 79, 79))
            .addGroup(layout.createSequentialGroup()
                .addGap(810, 810, 810)
                .addComponent(jLabel14)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel14)
                .addGap(87, 87, 87)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(98, 98, 98)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel19)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(59, 59, 59)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12)
                                    .addComponent(lineSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(trainSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(blockSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(speedSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(135, 135, 135)
                                .addComponent(dispatchButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(scheduleButton)))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(multSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(56, 56, 56)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addGap(4, 4, 4)
                        .addComponent(blueMaintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(openTrack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeTrack)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fixedBlock)
                            .addComponent(manualMode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(MovingBlock)
                            .addComponent(autoMode))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    */
    private void fixedBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedBlockActionPerformed
		// TODO add your handling code here:
		//System.out.println("Transfer control to fixed block");
		ctc.disableMBO("green");
		//ctc.disableMBO("red");
    }//GEN-LAST:event_fixedBlockActionPerformed


    private void dispatchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchButtonActionPerformed
		// TODO add your handling code here:
		double speed;
		String line = (String) lineSelect.getSelectedItem();
		String block = (String) blockSelect.getSelectedItem();
		String train = (String) trainSelect.getSelectedItem();
		if (speedSelect.getText() == null || speedSelect.getText().equals("")) {
			speed = 0;
		} else {
			speed = Double.parseDouble(speedSelect.getText());
		}

		ctc.routeTrain(train, line, block, speed);
		
		//ctc.sendSpeedAuthShort(train, speed, 100);
    }//GEN-LAST:event_dispatchButtonActionPerformed

	protected static void updateTrainTable(Object[][] rows, int count) {

		if (!trainTable.getValueAt(0, 0).equals("")) {
			for (Object[] row : rows) {
				for (int i = 0; i < trainTable.getRowCount(); i++) {
					if (trainTable.getValueAt(i, 1) != null && trainTable.getValueAt(i, 1).equals(row[1])) {
						for (int j = 1; j < trainTable.getColumnCount(); j++) {
							trainTable.setValueAt(row[j], i, j);
						}
					}
				}
			}
		} else {
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < trainTable.getColumnCount(); j++) {
					trainTable.setValueAt(rows[i][j], i, j);
				}
			}
		}

	}

	protected static void updateTrackTable(Object[][] rows, int count) {
 
		ArrayDeque<String> greens = new ArrayDeque<String>();
		ArrayDeque<String> reds = new ArrayDeque<String>();
		
		String line;
		
		trackModel.setRowCount(count);
		
		trackTable.setModel(trackModel);
		
		for (int i = 0; i < trackTable.getRowCount(); i++) 
		{
			line = (String)rows[i][0];

			if(line.equalsIgnoreCase("Green"))
			{
				greens.add(rows[i][1] + "" + rows[i][2]);
			}
			else if(line.equalsIgnoreCase("Red"))
			{
				reds.add(rows[i][1] + "" + rows[i][2]);
			}

			for (int j = 0; j < trackTable.getColumnCount(); j++) 
			{
				trackTable.setValueAt(rows[i][j], i, j);
			}
		}
		
		int sizegreen = greens.size() + 1;
		int sizered = reds.size() + 1;
		
		greenBlocks = new String[sizegreen];
		redBlocks = new String[sizered];
		
		greenBlocks[0] = "YARD";
		redBlocks[0] = "YARD";
		
		for(int i = 1; i < sizegreen; i++)
		{
			greenBlocks[i] = greens.poll();
		}
		
		for(int i = 1; i < sizered; i++)
		{
			redBlocks[i] = reds.poll();
		}
		
	}

	protected void updateThroughput(double through) {
		blueThrough.setText(Double.toString(through));
	}


    private void openTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTrackActionPerformed
		// TODO add your handling code here:

		// inform user
		String str = (String) blueMaintenance.getSelectedItem();
		System.out.println("Block " + str + " opened");

		Object obj = "";

		for (int i = 0; i < trackTable.getRowCount(); i++) {

			if (trackTable.getValueAt(i, 1) != null && (trackTable.getValueAt(i, 1).toString()).equals(str.substring(0, 1)) && (trackTable.getValueAt(i, 2).toString()).equals((str.substring(1)))) {
				trackTable.setValueAt(obj, i, 6);
			}
		}

    }//GEN-LAST:event_openTrackActionPerformed

    private void closeTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeTrackActionPerformed
		// TODO add your handling code here:

		String str = (String) blueMaintenance.getSelectedItem();
		System.out.println("Block " + str + " closed for maintenance");

		Object obj = "Closed";

		for (int i = 0; i < trackTable.getRowCount(); i++) {
			if (trackTable.getValueAt(i, 1) != null && (trackTable.getValueAt(i, 1).toString()).equals(str.substring(0, 1)) && (trackTable.getValueAt(i, 2).toString()).equals((str.substring(1)))) {
				trackTable.setValueAt(obj, i, 6);
			}
		}

    }//GEN-LAST:event_closeTrackActionPerformed

	private void lineSelectActionPerformed(java.awt.event.ActionEvent evt)
	{
		if(lineSelect.getSelectedIndex() == 0)
		{
			blockSelect.setModel(new javax.swing.DefaultComboBoxModel<>(greenBlocks));
		}
		else
		{
			blockSelect.setModel(new javax.swing.DefaultComboBoxModel<>(redBlocks));
		}
				
	}
	
    private void scheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleButtonActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_scheduleButtonActionPerformed

    private void multSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multSelectActionPerformed
		// TODO add your handling code here:
		
		String mult = (String) multSelect.getSelectedItem();
		int speedup = Integer.parseInt(mult.substring(1));
		
		ctc.changeSpeedUp(speedup);
		
    }//GEN-LAST:event_multSelectActionPerformed

    private void MovingBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MovingBlockActionPerformed
		// TODO add your handling code here:
		//System.out.println("Transfer control to MBO");
		ctc.enableMBO("Green");
		//ctc.enableMBO("Red");
		
    }//GEN-LAST:event_MovingBlockActionPerformed

    private void manualModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualModeActionPerformed
		// TODO add your handling code here:
		//System.out.println("Manual mode");
		ctc.manMode("green");
		//ctc.manMode("red");
    }//GEN-LAST:event_manualModeActionPerformed

    private void autoModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoModeActionPerformed
		// TODO add your handling code here:
		//System.out.println("Automatic mode");
		ctc.autoMode("green");
		//ctc.autoMode("red");
    }//GEN-LAST:event_autoModeActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public void showUI() {
		frame.setVisible(true);
	}

	/*
    public static void main(String args[]) {
         Set the Nimbus look and feel 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MyCtcUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyCtcUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyCtcUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyCtcUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

         Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyCtcUI().setVisible(true);
            }
        });
    }
	 */
	private javax.swing.JRadioButton MovingBlock;
	private javax.swing.JRadioButton autoMode;
	private javax.swing.JComboBox<String> blockSelect;
	private javax.swing.JComboBox<String> blueMaintenance;
	private javax.swing.JRadioButton closeTrack;
	private javax.swing.JButton dispatchButton;
	private javax.swing.JRadioButton fixedBlock;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel18;
	private javax.swing.JLabel jLabel19;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextField blueThrough;
	private javax.swing.JTextField jTextField4;
	private javax.swing.JTextField jTextField5;
	private javax.swing.JComboBox<String> lineSelect;
	private javax.swing.JRadioButton manualMode;
	private javax.swing.JComboBox<String> multSelect;
	private javax.swing.JRadioButton openTrack;
	private javax.swing.JButton scheduleButton;
	private javax.swing.JTextField speedSelect;
	private static javax.swing.JTable trackTable;
	private javax.swing.JComboBox<String> trainSelect;
	private static javax.swing.JTable trainTable;

	/**
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton MovingBlock;
    private javax.swing.JRadioButton autoMode;
    private javax.swing.JComboBox<String> blockSelect;
    private javax.swing.JComboBox<String> blueMaintenance;
    private javax.swing.JRadioButton closeTrack;
    private javax.swing.JButton dispatchButton;
    private javax.swing.JRadioButton fixedBlock;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JComboBox<String> lineSelect;
    private javax.swing.JRadioButton manualMode;
    private javax.swing.JComboBox<String> multSelect;
    private javax.swing.JRadioButton openTrack;
    private javax.swing.JButton scheduleButton;
    private javax.swing.JTextField speedSelect;
    private javax.swing.JTable trackTable;
    private javax.swing.JComboBox<String> trainSelect;
    private javax.swing.JTable trainTable;
    // End of variables declaration//GEN-END:variables
}
	 *
	 * /
	 *///
}
