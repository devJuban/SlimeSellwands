package com.artillexstudios.axapi.items;

import com.artillexstudios.axapi.AxPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent sellwand fields using Bukkit PDC on modern servers and NBT reflection on legacy servers. */
public final class NBTWrapper {
    private static final String LORE_PREFIX = "§0§0axsw:";
    private final ItemStack item;
    private final Map<String, Object> values = new LinkedHashMap<>();

    public NBTWrapper(ItemStack item) {
        this.item = item;
        if (item != null) {
            readPersistentData();
            if (values.isEmpty()) readLegacyTag();
            if (values.isEmpty()) readHiddenLore();
        }
    }

    public String getString(String key) { Object value = values.get(key); return value == null ? null : String.valueOf(value); }
    public UUID getUUID(String key) {
        Object value = values.get(key);
        if (value instanceof UUID) return (UUID) value;
        if (value == null) return null;
        try { return UUID.fromString(String.valueOf(value)); } catch (IllegalArgumentException ignored) { return null; }
    }
    public int getIntOr(String key, int fallback) { Object value = values.get(key); return value instanceof Number ? ((Number) value).intValue() : fallback; }
    public long getLong(String key) { Object value = values.get(key); return value instanceof Number ? ((Number) value).longValue() : value == null ? null : Long.parseLong(value.toString()); }
    public float getFloatOr(String key, float fallback) { Object value = values.get(key); return value instanceof Number ? ((Number) value).floatValue() : fallback; }
    public double getDoubleOr(String key, double fallback) { Object value = values.get(key); return value instanceof Number ? ((Number) value).doubleValue() : fallback; }
    public void set(String key, Object value) { if (value == null) values.remove(key); else values.put(key, value instanceof UUID ? value.toString() : value); }

    public void build() {
        if (item == null) return;
        if (!writePersistentData()) {
            writeLegacyTag();
            // Generic Bukkit ItemStacks on 1.8 cannot retain reflected NMS tags through inventory copies.
            // Keep a private serialized fallback in a dark lore entry so those copies preserve the data.
            writeHiddenLore();
        }
    }

    private void readPersistentData() {
        try {
            Object container = container(item.getItemMeta());
            if (container == null) return;
            for (Map.Entry<String, Class<?>> entry : valueTypes().entrySet()) {
                Object value = pdcGet(container, entry.getKey(), entry.getValue());
                if (value != null) values.put(entry.getKey(), value);
            }
        } catch (Throwable ignored) { }
    }

