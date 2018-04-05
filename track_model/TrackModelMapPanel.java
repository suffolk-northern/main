/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Gowest
 */
public class TrackModelMapPanel extends JPanel {

    private ArrayList<TrackBlock> blocks = new ArrayList<>();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (int i = 1; i <= TrackModel.getBlockCount("Green"); i++) {
            blocks.add(TrackModel.getBlock("Green", i));
        }
        double minLon = 999;
        double minLat = 999;
        double maxLon = -999;
        double maxLat = -999;

        for (TrackBlock tb : blocks) {
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
        double latMultiplier = 600 / (maxLat - minLat);
        double lonMultiplier = 600 / (maxLon - minLon);

        for (TrackBlock tb : blocks) {
            double xStart = 20 + (tb.start.longitude() - minLon) * lonMultiplier;
            double yStart = 640 - (tb.start.latitude() - minLat) * latMultiplier;
            double xEnd = 20 + (tb.end.longitude() - minLon) * lonMultiplier;
            double yEnd = 640 - (tb.end.latitude() - minLat) * latMultiplier;

            g.setColor(Color.green);
            if (tb.isOccupied) {
                g.setColor(Color.black);
                g.setFont(new Font("default", Font.BOLD, 16));
                g.drawString(Integer.toString(tb.block), (int) xStart + 5, (int) yStart + 5);
                g.setColor(Color.red);
            }
            g.drawLine((int) xStart, (int) yStart, (int) xEnd, (int) yEnd);
        }
    }
}
