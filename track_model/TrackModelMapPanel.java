/*
 * Roger Xue
 *
 * Visual map panel.
 */
package track_model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
    // Frame size.
    private int xDimension = 600;
    private int yDimension = 600;
    // Display margins.
    private int xBound = 20;
    private int yBound = 500;
    // Object images.
    private Image stationArt = null;
    private Image crossingArt = null;
    private Image mainLogoArt = null;

    /**
     * Initializes map panel.
     *
     * @param tm
     */
    public TrackModelMapPanel(TrackModel tm) {
        this.tm = tm;
        //
        // Gets maximum and minimum longitude and latitude of track blocks.
        //
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
        setBounds();
        getImages();
    }

    /**
     * Establishes window bounds and performs calculations on how to manage
     * display dimensions.
     */
    private void setBounds() {
        lonMultiplier = (xDimension - 60) / (maxLon - minLon);
        latMultiplier = (yDimension * 0.866) / (maxLat - minLat);
        xBound = 20;
        yBound = (int) (yDimension * 0.9);
    }

    /**
     * Gets images to put on map.
     */
    private void getImages() {
        try {
            stationArt = ImageIO.read(getClass().getResource("images/station-100px.png"));
            crossingArt = ImageIO.read(getClass().getResource("images/crossing-200px.png"));
            mainLogoArt = ImageIO.read(getClass().getResource("images/logo-2000px.png"));
        } catch (IOException ex) {
            Logger.getLogger(TrackModelMapPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Responds to TrackModelMapFrame resize.
     *
     * @param d
     */
    protected void windowResize(Dimension d) {
        xDimension = d.width;
        yDimension = d.height;
        setBounds();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        plopBackground(g);
        plopStations(g);
        plopCrossings(g);
        plopTrack(g);
//        plopBeacons(g);
        plopTrains(g);
    }

    /**
     * Plop background items.
     *
     * @param g
     */
    private void plopBackground(Graphics g) {
        g.drawImage(mainLogoArt, 20, 20,
                mainLogoArt.getWidth(this) * xDimension / 5000,
                mainLogoArt.getHeight(this) * xDimension / 5000,
                this);
    }

    /**
     * Places track blocks on map.
     *
     * @param g
     */
    private void plopTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (TrackBlock tb : tm.blocks) {
            double xStart = xBound + (tb.start.longitude() - minLon) * lonMultiplier;
            double yStart = yBound - (tb.start.latitude() - minLat) * latMultiplier;
            double xEnd = xBound + (tb.end.longitude() - minLon) * lonMultiplier;
            double yEnd = yBound - (tb.end.latitude() - minLat) * latMultiplier;
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
        int yardCount = 0;
        Graphics2D g2 = (Graphics2D) g;
        for (TrainData td : tm.trains) {
            double x = xBound + (td.trainModel.location().longitude() - minLon) * lonMultiplier;
            double y = yBound - (td.trainModel.location().latitude() - minLat) * latMultiplier;
            g2.setColor(Color.magenta);
            if (td.trackBlock.block == 0) {
                yardCount++;
                g2.fillOval(((int) x - 5) + (yardCount % 5), ((int) y - 5), 10, 10);
            } else {
                g2.fillOval((int) x - 5, (int) y - 5, 10, 10);
                g2.drawString(Integer.toString(td.trainModel.id()), (int) x + 15, (int) y + 15);
            }
        }
    }

    /**
     * Places stations on map.
     *
     * @param g
     */
    private void plopStations(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (Station s : tm.stations) {
            double x = xBound + (s.getLocation().longitude() - minLon) * lonMultiplier;
            double y = yBound - (s.getLocation().latitude() - minLat) * latMultiplier;
            g2.drawImage(stationArt, (int) x - 5, (int) y - 5,
                    stationArt.getWidth(this) * xDimension / 3000,
                    stationArt.getHeight(this) * xDimension / 3000,
                    this);
        }
    }

    /**
     * Places crossings on map.
     *
     * @param g
     */
    private void plopCrossings(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (Crossing c : tm.crossings) {
            TrackBlock tb = tm.getBlock(c.line, c.block);
            double x = xBound + (tb.start.longitude() - minLon) * lonMultiplier;
            double y = yBound - (tb.start.latitude() - minLat) * latMultiplier;
            g2.drawImage(crossingArt, (int) x, (int) y,
                    crossingArt.getWidth(this) * xDimension / 6000,
                    crossingArt.getHeight(this) * xDimension / 6000,
                    this);
        }
    }

    private void plopBeacons(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (Beacon b : tm.beacons) {
            double x = xBound + (b.location.longitude() - minLon) * lonMultiplier;
            double y = yBound - (b.location.latitude() - minLat) * latMultiplier;
            g2.setColor(Color.CYAN);
            g2.fillOval((int) x, (int) y, 5, 5);
        }
    }
}
