package pl.servercreators.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.servercreators.SCSprawdzMain;
import pl.servercreators.models.HistoryEntry;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryManager {
    
    private final SCSprawdzMain plugin;
    private final File historyFolder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HistoryManager(SCSprawdzMain plugin) {
        this.plugin = plugin;
        this.historyFolder = new File(plugin.getDataFolder(), "history");
        if (!this.historyFolder.exists()) {
            this.historyFolder.mkdirs();
        }
    }

    public void log(String playerName, String action, String moderatorName) {
        String time = LocalDateTime.now().format(formatter);


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File playerFile = new File(historyFolder, playerName + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            
            Map<String, Object> entry = Map.of(
                "date", time,
                "action", action,
                "moderator", moderatorName
            );

            List<Map<?, ?>> logs = (List<Map<?, ?>>) config.getList("entries", new ArrayList<>());
            logs.add(entry);
            
            config.set("entries", logs);
            config.set("last_update", time);

            try {
                config.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Nie udalo sie zapisac historii dla: " + playerName);
                e.printStackTrace();
            }
        });
    }

    public List<HistoryEntry> getHistory(String playerName) {
        File playerFile = new File(historyFolder, playerName + ".yml");
        if (!playerFile.exists()) return Collections.emptyList();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        List<?> rawEntries = config.getList("entries");
        
        if (rawEntries == null || rawEntries.isEmpty()) return Collections.emptyList();

        List<HistoryEntry> entries = new ArrayList<>();
        
        int limit = 14;

        int start = Math.max(0, rawEntries.size() - limit);

        for (int i = rawEntries.size() - 1; i >= start; i--) {
            Object obj = rawEntries.get(i);
            if (obj instanceof Map<?, ?> rawMap) {
                Map<String, Object> map = (Map<String, Object>) rawMap;
                
                entries.add(new HistoryEntry(
                    String.valueOf(map.getOrDefault("date", "Brak daty")),
                    String.valueOf(map.getOrDefault("action", "Brak akcji")),
                    String.valueOf(map.getOrDefault("moderator", "Brak moderatora"))
                ));
            }
        }

        return entries;
    }
}