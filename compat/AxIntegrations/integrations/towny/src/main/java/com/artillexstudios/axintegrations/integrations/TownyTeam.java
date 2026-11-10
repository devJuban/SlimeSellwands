package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownyTeam extends TeamIntegration {
    private TownyAPI api;

    public TownyTeam() {
        super("Towny");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.palmergames.bukkit.towny.TownyAPI");
    }

    @Override
    public boolean setup() {
        api = TownyAPI.getInstance();
        if (api == null) return false;
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Town town = getObject(player);
        if (town == null) return null;
        return town.getName();
    }

    @Override
    public OfflinePlayer getLeader(Player player) {
        Town town = getObject(player);
        if (town == null) return null;
        return Bukkit.getOfflinePlayer(town.getMayor().getUUID());
    }

    @Override
    public OfflinePlayer getLeader(String teamName) {
        Town town = getObject(teamName);
        if (town == null) return null;
        return Bukkit.getOfflinePlayer(town.getMayor().getUUID());
    }

    @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Town town = getObject(player);
        if (town == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (Resident member : town.getResidents()) {
            members.add(Bukkit.getOfflinePlayer(member.getUUID()));
        }
        return members;
    }

    @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Town town = getObject(teamName);
        if (town == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (Resident member : town.getResidents()) {
            members.add(Bukkit.getOfflinePlayer(member.getUUID()));
        }
        return members;
    }

        private Town getObject(Player player) {
        return api.getTown(player);
    }

        private Town getObject(String teamName) {
        return api.getTown(teamName);
    }
}
