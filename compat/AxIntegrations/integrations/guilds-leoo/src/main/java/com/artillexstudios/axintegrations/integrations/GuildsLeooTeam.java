package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.TeamIntegration;
import me.leoo.guilds.api.objects.GuildAPI;
import me.leoo.guilds.api.objects.guild.GuildProvider;
import me.leoo.guilds.api.objects.guild.GuildView;
import me.leoo.guilds.api.objects.player.PlayerView;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuildsLeooTeam extends TeamIntegration {
    private GuildAPI api;
    private GuildProvider provider;

    public GuildsLeooTeam() {
        super("Guilds");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.leoo.guilds.api.objects.GuildAPI");
    }

    @Override
    public boolean setup() {
        api = GuildAPI.get();
        if (api == null) return false;
        provider = api.getGuildProvider();
        return provider != null;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        GuildView guild = getObject(player);
        if (guild == null) return null;
        return guild.getName();
    }

        @Override
    public OfflinePlayer getLeader(Player player) {
        GuildView guild = getObject(player);
        if (guild == null) return null;
        return guild.getLeaderPlayer().getOfflinePlayer();
    }

        @Override
    public OfflinePlayer getLeader(String teamName) {
        GuildView guild = getObject(teamName);
        if (guild == null) return null;
        return guild.getLeaderPlayer().getOfflinePlayer();
    }

        @Override
    public List<OfflinePlayer> getMembers(Player player) {
        GuildView guild = getObject(player);
        if (guild == null) return java.util.Collections.emptyList();
        Set<OfflinePlayer> members = new HashSet<>();
        for (PlayerView member : guild.getOfflinePlayers()) {
            members.add(member.getOfflinePlayer());
        }
        for (PlayerView member : guild.getOnlinePlayers()) {
            members.add(member.getOfflinePlayer());
        }
        return new ArrayList<>(members);
    }

        @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        GuildView guild = getObject(teamName);
        if (guild == null) return java.util.Collections.emptyList();
        Set<OfflinePlayer> members = new HashSet<>();
        for (PlayerView member : guild.getOfflinePlayers()) {
            members.add(member.getOfflinePlayer());
        }
        for (PlayerView member : guild.getOnlinePlayers()) {
            members.add(member.getOfflinePlayer());
        }
        return new ArrayList<>(members);
    }

        @Override
    public List<Player> getOnlineMembers(Player player) {
        GuildView guild = getObject(player);
        if (guild == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (PlayerView member : guild.getOnlinePlayers()) {
            members.add(member.getOnlinePlayer());
        }
        return new ArrayList<>(members);
    }

        @Override
    public List<Player> getOnlineMembers(String teamName) {
        GuildView guild = getObject(teamName);
        if (guild == null) return java.util.Collections.emptyList();
        List<Player> members = new ArrayList<>();
        for (PlayerView member : guild.getOnlinePlayers()) {
            members.add(member.getOnlinePlayer());
        }
        return new ArrayList<>(members);
    }

        private GuildView getObject(Player player) {
        return provider.getByPlayer(player);
    }

        private GuildView getObject(String teamName) {
        return provider.getByName(teamName);
    }
}
