package pl.pozdro320.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import pl.pozdro320.SCSprawdzMain;

public class LocationManager {
    private final SCSprawdzMain plugin;

    public LocationManager(SCSprawdzMain plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public void save(String name, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        
        FileConfiguration config = getConfig();
        String path = "locations." + name;

        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        
        plugin.saveConfig();
    }

    public Location load(String name) {
        FileConfiguration config = getConfig();
        String path = "locations." + name;

        String worldName = config.getString(path + ".world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(
                world,
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw"),
                (float) config.getDouble(path + ".pitch")
        );
    }
}