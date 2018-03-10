/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import track_model.tables.BlockTable;
import track_model.tables.CrossingTable;
import track_model.tables.StationTable;
import track_model.tables.SwitchTable;
import track_model.tables.TrackModelTableModel;

/**
 *
 * @author Gowest
 */
public class TrackModelFrame extends javax.swing.JFrame {

    /**
     * Creates new form TrackModelFrame
     */
    public TrackModelFrame() {
        initComponents();
        if (TrackModel.doTablesExist()) {
            populateTables();
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

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new BlockTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new SwitchTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new CrossingTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new StationTable();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Track Model");

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Import");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Failure Tests");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("Blocks", jScrollPane1);

        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Switches", jScrollPane2);

        jScrollPane3.setViewportView(jTable3);

        jTabbedPane1.addTab("Crossings", jScrollPane3);

        jScrollPane6.setViewportView(jTable6);

        jTabbedPane1.addTab("Stations", jScrollPane6);

        jCheckBox1.setText("Show Occupied Blocks Only");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jCheckBox1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Clearing will wipe all existing track data. Continue?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            try {
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
                Statement stat = conn.createStatement();
                stat.executeUpdate("DROP TABLE IF EXISTS BLOCKS;");
                stat.executeUpdate("DROP TABLE IF EXISTS CONNECTIONS;");
                stat.executeUpdate("DROP TABLE IF EXISTS CROSSINGS;");
                stat.executeUpdate("DROP TABLE IF EXISTS STATIONS;");
                conn.close();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
            ((DefaultTableModel) jTable2.getModel()).setRowCount(0);
            ((DefaultTableModel) jTable3.getModel()).setRowCount(0);
            ((DefaultTableModel) jTable6.getModel()).setRowCount(0);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int response = JOptionPane.showConfirmDialog(null, "Importing new track will wipe all existing track data. Continue?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            jfc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                initializeDatabase();
                populateDatabase(jfc.getSelectedFile());
                populateTables();
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        MurphFrame mf = new MurphFrame(this, jTable1, jTable3);
        mf.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (TrackModel.doTablesExist()) {
            populateTables();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS BLOCKS;");
            stat.executeUpdate("DROP TABLE IF EXISTS CONNECTIONS;");
            stat.executeUpdate("DROP TABLE IF EXISTS CROSSINGS;");
            stat.executeUpdate("DROP TABLE IF EXISTS STATIONS;");

            stat.executeUpdate("CREATE TABLE BLOCKS (\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	block integer,\n"
                    + "	length float,\n"
                    + " curvature float,\n"
                    + "	grade float,\n"
                    + "	speed_limit integer,\n"
                    + "	underground boolean,\n"
                    + " power boolean,\n"
                    + "	occupied boolean,\n"
                    + "	heater boolean,\n"
                    + " message varchar(128),\n"
                    + " x float,\n"
                    + " y float,\n"
                    + " track_controller integer,\n"
                    + " tc_orientation,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE CONNECTIONS (\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	block integer,\n"
                    + "	prev_block integer,\n"
                    + "	prev_valid integer,\n"
                    + "	next_block integer,\n"
                    + "	next_valid integer,\n"
                    + "	switch_block integer,\n"
                    + "	switch_valid integer,\n"
                    + " current_setting integer,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE CROSSINGS (\n"
                    + "	line text NOT NULL,\n"
                    + "	block integer,\n"
                    + "	signal boolean,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE STATIONS (\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	block integer,\n"
                    + "	name text,\n"
                    + "	passengers integer,\n"
                    + " message varchar(128),\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateDatabase(File trackDataFile) {
        try {
            Random rand = new Random();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trackDataFile)));

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            conn.setAutoCommit(false);
            PreparedStatement blockStmt = conn.prepareStatement("INSERT INTO BLOCKS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement connStmt = conn.prepareStatement("INSERT INTO CONNECTIONS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement crossingStmt = conn.prepareStatement("INSERT INTO CROSSINGS VALUES(?, ?, ?);");
            PreparedStatement stationStmt = conn.prepareStatement("INSERT INTO STATIONS VALUES(?, ?, ?, ?, ?, ?);");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("%") || line.contains("Red")) {
                    continue;
                }
                List<String> items = Arrays.asList(line.split(","));
                blockStmt.setString(1, items.get(0));
                blockStmt.setString(2, items.get(1));
                blockStmt.setInt(3, Integer.parseInt(items.get(2)));
                blockStmt.setFloat(4, Float.parseFloat(items.get(3)));
                blockStmt.setFloat(5, Float.parseFloat(items.get(4)));
                blockStmt.setFloat(6, Float.parseFloat(items.get(5)));
                blockStmt.setInt(7, Integer.parseInt(items.get(6)));
                blockStmt.setBoolean(8, line.contains("UNDERGROUND"));
                blockStmt.setBoolean(9, true);
                blockStmt.setBoolean(10, false);
                blockStmt.setBoolean(11, false);
                blockStmt.setString(12, "");
                blockStmt.setFloat(13, Float.parseFloat(items.get(10)));
                blockStmt.setFloat(14, Float.parseFloat(items.get(11)));
                blockStmt.setInt(15, Integer.parseInt(items.get(18)));
                blockStmt.setInt(16, Integer.parseInt(items.get(19)));
                blockStmt.addBatch();

                connStmt.setString(1, items.get(0));
                connStmt.setString(2, items.get(1));
                connStmt.setInt(3, Integer.parseInt(items.get(2)));
                connStmt.setInt(4, Integer.parseInt(items.get(12)));
                connStmt.setInt(5, Integer.parseInt(items.get(13)));
                connStmt.setInt(6, Integer.parseInt(items.get(14)));
                connStmt.setInt(7, Integer.parseInt(items.get(15)));
                if (line.contains("SWITCH")) {
                    connStmt.setInt(8, Integer.parseInt(items.get(16)));
                    connStmt.setInt(9, Integer.parseInt(items.get(17)));
                    connStmt.setInt(10, Integer.parseInt(items.get(16)));
                } else {
                    connStmt.setNull(8, java.sql.Types.INTEGER);
                    connStmt.setNull(9, java.sql.Types.INTEGER);
                    connStmt.setNull(10, java.sql.Types.INTEGER);
                }
                connStmt.addBatch();

                if (line.contains("CROSSING")) {
                    crossingStmt.setString(1, items.get(0));
                    crossingStmt.setInt(2, Integer.parseInt(items.get(2)));
                    crossingStmt.setBoolean(3, false);
                    crossingStmt.addBatch();
                }
                if (line.contains("STATION")) {
                    List<String> swag = Arrays.asList(items.get(7).split(";"));
                    stationStmt.setString(1, items.get(0));
                    stationStmt.setString(2, items.get(1));
                    stationStmt.setInt(3, Integer.parseInt(items.get(2)));
                    stationStmt.setString(4, swag.size() > 1 ? swag.get(1).trim() : "");
                    stationStmt.setInt(5, rand.nextInt(20) + 1);
                    stationStmt.setString(6, "");
                    stationStmt.addBatch();
                }
            }
            blockStmt.executeBatch();
            connStmt.executeBatch();
            crossingStmt.executeBatch();
            stationStmt.executeBatch();
            conn.setAutoCommit(true);
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void populateTables() {

        TrackModelTableModel blockTableModel = TrackModelTableModel.getBlockTableModel();
        TrackModelTableModel switchTableModel = TrackModelTableModel.getSwitchTableModel();
        TrackModelTableModel crossingTableModel = TrackModelTableModel.getCrossingTableModel();
        TrackModelTableModel stationTableModel = TrackModelTableModel.getStationTableModel();

//        jTable1 = new TrackModelTable();
        jTable1.setModel(blockTableModel);
        jTable2.setModel(switchTableModel);
        jTable3.setModel(crossingTableModel);
        jTable6.setModel(stationTableModel);

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = jCheckBox1.isSelected()
                    ? stat.executeQuery("SELECT * FROM BLOCKS WHERE OCCUPIED")
                    : stat.executeQuery("SELECT * FROM BLOCKS");
            while (rs.next()) {
                Object rowData[] = {
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getDouble(4) * TrackBlock.METER_TO_YARD_MULTIPLIER,
                    rs.getDouble(5),
                    rs.getDouble(6),
                    rs.getInt(7) * TrackBlock.KILOMETER_TO_MILE_MULTIPLIER,
                    rs.getBoolean(8) ? "UNDERGROUND" : "",
                    rs.getBoolean(9) ? "POWER" : "OUTAGE",
                    rs.getBoolean(10) ? "OCCUPIED" : "",
                    rs.getBoolean(11) ? "ON" : "OFF",
                    rs.getString(12)
                };
                blockTableModel.addRow(rowData);
            }

            rs = stat.executeQuery("SELECT * FROM CONNECTIONS WHERE SWITCH_BLOCK;");
            while (rs.next()) {
                Object rowData2[] = {
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4),
                    rs.getInt(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8) == -1 ? "YARD" : rs.getInt(8),
                    rs.getInt(9),
                    rs.getInt(10) == -1 ? "YARD" : rs.getInt(10)
                };
                switchTableModel.addRow(rowData2);
            }

            rs = stat.executeQuery("SELECT * FROM CROSSINGS NATURAL JOIN BLOCKS;");
            while (rs.next()) {
                Object rowData[] = {
                    rs.getString(1),
                    rs.getString(4),
                    rs.getInt(2),
                    rs.getDouble(5),
                    rs.getBoolean(11) ? "OCCUPIED" : "",
                    rs.getBoolean(3) ? "ON" : "OFF"
                };
                crossingTableModel.addRow(rowData);
            }

            rs = stat.executeQuery("SELECT * FROM STATIONS;");
            while (rs.next()) {
                Object rowData[] = {
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getString(4),
                    rs.getInt(5)
                };
                stationTableModel.addRow(rowData);
            }

            rs.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTable jTable1;
    public javax.swing.JTable jTable2;
    public javax.swing.JTable jTable3;
    public javax.swing.JTable jTable6;
    // End of variables declaration//GEN-END:variables
}
