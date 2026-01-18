package pl.servercreators.managers;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.servercreators.SCSprawdzMain;
import pl.servercreators.data.HierarchyData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ConfigManager {

    private final SCSprawdzMain plugin;
    private final LocationManager locations;
    
    private MessageManager messages;
    private GuiManager gui;
    private List<String> allowedCommands;
    private HierarchyData hierarchy;
    private final FileConfiguration config;
    private FileConfiguration messagesConfig;

    public ConfigManager(SCSprawdzMain plugin) {
        this.plugin = plugin;
        
        plugin.saveDefaultConfig();
        plugin.saveResource("messages.yml", false);
        plugin.reloadConfig();
        
        this.config = plugin.getConfig();
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        this.locations = new LocationManager(plugin, this);
        this.messages = new MessageManager(this.messagesConfig);
        this.gui = new GuiManager(this.config);

        this.allowedCommands = config.getStringList("allowed-commands").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
                
        this.hierarchy = initHierarchyData(this.config);
    }

    public void reload() {
        plugin.reloadConfig();
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        this.messages = new MessageManager(this.messagesConfig);

        this.allowedCommands = plugin.getConfig().getStringList("allowed-commands").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        this.hierarchy = initHierarchyData(plugin.getConfig());

        this.gui = new GuiManager(plugin.getConfig());
    }

    private HierarchyData initHierarchyData(FileConfiguration config) {
        boolean enabled = config.getBoolean("hierarchy.enabled", true);
        Map<String, Integer> priorities = new HashMap<>();
        
        ConfigurationSection section = config.getConfigurationSection("hierarchy.rang");
        if (section != null) {
            for (String group : section.getKeys(false)) {
                priorities.put(group.toLowerCase(), section.getInt(group));
            }
        }
        return new HierarchyData(priorities, enabled);
    }
    
    public List<String> getBanCommands(String type) {
        return config.getStringList("commands." + type);
    }
}