    private boolean writePersistentData() {
        try {
            ItemMeta meta = item.getItemMeta();
            Object container = container(meta);
            if (container == null) return false;
            for (Map.Entry<String, Object> entry : values.entrySet()) pdcSet(container, entry.getKey(), entry.getValue());
            item.setItemMeta(meta);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    private Object container(ItemMeta meta) throws Exception {
        if (meta == null) return null;
        Method method = meta.getClass().getMethod("getPersistentDataContainer");
        return method.invoke(meta);
    }

    private Object key(String name) throws Exception {
        Class<?> type = Class.forName("org.bukkit.NamespacedKey");
        for (java.lang.reflect.Constructor<?> constructor : type.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (parameters.length == 2 && parameters[0].isInstance(AxPlugin.getInstance()) && parameters[1] == String.class) {
                return constructor.newInstance(AxPlugin.getInstance(), name);
            }
        }
        return null;
    }

    private Object dataType(Class<?> javaType) throws Exception {
        Class<?> type = Class.forName("org.bukkit.persistence.PersistentDataType");
        String field = javaType == String.class ? "STRING" : javaType == Integer.class ? "INTEGER" : javaType == Long.class ? "LONG" : javaType == Float.class ? "FLOAT" : "DOUBLE";
        return type.getField(field).get(null);
    }

    private Object pdcGet(Object container, String name, Class<?> valueType) throws Exception {
        Object key = key("axsellwands_" + name);
        if (key == null) return null;
        Class<?> type = Class.forName("org.bukkit.persistence.PersistentDataType");
        return container.getClass().getMethod("get", Class.forName("org.bukkit.NamespacedKey"), type).invoke(container, key, dataType(valueType));
    }

    private void pdcSet(Object container, String name, Object value) throws Exception {
        Class<?> valueType = value.getClass();
        if (valueType == UUID.class) { value = value.toString(); valueType = String.class; }
        Object key = key("axsellwands_" + name);
        if (key == null) return;
        Class<?> type = Class.forName("org.bukkit.persistence.PersistentDataType");
        container.getClass().getMethod("set", Class.forName("org.bukkit.NamespacedKey"), type, Object.class).invoke(container, key, dataType(valueType), value);
    }

    private Map<String, Class<?>> valueTypes() {
        Map<String, Class<?>> types = new LinkedHashMap<>();
        types.put("axsellwands-type", String.class);
        types.put("axsellwands-uuid", String.class);
        types.put("axsellwands-multiplier", Float.class);
        types.put("axsellwands-lastused", Long.class);
        types.put("axsellwands-uses", Integer.class);
        types.put("axsellwands-max-uses", Integer.class);
        types.put("axsellwands-sold-amount", Integer.class);
        types.put("axsellwands-sold-price", Double.class);
        return types;
    }

    private Object legacyHandle() throws Exception {
        String craftPackage = Bukkit.getServer().getClass().getPackage().getName();
        Class<?> craftStack = Class.forName(craftPackage + ".inventory.CraftItemStack");
        try { return craftStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item); }
        catch (NoSuchMethodException ignored) {
            Method getHandle = item.getClass().getMethod("getHandle");
            return getHandle.invoke(item);
        }
    }

    private Object legacyTag(Object handle, boolean create) throws Exception {
        Method getter = handle.getClass().getMethod("getTag");
        Object tag = getter.invoke(handle);
        if (tag == null && create) {
            String nmsPackage = handle.getClass().getPackage().getName();
            Class<?> tagClass = Class.forName(nmsPackage + ".NBTTagCompound");
            tag = tagClass.newInstance();
            handle.getClass().getMethod("setTag", tagClass).invoke(handle, tag);
        }
        return tag;
    }

    private void readLegacyTag() {
        try {
            Object tag = legacyTag(legacyHandle(), false);
            if (tag == null) return;
            for (Map.Entry<String, Class<?>> entry : valueTypes().entrySet()) {
                String name = entry.getKey();
                if (!(Boolean) tag.getClass().getMethod("hasKey", String.class).invoke(tag, name)) continue;
                Object value;
                Class<?> type = entry.getValue();
                String suffix = type == Integer.class ? "Int" : type == Long.class ? "Long" : type == Float.class ? "Float" : type == Double.class ? "Double" : "String";
                value = tag.getClass().getMethod("get" + suffix, String.class).invoke(tag, name);
                values.put(name, value);
            }
        } catch (Throwable ignored) { }
    }

    private boolean writeLegacyTag() {
        try {
            Object handle = legacyHandle();
            Object tag = legacyTag(handle, true);
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof UUID) value = value.toString();
                String suffix = value instanceof Integer ? "Int" : value instanceof Long ? "Long" : value instanceof Float ? "Float" : value instanceof Double ? "Double" : "String";
                tag.getClass().getMethod("set" + suffix, String.class, value.getClass()).invoke(tag, entry.getKey(), value);
            }
            return true;
        } catch (Throwable ignored) { return false; }
    }

    private void readHiddenLore() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;
        for (String line : meta.getLore()) if (line != null && line.startsWith(LORE_PREFIX)) {
            try {
                String decoded = new String(Base64.getDecoder().decode(line.substring(LORE_PREFIX.length())), StandardCharsets.UTF_8);
                for (String pair : decoded.split("\\n")) {
                    int at = pair.indexOf('=');
                    if (at > 0) values.put(pair.substring(0, at), decode(pair.substring(at + 1)));
                }
            } catch (Exception ignored) { }
        }
    }

    private void writeHiddenLore() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        java.util.List<String> lore = meta.hasLore() ? new java.util.ArrayList<>(meta.getLore()) : new java.util.ArrayList<String>();
        for (int i = lore.size() - 1; i >= 0; i--) if (lore.get(i) != null && lore.get(i).startsWith(LORE_PREFIX)) lore.remove(i);
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (data.length() > 0) data.append('\n');
            data.append(entry.getKey()).append('=').append(encode(entry.getValue()));
        }
        if (data.length() > 0) lore.add(LORE_PREFIX + Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private String encode(Object value) { return value == null ? "" : value.getClass().getSimpleName() + ":" + value; }
    private Object decode(String value) {
        int at = value.indexOf(':');
        if (at < 0) return value;
        String type = value.substring(0, at), raw = value.substring(at + 1);
        try {
            if (type.equals("Integer")) return Integer.parseInt(raw);
            if (type.equals("Long")) return Long.parseLong(raw);
            if (type.equals("Float")) return Float.parseFloat(raw);
            if (type.equals("Double")) return Double.parseDouble(raw);
        } catch (NumberFormatException ignored) { }
        return raw;
    }
}
