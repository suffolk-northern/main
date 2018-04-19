package mbo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.sql.Time;
import javax.swing.table.*;

import mbo.schedules.*;
/**
 *
 * @author Kaylene Stocking
 */
public class MboSchedulerUI extends javax.swing.JFrame 
{
	private LineSchedule schedule;
	private boolean scheduleRequest;
	private int[] throughput;
	private Time start;
	private Time end;
	private String lineName;
	
	// For the view schedule screen
	private String viewingStation;
	
    /**
     * Creates new form Scheduler
     */
    public MboSchedulerUI(String ln) {
        initComponents();
		lineName = ln;
		if (lineName == null)
			System.out.println("Name is null");
		scheduleRequest = false;
		
		// Default start and end times are 9 AM and 5 PM
		start = new Time(9, 0, 0);
		end = new Time(17, 0, 0);
    }
	
	// TODO: error messages for start time after end time
	// And changing times after entering throughput
	// Check for entering negative throughput

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startTimeCombo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        endTimeCombo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        enterThroughputButton = new javax.swing.JButton();
        generateScheduleButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        viewShceduleButton = new javax.swing.JButton();
        exportScheduleButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        loadScheduleButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        throughputTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        finishedThroughputButton = new javax.swing.JButton();
        schedulePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        stationScheduleTable = new javax.swing.JTable();
        stationScheduleCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        individualScheduleTable = new javax.swing.JTable();
        individualScheduleLabel = new javax.swing.JLabel();
        chooseStationLabel = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

