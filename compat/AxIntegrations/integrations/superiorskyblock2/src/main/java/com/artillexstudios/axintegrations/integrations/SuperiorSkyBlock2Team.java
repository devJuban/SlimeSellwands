package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class SuperiorSkyBlock2Team extends TeamIntegration {

    public SuperiorSkyBlock2Team() {
        super("SuperiorSkyBlock2");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Island island = getObject(player);
        if (island == null) return null;
        return island.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Island island = getObject(player);
        if (island == null) return null;
        return island.getOwner().asOfflinePlayer();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Island island = getObject(teamName);
        if (island == null) return null;
        return island.getOwner().asOfflinePlayer();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Island island = getObject(player);
        if (island == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (SuperiorPlayer member : island.getIslandMembers(true)) {
            members.add(member.asOfflinePlayer());
        }
        return members;
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Island island = getObject(teamName);
        if (island == null) return java.util.Collections.emptyList();
        List<OfflinePlayer> members = new ArrayList<>();
        for (SuperiorPlayer member : island.getIslandMembers(true)) {
            members.add(member.asOfflinePlayer());
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Island island = getObject(player);
        if (island == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (SuperiorPlayer member : island.getIslandMembers(true)) {
            if (!member.isOnline()) continue;
            members.add(member.asPlayer());
        }
        return members;
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Island island = getObject(teamName);
        if (island == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (SuperiorPlayer member : island.getIslandMembers(true)) {
            if (!member.isOnline()) continue;
            members.add(member.asPlayer());
        }
        return members;
    }

        private Island getObject(Player player) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (superiorPlayer == null) return null;
        return superiorPlayer.getIsland();
    }

        private Island getObject(String teamName) {
        return SuperiorSkyblockAPI.getIsland(teamName);
    }
}
