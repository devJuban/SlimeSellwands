package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleClansTeam extends TeamIntegration {
    private SimpleClans instance;
    private ClanManager manager;

    public SimpleClansTeam() {
        super("SimpleClans");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager");
    }

    @Override
    public boolean setup() {
        instance = SimpleClans.getInstance();
        if (instance == null) return false;
        manager = SimpleClans.getInstance().getClanManager();
        return manager != null;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Clan clan = getObject(player);
        if (clan == null) return null;
        return clan.getTag();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Clan clan = getObject(player);
        if (clan == null) return null;
        for (ClanPlayer leader : clan.getLeaders()) {
            return Bukkit.getOfflinePlayer(leader.getUniqueId());
        }
        return null;
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Clan clan = getObject(teamName);
        if (clan == null) return null;
        for (ClanPlayer leader : clan.getLeaders()) {
            return Bukkit.getOfflinePlayer(leader.getUniqueId());
        }
        return null;
    }

    @Override
    public boolean isLeader(Player player) {
        Clan clan = getObject(player);
        if (clan == null) return false;
        return clan.isLeader(player);
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Clan clan = getObject(player);
        if (clan == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (ClanPlayer member : clan.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(member.getUniqueId()));
        }
        return members;
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Clan clan = getObject(teamName);
        if (clan == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (ClanPlayer member : clan.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(member.getUniqueId()));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Clan clan = getObject(player);
        if (clan == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (ClanPlayer member : clan.getMembers()) {
            members.add(Bukkit.getPlayer(member.getUniqueId()));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Clan clan = getObject(teamName);
        if (clan == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (ClanPlayer member : clan.getMembers()) {
            members.add(Bukkit.getPlayer(member.getUniqueId()));
        }
        return members;
    }

        private Clan getObject(Player player) {
        ClanPlayer clanPlayer = manager.getClanPlayer(player);
        if (clanPlayer == null) return null;
        return clanPlayer.getClan();
    }

        private Clan getObject(String teamName) {
        return manager.getClan(teamName);
    }
}
