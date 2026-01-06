package pl.pozdro320.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.data.BlockCommandData;
import pl.pozdro320.data.HierarchyData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

private final SCSprawdzMain plugin;
    
    private FileConfiguration config;
    private FileConfiguration messagesConfig;

    private MessageManager messageManager;
    private GuiManager guiManager;
    private LocationManager locationManager;

    private BlockCommandData blockCommandData;
    private HierarchyData hierarchyData;

    public ConfigManager(SCSprawdzMain plugin) {
        this.plugin = plugin;
        this.locationManager = new LocationManager(plugin);
        this.loadConfig();
    }

    public void loadConfig() {

        plugin.reloadConfig();
        this.config = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        this.messageManager = new MessageManager(this.messagesConfig);
        this.guiManager = new GuiManager(this.config);
        
        loadBlockCommandData();
        loadHierarchyData();
    }


    private void loadBlockCommandData() {
        List<String> allowed = config.getStringList("allowed-commands");
        this.blockCommandData = new BlockCommandData(allowed);
    }

    private void loadHierarchyData() {
        Map<String, Integer> priorities = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("hierarchy");
        if (section != null) {
            for (String group : section.getKeys(false)) {
                priorities.put(group.toLowerCase(), section.getInt(group));
            }
        }
        this.hierarchyData = new HierarchyData(priorities);
    }

    public List<String> getBanCommands(String type) {
        return config.getStringList("commands." + type);
    }

    public List<String> getAllowedCommands() {
        return config.getStringList("allowed-commands");
    }

    public HierarchyData getHierarchy() {
        return hierarchyData;
    }

    public MessageManager getMessages() {
        return messageManager;
    }

    public GuiManager getGui() {
        return guiManager;
    }

    public LocationManager getLocations() {
        return locationManager;
    }

    public BlockCommandData getBlockCommandData() {
        return blockCommandData;
    }

    public FileConfiguration getConfig() {
        return config;
    } 
}
