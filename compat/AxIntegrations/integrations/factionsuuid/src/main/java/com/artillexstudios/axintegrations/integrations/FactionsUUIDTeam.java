package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import dev.kitteh.factions.FPlayer;
import dev.kitteh.factions.FPlayers;
import dev.kitteh.factions.Faction;
import dev.kitteh.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionsUUIDTeam extends TeamIntegration {

    public FactionsUUIDTeam() {
        super("FactionsUUID");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("dev.kitteh.factions.FPlayer");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return null;
        return faction.tag();
    }

    @Override
    public OfflinePlayer getLeader(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return null;
        FPlayer admin = faction.admin();
        if (admin == null) return null;
        return Bukkit.getOfflinePlayer(admin.uniqueId());
    }

    @Override
    public OfflinePlayer getLeader(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return null;
        FPlayer admin = faction.admin();
        if (admin == null) return null;
        return Bukkit.getOfflinePlayer(admin.uniqueId());
    }

    @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (FPlayer member : faction.members()) {
            members.add(Bukkit.getOfflinePlayer(member.uniqueId()));
        }
        return members;
    }

    @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (FPlayer member : faction.members()) {
            members.add(Bukkit.getOfflinePlayer(member.uniqueId()));
        }
        return members;
    }

    @Override
    public List<Player> getOnlineMembers(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return java.util.Collections.emptyList();
        return new ArrayList<>(faction.membersOnlineAsPlayers());
    }

    @Override
    public List<Player> getOnlineMembers(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return java.util.Collections.emptyList();
        return new ArrayList<>(faction.membersOnlineAsPlayers());
    }

        private Faction getObject(Player player) {
        FPlayer fPlayer = FPlayers.fPlayers().get(player);
        if (!fPlayer.hasFaction()) return null;
        return fPlayer.faction();
    }

        private Faction getObject(String teamName) {
        return Factions.factions().get(teamName);
    }
}
