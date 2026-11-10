package com.artillexstudios.axapi.utils;

import org.bukkit.ChatColor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    private static final Pattern HEX = Pattern.compile("(?i)&#([0-9a-f]{6})");
    private static final Pattern TAG = Pattern.compile("(?i)<(/?)(bold|b|italic|i|underlined|u|strikethrough|st|obfuscated|obf|reset|white|black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|purple|#([0-9a-f]{6}))>");

    private StringUtils() { }
    public static String formatToString(String input) { return format(input, null); }
    public static String formatToString(String input, Map<String, String> replacements) { return format(input, replacements); }
    public static String format(String input) { return format(input, null); }
    public static String format(String input, Map<String, String> replacements) {
        if (input == null) return "";
        if (replacements != null) for (Map.Entry<String, String> entry : replacements.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
        }
        Matcher gradient = Pattern.compile("(?i)<gradient:#([0-9a-f]{6}):#[0-9a-f]{6}>").matcher(input);
        StringBuffer gradientText = new StringBuffer();
        while (gradient.find()) gradient.appendReplacement(gradientText, Matcher.quoteReplacement("<#" + gradient.group(1) + ">"));
        gradient.appendTail(gradientText);
        input = gradientText.toString().replaceAll("(?i)</gradient>", "");
        Matcher hex = HEX.matcher(input);
        StringBuffer colored = new StringBuffer();
        while (hex.find()) hex.appendReplacement(colored, Matcher.quoteReplacement(nearestLegacyColor(hex.group(1))));
        hex.appendTail(colored);
        input = colored.toString();
        Matcher tag = TAG.matcher(input);
        colored = new StringBuffer();
        while (tag.find()) {
            String name = tag.group(2).toLowerCase(java.util.Locale.ROOT);
            String replacement;
            if (!tag.group(1).isEmpty()) replacement = "§r";
            else if (name.startsWith("#")) replacement = nearestLegacyColor(name.substring(1));
            else if (name.equals("b")) replacement = "§l";
            else if (name.equals("i")) replacement = "§o";
            else if (name.equals("u")) replacement = "§n";
            else if (name.equals("st")) replacement = "§m";
            else if (name.equals("obf")) replacement = "§k";
            else {
                ChatColor color = color(name);
                replacement = color == null ? (name.equals("reset") ? "§r" : "") : color.toString();
            }
            tag.appendReplacement(colored, Matcher.quoteReplacement(replacement));
        }
        tag.appendTail(colored);
        return ChatColor.translateAlternateColorCodes('&', colored.toString());
    }

    private static ChatColor color(String name) {
        switch (name) {
            case "white": return ChatColor.WHITE;
            case "black": return ChatColor.BLACK;
            case "dark_blue": return ChatColor.DARK_BLUE;
            case "dark_green": return ChatColor.DARK_GREEN;
            case "dark_aqua": return ChatColor.DARK_AQUA;
            case "dark_red": return ChatColor.DARK_RED;
            case "dark_purple": case "purple": return ChatColor.DARK_PURPLE;
            case "gold": return ChatColor.GOLD;
            case "gray": case "grey": return ChatColor.GRAY;
            case "dark_gray": case "dark_grey": return ChatColor.DARK_GRAY;
            case "blue": return ChatColor.BLUE;
            case "green": return ChatColor.GREEN;
            case "aqua": return ChatColor.AQUA;
            case "red": return ChatColor.RED;
            case "light_purple": return ChatColor.LIGHT_PURPLE;
            case "yellow": return ChatColor.YELLOW;
            default: return null;
        }
    }

    private static String nearestLegacyColor(String hex) {
        int rgb = Integer.parseInt(hex, 16);
        int[] palette = {0x000000,0x0000AA,0x00AA00,0x00AAAA,0xAA0000,0xAA00AA,0xFFAA00,0xAAAAAA,0x555555,0x5555FF,0x55FF55,0x55FFFF,0xFF5555,0xFF55FF,0xFFFF55,0xFFFFFF};
        char[] codes = "0123456789abcdef".toCharArray();
        int best = 0, score = Integer.MAX_VALUE;
        for (int i = 0; i < palette.length; i++) {
            int dr = ((rgb >> 16) & 255) - ((palette[i] >> 16) & 255);
            int dg = ((rgb >> 8) & 255) - ((palette[i] >> 8) & 255);
            int db = (rgb & 255) - (palette[i] & 255);
            int candidate = dr * dr + dg * dg + db * db;
            if (candidate < score) { score = candidate; best = i; }
        }
        return "§" + codes[best];
    }
}
