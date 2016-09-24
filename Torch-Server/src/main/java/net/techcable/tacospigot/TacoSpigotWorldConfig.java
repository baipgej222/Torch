package net.techcable.tacospigot;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class TacoSpigotWorldConfig {

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public TacoSpigotWorldConfig(String worldName) {
        this.worldName = worldName;
        this.config = TacoSpigotConfig.config;
        init();
    }

    public void init() {
        this.verbose = getBoolean("verbose", true);

        log("-------- World Settings For [" + worldName + "] --------");
        TacoSpigotConfig.readConfig(TacoSpigotWorldConfig.class, this);
    }

    private void log(String s) {
        if (verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    private void set(String path, Object val) {
        config.set("world-settings.default." + path, val);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getBoolean("world-settings." + worldName + "." + path, config.getBoolean("world-settings.default." + path));
    }

    private double getDouble(String path, double def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getDouble("world-settings." + worldName + "." + path, config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path));
    }

    private float getFloat(String path, float def) {
        return (float) getDouble(path, (double) def);
    }

    private <T> List getList(String path, T def) {
        config.addDefault("world-settings.default." + path, def);
        return (List<T>) config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
    }

    public boolean isHopperPushBased;
    private void isHopperPushBased() {
        isHopperPushBased = getBoolean("hopper.push-based", true);
    }

    public boolean optimizeArmorStandMovement;
    private void isArmorStandMoveWithoutGravity() {
        optimizeArmorStandMovement = getBoolean("armor-stand.optimize-movement", false); // Doesn't fully emulate vanilla behavior, see issue #1
    }

    public boolean isHopperFireIMIE;
    private void isHopperFireIMIE() {
        isHopperFireIMIE = getBoolean("hopper.fire-InventoryMoveItemEvent", true);
    }
}
