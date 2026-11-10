package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import me.ulrich.clans.Clans;
import me.ulrich.clans.api.ClanAPIManager;
import me.ulrich.clans.api.PlayerAPIManager;
import me.ulrich.clans.data.ClanData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UltimateClansTeam extends TeamIntegration {
    private Clans instance;
    private ClanAPIManager clanManager;
    private PlayerAPIManager playerManager;

    public UltimateClansTeam() {
        super("UltimateClans");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.ulrich.clans.Clans");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean setup() {
        instance = (Clans) Bukkit.getPluginManager().getPlugin("UltimateClans");
        if (instance == null) return false;
        clanManager = instance.getClanAPI();
        if (clanManager == null) return false;
        playerManager = instance.getPlayerAPI();
        return playerManager != null;
    }

    @Override
    public String getTeam(Player player) {
        ClanData clanData = getObject(player);
        if (clanData == null) return null;
        return clanData.getTagNoColor();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        ClanData clanData = getObject(player);
        if (clanData == null) return null;
        return Bukkit.getOfflinePlayer(clanData.getLeader());
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        ClanData clanData = getObject(teamName);
        if (clanData == null) return null;
        return Bukkit.getOfflinePlayer(clanData.getLeader());
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        ClanData clanData = getObject(player);
        if (clanData == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID uuid : clanData.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(uuid));
        }
        return members;
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        ClanData clanData = getObject(teamName);
        if (clanData == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID uuid : clanData.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(uuid));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        ClanData clanData = getObject(player);
        if (clanData == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (UUID uuid : clanData.getOnlineMembers()) {
            members.add(Bukkit.getPlayer(uuid));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        ClanData clanData = getObject(teamName);
        if (clanData == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (UUID uuid : clanData.getOnlineMembers()) {
            members.add(Bukkit.getPlayer(uuid));
        }
        return members;
    }

        private ClanData getObject(Player player) {
        return playerManager.getPlayerClan(player.getUniqueId()).orElse(null);
    }

        private ClanData getObject(String teamName) {
        return clanManager.getClanDataByTag(teamName).orElse(null);
    }
}
