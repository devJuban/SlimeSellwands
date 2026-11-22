package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class GangsPlusTeam extends TeamIntegration {

    public GangsPlusTeam() {
        super("GangsPlus");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("net.brcdev.gangs.GangsPlusApi");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Gang gang = getObject(player);
        if (gang == null) return null;
        return gang.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Gang gang = getObject(player);
        if (gang == null) return null;
        return gang.getOwner();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Gang gang = getObject(teamName);
        if (gang == null) return null;
        return gang.getOwner();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Gang gang = getObject(player);
        if (gang == null) return java.util.Collections.emptyList();
        return new ArrayList<>(gang.getAllMembers());
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Gang gang = getObject(teamName);
        if (gang == null) return java.util.Collections.emptyList();
        return new ArrayList<>(gang.getAllMembers());
    }

        private Gang getObject(Player player) {
        return GangsPlusApi.getPlayersGang(player);
    }

        private Gang getObject(String teamName) {
        for (Gang gang : GangsPlusApi.getAllGangs()) {
            if (gang.getName().equals(teamName)) return gang;
        }
        return null;
    }
}
