package com.artillexstudios.axintegrations.integrations;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartiesTeam extends TeamIntegration {
    private PartiesAPI api;

    public PartiesTeam() {
        super("Parties");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.alessiodp.parties.api.interfaces.PartiesAPI");
    }

    @Override
    public boolean setup() {
        api = com.alessiodp.parties.api.Parties.getApi();
        return api != null;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Party party = getObject(player);
        if (party == null) return null;
        return party.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Party party = getObject(player);
        if (party == null) return null;
        UUID leader = party.getLeader();
        if (leader == null) return null;
        return Bukkit.getOfflinePlayer(leader);
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return null;
        UUID leader = party.getLeader();
        if (leader == null) return null;
        return Bukkit.getOfflinePlayer(leader);
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Party party = getObject(player);
        if (party == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID uuid : party.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(uuid));
        }
        return members;
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID uuid : party.getMembers()) {
            members.add(Bukkit.getOfflinePlayer(uuid));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Party party = getObject(player);
        if (party == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (PartyPlayer member : party.getOnlineMembers()) {
            members.add(Bukkit.getPlayer(member.getPlayerUUID()));
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (PartyPlayer member : party.getOnlineMembers()) {
            members.add(Bukkit.getPlayer(member.getPlayerUUID()));
        }
        return members;
    }

        private Party getObject(Player player) {
        return api.getPartyOfPlayer(player.getUniqueId());
    }

        private Party getObject(String teamName) {
        return api.getParty(teamName);
    }
}
