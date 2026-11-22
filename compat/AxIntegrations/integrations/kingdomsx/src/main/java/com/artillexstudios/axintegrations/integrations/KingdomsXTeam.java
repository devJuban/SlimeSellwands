package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KingdomsXTeam extends TeamIntegration {

    public KingdomsXTeam() {
        super("KingdomsX");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("org.kingdoms.constants.group.Kingdom");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Kingdom kingdom = getObject(player);
        if (kingdom == null) return null;
        return kingdom.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Kingdom kingdom = getObject(player);
        if (kingdom == null) return null;
        UUID owner = kingdom.getOwnerId();
        if (owner == null) return null;
        return Bukkit.getOfflinePlayer(owner);
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Kingdom kingdom = getObject(teamName);
        if (kingdom == null) return null;
        UUID owner = kingdom.getOwnerId();
        if (owner == null) return null;
        return Bukkit.getOfflinePlayer(owner);
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Kingdom kingdom = getObject(player);
        if (kingdom == null) return java.util.Collections.emptyList();
        return new ArrayList<>(kingdom.getPlayerMembers());
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Kingdom kingdom = getObject(teamName);
        if (kingdom == null) return java.util.Collections.emptyList();
        return new ArrayList<>(kingdom.getPlayerMembers());
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Kingdom kingdom = getObject(player);
        if (kingdom == null) return java.util.Collections.emptyList();
        return new ArrayList<>(kingdom.getOnlineMembers());
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Kingdom kingdom = getObject(teamName);
        if (kingdom == null) return java.util.Collections.emptyList();
        return new ArrayList<>(kingdom.getOnlineMembers());
    }

        private Kingdom getObject(Player player) {
        KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
        if (kingdomPlayer == null) return null;
        return kingdomPlayer.getKingdom();
    }

        private Kingdom getObject(String teamName) {
        return Kingdom.getKingdom(teamName);
    }
}
