/*
 * Roger Xue
 *
 * Track Model UI in tabular form.
 */
package track_model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
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

    // Database helper.
    private final DbHelper dbHelper;
    // Reference to TrackModel so it can access block data.
    private final TrackModel tm;
    // Map frame.
    private TrackModelMapFrame tmmf;

    // TableModels used for displaying data.
    private TrackModelTableModel blockTableModel = TrackModelTableModel.getBlockTableModel();
    private TrackModelTableModel switchTableModel = TrackModelTableModel.getSwitchTableModel();
    private TrackModelTableModel crossingTableModel = TrackModelTableModel.getCrossingTableModel();
    private TrackModelTableModel stationTableModel = TrackModelTableModel.getStationTableModel();

    /**
     * Creates new form TrackModelFrame
     */
    public TrackModelFrame(TrackModel tm) {
        this.tm = tm;
        this.dbHelper = tm.dbHelper;
        initComponents();

        blockTable.setModel(blockTableModel);
        switchTable.setModel(switchTableModel);
        crossingTable.setModel(crossingTableModel);
        stationTable.setModel(stationTableModel);;

        if (tm.doTablesExist()) {
            refreshTables();
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

        clearButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        murphButton = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        blockScrollPane = new javax.swing.JScrollPane();
        blockTable = new BlockTable();
        switchScrollPane = new javax.swing.JScrollPane();
        switchTable = new SwitchTable();
        crossingScrollPane = new javax.swing.JScrollPane();
        crossingTable = new CrossingTable();
        stationScrollPane = new javax.swing.JScrollPane();
        stationTable = new StationTable();
        occupancyCheckBox = new javax.swing.JCheckBox();
        mapButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Track Model");

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        murphButton.setText("Failure Tests");
        murphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                murphButtonActionPerformed(evt);
            }
        });

        blockScrollPane.setViewportView(blockTable);

        tabbedPane.addTab("Blocks", blockScrollPane);

        switchScrollPane.setViewportView(switchTable);

        tabbedPane.addTab("Switches", switchScrollPane);

        crossingScrollPane.setViewportView(crossingTable);

        tabbedPane.addTab("Crossings", crossingScrollPane);

        stationScrollPane.setViewportView(stationTable);

        tabbedPane.addTab("Stations", stationScrollPane);

        occupancyCheckBox.setText("Show Occupied Blocks Only");
        occupancyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                occupancyCheckBoxActionPerformed(evt);
            }
        });

        mapButton.setText("Launch Visual Track Model");
        mapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mapButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mapButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(murphButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(occupancyCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(importButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(clearButton)
                        .addComponent(importButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(murphButton)
                        .addComponent(mapButton)
                        .addComponent(occupancyCheckBox)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Clears existing tables.
     *
     * @param evt
     */
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Clearing will wipe all existing track data. Continue?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            try {
                Connection conn = dbHelper.getConnection();
                dbHelper.execute(conn, "DROP TABLE IF EXISTS BLOCKS");
                dbHelper.execute(conn, "DROP TABLE IF EXISTS CONNECTIONS");
                dbHelper.execute(conn, "DROP TABLE IF EXISTS CROSSINGS");
                dbHelper.execute(conn, "DROP TABLE IF EXISTS STATIONS");
                conn.close();

                ((DefaultTableModel) blockTable.getModel()).setRowCount(0);
                ((DefaultTableModel) switchTable.getModel()).setRowCount(0);
                ((DefaultTableModel) crossingTable.getModel()).setRowCount(0);
                ((DefaultTableModel) stationTable.getModel()).setRowCount(0);
            } catch (SQLException ex) {
                Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_clearButtonActionPerformed
    /**
     * Launches file chooser to import data file.
     *
     * @param evt
     */
    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        int response = JOptionPane.showConfirmDialog(null, "Importing new track will wipe all existing track data. Continue?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            jfc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                initializeDatabase();
                populateDatabase(jfc.getSelectedFile());
                tm.initializeLocalArrays();
                refreshTables();
            }
        }
    }//GEN-LAST:event_importButtonActionPerformed
    /**
     * Launches Murph Frame when pressed for simulating failures.
     *
     * @param evt
     */
    private void murphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_murphButtonActionPerformed
        MurphFrame mf = new MurphFrame(tm.blocks, dbHelper);
        mf.setLocationRelativeTo(this);
        mf.setVisible(true);
    }//GEN-LAST:event_murphButtonActionPerformed
    /**
     * Choose to display only occupied blocks.
     *
     * @param evt
     */
    private void occupancyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_occupancyCheckBoxActionPerformed
        if (TrackModel.doTablesExist()) {
            refreshTables();
        }
    }//GEN-LAST:event_occupancyCheckBoxActionPerformed
    /**
     * Launches the map UI.
     *
     * @param evt
     */
    private void mapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mapButtonActionPerformed
        tmmf = new TrackModelMapFrame();
        tmmf.add(new TrackModelMapPanel(tm));
        tmmf.setLocationByPlatform(true);
        tmmf.pack();
        tmmf.setVisible(true);
    }//GEN-LAST:event_mapButtonActionPerformed

    /**
     * Initializes the database.
     */
    private void initializeDatabase() {
        try {
            Connection conn = dbHelper.getConnection();
            //
            // Drop old tables if they are there.
            //
            dbHelper.execute(conn, "DROP TABLE IF EXISTS BLOCKS");
            dbHelper.execute(conn, "DROP TABLE IF EXISTS CONNECTIONS");
            dbHelper.execute(conn, "DROP TABLE IF EXISTS CROSSINGS");
            dbHelper.execute(conn, "DROP TABLE IF EXISTS STATIONS");
            //
            // Creates tables.
            //
            dbHelper.execute(conn, "CREATE TABLE BLOCKS (\n"
                    + " line text NOT NULL,\n"
                    + " section varchar(1),\n"
                    + " block integer  NOT NULL,\n"
                    + " length float,\n"
                    + " curvature float,\n"
                    + " grade float,\n"
                    + " speed_limit integer,\n"
                    + " underground boolean,\n"
                    + " x float,\n"
                    + " y float,\n"
                    + " track_controller integer,\n"
                    + " tc_orientation integer,\n"
                    + " xcenter float,\n"
                    + " ycenter float,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            dbHelper.execute(conn, "CREATE TABLE CONNECTIONS (\n"
                    + " line text NOT NULL,\n"
                    + " section varchar(1) NOT NULL,\n"
                    + " block integer,\n"
                    + " prev_block integer,\n"
                    + " prev_valid integer,\n"
                    + " next_block integer,\n"
                    + " next_valid integer,\n"
                    + " switch_block integer,\n"
                    + " switch_valid integer,\n"
                    + " current_setting integer,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            dbHelper.execute(conn, "CREATE TABLE CROSSINGS (\n"
                    + " line text NOT NULL,\n"
                    + " block integer,\n"
                    + " signal boolean,\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            dbHelper.execute(conn, "CREATE TABLE STATIONS (\n"
                    + " line text NOT NULL,\n"
                    + " section varchar(1) NOT NULL,\n"
                    + " block integer,\n"
                    + " name text,\n"
                    + " passengers integer,\n"
                    + " message varchar(128),\n"
                    + " PRIMARY KEY (line, block)\n"
                    + ");");
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Populates tables from a file.
     *
     * @param trackDataFile
     */
    private void populateDatabase(File trackDataFile) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trackDataFile)));
            //
            // PreparedStatements for committing.
            //
            Connection conn = dbHelper.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement blockStmt = conn.prepareStatement("INSERT INTO BLOCKS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement connStmt = conn.prepareStatement("INSERT INTO CONNECTIONS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement crossingStmt = conn.prepareStatement("INSERT INTO CROSSINGS VALUES(?, ?, ?);");
            PreparedStatement stationStmt = conn.prepareStatement("INSERT INTO STATIONS VALUES(?, ?, ?, ?, ?, ?);");
            //
            // Parses through supplid CSV file.
            //
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("%")) {
                    continue;
                }
                List<String> items = Arrays.asList(line.split(","));
                //
                // Sets blocks table contents.
                //
                blockStmt.setString(1, items.get(0));
                blockStmt.setString(2, items.get(1));
                blockStmt.setInt(3, Integer.parseInt(items.get(2)));
                blockStmt.setFloat(4, Float.parseFloat(items.get(3)));
                blockStmt.setFloat(5, Float.parseFloat(items.get(4)));
                blockStmt.setFloat(6, Float.parseFloat(items.get(5)));
                blockStmt.setInt(7, Integer.parseInt(items.get(6)));
                blockStmt.setBoolean(8, line.contains("UNDERGROUND"));
                blockStmt.setFloat(9, Float.parseFloat(items.get(10)));
                blockStmt.setFloat(10, Float.parseFloat(items.get(11)));
                blockStmt.setInt(11, Integer.parseInt(items.get(18)));
                blockStmt.setInt(12, Integer.parseInt(items.get(19)));
                blockStmt.setFloat(13, Float.parseFloat(items.get(20)));
                blockStmt.setFloat(14, Float.parseFloat(items.get(21)));
                blockStmt.addBatch();
                //
                // Sets connection table contents.
                //
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
                //
                // Sets crossing table contents.
                //
                if (line.contains("CROSSING")) {
                    crossingStmt.setString(1, items.get(0));
                    crossingStmt.setInt(2, Integer.parseInt(items.get(2)));
                    crossingStmt.setBoolean(3, false);
                    crossingStmt.addBatch();
                }
                //
                // Sets station table contents.
                //
                if (line.contains("STATION")) {
                    List<String> swag = Arrays.asList(items.get(7).split(";"));
                    stationStmt.setString(1, items.get(0));
                    stationStmt.setString(2, items.get(1));
                    stationStmt.setInt(3, Integer.parseInt(items.get(2)));
                    stationStmt.setString(4, swag.size() > 1 ? swag.get(1).trim() : "");
                    stationStmt.setInt(5, 0);
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
        } catch (SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Refreshes the table data.
     */
    protected void refreshTables() {
        //
        // Refreshes the map UI.
        //
        if (tmmf != null) {
            tmmf.repaint();
        }
        //
        // Prepares new fresh vectors.
        //
        Vector<Vector<Object>> blockVector = new Vector<Vector<Object>>();
        Vector<Vector<Object>> switchVector = new Vector<>();
        Vector<Vector<Object>> crossingVector = new Vector<>();
        Vector<Vector<Object>> stationVector = new Vector<>();
        Vector<Object> row;
        //
        // Populates block vector.
        //
        for (TrackBlock tb : tm.blocks) {
            if (tb.block != 0 && (!occupancyCheckBox.isSelected() || tb.isOccupied)) {
                row = new Vector<>();
                row.add(tb.line.toUpperCase());
                row.add(tb.section);
                row.add(tb.block);
                row.add(tb.length * TrackBlock.METER_TO_YARD_MULTIPLIER);
                row.add(tb.curvature);
                row.add(tb.grade);
                row.add(tb.speedLimit * TrackBlock.KILOMETER_TO_MILE_MULTIPLIER);
                row.add(tb.isUnderground ? "UNDERGROUND" : "");
                row.add(tb.isPowerOn ? "POWER" : "OUTAGE");
                row.add(tb.isOccupied ? "OCCUPIED" : (tb.closedForMaintenance ? "CLOSED" : ""));
                row.add(tb.isHeaterOn ? "ON" : "OFF");
                row.add(tb.message);
                blockVector.add(row);
            }
            //
            // Populates switch vector.
            //
            if (tb.isSwitch) {
                row = new Vector<>();
                row.add(tb.line.toUpperCase());
                row.add(tb.section);
                row.add(tb.block);
                row.add(tb.prevBlockId);
                row.add(tb.prevBlockDir);
                row.add(tb.nextBlockId);
                row.add(tb.nextBlockDir);
                row.add(tb.switchBlockId == 0 ? "YARD" : tb.switchBlockId);
                row.add(tb.switchDirection);
                row.add(tb.switchPosition == 0 ? "YARD" : tb.switchPosition);
                switchVector.add(row);
            }
            //
            // Populates crossing vector.
            //
            if (tb.isCrossing) {
                Crossing c = tm.getCrossing(tb.line, tb.block);
                row = new Vector<>();
                row.add(tb.line.toUpperCase());
                row.add(tb.section);
                row.add(tb.block);
                row.add(tb.length);
                row.add(tb.isOccupied ? "OCCUPIED" : (tb.closedForMaintenance ? "CLOSED" : ""));
                row.add(c.signal ? "ON" : "OFF");
                crossingVector.add(row);
            }
            //
            // Populates station vector.
            //
            if (tb.isStation) {
                Station s = tm.getStation(tb.line, tb.block);
                row = new Vector<>();
                row.add(s.line.toUpperCase());
                row.add(s.section);
                row.add(s.block);
                row.add(s.name);
                row.add(s.passengers);
                stationVector.add(row);
            }
        }
        //
        // Replace existing vector with new vector.
        //
        blockTableModel.setDataVector(blockVector, TrackModelTableModel.getBlockColumnNames());
        switchTableModel.setDataVector(switchVector, TrackModelTableModel.getSwitchColumnNames());
        crossingTableModel.setDataVector(crossingVector, TrackModelTableModel.getCrossingColumnNames());
        stationTableModel.setDataVector(stationVector, TrackModelTableModel.getStationColumnNames());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane blockScrollPane;
    public javax.swing.JTable blockTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JScrollPane crossingScrollPane;
    public javax.swing.JTable crossingTable;
    private javax.swing.JButton importButton;
    private javax.swing.JButton mapButton;
    private javax.swing.JButton murphButton;
    private javax.swing.JCheckBox occupancyCheckBox;
    private javax.swing.JScrollPane stationScrollPane;
    public javax.swing.JTable stationTable;
    private javax.swing.JScrollPane switchScrollPane;
    public javax.swing.JTable switchTable;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
