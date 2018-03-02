/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackmodel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Gowest
 */
public class TestFrame extends javax.swing.JFrame {

    private TrackModelFrame tmf;

    /**
     * Creates new form MurphFrame
     */
    public TestFrame(TrackModelFrame tmf) {
        initComponents();
        this.tmf = tmf;
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addGap(131, 131, 131))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jButton3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6))
                                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        getSomething();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        runTrain();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        getSomething2();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        heat();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String message = JOptionPane.showInputDialog(this, "Set message for " + jComboBox1.getSelectedItem().toString() + ".");
        if (message != null) {

            String line = jComboBox1.getSelectedItem().toString().split(",")[0];
            line = line.substring(line.lastIndexOf(" "), line.length()).trim();
            String block = jComboBox1.getSelectedItem().toString().split(",")[2];
            block = block.substring(block.lastIndexOf(" "), block.length()).trim();

            for (int i = 0; i < tmf.jTable1.getRowCount(); i++) {
                if (tmf.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                    tmf.jTable1.setValueAt(message, i, 11);
                }
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String message = JOptionPane.showInputDialog(this, "Set beacon message for " + jComboBox2.getSelectedItem().toString() + ".");
        if (message != null) {

        String line = jComboBox2.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox2.getSelectedItem().toString().split(",")[1];
        block = block.substring(block.lastIndexOf(" "), block.length() - 1).trim();

            for (int i = 0; i < tmf.jTable6.getRowCount(); i++) {
                if (tmf.jTable6.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                    tmf.jTable6.setValueAt(message, i, 5);
                }
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void populateDropdown() {
        ArrayList<String> blockList = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS;");
            while (rs.next()) {
                blockList.add("Line: " + rs.getString(2) + ", Section: " + rs.getString(3) + ", Block: " + rs.getInt(1));
            }
            jComboBox1.setModel(new DefaultComboBoxModel(blockList.toArray()));
            rs = stat.executeQuery("SELECT * FROM STATIONS;");
            blockList = new ArrayList<>();
            while (rs.next()) {
                blockList.add("Station: " + rs.getString(4) + " (Line: " + rs.getString(2) + ", Block: " + rs.getInt(1) + ")");
            }
            jComboBox2.setModel(new DefaultComboBoxModel(blockList.toArray()));
            rs.close();
            rs.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TestFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getSomething() {
        String line = jComboBox1.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox1.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        for (int i = 0; i < tmf.jTable1.getRowCount(); i++) {
            if (tmf.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                JOptionPane.showMessageDialog(tmf,
                        "Line: " + tmf.jTable1.getValueAt(i, 0) + "\n"
                        + "Section: " + tmf.jTable1.getValueAt(i, 1) + "\n"
                        + "Block: " + tmf.jTable1.getValueAt(i, 2) + "\n"
                        + "Length: " + tmf.jTable1.getValueAt(i, 3) + "\n"
                        + "Grade: " + tmf.jTable1.getValueAt(i, 4) + "\n"
                        + "Speed Limit: " + tmf.jTable1.getValueAt(i, 6) + "\n"
                        + "Underground: " + tmf.jTable1.getValueAt(i, 7) + "\n"
                        + "Power Status: " + tmf.jTable1.getValueAt(i, 8) + "\n"
                        + "Occupied: " + tmf.jTable1.getValueAt(i, 9) + "\n"
                        + "Heater Status: " + tmf.jTable1.getValueAt(i, 10) + "\n"
                        + "Message: " + tmf.jTable1.getValueAt(i, 11) + "\n\n"
                        + "Connected Blocks: "
                );
            }
        }
        tmf.scanOccupiedBlocks();
    }

    private void heat() {
        String line = jComboBox1.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox1.getSelectedItem().toString().split(",")[2];
        block = block.substring(block.lastIndexOf(" "), block.length()).trim();

        for (int i = 0; i < tmf.jTable1.getRowCount(); i++) {
            if (tmf.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                tmf.jTable1.setValueAt("ON", i, 10);
            }
        }
        tmf.scanOccupiedBlocks();
    }

    private void getSomething2() {
        String line = jComboBox2.getSelectedItem().toString().split(",")[0];
        line = line.substring(line.lastIndexOf(" "), line.length()).trim();
        String block = jComboBox2.getSelectedItem().toString().split(",")[1];
        block = block.substring(block.lastIndexOf(" "), block.length() - 1).trim();
        for (int i = 0; i < tmf.jTable6.getRowCount(); i++) {
            if (tmf.jTable6.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable6.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                JOptionPane.showMessageDialog(tmf,
                        "Line: " + tmf.jTable6.getValueAt(i, 0) + "\n"
                        + "Section: " + tmf.jTable6.getValueAt(i, 1) + "\n"
                        + "Block: " + tmf.jTable6.getValueAt(i, 2) + "\n"
                        + "Name: " + tmf.jTable6.getValueAt(i, 3) + "\n"
                        + "Passengers: " + tmf.jTable6.getValueAt(i, 4) + "\n"
                        + "Message: " + tmf.jTable6.getValueAt(i, 5)
                );
            }
        }
        tmf.scanOccupiedBlocks();
    }

    private final ArrayList<Integer> trainz = new ArrayList<>();

    private void runTrain() {
        int[] blocks = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        trainz.add(0);
        int counter = trainz.size() - 1;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String line = "GREEN";
                String block = Integer.toString(blocks[trainz.get(counter) % 12]);

                for (int i = 0; i < tmf.jTable1.getRowCount(); i++) {
                    if (tmf.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                        tmf.jTable1.setValueAt(false, i != 0 ? (i - 1) % 12 : 11, 9);
                        tmf.jTable1.setValueAt(true, i, 9);
                    }
                }
                for (int i = 0; i < tmf.jTable3.getRowCount(); i++) {
                    if (tmf.jTable3.getValueAt(i, 2).toString().equalsIgnoreCase(block) && tmf.jTable3.getValueAt(i, 0).toString().equalsIgnoreCase(line)) {
                        tmf.jTable3.setValueAt(false, i != 0 ? (i - 1) % 12 : 11, 4);
                        tmf.jTable3.setValueAt(false, i != 0 ? (i - 1) % 12 : 11, 5);
                        tmf.jTable3.setValueAt(true, i, 4);
                        tmf.jTable3.setValueAt(true, i, 5);
                    }
                }
                tmf.scanOccupiedBlocks();
                trainz.set(counter, trainz.get(counter) + 1);
            }

        }, 0, 4000);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}