package com.eventmanagement;

import java.awt.Color;
import java.awt.Font;

public final class UIConstants {
    // Colors
    public static final Color PRIMARY_BACKGROUND = Color.decode("#EDF4ED");
    public static final Color SUCCESS = Color.decode("#ABD1B5");
    public static final Color PRIMARY_BUTTON = Color.decode("#197BBD");
    public static final Color INFO_SECONDARY = Color.decode("#6B7FD7");
    public static final Color TEXT = Color.decode("#2E4756");
    public static final Color WARNING = Color.decode("#FF9B71");
    public static final Color DANGER = Color.decode("#4A1942");

    public static final Color BORDER = Color.decode("#E0E0E0");
    public static final Color TABLE_ALT_ROW = Color.decode("#F5F9F5");

    // Fonts (Segoe UI)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.PLAIN, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private UIConstants() {
        // Prevent instantiation
    }
}
