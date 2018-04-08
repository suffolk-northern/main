/*
 * Roger Xue
 *
 * Visual map panel.
 */
package track_model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import track_model.TrackModel.TrainData;

public class TrackModelMapPanel extends JPanel {

    // Access to TrackModel objects.
    private TrackModel tm;
    // Variables used for maintaining map boundaries.
    private double minLon = 999;
    private double minLat = 999;
    private double maxLon = -999;
    private double maxLat = -999;
    private double lonMultiplier = 0;
    private double latMultiplier = 0;
    // Display margins.
    private final int X_BOUND = 20;
    private final int Y_BOUND = 640;

    /**
     * Initializes map panel.
     *
     * @param tm
     */
    public TrackModelMapPanel(TrackModel tm) {
        this.tm = tm;
        setBounds();
    }

    /**
     * Establishes window bounds.
     */
    private void setBounds() {
        for (TrackBlock tb : tm.blocks) {
            if (tb.start.latitude() > maxLat) {
                maxLat = tb.start.latitude();
            }
            if (tb.start.longitude() > maxLon) {
                maxLon = tb.start.longitude();
            }
            if (tb.start.latitude() < minLat) {
                minLat = tb.start.latitude();
            }
            if (tb.start.longitude() < minLon) {
                minLon = tb.start.longitude();
            }
        }
        latMultiplier = 600 / (maxLat - minLat);
        lonMultiplier = 600 / (maxLon - minLon);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        plopTrack(g);
        plopTrains(g);
    }

    /**
     * Places track blocks on map.
     *
     * @param g
     */
    private void plopTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (TrackBlock tb : tm.blocks) {
            double xStart = X_BOUND + (tb.start.longitude() - minLon) * lonMultiplier;
            double yStart = Y_BOUND - (tb.start.latitude() - minLat) * latMultiplier;
            double xEnd = X_BOUND + (tb.end.longitude() - minLon) * lonMultiplier;
            double yEnd = Y_BOUND - (tb.end.latitude() - minLat) * latMultiplier;
            //
            // Sets color of track block.
            // 
            g2.setStroke(new BasicStroke(0));
            if (tb.line.equalsIgnoreCase("green")) {
                g2.setColor(Color.green);
            } else {
                g2.setColor(Color.red);
            }
            //
            // Boldens occupied track blocks.
            //
            if (tb.isOccupied && tb.block != 0) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.black);
                g2.setFont(new Font("default", Font.BOLD, 16));
                g2.drawString(Integer.toString(tb.block), (int) xStart + 5, (int) yStart + 5);
            }
            g.drawLine((int) xStart, (int) yStart, (int) xEnd, (int) yEnd);
        }
    }

    /**
     * Places trains on map.
     *
     * @param g
     */
    private void plopTrains(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (TrainData td : tm.trains) {
            double x = X_BOUND + (td.trainModel.location().longitude() - minLon) * lonMultiplier;
            double y = Y_BOUND - (td.trainModel.location().latitude() - minLat) * latMultiplier;
            g2.setColor(Color.magenta);
            g2.fillOval((int) x - 5, (int) y - 5, 10, 10);
            g2.drawString(Integer.toString(td.trainModel.id()), (int) x + 15, (int) y + 15);
        }
    }
}