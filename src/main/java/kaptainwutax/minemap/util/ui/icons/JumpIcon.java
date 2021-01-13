package kaptainwutax.minemap.util.ui.icons;

import java.awt.*;

public class JumpIcon extends ButtonIcon {


    public JumpIcon(int size, int inset, float factor) {
        super(size,inset,factor,true,new Color(151, 167, 152,200), JumpIcon.class);
    }
    public JumpIcon() {
        super(JumpIcon.class);
    }
    @Override
    public void changeBColor(Color color) {
        super.changeBColor(color);
    }
}
