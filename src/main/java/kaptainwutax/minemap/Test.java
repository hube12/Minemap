package kaptainwutax.minemap;


import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.ui.component.TabHeader;
import kaptainwutax.minemap.util.ui.graphics.PieChart;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static kaptainwutax.mcutils.state.Dimension.OVERWORLD;

public class Test extends JFrame {
    Group allGroups;
    public Test(){
        JFrame frame = new JFrame("List of Biomes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));

        allGroups= new Group();

        frame.add(allGroups);

        // display it
        frame.pack();
        frame.setLocationRelativeTo(null); // center
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Test test=new Test();
    }


//    static class AllGroups{
//       public void createNewGroup(){
//           this.add(new Group());
//       }
//    }

    static class Group extends JTabbedPane{
        public Group(){
            addTab("1", new Panel());
            addTab("2", new Panel());
            addTab("3", new Panel());
        }

        public void addTab(String title,Panel panel){
            Header header = new Header(title);
            this.setTabComponentAt(this.addTabAndGetIndex(title, panel), header);
        }

        public int addTabAndGetIndex(String title, Component component) {
            super.addTab(title, component);
            return this.getTabCount() - 1;
        }
    }

    static class Header extends JPanel{
        public Header(String title){
            this.add(new JLabel(title));
        }
    }

    static class Panel extends JPanel{
        public Panel(){
        this.repaint();
        }
        @Override
        public void paintComponents(Graphics g) {
            super.paintComponents(g);
            Random random=new Random();
            g.setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()));
            g.fillRect(100,100,100,100);
        }
    }


}
