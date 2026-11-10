package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class SaberFactionsTeam extends TeamIntegration {
    private Factions factions;
    private FPlayers fPlayers;

    public SaberFactionsTeam() {
        super("SaberFactions");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.massivecraft.factions.Factions");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean setup() {
        factions = Factions.getInstance();
        if (factions == null) return false;
        fPlayers = FPlayers.getInstance();
        return fPlayers != null;
    }

    @Override
    public String getTeam(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return null;
        return faction.getTag();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return null;
        FPlayer fPlayer = faction.getFPlayerLeader();
        if (fPlayer == null) return null;
        return fPlayer.getPlayer();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return null;
        FPlayer fPlayer = faction.getFPlayerLeader();
        if (fPlayer == null) return null;
        return fPlayer.getPlayer();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (FPlayer member : faction.getFPlayers()) {
            members.add(member.getPlayer());
        }
        return members;
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (FPlayer member : faction.getFPlayers()) {
            members.add(member.getPlayer());
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Faction faction = getObject(player);
        if (faction == null) return java.util.Collections.emptyList();
        return new ArrayList<>(faction.getOnlinePlayers());
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Faction faction = getObject(teamName);
        if (faction == null) return java.util.Collections.emptyList();
        return new ArrayList<>(faction.getOnlinePlayers());
    }

        private Faction getObject(Player player) {
        FPlayer fplayer = fPlayers.getByPlayer(player);
        if (!fplayer.hasFaction()) return null;
        return fplayer.getFaction();
    }

        private Faction getObject(String teamName) {
        return factions.getByTag(teamName);
    }
}
