package com.artillexstudios.axintegrations.types;

import com.artillexstudios.axintegrations.Integration;
import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.IntegrationType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Rules
 * -
 */
public abstract class TeamIntegration extends Integration {

    /**
     * returns all loaded integrations
     */
    public static List<TeamIntegration> list() {
        return IntegrationManager.getIntegrations(TeamIntegration.class);
    }

    /**
     * returns a loaded integration
     * if you register multiple, you should use {@link this#list()} instead
     */
        public static TeamIntegration one() {
        return list().stream().findFirst().orElse(null);
    }

    /**
     * returns a loaded integration by name
     */
        public static TeamIntegration one(String name) {
        return list().stream().filter(i -> {
            return i.getName().equalsIgnoreCase(name) || i.getFormattedName().equalsIgnoreCase(name);
        }).findFirst().orElse(null);
    }

    public TeamIntegration(String name) {
        super(name, IntegrationType.TEAM);
    }

        public abstract String getTeam(Player player);

    public boolean isMember(Player player, String teamName) {
        return Objects.equals(getTeam(player), teamName);
    }

    public boolean isSameTeam(Player one, Player two) {
        String team1 = getTeam(one);
        if (team1 == null) return false;
        return team1.equals(getTeam(two));
    }

        public abstract OfflinePlayer getLeader(Player player);

        public abstract OfflinePlayer getLeader(String teamName);

    public boolean isLeader(Player player) {
        OfflinePlayer leader = getLeader(player);
        if (leader == null) return false;
        return leader.getUniqueId().equals(player.getUniqueId());
    }

        public abstract List<OfflinePlayer> getMembers(Player player);

        public abstract List<OfflinePlayer> getMembers(String teamName);

        public List<Player> getOnlineMembers(Player player) {
        return getMembers(player).stream()
                .map(OfflinePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }

        public List<Player> getOnlineMembers(String teamName) {
        return getMembers(teamName).stream()
                .map(OfflinePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }
}
