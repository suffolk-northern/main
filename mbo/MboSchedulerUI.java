/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbo;

import mbo.schedules.ScheduleWriter;
import mbo.schedules.LineSchedule;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
/**
 *
 * @author Fenne
 */
public class MboSchedulerUI extends javax.swing.JFrame {
	
	private LineSchedule schedule;
	
    /**
     * Creates new form Scheduler
     */
    public MboSchedulerUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Create New Schedule");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(110, 80, 180, 40);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8:00", "9:00", "10:00", "11:00", "12:00" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox1);
        jComboBox1.setBounds(200, 150, 70, 22);

        jLabel2.setText("Start Time");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(130, 150, 61, 16);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "17:00", "18:00", "19:00", "20:00" }));
        jPanel1.add(jComboBox2);
        jComboBox2.setBounds(200, 180, 70, 22);

        jLabel3.setText("End Time");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(130, 180, 54, 16);

        jButton1.setText("Input Throughput");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(130, 230, 131, 25);

        jButton3.setText("Generate Schedule");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(120, 290, 141, 25);
        jPanel1.add(jProgressBar1);
        jProgressBar1.setBounds(110, 330, 157, 14);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(120, 270, 141, 10);

        jButton4.setText("View Schedule");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);
        jButton4.setBounds(50, 360, 117, 25);

        jButton6.setText("Export Schedule");
        jButton6.setToolTipText("");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6);
        jButton6.setBounds(200, 360, 130, 25);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Automatic Train Dispatch");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(500, 80, 210, 40);

        jRadioButton1.setText("Enabled");
        jPanel1.add(jRadioButton1);
        jRadioButton1.setBounds(510, 120, 80, 25);

        jRadioButton2.setText("Disabled");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jRadioButton2);
        jRadioButton2.setBounds(610, 120, 77, 25);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1);
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

        jPanel1.add(jScrollPane5);
        jScrollPane5.setBounds(400, 210, 410, 180);

        jButton12.setText("Select Schedule File");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton12);
        jButton12.setBounds(420, 170, 160, 25);

        jButton13.setText("Open MBO Controller");
        jPanel1.add(jButton13);
        jButton13.setBounds(260, 440, 310, 25);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setText("MBO Overlay: Green Line");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(290, 20, 290, 29);

        getContentPane().add(jPanel1, "card2");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"8:00", "300"},
                {"9:00", "500"},
                {"10:00", "200"},
                {"11:00", "300"},
                {"12:00", "300"},
                {"13:00", "300"},
                {"14:00", "300"},
                {"15:00", "400"},
                {"16:00", "400"},
                {"17:00", "100"}
            },
            new String [] {
                "Hour", "Throughput"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("");
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Enter Throughput");

        jButton2.setText("Return to Scheduler");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                        .addComponent(jButton2)
                        .addGap(122, 122, 122))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap(189, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, "card3");

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"8:00:00", "Departure", "1", "3"},
                {"8:20:00", "Departure", "2", "4"},
                {"8:40:00", "Departure", "3", "5"},
                {"9:00:00", "Departure", "4", "6"},
                {"9:10:00", "Arrival", "1", "3"},
                {"9:15:00", "Departure", "5", "1"},
                {"9:30:00", "Departure", "1", "3"},
                {"9:45:00", "Arrival", "2", "4"}
            },
            new String [] {
                "Time", "Action", "Train ID", "Driver ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setToolTipText("");
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 370, 280));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Yard", "Station_1", "Station_2", "Station_3" }));
        jPanel2.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Overall Schedule");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, -1, -1));

        jButton5.setText("Return To Main Screen");
        jButton5.setToolTipText("");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 560, -1, -1));

        jLabel6.setText("Click a train or employee ID to see their schedule");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 400, -1, -1));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
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
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setToolTipText("");
        jScrollPane3.setViewportView(jTable3);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 380, 180));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Driver 4 Schedule");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, -1, -1));

        jLabel8.setText("Delay selected by:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 280, -1, -1));

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM))));
        jPanel2.add(jFormattedTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 280, 110, -1));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 400, 500, -1));

        jButton7.setText("Confirm");
        jPanel2.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 510, -1, -1));

        jButton8.setText("Undo");
        jPanel2.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 510, -1, -1));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pioneer", "Edgebrook", "Whited" }));
        jPanel2.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 320, -1, -1));

        jLabel9.setText("Edit station:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 320, -1, -1));

        jButton9.setText("Add");
        jPanel2.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 320, -1, -1));

        jButton10.setText("Remove");
        jPanel2.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 320, -1, -1));

        jButton11.setText("Remove this driver from schedule");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 360, -1, -1));

        jLabel10.setText("minutes");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 280, -1, -1));

        getContentPane().add(jPanel2, "card4");

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Enter throughput button in Panel1
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card3");
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Return to scheduler button in Panel3
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card2");       
    }                                        

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         

    }                                        

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // View schedule button in Panel1
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card4");       
    }                                        

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Return to scheduler button in Panel2
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "card2");              
    }                                        

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
        // Delete
        
    }                                           

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // Start time drop down in panel1
        JComboBox cb = (JComboBox)evt.getSource();
        String startTime = (String)cb.getSelectedItem();
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"9:00", "500"},
                {"10:00", "200"},
                {"11:00", "300"},
                {"12:00", "300"},
                {"13:00", "300"},
                {"14:00", "300"},
                {"15:00", "400"},
                {"16:00", "400"},
                {"17:00", "100"}
            },
            new String [] {
                "Hour", "Throughput"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }                                          

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Open schedule file
        javax.swing.JFileChooser openFile = new javax.swing.JFileChooser();
        openFile.showOpenDialog(null);
    }                                         

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
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
                new MboSchedulerUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration                   
}
