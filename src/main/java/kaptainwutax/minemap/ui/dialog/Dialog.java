package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;

import javax.swing.*;

public abstract class Dialog extends JDialog {

	public Dialog(String title) {
		this.setTitle(title);
		this.setAlwaysOnTop(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setLocation(
				MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
				MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
		);
		this.setVisible(false);
	}
}
