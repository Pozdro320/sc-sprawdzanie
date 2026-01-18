package pl.servercreators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import pl.servercreators.data.GuiItemData;

public class GuiManager {

    private final Map<String, GuiItemData> guiItems = new HashMap<>();
    private final FileConfiguration config;

    public GuiManager(FileConfiguration config) {
        this.config = config;
        
        this.loadGuiItems();
    }

    private void loadGuiItems() {
        guiItems.clear();

        ConfigurationSection guiSection = config.getConfigurationSection("gui");
        if (guiSection == null) return;
        
        for (String guiName : guiSection.getKeys(false)) {
            ConfigurationSection itemsSection = config.getConfigurationSection("gui." + guiName + ".items");
            if (itemsSection == null) continue;

            for (String itemName : itemsSection.getKeys(false)) {
                String path = "gui." + guiName + ".items." + itemName;
                
                Material mat = Material.matchMaterial(config.getString(path + ".material", "STONE"));
                if (mat == null) mat = Material.BARRIER;
                
                String name = config.getString(path + ".name", "");
                List<String> lore = config.getStringList(path + ".lore");

                guiItems.put(guiName + "." + itemName, new GuiItemData(mat, name, lore));
            }
        }
    }
    
    public String getGuiTitle(String guiName, String target) {
        String title = config.getString("gui." + guiName + ".title", "Menu");
        return PlaceholdersManager.replacePlaceholders(title, target, null);
    }

    public GuiItemData getGuiItem(String guiName, String itemName) {
        return guiItems.getOrDefault(guiName + "." + itemName,
            new GuiItemData(Material.BARRIER, "§cBłąd konf: " + itemName, new ArrayList<>()));
    }

    public List<String> getItemLoreForPlayer(String guiName, String itemName, String target) {
        GuiItemData data = getGuiItem(guiName, itemName);
        return data.getLore().stream()
                .map(line -> PlaceholdersManager.replacePlaceholders(line, target, null))
                .collect(Collectors.toList());
    }

    public String getItemNameForPlayer(String guiName, String itemName, String target) {
        GuiItemData data = getGuiItem(guiName, itemName);
        return PlaceholdersManager.replacePlaceholders(data.getName(), target, null);
    }
}
