package com.seedfinding.minemap.util.ui.interactive;

import javax.swing.text.JTextComponent;

public class Prompt {
    public static final String PROMPT = "promptText";

    public static String getPrompt(JTextComponent textComponent) {
        return (String) textComponent.getClientProperty(PROMPT);
    }

    public static void setPrompt(String promptText, JTextComponent textComponent) {
        if (textComponent.getToolTipText() == null || textComponent.getToolTipText().equals(getPrompt(textComponent))) {
            textComponent.setToolTipText(promptText);
        }
        textComponent.putClientProperty(PROMPT, promptText);
        textComponent.repaint();
    }
}