		mainMessageLabel = new JLabel();
		throughputMessageLabel = new JLabel();	
		scheduleMessageLabel = new JLabel();
		
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new CardLayout());
		
		// Main panel

        mainPanel.setLayout(null);
		
		mainMessageLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
		mainMessageLabel.setText(" ");
		mainPanel.add(mainMessageLabel);
		mainMessageLabel.setBounds(10, 400, 600, 40);

        jLabel1.setFont(new Font("Tahoma", 0, 18)); 
        jLabel1.setText("Create New Schedule");
        mainPanel.add(jLabel1);
        jLabel1.setBounds(110, 80, 180, 40);
		
		String[] times = new String[24];
		for (int i = 0; i < 24; i++)
		{
			if (i < 10)
				times[i] = String.format("0%d:00", i);
			else
				times[i] = String.format("%d:00", i);
		}
        startTimeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(times));
		// Default start time is 9 AM
		startTimeCombo.setSelectedIndex(9);
        startTimeCombo.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent evt) 
			{
                startTimeComboChanged(evt);
            }
        });
        mainPanel.add(startTimeCombo);
        startTimeCombo.setBounds(200, 150, 70, 22);

        jLabel2.setText("Start Time");
        mainPanel.add(jLabel2);
        jLabel2.setBounds(130, 150, 61, 16);

        endTimeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(times));
		// Default end time is 5 PM
		endTimeCombo.setSelectedIndex(17);
		endTimeCombo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt) 
			{
				endTimeComboChanged(evt);
			}
		});
        mainPanel.add(endTimeCombo);
        endTimeCombo.setBounds(200, 180, 70, 22);

        jLabel3.setText("End Time");
        mainPanel.add(jLabel3);
        jLabel3.setBounds(130, 180, 54, 16);

        enterThroughputButton.setText("Input Throughput");
        enterThroughputButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent evt) 
			{
                enterThroughputButtonClicked(evt);
            }
        });
        mainPanel.add(enterThroughputButton);
        enterThroughputButton.setBounds(130, 230, 131, 25);

        generateScheduleButton.setText("Generate Schedule");
        generateScheduleButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent evt) 
			{
                generateScheduleButtonClicked(evt);
            }
        });
        mainPanel.add(generateScheduleButton);
        generateScheduleButton.setBounds(120, 290, 141, 25);
        mainPanel.add(jProgressBar1);
        jProgressBar1.setBounds(110, 330, 157, 14);
        mainPanel.add(jSeparator1);
        jSeparator1.setBounds(120, 270, 141, 10);

        viewShceduleButton.setText("View Schedule");
        viewShceduleButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent evt) 
			{
                viewScheduleButtonClicked(evt);
            }
        });
        mainPanel.add(viewShceduleButton);
        viewShceduleButton.setBounds(50, 360, 117, 25);

        exportScheduleButton.setText("Export Schedule");
        exportScheduleButton.addActionListener(new ActionListener() 
		{
            public void actionPerformed(java.awt.event.ActionEvent evt) 
			{
                exportScheduleButtonClicked(evt);
            }
        });
        mainPanel.add(exportScheduleButton);
        exportScheduleButton.setBounds(200, 360, 130, 25);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Automatic Train Dispatch");
        mainPanel.add(jLabel11);
        jLabel11.setBounds(500, 80, 210, 40);

        jRadioButton1.setText("Enabled");
        mainPanel.add(jRadioButton1);
        jRadioButton1.setBounds(510, 120, 80, 25);

        jRadioButton2.setText("Disabled");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });
        mainPanel.add(jRadioButton2);
        jRadioButton2.setBounds(610, 120, 77, 25);

        mainPanel.add(jTextField1);
        jTextField1.setBounds(580, 170, 160, 22);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Schedule Time", "Train ID", "Driver ID", "Dispatched?"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable4);

        mainPanel.add(jScrollPane5);
        jScrollPane5.setBounds(400, 210, 410, 180);

        loadScheduleButton.setText("Select Schedule File");
        loadScheduleButton.addActionListener(new java.awt.event.ActionListener() 
		{
            public void actionPerformed(java.awt.event.ActionEvent evt) 
			{
                loadScheduleButtonClicked(evt);
            }
        });
        mainPanel.add(loadScheduleButton);
        loadScheduleButton.setBounds(420, 170, 160, 25);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); 
		String titleString = String.format("MBO Scheduler: %s Line", lineName);
        jLabel12.setText(titleString);
        mainPanel.add(jLabel12);
        jLabel12.setBounds(290, 20, 290, 29);

        getContentPane().add(mainPanel, "card2");

        throughputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Not initialized", "Not initialized"},
            },
            new String [] {"Hour", "Throughput"}
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        jScrollPane1.setViewportView(throughputTable);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Enter Throughput");
		
		throughputMessageLabel.setFont(new java.awt.Font("Tahoma", 0, 14));	
		throughputMessageLabel.setText("");

        finishedThroughputButton.setText("Return to Scheduler");
        finishedThroughputButton.addActionListener(new java.awt.event.ActionListener() 
		{
            public void actionPerformed(java.awt.event.ActionEvent evt) 
			{
                finishedThroughputButtonClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(127, 127, 127)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(finishedThroughputButton)
                        .addGap(122, 122, 122))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(18, 18, 18)
				// .addComponent(throughputMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(finishedThroughputButton)
                .addContainerGap(189, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, "card3");

		// Schedule panel
		
        schedulePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		
		scheduleMessageLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
		scheduleMessageLabel.setText("Testing testing testing");
		schedulePanel.add(scheduleMessageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 500, -1, -1));

        stationScheduleTable.setModel(new DefaultTableModel(
            new Object [][] {
                {"Not initialized", "Not initialized", "Not initialized"}
            },
            new String [] {
                "Time", "Action", "Train ID"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        jScrollPane2.setViewportView(stationScheduleTable);

        schedulePanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 370, 280));

        stationScheduleCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Not initialized" }));
		stationScheduleCombo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent evt)
			{
				stationScheduleComboChanged(evt);
			}
		});
        schedulePanel.add(stationScheduleCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Station Schedule");
        schedulePanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, -1, -1));

        jButton5.setText("Return To Main Screen");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        schedulePanel.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 560, -1, -1));

        jLabel6.setText("Click a train or employee ID to see their schedule");
        schedulePanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 400, -1, -1));

        individualScheduleTable.setModel(new DefaultTableModel(
            new Object [][] {
                {"8:20:00", "Begin Shift", "Yard"},
                {"8:20:00", "Begin Driving Train 2", "Yard"},
                {"12:20:00", "Get off Train", "Yard"},
                {"12:20:00", "Begin Break", "Yard"},
                {"12:50:00", "End Break", "Yard"},
                {"12:50:00", "Begin Driving Train 6", "Yard"},
                {"17:50:00", "End Driving Train 6", "Yard"},
                {"17:50:00", "End Shift", "Yard"}
            },
            new String [] {
                "Time", "Action", "Station"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) 
			{
                return false;
            }
        });
        jScrollPane3.setViewportView(individualScheduleTable);

        schedulePanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 380, 180));

        individualScheduleLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        individualScheduleLabel.setText("Choose a train or driver");
        schedulePanel.add(individualScheduleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, -1, -1));

        chooseStationLabel.setText("Select a station:");
        schedulePanel.add(chooseStationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, -1, -1));

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM))));
        schedulePanel.add(jFormattedTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 280, 110, -1));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        schedulePanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 400, 500, -1));

        jButton7.setText("Confirm");
        schedulePanel.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 510, -1, -1));

        jButton8.setText("Undo");
        schedulePanel.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 510, -1, -1));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pioneer", "Edgebrook", "Whited" }));
        schedulePanel.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 320, -1, -1));

        jLabel9.setText("Edit station:");
        schedulePanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 320, -1, -1));

        jButton9.setText("Add");
        schedulePanel.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 320, -1, -1));

        jButton10.setText("Remove");
        schedulePanel.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 320, -1, -1));

        jLabel10.setText("minutes");
        schedulePanel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 280, -1, -1));

        getContentPane().add(schedulePanel, "card4");

        pack();
    }// </editor-fold>                        

    private void enterThroughputButtonClicked(java.awt.event.ActionEvent evt) 
	{                                         
        // Enter throughput button in Panel1
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card3");
		generateThroughputTable();
    }                                        

    private void finishedThroughputButtonClicked(ActionEvent evt) 
	{                                         
        // Return to scheduler button in throughputPanel
		TableModel model = throughputTable.getModel();
		throughput = new int[model.getRowCount()];
		boolean throughputError = false;
		for (int i = 0; i < model.getRowCount(); i++)
		{
			String s = model.getValueAt(i, 1).toString();
			int enteredVal = Integer.parseInt(s);
			if (enteredVal < 0)
			{
				throughput[i] = 0;
				throughputError = true;
			}
			else
				throughput[i] = enteredVal;
		}
		if (!throughputError)
		{
			CardLayout cl = (CardLayout)(getContentPane().getLayout());
			cl.show(getContentPane(), "card2"); 
		}
		else
			setMessage("Please make sure all throughput values are positive integers.", "throughput");
    }                                        

    private void generateScheduleButtonClicked(java.awt.event.ActionEvent evt) 
	{                                         
		scheduleRequest = true;
    }                                        

    private void viewScheduleButtonClicked(java.awt.event.ActionEvent evt) 
	{                                         
        // View schedule button in main panel
		if (schedule != null)
		{
			// Make the station schedules
			if (!schedule.stationSchedulesExist())
				schedule.generateStationSchedules();
			String defaultStation = schedule.getStationNames().get(0);
			// Fill in station selection combo box
			String[] stations = new String[schedule.getStationNames().size()];
			for (int i = 0; i < schedule.getStationNames().size(); i++)
			{
				stations[i] = schedule.getStationNames().get(i);
			}
			stationScheduleCombo.setModel(new javax.swing.DefaultComboBoxModel<>(stations));
			// Make the station schedule table
			generateStationTable(defaultStation);
			CardLayout cl = (CardLayout)(getContentPane().getLayout());
			cl.show(getContentPane(), "card4");  
		}
		else
			setMessage("No schedule loaded. Please create or load schedule.");
    }                                        

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) 
	{                                         
        // Return to scheduler button in Panel2
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card2");              
    }                                                                           

    private void startTimeComboChanged(java.awt.event.ActionEvent evt) 
	{                                           
        // Start time drop down in panel1
        JComboBox cb = (JComboBox) evt.getSource();
        String startTime = (String) cb.getSelectedItem();
		int startHour = Integer.parseInt(startTime.substring(0, 2));
		start = new Time(startHour, 0, 0);
    }  
	
	private void endTimeComboChanged(ActionEvent evt)
	{
        JComboBox cb = (JComboBox)evt.getSource();
        String endTime = (String)cb.getSelectedItem();
		int endHour = Integer.parseInt(endTime.substring(0, 2));
		end = new Time(endHour, 0, 0);	
	}

	private void generateThroughputTable()
	{
		// TODO: worry about overnight schedules?
		int startHour = start.getHours();
		int endHour = end.getHours();
		int numHours = endHour - startHour;
		Object[][] tableObject = new Object[numHours][2];
		int ind = 0;
		for (int i = startHour; i < endHour; i++)
		{
			if (i < 10)
				tableObject[ind][0] = String.format("0%d:00", i); 
			else
				tableObject[ind][0] = String.format("%d:00", i);
			tableObject[ind][1] = "0";
			ind += 1;
		}
		
		String[] headers = {"Hour", "Throughput"};		
		DefaultTableModel mod = new DefaultTableModel(tableObject, headers) 
		{
			public boolean isCellEditable(int rowIndex, int colIndex)
			{
				if (colIndex == 1)
					return true;
				else
					return false;
			}
		};
		throughputTable.setModel(mod);
	}
	
	public void generateStationTable(String stationName)
	{
		StationSchedule ss = schedule.getStationSchedule(stationName);
		if (ss != null)
		{
			int numEvents = ss.getEvents().size();
			System.out.printf("numEvents: %d%n", numEvents);
			String[][] tableObject = new String[numEvents][3];
			System.out.println(tableObject.length);
			int ind = 0;
			for (StationEvent se : ss.getEvents())
			{
				if (se == null)
					System.out.println("station event is null");
				else if (se.getTime() == null)
					System.out.println("Time is null");
				System.out.println(se.getTime().toString());
				tableObject[ind][0] = se.getTime().toString();
				if (se.getEvent() == TrainEvent.EventType.ARRIVAL)
					tableObject[ind][1] = "ARRIVAL";
				else
					tableObject[ind][1] = "DEPARTURE";
				tableObject[ind][2] = String.format("%d", se.getTrainID());
				ind += 1;
			}

			String[] headers = {"Time", "Action", "Train ID"};
			DefaultTableModel mod = new DefaultTableModel(tableObject, headers)
			{
				public boolean isCellEditable(int rowIndex, int colIndex)
				{
					return false;
				}		
			};
			stationScheduleTable.setModel(mod);
		}
	}                
	
	private void stationScheduleComboChanged(ActionEvent evt)
	{
        JComboBox cb = (JComboBox) evt.getSource();
		String newStation = (String) cb.getSelectedItem();
		generateStationTable(newStation);
	}

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) 
	{                                              
        // TODO add your handling code here:
		System.out.println("This is radio button 2!");
    }                                                                                  

    private void loadScheduleButtonClicked(java.awt.event.ActionEvent evt) 
	{                                          
        // Open schedule file
        javax.swing.JFileChooser openFile = new javax.swing.JFileChooser();
        openFile.showOpenDialog(null);
    }                                         

    private void exportScheduleButtonClicked(ActionEvent evt) 
	{                                         
        // Export schedule file
		javax.swing.JFileChooser sFile = new javax.swing.JFileChooser();
		int returnVal = sFile.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = sFile.getSelectedFile();
			ScheduleWriter sw = new ScheduleWriter(schedule);
			try 
			{
				sw.writeSchedule(file);
			}
			catch (Exception e)
			{
				System.out.println("Error writing file");
			}
		}
    }  
	
	public void setSchedule(LineSchedule ls)
	{
		if (ls != null)
		{
			schedule = ls;
			scheduleRequest = false;
		}
	}
	
	public void setMessage(String s)
	{
		mainMessageLabel.setText(s);
	}
	
	public void setMessage(String s, String panel)
	{
		if (panel.equalsIgnoreCase("throughput"))
			throughputMessageLabel.setText(s);
		else if (panel.equalsIgnoreCase("schedule"))
			scheduleMessageLabel.setText(s);
		else
			mainMessageLabel.setText(s);
	}
	
	public boolean scheduleRequested()
	{
		return scheduleRequest;
	}
	
	public int[] getThroughput()
	{
		return throughput;
	}
	
	public Time getStartTime()
	{
		return start;
	}
	
	public Time getEndTime()
	{
		return end;
	}

//    private class ThroughputPanel {
//        public static javax.swing.JScrollPane newPanel()
//        {
//            javax.swing.JScrollPane panel = new javax.swing.JScrollPane();
//            javax.swing.JTable table = new javax.swing.JTable();
//            return panel;
//        }
//    }
    
    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(MboSchedulerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MboSchedulerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MboSchedulerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MboSchedulerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MboSchedulerUI("Blue").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton enterThroughputButton;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton loadScheduleButton;
    private javax.swing.JButton finishedThroughputButton;
    private javax.swing.JButton generateScheduleButton;
    private javax.swing.JButton viewShceduleButton;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton exportScheduleButton;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> startTimeCombo;
    private javax.swing.JComboBox<String> endTimeCombo;
    private javax.swing.JComboBox<String> stationScheduleCombo;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel individualScheduleLabel;
    private javax.swing.JLabel chooseStationLabel;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel schedulePanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable throughputTable;
    private javax.swing.JTable stationScheduleTable;
    private javax.swing.JTable individualScheduleTable;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
	private JLabel mainMessageLabel;
	private JLabel throughputMessageLabel;
	private JLabel scheduleMessageLabel;
    // End of variables declaration                   
}
