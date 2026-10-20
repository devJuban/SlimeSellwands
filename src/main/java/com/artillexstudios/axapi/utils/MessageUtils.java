package com.artillexstudios.axapi.utils;

import com.artillexstudios.axapi.config.Config;
import org.bukkit.command.CommandSender;

import java.util.Map;

public final class MessageUtils {
    private final Config messages;
    private final Config config;

    public MessageUtils(Config messages, Config config) {
        this.messages = messages;
        this.config = config;
    }

    public void sendLang(CommandSender sender, String key) { sendLang(sender, key, null); }

    public void sendLang(CommandSender sender, String key, Map<String, String> replacements) {
        String text = messages.getString(key, "");
        String prefix = config.getString("prefix", "");
        sender.sendMessage(StringUtils.format(prefix + text, replacements));
    }

    public void sendFormatted(CommandSender sender, String key, Map<String, String> replacements) {
        sendLang(sender, key, replacements);
    }
}
