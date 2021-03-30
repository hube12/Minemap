package kaptainwutax.minemap;

import com.formdev.flatlaf.FlatDarkLaf;
import kaptainwutax.minemap.init.*;
import kaptainwutax.minemap.ui.MenuBar;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.dialog.Dialog;
import wearblackallday.swing.components.builder.FrameBuilder;

public class MineMap {

	public static final WorldTabs WORLD_TABS;
	public static final Dialog COORD_HOPPER_DIALOG;
	public static final Dialog ENTER_SEED_DIALOG;
	public static final Dialog RENAME_TAB_DIALOG;
	public static final Dialog SALT_DIALOG;

	static {
		Features.registerFeatures();
		Icons.registerIcons();
		Configs.registerConfigs();
		FlatDarkLaf.install();
		WORLD_TABS = new WorldTabs();
		COORD_HOPPER_DIALOG = new Dialog.CoordHopperDialogue();
		ENTER_SEED_DIALOG = new Dialog.EnterSeedDialog();
		RENAME_TAB_DIALOG = new Dialog.RenameTabDialog();
		SALT_DIALOG = new Dialog.SaltDialog();
	}

	public static void main(String[] args) {
		FrameBuilder.newBuilder().visible().fullScreen().contentPane(WORLD_TABS).menu(MenuBar.create()).create();
	}
}
