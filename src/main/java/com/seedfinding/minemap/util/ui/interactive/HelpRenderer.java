package com.seedfinding.minemap.util.ui.interactive;

import com.seedfinding.minemap.MineMap;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders help messages in a JTextPane like a man page: bold section headings, bold coloured key combos and
 * underlined menu paths. Messages look like {@code "Section: some text with Ctrl+F and {About > UI Look}"};
 * text between curly braces is underlined (the braces are dropped), key combos are detected automatically.
 */
public class HelpRenderer {
    private static final Pattern MARKUP = Pattern.compile(
        "\\{([^}]*)}"                                                                  // 1: underlined
            + "|((?:Ctrl|Alt|Shift)(?:\\+(?:NumPad [+\\-]|[A-Za-z0-9]+))+|NumPad [+\\-]|\\bEsc\\b|\\bEnter\\b|\\bUp/Down\\b)" // 2: key
            + "|(/regex/)");                                                           // 3: underlined

    private HelpRenderer() {
    }

    /** Replace the pane content with the messages, headings are only repeated when the section changes. */
    public static void showMessages(JTextPane pane, List<String> messages) {
        StyledDocument doc = reset(pane);
        Palette palette = new Palette(pane);
        String lastSection = null;
        for (String message : messages) {
            int colon = message.indexOf(": ");
            String section = colon > 0 && colon < 20 ? message.substring(0, colon) : null;
            String body = section == null ? message : message.substring(colon + 2);
            if (section != null && !section.equals(lastSection)) {
                append(doc, section.toUpperCase() + "\n", palette.heading());
            }
            lastSection = section;
            appendBody(doc, body, palette);
            append(doc, "\n", palette.body());
        }
        pane.setCaretPosition(0);
    }

    /** Show a single section: a bold heading and a body (same markup as messages). */
    public static void showNotice(JTextPane pane, String heading, String body) {
        showMessages(pane, List.of(heading + ": " + body));
    }

    private static StyledDocument reset(JTextPane pane) {
        pane.setText("");
        return pane.getStyledDocument();
    }

    private static void appendBody(StyledDocument doc, String text, Palette palette) {
        Matcher m = MARKUP.matcher(text);
        int last = 0;
        while (m.find()) {
            if (m.start() > last) append(doc, text.substring(last, m.start()), palette.body());
            if (m.group(1) != null) append(doc, m.group(1), palette.underline());
            else if (m.group(2) != null) append(doc, m.group(2), palette.key());
            else append(doc, m.group(3), palette.underline());
            last = m.end();
        }
        if (last < text.length()) append(doc, text.substring(last), palette.body());
    }

    private static void append(StyledDocument doc, String text, AttributeSet style) {
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ignored) {
            // appending at the end can not be out of bounds
        }
    }

    private static class Palette {
        private final Color fg;
        private final Color headingColor;
        private final Color keyColor;

        Palette(JTextPane pane) {
            boolean dark = MineMap.isDarkTheme();
            this.fg = pane.getForeground();
            this.headingColor = dark ? new Color(0x56B6E0) : new Color(0x0A5C8A);
            this.keyColor = dark ? new Color(0xE5C07B) : new Color(0x9A5B00);
        }

        AttributeSet heading() {
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setBold(s, true);
            StyleConstants.setForeground(s, headingColor);
            return s;
        }

        AttributeSet body() {
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setForeground(s, fg);
            StyleConstants.setLeftIndent(s, 12);
            return s;
        }

        AttributeSet key() {
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setBold(s, true);
            StyleConstants.setForeground(s, keyColor);
            return s;
        }

        AttributeSet underline() {
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setUnderline(s, true);
            StyleConstants.setForeground(s, fg);
            return s;
        }
    }
}
