/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackmodel;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
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
            scanOccupiedBlocks();
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
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();

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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Section", "Block", "Length", "Grade", "Speed Limit", "Occupied", "Heater", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFocusable(false);
        jTable1.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("Blocks", jScrollPane1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Section", "Block", "Length", "Grade", "Speed Limit", "Occupied", "Heater", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setFocusable(false);
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Switches", jScrollPane2);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Section", "Block", "Length", "Grade", "Speed Limit", "Occupied", "Heater", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setFocusable(false);
        jScrollPane3.setViewportView(jTable3);

        jTabbedPane1.addTab("Crossings", jScrollPane3);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Line", "Section", "Block", "Length", "Grade", "Speed Limit", "Occupied", "Heater", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable6.setFocusable(false);
        jScrollPane6.setViewportView(jTable6);

        jTabbedPane1.addTab("Stations", jScrollPane6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 628, Short.MAX_VALUE)
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
                    .addComponent(jButton3))
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
                stat.executeUpdate("DROP TABLE IF EXISTS SWITCHES;");
                stat.executeUpdate("DROP TABLE IF EXISTS CROSSINGS;");
                stat.executeUpdate("DROP TABLE IF EXISTS STATIONS;");
                conn.close();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);
            model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);
            model = (DefaultTableModel) jTable6.getModel();
            model.setRowCount(0);
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
                TestFrame tf = new TestFrame(this);
                tf.setVisible(true);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        MurphFrame mf = new MurphFrame(this, jTable1, jTable3);
        mf.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TrackModelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TrackModelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TrackModelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TrackModelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TrackModelFrame().setVisible(true);
