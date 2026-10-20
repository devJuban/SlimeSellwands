package com.artillexstudios.axsellwands.commands;

import com.artillexstudios.axsellwands.AxSellwands;
import com.artillexstudios.axsellwands.sellwands.Sellwands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommandManager implements org.bukkit.command.CommandExecutor, TabCompleter {
    private static final CommandManager INSTANCE = new CommandManager();
    private CommandManager() { }

    public static void load() {
        PluginCommand command = AxSellwands.getInstance().getCommand("axsellwands");
        if (command == null) {
            AxSellwands.getInstance().getLogger().severe("Command axsellwands is missing from plugin.yml");
            return;
        }
        command.setExecutor(INSTANCE);
        command.setTabCompleter(INSTANCE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("axsellwands.admin")) {
            AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.no-permission");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            com.artillexstudios.axsellwands.commands.subcommands.Help.INSTANCE.execute(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (args.length != 1) { AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.invalid-command"); return true; }
            com.artillexstudios.axsellwands.commands.subcommands.Reload.INSTANCE.execute(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3 || args.length > 4) { AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.missing-argument"); return true; }
            Player player = Bukkit.getPlayerExact(args[1]);
            if (player == null) {
                AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.invalid-player", java.util.Collections.singletonMap("%player%", args[1]));
                return true;
            }
            com.artillexstudios.axsellwands.sellwands.Sellwand wand = Sellwands.getSellwands().get(args[2]);
            if (wand == null) {
                AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.invalid-value", java.util.Collections.singletonMap("%value%", args[2]));
                return true;
            }
            Integer amount = null;
            if (args.length == 4) {
                try { amount = Integer.parseInt(args[3]); }
                catch (NumberFormatException ex) {
                    AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.invalid-value", java.util.Collections.singletonMap("%value%", args[3]));
                    return true;
                }
                if (amount < 1 || amount > 64) {
                    AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.out-of-range", java.util.Collections.singletonMap("%number%", args[3]));
                    return true;
                }
            }
            com.artillexstudios.axsellwands.commands.subcommands.Give.INSTANCE.execute(sender, player, wand, amount);
            return true;
        }
        AxSellwands.MESSAGEUTILS.sendLang(sender, "commands.invalid-command");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) options.addAll(Arrays.asList("help", "give", "reload"));
        else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) options.add(player.getName());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) options.addAll(Sellwands.getSellwands().keySet());
        String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(java.util.Locale.ROOT);
        List<String> filtered = new ArrayList<>();
        for (String option : options) if (option.toLowerCase(java.util.Locale.ROOT).startsWith(prefix)) filtered.add(option);
        return filtered;
    }
}
