package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.TeamIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Optional ZelTeams adapter without a hard Java 21 provider dependency. */
public class ZelTeamsTeam extends TeamIntegration {
    private static final String API_CLASS = "com.zeltuv.teams.api.ZelTeamsAPI";
    private Class<?> apiType;
    private Object manager;

    public ZelTeamsTeam() {
        super("ZelTeams");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ZelTeams");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            apiType = Class.forName(API_CLASS, false, plugin.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Object api = invokeStatic(apiType, "getInstance");
            manager = invoke(api, "getTeamManager");
            return manager != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize ZelTeams", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getTeam(Player player) {
        Object team = getObject(player);
        if (team == null) return null;
        try {
            return (String) invoke(team, "getTag");
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ZelTeams tag", error);
        }
    }

    @Override
    public OfflinePlayer getLeader(Player player) {
        return getLeader(getObject(player));
    }

    @Override
    public OfflinePlayer getLeader(String teamName) {
        return getLeader(getObject(teamName));
    }

    private OfflinePlayer getLeader(Object team) {
        if (team == null) return null;
        try {
            Object owner = invoke(team, "getOwner");
            UUID uuid = (UUID) invoke(owner, "getUuid");
            return Bukkit.getOfflinePlayer(uuid);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ZelTeams owner", error);
        }
    }

    @Override
    public List<OfflinePlayer> getMembers(Player player) {
        return getMembers(getObject(player));
    }

    @Override
    public List<OfflinePlayer> getMembers(String teamName) {
        return getMembers(getObject(teamName));
    }

    private List<OfflinePlayer> getMembers(Object team) {
        if (team == null) return Collections.emptyList();
        try {
            Object members = invoke(team, "getAllMembers");
            if (!(members instanceof Iterable)) return Collections.emptyList();
            List<OfflinePlayer> result = new ArrayList<>();
            for (Object member : (Iterable<?>) members) {
                result.add(Bukkit.getOfflinePlayer((UUID) invoke(member, "getUuid")));
            }
            return result;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ZelTeams members", error);
        }
    }

    private Object getObject(Player player) {
        if (player == null || manager == null) return null;
        try {
            return invoke(invoke(manager, "getTeam", player), "orElse", (Object) null);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not find ZelTeams team", error);
        }
    }

    private Object getObject(String teamName) {
        if (manager == null) return null;
        try {
            return invoke(invoke(manager, "getByTag", teamName), "orElse", (Object) null);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not find ZelTeams team", error);
        }
    }

    private static Object invokeStatic(Class<?> owner, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(owner, name, arguments);
        if (method == null || !Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(owner.getName() + "." + name);
        }
        return method.invoke(null, arguments);
    }

    private static Object invoke(Object target, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(target.getClass(), name, arguments);
        if (method == null) throw new NoSuchMethodException(target.getClass().getName() + "." + name);
        return method.invoke(target, arguments);
    }

    private static Method findMethod(Class<?> owner, String name, Object... arguments) {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int index = 0; index < parameters.length; index++) {
                if (arguments[index] != null && !parameters[index].isAssignableFrom(arguments[index].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) return method;
        }
        return null;
    }
}
