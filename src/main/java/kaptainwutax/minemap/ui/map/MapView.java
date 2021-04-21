package kaptainwutax.minemap.ui.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MapView extends JLayeredPane {

    public final MapLeftSideBar leftBar;
    public final MapRightSideBar rightBar;
    private final MapCanvas canvas;
    public MapView(MapPanel map){
        this.canvas = new MapCanvas(map);
        this.add(this.canvas,JLayeredPane.PALETTE_LAYER);
        this.leftBar = new MapLeftSideBar(map);
        this.rightBar = new MapRightSideBar(map);
        this.add(this.leftBar, JLayeredPane.MODAL_LAYER);
        this.add(this.rightBar, JLayeredPane.MODAL_LAYER);
        this.setMinimumSize(new Dimension(200,200));
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        this.canvas.setSize(d);
        System.out.println("EEE "+d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.canvas.repaint();
    }
}
