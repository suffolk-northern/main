/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Gowest
 */
public class TestFrame extends javax.swing.JFrame {

    private TrackModelFrame tmf;
    private final DbHelper dbHelper;

    /**
     * Creates new form MurphFrame
     */
    public TestFrame(TrackModelFrame tmf, DbHelper dbHelper) {
        initComponents();
        this.tmf = tmf;
        this.dbHelper = dbHelper;
        if (TrackModel.doTablesExist()) {
            populateDropdown();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Track Model Testing");
        setResizable(false);

        jComboBox1.setMaximumRowCount(24);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Block 1", "Block 2", "Block 3", "Block 4", "Block 5" }));

        jButton1.setText("Request Information");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Block:");

        jButton2.setText("Run Train");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Select Station:");

        jComboBox2.setMaximumRowCount(24);
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Block 1", "Block 2", "Block 3", "Block 4", "Block 5" }));

        jButton3.setText("Request Information");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Turn On Heater");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Set Message");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Set Beacon Message");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel3.setText("Select Switch:");

        jComboBox3.setMaximumRowCount(24);
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Block 1", "Block 2", "Block 3", "Block 4", "Block 5" }));

        jButton7.setText("Flip Switch");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Close for Maintenance");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jButton3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6))
                                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String line = jComboBox1.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox1.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        JOptionPane.showMessageDialog(tmf, TrackModel.getBlock(line, Integer.parseInt(block)).toString());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        runTrain();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        getSomething2();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String line = jComboBox1.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox1.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        TrackModel.setHeater(line, Integer.parseInt(block), true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String message = JOptionPane.showInputDialog(this, "Set message for " + jComboBox1.getSelectedItem().toString() + ".");
        if (message != null) {

            String line = jComboBox1.getSelectedItem().toString().split(",")[0];
            line = line.substring(line.lastIndexOf(" "), line.length()).trim();
            String block = jComboBox1.getSelectedItem().toString().split(",")[2];
            block = block.substring(block.lastIndexOf(" "), block.length()).trim();

            TrackModel.setBlockMessage(line, Integer.parseInt(block), message);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String message = JOptionPane.showInputDialog(this, "Set beacon message for " + jComboBox2.getSelectedItem().toString() + ".");
        if (message != null) {

            String line = jComboBox2.getSelectedItem().toString().split(",")[0];
            line = line.substring(line.lastIndexOf(" "), line.length()).trim();
            String block = jComboBox2.getSelectedItem().toString().split(",")[1];
            block = block.substring(block.lastIndexOf(" "), block.length() - 1).trim();

            for (int i = 0; i < tmf.stationTable.getRowCount(); i++) {
                if (tmf.stationTable.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.blockTable.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                    tmf.stationTable.setValueAt(message, i, 5);
                }
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String line = jComboBox3.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox3.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        TrackModel.flipSwitch(line, Integer.parseInt(block));
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String line = jComboBox1.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox1.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        TrackModel.setMaintenance(line, Integer.parseInt(block), true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void populateDropdown() {
        ArrayList<String> blockList = new ArrayList<>();
        try {
            Connection conn = dbHelper.getConnection();
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS;");
            while (rs.next()) {
                blockList.add("Line: " + rs.getString(1) + ", Section: " + rs.getString(2) + ", Block: " + rs.getInt(3));
            }
            jComboBox1.setModel(new DefaultComboBoxModel(blockList.toArray()));

            rs = stat.executeQuery("SELECT * FROM STATIONS;");
            blockList = new ArrayList<>();
            while (rs.next()) {
                blockList.add("Station: " + rs.getString(4) + " (Line: " + rs.getString(1) + ", Block: " + rs.getInt(3) + ")");
            }
            jComboBox2.setModel(new DefaultComboBoxModel(blockList.toArray()));

            rs = stat.executeQuery("SELECT * FROM CONNECTIONS WHERE SWITCH_BLOCK;");
            blockList = new ArrayList<>();
            while (rs.next()) {
                blockList.add("Line: " + rs.getString(1) + ", Section: " + rs.getString(2) + ", Block: " + rs.getInt(3));
            }
            jComboBox3.setModel(new DefaultComboBoxModel(blockList.toArray()));
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TestFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getSomething2() {
        String line = jComboBox2.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox2.getSelectedItem().toString().split(",")[1];
        block = block.substring(block.lastIndexOf(" "), block.length() - 1).trim();
        for (int i = 0; i < tmf.stationTable.getRowCount(); i++) {
            if (tmf.stationTable.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.stationTable.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                JOptionPane.showMessageDialog(tmf,
                        "Line: " + tmf.stationTable.getValueAt(i, 0) + "\n"
                        + "Section: " + tmf.stationTable.getValueAt(i, 1) + "\n"
                        + "Block: " + tmf.stationTable.getValueAt(i, 2) + "\n"
                        + "Name: " + tmf.stationTable.getValueAt(i, 3) + "\n"
                        + "Passengers: " + tmf.stationTable.getValueAt(i, 4) + "\n"
                        + "Message: " + tmf.stationTable.getValueAt(i, 5)
                );
            }
        }
    }

    private final ArrayList<Integer> trainz = new ArrayList<>();

    private void runTrain() {
        ArrayList<Integer> blocks = TrackModel.getDefaultLine("Green");
        trainz.add(0);
        int counter = trainz.size() - 1;
        Timer timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String line = "Green";
                int pos = trainz.get(counter);

                if (pos < blocks.size()) {
                    TrackModel.setOccupancy(line, blocks.get(pos), true);
                }
                if (pos > 0) {
                    TrackModel.setOccupancy(line, blocks.get(pos - 1), false);
                }
                if (pos == blocks.size()) {
                    ((Timer) e.getSource()).stop();
                }
                trainz.set(counter, pos + 1);
            }
        });
        timer.setDelay(50);
        timer.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
