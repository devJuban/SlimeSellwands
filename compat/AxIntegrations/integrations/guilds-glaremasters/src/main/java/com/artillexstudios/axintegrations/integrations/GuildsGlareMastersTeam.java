package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import me.glaremasters.guilds.Guilds;
import me.glaremasters.guilds.api.GuildsAPI;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class GuildsGlareMastersTeam extends TeamIntegration {
    private GuildsAPI api;

    public GuildsGlareMastersTeam() {
        super("Guilds");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.glaremasters.guilds.Guilds");
    }

    @Override
    public boolean setup() {
        api = Guilds.getApi();
        return api != null;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Guild guild = getObject(player);
        if (guild == null) return null;
        return guild.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        Guild guild = getObject(player);
        if (guild == null) return null;
        return guild.getGuildMaster().getAsOfflinePlayer();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        Guild guild = getObject(teamName);
        if (guild == null) return null;
        return guild.getGuildMaster().getAsOfflinePlayer();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        Guild guild = getObject(player);
        if (guild == null) return java.util.Collections.emptyList();
        return new ArrayList<>(guild.getAllAsPlayers());
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        Guild guild = getObject(teamName);
        if (guild == null) return java.util.Collections.emptyList();
        return new ArrayList<>(guild.getAllAsPlayers());
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        Guild guild = getObject(player);
        if (guild == null) return java.util.Collections.emptyList();
        return new ArrayList<>(guild.getOnlineAsPlayers());
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        Guild guild = getObject(teamName);
        if (guild == null) return java.util.Collections.emptyList();
        return new ArrayList<>(guild.getOnlineAsPlayers());
    }

        private Guild getObject(Player player) {
        return api.getGuildByPlayerId(player.getUniqueId());
    }

        private Guild getObject(String teamName) {
        return api.getGuild(teamName);
    }
}
