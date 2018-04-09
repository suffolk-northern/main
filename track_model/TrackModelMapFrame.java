/*
 * Roger Xue
 *
 * Track Model UI in visual map form.
 */
package track_model;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TrackModelMapFrame extends javax.swing.JFrame {

    private TrackModelMapPanel tmmp;

    /**
     * Creates new form TrackModelMap
     */
    public TrackModelMapFrame(TrackModel tm) {
        tmmp = new TrackModelMapPanel(tm);
        this.setMinimumSize(new Dimension(600, 600));
        this.setTitle("Track Model Map");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.add(tmmp);
        this.setLocationByPlatform(true);
        this.pack();

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component) e.getSource();
                tmmp.windowResize(c.getSize());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // Do nothing
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // Do nothing
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // Do nothing
            }
        });
    }
}