//            }
//        });
//    }
    private void initializeDatabase() {

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS BLOCKS;");
            stat.executeUpdate("DROP TABLE IF EXISTS SWITCHES;");
            stat.executeUpdate("DROP TABLE IF EXISTS CROSSINGS;");
            stat.executeUpdate("DROP TABLE IF EXISTS STATIONS;");

            stat.executeUpdate("CREATE TABLE BLOCKS (\n"
                    + "	id integer,\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	length float,\n"
                    + " curvature float,\n"
                    + "	grade float,\n"
                    + "	direction integer,\n"
                    + "	speed_limit integer,\n"
                    + "	underground boolean,\n"
                    + " power text,\n"
                    + "	occupied boolean,\n"
                    + "	heater boolean,\n"
                    + " message varchar(128),\n"
                    + " PRIMARY KEY (line, id)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE SWITCHES (\n"
                    + "	id integer,\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	section_out_a varchar(1),\n"
                    + "	block_out_a integer,\n"
                    + "	direction_out_a integer,\n"
                    + "	section_out_b varchar(1),\n"
                    + "	block_out_b integer,\n"
                    + "	direction_out_b varchar(1),\n"
                    + " current_setting text,\n"
                    + " PRIMARY KEY (line, id)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE CROSSINGS (\n"
                    + "	id integer,\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	length float,\n"
                    + "	occupied boolean,\n"
                    + "	signal boolean,\n"
                    + " PRIMARY KEY (line, id)\n"
                    + ");");
            stat.executeUpdate("CREATE TABLE STATIONS (\n"
                    + "	id integer,\n"
                    + "	line text NOT NULL,\n"
                    + "	section varchar(1) NOT NULL,\n"
                    + "	name text,\n"
                    + "	passengers integer,\n"
                    + " message varchar(128),\n"
                    + " PRIMARY KEY (line, id)\n"
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
            PreparedStatement blockStmt = conn.prepareStatement("INSERT INTO BLOCKS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement switchStmt = conn.prepareStatement("INSERT INTO SWITCHES VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement crossingStmt = conn.prepareStatement("INSERT INTO CROSSINGS VALUES(?, ?, ?, ?, ?, ?);");
            PreparedStatement stationStmt = conn.prepareStatement("INSERT INTO STATIONS VALUES(?, ?, ?, ?, ?, ?);");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("%")) {
                    continue;
                }
                List<String> items = Arrays.asList(line.split(","));
                blockStmt.setInt(1, Integer.parseInt(items.get(2)));
                blockStmt.setString(2, items.get(0));
                blockStmt.setString(3, items.get(1));
                blockStmt.setFloat(4, Float.parseFloat(items.get(3)));
                blockStmt.setFloat(5, Float.parseFloat(items.get(4)));
                blockStmt.setFloat(6, Float.parseFloat(items.get(5)));
                blockStmt.setInt(7, Integer.parseInt(items.get(6)));
                blockStmt.setInt(8, Integer.parseInt(items.get(7)));
                blockStmt.setBoolean(9, line.contains("UNDERGROUND"));
                blockStmt.setString(10, "GOOD");
                blockStmt.setBoolean(11, false);
                blockStmt.setBoolean(12, false);
                blockStmt.setString(13, "");
                blockStmt.addBatch();
                if (line.contains("SWITCH")) {
                    switchStmt.setInt(1, Integer.parseInt(items.get(2)));
                    switchStmt.setString(2, items.get(0));
                    switchStmt.setString(3, items.get(1));
                    switchStmt.setString(4, items.get(11));
                    switchStmt.setInt(5, Integer.parseInt(items.get(12)));
                    switchStmt.setInt(6, Integer.parseInt(items.get(13)));
                    switchStmt.setString(7, items.get(14));
                    switchStmt.setInt(8, Integer.parseInt(items.get(15)));
                    switchStmt.setInt(9, Integer.parseInt(items.get(16)));
                    switchStmt.addBatch();
                }
                if (line.contains("CROSSING")) {
                    crossingStmt.setInt(1, Integer.parseInt(items.get(2)));
                    crossingStmt.setString(2, items.get(0));
                    crossingStmt.setString(3, items.get(1));
                    crossingStmt.setFloat(4, Float.parseFloat(items.get(3)));
                    crossingStmt.setBoolean(5, false);
                    crossingStmt.setBoolean(6, false);
                    crossingStmt.addBatch();
                }
                if (line.contains("STATION")) {
                    List<String> swag = Arrays.asList(items.get(7).split(";"));
                    stationStmt.setInt(1, Integer.parseInt(items.get(2)));
                    stationStmt.setString(2, items.get(0));
                    stationStmt.setString(3, items.get(1));
                    stationStmt.setString(4, swag.size() > 1 ? swag.get(1).trim() : "");
                    stationStmt.setInt(5, rand.nextInt(20) + 1);
                    stationStmt.setString(6, "");
                    stationStmt.addBatch();
                }
            }
            blockStmt.executeBatch();
            switchStmt.executeBatch();
            crossingStmt.executeBatch();
            stationStmt.executeBatch();
            conn.setAutoCommit(true);
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTables() {
        Object[] columnNames = {"Line", "Section", "Block #", "Length (yd)", "Curvature", "Grade", "Direction", "Speed Limit (mph)", "Underground", "Power", "Occupied", "Heater", "Message"};
        Object[] columnNames2 = {"Line", "Section In", "Block In", "Section Out A", "Block Out A", "Direction Out A", "Section Out B", "Block Out B", "Direction Out B", "Current Setting"};
        Object[] columnNames3 = {"Line", "Section", "Block #", "Length", "Occupied", "Signal"};
        Object[] columnNames4 = {"Line", "Section", "Block #", "Name", "Passengers Embarking", "Beacon Message"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        DefaultTableModel model2 = new DefaultTableModel(columnNames2, 0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        DefaultTableModel model3 = new DefaultTableModel(columnNames3, 0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        DefaultTableModel model4 = new DefaultTableModel(columnNames4, 0) {
            public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };

        jTable1.setModel(model);
        jTable2.setModel(model2);
        jTable3.setModel(model3);
        jTable6.setModel(model4);

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS;");
            while (rs.next()) {
                Object rowData[] = {rs.getString(2), rs.getString(3), rs.getInt(1), rs.getFloat(4), rs.getFloat(5), rs.getFloat(6), rs.getInt(7), rs.getInt(8), rs.getBoolean(9), rs.getString(10), rs.getBoolean(11),
                    (boolean) rs.getBoolean(11) ? "ON" : "OFF", rs.getString(12)};
                model.addRow(rowData);
            }

            rs = stat.executeQuery("SELECT * FROM SWITCHES;");
            while (rs.next()) {
                Object rowData2[] = {rs.getString(2), rs.getString(3), rs.getInt(1), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getInt(8), rs.getString(9), rs.getInt(5)};
                model2.addRow(rowData2);
            }

            rs = stat.executeQuery("SELECT * FROM CROSSINGS;");
            while (rs.next()) {
                Object rowData[] = {rs.getString(2), rs.getString(3), rs.getInt(1), rs.getFloat(4), rs.getBoolean(5), rs.getBoolean(6)};
                model3.addRow(rowData);
            }

            rs = stat.executeQuery("SELECT * FROM STATIONS;");
            while (rs.next()) {
                Object rowData[] = {rs.getString(2), rs.getString(3), rs.getInt(1), rs.getString(4), rs.getInt(5)};
                model4.addRow(rowData);
            }

            rs.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void scanOccupiedBlocks() {
        colorRows(jTable1, 10);
        colorRows(jTable3, 4);
        jTable1.repaint();
        jTable3.repaint();
    }

    public void colorRows(JTable table, int colIndex) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                boolean status = (boolean) table.getModel().getValueAt(row, colIndex);
                boolean outage = colIndex == 4 ? false : table.getModel().getValueAt(row, 8).toString().equalsIgnoreCase("OUTAGE");
                if (status) {
                    setBackground(Color.RED);
                    setForeground(Color.WHITE);
                } else if (outage) {
                    setBackground(Color.ORANGE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
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