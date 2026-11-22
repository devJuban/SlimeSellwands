package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import com.artillexstudios.axparties.api.AxPartiesAPI;
import com.artillexstudios.axparties.party.Party;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class AxParties extends TeamIntegration {

    public AxParties() {
        super("AxParties");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.artillexstudios.axparties.api.AxPartiesAPI");
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
        return party.getOwner();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return null;
        return party.getOwner();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Party party = getObject(player);
        if (party == null) return java.util.Collections.emptyList();
        return new ArrayList<>(party.getMembers());
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return java.util.Collections.emptyList();
        return new ArrayList<>(party.getMembers());
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Party party = getObject(player);
        if (party == null) return java.util.Collections.emptyList();
        return new ArrayList<>(party.getOnlineMembers());
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Party party = getObject(teamName);
        if (party == null) return java.util.Collections.emptyList();
        return new ArrayList<>(party.getOnlineMembers());
    }

        private Party getObject(Player player) {
        return AxPartiesAPI.getPartyOf(player).orElse(null);
    }

        private Party getObject(String teamName) {
        return AxPartiesAPI.getPartyByName(teamName).orElse(null);
    }
}
